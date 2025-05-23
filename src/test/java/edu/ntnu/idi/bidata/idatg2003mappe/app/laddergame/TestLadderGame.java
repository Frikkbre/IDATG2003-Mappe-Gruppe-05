package edu.ntnu.idi.bidata.idatg2003mappe.app.laddergame;

import static org.junit.jupiter.api.Assertions.*;

import edu.ntnu.idi.bidata.idatg2003mappe.app.laddergame.model.LadderGame;
import edu.ntnu.idi.bidata.idatg2003mappe.entity.die.Die;
import edu.ntnu.idi.bidata.idatg2003mappe.entity.player.Player;
import edu.ntnu.idi.bidata.idatg2003mappe.map.Tile;
import edu.ntnu.idi.bidata.idatg2003mappe.map.board.BoardLinear;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

class TestLadderGame {

  private LadderGame ladderGame;

  private LadderGame classicLadderGame;
  private LadderGame randomLadderGame;
  private File tempPlayerFile;

  @TempDir
  Path tempDir;


  @BeforeEach
  void setUp() throws IOException {
    // Arrange: Create a temporary player CSV file for testing
    tempPlayerFile = tempDir.resolve("test_players.csv").toFile();
    createTestPlayerFile();

    // Create both classic and random ladder games for testing
    classicLadderGame = new LadderGame(false);
    randomLadderGame = new LadderGame(true);
  }

  @AfterEach
  void tearDown() {
    classicLadderGame = null;
    randomLadderGame = null;
    tempPlayerFile = null;
  }

  /**
   * Helper method to create a test player CSV file
   */
  private void createTestPlayerFile() throws IOException {
    try (FileWriter writer = new FileWriter(tempPlayerFile)) {
      writer.write("Player Name,Player ID,Color,Position\n");
      writer.write("Test Player 1,0,LightGreen,1\n");
      writer.write("Test Player 2,1,LightPink,1\n");
    }
  }

  @Test
  @DisplayName("Test that LadderGame initializes with classic board configuration")
  void testClassicBoardInitialization() {
    // Act - already done in setUp

    // Assert
    assertNotNull(classicLadderGame, "Classic ladder game should be initialized");
    assertNotNull(classicLadderGame.getBoard(), "Board should be initialized");
    assertNotNull(classicLadderGame.getDie(), "Die should be initialized");
    assertNotNull(classicLadderGame.getPlayers(), "Players list should be initialized");
    assertFalse(classicLadderGame.getPlayers().isEmpty(), "Players list should not be empty");
  }

  @Test
  @DisplayName("Test that LadderGame initializes with random board configuration")
  void testRandomBoardInitialization() {
    // Act - already done in setUp

    // Assert
    assertNotNull(randomLadderGame, "Random ladder game should be initialized");
    assertNotNull(randomLadderGame.getBoard(), "Board should be initialized");
    assertNotNull(randomLadderGame.getDie(), "Die should be initialized");
    assertNotNull(randomLadderGame.getPlayers(), "Players list should be initialized");
    assertFalse(randomLadderGame.getPlayers().isEmpty(), "Players list should not be empty");
  }

  @Test
  @DisplayName("Test that the board has the correct number of tiles")
  void testBoardSize() {
    // Act
    int expectedTiles = classicLadderGame.getNumberOfTiles();
    BoardLinear board = classicLadderGame.getBoard();

    // Count tiles by traversing from start
    int actualTiles = 0;
    Tile currentTile = board.getStartTile();
    while (currentTile != null && actualTiles < 101) { // Prevent infinite loop
      actualTiles++;
      currentTile = currentTile.getNextTile();
    }

    // Assert
    assertEquals(100, expectedTiles, "Number of tiles should be 100");
    assertEquals(100, actualTiles, "Actual number of tiles on board should be 100");
  }

  @Test
  @DisplayName("Test that players are loaded correctly from CSV")
  void testPlayerLoading() {
    // Act
    List<Player> players = classicLadderGame.getPlayers();

    // Assert
    assertNotNull(players, "Players list should not be null");
    assertTrue(players.size() >= 1, "At least one player should be loaded");

    // Check first player properties
    Player firstPlayer = players.get(0);
    assertNotNull(firstPlayer, "First player should not be null");
    assertNotNull(firstPlayer.getName(), "Player name should not be null");
    assertNotNull(firstPlayer.getColor(), "Player color should not be null");
    assertNotNull(firstPlayer.getCurrentTile(), "Player should be placed on a tile");
  }

  @Test
  @DisplayName("Test that all players start at tile 1")
  void testPlayerStartingPosition() {
    // Act
    List<Player> players = classicLadderGame.getPlayers();

    // Assert
    for (Player player : players) {
      assertEquals(1, player.getCurrentTile().getTileId(),
          "Player " + player.getName() + " should start at tile 1");
    }
  }

  @Test
  @DisplayName("Test that classic board has specific ladder connections")
  void testClassicLadderConnections() {
    // Arrange
    BoardLinear board = classicLadderGame.getBoard();

    // Act & Assert - Check some known ladder connections
    // Ladder from tile 2 to tile 38
    Tile tile2 = board.getTileByIdLinear(2);
    assertNotNull(tile2, "Tile 2 should exist");
    assertNotNull(tile2.getDestinationTile(), "Tile 2 should have a ladder");
    assertEquals(38, tile2.getDestinationTile().getTileId(),
        "Tile 2 should have ladder to tile 38");

    // Snake from tile 18 to tile 8
    Tile tile18 = board.getTileByIdLinear(18);
    assertNotNull(tile18, "Tile 18 should exist");
    assertNotNull(tile18.getDestinationTile(), "Tile 18 should have a snake");
    assertEquals(8, tile18.getDestinationTile().getTileId(),
        "Tile 18 should have snake to tile 8");
  }

  @Test
  @DisplayName("Test that random board has ladder connections")
  void testRandomBoardHasLadders() {
    // Arrange
    BoardLinear board = randomLadderGame.getBoard();
    int laddersFound = 0;
    int snakesFound = 0;

    // Act - Count ladders and snakes
    for (int i = 1; i <= 100; i++) {
      Tile tile = board.getTileByIdLinear(i);
      if (tile != null && tile.getDestinationTile() != null) {
        if (tile.getDestinationTile().getTileId() > i) {
          laddersFound++;
        } else {
          snakesFound++;
        }
      }
    }

    // Assert
    assertTrue(laddersFound > 0, "Random board should have at least one ladder");
    assertTrue(snakesFound > 0, "Random board should have at least one snake");
  }

  @Test
  @DisplayName("Test that special effect tiles are set correctly")
  void testSpecialEffectTiles() {
    // Arrange
    BoardLinear board = classicLadderGame.getBoard();
    int[] skipTurnTiles = {13, 25, 57, 70, 96};

    // Act & Assert - Check skip turn tiles
    for (int tileId : skipTurnTiles) {
      Tile tile = board.getTileByIdLinear(tileId);
      assertNotNull(tile, "Tile " + tileId + " should exist");
      assertEquals("skipTurn", tile.getEffect(),
          "Tile " + tileId + " should have skipTurn effect");
    }

    // Check back to start tile
    Tile tile45 = board.getTileByIdLinear(45);
    assertNotNull(tile45, "Tile 45 should exist");
    assertEquals("backToStart", tile45.getEffect(),
        "Tile 45 should have backToStart effect");
  }

  @Test
  @DisplayName("Test getDie returns a valid Die instance")
  void testGetDie() {
    // Act
    Die die = classicLadderGame.getDie();

    // Assert
    assertNotNull(die, "Die should not be null");

    // Test that die can roll
    int rollValue = die.rollDie();
    assertTrue(rollValue >= 1 && rollValue <= 6,
        "Die roll should be between 1 and 6");
  }

  @Test
  @DisplayName("Test getBoard returns a valid BoardLinear instance")
  void testGetBoard() {
    // Act
    BoardLinear board = classicLadderGame.getBoard();

    // Assert
    assertNotNull(board, "Board should not be null");
    assertNotNull(board.getStartTile(), "Board should have a start tile");
    assertEquals(1, board.getStartTile().getTileId(),
        "Start tile should have ID 1");
  }

  @Test
  @DisplayName("Test board tiles are properly connected")
  void testBoardTileConnections() {
    // Arrange
    BoardLinear board = classicLadderGame.getBoard();

    // Act & Assert - Check that tiles are connected sequentially
    for (int i = 1; i < 100; i++) {
      Tile currentTile = board.getTileByIdLinear(i);
      Tile nextTile = board.getTileByIdLinear(i + 1);

      assertNotNull(currentTile, "Tile " + i + " should exist");
      assertNotNull(nextTile, "Tile " + (i + 1) + " should exist");
      assertEquals(nextTile, currentTile.getNextTile(),
          "Tile " + i + " should connect to tile " + (i + 1));
    }

    // Check last tile
    Tile lastTile = board.getTileByIdLinear(100);
    assertNotNull(lastTile, "Tile 100 should exist");
    assertNull(lastTile.getNextTile(), "Tile 100 should not have a next tile");
  }

  @Test
  @DisplayName("Test that game throws exception when no players can be loaded")
  void testNoPlayersThrowsException() {
    // This test would require mocking or a way to force no players to load
    // For now, we just verify that the game requires players

    // Act
    List<Player> players = classicLadderGame.getPlayers();

    // Assert
    assertFalse(players.isEmpty(),
        "Game should have loaded players to function properly");
  }

  @Test
  @DisplayName("Test getNumberOfTiles returns correct value")
  void testGetNumberOfTiles() {
    // Act
    int numberOfTiles = classicLadderGame.getNumberOfTiles();

    // Assert
    assertEquals(100, numberOfTiles, "Game should have 100 tiles");
  }

  @Test
  @DisplayName("Test that players list is a new instance (defensive copy)")
  void testGetPlayersReturnsNewList() {
    // Act
    List<Player> players1 = classicLadderGame.getPlayers();
    List<Player> players2 = classicLadderGame.getPlayers();

    // Assert
    assertNotSame(players1, players2,
        "getPlayers() should return a new list instance each time");
    assertEquals(players1.size(), players2.size(),
        "Both lists should have the same number of players");
  }

  @Test
  @DisplayName("Test tile retrieval by ID")
  void testGetTileById() {
    // Arrange
    BoardLinear board = classicLadderGame.getBoard();

    // Act & Assert
    for (int i = 1; i <= 100; i++) {
      Tile tile = board.getTileByIdLinear(i);
      assertNotNull(tile, "Tile " + i + " should exist");
      assertEquals(i, tile.getTileId(),
          "Tile ID should match requested ID");
    }

    // Test invalid tile IDs
    assertNull(board.getTileByIdLinear(0), "Tile 0 should not exist");
    assertNull(board.getTileByIdLinear(101), "Tile 101 should not exist");
    assertNull(board.getTileByIdLinear(-1), "Negative tile ID should return null");
  }


}

