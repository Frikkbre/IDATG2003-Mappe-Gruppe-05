package edu.ntnu.idi.bidata.idatg2003mappe.filehandling.game;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import edu.ntnu.idi.bidata.idatg2003mappe.app.laddergame.controller.LadderGameController;
import edu.ntnu.idi.bidata.idatg2003mappe.app.laddergame.ui.LadderGameGUI;
import edu.ntnu.idi.bidata.idatg2003mappe.app.missingdiamond.controller.MissingDiamondController;
import edu.ntnu.idi.bidata.idatg2003mappe.app.missingdiamond.ui.MissingDiamondGUI;
import edu.ntnu.idi.bidata.idatg2003mappe.entity.player.Player;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Alert;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * <p>Handles saving and loading game states for different board games.</p>
 * <p>This class provides methods for saving the current game state to a CSV file
 * and loading saved games for the Ladder Game and Missing Diamond game.</p>
 * <p>Features include:</p>
 * <ul>
 *   <li>Quick save functionality to store player positions</li>
 *   <li>Game-specific loading for different game types</li>
 *   <li>Error handling with user-friendly alerts</li>
 * </ul>
 *
 * @author Simen Gudbrandsen and Frikk Breadsroed
 * @version 0.0.3
 * @since 21.05.2025
 */
public class GameSaveLoadHandler {

  private static final String lastSaveDir = "data/saves";
  private static final String lastSaveFile = "LastSave.csv";
  private static final String fullPath = lastSaveDir + "/" + lastSaveFile;

  private static final Logger logger = Logger.getLogger(GameSaveLoadHandler.class.getName());

  /**
   * <p>Creates an event handler for quick-saving the current game state.</p>
   * <p>This method returns an event handler that, when triggered, saves the current
   * game state to a CSV file. The save includes information about all players,
   * such as their names, IDs, colors, and current positions on the board.</p>
   *
   * @param playersFromController The list of {@link Player} objects to save
   * @return An {@link EventHandler} that saves the game when triggered
   */
  public EventHandler<ActionEvent> quickSaveGame(List<Player> playersFromController) {
    return event -> {
      try {
        // Make sure directory exists
        File saveDir = new File(lastSaveDir);
        if (!saveDir.exists()) {
          saveDir.mkdirs();
        }

        if (playersFromController == null || playersFromController.isEmpty()) {
          showAlert(Alert.AlertType.ERROR, "Error", "Save Error", "No players found to save.");
          return;
        }

        // Save to CSV
        File csvFile = new File(fullPath);
        try (FileWriter outputFile = new FileWriter(csvFile);
             CSVWriter writer = new CSVWriter(outputFile)) {

          // Write header
          String[] header = {"Player Name", "ID", "Color", "Position"};
          writer.writeNext(header);

          // FIX: Actually write the player data to the CSV file
          for (Player player : playersFromController) {
            String[] playerData = {
                player.getName(),
                String.valueOf(player.getID()),
                player.getColor(),
                String.valueOf(player.getCurrentTile().getTileId())
            };
            writer.writeNext(playerData);

            logger.info("Saving player: " + player.getName() + ", ID: " + player.getID() +
                ", Color: " + player.getColor() + ", Position: " + player.getCurrentTile().getTileId());
          }

          // CSVWriter will be automatically closed by try-with-resources
        }

        showAlert(Alert.AlertType.INFORMATION, "Game Saved", "Game Saved Successfully",
            "Your game has been saved to LastSave.csv with " + playersFromController.size() + " players.");

      } catch (Exception ex) {
        showAlert(Alert.AlertType.ERROR, "Error", "Save Error",
            "Could not save the game: " + ex.getMessage());
        logger.severe("Error saving game: " + ex.getMessage());
        ex.printStackTrace();
      }
    };
  }

  /**
   * <p>Loads a saved Ladder Game.</p>
   * <p>This method reads player data from the saved CSV file and applies it to
   * the provided Ladder Game controller. It updates player positions and other
   * game state information based on the saved data.</p>
   * <p>The method performs these steps:</p>
   * <ol>
   *   <li>Check if the save file exists</li>
   *   <li>Read player data from the CSV file</li>
   *   <li>Create a {@link GameState} object with the loaded data</li>
   *   <li>Apply the game state to the controller</li>
   *   <li>Update the UI to reflect the loaded state</li>
   * </ol>
   *
   * @param ladderGameGUI The {@link LadderGameGUI} instance to update
   * @param controller    The {@link LadderGameController} to apply the state to
   * @param randomLadders Whether the game uses random ladders
   */
  public void loadLastSaveLadderGame(LadderGameGUI ladderGameGUI, LadderGameController controller, boolean randomLadders) {
    // Check if the CSV file exists
    File csvFile = new File(fullPath);
    if (!csvFile.exists() || !csvFile.isFile()) {
      Alert alert = new Alert(Alert.AlertType.INFORMATION);
      alert.setTitle("No Save Found");
      alert.setHeaderText("No Save File Found");
      alert.setContentText("There is no saved game to load.");
      alert.showAndWait();
      return;
    }

    try {
      // Read the CSV file
      try (CSVReader reader = new CSVReader(new FileReader(csvFile))) {
        logger.info("Loading game from: " + fullPath);

        String[] header = reader.readNext(); // Skip header
        if (header != null) {
          logger.info("Header: " + String.join(", ", header));
        }

        // Create GameState
        GameState gameState = new GameState();
        gameState.setRandomLadders(randomLadders);
        gameState.setCurrentPlayerIndex(0); // Default to first player's turn

        // Read player data
        List<GameState.PlayerPosition> playerPositions = new ArrayList<>();
        String[] record;
        while ((record = reader.readNext()) != null) {
          if (record.length >= 4) {
            String playerName = record[0];
            int playerId = Integer.parseInt(record[1]);
            int position = Integer.parseInt(record[3]);

            playerPositions.add(new GameState.PlayerPosition(playerName, playerId, position));
            logger.info("Loaded player: " + playerName + " at position " + position);
          }
        }

        gameState.setPlayerPositions(playerPositions);

        // Apply the game state to the existing controller
        controller.applyGameState(gameState);

        // Show success alert
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Game Loaded");
        alert.setHeaderText("Game Loaded Successfully");
        alert.setContentText("Your last saved ladder game has been loaded from LastSave.csv with " +
            playerPositions.size() + " players.");
        alert.showAndWait();

        ladderGameGUI.updateBoardUI();
      }
    } catch (Exception ex) {
      Alert alert = new Alert(Alert.AlertType.ERROR);
      alert.setTitle("Error");
      alert.setHeaderText("Load Error");
      alert.setContentText("Could not load the game: " + ex.getMessage());
      alert.showAndWait();
      logger.severe("Error loading ladder game: " + ex.getMessage());
      ex.printStackTrace();
    }
  }

  /**
   * <p>Loads a saved Missing Diamond game.</p>
   * <p>This method reads player data from the saved CSV file and applies it to
   * the provided Missing Diamond controller. It updates player positions and other
   * game state information based on the saved data.</p>
   * <p>The method performs these steps:</p>
   * <ol>
   *   <li>Check if the save file exists</li>
   *   <li>Read player data from the CSV file</li>
   *   <li>Create a {@link GameState} object with the loaded data</li>
   *   <li>Apply the game state to the controller</li>
   *   <li>Update the UI to reflect the loaded state</li>
   * </ol>
   *
   * @param missingDiamondGUI The {@link MissingDiamondGUI} instance to update
   * @param controller        The {@link MissingDiamondController} to apply the state to
   */
  public void loadLastSaveMissingDiamond(MissingDiamondGUI missingDiamondGUI, MissingDiamondController controller) {
    // Check if the CSV file exists
    File csvFile = new File(fullPath);
    if (!csvFile.exists() || !csvFile.isFile()) {
      Alert alert = new Alert(Alert.AlertType.INFORMATION);
      alert.setTitle("No Save Found");
      alert.setHeaderText("No Save File Found");
      alert.setContentText("There is no saved game to load.");
      alert.showAndWait();
      return;
    }

    try {
      // Read the CSV file
      try (CSVReader reader = new CSVReader(new FileReader(csvFile))) {
        String[] header = reader.readNext(); // Skip header
        if (header != null) {
          logger.info("Header: " + String.join(", ", header));
        }

        // Create GameState
        GameState gameState = new GameState();
        gameState.setCurrentPlayerIndex(0); // Default to first player's turn

        // Read player data
        List<GameState.PlayerPosition> playerPositions = new ArrayList<>();
        String[] record;
        while ((record = reader.readNext()) != null) {
          if (record.length >= 4) {
            String playerName = record[0];
            int playerId = Integer.parseInt(record[1]);
            int position = Integer.parseInt(record[3]);

            playerPositions.add(new GameState.PlayerPosition(playerName, playerId, position));
            logger.info("Loaded player: " + playerName + " at position " + position);
          }
        }

        gameState.setPlayerPositions(playerPositions);

        // Apply the game state to the existing controller
        controller.applyGameState(gameState);

        // Show success alert
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Game Loaded");
        alert.setHeaderText("Game Loaded Successfully");
        alert.setContentText("Your last saved missing diamond game has been loaded from LastSave.csv with " +
            playerPositions.size() + " players.");
        alert.showAndWait();

        // Update the UI
        missingDiamondGUI.updateBoardUI();
      }
    } catch (Exception ex) {
      Alert alert = new Alert(Alert.AlertType.ERROR);
      alert.setTitle("Error");
      alert.setHeaderText("Load Error");
      alert.setContentText("Could not load the game: " + ex.getMessage());
      alert.showAndWait();
      logger.severe("Error loading missing diamond game: " + ex.getMessage());
      ex.printStackTrace();
    }
  }

  /**
   * <p>Shows an alert dialog with the specified properties.</p>
   * <p>This helper method creates and displays a JavaFX Alert dialog with
   * the provided type, title, header, and content. It is used to inform
   * the user about the success or failure of save/load operations.</p>
   *
   * @param type    The {@link Alert.AlertType} to determine the alert style
   * @param title   The title of the alert dialog
   * @param header  The header text of the alert dialog
   * @param content The main content text of the alert dialog
   */
  private void showAlert(Alert.AlertType type, String title, String header, String content) {
    Alert alert = new Alert(type);
    alert.setTitle(title);
    alert.setHeaderText(header);
    alert.setContentText(content);
    alert.showAndWait();
  }
}