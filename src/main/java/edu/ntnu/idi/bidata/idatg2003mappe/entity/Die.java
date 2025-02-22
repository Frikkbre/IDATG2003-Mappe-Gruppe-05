package edu.ntnu.idi.bidata.idatg2003mappe.entity;

//Imports

import java.util.concurrent.ThreadLocalRandom;

/**
 * Represents a die.
 *
 * @author Simen Gudbrandsen and Frikk Breadsroed
 * @version 0.0.3
 * @since 03.02.2025
 */

public class Die {

  private int dieValue;

  /**
   * Return an integer between 1 and 6 (inclusive) to simulate a die.
   *
   * @return int
   */
  public int rollDie() {
    dieValue = ThreadLocalRandom.current().nextInt(1, 6 + 1);
    return dieValue;
  }

  /**
   * Returns true if "die roll" is 4,5 or 6 and false otherwise
   *
   * @return boolean
   */
  public boolean rollToTurnMarker() { //Only relevant for "Den forsvunne diamanten"?
    int number = ThreadLocalRandom.current().nextInt(1, 6 + 1);
    return number > 3;
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