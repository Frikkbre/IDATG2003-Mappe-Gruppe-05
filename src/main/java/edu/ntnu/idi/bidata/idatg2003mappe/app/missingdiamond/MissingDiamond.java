package edu.ntnu.idi.bidata.idatg2003mappe.app.missingdiamond;

import edu.ntnu.idi.bidata.idatg2003mappe.entity.Die;
import edu.ntnu.idi.bidata.idatg2003mappe.entity.Player;
import edu.ntnu.idi.bidata.idatg2003mappe.filehandling.exceptionhandling.FileHandlingException;
import edu.ntnu.idi.bidata.idatg2003mappe.filehandling.playerInfo.PlayerFileHandler;
import edu.ntnu.idi.bidata.idatg2003mappe.map.Tile;
import edu.ntnu.idi.bidata.idatg2003mappe.map.board.BoardBranching;

import java.util.*;

/**
 * Represents the Missing Diamond game.
 *
 * @author Simen Gudbrandsen and Frikk Breadsroed
 * @version 0.0.2
 * @since 16.02.2025
 */
public class MissingDiamond {
  private final BoardBranching board;
  private final List<Player> players;
  private final Die die;
  private boolean gameFinished;
  private Player currentPlayer;
  private int currentPlayerIndex;
  private Tile diamondLocation;
  private int currentRoll; // Store the last roll value

  /**
   * Constructor for the MissingDiamond class.
   * Reads players from CSV file.
   */
  public MissingDiamond() {
    System.out.println("Starting Missing Diamond Game with players from file.");
    this.board = createBoard();
    this.players = createPlayers();
    this.die = new Die();
    this.gameFinished = false;
    this.currentPlayerIndex = 0;
    this.currentPlayer = players.isEmpty() ? null : players.get(currentPlayerIndex);
    this.currentRoll = 0;
  }

  private BoardBranching createBoard() {
    BoardBranching board = new BoardBranching();

    // Create tiles for the main circular path (20 tiles)
    List<Tile> outerTiles = new ArrayList<>();
    for (int i = 1; i <= 20; i++) {
      Tile tile = new Tile(i);
      board.addTileToBoard(tile);
      outerTiles.add(tile);
    }

    // Connect the outer tiles in a circle
    for (int i = 0; i < outerTiles.size(); i++) {
      Tile current = outerTiles.get(i);
      Tile next = outerTiles.get((i + 1) % outerTiles.size());
      board.connectTiles(current, next);
    }

    // Create center tile
    Tile centerTile = new Tile(21);
    board.addTileToBoard(centerTile);

    // Connect center tile to the circle at four points (north, east, south, west)
    board.connectTiles(centerTile, outerTiles.get(0));  // North
    board.connectTiles(centerTile, outerTiles.get(5));  // East
    board.connectTiles(centerTile, outerTiles.get(10)); // South
    board.connectTiles(centerTile, outerTiles.get(15)); // West

    // Set a random tile as the diamond location (for demo purposes)
    diamondLocation = outerTiles.get(outerTiles.size() - 1);

    return board;
  }

  private List<Player> createPlayers() {
    try {
      Tile startTile = board.getStartTile();
      return PlayerFileHandler.readPlayersFromFile(startTile);
    } catch (FileHandlingException e) {
      System.err.println("Error reading player data: " + e.getMessage());
      // Fallback to default creation if file reading fails
      List<Player> players = new ArrayList<>();
      Tile startTile = board.getStartTile();
      // Create 2 players as fallback
      for (int i = 1; i <= 2; i++) {
        Player player = new Player("Player " + i, startTile, i);
        players.add(player);
      }
      return players;
    }
  }

  public String playTurn() {
    // Roll the die
    this.currentRoll = die.rollDie();
    return currentPlayer.getName() + " rolled a " + currentRoll + ".";
  }

  /**
   * Gets all tiles that are exactly N steps away from a starting tile.
   * Uses a simple recursive approach to find all possible destinations.
   *
   * @param startTile The starting tile.
   * @param steps The number of steps to move.
   * @return Set of tiles that are exactly N steps away.
   */
  public Set<Tile> getTilesExactlyNStepsAway(Tile startTile, int steps) {
    Set<Tile> result = new HashSet<>();

    // No valid moves if steps is invalid
    if (steps <= 0) {
      return result;
    }

    // We'll use a helper method to do a depth-first search of exactly N steps
    findExactPathsOfLength(startTile, null, steps, result);

    return result;
  }

  private void findExactPathsOfLength(Tile currentTile, Tile previousTile, int remainingSteps, Set<Tile> result) {
    // If we've used all our steps, add the current tile to our result
    if (remainingSteps == 0) {
      result.add(currentTile);
      return;
    }

    // Otherwise, continue the search from each neighbor (except the one we just came from)
    for (Tile neighbor : currentTile.getNextTiles()) {
      if (neighbor != previousTile) {  // Prevent immediate backtracking
        findExactPathsOfLength(neighbor, currentTile, remainingSteps - 1, result);
      }
    }
  }

  /**
   * Gets all possible moves for the current player based on the last die roll.
   *
   * @return Set of tiles that the player can move to.
   */
  public Set<Tile> getPossibleMovesForCurrentRoll() {
    if (currentRoll < 1) {
      return new HashSet<>();
    }

    return getTilesExactlyNStepsAway(currentPlayer.getCurrentTile(), currentRoll);
  }

  /**
   * Moves the current player to the selected tile and handles any special actions.
   *
   * @param destinationTile The tile to move to.
   * @return A message describing the move result.
   */
  public String movePlayerToTile(Tile destinationTile) {
    String result = "";

    if (destinationTile == null) {
      return "Invalid destination tile.";
    }

    // Check if the move is valid (destination is exactly N steps away)
    Set<Tile> validMoves = getPossibleMovesForCurrentRoll();
    if (!validMoves.contains(destinationTile)) {
      return "Cannot move to that tile - it's not exactly " + currentRoll + " steps away.";
    }

    // Move the player
    result += currentPlayer.getName() + " moved to tile " + destinationTile.getTileId() + ". ";
    currentPlayer.placePlayer(destinationTile);

    // Reset current roll
    currentRoll = 0;

    // Check if player found the diamond
    if (destinationTile == diamondLocation) {
      result += currentPlayer.getName() + " found the diamond and won the game!";
      gameFinished = true;
      return result;
    }

    // Move to next player if game not finished
    if (!gameFinished) {
      currentPlayerIndex = (currentPlayerIndex + 1) % players.size();
      currentPlayer = players.get(currentPlayerIndex);
    }

    return result;
  }

  /**
   * Gets the list of players.
   *
   * @return The list of players.
   */
  public List<Player> getPlayers() {
    return players;
  }

  /**
   * Gets the game board.
   *
   * @return The game board.
   */
  public BoardBranching getBoard() {
    return board;
  }

  /**
   * Gets the die.
   *
   * @return The die.
   */
  public Die getDie() {
    return die;
  }

  /**
   * Gets the current roll value.
   *
   * @return The current roll value.
   */
  public int getCurrentRoll() {
    return currentRoll;
  }

  /**
   * Checks if the game is finished.
   *
   * @return True if the game is finished, false otherwise.
   */
  public boolean isGameFinished() {
    return gameFinished;
  }

  /**
   * Gets the current player.
   *
   * @return The current player.
   */
  public Player getCurrentPlayer() {
    return currentPlayer;
  }

  /**
   * Gets the index of the current player.
   *
   * @return The index of the current player.
   */
  public int getCurrentPlayerIndex() {
    return currentPlayerIndex;
  }
}