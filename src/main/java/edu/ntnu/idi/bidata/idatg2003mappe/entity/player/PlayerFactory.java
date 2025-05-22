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

/**
 * Factory class for creating Player objects.
 *
 * @author Simen Gudbrandsen and Frikk Breadsroed
 * @version 0.0.1
 * @since 21.05.2025
 */
public class PlayerFactory {

  private static final String DEFAULT_PLAYER_DATA_FILE = "src/main/resources/saves/playerData/Players.csv";

  /**
   * Creates players from a CSV file.
   *
   * @param filePath The path to the CSV file.
   * @param board    The board where players will be placed.
   * @return A list of players.
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
            System.out.println("Player " + playerName + " added to the game.");
          }
        }
      } catch (IOException | CsvValidationException e) {
        System.out.println("Error reading player data: " + e.getMessage());
      }
    }

    // If no players were created from file, create default players
    if (players.isEmpty()) {
      players = createDefaultPlayers(2, board.getStartTile());
    }

    return players;
  }

  /**
   * Creates players from the default CSV file.
   *
   * @param board The board where players will be placed.
   * @return A list of players.
   */
  public static List<Player> createPlayersFromDefaultCSV(Board board) {
    return createPlayersFromCSV(DEFAULT_PLAYER_DATA_FILE, board);
  }

  /**
   * Creates a specified number of default players.
   *
   * @param count     The number of players to create.
   * @param startTile The starting tile for the players.
   * @return A list of players.
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