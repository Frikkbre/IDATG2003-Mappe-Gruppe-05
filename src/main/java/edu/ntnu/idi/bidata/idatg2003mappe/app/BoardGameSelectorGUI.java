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
  private LadderGameGUI ladderGameGUI;
  private Stage primaryStage;
  private Spinner<Integer> numberOfPlayers; //TODO - Change this out with int?
  private int currentPlayers;               //TODO - This instead of the spinner?


  /**
   * Method to set the stage of the application.
   * @param primaryStage
   */
  public void setStage(Stage primaryStage) {
    this.primaryStage = primaryStage;
  }

  /**
   * Used to get the stage of the application.
   * Used in other game classes to add their scene to the stage.
   * @return the primaryStage
   */
  public Stage getStage() {
    return primaryStage;
  }

  /*public void setNumberOfPlayers(int numberOfPlayers) { //TODO - remove this since its allready in the LadderGameGUI
    if (numberOfPlayers >= 2  && numberOfPlayers <= 6) {
      this.currentPlayers = numberOfPlayers;
    }else {
      throw new IllegalArgumentException("Number of players must be between 2 and 6");
    }
  }*/

  /**
   * Method to return the number of players.
   * Used to determine the number of players in the game.
   * @return number of players selected on spinner
   */
  public int getNumberOfPlayers() {
    System.out.println("Number of players: " + currentPlayers);
    return currentPlayers;
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


    this.ladderGameGUI = new LadderGameGUI(); //To redirect on button click.
  }

  private Pane createCenterPane() {
    Button button1 = new Button("Ladder game");
    button1.setOnAction(event -> {
      try {
        // Explicitly update currentPlayers before starting the game
        ladderGameGUI.setNumberOfPlayers(numberOfPlayers.getValue());
        System.out.println("Button 1 pressed. Number of players: " + getNumberOfPlayers());
        ladderGameGUI.start(getStage());
      } catch (Exception e) {
        e.printStackTrace();
      }
    });

    Button button2 = new Button("Missing diamond");
    button2.setOnAction(event -> {
      try {
        // Explicitly update currentPlayers before starting the game
        ladderGameGUI.setNumberOfPlayers(numberOfPlayers.getValue());
        System.out.println("Button 2 pressed. Number of players: " + getNumberOfPlayers());
        // MissingDiamond.start(getStage()); TODO - implement diamond game start method.
      } catch (Exception e) {
        e.printStackTrace();
      }
    });

    numberOfPlayers = new Spinner<>(2, 6, 2); // Initialize the spinner
    numberOfPlayers.setEditable(true);

    // Add a listener to update currentPlayers whenever the spinner value changes
    numberOfPlayers.valueProperty().addListener((obs, oldValue, newValue) -> {
      ladderGameGUI.setNumberOfPlayers(newValue);
      System.out.println("Spinner value changed. Current players: " + currentPlayers); //TODO - remove this
    });

    FlowPane centerPane = new FlowPane();
    centerPane.getChildren().addAll(button1, button2, numberOfPlayers);
    centerPane.setAlignment(Pos.CENTER);
    return centerPane;
  }

  public static void main(String[] args) {
    launch(args);
  }
}
