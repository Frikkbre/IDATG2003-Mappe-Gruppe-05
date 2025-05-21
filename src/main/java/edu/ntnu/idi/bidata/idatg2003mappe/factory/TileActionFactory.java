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
   * @param currentTile The tile where the action is placed.
   * @param destinationTile The destination tile.
   * @return A LadderAction object.
   */
  public static TileAction createLadderAction(Tile currentTile, Tile destinationTile) {
    currentTile.setDestinationTile(destinationTile);
    return new LadderAction(currentTile);
  }

  /**
   * Creates a ladder action for an existing tile with destination.
   *
   * @param tileWithDestination A tile that already has a destination set.
   * @return A LadderAction object.
   */
  public static TileAction createLadderAction(Tile tileWithDestination) {
    return new LadderAction(tileWithDestination);
  }

  /**
   * Creates a SkipTurnAction that causes a player to skip their next turn.
   *
   * @param tile The tile where the action is placed.
   * @return A SkipTurnAction object.
   */
  public static TileAction createSkipTurnAction(Tile tile) {
    return new SkipTurnAction(tile);
  }

  /**
   * Creates a ResetToStartAction that sends a player back to the start.
   *
   * @param tile The tile where the action is placed.
   * @param startTile The start tile of the game.
   * @return A ResetToStartAction object.
   */
  public static TileAction createResetToStartAction(Tile tile, Tile startTile) {
    tile.setDestinationTile(startTile);
    return new ResetToStartAction(tile);
  }
}