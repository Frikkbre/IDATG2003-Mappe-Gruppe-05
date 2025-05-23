package edu.ntnu.idi.bidata.idatg2003mappe.app.playersetup;

import edu.ntnu.idi.bidata.idatg2003mappe.app.boardgameselector.BoardGameSelector;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

/**
 * <p>Welcome screen for setting up players before starting the game.</p>
 * <p>Allows users to choose number of players, their names and colors.</p>
 * <p>This screen serves as the entry point to the game, where player configuration
 * is handled before proceeding to the game selection screen.</p>
 * <p>Now features enhanced CSS styling for a professional appearance.</p>
 *
 * @author Simen Gudbrandsen and Frikk Breadsroed
 * @version 1.1.0
 * @since 23.05.2025
 */
public class PlayerSetupScreen extends Application {

  private final List<PlayerRow> playerRows = new ArrayList<>();
  private final List<String> availableColors = List.of(
      "LightGreen", "LightPink", "Green", "HotPink", "Orange", "Blue"
  );
  private Stage primaryStage;
  // UI Components
  private VBox playerContainer;
  private Button continueButton;
  private Spinner<Integer> playerCountSpinner;

  public static void main(String[] args) {
    launch(args);
  }

  @Override
  public void start(Stage primaryStage) {
    this.primaryStage = primaryStage;
    primaryStage.setTitle("Game Setup - Choose Players");

    // Create main layout
    BorderPane mainLayout = new BorderPane();
    mainLayout.setMinHeight(840);
    mainLayout.setMaxHeight(840);
    mainLayout.setMinWidth(1440);
    mainLayout.setMaxWidth(1440);
    mainLayout.setPrefHeight(840);
    mainLayout.setPrefWidth(1440);

    // Apply CSS background
    mainLayout.getStyleClass().add("main-container-solid");  // Changed from main-container to main-container-solid for solid blue

    // Create center content with glass effect
    VBox centerContent = createCenterContent();
    centerContent.getStyleClass().add("center-content");

    // Wrap center content in a container for better positioning
    StackPane centerWrapper = new StackPane(centerContent);
    centerWrapper.setPadding(new Insets(50));

    mainLayout.setCenter(centerWrapper);

    // Create scene and load CSS
    Scene scene = new Scene(mainLayout);
    loadCSS(scene);

    primaryStage.setScene(scene);
    primaryStage.show();

    // Initialize with default number of players
    updatePlayerRows();
  }

  /**
   * <p>Loads the CSS stylesheet for the application.</p>
   * <p>Provides enhanced styling and professional appearance.</p>
   *
   * @param scene The scene to apply CSS styling to
   */
  private void loadCSS(Scene scene) {
    try {
      String cssFile = getClass().getResource("/game-style/game-styles.css").toExternalForm();
      scene.getStylesheets().add(cssFile);
    } catch (Exception e) {
      System.err.println("Warning: Could not load CSS file. Using default styling.");
    }
  }

  /**
   * <p>Creates the center content with enhanced styling.</p>
   * <p>Contains all the main UI elements in a structured layout.</p>
   *
   * @return A {@link VBox} containing all center content elements
   */
  private VBox createCenterContent() {
    VBox content = new VBox(25);
    content.setAlignment(Pos.CENTER);
    content.setPrefWidth(600);
    content.setMaxWidth(600);

    // Create header
    VBox header = createHeader();

    // Create player setup section
    VBox playerSetup = createPlayerSetupSection();

    // Create footer
    HBox footer = createFooter();

    content.getChildren().addAll(header, playerSetup, footer);
    return content;
  }

  /**
   * <p>Creates the header section with title and player count selector.</p>
   * <p>Enhanced with CSS styling for better visual hierarchy.</p>
   *
   * @return A {@link VBox} containing the header elements
   */
  private VBox createHeader() {
    VBox header = new VBox(20);
    header.setAlignment(Pos.CENTER);

    // Title with CSS styling
    Label titleLabel = new Label("Welcome to Board Games!");
    titleLabel.getStyleClass().add("title-label");

    // Player count selection
    HBox playerCountBox = new HBox(15);
    playerCountBox.setAlignment(Pos.CENTER);

    Label countLabel = new Label("Number of players:");
    countLabel.getStyleClass().add("subtitle-label");

    playerCountSpinner = new Spinner<>(2, 5, 2);
    playerCountSpinner.setPrefWidth(80);
    playerCountSpinner.getStyleClass().add("game-spinner");
    playerCountSpinner.valueProperty().addListener((obs, oldVal, newVal) -> updatePlayerRows());

    playerCountBox.getChildren().addAll(countLabel, playerCountSpinner);

    header.getChildren().addAll(titleLabel, playerCountBox);
    return header;
  }

  /**
   * <p>Creates the player setup section.</p>
   * <p>Contains the dynamic player configuration rows.</p>
   *
   * @return A {@link VBox} containing the player setup elements
   */
  private VBox createPlayerSetupSection() {
    VBox playerSetup = new VBox(15);
    playerSetup.setAlignment(Pos.CENTER);
    playerSetup.getStyleClass().add("spaced-container");

    Label setupLabel = new Label("Set up your players:");
    setupLabel.getStyleClass().add("subtitle-label");

    // Container for player rows
    playerContainer = new VBox(10);
    playerContainer.setAlignment(Pos.CENTER);

    playerSetup.getChildren().addAll(setupLabel, playerContainer);
    return playerSetup;
  }

  /**
   * <p>Creates the footer with the continue button.</p>
   * <p>Enhanced with CSS button styling.</p>
   *
   * @return A {@link HBox} containing the footer elements
   */
  private HBox createFooter() {
    HBox footer = new HBox();
    footer.setAlignment(Pos.CENTER);

    continueButton = new Button("Continue to Game Selection");
    continueButton.setPrefSize(250, 50);
    continueButton.getStyleClass().add("game-button");
    continueButton.setOnAction(e -> handleContinue());

    footer.getChildren().add(continueButton);
    return footer;
  }

  /**
   * <p>Updates the player rows based on the selected number of players.</p>
   * <p>This method is called whenever the player count spinner value changes.
   * It creates or removes player configuration rows to match the selected count.</p>
   */
  private void updatePlayerRows() {
    int playerCount = playerCountSpinner.getValue();

    // Clear existing rows
    playerRows.clear();
    playerContainer.getChildren().clear();

    // Create new rows
    IntStream.rangeClosed(1, playerCount)
        .mapToObj(i -> new PlayerRow(i, availableColors))
        .peek(playerRows::add)
        .map(PlayerRow::getContainer)
        .forEach(playerContainer.getChildren()::add);

  }

  /**
   * <p>Handles the continue button click.</p>
   * <p>Validates input and saves player data before proceeding to the game selection screen.
   * Validation includes checking for:</p>
   * <ul>
   *   <li>Empty player names</li>
   *   <li>Duplicate player names</li>
   *   <li>Duplicate player colors</li>
   * </ul>
   */
  private void handleContinue() {
    playerRows.forEach(row -> row.getContainer().setStyle("-fx-border-color: transparent;"));

    // Validate all players have names
    if (playerRows.stream().anyMatch(row -> !row.isValid())) {
      showAlert("Error", "All players must have names!", Alert.AlertType.ERROR);
      return;
    }

    // Check for duplicate names
    List<String> names = new ArrayList<>();
    if (playerRows.stream().anyMatch(row -> {
      String name = row.getName();
      if (names.contains(name)) {
        return true;
      }
      names.add(name);
      return false;
    })) {
      showAlert("Error", "Player names must be unique!", Alert.AlertType.ERROR);
      return;
    }

    // Check for duplicate colors
    List<String> colors = new ArrayList<>();
    if (playerRows.stream().anyMatch(row -> {
      String color = row.getColor();
      if (colors.contains(color)) {
        return true;
      }
      colors.add(color);
      return false;
    })) {
      showAlert("Error", "Each player must have a different color!", Alert.AlertType.ERROR);
      return;
    }

    // Save player data and proceed
    if (savePlayerData()) {
      proceedToGameSelection();
    }
  }


  /**
   * <p>Saves player data to CSV file.</p>
   * <p>Creates a CSV file containing all player information, including:
   * name, ID, color, and starting position. The file is saved to the default
   * location where the game expects to find player data.</p>
   *
   * @return {@code true} if the data was successfully saved, {@code false} otherwise
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
        IntStream.range(0, playerRows.size())
            .mapToObj(i -> String.format("\"%s\",\"%d\",\"%s\",\"1\"\n",
                playerRows.get(i).getName(), i, playerRows.get(i).getColor()))
            .forEach(row -> {
              try {
                writer.write(row);
              } catch (IOException e) {
                throw new RuntimeException("Error writing player data", e);
              }
            });
      }

      return true;

    } catch (IOException e) {
      showAlert("Error", "Failed to save player data: " + e.getMessage(), Alert.AlertType.ERROR);
      return false;
    }
  }


  /**
   * <p>Proceeds to the game selection screen.</p>
   * <p>Initializes and displays the {@link BoardGameSelector} screen,
   * where users can choose which game to play.</p>
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
   * <p>Shows an alert dialog with enhanced styling.</p>
   * <p>Displays an alert dialog with the specified title, message, and type.
   * Used to communicate errors or important information to the user.</p>
   *
   * @param title   The title of the alert dialog
   * @param message The message to display in the alert dialog
   * @param type    The type of alert (ERROR, WARNING, INFORMATION, etc.)
   */
  private void showAlert(String title, String message, Alert.AlertType type) {
    Alert alert = new Alert(type);
    alert.setTitle(title);
    alert.setHeaderText(null);
    alert.setContentText(message);

    // Apply CSS styling to alert if possible
    alert.getDialogPane().getStylesheets().addAll(
        primaryStage.getScene().getStylesheets()
    );

    alert.showAndWait();
  }

  /**
   * <p>Inner class to represent a player setup row.</p>
   * <p>Contains UI elements for configuring a single player's name and color.</p>
   * <p>Now includes CSS styling for better visual presentation.</p>
   */
  private static class PlayerRow {
    private final TextField nameField;
    private final ComboBox<String> colorCombo;
    private final HBox container;

    public PlayerRow(int playerNumber, List<String> availableColors) {
      // Create name field with CSS styling
      nameField = new TextField("Player " + playerNumber);
      nameField.setPrefWidth(150);
      nameField.getStyleClass().add("game-text-field");

      // Create color combo box with CSS styling
      colorCombo = new ComboBox<>();
      colorCombo.getItems().addAll(availableColors);
      colorCombo.setValue(availableColors.get((playerNumber - 1) % availableColors.size()));
      colorCombo.setPrefWidth(120);
      colorCombo.getStyleClass().add("game-combo-box");

      // Create container with CSS styling
      container = new HBox(15);
      container.setAlignment(Pos.CENTER);
      container.getStyleClass().add("player-row");

      Label playerLabel = new Label("Player " + playerNumber + ":");
      playerLabel.setPrefWidth(80);
      playerLabel.getStyleClass().add("info-label");

      Label nameLabel = new Label("Name:");
      nameLabel.setPrefWidth(50);
      nameLabel.getStyleClass().add("info-label");

      Label colorLabel = new Label("Color:");
      colorLabel.setPrefWidth(50);
      colorLabel.getStyleClass().add("info-label");

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
}

