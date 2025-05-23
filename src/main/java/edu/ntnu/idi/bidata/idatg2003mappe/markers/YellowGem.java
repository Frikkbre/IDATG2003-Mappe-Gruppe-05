package edu.ntnu.idi.bidata.idatg2003mappe.markers;

/**
 * <p>A yellow gem marker in the Missing Diamond game.</p>
 * <p>Yellow gems are valuable items that players can collect during the game.
 * When a player finds a yellow gem, they receive its monetary value.</p>
 * <p>Yellow gems (topazes) are the most valuable of the regular gems,
 * providing substantial rewards when discovered.</p>
 *
 * @author Simen Gudbrandsen and Frikk Breadsroed
 * @version 0.0.1
 * @since 03.02.2025
 */
public class YellowGem extends Marker {
  private static final String TYPE = "YellowGem";
  private static final int VALUE = 1000;

  /**
   * <p>Constructor for the YellowGem class.</p>
   * <p>Initializes a new YellowGem marker with predefined type and value.
   * Yellow gems have a standard value of 1000 currency units.</p>
   */
  public YellowGem() {
    super(TYPE, VALUE);
  }
}
