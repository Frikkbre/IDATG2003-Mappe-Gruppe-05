package edu.ntnu.idi.bidata.idatg2003mappe.app.missingdiamond;

import edu.ntnu.idi.bidata.idatg2003mappe.entity.Die;
import edu.ntnu.idi.bidata.idatg2003mappe.entity.Player;
import edu.ntnu.idi.bidata.idatg2003mappe.map.Tile;
import edu.ntnu.idi.bidata.idatg2003mappe.map.board.BoardBranching;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents the Missing Diamond game.
 *
 * @author Simen Gudbrandsen and Frikk Breadsroed
 * @version 0.0.1
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

  /**
   * Constructor for the MissingDiamond class.
   *
   * @param numberOfPlayers The number of players in the game.
   */

  public MissingDiamond(int numberOfPlayers) {
    System.out.println("Starting Missing Diamond Game with " + numberOfPlayers + " players.");
    this.board = createBoard();
    this.players = createPlayers(numberOfPlayers);
    this.die = new Die();
    this.gameFinished = false;
    this.currentPlayerIndex = 0;
    this.currentPlayer = players.get(currentPlayerIndex);
  }

  private BoardBranching createBoard() {
    BoardBranching board = new BoardBranching();
    List<Tile> mainPath = new ArrayList<>();

    for (int i = 0; i < 30; i++) {
      Tile tile = new Tile(i + 1);
      board.addTileToBoard(tile);
      mainPath.add(tile);
    }

    for (int i = 0; i < mainPath.size() - 1; i++) {
      board.connectTiles(mainPath.get(i), mainPath.get(i + 1));
    }

    // Starting branch for testing, Later will be replaced by random and the original map
    Tile branchTile1 = new Tile(100);
    Tile branchTile2 = new Tile(101);
    board.addTileToBoard(branchTile1);
    board.addTileToBoard(branchTile2);

    board.connectTiles(mainPath.get(5), branchTile1);
    board.connectTiles(branchTile1, branchTile2);
    board.connectTiles(branchTile2, mainPath.get(10));

    // Set the diamond location
    diamondLocation = mainPath.get(mainPath.size() - 1);

    return board;
  }

  private List<Player> createPlayers(int numberOfPlayers) {
    List<Player> players = new ArrayList<>();
    Tile startTile = board.getStartTile();

    for (int i = 1; i <= numberOfPlayers; i++) {
      Player player = new Player("Player " + i, startTile, i);
      players.add(player);
    }

    return players;
  }

  public String playTurn() {
    String result = "";

    // Roll the die
    int roll = die.rollDie();
    result += currentPlayer.getName() + " rolled a " + roll + ". ";

    // In Missing Diamond, the player chooses where to move rather than automatically moving
    // The controller will handle the movement based on player choice
    return result;
  }

  public String movePlayerToTile(Tile destinationTile) {
    String result = "";

    if (destinationTile == null) {
      return "Invalid destination tile.";
    }

    // Check if the move is valid
    Tile currentTile = currentPlayer.getCurrentTile();
    if (!currentTile.getNextTiles().contains(destinationTile)) {
      return "Cannot move to that tile from your current position.";
    }

    // Move the player
    result += "Player " + currentPlayer.getName() + " moving from tile " +
        currentTile.getTileId() + " to tile " + destinationTile.getTileId() + ". ";

    currentPlayer.placePlayer(destinationTile);

    // Check for special tile effects (similar to ladder in LadderGame)
    // This would be expanded with actual game mechanics

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

  public List<Player> getPlayers() {
    return players;
  }

  public BoardBranching getBoard() {
    return board;
  }

  public Die getDie() {
    return die;
  }

  public boolean isGameFinished() {
    return gameFinished;
  }

  public Player getCurrentPlayer() {
    return currentPlayer;
  }

  /**
   * The location is already set in the constructor, but this method is here
   * for debug or later use.
   *
   * @return diamondLocation
   */

  public Tile getDiamondLocation() {
    return diamondLocation;
  }

  public int getCurrentPlayerIndex() {
    return currentPlayerIndex;
  }

}
