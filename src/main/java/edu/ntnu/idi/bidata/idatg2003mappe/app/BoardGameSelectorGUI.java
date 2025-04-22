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
  private Stage primaryStage = new Stage();

  public BoardGameSelectorGUI() {
    // Initialize the controller
    this.boardGameSelectorController = new BoardGameSelectorController();
  }

  public BoardGameSelectorGUI(BoardGameSelectorController boardGameSelectorController) {
    this.boardGameSelectorController = boardGameSelectorController;
  }

  @Override
  public void start(Stage primaryStage) throws Exception {
    this.primaryStage = primaryStage; // Assign the parameter to the class field

    BorderPane borderPane = new BorderPane();
    borderPane.setMinHeight(540); // 16:9 aspect ratio (1920x1080)/2
    borderPane.setMaxHeight(540);
    borderPane.setMinWidth(960);
    borderPane.setMaxWidth(960);
    borderPane.setPrefHeight(540);
    borderPane.setPrefWidth(960);
    borderPane.setCenter(createCenterPane());

    NavBar navBar = new NavBar();
    borderPane.setTop(navBar.createMenuBar());
    Scene scene = new Scene(borderPane);
    primaryStage.setScene(scene);
    primaryStage.setTitle("Select a board game");
    primaryStage.show();

    this.ladderGame = new LadderGameGUI(); // To redirect on button click.
  }

  public void setScene(Scene scene) {
    if (primaryStage == null) {
      throw new IllegalStateException("Scene is not initialized.");
    } else if (scene == null) {
      throw new IllegalArgumentException("Scene is not initialized.");
    }
    this.primaryStage.setScene(scene);
  }

  private Pane createCenterPane() {
    Button button1 = new Button("Ladder game");
    button1.setOnAction(e -> {
      try {
        System.out.println("Switching to ladder game"); //TODO - remove
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