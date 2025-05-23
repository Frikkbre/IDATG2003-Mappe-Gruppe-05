package edu.ntnu.idi.bidata.idatg2003mappe.movement;

import edu.ntnu.idi.bidata.idatg2003mappe.map.Tile;

/**
 * <p>Factory class for creating TileAction objects.</p>
 * <p>This factory provides methods to create various types of tile actions
 * that can be applied to tiles on a game board.</p>
 *
 * @author Simen Gudbrandsen and Frikk Breadsroed
 * @version 0.0.1
 * @since 21.05.2025
 */
public class TileActionFactory {

  /**
   * <p>Creates a ladder action that moves a player to a destination tile.</p>
   * <p>This method associates the destination tile with the current tile and
   * creates a new {@link LadderAction} object that can be triggered when a player
   * lands on the current tile.</p>
   *
   * @param currentTile     The tile where the action is placed.
   * @param destinationTile The destination tile where the player will move to.
   * @return A {@link LadderAction} object that can be used to move players.
   * @throws IllegalArgumentException if any of the parameters are null.
   */
  public static TileAction createLadderAction(Tile currentTile, Tile destinationTile) {
    currentTile.setDestinationTile(destinationTile);
    return new LadderAction(currentTile);
  }

}
