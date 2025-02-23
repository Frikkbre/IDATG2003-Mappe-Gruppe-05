package edu.ntnu.idi.bidata.idatg2003mappe.entity;

import edu.ntnu.idi.bidata.idatg2003mappe.map.Tile;

/**
 * Represents a player in the game
 * Has methods to add player, place player and move player.
 *
 * @author Simen Gudbrandsen and Frikk Breadsroed
 * @version 0.0.1
 * @since 19.02.2025
 */

public class Player {
  private String name;
  private int ID;
  private Tile currentTile;


  /**
   * Constructor for Player
   *
   * @param name
   */
  public Player(String name, Tile startTile, int ID) {
    setName(name);
    setID(ID);
    currentTile = startTile;
  }




  /**
   * Places the player on the board
   */
  public void placePlayer(Tile tileId) {
    currentTile = tileId;
  }

  /**
   * Moves the player on the board
   *
   * @param tilesToMove
   */
  public void movePlayer(int tilesToMove) {
    if (currentTile == null) {
      throw new IllegalStateException("Player's current tile is not set.");
    }
    currentTile = currentTile.getTileAtDistance(tilesToMove);
  }

  /**
   * Sets the name of the player
   *
   * @param name
   */
  public void setName(String name) {
    if (name.isBlank()) {
      throw new IllegalArgumentException("Name cannot be blank");
    }
    this.name = name;
  }

  void setID(int id) {
    if(id < 0 || id > 6) {
      throw new IllegalArgumentException("ID cannot be negative");
    }
    this.ID = id;
  }

  /**
   * Gets the name of the player
   *
   * @return name
   */

  public String getName() {
    return name;
  }

  /**
   * Gets the current tile of the player
   *
   * @return currentTile
   */

  public Tile getCurrentTile() {
    return currentTile;
  }

  public int getID() {
    return ID;
  }
}
