package edu.ntnu.idi.bidata.idatg2003mappe.app.boardgameselector;

import edu.ntnu.idi.bidata.idatg2003mappe.app.laddergame.model.LadderGame;
import edu.ntnu.idi.bidata.idatg2003mappe.app.missingdiamond.model.MissingDiamond;

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
    System.out.println("Select a game to play:");
    System.out.println("1: Ladder Game");
    System.out.println("2: Missing Diamond");
    System.out.println("0: Exit");
    int gameSelector = inputScanner.nextInt();

    switch (gameSelector) {
      case 1:
        numberOfPlayers = getNumberOfPlayers();
        // Create player data for CSV here
        writePlayersToCSV(numberOfPlayers);
        // Now create the game with updated constructor
        new LadderGame(false);
        break;
      case 2:
        numberOfPlayers = getNumberOfPlayers();
        // Create player data for CSV here
        writePlayersToCSV(numberOfPlayers);
        // Now create the game with updated constructor
        new MissingDiamond();
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
   * Method to write player data to the CSV file
   *
   * @param numberOfPlayers the number of players to create
   */
  private void writePlayersToCSV(int numberOfPlayers) {
    // This method would create the CSV file with player data
    // For now, it's just a placeholder - the full implementation would be similar to
    // what we did in BoardGameSelectorGUI
    System.out.println("Setting up " + numberOfPlayers + " players...");

    // In a real implementation, this would create the CSV file:
    /*
    try {
      File playerDir = new File("src/main/resources/saves/playerData/");
      if (!playerDir.exists()) {
        playerDir.mkdirs();
      }

      File playerFile = new File("src/main/resources/saves/playerData/Players.csv");
      FileWriter outputfile = new FileWriter(playerFile);
      CSVWriter playerWriter = new CSVWriter(outputfile);

      // Write header
      String[] header = { "Player", "Color", "Score" };
      playerWriter.writeNext(header);

      // Write player data
      for (int i = 0; i < numberOfPlayers; i++) {
        String[] playerData = { "Player " + (i + 1), "Color" + i, "0" };
        playerWriter.writeNext(playerData);
      }

      playerWriter.flush();
      playerWriter.close();
    } catch (IOException e) {
      System.err.println("Error writing player data: " + e.getMessage());
    }
    */
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