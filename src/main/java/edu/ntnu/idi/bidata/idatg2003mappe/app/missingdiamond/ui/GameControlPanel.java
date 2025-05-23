package edu.ntnu.idi.bidata.idatg2003mappe.app.missingdiamond.ui;

import edu.ntnu.idi.bidata.idatg2003mappe.app.common.ui.UIComponentFactory;
import edu.ntnu.idi.bidata.idatg2003mappe.app.missingdiamond.controller.MissingDiamondController;
import edu.ntnu.idi.bidata.idatg2003mappe.banker.Banker;
import edu.ntnu.idi.bidata.idatg2003mappe.entity.player.Player;
import edu.ntnu.idi.bidata.idatg2003mappe.map.Tile;
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
 * <p>Panel containing game controls and game log for the Missing Diamond game.</p>
 * <p>This panel provides players with action buttons for gameplay, including:</p>
 * <ul>
 *   <li>Die rolling for movement</li>
 *   <li>Token interaction options at special locations</li>
 *   <li>Turn management controls</li>
 * </ul>
 * <p>It also displays a game log that shows recent game events and player information
 * including current player and their financial status.</p>
 * <p>Token interaction features two options:</p>
 * <ol>
 *   <li>A free but risky option that requires rolling 4-6 to succeed</li>
 *   <li>A guaranteed option that costs 300 coins</li>
 * </ol>
 *
 * @author Simen Gudbrandsen and Frikk Breadsroed
 * @version 0.2.0
 * @since 23.05.2025
 */
public class GameControlPanel extends VBox {
  private final MissingDiamondController gameController;
  private final BoardView boardView;
  private final Button rollDieButton;
  private final Button openTokenButton;
  private final Button buyTokenFlipButton;
  private final Label selectMoveLabel;
  private final Button endTurnButton;
  private final TextArea gameLog;
  private final Label playerMoneyLabel;
  private PlayerStatusPanel statusPanel;

  private static final int MAX_LOG_LINES = 8;

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

    // Create combined token interaction button (dice roll - free but risky)
    openTokenButton = UIComponentFactory.createActionButton("Try Token (Free - Roll 4-6)", e -> {
      Tile currentTile = gameController.getCurrentPlayer().getCurrentTile();
      Marker token = gameController.getTokenAtTileId(currentTile.getTileId());

      if (token == null) {
        logMessage("There is no token at your current location.");
        return;
      }

      // Roll die for token opening
      int roll = gameController.getDie().rollDie();
      logMessage("You rolled a " + roll + " to try to get the token...");

      // Check success (4-6 succeeds, 1-3 fails)
      if (roll >= 4) {
        // Success - remove token and apply effects
        gameController.removeTokenFromTile(currentTile);

        String tokenType = token.getType();
        logMessage("Success! You rolled " + roll + " and got the token: " + tokenType + "!");

        // Apply token effects
        applyTokenEffects(token, gameController.getCurrentPlayer());

      } else {
        logMessage("You rolled " + roll + " but couldn't get the token (need 4-6). The token remains here.");
      }

      // End the turn automatically after token interaction
      gameController.endTurn();
      // Reset roll state to ensure the next player can roll
      gameController.resetRollState();

      logMessage("Turn ended.");
      boardView.updateUI();
      updateControls();
      updatePlayerInfo();
    });

    // Create buy token flip button (guaranteed success for 300 coins)
    buyTokenFlipButton = UIComponentFactory.createActionButton("Buy Token Flip (£300 - Guaranteed)", e -> {
      Tile currentTile = gameController.getCurrentPlayer().getCurrentTile();
      Marker token = gameController.getTokenAtTileId(currentTile.getTileId());

      if (token == null) {
        logMessage("There is no token at your current location.");
        return;
      }

      Player currentPlayer = gameController.getCurrentPlayer();
      Banker banker = gameController.getBanker();

      // Check if player has enough money
      if (banker.getBalance(currentPlayer) < 300) {
        logMessage("You don't have enough money! You need £300 but only have £" +
            banker.getBalance(currentPlayer) + ".");
        return;
      }

      // Attempt to buy the token flip
      boolean success = gameController.buyTokenFlip(currentTile);

      if (success) {
        String tokenType = token.getType();
        logMessage("You paid £300 and successfully flipped the token: " + tokenType + "!");

        // Apply token effects
        applyTokenEffects(token, currentPlayer);
      } else {
        logMessage("Failed to buy token flip.  Transaction error occurred.");
      }

      logMessage("Turn ended.");
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
      gameController.resetRollState();

      logMessage("Turn ended.");
      updateControls();
      updatePlayerInfo();
      boardView.updateUI();
    });

    // Create game log with proper settings to prevent scrolling
    gameLog = createFixedGameLog();

    // Create actions section
    Label actionsLabel = new Label("Actions");
    actionsLabel.setFont(Font.font("System", FontWeight.BOLD, 14));

    // Create token options section
    Label tokenOptionsLabel = new Label("Token Options (when at a red tile):");
    tokenOptionsLabel.setFont(Font.font("System", FontWeight.BOLD, 12));
    tokenOptionsLabel.setTextFill(Color.DARKRED);

    // Add components to panel (Skip button removed)
    getChildren().addAll(
        playerLabel,
        playerMoneyLabel,
        actionsLabel,
        rollDieButton,
        selectMoveLabel,
        tokenOptionsLabel,
        openTokenButton,
        buyTokenFlipButton,
        endTurnButton,
        gameLog
    );

    // Initialize controls visibility
    updateControls();
    updatePlayerInfo();
  }

  /**
   * Creates a game log that fits content without scrolling.
   */
  private TextArea createFixedGameLog() {
    TextArea log = new TextArea();
    log.setPrefHeight(200); // Smaller fixed height
    log.setMaxHeight(200);  // Prevent growing
    log.setEditable(false);
    log.setWrapText(true);  // Enable word wrap

    // Disable scrollbars
    log.setStyle("-fx-control-inner-background: #f4f4f4; -fx-font-family: monospace; -fx-font-size: 11px;");

    return log;
  }

  /**
   * Logs a message with automatic line management to prevent scrolling.
   */
  public void logMessage(String message) {
    // If message is short enough, just add it directly
    if (message.length() <= 45) {
      addLogLine(message);
      return;
    }

    // For longer messages, split at logical points (periods, exclamation marks)
    String[] sentences = message.split("[.!]");
    for (String sentence : sentences) {
      sentence = sentence.trim();
      if (sentence.length() > 0) {
        // Add punctuation back if it was a complete sentence
        if (!sentence.equals(sentences[sentences.length - 1]) || message.endsWith(".") || message.endsWith("!")) {
          sentence += message.contains("!") ? "!" : ".";
        }
        addLogLine(sentence);
      }
    }
  }

  /**
   * Adds a single line to the log, removing old lines if necessary.
   */
  private void addLogLine(String line) {
    String currentText = gameLog.getText();
    String[] lines = currentText.split("\n");

    // If we have too many lines, remove the oldest ones
    if (lines.length >= MAX_LOG_LINES) {
      StringBuilder newText = new StringBuilder();
      // Keep only the most recent lines (skip the oldest)
      for (int i = 1; i < lines.length; i++) {
        newText.append(lines[i]).append("\n");
      }
      newText.append(line);
      gameLog.setText(newText.toString());
    } else {
      // Just append the new line
      gameLog.appendText(line + "\n");
    }

    gameLog.setScrollTop(Double.MAX_VALUE);
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

  public void setStatusPanel(PlayerStatusPanel statusPanel) {
    this.statusPanel = statusPanel;
  }

  private void applyTokenEffects(Marker token, Player player) {
    Banker banker = gameController.getBanker();

    //logMessage("DEBUG: Token type is: '" + token.getType() + "'");

    switch (token.getType()) {
      case "Diamond":
        player.addInventoryItem("diamond");
        logMessage("MISSING DIAMOND FOUND!");
        logMessage("Return to start to win!");
        break;
      case "RedGem":
        banker.deposit(player, token.getValue());
        logMessage("Ruby found: +£" + token.getValue());
        break;
      case "GreenGem":
        banker.deposit(player, token.getValue());
        logMessage("Emerald found: +£" + token.getValue());
        break;
      case "YellowGem":
        banker.deposit(player, token.getValue());
        logMessage("Topaz found: +£" + token.getValue());
        break;
      case "Bandit":
        int currentBalance = banker.getBalance(player);
        if (currentBalance > 0) {
          boolean success = banker.withdraw(player, currentBalance);
          if (success) {
            logMessage("OH NO! A bandit stole all your money (£" + currentBalance + ")!");
          } else {
            logMessage("A bandit appeared, but you have no money!");
          }
        } else {
          logMessage("Bandit found nothing to steal");
        }
        break;
      case "Visa":
        player.addInventoryItem("visa");
        logMessage("You found a visa card!");
        break;
      case "Blank":
        logMessage("Empty token - nothing here");
        break;
      default:
        logMessage("Nothing found");
        break;
    }
    if (statusPanel != null) {
      statusPanel.updateScoreBoard();
    }
  }

  /**
   * Updates the controls based on the current game state.
   * Skip button removed - End Turn always available for same functionality.
   */
  private void updateControls() {
    // Hide all action buttons by default
    selectMoveLabel.setVisible(false);

    // ALWAYS show roll button and end turn button - never hide them
    rollDieButton.setVisible(true);

    openTokenButton.setVisible(true);
    buyTokenFlipButton.setVisible(true);

    endTurnButton.setVisible(true);

    // Get the fresh state from controller
    boolean hasRolled = gameController.hasRolled();

    // Only disable (not hide) the roll button if the user has already rolled
    rollDieButton.setDisable(hasRolled);

    // Show token buttons only when at a tile with a token
    Player currentPlayer = gameController.getCurrentPlayer();

    // Show move selection label if player has rolled and has moves
    if (hasRolled) {
      List<Tile> possibleMoves = gameController.getPossibleMoves();
      if (!possibleMoves.isEmpty()) {
        selectMoveLabel.setVisible(true);
      }
    }

    // Update button text to show current money status for the buy token flip button
    if (buyTokenFlipButton.isVisible() && currentPlayer != null) {
      Banker banker = gameController.getBanker();
      int balance = banker.getBalance(currentPlayer);
      String buttonText = "Buy Token Flip (£300 - Guaranteed)";

      if (balance < 300) {
        buttonText += " - Need £" + (300 - balance) + " more";
        buyTokenFlipButton.setDisable(true);
      } else {
        buyTokenFlipButton.setDisable(false);
      }

      buyTokenFlipButton.setText(buttonText);
    }
  }

  public void setRollButtonDisabled(boolean disabled) {
    rollDieButton.setDisable(disabled);
  }
}