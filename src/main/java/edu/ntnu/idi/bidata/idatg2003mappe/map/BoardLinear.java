package edu.ntnu.idi.bidata.idatg2003mappe.map;

import java.util.List;

/**
 * A linear board with tiles.
 *
 * @version 0.0.1
 * @since 18.02.2025
 * @author Simen Gudbrandsen and Frikk Breadsroed
 */

public class BoardLinear extends Board {

  public BoardLinear() {
    super();
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
