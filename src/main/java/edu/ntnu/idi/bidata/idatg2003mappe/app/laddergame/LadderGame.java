package edu.ntnu.idi.bidata.idatg2003mappe.app.laddergame;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import edu.ntnu.idi.bidata.idatg2003mappe.entity.Die;
import edu.ntnu.idi.bidata.idatg2003mappe.entity.Player;
import edu.ntnu.idi.bidata.idatg2003mappe.map.board.BoardLinear;
import edu.ntnu.idi.bidata.idatg2003mappe.map.Tile;
import edu.ntnu.idi.bidata.idatg2003mappe.movement.LadderAction;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

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

  private static final String PLAYER_DATA_FILE = "src/main/resources/saves/playerData/Players.csv";
  private final BoardLinear board;
  private final List<Player> players;
  private final Die die;
  private final int numberOfTiles;
  private final boolean randomLadders;

  public LadderGame(boolean randomLadders) {
    System.out.println("Starting Ladder Game with players from file.");

    this.randomLadders = randomLadders;
    this.numberOfTiles = 100;
    this.board = createBoard(numberOfTiles);
    this.players = readPlayersFromCSV();
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

    for (int i = 0; i < numberOfTiles - 1; i++) {
      tiles[i].setNextTile(tiles[i + 1]);
    }

    if (randomLadders) {
      generateRandomLadders(tiles);
    } else {
      setClassicLadders(tiles);
    }

    return board;
  }

  /**
   * Method to set the classic ladders and snakes on the board.
   * The ladders and snakes are hardcoded to the board. For a classic game of Ladder,
   */

  private void setClassicLadders(Tile[] tiles) {
    if (numberOfTiles >= 100) {
      //Ladders
      tiles[2].setDestinationTile(tiles[38]);
      tiles[5].setDestinationTile(tiles[15]);
      tiles[10].setDestinationTile(tiles[32]);
      tiles[22].setDestinationTile(tiles[43]);
      tiles[29].setDestinationTile(tiles[85]);
      tiles[52].setDestinationTile(tiles[67]);
      tiles[73].setDestinationTile(tiles[92]);
      tiles[80].setDestinationTile(tiles[99]);

      //Snakes
      tiles[18].setDestinationTile(tiles[8]);
      tiles[62].setDestinationTile(tiles[12]);
      tiles[55].setDestinationTile(tiles[35]);
      tiles[65].setDestinationTile(tiles[61]);
      tiles[88].setDestinationTile(tiles[37]);
      tiles[94].setDestinationTile(tiles[74]);
      tiles[98].setDestinationTile(tiles[80]);
    }
  }

  /**
   * Method to generate random ladders and snakes on the board.
   * The ladders and snakes are randomly generated on the board.
   * The number of ladders and snakes are hardcoded for now.
   */

  private void generateRandomLadders(Tile[] tiles) {
    Random random = new Random();
    int numLadders = 8; // Number of ladders to generate
    int numSnakes = 8;  // Number of snakes to generate

    // Generate ladders
    for (int i = 0; i < numLadders; i++) {
      int start = random.nextInt(99) + 1; // Start between 1 and 99
      int end = start + random.nextInt(15) + 5; // End at least 5 tiles ahead but within 100

      if (end < 100 && tiles[start].getDestinationTile() == null) {
        tiles[start].setDestinationTile(tiles[end]);
      } else {
        i--; // Retry this ladder if it was invalid
      }
    }

    // Generate snakes
    for (int i = 0; i < numSnakes; i++) {
      int start = random.nextInt(89) + 10; // Start between 10 and 99
      int end = start - random.nextInt(Math.min(start - 1, 15)) - 5; // Ensure it doesn't go below 1

      if (end > 1 && tiles[start].getDestinationTile() == null) {
        tiles[start].setDestinationTile(tiles[end]);
      } else {
        i--; // Retry this snake if it was invalid
      }
    }
  }

  /**
   * Method to create a list of players for the game from the CSV file.
   *
   * @return the list of players
   */
  protected List<Player> readPlayersFromCSV() {
    List<Player> players = new ArrayList<>();
    Tile startTile = board.getTiles().get(0);

    // Try to read from CSV file
    File file = new File(PLAYER_DATA_FILE);
    if (file.exists() && file.isFile()) {
      try (CSVReader reader = new CSVReader(new FileReader(file))) {
        String[] record;
        reader.readNext();

        while ((record = reader.readNext()) != null) {
          // Expected format: Player Name, Player ID, Color, Position
          if (record.length >= 2) {
            String playerName = record[0];
            int playerID = Integer.parseInt(record[1]);
            String playerColor = record[2];
            int position = Integer.parseInt(record[3]);
            Tile playerTile = board.getTiles().get(position);

            Player player = new Player(playerName, playerID, playerColor, playerTile);
            players.add(player);
            System.out.println("Player " + playerName + " added to the game.");
            System.out.println("Player ID: " + playerID);
            System.out.println("Player Color: " + playerColor);
            System.out.println("Player Position: " + position);
            System.out.println("----------------------");
          }
        }
      } catch (IOException | CsvValidationException e) {
        System.out.println("Error reading player data: " + e.getMessage());
      }
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