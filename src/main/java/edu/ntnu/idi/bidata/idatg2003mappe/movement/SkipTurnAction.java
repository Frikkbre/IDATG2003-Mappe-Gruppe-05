package edu.ntnu.idi.bidata.idatg2003mappe.movement;

import edu.ntnu.idi.bidata.idatg2003mappe.entity.player.Player;
import edu.ntnu.idi.bidata.idatg2003mappe.map.Tile;

/**
 * Action that causes a player to skip their next turn.
 *
 * @author Simen Gudbrandsen and Frikk Breadsroed
 * @version 0.0.1
 * @since 21.05.2025
 */
public class SkipTurnAction implements TileAction {

  private final Tile currentTile;

  /**
   * Constructor for the SkipTurnAction class.
   *
   * @param currentTile The current tile.
   */
  public SkipTurnAction(Tile currentTile) {
    this.currentTile = currentTile;
  }

  /**
   * Performs the skip turn action on the player.
   * This would need to be handled at the game level.
   *
   * @param player The player to perform the action on.
   */
  @Override
  public void performAction(Player player) {
    System.out.println("Skip turn action: " + player.getName() +
        " must skip their next turn.");
    // The actual skipping logic would be handled by the game controller
  }
}