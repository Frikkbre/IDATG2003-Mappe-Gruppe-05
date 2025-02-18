package edu.ntnu.idi.bidata.idatg2003mappe.entity;

import edu.ntnu.idi.bidata.idatg2003mappe.map.Tile;

/**
 * Represents a player in the game
 * Has methods to add player, place player and move player.
 */
public class Player {
  private String name;
  private Tile currentTile;

  /**
   * Constructor for Player
   * @param name
   */
  public Player(String name, Tile startTile){
    setName(name);
    currentTile = startTile;
  }


  /**
   * Places the player on the board
   */
  public void placePlayer(Tile tileId){
    currentTile = tileId;
  }

  /**
   * Moves the player on the board
   * @param tilesToMove
   */
  public void movePlayer(int tilesToMove) {
    validateMove(tilesToMove);
    currentTile = getDestinationTile(currentTile, tilesToMove);
  }

  /**
   * Validates the move
   * @param tilesToMove
   */

  private void validateMove(int tilesToMove) {
    if (tilesToMove < 0) {
      throw new IllegalArgumentException("tilesToMove must be non-negative.");
    }
    if (currentTile == null) {
      throw new IllegalStateException("Player's current tile is not set.");
    }
  }

  /**
   * Gets the destination tile
   * @param startTile
   * @param tilesToMove
   * @return destinationTile
   */

  private Tile getDestinationTile(Tile startTile, int tilesToMove) {
    Tile destinationTile = startTile;
    int movesMade = 0;
    while (movesMade < tilesToMove && destinationTile.getNextTile() != null) {
      destinationTile = destinationTile.getNextTile();
      movesMade++;
    }
    if (movesMade < tilesToMove) {
      System.out.println("Player moved to the last tile (" + destinationTile.getTileId() + ").");
    }
    return destinationTile;
  }


  /**
   * Sets the name of the player
   * @param name
   */
  public void setName(String name){
    if(name.isBlank()){
      throw new IllegalArgumentException("Name cannot be blank");
    }
    this.name = name;
  }

  /**
   * Gets the name of the player
   * @return name
   */

  public String getName() {
    return name;
  }

  /**
   * Gets the current tile of the player
   * @return currentTile
   */

  public Tile getCurrentTile(){
    return currentTile;
  }

}
