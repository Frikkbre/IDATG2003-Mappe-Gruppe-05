package edu.ntnu.idi.bidata.idatg2003mappe.app.missingdiamond.ui;

import edu.ntnu.idi.bidata.idatg2003mappe.app.common.ui.NavBar;
import edu.ntnu.idi.bidata.idatg2003mappe.app.common.ui.UIComponentFactory;
import edu.ntnu.idi.bidata.idatg2003mappe.app.missingdiamond.controller.MissingDiamondController;
import edu.ntnu.idi.bidata.idatg2003mappe.app.missingdiamond.service.MapConfigService;
import edu.ntnu.idi.bidata.idatg2003mappe.filehandling.exceptionhandling.FileHandlingException;
import edu.ntnu.idi.bidata.idatg2003mappe.filehandling.map.MapConfig;
import edu.ntnu.idi.bidata.idatg2003mappe.util.map.MapDesignerListener;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.MenuBar;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import java.util.logging.Logger;

/**
 * GUI class for the Missing Diamond game.
 *
 * @author Simen Gudbrandsen and Frikk Breadsroed
 * @version 0.0.6
 * @since 23.04.2025
 */
public class MissingDiamondGUI extends Application implements MapDesignerListener {

  // Logger for logging messages
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

  @Override
  public void start(Stage primaryStage) {
    this.primaryStage = primaryStage;
    primaryStage.setTitle("Missing Diamond");

    // Initialize main layout
    mainLayout = new BorderPane();
    mainLayout.setPrefSize(1440, 840);
    mainLayout.setStyle("-fx-background-color: white;");

    // Create and set up board view first (needed for other components)
    boardView = new BoardView();

    // Initialize game controller
    initializeGameController();

    // Now set up NavBar with the proper controller reference
    navBar = new NavBar();
    navBar.setStage(primaryStage);
    navBar.setGameController(gameController);
    navBar.setMissingDiamondGUI(this);

    // Initialize UI components
    initializeUIComponents();

    // Set up layout
    setupLayout();

    // Load map configuration
    loadMapConfiguration();

    // Create scene and show stage
    Scene scene = new Scene(mainLayout);
    primaryStage.setScene(scene);
    primaryStage.show();

    // Update UI after layout is complete
    Platform.runLater(() -> boardView.updateUI());
  }

  private void initializeGameController() {
    gameController = new MissingDiamondController();

    // Connect controller to UI components
    boardView.setGameController(gameController);
  }

  private void initializeUIComponents() {
    mapDesignerManager = createMapDesignerManager();
    controlPanel = new GameControlPanel(gameController, boardView);
    statusPanel = new PlayerStatusPanel(gameController);

    controlPanel.setStatusPanel(statusPanel);

    boardView.setMapDesignerManager(mapDesignerManager);

    // Connect UI components with controller
    gameController.registerView(this);

    // Add mouse click handler for coordinate mode
    boardView.getOverlayPane().setOnMouseClicked(e -> {

      // Check if coordinate mode is enabled
          mapDesignerManager.getMapDesignerTool().isCoordinateMode();

      if (mapDesignerManager.getMapDesignerTool().isCoordinateMode()) {
        mapDesignerManager.getMapDesignerTool().handleCoordinateClick(e.getX(), e.getY(), boardView.getMapView());
      }
    });
  }

  private MapDesignerManager createMapDesignerManager() {
    return new MapDesignerManager(
        boardView.getOverlayPane(),
        boardView.getMapView().getFitWidth(),
        boardView.getMapView().getFitHeight(),

        this
    );
  }

  private void setupLayout() {
    MenuBar menuBar = navBar.createMenuBar();

    // Create developer controls
    HBox devControls = createDevControls();

    // Set up the top container with all elements
    VBox topContainer = new VBox(
        menuBar,
        mapDesignerManager.getStatusLabel(),
        devControls
    );
    mainLayout.setTop(topContainer);

    // Set up center with board and control panel
    GridPane grid = new GridPane();

    ColumnConstraints col0 = new ColumnConstraints();
    col0.setMinWidth(250);
    col0.setMaxWidth(250);
    col0.setPrefWidth(250);

    ColumnConstraints col1 = new ColumnConstraints();
    col1.setHgrow(Priority.ALWAYS);

    grid.getColumnConstraints().addAll(col0, col1);
    grid.setPadding(Insets.EMPTY);
    grid.setVgap(0);
    grid.setHgap(0);

    // Add sidebar and board to grid
    VBox leftSidebar = createLeftSidebar();
    grid.add(leftSidebar, 0, 0);

    StackPane mapContainer = new StackPane();
    mapContainer.setAlignment(javafx.geometry.Pos.CENTER);
    mapContainer.getChildren().add(boardView);

    grid.add(mapContainer, 1, 0);

    RowConstraints row = new RowConstraints();
    row.setVgrow(Priority.ALWAYS);
    grid.getRowConstraints().add(row);

    mainLayout.setCenter(grid);
  }

  private HBox createDevControls() {
    HBox devControls = new HBox(10);
    devControls.setPadding(new Insets(5));
    devControls.setAlignment(javafx.geometry.Pos.CENTER_LEFT);

    // Add map designer components
    devControls.getChildren().addAll(
        new Label("Tile Type:"), mapDesignerManager.getTileTypeSelector(),
        new Label("Source ID:"), mapDesignerManager.getSourceIdField(),
        new Label("Target ID:"), mapDesignerManager.getTargetIdField(),
        UIComponentFactory.createActionButton("Create Connection", e ->
            mapDesignerManager.createConnection())
    );

    // Hide developer controls initially
    mapDesignerManager.getTileTypeSelector().setVisible(false);
    mapDesignerManager.getSourceIdField().setVisible(false);
    mapDesignerManager.getTargetIdField().setVisible(false);

    return devControls;
  }

  private VBox createLeftSidebar() {
    VBox sidebar = new VBox(10);
    sidebar.setPrefWidth(250);
    sidebar.setMinWidth(250);
    sidebar.setMaxWidth(250);
    sidebar.setPadding(new Insets(10));
    sidebar.setBorder(new Border(new BorderStroke(
        javafx.scene.paint.Color.LIGHTGRAY,
        BorderStrokeStyle.SOLID,
        CornerRadii.EMPTY,
        BorderWidths.DEFAULT
    )));

    sidebar.getChildren().addAll(
        statusPanel,
        controlPanel
    );

    return sidebar;
  }

  public void updateBoardUI() {
    if (boardView != null) {
      boardView.updateUI();

      // Also update the player status panel if available
      if (statusPanel != null) {
        statusPanel.updateScoreBoard();
      }
    } else {
      logger.severe("Error: Cannot update board UI - boardView is null");
    }
  }

  private void loadMapConfiguration() {
    try {
      MapConfig mapConfig = MapConfigService.loadMapConfig();

      // Update board view with loaded configuration
      Platform.runLater(() -> {
        boardView.createLocationsFromConfig(mapConfig);
        boardView.synchronizeTilesWithDesigner(mapDesignerManager);
        boardView.updateUI();
      });
    } catch (FileHandlingException e) {
      controlPanel.logMessage("Error loading map configuration: " + e.getMessage());
      logger.severe("Error loading map configuration: " + e.getMessage());

      // Fall back to hard-coded map data
      Platform.runLater(() -> {
        boardView.createDefaultLocations();
        boardView.synchronizeTilesWithDesigner(mapDesignerManager);
        boardView.updateUI();
      });
    }
  }

  public MapDesignerManager getMapDesignerManager() {
    return mapDesignerManager;
  }

  @Override
  public void onLogMessage(String message) {
    controlPanel.logMessage(message);
  }

  @Override
  public void onCoordinateModeToggled(boolean enabled) {
    mapDesignerManager.getTileTypeSelector().setVisible(enabled);
    mapDesignerManager.getSourceIdField().setVisible(enabled);
    mapDesignerManager.getTargetIdField().setVisible(enabled);

    controlPanel.setRollButtonDisabled(enabled);

    logger.info("Coordinate mode toggled: " + enabled);
  }

  @Override
  public void onConnectionModeToggled(boolean enabled) {
    logger.info("Connection mode toggled: " + enabled);
    mapDesignerManager.resetConnectionSourceId();
  }

  @Override
  public void onMapDataExported(String data, boolean success) {
    logger.info("Map data exported: " + success);
  }

  public static void main(String[] args) {
    launch(args);
  }
}