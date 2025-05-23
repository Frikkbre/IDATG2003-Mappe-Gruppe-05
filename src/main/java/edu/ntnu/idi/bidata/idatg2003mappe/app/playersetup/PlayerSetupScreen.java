package edu.ntnu.idi.bidata.idatg2003mappe.app.playersetup;

import edu.ntnu.idi.bidata.idatg2003mappe.app.boardgameselector.BoardGameSelector;
import javafx.animation.FadeTransition;
import javafx.animation.ScaleTransition;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>Welcome screen for setting up players before starting the game.</p>
 * <p>Allows users to choose number of players, their names and colors.</p>
 * <p>This screen serves as the entry point to the game, where player configuration
 * is handled before proceeding to the game selection screen.</p>
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
   * <p>Inner class to represent a player setup row.</p>
   * <p>Contains UI elements for configuring a single player's name and color.</p>
   */
  private static class PlayerRow {
    private final TextField nameField;
    private final ComboBox<String> colorCombo;
    private final HBox container;

    public PlayerRow(int playerNumber, List<String> availableColors) {
      // Create container with card style
      container = new HBox(15);
      container.getStyleClass().add("player-status");
      container.setAlignment(Pos.CENTER);

      Label playerLabel = new Label("Player " + playerNumber);
      playerLabel.getStyleClass().add("heading");
      playerLabel.setPrefWidth(100);

      // Name field with modern styling
      nameField = new TextField("Player " + playerNumber);
      nameField.getStyleClass().add("text-field");
      nameField.setPrefWidth(200);

      // Color combo box with styling
      colorCombo = new ComboBox<>();
      colorCombo.getItems().addAll(availableColors);
      colorCombo.setValue(availableColors.get((playerNumber - 1) % availableColors.size()));
      colorCombo.getStyleClass().add("combo-box");
      colorCombo.setPrefWidth(150);

      container.getChildren().addAll(playerLabel, nameField, colorCombo);
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
    primaryStage.setTitle("Board Games - Player Setup");

    // Create main layout
    BorderPane mainLayout = new BorderPane();
    mainLayout.getStyleClass().add("main-container");

    // Create header
    VBox header = createHeader();
    mainLayout.setTop(header);

    // Create center content
    ScrollPane scrollPane = new ScrollPane();
    scrollPane.setFitToWidth(true);
    VBox centerContent = createCenterContent();
    scrollPane.setContent(centerContent);
    scrollPane.getStyleClass().add("scroll-pane");
    mainLayout.setCenter(scrollPane);

    // Create footer with continue button
    HBox footer = createFooter();
    mainLayout.setBottom(footer);

    // Create scene and add CSS
    Scene scene = new Scene(mainLayout, 1440, 840);
    scene.getStylesheets().add(getClass().getResource("/game-style/game-styles.css").toExternalForm());

    primaryStage.setScene(scene);
    primaryStage.show();

    // Apply entrance animation
    applyEntranceAnimation(mainLayout);

    // Initialize with default number of players
    updatePlayerRows();
  }

  /**
   * <p>Creates the header section with title and player count selector.</p>
   * <p>The header contains the main title of the application and a spinner
   * that allows users to select the number of players (2-5).</p>
   *
   * @return A {@link VBox} containing the header elements
   */
  private VBox createHeader() {
    VBox header = new VBox(30);
    header.setPadding(new Insets(40));
    header.setAlignment(Pos.CENTER);
    header.getStyleClass().add("card");

    // Animated title
    Label titleLabel = new Label("Welcome to Board Games!");
    titleLabel.getStyleClass().add("title");

    Label subtitleLabel = new Label("Set up your players to begin");
    subtitleLabel.getStyleClass().add("subtitle");

    // Player count selection
    HBox playerCountBox = new HBox(15);
    playerCountBox.setAlignment(Pos.CENTER);

    Label countLabel = new Label("Number of players:");
    countLabel.getStyleClass().add("heading");

    playerCountSpinner = new Spinner<>(2, 5, 2);
    playerCountSpinner.getStyleClass().add("spinner");
    playerCountSpinner.setPrefWidth(100);
    playerCountSpinner.valueProperty().addListener((obs, oldVal, newVal) -> updatePlayerRows());

    playerCountBox.getChildren().addAll(countLabel, playerCountSpinner);

    header.getChildren().addAll(titleLabel, subtitleLabel, playerCountBox);
    return header;
  }

  /**
   * <p>Creates the center content with player setup rows.</p>
   * <p>This section contains a container for all player configuration rows,
   * which will be dynamically updated based on the selected player count.</p>
   *
   * @return A {@link VBox} containing the center content elements
   */
  private VBox createCenterContent() {
    VBox centerContent = new VBox(20);
    centerContent.setPadding(new Insets(20));
    centerContent.setAlignment(Pos.TOP_CENTER);
    centerContent.getStyleClass().add("center-container");

    // Container for player rows
    playerContainer = new VBox(15);
    playerContainer.setAlignment(Pos.CENTER);
    playerContainer.getStyleClass().add("card");
    playerContainer.setPadding(new Insets(20));

    centerContent.getChildren().add(playerContainer);
    return centerContent;
  }

  /**
   * <p>Creates the footer with the continue button.</p>
   * <p>The footer contains a button that allows users to proceed to the game
   * selection screen after completing player setup.</p>
   *
   * @return A {@link HBox} containing the footer elements
   */
  private HBox createFooter() {
    HBox footer = new HBox();
    footer.setPadding(new Insets(30));
    footer.setAlignment(Pos.CENTER);

    continueButton = new Button("Continue to Game Selection");
    continueButton.getStyleClass().addAll("button", "animated-button");
    continueButton.setPrefSize(300, 60);
    continueButton.setOnAction(e -> handleContinue());

    // Add hover animation
    addButtonAnimation(continueButton);

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

    // Add header
    Label setupLabel = new Label("Configure Your Players");
    setupLabel.getStyleClass().add("subtitle");
    playerContainer.getChildren().add(setupLabel);

    // Create new rows with animation
    for (int i = 1; i <= playerCount; i++) {
      PlayerRow playerRow = new PlayerRow(i, availableColors);
      playerRows.add(playerRow);

      HBox rowContainer = playerRow.getContainer();
      playerContainer.getChildren().add(rowContainer);

      // Add stagger animation
      FadeTransition fadeIn = new FadeTransition(Duration.millis(300), rowContainer);
      fadeIn.setFromValue(0);
      fadeIn.setToValue(1);
      fadeIn.setDelay(Duration.millis(i * 100));
      fadeIn.play();
    }
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
   * <p>Shows a styled alert dialog.</p>
   * <p>Displays an alert dialog with the specified title, message, and type.
   * Used to communicate errors or important information to the user.</p>
   *
   * @param title The title of the alert dialog
   * @param message The message to display in the alert dialog
   * @param type The type of alert (ERROR, WARNING, INFORMATION, etc.)
   */
  private void showAlert(String title, String message, Alert.AlertType type) {
    Alert alert = new Alert(type);
    alert.setTitle(title);
    alert.setHeaderText(null);
    alert.setContentText(message);

    // Apply CSS to alert
    DialogPane dialogPane = alert.getDialogPane();
    dialogPane.getStylesheets().add(getClass().getResource("/game-style/game-styles.css").toExternalForm());
    dialogPane.getStyleClass().add("dialog-pane");

    alert.showAndWait();
  }

  /**
   * <p>Applies entrance animation to the main layout.</p>
   * <p>Creates a smooth fade-in effect when the screen loads.</p>
   *
   * @param node The node to animate
   */
  private void applyEntranceAnimation(javafx.scene.Node node) {
    FadeTransition fadeIn = new FadeTransition(Duration.millis(500), node);
    fadeIn.setFromValue(0);
    fadeIn.setToValue(1);
    fadeIn.play();
  }

  /**
   * <p>Adds hover animation to buttons.</p>
   * <p>Creates a scale effect when hovering over buttons.</p>
   *
   * @param button The button to animate
   */
  private void addButtonAnimation(Button button) {
    button.setOnMouseEntered(e -> {
      ScaleTransition scaleUp = new ScaleTransition(Duration.millis(200), button);
      scaleUp.setToX(1.05);
      scaleUp.setToY(1.05);
      scaleUp.play();
    });

    button.setOnMouseExited(e -> {
      ScaleTransition scaleDown = new ScaleTransition(Duration.millis(200), button);
      scaleDown.setToX(1.0);
      scaleDown.setToY(1.0);
      scaleDown.play();
    });
  }

  public static void main(String[] args) {
    launch(args);
  }
}