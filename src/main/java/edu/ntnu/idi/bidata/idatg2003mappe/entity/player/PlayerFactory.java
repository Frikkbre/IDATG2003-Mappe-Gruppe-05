package edu.ntnu.idi.bidata.idatg2003mappe.entity.player;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import edu.ntnu.idi.bidata.idatg2003mappe.map.Tile;
import edu.ntnu.idi.bidata.idatg2003mappe.map.board.Board;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * <p>Factory class for creating Player objects.</p>
 * <p>This factory provides methods for creating players from various sources,
 * primarily from CSV files or with default values. It follows the Factory design pattern
 * to encapsulate player creation logic.</p>
 * <p>The factory provides these capabilities:</p>
 * <ul>
 *   <li>Creating players from CSV data files</li>
 *   <li>Generating default players when no data exists</li>
 *   <li>Placing players on appropriate starting tiles</li>
 * </ul>
 *
 * @author Simen Gudbrandsen and Frikk Breadsroed
 * @version 0.0.1
 * @since 21.05.2025
 */
public class PlayerFactory {

  private static final String DEFAULT_PLAYER_DATA_FILE = "src/main/resources/saves/playerData/Players.csv";

  private static final Logger logger = Logger.getLogger(PlayerFactory.class.getName());

  /**
   * <p>Creates players from a CSV file.</p>
   * <p>Reads player data from the specified CSV file and creates Player objects.
   * Each CSV record should contain:</p>
   * <ol>
   *   <li>Player name</li>
   *   <li>Player ID (integer)</li>
   *   <li>Player color (as a string)</li>
   *   <li>Starting position (tile ID as integer)</li>
   * </ol>
   * <p>If the file cannot be read or contains invalid data, the method
   * falls back to creating default players.</p>
   *
   * @param filePath The path to the CSV file
   * @param board    The {@link Board} where players will be placed
   * @return A list of {@link Player} objects
   */
  public static List<Player> createPlayersFromCSV(String filePath, Board board) {
    List<Player> players = new ArrayList<>();

    // Try to read from CSV file
    File file = new File(filePath);
    if (file.exists() && file.isFile()) {
      try (CSVReader reader = new CSVReader(new FileReader(file))) {
        String[] record;
        // Skip header
        reader.readNext();

        while ((record = reader.readNext()) != null) {
          // Expected format: Player Name, Player ID, Color, Position
          if (record.length >= 4) {
            String playerName = record[0];
            int playerID = Integer.parseInt(record[1]);
            String playerColor = record[2];
            int position = Integer.parseInt(record[3]);

            Tile playerTile = board.getTileById(position);
            if (playerTile == null) {
              // Fallback to start tile if position is invalid
              playerTile = board.getStartTile();
            }

            Player player = new Player(playerName, playerID, playerColor, playerTile);
            players.add(player);
            logger.info("Player " + playerName + " added to the game.");
          }
        }
      } catch (IOException | CsvValidationException e) {
        logger.warning("Error reading player data: " + e.getMessage());
      }
    }

    // If no players were created from file, create default players
    if (players.isEmpty()) {
      players = createDefaultPlayers(2, board.getStartTile());
    }

    return players;
  }

  /**
   * <p>Creates players from the default CSV file.</p>
   * <p>Uses the standard location (<code>src/main/resources/saves/playerData/Players.csv</code>)
   * to load player data. If the file doesn't exist or can't be read,
   * falls back to creating default players.</p>
   *
   * @param board The {@link Board} where players will be placed
   * @return A list of {@link Player} objects
   */
  public static List<Player> createPlayersFromDefaultCSV(Board board) {
    return createPlayersFromCSV(DEFAULT_PLAYER_DATA_FILE, board);
  }

  /**
   * <p>Creates a specified number of default players.</p>
   * <p>Generates players with standard names ("Player 1", "Player 2", etc.)
   * and a rotating set of colors. All players are placed at the same starting tile.</p>
   * <p>This method is typically used as a fallback when player data cannot be
   * loaded from external sources.</p>
   *
   * @param count     The number of players to create
   * @param startTile The starting {@link Tile} for the players
   * @return A list of {@link Player} objects
   */
  public static List<Player> createDefaultPlayers(int count, Tile startTile) {
    List<Player> players = new ArrayList<>();
    String[] colors = {"LightGreen", "LightPink", "Green", "HotPink", "Orange"};

    for (int i = 0; i < count; i++) {
      String color = colors[i % colors.length];
      players.add(new Player("Player " + (i + 1), i, color, startTile));
    }

    return players;
  }
}
