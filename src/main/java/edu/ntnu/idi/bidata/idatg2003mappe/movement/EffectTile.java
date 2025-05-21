package edu.ntnu.idi.bidata.idatg2003mappe.movement;

import edu.ntnu.idi.bidata.idatg2003mappe.entity.Player;
import edu.ntnu.idi.bidata.idatg2003mappe.map.Tile;

/**
 * Class that handles effect tiles in Ladder game
 * Has effects like making a player skip a turn or get extra points
 */
public class EffectTile implements TileAction {


  @Override
  public void performAction(Player player) {
    /*if(currentTile.getEffect() != null) {
      currentTile.getEffect().applyEffect(player);
    } else {
      System.out.println("No effect action on this tile.");
    }*/
    System.out.println("Effect action: applying effect to player on tile ");
  }

  public void skipTurn(Player player) {
    player.setSkipTurn(true);
  }
}
