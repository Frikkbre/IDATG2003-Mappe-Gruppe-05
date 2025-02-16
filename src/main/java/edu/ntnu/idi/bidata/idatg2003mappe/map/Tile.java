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

  /**
   * Sets the next tile.
   * @param nextTile The next tile.
   */

  public void setNextTile(Tile nextTile) {
    this.nextTile = nextTile;
  }

  /**
   * Sets the previous tile.
   * @param previousTile The previous tile.
   */

  public void setPreviousTile(Tile previousTile) {
    this.previousTile = previousTile;
  }

  /**
   * Sets the destination tile.
   * @param destinationTile The destination tile.
   */

  public void setDestinationTile(Tile destinationTile) {
    this.destinationTile = destinationTile;
  }

  /**
   * Gets the current tile.
   * @return The current tile.
   */

  public int getTile() {
    return currentTile;
  }

  /**
   * Gets the next tile.
   * @return The next tile.
   */

  public Tile getNextTile() {
    return nextTile;
  }

  /**
   * Gets the previous tile.
   * @return The previous tile.
   */

  public Tile getPreviousTile() {
    return previousTile;
  }

  /**
   * Gets the destination tile.
   * @return The destination tile.
   */

  public Tile getDestinationTile() {
    return destinationTile;
  }

}
