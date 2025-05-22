package edu.ntnu.idi.bidata.idatg2003mappe.movement;

import edu.ntnu.idi.bidata.idatg2003mappe.entity.player.Player;
import edu.ntnu.idi.bidata.idatg2003mappe.map.Tile;

/**
 * Class that handles effect tiles in board games.
 *
 * @author Simen Gudbrandsen and Frikk Breadsroed
 * @version 0.0.1
 * @since 21.05.2025
 */
public class EffectTile implements TileAction {
  private final Tile currentTile;
  private final String effectType;
  private final Tile startTile; // Add this field

  /**
   * Constructor for the EffectTile class.
   *
   * @param currentTile The tile where the effect is triggered
   * @param effectType  The type of effect to apply
   * @param startTile
   */
  public EffectTile(Tile currentTile, String effectType, Tile startTile) {
    this.currentTile = currentTile;
    this.effectType = effectType;
    this.startTile = startTile; // Store the start tile
  }

  /**
   * Performs the effect action on the player based on the effect type.
   *
   * @param player The player to apply the effect to
   */
  @Override
  public void performAction(Player player) {
    if (effectType == null) {
      return;
    }

    switch (effectType) {
      case "skipTurn":
        skipTurn(player);
        break;

      case "backToStart":
        backToStart(player);
        break;

      default:

        break;
    }
  }

  /**
   * Makes the player skip their next turn.
   *
   * @param player The player who will skip their turn
   */
  public void skipTurn(Player player) {
    player.setSkipTurn(true);
  }

  /**
   * Moves the player back to the start tile.
   *
   * @param player
   */
  public void backToStart(Player player) {
    player.placePlayer(startTile);
  }
}