package edu.ntnu.idi.bidata.idatg2003mappe.movement;

import edu.ntnu.idi.bidata.idatg2003mappe.entity.Player;
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

  /**
   * Constructor for the EffectTile class.
   *
   * @param currentTile The tile where the effect is triggered
   * @param effectType The type of effect to apply
   */
  public EffectTile(Tile currentTile, String effectType) {
    this.currentTile = currentTile;
    this.effectType = effectType;
  }

  /**
   * Performs the effect action on the player based on the effect type.
   *
   * @param player The player to apply the effect to
   */
  @Override
  public void performAction(Player player) {
    if (effectType == null) {
      System.out.println("No effect action on this tile.");
      return;
    }

    switch (effectType) {
      case "skipTurn":
        skipTurn(player);
        System.out.println(player.getName() + " will skip their next turn!");
        break;
      case "backToStart":
        backToStart(player);
        System.out.println(player.getName() + " Has to go back to start!");
        break;
      default:
        System.out.println("Unknown effect type: " + effectType);
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

  public void backToStart(Player player) {
    player.movePlayer(-44); // Move back to start
  }
}