package edu.ntnu.idi.bidata.idatg2003mappe.entity.player;

import edu.ntnu.idi.bidata.idatg2003mappe.map.Tile;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * <p>Represents a player in the game with Observer pattern support.</p>
 * <p>This class models a player entity with properties such as name, color,
 * and position on the game board. It also implements the Observer pattern to
 * notify interested components about player movements and state changes.</p>
 * <p>Key features include:</p>
 * <ul>
 *   <li>Player identification (name, ID, color)</li>
 *   <li>Position tracking on the game board</li>
 *   <li>Inventory management for collected items</li>
 *   <li>Turn status tracking (skip turn flag)</li>
 *   <li>Observer notifications for game events</li>
 * </ul>
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
  private final Set<String> inventory = new HashSet<>();

  // Observer pattern support
  private final List<PlayerObserver> observers = new ArrayList<>();

  /**
   * <p>Constructor for Player.</p>
   * <p>Initializes a new player with the specified properties and
   * places them at the given starting tile on the game board.</p>
   *
   * @param name      The player's name
   * @param ID        The player's unique identifier
   * @param color     The player's color for visual representation
   * @param startTile The starting {@link Tile} for the player
   */
  public Player(String name, int ID, String color, Tile startTile) {
    setName(name);
    setID(ID);
    currentTile = startTile;
    setColor(color);
  }

  /**
   * <p>Adds an observer to the player.</p>
   * <p>Registers a new {@link PlayerObserver} to receive notifications
   * about this player's events, such as movement or state changes.</p>
   *
   * @param observer The {@link PlayerObserver} to add
   */
  public void addObserver(PlayerObserver observer) {
    observers.add(observer);
  }

  /**
   * <p>Notifies observers that the player has moved.</p>
   * <p>Calls the {@link PlayerObserver#onPlayerMoved} method on all
   * registered observers, providing information about the movement.</p>
   *
   * @param oldTile The {@link Tile} the player moved from
   * @param newTile The {@link Tile} the player moved to
   */
  private void notifyPlayerMoved(Tile oldTile, Tile newTile) {
    for (PlayerObserver observer : observers) {
      observer.onPlayerMoved(this, oldTile, newTile);
    }
  }

  /**
   * <p>Places the player on the board.</p>
   * <p>Sets the player's current tile to the specified tile and
   * notifies observers about the position change.</p>
   *
   * @param tile The {@link Tile} to place the player on
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
   * <p>Moves the player on the board.</p>
   * <p>Advances the player by the specified number of tiles along the
   * board's path and notifies observers about the movement.</p>
   *
   * @param tilesToMove Number of tiles to move
   * @throws IllegalStateException If the player's current tile is not set
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
   * <p>Adds an item to the player's inventory.</p>
   * <p>Stores the item name in the player's inventory set for
   * later use in game mechanics such as win conditions.</p>
   *
   * @param itemName The name of the item to add
   */
  public void addInventoryItem(String itemName) {
    inventory.add(itemName);
  }

  /**
   * <p>Checks if the player has an item in their inventory.</p>
   * <p>Verifies whether the specified item exists in the player's
   * inventory collection.</p>
   *
   * @param itemName The name of the item to check for
   * @return <code>true</code> if the player has the item, <code>false</code> otherwise
   */
  public boolean hasInventoryItem(String itemName) {
    return inventory.contains(itemName);
  }

  /**
   * <p>Sets the player's color.</p>
   * <p>The color is used for visual representation of the player token
   * on the game board.</p>
   *
   * @param color The color to set
   * @throws IllegalArgumentException If the color is blank
   */
  public void setColor(String color) {
    if (color.isBlank()) {
      throw new IllegalArgumentException("Color cannot be blank");
    }
    this.color = color;
  }

  /**
   * <p>Sets the player's name.</p>
   * <p>The name is used to identify the player in the game interface
   * and result displays.</p>
   *
   * @param name The name to set
   * @throws IllegalArgumentException If the name is blank
   */
  public void setName(String name) {
    if (name.isBlank()) {
      throw new IllegalArgumentException("Name cannot be blank");
    }
    this.name = name;
  }

  /**
   * <p>Sets the player's ID.</p>
   * <p>The ID is a unique identifier for the player within the game system.</p>
   *
   * @param id The ID to set
   * @throws IllegalArgumentException If the ID is negative or greater than 6
   */
  public void setID(int id) {
    if (id < 0 || id > 6) {
      throw new IllegalArgumentException("ID cannot be negative or greater than 6");
    }
    this.ID = id;
  }

  /**
   * <p>Gets the player's color.</p>
   * <p>Returns the color used for visual representation of this player.</p>
   *
   * @return The player's color as a string
   */
  public String getColor() {
    return this.color;
  }

  /**
   * <p>Gets the player's name.</p>
   * <p>Returns the identifying name of this player.</p>
   *
   * @return The player's name
   */
  public String getName() {
    return name;
  }

  /**
   * <p>Gets the current tile of the player.</p>
   * <p>Returns the tile where the player is currently located on the board.</p>
   *
   * @return The player's current {@link Tile}
   */
  public Tile getCurrentTile() {
    return currentTile;
  }

  /**
   * <p>Gets the player's ID.</p>
   * <p>Returns the unique identifier for this player.</p>
   *
   * @return The player's ID
   */
  public int getID() {
    return ID;
  }

  /**
   * <p>Checks if the player should skip their next turn.</p>
   * <p>Returns the status of the skip turn flag, which is set when a
   * player lands on a skip turn tile.</p>
   *
   * @return <code>true</code> if the player should skip their turn, <code>false</code> otherwise
   */
  public boolean isSkipTurn() {
    return skipTurn;
  }

  /**
   * <p>Sets whether the player should skip their next turn.</p>
   * <p>This is typically called when a player lands on a skip turn tile
   * or is affected by a game event that causes them to miss a turn.</p>
   *
   * @param skipTurn <code>true</code> to make the player skip their next turn, <code>false</code> otherwise
   */
  public void setSkipTurn(boolean skipTurn) {
    this.skipTurn = skipTurn;
  }
}
