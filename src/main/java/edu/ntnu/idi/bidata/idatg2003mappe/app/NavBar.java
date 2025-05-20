package edu.ntnu.idi.bidata.idatg2003mappe.app;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.opencsv.exceptions.CsvValidationException;
import edu.ntnu.idi.bidata.idatg2003mappe.entity.Player;
import edu.ntnu.idi.bidata.idatg2003mappe.filehandling.exceptionhandling.FileHandlingException;
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

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class NavBar {
  BoardGameSelectorGUI boardGameSelectorGUI = new BoardGameSelectorGUI();

  private static final String LAST_SAVE_DIR = "src/main/resources/saves";
  private static final String LAST_SAVE_FILE = "LastSave.csv";
  private static final String FULL_PATH = LAST_SAVE_DIR + "/" + LAST_SAVE_FILE;

  private Stage stage;
  private Object gameController;
  private LadderGameGUI ladderGameGUI; // Reference to the ladder game GUI

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
        if(getStage().equals(boardGameSelectorGUI.getStage())) {
          throw new FileHandlingException("Already in main menu.");
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
      try {
        // Make sure directory exists
        File saveDir = new File(LAST_SAVE_DIR);
        if (!saveDir.exists()) {
          saveDir.mkdirs();
        }

        // Get players from the controller
        List<Player> players = getPlayersFromController();

        if (players == null || players.isEmpty()) {
          showAlert(Alert.AlertType.ERROR, "Error", "Save Error", "No players found to save.");
          return;
        }

        // Save to CSV
        File csvFile = new File(FULL_PATH);
        FileWriter outputFile = new FileWriter(csvFile);
        CSVWriter writer = new CSVWriter(outputFile);

        // Write header
        String[] header = {"Player Name", "ID", "Color", "Position"};
        writer.writeNext(header);

        // Write player data
        for (Player player : players) {
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

  private EventHandler<ActionEvent> loadLastSave() {
    return event -> {
      try {
        File file = new File(FULL_PATH);
        if (!file.exists() || !file.isFile()) {
          showAlert(Alert.AlertType.INFORMATION, "No Save Found", "No Save File Found",
              "There is no saved game to load.");
          return;
        }

        // Read from CSV
        CSVReader reader = new CSVReader(new FileReader(file));
        String[] nextLine;

        // Skip header
        reader.readNext();

        // Read player data
        List<PlayerData> playerDataList = new ArrayList<>();
        while ((nextLine = reader.readNext()) != null) {
          String name = nextLine[0];
          int id = Integer.parseInt(nextLine[1]);
          String color = nextLine[2];
          int position = Integer.parseInt(nextLine[3]);

          playerDataList.add(new PlayerData(name, id, color, position));
        }

        reader.close();

        // Apply the loaded data to the current game
        boolean success = applyPlayerDataToGame(playerDataList);

        if (success) {
          showAlert(Alert.AlertType.INFORMATION, "Game Loaded", "Game Loaded Successfully",
              "Your last saved game has been loaded.");
        } else {
          showAlert(Alert.AlertType.ERROR, "Error", "Load Error",
              "Could not apply loaded data to the current game.");
        }
      } catch (IOException | CsvValidationException ex) {
        showAlert(Alert.AlertType.ERROR, "Error", "Load Error",
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