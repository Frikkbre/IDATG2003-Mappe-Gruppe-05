package edu.ntnu.idi.bidata.idatg2003mappe.entity;

//Imports
import java.util.concurrent.ThreadLocalRandom;


public class Dice {

  /**
   * Return an integer between 1 and 6 (inclusive) to simulate a dice.
   *
   * @return int
   */
  public int rollDice() {
    return ThreadLocalRandom.current().nextInt(1, 6 + 1); //TODO - assign value to variable?
  }

  /**
   * Returns true if "dice roll" is 4,5 or 6 and false otherwise
   *
   * @return boolean
   */
  public boolean rollToTurnMarker() {
    int number = ThreadLocalRandom.current().nextInt(1, 6 + 1);
    return number > 3;
  }
}