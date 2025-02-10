package edu.ntnu.idi.bidata.idatg2003mappe.entity;

import edu.ntnu.idi.bidata.idatg2003mappe.map.Tile;

public class Player {
  private String name;
  private Tile currentTile;

  public Player(String name){
    setName(name);
  }

  public void addPlayer(String name){
    Player player = new Player(name);
    player.placePlayer(); //TODO - set player to default position
  }

  public void placePlayer(){
    // Place player on the board
  }

  public void move(){
    // Move the player
  }


  public void setName(String name){
    if(name.isBlank()){
      throw new IllegalArgumentException("Name cannot be blank");
    }
    this.name = name;
  }

}
