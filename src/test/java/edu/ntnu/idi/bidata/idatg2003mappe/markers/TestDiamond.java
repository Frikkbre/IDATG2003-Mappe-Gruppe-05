package edu.ntnu.idi.bidata.idatg2003mappe.markers;

import static org.junit.jupiter.api.Assertions.*;

import edu.ntnu.idi.bidata.idatg2003mappe.map.Tile;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

class TestDiamond {

  private Diamond diamond;
  private Tile testTile;

  @BeforeEach
  void setUp() {
    diamond = new Diamond();
    testTile = new Tile(1);
  }

  @AfterEach
  void tearDown() {
    diamond = null;
    testTile = null;
  }

  @Test
  @DisplayName("Diamond should be missing by default")
  void testDiamondIsMissingByDefault() {
    // Assert
    assertTrue(diamond.isMissing(), "Diamond should be missing by default");
  }

  @Test
  @DisplayName("Setting diamond as not missing should update state")
  void testSetMissing() {
    // Act
    diamond.setMissing(false);

    // Assert
    assertFalse(diamond.isMissing(), "Diamond should not be missing after setMissing(false)");
  }

  @Test
  @DisplayName("Finding diamond should set it as not missing")
  void testFindDiamond() {
    // Act
    diamond.find();

    // Assert
    assertFalse(diamond.isMissing(), "Diamond should not be missing after being found");
  }

  @Test
  @DisplayName("Finding diamond should also reveal it")
  void testFindDiamondRevealsIt() {
    // Arrange
    assertFalse(diamond.isRevealed(), "Diamond should not be revealed initially");

    // Act
    diamond.find();

    // Assert
    assertTrue(diamond.isRevealed(), "Diamond should be revealed after being found");
  }

  @Test
  @DisplayName("Diamond should have the correct type")
  void testDiamondType() {
    // Assert
    assertEquals("Diamond", diamond.getType(), "Diamond should have type 'Diamond'");
  }

  @Test
  @DisplayName("Diamond should have the correct value")
  void testDiamondValue() {
    // Assert
    assertEquals(2000, diamond.getValue(), "Diamond should have value 2000");
  }

  @Test
  @DisplayName("Diamond can be placed on a tile")
  void testSetLocation() {
    // Act
    diamond.setLocation(testTile);

    // Assert
    assertEquals(testTile, diamond.getLocation(), "Diamond should be on the correct tile");
  }

  @Test
  @DisplayName("Diamond can be removed from a tile")
  void testRemoveFromLocation() {
    // Arrange
    diamond.setLocation(testTile);

    // Act
    diamond.removeFromLocation();

    // Assert
    assertNull(diamond.getLocation(), "Diamond should have no location after removal");
  }
}