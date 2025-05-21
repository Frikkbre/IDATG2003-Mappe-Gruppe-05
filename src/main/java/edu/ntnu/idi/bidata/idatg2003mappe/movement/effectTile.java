package edu.ntnu.idi.bidata.idatg2003mappe.movement;

import edu.ntnu.idi.bidata.idatg2003mappe.entity.Player;
import edu.ntnu.idi.bidata.idatg2003mappe.map.Tile;

/**
 * Class that handles effect tiles in Ladder game
 * Has effects like making a player skip a turn or get extra points
 */
public class effectTile implements TileAction {

  private final Tile currentTile;

  public effectTile(Tile currentTile) {
    this.currentTile = currentTile;

  }

  @Override
  public void performAction(Player player) {
    if(currentTile.getEffect() != null) {
      currentTile.getEffect().applyEffect(player);
    } else {
      System.out.println("No effect action on this tile.");
    }
  }

  public void skipTurn(Player player) {
    player.setSkipTurn(true);
  }
}
