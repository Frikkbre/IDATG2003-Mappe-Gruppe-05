package edu.ntnu.idi.bidata.idatg2003mappe.filehandling.player;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import edu.ntnu.idi.bidata.idatg2003mappe.entity.player.Player;
import edu.ntnu.idi.bidata.idatg2003mappe.map.Tile;
import edu.ntnu.idi.bidata.idatg2003mappe.map.board.BoardBranching;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * <p>Handles reading player data from CSV files.</p>
 * <p>This class provides functionality for loading player information from CSV files
 * and creating {@link Player} objects from that data. It supports fallback to default
 * players if the file is missing or contains invalid data.</p>
 * <p>The CSV file should contain:</p>
 * <ul>
 *   <li>Player name</li>
 *   <li>Player ID</li>
 *   <li>Player color</li>
 *   <li>Starting position (tile ID)</li>
 * </ul>
 *
 * @author Simen Gudbrandsen and Frikk Breadsroed
 * @version 0.0.1
 * @since 21.05.2025
 */
public class PlayerFileHandler {

  private static final Logger logger = Logger.getLogger(PlayerFileHandler.class.getName());
  private static final String PLAYER_DATA_FILE = "data/saves/playerData/Players.csv";

  /**
   * <p>Reads players from a CSV file.</p>
   * <p>This method attempts to read player data from the standard CSV file location.
   * If the file exists and contains valid data, it creates {@link Player} objects
   * based on that data. If the file is missing or contains invalid data, it falls
   * back to creating a default player.</p>
   * <p>For each player, the method:</p>
   * <ol>
   *   <li>Reads name, ID, color, and position from the CSV</li>
   *   <li>Attempts to find the corresponding tile on the board</li>
   *   <li>Falls back to a default tile if the specified one isn't found</li>
   *   <li>Creates a new {@link Player} with the parsed data</li>
   * </ol>
   *
   * @param board             The game board, used to get tiles by ID
   * @param defaultStartTiles The list of default starting tiles for fallback
   * @return A list of {@link Player} objects created from the file, or a default player if the file is empty/invalid
   */
  public List<Player> readPlayersFromCSV(BoardBranching board, List<Tile> defaultStartTiles) {
    List<Player> localPlayers = new ArrayList<>();
    Tile fallbackStartTile = !defaultStartTiles.isEmpty() ? defaultStartTiles.get(0) : (board != null ? board.getStartTile() : null);

    File file = new File(PLAYER_DATA_FILE);
    if (file.exists() && file.isFile()) {
      try (CSVReader reader = new CSVReader(new FileReader(file))) {
        String[] record;
        reader.readNext(); // Skip header

        while ((record = reader.readNext()) != null) {
          if (record.length >= 4) {
            String playerName = record[0];
            int playerID = Integer.parseInt(record[1]);
            String playerColor = record[2];
            int position = Integer.parseInt(record[3]);

            Tile playerTile = (board != null) ? board.getTileById(position) : null;
            if (playerTile == null) {
              playerTile = fallbackStartTile;
              logger.warning("Player " + playerName + " had an invalid start position " + position + ". Placing on fallback tile.");
            }

            if (playerTile == null && board == null) {
              logger.severe("Cannot place player " + playerName + " as board and fallback tile are null.");
              continue; // Skip this player if no valid tile can be assigned
            }


            Player player = new Player(playerName, playerID, playerColor, playerTile);
            localPlayers.add(player);
            logger.info("Player " + playerName + " loaded from CSV.");
          }
        }
      } catch (IOException | CsvValidationException | NumberFormatException e) {
        logger.warning("Error reading player data from CSV: " + e.getMessage());
      }
    } else {
      logger.info("Player data file not found: " + PLAYER_DATA_FILE);
    }

    if (localPlayers.isEmpty()) {
      logger.info("No players read from CSV or file not found. Creating a default player.");
      if (fallbackStartTile != null) {
        localPlayers.add(new Player("Player 1", 0, "Blue", fallbackStartTile));
      } else {
        logger.severe("Cannot create default player as fallback start tile is null (board might be null too).");
      }
    }
    return localPlayers;
  }
}
