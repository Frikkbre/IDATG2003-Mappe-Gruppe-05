package edu.ntnu.idi.bidata.idatg2003mappe.app.missingdiamond.ui;

import edu.ntnu.idi.bidata.idatg2003mappe.app.missingdiamond.controller.MissingDiamondController;
import edu.ntnu.idi.bidata.idatg2003mappe.map.Tile;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Class responsible for highlighting tiles on the game board.
 * Separates the visual highlighting logic from the board view.
 *
 * @author Simen Gudbrandsen and Frikk Breadsroed
 * @version 0.0.1
 * @since 21.05.2025
 */
public class TileHighlighter {
  private final Map<Integer, Circle> tileCircles;
  private final Set<Integer> specialTileIds;
  private final MissingDiamondController gameController;

  /**
   * Creates a new TileHighlighter.
   *
   * @param tileCircles Map of tile IDs to Circle objects
   * @param specialTileIds Set of IDs for special tiles
   * @param gameController Game controller reference
   */
  public TileHighlighter(Map<Integer, Circle> tileCircles, Set<Integer> specialTileIds,
                         MissingDiamondController gameController) {
    this.tileCircles = tileCircles;
    this.specialTileIds = specialTileIds;
    this.gameController = gameController;
  }

  /**
   * Resets all tiles to their original colors based on their type.
   */
  public void resetTileColors() {
    for (Map.Entry<Integer, Circle> entry : tileCircles.entrySet()) {
      int tileId = entry.getKey();
      Circle tile = entry.getValue();

      // Check if it's a special tile
      if (specialTileIds.contains(tileId)) {
        tile.setFill(Color.RED);
      } else {
        tile.setFill(Color.BLACK);
      }
    }
  }

  /**
   * Highlights the specified tiles as possible moves.
   *
   * @param possibleMoves List of tiles that represent valid moves
   */
  public void highlightTiles(List<Tile> possibleMoves) {
    for (Tile tile : possibleMoves) {
      Circle tileCircle = tileCircles.get(tile.getTileId());
      if (tileCircle != null) {
        tileCircle.setFill(Color.YELLOW);
      }
    }
  }

  /**
   * Highlights possible moves based on the current game state.
   */
  public void highlightPossibleMoves() {
    if (gameController == null) return;

    // Reset all tiles to original colors
    resetTileColors();

    // Only highlight possible moves if die has been rolled
    if (gameController.hasRolled()) {
      // Highlight possible moves in yellow
      List<Tile> possibleMoves = gameController.getPossibleMoves();
      highlightTiles(possibleMoves);
    }
  }
}