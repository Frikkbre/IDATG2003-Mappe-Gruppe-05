package edu.ntnu.idi.bidata.idatg2003mappe.map.board;

import static org.junit.jupiter.api.Assertions.*;

import edu.ntnu.idi.bidata.idatg2003mappe.map.Tile;
import org.junit.jupiter.api.*;

import java.util.List;

/**
 * Comprehensive test class for BoardBranching following AAA pattern.
 * Tests board initialization, tile management, connections, and branching functionality.
 * Includes both positive and negative test cases as required for A grade.
 * 
 * @author Simen Gudbrandsen and Frikk Breadsroed
 * @version 1.0.0
 * @since 23.5.2025
 */
@DisplayName("BoardBranching Test Suite")
class TestBoardBranching {

  private BoardBranching board;
  private Tile tile1;
  private Tile tile2;
  private Tile tile3;
  private Tile tile4;
  private Tile tile5;

  @BeforeAll
  static void setUpClass() {
    System.out.println("Starting BoardBranching test suite...");
  }

  @AfterAll
  static void tearDownClass() {
    System.out.println("BoardBranching test suite completed.");
  }

  @BeforeEach
  void setUp() {
    // Arrange - Set up fresh instances for each test
    board = new BoardBranching();

    // Create test tiles with different IDs
    tile1 = new Tile(1);
    tile2 = new Tile(2);
    tile3 = new Tile(3);
    tile4 = new Tile(4);
    tile5 = new Tile(5);
  }

  @AfterEach
  void tearDown() {
    // Clean up after each test
    board = null;
    tile1 = null;
    tile2 = null;
    tile3 = null;
    tile4 = null;
    tile5 = null;
  }

  // ==================== Board Initialization Tests ====================

  @Test
  @DisplayName("Should initialize with default board name 'Missing Diamond'")
  void testBoardInitialization_DefaultName() {
    // Act - board already created in setUp()

    // Assert
    assertNotNull(board, "Board should be initialized");
    assertEquals("Missing Diamond", board.getBoardName(), // Changed from setBoardName()
        "Default board name should be 'Missing Diamond'");
  }

  @Test
  @DisplayName("Should have null start tile initially")
  void testBoardInitialization_NullStartTile() {
    // Act
    Tile startTile = board.getStartTile();

    // Assert
    assertNull(startTile, "Start tile should be null initially");
  }

  // ==================== Board Name Tests ====================

  @Test
  @DisplayName("Should successfully set custom board name")
  void testSetBoardName_ValidName() {
    // Arrange
    String customName = "Custom Adventure Map";

    // Act
    board.setBoardName(customName);

    // Assert
    assertEquals(customName, board.getBoardName(),
        "Board name should be updated to custom name");
  }

  @Test
  @DisplayName("Should handle empty string as board name")
  void testSetBoardName_EmptyString() {
    // Arrange
    String emptyName = "";

    // Act
    board.setBoardName(emptyName);

    // Assert
    assertEquals(emptyName, board.getBoardName(),
        "Board should accept empty string as name");
  }

  @Test
  @DisplayName("Should handle null board name")
  void testSetBoardName_NullName() {
    // Arrange
    String nullName = null;

    // Act
    board.setBoardName(nullName);

    // Assert
    assertNull(board.getBoardName(), "Board should accept null name");
  }

  // ==================== Tile Addition Tests ====================

  @Test
  @DisplayName("Should successfully add single tile to board")
  void testAddTileToBoard_SingleTile() {
    // Act
    board.addTileToBoard(tile1);

    // Assert
    List<Tile> tiles = board.getTiles();
    assertEquals(1, tiles.size(), "Board should have exactly one tile");
    assertTrue(tiles.contains(tile1), "Board should contain the added tile");
    assertEquals(tile1, board.getStartTile(), "First tile should become start tile");
    assertEquals(tile1, board.getTileById(1), "Should retrieve tile by its ID");
  }

  @Test
  @DisplayName("Should successfully add multiple tiles to board")
  void testAddTileToBoard_MultipleTiles() {
    // Act
    board.addTileToBoard(tile1);
    board.addTileToBoard(tile2);
    board.addTileToBoard(tile3);

    // Assert
    List<Tile> tiles = board.getTiles();
    assertEquals(3, tiles.size(), "Board should have three tiles");
    assertTrue(tiles.contains(tile1), "Board should contain tile1");
    assertTrue(tiles.contains(tile2), "Board should contain tile2");
    assertTrue(tiles.contains(tile3), "Board should contain tile3");
    assertEquals(tile1, board.getStartTile(), "Start tile should remain the first tile added");
  }

  @Test
  @DisplayName("Should maintain tile order when adding multiple tiles")
  void testAddTileToBoard_TileOrder() {
    // Act
    board.addTileToBoard(tile3);
    board.addTileToBoard(tile1);
    board.addTileToBoard(tile2);

    // Assert
    List<Tile> tiles = board.getTiles();
    assertEquals(tile3, tiles.get(0), "First tile should be tile3");
    assertEquals(tile1, tiles.get(1), "Second tile should be tile1");
    assertEquals(tile2, tiles.get(2), "Third tile should be tile2");
    assertEquals(tile3, board.getStartTile(), "Start tile should be the first tile added");
  }

  @Test
  @DisplayName("Should handle adding duplicate tiles")
  void testAddTileToBoard_DuplicateTiles() {
    // Act
    board.addTileToBoard(tile1);
    board.addTileToBoard(tile1); // Add same tile again

    // Assert
    List<Tile> tiles = board.getTiles();
    assertEquals(2, tiles.size(), "Board should contain tile twice if added twice");
    assertEquals(tile1, tiles.get(0), "First position should be tile1");
    assertEquals(tile1, tiles.get(1), "Second position should also be tile1");
  }

  // ==================== Tile Retrieval by ID Tests ====================

  @Test
  @DisplayName("Should successfully retrieve tile by existing ID")
  void testGetTileById_ExistingId() {
    // Arrange
    board.addTileToBoard(tile1);
    board.addTileToBoard(tile2);
    board.addTileToBoard(tile3);

    // Act & Assert
    assertEquals(tile1, board.getTileById(1), "Should retrieve tile1 by ID 1");
    assertEquals(tile2, board.getTileById(2), "Should retrieve tile2 by ID 2");
    assertEquals(tile3, board.getTileById(3), "Should retrieve tile3 by ID 3");
  }

  // ==================== Inheritance Tests ====================

  @Test
  @DisplayName("Should inherit base Board functionality correctly")
  void testInheritance_BaseClassFunctionality() {
    // Arrange & Act
    board.addTileToBoard(tile1);
    board.addTileToBoard(tile2);

    // Assert - Test inherited methods from Board class
    assertEquals(tile1, board.getStartTile(),
        "Should inherit getStartTile functionality");

    List<Tile> tiles = board.getTiles();
    assertNotNull(tiles, "Should inherit tiles list");
    assertEquals(2, tiles.size(), "Should maintain tile count");
  }

  @Test
  @DisplayName("Should extend base Board with branching-specific functionality")
  void testBranchingSpecificFunctionality() {
    // Arrange
    board.addTileToBoard(tile1);
    board.addTileToBoard(tile2);

    // Act - Use branching-specific methods
    board.connectTiles(tile1, tile2);
    board.setBoardName("Branching Board");

    // Assert - Verify branching-specific functionality
    assertEquals("Branching Board", board.getBoardName(),
        "Should have branching-specific board name functionality");
    assertEquals(tile2, board.getTileById(2),
        "Should have branching-specific tile retrieval by ID");
    assertTrue(tile1.getNextTiles().contains(tile2),
        "Should have branching-specific tile connection functionality");
  }

  // ==================== Performance Tests ====================

  @Test
  @DisplayName("Should perform tile retrieval efficiently with many tiles")
  void testPerformance_TileRetrieval() {
    // Arrange
    final int TILE_COUNT = 1000;
    for (int i = 1; i <= TILE_COUNT; i++) {
      board.addTileToBoard(new Tile(i));
    }

    // Act & Assert - Multiple retrievals should be reasonably fast
    long startTime = System.currentTimeMillis();

    for (int i = 1; i <= TILE_COUNT; i++) {
      assertNotNull(board.getTileById(i),
          "Should find tile " + i);
    }

    long endTime = System.currentTimeMillis();
    long duration = endTime - startTime;

    // Allow reasonable time for retrieval operations (adjust as needed)
    assertTrue(duration < 5000,
        "Tile retrieval should be reasonably fast even with many tiles");
  }
}
