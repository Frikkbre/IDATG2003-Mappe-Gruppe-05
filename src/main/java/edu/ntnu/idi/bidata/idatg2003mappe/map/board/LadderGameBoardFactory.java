package edu.ntnu.idi.bidata.idatg2003mappe.map.board;

import edu.ntnu.idi.bidata.idatg2003mappe.map.Tile;
import edu.ntnu.idi.bidata.idatg2003mappe.movement.TileActionFactory;

import java.util.*;

/**
 * Factory class for creating different types of game boards.
 *
 * @author Simen Gudbrandsen and Frikk Breadsroed
 * @version 0.0.1
 * @since 19.04.2025
 */
public class LadderGameBoardFactory {
  private static final int BOARD_SIZE = 100;
  private static final int NUM_LADDERS = 8;
  private static final int NUM_SNAKES = 8;

  /**
   * Creates a classic ladder game board with predefined ladders.
   */
  public static BoardLinear createClassicLadderBoard() {
    BoardLinear board = new BoardLinear();
    Tile[] tiles = new Tile[100];

    // Create and connect all tiles
    for (int i = 0; i < 100; i++) {
      tiles[i] = new Tile(i + 1);
      board.addTileToBoard(tiles[i]);
    }
    for (int i = 0; i < 99; i++) {
      tiles[i].setNextTile(tiles[i + 1]);
    }

    // Add classic ladders (up)
    tiles[1].setDestinationTile(tiles[37]); // 2 -> 38
    tiles[4].setDestinationTile(tiles[14]); // 5 -> 15
    tiles[9].setDestinationTile(tiles[31]); // 10 -> 32
    tiles[21].setDestinationTile(tiles[42]); // 22 -> 43
    tiles[28].setDestinationTile(tiles[84]); // 29 -> 85
    tiles[52].setDestinationTile(tiles[67]); // 53 -> 68
    tiles[73].setDestinationTile(tiles[92]); // 74 -> 93
    tiles[80].setDestinationTile(tiles[99]); // 81 -> 100

    // Add snakes (down)
    tiles[17].setDestinationTile(tiles[7]); // 18 -> 8
    tiles[61].setDestinationTile(tiles[11]); // 62 -> 12
    tiles[87].setDestinationTile(tiles[36]); // 88 -> 37
    tiles[55].setDestinationTile(tiles[35]); // 56 -> 36
    tiles[65].setDestinationTile(tiles[61]); // 66 -> 62
    tiles[88].setDestinationTile(tiles[37]); // 89 -> 38
    tiles[94].setDestinationTile(tiles[74]); // 95 -> 75
    tiles[98].setDestinationTile(tiles[80]); // 99 -> 81

    // Set up tile effects
    setupTileEffects(board);

    return board;
  }

  /**
   * Creates a ladder game board with randomly placed ladders and snakes.
   */
  public static BoardLinear createRandomLadderBoard() {
    BoardLinear board = new BoardLinear();
    Tile[] tiles = new Tile[BOARD_SIZE];

    // Create and connect all tiles
    for (int i = 0; i < BOARD_SIZE; i++) {
      tiles[i] = new Tile(i + 1);
      board.addTileToBoard(tiles[i]);
    }
    for (int i = 0; i < BOARD_SIZE - 1; i++) {
      tiles[i].setNextTile(tiles[i + 1]);
    }

    // Generate random ladders and snakes
    generateRandomLadders(tiles);

    // Set up tile effects
    setupTileEffects(board);

    return board;
  }

  /**
   * Generates random ladders and snakes on the board.
   */
  private static void generateRandomLadders(Tile[] tiles) {
    Random random = new Random();

    // Generate ladders (going up)
    generateRandomConnections(tiles, random, NUM_LADDERS, true);

    // Generate snakes (going down)
    generateRandomConnections(tiles, random, NUM_SNAKES, false);
  }

  /**
   * Generates random connections (ladders or snakes).
   */
  private static void generateRandomConnections(Tile[] tiles, Random random, int count, boolean isLadder) {
    int attempts = 0;
    int created = 0;

    while (created < count && attempts < count * 3) { // Prevent infinite loops
      attempts++;

      if (isLadder) {
        createRandomLadder(tiles, random);
      } else {
        createRandomSnake(tiles, random);
      }
      created++;
    }
  }

  /**
   * Creates a single random ladder.
   */
  private static void createRandomLadder(Tile[] tiles, Random random) {
    int start = random.nextInt(BOARD_SIZE - 20) + 1; // Avoid tiles too close to end
    int end = start + random.nextInt(15) + 5; // Jump forward 5-19 spaces

    if (end < BOARD_SIZE && tiles[start].getDestinationTile() == null) {
      TileActionFactory.createLadderAction(tiles[start], tiles[end]);
    }
  }

  /**
   * Creates a single random snake.
   */
  private static void createRandomSnake(Tile[] tiles, Random random) {
    int start = random.nextInt(BOARD_SIZE - 20) + 20; // Start from middle-to-end
    int end = start - random.nextInt(15) - 5; // Jump back 5-19 spaces

    if (end > 0 && tiles[start].getDestinationTile() == null) {
      TileActionFactory.createLadderAction(tiles[start], tiles[end]);
    }
  }

  /**
   * Sets up special tile effects throughout the board.
   */
  public static void setupTileEffects(BoardLinear board) {
    // Configure skip turn tiles
    int[] skipTurnTiles = {13, 25, 57, 70, 96};
    for (int tileId : skipTurnTiles) {
      setTileEffect(board, tileId, "skipTurn");
    }

    // Configure back to start tiles
    setTileEffect(board, 45, "backToStart");
  }

  /**
   * Helper method to set an effect on a specific tile.
   */
  private static void setTileEffect(BoardLinear board, int tileId, String effect) {
    if (isValidTileRange(tileId)) {
      Tile tile = board.getTileById(tileId);
      if (tile != null) {
        tile.setEffect(effect);
      }
    }
  }

  /**
   * Validates if a tile number is within the valid range.
   */
  private static boolean isValidTileRange(int tileNumber) {
    return tileNumber >= 1 && tileNumber <= BOARD_SIZE;
  }

}
