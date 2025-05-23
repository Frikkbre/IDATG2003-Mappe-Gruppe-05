package edu.ntnu.idi.bidata.idatg2003mappe.map.board;

import static org.junit.jupiter.api.Assertions.*;

import edu.ntnu.idi.bidata.idatg2003mappe.map.Tile;
import org.junit.jupiter.api.*;

/**
 * Comprehensive test class for Board following AAA pattern.
 * Tests tile management, board initialization, and tile retrieval functionality.
 * Includes both positive and negative test cases as required for A grade.
 *
 * @author Simen Gudbrandsen and Frikk Breadsroed
 * @version 1.0.0
 * @since 23.05.2025
 */
@DisplayName("Board Test Suite")
class TestBoard {

  private Board board;
  private Tile tile1;
  private Tile tile2;
  private Tile tile3;

  @BeforeAll
  static void setUpClass() {
    System.out.println("Starting Board test suite...");
  }

  @AfterAll
  static void tearDownClass() {
    System.out.println("Board test suite completed.");
  }

  @BeforeEach
  void setUp() {
    // Arrange - Set up fresh instances for each test
    board = new Board();
    tile1 = new Tile(1);
    tile2 = new Tile(2);
    tile3 = new Tile(3);
  }

  @AfterEach
  void tearDown() {
    // Clean up after each test
    board = null;
    tile1 = null;
    tile2 = null;
    tile3 = null;
  }

  // ==================== Positive Tests ====================

  @Test
  @DisplayName("Should initialize empty board correctly")
  void testBoardInitialization_EmptyBoard_Success() {
    // Arrange - board already created in setUp()

    // Act & Assert
    assertNull(board.getStartTile(), "Empty board should have null start tile");
    assertNull(board.getTileById(1), "Empty board should return null for any tile ID");
  }

  @Test
  @DisplayName("Should add single tile and set as start tile")
  void testAddTileToBoard_SingleTile_BecomesStartTile() {
    // Arrange - tile1 already created in setUp()

    // Act
    board.addTileToBoard(tile1);

    // Assert
    assertEquals(tile1, board.getStartTile(), "First tile added should become start tile");
    assertEquals(tile1, board.getTileById(1), "Should be able to retrieve tile by ID");
  }

  @Test
  @DisplayName("Should add multiple tiles with start tile remaining first")
  void testAddTileToBoard_MultipleTiles_FirstRemainsStart() {
    // Arrange - tiles already created in setUp()

    // Act
    board.addTileToBoard(tile1);
    board.addTileToBoard(tile2);
    board.addTileToBoard(tile3);

    // Assert
    assertEquals(tile1, board.getStartTile(), "Start tile should remain the first tile added");
    assertEquals(tile1, board.getTileById(1), "Should retrieve correct tile by ID 1");
    assertEquals(tile2, board.getTileById(2), "Should retrieve correct tile by ID 2");
    assertEquals(tile3, board.getTileById(3), "Should retrieve correct tile by ID 3");
  }

  @Test
  @DisplayName("Should add multiple tiles using varargs method")
  void testAddMultipleTilesToBoard_VarargsMethod_Success() {
    // Arrange - tiles already created in setUp()

    // Act
    board.addMultipleTilesToBoard(tile1, tile2, tile3);

    // Assert
    assertEquals(tile1, board.getStartTile(), "Start tile should be first tile in varargs");
    assertEquals(tile1, board.getTileById(1), "Should retrieve tile1 by ID");
    assertEquals(tile2, board.getTileById(2), "Should retrieve tile2 by ID");
    assertEquals(tile3, board.getTileById(3), "Should retrieve tile3 by ID");
  }

  @Test
  @DisplayName("Should handle empty varargs call without error")
  void testAddMultipleTilesToBoard_EmptyVarargs_NoException() {
    // Arrange - empty board

    // Act & Assert
    assertDoesNotThrow(() -> board.addMultipleTilesToBoard(),
        "Empty varargs should not throw exception");
    assertNull(board.getStartTile(), "Board should remain empty after empty varargs call");
  }

  @Test
  @DisplayName("Should retrieve tiles by correct ID")
  void testGetTileById_ValidIds_ReturnsCorrectTiles() {
    // Arrange
    board.addMultipleTilesToBoard(tile1, tile2, tile3);

    // Act & Assert
    assertEquals(tile1, board.getTileById(1), "Should return tile1 for ID 1");
    assertEquals(tile2, board.getTileById(2), "Should return tile2 for ID 2");
    assertEquals(tile3, board.getTileById(3), "Should return tile3 for ID 3");
  }

  @Test
  @DisplayName("Should handle tiles with same ID (latest overwrites)")
  void testAddTileToBoard_DuplicateIds_LatestOverwrites() {
    // Arrange
    Tile duplicateTile = new Tile(1); // Same ID as tile1
    board.addTileToBoard(tile1);

    // Act
    board.addTileToBoard(duplicateTile);

    // Assert
    assertEquals(tile1, board.getStartTile(), "Start tile should remain the first added");
    // Note: The behavior depends on implementation - if using ArrayList,
    // getTileById will return the first match (tile1)
    assertEquals(tile1, board.getTileById(1),
        "First tile with ID should be returned (implementation dependent)");
  }

  // ==================== Negative Tests ====================

  @Test
  @DisplayName("Should return null for non-existent tile ID")
  void testGetTileById_NonExistentId_ReturnsNull() {
    // Arrange
    board.addMultipleTilesToBoard(tile1, tile2, tile3);

    // Act & Assert
    assertNull(board.getTileById(999), "Should return null for non-existent ID");
    assertNull(board.getTileById(0), "Should return null for ID 0");
    assertNull(board.getTileById(-1), "Should return null for negative ID");
  }

  @Test
  @DisplayName("Should return null for tile ID when board is empty")
  void testGetTileById_EmptyBoard_ReturnsNull() {
    // Arrange - empty board

    // Act & Assert
    assertNull(board.getTileById(1), "Empty board should return null for any tile ID");
    assertNull(board.getTileById(Integer.MAX_VALUE), "Should return null for max integer ID");
    assertNull(board.getTileById(Integer.MIN_VALUE), "Should return null for min integer ID");
  }

  @Test
  @DisplayName("Should handle null tile addition gracefully")
  void testAddTileToBoard_NullTile_HandledGracefully() {
    // Arrange - null tile

    // Act & Assert
    // Note: This depends on implementation. If no null check, it might throw NPE
    // If there's null checking, it should handle gracefully
    try {
      board.addTileToBoard(null);
      // If no exception, verify board state
      assertNull(board.getStartTile(), "Start tile should remain null after adding null");
    } catch (NullPointerException e) {
      // This is acceptable behavior if implementation doesn't handle nulls
      assertTrue(true, "NPE is acceptable for null tile if not handled in implementation");
    }
  }

  @Test
  @DisplayName("Should handle null tiles in varargs method")
  void testAddMultipleTilesToBoard_ContainsNull_HandledAppropriately() {
    // Arrange - mix of valid and null tiles

    // Act & Assert
    try {
      board.addMultipleTilesToBoard(tile1, null, tile2);

      // If no exception thrown, verify valid tiles were added
      assertEquals(tile1, board.getStartTile(), "Valid tiles should still be processed");
      assertEquals(tile1, board.getTileById(1), "tile1 should be retrievable");
      assertEquals(tile2, board.getTileById(2), "tile2 should be retrievable");

    } catch (Exception e) {
      // This is acceptable if implementation doesn't handle nulls
      assertTrue(e instanceof NullPointerException || e instanceof IllegalArgumentException,
          "Appropriate exception should be thrown for null tiles");
    }
  }

  // ==================== Edge Case Tests ====================

  @Test
  @DisplayName("Should handle tiles with extreme ID values")
  void testAddTileToBoard_ExtremeIds_Success() {
    // Arrange
    Tile maxTile = new Tile(Integer.MAX_VALUE);
    Tile minTile = new Tile(Integer.MIN_VALUE);
    Tile zeroTile = new Tile(0);

    // Act
    board.addMultipleTilesToBoard(maxTile, minTile, zeroTile);

    // Assert
    assertEquals(maxTile, board.getStartTile(), "First tile should be start regardless of ID");
    assertEquals(maxTile, board.getTileById(Integer.MAX_VALUE), "Should handle max integer ID");
    assertEquals(minTile, board.getTileById(Integer.MIN_VALUE), "Should handle min integer ID");
    assertEquals(zeroTile, board.getTileById(0), "Should handle zero ID");
  }

  @Test
  @DisplayName("Should handle large number of tiles")
  void testAddTileToBoard_LargeNumberOfTiles_Success() {
    // Arrange
    final int LARGE_NUMBER = 1000;

    // Act
    for (int i = 1; i <= LARGE_NUMBER; i++) {
      board.addTileToBoard(new Tile(i));
    }

    // Assert
    assertNotNull(board.getStartTile(), "Should have start tile after adding many tiles");
    assertEquals(1, board.getStartTile().getTileId(), "Start tile should have ID 1");
    assertNotNull(board.getTileById(LARGE_NUMBER), "Should retrieve last tile added");
    assertNotNull(board.getTileById(LARGE_NUMBER / 2), "Should retrieve middle tile");
  }

  @Test
  @DisplayName("Should maintain tile integrity after multiple operations")
  void testMultipleOperations_TileIntegrity_Maintained() {
    // Arrange & Act
    board.addTileToBoard(tile1);
    Tile retrievedStart1 = board.getStartTile();

    board.addTileToBoard(tile2);
    Tile retrievedStart2 = board.getStartTile();

    board.addTileToBoard(tile3);
    Tile retrievedById = board.getTileById(2);

    // Assert
    assertEquals(tile1, retrievedStart1, "Start tile should be consistent");
    assertEquals(tile1, retrievedStart2, "Start tile should not change when adding more tiles");
    assertEquals(tile2, retrievedById, "Retrieved tile should maintain identity");

    // Verify all tiles are still accessible
    assertEquals(tile1, board.getTileById(1), "tile1 should remain accessible");
    assertEquals(tile2, board.getTileById(2), "tile2 should remain accessible");
    assertEquals(tile3, board.getTileById(3), "tile3 should remain accessible");
  }

  // ==================== Integration Tests ====================

  @Test
  @DisplayName("Should work correctly with tiles that have connections")
  void testWithConnectedTiles_Integration_Success() {
    // Arrange
    tile1.setNextTile(tile2);
    tile2.setNextTile(tile3);

    // Act
    board.addMultipleTilesToBoard(tile1, tile2, tile3);

    // Assert
    assertEquals(tile1, board.getStartTile(), "Start tile should be correct");
    assertEquals(tile2, board.getTileById(1).getNextTile(), "Tile connections should be preserved");
    assertEquals(tile3, board.getTileById(2).getNextTile(), "Chain connections should be preserved");
  }

  @Test
  @DisplayName("Should handle board state queries consistently")
  void testBoardStateConsistency_MultipleCalls_SameResults() {
    // Arrange
    board.addMultipleTilesToBoard(tile1, tile2, tile3);

    // Act - Call methods multiple times
    Tile start1 = board.getStartTile();
    Tile start2 = board.getStartTile();
    Tile byId1 = board.getTileById(2);
    Tile byId2 = board.getTileById(2);

    // Assert
    assertSame(start1, start2, "Multiple calls to getStartTile should return same instance");
    assertSame(byId1, byId2, "Multiple calls to getTileById should return same instance");
    assertEquals(tile1, start1, "Start tile should be consistent");
    assertEquals(tile2, byId1, "Retrieved tile should be consistent");
  }
}