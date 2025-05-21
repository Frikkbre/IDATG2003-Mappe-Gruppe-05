package edu.ntnu.idi.bidata.idatg2003mappe.app.missingdiamond.ui;

import edu.ntnu.idi.bidata.idatg2003mappe.app.common.ui.UIComponentFactory;
import edu.ntnu.idi.bidata.idatg2003mappe.app.missingdiamond.controller.MissingDiamondController;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;

/**
 * Panel containing game controls like dice rolling and game log.
 */
public class GameControlPanel extends VBox {
  private final MissingDiamondController gameController;
  private final BoardView boardView;
  private final Button rollDieButton;
  private final TextArea gameLog;

  public GameControlPanel(MissingDiamondController controller, BoardView boardView) {
    super(10); // 10px spacing
    this.gameController = controller;
    this.boardView = boardView;

    // Create roll button
    rollDieButton = UIComponentFactory.createActionButton("Roll Die", e -> {
      String result = gameController.playTurn();
      logMessage(result);
      boardView.highlightPossibleMoves();
      boardView.updateUI();
    });

    // Create game log
    gameLog = UIComponentFactory.createGameLog();

    // Add components to panel
    getChildren().addAll(rollDieButton, gameLog);
  }

  public void logMessage(String message) {
    gameLog.appendText(message + "\n");
  }

  public void setRollButtonDisabled(boolean disabled) {
    rollDieButton.setDisable(disabled);
  }

  public TextArea getGameLog() {
    return gameLog;
  }
}