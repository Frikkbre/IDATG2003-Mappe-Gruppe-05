package edu.ntnu.idi.bidata.idatg2003mappe.movement;

import edu.ntnu.idi.bidata.idatg2003mappe.entity.player.Player;
import edu.ntnu.idi.bidata.idatg2003mappe.map.Tile;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DisplayName("LadderAction Test Suite")
class TestLadderAction { // Removed public modifier

  private Player player;
  // private Tile currentTile; // This is the tile where the ladder starts - Removed as it's initialized in setUp and used by ladderAction
  private Tile destinationTile; // This is the tile where the ladder ends
  private LadderAction ladderAction;

  @BeforeEach
  void setUp() {
    // Initialize a default start tile for player, can be any tile not used in specific assertions
    Tile playerStartTile = new Tile(0);
    player = new Player("TestPlayer1", 1, "Blue", playerStartTile);

    Tile currentTileSetup = new Tile(1); // Renamed to avoid conflict with class field if it were kept
    destinationTile = new Tile(10);

    // Assuming Tile has a method to set its ladder destination for testing
    // This might be named setDestinationTile, addSpecialConnection, etc.
    // For LadderAction to work, currentTile must be able to return destinationTile via getDestinationTile()
    currentTileSetup.setDestinationTile(destinationTile);

    ladderAction = new LadderAction(currentTileSetup);
  }

  @Test
  @DisplayName("Constructor should throw IllegalArgumentException for null tile")
  void constructor_NullTile_ThrowsIllegalArgumentException() {
    assertThrows(IllegalArgumentException.class, () -> new LadderAction(null),
        "Constructor should throw IllegalArgumentException if tile is null.");
  }

  @Test
  @DisplayName("Should move player to destination tile when destination exists")
  void testPerformAction_MovesPlayer_WhenDestinationExists() {
    Tile initialPosition = new Tile(0); // A different tile
    player.placePlayer(initialPosition);
    assertNotNull(player.getCurrentTile(), "Player's current tile should be set before action.");
    assertNotEquals(destinationTile, player.getCurrentTile(), "Player should not start at the ladder's destination.");

    ladderAction.performAction(player);

    assertEquals(destinationTile, player.getCurrentTile(),
        "Player should be moved to the ladder's destination tile.");
  }

  @Test
  @DisplayName("Should not move player when destination tile is null")
  void testPerformAction_DoesNotMovePlayer_WhenDestinationIsNull() {
    Tile ladderStartNoDest = new Tile(5);
    // Assuming a new Tile by default has its destination as null,
    // or that setDestinationTile(null) can be called if needed.
    // If currentTile.getDestinationTile() returns null, player should not move.
    // ladderStartNoDest.setDestinationTile(null); // Explicitly if necessary

    LadderAction actionOnNoDestTile = new LadderAction(ladderStartNoDest);

    Tile initialPosition = new Tile(0);
    player.placePlayer(initialPosition);

    actionOnNoDestTile.performAction(player);

    assertEquals(initialPosition, player.getCurrentTile(),
        "Player should remain in the initial position if ladder has no destination.");
  }

  @Test
  @DisplayName("Constructor should correctly use the provided tile for action")
  void testConstructor_UsesProvidedTile() {
    Tile specificStartTile = new Tile(20);
    Tile specificEndTile = new Tile(30);
    specificStartTile.setDestinationTile(specificEndTile);

    LadderAction specificLadderAction = new LadderAction(specificStartTile);

    // Initialize a default start tile for player
    Tile constructorTestPlayerStartTile = new Tile(99);
    Player testPlayerForConstructor = new Player("ConstructorTestPlayer", 2, "Red", constructorTestPlayerStartTile);
    Tile initialPos = new Tile(100); // Start player somewhere else
    testPlayerForConstructor.placePlayer(initialPos);

    specificLadderAction.performAction(testPlayerForConstructor);

    assertEquals(specificEndTile, testPlayerForConstructor.getCurrentTile(),
        "LadderAction should use the tile provided in its constructor to determine destination.");
  }
}
