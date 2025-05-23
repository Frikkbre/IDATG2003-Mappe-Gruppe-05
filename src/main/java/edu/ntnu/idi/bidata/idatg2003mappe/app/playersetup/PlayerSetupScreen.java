package edu.ntnu.idi.bidata.idatg2003mappe.app.playersetup;

import edu.ntnu.idi.bidata.idatg2003mappe.app.boardgameselector.BoardGameSelector;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Welcome screen for setting up players before starting the game.
 * Allows users to choose number of players, their names and colors.
 *
 * @author Simen Gudbrandsen and Frikk Breadsroed
 * @version 1.0.0
 * @since 23.05.2025
 */
public class PlayerSetupScreen extends Application {

  private Stage primaryStage;
  private final List<PlayerRow> playerRows = new ArrayList<>();
  private final List<String> availableColors = List.of(
      "LightGreen", "LightPink", "Green", "HotPink", "Orange", "Blue"
  );

  // UI Components
  private VBox playerContainer;
  private Button continueButton;
  private Spinner<Integer> playerCountSpinner;

  /**
   * Inner class to represent a player setup row.
   */
  private static class PlayerRow {
    private final TextField nameField;
    private final ComboBox<String> colorCombo;
    private final HBox container;

    public PlayerRow(int playerNumber, List<String> availableColors) {
      // Create name field
      nameField = new TextField("Player " + playerNumber);
      nameField.setPrefWidth(150);

      // Create color combo box
      colorCombo = new ComboBox<>();
      colorCombo.getItems().addAll(availableColors);
      colorCombo.setValue(availableColors.get((playerNumber - 1) % availableColors.size()));
      colorCombo.setPrefWidth(120);

      // Create container
      container = new HBox(10);
      container.setAlignment(Pos.CENTER_LEFT);

      Label playerLabel = new Label("Player " + playerNumber + ":");
      playerLabel.setPrefWidth(80);

      Label nameLabel = new Label("Name:");
      nameLabel.setPrefWidth(50);

      Label colorLabel = new Label("Color:");
      colorLabel.setPrefWidth(50);

      container.getChildren().addAll(
          playerLabel, nameLabel, nameField, colorLabel, colorCombo
      );
    }

    public String getName() {
      return nameField.getText().trim();
    }

    public String getColor() {
      return colorCombo.getValue();
    }

    public HBox getContainer() {
      return container;
    }

    public boolean isValid() {
      return !getName().isEmpty();
    }
  }

  @Override
  public void start(Stage primaryStage) {
    this.primaryStage = primaryStage;
    primaryStage.setTitle("Game Setup - Choose Players");

    // Create main layout
    BorderPane mainLayout = new BorderPane();
    mainLayout.setPrefSize(600, 500);
    mainLayout.setStyle("-fx-background-color: lightblue;");

    // Create header
    VBox header = createHeader();
    mainLayout.setTop(header);

    // Create center content
    VBox centerContent = createCenterContent();
    mainLayout.setCenter(centerContent);

    // Create footer with continue button
    HBox footer = createFooter();
    mainLayout.setBottom(footer);

    // Create scene and show
    Scene scene = new Scene(mainLayout);
    primaryStage.setScene(scene);
    primaryStage.show();

    // Initialize with default number of players
    updatePlayerRows();
  }

  /**
   * Creates the header section with title and player count selector.
   */
  private VBox createHeader() {
    VBox header = new VBox(20);
    header.setPadding(new Insets(20));
    header.setAlignment(Pos.CENTER);

    // Title
    Label titleLabel = new Label("Welcome to Board Games!");
    titleLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: darkblue;");

    // Player count selection
    HBox playerCountBox = new HBox(10);
    playerCountBox.setAlignment(Pos.CENTER);

    Label countLabel = new Label("Number of players:");
    countLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");

    playerCountSpinner = new Spinner<>(2, 5, 2);
    playerCountSpinner.setPrefWidth(80);
    playerCountSpinner.valueProperty().addListener((obs, oldVal, newVal) -> updatePlayerRows());

    playerCountBox.getChildren().addAll(countLabel, playerCountSpinner);

    header.getChildren().addAll(titleLabel, playerCountBox);
    return header;
  }

  /**
   * Creates the center content with player setup rows.
   */
  private VBox createCenterContent() {
    VBox centerContent = new VBox(15);
    centerContent.setPadding(new Insets(20));
    centerContent.setAlignment(Pos.TOP_CENTER);

    Label setupLabel = new Label("Set up your players:");
    setupLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

    // Container for player rows
    playerContainer = new VBox(10);
    playerContainer.setAlignment(Pos.CENTER);

    centerContent.getChildren().addAll(setupLabel, playerContainer);
    return centerContent;
  }

  /**
   * Creates the footer with the continue button.
   */
  private HBox createFooter() {
    HBox footer = new HBox();
    footer.setPadding(new Insets(20));
    footer.setAlignment(Pos.CENTER);

    continueButton = new Button("Continue to Game Selection");
    continueButton.setPrefSize(200, 40);
    continueButton.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");
    continueButton.setOnAction(e -> handleContinue());

    footer.getChildren().add(continueButton);
    return footer;
  }

  /**
   * Updates the player rows based on the selected number of players.
   */
  private void updatePlayerRows() {
    int playerCount = playerCountSpinner.getValue();

    // Clear existing rows
    playerRows.clear();
    playerContainer.getChildren().clear();

    // Create new rows
    for (int i = 1; i <= playerCount; i++) {
      PlayerRow playerRow = new PlayerRow(i, availableColors);
      playerRows.add(playerRow);
      playerContainer.getChildren().add(playerRow.getContainer());
    }
  }

  /**
   * Handles the continue button click.
   * Validates input and saves player data before proceeding.
   */
  private void handleContinue() {
    // Validate all players have names
    for (PlayerRow row : playerRows) {
      if (!row.isValid()) {
        showAlert("Error", "All players must have names!", Alert.AlertType.ERROR);
        return;
      }
    }

    // Check for duplicate names
    List<String> names = new ArrayList<>();
    for (PlayerRow row : playerRows) {
      String name = row.getName();
      if (names.contains(name)) {
        showAlert("Error", "Player names must be unique!", Alert.AlertType.ERROR);
        return;
      }
      names.add(name);
    }

    // Check for duplicate colors
    List<String> colors = new ArrayList<>();
    for (PlayerRow row : playerRows) {
      String color = row.getColor();
      if (colors.contains(color)) {
        showAlert("Error", "Each player must have a different color!", Alert.AlertType.ERROR);
        return;
      }
      colors.add(color);
    }

    // Save player data and proceed
    if (savePlayerData()) {
      proceedToGameSelection();
    }
  }

  /**
   * Saves player data to CSV file.
   */
  private boolean savePlayerData() {
    try {
      // Ensure directory exists
      File playerDir = new File("src/main/resources/saves/playerData/");
      if (!playerDir.exists()) {
        playerDir.mkdirs();
      }

      // Write to CSV file
      File playerFile = new File("src/main/resources/saves/playerData/Players.csv");
      try (FileWriter writer = new FileWriter(playerFile)) {
        // Write header
        writer.write("\"Player name\",\"ID\",\"Color\",\"Position\"\n");

        // Write player data
        for (int i = 0; i < playerRows.size(); i++) {
          PlayerRow row = playerRows.get(i);
          writer.write(String.format("\"%s\",\"%d\",\"%s\",\"1\"\n",
              row.getName(), i, row.getColor()));
        }
      }

      return true;

    } catch (IOException e) {
      showAlert("Error", "Failed to save player data: " + e.getMessage(), Alert.AlertType.ERROR);
      return false;
    }
  }

  /**
   * Proceeds to the game selection screen.
   */
  private void proceedToGameSelection() {
    try {
      BoardGameSelector gameSelector = new BoardGameSelector();
      gameSelector.start(primaryStage);
    } catch (Exception e) {
      showAlert("Error", "Failed to load game selection: " + e.getMessage(), Alert.AlertType.ERROR);
    }
  }

  /**
   * Shows an alert dialog.
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