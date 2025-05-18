package edu.ntnu.idi.bidata.idatg2003mappe.app;

import com.opencsv.CSVWriter;
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
import java.io.IOException;
import java.util.ArrayList;

/**
 * Class for the board game selector GUI.
 */
public class BoardGameSelectorGUI extends Application {
  private LadderGameGUI ladderGameGUI;
  private MissingDiamondGUI missingDiamondGUI;
  private Stage primaryStage;
  private Spinner<Integer> numberOfPlayers;
  private File playerFile = new File("src/main/resources/saves/playerData/Players.csv");
  private CSVWriter playerWriter;
  private FileWriter outputfile;
  private ArrayList<String> ColorList = new ArrayList<>();


  /**
   * populateColors method.
   * This method is used to populate the color list with colors.
   */
  public void populateColors() {
    // Add colors to the color list
    ColorList.add("LightGreen");
    ColorList.add("LightPink");
    ColorList.add("Green");
    ColorList.add("HotPink");
    ColorList.add("Orange");
  }

  /**
   * Returns the color of the index passed in.
   * Used to assign colors to players.
   * @param index
   * @return color
   */
  public String getColor(int index) {
    if (index < 0 || index >= ColorList.size()) {
      throw new IndexOutOfBoundsException("Invalid index: " + index);
    }
    return ColorList.get(index);
  }

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

    // Populate the color list
    populateColors();

    this.ladderGameGUI = new LadderGameGUI();
    this.missingDiamondGUI = new MissingDiamondGUI();
  }

  private Pane createCenterPane() {
    Button button1 = new Button("Ladder game");
    button1.setOnAction(event -> {
      try {
        // Make sure directory exists
        File playerDir = new File("src/main/resources/saves/playerData/");
        if (!playerDir.exists()) {
          playerDir.mkdirs();
        }

        // Reset the file and writer
        outputfile = new FileWriter(playerFile);
        playerWriter = new CSVWriter(outputfile);

        // Write header
        String[] header = { "Player name", "ID", "Color", "Position" };
        playerWriter.writeNext(header);

        // Write player data
        for (int i = 0; i < numberOfPlayers.getValue(); i++) {
          String[] playerData = { "Player " + (i + 1), String.valueOf(i + 1), getColor(i), "0" };
          playerWriter.writeNext(playerData);
        }

        playerWriter.flush();
        playerWriter.close();

        ladderGameGUI.start(getStage());
      } catch (Exception e) {
        e.printStackTrace();
      }
    });

    Button button2 = new Button("Missing diamond");
    button2.setOnAction(event -> {
      try {
        // Make sure directory exists
        File playerDir = new File("src/main/resources/saves/playerData/");
        if (!playerDir.exists()) {
          playerDir.mkdirs();
        }

        // Reset the file and writer
        outputfile = new FileWriter(playerFile);
        playerWriter = new CSVWriter(outputfile);

        // Write header
        String[] header = { "Player name", "ID", "Color", "Position" };
        playerWriter.writeNext(header);

        // Write player data
        for (int i = 0; i < numberOfPlayers.getValue(); i++) {
          String[] playerData = { "Player " + (i + 1), String.valueOf(i), getColor(i), "1" };
          playerWriter.writeNext(playerData);
        }

        playerWriter.flush();
        playerWriter.close();

        missingDiamondGUI.start(getStage());
      } catch (Exception e) {
        e.printStackTrace();
      }
    });

    numberOfPlayers = new Spinner<>(2, 5, 2);
    numberOfPlayers.setEditable(true);

    FlowPane centerPane = new FlowPane();
    centerPane.getChildren().addAll(button1, button2, numberOfPlayers);
    centerPane.setAlignment(Pos.CENTER);
    return centerPane;
  }

  public static void main(String[] args) {
    launch(args);
  }
}