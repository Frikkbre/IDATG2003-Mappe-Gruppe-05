package edu.ntnu.idi.bidata.idatg2003mappe.app;

import com.opencsv.AbstractCSVWriter;
import edu.ntnu.idi.bidata.idatg2003mappe.app.laddergame.LadderGameGUI;
import edu.ntnu.idi.bidata.idatg2003mappe.app.missingdiamond.MissingDiamondGUI;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileWriter;
import com.opencsv.CSVWriter;
import java.io.IOException;

/**
 * Class for the board game selector GUI.
 */
public class BoardGameSelectorGUI extends Application {
  private LadderGameGUI ladderGameGUI;
  private MissingDiamondGUI missingDiamondGUI;
  private Stage primaryStage;
  private Spinner<Integer> numberOfPlayers; //TODO - Change this out with int?
  private int currentPlayers;               //TODO - This instead of the spinner?
  private File playerFile = new File("src/main/resources/saves/playerData/Players.csv");
  private CSVWriter playerWriter;
  private FileWriter outputfile;


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

  /**
   * Method to return the number of players.
   * Used to determine the number of players in the game.
   * @return number of players selected on spinner
   */
  public int getNumberOfPlayers() {
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
    borderPane.setStyle("-fx-background-color: lightblue;");
    Scene scene = new Scene(borderPane);
    setStage(primaryStage);
    primaryStage.setScene(scene);
    primaryStage.setTitle("Select a board game");
    primaryStage.show();

    try {
      outputfile = new FileWriter(playerFile);
      playerWriter = new CSVWriter(outputfile);
        String[] header = { "Player", "Score" };
    } catch (IOException e) {
      e.printStackTrace();
      System.out.println("Error creating player file");
    }

    this.ladderGameGUI = new LadderGameGUI();
    this.missingDiamondGUI = new MissingDiamondGUI();
  }

  private Pane createCenterPane() {
    Button button1 = new Button("Ladder game");
    button1.setOnAction(event -> {
      try {
        for (int i = 0; i < numberOfPlayers.getValue(); i++) {
          String[] playerData = { "Player " + (i + 1), "0" };
          playerWriter.writeNext(playerData);
        }
        playerWriter.flush(); // Ensure data is written to the file
        playerWriter.close(); // Close the writer after use
        ladderGameGUI.setNumberOfPlayers(numberOfPlayers.getValue());
        ladderGameGUI.start(getStage());
      } catch (Exception e) {
        e.printStackTrace();
      }
    });

    Button button2 = new Button("Missing diamond");
    button2.setOnAction(event -> {
      try {
        missingDiamondGUI.setNumberOfPlayers(numberOfPlayers.getValue());
        missingDiamondGUI.start(getStage());
      } catch (Exception e) {
        e.printStackTrace();
      }
    });

    numberOfPlayers = new Spinner<>(2, 6, 2);
    numberOfPlayers.setEditable(true);

    numberOfPlayers.valueProperty().addListener((obs, oldValue, newValue) -> {
      ladderGameGUI.setNumberOfPlayers(newValue);
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
