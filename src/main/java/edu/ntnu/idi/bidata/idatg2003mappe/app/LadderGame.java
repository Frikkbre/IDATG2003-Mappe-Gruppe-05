package edu.ntnu.idi.bidata.idatg2003mappe.app;

import edu.ntnu.idi.bidata.idatg2003mappe.entity.Dice;
import edu.ntnu.idi.bidata.idatg2003mappe.entity.Player;
import edu.ntnu.idi.bidata.idatg2003mappe.map.BoardLinear;
import edu.ntnu.idi.bidata.idatg2003mappe.map.Tile;

import java.util.ArrayList;
import java.util.List;

public class LadderGame {

  private BoardLinear board;
  private List<Player> players;
  private Dice dice;
  private int numberOfTiles;

  public LadderGame(int numberOfPlayers){
    System.out.println("Starting Ladder Game with " + numberOfPlayers + " players.");

    this.numberOfTiles = 100;
    this.board = createBoard();
    this.players = createPlayers(numberOfPlayers);
    this.dice = new Dice();

    playGame();
  }

  private BoardLinear createBoard() {
    BoardLinear board = new BoardLinear();
    int numTiles = this.numberOfTiles;
    Tile[] tiles = new Tile[numTiles];

    for (int i = 0; i < numTiles; i++) {
      tiles[i] = new Tile(i + 1); // Tile IDs starting at 1.
      board.addTileToBoard(tiles[i]);
    }

    for (int i = 0; i < numTiles - 1; i++) {
      tiles[i].setNextTile(tiles[i + 1]);
    }
    return board;
  }

  private List<Player> createPlayers(int numberOfPlayers) {
    List<Player> players = new ArrayList<>();
    for (int i = 1; i <= numberOfPlayers; i++) {
      Player player = new Player("Player " + i);
      players.add(player);
    }
    return players;
  }

  private void playGame() {

  }
}
