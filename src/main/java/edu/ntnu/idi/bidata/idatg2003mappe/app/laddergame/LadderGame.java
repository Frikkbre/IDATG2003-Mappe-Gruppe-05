package edu.ntnu.idi.bidata.idatg2003mappe.app.laddergame;

import edu.ntnu.idi.bidata.idatg2003mappe.entity.Die;
import edu.ntnu.idi.bidata.idatg2003mappe.entity.Player;
import edu.ntnu.idi.bidata.idatg2003mappe.map.BoardLinear;
import edu.ntnu.idi.bidata.idatg2003mappe.map.Tile;
import edu.ntnu.idi.bidata.idatg2003mappe.movement.LadderAction;

import java.util.ArrayList;
import java.util.List;

/**
 * Class to represent a game of Ladder.
 * The game consists of a board, a number of players, a dice and a number of tiles.
 * The game is played by the players taking turns to roll the dice and move their markers on the board.
 * The game is won by the first player to reach the last tile on the board.
 *
 * @author Simen Gudbrandsen and Frikk Breadsroed
 * @version 0.3
 * @since 14.02.2025
 */

public class LadderGame {

  private final BoardLinear board;
  private final List<Player> players;
  private final Die die;
  private final int numberOfTiles;

  public LadderGame(int numberOfPlayers) {
    System.out.println("Starting Ladder Game with " + numberOfPlayers + " players.");

    this.numberOfTiles = 100;
    this.board = createBoard(numberOfTiles);
    this.players = createPlayers(numberOfPlayers);
    this.die = new Die();

    //playGame();
  }

  /**
   * Method to create a board for the game.
   * The board consists of a number of tiles, each with a number.
   * The tiles are connected in a linear fashion.
   * The board also contains ladders that connect two tiles.
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

    //Connecting the tiles in a linear fashion.

    for (int i = 0; i < numberOfTiles - 1; i++) {
      tiles[i].setNextTile(tiles[i + 1]);
    }

    //Hardcoding ladders to the board.

    if (numberOfTiles >= 100) {

      // Ladders
      tiles[2].setDestinationTile(tiles[38]);
      tiles[5].setDestinationTile(tiles[15]);
      tiles[10].setDestinationTile(tiles[32]);
      tiles[22].setDestinationTile(tiles[43]);
      tiles[29].setDestinationTile(tiles[85]);
      tiles[52].setDestinationTile(tiles[67]);
      tiles[73].setDestinationTile(tiles[92]);
      tiles[80].setDestinationTile(tiles[99]);

      // Snakes
      tiles[18].setDestinationTile(tiles[8]);
      tiles[62].setDestinationTile(tiles[12]);
      tiles[55].setDestinationTile(tiles[35]);
      tiles[65].setDestinationTile(tiles[61]);
      tiles[88].setDestinationTile(tiles[37]);
      tiles[94].setDestinationTile(tiles[74]);
      tiles[98].setDestinationTile(tiles[80]);
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
      Player player = new Player("Player " + i, startTile, i);
      players.add(player);
    }
    return players;
  }


  /**
   * Method to play the game.
   * The game is played by the players taking turns
   * to roll the dice and move their markers on the board.
   * If a player lands on a tile with a ladder, the player
   * is moved to the destination tile of the ladder.
   * The game is won by the first player to
   * reach the last tile on the board.
   */
  void playGame() {
    boolean hasWon = false;
    int indexCurrentPlayer = 0;
    int roll = 0;

    while (!hasWon) {
      Player currentPlayer = players.get(indexCurrentPlayer);
      System.out.println("Player " + (indexCurrentPlayer + 1) + " turn.");

      roll = die.rollDie();
      System.out.println("Die : die rolled: " + roll);

      System.out.println("Tile Current : Tile before moving " + currentPlayer.getCurrentTile().getTileId());
      currentPlayer.movePlayer(roll);
      System.out.println("Tile Moved :  Tile after moving " + currentPlayer.getCurrentTile().getTileId());

      // Check if the tile has a ladder destination
      Tile currentTile = currentPlayer.getCurrentTile();
      if (currentTile.getDestinationTile() != null) {

        // Create and perform the ladder action
        LadderAction ladderAction = new LadderAction(currentTile);
        ladderAction.performAction(currentPlayer);
        System.out.println("After ladder action: moved to tile " + currentPlayer.getCurrentTile().getTileId());
        System.out.println(currentPlayer.getName() + " is now at tile " + currentPlayer.getCurrentTile().getTileId());
      }

      // Check win condition (reached the last tile)
      if (currentPlayer.getCurrentTile().getTileId() == numberOfTiles) {
        System.out.println(currentPlayer.getName() + " wins the game!");
        hasWon = true;
      }

      // Next player's turn
      indexCurrentPlayer = (indexCurrentPlayer + 1) % players.size();
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

  /**
   * Method to get the board of the game.
   *
   * @return the board
   */

  public BoardLinear getBoard() {
    return board;
  }

  /**
   * Method to get the die of the game.
   *
   * @return the die
   */

  public Die getDie() {
    return die;
  }

  /**
   * Method to get the number of tiles on the board.
   *
   * @return the number of tiles
   */

  public int getNumberOfTiles() {
    return numberOfTiles;
  }
}
