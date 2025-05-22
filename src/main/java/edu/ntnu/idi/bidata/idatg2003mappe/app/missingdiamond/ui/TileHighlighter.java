package edu.ntnu.idi.bidata.idatg2003mappe.app.missingdiamond.ui;

import edu.ntnu.idi.bidata.idatg2003mappe.app.missingdiamond.controller.MissingDiamondController;
import edu.ntnu.idi.bidata.idatg2003mappe.app.missingdiamond.gamelogic.MissingDiamondMovement;
import edu.ntnu.idi.bidata.idatg2003mappe.map.Tile;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Enhanced tile highlighter that properly handles Missing Diamond movement rules.
 * Highlights both regular movement tiles and special tiles (red tiles with tokens).
 *
 * @author Simen Gudbrandsen and Frikk Breadsroed
 * @version 0.0.1
 * @since 22.05.2025
 */
public class TileHighlighter {
  private final Map<Integer, Circle> tileCircles;
  private final Set<Integer> specialTileIds;
  private final MissingDiamondController gameController;
  private final MissingDiamondMovement movementLogic;

  private static final Color SPECIAL_TILE_COLOR = Color.RED;
  private static final Color NORMAL_TILE_COLOR = Color.BLACK;
  private static final Color VALID_MOVE_COLOR = Color.YELLOW;
  private static final Color SPECIAL_VALID_MOVE_COLOR = Color.ORANGE;
  private static final Color CURRENT_PLAYER_COLOR = Color.LIME;

  private static final double NORMAL_STROKE_WIDTH = 1.5;
  private static final double HIGHLIGHTED_STROKE_WIDTH = 3.0;

  /**
   * Creates a new enhanced TileHighlighter.
   *
   * @param tileCircles    Map of tile IDs to Circle objects
   * @param specialTileIds Set of IDs for special tiles (red tiles)
   * @param gameController Game controller reference
   */
  public TileHighlighter(Map<Integer, Circle> tileCircles, Set<Integer> specialTileIds,
                         MissingDiamondController gameController) {
    this.tileCircles = tileCircles;
    this.specialTileIds = specialTileIds;
    this.gameController = gameController;

    // Initialize movement logic with special tile information
    this.movementLogic = new MissingDiamondMovement(specialTileIds);
  }

  /**
   * Resets all tiles to their original colors and stroke widths.
   */
  public void resetTileColors() {
    for (Map.Entry<Integer, Circle> entry : tileCircles.entrySet()) {
      int tileId = entry.getKey();
      Circle tile = entry.getValue();

      // Reset to original color based on tile type
      if (specialTileIds.contains(tileId)) {
        tile.setFill(SPECIAL_TILE_COLOR);
      } else {
        tile.setFill(NORMAL_TILE_COLOR);
      }

      // Reset stroke
      tile.setStroke(Color.WHITE);
      tile.setStrokeWidth(NORMAL_STROKE_WIDTH);
    }
  }

  /**
   * Highlights possible moves based on the current die roll.
   * This is the main method that should be called to update tile highlighting.
   */
  public void highlightPossibleMoves() {
    if (gameController == null) {
      System.out.println("DEBUG: Game controller is null, cannot highlight moves");
      return;
    }

    // Reset all tiles first
    resetTileColors();

    // Highlight current player position
    highlightCurrentPlayerPosition();

    // Only highlight possible moves if die has been rolled
    if (!gameController.hasRolled()) {
      System.out.println("DEBUG: Die not rolled yet, no moves to highlight");
      return;
    }

    // Get possible moves from controller
    List<Tile> possibleMoves = gameController.getPossibleMoves();
    System.out.println("DEBUG: Highlighting " + possibleMoves.size() + " possible moves");

    // Highlight each possible move
    for (Tile tile : possibleMoves) {
      highlightTile(tile);
    }
  }

  /**
   * Highlights the current player's position.
   */
  private void highlightCurrentPlayerPosition() {
    if (gameController.getCurrentPlayer() != null) {
      int currentTileId = gameController.getCurrentPlayer().getCurrentTile().getTileId();
      Circle currentTileCircle = tileCircles.get(currentTileId);

      if (currentTileCircle != null) {
        currentTileCircle.setStroke(CURRENT_PLAYER_COLOR);
        currentTileCircle.setStrokeWidth(HIGHLIGHTED_STROKE_WIDTH);
      }
    }
  }

  /**
   * Highlights a specific tile as a valid move destination.
   *
   * @param tile The tile to highlight
   */
  private void highlightTile(Tile tile) {
    Circle tileCircle = tileCircles.get(tile.getTileId());
    if (tileCircle == null) {
      System.out.println("DEBUG: No circle found for tile " + tile.getTileId());
      return;
    }

    boolean isSpecialTile = specialTileIds.contains(tile.getTileId());
    boolean hasToken = gameController.hasTokenAtTile(tile);

    // Choose highlighting color based on tile type and whether it has a token
    Color highlightColor;
    if (isSpecialTile && hasToken) {
      // Special tile with token - most important
      highlightColor = SPECIAL_VALID_MOVE_COLOR;
      System.out.println("DEBUG: Highlighting special tile with token: " + tile.getTileId());
    } else if (isSpecialTile) {
      // Special tile without token
      highlightColor = Color.LIGHTCORAL;
      System.out.println("DEBUG: Highlighting special tile without token: " + tile.getTileId());
    } else {
      // Regular movement tile
      highlightColor = VALID_MOVE_COLOR;
      System.out.println("DEBUG: Highlighting regular tile: " + tile.getTileId());
    }

    // Apply highlighting
    tileCircle.setFill(highlightColor);
    tileCircle.setStroke(Color.WHITE);
    tileCircle.setStrokeWidth(HIGHLIGHTED_STROKE_WIDTH);
  }

  /**
   * Gets the movement logic instance for external use.
   *
   * @return The movement logic instance
   */
  public MissingDiamondMovement getMovementLogic() {
    return movementLogic;
  }
}