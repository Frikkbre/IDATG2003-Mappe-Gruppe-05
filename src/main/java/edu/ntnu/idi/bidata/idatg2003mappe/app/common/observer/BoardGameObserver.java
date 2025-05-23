package edu.ntnu.idi.bidata.idatg2003mappe.app.common.observer;

import edu.ntnu.idi.bidata.idatg2003mappe.entity.player.Player;
import edu.ntnu.idi.bidata.idatg2003mappe.map.Tile;

/**
 * <p>Observer interface for board game events.</p>
 * <p>This interface defines methods that are called when specific game events occur,
 * allowing components to react to changes in the game state.</p>
 *
 * @author Simen Gudbrandsen and Frikk Breadsroed
 * @version 0.0.1
 * @since 21.05.2025
 */
public interface BoardGameObserver {

  /**
   * <p>Called when a player has moved to a new tile.</p>
   * <p>Notifies observers about player movement including origin and destination tiles.</p>
   *
   * @param player   The player who moved
   * @param fromTile The tile the player moved from
   * @param toTile   The tile the player moved to
   */
  void onPlayerMoved(Player player, Tile fromTile, Tile toTile);

  /**
   * <p>Called when a die has been rolled during gameplay.</p>
   * <p>Notifies observers about the roll outcome and which player performed the roll.</p>
   *
   * @param player    The player who rolled the die
   * @param rollValue The value that was rolled (typically 1-6)
   */
  void onDieRolled(Player player, int rollValue);

  /**
   * <p>Called when the game has ended with a winner.</p>
   * <p>Notifies observers that the game has concluded and which player won.</p>
   *
   * @param winner The winning player
   */
  void onGameEnded(Player winner);

  /**
   * <p>Called when the turn changes to a new player.</p>
   * <p>Notifies observers about turn progression to the next player.</p>
   *
   * @param newCurrentPlayer The new current player whose turn it is now
   */
  void onTurnChanged(Player newCurrentPlayer);
}
