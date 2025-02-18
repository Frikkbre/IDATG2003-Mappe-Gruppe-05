package edu.ntnu.idi.bidata.idatg2003mappe.map;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TestTile {

  private Tile tile1;
  private Tile tile2;
  private Tile tile3;

  @BeforeEach
  void setup() {
    tile1 = new Tile(1);
    tile2 = new Tile(2);
    tile3 = new Tile(3);
  }

  @AfterEach
  void tearDown() {
    tile1 = null;
    tile2 = null;
    tile3 = null;
  }

  @Test void setNextTile() {
    tile1.setNextTile(tile2);
    assertEquals(tile2, tile1.getNextTile());
  }

  @Test void negativeSetNextTile() {
    tile1.setNextTile(tile2);
    assertNotEquals(tile1, tile1.getNextTile());
  }

  @Test void setPreviousTile() {
    tile2.setPreviousTile(tile1);
    assertEquals(tile1, tile2.getPreviousTile());
  }

  @Test void negativeSetPreviousTile() {
    tile2.setPreviousTile(tile1);
    assertNotEquals(tile2, tile2.getPreviousTile());
  }

  @Test
  void testAddTiles() {
    tile1.addTileToTileBranch(tile2);
    tile1.addTileToTileBranch(tile3);
    assertEquals(2, tile1.getNextTiles().size());
    assertTrue(tile1.getNextTiles().contains(tile2));
    assertTrue(tile1.getNextTiles().contains(tile3));
  }

  @Test
  void testAddDifferentTilePath() {
    tile1.addTileToTileBranch(tile2);
    tile1.addTileToTileBranch(tile3);
    tile2.addTileToTileBranch(tile1);
    assertEquals(2, tile1.getNextTiles().size());
    assertEquals(1, tile2.getNextTiles().size());
    assertTrue(tile1.getNextTiles().contains(tile2));
    assertTrue(tile1.getNextTiles().contains(tile3));
    assertTrue(tile2.getNextTiles().contains(tile1));
  }

  @Test
  void negativeTestAddTiles() {
    tile1.addTileToTileBranch(tile2);
    tile1.addTileToTileBranch(tile3);
    assertEquals(2, tile1.getNextTiles().size());
    assertFalse(tile1.getNextTiles().contains(tile1));
  }

  @Test
  void testConstructorAndGetTileId() {
    assertEquals(1, tile1.getTileId());
    assertEquals(2, tile2.getTileId());
    assertEquals(3, tile3.getTileId());
  }

  @Test
  void testSetAndGetNextTile() {
    tile1.setNextTile(tile2);
    assertEquals(tile2, tile1.getNextTile());
  }

  @Test
  void testSetAndGetPreviousTile() {
    tile2.setPreviousTile(tile1);
    assertEquals(tile1, tile2.getPreviousTile());
  }

  @Test
  void testSetAndGetDestinationTile() {
    tile1.setDestinationTile(tile3);
    assertEquals(tile3, tile1.getDestinationTile());
  }
}
