package edu.ntnu.idi.bidata.idatg2003mappe.app.missingdiamond;

import edu.ntnu.idi.bidata.idatg2003mappe.entity.Die;
import edu.ntnu.idi.bidata.idatg2003mappe.entity.Player;
import edu.ntnu.idi.bidata.idatg2003mappe.filehandling.exceptionhandling.FileHandlingException;
import edu.ntnu.idi.bidata.idatg2003mappe.filehandling.map.MapConfig;
import edu.ntnu.idi.bidata.idatg2003mappe.filehandling.map.MapConfigFileHandler;
import edu.ntnu.idi.bidata.idatg2003mappe.map.Tile;
import edu.ntnu.idi.bidata.idatg2003mappe.map.board.BoardBranching;
import java.util.Random;


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
   *
   * @param numberOfPlayers The number of players in the game.
   */
  public MissingDiamond(int numberOfPlayers) {
    System.out.println("Starting Missing Diamond Game with " + numberOfPlayers + " players.");
    this.board = createBoard();
    this.players = createPlayers(numberOfPlayers, this.board);
    this.die = new Die();
    this.gameFinished = false;
    this.currentPlayerIndex = 0;
    this.currentPlayer = players.get(currentPlayerIndex);
    this.currentRoll = 0;
  }

  public MissingDiamond(int numberOfPlayers, String mapFilePath) {
    System.out.println("Starting Missing Diamond Game with " + numberOfPlayers + " players.");

    BoardBranching boardInstance; // Temporary local variable
    List<Player> playersInstance;
    Die dieInstance = new Die();
    boolean gameFinishedInstance = false;
    int currentPlayerIndexInstance = 0;

    try {
      // Load map configuration from file
      MapConfigFileHandler mapFileHandler = new MapConfigFileHandler();
      MapConfig mapConfig = mapFileHandler.read(mapFilePath);

      // Create board from configuration
      boardInstance = createBoardFromConfig(mapConfig);

      // Initialize players using the created board
      playersInstance = createPlayers(numberOfPlayers, boardInstance);

      // Randomly place the diamond at one of the locations
      int randomTileId = new Random().nextInt(32) + 1;

    } catch (FileHandlingException e) {
      System.err.println("Error loading map configuration: " + e.getMessage());
      // Fall back to default board creation
      boardInstance = createDefaultBoard();
      playersInstance = createPlayers(numberOfPlayers, boardInstance);
    }

    // Only assign to final fields once
    this.board = boardInstance;
    this.players = playersInstance;
    this.die = dieInstance;
    this.gameFinished = gameFinishedInstance;
    this.currentPlayerIndex = currentPlayerIndexInstance;
    this.currentPlayer = players.get(currentPlayerIndex);
    this.currentRoll = 0;

    // Place diamond after board is initialized
    int randomTileId = new Random().nextInt(32) + 1;
    this.diamondLocation = board.getTileById(randomTileId);
  }

  /**
   * Creates a default board when no configuration file is available.
   * This serves as a fallback when a map configuration cannot be loaded.
   *
   * @return A default board configuration
   */
  private BoardBranching createDefaultBoard() {
    // Simply delegate to the existing createBoard method
    return createBoard();
  }

  private BoardBranching createBoardFromConfig(MapConfig mapConfig) {
    BoardBranching board = new BoardBranching();
    board.setBoardName(mapConfig.getName());

    // Create all tiles
    for (MapConfig.Location location : mapConfig.getLocations()) {
      Tile tile = new Tile(location.getId());
      board.addTileToBoard(tile);
    }

    // Add all connections
    for (MapConfig.Connection connection : mapConfig.getConnections()) {
      Tile fromTile = board.getTileById(connection.getFromId());
      Tile toTile = board.getTileById(connection.getToId());

      if (fromTile != null && toTile != null) {
        board.connectTiles(fromTile, toTile);
      }
    }

    return board;
  }

  private BoardBranching createBoard() {
    BoardBranching board = new BoardBranching();

    // Create all locations
    for (int i = 1; i <= 32; i++) {
      Tile tile = new Tile(i);
      board.addTileToBoard(tile);
    }

    // Add all connections matching the GUI's CONNECTIONS array
    // North Africa
    connectTiles(board, 1, 2, 3);
    connectTiles(board, 2, 19);
    connectTiles(board, 3, 4, 6);
    connectTiles(board, 4, 5);
    connectTiles(board, 5, 13);
    connectTiles(board, 6, 7);

    // West Africa
    connectTiles(board, 7, 9, 10);
    connectTiles(board, 8, 17, 20);
    connectTiles(board, 9, 18, 19);

    // Central Africa
    connectTiles(board, 10, 11, 12);
    connectTiles(board, 11, 15, 16);
    connectTiles(board, 12, 13);
    connectTiles(board, 13, 14);
    connectTiles(board, 14, 15);

    // East Africa
    connectTiles(board, 15, 16, 24);
    connectTiles(board, 16, 21, 24);

    // West Coast
    connectTiles(board, 17, 18);
    connectTiles(board, 18, 19);
    connectTiles(board, 19, 21);
    connectTiles(board, 20, 29);

    // Central Paths
    connectTiles(board, 21, 22, 23);
    connectTiles(board, 22, 29);
    connectTiles(board, 23, 24, 25);
    connectTiles(board, 25, 26, 28);
    connectTiles(board, 26, 27);
    connectTiles(board, 27, 31);

    // South Africa
    connectTiles(board, 28, 29, 30);
    connectTiles(board, 29, 32);
    connectTiles(board, 30, 31);

    // Randomly place the diamond at one of the locations
    int diamondLocation = new Random().nextInt(32) + 1;
    this.diamondLocation = board.getTileById(diamondLocation);

    return board;
  }

  // Helper method to connect one tile to multiple others
  private void connectTiles(BoardBranching board, int fromId, int... toIds) {
    Tile fromTile = board.getTileById(fromId);
    for (int toId : toIds) {
      Tile toTile = board.getTileById(toId);
      board.connectTiles(fromTile, toTile);
    }
  }

  private List<Player> createPlayers(int numberOfPlayers, BoardBranching board) {
    List<Player> players = new ArrayList<>();
    Tile startTile = board.getStartTile();

    for (int i = 1; i <= numberOfPlayers; i++) {
      Player player = new Player("Player " + i, startTile, i);
      players.add(player);
    }

    return players;
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