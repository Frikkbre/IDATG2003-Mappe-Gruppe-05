package edu.ntnu.idi.bidata.idatg2003mappe.map;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>Represents a tile on the board.</p>
 * <p>A tile is a basic building block of a game board. It has properties such as
 * an identifier, connections to other tiles, and potentially special effects.</p>
 * <p>Tiles can be connected in different ways:</p>
 * <ul>
 *   <li>Linear connections (next/previous) for sequential board layouts</li>
 *   <li>Branching connections for more complex board designs</li>
 *   <li>Destination connections for special movements like ladders or teleports</li>
 * </ul>
 *
 * @author Simen Gudbrands and Frikk Breadsroed
 * @version 0.0.2
 * @since 15.02.2025
 */
public class Tile {

  private String effect;

  /**
   * <p>This is a unique identifier for the tile.</p>
   * <p>Either position or order of the tile.</p>
   */
  private final int tileId;

  /**
   * <p>The next tiles on the board that are connected
   * to this tile. Useful for the missing diamond game.</p>
   */
  private final List<Tile> nextTilesOnBoard;

  /**
   * <p>The next immediate tile next to the main or current
   * tile. Showing which tile is next.</p>
   */
  private Tile nextTile;

  /**
   * <p>The previous immediate tile next to the main or current
   * tile. Showing which tile is previous to the current tile.
   * Useful for backtracking or undoing moves.</p>
   */
  private Tile previousTile;

  /**
   * <p>The destination tile. Useful for jumping the player
   * to a different tile. For example, in the ladder game.</p>
   */
  private Tile destinationTile;

  /**
   * <p>Constructor for the Tile class.</p>
   * <p>Creates a new tile with the specified ID and initializes
   * an empty list for branching connections.</p>
   *
   * @param tileId The current tile id.
   */
  public Tile(int tileId) {
    this.tileId = tileId;
    this.nextTilesOnBoard = new ArrayList<>();
  }

  /**
   * <p>Adds a tile to the list of next tiles.</p>
   * <p>Useful for the MissingDiamond board game as
   * it need to branch different tiles.</p>
   *
   * @param tile The tile to add to tile.
   */
  public void addTileToTileBranch(Tile tile) {
    nextTilesOnBoard.add(tile);
  }

  /**
   * <p>Sets the next tile.</p>
   * <p>Establishes a one-way connection from this tile to the next,
   * creating a path for sequential movement.</p>
   *
   * @param nextTile The next tile.
   */
  public void setNextTile(Tile nextTile) {
    this.nextTile = nextTile;
  }

  /**
   * <p>Sets the previous tile.</p>
   * <p>Establishes a reference to the tile that comes before this one
   * in a sequential layout.</p>
   *
   * @param previousTile The previous tile.
   */
  public void setPreviousTile(Tile previousTile) {
    this.previousTile = previousTile;
  }

  /**
   * <p>Sets the destination tile.</p>
   * <p>Establishes a special connection to another tile, used for
   * implementing ladders, slides, or teleportation effects.</p>
   *
   * @param destinationTile The destination tile.
   */
  public void setDestinationTile(Tile destinationTile) {
    this.destinationTile = destinationTile;
  }

  /**
   * <p>Gets the tile at a certain distance from the current tile.</p>
   * <p>If the distance is negative, an IllegalArgumentException is thrown.
   * If the distance is greater than the number of tiles on the board,
   * the last tile is returned.</p>
   * <p>This method moves along the path defined by the nextTile connections.</p>
   *
   * @param steps The number of steps to move.
   * @return The tile at the specified distance.
   * @throws IllegalArgumentException if steps is negative (except special case -44)
   */
  public Tile getTileAtDistance(int steps) {
    Tile current = this;
    if (steps == -44) {
      current = current.getNextTile();
    } else if (steps < 0) {
      throw new IllegalArgumentException("steps must be non-negative.");
    }

    for (int i = 0; i < steps && current.getNextTile() != null; i++) {
      current = current.getNextTile();
    }
    return current;
  }

  /**
   * <p>Gets the current tile id.</p>
   * <p>Returns the unique identifier assigned to this tile.</p>
   *
   * @return The current tile id.
   */
  public int getTileId() {
    return tileId;
  }

  /**
   * <p>Gets the next tile.</p>
   * <p>Returns the tile that follows this one in a sequential layout.</p>
   *
   * @return The next tile.
   */
  public Tile getNextTile() {
    return nextTile;
  }

  /**
   * <p>Gets the previous tile.</p>
   * <p>Returns the tile that comes before this one in a sequential layout.</p>
   *
   * @return The previous tile.
   */
  public Tile getPreviousTile() {
    return previousTile;
  }

  /**
   * <p>Gets the destination tile.</p>
   * <p>Returns the special destination connected to this tile,
   * used for ladders, slides, or teleportation effects.</p>
   *
   * @return The destination tile.
   */
  public Tile getDestinationTile() {
    return destinationTile;
  }

  /**
   * <p>Gets the list of next tiles.</p>
   * <p>Returns all tiles connected to this one in a branching layout,
   * used in games with non-linear movement like Missing Diamond.</p>
   *
   * @return The list of next tiles.
   */
  public List<Tile> getNextTiles() {
    return nextTilesOnBoard;
  }

  /**
   * <p>Gets the effect of the tile.</p>
   * <p>Returns the special effect associated with this tile,
   * such as "skipTurn" or "backToStart".</p>
   *
   * @return The effect string, or null if this tile has no special effect.
   */
  public String getEffect() {
    return effect;
  }

  /**
   * <p>Sets the effect of the tile.</p>
   * <p>Assigns a special effect to this tile that will be triggered
   * when a player lands on it.</p>
   *
   * @param effect The effect string to assign.
   */
  public void setEffect(String effect) {
    this.effect = effect;
  }
}
