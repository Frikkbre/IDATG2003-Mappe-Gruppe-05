package edu.ntnu.idi.bidata.idatg2003mappe.map;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class Tile {

  private Tile tile1;
  private Tile tile2;

  @BeforeEach
  void setUp() {
    tile1 = new Tile();
    tile2 = new Tile();
  }

  @AfterEach
  void tearDown() {
    tile1 = new Tile(null);
    tile2 = new Tile(null);
  }

  @Test
  void testSetNextTile() {



  }




}
