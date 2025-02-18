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
   * Moves the player
   */
  public void movePlayer(int tilesToMove){
    Tile nextTile = currentTile;
    for (int i = 0; i < tilesToMove; i++){
      if (nextTile.getNextTiles() != null){
        nextTile = nextTile.getNextTile();
      }else{
        throw new IllegalArgumentException("Cannot move player past the last tile");
      }
    }
    currentTile = nextTile;
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

  public Tile getCurrentTile(){
    return currentTile;
  }

}
