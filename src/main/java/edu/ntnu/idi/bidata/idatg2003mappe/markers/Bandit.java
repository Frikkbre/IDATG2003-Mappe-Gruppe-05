package edu.ntnu.idi.bidata.idatg2003mappe.markers;

/**
 * Represents a Bandit marker in the Missing Diamond game.
 * Bandits can interact with players, potentially causing negative effects.
 *
 * @author Simen Gudbrandsen and Frikk Breadsroed
 * @version 0.0.2
 * @since 03.02.2025
 */
public class Bandit extends Marker {
  /**
   * The default penalty when a player encounters a bandit.
   */
  private static final int DEFAULT_PENALTY = 2;

  /**
   * The amount of steps the player will be moved back.
   */
  private final int penaltySteps;

  /**
   * Constructs a Bandit marker with default penalty.
   */
  public Bandit() {
    this(DEFAULT_PENALTY);
  }

  /**
   * Constructs a Bandit marker with a specific penalty.
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

  @Override
  public String toString() {
    return String.format("Bandit (Penalty: %d steps)", penaltySteps);
  }
}