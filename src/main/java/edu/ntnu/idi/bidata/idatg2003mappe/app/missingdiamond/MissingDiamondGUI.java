package edu.ntnu.idi.bidata.idatg2003mappe.app.missingdiamond;

import edu.ntnu.idi.bidata.idatg2003mappe.app.NavBar;
import edu.ntnu.idi.bidata.idatg2003mappe.entity.Player;
import edu.ntnu.idi.bidata.idatg2003mappe.filehandling.exceptionhandling.FileHandlingException;
import edu.ntnu.idi.bidata.idatg2003mappe.filehandling.map.MapConfig;
import edu.ntnu.idi.bidata.idatg2003mappe.filehandling.map.MapConfigFileHandler;
import edu.ntnu.idi.bidata.idatg2003mappe.map.Tile;
import edu.ntnu.idi.bidata.idatg2003mappe.util.MapDesignerListener;
import edu.ntnu.idi.bidata.idatg2003mappe.util.MapDesignerTool;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.stage.Stage;

import java.util.*;

/**
 * GUI class for the Missing Diamond game.
 *
 * @author Simen Gudbrandsen and Frikk Breadsroed
 * @version 0.0.5
 * @since 23.04.2025
 */
public class MissingDiamondGUI extends Application implements MapDesignerListener {

  // Number of players for the game
  private int numberOfPlayers = 2; // Default to 2 players if not specified

  // Game controller
  private MissingDiamondController gameController;

  // UI components
  private BorderPane mainLayout;
  private StackPane boardPane;
  private Pane overlayPane;
  private ImageView mapView;
  private TextArea scoreBoard;
  private TextArea gameLog;
  private Button rollDieButton;
  private Stage primaryStage;

  // Board data
  private Map<Integer, Circle> tileCircles = new HashMap<>();
  private Map<Player, Circle> playerMarkers = new HashMap<>();
  private Set<Integer> specialTileIds = new HashSet<>();

  // Map designer tool
  private MapDesignerTool mapDesigner;

  // Connection tracking
  private int connectionSourceId = -1;

  // Player colors
  private final Color[] playerColors = {
      Color.ORANGE, Color.INDIGO, Color.GREEN, Color.YELLOW, Color.BROWN, Color.PURPLE
  };

  /**
   * Main method to launch the application.
   * @param primaryStage
   */
  @Override
  public void start(Stage primaryStage) {
    this.primaryStage = primaryStage; // Store the stage reference

    // Create board pane first to get overlay reference
    boardPane = (StackPane) createBoardPane(); //TODO: check if this is correct

    // Initialize game controller based on number of players
    if (numberOfPlayers > 0) {
      gameController = new MissingDiamondController();
    } else {
      gameController = new MissingDiamondController(); // Default constructor
    }

    mainLayout = new BorderPane();
    mainLayout.setPrefSize(1440, 840);
    mainLayout.setStyle("-fx-background-color: white;");
    primaryStage.setTitle("Missing Diamond");

    // Initialize the map designer (after overlay is created)
    mapDesigner = new MapDesignerTool(overlayPane, mapView.getFitWidth(), mapView.getFitHeight(), this);

    // Create the menu bar with designer menu
    MenuBar menuBar = createMenuBar();

    // Add NavBar functionality
    NavBar navBar = new NavBar();
    
    navBar.setStage(primaryStage); // Set the stage in NavBar
    navBar.setGameController(gameController);
    navBar.setMissingDiamondGUI(this);
    mainLayout.setTop(navBar.createMenuBar());

    // Create developer controls HBox
    HBox devControls = new HBox(10);
    devControls.setPadding(new Insets(5));
    devControls.setAlignment(Pos.CENTER_LEFT);

    // Add map designer components
    devControls.getChildren().addAll(
        new Label("Tile Type:"), mapDesigner.getTileTypeSelector(),
        new Label("Source ID:"), mapDesigner.getSourceIdField(),
        new Label("Target ID:"), mapDesigner.getTargetIdField(),
        new Button("Create Connection") {{ setOnAction(e -> mapDesigner.createConnection()); }}
    );

    // Hide developer controls initially
    mapDesigner.getTileTypeSelector().setVisible(false);
    mapDesigner.getSourceIdField().setVisible(false);
    mapDesigner.getTargetIdField().setVisible(false);

    // Set up the top container with all elements
    VBox topContainer = new VBox(menuBar, mapDesigner.getStatusLabel(), devControls, navBar.createMenuBar());
    mainLayout.setTop(topContainer);

    // Load map configuration
    try {
      MapConfigFileHandler mapFileHandler = new MapConfigFileHandler();
      MapConfig mapConfig;

      if (mapFileHandler.defaultMapExists()) {
        mapConfig = mapFileHandler.loadFromDefaultLocation();
      } else {
        // Create a default map if none exists and save it
        mapConfig = createDefaultMapConfig();
        mapFileHandler.saveToDefaultLocation(mapConfig);
      }

      // Update this part to use the loaded configuration
      Platform.runLater(() -> {
        createGameLocationsFromConfig(overlayPane, mapView, mapConfig);
        synchronizeTilesWithDesigner();
        updateBoardUI();
      });
    } catch (FileHandlingException e) {
      gameLog.appendText("Error loading map configuration: " + e.getMessage() + "\n");
      System.err.println("Error loading map configuration: " + e.getMessage());

      // Fall back to hard-coded map data
      Platform.runLater(() -> {
        createGameLocations(overlayPane, mapView); // Your existing method
        synchronizeTilesWithDesigner();
        updateBoardUI();
      });
    }

    // Create left panel
    VBox leftSidebar = createLeftPanel();
    leftSidebar.setPrefWidth(250);
    leftSidebar.setMinWidth(250);
    leftSidebar.setMaxWidth(250);
    leftSidebar.setPadding(new Insets(10));
    leftSidebar.setBorder(new Border(new BorderStroke(
        Color.LIGHTGRAY, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT
    )));

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

    grid.add(leftSidebar, 0, 0);

    StackPane mapContainer = new StackPane();
    mapContainer.setAlignment(Pos.CENTER);
    mapContainer.getChildren().add(boardPane);

    grid.add(mapContainer, 1, 0);

    RowConstraints row = new RowConstraints();
    row.setVgrow(Priority.ALWAYS);
    grid.getRowConstraints().add(row);

    mainLayout.setCenter(grid);

    Scene scene = new Scene(mainLayout);
    primaryStage.setScene(scene);
    primaryStage.show();

    // Wait for layout to complete after window is shown
    Platform.runLater(() -> {
      updateBoardUI();
    });
  }

  private MapConfig createDefaultMapConfig() {
    MapConfig mapConfig = new MapConfig();
    mapConfig.setName("Default Missing Diamond Map");

    for (int i = 1; i <= 5; i++) {
      mapConfig.addLocation(
          new MapConfig.Location(i, "Location" + i, 0.1 * i, 0.1 * i, false)
      );

      // Add basic connections
      if (i > 1) {
        mapConfig.addConnection(new MapConfig.Connection(i-1, i));
      }
    }

    return mapConfig;
  }

  private MenuBar createMenuBar() {
    // File menu
    Menu fileMenu = new Menu("File");
    MenuItem exitItem = new MenuItem("Exit");
    exitItem.setOnAction(e -> System.exit(0));
    fileMenu.getItems().add(exitItem);

    // Get designer menu from the map designer
    Menu devMenu = mapDesigner.createDesignerMenu();

    MenuBar menuBar = new MenuBar();
    menuBar.getMenus().addAll(fileMenu, devMenu);
    menuBar.setStyle("-fx-background-color: #57B9FF;");

    return menuBar;
  }

  /**
   * Creates the left panel with scoreboard, roll button, and game log.
   *
   * @return VBox containing the left panel components
   */

  private VBox createLeftPanel() {
    VBox panel = new VBox(10);

    // Create scoreboard
    scoreBoard = new TextArea();
    scoreBoard.setPrefHeight(200);
    scoreBoard.setEditable(false);

    // Create roll button
    rollDieButton = new Button("Roll Die");
    rollDieButton.setMaxWidth(Double.MAX_VALUE);
    rollDieButton.setPrefHeight(40);
    rollDieButton.setOnAction(e -> {
      String result = gameController.playTurn();
      gameLog.appendText(result + "\n");
      highlightPossibleMoves();
      updateBoardUI();
    });

    // Create game log
    gameLog = new TextArea();
    gameLog.setPrefHeight(400);
    gameLog.setEditable(false);

    // Add components to panel
    panel.getChildren().addAll(scoreBoard, rollDieButton, gameLog);

    // Update scoreboard initially
    updateScoreBoard();

    return panel;
  }

  /**
   * Creates the board pane with a simple circular path and cross paths.
   *
   * @return Pane containing the board
   */
  private Pane createBoardPane() {
    Pane pane = new Pane();
    pane.setStyle("-fx-background-color: lightblue;");
  
    StackPane root = new StackPane();
    root.setPrefSize(900, 700);
    root.setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);

    System.out.println("Loading map image...");
    Image mapImage = new Image(getClass().getResourceAsStream("/images/afrikan_tahti_map.jpg"));
    if (mapImage.isError()) {
      System.out.println("ERROR: Failed to load map image: " + mapImage.getException());
    } else {
      System.out.println("Image loaded successfully: " + mapImage.getWidth() + "x" + mapImage.getHeight());
    }

    mapView = new ImageView(mapImage);
    mapView.setFitWidth(900);
    mapView.setFitHeight(700);
    mapView.setPreserveRatio(true);

    root.getChildren().add(mapView);

    // Create a transparent pane for game elements
    overlayPane = new Pane();
    overlayPane.setPickOnBounds(true);
    overlayPane.setPrefSize(900, 700);
    overlayPane.setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
    root.getChildren().add(overlayPane);

    overlayPane.setOnMouseClicked(e -> {
      // Only process clicks if in designer mode
      if (mapDesigner.isCoordinateMode()) {
        mapDesigner.handleCoordinateClick(e.getX(), e.getY(), mapView);
        e.consume();
      } else if (mapDesigner.isConnectionMode()) {
        // Find which tile was clicked
        for (Map.Entry<Integer, Circle> entry : tileCircles.entrySet()) {
          Circle circle = entry.getValue();
          double distance = Math.sqrt(
              Math.pow(circle.getCenterX() - e.getX(), 2) +
                  Math.pow(circle.getCenterY() - e.getY(), 2)
          );

          if (distance <= circle.getRadius()) {
            int tileId = entry.getKey();
            System.out.println("Connection mode - tile clicked: " + tileId);
            handleConnectionClick(tileId);
            e.consume();
            return;
          }
        }

        // If we get here, no tile was clicked
        gameLog.appendText("No tile found at click location. Try again.\n");
      } else {
        handleGameClick(e.getX(), e.getY());
      }
    });

    // Add a listener that will create game locations AFTER the image is rendered
    mapView.imageProperty().addListener((obs, oldImg, newImg) -> {
      if (newImg != null) {
        // Add a small delay to ensure layout is complete
        new Thread(() -> {
          try {
            Thread.sleep(100);
            Platform.runLater(() -> {
              createGameLocations(overlayPane, mapView);
            });
          } catch (InterruptedException e) {
            e.printStackTrace();
          }
        }).start();
      }
    });

    // Update overlay size when map bounds change
    mapView.boundsInParentProperty().addListener((obs, old, bounds) -> {
      overlayPane.setPrefSize(bounds.getWidth(), bounds.getHeight());
      overlayPane.setMaxSize(bounds.getWidth(), bounds.getHeight());

      // Update map designer dimensions
      if (mapDesigner != null) {
        mapDesigner.updateMapDimensions(bounds.getWidth(), bounds.getHeight());
      }

      // Update game location positions
      updateGameLocations();
    });

    return root;
  }

  private void handleGameClick(double x, double y) {
    // Debug log
    System.out.println("Game click at: " + x + ", " + y);

    // Get tile id from clicked point (if any)
    for (Map.Entry<Integer, Circle> entry : tileCircles.entrySet()) {
      Circle circle = entry.getValue();
      double distance = Math.sqrt(
          Math.pow(circle.getCenterX() - x, 2) +
              Math.pow(circle.getCenterY() - y, 2)
      );

      if (distance <= circle.getRadius()) {
        // Tile was clicked
        System.out.println("Tile clicked: " + entry.getKey());
        handleTileClick(entry.getKey());
        return;
      }
    }
  }

  private void handleConnectionClick(int tileId) {
    System.out.println("Connection mode - tile clicked: " + tileId);

    if (connectionSourceId == -1) {
      // First click - remember source ID
      connectionSourceId = tileId;
      gameLog.appendText("Selected source ID: " + tileId + ". Now click on target tile.\n");
    } else {
      // Second click - create connection with saved source and new target
      int sourceId = connectionSourceId;
      int targetId = tileId;

      gameLog.appendText("Creating connection: " + sourceId + " → " + targetId + "\n");

      // Use the direct method to create connection
      boolean success = mapDesigner.createDirectConnection(sourceId, targetId);

      if (success) {
        gameLog.appendText("Successfully connected tiles " + sourceId + " to " + targetId + ".\n");
      } else {
        gameLog.appendText("Failed to connect tiles. Make sure both tiles exist.\n");
      }

      // Reset for next connection
      connectionSourceId = -1;
    }
  }

  private void createGameLocations(Pane pane, ImageView mapView) {
    System.out.println("WARNING: Using fallback method to create game locations");
    gameLog.appendText("Warning: Using hardcoded map fallback. JSON loading failed.\n");
  }

  /**
   * Method to create game locations from the map configuration.
   */
  private void createGameLocationsFromConfig(Pane pane, ImageView mapView, MapConfig mapConfig) {
    System.out.println("Creating game locations from loaded configuration");

    // Get the actual rendered dimensions
    double mapWidth = mapView.getBoundsInParent().getWidth();
    double mapHeight = mapView.getBoundsInParent().getHeight();

    if (mapWidth <= 0 || mapHeight <= 0) {
      System.out.println("ERROR: Invalid map dimensions: " + mapWidth + "x" + mapHeight);
      return;
    }

    // Clear existing tiles
    pane.getChildren().clear();
    tileCircles.clear();

    // Create and position the locations
    for (MapConfig.Location location : mapConfig.getLocations()) {
      int tileId = location.getId();
      String name = location.getName();

      // Calculate actual coordinates based on percentages
      double xPercent = location.getXPercent();
      double yPercent = location.getYPercent();
      double x = mapWidth * xPercent;
      double y = mapHeight * yPercent;

      // Create the tile circle
      boolean isSpecial = location.isSpecial();
      Color tileColor = isSpecial ? Color.RED : Color.BLACK;
      Circle tile = createTileCircle(x, y, tileId, tileColor);

      pane.getChildren().add(tile);
      tileCircles.put(tileId, tile);
    }

    // Create connections after adding all tiles
    createPathsFromConfig(pane, mapConfig);
  }

  private void createPathsFromConfig(Pane pane, MapConfig mapConfig) {
    // Create the paths between locations
    for (MapConfig.Connection connection : mapConfig.getConnections()) {
      int fromId = connection.getFromId();
      int toId = connection.getToId();

      // Find the circles for these IDs
      Circle fromCircle = tileCircles.get(fromId);
      Circle toCircle = tileCircles.get(toId);

      if (fromCircle != null && toCircle != null) {
        Line line = new Line(
            fromCircle.getCenterX(), fromCircle.getCenterY(),
            toCircle.getCenterX(), toCircle.getCenterY()
        );
        line.setStroke(Color.BLACK);
        line.setStrokeWidth(1.5);
        line.setUserData("connection"); // For identification

        // Put lines under the circles
        pane.getChildren().add(0, line);
      }
    }
  }

  private void synchronizeTilesWithDesigner() {
    System.out.println("Synchronizing " + tileCircles.size() + " tiles with map designer...");

    for (Map.Entry<Integer, Circle> entry : tileCircles.entrySet()) {
      int tileId = entry.getKey();
      Circle circle = entry.getValue();

      // Create a CoordinatePoint that matches the existing circle
      double xPercent = circle.getCenterX() / mapView.getFitWidth();
      double yPercent = circle.getCenterY() / mapView.getFitHeight();

      // Determine if special based on circle color
      boolean isSpecial = circle.getFill().equals(Color.RED);
      String pointName = isSpecial ? "SpecialLoc" + tileId : "Location" + tileId;

      // Register this point with the map designer
      registerExistingPointWithDesigner(
          tileId,
          circle.getCenterX(),
          circle.getCenterY(),
          xPercent,
          yPercent,
          pointName,
          isSpecial
      );
    }

    gameLog.appendText("Synchronized " + tileCircles.size() + " map locations with designer.\n");
  }

  private void registerExistingPointWithDesigner(
      int id, double x, double y, double xPercent, double yPercent,
      String name, boolean isSpecial) {

    // Create an entry in mapDesigner.pointsById that matches our existing circle
    try {
      // Call the method in MapDesignerTool
      mapDesigner.registerExistingPoint(id, x, y, xPercent, yPercent, name, isSpecial);
    } catch (Exception e) {
      System.err.println("Error registering point: " + e.getMessage());
    }
  }

  private void updateGameLocations() {
    // Only update if tiles already exist
    if (tileCircles.isEmpty()) {
      System.out.println("updateGameLocations: No tiles to update");
      return;
    }

    double mapWidth = mapView.getBoundsInParent().getWidth();
    double mapHeight = mapView.getBoundsInParent().getHeight();

    System.out.println("Updating location positions. Map size: " + mapWidth + "x" + mapHeight);

    // Instead of using LOCATION_DATA, get positions from actual circles
    for (Map.Entry<Integer, Circle> entry : tileCircles.entrySet()) {
      int tileId = entry.getKey();
      Circle circle = entry.getValue();

      if (circle != null) {
        double xPct = circle.getCenterX() / mapView.getFitWidth();
        double yPct = circle.getCenterY() / mapView.getFitHeight();

        double x = mapWidth * xPct;
        double y = mapHeight * yPct;

        // Update circle position
        circle.setCenterX(x);
        circle.setCenterY(y);

        // Update any labels associated with this tile
        for (Node node : overlayPane.getChildren()) {
          if (node instanceof Label && node.getUserData() != null
              && node.getUserData().equals(tileId)) {
            Label label = (Label) node;
            label.setLayoutX(x + 5);
            label.setLayoutY(y - 15);
          }
        }
      }
    }

    // Update connections
    updateConnections();
  }

  // Add a method to update connections
  private void updateConnections() {
    // Remove existing connections
    overlayPane.getChildren().removeIf(node ->
        node instanceof Line && "connection".equals(node.getUserData()));

    try {
      MapConfigFileHandler fileHandler = new MapConfigFileHandler();
      if (fileHandler.defaultMapExists()) {
        MapConfig mapConfig = fileHandler.loadFromDefaultLocation();
        createPathsFromConfig(overlayPane, mapConfig);
      }
    } catch (FileHandlingException e) {
      System.err.println("Error loading connections: " + e.getMessage());
    }
  }

  private Circle createTileCircle(double x, double y, int tileId, Color color) {
    // Create circle
    Circle tile = new Circle();
    tile.setCenterX(x);
    tile.setCenterY(y);

    // Special locations (red) get a larger radius
    boolean isSpecial = color == Color.RED;
    tile.setRadius(isSpecial ? 12 : 5);  // 15px for special, 10px for regular

    tile.setFill(color);
    tile.setStroke(Color.WHITE);
    tile.setStrokeWidth(1.5);
    tile.setUserData(tileId);

    // Add click event
    tile.setOnMouseClicked(e -> {
      System.out.println("Circle clicked directly: " + tileId);
      if (mapDesigner.isConnectionMode()) {
        handleConnectionClick(tileId);
      } else {
        handleTileClick(tileId);
      }
      e.consume(); // Prevent event bubbling
    });

    if (isSpecial) {
      specialTileIds.add(tileId);
    }

    return tile;
  }

  /**
   * Updates the scoreboard with player names and their current tile.
   */
  private void updateScoreBoard() {
    scoreBoard.clear();
    scoreBoard.appendText("Scoreboard:\n");

    List<Player> players = gameController.getPlayers();
    for (Player player : players) {
      scoreBoard.appendText(player.getName() + ": Tile " +
          player.getCurrentTile().getTileId() + "\n");
    }
  }

  /**
   * Highlights possible moves for the current player.
   */
  private void highlightPossibleMoves() {
    // Reset all tiles to original colors
    for (Map.Entry<Integer, Circle> entry : tileCircles.entrySet()) {
      int tileId = entry.getKey();
      Circle tile = entry.getValue();

      // Check if it's a special tile using our set
      if (specialTileIds.contains(tileId)) {
        tile.setFill(Color.RED);
      } else {
        tile.setFill(Color.BLACK);
      }
    }

    // Only highlight possible moves if die has been rolled
    if (gameController.hasRolled()) {
      // Highlight possible moves in yellow
      List<Tile> possibleMoves = gameController.getPossibleMoves();
      for (Tile tile : possibleMoves) {
        Circle tileCircle = tileCircles.get(tile.getTileId());
        if (tileCircle != null) {
          tileCircle.setFill(Color.YELLOW);
        }
      }
    }
  }


  /**
   * Updates the board UI with player positions and highlights possible moves.
   */

  //Updates the board UI with current game state.
  public void updateBoardUI() { //TODO: check if this is correct
    // First clear existing player markers from the board
    for (Circle marker : playerMarkers.values()) {
      overlayPane.getChildren().remove(marker);
    }
    playerMarkers.clear();

    // Add player markers at their current positions
    List<Player> players = gameController.getPlayers();
    for (int i = 0; i < players.size(); i++) {
      Player player = players.get(i);
      int tileId = player.getCurrentTile().getTileId();
      Circle tileCircle = tileCircles.get(tileId);

      if (tileCircle != null) {
        // Create offset for player marker based on player index
        double offsetX = (i % 2 == 0) ? -15 : 15;
        double offsetY = (i < 2) ? -15 : 15;

        // Create player marker
        Circle playerMarker = new Circle(
            tileCircle.getCenterX() + offsetX,
            tileCircle.getCenterY() + offsetY,
            10,
            Paint.valueOf(player.getColor()) //Using valueOf to make string usable with Paint.
        );
        playerMarker.setStroke(Color.BLACK);
        playerMarker.setStrokeWidth(1.5);

        // Add to overlay pane
        overlayPane.getChildren().add(playerMarker);
        playerMarkers.put(player, playerMarker);
      }
    }

    // Update scoreboard
    updateScoreBoard();
  }

  /**
   * Handles tile click events.
   *
   * @param tileId The ID of the clicked tile.
   */
  private void handleTileClick(int tileId) {
    // Only allow moves if the player has rolled
    if (!gameController.hasRolled()) {
      gameLog.appendText("You must roll the die first.\n");
      return;
    }

    // Check if the tile is a valid move
    List<Tile> possibleMoves = gameController.getPossibleMoves();
    boolean validMove = possibleMoves.stream()
        .anyMatch(tile -> tile.getTileId() == tileId);

    if (validMove) {
      // Move the player to the selected tile
      String moveResult = gameController.movePlayer(tileId);
      gameLog.appendText(moveResult + "\n");

      // Reset highlighting and update the board
      highlightPossibleMoves();
      updateBoardUI();
    } else {
      gameLog.appendText("Cannot move to tile " + tileId + ".\n");
    }
  }

  /**
   * Returns the primary stage of the application.
   *
   * @return The primary stage
   */
  public Stage getStage() {
    return primaryStage;
  }

  // MapDesignerListener implementation

  @Override
  public void onLogMessage(String message) {
    gameLog.appendText(message + "\n");
    System.out.println("Log: " + message); // Debug output
  }

  @Override
  public void onCoordinateModeToggled(boolean enabled) {
    // Show/hide UI elements
    mapDesigner.getTileTypeSelector().setVisible(enabled);
    mapDesigner.getSourceIdField().setVisible(enabled);
    mapDesigner.getTargetIdField().setVisible(enabled);

    // Disable/enable game controls
    rollDieButton.setDisable(enabled);

    // Debug output
    System.out.println("Coordinate mode toggled: " + enabled);
  }

  @Override
  public void onConnectionModeToggled(boolean enabled) {
    // Debug output
    System.out.println("Connection mode toggled: " + enabled);

    // Reset connection source ID when toggling off
    if (!enabled) {
      connectionSourceId = -1;
    }
  }

  @Override
  public void onMapDataExported(String data, boolean success) {
    // Debug output
    System.out.println("Map data exported: " + success);
  }

  //Main method to launch the application.
  public static void main(String[] args) {
    launch(args);
  }
}