package edu.ntnu.idi.bidata.idatg2003mappe.map;

import java.util.ArrayList;
import java.util.List;

public class BoardLinear {
  private List<Tile> tiles;
  private Tile startTile;

  public void addTile(Tile tile) {
    if (tiles == null) {
      tiles = new ArrayList<>();
    }
    tiles.add(tile);
    if (tiles.size() == 1) {
      startTile = tile;
    }
  }

  public void addMultipleTiles(Tile... tiles) {
    for (Tile tile : tiles) {
      addTile(tile);
    }
  }

  public Tile getStartTile() {
    return startTile;
  }
}
