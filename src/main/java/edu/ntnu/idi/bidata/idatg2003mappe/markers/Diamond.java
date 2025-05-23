package edu.ntnu.idi.bidata.idatg2003mappe.markers;

/**
 * <p>A diamond marker representing the main objective in the Missing Diamond game.</p>
 * <p>The diamond is the most valuable marker and the primary goal for players to find.
 * Finding and returning the diamond to a starting city is one of the win conditions.</p>
 * <p>Unlike other markers, the Diamond has an additional property tracking whether
 * it's missing (not found by any player yet), which affects game mechanics.</p>
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
   * <p>Constructor for the Diamond class.</p>
   * <p>Initializes a new Diamond marker with predefined type and value.
   * The diamond starts in the "missing" state, meaning no player has found it yet.</p>
   */
  public Diamond() {
    super(TYPE, VALUE);
    this.isMissing = true;
  }

  /**
   * <p>Checks if the diamond is missing.</p>
   * <p>Returns whether the diamond has been found by any player yet.
   * This affects win conditions for players with visa cards.</p>
   *
   * @return <code>true</code> if the diamond is still missing, <code>false</code> if it has been found
   */
  public boolean isMissing() {
    return isMissing;
  }

  /**
   * <p>Sets whether the diamond is missing.</p>
   * <p>Updates the missing status of the diamond, which can affect
   * game mechanics such as win conditions for players with visas.</p>
   *
   * @param missing <code>true</code> to mark the diamond as missing, <code>false</code> to mark it as found
   */
  public void setMissing(boolean missing) {
    isMissing = missing;
  }

  /**
   * <p>Finds the diamond (sets it as not missing).</p>
   * <p>Marks the diamond as found and reveals it. This is typically called
   * when a player discovers the diamond during gameplay.</p>
   */
  public void find() {
    isMissing = false;
    reveal();
  }
}
