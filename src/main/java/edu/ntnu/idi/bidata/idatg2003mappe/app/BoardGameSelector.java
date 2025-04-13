package edu.ntnu.idi.bidata.idatg2003mappe.app;

import edu.ntnu.idi.bidata.idatg2003mappe.app.laddergame.LadderGame;
import edu.ntnu.idi.bidata.idatg2003mappe.app.missingdiamond.MissingDiamond;

import java.util.InputMismatchException;
import java.util.Scanner;

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
  int numberOfPlayers;

  private final Scanner inputScanner = new Scanner(System.in);

  /**
   * Constructor for the BoardGameSelector class.
   * Serves as a start screen for the user to select a game to play and how many players to play with.
   * The user can select between Ladder Game and Missing Diamond.
   * The user can also exit the program.
   */
  public BoardGameSelector() {
    /*System.out.println("Select a game to play:"); TODO - Remove TUI commands?
    System.out.println("1: Ladder Game");
    System.out.println("2: Missing Diamond");
    System.out.println("0: Exit");
    int gameSelector = inputScanner.nextInt();*/

  }
  public void switchGame(int gameSelector) { //TODO - use other params than int?
    switch (gameSelector) {
      case 1: {
        numberOfPlayers = getNumberOfPlayers();
        new LadderGame(numberOfPlayers, false);
        System.out.println("Starting Ladder Game with " + numberOfPlayers + " players.");
        break;
      }
      case 2: {
        numberOfPlayers = getNumberOfPlayers();
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
   * Method that takes user input to get the number of players to play the game.
   * Throws an exception if the input is less than 1, more than 6 or a float value.
   *
   * @return number of players
   */
  public int getNumberOfPlayers() {
    int players = 0;
    boolean validInput = false;
    while (!validInput) {
      try {
        System.out.println("Enter number of players:");
        players = inputScanner.nextInt();
        if (players > 0 && players < 7) {
          validInput = true;
        } else {
          System.out.println("Number of players must be a positive integer less than 7. Please try again.");
        }
      } catch (InputMismatchException e) {
        System.out.println("Invalid input. Please enter a valid number.");
        inputScanner.next(); // Clear the invalid input
      }
    }
    return players;
  }
}