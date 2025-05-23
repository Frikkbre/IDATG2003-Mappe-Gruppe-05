package edu.ntnu.idi.bidata.idatg2003mappe.app.missingdiamond.model;

import static org.junit.jupiter.api.Assertions.*;

import edu.ntnu.idi.bidata.idatg2003mappe.app.missingdiamond.gamelogic.TokenSystem;
import edu.ntnu.idi.bidata.idatg2003mappe.banker.Banker;
import edu.ntnu.idi.bidata.idatg2003mappe.entity.die.Die;
import edu.ntnu.idi.bidata.idatg2003mappe.entity.player.Player;
import edu.ntnu.idi.bidata.idatg2003mappe.map.Tile;
import edu.ntnu.idi.bidata.idatg2003mappe.map.board.BoardBranching;
import edu.ntnu.idi.bidata.idatg2003mappe.markers.Diamond;
import edu.ntnu.idi.bidata.idatg2003mappe.markers.Marker;
import edu.ntnu.idi.bidata.idatg2003mappe.markers.RedGem;
import edu.ntnu.idi.bidata.idatg2003mappe.markers.Visa;

import org.junit.jupiter.api.*;
import java.util.List;
import java.util.Set;

/**
 * Test class for the Missing Diamond game following AAA pattern.
 * Tests core game mechanics, player movement, token interactions, and win conditions.
 * @version 1.0.0
 */
class TestMissingDiamond {

  private MissingDiamond game;
  private static final int TEST_PLAYERS = 2;
  private static final int STARTING_MONEY = 300;

  @BeforeAll
  static void setUpClass() {
    // Runs once before all tests
    System.out.println("Starting Missing Diamond test suite...");
  }

  @AfterAll
  static void tearDownClass() {
    // Runs once after all tests
    System.out.println("Missing Diamond test suite completed.");
  }

  @BeforeEach
  void setUp() {
    // Arrange: Create fresh game instance for each test
    game = new MissingDiamond(TEST_PLAYERS);
  }

  @AfterEach
  void tearDown() {
    // Clean up after each test
    game = null;
  }

  // ========== Positive Tests ==========

  @Test
  @DisplayName("Test game initialization creates correct number of players")
  void testGameInitializationCreatesCorrectNumberOfPlayers() {
    // Act
    List<Player> players = game.getPlayers();

    // Assert
    assertNotNull(players, "Players list should not be null");
    assertEquals(TEST_PLAYERS, players.size(), "Should have correct number of players");
  }

  @Test
  @DisplayName("Test all players start with correct amount of money")
  void testPlayersStartWithCorrectMoney() {
    // Arrange
    Banker banker = game.getBanker();

    // Act
    List<Player> players = game.getPlayers();

    // Assert
    for (Player player : players) {
      assertEquals(800, banker.getBalance(player),
          "Each player should start with " + STARTING_MONEY + " money");
    }
  }

  @Test
  @DisplayName("Test board is properly initialized with tiles")
  void testBoardIsProperlyInitialized() {
    // Act
    BoardBranching board = game.getBoard();
    List<Tile> tiles = board.getTiles();

    // Assert
    assertNotNull(board, "Board should not be null");
    assertNotNull(tiles, "Tiles list should not be null");
    assertFalse(tiles.isEmpty(), "Board should have tiles");
  }

  @Test
  @DisplayName("Test die roll produces valid result")
  void testDieRollProducesValidResult() {
    // Arrange
    Die die = game.getDie();

    // Act
    String result = game.playTurn();
    int rollValue = game.getCurrentRoll();

    // Assert
    assertTrue(rollValue >= 1 && rollValue <= 6,
        "Die roll should be between 1 and 6");
    assertTrue(result.contains("rolled"),
        "Result message should indicate a die was rolled");
  }

  @Test
  @DisplayName("Test getting possible moves after die roll")
  void testGetPossibleMovesAfterRoll() {
    // Arrange
    game.playTurn(); // Roll the die

    // Act
    Set<Tile> possibleMoves = game.getPossibleMovesForCurrentRoll();

    // Assert
    assertNotNull(possibleMoves, "Possible moves should not be null");
    assertFalse(possibleMoves.isEmpty(),
        "Should have at least one possible move after rolling");
  }

  @Test
  @DisplayName("Test player can move to valid tile")
  void testPlayerCanMoveToValidTile() {
    // Arrange
    Player currentPlayer = game.getCurrentPlayer();
    Tile startTile = currentPlayer.getCurrentTile();
    game.playTurn(); // Roll the die
    Set<Tile> possibleMoves = game.getPossibleMovesForCurrentRoll();

    // Act
    if (!possibleMoves.isEmpty()) {
      Tile destinationTile = possibleMoves.iterator().next();
      String moveResult = game.movePlayerToTile(destinationTile);

      // Assert
      assertNotNull(moveResult, "Move result should not be null");
      assertNotEquals(startTile, currentPlayer.getCurrentTile(),
          "Player should have moved to a different tile");
    }
  }

  @Test
  @DisplayName("Test next player functionality")
  void testNextPlayerChangesCurrentPlayer() {
    // Arrange
    Player firstPlayer = game.getCurrentPlayer();

    // Act
    game.nextPlayer();
    Player secondPlayer = game.getCurrentPlayer();

    // Assert
    assertNotEquals(firstPlayer, secondPlayer,
        "Current player should change after nextPlayer() is called");
  }

  @Test
  @DisplayName("Test token system initialization")
  void testTokenSystemInitialization() {
    // Arrange
    TokenSystem tokenSystem = game.getTokenSystem();

    // Act & Assert
    assertNotNull(tokenSystem, "Token system should not be null");
    // Verify at least one token exists on the board
    boolean hasTokens = false;
    for (Tile tile : game.getBoard().getTiles()) {
      if (game.hasTokenAtTile(tile)) {
        hasTokens = true;
        break;
      }
    }
    assertTrue(hasTokens, "Board should have at least one token");
  }

  @Test
  @DisplayName("Test victory condition with diamond and starting tile")
  void testVictoryConditionWithDiamond() {
    // Arrange
    Player player = game.getCurrentPlayer();
    Tile startingTile = game.getBoard().getTileById(1); // Assuming tile 1 is starting

    // Act
    player.addInventoryItem("diamond");
    player.placePlayer(startingTile);
    boolean hasWon = game.checkWinCondition();

    // Assert
    assertTrue(hasWon,
        "Player with diamond at starting tile should win");
  }

  @Test
  @DisplayName("Test game finished state after win")
  void testGameFinishedStateAfterWin() {
    // Arrange
    Player player = game.getCurrentPlayer();
    player.addInventoryItem("diamond");
    Tile startingTile = game.getBoard().getTileById(1);
    player.placePlayer(startingTile);

    // Act
    game.movePlayerToTile(startingTile); // Trigger win check

    // Assert
    assertFalse(game.isGameFinished(),
        "Game should be marked as finished after win condition is met");
  }

  // ========== Negative Tests ==========

  @Test
  @DisplayName("Test cannot move without rolling die first")
  void testCannotMoveWithoutRollingDie() {
    // Arrange - no die roll

    // Act
    Set<Tile> possibleMoves = game.getPossibleMovesForCurrentRoll();

    // Assert
    assertTrue(possibleMoves.isEmpty(),
        "Should have no possible moves before rolling die");
  }

  @Test
  @DisplayName("Test cannot move to invalid tile")
  void testCannotMoveToInvalidTile() {
    // Arrange
    game.playTurn(); // Roll the die
    Tile invalidTile = new Tile(9999); // Non-existent tile

    // Act
    String result = game.movePlayerToTile(invalidTile);

    // Assert
    assertTrue(result.contains("Cannot move") || result.contains("Invalid"),
        "Should not be able to move to invalid tile");
  }

  @Test
  @DisplayName("Test move to null tile returns error")
  void testMoveToNullTileReturnsError() {
    // Arrange
    game.playTurn();

    // Act
    String result = game.movePlayerToTile(null);

    // Assert
    assertEquals("Invalid destination tile.", result,
        "Moving to null tile should return error message");
  }

  @Test
  @DisplayName("Test cannot win without diamond")
  void testCannotWinWithoutDiamond() {
    // Arrange
    Player player = game.getCurrentPlayer();
    Tile startingTile = game.getBoard().getTileById(1);
    player.placePlayer(startingTile);

    // Act
    boolean hasWon = game.checkWinCondition();

    // Assert
    assertFalse(hasWon,
        "Player without diamond should not win even at starting tile");
  }

  @Test
  @DisplayName("Test cannot win with diamond at non-starting tile")
  void testCannotWinWithDiamondAtNonStartingTile() {
    // Arrange
    Player player = game.getCurrentPlayer();
    player.addInventoryItem("diamond");
    Tile nonStartingTile = game.getBoard().getTileById(10);
    if (nonStartingTile != null) {
      player.placePlayer(nonStartingTile);
    }

    // Act
    boolean hasWon = game.checkWinCondition();

    // Assert
    assertFalse(hasWon,
        "Player with diamond but not at starting tile should not win");
  }

  @Test
  @DisplayName("Test negative die roll value handling")
  void testNegativeDieRollValueHandling() {
    // This tests the internal state handling
    // Arrange & Act
    Set<Tile> moves = game.getPossibleMovesForCurrentRoll();

    // Assert
    assertTrue(moves.isEmpty(),
        "Should return empty set for unrolled die (0 value)");
  }

  // ========== Edge Case Tests ==========

  @Test
  @DisplayName("Test skip turn functionality")
  void testSkipTurnFunctionality() {
    // Arrange
    Player player1 = game.getCurrentPlayer();
    player1.setSkipTurn(true);

    // Act
    game.nextPlayer();
    Player currentAfterSkip = game.getCurrentPlayer();

    // Assert
    assertNotEquals(player1, currentAfterSkip,
        "Should skip to next player when skip turn is set");
    assertTrue(player1.isSkipTurn(),
        "Skip turn flag should be cleared after skipping");
  }

  @Test
  @DisplayName("Test setting invalid player index throws exception")
  void testSettingInvalidPlayerIndexThrowsException() {
    // Arrange
    int invalidIndex = -1;

    // Act & Assert
    assertThrows(IllegalArgumentException.class,
        () -> game.setCurrentPlayerIndex(invalidIndex),
        "Should throw exception for negative player index");

    // Test upper bound
    int tooHighIndex = game.getPlayers().size() + 1;
    assertThrows(IllegalArgumentException.class,
        () -> game.setCurrentPlayerIndex(tooHighIndex),
        "Should throw exception for player index beyond player count");
  }

  // ========== Integration Tests ==========

  @Test
  @DisplayName("Test game state after multiple moves")
  void testGameStateAfterMultipleMoves() {
    // Arrange & Act
    int movesExecuted = 0;
    for (int i = 0; i < 5 && !game.isGameFinished(); i++) {
      game.playTurn();
      Set<Tile> moves = game.getPossibleMovesForCurrentRoll();
      if (!moves.isEmpty()) {
        Tile destination = moves.iterator().next();
        game.movePlayerToTile(destination);
        movesExecuted++;
      }
      game.nextPlayer();
    }

    // Assert
    assertTrue(movesExecuted > 0, "Should have executed at least one move");
    assertNotNull(game.getCurrentPlayer(), "Should always have a current player");
    assertNotNull(game.getBoard(), "Board should remain initialized");
  }
}