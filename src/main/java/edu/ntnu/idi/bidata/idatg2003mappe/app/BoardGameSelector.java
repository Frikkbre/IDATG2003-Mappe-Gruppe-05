package edu.ntnu.idi.bidata.idatg2003mappe.app;

import java.util.InputMismatchException;
import java.util.Scanner;

public class BoardGameSelector {
  int numberOfPlayers;

  private Scanner inputScanner = new Scanner(System.in);

  /**
   * Constructor for the BoardGameSelector class.
   * Serves as a start screen for the user to select a game to play and how many players to play with.
   */
  public BoardGameSelector() {
    System.out.println("Select a game to play:");
    System.out.println("1: Ladder Game");
    System.out.println("2: Missing Diamond");
    System.out.println("0: Exit");
    int gameSelector = inputScanner.nextInt();

    switch (gameSelector) {
      case 1:
        numberOfPlayers = getNumberOfPlayers();
        new LadderGame(numberOfPlayers);
        break;
      case 2:
        numberOfPlayers = getNumberOfPlayers();
        new MissingDiamond(numberOfPlayers);
        break;
      case 0:
        System.out.println("Exiting...");
        break;
      default:
        System.out.println("Invalid selection. Exiting...");
        break;
    }
  }

  /**
   * Method that takes user input to get the number of players to play the game.
   * Throws an exception if the input is less than 1 or a float value.
   * @return
   */
  private int getNumberOfPlayers() {
    int players = 0;
    boolean validInput = false;
    while (!validInput) {
      try {
        System.out.println("Enter number of players:");
        players = inputScanner.nextInt();
        if (players > 0 && players < 7) {
          validInput = true;
        } else {
          System.out.println("Number of players must be a positive integer. Please try again.");
        }
      } catch (InputMismatchException e) {
        System.out.println("Invalid input. Please enter a valid number.");
        inputScanner.next(); // Clear the invalid input
      }
    }
    return players;
  }
}