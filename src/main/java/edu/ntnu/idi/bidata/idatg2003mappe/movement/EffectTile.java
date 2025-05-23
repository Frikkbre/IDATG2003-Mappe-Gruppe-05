package edu.ntnu.idi.bidata.idatg2003mappe.movement;

import edu.ntnu.idi.bidata.idatg2003mappe.entity.player.Player;
import edu.ntnu.idi.bidata.idatg2003mappe.map.Tile;

/**
 * <p>Class that handles effect tiles in board games.</p>
 * <p>This class implements the {@link TileAction} interface and provides
 * functionality for tiles that apply special effects to players
 * who land on them, such as skipping turns or moving to a different location.</p>
 *
 * @author Simen Gudbrandsen and Frikk Breadsroed
 * @version 0.0.1
 * @since 21.05.2025
 */
public class EffectTile implements TileAction {
  private final Tile currentTile;
  private final String effectType;
  private final Tile startTile;

  /**
   * <p>Constructor for the EffectTile class.</p>
   * <p>Initializes a new effect tile with the specified parameters.</p>
   *
   * @param currentTile The tile where the effect is triggered.
   * @param effectType  The type of effect to apply (e.g., "skipTurn", "backToStart").
   * @param startTile   The starting tile of the game board, used for effects that return players to start.
   */
  public EffectTile(Tile currentTile, String effectType, Tile startTile) {
    this.currentTile = currentTile;
    this.effectType = effectType;
    this.startTile = startTile;
  }

  /**
   * <p>Performs the effect action on the player based on the effect type.</p>
   * <p>This method determines which specific effect to apply based on the
   * effectType property and calls the appropriate method.</p>
   *
   * @param player The player to apply the effect to.
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
        // No action for unknown effect types
        break;
    }
  }

  /**
   * <p>Makes the player skip their next turn.</p>
   * <p>Sets the skipTurn flag on the player to true, which should be
   * checked by the game controller during turn management.</p>
   *
   * @param player The player who will skip their next turn.
   */
  public void skipTurn(Player player) {
    player.setSkipTurn(true);
  }

  /**
   * <p>Moves the player back to the start tile.</p>
   * <p>Places the player on the start tile of the game board,
   * effectively resetting their position.</p>
   *
   * @param player The player to move back to the start tile.
   */
  public void backToStart(Player player) {
    player.placePlayer(startTile);
  }
}
