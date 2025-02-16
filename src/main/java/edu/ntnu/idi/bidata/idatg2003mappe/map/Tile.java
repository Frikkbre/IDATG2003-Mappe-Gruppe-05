package edu.ntnu.idi.bidata.idatg2003mappe.map;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a tile on the board.
 *
 * @author Simen Gudbrands and Frikk Breadsroed
 * @version 1.0
 * @since 15.02.2025
 */
public class Tile {
  private int tileId;
  private List<Tile> nextTilesOnBoard;
  private Tile nextTile;
  private Tile previousTile;
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
  public void addTiles(Tile tile) {
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
