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

import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * <p>Represents the core game model for the Missing Diamond adventure board game.</p>
 *
 * <p>The Missing Diamond game is a strategic adventure where players navigate across a map
 * of interconnected cities in search of a valuable diamond. Players can:</p>
 *
 * <ul>
 *   <li>Roll dice to determine movement options</li>
 *   <li>Collect tokens with various effects (gems, diamond, visa, bandit)</li>
 *   <li>Navigate through special tiles and city locations</li>
 *   <li>Manage their money resources through the banker system</li>
 * </ul>
 *
 * <p>The game ends when a player finds the diamond token and reaches the victory condition.
 * Players must strategically decide their movement paths and token interactions to maximize
 * their chances of finding the diamond while maintaining sufficient resources.</p>
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
  private final Die die;
  private final TokenSystem tokenSystem;
  private final Banker banker;
  // City tiles
  private final Collection<Tile> cityTiles = new ArrayList<>();
  private final List<Tile> startingTiles = new ArrayList<>();
  // NEW: Set of IDs for special tiles where players can choose to stop
  private final Set<Integer> specialTileIdsSet;
  private List<Player> players = new ArrayList<>();
  // Game state
  private boolean gameFinished;
  private Player currentPlayer;
  private int currentPlayerIndex;
  private int currentRoll; // Store the last roll value
  private Player winner;

  /**
   * <p>Constructs a new Missing Diamond game instance with a specified number of players
   * using the default map.</p>
   *
   * <p>This constructor initializes the game with the default map configuration and
   * creates the specified number of players with starting positions and funds.</p>
   *
   * @param numberOfPlayers The number of players in the game
   */
  public MissingDiamond(int numberOfPlayers) {
    this(numberOfPlayers, MapConfigFileHandler.getDefaultMapResource());
  }

  /**
   * <p>Constructs a new Missing Diamond game instance with a specified number of players
   * and a custom map path.</p>
   *
   * <p>This constructor initializes the game with the specified map configuration and
   * creates the specified number of players with starting positions and funds. If the map
   * cannot be loaded, it will fall back to using a default board configuration.</p>
   *
   * <p>The mapPath can be either:</p>
   * <ul>
   *   <li>A classpath resource path starting with "/" (e.g., "/maps/default.json")</li>
   *   <li>A file system path for custom user maps</li>
   * </ul>
   *
   * @param numberOfPlayers The number of players in the game
   * @param mapPath         The path to the map (classpath resource or file system)
   */
  public MissingDiamond(int numberOfPlayers, String mapPath) {
    logger.info("Starting Missing Diamond Game with " + numberOfPlayers + " players.");

    this.banker = new Banker();
    this.tokenSystem = new TokenSystem();
    this.die = new Die();
    this.specialTileIdsSet = new HashSet<>(); // Initialize NEW field

    BoardBranching boardInstance;
    MapConfig mapConfig = null;
    try {
      MapConfigFileHandler mapFileHandler = new MapConfigFileHandler();
      // Use classpath resource loading for paths starting with "/"
      if (mapPath.startsWith("/")) {
        mapConfig = mapFileHandler.readFromResource(mapPath);
      } else {
        mapConfig = mapFileHandler.read(mapPath);
      }

      if (mapConfig != null) {
        boardInstance = createBoardFromConfig(mapConfig);
        // Populate specialTileIdsSet from mapConfig
        if (mapConfig.getLocations() != null) {
          mapConfig.getLocations().stream()
              .filter(MapConfig.Location::isSpecial)
              .map(MapConfig.Location::getId)
              .forEach(specialTileIdsSet::add);

        }
      } else {
        logger.severe("Map configuration is null or could not be read. Falling back to default board.");
        boardInstance = createDefaultBoard();
      }

    } catch (FileHandlingException e) {
      logger.severe("Error loading map configuration: " + e.getMessage());
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

    players.forEach(player -> {
      banker.registerPlayer(player);
      banker.deposit(player, STARTING_MONEY);
    });
  }

  /**
   * <p>Constructs a new Missing Diamond game instance by loading players from a CSV file
   * and using the default map configuration.</p>
   *
   * <p>This constructor attempts to load the default map configuration and player information
   * from a CSV file. If either file cannot be loaded, it will use appropriate fallback
   * mechanisms to ensure the game can still start.</p>
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
          mapConfig.getLocations().stream()
              .filter(MapConfig.Location::isSpecial)
              .map(MapConfig.Location::getId)
              .forEach(specialTileIdsSet::add);

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

    players.forEach(player -> {
      banker.registerPlayer(player);
      banker.deposit(player, STARTING_MONEY);
    });
  }

  /**
   * <p>Creates a game board from a map configuration.</p>
   *
   * <p>This method processes the map configuration to create tiles and connections between
   * them according to the specified layout in the configuration.</p>
   *
   * @param mapConfig The map configuration containing location and connection information
   * @return A fully initialized branching board with all tiles and connections
   */
  private BoardBranching createBoardFromConfig(MapConfig mapConfig) {
    BoardBranching board = new BoardBranching();
    board.setBoardName(mapConfig.getName());

    // Create all tiles
    mapConfig.getLocations().forEach(location -> board.addTileToBoard(new Tile(location.getId())));

    // Add all connections
    mapConfig.getConnections().forEach(connection -> {
      Tile fromTile = board.getTileById(connection.getFromId());
      Tile toTile = board.getTileById(connection.getToId());

      if (fromTile != null && toTile != null) {
        board.connectTiles(fromTile, toTile);
      }
    });

    return board;
  }


  /**
   * <p>Creates a default game board with a predefined structure when no map configuration
   * is available.</p>
   *
   * <p>This method generates a simple network of connected tiles to serve as a fallback
   * when the map configuration cannot be loaded. It also defines which tiles are considered
   * special (allowing players to stop before reaching their exact die roll).</p>
   *
   * @return A default branching board with a basic network of connected tiles
   */
  private BoardBranching createDefaultBoard() {
    BoardBranching board = new BoardBranching();
    board.setBoardName("Default Missing Diamond Map");

    // Create basic city tiles
    IntStream.rangeClosed(1, 20)
        .mapToObj(Tile::new)
        .forEach(board::addTileToBoard);


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
    IntStream.rangeClosed(3, 18).forEach(i -> {
      Tile current = board.getTileById(i);
      Tile next = board.getTileById(i + 1);

      if (current != null && next != null) {
        board.connectTiles(current, next);
      }

      // Add some cross-connections
      if (i % 3 == 0 && i + 4 <= 20) {
        Optional.ofNullable(board.getTileById(i + 4))
            .ifPresent(crossTile -> board.connectTiles(current, crossTile));
      }
    });


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
   * <p>Creates player objects for the game with appropriate starting positions and funds.</p>
   *
   * <p>This method initializes the specified number of players with unique colors, names,
   * and IDs. All players start at one of the designated starting tiles and receive the
   * standard starting money amount.</p>
   *
   * @param numberOfPlayers The number of players to create
   * @param board           The game board where players will be placed
   * @return A list of initialized player objects
   */
  private List<Player> createPlayers(int numberOfPlayers, BoardBranching board) {
    Tile startTile = startingTiles.stream().findFirst().orElse(board.getStartTile());

    String[] playerColors = {"Orange", "Blue", "Green", "Yellow", "Purple", "Red"};

    return IntStream.range(0, numberOfPlayers)
        .mapToObj(i -> {
          String color = playerColors[i % playerColors.length]; // Wraparound for colors
          Player player = new Player("Player " + (i + 1), i, color, startTile);
          banker.registerPlayer(player);
          banker.deposit(player, STARTING_MONEY);
          return player;
        })
        .collect(Collectors.toList());
  }

  /**
   * <p>Identifies all city tiles on the board that can have tokens placed on them.</p>
   *
   * <p>This method populates the cityTiles collection with all valid tile locations
   * where tokens can be placed during gameplay.</p>
   */
  private void identifyCityTiles() {
    // In a real implementation, this would be based on map data
    // For now, assuming tiles with IDs 1-20 are cities
    IntStream.rangeClosed(1, 32)
        .mapToObj(board::getTileById)
        .filter(Objects::nonNull)
        .forEach(cityTiles::add);

  }

  /**
   * <p>Identifies the valid starting tile locations for players.</p>
   *
   * <p>This method populates the startingTiles collection with tiles that are
   * designated as starting positions (typically Cairo and Tangiers).</p>
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
   * <p>Simulates the current player's turn by rolling the die.</p>
   *
   * <p>This method rolls the die for the current player and returns a message
   * describing the result. The roll value is stored in the currentRoll field.</p>
   *
   * @return A message describing the die roll result
   */
  public String playTurn() {
    // Roll the die
    this.currentRoll = die.rollDie();
    return currentPlayer.getName() + " rolled a " + currentRoll + ".";
  }

  /**
   * <p>Determines if a tile is a special tile where players can optionally stop.</p>
   *
   * <p>Special tiles allow players to stop before reaching their exact die roll,
   * providing strategic options for movement.</p>
   *
   * @param tile The tile to check
   * @return {@code true} if the tile is designated as special, {@code false} otherwise
   */
  private boolean isSpecialTile(Tile tile) {
    if (tile == null || this.specialTileIdsSet == null) {
      return false;
    }
    return this.specialTileIdsSet.contains(tile.getTileId());
  }

  /**
   * <p>Recursive helper method to find all valid destination tiles for a player's move.</p>
   *
   * <p>This method explores the board recursively to identify all tiles that a player
   * can legally move to based on their die roll and the special tile rules.</p>
   *
   * @param currentTile   The current tile being explored in the recursion
   * @param dieRoll       The original die roll value
   * @param visitedInCall Set of tiles already visited in this recursive call branch
   * @param resultOutput  Set to be populated with valid destination tiles
   * @param currentDepth  Current recursion depth (steps taken so far)
   */
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
    currentTile.getNextTiles().stream()
        .filter(neighbor -> !visitedInCall.contains(neighbor))
        .peek(visitedInCall::add) // Mark as visited
        .forEach(neighbor -> recursiveMoveFinder(neighbor, dieRoll, visitedInCall, resultOutput, currentDepth + 1));

  }

  /**
   * <p>Gets all possible destination tiles for the current player based on their die roll.</p>
   *
   * <p>This method calculates all valid tiles that the player can move to, considering:
   * <ul>
   *   <li>The player must move exactly their die roll value (for normal tiles)</li>
   *   <li>The player may stop early on special tiles if encountered within the roll distance</li>
   * </ul></p>
   *
   * @return A set of tiles that the player can legally move to
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
   * <p>Moves the current player to the selected destination tile.</p>
   *
   * <p>This method updates the player's position on the board if the move is valid.
   * It also checks for victory conditions after the move is complete.</p>
   *
   * @param destinationTile The tile to move the player to
   * @return A message describing the result of the move
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
   * <p>Opens a token at the current player's location.</p>
   * <p>Processes the result based on the token type.</p>
   *
   * @param tile The tile to check for token presence
   * @return {@code true} if a token exists at the tile, {@code false} otherwise
   */
  public boolean hasTokenAtTile(Tile tile) {
    return tokenSystem.getTokenAtTile(tile) != null;
  }

  /**
   * <p>Gets the token marker at a specific tile.</p>
   *
   * @param tile The tile to retrieve the token from
   * @return The marker at the tile, or {@code null} if no marker exists
   */
  public Marker getTokenAtTile(Tile tile) {
    return tokenSystem.getTokenAtTile(tile);
  }

  /**
   * <p>Checks if the current player has met the victory condition.</p>
   *
   * <p>The victory condition is met when a player has found the diamond token.</p>
   *
   * @return {@code true} if the victory condition is met, {@code false} otherwise
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
   * <p>Gets the list of all players in the game.</p>
   *
   * @return The list of all players
   */
  public List<Player> getPlayers() {
    return players;
  }

  /**
   * <p>Gets the game board.</p>
   *
   * @return The branching board representing the game map
   */
  public BoardBranching getBoard() {
    return board;
  }

  /**
   * <p>Gets the die used for determining player movement.</p>
   *
   * @return The die object
   */
  public Die getDie() {
    return die;
  }

  /**
   * <p>Gets the value of the current player's die roll.</p>
   *
   * @return The current roll value
   */
  public int getCurrentRoll() {
    return currentRoll;
  }

  /**
   * <p>Checks if the game has finished.</p>
   *
   * @return {@code true} if the game is finished, {@code false} otherwise
   */
  public boolean isGameFinished() {
    return gameFinished;
  }

  /**
   * <p>Gets the current active player.</p>
   *
   * @return The current player
   */
  public Player getCurrentPlayer() {
    return currentPlayer;
  }

  /**
   * <p>Sets the active player by index.</p>
   *
   * <p>This method changes the current player to the player at the specified index
   * in the players list.</p>
   *
   * @param playerIndex The index of the player to set as active
   * @throws IllegalArgumentException if the index is out of bounds
   */
  public void setCurrentPlayerIndex(int playerIndex) {
    if (playerIndex < 0 || playerIndex >= players.size()) {
      throw new IllegalArgumentException("Invalid player index.");
    }
    this.currentPlayerIndex = playerIndex;
    this.currentPlayer = players.get(playerIndex);
  }

  /**
   * <p>Gets the banker that manages all financial transactions.</p>
   *
   * @return The banker object
   */
  public Banker getBanker() {
    return banker;
  }

  /**
   * <p>Gets the token system that manages all tokens and their effects.</p>
   *
   * @return The token system object
   */
  public TokenSystem getTokenSystem() {
    return tokenSystem;
  }
}
