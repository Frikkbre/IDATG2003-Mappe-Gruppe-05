package edu.ntnu.idi.bidata.idatg2003mappe.app;

import edu.ntnu.idi.bidata.idatg2003mappe.app.laddergame.LadderGame;
import edu.ntnu.idi.bidata.idatg2003mappe.app.laddergame.LadderGameGUI;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.stage.Stage;

public class NavBar {
  private LadderGameGUI ladderGameGUI = new LadderGameGUI();

  public MenuBar createMenuBar() {
    MenuItem openMenuItem = new MenuItem("Open");
    openMenuItem.setOnAction(openFile());

    MenuItem saveMenuItem = new MenuItem("Save");
    saveMenuItem.setOnAction(saveFile());

    MenuItem closeMenuItem = new MenuItem("Close");
    closeMenuItem.setOnAction(closeFile());

    Menu fileMenu = new Menu("File");

    fileMenu.getItems().addAll(openMenuItem, saveMenuItem, new SeparatorMenuItem(), closeMenuItem);
    MenuBar menuBar = new MenuBar();
    menuBar.getMenus().addAll(fileMenu);
    menuBar.setStyle("-fx-background-color: #57B9FF;");
        /*+ "-fx-text-fill: #000000;"
        + "-fx-font-size: 14px;"
        + "-fx-padding: 10px;");*/
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
  private EventHandler<ActionEvent> closeFile() {
    return event -> {
      System.exit(0);
    };
  }

  public MenuBar createMenuBarLadders(Stage primaryStage) {
    MenuItem openMenuItem = new MenuItem("Open");
    openMenuItem.setOnAction(openFile());

    MenuItem saveMenuItem = new MenuItem("Save");
    saveMenuItem.setOnAction(saveFile());

    MenuItem closeMenuItem = new MenuItem("Close");
    closeMenuItem.setOnAction(closeFile());

    Menu fileMenu = new Menu("File");

    fileMenu.getItems().addAll(openMenuItem, saveMenuItem, new SeparatorMenuItem(), closeMenuItem);
    MenuBar menuBar = new MenuBar();
    menuBar.getMenus().addAll(fileMenu);
    menuBar.setStyle("-fx-background-color: #57B9FF;");
        /*+ "-fx-text-fill: #000000;"
        + "-fx-font-size: 14px;"
        + "-fx-padding: 10px;");*/

    Menu settingsMenu = new Menu("Settings");
    MenuItem toggleModeItem = new MenuItem("Toggle Classic/Random Mode");
    MenuItem restartGameItem = new MenuItem("Restart Game");
    toggleModeItem.setOnAction(e -> ladderGameGUI.toggleGameMode(primaryStage));
    restartGameItem.setOnAction(e -> ladderGameGUI.restartGame(primaryStage));
    settingsMenu.getItems().addAll(toggleModeItem, restartGameItem);

    // Add both menus to the menu bar
    menuBar.getMenus().addAll(fileMenu, settingsMenu);
    return menuBar;
  }
}
