package edu.ntnu.idi.bidata.idatg2003mappe.movement;

import edu.ntnu.idi.bidata.idatg2003mappe.entity.player.Player;
import edu.ntnu.idi.bidata.idatg2003mappe.map.Tile;

/**
 * Action that sends a player back to the start tile.
 *
 * @author Simen Gudbrandsen and Frikk Breadsroed
 * @version 0.0.1
 * @since 21.05.2025
 */
public class ResetToStartAction implements TileAction {

  private final Tile currentTile;

  /**
   * Constructor for the ResetToStartAction class.
   *
   * @param currentTile The current tile.
   */
  public ResetToStartAction(Tile currentTile) {
    this.currentTile = currentTile;
  }

  /**
   * Performs the reset to start action on the player.
   *
   * @param player The player to move back to start.
   */
  @Override
  public void performAction(Player player) {
    Tile destination = currentTile.getDestinationTile();
    if (destination != null) {
      System.out.println("Reset action: moving player " +
          player.getName() + " back to the start.");
      player.placePlayer(destination);
    } else {
      System.out.println("Reset action failed: start tile not set.");
    }
  }
}