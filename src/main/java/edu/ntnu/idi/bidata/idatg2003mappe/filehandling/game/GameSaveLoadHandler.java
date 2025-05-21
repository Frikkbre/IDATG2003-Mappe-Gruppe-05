package edu.ntnu.idi.bidata.idatg2003mappe.filehandling.game;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import edu.ntnu.idi.bidata.idatg2003mappe.app.NavBar;
import edu.ntnu.idi.bidata.idatg2003mappe.app.laddergame.LadderGameController;
import edu.ntnu.idi.bidata.idatg2003mappe.app.laddergame.LadderGameGUI;
import edu.ntnu.idi.bidata.idatg2003mappe.app.missingdiamond.MissingDiamondController;
import edu.ntnu.idi.bidata.idatg2003mappe.app.missingdiamond.MissingDiamondGUI;
import edu.ntnu.idi.bidata.idatg2003mappe.entity.Player;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Alert;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * Handles saving and loading game states for both Ladder Game and Missing Diamond.
 */
public class GameSaveLoadHandler {
  private LadderGameController ladderGameController;
  private MissingDiamondController missingDiamondController;
  private Player player;

  private static final String lastSaveDir = "src/main/resources/saves";
  private static final String lastSaveFile = "LastSave.csv";
  private static final String fullPath = lastSaveDir + "/" + lastSaveFile;

  private Object gameController;

  /**
   * Constructor for GameSaveLoadHandler.
    * @param playersFromController
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
        FileWriter outputFile = new FileWriter(csvFile);
        CSVWriter writer = new CSVWriter(outputFile);

        // Write header
        String[] header = {"Player Name", "ID", "Color", "Position"};
        writer.writeNext(header);

        // Write player data
        for (Player player : playersFromController) {
          String[] data = {
              player.getName(),
              String.valueOf(player.getID()),
              player.getColor(),
              String.valueOf(player.getCurrentTile().getTileId())
          };
          writer.writeNext(data);
        }

        writer.close();

        showAlert(Alert.AlertType.INFORMATION, "Game Saved", "Game Saved Successfully",
            "Your game has been saved to LastSave.csv");
      } catch (Exception ex) {
        showAlert(Alert.AlertType.ERROR, "Error", "Save Error",
            "Could not save the game: " + ex.getMessage());
      }
    };
  }

  /**
   * loads a saved ladder game
   * takes in ladderGameGUI
   * and randomLadders to determine if the game is random or not
   * @param ladderGameGUI
   * @param randomLadders
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
      CSVReader reader = new CSVReader(new FileReader(csvFile));
      System.out.println("Loading game from: " + fullPath);

      String[] header = reader.readNext(); // Skip header
      System.out.println("Header: " + String.join(", ", header));

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
          System.out.println(position + " " + playerId + " " + playerName);

          playerPositions.add(new GameState.PlayerPosition(playerName, playerId, position));
        }
      }
      reader.close();

      gameState.setPlayerPositions(playerPositions);

      // Get the controller from NavBar instead of creating a new one
      NavBar navBar = ladderGameGUI.navBar; // Assuming navBar is accessible

      // Apply the game state to the existing controller
      controller.applyGameState(gameState);

      // Show success alert
      Alert alert = new Alert(Alert.AlertType.INFORMATION);
      alert.setTitle("Game Loaded");
      alert.setHeaderText("Game Loaded Successfully");
      alert.setContentText("Your last saved ladder game has been loaded from LastSave.csv");
      alert.showAndWait();

      ladderGameGUI.updateBoardUI();
    } catch (Exception ex) {
      Alert alert = new Alert(Alert.AlertType.ERROR);
      alert.setTitle("Error");
      alert.setHeaderText("Load Error");
      alert.setContentText("Could not load the game: " + ex.getMessage());
      alert.showAndWait();
    }
  }

  /**
   * Loads the last save for the Missing Diamond game.
   *
   * @param missingDiamondGUI The GUI to update after loading the game
   * @param controller The controller to apply the game state to
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
      CSVReader reader = new CSVReader(new FileReader(csvFile));
      System.out.println("Loading game from: " + fullPath);

      String[] header = reader.readNext(); // Skip header
      System.out.println("Header: " + String.join(", ", header));

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
          System.out.println(position + " " + playerId + " " + playerName);

          playerPositions.add(new GameState.PlayerPosition(playerName, playerId, position));
        }
      }
      reader.close();

      gameState.setPlayerPositions(playerPositions);

      // Apply the game state to the existing controller
      controller.applyGameState(gameState);

      // Show success alert
      Alert alert = new Alert(Alert.AlertType.INFORMATION);
      alert.setTitle("Game Loaded");
      alert.setHeaderText("Game Loaded Successfully");
      alert.setContentText("Your last saved missing diamond game has been loaded from LastSave.csv");
      alert.showAndWait();


      // Update the UI
      missingDiamondGUI.updateBoardUI();
    } catch (Exception ex) {
      Alert alert = new Alert(Alert.AlertType.ERROR);
      alert.setTitle("Error");
      alert.setHeaderText("Load Error");
      alert.setContentText("Could not load the game: " + ex.getMessage());
      alert.showAndWait();
    }
  }

  /**
   * Gets the players from the game controller.
   * @return
   */
  private List<Player> getPlayersFromController() {
    if (gameController instanceof LadderGameController) {
      return ((LadderGameController) gameController).getPlayers();
    } else if (gameController instanceof MissingDiamondController) {
      return ((MissingDiamondController) gameController).getPlayers();
    }
    return null;
  }

  /**
   * shows alert when saving and loading
   * positive and negative
   * @param type
   * @param title
   * @param header
   * @param content
   */
  private void showAlert(Alert.AlertType type, String title, String header, String content) {
    Alert alert = new Alert(type);
    alert.setTitle(title);
    alert.setHeaderText(header);
    alert.setContentText(content);
    alert.showAndWait();
  }
}
