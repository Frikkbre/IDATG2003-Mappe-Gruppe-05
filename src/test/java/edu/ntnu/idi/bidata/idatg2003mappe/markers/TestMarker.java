package edu.ntnu.idi.bidata.idatg2003mappe.markers;

import static org.junit.jupiter.api.Assertions.*;

import edu.ntnu.idi.bidata.idatg2003mappe.map.Tile;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

class TestMarker {

  // Since Marker is abstract, we'll use a concrete implementation for testing
  private Marker marker;
  private Tile testTile;

  @BeforeEach
  void setUp() {
    // Using RedGem as a concrete implementation of Marker
    marker = new RedGem();
    testTile = new Tile(1);
  }

  @AfterEach
  void tearDown() {
    marker = null;
    testTile = null;
  }

  @Test
  @DisplayName("Marker should not be revealed by default")
  void testMarkerNotRevealedByDefault() {
    // Assert
    assertFalse(marker.isRevealed(), "Marker should not be revealed by default");
  }

  @Test
  @DisplayName("Marker can be revealed")
  void testRevealMarker() {
    // Act
    marker.reveal();

    // Assert
    assertTrue(marker.isRevealed(), "Marker should be revealed after reveal() is called");
  }

  @Test
  @DisplayName("Marker can be hidden")
  void testHideMarker() {
    // Arrange
    marker.reveal();
    assertTrue(marker.isRevealed(), "Marker should be revealed for this test");

    // Act
    marker.hide();

    // Assert
    assertFalse(marker.isRevealed(), "Marker should be hidden after hide() is called");
  }

  @Test
  @DisplayName("Marker can be placed on a tile")
  void testSetLocation() {
    // Act
    marker.setLocation(testTile);

    // Assert
    assertEquals(testTile, marker.getLocation(), "Marker should be on the correct tile");
  }

  @Test
  @DisplayName("Marker can be removed from a tile")
  void testRemoveFromLocation() {
    // Arrange
    marker.setLocation(testTile);

    // Act
    marker.removeFromLocation();

    // Assert
    assertNull(marker.getLocation(), "Marker should have no location after removal");
  }

  @Test
  @DisplayName("Marker returns correct type")
  void testGetType() {
    // For RedGem, the type should be "RedGem"
    assertEquals("RedGem", marker.getType(), "Marker should return correct type");
  }

  @Test
  @DisplayName("Marker returns correct value")
  void testGetValue() {
    // For RedGem, the value should be 100
    assertEquals(100, marker.getValue(), "Marker should return correct value");
  }

  @Test
  @DisplayName("Test with multiple marker types")
  void testMultipleMarkerTypes() {
    // Arrange
    Marker redGem = new RedGem();
    Marker greenGem = new GreenGem();
    Marker yellowGem = new YellowGem();
    Marker diamond = new Diamond();
    Marker visa = new Visa();

    // Assert
    assertEquals("RedGem", redGem.getType(), "RedGem should have correct type");
    assertEquals("GreenGem", greenGem.getType(), "GreenGem should have correct type");
    assertEquals("YellowGem", yellowGem.getType(), "YellowGem should have correct type");
    assertEquals("Diamond", diamond.getType(), "Diamond should have correct type");
    assertEquals("Visa", visa.getType(), "Visa should have correct type");

    assertEquals(100, redGem.getValue(), "RedGem should have correct value");
    assertEquals(500, greenGem.getValue(), "GreenGem should have correct value");
    assertEquals(1000, yellowGem.getValue(), "YellowGem should have correct value");
    assertEquals(2000, diamond.getValue(), "Diamond should have correct value");
    assertEquals(1500, visa.getValue(), "Visa should have correct value");
  }
}