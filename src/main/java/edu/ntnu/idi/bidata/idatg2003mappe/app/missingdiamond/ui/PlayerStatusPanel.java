package edu.ntnu.idi.bidata.idatg2003mappe.app.missingdiamond.ui;

import edu.ntnu.idi.bidata.idatg2003mappe.app.missingdiamond.controller.MissingDiamondController;
import edu.ntnu.idi.bidata.idatg2003mappe.entity.player.Player;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;

import java.util.List;

/**
 * Panel displaying player status information.
 */
public class PlayerStatusPanel extends VBox {
  private final MissingDiamondController gameController;
  private final TextArea scoreBoard;

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