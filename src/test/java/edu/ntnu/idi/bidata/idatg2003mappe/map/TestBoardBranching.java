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
    assertEquals("Missing Diamond", board.getBoardName(),
        "Default board name should be 'Missing Diamond'");
  }

  @Test
  @DisplayName("Should initialize with empty tile collection")
  void testBoardInitialization_EmptyTiles() {
    // Act
    List<Tile> tiles = board.getTiles();

    // Assert
    assertNotNull(tiles, "Tiles list should not be null");
    assertTrue(tiles.isEmpty(), "Board should start with no tiles");
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

  @Test
  @DisplayName("Should handle adding null tile")
  void testAddTileToBoard_NullTile() {
    // Act & Assert
    assertDoesNotThrow(() -> board.addTileToBoard(null),
        "Adding null tile should not throw exception");

    List<Tile> tiles = board.getTiles();
    assertEquals(1, tiles.size(), "Board should have one entry");
    assertNull(tiles.get(0), "Entry should be null");
    assertNull(board.getStartTile(), "Start tile should be null");
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

  @Test
  @DisplayName("Should return null for non-existing tile ID")
  void testGetTileById_NonExistingId() {
    // Arrange
    board.addTileToBoard(tile1);
    board.addTileToBoard(tile2);

    // Act
    Tile result = board.getTileById(99);

    // Assert
    assertNull(result, "Should return null for non-existing tile ID");
  }

  @Test
  @DisplayName("Should return null when searching empty board")
  void testGetTileById_EmptyBoard() {
    // Arrange - empty board

    // Act
    Tile result = board.getTileById(1);

    // Assert
    assertNull(result, "Should return null when searching empty board");
  }

  @Test
  @DisplayName("Should handle negative tile ID")
  void testGetTileById_NegativeId() {
    // Arrange
    board.addTileToBoard(tile1);

    // Act
    Tile result = board.getTileById(-1);

    // Assert
    assertNull(result, "Should return null for negative tile ID");
  }

  @Test
  @DisplayName("Should handle zero tile ID")
  void testGetTileById_ZeroId() {
    // Arrange
    board.addTileToBoard(tile1);

    // Act
    Tile result = board.getTileById(0);

    // Assert
    assertNull(result, "Should return null for zero tile ID");
  }

  @Test
  @DisplayName("Should retrieve first matching tile when duplicates exist")
  void testGetTileById_DuplicateIds() {
    // Arrange
    Tile firstTile1 = new Tile(1);
    Tile secondTile1 = new Tile(1); // Same ID as first

    board.addTileToBoard(firstTile1);
    board.addTileToBoard(secondTile1);
    board.addTileToBoard(tile2);

    // Act
    Tile result = board.getTileById(1);

    // Assert
    assertEquals(firstTile1, result, "Should return first tile with matching ID");
    assertNotEquals(secondTile1, result, "Should not return second tile with same ID");
  }

  // ==================== Tile Connection Tests ====================

  @Test
  @DisplayName("Should successfully connect two tiles bidirectionally")
  void testConnectTiles_ValidTiles() {
    // Arrange
    board.addTileToBoard(tile1);
    board.addTileToBoard(tile2);

    // Act
    board.connectTiles(tile1, tile2);

    // Assert
    assertTrue(tile1.getNextTiles().contains(tile2),
        "Tile1 should have tile2 in its next tiles");
    assertTrue(tile2.getNextTiles().contains(tile1),
        "Tile2 should have tile1 in its next tiles");
  }

  @Test
  @DisplayName("Should create branching paths with multiple connections")
  void testConnectTiles_MultipleBranches() {
    // Arrange
    board.addTileToBoard(tile1);
    board.addTileToBoard(tile2);
    board.addTileToBoard(tile3);
    board.addTileToBoard(tile4);

    // Act - Create branching structure: tile1 connects to tile2 and tile3
    board.connectTiles(tile1, tile2);
    board.connectTiles(tile1, tile3);
    board.connectTiles(tile2, tile4);

    // Assert
    assertEquals(2, tile1.getNextTiles().size(),
        "Tile1 should have 2 connections");
    assertTrue(tile1.getNextTiles().contains(tile2),
        "Tile1 should connect to tile2");
    assertTrue(tile1.getNextTiles().contains(tile3),
        "Tile1 should connect to tile3");

    assertEquals(2, tile2.getNextTiles().size(),
        "Tile2 should have 2 connections");
    assertTrue(tile2.getNextTiles().contains(tile1),
        "Tile2 should connect back to tile1");
    assertTrue(tile2.getNextTiles().contains(tile4),
        "Tile2 should connect to tile4");
  }

  @Test
  @DisplayName("Should handle connecting tile to itself")
  void testConnectTiles_SelfConnection() {
    // Arrange
    board.addTileToBoard(tile1);

    // Act
    board.connectTiles(tile1, tile1);

    // Assert
    assertTrue(tile1.getNextTiles().contains(tile1),
        "Tile should be able to connect to itself");
    assertEquals(1, tile1.getNextTiles().size(),
        "Self-connected tile should have one connection");
  }

  @Test
  @DisplayName("Should handle duplicate connections gracefully")
  void testConnectTiles_DuplicateConnection() {
    // Arrange
    board.addTileToBoard(tile1);
    board.addTileToBoard(tile2);

    // Act
    board.connectTiles(tile1, tile2);
    board.connectTiles(tile1, tile2); // Connect again

    // Assert
    // Note: This depends on implementation - might add duplicate or ignore
    // Testing current behavior
    assertTrue(tile1.getNextTiles().contains(tile2),
        "Connection should still exist");
    assertTrue(tile2.getNextTiles().contains(tile1),
        "Reverse connection should still exist");
  }

  @Test
  @DisplayName("Should handle connecting null tiles")
  void testConnectTiles_NullTiles() {
    // Act & Assert
    assertDoesNotThrow(() -> board.connectTiles(null, tile1),
        "Should handle null first tile gracefully");
    assertDoesNotThrow(() -> board.connectTiles(tile1, null),
        "Should handle null second tile gracefully");
    assertDoesNotThrow(() -> board.connectTiles(null, null),
        "Should handle both null tiles gracefully");
  }

  // ==================== Complex Board Structure Tests ====================

  @Test
  @DisplayName("Should create complex branching network")
  void testComplexBranchingNetwork() {
    // Arrange
    board.addTileToBoard(tile1);
    board.addTileToBoard(tile2);
    board.addTileToBoard(tile3);
    board.addTileToBoard(tile4);
    board.addTileToBoard(tile5);

    // Act - Create diamond-shaped network
    board.connectTiles(tile1, tile2);
    board.connectTiles(tile1, tile3);
    board.connectTiles(tile2, tile4);
    board.connectTiles(tile3, tile4);
    board.connectTiles(tile4, tile5);

    // Assert
    assertEquals(2, tile1.getNextTiles().size(), "Tile1 should have 2 connections");
    assertEquals(2, tile2.getNextTiles().size(), "Tile2 should have 2 connections");
    assertEquals(2, tile3.getNextTiles().size(), "Tile3 should have 2 connections");
    assertEquals(3, tile4.getNextTiles().size(), "Tile4 should have 3 connections");
    assertEquals(1, tile5.getNextTiles().size(), "Tile5 should have 1 connection");

    // Verify specific connections
    assertTrue(tile1.getNextTiles().contains(tile2) && tile1.getNextTiles().contains(tile3),
        "Tile1 should connect to both tile2 and tile3");
    assertTrue(tile4.getNextTiles().contains(tile2) && tile4.getNextTiles().contains(tile3) && tile4.getNextTiles().contains(tile5),
        "Tile4 should be connected to tile2, tile3, and tile5");
  }

  @Test
  @DisplayName("Should maintain board integrity after multiple operations")
  void testBoardIntegrity_MultipleOperations() {
    // Arrange & Act - Perform multiple operations
    board.setBoardName("Test Adventure");
    board.addTileToBoard(tile1);
    board.addTileToBoard(tile2);
    board.addTileToBoard(tile3);
    board.connectTiles(tile1, tile2);
    board.connectTiles(tile2, tile3);

    // Assert - Verify all aspects are maintained
    assertEquals("Test Adventure", board.getBoardName(),
        "Board name should be maintained");
    assertEquals(3, board.getTiles().size(),
        "Board should have 3 tiles");
    assertEquals(tile1, board.getStartTile(),
        "Start tile should be maintained");
    assertEquals(tile1, board.getTileById(1),
        "Tile retrieval should work");
    assertEquals(tile2, board.getTileById(2),
        "Tile retrieval should work");
    assertEquals(tile3, board.getTileById(3),
        "Tile retrieval should work");

    // Verify connections are maintained
    assertTrue(tile1.getNextTiles().contains(tile2),
        "Connections should be maintained");
    assertTrue(tile2.getNextTiles().contains(tile1),
        "Reverse connections should be maintained");
    assertTrue(tile2.getNextTiles().contains(tile3),
        "All connections should be maintained");
  }

  // ==================== Edge Case Tests ====================

  @Test
  @DisplayName("Should handle large number of tiles")
  void testLargeNumberOfTiles() {
    // Arrange & Act
    for (int i = 1; i <= 100; i++) {
      board.addTileToBoard(new Tile(i));
    }

    // Assert
    assertEquals(100, board.getTiles().size(),
        "Board should handle large number of tiles");
    assertNotNull(board.getTileById(1),
        "Should retrieve first tile");
    assertNotNull(board.getTileById(50),
        "Should retrieve middle tile");
    assertNotNull(board.getTileById(100),
        "Should retrieve last tile");
    assertNull(board.getTileById(101),
        "Should return null for non-existing tile");
  }

  @Test
  @DisplayName("Should handle tiles with same ID correctly")
  void testTilesWithSameId() {
    // Arrange
    Tile duplicateIdTile1 = new Tile(5);
    Tile duplicateIdTile2 = new Tile(5);
    Tile differentIdTile = new Tile(10);

    // Act
    board.addTileToBoard(duplicateIdTile1);
    board.addTileToBoard(duplicateIdTile2);
    board.addTileToBoard(differentIdTile);

    // Assert
    assertEquals(3, board.getTiles().size(),
        "Board should contain all tiles even with duplicate IDs");
    assertEquals(duplicateIdTile1, board.getTileById(5),
        "Should return first tile with matching ID");
    assertEquals(differentIdTile, board.getTileById(10),
        "Should return tile with unique ID");
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