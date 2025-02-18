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
  public Player(String name){
    setName(name);
  }

  /**
   * Adds a player to the game
   * @param name
   */
  public void addPlayer(String name){
    Player player = new Player(name);
    Tile startTile = new Tile(1);
    player.placePlayer(startTile); //TODO - set player to default position
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
  public void move(int tilesToMove){
    Tile nextTile = currentTile;
    for (int i = 0; i < tilesToMove; i++){
      if (nextTile.getNextTiles() != null){
        nextTile = nextTile.getNextTile();
      }else{
        break;
      }
    }
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
