package edu.ntnu.idi.bidata.idatg2003mappe.app.missingdiamond.model;

import edu.ntnu.idi.bidata.idatg2003mappe.app.missingdiamond.gamelogic.TokenSystem;
import edu.ntnu.idi.bidata.idatg2003mappe.banker.Banker;
import edu.ntnu.idi.bidata.idatg2003mappe.entity.die.Die;
import edu.ntnu.idi.bidata.idatg2003mappe.entity.player.Player;
import edu.ntnu.idi.bidata.idatg2003mappe.filehandling.exceptionhandling.FileHandlingException;
import edu.ntnu.idi.bidata.idatg2003mappe.filehandling.map.MapConfig;
import edu.ntnu.idi.bidata.idatg2003mappe.filehandling.map.MapConfigFileHandler;
import edu.ntnu.idi.bidata.idatg2003mappe.filehandling.player.PlayerFileHandler;
import edu.ntnu.idi.bidata.idatg2003mappe.map.Tile;
import edu.ntnu.idi.bidata.idatg2003mappe.map.board.BoardBranching;
import edu.ntnu.idi.bidata.idatg2003mappe.markers.Marker;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

/**
 * Represents the Missing Diamond game.
 * This class manages the game state and rules for the Missing Diamond game.
 *
 * //TODO ADD Javadoc for the class
 *
 * @author Simen Gudbrandsen and Frikk Breadsroed
 * @version 0.1.0
 * @since 23.05.2025
 */
public class MissingDiamond {

  // Logger for logging messages
  private static final Logger logger = Logger.getLogger(MissingDiamond.class.getName());

  // Starting money
  private static final int STARTING_MONEY = 300;

  // Game components
  private final BoardBranching board;
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

  // City tiles
  private final List<Tile> cityTiles = new ArrayList<>();
  private final List<Tile> startingTiles = new ArrayList<>();

  // NEW: Set of IDs for special tiles where players can choose to stop
  private final Set<Integer> specialTileIdsSet;

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
   * @param mapFilePath     The path to the map file
   */
  public MissingDiamond(int numberOfPlayers, String mapFilePath) {
    logger.info("Starting Missing Diamond Game with " + numberOfPlayers + " players.");

    this.banker = new Banker();
    this.tokenSystem = new TokenSystem();
    this.die = new Die();
    this.specialTileIdsSet = new HashSet<>(); // Initialize NEW field

    BoardBranching boardInstance;
    MapConfig mapConfig = null;
    try {
      MapConfigFileHandler mapFileHandler = new MapConfigFileHandler();
      mapConfig = mapFileHandler.read(mapFilePath); // mapConfig might be null if file not found or error

      if (mapConfig != null) {
        boardInstance = createBoardFromConfig(mapConfig);
        // Populate specialTileIdsSet from mapConfig
        if (mapConfig.getLocations() != null) {
          for (MapConfig.Location location : mapConfig.getLocations()) {
            if (location.isSpecial()) {
              this.specialTileIdsSet.add(location.getId());
            }
          }
        }
      } else {
        System.err.println("Error: Map configuration is null or could not be read. Falling back to default board.");
        boardInstance = createDefaultBoard();
      }

    } catch (FileHandlingException e) {
      System.err.println("Error loading map configuration: " + e.getMessage());
      boardInstance = createDefaultBoard();
      // Handle special tiles for default board after fallback
    }

    this.board = boardInstance;
    this.players = createPlayers(numberOfPlayers, boardInstance);
    this.gameFinished = false;
    this.currentPlayerIndex = 0;
    this.currentPlayer = players.get(currentPlayerIndex);
    this.currentRoll = 0;

    identifyCityTiles(); // This might be redundant if mapConfig is used for special tiles
    identifyStartingTiles();

    tokenSystem.setStartingTiles(startingTiles);
    tokenSystem.initializeTokens(cityTiles);

    for (Player player : players) {
      banker.registerPlayer(player);
      banker.deposit(player, STARTING_MONEY);
    }
  }

  /**
   * Constructor for MissingDiamond that reads players from CSV file.
   */
  public MissingDiamond() {

    this.banker = new Banker();
    this.tokenSystem = new TokenSystem();
    this.die = new Die();
    this.specialTileIdsSet = new HashSet<>(); // Initialize NEW field

    BoardBranching boardInstance;
    MapConfig mapConfig = null;
    try {
      MapConfigFileHandler mapFileHandler = new MapConfigFileHandler();
      if (mapFileHandler.defaultMapExists()) {
        mapConfig = mapFileHandler.loadFromDefaultLocation();
        boardInstance = createBoardFromConfig(mapConfig);
        // Populate specialTileIdsSet from mapConfig
        if (mapConfig != null && mapConfig.getLocations() != null) {
          for (MapConfig.Location location : mapConfig.getLocations()) {
            if (location.isSpecial()) {
              this.specialTileIdsSet.add(location.getId());
            }
          }
        }
      } else {
        boardInstance = createDefaultBoard();
        // Define default special tiles if any for default board
      }
    } catch (FileHandlingException e) {
      boardInstance = createDefaultBoard();
      // Handle special tiles for default board after fallback
    }

    this.board = boardInstance;
    // Identify starting tiles before reading players, as it might be needed for fallback
    identifyStartingTiles();
    PlayerFileHandler playerFileHandler = new PlayerFileHandler();
    this.players = playerFileHandler.readPlayersFromCSV(this.board, this.startingTiles);
    this.gameFinished = false;
    this.currentPlayerIndex = 0;
    this.currentPlayer = players.isEmpty() ? null : players.get(currentPlayerIndex);
    this.currentRoll = 0;

    identifyCityTiles();
    identifyStartingTiles();

    tokenSystem.setStartingTiles(startingTiles);
    tokenSystem.initializeTokens(cityTiles);

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
   * Also populates specialTileIdsSet with default special tiles if not already populated by a map config.
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

    // If specialTileIdsSet is empty (meaning no map config defined them), add defaults for this board.
    if (this.specialTileIdsSet.isEmpty()) {
      // Example: Make tiles 5, 10, 15 special for the default board
      this.specialTileIdsSet.add(5);
      this.specialTileIdsSet.add(10);
      this.specialTileIdsSet.add(15);
    }

    return board;
  }

  /**
   * Creates players for the game.
   *
   * @param numberOfPlayers The number of players to create
   * @param board           The game board
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

  // Removed readPlayersFromCSV() method from here

  /**
   * Identifies all city tiles on the board.
   */
  private void identifyCityTiles() {
    // In a real implementation, this would be based on map data
    // For now, assuming tiles with IDs 1-20 are cities
    for (int i = 1; i <= 32; i++) {
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

  // NEW helper method to check if a tile is special
  private boolean isSpecialTile(Tile tile) {
    if (tile == null || this.specialTileIdsSet == null) {
      return false;
    }
    return this.specialTileIdsSet.contains(tile.getTileId());
  }

  private void recursiveMoveFinder(Tile currentTile, int dieRoll, Set<Tile> visitedInCall, Set<Tile> resultOutput, int currentDepth) {

    // Logic for adding to resultOutput (based on currentTile, which is reached at currentDepth)
    if (currentDepth > 0) { // Only consider tiles reached after at least one step
      if (isSpecialTile(currentTile)) {
        resultOutput.add(currentTile); // Special tiles are valid stops if reached within dieRoll.
      } else { // Not a special tile
        if (currentDepth == dieRoll) {
          resultOutput.add(currentTile); // Non-special tiles only valid if exactly at dieRoll.
        }
      }
    }

    // Stop condition for recursion: if current depth has reached die roll, no more steps can be taken from here.
    if (currentDepth >= dieRoll) {
      return;
    }

    // Recursive step: explore neighbors
    for (Tile neighbor : currentTile.getNextTiles()) {
      if (!visitedInCall.contains(neighbor)) {
        visitedInCall.add(neighbor); // Mark neighbor as visited for this entire call to prevent cycles and re-processing
        recursiveMoveFinder(neighbor, dieRoll, visitedInCall, resultOutput, currentDepth + 1);
        // No removal from visitedInCall, to prevent re-exploring already processed nodes in this call.
      }
    }
  }

  /**
   * Gets all possible moves for the current player based on the last die roll.
   * Allows stopping on special tiles if encountered within the die roll distance.
   *
   * @return Set of tiles that the player can move to
   */
  public Set<Tile> getPossibleMovesForCurrentRoll() {
    Set<Tile> possibleMoves = new HashSet<>();
    if (currentRoll < 1 || currentPlayer == null || currentPlayer.getCurrentTile() == null) {
      return possibleMoves;
    }

    Tile startTile = currentPlayer.getCurrentTile();
    Set<Tile> visitedForThisCall = new HashSet<>();

    // Add the start tile itself to visited so the recursion starts by exploring its neighbors
    visitedForThisCall.add(startTile);

    // Call the recursive helper, starting at depth 0 for the player's current tile.
    // The recursive function will add valid destination tiles to 'possibleMoves'.
    recursiveMoveFinder(startTile, currentRoll, visitedForThisCall, possibleMoves, 0);

    return possibleMoves;
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
}
