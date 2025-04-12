package edu.ntnu.idi.bidata.idatg2003mappe.app.laddergame;

import edu.ntnu.idi.bidata.idatg2003mappe.entity.Die;
import edu.ntnu.idi.bidata.idatg2003mappe.entity.Player;
import edu.ntnu.idi.bidata.idatg2003mappe.filehandling.game.GameState;
import edu.ntnu.idi.bidata.idatg2003mappe.map.BoardLinear;
import edu.ntnu.idi.bidata.idatg2003mappe.map.Tile;
import edu.ntnu.idi.bidata.idatg2003mappe.movement.LadderAction;

import java.util.List;

public class LadderGameController {
  private final LadderGame game;
  private int currentPlayerIndex = 0;
  private boolean randomLadders;

  public LadderGameController(int numberOfPlayers, boolean randomLadders) {
    // Create a new game. (Ensure that LadderGame’s constructor
    // does not automatically start playing the game.)
    this.randomLadders = randomLadders;
    game = new LadderGame(numberOfPlayers, randomLadders);
  }

  /**
   * Plays a single turn. Returns a message describing the move.
   * If the game is finished, it returns a winning message.
   */
  public String playTurn() {
    List<Player> players = game.getPlayers();
    if (players.isEmpty()) {
      return "No players in the game.";
    }

    Player currentPlayer = players.get(currentPlayerIndex);
    // Use the die from the game instance
    int roll = game.getDie().rollDie();
    StringBuilder message = new StringBuilder();

    message.append(currentPlayer.getName() + " rolled: " + roll + "\n");

    // Move the player
    currentPlayer.movePlayer(roll);
    message.append("Moved to tile " + currentPlayer.getCurrentTile().getTileId() + "\n");

    // Check for ladder action
    if (currentPlayer.getCurrentTile().getDestinationTile() != null) {
      LadderAction ladderAction = new LadderAction(currentPlayer.getCurrentTile());
      ladderAction.performAction(currentPlayer);
      message.append("Ladder! Moved to tile " + currentPlayer.getCurrentTile().getTileId() + "\n");
    }

    // Check win condition
    if (currentPlayer.getCurrentTile().getTileId() == game.getNumberOfTiles()) {
      message.append(currentPlayer.getName() + " wins the game!");
      return message.toString();
    }

    // Update to next player
    currentPlayerIndex = (currentPlayerIndex + 1) % players.size();
    return message.toString();
  }

  /**
   * Updates the board UI.
   */

  public Tile getTileByIdLinear(int tileNumber) {
    return game.getBoard().getTileByIdLinear(tileNumber);
  }

  /**
   * Returns the players in the game.
   */

  public List<Player> getPlayers() {
    return game.getPlayers();
  }

  /**
   * Returns the current player index.
   */
  public int getCurrentPlayerIndex() {
    return currentPlayerIndex;
  }

  /**
   * Sets the current player index.
   *
   * @param index The new current player index.
   */
  public void setCurrentPlayerIndex(int index) {
    this.currentPlayerIndex = index;
  }

  /**
   * Returns whether the game uses random ladders.
   */
  public boolean isRandomLadders() {
    return randomLadders;
  }

  /**
   * Creates a GameState object representing the current state of the game.
   *
   * @return A GameState object.
   */
  public GameState createGameState() {
    return new GameState(currentPlayerIndex, randomLadders, game.getPlayers());
  }

  /**
   * Applies a GameState to this controller.
   *
   * @param gameState The GameState to apply.
   */
  public void applyGameState(GameState gameState) {
    this.currentPlayerIndex = gameState.getCurrentPlayerIndex();
  }
}