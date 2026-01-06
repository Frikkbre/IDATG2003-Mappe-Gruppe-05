package edu.ntnu.idi.bidata.idatg2003mappe.app.boardgameselector;

import edu.ntnu.idi.bidata.idatg2003mappe.app.laddergame.ui.LadderGameGUI;
import edu.ntnu.idi.bidata.idatg2003mappe.app.missingdiamond.ui.MissingDiamondGUI;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.File;
import java.util.logging.Logger;

/**
 * <p>Class for the board game selector GUI.</p>
 * <p>Now simplified to only handle game selection since player setup
 * is handled in the dedicated PlayerSetupScreen.</p>
 * <p>Enhanced with modern CSS styling for professional appearance.</p>
 *
 * @author Simen Gudbrandsen and Frikk Breadsroed
 * @version 0.1.0
 * @since 21.05.2025
 */
public class BoardGameSelector extends Application {
  private static final Logger logger = Logger.getLogger(BoardGameSelector.class.getName());
  private LadderGameGUI ladderGameGUI;
  private MissingDiamondGUI missingDiamondGUI;
  private Stage primaryStage;

  public static void main(String[] args) {
    launch(args);
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
   * <p>Method to set the stage of the application.</p>
   *
   * @param primaryStage the primary stage to set
   */
  public void setStage(Stage primaryStage) {
    this.primaryStage = primaryStage;
  }

  /**
   * <p>Start method for the JavaFX application.</p>
   * <p>This method is used to start the JavaFX application with enhanced CSS styling.</p>
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

    // Create main layout
    BorderPane borderPane = new BorderPane();
    borderPane.setMinHeight(840);
    borderPane.setMaxHeight(840);
    borderPane.setMinWidth(1440);
    borderPane.setMaxWidth(1440);
    borderPane.setPrefHeight(840);
    borderPane.setPrefWidth(1440);

    // Apply CSS background
    borderPane.getStyleClass().add("main-container-solid");

    // Create center content with glass effect
    VBox centerContent = createCenterPane();
    centerContent.getStyleClass().add("center-content");

    // Wrap center content for better positioning
    StackPane centerWrapper = new StackPane(centerContent);
    centerWrapper.setPadding(new Insets(50));

    borderPane.setCenter(centerWrapper);

    // Create scene and load CSS
    Scene scene = new Scene(borderPane);
    loadCSS(scene);

    setStage(primaryStage);
    primaryStage.setScene(scene);
    primaryStage.setTitle("Select a board game");
    primaryStage.show();

    // Initialize game instances
    this.ladderGameGUI = new LadderGameGUI();
    this.missingDiamondGUI = new MissingDiamondGUI();
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
      logger.warning("Could not load CSS file. Using default styling.");
    }
  }

  /**
   * <p>Method to create the center pane of the GUI.</p>
   * <p>Uses Material Design card styling with proper elevation.</p>
   *
   * @return centerPane A VBox containing the game selection UI elements
   */
  private VBox createCenterPane() {
    VBox centerPane = new VBox(35);
    centerPane.setAlignment(Pos.CENTER);
    centerPane.setPrefWidth(550);
    centerPane.setMaxWidth(550);
    centerPane.setPadding(new Insets(40));
    centerPane.getStyleClass().addAll("md-card-elevated", "md-surface");

    // Welcome section
    VBox welcomeSection = createWelcomeSection();

    // Game selection section
    VBox gameSection = createGameSelectionSection();

    // Player info section
    VBox playerInfoSection = createPlayerInfoSection();

    centerPane.getChildren().addAll(welcomeSection, gameSection, playerInfoSection);
    return centerPane;
  }

  /**
   * <p>Creates the welcome section with title and subtitle.</p>
   * <p>Uses Material Design typography for visual hierarchy.</p>
   *
   * @return A {@link VBox} containing welcome elements
   */
  private VBox createWelcomeSection() {
    VBox welcomeSection = new VBox(10);
    welcomeSection.setAlignment(Pos.CENTER);

    Label titleLabel = new Label("Choose Your Game");
    titleLabel.getStyleClass().add("md-headline-medium");

    Label subtitleLabel = new Label("Select a board game to begin your adventure");
    subtitleLabel.getStyleClass().add("md-body-large");

    welcomeSection.getChildren().addAll(titleLabel, subtitleLabel);
    return welcomeSection;
  }

  /**
   * <p>Creates the game selection section with Material Design game cards.</p>
   * <p>Features card-based layout with icons and descriptions.</p>
   *
   * @return A {@link VBox} containing game selection elements
   */
  private VBox createGameSelectionSection() {
    VBox gameSection = new VBox(16);
    gameSection.setAlignment(Pos.CENTER);

    Label sectionTitle = new Label("Available Games");
    sectionTitle.getStyleClass().add("md-title-large");

    // Game selection cards with Material Design styling
    Button ladderGameButton = createGameButton("Ladder Game",
        "Classic snakes and ladders with modern twists", "ladder");
    ladderGameButton.setOnAction(event -> startLadderGame());

    Button missingDiamondButton = createGameButton("Missing Diamond",
        "Adventure across Africa to find the lost diamond", "diamond");
    missingDiamondButton.setOnAction(event -> startMissingDiamond());

    gameSection.getChildren().addAll(sectionTitle, ladderGameButton, missingDiamondButton);
    return gameSection;
  }

  /**
   * <p>Creates a Material Design game card button.</p>
   * <p>Includes icon container, title, and description for better UX.</p>
   *
   * @param title       The title of the game
   * @param description Brief description of the game
   * @param gameType    The type of game (for icon selection)
   * @return A styled {@link Button} as a Material Design card
   */
  private Button createGameButton(String title, String description, String gameType) {
    Button button = new Button();

    // Create icon container with game-specific styling
    Label iconLabel = new Label(gameType.equals("ladder") ? "🪜" : "💎");
    iconLabel.getStyleClass().add("md-game-icon");
    iconLabel.setMinSize(48, 48);
    iconLabel.setMaxSize(48, 48);
    iconLabel.setAlignment(Pos.CENTER);

    // Create text content
    VBox textContent = new VBox(4);
    textContent.setAlignment(Pos.CENTER_LEFT);

    Label titleLabel = new Label(title);
    titleLabel.getStyleClass().add("md-title-medium");

    Label descLabel = new Label(description);
    descLabel.getStyleClass().add("md-body-medium");
    descLabel.setWrapText(true);
    descLabel.setMaxWidth(300);

    textContent.getChildren().addAll(titleLabel, descLabel);

    // Combine icon and text in horizontal layout
    javafx.scene.layout.HBox cardContent = new javafx.scene.layout.HBox(16);
    cardContent.setAlignment(Pos.CENTER_LEFT);
    cardContent.getChildren().addAll(iconLabel, textContent);

    button.setGraphic(cardContent);
    button.setPrefSize(450, 90);
    button.setMaxWidth(450);
    button.getStyleClass().add("md-game-card");

    return button;
  }

  /**
   * <p>Creates a section showing player information.</p>
   * <p>Uses Material Design chip styling with success colors.</p>
   *
   * @return A {@link VBox} containing player info elements
   */
  private VBox createPlayerInfoSection() {
    VBox playerInfoSection = new VBox(8);
    playerInfoSection.setAlignment(Pos.CENTER);
    playerInfoSection.getStyleClass().add("md-card-filled");
    playerInfoSection.setPadding(new Insets(16, 24, 16, 24));

    // Success indicator with checkmark
    Label infoTitle = new Label("✓ Players Ready");
    infoTitle.getStyleClass().addAll("md-title-medium", "md-success-text");

    // Count players from file
    int playerCount = countPlayersFromFile();
    Label playerCountLabel = new Label(String.format("%d players configured and ready to play", playerCount));
    playerCountLabel.getStyleClass().add("md-body-medium");

    playerInfoSection.getChildren().addAll(infoTitle, playerCountLabel);
    return playerInfoSection;
  }

  /**
   * <p>Counts the number of players from the saved file.</p>
   * <p>Reads the player data file to determine how many players are configured.</p>
   *
   * @return The number of configured players
   */
  private int countPlayersFromFile() {
    File playerFile = new File("data/saves/playerData/Players.csv");
    if (!playerFile.exists()) {
      return 0;
    }

    try {
      java.util.Scanner scanner = new java.util.Scanner(playerFile);
      int count = 0;

      // Skip header
      if (scanner.hasNextLine()) {
        scanner.nextLine();
      }

      // Count data lines
      while (scanner.hasNextLine()) {
        String line = scanner.nextLine().trim();
        if (!line.isEmpty()) {
          count++;
        }
      }
      scanner.close();
      return count;
    } catch (Exception e) {
      return 0;
    }
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
    File playerFile = new File("data/saves/playerData/Players.csv");
    return playerFile.exists() && playerFile.isFile();
  }

  /**
   * <p>Shows an alert dialog with enhanced styling.</p>
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

    // Apply CSS styling to alert if possible
    alert.getDialogPane().getStylesheets().addAll(
        primaryStage.getScene().getStylesheets()
    );

    alert.showAndWait();
  }
}