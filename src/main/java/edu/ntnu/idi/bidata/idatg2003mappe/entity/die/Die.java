package edu.ntnu.idi.bidata.idatg2003mappe.entity.die;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * <p>Represents a die with Observer pattern support.</p>
 * <p>This class simulates a six-sided die that can be rolled to produce
 * random values between 1 and 6. It also implements the Observer pattern
 * to notify interested components when the die is rolled.</p>
 * <p>Features include:</p>
 * <ul>
 *   <li>Standard die rolling (1-6)</li>
 *   <li>Special rolling for token interactions (4-6 for success)</li>
 *   <li>Observer notifications when the die is rolled</li>
 * </ul>
 *
 * @author Simen Gudbrandsen and Frikk Breadsroed
 * @version 0.0.4
 * @since 03.02.2025
 */
public class Die {

  private int dieValue;

  // Observer pattern support
  private final List<DieObserver> observers = new ArrayList<>();

  /**
   * <p>Notifies observers that the die has been rolled.</p>
   * <p>Calls the {@code onDieRolled} method on all registered observers,
   * passing the new die value.</p>
   *
   * @param value The value rolled.
   */
  private void notifyDieRolled(int value) {
    observers.forEach(observer -> observer.onDieRolled(value));
  }

  /**
   * <p>Return an integer between 1 and 6 (inclusive) to simulate a die.</p>
   * <p>Generates a random number using {@code ThreadLocalRandom} and
   * notifies all observers about the roll.</p>
   *
   * @return The rolled value (1-6)
   */
  public int rollDie() {
    dieValue = ThreadLocalRandom.current().nextInt(1, 6 + 1);

    // Notify observers about the roll
    notifyDieRolled(dieValue);

    return dieValue;
  }

  /**
   * <p>Returns true if "die roll" is 4, 5 or 6 and false otherwise.</p>
   * <p>This specialized roll is used for token interactions,
   * where higher values (4-6) represent success.</p>
   * <p>Note: This method actually generates values from 4-6 only when returning true,
   * and 1-3 when returning false, to provide a visual association with the success/failure.</p>
   *
   * @return {@code true} if the roll is 4 or higher, {@code false} otherwise
   */
  public boolean rollToTurnMarker() {
    dieValue = ThreadLocalRandom.current().nextInt(3, 7);

    // Notify observers about the roll
    notifyDieRolled(dieValue);

    return dieValue > 3;
  }

  /**
   * <p>Sets the die value directly.</p>
   * <p>Allows manual setting of the die value, primarily for testing
   * or for predetermined scenarios.</p>
   *
   * @param dieValue The value to set (must be 1-6)
   * @throws IllegalArgumentException if the value is outside the range 1-6
   */
  public void setDieValue(int dieValue) {
    if (dieValue < 1 || dieValue > 6) {
      throw new IllegalArgumentException("Die value must be between 1 and 6.");
    }
    this.dieValue = dieValue;
  }

  /**
   * <p>Gets the current die value.</p>
   * <p>Returns the last rolled or set value of the die.</p>
   *
   * @return The current die value
   */
  public int getDieValue() {
    return dieValue;
  }
}
