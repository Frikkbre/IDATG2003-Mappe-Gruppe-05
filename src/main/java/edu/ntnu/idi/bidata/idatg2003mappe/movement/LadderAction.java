package edu.ntnu.idi.bidata.idatg2003mappe.movement;

import edu.ntnu.idi.bidata.idatg2003mappe.entity.player.Player;
import edu.ntnu.idi.bidata.idatg2003mappe.map.Tile;

import java.util.logging.Logger;

/**
 * <p>Class for ladder action that implements the {@link TileAction} interface.</p>
 * <p>This class handles the logic for "ladder" tile effects in board games, where
 * landing on a specific tile triggers movement to another destination tile, similar
 * to ladders in Snakes and Ladders.</p>
 *
 * @author Simen Gudbrandsen and Frikk Breadsroed
 * @version 0.0.1
 * @since 20.02.2025
 */

public class LadderAction implements TileAction {

  private static final Logger logger = Logger.getLogger(LadderAction.class.getName());
  private final Tile currentTile;

  /**
   * <p>Constructor for the LadderAction class.</p>
   * <p>Initializes a new ladder action associated with the specified tile.</p>
   *
   * @param currentTile The current tile that triggers the ladder effect.
   * @throws IllegalArgumentException if the currentTile parameter is null.
   */

  public LadderAction(Tile currentTile) {
    if (currentTile == null) {
      throw new IllegalArgumentException("Tile cannot be null");
    }
    this.currentTile = currentTile;
  }

  /**
   * <p>Method to perform the action of the ladder.</p>
   * <p>Moves the player from the current tile to the destination tile
   * defined in the tile's destination property. If no destination is set,
   * a warning is logged and no movement occurs.</p>
   *
   * @param player The player to move to the destination tile.
   */

  @Override
  public void performAction(Player player) {
    Tile destination = currentTile.getDestinationTile();
    if (destination != null) {
      player.placePlayer(destination);
    } else {
      logger.warning("No ladder action on this tile.");
    }
  }
}
