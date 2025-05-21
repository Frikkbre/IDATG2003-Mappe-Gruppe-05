package edu.ntnu.idi.bidata.idatg2003mappe.entity.die;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Represents a die with Observer pattern support.
 *
 * @author Simen Gudbrandsen and Frikk Breadsroed
 * @version 0.0.4
 * @since 25.05.2025
 */
public class Die {

  private int dieValue;

  // Observer pattern support
  private List<DieObserver> observers = new ArrayList<>();

  /**
   * Adds an observer to the die.
   *
   * @param observer The observer to add.
   */
  public void addObserver(DieObserver observer) {
    observers.add(observer);
  }

  /**
   * Removes an observer from the die.
   *
   * @param observer The observer to remove.
   */
  public void removeObserver(DieObserver observer) {
    observers.remove(observer);
  }

  /**
   * Notifies observers that the die has been rolled.
   *
   * @param value The value rolled.
   */
  private void notifyDieRolled(int value) {
    for (DieObserver observer : observers) {
      observer.onDieRolled(value);
    }
  }

  /**
   * Return an integer between 1 and 6 (inclusive) to simulate a die.
   *
   * @return int
   */
  public int rollDie() {
    dieValue = ThreadLocalRandom.current().nextInt(1, 6 + 1);

    // Notify observers about the roll
    notifyDieRolled(dieValue);

    return dieValue;
  }

  /**
   * Returns true if "die roll" is 4,5 or 6 and false otherwise
   *
   * @return boolean
   */
  public boolean rollToTurnMarker() {
    dieValue = ThreadLocalRandom.current().nextInt(3, 7);

    // Notify observers about the roll
    notifyDieRolled(dieValue);

    return dieValue > 3;
  }

  public void setDieValue(int dieValue) {
    if (dieValue < 1 || dieValue > 6) {
      throw new IllegalArgumentException("Die value must be between 1 and 6.");
    }
    this.dieValue = dieValue;
  }

  /**
   * Returns a random number between 1 and 6.
   *
   * @return int
   */

  public int getDieValue() {
    return dieValue;
  }

}