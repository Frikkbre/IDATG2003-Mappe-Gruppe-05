package edu.ntnu.idi.bidata.idatg2003mappe.map;

import static org.junit.jupiter.api.Assertions.*;

import edu.ntnu.idi.bidata.idatg2003mappe.map.board.BoardLinear;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TestBoardLinear {

  private BoardLinear board;

  @BeforeEach
  void setUp() {
    board = new BoardLinear();
  }

  @AfterEach
  void tearDown() {
    board = null;
  }

  @Test
  void testAddTile() {
    Tile tile = new Tile(1);
    board.addTileToBoard(tile);
    assertEquals(tile, board.getStartTile(), "Start tile should be the first tile added.");
  }

  @Test
  void testAddMultipleTiles() {
    Tile tile1 = new Tile(1);
    Tile tile2 = new Tile(2);
    Tile tile3 = new Tile(3);
    board.addMultipleTilesToBoard(tile1, tile2, tile3);
    assertEquals(tile1, board.getStartTile(), "Start tile should be the first tile added.");
  }

  @Test
  void testGetStartTileWhenEmpty() {
    assertNull(board.getStartTile(), "Start tile should be null when no tile is added.");
  }

}
