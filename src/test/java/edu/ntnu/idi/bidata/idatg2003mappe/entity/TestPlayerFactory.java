package edu.ntnu.idi.bidata.idatg2003mappe.entity.player;

import static org.junit.jupiter.api.Assertions.*;

import edu.ntnu.idi.bidata.idatg2003mappe.map.Tile;
import edu.ntnu.idi.bidata.idatg2003mappe.map.board.Board;
import edu.ntnu.idi.bidata.idatg2003mappe.map.board.BoardLinear;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

/**
 * Unit tests for the PlayerFactory class.
 * Tests creation of players from CSV files and default player creation.
 *@version 1.0.0
 * @since 1.0.0
 */
class TestPlayerFactory {

  private Board board;
  private Tile startTile;

  @TempDir
  Path tempDir;

  /**
   * Set up test environment before each test.
   */
  @BeforeEach
  void setUp() {
    board = new BoardLinear();
    startTile = new Tile(1);
    board.addTileToBoard(startTile);

    // Add more tiles to the board
    for (int i = 2; i <= 10; i++) {
      Tile tile = new Tile(i);
      board.addTileToBoard(tile);
    }
  }

  /**
   * Tear down test environment after each test.
   */
  @AfterEach
  void tearDown() {
    board = null;
    startTile = null;
  }

  /**
   * Test creating players from a valid CSV file.
   */
  @Test
  void testCreatePlayersFromValidCSV() throws IOException {
    // Arrange
    File csvFile = tempDir.resolve("valid_players.csv").toFile();
    try (FileWriter writer = new FileWriter(csvFile)) {
      writer.write("Player name,ID,Color,Position\n");
      writer.write("Alice,0,Red,1\n");
      writer.write("Bob,1,Blue,1\n");
      writer.write("Charlie,2,Green,1\n");
    }

    // Act
    List<Player> players = PlayerFactory.createPlayersFromCSV(csvFile.getAbsolutePath(), board);

    // Assert
    assertEquals(3, players.size(), "Should create 3 players from the CSV file");

    Player alice = players.get(0);
    assertEquals("Alice", alice.getName(), "First player should be named Alice");
    assertEquals(0, alice.getID(), "Alice should have ID 0");
    assertEquals("Red", alice.getColor(), "Alice should have color Red");
    assertEquals(startTile, alice.getCurrentTile(), "Alice should be on the start tile");

    Player bob = players.get(1);
    assertEquals("Bob", bob.getName(), "Second player should be named Bob");
    assertEquals(1, bob.getID(), "Bob should have ID 1");
    assertEquals("Blue", bob.getColor(), "Bob should have color Blue");
    assertEquals(startTile, bob.getCurrentTile(), "Bob should be on the start tile");
  }

  /**
   * Test creating players from a CSV file with invalid position.
   */
  @Test
  void testCreatePlayersWithInvalidPosition() throws IOException {
    // Arrange
    File csvFile = tempDir.resolve("invalid_position.csv").toFile();
    try (FileWriter writer = new FileWriter(csvFile)) {
      writer.write("Player name,ID,Color,Position\n");
      writer.write("Alice,0,Red,999\n"); // Position 999 doesn't exist on the board
    }

    // Act
    List<Player> players = PlayerFactory.createPlayersFromCSV(csvFile.getAbsolutePath(), board);

    // Assert
    assertEquals(1, players.size(), "Should create 1 player from the CSV file");
    Player alice = players.get(0);
    assertEquals(startTile, alice.getCurrentTile(), "Alice should be on the start tile as fallback");
  }

  /**
   * Test creating players from a non-existent CSV file.
   */
  @Test
  void testCreatePlayersFromNonExistentFile() {
    // Arrange
    String nonExistentFile = tempDir.resolve("non_existent.csv").toString();

    // Act
    List<Player> players = PlayerFactory.createPlayersFromCSV(nonExistentFile, board);

    // Assert
    assertNotNull(players, "Should return a non-null list even if file doesn't exist");
    assertFalse(players.isEmpty(), "Should create default players if file doesn't exist");
  }

  /**
   * Test creating players from a malformed CSV file.
   */
  @Test
  void testCreatePlayersFromMalformedCSV() throws IOException {
    // Arrange
    File csvFile = tempDir.resolve("malformed.csv").toFile();
    try (FileWriter writer = new FileWriter(csvFile)) {
      writer.write("This is not a valid CSV file format");
    }

    // Act
    List<Player> players = PlayerFactory.createPlayersFromCSV(csvFile.getAbsolutePath(), board);

    // Assert
    assertNotNull(players, "Should return a non-null list even if file is malformed");
    assertFalse(players.isEmpty(), "Should create default players if file is malformed");
  }

  /**
   * Test creating default players.
   */
  @Test
  void testCreateDefaultPlayers() {
    // Arrange
    int count = 3;

    // Act
    List<Player> players = PlayerFactory.createDefaultPlayers(count, startTile);

    // Assert
    assertEquals(count, players.size(), "Should create the specified number of players");

    for (int i = 0; i < count; i++) {
      Player player = players.get(i);
      assertEquals("Player " + (i + 1), player.getName(), "Player should have correct default name");
      assertEquals(i, player.getID(), "Player should have correct ID");
      assertNotNull(player.getColor(), "Player should have a color assigned");
      assertEquals(startTile, player.getCurrentTile(), "Player should be on the start tile");
    }
  }

  /**
   * Test creating zero default players.
   */
  @Test
  void testCreateZeroDefaultPlayers() {
    // Arrange
    int count = 0;

    // Act
    List<Player> players = PlayerFactory.createDefaultPlayers(count, startTile);

    // Assert
    assertEquals(0, players.size(), "Should create no players when count is 0");
  }


  /**
   * Test creating players from default CSV location.
   */
  @Test
  void testCreatePlayersFromDefaultCSV() {
    // Act
    List<Player> players = PlayerFactory.createPlayersFromDefaultCSV(board);

    // Assert
    assertNotNull(players, "Should return a non-null list of players");
    assertFalse(players.isEmpty(), "Should not return an empty list");
  }
}