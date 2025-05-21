package edu.ntnu.idi.bidata.idatg2003mappe.filehandling.game;

import edu.ntnu.idi.bidata.idatg2003mappe.app.laddergame.controller.LadderGameController;
import edu.ntnu.idi.bidata.idatg2003mappe.app.missingdiamond.controller.MissingDiamondController;

/**
 * A registry to track the current active game controller.
 */
public class GameRegistry {
  private static LadderGameController currentLadderGame;
  private static MissingDiamondController currentMissingDiamondGame;
  private static boolean isLadderGame = false;

  /**
   * Registers a LadderGameController as the current game.
   *
   * @param controller The LadderGameController to register.
   */
  public static void registerLadderGame(LadderGameController controller) {
    currentLadderGame = controller;
    isLadderGame = true;
  }

  /**
   * Registers a MissingDiamondController as the current game.
   *
   * @param controller The MissingDiamondController to register.
   */
  public static void registerMissingDiamondGame(MissingDiamondController controller) {
    currentMissingDiamondGame = controller;
    isLadderGame = false;
  }

  /**
   * Gets the current LadderGameController if it's registered.
   *
   * @return The current LadderGameController or null if not registered.
   */
  public static LadderGameController getCurrentLadderGame() {
    return currentLadderGame;
  }

  /**
   * Gets the current MissingDiamondController if it's registered.
   *
   * @return The current MissingDiamondController or null if not registered.
   */
  public static MissingDiamondController getCurrentMissingDiamondGame() {
    return currentMissingDiamondGame;
  }

  /**
   * Checks if the current game is a ladder game.
   *
   * @return True if the current game is a ladder game, false otherwise.
   */
  public static boolean isLadderGame() {
    return isLadderGame;
  }

  /**
   * Clears the registry.
   */
  public static void clear() {
    currentLadderGame = null;
    currentMissingDiamondGame = null;
  }
}