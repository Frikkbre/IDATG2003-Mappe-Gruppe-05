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

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

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
  private static final int MAX_LOG_LINES = 8;
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

  /**
   * <p>Constructs a new game control panel with all necessary UI components.</p>
   * <p>The panel contains several action buttons and a game log that displays
   * messages about game events.</p>
   *
   * @param controller the game controller that handles game logic
   * @param boardView  the board view that displays the game board
   */
  public GameControlPanel(MissingDiamondController controller, BoardView boardView) {
    super(12); // 12px spacing for Material Design
    this.gameController = controller;
    this.boardView = boardView;
    setPadding(new Insets(8));

    // Create player info section with Material Design typography
    Label playerLabel = new Label("Current Player");
    playerLabel.getStyleClass().add("md-label-large");

    playerMoneyLabel = new Label("Money: £0");
    playerMoneyLabel.getStyleClass().add("md-title-small");

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

    // Add a label for selecting a move with Material styling
    selectMoveLabel = new Label("Select a highlighted tile to move");
    selectMoveLabel.getStyleClass().addAll("md-body-medium", "md-info-text");

    // Add an emergency end turn button
    endTurnButton = UIComponentFactory.createActionButton("End Turn", e -> {
      gameController.endTurn();
      gameController.resetRollState();

      logMessage("Turn ended.");
      updateControls();
      updatePlayerInfo();
      boardView.updateUI();
    });

    // Create game log with Material Design styling
    gameLog = createFixedGameLog();

    // Create actions section with Material typography
    Label actionsLabel = new Label("Actions");
    actionsLabel.getStyleClass().add("md-title-small");

    // Create token options section
    Label tokenOptionsLabel = new Label("Token Options (at red tiles):");
    tokenOptionsLabel.getStyleClass().addAll("md-label-large", "md-warning-text");

    // Add components to panel with proper layout
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
   * <p>Creates a Material Design game log text area with fixed height.</p>
   * <p>The log area is configured to display a limited number of messages.</p>
   *
   * @return a configured TextArea for displaying game messages
   */
  private TextArea createFixedGameLog() {
    TextArea log = new TextArea();
    log.setPrefHeight(180);
    log.setMaxHeight(180);
    log.setEditable(false);
    log.setWrapText(true);

    // Apply Material Design CSS class for styling
    log.getStyleClass().add("md-game-log");

    return log;
  }

  /**
   * <p>Logs a message to the game log.</p>
   * <p>Long messages are automatically split into multiple lines to fit
   * the display area. Old messages are removed when the log reaches its
   * maximum capacity.</p>
   *
   * @param message the message to log
   */
  public void logMessage(String message) {
    // If message is short enough, just add it directly
    if (message.length() <= 45) {
      addLogLine(message);
      return;
    }

    // Split at logical points (periods, exclamation marks)
    String[] sentences = message.split("[.!]");

    Arrays.stream(sentences).map(String::trim).filter(sentence -> !sentence.isEmpty()).forEach(sentence -> {
      if (!sentence.equals(sentences[sentences.length - 1]) || message.endsWith(".") || message.endsWith("!")) {
        sentence += message.contains("!") ? "!" : ".";
      }
      addLogLine(sentence);
    });
  }

  /**
   * <p>Adds a single line to the game log.</p>
   * <p>If the log exceeds the maximum number of lines, the oldest line
   * is removed to make room for the new one.</p>
   *
   * @param line the line to add to the log
   */
  private void addLogLine(String line) {
    String[] lines = gameLog.getText().split("\n");

    // If we have too many lines, keep only the latest ones
    String newText = (lines.length >= MAX_LOG_LINES)
        ? Arrays.stream(lines).skip(1).collect(Collectors.joining("\n")) + "\n" + line
        : gameLog.getText() + line + "\n";

    gameLog.setText(newText);
    gameLog.setScrollTop(Double.MAX_VALUE);
  }

  /**
   * <p>Updates the player information display.</p>
   * <p>Shows the current player's name and money balance.</p>
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
   * <p>Sets the status panel for this control panel.</p>
   * <p>The status panel is updated when token effects are applied.</p>
   *
   * @param statusPanel the player status panel to set
   */
  public void setStatusPanel(PlayerStatusPanel statusPanel) {
    this.statusPanel = statusPanel;
  }

  /**
   * <p>Applies the effects of a token to a player.</p>
   * <p>Different token types have different effects:</p>
   * <ul>
   *   <li>Diamond: Adds a diamond to the player's inventory</li>
   *   <li>Gems (Red/Green/Yellow): Deposits money into the player's account</li>
   *   <li>Bandit: Steals all of the player's money</li>
   *   <li>Visa: Adds a visa card to the player's inventory</li>
   *   <li>Blank: No effect</li>
   * </ul>
   *
   * @param token  the token to apply effects for
   * @param player the player to apply effects to
   */
  private void applyTokenEffects(Marker token, Player player) {
    Banker banker = gameController.getBanker();

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
   * <p>Updates the visibility and state of control buttons based on the current game state.</p>
   * <p>This method ensures that only appropriate actions are available to the player
   * at each stage of the game.</p>
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

  /**
   * <p>Sets whether the roll button is disabled.</p>
   *
   * @param disabled true to disable the roll button, false to enable it
   */
  public void setRollButtonDisabled(boolean disabled) {
    rollDieButton.setDisable(disabled);
  }
}
