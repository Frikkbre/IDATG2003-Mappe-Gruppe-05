package edu.ntnu.idi.bidata.idatg2003mappe.markers;

/**
 * A green gem marker.
 *
 * @author Simen Gudbrandsen and Frikk Breadsroed
 * @version 0.0.1
 * @since 03.02.2025
 */
public class GreenGem extends Marker {
  private static final String TYPE = "GreenGem";
  private static final int VALUE = 4000;

  /**
   * Constructor for the GreenGem class.
   */
  public GreenGem() {
    super(TYPE, VALUE);
  }
}