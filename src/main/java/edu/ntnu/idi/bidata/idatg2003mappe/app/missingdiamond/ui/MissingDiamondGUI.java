package edu.ntnu.idi.bidata.idatg2003mappe.app.missingdiamond.ui;

import edu.ntnu.idi.bidata.idatg2003mappe.app.common.ui.NavBar;
import edu.ntnu.idi.bidata.idatg2003mappe.app.missingdiamond.controller.MissingDiamondController;
import edu.ntnu.idi.bidata.idatg2003mappe.app.missingdiamond.service.MapConfigService;
import edu.ntnu.idi.bidata.idatg2003mappe.entity.player.Player;
import edu.ntnu.idi.bidata.idatg2003mappe.filehandling.exceptionhandling.FileHandlingException;
import edu.ntnu.idi.bidata.idatg2003mappe.filehandling.map.MapConfig;
import edu.ntnu.idi.bidata.idatg2003mappe.util.map.MapDesignerListener;
import javafx.animation.FadeTransition;
import javafx.animation.ScaleTransition;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.logging.Logger;

/**
 * <p>Enhanced GUI class for the Missing Diamond game with modern CSS styling.</p>
 * <p>Features a professional, user-friendly interface with:</p>
 * <ul>
 *   <li>Modern glassmorphism design using CSS template</li>
 *   <li>Responsive layout with proper visual hierarchy</li>
 *   <li>Enhanced user feedback and error handling</li>
 *   <li>Smooth animations and transitions</li>
 *   <li>Intuitive game controls and information display</li>
 * </ul>
 *
 * @author Simen Gudbrandsen and Frikk Breadsroed
 * @version 1.0.0
 * @since 23.05.2025
 */
public class MissingDiamondGUI extends Application implements MapDesignerListener {

  private static final Logger logger = Logger.getLogger(MissingDiamondGUI.class.getName());

  // Game components
  private MissingDiamondController gameController;
  private Stage primaryStage;
  private BorderPane mainLayout;
  private NavBar navBar;

  // UI Components
  private BoardView boardView;
  private GameControlPanel controlPanel;
  private PlayerStatusPanel statusPanel;
  private MapDesignerManager mapDesignerManager;

  // Enhanced UI Elements
  private VBox gameHeader;
  private HBox gameContent;
  private VBox leftSidebar;
  private Label gameStatusLabel;
  private ProgressIndicator loadingIndicator;
  private VBox developerToolsPanel;

  public static void main(String[] args) {
    launch(args);
  }

  @Override
  public void start(Stage primaryStage) {
    this.primaryStage = primaryStage;
    primaryStage.setTitle("Missing Diamond - Adventure Across Africa");
    primaryStage.setMinWidth(1200);
    primaryStage.setMinHeight(800);

    try {
      initializeComponents();
      setupMainLayout();
      createScene(); // Create scene before loading stylesheet
      loadStylesheet();

      // Load game configuration with loading animation
      loadGameConfigurationWithAnimation();

      primaryStage.show();

      // Initialize game state after UI is ready
      Platform.runLater(this::finalizeInitialization);

    } catch (Exception e) {
      logger.severe("Failed to initialize Missing Diamond GUI: " + e.getMessage());
      showErrorDialog("Initialization Error",
          "Failed to start the game. Please check your installation.", e);
    }
  }

  /**
   * <p>Sets up the main layout structure using Material Design CSS classes.</p>
   */
  private void setupMainLayout() {
    mainLayout = new BorderPane();
    mainLayout.getStyleClass().add("md-game-background");

    // Create main content area
    createMainContent();

    // Create developer tools panel (initially hidden)
    createDeveloperToolsPanel();

    // Set layout components
    mainLayout.setTop(createTopSection());
    mainLayout.setCenter(gameContent);

    logger.info("Main layout structure created");
  }

  /**
   * <p>Creates the top section with navigation and game status.</p>
   */
  private VBox createTopSection() {
    VBox topSection = new VBox();
    topSection.getChildren().addAll(
        navBar.createMenuBar(),
        developerToolsPanel
    );
    return topSection;
  }

  /**
   * <p>Creates the main game content with Material Design responsive layout.</p>
   */
  private void createMainContent() {
    gameContent = new HBox(24);
    gameContent.setPadding(new Insets(24));
    gameContent.setAlignment(Pos.CENTER);

    // Left sidebar for controls and status
    createLeftSidebar();

    // Center board area
    StackPane boardContainer = createBoardContainer();

    // Set up responsive constraints
    HBox.setHgrow(boardContainer, Priority.ALWAYS);

    gameContent.getChildren().addAll(leftSidebar, boardContainer);
  }

  private void initializeUIComponents() {
    // Initialize gameHeader with Material Design styling
    gameHeader = new VBox(10);
    gameHeader.setPadding(new Insets(16, 24, 16, 24));
    gameHeader.setAlignment(Pos.CENTER);
    gameHeader.getStyleClass().add("md-card-filled");

    // Initialize game status label with Material typography
    gameStatusLabel = new Label("Loading game...");
    gameStatusLabel.getStyleClass().add("md-title-medium");

    // Initialize loading indicator
    loadingIndicator = new ProgressIndicator();
    loadingIndicator.setPrefSize(28, 28);
    loadingIndicator.setVisible(false);

    // Add components to gameHeader
    HBox statusBox = new HBox(12);
    statusBox.setAlignment(Pos.CENTER);
    statusBox.getChildren().addAll(gameStatusLabel, loadingIndicator);

    gameHeader.getChildren().add(statusBox);
  }

  private void initializeComponents() {
    // Initialize UI components first
    initializeUIComponents();

    // Initialize core game components
    gameController = new MissingDiamondController();
    boardView = new BoardView();

    // Connect board view to controller
    boardView.setGameController(gameController);

    // Initialize map designer
    mapDesignerManager = createMapDesignerManager();
    boardView.setMapDesignerManager(mapDesignerManager);

    // Initialize UI panels
    controlPanel = new GameControlPanel(gameController, boardView);
    statusPanel = new PlayerStatusPanel(gameController);
    controlPanel.setStatusPanel(statusPanel);

    // Initialize navigation
    navBar = new NavBar();
    navBar.setStage(primaryStage);
    navBar.setGameController(gameController);
    navBar.setMissingDiamondGUI(this);

    // Register listener
    gameController.registerView(this);

    logger.info("Game components initialized successfully");
  }

  /**
   * <p>Creates the Material Design left sidebar with game controls and player status.</p>
   */
  private void createLeftSidebar() {
    leftSidebar = new VBox(20);
    leftSidebar.setPrefWidth(300);
    leftSidebar.setMaxWidth(300);
    leftSidebar.setPadding(new Insets(20));
    leftSidebar.getStyleClass().add("md-sidebar");

    // Player status section with Material card
    VBox statusSection = new VBox(12);
    statusSection.getStyleClass().add("md-card-elevated");
    statusSection.setPadding(new Insets(16));

    Label statusTitle = new Label("Player Status");
    statusTitle.getStyleClass().add("md-title-medium");

    statusSection.getChildren().addAll(statusTitle, statusPanel);

    // Game controls section with Material card
    VBox controlsSection = new VBox(12);
    controlsSection.getStyleClass().add("md-card-elevated");
    controlsSection.setPadding(new Insets(16));

    Label controlsTitle = new Label("Game Controls");
    controlsTitle.getStyleClass().add("md-title-medium");

    controlsSection.getChildren().addAll(controlsTitle, controlPanel);

    // Add sections with Material dividers
    Separator divider = new Separator();
    divider.getStyleClass().add("md-divider");

    leftSidebar.getChildren().addAll(
        statusSection,
        divider,
        controlsSection
    );
  }

  /**
   * <p>Creates the board container with Material Design styling.</p>
   */
  private StackPane createBoardContainer() {
    StackPane boardContainer = new StackPane();
    boardContainer.setAlignment(Pos.CENTER);
    boardContainer.getStyleClass().add("md-board-container");
    boardContainer.setPadding(new Insets(16));

    // Board section with proper styling
    VBox boardSection = new VBox(12);
    boardSection.setAlignment(Pos.CENTER);

    boardSection.getChildren().addAll(boardView);
    boardContainer.getChildren().add(boardSection);

    return boardContainer;
  }

  /**
   * <p>Creates the developer tools panel with Material Design styling (hidden by default).</p>
   */
  private void createDeveloperToolsPanel() {
    developerToolsPanel = new VBox(12);
    developerToolsPanel.setPadding(new Insets(12, 24, 12, 24));
    developerToolsPanel.getStyleClass().add("md-card-filled");
    developerToolsPanel.setVisible(false);
    developerToolsPanel.setManaged(false);

    Label devTitle = new Label("Developer Tools");
    devTitle.getStyleClass().add("md-title-medium");

    HBox devControls = new HBox(16);
    devControls.setAlignment(Pos.CENTER_LEFT);

    // Tile type selector
    Label tileTypeLabel = new Label("Tile Type:");
    tileTypeLabel.getStyleClass().add("md-label-large");

    // Connection controls
    Label sourceLabel = new Label("Source ID:");
    Label targetLabel = new Label("Target ID:");
    sourceLabel.getStyleClass().add("md-label-large");
    targetLabel.getStyleClass().add("md-label-large");

    Button createConnectionBtn = new Button("Create Connection");
    createConnectionBtn.getStyleClass().add("md-button-outlined");
    createConnectionBtn.setOnAction(e -> mapDesignerManager.createConnection());

    devControls.getChildren().addAll(
        tileTypeLabel, mapDesignerManager.getTileTypeSelector(),
        sourceLabel, mapDesignerManager.getSourceIdField(),
        targetLabel, mapDesignerManager.getTargetIdField(),
        createConnectionBtn
    );

    developerToolsPanel.getChildren().addAll(devTitle, devControls);
  }

  /**
   * <p>Creates the map designer manager with proper dimensions.</p>
   */
  private MapDesignerManager createMapDesignerManager() {
    return new MapDesignerManager(
        boardView.getOverlayPane(),
        900, // Board width
        700, // Board height
        this
    );
  }

  /**
   * <p>Loads the CSS stylesheet for enhanced styling.</p>
   */
  private void loadStylesheet() {
    try {
      String cssFile = getClass().getResource("/game-style/game-styles.css").toExternalForm();
      logger.info("Loading CSS from: " + cssFile);
    } catch (Exception e) {
      logger.warning("Could not load CSS file: " + e.getMessage());
      showWarningDialog("Styling Warning",
          "Could not load game styling. Using default appearance.");
    }
  }

  /**
   * <p>Creates and configures the main scene.</p>
   */
  private void createScene() {
    Scene scene = new Scene(mainLayout, 1440, 840);

    // Load CSS
    try {
      String cssFile = getClass().getResource("/game-style/game-styles.css").toExternalForm();
      scene.getStylesheets().add(cssFile);
    } catch (Exception e) {
      logger.warning("Failed to load CSS: " + e.getMessage());
    }

    primaryStage.setScene(scene);

    // Add window resize handling
    scene.widthProperty().addListener((obs, oldVal, newVal) -> handleWindowResize());
    scene.heightProperty().addListener((obs, oldVal, newVal) -> handleWindowResize());
  }

  /**
   * <p>Handles window resize events for responsive design.</p>
   */
  private void handleWindowResize() {
    Platform.runLater(() -> {

      // Update board view dimensions if needed
      if (mapDesignerManager != null) {
        mapDesignerManager.getMapDesignerTool().updateMapDimensions(
            boardView.getMapView().getFitWidth(),
            boardView.getMapView().getFitHeight()
        );
      }
    });
  }

  /**
   * <p>Loads game configuration with animated loading feedback.</p>
   */
  private void loadGameConfigurationWithAnimation() {
    showLoading(true);

    // Load in background thread
    Platform.runLater(() -> {
      try {
        MapConfig mapConfig = MapConfigService.loadMapConfig();

        Platform.runLater(() -> {
          boardView.createLocationsFromConfig(mapConfig);
          boardView.synchronizeTilesWithDesigner(mapDesignerManager);
          showLoading(false);

          // Add fade-in animation for board
          FadeTransition fade = new FadeTransition(Duration.millis(800), boardView);
          fade.setFromValue(0);
          fade.setToValue(1);
          fade.play();
        });

      } catch (FileHandlingException e) {
        Platform.runLater(() -> {
          boardView.createDefaultLocations();
          boardView.synchronizeTilesWithDesigner(mapDesignerManager);
          showLoading(false);

          showWarningDialog("Map Loading Warning",
              "Could not load custom map. Using default configuration.");
        });
      }
    });
  }

  /**
   * <p>Finalizes initialization after UI is ready.</p>
   */
  private void finalizeInitialization() {
    updateBoardUI();
    updateGameStatus(getCurrentPlayerStatus());

    // Add click handler for coordinate mode
    boardView.getOverlayPane().setOnMouseClicked(e -> {
      if (mapDesignerManager.getMapDesignerTool().isCoordinateMode()) {
        mapDesignerManager.getMapDesignerTool().handleCoordinateClick(
            e.getX(), e.getY(), boardView.getMapView());
      }
    });

    logger.info("Missing Diamond GUI initialization completed");
  }

  /**
   * <p>Updates the game status label with current information.</p>
   */
  private void updateGameStatus(String status) {
    if (gameStatusLabel != null) {
      gameStatusLabel.setText(status);

      // Add subtle animation
      ScaleTransition scale = new ScaleTransition(Duration.millis(200), gameStatusLabel);
      scale.setFromX(0.95);
      scale.setFromY(0.95);
      scale.setToX(1.0);
      scale.setToY(1.0);
      scale.play();
    }
  }

  /**
   * <p>Gets the current player status string.</p>
   */
  private String getCurrentPlayerStatus() {
    if (gameController != null && gameController.getCurrentPlayer() != null) {
      Player currentPlayer = gameController.getCurrentPlayer();
      int balance = gameController.getBanker().getBalance(currentPlayer);
      return String.format("Current Player: %s | Balance: £%d",
          currentPlayer.getName(), balance);
    }
    return "Game ready - Roll the die to start!";
  }

  /**
   * <p>Shows or hides the loading indicator.</p>
   */
  private void showLoading(boolean show) {
    if (loadingIndicator != null) {
      loadingIndicator.setVisible(show);
    }
  }

  /**
   * <p>Updates the board UI and related components.</p>
   */
  public void updateBoardUI() {
    if (boardView != null) {
      boardView.updateUI();
      updateGameStatus(getCurrentPlayerStatus());

      if (statusPanel != null) {
        statusPanel.updateScoreBoard();
      }
    }
  }

  /**
   * <p>Shows an error dialog with enhanced styling.</p>
   */
  private void showErrorDialog(String title, String message, Exception e) {
    Alert alert = new Alert(Alert.AlertType.ERROR);
    alert.setTitle(title);
    alert.setHeaderText(null);
    alert.setContentText(message + (e != null ? "\n\nError: " + e.getMessage() : ""));

    if (primaryStage != null && primaryStage.getScene() != null) {
      alert.getDialogPane().getStylesheets().addAll(primaryStage.getScene().getStylesheets());
    }
    alert.showAndWait();
  }

  /**
   * <p>Shows a warning dialog with enhanced styling.</p>
   */
  private void showWarningDialog(String title, String message) {
    Alert alert = new Alert(Alert.AlertType.WARNING);
    alert.setTitle(title);
    alert.setHeaderText(null);
    alert.setContentText(message);

    if (primaryStage != null && primaryStage.getScene() != null) {
      alert.getDialogPane().getStylesheets().addAll(primaryStage.getScene().getStylesheets());
    }
    alert.showAndWait();
  }

  // MapDesignerListener implementation
  @Override
  public void onLogMessage(String message) {
    controlPanel.logMessage(message);
    logger.info("Map Designer: " + message);
  }

  @Override
  public void onCoordinateModeToggled(boolean enabled) {
    developerToolsPanel.setVisible(enabled);
    developerToolsPanel.setManaged(enabled);

    if (enabled) {
      updateGameStatus("Developer Mode: Click on map to place coordinates");
      // Blur the game content slightly when in dev mode
      GaussianBlur blur = new GaussianBlur(1.5);
      gameContent.setEffect(blur);
    } else {
      updateGameStatus(getCurrentPlayerStatus());
      gameContent.setEffect(null);
    }

    controlPanel.setRollButtonDisabled(enabled);
  }

  @Override
  public void onConnectionModeToggled(boolean enabled) {
    mapDesignerManager.resetConnectionSourceId();
    String mode = enabled ? "Connection Mode: Click source then target tile" : getCurrentPlayerStatus();
    updateGameStatus(mode);
  }

  @Override
  public void onMapDataExported(String data, boolean success) {
    String message = success ? "Map data exported successfully!" : "Failed to export map data";
    updateGameStatus(message);

    if (success) {
      Alert alert = new Alert(Alert.AlertType.INFORMATION);
      alert.setTitle("Export Success");
      alert.setHeaderText(null);
      alert.setContentText("Map configuration has been exported successfully!");
      alert.getDialogPane().getStylesheets().addAll(primaryStage.getScene().getStylesheets());
      alert.showAndWait();
    }
  }

  /**
   * <p>Gets the map designer manager for external access.</p>
   */
  public MapDesignerManager getMapDesignerManager() {
    return mapDesignerManager;
  }
}