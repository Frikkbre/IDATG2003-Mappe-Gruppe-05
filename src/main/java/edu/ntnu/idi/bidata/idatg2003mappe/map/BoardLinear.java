package edu.ntnu.idi.bidata.idatg2003mappe.map;

import java.util.ArrayList;
import java.util.List;

/**
 * A linear board with tiles.
 *
 * @version 0.0.1
 * @since 18.02.2025
 * @author Simen Gudbrandsen and Frikk Breadsroed
 */

public class BoardLinear { //TODO: Might be better to make a superclass for BoardLinear. Maybe Board class?
  private List<Tile> tiles;
  private Tile startTile;

  /**
   * Adds a tile to the board.
   *
   * @param tile The tile to add.
   */

  public void addTileToBoard(Tile tile) {
    if (tiles == null) { //Can this ever be null?
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

  //TODO: Setters?

  /**
   * Gets the start tile of the board.
   *
   * @return The start tile.
   */

  public Tile getStartTile() {
    return startTile;
  }

  /**
   * Gets the tiles of the board.
   *
   * @return The tiles.
   */

  public List<Tile> getTiles() {
    return tiles;
  }
}
