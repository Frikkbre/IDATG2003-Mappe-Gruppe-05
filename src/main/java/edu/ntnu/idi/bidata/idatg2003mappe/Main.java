package edu.ntnu.idi.bidata.idatg2003mappe;

import edu.ntnu.idi.bidata.idatg2003mappe.app.playersetup.PlayerSetupScreen;

/**
 * Main class for the application.
 *
 * @author Simen Gudbrandsen and Frikk Breadsroed
 * @version 0.0.2
 * @since 16.02.2025
 */

public class Main {

  /**
   * Main method for the application.
   * Starts with the player setup screen where users can configure
   * player names and colors before selecting a game.
   *
   * @param args Command line arguments
   */

  public static void main(String[] args) {
    PlayerSetupScreen.main(args);
  }
}