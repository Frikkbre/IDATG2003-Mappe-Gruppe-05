package edu.ntnu.idi.bidata.idatg2003mappe.app.missingdiamond.ui;

import edu.ntnu.idi.bidata.idatg2003mappe.app.missingdiamond.controller.MissingDiamondController;

/**
 * <p>Utility class containing key updates for the BoardView class.</p>
 * <p>This class provides helper methods and functionality to properly handle
 * interactions with special tiles in the Missing Diamond game.</p>
 * <p>It serves as a companion to the {@link BoardView} class, offering specialized
 * handling for:</p>
 * <ul>
 *   <li>Token visibility on special tiles</li>
 *   <li>Interaction validation for special tile actions</li>
 *   <li>Visual indication of tile states</li>
 * </ul>
 *
 * @author Simen Gudbrandsen and Frikk Breadsroed
 * @version 0.0.1
 * @since 23.05.2025
 */
public class BoardViewUpdates {

  private final MissingDiamondController gameController;

  /**
   * <p>Creates a new BoardViewUpdates instance.</p>
   * <p>Initializes the updates helper with a reference to the game controller
   * to access game state information.</p>
   *
   * @param gameController The {@link MissingDiamondController} to use for game state access
   */
  public BoardViewUpdates(MissingDiamondController gameController) {
    this.gameController = gameController;
  }
}
