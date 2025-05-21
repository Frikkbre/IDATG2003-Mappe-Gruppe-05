package edu.ntnu.idi.bidata.idatg2003mappe.app.missingdiamond.controller;

import edu.ntnu.idi.bidata.idatg2003mappe.app.missingdiamond.model.MissingDiamond;
import edu.ntnu.idi.bidata.idatg2003mappe.entity.player.Player;
import edu.ntnu.idi.bidata.idatg2003mappe.map.Tile;
import edu.ntnu.idi.bidata.idatg2003mappe.util.MapDesignerListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Controller class for the Missing Diamond game.
 * This class handles the game logic and player interactions.
 *
 * @author Simen Gudbrandsen and Frikk Breadsroed
 * @version 0.0.2
 * @since 23.04.2025
 */
public class MissingDiamondController {
  private final MissingDiamond game;
  private boolean hasRolled = false;
  private MapDesignerListener view;

  public MissingDiamondController() {
    this.game = new MissingDiamond();
  }

  public String playTurn() {
    // Check if player has already rolled
    if (hasRolled) {
      return "Player " + getCurrentPlayer().getName() + " must move before rolling again.";
    }

    // Roll the die
    String result = game.playTurn();
    hasRolled = true;

    // Check if there are any valid moves after rolling
    List<Tile> possibleMoves = getPossibleMoves();
    if (possibleMoves.isEmpty()) {
      // No valid moves, so automatically end turn
      hasRolled = false;
      game.skipTurn(); // You'll need to add this method to MissingDiamond class
      return result + "\nNo valid moves available. Turn passed to next player.";
    }

    return result;
  }

  public String movePlayer(int tileId) {
    // Check if player has rolled
    if (!hasRolled) {
      return "Player must roll the die first.";
    }

    // Get destination tile
    Tile destinationTile = game.getBoard().getTileById(tileId);
    if (destinationTile == null) {
      return "Invalid tile ID.";
    }

    // Check if the destination tile is a valid move based on the die roll
    Set<Tile> validMoves = game.getPossibleMovesForCurrentRoll();
    if (!validMoves.contains(destinationTile)) {
      return "Cannot move to tile " + tileId + " - it's not exactly " +
          game.getCurrentRoll() + " steps away.";
    }

    // Move the player
    String moveResult = game.movePlayerToTile(destinationTile);

    // Reset rolled state after move
    hasRolled = false;

    return moveResult;
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

  public int getCurrentRoll() {
    return game.getCurrentRoll();
  }

  public boolean isGameFinished() {
    return game.isGameFinished();
  }

  public Tile getTileById(int tileId) {
    return game.getBoard().getTileById(tileId);
  }

  public List<Tile> getPossibleMoves() {
    if (!hasRolled) {
      return new ArrayList<>();
    }
    return new ArrayList<>(game.getPossibleMovesForCurrentRoll());
  }

  public boolean hasRolled() {
    return hasRolled;
  }

  public void registerView(MapDesignerListener view) {
    this.view = view;
  }
}