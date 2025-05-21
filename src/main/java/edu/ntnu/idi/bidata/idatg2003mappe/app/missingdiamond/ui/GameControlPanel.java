package edu.ntnu.idi.bidata.idatg2003mappe.app.missingdiamond.ui;

import edu.ntnu.idi.bidata.idatg2003mappe.app.common.ui.UIComponentFactory;
import edu.ntnu.idi.bidata.idatg2003mappe.app.missingdiamond.controller.MissingDiamondController;
import edu.ntnu.idi.bidata.idatg2003mappe.banker.Banker;
import edu.ntnu.idi.bidata.idatg2003mappe.entity.player.Player;
import edu.ntnu.idi.bidata.idatg2003mappe.map.Tile;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.util.List;

/**
 * Panel containing game controls like dice rolling and game log.
 */
public class GameControlPanel extends VBox {
  private final MissingDiamondController gameController;
  private final BoardView boardView;
  private final Button rollDieButton;
  private final Button buyTokenButton;
  private final Button tryWinTokenButton;
  private final Button skipTokenButton;
  private final Button planeButton;
  private final Button shipButton;
  private final TextArea gameLog;
  private final Label playerMoneyLabel;

  public GameControlPanel(MissingDiamondController controller, BoardView boardView) {
    super(10); // 10px spacing
    this.gameController = controller;
    this.boardView = boardView;
    setPadding(new Insets(10));

    // Create player info section
    Label playerLabel = new Label("Current Player: ");
    playerLabel.setFont(Font.font("System", FontWeight.BOLD, 14));

    playerMoneyLabel = new Label("Money: £0");

    // Create roll button
    rollDieButton = UIComponentFactory.createActionButton("Roll Die", e -> {
      String result = gameController.playTurn();
      logMessage(result);
      boardView.highlightPossibleMoves();
      boardView.updateUI();
      updateControls();
      updatePlayerInfo();
    });

    // Create token interaction buttons
    buyTokenButton = UIComponentFactory.createActionButton("Buy Token", e -> {
      String result = gameController.buyToken();
      logMessage(result);
      boardView.updateUI();
      updateControls();
      updatePlayerInfo();
    });

    tryWinTokenButton = UIComponentFactory.createActionButton("Try to Win Token", e -> {
      String result = gameController.tryWinToken();
      logMessage(result);
      boardView.updateUI();
      updateControls();
      updatePlayerInfo();
    });

    skipTokenButton = UIComponentFactory.createActionButton("Skip (Continue Journey)", e -> {
      String result = gameController.skipTokenAction();
      logMessage(result);
      boardView.updateUI();
      updateControls();
      updatePlayerInfo();
    });

    // Create travel buttons
    planeButton = UIComponentFactory.createActionButton("Travel by Plane", e -> {
      String result = gameController.initiateAirTravel();
      logMessage(result);
      // In a real implementation, this would show a dialog to select the destination
      updateControls();
    });

    shipButton = UIComponentFactory.createActionButton("Travel by Ship", e -> {
      String result = gameController.travelByShip();
      logMessage(result);
      boardView.updateUI();
      updateControls();
      updatePlayerInfo();
    });

    // Create game log
    gameLog = UIComponentFactory.createGameLog();

    // Create actions section
    Label actionsLabel = new Label("Actions");
    actionsLabel.setFont(Font.font("System", FontWeight.BOLD, 14));

    // Add components to panel
    getChildren().addAll(
        playerLabel,
        playerMoneyLabel,
        actionsLabel,
        rollDieButton,
        buyTokenButton,
        tryWinTokenButton,
        skipTokenButton,
        planeButton,
        shipButton,
        gameLog
    );

    // Initialize controls visibility
    updateControls();
    updatePlayerInfo();
  }

  /**
   * Updates the player information display.
   */
  private void updatePlayerInfo() {
    Player currentPlayer = gameController.getCurrentPlayer();
    if (currentPlayer != null) {
      Banker banker = gameController.getBanker();
      int balance = banker.getBalance(currentPlayer);

      playerMoneyLabel.setText(String.format("Player: %s - Money: £%d",
          currentPlayer.getName(), balance));
    }
  }

  /**
   * Updates the controls based on the current game state.
   */
  private void updateControls() {
    // Hide all action buttons by default
    buyTokenButton.setVisible(false);
    tryWinTokenButton.setVisible(false);
    skipTokenButton.setVisible(false);
    planeButton.setVisible(false);
    shipButton.setVisible(false);

    // Show roll button only when awaiting roll
    rollDieButton.setVisible(!gameController.hasRolled());

    // Show token buttons when at a location with a token
    Tile currentTile = gameController.getCurrentPlayer().getCurrentTile();
    boolean hasToken = gameController.getTokenAtTileId(currentTile.getTileId()) != null;

    if (gameController.hasRolled() && hasToken) {
      buyTokenButton.setVisible(true);
      tryWinTokenButton.setVisible(true);
      skipTokenButton.setVisible(true);
    }
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