package edu.ntnu.idi.bidata.idatg2003mappe.app.boardgameselector;

import edu.ntnu.idi.bidata.idatg2003mappe.app.common.ui.NavBar;
import edu.ntnu.idi.bidata.idatg2003mappe.app.laddergame.ui.LadderGameGUI;
import edu.ntnu.idi.bidata.idatg2003mappe.app.missingdiamond.ui.MissingDiamondGUI;
import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.TranslateTransition;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.effect.Glow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.util.Duration;

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

    setStage(primaryStage);
    primaryStage.setTitle("Select Your Adventure");

    // Create main layout
    BorderPane mainLayout = new BorderPane();
    mainLayout.getStyleClass().add("main-container");

    // Create content
    VBox centerContent = createCenterContent();
    mainLayout.setCenter(centerContent);

    // Create scene and add CSS
    Scene scene = new Scene(mainLayout, 1440, 840);
    scene.getStylesheets().add(getClass().getResource("/game-style/game-styles.css").toExternalForm());

    primaryStage.setScene(scene);
    primaryStage.show();

    // Initialize game GUIs
    this.ladderGameGUI = new LadderGameGUI();
    this.missingDiamondGUI = new MissingDiamondGUI();

    // Apply entrance animation
    applyEntranceAnimation(centerContent);
  }

  /**
   * <p>Method to create the center content of the GUI.</p>
   * <p>Creates a visually appealing game selection interface with animated elements.</p>
   *
   * @return centerPane A VBox containing the game selection UI elements
   */
  private VBox createCenterContent() {
    VBox centerPane = new VBox(50);
    centerPane.setAlignment(Pos.CENTER);
    centerPane.setPadding(new Insets(40));

    // Title with animation
    Label titleLabel = new Label("Choose Your Adventure");
    titleLabel.getStyleClass().add("title");

    Label subtitleLabel = new Label("Select a game to begin your journey");
    subtitleLabel.getStyleClass().add("subtitle");

    // Game cards container
    HBox gameCards = new HBox(40);
    gameCards.setAlignment(Pos.CENTER);

    // Create game cards
    VBox ladderCard = createGameCard(
        "Ladder Game",
        "Classic snakes and ladders adventure",
        "🎲 Roll the dice\n🪜 Climb ladders\n🐍 Avoid snakes\n🏆 Reach the top!",
        "ladder-card"
    );

    VBox missingDiamondCard = createGameCard(
        "Missing Diamond",
        "Find the legendary African Star",
        "💎 Search for treasure\n🗺️ Explore Africa\n💰 Collect gems\n⭐ Find the diamond!",
        "diamond-card"
    );

    // Add click handlers
    ladderCard.setOnMouseClicked(e -> startLadderGame());
    missingDiamondCard.setOnMouseClicked(e -> startMissingDiamond());

    gameCards.getChildren().addAll(ladderCard, missingDiamondCard);

    centerPane.getChildren().addAll(titleLabel, subtitleLabel, gameCards);
    return centerPane;
  }

  /**
   * <p>Creates a game card with modern design.</p>
   * <p>Each card represents a game option with title, description, and features.</p>
   *
   * @param title The game title
   * @param subtitle The game subtitle
   * @param features The game features
   * @param styleClass Additional style class for the card
   * @return A styled VBox representing the game card
   */
  private VBox createGameCard(String title, String subtitle, String features, String styleClass) {
    VBox card = new VBox(20);
    card.getStyleClass().addAll("card", "glass-effect");
    card.setPrefSize(400, 300);
    card.setAlignment(Pos.CENTER);
    card.setPadding(new Insets(30));
    card.setCursor(javafx.scene.Cursor.HAND);

    // Title
    Label titleLabel = new Label(title);
    titleLabel.getStyleClass().add("heading");

    // Subtitle
    Label subtitleLabel = new Label(subtitle);
    subtitleLabel.getStyleClass().add("body-text");

    // Features
    Label featuresLabel = new Label(features);
    featuresLabel.getStyleClass().add("body-text");
    featuresLabel.setStyle("-fx-text-alignment: left;");

    // Play button
    Button playButton = new Button("PLAY NOW");
    playButton.getStyleClass().addAll("button", "game-button");
    playButton.setPrefWidth(200);

    card.getChildren().addAll(titleLabel, subtitleLabel, featuresLabel, playButton);

    // Add hover effects
    addCardHoverEffects(card);

    return card;
  }

  /**
   * <p>Adds hover effects to game cards.</p>
   * <p>Creates smooth transitions and glow effects when hovering.</p>
   *
   * @param card The card to add effects to
   */
  private void addCardHoverEffects(VBox card) {
    Glow glow = new Glow(0);
    card.setEffect(glow);

    card.setOnMouseEntered(e -> {
      ScaleTransition scaleUp = new ScaleTransition(Duration.millis(200), card);
      scaleUp.setToX(1.05);
      scaleUp.setToY(1.05);

      TranslateTransition moveUp = new TranslateTransition(Duration.millis(200), card);
      moveUp.setToY(-10);

      ParallelTransition hoverAnimation = new ParallelTransition(scaleUp, moveUp);
      hoverAnimation.play();

      glow.setLevel(0.3);
    });

    card.setOnMouseExited(e -> {
      ScaleTransition scaleDown = new ScaleTransition(Duration.millis(200), card);
      scaleDown.setToX(1.0);
      scaleDown.setToY(1.0);

      TranslateTransition moveDown = new TranslateTransition(Duration.millis(200), card);
      moveDown.setToY(0);

      ParallelTransition exitAnimation = new ParallelTransition(scaleDown, moveDown);
      exitAnimation.play();

      glow.setLevel(0);
    });
  }

  /**
   * <p>Starts the Ladder Game.</p>
   * <p>Attempts to launch the Ladder Game UI and displays an error alert if it fails.</p>
   */
  private void startLadderGame() {
    try {
      // Add exit animation before switching
      FadeTransition fadeOut = new FadeTransition(Duration.millis(300), primaryStage.getScene().getRoot());
      fadeOut.setFromValue(1);
      fadeOut.setToValue(0);
      fadeOut.setOnFinished(e -> {
        try {
          ladderGameGUI.start(getStage());
        } catch (Exception ex) {
          showAlert("Error", "Failed to start Ladder Game: " + ex.getMessage(), Alert.AlertType.ERROR);
        }
      });
      fadeOut.play();
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
      // Add exit animation before switching
      FadeTransition fadeOut = new FadeTransition(Duration.millis(300), primaryStage.getScene().getRoot());
      fadeOut.setFromValue(1);
      fadeOut.setToValue(0);
      fadeOut.setOnFinished(e -> {
        try {
          missingDiamondGUI.start(getStage());
        } catch (Exception ex) {
          showAlert("Error", "Failed to start Missing Diamond: " + ex.getMessage(), Alert.AlertType.ERROR);
        }
      });
      fadeOut.play();
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
   * <p>Shows a styled alert dialog.</p>
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

    // Apply CSS to alert
    DialogPane dialogPane = alert.getDialogPane();
    dialogPane.getStylesheets().add(getClass().getResource("/game-style/game-styles.css").toExternalForm());
    dialogPane.getStyleClass().add("dialog-pane");

    alert.showAndWait();
  }

  /**
   * <p>Applies entrance animation to the content.</p>
   * <p>Creates a smooth fade-in and scale effect when the screen loads.</p>
   *
   * @param node The node to animate
   */
  private void applyEntranceAnimation(javafx.scene.Node node) {
    node.setOpacity(0);
    node.setScaleX(0.8);
    node.setScaleY(0.8);

    FadeTransition fadeIn = new FadeTransition(Duration.millis(500), node);
    fadeIn.setFromValue(0);
    fadeIn.setToValue(1);

    ScaleTransition scaleIn = new ScaleTransition(Duration.millis(500), node);
    scaleIn.setFromX(0.8);
    scaleIn.setFromY(0.8);
    scaleIn.setToX(1);
    scaleIn.setToY(1);

    ParallelTransition entrance = new ParallelTransition(fadeIn, scaleIn);
    entrance.play();
  }

  public static void main(String[] args) {
    launch(args);
  }
}