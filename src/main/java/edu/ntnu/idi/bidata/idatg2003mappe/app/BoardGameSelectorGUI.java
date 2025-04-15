package edu.ntnu.idi.bidata.idatg2003mappe.app;

import edu.ntnu.idi.bidata.idatg2003mappe.app.laddergame.LadderGameGUI;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

/**
 * Class for the board game selector GUI.
 */
public class BoardGameSelectorGUI extends Application {
  private BoardGameSelectorController boardGameSelectorController;
  private LadderGameGUI ladderGame;

  public BoardGameSelectorGUI() {
    // Initialize the controller
    this.boardGameSelectorController = new BoardGameSelectorController();
  }

  public BoardGameSelectorGUI(BoardGameSelectorController boardGameSelectorController) {
    this.boardGameSelectorController = boardGameSelectorController;
  }

  @Override
  public void start(Stage primaryStage) throws Exception {
    BorderPane borderPane = new BorderPane();
    borderPane.setMinHeight(540); // 16:9 aspect ratio (1920x1080)/2
    borderPane.setMaxHeight(540);
    borderPane.setMinWidth(960);
    borderPane.setMaxWidth(960);
    borderPane.setPrefHeight(540);
    borderPane.setPrefWidth(960);
    borderPane.setCenter(createCenterPane());
    borderPane.setTop(createMenuBar());
    Scene scene = new Scene(borderPane);
    primaryStage.setScene(scene);
    primaryStage.setTitle("Select a board game");
    primaryStage.show();

    this.ladderGame = new LadderGameGUI(); // To redirect on button click.
  }

  private MenuBar createMenuBar() {
    MenuItem openMenuItem = new MenuItem("Open");
    MenuItem saveMenuItem = new MenuItem("Save");
    MenuItem closeMenuItem = new MenuItem("Close");
    Menu fileMenu = new Menu("File");
    fileMenu.getItems().addAll(openMenuItem, saveMenuItem, new SeparatorMenuItem(), closeMenuItem);
    MenuBar menuBar = new MenuBar();
    menuBar.getMenus().addAll(fileMenu);
    return menuBar;
  }

  private Pane createCenterPane() {
    Button button1 = new Button("Ladder game");
    button1.setOnAction(e -> {
      try {
        System.out.println("Switching to ladder game");
        boardGameSelectorController.switchGame(1);
      } catch (Exception ex) {
        System.out.println(ex);
      }
    });

    Button button2 = new Button("Missing diamond");
    button2.setOnAction(e -> {
      try {
        boardGameSelectorController.switchGame(2);
      } catch (Exception ex) {
        ex.printStackTrace();
      }
    });

    FlowPane centerPane = new FlowPane();
    centerPane.getChildren().addAll(button1, button2);
    centerPane.setAlignment(Pos.CENTER);
    return centerPane;
  }

  public static void main(String[] args) {
    launch(args);
  }
}