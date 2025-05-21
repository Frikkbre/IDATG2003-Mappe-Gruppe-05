package edu.ntnu.idi.bidata.idatg2003mappe.app.laddergame.controller;

import edu.ntnu.idi.bidata.idatg2003mappe.app.laddergame.model.LadderGame;
import edu.ntnu.idi.bidata.idatg2003mappe.entity.player.Player;
import edu.ntnu.idi.bidata.idatg2003mappe.filehandling.game.GameState;
import edu.ntnu.idi.bidata.idatg2003mappe.map.Tile;
import edu.ntnu.idi.bidata.idatg2003mappe.movement.LadderAction;

import java.util.List;

public class LadderGameController {
  private final LadderGame game;
  private final boolean randomLadders;
  private int currentPlayerIndex = 0;

  public LadderGameController(boolean randomLadders) {
    this.randomLadders = randomLadders;
    game = new LadderGame(randomLadders);
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

    // Restore player positions
    if (gameState.getPlayerPositions() != null) {
      List<GameState.PlayerPosition> positions = gameState.getPlayerPositions();
      List<Player> players = game.getPlayers();

      for (int i = 0; i < players.size() && i < positions.size(); i++) {
        GameState.PlayerPosition pos = positions.get(i);
        Player player = players.get(i);

        // Find the tile with the saved ID and place player there
        Tile tile = game.getBoard().getTileByIdLinear(pos.getTileId());
        if (tile != null) {
          player.placePlayer(tile);
        }
      }
    }
  }
}