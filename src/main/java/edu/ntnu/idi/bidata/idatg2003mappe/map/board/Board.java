package edu.ntnu.idi.bidata.idatg2003mappe.map.board;

import edu.ntnu.idi.bidata.idatg2003mappe.map.Tile;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>Represents a generic board with tiles.</p>
 * <p>This abstract base class provides common functionality for all types of game boards.
 * It maintains a collection of tiles and provides methods for adding tiles and
 * accessing the starting tile.</p>
 * <p>Key features include:</p>
 * <ul>
 *   <li>Tile management (adding and retrieving)</li>
 *   <li>First tile designation as start point</li>
 *   <li>Board identification with name</li>
 * </ul>
 *
 * @author Simen Gudbrandsen and Frikk Breadsroed
 * @version 0.0.1
 * @since 22.02.2025
 */
public class Board {
  protected Tile startTile;
  protected List<Tile> tiles;
  protected String boardName;

  /**
   * <p>Adds a tile to the board.</p>
   * <p>If this is the first tile added, it becomes the starting tile.
   * The method initializes the tiles list if it doesn't exist yet.</p>
   *
   * @param tile The tile to add.
   */
  public void addTileToBoard(Tile tile) {
    if (tiles == null) {
      tiles = new ArrayList<>();
    }
    tiles.add(tile);
    if (tiles.size() == 1) {
      startTile = tile;
    }
  }

  /**
   * <p>Adds multiple tiles to the board.</p>
   * <p>Convenience method to add several tiles at once.
   * Calls {@link #addTileToBoard(Tile)} for each provided tile.</p>
   *
   * @param tiles The tiles to add.
   */
  public void addMultipleTilesToBoard(Tile... tiles) {
    for (Tile tile : tiles) {
      addTileToBoard(tile);
    }
  }

  /**
   * <p>Gets the start tile of the board.</p>
   * <p>Returns the first tile that was added to the board,
   * which serves as the starting point for players.</p>
   *
   * @return The start tile.
   */
  public Tile getStartTile() {
    return startTile;
  }

  /**
   * <p>Gets a tile by its ID.</p>
   * <p>Searches through all tiles to find one with the matching ID.
   * This is a base implementation that performs a linear search;
   * subclasses may provide more efficient lookup methods.</p>
   *
   * @param tileId The ID of the tile to find
   * @return The tile with the specified ID, or <code>null</code> if not found or if the board is empty
   */
  public Tile getTileById(int tileId) {
    if (tiles == null) {
      return null;
    }

    for (Tile tile : tiles) {
      if (tile.getTileId() == tileId) {
        return tile;
      }
    }
    return null;
  }
}
