package edu.ntnu.idi.bidata.idatg2003mappe.map;

/**
 * Represents a tile on the board.
 *
 * @version 1.0
 * @since 15.02.2025
 * @author Simen Gudbrandsen and Frikk Braendsroed
 */

public class Tile {
  private int currentTile;
  private Tile nextTile;
  private Tile previousTile;
  private Tile destinationTile;

  /**
   * Constructor for the Tile class.
   * @param currentTile The current tile.
   */

  public Tile(int currentTile) {
    this.currentTile = currentTile;
  }

  public Tile moveToTile (int steps) { //TODO: Not sure if this is the correct implementation. Needs work.
    Tile nextTile = this;
    for (int i = 0; i < steps; i++) {
      nextTile = nextTile.nextTile;
    }
    return nextTile;
  }

  public int getCurrentTile() {
    return currentTile;
  }


}
