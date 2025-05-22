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

  @Override
  public void addTileToBoard(Tile tile) {
    super.addTileToBoard(tile);
    tilesById.put(tile.getTileId(), tile);
  }

  public Tile getTileById(int tileId) {
    return tilesById.get(tileId);
  }

  public void connectTiles(Tile tile1, Tile tile2) {
    tile1.addTileToTileBranch(tile2);
    tile2.addTileToTileBranch(tile1);
  }

  public List<Tile> getTiles() {
    return tiles;
  }
}
