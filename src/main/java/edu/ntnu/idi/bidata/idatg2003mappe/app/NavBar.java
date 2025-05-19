package edu.ntnu.idi.bidata.idatg2003mappe.app;

import edu.ntnu.idi.bidata.idatg2003mappe.filehandling.exceptionhandling.FileHandlingException;
import edu.ntnu.idi.bidata.idatg2003mappe.filehandling.game.BoardFileHandler;
import edu.ntnu.idi.bidata.idatg2003mappe.filehandling.game.GameState;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Alert;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;

public class NavBar {
  BoardGameSelectorGUI boardGameSelectorGUI = new BoardGameSelectorGUI();

  public interface GameStateProvider {
    GameState getCurrentGameState();
    void loadGameState(GameState gameState);
  }

  private GameStateProvider gameStateProvider;
  private Stage stage;

  public Stage getStage() {
    return stage;
  }

  public void setGameStateProvider(GameStateProvider provider) {
    this.gameStateProvider = provider;
  }

  public void setStage(Stage stage) {
    this.stage = stage;
  }

  public MenuBar createMenuBar() {
    MenuItem openMenuItem = new MenuItem("Open");
    openMenuItem.setOnAction(openFile());

    MenuItem quickSaveMenuItem = new MenuItem("Quick Save");
    quickSaveMenuItem.setOnAction(quickSaveGame());

    MenuItem loadLastSaveMenuItem = new MenuItem("Load Last Save");
    loadLastSaveMenuItem.setOnAction(loadLastSave());

    MenuItem closeMenuItem = new MenuItem("Close");
    closeMenuItem.setOnAction(closeFile());

    Menu fileMenu = new Menu("File");
    fileMenu.getItems().addAll(
        quickSaveMenuItem,
        loadLastSaveMenuItem,
        new SeparatorMenuItem(),
        openMenuItem,
        new SeparatorMenuItem(),
        closeMenuItem
    );


    Menu navigateMenu = new Menu("Navigate");
    MenuItem navigateMenuItem = new MenuItem("Return to Main Menu");
    navigateMenuItem.setOnAction(event -> {
      try {
        boardGameSelectorGUI.start(getStage());
      } catch (Exception e) {
        throw new RuntimeException(e);
      }
    });

    navigateMenu.getItems().addAll(navigateMenuItem);



    MenuBar menuBar = new MenuBar();
    menuBar.getMenus().addAll(fileMenu, navigateMenu);
    menuBar.setStyle("-fx-background-color: #57B9FF;");

    return menuBar;
  }

  private EventHandler<ActionEvent> openFile() {
    // Implement file opening logic here
    return null;
  }
  private EventHandler<ActionEvent> saveFile() {
    // Implement file saving logic here
    return null;
  }

  private EventHandler<ActionEvent> quickSaveGame() {
    return event -> {
      if (gameStateProvider == null) {
        showAlert(Alert.AlertType.WARNING, "Save Error",
            "No active game",
            "There is no active game to save.");
        return;
      }

      try {
        BoardFileHandler fileHandler = new BoardFileHandler();
        GameState gameState = gameStateProvider.getCurrentGameState();
        fileHandler.saveToDefaultLocation(gameState);

        showAlert(Alert.AlertType.INFORMATION, "Game Saved",
            "Game Saved Successfully",
            "Your game has been saved to the default location.");
      } catch (FileHandlingException ex) {
        showAlert(Alert.AlertType.ERROR, "Save Error",
            "Save Error",
            "Could not save the game: " + ex.getMessage());
      }
    };
  }

  private EventHandler<ActionEvent> loadLastSave() {
    return event -> {
      if (gameStateProvider == null) {
        showAlert(Alert.AlertType.WARNING, "Load Error",
            "No active game",
            "There is no active game to load data into.");
        return;
      }

      BoardFileHandler fileHandler = new BoardFileHandler();

      if (!fileHandler.defaultSaveExists()) {
        showAlert(Alert.AlertType.INFORMATION, "No Save Found",
            "No Save File Found",
            "There is no saved game to load.");
        return;
      }

      try {
        GameState gameState = fileHandler.loadFromDefaultLocation();
        gameStateProvider.loadGameState(gameState);

        showAlert(Alert.AlertType.INFORMATION, "Game Loaded",
            "Game Loaded Successfully",
            "Your last saved game has been loaded.");
      } catch (FileHandlingException ex) {
        showAlert(Alert.AlertType.ERROR, "Load Error",
            "Load Error",
            "Could not load the game: " + ex.getMessage());
      }
    };
  }

  private EventHandler<ActionEvent> closeFile() {
    return event -> {
      System.exit(0);
    };
  }

  private void showAlert(Alert.AlertType type, String title, String header, String content) {
    Alert alert = new Alert(type);
    alert.setTitle(title);
    alert.setHeaderText(header);
    alert.setContentText(content);
    alert.showAndWait();
  }
}
