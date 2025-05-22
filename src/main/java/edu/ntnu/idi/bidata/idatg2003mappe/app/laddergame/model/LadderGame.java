package edu.ntnu.idi.bidata.idatg2003mappe.app.laddergame.model;

import edu.ntnu.idi.bidata.idatg2003mappe.entity.die.Die;
import edu.ntnu.idi.bidata.idatg2003mappe.entity.player.Player;
import edu.ntnu.idi.bidata.idatg2003mappe.entity.player.PlayerFactory;
import edu.ntnu.idi.bidata.idatg2003mappe.map.Tile;
import edu.ntnu.idi.bidata.idatg2003mappe.map.board.BoardLinear;
import edu.ntnu.idi.bidata.idatg2003mappe.movement.TileActionFactory;

import java.util.*;

/**
 * Represents a Ladder Game (Snakes and Ladders) with configurable board setup.
 * This class handles the core game logic including board creation, player management,
 * and turn-based gameplay mechanics.
 *
 * @author Simen Gudbrandsen and Frikk Breadsroed
 * @version 1.0.0
 * @since 21.05.2025
 */
public class LadderGame {

  // Game configuration constants
  private static final String PLAYER_DATA_FILE = "src/main/resources/saves/playerData/Players.csv";
  private static final int BOARD_SIZE = 100;
  private static final int NUM_LADDERS = 8;
  private static final int NUM_SNAKES = 8;

  // Core game components
  private final BoardLinear board;
  private final List<Player> players;
  private final Die die;
  private final boolean randomLadders;

  // Special tile effects configuration
  private final Map<Integer, String> tileEffects;

  /**
   * Creates a new Ladder Game with the specified configuration.
   *
   * @param randomLadders true for random placement, false for classic setup
   * @throws IllegalStateException if game initialization fails
   */
  public LadderGame(boolean randomLadders) {
    this.randomLadders = randomLadders;
    this.tileEffects = new HashMap<>();
    this.die = new Die();

    // Initialize game components
    this.board = createBoard();
    this.players = loadPlayers();

    if (players.isEmpty()) {
      throw new IllegalStateException("No players could be loaded for the game");
    }
  }

  /**
   * Creates and configures the game board with tiles, ladders, and effects.
   *
   * @return configured BoardLinear instance
   */
  private BoardLinear createBoard() {
    BoardLinear gameBoard = new BoardLinear();
    Tile[] tiles = createTiles(gameBoard);
    connectTiles(tiles);

    if (randomLadders) {
      generateRandomLadders(tiles);
    } else {
      setClassicLadders(tiles);
    }

    setupTileEffects(tiles);
    return gameBoard;
  }

  /**
   * Creates all tiles for the board.
   */
  private Tile[] createTiles(BoardLinear gameBoard) {
    Tile[] tiles = new Tile[BOARD_SIZE];

    for (int i = 0; i < BOARD_SIZE; i++) {
      tiles[i] = new Tile(i + 1);
      gameBoard.addTileToBoard(tiles[i]);
    }

    return tiles;
  }

  /**
   * Connects tiles in sequence to form the game path.
   */
  private void connectTiles(Tile[] tiles) {
    for (int i = 0; i < BOARD_SIZE - 1; i++) {
      tiles[i].setNextTile(tiles[i + 1]);
    }
  }

  /**
   * Sets up the classic ladder and snake configuration.
   *
   * @param tiles array of game tiles
   */
  private void setClassicLadders(Tile[] tiles) {
    // Classic ladders (going up)
    createLadder(tiles, 3, 39);   // 3 → 39
    createLadder(tiles, 6, 16);   // 6 → 16
    createLadder(tiles, 11, 33);  // 11 → 33
    createLadder(tiles, 23, 44);  // 23 → 44
    createLadder(tiles, 30, 86);  // 30 → 86
    createLadder(tiles, 53, 68);  // 53 → 68
    createLadder(tiles, 74, 93);  // 74 → 93
    createLadder(tiles, 81, 100); // 81 → 100

    // Classic snakes (going down)
    createLadder(tiles, 19, 9);   // 19 → 9
    createLadder(tiles, 63, 13);  // 63 → 13
    createLadder(tiles, 56, 36);  // 56 → 36
    createLadder(tiles, 66, 62);  // 66 → 62
    createLadder(tiles, 89, 38);  // 89 → 38
    createLadder(tiles, 95, 75);  // 95 → 75
    createLadder(tiles, 99, 81);  // 99 → 81
  }

  /**
   * Helper method to create a ladder/snake connection.
   */
  private void createLadder(Tile[] tiles, int from, int to) {
    if (isValidTileRange(from) && isValidTileRange(to)) {
      TileActionFactory.createLadderAction(tiles[from - 1], tiles[to - 1]);
    }
  }

  /**
   * Validates if a tile number is within the valid range.
   */
  private boolean isValidTileRange(int tileNumber) {
    return tileNumber >= 1 && tileNumber <= BOARD_SIZE;
  }

  /**
   * Generates random ladders and snakes on the board.
   *
   * @param tiles array of game tiles
   */
  private void generateRandomLadders(Tile[] tiles) {
    Random random = new Random();

    // Generate ladders (going up)
    generateRandomConnections(tiles, random, NUM_LADDERS, true);

    // Generate snakes (going down)
    generateRandomConnections(tiles, random, NUM_SNAKES, false);
  }

  /**
   * Generates random connections (ladders or snakes).
   */
  private void generateRandomConnections(Tile[] tiles, Random random, int count, boolean isLadder) {
    int attempts = 0;
    int created = 0;

    while (created < count && attempts < count * 3) { // Prevent infinite loops
      attempts++;

      if (isLadder) {
        createRandomLadder(tiles, random, created);
      } else {
        createRandomSnake(tiles, random, created);
      }
      created++;
    }
  }

  /**
   * Creates a single random ladder.
   */
  private void createRandomLadder(Tile[] tiles, Random random, int created) {
    int start = random.nextInt(BOARD_SIZE - 20) + 1; // Avoid tiles too close to end
    int end = start + random.nextInt(15) + 5; // Jump forward 5-19 spaces

    if (end < BOARD_SIZE && tiles[start].getDestinationTile() == null) {
      TileActionFactory.createLadderAction(tiles[start], tiles[end]);
    }
  }

  /**
   * Creates a single random snake.
   */
  private void createRandomSnake(Tile[] tiles, Random random, int created) {
    int start = random.nextInt(BOARD_SIZE - 20) + 20; // Start from middle-to-end
    int end = start - random.nextInt(15) - 5; // Jump back 5-19 spaces

    if (end > 0 && tiles[start].getDestinationTile() == null) {
      TileActionFactory.createLadderAction(tiles[start], tiles[end]);
    }
  }

  /**
   * Sets up special tile effects throughout the board.
   *
   * @param tiles array of game tiles
   */
  private void setupTileEffects(Tile[] tiles) {
    // Configure skip turn tiles
    int[] skipTurnTiles = {13, 25, 57, 70, 96};
    for (int tileId : skipTurnTiles) {
      setTileEffect(tiles, tileId, "skipTurn");
    }

    // Configure back to start tiles
    setTileEffect(tiles, 45, "backToStart");
  }

  /**
   * Helper method to set an effect on a specific tile.
   */
  private void setTileEffect(Tile[] tiles, int tileId, String effect) {
    if (isValidTileRange(tileId)) {
      tiles[tileId - 1].setEffect(effect);
      tileEffects.put(tileId, effect);
    }
  }

  /**
   * Loads players from CSV file using PlayerFactory.
   *
   * @return list of players for the game
   */
  private List<Player> loadPlayers() {
    return PlayerFactory.createPlayersFromCSV(PLAYER_DATA_FILE, board);
  }

  // Public getters

  /**
   * Gets all players in the game.
   *
   * @return unmodifiable list of players
   */
  public List<Player> getPlayers() {
    return new ArrayList<>(players);
  }

  /**
   * Gets the game board.
   *
   * @return the board instance
   */
  public BoardLinear getBoard() {
    return board;
  }

  /**
   * Gets the game die.
   *
   * @return the die instance
   */
  public Die getDie() {
    return die;
  }

  /**
   * Gets the total number of tiles on the board.
   *
   * @return number of tiles
   */
  public int getNumberOfTiles() {
    return BOARD_SIZE;
  }

}