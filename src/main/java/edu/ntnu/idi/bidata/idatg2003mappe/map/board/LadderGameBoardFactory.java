package edu.ntnu.idi.bidata.idatg2003mappe.map.board;

import edu.ntnu.idi.bidata.idatg2003mappe.map.Tile;
import edu.ntnu.idi.bidata.idatg2003mappe.movement.TileActionFactory;

import java.util.*;

/**
 * <p>Factory class for creating different types of ladder game boards.</p>
 * <p>This class provides methods for creating boards with various configurations:</p>
 * <ul>
 *   <li>Classic ladder game board with predefined ladder and snake positions</li>
 *   <li>Random ladder game board with randomized ladder and snake positions</li>
 * </ul>
 * <p>The factory also handles setting up special tile effects like "skip turn" and "back to start".</p>
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
   * <p>Creates a classic ladder game board with predefined ladders.</p>
   * <p>This method builds a 100-tile board with fixed ladder and snake positions
   * based on the traditional Snakes and Ladders game layout.</p>
   *
   * @return A {@link BoardLinear} instance with classic ladder and snake configuration
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
   * <p>Creates a ladder game board with randomly placed ladders and snakes.</p>
   * <p>This method builds a 100-tile board where ladders and snakes are
   * placed in random positions, creating a unique gameplay experience each time.</p>
   *
   * @return A {@link BoardLinear} instance with randomized ladder and snake configuration
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
   * <p>Generates random ladders and snakes on the board.</p>
   * <p>Places the specified number of ladders and snakes at random positions
   * on the board, ensuring they don't overlap or cause invalid movements.</p>
   *
   * @param tiles The array of tiles representing the board
   */
  private static void generateRandomLadders(Tile[] tiles) {
    Random random = new Random();

    // Generate ladders (going up)
    generateRandomConnections(tiles, random, NUM_LADDERS, true);

    // Generate snakes (going down)
    generateRandomConnections(tiles, random, NUM_SNAKES, false);
  }

  /**
   * <p>Generates random connections (ladders or snakes).</p>
   * <p>Creates the specified number of connections of the given type (ladder or snake),
   * trying multiple times if necessary to ensure the desired count is reached.</p>
   *
   * @param tiles The array of tiles representing the board
   * @param random The random number generator to use
   * @param count The number of connections to create
   * @param isLadder <code>true</code> to create ladders, <code>false</code> to create snakes
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
   * <p>Creates a single random ladder.</p>
   * <p>Places a ladder at a random position on the board,
   * connecting a lower tile to a higher tile.</p>
   *
   * @param tiles The array of tiles representing the board
   * @param random The random number generator to use
   */
  private static void createRandomLadder(Tile[] tiles, Random random) {
    int start = random.nextInt(BOARD_SIZE - 20) + 1; // Avoid tiles too close to end
    int end = start + random.nextInt(15) + 5; // Jump forward 5-19 spaces

    if (end < BOARD_SIZE && tiles[start].getDestinationTile() == null) {
      TileActionFactory.createLadderAction(tiles[start], tiles[end]);
    }
  }

  /**
   * <p>Creates a single random snake.</p>
   * <p>Places a snake at a random position on the board,
   * connecting a higher tile to a lower tile.</p>
   *
   * @param tiles The array of tiles representing the board
   * @param random The random number generator to use
   */
  private static void createRandomSnake(Tile[] tiles, Random random) {
    int start = random.nextInt(BOARD_SIZE - 20) + 20; // Start from middle-to-end
    int end = start - random.nextInt(15) - 5; // Jump back 5-19 spaces

    if (end > 0 && tiles[start].getDestinationTile() == null) {
      TileActionFactory.createLadderAction(tiles[start], tiles[end]);
    }
  }

  /**
   * <p>Sets up special tile effects throughout the board.</p>
   * <p>Configures specific tiles to have special effects when landed on,
   * such as skipping a turn or going back to the start.</p>
   *
   * @param board The board to set up effects on
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
   * <p>Helper method to set an effect on a specific tile.</p>
   * <p>Applies the specified effect to the tile with the given ID,
   * if the tile exists and the ID is valid.</p>
   *
   * @param board The board containing the tile
   * @param tileId The ID of the tile to set the effect on
   * @param effect The effect to apply
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
   * <p>Validates if a tile number is within the valid range.</p>
   * <p>Checks if the specified tile ID is between 1 and the board size (inclusive).</p>
   *
   * @param tileNumber The tile ID to validate
   * @return <code>true</code> if the tile ID is valid, <code>false</code> otherwise
   */
  private static boolean isValidTileRange(int tileNumber) {
    return tileNumber >= 1 && tileNumber <= BOARD_SIZE;
  }

}
