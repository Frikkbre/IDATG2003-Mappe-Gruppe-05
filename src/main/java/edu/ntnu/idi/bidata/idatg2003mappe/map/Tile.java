package edu.ntnu.idi.bidata.idatg2003mappe.map;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a tile on the board.
 *
 * @author Simen Gudbrands and Frikk Breadsroed
 * @version 0.0.2
 * @since 15.02.2025
 */

public class Tile {

  /**
   * This is a unique identifier for the tile.
   * Either position or order of the tile.
   */
  private int tileId;

  /**
   * The next tiles on the board that are connected
   * to this tile. Useful for the missing diamond game.
   */
  private List<Tile> nextTilesOnBoard;

  /**
   * The next immediate tile next to the main or current
   * tile. Showing which tile is next.
   */
  private Tile nextTile;

  /**
   * The previous immediate tile next to the main or current
   * tile. Showing which tile is previous to the current tile.
   * Useful for backtracking or undoing moves.
   */
  private Tile previousTile;

  /**
   * The destination tile. Useful for jumping the player
   * to a different tile. For example, in the ladder game.
   */
  private Tile destinationTile;

  /**
   * Constructor for the Tile class.
   *
   * @param tileId The current tile id.
   */
  public Tile(int tileId) {
    this.tileId = tileId;
    this.nextTilesOnBoard = new ArrayList<>();
  }

  /**
   * Adds a tile to the list of next tiles.
   * Useful for the MissingDiamond board game as
   * it need to branch different tiles.
   *
   * @param tile The tile to add to tile.
   */
  public void addTileToTileBranch(Tile tile) {
    nextTilesOnBoard.add(tile);
  }

  /**
   * Sets the next tile.
   *
   * @param nextTile The next tile.
   */
  public void setNextTile(Tile nextTile) {
    this.nextTile = nextTile;
  }

  /**
   * Sets the previous tile.
   *
   * @param previousTile The previous tile.
   */
  public void setPreviousTile(Tile previousTile) {
    this.previousTile = previousTile;
  }

  /**
   * Sets the destination tile.
   *
   * @param destinationTile The destination tile.
   */
  public void setDestinationTile(Tile destinationTile) {
    this.destinationTile = destinationTile;
  }

  /**
   * Gets the tile at a certain distance from the current tile.
   * If the distance is negative, an IllegalArgumentException is thrown.
   * If the distance is greater than the number of tiles on the board,
   * the last tile is returned.
   *
   * @param steps The number of steps to move.
   * @return The tile at the specified distance.
   */

  public Tile getTileAtDistance(int steps) {
    if (steps < 0) {
      throw new IllegalArgumentException("steps must be non-negative.");
    }

    Tile current = this;
    for (int i = 0; i < steps; i++) {
      if (current.getNextTile() == null) {
        System.out.println("Reached the last tile at tile ID " + current.getTileId());
        return current;
      }
      current = current.getNextTile();
    }
    return current;
  }


  /**
   * Gets the current tile id.
   *
   * @return The current tile id.
   */
  public int getTileId() {
    return tileId;
  }

  /**
   * Gets the next tile.
   *
   * @return The next tile.
   */
  public Tile getNextTile() {
    return nextTile;
  }

  /**
   * Gets the previous tile.
   *
   * @return The previous tile.
   */
  public Tile getPreviousTile() {
    return previousTile;
  }

  /**
   * Gets the destination tile.
   *
   * @return The destination tile.
   */
  public Tile getDestinationTile() {
    return destinationTile;
  }

  /**
   * Gets the list of next tiles.
   *
   * @return The list of next tiles.
   */
  public List<Tile> getNextTiles() {
    return nextTilesOnBoard;
  }
}
