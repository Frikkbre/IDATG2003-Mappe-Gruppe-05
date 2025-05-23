package edu.ntnu.idi.bidata.idatg2003mappe.markers;

/**
 * <p>Represents a Bandit marker in the Missing Diamond game.</p>
 * <p>Bandits are negative markers that cause players to lose all their money
 * when discovered. They are one of the risks players face when investigating
 * markers at special locations.</p>
 * <p>Unlike other markers, Bandits have a configurable penalty that determines
 * how harmful they are when encountered.</p>
 *
 * @author Simen Gudbrandsen and Frikk Breadsroed
 * @version 0.0.2
 * @since 03.02.2025
 */
public class Bandit extends Marker {
  /**
   * <p>The default penalty when a player encounters a bandit.</p>
   */
  private static final int DEFAULT_PENALTY = 2;

  /**
   * <p>The amount of steps the player will be moved back.</p>
   */
  private final int penaltySteps;

  /**
   * <p>Constructs a Bandit marker with default penalty.</p>
   * <p>Creates a Bandit with the standard penalty of 2 steps back.</p>
   */
  public Bandit() {
    this(DEFAULT_PENALTY);
  }

  /**
   * <p>Constructs a Bandit marker with a specific penalty.</p>
   * <p>Creates a Bandit that will cause the specified number of
   * penalty steps when encountered by a player.</p>
   *
   * @param penaltySteps The number of steps the player will be moved back
   * @throws IllegalArgumentException if penalty steps is negative
   */
  public Bandit(int penaltySteps) {
    super("Bandit", 0);
    if (penaltySteps < 0) {
      throw new IllegalArgumentException("Penalty steps cannot be negative");
    }
    this.penaltySteps = penaltySteps;
  }

  /**
   * <p>Returns a string representation of the Bandit.</p>
   * <p>Includes the penalty steps in the description to provide
   * more detailed information about this Bandit instance.</p>
   *
   * @return A string describing the Bandit and its penalty
   */
  @Override
  public String toString() {
    return String.format("Bandit (Penalty: %d steps)", penaltySteps);
  }
}
