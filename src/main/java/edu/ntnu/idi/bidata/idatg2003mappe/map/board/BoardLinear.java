package edu.ntnu.idi.bidata.idatg2003mappe.map.board;

import edu.ntnu.idi.bidata.idatg2003mappe.map.Tile;

import java.util.List;

/**
 * <p>A linear board with sequential tiles.</p>
 * <p>This class represents a board with a linear path of connected tiles,
 * typically used in games like Snakes and Ladders where movement follows
 * a predetermined path.</p>
 * <p>Features of linear boards include:</p>
 * <ul>
 *   <li>Sequential tile ordering</li>
 *   <li>Fixed paths with no branching</li>
 *   <li>Optional special connections (ladders/slides)</li>
 * </ul>
 *
 * @author Simen Gudbrandsen and Frikk Breadsroed
 * @version 0.0.3
 * @since 18.02.2025
 */
public class BoardLinear extends Board {

  /**
   * <p>Constructor for BoardLinear.</p>
   * <p>Creates an empty linear board with no tiles.</p>
   */
  public BoardLinear() {
    super();
  }

  /**
   * <p>Gets the tile by id using linear search.</p>
   * <p>Searches through all tiles sequentially to find the one with the specified ID.
   * This method may be less efficient than indexed lookups for large boards.</p>
   *
   * @param tileId The tile id.
   * @return The tile with the specified id, or <code>null</code> if not found.
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
   * <p>Gets all tiles of the board.</p>
   * <p>Returns the complete list of tiles that make up this board.</p>
   *
   * @return The list of all tiles on this board.
   */
  public List<Tile> getTiles() {
    return tiles;
  }
}
