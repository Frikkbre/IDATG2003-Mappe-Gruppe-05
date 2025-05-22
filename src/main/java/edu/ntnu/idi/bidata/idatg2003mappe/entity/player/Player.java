package edu.ntnu.idi.bidata.idatg2003mappe.entity.player;

import edu.ntnu.idi.bidata.idatg2003mappe.map.Tile;

import java.util.ArrayList;
import java.util.List;
import java.util.HashSet;
import java.util.Set;

/**
 * Represents a player in the game with Observer pattern support.
 * Has methods to add player, place player and move player.
 *
 * @author Simen Gudbrandsen and Frikk Breadsroed
 * @version 0.0.3
 * @since 23.05.2025
 */
public class Player {
  private String name;
  private int ID;
  private String color;
  private Tile currentTile;
  private boolean skipTurn = false;
  private Set<String> inventory = new HashSet<>();

  // Observer pattern support
  private List<PlayerObserver> observers = new ArrayList<>();

  /**
   * Constructor for Player
   *
   * @param name Player's name
   * @param ID Player's ID
   * @param color Player's color
   * @param startTile Starting tile for the player
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
   *
   * @param tile The tile to place the player on
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
   * @param tilesToMove Number of tiles to move
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

  /**
   * Adds an item to the player's inventory
   *
   * @param itemName The name of the item to add
   */
  public void addInventoryItem(String itemName) {
    inventory.add(itemName);
  }

  /**
   * Checks if the player has an item in their inventory
   *
   * @param itemName The name of the item to check for
   * @return True if the player has the item, false otherwise
   */
  public boolean hasInventoryItem(String itemName) {
    return inventory.contains(itemName);
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

  /**
   * returns if the player should skip a turn or not
   * @return skipTurn
   */
  public boolean isSkipTurn() {
    return skipTurn;
  }

  /**
   * sets if the player should skip a turn or not
   * called when a player lands on a skip turn tile
   * @param skipTurn
   */
  public void setSkipTurn(boolean skipTurn) {
    this.skipTurn = skipTurn;
  }
}