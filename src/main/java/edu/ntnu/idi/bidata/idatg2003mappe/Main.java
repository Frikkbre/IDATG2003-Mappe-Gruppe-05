package edu.ntnu.idi.bidata.idatg2003mappe;

import edu.ntnu.idi.bidata.idatg2003mappe.app.playersetup.PlayerSetupScreen;

/**
 * <p>Main class for the application.</p>
 * <p>This class contains the entry point for the board game application suite
 * and launches the player setup screen as the first step in the application flow.</p>
 *
 * @author Simen Gudbrandsen and Frikk Breadsroed
 * @version 0.0.2
 * @since 16.02.2025
 */
public class Main {

  /**
   * <p>Main method for the application.</p>
   * <p>Starts with the player setup screen where users can configure
   * player names and colors before selecting a game from the available options.</p>
   *
   * @param args Command line arguments (not used)
   */
  public static void main(String[] args) {
    PlayerSetupScreen.main(args);
  }
}
