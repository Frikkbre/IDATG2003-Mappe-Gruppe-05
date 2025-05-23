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
   * <p>Gets the starting tile of the board.</p>
   * <p>Returns the first tile that was added to the board,
   * which serves as the starting point for players.</p>
   *
   * @return The starting tile of the board.
   */
  public Tile getTileByIdLinear(int tileId) {
    return tiles.stream()
        .filter(tile -> tile.getTileId() == tileId)
        .findFirst()
        .orElse(null);
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
