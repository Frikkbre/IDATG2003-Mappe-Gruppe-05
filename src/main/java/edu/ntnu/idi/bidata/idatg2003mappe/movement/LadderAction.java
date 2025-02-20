package edu.ntnu.idi.bidata.idatg2003mappe.movement;

import edu.ntnu.idi.bidata.idatg2003mappe.map.Tile;
import edu.ntnu.idi.bidata.idatg2003mappe.entity.Player;

/**
 * Class for ladder action.
 * This class implements the TileAction interface.
 *
 * @version 0.1
 * @since 20.02.2025
 * @author Simen Gudbrandsen and Frikk Breadsroed
 */

public class LadderAction implements TileAction {

  private Tile currentTile;

  /**
   * Constructor for the LadderAction class.
   *
   * @param currentTile The current tile.
   */

  public LadderAction(Tile currentTile) {
    this.currentTile = currentTile;
  }

  /**
   * Method to perform the action of the ladder.
   * Moves the player to the destination tile.
   *
   * @param player The player to move.
   */

  @Override
  public void performAction(Player player) {
    Tile destination = currentTile.getDestinationTile();
    if (destination != null) {
      System.out.println("Ladder action: moving player from tile "
          + currentTile.getTileId() + " to tile "
          + destination.getTileId());
      player.placePlayer(destination);
    } else {
      System.out.println("No ladder action on this tile.");
    }
  }
}
