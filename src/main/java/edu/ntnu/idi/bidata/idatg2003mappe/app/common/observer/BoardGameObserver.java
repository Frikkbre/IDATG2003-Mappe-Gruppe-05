package edu.ntnu.idi.bidata.idatg2003mappe.app.common.observer;

import edu.ntnu.idi.bidata.idatg2003mappe.entity.player.Player;
import edu.ntnu.idi.bidata.idatg2003mappe.map.Tile;

/**
 * Observer interface for board game events.
 *
 * @author Simen Gudbrandsen and Frikk Breadsroed
 * @version 0.0.1
 * @since 21.05.2025
 */
public interface BoardGameObserver {

  /**
   * Called when a player has moved.
   *
   * @param player   The player who moved.
   * @param fromTile The tile the player moved from.
   * @param toTile   The tile the player moved to.
   */
  void onPlayerMoved(Player player, Tile fromTile, Tile toTile);

  /**
   * Called when a die has been rolled.
   *
   * @param player    The player who rolled.
   * @param rollValue The value rolled.
   */
  void onDieRolled(Player player, int rollValue);

  /**
   * Called when the game has ended.
   *
   * @param winner The winning player.
   */
  void onGameEnded(Player winner);

  /**
   * Called when the turn changes to a new player.
   *
   * @param newCurrentPlayer The new current player.
   */
  void onTurnChanged(Player newCurrentPlayer);
}