package edu.ntnu.idi.bidata.idatg2003mappe.app.missingdiamond.ui;

import edu.ntnu.idi.bidata.idatg2003mappe.app.missingdiamond.controller.MissingDiamondController;
import edu.ntnu.idi.bidata.idatg2003mappe.map.Tile;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * <p>Enhanced tile highlighter that properly handles Missing Diamond movement rules.</p>
 * <p>Highlights both regular movement tiles and special tiles (red tiles with tokens)
 * to provide visual feedback about valid moves during gameplay.</p>
 * <p>The highlighter uses different colors to indicate:</p>
 * <ul>
 *   <li>Regular valid moves</li>
 *   <li>Special tiles with tokens</li>
 *   <li>Special tiles without tokens</li>
 *   <li>The current player's position</li>
 * </ul>
 *
 * @version 0.0.1
 * @since 22.05.2025
 */
public class TileHighlighter {
  private static final Color SPECIAL_TILE_COLOR = Color.RED;
  private static final Color NORMAL_TILE_COLOR = Color.BLACK;
  private static final Color VALID_MOVE_COLOR = Color.YELLOW;
  private static final Color SPECIAL_VALID_MOVE_COLOR = Color.ORANGE;
  private static final Color CURRENT_PLAYER_COLOR = Color.LIME;
  private static final double NORMAL_STROKE_WIDTH = 1.5;
  private static final double HIGHLIGHTED_STROKE_WIDTH = 3.0;
  private final Map<Integer, Circle> tileCircles;
  private final Set<Integer> specialTileIds;
  private final MissingDiamondController gameController;

  /**
   * <p>Creates a new enhanced TileHighlighter.</p>
   * <p>Initializes a highlighter that can visually indicate valid moves on the game board
   * based on the current game state and die roll.</p>
   *
   * @param tileCircles    Map of tile IDs to Circle objects representing visual tiles
   * @param specialTileIds Set of IDs for special tiles (red tiles)
   * @param gameController Game controller reference for accessing game state
   */
  public TileHighlighter(Map<Integer, Circle> tileCircles, Set<Integer> specialTileIds,
                         MissingDiamondController gameController) {
    this.tileCircles = tileCircles;
    this.specialTileIds = specialTileIds;
    this.gameController = gameController;

  }

  /**
   * <p>Resets all tiles to their original colors and stroke widths.</p>
   * <p>This method should be called before applying new highlighting to ensure
   * a clean visual state.</p>
   */
  public void resetTileColors() {

    tileCircles.forEach((tileId, tile) -> {
      // Reset to original color based on tile type
      if (specialTileIds.contains(tileId)) {
        tile.setFill(SPECIAL_TILE_COLOR);
      } else {
        tile.setFill(NORMAL_TILE_COLOR);
      }

      // Reset stroke
      tile.setStroke(Color.WHITE);
      tile.setStrokeWidth(NORMAL_STROKE_WIDTH);
    });
  }

  /**
   * <p>Highlights possible moves based on the current die roll.</p>
   * <p>This is the main method that should be called to update tile highlighting
   * after a player rolls the die or the game state changes.</p>
   * <p>The method performs these steps:</p>
   * <ol>
   *   <li>Resets all tile colors to their default state</li>
   *   <li>Highlights the current player's position</li>
   *   <li>If the die has been rolled, highlights all valid destination tiles</li>
   * </ol>
   */
  public void highlightPossibleMoves() {
    if (gameController == null) {
      return;
    }

    // Reset all tiles first
    resetTileColors();

    // Highlight current player position
    highlightCurrentPlayerPosition();

    // Only highlight possible moves if die has been rolled
    if (!gameController.hasRolled()) {
      return;
    }

    // Get possible moves from controller
    List<Tile> possibleMoves = gameController.getPossibleMoves();

    // Highlight each possible move
    possibleMoves.forEach(this::highlightTile);
  }

  /**
   * <p>Highlights the current player's position.</p>
   * <p>Adds a distinctive border around the tile where the current player is located
   * to make it easy to identify.</p>
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
   * <p>Highlights a specific tile as a valid move destination.</p>
   * <p>Uses different colors based on the tile type and whether it has a token:
   * <ul>
   *   <li>Special tile with token: orange</li>
   *   <li>Special tile without token: light coral</li>
   *   <li>Regular movement tile: yellow</li>
   * </ul>
   *
   * @param tile The {@link Tile} to highlight as a valid destination
   */
  private void highlightTile(Tile tile) {
    Circle tileCircle = tileCircles.get(tile.getTileId());
    if (tileCircle == null) {
      return;
    }

    boolean isSpecialTile = specialTileIds.contains(tile.getTileId());
    boolean hasToken = gameController.hasTokenAtTile(tile);

    // Choose highlighting color based on tile type and whether it has a token
    Color highlightColor;
    if (isSpecialTile && hasToken) {
      // Special tile with token
      highlightColor = SPECIAL_VALID_MOVE_COLOR;

    } else if (isSpecialTile) {
      // Special tile without token
      highlightColor = Color.LIGHTCORAL;
    } else {
      // Regular movement tile
      highlightColor = VALID_MOVE_COLOR;
    }

    // Apply highlighting
    tileCircle.setFill(highlightColor);
    tileCircle.setStroke(Color.WHITE);
    tileCircle.setStrokeWidth(HIGHLIGHTED_STROKE_WIDTH);
  }

}
