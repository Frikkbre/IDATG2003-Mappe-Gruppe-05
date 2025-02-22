package edu.ntnu.idi.bidata.idatg2003mappe.entity;

//Imports

import java.util.concurrent.ThreadLocalRandom;

/**
 * Represents a die.
 *
 * @author Simen Gudbrandsen and Frikk Breadsroed
 * @version 0.0.2
 * @since 03.02.2025
 */

public class Die {

  /**
   * Return an integer between 1 and 6 (inclusive) to simulate a die.
   *
   * @return int
   */
  public int rollDie() {
    return ThreadLocalRandom.current().nextInt(1, 6 + 1); //TODO - assign value to variable?
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
}