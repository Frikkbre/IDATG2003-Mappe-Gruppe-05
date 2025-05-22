package edu.ntnu.idi.bidata.idatg2003mappe.markers;

/**
 * A red gem marker.
 *
 * @author Simen Gudbrandsen and Frikk Breadsroed
 * @version 0.0.1
 * @since 03.02.2025
 */
public class RedGem extends Marker {
  private static final String TYPE = "RedGem";
  private static final int VALUE = 100;

  /**
   * Constructor for the RedGem class.
   */
  public RedGem() {
    super(TYPE, VALUE);
  }
}