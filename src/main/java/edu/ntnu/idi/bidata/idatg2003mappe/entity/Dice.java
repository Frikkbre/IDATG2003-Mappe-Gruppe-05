package edu.ntnu.idi.bidata.idatg2003mappe.entity;

//Imports
import java.util.concurrent.ThreadLocalRandom;


public class Dice {
  int number;

  public int rollDice(){
    int randomInt = ThreadLocalRandom.current().nextInt(1, 6 + 1);
    return randomInt;
  }
}
