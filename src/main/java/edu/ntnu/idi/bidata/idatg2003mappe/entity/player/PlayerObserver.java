package edu.ntnu.idi.bidata.idatg2003mappe.entity.player;

import edu.ntnu.idi.bidata.idatg2003mappe.map.Tile;

/**
 * <p>Observer interface for player events.</p>
 * <p>This interface is part of the Observer design pattern implementation
 * for player-related events. Classes that implement this interface can
 * receive notifications about player actions and state changes.</p>
 * <p>The observer pattern allows various components to react to player events without
 * direct coupling between the player and those components. Events include:</p>
 * <ul>
 *   <li>Player movement between tiles</li>
 *   <li>Inventory changes</li>
 *   <li>Status effect applications</li>
 * </ul>
 *
 * @author Simen Gudbrandsen and Frikk Breadsroed
 * @version 0.0.1
 * @since 21.05.2025
 */
public interface PlayerObserver {

  /**
   * <p>Called when a player has moved.</p>
   * <p>This method is invoked by the observed {@link Player} object whenever
   * the player changes position on the board. Both the previous and new
   * tile positions are provided to allow observers to respond appropriately.</p>
   *
   * @param player  The {@link Player} who moved
   * @param oldTile The {@link Tile} the player moved from
   * @param newTile The {@link Tile} the player moved to
   */
  void onPlayerMoved(Player player, Tile oldTile, Tile newTile);
}