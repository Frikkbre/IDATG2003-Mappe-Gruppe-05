package edu.ntnu.idi.bidata.idatg2003mappe.entity;

import edu.ntnu.idi.bidata.idatg2003mappe.map.Tile;

/**
 * Observer interface for player events.
 *
 * @author Simen Gudbrandsen and Frikk Breadsroed
 * @version 0.0.1
 * @since 21.05.2025
 */
public interface PlayerObserver {

  /**
   * Called when a player has moved.
   *
   * @param player The player who moved.
   * @param oldTile The tile the player moved from.
   * @param newTile The tile the player moved to.
   */
  void onPlayerMoved(Player player, Tile oldTile, Tile newTile);
}