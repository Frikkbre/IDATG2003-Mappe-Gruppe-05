package edu.ntnu.idi.bidata.idatg2003mappe.map.board;

import edu.ntnu.idi.bidata.idatg2003mappe.map.Tile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class represents a board which uses branching paths.
 * For example, Missing Diamond game.
 *
 * @author Simen Gudbrandsen and Frikk Breadsroed
 * @version 0.0.1
 * @since 22.04.2025
 */

public class BoardBranching extends Board {

  private final Map<Integer, Tile> tilesById;

  public BoardBranching() {
    super();
    this.tilesById = new HashMap<>();
    this.boardName = "Missing Diamond";
  }

  public void setBoardName(String boardName) {
    this.boardName = boardName;
  }

  /**
   * Gets the name of this board.
   *
   * @return the board name
   */
  public String getBoardName() {
    return boardName;
  }

  @Override
  public void addTileToBoard(Tile tile) {
    if (tile == null) {
      throw new IllegalArgumentException("Tile cannot be null");
    }
    super.addTileToBoard(tile);
    tilesById.put(tile.getTileId(), tile);
  }

  public Tile getTileById(int tileId) {
    if (!tilesById.containsKey(tileId)) {
      throw new IllegalArgumentException("Tile with ID " + tileId + " does not exist");
    }
    return tilesById.get(tileId);
  }

  public void connectTiles(Tile tile1, Tile tile2) {
    if (tile1 == null || tile2 == null) {
      throw new IllegalArgumentException("Tiles cannot be null");
    }
    tile1.addTileToTileBranch(tile2);
    tile2.addTileToTileBranch(tile1);
  }

  public List<Tile> getTiles() {
    return tiles;
  }
}
