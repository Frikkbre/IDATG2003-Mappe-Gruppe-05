package edu.ntnu.idi.bidata.idatg2003mappe.app.missingdiamond;

import edu.ntnu.idi.bidata.idatg2003mappe.entity.Player;
import edu.ntnu.idi.bidata.idatg2003mappe.map.Tile;

import java.util.List;

/**
 * Controller class for the Missing Diamond game.
 * This class handles the game logic and player interactions.
 *
 * @author Simen Gudbrandsen and Frikk Breadsroed
 * @version 0.0.1
 * @since 23.04.2025
 */
public class MissingDiamondController {
  private final MissingDiamond game;

  public MissingDiamondController(int numberOfPlayers) {
    this.game = new MissingDiamond(numberOfPlayers);
  }

  public String movePlayer(int tileId) {
    Player currentPlayer = game.getCurrentPlayer();
    Tile currentTile = currentPlayer.getCurrentTile();
    Tile destinationTile = game.getBoard().getTileById(tileId);

    if (destinationTile == null) {
      return "Invalid tile ID.";
    }

    // Check if the destination tile is a valid move (connected to current tile)
    if (!currentTile.getNextTiles().contains(destinationTile)) {
      return "Cannot move to tile " + tileId + " from the current position.";
    }

    // Move the player
    currentPlayer.placePlayer(destinationTile);

    String message = currentPlayer.getName() + " moved to tile " + tileId + ".";

    // This is a simplified implementation, additional game logic can be added here

    return message;
  }

  public String playTurn() {
    return game.playTurn();
  }

  public List<Player> getPlayers() {
    return game.getPlayers();
  }

  public Player getCurrentPlayer() {
    return game.getCurrentPlayer();
  }

  public int getCurrentPlayerIndex() {
    return game.getCurrentPlayerIndex();
  }

  public boolean isGameFinished() {
    return game.isGameFinished();
  }

  public Tile getTileById(int tileId) {
    return game.getBoard().getTileById(tileId);
  }

  public List<Tile> getPossibleMoves() {
    return game.getCurrentPlayer().getCurrentTile().getNextTiles();
  }
}
