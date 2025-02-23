package edu.ntnu.idi.bidata.idatg2003mappe.map;

import java.util.List;

/**
 * A linear board with tiles.
 *
 * @author Simen Gudbrandsen and Frikk Breadsroed
 * @version 0.0.3
 * @since 18.02.2025
 */

public class BoardLinear extends Board {

  public BoardLinear() {
    super();
  }

  /**
   * Gets the tile by id.
   *
   * @param tileId The tile id.
   * @return The tile with the specified id.
   */

  public Tile getTileByIdLinear(int tileId) {
    for (Tile tile : tiles) {
      if (tile.getTileId() == tileId) {
        return tile;
      }
    }
    return null;
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
