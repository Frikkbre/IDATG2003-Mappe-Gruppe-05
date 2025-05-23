package edu.ntnu.idi.bidata.idatg2003mappe.markers;

/**
 * <p>Represents a Visa marker in the Missing Diamond game.</p>
 * <p>A Visa is a special marker that provides an alternative win condition.
 * If a player has a Visa and returns to a starting city after the diamond
 * has been found by any player, they win the game.</p>
 * <p>Visas serve as a "backup plan" for players who don't find the diamond,
 * giving them another path to victory.</p>
 *
 * @author Simen Gudbrandsen and Frikk Breadsroed
 * @version 0.0.2
 * @since 03.02.2025
 */
public class Visa extends Marker {
  private static final String TYPE = "Visa";
  private static final int VALUE = 1500;

  /**
   * <p>Constructor for the Visa class.</p>
   * <p>Initializes a new Visa marker with predefined type and value.
   * Visas have a standard value of 1500 currency units, though their
   * main value is in providing an alternative win condition.</p>
   */
  public Visa() {
    super(TYPE, VALUE);
  }
}
