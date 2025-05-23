package edu.ntnu.idi.bidata.idatg2003mappe.app.common.ui;

import edu.ntnu.idi.bidata.idatg2003mappe.app.boardgameselector.BoardGameSelector;
import edu.ntnu.idi.bidata.idatg2003mappe.app.laddergame.controller.LadderGameController;
import edu.ntnu.idi.bidata.idatg2003mappe.app.laddergame.ui.LadderGameGUI;
import edu.ntnu.idi.bidata.idatg2003mappe.app.missingdiamond.controller.MissingDiamondController;
import edu.ntnu.idi.bidata.idatg2003mappe.app.missingdiamond.ui.MissingDiamondGUI;
import edu.ntnu.idi.bidata.idatg2003mappe.entity.player.Player;
import edu.ntnu.idi.bidata.idatg2003mappe.filehandling.game.GameSaveLoadHandler;
import edu.ntnu.idi.bidata.idatg2003mappe.util.map.MapDesignerTool;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;

/**
 * <p>Navigation bar component for the board game application.</p>
 * <p>Provides menu options for saving/loading games, navigation between screens,
 * and game-specific functionality.</p>
 *
 * @author Simen Gudbrandsen and Frikk Breadsroed
 * @version 0.0.2
 * @since 21.05.2025
 */
public class NavBar {
  private final LadderGameGUI ladderGameGUI = new LadderGameGUI();
  public Object gameController;
  BoardGameSelector boardGameSelector = new BoardGameSelector();
  GameSaveLoadHandler gameSaveLoadHandler = new GameSaveLoadHandler();
  private MissingDiamondGUI missingDiamondGUI = new MissingDiamondGUI();
  private Stage stage;
  private MapDesignerTool mapDesignerTool;

  /**
   * <p>Returns the stage associated with this NavBar.</p>
   * <p>The stage is used for navigation between different scenes.</p>
   *
   * @return The JavaFX Stage object
   */
  public Stage getStage() {
    return stage;
  }

  /**
   * <p>Sets the stage for this NavBar.</p>
   * <p>This method associates a JavaFX Stage with this navigation bar
   * to enable scene transitions.</p>
   *
   * @param stage The JavaFX Stage to associate with this NavBar
   */
  public void setStage(Stage stage) {
    this.stage = stage;
  }

  /**
   * <p>Sets the game controller for this NavBar.</p>
   * <p>This method associates either a LadderGameController or
   * MissingDiamondController with this NavBar to enable game-specific actions.</p>
   *
   * @param controller The game controller (LadderGameController or MissingDiamondController)
   */
  public void setGameController(Object controller) {
    this.gameController = controller;
  }

  /**
   * <p>Sets the MissingDiamondGUI instance.</p>
   * <p>This method associates a MissingDiamondGUI instance with this NavBar
   * to access game-specific UI elements.</p>
   *
   * @param gui The MissingDiamondGUI instance to associate with this NavBar
   */
  public void setMissingDiamondGUI(MissingDiamondGUI gui) {
    this.missingDiamondGUI = gui;
  }

  /**
   * <p>Creates the menu bar with all navigation options.</p>
   * <p>Builds a JavaFX MenuBar with File menu and game-specific menus based on the current controller.</p>
   *
   * @return The configured JavaFX MenuBar
   */
  public MenuBar createMenuBar() {

    MenuItem quickSaveMenuItem = getMenuItem();

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
        if (getStage().equals(boardGameSelector.getStage())) {
          throw new IllegalArgumentException("Already in main menu.");
        }
        boardGameSelector.start(getStage());
      } catch (Exception e) {
        throw new RuntimeException(e);
      }
    });

    navigateMenu.getItems().addAll(navigateMenuItem);

    MenuBar menuBar = new MenuBar();

    // Only add Mode menu for Ladder Game
    if (gameController instanceof LadderGameController) {
      Menu modeMenu = new Menu("Mode");
      MenuItem randomLadders = new MenuItem("Toggle Random Ladders");
      randomLadders.setOnAction(event -> {
        LadderGameController ladderGameController = (LadderGameController) gameController;
        boolean newRandomState = !ladderGameController.isRandomLadders();

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
      });

      modeMenu.getItems().addAll(randomLadders);
      menuBar.getMenus().addAll(fileMenu, modeMenu, navigateMenu);
    } else if (gameController instanceof MissingDiamondController && missingDiamondGUI != null) {
      // Missing Diamond - no Mode menu, but add Developer menu
      Menu developerMenu = missingDiamondGUI.getMapDesignerManager().getMapDesignerTool().createDesignerMenu();
      menuBar.getMenus().addAll(fileMenu, navigateMenu, developerMenu);
    } else {
      // Default case - no Mode menu
      menuBar.getMenus().addAll(fileMenu, navigateMenu);
    }

    menuBar.setStyle("-fx-background-color: #57B9FF;");

    return menuBar;
  }

  /**
   * <p>Creates a menu item for the quick save functionality.</p>
   * <p>The created menu item triggers the game save process when clicked.</p>
   *
   * @return A MenuItem configured for quick save functionality
   */
  @NotNull
  private MenuItem getMenuItem() {
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
    return quickSaveMenuItem;
  }

  /**
   * <p>Creates an event handler that loads the appropriate saved game.</p>
   * <p>Determines the game type from the current controller and loads the last save accordingly.</p>
   *
   * @return EventHandler for loading the last saved game
   */
  private EventHandler<ActionEvent> determineGameTypeAndLoad() {
    return event -> {
      if (gameController instanceof LadderGameController) {
        LadderGameGUI ladderGameGUI = this.ladderGameGUI;
        gameSaveLoadHandler.loadLastSaveLadderGame(ladderGameGUI, (LadderGameController) gameController,
            ((LadderGameController) gameController).isRandomLadders());
      } else if (gameController instanceof MissingDiamondController) {
        MissingDiamondGUI missingDiamondGUI = this.missingDiamondGUI;
        gameSaveLoadHandler.loadLastSaveMissingDiamond(missingDiamondGUI, (MissingDiamondController) gameController);
      }
    };
  }

  /**
   * <p>Creates an event handler that closes the application.</p>
   * <p>When triggered, this handler will terminate the application completely.</p>
   *
   * @return EventHandler for closing the application
   */
  private EventHandler<ActionEvent> closeFile() {
    return event -> System.exit(0);
  }

  /**
   * <p>Retrieves the list of players from the current game controller.</p>
   * <p>This method determines the controller type and extracts the player list accordingly.</p>
   *
   * @return List of Player objects from the current game, or an empty list if no controller is set
   */
  private List<Player> getPlayersFromController() {
    if (gameController instanceof LadderGameController ladderGameController) {
      return ladderGameController.getPlayers();
    } else if (gameController instanceof MissingDiamondController missingDiamondController) {
      return missingDiamondController.getPlayers();
    }
    return Collections.emptyList();
  }
}
