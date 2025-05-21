package edu.ntnu.idi.bidata.idatg2003mappe.app.common.ui;

import edu.ntnu.idi.bidata.idatg2003mappe.app.boardgameselector.BoardGameSelectorGUI;
import edu.ntnu.idi.bidata.idatg2003mappe.filehandling.exceptionhandling.FileHandlingException;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.*;
import javafx.stage.Stage;


public class NavBar {
  BoardGameSelectorGUI boardGameSelectorGUI = new BoardGameSelectorGUI();

  private Stage stage;

  public Stage getStage() {
    return stage;
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
        if (getStage().equals(boardGameSelectorGUI.getStage())) {
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
    };
  }

  private EventHandler<ActionEvent> loadLastSave() {
    return event -> {
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
