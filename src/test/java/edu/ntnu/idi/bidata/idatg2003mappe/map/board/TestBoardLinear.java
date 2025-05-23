package edu.ntnu.idi.bidata.idatg2003mappe.map.board;

import static org.junit.jupiter.api.Assertions.*;

import edu.ntnu.idi.bidata.idatg2003mappe.map.Tile;
import org.junit.jupiter.api.*;

import java.util.List;

/**
 * Comprehensive test class for BoardLinear following AAA pattern.
 * Tests linear board functionality, tile retrieval, and inheritance from Board.
 * Includes both positive and negative test cases as required for A grade.
 *
 * @author Simen Gudbrandsen and Frikk Breadsroed
 * @version 1.0.0
 * @since 23.05.2025
 */
@DisplayName("BoardLinear Test Suite")
class TestBoardLinear {

  private BoardLinear boardLinear;
  private Tile tile1;
  private Tile tile2;
  private Tile tile3;
  private Tile tile4;
  private Tile tile5;

  @BeforeAll
  static void setUpClass() {
    System.out.println("Starting BoardLinear test suite...");
  }

  @AfterAll
  static void tearDownClass() {
    System.out.println("BoardLinear test suite completed.");
  }

  @BeforeEach
  void setUp() {
    // Arrange - Set up fresh instances for each test
    boardLinear = new BoardLinear();
    tile1 = new Tile(1);
    tile2 = new Tile(2);
    tile3 = new Tile(3);
    tile4 = new Tile(4);
    tile5 = new Tile(5);
  }

  @AfterEach
  void tearDown() {
    // Clean up after each test
    boardLinear = null;
    tile1 = null;
    tile2 = null;
    tile3 = null;
    tile4 = null;
    tile5 = null;
  }

  // ==================== Constructor Tests ====================

  @Test
  @DisplayName("Should inherit Board functionality correctly")
  void testInheritance_BoardFunctionality_Works() {
    // Arrange - boardLinear is a Board instance

    // Act
    boardLinear.addTileToBoard(tile1);

    // Assert
    assertTrue(boardLinear instanceof Board, "BoardLinear should be instance of Board");
    assertEquals(tile1, boardLinear.getStartTile(), "Should inherit getStartTile from Board");
    assertEquals(tile1, boardLinear.getTileById(1), "Should inherit getTileById from Board");
  }

  // ==================== getTileByIdLinear Tests (Positive) ====================

  @Test
  @DisplayName("Should find tile by ID in linear board")
  void testGetTileByIdLinear_ExistingTile_ReturnsCorrectTile() {
    // Arrange
    boardLinear.addMultipleTilesToBoard(tile1, tile2, tile3);

    // Act
    Tile foundTile = boardLinear.getTileByIdLinear(2);

    // Assert
    assertEquals(tile2, foundTile, "Should return tile2 for ID 2");
    assertEquals(2, foundTile.getTileId(), "Returned tile should have correct ID");
  }

  @Test
  @DisplayName("Should find first tile by ID")
  void testGetTileByIdLinear_FirstTile_Success() {
    // Arrange
    boardLinear.addMultipleTilesToBoard(tile1, tile2, tile3);

    // Act
    Tile foundTile = boardLinear.getTileByIdLinear(1);

    // Assert
    assertEquals(tile1, foundTile, "Should return first tile");
    assertEquals(boardLinear.getStartTile(), foundTile, "Should be same as start tile");
  }

  @Test
  @DisplayName("Should find last tile by ID")
  void testGetTileByIdLinear_LastTile_Success() {
    // Arrange
    boardLinear.addMultipleTilesToBoard(tile1, tile2, tile3);

    // Act
    Tile foundTile = boardLinear.getTileByIdLinear(3);

    // Assert
    assertEquals(tile3, foundTile, "Should return last tile");
    assertEquals(3, foundTile.getTileId(), "Should have correct ID");
  }

  @Test
  @DisplayName("Should handle single tile board")
  void testGetTileByIdLinear_SingleTile_Success() {
    // Arrange
    boardLinear.addTileToBoard(tile1);

    // Act
    Tile foundTile = boardLinear.getTileByIdLinear(1);

    // Assert
    assertEquals(tile1, foundTile, "Should return the only tile");
    assertEquals(boardLinear.getStartTile(), foundTile, "Should be the start tile");
  }

  // ==================== getTileByIdLinear Tests (Negative) ====================

  @Test
  @DisplayName("Should return null for non-existent tile ID")
  void testGetTileByIdLinear_NonExistentId_ReturnsNull() {
    // Arrange
    boardLinear.addMultipleTilesToBoard(tile1, tile2, tile3);

    // Act & Assert
    assertNull(boardLinear.getTileByIdLinear(999), "Should return null for non-existent ID");
    assertNull(boardLinear.getTileByIdLinear(0), "Should return null for ID 0");
    assertNull(boardLinear.getTileByIdLinear(-1), "Should return null for negative ID");
  }

  @Test
  @DisplayName("Should handle extreme ID values appropriately")
  void testGetTileByIdLinear_ExtremeValues_ReturnsNull() {
    // Arrange
    boardLinear.addTileToBoard(tile1);

    // Act & Assert
    assertNull(boardLinear.getTileByIdLinear(Integer.MAX_VALUE),
        "Should return null for max integer");
    assertNull(boardLinear.getTileByIdLinear(Integer.MIN_VALUE),
        "Should return null for min integer");
  }

  // ==================== getTiles Tests ====================

  @Test
  @DisplayName("Should return tiles list with single tile")
  void testGetTiles_SingleTile_ReturnsListWithOne() {
    // Arrange
    boardLinear.addTileToBoard(tile1);

    // Act
    List<Tile> tiles = boardLinear.getTiles();

    // Assert
    assertEquals(1, tiles.size(), "Should have exactly one tile");
    assertEquals(tile1, tiles.get(0), "Should contain the added tile");
  }

  // ==================== Edge Case Tests ====================

  @Test
  @DisplayName("Should handle tiles with duplicate IDs")
  void testGetTileByIdLinear_DuplicateIds_ReturnsFirst() {
    // Arrange
    Tile duplicateTile = new Tile(2); // Same ID as tile2
    boardLinear.addMultipleTilesToBoard(tile1, tile2, duplicateTile);

    // Act
    Tile foundTile = boardLinear.getTileByIdLinear(2);

    // Assert
    assertEquals(tile2, foundTile, "Should return first tile with matching ID");
    assertNotEquals(duplicateTile, foundTile, "Should not return the duplicate");
  }

  @Test
  @DisplayName("Should handle large number of tiles efficiently")
  void testGetTileByIdLinear_LargeNumberOfTiles_PerformanceTest() {
    // Arrange
    final int LARGE_NUMBER = 1000;
    for (int i = 1; i <= LARGE_NUMBER; i++) {
      boardLinear.addTileToBoard(new Tile(i));
    }

    // Act & Assert
    long startTime = System.nanoTime();
    Tile foundTile = boardLinear.getTileByIdLinear(LARGE_NUMBER);
    long endTime = System.nanoTime();

    assertNotNull(foundTile, "Should find tile even with large number of tiles");
    assertEquals(LARGE_NUMBER, foundTile.getTileId(), "Should find correct tile");

    // Performance assertion (should complete in reasonable time)
    long duration = endTime - startTime;
    assertTrue(duration < 1_000_000_000, // 1 second in nanoseconds
        "Search should complete in reasonable time even with 1000 tiles");
  }

  @Test
  @DisplayName("Should maintain consistency between inherited and specific methods")
  void testMethodConsistency_InheritedVsSpecific_SameResults() {
    // Arrange
    boardLinear.addMultipleTilesToBoard(tile1, tile2, tile3);

    // Act
    Tile inheritedResult = boardLinear.getTileById(2);
    Tile specificResult = boardLinear.getTileByIdLinear(2);

    // Assert
    assertEquals(inheritedResult, specificResult,
        "Inherited getTileById and getTileByIdLinear should return same result");
    assertSame(inheritedResult, specificResult,
        "Should return same instance, not just equal objects");
  }

  // ==================== Integration Tests ====================

  @Test
  @DisplayName("Should work correctly with connected tiles")
  void testIntegration_ConnectedTiles_Success() {
    // Arrange
    tile1.setNextTile(tile2);
    tile2.setNextTile(tile3);
    tile3.setNextTile(tile4);
    boardLinear.addMultipleTilesToBoard(tile1, tile2, tile3, tile4);

    // Act
    Tile foundTile2 = boardLinear.getTileByIdLinear(2);
    Tile foundTile3 = boardLinear.getTileByIdLinear(3);

    // Assert
    assertEquals(tile2, foundTile2, "Should find tile2");
    assertEquals(tile3, foundTile2.getNextTile(), "tile2 should connect to tile3");
    assertEquals(tile4, foundTile3.getNextTile(), "tile3 should connect to tile4");
  }

  @Test
  @DisplayName("Should support complete linear board operations")
  void testCompleteLinearBoardOperations_FullWorkflow_Success() {
    // Arrange & Act - Build a complete linear board
    boardLinear.addTileToBoard(tile1);
    assertEquals(1, boardLinear.getTiles().size(), "Should have 1 tile");

    boardLinear.addTileToBoard(tile2);
    assertEquals(2, boardLinear.getTiles().size(), "Should have 2 tiles");

    boardLinear.addMultipleTilesToBoard(tile3, tile4, tile5);
    assertEquals(5, boardLinear.getTiles().size(), "Should have 5 tiles total");

    // Assert - Verify all operations work correctly
    assertEquals(tile1, boardLinear.getStartTile(), "Start tile should be first added");

    for (int i = 1; i <= 5; i++) {
      Tile foundTile = boardLinear.getTileByIdLinear(i);
      assertNotNull(foundTile, "Should find tile with ID " + i);
      assertEquals(i, foundTile.getTileId(), "Found tile should have correct ID");
    }

    List<Tile> allTiles = boardLinear.getTiles();
    assertEquals(5, allTiles.size(), "Should return all 5 tiles");
    assertTrue(allTiles.contains(tile1), "Should contain tile1");
    assertTrue(allTiles.contains(tile5), "Should contain tile5");
  }

  @Test
  @DisplayName("Should handle mixed operations correctly")
  void testMixedOperations_AddAndRetrieve_ConsistentState() {
    // Arrange & Act - Mix single and multiple additions
    boardLinear.addTileToBoard(tile1);
    Tile retrieved1 = boardLinear.getTileByIdLinear(1);

    boardLinear.addMultipleTilesToBoard(tile2, tile3);
    Tile retrieved2 = boardLinear.getTileByIdLinear(2);
    Tile retrieved3 = boardLinear.getTileByIdLinear(3);

    boardLinear.addTileToBoard(tile4);
    Tile retrieved4 = boardLinear.getTileByIdLinear(4);

    // Assert
    assertEquals(tile1, retrieved1, "Should retrieve tile1 correctly");
    assertEquals(tile2, retrieved2, "Should retrieve tile2 correctly");
    assertEquals(tile3, retrieved3, "Should retrieve tile3 correctly");
    assertEquals(tile4, retrieved4, "Should retrieve tile4 correctly");

    assertEquals(4, boardLinear.getTiles().size(), "Should have 4 tiles total");
    assertEquals(tile1, boardLinear.getStartTile(), "Start tile should remain tile1");
  }
}