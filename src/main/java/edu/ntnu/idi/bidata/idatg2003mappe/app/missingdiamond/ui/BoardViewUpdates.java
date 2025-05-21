package edu.ntnu.idi.bidata.idatg2003mappe.app.missingdiamond.ui;

import edu.ntnu.idi.bidata.idatg2003mappe.app.missingdiamond.controller.MissingDiamondController;
import edu.ntnu.idi.bidata.idatg2003mappe.entity.player.Player;
import edu.ntnu.idi.bidata.idatg2003mappe.map.Tile;
import javafx.scene.control.Alert;
import javafx.scene.control.TextArea;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * These are the key updates needed for the BoardView class to properly handle
 * interaction with special tiles.
 */
public class BoardViewUpdates {

  private final MissingDiamondController gameController;

  private TileHighlighter tileHighlighter;

  public BoardViewUpdates(MissingDiamondController gameController) {
    this.gameController = gameController;
  }

  /**
   * Handles a click on a game tile during gameplay.
   *
   * @param tileId The ID of the clicked tile
   * @param gameController The game controller
   * @param gameLog The game log for messages
   */
  public void handleGameplayTileClick(int tileId, MissingDiamondController gameController, TextArea gameLog) {
    if (gameController == null) return;

    // Only allow moves if the player has rolled
    if (!gameController.hasRolled()) {
      logMessage("You must roll the die first.", gameLog);
      return;
    }

    // Check if the tile is a valid move
    List<Tile> possibleMoves = gameController.getPossibleMoves();
    boolean validMove = possibleMoves.stream()
        .anyMatch(tile -> tile.getTileId() == tileId);

    if (validMove) {
      // Move the player to the selected tile
      String moveResult = gameController.movePlayer(tileId);
      logMessage(moveResult, gameLog);

      // Update the board display
      updateUI();

      // Check for special tile interactions (tokens)
      Tile destinationTile = gameController.getTileById(tileId);
      if (gameController.isSpecialTile(tileId) &&
          gameController.hasTokenAtTile(destinationTile)) {

        // Show token options - this will be handled by the controller
        // which will update the UI to show token interaction buttons
        showTokenInteractionOptions();
      }

      // Check for game end
      if (gameController.isGameFinished()) {
        showGameOverDialog();
      }
    } else {
      logMessage("Cannot move to tile " + tileId + ".", gameLog);
    }
  }

  private void showGameOverDialog() {
    Alert alert = new Alert(Alert.AlertType.INFORMATION);
    alert.setTitle("Game Over");
    alert.setHeaderText("Game Finished!");

    // Get the winner info from the game controller
    if (gameController != null && gameController.getCurrentPlayer() != null) {
      alert.setContentText(gameController.getCurrentPlayer().getName() + " has won the game!");
    } else {
      alert.setContentText("The game has ended!");
    }

    alert.showAndWait();
  }

  /**
   * Shows UI elements for token interaction (buy, try to win, skip).
   * This method would update the UI to show the token interaction buttons.
   */
  private void showTokenInteractionOptions() {
    //tokenInteractionPanel.setVisible(true);
    //buyTokenButton.setVisible(true);
    //tryWinTokenButton.setVisible(true);
    //skipTokenButton.setVisible(true);
  }

  /**
   * Creates a specialized highlighter for valid moves that properly
   * handles special tiles.
   */
  public TileHighlighter createTileHighlighter(
      Map<Integer, Circle> tileCircles,
      Set<Integer> specialTileIds,
      MissingDiamondController gameController) {

    return new TileHighlighter(tileCircles, specialTileIds, gameController);
  }

  /**
   * Updates the visualization of the board to reflect the current game state.
   * This includes player positions and highlighting valid moves.
   */
  public void updateUI() {
    // Update player positions
    updatePlayerPositions();

    // Highlight possible moves
    highlightPossibleMoves();
  }

  /**
   * Updates the visualization of player positions on the board.
   */
  private void updatePlayerPositions() {
    // This would update the visual position of player markers on the board
    // Implementation would clear and redraw player markers at their current positions
  }

  /**
   * Highlights possible moves, including special tiles that can be interacted with.
   */
  private void highlightPossibleMoves() {
    if (tileHighlighter != null) {
      tileHighlighter.highlightPossibleMoves();
    }
  }

  /**
   * Logs a message to the game log.
   */
  private void logMessage(String message, TextArea gameLog) {
    if (gameLog != null) {
      gameLog.appendText(message + "\n");
    }
  }
}