package edu.ntnu.idi.bidata.idatg2003mappe.map;

import edu.ntnu.idi.bidata.idatg2003mappe.movement.LadderAction;

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

  /**
   * Moves the player to the next tile.
   * @param steps The number of steps to move.
   * @return The next tile.
   */

  public Tile moveToTile (int steps) { //TODO: Not sure if this is the correct implementation. Needs work.
    Tile currentTile = this;
    for (int i = 0; i < steps; i++) {
      if (currentTile.nextTile == null) {
        return currentTile;
      }
      currentTile = currentTile.nextTile;
    }
    return currentTile;
  }

  public void setNextTile(Tile nextTile) {
    this.nextTile = nextTile;
  }

  public void setPreviousTile(Tile previousTile) {
    this.previousTile = previousTile;
  }

  public void setDestinationTile(Tile destinationTile) {
    this.destinationTile = destinationTile;
  }

  public int getTile() {
    return currentTile;
  }

  public Tile getNextTile() {
    return nextTile;
  }

  public Tile getPreviousTile() {
    return previousTile;
  }

  public Tile getDestinationTile() {
    return destinationTile;
  }


}
