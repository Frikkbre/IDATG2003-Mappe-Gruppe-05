package edu.ntnu.idi.bidata.idatg2003mappe.app.missingdiamond.ui;

import edu.ntnu.idi.bidata.idatg2003mappe.app.common.ui.UIComponentFactory;
import edu.ntnu.idi.bidata.idatg2003mappe.app.missingdiamond.controller.MissingDiamondController;
import edu.ntnu.idi.bidata.idatg2003mappe.banker.Banker;
import edu.ntnu.idi.bidata.idatg2003mappe.entity.player.Player;
import edu.ntnu.idi.bidata.idatg2003mappe.map.Tile;
import edu.ntnu.idi.bidata.idatg2003mappe.entity.die.Die;
import edu.ntnu.idi.bidata.idatg2003mappe.markers.Marker;
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
  private final Button openTokenButton;
  private final Button skipTokenButton;
  private final Label selectMoveLabel;
  private final Button endTurnButton;
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

    // Create combined token interaction button
    openTokenButton = UIComponentFactory.createActionButton("Open Token", e -> {
      Tile currentTile = gameController.getCurrentPlayer().getCurrentTile();
      Marker token = gameController.getTokenAtTileId(currentTile.getTileId());

      if (token == null) {
        logMessage("There is no token at your current location.");
        return;
      }

      // Roll die for token opening
      int roll = gameController.getDie().rollDie();
      logMessage("You rolled a " + roll + " to open the token...");

      // Check success (4-6 succeeds, 1-3 fails)
      if (roll >= 4) {
        // Success - remove token and apply effects
        gameController.removeTokenFromTile(currentTile);

        String tokenType = token.getType();
        logMessage("Success! You opened the token and found: " + tokenType + "!");

        // Apply token effects
        applyTokenEffects(token, gameController.getCurrentPlayer());

      } else {
        logMessage("You rolled a " + roll + " but couldn't open the token (need 4-6). Better luck next time!");
      }

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

    // Add a label for selecting a move
    selectMoveLabel = new Label("Select a highlighted tile to move");
    selectMoveLabel.setFont(Font.font("System", FontWeight.BOLD, 12));
    selectMoveLabel.setTextFill(Color.DARKBLUE);

    // Add an emergency end turn button
    endTurnButton = UIComponentFactory.createActionButton("End Turn", e -> {
      gameController.endTurn();
      logMessage("Turn ended.");
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
        selectMoveLabel,
        openTokenButton,
        skipTokenButton,
        endTurnButton,
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

  private void applyTokenEffects(Marker token, Player player) {
    Banker banker = gameController.getBanker();

    logMessage("DEBUG: Token type is: '" + token.getType() + "'");

    switch (token.getType()) {
      case "Diamond":
        player.addInventoryItem("diamond");
        break;
      case "RedGem":
        banker.deposit(player, token.getValue());
        logMessage("You found a ruby worth " + token.getValue() + "!");
        break;
      case "GreenGem":
        banker.deposit(player, token.getValue());
        logMessage("You found an emerald worth " + token.getValue() + "!");
        break;
      case "YellowGem":
        banker.deposit(player, token.getValue());
        logMessage("You found a topaz worth " + token.getValue() + "!");
        break;
      case "Bandit":
        int currentBalance = banker.getBalance(player);
        banker.withdraw(player, currentBalance);
        break;
      case "Visa":
        player.addInventoryItem("visa");
        logMessage("You found a visa for free travel!");
        break;
      default:
        logMessage("Nothing special here.");
        break;
    }
  }

  /**
   * Updates the controls based on the current game state.
   */
  private void updateControls() {
    // Hide all action buttons by default
    selectMoveLabel.setVisible(false);
    endTurnButton.setVisible(false);

    // Show roll button only when awaiting roll
    boolean hasRolled = gameController.hasRolled();
    rollDieButton.setVisible(!hasRolled);

    // Always show token buttons
    openTokenButton.setVisible(true);
    skipTokenButton.setVisible(true);

    // Show the end turn button in all states as a failsafe
    endTurnButton.setVisible(true);

    // Show move selection label if player has rolled and has moves
    if (hasRolled) {
      List<Tile> possibleMoves = gameController.getPossibleMoves();
      if (!possibleMoves.isEmpty()) {
        selectMoveLabel.setVisible(true);
      }
    }
  }

  /**
   * Checks if a tile is a red tile (special location) based on the map configuration.
   * You can implement this by checking against the special tile IDs from your map config.
   */
  private boolean isRedTileFromMapConfig(int tileId) {
    // Simply use the controller's method to check if this is a red tile
    return gameController.isRedTileFromConfig(tileId);
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
