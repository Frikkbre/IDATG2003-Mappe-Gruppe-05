package edu.ntnu.idi.bidata.idatg2003mappe.movement;

import edu.ntnu.idi.bidata.idatg2003mappe.entity.player.Player;

/**
 * <p>Represents a TileAction interface for defining actions that can be performed when a player lands on a tile.</p>
 * <p>This interface is part of the Strategy pattern implementation for different tile behaviors
 * that can affect player movement or game state.</p>
 *
 * @author Simen Gudbrandsen and Frikk Breadsroed
 * @version 0.0.1
 * @since 20.02.2025
 */

public interface TileAction {
  /**
   * <p>Performs the specific action associated with a tile when a player lands on it.</p>
   * <p>Implementations of this method define what happens to the player
   * when they interact with a specific type of tile.</p>
   *
   * @param player The player who triggered the tile action.
   */
  void performAction(Player player);
}
