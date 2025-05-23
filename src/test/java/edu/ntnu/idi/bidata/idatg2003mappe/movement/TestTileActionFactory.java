package edu.ntnu.idi.bidata.idatg2003mappe.movement;

import static org.junit.jupiter.api.Assertions.*;

import edu.ntnu.idi.bidata.idatg2003mappe.entity.player.Player;
import edu.ntnu.idi.bidata.idatg2003mappe.map.Tile;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for the TileActionFactory class.
 * Tests the creation of various tile actions through the factory.
 *
 * @version 1.0.0
 * @since 1.0.0
 */
class TestTileActionFactory {

  private Tile sourceTile;
  private Tile destinationTile;
  private Player player;

  /**
   * Set up test environment before each test.
   */
  @BeforeEach
  void setUp() {
    sourceTile = new Tile(5);
    destinationTile = new Tile(10);
    player = new Player("TestPlayer", 0, "Blue", sourceTile);
  }

  /**
   * Tear down test environment after each test.
   */
  @AfterEach
  void tearDown() {
    sourceTile = null;
    destinationTile = null;
    player = null;
  }

  /**
   * Test that createLadderAction correctly creates a LadderAction.
   */
  @Test
  void testCreateLadderAction() {
    // Act
    TileAction action = TileActionFactory.createLadderAction(sourceTile, destinationTile);

    // Assert
    assertNotNull(action, "Created action should not be null");
    assertTrue(action instanceof LadderAction, "Created action should be a LadderAction");

    // Verify destination tile is set correctly on source tile
    assertEquals(destinationTile, sourceTile.getDestinationTile(),
        "Source tile should have destination tile set correctly");
  }

  /**
   * Test that the LadderAction created by the factory moves the player correctly.
   */
  @Test
  void testLadderActionMovesPlayer() {
    // Arrange
    TileAction action = TileActionFactory.createLadderAction(sourceTile, destinationTile);
    player.placePlayer(sourceTile);
    assertEquals(sourceTile, player.getCurrentTile(), "Player should initially be on source tile");

    // Act
    action.performAction(player);

    // Assert
    assertEquals(destinationTile, player.getCurrentTile(),
        "Player should be moved to destination tile after action is performed");
  }

  /**
   * Test that createLadderAction with null source tile throws exception.
   */
  @Test
  void testCreateLadderActionWithNullSourceTile() {
    // Act & Assert
    assertThrows(NullPointerException.class,
        () -> TileActionFactory.createLadderAction(null, destinationTile),
        "Creating ladder action with null source tile should throw NullPointerException");
  }

  /**
   * Test that multiple ladder actions can be created for different tile pairs.
   */
  @Test
  void testMultipleLadderActions() {
    // Arrange
    Tile sourceTile1 = new Tile(1);
    Tile destinationTile1 = new Tile(20);

    Tile sourceTile2 = new Tile(15);
    Tile destinationTile2 = new Tile(5);

    // Act
    TileAction action1 = TileActionFactory.createLadderAction(sourceTile1, destinationTile1);
    TileAction action2 = TileActionFactory.createLadderAction(sourceTile2, destinationTile2);

    // Assert
    assertNotNull(action1, "First action should not be null");
    assertNotNull(action2, "Second action should not be null");

    assertEquals(destinationTile1, sourceTile1.getDestinationTile(),
        "First source tile should have correct destination");
    assertEquals(destinationTile2, sourceTile2.getDestinationTile(),
        "Second source tile should have correct destination");
  }

  /**
   * Test that ladder action can handle both up and down movements.
   */
  @Test
  void testLadderActionUpAndDown() {
    // Arrange
    Tile startTile = new Tile(1);
    Tile upTile = new Tile(10);
    Tile downTile = new Tile(5);

    Player playerUp = new Player("PlayerUp", 0, "Red", startTile);
    Player playerDown = new Player("PlayerDown", 1, "Blue", startTile);

    // Create up ladder
    TileAction upAction = TileActionFactory.createLadderAction(startTile, upTile);
    playerUp.placePlayer(startTile);

    // Act - move up
    upAction.performAction(playerUp);

    // Assert - moved up
    assertEquals(upTile, playerUp.getCurrentTile(), "Player should move up the ladder");

    // Reset and create down ladder
    startTile.setDestinationTile(null); // Clear previous destination
    TileAction downAction = TileActionFactory.createLadderAction(startTile, downTile);
    playerDown.placePlayer(startTile);

    // Act - move down
    downAction.performAction(playerDown);

    // Assert - moved down
    assertEquals(downTile, playerDown.getCurrentTile(), "Player should move down the ladder");
  }

  /**
   * Test that the factory produces independent LadderAction instances.
   */
  @Test
  void testLadderActionInstancesAreIndependent() {
    // Arrange
    Tile sourceTile1 = new Tile(1);
    Tile destinationTile1 = new Tile(10);

    Tile sourceTile2 = new Tile(5);
    Tile destinationTile2 = new Tile(15);

    // Act
    TileAction action1 = TileActionFactory.createLadderAction(sourceTile1, destinationTile1);
    TileAction action2 = TileActionFactory.createLadderAction(sourceTile2, destinationTile2);

    // Assert
    assertNotSame(action1, action2, "Factory should create different instances");
  }

  /**
   * Test that the factory sets source tile's destination correctly even when destination is
   * already set.
   */
  @Test
  void testOverwriteExistingDestination() {
    // Arrange
    Tile originalDestination = new Tile(15);
    sourceTile.setDestinationTile(originalDestination);
    assertEquals(originalDestination, sourceTile.getDestinationTile(), "Source tile should initially point to original destination");

    // Act
    TileActionFactory.createLadderAction(sourceTile, destinationTile);

    // Assert
    assertEquals(destinationTile, sourceTile.getDestinationTile(), "Source tile's destination should be overwritten");
    assertNotEquals(originalDestination, sourceTile.getDestinationTile(), "Source tile should no longer point to original destination");
  }

  /**
   * Test that LadderAction implementation doesn't throw exceptions with valid inputs.
   */
  @Test
  void testLadderActionRobustness() {
    // Arrange
    TileAction action = TileActionFactory.createLadderAction(sourceTile, destinationTile);

    // Act & Assert - No exceptions should be thrown
    assertDoesNotThrow(() -> action.performAction(player),
        "LadderAction should not throw exceptions with valid inputs");
  }

  /**
   * Test behavior when attempting to create a ladder action with the same source and destination.
   */
  @Test
  void testCreateLadderActionWithSameSourceAndDestination() {
    // Arrange
    Tile tile = new Tile(7);

    // Act
    TileAction action = TileActionFactory.createLadderAction(tile, tile);

    // Assert
    assertNotNull(action, "Action should be created even with same source and destination");
    assertEquals(tile, tile.getDestinationTile(), "Tile should point to itself as destination");

    // Verify the action has no effect on player position
    player.placePlayer(tile);
    action.performAction(player);
    assertEquals(tile, player.getCurrentTile(), "Player should remain on the same tile");
  }
}