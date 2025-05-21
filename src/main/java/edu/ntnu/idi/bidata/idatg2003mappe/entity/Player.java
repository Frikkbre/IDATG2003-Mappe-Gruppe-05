package edu.ntnu.idi.bidata.idatg2003mappe.entity;

import edu.ntnu.idi.bidata.idatg2003mappe.map.Tile;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a player in the game with Observer pattern support.
 * Has methods to add player, place player and move player.
 *
 * @author Simen Gudbrandsen and Frikk Breadsroed
 * @version 0.0.2
 * @since 21.05.2025
 */
public class Player {
  private String name;
  private int ID;
  private String color;
  private Tile currentTile;

  // Observer pattern support
  private List<PlayerObserver> observers = new ArrayList<>();

  /**
   * Constructor for Player
   *
   * @param name
   */
  public Player(String name, int ID, String color, Tile startTile) {
    setName(name);
    setID(ID);
    currentTile = startTile;
    setColor(color);
  }

  /**
   * Adds an observer to the player.
   *
   * @param observer The observer to add.
   */
  public void addObserver(PlayerObserver observer) {
    observers.add(observer);
  }

  /**
   * Removes an observer from the player.
   *
   * @param observer The observer to remove.
   */
  public void removeObserver(PlayerObserver observer) {
    observers.remove(observer);
  }

  /**
   * Notifies observers that the player has moved.
   *
   * @param oldTile The tile the player moved from.
   * @param newTile The tile the player moved to.
   */
  private void notifyPlayerMoved(Tile oldTile, Tile newTile) {
    for (PlayerObserver observer : observers) {
      observer.onPlayerMoved(this, oldTile, newTile);
    }
  }

  /**
   * Places the player on the board
   */
  public void placePlayer(Tile tile) {
    Tile oldTile = currentTile;
    currentTile = tile;

    // Notify observers about the movement
    if (oldTile != null) {
      notifyPlayerMoved(oldTile, currentTile);
    }
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

    Tile oldTile = currentTile;
    currentTile = currentTile.getTileAtDistance(tilesToMove);

    // Notify observers about the movement
    notifyPlayerMoved(oldTile, currentTile);
  }

  public void setColor(String color) {
    if (color.isBlank()) {
      throw new IllegalArgumentException("Color cannot be blank");
    }
    this.color = color;
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
    if (id < 0 || id > 6) {
      throw new IllegalArgumentException("ID cannot be negative or greater than 6");
    }
    this.ID = id;
  }

  public String getColor() {
    return this.color;
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