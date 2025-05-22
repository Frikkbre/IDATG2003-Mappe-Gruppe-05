package edu.ntnu.idi.bidata.idatg2003mappe.app.common.ui;

import edu.ntnu.idi.bidata.idatg2003mappe.app.boardgameselector.BoardGameSelectorGUI;
import edu.ntnu.idi.bidata.idatg2003mappe.app.laddergame.controller.LadderGameController;
import edu.ntnu.idi.bidata.idatg2003mappe.app.laddergame.ui.LadderGameGUI;
import edu.ntnu.idi.bidata.idatg2003mappe.app.missingdiamond.controller.MissingDiamondController;
import edu.ntnu.idi.bidata.idatg2003mappe.app.missingdiamond.ui.MissingDiamondGUI;

import edu.ntnu.idi.bidata.idatg2003mappe.entity.player.Player;
import edu.ntnu.idi.bidata.idatg2003mappe.filehandling.game.GameSaveLoadHandler;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Alert;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.stage.Stage;

import java.util.List;

public class NavBar {
  BoardGameSelectorGUI boardGameSelectorGUI = new BoardGameSelectorGUI();
  private LadderGameGUI ladderGameGUI = new LadderGameGUI();
  private MissingDiamondGUI missingDiamondGUI = new MissingDiamondGUI();
  GameSaveLoadHandler gameSaveLoadHandler = new GameSaveLoadHandler();

  private static final String lastSaveDir = "src/main/resources/saves";
  private static final String lastSaveFile = "LastSave.csv";

  private Stage stage;
  public Object gameController;

  /**
   * returns the stage of the NavBar
   * @return stage
   */
  public Stage getStage() {
    return stage;
  }

  /**
   * Set the stage for this NavBar
   * @param stage The stage to set
   */
  public void setStage(Stage stage) {
    this.stage = stage;
  }

  /**
   * Set the game controller for this NavBar
   * @param controller The game controller (LadderGameController or MissingDiamondController)
   */
  public void setGameController(Object controller) {
    this.gameController = controller;
  }

  /**
   * Set the missing diamond GUI instance
   * @param gui The MissingDiamondGUI instance
   */
  public void setMissingDiamondGUI(MissingDiamondGUI gui) {
    this.missingDiamondGUI = gui;
  }

  public MenuBar createMenuBar() {

    MenuItem quickSaveMenuItem = new MenuItem("Quick Save");
    quickSaveMenuItem.setOnAction(event -> {
      List<Player> players = getPlayersFromController();
      if (players != null && !players.isEmpty()) {
        gameSaveLoadHandler.quickSaveGame(players).handle(event);
      } else {
        // Show error message
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText("Save Error");
        alert.setContentText("No players found to save.");
        alert.showAndWait();
      }
    });

    MenuItem loadLastSaveMenuItem = new MenuItem("Load Last Save");
    loadLastSaveMenuItem.setOnAction(determineGameTypeAndLoad());

    MenuItem closeMenuItem = new MenuItem("Close");
    closeMenuItem.setOnAction(closeFile());

    Menu fileMenu = new Menu("File");
    fileMenu.getItems().addAll(
        quickSaveMenuItem,
        loadLastSaveMenuItem,
        new SeparatorMenuItem(),
        closeMenuItem
    );

    Menu modeMenu = new Menu("Mode");
    MenuItem randomLadders = new MenuItem("Toggle Random Ladders");
    randomLadders.setOnAction(event -> {
      if (gameController instanceof LadderGameController) {
        LadderGameController ladderController = (LadderGameController) gameController;
        boolean newRandomState = !ladderController.isRandomLadders();

        try {
          // Create new LadderGameGUI with toggled random state
          LadderGameGUI newLadderGUI = new LadderGameGUI();
          newLadderGUI.randomLadders = newRandomState;
          newLadderGUI.start(getStage());

          Alert alert = new Alert(Alert.AlertType.INFORMATION);
          alert.setTitle("Mode Changed");
          alert.setHeaderText("Random Ladders Mode");
          alert.setContentText("Random ladders mode is now " + (newRandomState ? "enabled" : "disabled") +
              ". Game has been restarted.");
          alert.showAndWait();
        } catch (Exception e) {
          System.err.println("Error restarting ladder game: " + e.getMessage());
        }
      }
    });

    modeMenu.getItems().addAll(randomLadders);

    Menu navigateMenu = new Menu("Navigate");
    MenuItem navigateMenuItem = new MenuItem("Return to Main Menu");
    navigateMenuItem.setOnAction(event -> {
      try {
        if(getStage().equals(boardGameSelectorGUI.getStage())) {
          throw new IllegalArgumentException("Already in main menu.");
        }
        boardGameSelectorGUI.start(getStage());
      } catch (Exception e) {
        throw new RuntimeException(e);
      }
    });

    navigateMenu.getItems().addAll(navigateMenuItem);

    MenuBar menuBar = new MenuBar();
    menuBar.getMenus().addAll(fileMenu, modeMenu, navigateMenu);
    menuBar.setStyle("-fx-background-color: #57B9FF;");

    return menuBar;
  }

  /**
   * Determines the game type and loads the last save accordingly
   * @return EventHandler for loading the last save
   */
  private EventHandler<ActionEvent> determineGameTypeAndLoad() {
    return event -> {
      if (gameController instanceof LadderGameController) {
        LadderGameGUI ladderGameGUI = this.ladderGameGUI;
        gameSaveLoadHandler.loadLastSaveLadderGame(ladderGameGUI, (LadderGameController) gameController,
            ((LadderGameController) gameController).isRandomLadders());
      } else if (gameController instanceof MissingDiamondController) {
        MissingDiamondGUI missingDiamondGUI = this.missingDiamondGUI; // Add this field to NavBar
        gameSaveLoadHandler.loadLastSaveMissingDiamond(missingDiamondGUI, (MissingDiamondController) gameController);
      }
    };
  }

  /**
   * Closes the application
   * @return EventHandler for closing the application
   */
  private EventHandler<ActionEvent> closeFile() {
    return event -> {
      System.exit(0);
    };
  }

  /**
   * Gets the list of players from the current game controller
   * @return List of players or null if no controller is set
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
   * Helper class to store player data from CSV
   */
  public static class PlayerData {
    private final String name;
    private final int id;
    private final String color;
    private final int position;

    public PlayerData(String name, int id, String color, int position) {
      this.name = name;
      this.id = id;
      this.color = color;
      this.position = position;
    }

    public String getName() {
      return name;
    }

    public int getId() {
      return id;
    }

    public String getColor() {
      return color;
    }

    public int getPosition() {
      return position;
    }
  }
}