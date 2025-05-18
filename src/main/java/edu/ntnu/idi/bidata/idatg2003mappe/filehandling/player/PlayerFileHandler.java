package edu.ntnu.idi.bidata.idatg2003mappe.filehandling.player;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import edu.ntnu.idi.bidata.idatg2003mappe.entity.Player;
import edu.ntnu.idi.bidata.idatg2003mappe.filehandling.exceptionhandling.FileHandlingException;
import edu.ntnu.idi.bidata.idatg2003mappe.map.Tile;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PlayerFileHandler {
  private static final String PLAYER_DATA_FILE = "src/main/resources/saves/playerData/Players.csv";

  /**
   * Reads player data from the Players.csv file and creates Player objects.
   *
   * @param startTile The starting tile for all players
   * @return A list of Player objects
   * @throws FileHandlingException If there's an error reading the file
   */
  public static List<Player> readPlayersFromFile(Tile startTile) throws FileHandlingException {
    List<Player> players = new ArrayList<>();

    try (CSVReader reader = new CSVReader(new FileReader(PLAYER_DATA_FILE))) {
      String[] record;
      // Skip header if present
      reader.readNext();

      int id = 1;
      while ((record = reader.readNext()) != null) {
        // Expected format: Player Name, Color, Score
        if (record.length >= 2) {
          String playerName = record[0];
          // Color is stored but not used in Player constructor directly
          // String color = record[1];

          Player player = new Player(playerName, startTile, id);
          players.add(player);
          id++;
        }
      }
    } catch (IOException | CsvValidationException e) {
      throw new FileHandlingException("Error reading player data from CSV file", e);
    }

    if (players.isEmpty()) {
      throw new FileHandlingException("No players found in CSV file");
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