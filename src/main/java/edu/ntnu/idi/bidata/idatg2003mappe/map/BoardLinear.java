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

public class BoardLinear {
  private List<Tile> tiles;
  private Tile startTile;

  public void addTileToBoard(Tile tile) {
    if (tiles == null) { //Can this ever be null?
      tiles = new ArrayList<>();
    }
    tiles.add(tile);
    if (tiles.size() == 1) {
      startTile = tile;
    }
  }

  public void addMultipleTilesToBoard(Tile... tiles) {
    for (Tile tile : tiles) {
      addTileToBoard(tile);
    }
  }

  public Tile getStartTile() {
    return startTile;
  }

  public List<Tile> getTiles() {
    return tiles;
  }
}
