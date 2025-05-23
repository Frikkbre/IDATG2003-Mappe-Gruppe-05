package edu.ntnu.idi.bidata.idatg2003mappe.map.board;

import edu.ntnu.idi.bidata.idatg2003mappe.map.Tile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>This class represents a board which uses branching paths.</p>
 * <p>Unlike linear boards, a branching board allows tiles to connect
 * to multiple other tiles in any direction, creating a network or graph
 * structure. This is ideal for games like Missing Diamond that need
 * more complex movement options.</p>
 * <p>Features of branching boards include:</p>
 * <ul>
 *   <li>Non-linear tile connections</li>
 *   <li>Multiple possible paths from each location</li>
 *   <li>Efficient tile lookup by ID</li>
 * </ul>
 *
 * @author Simen Gudbrandsen and Frikk Breadsroed
 * @version 0.0.1
 * @since 22.04.2025
 */
public class BoardBranching extends Board {

  private final Map<Integer, Tile> tilesById;

  /**
   * <p>Constructor for BoardBranching.</p>
   * <p>Creates an empty branching board with a default name of "Missing Diamond"
   * and initializes the tile lookup map.</p>
   */
  public BoardBranching() {
    super();
    this.tilesById = new HashMap<>();
    this.boardName = "Missing Diamond";
  }

  /**
   * <p>Sets the name of this board.</p>
   * <p>Changes the display name of the board, which can be used
   * for identification in UI elements.</p>
   *
   * @param boardName The new name for the board
   */
  public void setBoardName(String boardName) {
    this.boardName = boardName;
  }

  /**
   * <p>Gets the name of this board.</p>
   * <p>Returns the display name assigned to this board.</p>
   *
   * @return The board name
   */
  public String getBoardName() {
    return boardName;
  }

  /**
   * <p>Adds a tile to the board.</p>
   * <p>Extends the parent method to also add the tile to the
   * ID-based lookup map for efficient retrieval.</p>
   *
   * @param tile The tile to add
   * @throws IllegalArgumentException if the tile is null
   */
  @Override
  public void addTileToBoard(Tile tile) {
    if (tile == null) {
      throw new IllegalArgumentException("Tile cannot be null");
    }
    super.addTileToBoard(tile);
    tilesById.put(tile.getTileId(), tile);
  }

  /**
   * <p>Gets a tile by its ID using the lookup map.</p>
   * <p>Provides efficient O(1) retrieval of tiles by their ID.</p>
   *
   * @param tileId The ID of the tile to retrieve
   * @return The tile with the specified ID
   * @throws IllegalArgumentException if no tile with the given ID exists
   */
  public Tile getTileById(int tileId) {
    if (!tilesById.containsKey(tileId)) {
      throw new IllegalArgumentException("Tile with ID " + tileId + " does not exist");
    }
    return tilesById.get(tileId);
  }

  /**
   * <p>Connects two tiles bidirectionally.</p>
   * <p>Creates a two-way connection between the specified tiles,
   * allowing movement in either direction.</p>
   *
   * @param tile1 The first tile to connect
   * @param tile2 The second tile to connect
   * @throws IllegalArgumentException if either tile is null
   */
  public void connectTiles(Tile tile1, Tile tile2) {
    if (tile1 == null || tile2 == null) {
      throw new IllegalArgumentException("Tiles cannot be null");
    }
    tile1.addTileToTileBranch(tile2);
    tile2.addTileToTileBranch(tile1);
  }

  /**
   * <p>Gets all tiles of the board.</p>
   * <p>Returns the complete list of tiles that make up this board.</p>
   *
   * @return The list of all tiles on this board
   */
  public List<Tile> getTiles() {
    return tiles;
  }
}
