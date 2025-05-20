package edu.ntnu.idi.bidata.idatg2003mappe.filehandling.game;

import com.opencsv.CSVWriter;
import edu.ntnu.idi.bidata.idatg2003mappe.app.laddergame.LadderGameController;
import edu.ntnu.idi.bidata.idatg2003mappe.app.laddergame.LadderGameGUI;
import edu.ntnu.idi.bidata.idatg2003mappe.app.missingdiamond.MissingDiamondController;
import edu.ntnu.idi.bidata.idatg2003mappe.entity.Player;
import edu.ntnu.idi.bidata.idatg2003mappe.filehandling.exceptionhandling.FileHandlingException;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Alert;

import java.io.File;
import java.io.FileWriter;
import java.util.List;

public class GameSaveLoadHandler {
  private LadderGameController ladderGameController;
  private MissingDiamondController missingDiamondController;
  private Player player;

  private static final String lastSaveDir = "src/main/resources/saves";
  private static final String lastSaveFile = "LastSave.csv";
  private static final String fullPath = lastSaveDir + "/" + lastSaveFile;

  private Object gameController;

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
  public void loadLastSaveLadderGame(LadderGameGUI ladderGameGUI, boolean randomLadders) {
    BoardFileHandler fileHandler = new BoardFileHandler();

    if (!fileHandler.defaultSaveExists()) {
      Alert alert = new Alert(Alert.AlertType.INFORMATION);
      alert.setTitle("No Save Found");
      alert.setHeaderText("No Save File Found");
      alert.setContentText("There is no saved game to load.");
      alert.showAndWait();
      return;
    }

    try {
      GameState gameState = fileHandler.loadFromDefaultLocation();

      // Create a new game with the loaded state
      randomLadders = gameState.isRandomLadders();
      ladderGameController = new LadderGameController(randomLadders);
      ladderGameController.applyGameState(gameState);

      Alert alert = new Alert(Alert.AlertType.INFORMATION);
      alert.setTitle("Game Loaded");
      alert.setHeaderText("Game Loaded Successfully");
      alert.setContentText("Your last saved ladder game has been loaded");
      alert.showAndWait();

      ladderGameGUI.updateBoardUI();
    } catch (FileHandlingException ex) {
      Alert alert = new Alert(Alert.AlertType.ERROR);
      alert.setTitle("Error");
      alert.setHeaderText("Load Error");
      alert.setContentText("Could not load the game: " + ex.getMessage());
      alert.showAndWait();
    }
  }

  /**
   * Loads the last save for the Missing Diamond game.
   */
  public void loadLastSaveMissingDiamond(){
    BoardFileHandler fileHandler = new BoardFileHandler();

    if (!fileHandler.defaultSaveExists()) {
      Alert alert = new Alert(Alert.AlertType.INFORMATION);
      alert.setTitle("No Save Found");
      alert.setHeaderText("No Save File Found");
      alert.setContentText("There is no saved game to load.");
      alert.showAndWait();
      return;
    }

    try {
      GameState gameState = fileHandler.loadFromDefaultLocation();

      // Create a new game with the loaded state
      missingDiamondController = new MissingDiamondController();
      missingDiamondController.applyGameState(gameState);

      Alert alert = new Alert(Alert.AlertType.INFORMATION);
      alert.setTitle("Game Loaded");
      alert.setHeaderText("Game Loaded Successfully");
      alert.setContentText("Your last saved missing diamond game has been loaded");
      alert.showAndWait();

    } catch (FileHandlingException ex) {
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
