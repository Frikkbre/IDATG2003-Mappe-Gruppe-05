package edu.ntnu.idi.bidata.idatg2003mappe.app;

import edu.ntnu.idi.bidata.idatg2003mappe.entity.Dice;
import edu.ntnu.idi.bidata.idatg2003mappe.entity.Player;
import edu.ntnu.idi.bidata.idatg2003mappe.map.BoardLinear;
import edu.ntnu.idi.bidata.idatg2003mappe.map.Tile;

import java.util.ArrayList;
import java.util.List;

/**
 * Class to represent a game of Ladder.
 * The game consists of a board, a number of players, a dice and a number of tiles.
 * The game is played by the players taking turns to roll the dice and move their markers on the board.
 * The game is won by the first player to reach the last tile on the board.
 *
 * @version 0.1
 * @since 14.02.2025
 * @author Simen Gudbrandsen and Frikk Breadsroed
 */

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

  /**
   * Method to create a board for the game.
   * The board consists of a number of tiles, each with a number.
   * The tiles are connected in a linear fashion.
   * @return the board
   */

  private BoardLinear createBoard() {
    BoardLinear board = new BoardLinear();
    int numTiles = this.numberOfTiles;
    Tile[] tiles = new Tile[numTiles];

    for (int i = 0; i < numTiles; i++) {
      tiles[i] = new Tile(i + 1);
      board.addTileToBoard(tiles[i]);
    }

    for (int i = 0; i < numTiles - 1; i++) {
      tiles[i].setNextTile(tiles[i + 1]);
    }
    return board;
  }

  /**
   * Method to create a list of players for the game.
   * The players are created with a name and added to a list.
   * @param numberOfPlayers the number of players to create
   * @return the list of players
   */

  private List<Player> createPlayers(int numberOfPlayers) {
    List<Player> players = new ArrayList<>();
    for (int i = 1; i <= numberOfPlayers; i++) {
      Player player = new Player("Player " + i);
      players.add(player);
    }
    return players;
  }

  private void playGame() { //TODO - Implement game logic
  }
}
