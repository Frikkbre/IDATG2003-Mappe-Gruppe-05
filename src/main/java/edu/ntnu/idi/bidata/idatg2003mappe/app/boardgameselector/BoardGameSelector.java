package edu.ntnu.idi.bidata.idatg2003mappe.app.boardgameselector;

import edu.ntnu.idi.bidata.idatg2003mappe.app.common.ui.NavBar;
import edu.ntnu.idi.bidata.idatg2003mappe.app.laddergame.ui.LadderGameGUI;
import edu.ntnu.idi.bidata.idatg2003mappe.app.missingdiamond.ui.MissingDiamondGUI;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.File;

/**
 * <p>Class for the board game selector GUI.</p>
 * <p>Now simplified to only handle game selection since player setup
 * is handled in the dedicated PlayerSetupScreen.</p>
 *
 * @author Simen Gudbrandsen and Frikk Breadsroed
 * @version 0.0.2
 * @since 21.05.2025
 */
public class BoardGameSelector extends Application {
  private LadderGameGUI ladderGameGUI;
  private MissingDiamondGUI missingDiamondGUI;
  private Stage primaryStage;

  /**
   * <p>Method to set the stage of the application.</p>
   *
   * @param primaryStage the primary stage to set
   */
  public void setStage(Stage primaryStage) {
    this.primaryStage = primaryStage;
  }

  /**
   * <p>Used to get the stage of the application.</p>
   * <p>Used in other game classes to add their scene to the stage.</p>
   *
   * @return the primaryStage
   */
  public Stage getStage() {
    return primaryStage;
  }

  /**
   * <p>Start method for the JavaFX application.</p>
   * <p>This method is used to start the JavaFX application.</p>
   *
   * @param primaryStage The primary stage for this application.
   * @throws Exception If an error occurs during startup.
   */
  @Override
  public void start(Stage primaryStage) throws Exception {
    // Verify that player data exists
    if (!playerDataExists()) {
      showAlert("No Player Data",
          "No player data found. Please restart the application to set up players.",
          Alert.AlertType.WARNING);
      return;
    }

    BorderPane borderPane = new BorderPane();
    borderPane.setMinHeight(840); //16:9 aspect ratio   (1920x1080)/2
    borderPane.setMaxHeight(840);
    borderPane.setMinWidth(1440);
    borderPane.setMaxWidth(1440);
    borderPane.setPrefHeight(840);
    borderPane.setPrefWidth(1440);
    borderPane.setCenter(createCenterPane());
    borderPane.setStyle("-fx-background-color: lightblue;");

    Scene scene = new Scene(borderPane);
    setStage(primaryStage);
    primaryStage.setScene(scene);
    primaryStage.setTitle("Select a board game");
    primaryStage.show();

    this.ladderGameGUI = new LadderGameGUI();
    this.missingDiamondGUI = new MissingDiamondGUI();
  }

  /**
   * <p>Method to create the center pane of the GUI.</p>
   * <p>Simplified to only show game selection buttons.</p>
   *
   * @return centerPane A VBox containing the game selection UI elements
   */
  private VBox createCenterPane() {
    VBox centerPane = new VBox(30);
    centerPane.setAlignment(Pos.CENTER);

    // Title
    Label titleLabel = new Label("Choose Your Game");
    titleLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: darkblue;");

    // Game selection buttons
    Button ladderGameButton = new Button("Ladder Game");
    ladderGameButton.setPrefSize(200, 60);
    ladderGameButton.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
    ladderGameButton.setOnAction(event -> startLadderGame());

    Button missingDiamondButton = new Button("Missing Diamond");
    missingDiamondButton.setPrefSize(200, 60);
    missingDiamondButton.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
    missingDiamondButton.setOnAction(event -> startMissingDiamond());

    centerPane.getChildren().addAll(titleLabel, ladderGameButton, missingDiamondButton);
    return centerPane;
  }

  /**
   * <p>Starts the Ladder Game.</p>
   * <p>Attempts to launch the Ladder Game UI and displays an error alert if it fails.</p>
   */
  private void startLadderGame() {
    try {
      ladderGameGUI.start(getStage());
    } catch (Exception e) {
      showAlert("Error", "Failed to start Ladder Game: " + e.getMessage(), Alert.AlertType.ERROR);
    }
  }

  /**
   * <p>Starts the Missing Diamond game.</p>
   * <p>Attempts to launch the Missing Diamond game UI and displays an error alert if it fails.</p>
   */
  private void startMissingDiamond() {
    try {
      missingDiamondGUI.start(getStage());
    } catch (Exception e) {
      showAlert("Error", "Failed to start Missing Diamond: " + e.getMessage(), Alert.AlertType.ERROR);
    }
  }

  /**
   * <p>Checks if player data file exists.</p>
   * <p>Verifies that the required player data CSV file is present before allowing game selection.</p>
   *
   * @return <code>true</code> if player data exists, <code>false</code> otherwise
   */
  private boolean playerDataExists() {
    File playerFile = new File("src/main/resources/saves/playerData/Players.csv");
    return playerFile.exists() && playerFile.isFile();
  }

  /**
   * <p>Shows an alert dialog.</p>
   * <p>Displays information to the user through a JavaFX Alert dialog.</p>
   *
   * @param title   the title of the alert
   * @param message the message to display
   * @param type    the type of alert (ERROR, WARNING, INFORMATION, etc.)
   */
  private void showAlert(String title, String message, Alert.AlertType type) {
    Alert alert = new Alert(type);
    alert.setTitle(title);
    alert.setHeaderText(null);
    alert.setContentText(message);
    alert.showAndWait();
  }

  public static void main(String[] args) {
    launch(args);
  }
}
