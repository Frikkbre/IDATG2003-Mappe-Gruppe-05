package edu.ntnu.idi.bidata.idatg2003mappe.markers;

/**
 * Represents a blank marker in the Missing Diamond game.
 * Blank markers have no special effect.
 *
 * @author Simen Gudbrandsen and Frikk Breadsroed
 * @version 0.0.1
 * @since 23.05.2025
 */
public class BlankMarker extends Marker {
  private static final String TYPE = "Blank";
  private static final int VALUE = 0;

  /**
   * Constructor for the BlankMarker class.
   */
  public BlankMarker() {
    super(TYPE, VALUE);
  }
}