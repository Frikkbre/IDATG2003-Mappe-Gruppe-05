package edu.ntnu.idi.bidata.idatg2003mappe.entity;

import static org.junit.jupiter.api.Assertions.*;

import edu.ntnu.idi.bidata.idatg2003mappe.map.Tile;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TestPlayer {

  private Player player;
  private final Tile startingTile = new Tile(0);

  @BeforeEach
  void setUp() {
    player = new Player("1", startingTile, 1);
  }

  @AfterEach
  void tearDown() {
    player = null;
  }

  // Test if the player is placed on the correct tile.

  @Test
  void testMovePlayer_WithValidCurrentTile() {
    // Arrange: create a small chain of tiles
    Tile tile1 = new Tile(1);
    Tile tile2 = new Tile(2);
    Tile tile3 = new Tile(3);
    tile1.setNextTile(tile2);
    tile2.setNextTile(tile3);

    // Place the player on tile1
    player.placePlayer(tile1);

    // Act: move the player 2 steps
    player.movePlayer(2);

    // Assert: the player should now be on tile3
    assertEquals(tile3, player.getCurrentTile(),
        "Player should move from tile1 to tile3 in 2 steps.");
  }

  // Ensuring illegalArgumentException is thrown when moving player with negative steps

  @Test
  void testMovePlayer_CurrentTileNotSet_ThrowsException() {
    player.placePlayer(null);

    Player playerWithoutTile = new Player("1", null, 1);

    assertNull(playerWithoutTile.getCurrentTile(), "currentTile should be null for this test.");

    assertThrows(IllegalStateException.class, () -> playerWithoutTile.movePlayer(1),
        "Expected an IllegalStateException when currentTile is not set.");
  }

  // Test the setName method

  @Test
  void testSetName_ValidName() {
    // Act
    player.setName("test");

    // Assert
    assertEquals("test", player.getName(),
        "Player's name should be set to 'test'.");
  }

  // Test the setName method with an invalid name (Negative test)

  @Test
  void testSetName_BlankName_ThrowsException() {
    // Act & Assert
    assertThrows(IllegalArgumentException.class, () -> player.setName(""),
        "Expected an IllegalArgumentException when name is blank.");
  }

  @Test
  void testSetName_InvalidID_ThrowsException() {
    // Act & Assert
    assertThrows(IllegalArgumentException.class, () -> player.setID(-1),
        "Expected an IllegalArgumentException when ID is invalid.");
  }


}
