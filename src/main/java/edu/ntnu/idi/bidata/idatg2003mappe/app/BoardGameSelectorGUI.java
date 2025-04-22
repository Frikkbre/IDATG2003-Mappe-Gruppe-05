package edu.ntnu.idi.bidata.idatg2003mappe.app;

import edu.ntnu.idi.bidata.idatg2003mappe.app.laddergame.LadderGameGUI;
import edu.ntnu.idi.bidata.idatg2003mappe.app.missingdiamond.MissingDiamond;
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
  private LadderGameGUI LadderGame;
  private Stage primaryStage;

  public Stage getStage() {
    return primaryStage;
  }
    public void setStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

  @Override
  public void start(Stage primaryStage) throws Exception {
    BorderPane borderPane = new BorderPane();
    borderPane.setMinHeight(840); //16:9 aspect ratio   (1920x1080)/2
    borderPane.setMaxHeight(840);
    borderPane.setMinWidth(1440);
    borderPane.setMaxWidth(1440);
    borderPane.setPrefHeight(840);
    borderPane.setPrefWidth(1440);
    borderPane.setCenter(createCenterPane());

    NavBar navBar = new NavBar();
    borderPane.setTop(navBar.createMenuBar());
    Scene scene = new Scene(borderPane);
    setStage(primaryStage);
    primaryStage.setScene(scene);
    primaryStage.setTitle("Select a board game");
    primaryStage.show();


    this.LadderGame = new LadderGameGUI(); //To redirect on button click.
  }

  private Pane createCenterPane() {
    Button button1 = new Button("Ladder game");
    button1.setOnAction(event -> {
      try {
        LadderGame.start(getStage());
      } catch (Exception e) {
        e.printStackTrace();
      }
    });
    Button button2 = new Button("Missing diamond");
    button2.setOnAction(event -> {
      try {
        //MissingDiamond.start(getStage()); TODO - implement diamond game start method.
      } catch (Exception e) {
        e.printStackTrace();
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
