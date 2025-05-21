package edu.ntnu.idi.bidata.idatg2003mappe.markers;

/**
 * Represents a Visa marker in the Missing Diamond game.
 * A Visa allows a player to travel by airplane for free without paying the normal fee.
 *
 * @author Simen Gudbrandsen and Frikk Breadsroed
 * @version 0.0.2
 * @since 03.02.2025
 */
public class Visa extends Marker {
  private static final String TYPE = "Visa";
  private static final int VALUE = 1500;

  /**
   * Constructor for the Visa class.
   */
  public Visa() {
    super(TYPE, VALUE);
  }
}