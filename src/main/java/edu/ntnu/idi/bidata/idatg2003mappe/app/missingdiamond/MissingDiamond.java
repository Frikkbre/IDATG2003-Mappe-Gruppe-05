package edu.ntnu.idi.bidata.idatg2003mappe.app.missingdiamond;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import edu.ntnu.idi.bidata.idatg2003mappe.entity.Die;
import edu.ntnu.idi.bidata.idatg2003mappe.entity.Player;
import edu.ntnu.idi.bidata.idatg2003mappe.filehandling.exceptionhandling.FileHandlingException;
import edu.ntnu.idi.bidata.idatg2003mappe.filehandling.map.MapConfig;
import edu.ntnu.idi.bidata.idatg2003mappe.filehandling.map.MapConfigFileHandler;
import edu.ntnu.idi.bidata.idatg2003mappe.filehandling.playerInfo.PlayerFileHandler;
import edu.ntnu.idi.bidata.idatg2003mappe.map.Tile;
import edu.ntnu.idi.bidata.idatg2003mappe.map.board.BoardBranching;
import edu.ntnu.idi.bidata.idatg2003mappe.map.board.BoardLinear;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

/**
 * Represents the Missing Diamond game.
 *
 * @author Simen Gudbrandsen and Frikk Breadsroed
 * @version 0.0.2
 * @since 16.02.2025
 */
public class MissingDiamond {
  private static final String PLAYER_DATA_FILE = "src/main/resources/saves/playerData/Players.csv";
  private final BoardBranching board;
  private final BoardLinear boardLinear = new BoardLinear();
  private List<Player> players = new ArrayList<>();
  private final Die die;
  private boolean gameFinished;
  private Player currentPlayer;
  private int currentPlayerIndex;
  private Tile diamondLocation;
  private int currentRoll; // Store the last roll value

public MissingDiamond(int numberOfPlayers) {
    this(numberOfPlayers, "src/main/resources/maps/missing_diamond_default.json");
}

public MissingDiamond(int numberOfPlayers, String mapFilePath) {
    System.out.println("Starting Missing Diamond Game with " + numberOfPlayers + " players.");

    BoardBranching boardInstance;
    List<Player> playersInstance;
    Die dieInstance = new Die();
    boolean gameFinishedInstance = false;
    int currentPlayerIndexInstance = 0;

    try {
        // Load map configuration from file
        MapConfigFileHandler mapFileHandler = new MapConfigFileHandler();
        MapConfig mapConfig = mapFileHandler.read(mapFilePath);

        // Only try to create board from config if mapConfig is not null
        if (mapConfig != null) {
            // Create board from configuration
            boardInstance = createBoardFromConfig(mapConfig);
        } else {
            System.err.println("Error: Map configuration is null");
            boardInstance = createEmptyDefaultBoard();
        }

        // Initialize players using the created board
        playersInstance = createPlayers(numberOfPlayers, boardInstance);

    } catch (FileHandlingException e) {
        System.err.println("Error loading map configuration: " + e.getMessage());
        // Fall back to default board creation
        boardInstance = createEmptyDefaultBoard();
        playersInstance = createPlayers(numberOfPlayers, boardInstance);
    }

    // Assign to final fields
    this.board = boardInstance;
    this.players = playersInstance;
    this.die = dieInstance;
    this.gameFinished = gameFinishedInstance;
    this.currentPlayerIndex = currentPlayerIndexInstance;
    this.currentPlayer = players.get(currentPlayerIndex);
    this.currentRoll = 0;
}

/**
 * Constructor for the MissingDiamond class.
 * Reads players from CSV file.
 */
public MissingDiamond() {
    System.out.println("Starting Missing Diamond Game with players from file.");
    this.board = createBoard();
    readPlayersFromCSV();
    this.die = new Die();
    this.gameFinished = false;
    this.currentPlayerIndex = 0;
    this.currentPlayer = players.isEmpty() ? null : players.get(currentPlayerIndex);
    this.currentRoll = 0;
}

private BoardBranching createBoardFromConfig(MapConfig mapConfig) {
    BoardBranching board = new BoardBranching();
    board.setBoardName(mapConfig.getName());

    // Create all tiles
    for (MapConfig.Location location : mapConfig.getLocations()) {
        Tile tile = new Tile(location.getId());
        board.addTileToBoard(tile);
    }

    // Add all connections
    for (MapConfig.Connection connection : mapConfig.getConnections()) {
        Tile fromTile = board.getTileById(connection.getFromId());
        Tile toTile = board.getTileById(connection.getToId());

        if (fromTile != null && toTile != null) {
            board.connectTiles(fromTile, toTile);
        }
    }

    return board;
}

private BoardBranching createEmptyDefaultBoard() {
    BoardBranching board = new BoardBranching();

    // Create just a few connected tiles as absolute fallback
    for (int i = 1; i <= 5; i++) {
        Tile tile = new Tile(i);
        board.addTileToBoard(tile);
    }

    // Connect the tiles in a simple path
    for (int i = 1; i <= 4; i++) {
        board.connectTiles(board.getTileById(i), board.getTileById(i+1));
    }

    return board;
}

/**
 * Creates the game board.
 * @return Board
 */
private BoardBranching createBoard() {
    BoardBranching board = new BoardBranching();

    // Create all locations
    // [Original board creation code here]
    
    return board;
}

  private BoardBranching createEmptyDefaultBoard() {

  /**
   * Creates the game board.
   * @return Board
   */
  private BoardBranching createBoard() {

    BoardBranching board = new BoardBranching();

    // Create just a few connected tiles as absolute fallback
    for (int i = 1; i <= 5; i++) {
      Tile tile = new Tile(i);
      board.addTileToBoard(tile);
    }

    // Connect the tiles in a simple path
    for (int i = 1; i <= 4; i++) {
      board.connectTiles(board.getTileById(i), board.getTileById(i+1));
    }

    return board;
  }

  private BoardBranching createBoard() {
    BoardBranching board = new BoardBranching();

    // Create all locations
    for (int i = 1; i <= 32; i++) {
      Tile tile = new Tile(i);
      board.addTileToBoard(tile);
    }

    // Add all connections matching the GUI's CONNECTIONS array
    // North Africa
    connectTiles(board, 1, 2, 3);
    connectTiles(board, 2, 19);
    connectTiles(board, 3, 4, 6);
    connectTiles(board, 4, 5);
    connectTiles(board, 5, 13);
    connectTiles(board, 6, 7);

    // West Africa
    connectTiles(board, 7, 9, 10);
    connectTiles(board, 8, 17, 20);
    connectTiles(board, 9, 18, 19);

    // Central Africa
    connectTiles(board, 10, 11, 12);
    connectTiles(board, 11, 15, 16);
    connectTiles(board, 12, 13);
    connectTiles(board, 13, 14);
    connectTiles(board, 14, 15);

    // East Africa
    connectTiles(board, 15, 16, 24);
    connectTiles(board, 16, 21, 24);

    // West Coast
    connectTiles(board, 17, 18);
    connectTiles(board, 18, 19);
    connectTiles(board, 19, 21);
    connectTiles(board, 20, 29);

    // Central Paths
    connectTiles(board, 21, 22, 23);
    connectTiles(board, 22, 29);
    connectTiles(board, 23, 24, 25);
    connectTiles(board, 25, 26, 28);
    connectTiles(board, 26, 27);
    connectTiles(board, 27, 31);

    // South Africa
    connectTiles(board, 28, 29, 30);
    connectTiles(board, 29, 32);
    connectTiles(board, 30, 31);

    // Randomly place the diamond at one of the locations
    int diamondLocation = new Random().nextInt(32) + 1;
    this.diamondLocation = board.getTileById(diamondLocation);

    return board;
  }


  // Helper method to connect one tile to multiple others
private void connectTiles(BoardBranching board, int fromId, int... toIds) {
    Tile fromTile = board.getTileById(fromId);
    for (int toId : toIds) {
        Tile toTile = board.getTileById(toId);
        board.connectTiles(fromTile, toTile);
    }
}

private List<Player> createPlayers(int numberOfPlayers, BoardBranching board) {
    List<Player> players = new ArrayList<>();
    Tile startTile = board.getStartTile();

    for (int i = 1; i <= numberOfPlayers; i++) {
        Player player = new Player("Player " + i, startTile, i);
        players.add(player);
    }
    
    return players;
}
    
  protected List<Player> readPlayersFromCSV() {
    List<Player> localPlayers = new ArrayList<>();

    // Try to read from CSV file
    File file = new File(PLAYER_DATA_FILE);
    if (file.exists() && file.isFile()) {
      try (CSVReader reader = new CSVReader(new FileReader(file))) {
        String[] record;
        reader.readNext();

        while ((record = reader.readNext()) != null) {
          // Expected format: Player Name, Player ID, Color, Position
          if (record.length > 0) {
            String playerName = record[0];
            int playerID = Integer.parseInt(record[1]);
            String playerColor = record[2];
            int position = Integer.parseInt(record[3]);
            Tile playerTile = board.getTileById(position);

            Player player = new Player(playerName, playerID, playerColor, playerTile);
            players.add(player);
            System.out.println("Player " + playerName + " added to the game.");
            System.out.println("Player ID: " + playerID);
            System.out.println("Player Color: " + playerColor);
            System.out.println("Player Position: " + position);
            System.out.println("Player list" + players);
            System.out.println("----------------------");
          }
        }
      } catch (IOException | CsvValidationException e) {
        System.out.println("Error reading player data: " + e.getMessage());
      }
    }
    return players;
  }

  public String playTurn() {
    // Roll the die
    this.currentRoll = die.rollDie();
    return currentPlayer.getName() + " rolled a " + currentRoll + ".";
  }

  /**
   * Gets all tiles that are exactly N steps away from a starting tile.
   * Uses a simple recursive approach to find all possible destinations.
   *
   * @param startTile The starting tile.
   * @param steps The number of steps to move.
   * @return Set of tiles that are exactly N steps away.
   */
  public Set<Tile> getTilesExactlyNStepsAway(Tile startTile, int steps) {
    Set<Tile> result = new HashSet<>();

    // No valid moves if steps is invalid
    if (steps <= 0) {
      return result;
    }

    // We'll use a helper method to do a depth-first search of exactly N steps
    findExactPathsOfLength(startTile, null, steps, result);

    return result;
  }

  private void findExactPathsOfLength(Tile currentTile, Tile previousTile, int remainingSteps, Set<Tile> result) {
    // If we've used all our steps, add the current tile to our result
    if (remainingSteps == 0) {
      result.add(currentTile);
      return;
    }

    // Otherwise, continue the search from each neighbor (except the one we just came from)
    for (Tile neighbor : currentTile.getNextTiles()) {
      if (neighbor != previousTile) {  // Prevent immediate backtracking
        findExactPathsOfLength(neighbor, currentTile, remainingSteps - 1, result);
      }
    }
  }

  /**
   * Gets all possible moves for the current player based on the last die roll.
   *
   * @return Set of tiles that the player can move to.
   */
  public Set<Tile> getPossibleMovesForCurrentRoll() {
    if (currentRoll < 1) {
      return new HashSet<>();
    }

    return getTilesExactlyNStepsAway(currentPlayer.getCurrentTile(), currentRoll);
  }

  /**
   * Moves the current player to the selected tile and handles any special actions.
   *
   * @param destinationTile The tile to move to.
   * @return A message describing the move result.
   */
  public String movePlayerToTile(Tile destinationTile) {
    String result = "";

    if (destinationTile == null) {
      return "Invalid destination tile.";
    }

    // Check if the move is valid (destination is exactly N steps away)
    Set<Tile> validMoves = getPossibleMovesForCurrentRoll();
    if (!validMoves.contains(destinationTile)) {
      return "Cannot move to that tile - it's not exactly " + currentRoll + " steps away.";
    }

    // Move the player
    result += currentPlayer.getName() + " moved to tile " + destinationTile.getTileId() + ". ";
    currentPlayer.placePlayer(destinationTile);

    // Reset current roll
    currentRoll = 0;

    // Check if player found the diamond
    if (destinationTile == diamondLocation) {
      result += currentPlayer.getName() + " found the diamond and won the game!";
      gameFinished = true;
      return result;
    }

    // Move to next player if game not finished
    if (!gameFinished) {
      currentPlayerIndex = (currentPlayerIndex + 1) % players.size();
      currentPlayer = players.get(currentPlayerIndex);
    }

    return result;
  }

  public void skipTurn() {
    // Reset current roll
    currentRoll = 0;

    // Move to next player
    currentPlayerIndex = (currentPlayerIndex + 1) % players.size();
    currentPlayer = players.get(currentPlayerIndex);
  }

  /**
   * Gets the list of players.
   *
   * @return The list of players.
   */
  public List<Player> getPlayers() {
    return players;
  }

  /**
   * Gets the game board.
   *
   * @return The game board.
   */
  public BoardBranching getBoard() {
    return board;
  }

  /**
   * Gets the die.
   *
   * @return The die.
   */
  public Die getDie() {
    return die;
  }

  /**
   * Gets the current roll value.
   *
   * @return The current roll value.
   */
  public int getCurrentRoll() {
    return currentRoll;
  }

  /**
   * Checks if the game is finished.
   *
   * @return True if the game is finished, false otherwise.
   */
  public boolean isGameFinished() {
    return gameFinished;
  }

  /**
   * Gets the current player.
   *
   * @return The current player.
   */
  public Player getCurrentPlayer() {
    return currentPlayer;
  }

  /**
   * Gets the index of the current player.
   *
   * @return The index of the current player.
   */
  public int getCurrentPlayerIndex() {
    return currentPlayerIndex;
  }
}