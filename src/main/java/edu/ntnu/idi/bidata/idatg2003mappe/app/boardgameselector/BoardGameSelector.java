package edu.ntnu.idi.bidata.idatg2003mappe.app.boardgameselector;

import com.opencsv.CSVWriter;
import edu.ntnu.idi.bidata.idatg2003mappe.app.common.ui.NavBar;
import edu.ntnu.idi.bidata.idatg2003mappe.app.laddergame.ui.LadderGameGUI;
import edu.ntnu.idi.bidata.idatg2003mappe.app.missingdiamond.ui.MissingDiamondGUI;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.stream.Stream;

/**
 * Class for the board game selector GUI.
 * It allows the user to select between
 * Ladder Game and Missing Diamond.
 *
 * @author Simen Gudbrandsen and Frikk Breadsroed
 * @version 0.0.1
 * @since 21.05.2025
 */
public class BoardGameSelector extends Application {
  private LadderGameGUI ladderGameGUI;
  private MissingDiamondGUI missingDiamondGUI;
  private Stage primaryStage;
  private Spinner<Integer> numberOfPlayers;
  private final File playerFile = new File("src/main/resources/saves/playerData/Players.csv");
  private CSVWriter playerWriter;
  private FileWriter outputfile;
  private final ArrayList<String> colorList = new ArrayList<>();

  /**
   * Method to set the stage of the application.
   *
   * @param primaryStage
   */
  public void setStage(Stage primaryStage) {
    this.primaryStage = primaryStage;
  }

  /**
   * Used to get the stage of the application.
   * Used in other game classes to add their scene to the stage.
   *
   * @return the primaryStage
   */
  public Stage getStage() {
    return primaryStage;
  }

  /**
   * Start method for the JavaFX application.
   * This method is used to start the JavaFX application.
   *
   * @param primaryStage The primary stage for this application.
   * @throws Exception If an error occurs during startup.
   */
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

    getColorList();

    this.ladderGameGUI = new LadderGameGUI();
    this.missingDiamondGUI = new MissingDiamondGUI();
  }

  /**
   * Returns the color of the index passed in.
   * Used to assign colors to players.
   *
   * @param index
   * @return color
   */
  public String getColor(int index) {
    if (index < 0 || index >= colorList.size()) {
      throw new IndexOutOfBoundsException("Invalid index: " + index);
    }
    return colorList.get(index);
  }

  /**
   * Returns a stream of all available colors.
   *
   * @return Stream of colors
   */
  public Stream<String> getColorList() {
    colorList.add("LightGreen");
    colorList.add("LightPink");
    colorList.add("Green");
    colorList.add("HotPink");
    colorList.add("Orange");
    return colorList.stream();
  }

  /**
   * Method to create the center pane of the GUI.
   * This method is used to create the center pane of the GUI.
   *
   * @return centerPane
   */
  private Pane createCenterPane() {
    Button button1 = new Button("Ladder game");
    button1.setOnAction(event -> writeToFile("ladderGame"));

    Button button2 = new Button("Missing diamond");
    button2.setOnAction(event -> writeToFile("missingDiamond"));

    numberOfPlayers = new Spinner<>(2, 5, 2);
    numberOfPlayers.setEditable(true);
    Label spinnerLabel = new Label("   Number of players: ");

    FlowPane centerPane = new FlowPane();
    centerPane.getChildren().addAll(button1, button2, spinnerLabel, numberOfPlayers);
    centerPane.setAlignment(Pos.CENTER);
    return centerPane;
  }

  /**
   * Method to write the player data to a file.
   * This method is used to write the player data to a CSV file.
   *
   * @param game The game chosen by the user.
   */
  public void writeToFile(String game) {
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
      String[] header = {"Player name", "ID", "Color", "Position"};
      playerWriter.writeNext(header);

      // Write player data specific to game chosen
      if (game.equals("ladderGame")) {
        for (int i = 0; i < numberOfPlayers.getValue(); i++) {
          String[] playerData = {"Player " + (i + 1), String.valueOf(i), getColor(i), "0"};
          playerWriter.writeNext(playerData);
        }
      } else if (game.equals("missingDiamond")) {
        for (int i = 0; i < numberOfPlayers.getValue(); i++) {
          String[] playerData = {"Player " + (i + 1), String.valueOf(i), getColor(i), "1"};
          playerWriter.writeNext(playerData);
        }

      }

      playerWriter.flush();
      playerWriter.close();
      if (game.equals("ladderGame")) {
        ladderGameGUI.start(getStage());
      } else if (game.equals("missingDiamond")) {
        missingDiamondGUI.start(getStage());
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public static void main(String[] args) {
    launch(args);
  }
}
