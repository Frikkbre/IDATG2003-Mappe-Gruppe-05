package edu.ntnu.idi.bidata.idatg2003mappe.map.board;

import static org.junit.jupiter.api.Assertions.*;

import edu.ntnu.idi.bidata.idatg2003mappe.map.Tile;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for the LadderGameBoardFactory class.
 * Tests the creation of both classic and random ladder game boards.
 *
 * @version 1.0.0
 * @since 1.0.0
 */
class TestLadderGameBoardFactory {

  /**
   * Test that the createClassicLadderBoard method creates a board with exactly 100 tiles.
   */
  @Test
  void testClassicBoardHas100Tiles() {
    // Act
    BoardLinear board = LadderGameBoardFactory.createClassicLadderBoard();

    // Assert
    assertNotNull(board, "Board should not be null");
    assertEquals(100, board.getTiles().size(), "Classic board should have exactly 100 tiles");
  }

  /**
   * Test that the createRandomLadderBoard method creates a board with exactly 100 tiles.
   */
  @Test
  void testRandomBoardHas100Tiles() {
    // Act
    BoardLinear board = LadderGameBoardFactory.createRandomLadderBoard();

    // Assert
    assertNotNull(board, "Board should not be null");
    assertEquals(100, board.getTiles().size(), "Random board should have exactly 100 tiles");
  }

  /**
   * Test that all tiles in the classic board are properly connected in sequence (nextTile).
   */
  @Test
  void testClassicBoardTilesAreConnectedInSequence() {
    // Act
    BoardLinear board = LadderGameBoardFactory.createClassicLadderBoard();

    // Assert
    Tile currentTile = board.getStartTile();
    assertEquals(1, currentTile.getTileId(), "First tile should have ID 1");

    for (int i = 2; i <= 100; i++) {
      Tile nextTile = currentTile.getNextTile();
      assertNotNull(nextTile, "Next tile should not be null for tile " + (i-1));
      assertEquals(i, nextTile.getTileId(), "Next tile should have ID " + i);
      currentTile = nextTile;
    }

    // The last tile should not have a next tile
    assertNull(currentTile.getNextTile(), "The last tile should not have a next tile");
  }

  /**
   * Test that all tiles in the random board are properly connected in sequence.
   */
  @Test
  void testRandomBoardTilesAreConnectedInSequence() {
    // Act
    BoardLinear board = LadderGameBoardFactory.createRandomLadderBoard();

    // Assert
    Tile currentTile = board.getStartTile();
    assertEquals(1, currentTile.getTileId(), "First tile should have ID 1");

    for (int i = 2; i <= 100; i++) {
      Tile nextTile = currentTile.getNextTile();
      assertNotNull(nextTile, "Next tile should not be null for tile " + (i-1));
      assertEquals(i, nextTile.getTileId(), "Next tile should have ID " + i);
      currentTile = nextTile;
    }

    // The last tile should not have a next tile
    assertNull(currentTile.getNextTile(), "The last tile should not have a next tile");
  }

  /**
   * Test that the classic board has the correct number of ladders (8 up and 8 down).
   */
  @Test
  void testClassicBoardHasCorrectNumberOfLadders() {
    // Act
    BoardLinear board = LadderGameBoardFactory.createClassicLadderBoard();

    // Assert
    int upLadderCount = 0;
    int downLadderCount = 0;

    for (Tile tile : board.getTiles()) {
      if (tile.getDestinationTile() != null) {
        if (tile.getDestinationTile().getTileId() > tile.getTileId()) {
          upLadderCount++;
        } else {
          downLadderCount++;
        }
      }
    }

    assertEquals(8, upLadderCount, "Classic board should have 8 up ladders");
    assertEquals(8, downLadderCount, "Classic board should have 8 down ladders");
  }

  /**
   * Test that the random board has some ladders.
   */
  @Test
  void testRandomBoardHasSomeLadders() {
    // Act
    BoardLinear board = LadderGameBoardFactory.createRandomLadderBoard();

    // Assert
    int ladderCount = 0;

    for (Tile tile : board.getTiles()) {
      if (tile.getDestinationTile() != null) {
        ladderCount++;
      }
    }

    assertTrue(ladderCount > 0, "Random board should have at least some ladders");
  }

  /**
   * Test that the classic board has correct specific ladder placements.
   */
  @Test
  void testClassicBoardHasCorrectLadderPlacements() {
    // Act
    BoardLinear board = LadderGameBoardFactory.createClassicLadderBoard();

    // Test a few specific ladder placements
    assertDestination(board, 1, 37, "Tile 2 should have ladder to tile 38");
    assertDestination(board, 4, 14, "Tile 5 should have ladder to tile 15");
    assertDestination(board, 9, 31, "Tile 10 should have ladder to tile 32");

    // Test a few specific snake placements
    assertDestination(board, 17, 7, "Tile 18 should have snake to tile 8");
    assertDestination(board, 61, 11, "Tile 62 should have snake to tile 12");
    assertDestination(board, 87, 36, "Tile 88 should have snake to tile 37");
  }

  /**
   * Test that the classic board has the correct skip turn effect on specific tiles.
   */
  @Test
  void testClassicBoardHasCorrectSkipTurnEffects() {
    // Act
    BoardLinear board = LadderGameBoardFactory.createClassicLadderBoard();

    // Check tiles that should have skipTurn effect
    int[] skipTurnTiles = {13, 25, 57, 70, 96};

    for (int tileId : skipTurnTiles) {
      Tile tile = board.getTileById(tileId);
      assertNotNull(tile, "Tile " + tileId + " should exist");
      assertEquals("skipTurn", tile.getEffect(), "Tile " + tileId + " should have skipTurn effect");
    }

    // Check a tile that should not have the skipTurn effect
    Tile normalTile = board.getTileById(50);
    assertNotNull(normalTile, "Tile 50 should exist");
    assertNotEquals("skipTurn", normalTile.getEffect(), "Tile 50 should not have skipTurn effect");
  }

  /**
   * Test that the classic board has the correct back to start effect on specific tiles.
   */
  @Test
  void testClassicBoardHasCorrectBackToStartEffect() {
    // Act
    BoardLinear board = LadderGameBoardFactory.createClassicLadderBoard();

    // Check the tile that should have backToStart effect
    Tile tile = board.getTileById(45);
    assertNotNull(tile, "Tile 45 should exist");
    assertEquals("backToStart", tile.getEffect(), "Tile 45 should have backToStart effect");
  }

  /**
   * Test that different calls to createRandomLadderBoard create different boards.
   */
  @Test
  void testRandomBoardsAreDifferent() {
    // Act
    BoardLinear board1 = LadderGameBoardFactory.createRandomLadderBoard();
    BoardLinear board2 = LadderGameBoardFactory.createRandomLadderBoard();

    // Assert
    // Collect ladder information from both boards
    boolean boardsAreDifferent = false;

    for (int i = 1; i <= 100; i++) {
      Tile tile1 = board1.getTileById(i);
      Tile tile2 = board2.getTileById(i);

      if (tile1.getDestinationTile() != null && tile2.getDestinationTile() != null) {
        // If both tiles have destinations but different ones
        if (tile1.getDestinationTile().getTileId() != tile2.getDestinationTile().getTileId()) {
          boardsAreDifferent = true;
          break;
        }
      } else if ((tile1.getDestinationTile() != null) != (tile2.getDestinationTile() != null)) {
        // If one tile has a destination and the other doesn't
        boardsAreDifferent = true;
        break;
      }
    }

    assertTrue(boardsAreDifferent, "Two random boards should be different from each other");
  }

  /**
   * Helper method to assert that a tile has the correct destination.
   */
  private void assertDestination(BoardLinear board, int sourceId, int destinationId, String message) {
    Tile sourceTile = board.getTileById(sourceId + 1); // +1 because IDs are 1-based
    assertNotNull(sourceTile, "Source tile " + (sourceId + 1) + " should exist");

    Tile destinationTile = sourceTile.getDestinationTile();
    assertNotNull(destinationTile, "Destination tile should not be null for source tile " + (sourceId + 1));
    assertEquals(destinationId + 1, destinationTile.getTileId(), message);
  }

  /**
   * Test that the classic board has consistent ladders across multiple calls.
   */
  @Test
  void testClassicBoardConsistency() {
    // Act
    BoardLinear board1 = LadderGameBoardFactory.createClassicLadderBoard();
    BoardLinear board2 = LadderGameBoardFactory.createClassicLadderBoard();

    // Assert
    for (int i = 1; i <= 100; i++) {
      Tile tile1 = board1.getTileById(i);
      Tile tile2 = board2.getTileById(i);

      // Compare destinations
      if (tile1.getDestinationTile() == null && tile2.getDestinationTile() == null) {
        // Both tiles have no destination - that's consistent
        continue;
      }

      assertNotNull(tile1.getDestinationTile(), "Tile " + i + " should have a destination in board 1");
      assertNotNull(tile2.getDestinationTile(), "Tile " + i + " should have a destination in board 2");
      assertEquals(
          tile1.getDestinationTile().getTileId(),
          tile2.getDestinationTile().getTileId(),
          "Destination of tile " + i + " should be consistent across classic boards"
      );
    }
  }

  /**
   * Test that setupTileEffects properly applies effects to tiles.
   */
  @Test
  void testSetupTileEffects() {
    // Arrange
    BoardLinear board = new BoardLinear();
    for (int i = 1; i <= 100; i++) {
      board.addTileToBoard(new Tile(i));
    }

    // Act
    LadderGameBoardFactory.setupTileEffects(board);

    // Assert
    // Check skipTurn effects
    int[] skipTurnTiles = {13, 25, 57, 70, 96};
    for (int tileId : skipTurnTiles) {
      Tile tile = board.getTileById(tileId);
      assertEquals("skipTurn", tile.getEffect(), "Tile " + tileId + " should have skipTurn effect");
    }

    // Check backToStart effect
    assertEquals("backToStart", board.getTileById(45).getEffect(), "Tile 45 should have backToStart effect");
  }
}