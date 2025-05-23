package edu.ntnu.idi.bidata.idatg2003mappe.app.missingdiamond.ui;

import edu.ntnu.idi.bidata.idatg2003mappe.app.missingdiamond.controller.MissingDiamondController;
import edu.ntnu.idi.bidata.idatg2003mappe.entity.player.Player;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;

import java.util.List;

/**
 * <p>Panel displaying player status information.</p>
 * <p>This component shows a scoreboard with all players' current information,
 * including their names and current money balances. It updates automatically
 * when the game state changes.</p>
 * <p>The panel is designed to fit in the game's sidebar and provide at-a-glance
 * information about all players' financial status.</p>
 *
 * @author Simen Gudbrandsen and Frikk Breadsroed
 * @version 0.0.1
 * @since 23.05.2025
 */
public class PlayerStatusPanel extends VBox {
  private final MissingDiamondController gameController;
  private final TextArea scoreBoard;

  /**
   * <p>Creates a new player status panel.</p>
   * <p>Initializes the panel with a scoreboard showing all players' current status,
   * with automatic updates when the game state changes.</p>
   *
   * @param controller The {@link MissingDiamondController} to get player data from
   */
  public PlayerStatusPanel(MissingDiamondController controller) {
    super(10); // 10px spacing
    this.gameController = controller;

    // Create scoreboard
    scoreBoard = new TextArea();
    scoreBoard.setPrefHeight(200);
    scoreBoard.setEditable(false);

    // Add to layout
    getChildren().add(scoreBoard);

    // Update initially
    updateScoreBoard();
  }

  /**
   * <p>Updates the scoreboard with current player information.</p>
   * <p>This method refreshes the displayed information to show the current
   * financial status of all players in the game.</p>
   * <p>Should be called whenever a player's balance changes or at the end of each turn.</p>
   */
  public void updateScoreBoard() {
    scoreBoard.clear();
    scoreBoard.appendText("Scoreboard:\n");

    List<Player> players = gameController.getPlayers();
    for (Player player : players) {
      scoreBoard.appendText(player.getName() + ": £" +
          gameController.getBanker().getBalance(player) + "\n");
    }
  }
}
