package edu.ntnu.idi.bidata.idatg2003mappe.app;

import edu.ntnu.idi.bidata.idatg2003mappe.entity.Player;
import edu.ntnu.idi.bidata.idatg2003mappe.filehandling.game.GameSaveLoadHandler;
import edu.ntnu.idi.bidata.idatg2003mappe.filehandling.game.GameState;
import edu.ntnu.idi.bidata.idatg2003mappe.app.laddergame.LadderGameController;
import edu.ntnu.idi.bidata.idatg2003mappe.app.laddergame.LadderGameGUI;
import edu.ntnu.idi.bidata.idatg2003mappe.app.missingdiamond.MissingDiamondController;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Alert;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;

public class NavBar {
  BoardGameSelectorGUI boardGameSelectorGUI = new BoardGameSelectorGUI();
  GameSaveLoadHandler gameSaveLoadHandler = new GameSaveLoadHandler();

  private static final String lastSaveDir = "src/main/resources/saves";
  private static final String lastSaveFile = "LastSave.csv";
  private static final String fullPath = lastSaveDir + "/" + lastSaveFile;

  private Stage stage;
  private Object gameController;
  private LadderGameGUI ladderGameGUI = new LadderGameGUI(); // Reference to the ladder game GUI

  public Stage getStage() {
    return stage;
  }

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
   * Set the ladder game GUI instance
   * @param gui The LadderGameGUI instance
   */
  public void setLadderGameGUI(LadderGameGUI gui) {
    this.ladderGameGUI = gui;
  }

  public MenuBar createMenuBar() {

    MenuItem quickSaveMenuItem = new MenuItem("Quick Save");
    quickSaveMenuItem.setOnAction(gameSaveLoadHandler.quickSaveGame(getPlayersFromController()));

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
    menuBar.getMenus().addAll(fileMenu, navigateMenu);
    menuBar.setStyle("-fx-background-color: #57B9FF;");

    return menuBar;
  }

  private EventHandler<ActionEvent> determineGameTypeAndLoad() {
    return event -> {
      if (gameController instanceof LadderGameController) {
        LadderGameGUI ladderGameGUI = this.ladderGameGUI;
        gameSaveLoadHandler.loadLastSaveLadderGame(ladderGameGUI, ((LadderGameController) gameController).isRandomLadders());
      } else if (gameController instanceof MissingDiamondController) {
        gameSaveLoadHandler.loadLastSaveMissingDiamond();
      }
    };
  }

  private EventHandler<ActionEvent> closeFile() {
    return event -> {
      System.exit(0);
    };
  }

  //TODO - remove
  private void showAlert(Alert.AlertType type, String title, String header, String content) {
    Alert alert = new Alert(type);
    alert.setTitle(title);
    alert.setHeaderText(header);
    alert.setContentText(content);
    alert.showAndWait();
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
   * Applies player data loaded from CSV to the current game
   * @param playerDataList List of player data
   * @return true if successful, false otherwise
   */
  private boolean applyPlayerDataToGame(List<PlayerData> playerDataList) {
    if (gameController instanceof LadderGameController) {
      // For LadderGameController, we need to create a GameState and apply it
      LadderGameController ladderGameController = (LadderGameController) gameController;

      // Create player positions for GameState
      List<GameState.PlayerPosition> positions = new ArrayList<>();
      for (PlayerData data : playerDataList) {
        positions.add(new GameState.PlayerPosition(
            data.getName(), data.getId(), data.getPosition()));
      }

      // Create GameState
      GameState ladderGameState = new GameState();
      ladderGameState.setPlayerPositions(positions);
      ladderGameState.setRandomLadders(ladderGameController.isRandomLadders());
      ladderGameState.setCurrentPlayerIndex(ladderGameController.getCurrentPlayerIndex());

      // Apply GameState
      ladderGameController.applyGameState(ladderGameState);

      // Update the board UI if ladder game GUI is set
      if (ladderGameGUI != null) {
        ladderGameGUI.updateBoardUI();
      }

      return true;
    } else if (gameController instanceof MissingDiamondController) {
      MissingDiamondController missingDiamondController = (MissingDiamondController) gameController;

      // Create player positions for GameState
      List<GameState.PlayerPosition> positions = new ArrayList<>();
      for (PlayerData data : playerDataList) {
        positions.add(new GameState.PlayerPosition(
            data.getName(), data.getId(), data.getPosition()));
      }

      // Create GameState
      GameState missingDiamondGameState = new GameState();
      missingDiamondGameState.setPlayerPositions(positions);
      missingDiamondGameState.setCurrentPlayerIndex(missingDiamondController.getCurrentPlayerIndex());

      // Apply GameState
      missingDiamondController.applyGameState(missingDiamondGameState);

      // Update the board UI if ladder game GUI is set
      if (ladderGameGUI != null) {
        ladderGameGUI.updateBoardUI();
      }
      return true;
    }
    return false;
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