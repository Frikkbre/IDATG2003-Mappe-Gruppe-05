package edu.ntnu.idi.bidata.idatg2003mappe.movement;

import edu.ntnu.idi.bidata.idatg2003mappe.map.Tile;

/**
 * Factory class for creating TileAction objects.
 *
 * @author Simen Gudbrandsen and Frikk Breadsroed
 * @version 0.0.1
 * @since 21.05.2025
 */
public class TileActionFactory {

  /**
   * Creates a ladder action that moves a player to a destination tile.
   *
   * @param currentTile     The tile where the action is placed.
   * @param destinationTile The destination tile.
   * @return A LadderAction object.
   */
  public static TileAction createLadderAction(Tile currentTile, Tile destinationTile) {
    currentTile.setDestinationTile(destinationTile);
    return new LadderAction(currentTile);
  }

}