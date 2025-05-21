package edu.ntnu.idi.bidata.idatg2003mappe.app.missingdiamond.ui;

import edu.ntnu.idi.bidata.idatg2003mappe.app.missingdiamond.controller.MissingDiamondController;
import edu.ntnu.idi.bidata.idatg2003mappe.map.Tile;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Enhanced class responsible for highlighting tiles on the game board.
 * Now supports highlighting special tiles (with tokens) differently from regular valid moves.
 *
 * @author Your Name
 * @version 1.0.0
 * @since 22.05.2025
 */
public class TileHighlighter {
  private final Map<Integer, Circle> tileCircles;
  private final Set<Integer> specialTileIds;
  private final MissingDiamondController gameController;

  // Colors for different tile types
  private static final Color SPECIAL_TILE_COLOR = Color.RED;
  private static final Color NORMAL_TILE_COLOR = Color.BLACK;
  private static final Color VALID_MOVE_COLOR = Color.YELLOW;
  private static final Color SPECIAL_VALID_MOVE_COLOR = Color.ORANGE;

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
        tile.setFill(SPECIAL_TILE_COLOR);
      } else {
        tile.setFill(NORMAL_TILE_COLOR);
      }
    }
  }

  /**
   * Highlights the specified tiles as possible moves, with special highlighting for
   * tiles that contain tokens or are otherwise of special interest.
   *
   * @param possibleMoves List of tiles that represent valid moves
   */
  public void highlightTiles(List<Tile> possibleMoves) {
    for (Tile tile : possibleMoves) {
      Circle tileCircle = tileCircles.get(tile.getTileId());
      if (tileCircle != null) {
        // Highlight special tiles (with tokens) differently
        if (specialTileIds.contains(tile.getTileId())) {
          tileCircle.setFill(SPECIAL_VALID_MOVE_COLOR);
        } else {
          tileCircle.setFill(VALID_MOVE_COLOR);
        }

        // Make highlighted tiles more visible
        tileCircle.setStroke(Color.WHITE);
        tileCircle.setStrokeWidth(2.0);
      }
    }
  }

  /**
   * Highlights possible moves based on the current game state.
   * Uses different colors for regular tiles and special tiles.
   */
  /**
   * Highlights possible moves, with different colors for special tiles
   */
  public void highlightPossibleMoves() {
    if (gameController == null) return;

    // Reset all tiles to original colors
    resetTileColors();

    // Only highlight if die has been rolled
    if (gameController.hasRolled()) {
      List<Tile> possibleMoves = gameController.getPossibleMoves();

      for (Tile tile : possibleMoves) {
        Circle tileCircle = tileCircles.get(tile.getTileId());
        if (tileCircle != null) {
          // Different highlighting for special tiles
          if (specialTileIds.contains(tile.getTileId())) {
            tileCircle.setFill(Color.ORANGE);
            tileCircle.setStrokeWidth(3.0);
          } else {
            tileCircle.setFill(Color.YELLOW);
            tileCircle.setStrokeWidth(2.0);
          }
        }
      }
    }
  }
}