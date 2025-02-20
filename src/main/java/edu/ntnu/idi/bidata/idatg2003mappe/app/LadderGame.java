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
 * @version 0.2
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
    this.board = createBoard(numberOfTiles);
    this.players = createPlayers(numberOfPlayers);
    this.dice = new Dice();

    playGame();
  }

  /**
   * Method to create a board for the game.
   * The board consists of a number of tiles, each with a number.
   * The tiles are connected in a linear fashion.
   *
   * @return the board
   */

  protected BoardLinear createBoard(int numberOfTiles) {
    BoardLinear board = new BoardLinear();
    Tile[] tiles = new Tile[numberOfTiles];

    for (int i = 0; i < numberOfTiles; i++) {
      tiles[i] = new Tile(i + 1);
      board.addTileToBoard(tiles[i]);
    }

    for (int i = 0; i < numberOfTiles - 1; i++) {
      tiles[i].setNextTile(tiles[i + 1]);
    }
    return board;
  }

  /**
   * Method to create a list of players for the game.
   * The players are created with a name and added to a list.
   *
   * @param numberOfPlayers the number of players to create
   * @return the list of players
   */

  protected List<Player> createPlayers(int numberOfPlayers) {
    List<Player> players = new ArrayList<>();
    Tile startTile = board.getTiles().get(0);
    for (int i = 1; i <= numberOfPlayers; i++) {
      Player player = new Player("Player " + i, startTile);
      players.add(player);
    }
    return players;
  }


  /**
   * main game method
   * The game is played by the players taking turns to roll
   * the dice and move their markers on the board.
   * The game is won by the first player to reach the last
   * tile on the board.
   */
  void playGame() {
    boolean hasWon = false;
    int indexCurrentPlayer = 0;
    int roll = 0;

    while(!hasWon){
      Player currentPlayer = players.get(indexCurrentPlayer);
      System.out.println("Player : current player is: " + (indexCurrentPlayer + 1));

      roll = dice.rollDice();
      System.out.println("Die : die rolled: " + roll);

      System.out.println("Tile Current : Tile before moving " + currentPlayer.getCurrentTile().getTileId());
      currentPlayer.movePlayer(roll);
      System.out.println("Tile Moved :  Tile after moving " + currentPlayer.getCurrentTile().getTileId());

      //Check if player is at destination tile
      if (currentPlayer.getCurrentTile().getTileId() == numberOfTiles) { //numberOfTiles - 1?
        hasWon = true;
      }

      //If-statement to check if index is at end og list of players
      if(indexCurrentPlayer != players.size() - 1){
        indexCurrentPlayer++;
      }else{
        indexCurrentPlayer = 0;
      }
    }
  }

  /**
   * Method to get the list of players in the game.
   *
   * @return the list of players
   */

  public List<Player> getPlayers() {
    return players;
  }
}
