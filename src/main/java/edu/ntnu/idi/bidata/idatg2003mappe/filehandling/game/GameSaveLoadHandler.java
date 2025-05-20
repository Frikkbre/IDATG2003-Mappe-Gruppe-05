package edu.ntnu.idi.bidata.idatg2003mappe.filehandling.game;

import edu.ntnu.idi.bidata.idatg2003mappe.app.laddergame.LadderGameController;
import edu.ntnu.idi.bidata.idatg2003mappe.app.laddergame.LadderGameGUI;
import edu.ntnu.idi.bidata.idatg2003mappe.app.missingdiamond.MissingDiamondController;
import edu.ntnu.idi.bidata.idatg2003mappe.filehandling.exceptionhandling.FileHandlingException;
import javafx.scene.control.Alert;

public class GameSaveLoadHandler {
  private LadderGameController ladderGameController;
  private MissingDiamondController missingDiamondController;

  public void quickSaveGameLadderGame(){
    try {
      BoardFileHandler fileHandler = new BoardFileHandler();
      GameState gameState = ladderGameController.createGameState();
      fileHandler.saveToDefaultLocation(gameState);

      Alert alert = new Alert(Alert.AlertType.INFORMATION);
      alert.setTitle("Game Saved");
      alert.setHeaderText("Game Saved Successfully");
      alert.setContentText("Your game has been saved to the default location.");
      alert.showAndWait();
    } catch (FileHandlingException ex) {
      Alert alert = new Alert(Alert.AlertType.ERROR);
      alert.setTitle("Error");
      alert.setHeaderText("Save Error");
      alert.setContentText("Could not save the game: " + ex.getMessage());
      alert.showAndWait();
    }
  }


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
      alert.setContentText("Your last saved game has been loaded.");
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
}
