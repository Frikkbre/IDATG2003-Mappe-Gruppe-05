package edu.ntnu.idi.bidata.idatg2003mappe.map;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a board with tiles.
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
   * Adds a tile to the board.
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
   * Adds multiple tiles to the board.
   *
   * @param tiles The tiles to add.
   */

  public void addMultipleTilesToBoard(Tile... tiles) {
    for (Tile tile : tiles) {
      addTileToBoard(tile);
    }
  }

  /**
   * Gets the start tile of the board.
   *
   * @return The start tile.
   */

  public Tile getStartTile() {
    return startTile;
  }

  /**
   * Gets the name of the board.
   *
   * @return The name of the board.
   */

  public String getBoardName() {
    return boardName;
  }

}
