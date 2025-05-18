package edu.ntnu.idi.bidata.idatg2003mappe.filehandling.playerInfo;


import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import edu.ntnu.idi.bidata.idatg2003mappe.entity.Player;
import edu.ntnu.idi.bidata.idatg2003mappe.map.Tile;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Utility class for reading player data from the CSV file.
 *
 * @author Simen Gudbrandsen and Frikk Breadsroed
 * @version 0.0.1
 * @since 18.05.2025
 */
public class PlayerFileHandler {
  private static final String PLAYER_DATA_FILE = "src/main/resources/saves/playerData/Players.csv";

  /**
   * Reads player data from the Players.csv file and creates Player objects.
   *
   * @param startTile The starting tile for all players
   * @return A list of Player objects
   * @throws IllegalArgumentException If there's an error reading the file
   */
  public static List<Player> readPlayersFromFile(Tile startTile) throws IllegalArgumentException {
    List<Player> players = new ArrayList<>();

    File file = new File(PLAYER_DATA_FILE);
    if (!file.exists()) {
      throw new IllegalArgumentException("Player file does not exist: " + PLAYER_DATA_FILE);
    }

    try (CSVReader reader = new CSVReader(new FileReader(PLAYER_DATA_FILE))) {
      String[] record;
      // Skip header if present
      reader.readNext();

      while ((record = reader.readNext()) != null) {
        // Expected format: Player Name, Color, Score
        if (record.length >= 2) {
          String playerName = record[0];
          int id = Integer.parseInt(record[1]);
          String color = record[2];
          int position = Integer.parseInt(record[3]);

          Player player = new Player(playerName, id, color, startTile);
          players.add(player);
        }
      }
    } catch (IOException | CsvValidationException e) {
      throw new IllegalArgumentException("Error reading player data from CSV file", e);
    }

    if (players.isEmpty()) {
      throw new IllegalArgumentException("No players found in CSV file");
    }

    return players;
  }

  /**
   * Checks if the Players.csv file exists.
   *
   * @return true if the file exists, false otherwise
   */
  public static boolean playerFileExists() {
    File file = new File(PLAYER_DATA_FILE);
    return file.exists() && file.isFile();
  }
}