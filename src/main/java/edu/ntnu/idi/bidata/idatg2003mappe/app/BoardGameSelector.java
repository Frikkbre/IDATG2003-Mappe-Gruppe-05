package edu.ntnu.idi.bidata.idatg2003mappe.app;

import edu.ntnu.idi.bidata.idatg2003mappe.app.laddergame.LadderGame;
import edu.ntnu.idi.bidata.idatg2003mappe.app.missingdiamond.MissingDiamond;

import java.util.InputMismatchException;

/**
 * Class that serves as a start screen for the user to select a game to play and how many players to play with.
 * The user can select between Ladder Game and Missing Diamond.
 * The user can also exit the program.
 *
 * @author Simen Gudbrandsen and Frikk Breadsroed
 * @version 0.0.1
 * @since 17.02.2025
 */

public class BoardGameSelector {
  int numberOfPlayers = 6;//TODO - make this variable

  /**
   * Constructor for the BoardGameSelector class.
   * Serves as a start screen for the user to select a game to play and how many players to play with.
   * The user can select between Ladder Game and Missing Diamond.
   * The user can also exit the program.
   */
  public BoardGameSelector() {
  }

  /**
   * Method that changes the scene from selector to the desired game.
   * @param gameSelector
   */
  public void switchGame(int gameSelector) { //TODO - use other params than int?
    setNumberOfPlayers(numberOfPlayers);
    switch (gameSelector) {
      case 1: {
        new LadderGame(numberOfPlayers, false);
        break;
      }
      case 2: {
        new MissingDiamond(numberOfPlayers);
        break;
      }
      case 0: {
        System.out.println("Exiting...");
        break;
      }
      default: {
        System.out.println("Invalid selection. Exiting...");
        break;
      }
    }
  }
  /**
   * Method that validates user input to check if it matches the requirements.
   * Throws an exception if the input is less than 1, more than 6 or a float value.
   *
   * @return number of players
   */
  public int setNumberOfPlayers(int numberOfPlayers) {
    int players = 0;
    boolean validInput = false;
    while (!validInput) {
      try {
        if (this.numberOfPlayers > 0 && this.numberOfPlayers < 7) {
          validInput = true;
        } else {
          throw new IllegalArgumentException("Amount of players must be between 1 and 6.");
        }
      } catch (InputMismatchException e) {
        throw new IllegalArgumentException("Invalid input. Please enter a valid number.");
      }
    }
    return numberOfPlayers;
  }
}