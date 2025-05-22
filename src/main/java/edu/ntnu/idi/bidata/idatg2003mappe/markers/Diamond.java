package edu.ntnu.idi.bidata.idatg2003mappe.markers;

/**
 * A diamond marker.
 *
 * @author Simen Gudbrandsen and Frikk Breadsroed
 * @version 0.0.1
 * @since 03.02.2025
 */
public class Diamond extends Marker {
  private static final String TYPE = "Diamond";
  private static final int VALUE = 2000;
  private boolean isMissing;

  /**
   * Constructor for the Diamond class.
   */
  public Diamond() {
    super(TYPE, VALUE);
    this.isMissing = true;
  }

  /**
   * Checks if the diamond is missing.
   *
   * @return true if the diamond is missing, false otherwise
   */
  public boolean isMissing() {
    return isMissing;
  }

  /**
   * Sets whether the diamond is missing.
   *
   * @param missing Whether the diamond is missing
   */
  public void setMissing(boolean missing) {
    isMissing = missing;
  }

  /**
   * Finds the diamond (sets it as not missing).
   */
  public void find() {
    isMissing = false;
    reveal();
  }
}