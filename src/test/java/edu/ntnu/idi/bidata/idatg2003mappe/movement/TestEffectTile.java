package edu.ntnu.idi.bidata.idatg2003mappe.movement;

import static org.junit.jupiter.api.Assertions.*;

import edu.ntnu.idi.bidata.idatg2003mappe.entity.player.Player;
import edu.ntnu.idi.bidata.idatg2003mappe.map.Tile;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for the EffectTile class.
 * Tests various types of tile effects and ensures they perform correctly on players.
 *
 * @version 1.0.0
 * @since 1.0.0
 */
class TestEffectTile {

  private Player player;
  private Tile currentTile;
  private Tile startTile;
  private EffectTile effectTile;

  /**
   * Set up test environment before each test.
   */
  @BeforeEach
  void setUp() {
    startTile = new Tile(1);
    currentTile = new Tile(5);
    player = new Player("TestPlayer", 0, "Blue", startTile);
    player.placePlayer(currentTile); // Place player on the current tile
  }

  /**
   * Tear down test environment after each test.
   */
  @AfterEach
  void tearDown() {
    player = null;
    currentTile = null;
    startTile = null;
    effectTile = null;
  }

  /**
   * Test that skip turn effect correctly sets player's skip turn status to true.
   */
  @Test
  void testSkipTurnEffect() {
    // Arrange
    effectTile = new EffectTile(currentTile, "skipTurn", startTile);
    assertFalse(player.isSkipTurn(), "Player should not be set to skip turn before effect is applied");

    // Act
    effectTile.performAction(player);

    // Assert
    assertTrue(player.isSkipTurn(), "Player should be set to skip turn after effect is applied");
  }

  /**
   * Test that back to start effect correctly moves player back to start tile.
   */
  @Test
  void testBackToStartEffect() {
    // Arrange
    effectTile = new EffectTile(currentTile, "backToStart", startTile);
    assertEquals(currentTile, player.getCurrentTile(), "Player should be on current tile before effect is applied");

    // Act
    effectTile.performAction(player);

    // Assert
    assertEquals(startTile, player.getCurrentTile(), "Player should be moved back to start tile after effect is applied");
  }

  /**
   * Test that effect is not applied when effect type is null.
   */
  @Test
  void testNullEffectType() {
    // Arrange
    effectTile = new EffectTile(currentTile, null, startTile);
    Tile originalTile = player.getCurrentTile();
    boolean originalSkipStatus = player.isSkipTurn();

    // Act
    effectTile.performAction(player);

    // Assert
    assertEquals(originalTile, player.getCurrentTile(), "Player should remain on the same tile when effect type is null");
    assertEquals(originalSkipStatus, player.isSkipTurn(), "Player's skip turn status should remain unchanged when effect type is null");
  }

  /**
   * Test that unknown effect type doesn't change player state.
   */
  @Test
  void testUnknownEffectType() {
    // Arrange
    effectTile = new EffectTile(currentTile, "unknownEffect", startTile);
    Tile originalTile = player.getCurrentTile();
    boolean originalSkipStatus = player.isSkipTurn();

    // Act
    effectTile.performAction(player);

    // Assert
    assertEquals(originalTile, player.getCurrentTile(), "Player should remain on the same tile when effect type is unknown");
    assertEquals(originalSkipStatus, player.isSkipTurn(), "Player's skip turn status should remain unchanged when effect type is unknown");
  }

  /**
   * Test that skipTurn effect works correctly even if player is already set to skip turn.
   */
  @Test
  void testSkipTurnWhenAlreadySetToSkip() {
    // Arrange
    effectTile = new EffectTile(currentTile, "skipTurn", startTile);
    player.setSkipTurn(true);

    // Act
    effectTile.performAction(player);

    // Assert
    assertTrue(player.isSkipTurn(), "Player should still be set to skip turn after effect is applied");
  }

  /**
   * Test that backToStart effect moves player to the correct start tile.
   */
  @Test
  void testBackToStartWithDifferentStartTile() {
    // Arrange
    Tile differentStartTile = new Tile(10);
    effectTile = new EffectTile(currentTile, "backToStart", differentStartTile);

    // Act
    effectTile.performAction(player);

    // Assert
    assertEquals(differentStartTile, player.getCurrentTile(), "Player should be moved to the specified start tile");
    assertNotEquals(startTile, player.getCurrentTile(), "Player should not be on the original start tile");
  }
}