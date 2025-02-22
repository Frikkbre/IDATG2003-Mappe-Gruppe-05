package edu.ntnu.idi.bidata.idatg2003mappe.movement;

import edu.ntnu.idi.bidata.idatg2003mappe.entity.Player;

/**
 * Represents a TileAction.
 *
 * @version 0.0.1
 * @since 20.02.2025
 * @author Simen Gudbrandsen and Frikk Breadsroed
 */

public interface TileAction {
  void performAction(Player player);
}
