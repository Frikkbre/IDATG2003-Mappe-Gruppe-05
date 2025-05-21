package edu.ntnu.idi.bidata.idatg2003mappe.app.missingdiamond.model;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import edu.ntnu.idi.bidata.idatg2003mappe.app.missingdiamond.gamelogic.TokenSystem;
import edu.ntnu.idi.bidata.idatg2003mappe.banker.Banker;
import edu.ntnu.idi.bidata.idatg2003mappe.entity.die.Die;
import edu.ntnu.idi.bidata.idatg2003mappe.entity.player.Player;
import edu.ntnu.idi.bidata.idatg2003mappe.filehandling.exceptionhandling.FileHandlingException;
import edu.ntnu.idi.bidata.idatg2003mappe.filehandling.map.MapConfig;
import edu.ntnu.idi.bidata.idatg2003mappe.filehandling.map.MapConfigFileHandler;
import edu.ntnu.idi.bidata.idatg2003mappe.map.Tile;
import edu.ntnu.idi.bidata.idatg2003mappe.map.board.BoardBranching;
import edu.ntnu.idi.bidata.idatg2003mappe.map.board.BoardLinear;
import edu.ntnu.idi.bidata.idatg2003mappe.markers.Marker;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

/**
 * Represents the Missing Diamond game.
 * This class manages the game state and rules for the Missing Diamond game.
 *
 * @author Simen Gudbrandsen and Frikk Breadsroed
 * @version 0.1.0
 * @since 23.05.2025
 */
public class MissingDiamond {
  private static final String PLAYER_DATA_FILE = "src/main/resources/saves/playerData/Players.csv";

  // Travel cost constants
  private static final int PLANE_COST = 300;
  private static final int SHIP_COST = 100;
  private static final int TOKEN_PURCHASE_COST = 100;

  // Starting money
  private static final int STARTING_MONEY = 300;

  // Game components
  private final BoardBranching board;
  private final BoardLinear boardLinear = new BoardLinear();
  private List<Player> players = new ArrayList<>();
  private final Die die;
  private final TokenSystem tokenSystem;
  private final Banker banker;

  // Game state
  private boolean gameFinished;
  private Player currentPlayer;
  private int currentPlayerIndex;
  private int currentRoll; // Store the last roll value
  private Player winner;

  // Travel routes
  private final Map<Integer, List<Integer>> planeRoutes = new HashMap<>();
  private final Map<Integer, List<Integer>> shipRoutes = new HashMap<>();

  // City tiles
  private final List<Tile> cityTiles = new ArrayList<>();
  private final List<Tile> startingTiles = new ArrayList<>();

  /**
   * Constructor for MissingDiamond with specified number of players.
   *
   * @param numberOfPlayers The number of players in the game
   */
  public MissingDiamond(int numberOfPlayers) {
    this(numberOfPlayers, "src/main/resources/maps/missing_diamond_default.json");
  }

  /**
   * Constructor for MissingDiamond with specified number of players and map file.
   *
   * @param numberOfPlayers The number of players in the game
   * @param mapFilePath The path to the map file
   */
  public MissingDiamond(int numberOfPlayers, String mapFilePath) {
    System.out.println("Starting Missing Diamond Game with " + numberOfPlayers + " players.");

    // Create game components
    this.banker = new Banker();
    this.tokenSystem = new TokenSystem();
    this.die = new Die();

    // Initialize the board
    BoardBranching boardInstance;
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
        boardInstance = createDefaultBoard();
      }

    } catch (FileHandlingException e) {
      System.err.println("Error loading map configuration: " + e.getMessage());
      // Fall back to default board creation
      boardInstance = createDefaultBoard();
    }

    this.board = boardInstance;

    // Initialize players
    this.players = createPlayers(numberOfPlayers, boardInstance);

    // Initialize game state
    this.gameFinished = false;
    this.currentPlayerIndex = 0;
    this.currentPlayer = players.get(currentPlayerIndex);
    this.currentRoll = 0;

    // Set up travel routes
    setupTravelRoutes();

    // Identify city tiles and starting tiles
    identifyCityTiles();
    identifyStartingTiles();

    // Initialize tokens on city tiles
    tokenSystem.setStartingTiles(startingTiles);
    tokenSystem.initializeTokens(cityTiles);

    // Register players with banker and give starting money
    for (Player player : players) {
      banker.registerPlayer(player);
      banker.deposit(player, STARTING_MONEY);
    }
  }

  /**
   * Constructor for MissingDiamond that reads players from CSV file.
   */
  public MissingDiamond() {
    System.out.println("Starting Missing Diamond Game with players from file.");

    // Create game components
    this.banker = new Banker();
    this.tokenSystem = new TokenSystem();
    this.die = new Die();

    // Initialize the board
    BoardBranching boardInstance;
    try {
      MapConfigFileHandler mapFileHandler = new MapConfigFileHandler();
      MapConfig mapConfig;

      if (mapFileHandler.defaultMapExists()) {
        mapConfig = mapFileHandler.loadFromDefaultLocation();
        boardInstance = createBoardFromConfig(mapConfig);
      } else {
        // Fall back to default board if no JSON file exists
        boardInstance = createDefaultBoard();
      }
    } catch (FileHandlingException e) {
      System.err.println("Error loading map configuration: " + e.getMessage());
      boardInstance = createDefaultBoard();
    }

    this.board = boardInstance;

    // Read players from CSV
    this.players = readPlayersFromCSV();

    // Initialize game state
    this.gameFinished = false;
    this.currentPlayerIndex = 0;
    this.currentPlayer = players.isEmpty() ? null : players.get(currentPlayerIndex);
    this.currentRoll = 0;

    // Set up travel routes
    setupTravelRoutes();

    // Identify city tiles and starting tiles
    identifyCityTiles();
    identifyStartingTiles();

    // Initialize tokens on city tiles
    tokenSystem.setStartingTiles(startingTiles);
    tokenSystem.initializeTokens(cityTiles);

    // Register players with banker and give starting money
    for (Player player : players) {
      banker.registerPlayer(player);
      banker.deposit(player, STARTING_MONEY);
    }
  }

  /**
   * Creates a board from a map configuration.
   *
   * @param mapConfig The map configuration
   * @return A board created from the map configuration
   */
  private BoardBranching createBoardFromConfig(MapConfig mapConfig) {
    BoardBranching board = new BoardBranching();
    board.setBoardName(mapConfig.getName());

    // Create all tiles
    for (MapConfig.Location location : mapConfig.getLocations()) {
      Tile tile = new Tile(location.getId());
      // Store the location name as a property of the tile
      // (This would require adding a name field to the Tile class)
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

  /**
   * Creates a default board with a simple structure.
   *
   * @return A default board
   */
  private BoardBranching createDefaultBoard() {
    BoardBranching board = new BoardBranching();
    board.setBoardName("Default Missing Diamond Map");

    // Create basic city tiles
    for (int i = 1; i <= 20; i++) {
      Tile tile = new Tile(i);
      board.addTileToBoard(tile);
    }

    // Connect the tiles in a simple network
    // Starting tiles (Cairo and Tangiers)
    Tile cairo = board.getTileById(1);
    Tile tangiers = board.getTileById(2);

    // Create connections between cities
    board.connectTiles(cairo, board.getTileById(3));
    board.connectTiles(cairo, board.getTileById(4));
    board.connectTiles(tangiers, board.getTileById(5));
    board.connectTiles(tangiers, board.getTileById(6));

    // Add more connections to create a network
    for (int i = 3; i <= 18; i++) {
      Tile current = board.getTileById(i);
      Tile next = board.getTileById(i + 1);
      if (current != null && next != null) {
        board.connectTiles(current, next);
      }

      // Add some cross-connections
      if (i % 3 == 0 && i + 4 <= 20) {
        Tile crossTile = board.getTileById(i + 4);
        if (crossTile != null) {
          board.connectTiles(current, crossTile);
        }
      }
    }

    return board;
  }

  /**
   * Creates players for the game.
   *
   * @param numberOfPlayers The number of players to create
   * @param board The game board
   * @return A list of players
   */
  private List<Player> createPlayers(int numberOfPlayers, BoardBranching board) {
    List<Player> players = new ArrayList<>();
    Tile startTile = !startingTiles.isEmpty() ? startingTiles.get(0) : board.getStartTile();

    // Define colors for players
    String[] playerColors = {"Orange", "Blue", "Green", "Yellow", "Purple", "Red"};

    for (int i = 0; i < numberOfPlayers; i++) {
      // Get color with wraparound if more players than colors
      String color = playerColors[i % playerColors.length];

      // Create player with correct parameter order: name, id, color, tile
      Player player = new Player("Player " + (i + 1), i, color, startTile);
      players.add(player);

      // Register player with banker and give starting money
      banker.registerPlayer(player);
      banker.deposit(player, STARTING_MONEY);
    }

    return players;
  }

  /**
   * Reads players from a CSV file.
   *
   * @return A list of players read from the file
   */
  protected List<Player> readPlayersFromCSV() {
    List<Player> localPlayers = new ArrayList<>();

    // Try to read from CSV file
    File file = new File(PLAYER_DATA_FILE);
    if (file.exists() && file.isFile()) {
      try (CSVReader reader = new CSVReader(new FileReader(file))) {
        String[] record;
        reader.readNext(); // Skip header

        while ((record = reader.readNext()) != null) {
          // Expected format: Player Name, Player ID, Color, Position
          if (record.length >= 4) {
            String playerName = record[0];
            int playerID = Integer.parseInt(record[1]);
            String playerColor = record[2];
            int position = Integer.parseInt(record[3]);

            Tile playerTile = board.getTileById(position);
            if (playerTile == null) {
              // Fallback to start tile if position is invalid
              playerTile = !startingTiles.isEmpty() ? startingTiles.get(0) : board.getStartTile();
            }

            Player player = new Player(playerName, playerID, playerColor, playerTile);
            localPlayers.add(player);
            System.out.println("Player " + playerName + " added to the game.");
          }
        }
      } catch (IOException | CsvValidationException e) {
        System.out.println("Error reading player data: " + e.getMessage());
      }
    }

    // If no players were read from file, create a default player
    if (localPlayers.isEmpty()) {
      Tile startTile = !startingTiles.isEmpty() ? startingTiles.get(0) : board.getStartTile();
      localPlayers.add(new Player("Player 1", 0, "Blue", startTile));
    }

    return localPlayers;
  }

  /**
   * Sets up travel routes (plane and ship) between cities.
   */
  private void setupTravelRoutes() {
    // This would be populated from map data in a real implementation
    // For now, creating some example routes

    // Example plane routes (city ID -> list of destination city IDs)
    planeRoutes.put(1, Arrays.asList(10, 15)); // Cairo -> cities 10 and 15
    planeRoutes.put(2, Arrays.asList(12, 18)); // Tangiers -> cities 12 and 18

    // Example ship routes
    shipRoutes.put(5, Arrays.asList(9, 14));
    shipRoutes.put(8, Arrays.asList(13, 17));
  }

  /**
   * Identifies all city tiles on the board.
   */
  private void identifyCityTiles() {
    // In a real implementation, this would be based on map data
    // For now, assuming tiles with IDs 1-20 are cities
    for (int i = 1; i <= 20; i++) {
      Tile tile = board.getTileById(i);
      if (tile != null) {
        cityTiles.add(tile);
      }
    }
  }

  /**
   * Identifies starting tiles (Cairo and Tangiers).
   */
  private void identifyStartingTiles() {
    // In a real implementation, this would be based on map data
    // For now, assuming tiles with IDs 1 and 2 are Cairo and Tangiers
    Tile cairo = board.getTileById(1);
    Tile tangiers = board.getTileById(2);

    if (cairo != null) {
      startingTiles.add(cairo);
    }

    if (tangiers != null) {
      startingTiles.add(tangiers);
    }
  }

  /**
   * Rolls the die and returns a message about the result.
   *
   * @return A message describing the die roll
   */
  public String playTurn() {
    // Roll the die
    this.currentRoll = die.rollDie();
    return currentPlayer.getName() + " rolled a " + currentRoll + ".";
  }

  /**
   * Gets all tiles that are exactly N steps away from a starting tile.
   *
   * @param startTile The starting tile
   * @param steps The number of steps to move
   * @return Set of tiles that are exactly N steps away
   */
  public Set<Tile> getTilesExactlyNStepsAway(Tile startTile, int steps) {
    Set<Tile> result = new HashSet<>();

    // No valid moves if steps is invalid
    if (steps <= 0) {
      return result;
    }

    // Use a helper method to do a depth-first search of exactly N steps
    findExactPathsOfLength(startTile, null, steps, result);

    return result;
  }

  /**
   * Helper method for finding all tiles exactly N steps away.
   *
   * @param currentTile The current tile in the search
   * @param previousTile The previous tile in the search (to avoid backtracking)
   * @param remainingSteps The remaining number of steps to take
   * @param result The set of result tiles
   */
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
   * @return Set of tiles that the player can move to
   */
  public Set<Tile> getPossibleMovesForCurrentRoll() {
    if (currentRoll < 1) {
      return new HashSet<>();
    }

    return getTilesExactlyNStepsAway(currentPlayer.getCurrentTile(), currentRoll);
  }

  /**
   * Moves the current player to the selected tile.
   *
   * @param destinationTile The tile to move to
   * @return A message describing the move result
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
    Tile oldTile = currentPlayer.getCurrentTile();
    result += currentPlayer.getName() + " moved to tile " + destinationTile.getTileId() + ". ";
    currentPlayer.placePlayer(destinationTile);

    // Reset current roll
    currentRoll = 0;

    // Check victory condition
    if (checkWinCondition()) {
      result += currentPlayer.getName() + " has won the game!";
      gameFinished = true;
      winner = currentPlayer;
      return result;
    }

    return result;
  }

  /**
   * Buys the token at the current player's tile.
   *
   * @return A message describing the result of the purchase
   */
  public String buyToken() {
    Tile currentTile = currentPlayer.getCurrentTile();
    Marker token = tokenSystem.getTokenAtTile(currentTile);

    if (token == null) {
      return "No token at this location.";
    }

    if (banker.withdraw(currentPlayer, TOKEN_PURCHASE_COST)) {
      String tokenType = token.getType();

      boolean success = tokenSystem.buyToken(currentPlayer, currentTile, banker);
      if (success) {
        return "You bought the token for £" + TOKEN_PURCHASE_COST + ". It was a " + tokenType + "!";
      } else {
        // Refund the money since token buy failed
        banker.deposit(currentPlayer, TOKEN_PURCHASE_COST);
        return "Error processing token purchase.";
      }
    } else {
      return "You don't have enough money to buy the token.";
    }
  }

  /**
   * Attempts to win the token at the current player's tile with a die roll.
   *
   * @return A message describing the result
   */
  public String tryWinToken() {
    Tile currentTile = currentPlayer.getCurrentTile();
    Marker token = tokenSystem.getTokenAtTile(currentTile);

    if (token == null) {
      return "No token at this location.";
    }

    die.rollToTurnMarker();
    int roll = die.getDieValue();
    boolean success = tokenSystem.tryWinToken(currentPlayer, currentTile, roll, banker);

    if (success) {
      String tokenType = token.getType();
      return "You rolled a " + roll + " and won the token! It was a " + tokenType + "!";
    } else {
      return "You rolled a " + roll + " and failed to win the token.";
    }
  }

  /**
   * Travels by plane to a destination tile.
   *
   * @param destinationTile The destination tile
   * @return A message describing the result
   */
  public String travelByPlane(Tile destinationTile) {
    Tile currentTile = currentPlayer.getCurrentTile();

    // Check if there's a plane route from current tile to destination
    List<Integer> destinations = planeRoutes.getOrDefault(currentTile.getTileId(), Collections.emptyList());
    if (!destinations.contains(destinationTile.getTileId())) {
      return "There is no plane route to that destination.";
    }

    // Check if player can afford the ticket
    if (banker.withdraw(currentPlayer, PLANE_COST)) {
      currentPlayer.placePlayer(destinationTile);
      return "You flew to tile " + destinationTile.getTileId() + " for £" + PLANE_COST + ".";
    } else {
      return "You don't have enough money for a plane ticket.";
    }
  }

  /**
   * Travels by ship from the current location.
   *
   * @return A message describing the result
   */
  public String travelByShip() {
    Tile currentTile = currentPlayer.getCurrentTile();

    // Check if there are ship routes from the current tile
    List<Integer> destinations = shipRoutes.getOrDefault(currentTile.getTileId(), Collections.emptyList());
    if (destinations.isEmpty()) {
      return "There are no ship routes from this location.";
    }

    // Check if player can afford the ticket
    if (banker.withdraw(currentPlayer, SHIP_COST)) {
      int roll = die.rollDie();

      // Find all tiles that can be reached with the roll
      Set<Tile> possibleDestinations = new HashSet<>();
      for (Integer destId : destinations) {
        Tile destTile = board.getTileById(destId);
        if (destTile != null) {
          possibleDestinations.add(destTile);
        }
      }

      return "You rolled a " + roll + " for ship travel. You can move to any of the sea routes for £" + SHIP_COST + ".";
    } else {
      return "You don't have enough money for a ship ticket.";
    }
  }

  /**
   * Checks if there is a token at a specific tile.
   *
   * @param tile The tile to check
   * @return True if there is a token at the tile, false otherwise
   */
  public boolean hasTokenAtTile(Tile tile) {
    return tokenSystem.getTokenAtTile(tile) != null;
  }

  /**
   * Gets the token at a specific tile.
   *
   * @param tile The tile to check
   * @return The marker at the tile, or null if no marker exists
   */
  public Marker getTokenAtTile(Tile tile) {
    return tokenSystem.getTokenAtTile(tile);
  }

  /**
   * Checks if the current player has met the win condition.
   *
   * @return True if the win condition is met, false otherwise
   */
  public boolean checkWinCondition() {
    return tokenSystem.checkVictoryCondition(currentPlayer, currentPlayer.getCurrentTile());
  }

  /**
   * Moves to the next player's turn.
   */
  public void nextPlayer() {
    currentPlayerIndex = (currentPlayerIndex + 1) % players.size();
    currentPlayer = players.get(currentPlayerIndex);

    // Skip players that need to skip their turn
    if (currentPlayer.isSkipTurn()) {
      currentPlayer.setSkipTurn(false);
      nextPlayer();
    }
  }

  /**
   * Skips the current player's turn.
   */
  public void skipTurn() {
    // Reset current roll
    currentRoll = 0;

    // Move to next player
    nextPlayer();
  }

  // Getters and setters

  /**
   * Gets the list of players.
   *
   * @return The list of players
   */
  public List<Player> getPlayers() {
    return players;
  }

  /**
   * Gets the game board.
   *
   * @return The game board
   */
  public BoardBranching getBoard() {
    return board;
  }

  /**
   * Gets the die.
   *
   * @return The die
   */
  public Die getDie() {
    return die;
  }

  /**
   * Gets the current roll value.
   *
   * @return The current roll value
   */
  public int getCurrentRoll() {
    return currentRoll;
  }

  /**
   * Checks if the game is finished.
   *
   * @return True if the game is finished, false otherwise
   */
  public boolean isGameFinished() {
    return gameFinished;
  }

  /**
   * Sets whether the game is finished.
   *
   * @param gameFinished True if the game is finished, false otherwise
   */
  public void setGameFinished(boolean gameFinished) {
    this.gameFinished = gameFinished;
  }

  /**
   * Gets the current player.
   *
   * @return The current player
   */
  public Player getCurrentPlayer() {
    return currentPlayer;
  }

  /**
   * Sets the current player.
   *
   * @param playerIndex The new current player index
   */
  public void setCurrentPlayerIndex(int playerIndex) {
    if (playerIndex < 0 || playerIndex >= players.size()) {
      throw new IllegalArgumentException("Invalid player index.");
    }
    this.currentPlayerIndex = playerIndex;
    this.currentPlayer = players.get(playerIndex);
  }

  /**
   * Gets the index of the current player.
   *
   * @return The index of the current player
   */
  public int getCurrentPlayerIndex() {
    return currentPlayerIndex;
  }

  /**
   * Gets the banker.
   *
   * @return The banker
   */
  public Banker getBanker() {
    return banker;
  }

  /**
   * Gets the token system.
   *
   * @return The token system
   */
  public TokenSystem getTokenSystem() {
    return tokenSystem;
  }

  /**
   * Gets the winning player.
   *
   * @return The winning player, or null if no winner yet
   */
  public Player getWinner() {
    return winner;
  }

  /**
   * Gets a list of destinations reachable by plane from the current location.
   *
   * @return A list of destination tile IDs
   */
  public List<Integer> getPlaneDestinations() {
    Tile currentTile = currentPlayer.getCurrentTile();
    return planeRoutes.getOrDefault(currentTile.getTileId(), Collections.emptyList());
  }

  /**
   * Gets a list of destinations reachable by ship from the current location.
   *
   * @return A list of destination tile IDs
   */
  public List<Integer> getShipDestinations() {
    Tile currentTile = currentPlayer.getCurrentTile();
    return shipRoutes.getOrDefault(currentTile.getTileId(), Collections.emptyList());
  }

  /**
   * Gets the cost of a plane ticket.
   *
   * @return The cost of a plane ticket
   */
  public int getPlaneCost() {
    return PLANE_COST;
  }

  /**
   * Gets the cost of a ship ticket.
   *
   * @return The cost of a ship ticket
   */
  public int getShipCost() {
    return SHIP_COST;
  }

  /**
   * Gets the cost of purchasing a token.
   *
   * @return The cost of purchasing a token
   */
  public int getTokenPurchaseCost() {
    return TOKEN_PURCHASE_COST;
  }

  /**
   * Gets all city tiles on the board.
   *
   * @return A list of all city tiles
   */
  public List<Tile> getCityTiles() {
    return new ArrayList<>(cityTiles);
  }

  /**
   * Gets all starting tiles (Cairo and Tangiers).
   *
   * @return A list of starting tiles
   */
  public List<Tile> getStartingTiles() {
    return new ArrayList<>(startingTiles);
  }
}