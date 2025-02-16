package edu.ntnu.idi.bidata.idatg2003mappe.map;

import java.util.ArrayList;
import java.util.List;

public class BoardLinear {

  public List<Tile> tiles;

  public void LadderBoard (int totalTiles) {
    this.tiles = new ArrayList<Tile>();
    for (int i = 1; i <= totalTiles; i++) {
      Tile tile = new Tile(i);
      tiles.add(tile);
      if (i > 1) {
        tiles.get(i - 2).addTile(tile);
        tile.setPreviousTile(tiles.get(i - 2));
      }
    }
  }

  public Tile getTile(int tileNumber) {
    return tiles.get(tileNumber - 1);
  }

}
