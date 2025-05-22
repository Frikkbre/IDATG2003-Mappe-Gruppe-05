package edu.ntnu.idi.bidata.idatg2003mappe.app.missingdiamond.ui;

import edu.ntnu.idi.bidata.idatg2003mappe.app.missingdiamond.controller.MissingDiamondController;
import edu.ntnu.idi.bidata.idatg2003mappe.app.missingdiamond.service.MapConfigService;
import edu.ntnu.idi.bidata.idatg2003mappe.entity.player.Player;
import edu.ntnu.idi.bidata.idatg2003mappe.entity.player.PlayerObserver;
import edu.ntnu.idi.bidata.idatg2003mappe.filehandling.map.MapConfig;
import edu.ntnu.idi.bidata.idatg2003mappe.map.Tile;
import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;

import java.util.*;
import java.util.logging.Logger;

/**
 * Component responsible for displaying the game board and handling interactions with it.
 */
public class BoardView extends StackPane {

  private static final Logger logger = Logger.getLogger(BoardView.class.getName());

  /**
   * Interface for board update notifications.
   */
  public interface BoardUpdateListener {
    void onBoardUpdated();
  }

  // Game controller
  private MissingDiamondController gameController;

  // UI components
  private Pane overlayPane;
  private ImageView mapView;
  private TextArea gameLog;

  private TileHighlighter tileHighlighter;

  private MapDesignerManager mapDesignerManager;

  // Event listeners
  private final List<BoardUpdateListener> updateListeners = new ArrayList<>();

  // Board data
  private final Map<Integer, Circle> tileCircles = new HashMap<>();
  private final Map<Player, Circle> playerMarkers = new HashMap<>();
  private final Set<Integer> specialTileIds = new HashSet<>();

  // FIX: Store original percentages to prevent corruption during resize
  private final Map<Integer, Double> tileXPercentages = new HashMap<>();
  private final Map<Integer, Double> tileYPercentages = new HashMap<>();

  public BoardView() {
    setPrefSize(900, 700);
    setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);

    // Load map image
    loadMapImage();

    // Create overlay pane
    createOverlayPane();

    // Set up event handling
    setupEventHandling();
  }

  private void loadMapImage() {
    logger.info("Loading map image...");
    Image mapImage = new Image(getClass().getResourceAsStream("/images/afrikan_tahti_map.jpg"));
    if (mapImage.isError()) {
      logger.severe("ERROR: Failed to load map image: " + mapImage.getException());
    } else {
      logger.info("Image loaded successfully: " + mapImage.getWidth() + "x" + mapImage.getHeight());
    }

    mapView = new ImageView(mapImage);
    mapView.setFitWidth(900);
    mapView.setFitHeight(700);
    mapView.setPreserveRatio(true);

    getChildren().add(mapView);

    // Add a listener that will create game locations AFTER the image is rendered
    mapView.imageProperty().addListener((obs, oldImg, newImg) -> {
      if (newImg != null) {
        // Add a small delay to ensure layout is complete
        new Thread(() -> {
          try {
            Thread.sleep(100);
            Platform.runLater(this::createDefaultLocations);
          } catch (InterruptedException e) {
            e.printStackTrace();
          }
        }).start();
      }
    });

    // Update overlay size when map bounds change
    mapView.boundsInParentProperty().addListener((obs, old, bounds) -> {
      if (overlayPane != null) {
        overlayPane.setPrefSize(bounds.getWidth(), bounds.getHeight());
        overlayPane.setMaxSize(bounds.getWidth(), bounds.getHeight());

        // Update game location positions
        updateLocationPositions();
      }
    });
  }

  private void setupEventHandling() {
    overlayPane.setOnMouseClicked(e -> {
      handleGameClick(e.getX(), e.getY());
    });
  }

  // In BoardView.java - make sure overlayPane is correctly set up
  private void createOverlayPane() {
    overlayPane = new Pane();
    overlayPane.setPickOnBounds(false);
    overlayPane.setPrefSize(900, 700);
    overlayPane.setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);

    // Make sure pane is visible
    overlayPane.setStyle("-fx-background-color: transparent;");

    getChildren().add(overlayPane);

    // Ensure overlay is on top
    overlayPane.toFront();
  }

  private void handleGameClick(double x, double y) {
    // INFO
    logger.info("Game click at: " + x + ", " + y);

    // Get tile id from clicked point (if any)
    for (Map.Entry<Integer, Circle> entry : tileCircles.entrySet()) {
      Circle circle = entry.getValue();
      double distance = Math.sqrt(
          Math.pow(circle.getCenterX() - x, 2) +
              Math.pow(circle.getCenterY() - y, 2)
      );

      if (distance <= circle.getRadius()) {
        // INFO: Tile was clicked
        logger.info("Tile clicked: " + entry.getKey());
        handleTileClick(entry.getKey());
        return;
      }
    }
  }

  private void handleTileClick(int tileId) {
    if (gameController == null) return;

    // Add debugging statements
    logger.info("DEBUG: Tile clicked: " + tileId);
    logger.info("DEBUG: Connection mode active: " +
        (mapDesignerManager != null ? mapDesignerManager.isConnectionMode() : "mapDesignerManager is null"));

    // Add this block to handle connection mode
    if (mapDesignerManager != null && mapDesignerManager.isConnectionMode()) {
      int connectionSourceId = mapDesignerManager.getConnectionSourceId();

      if (connectionSourceId == -1) {
        // First click, set the source tile
        mapDesignerManager.setConnectionSourceId(tileId);
      } else {
        // Second click, create a connection
        boolean success = mapDesignerManager.createDirectConnection(connectionSourceId, tileId);

        if (success) {
          // Draw the connection line
          createConnectionLine(connectionSourceId, tileId);
          logger.info("DEBUG: Connection created successfully");
        } else {
          logger.warning("DEBUG: Connection creation failed");
        }

        // Reset for next connection
        mapDesignerManager.setConnectionSourceId(-1);
      }
      return;
    }

    // Handle gameplay mode
    handleGameplayTileClick(tileId);
  }

  /**
   * Handles a tile click during gameplay (not in design mode).
   *
   * @param tileId The ID of the clicked tile
   */
  private void handleGameplayTileClick(int tileId) {
    if (gameController == null) return;

    // Only allow moves if the player has rolled
    if (!gameController.hasRolled()) {
      logMessage("You must roll the die first.");
      return;
    }

    // Check if the tile is a valid move
    List<Tile> possibleMoves = gameController.getPossibleMoves();
    boolean validMove = possibleMoves.stream()
        .anyMatch(tile -> tile.getTileId() == tileId);

    if (validMove) {
      // Move the player to the selected tile
      String moveResult = gameController.movePlayer(tileId);
      logMessage(moveResult);

      // Reset highlighting and update the board
      highlightPossibleMoves();
      updateUI();

      // Notify listeners that the board has been updated
      notifyBoardUpdated();

      // Check for game end
      if (gameController.isGameFinished()) {
        showGameOverDialog();
      }
    } else {
      logMessage("Cannot move to tile " + tileId + ".");
    }
  }

  /**
   * Shows a dialog when the game is over.
   */
  private void showGameOverDialog() {
    if (gameController == null) return;

    Alert alert = new Alert(Alert.AlertType.INFORMATION);
    alert.setTitle("Game Over");
    alert.setHeaderText("Game Finished!");
    alert.setContentText(gameController.getCurrentPlayer().getName() + " has won the game!");
    alert.showAndWait();
  }

  /**
   * Notifies listeners that the board has been updated.
   */
  private void notifyBoardUpdated() {
    for (BoardUpdateListener listener : updateListeners) {
      listener.onBoardUpdated();
    }
  }

  /**
   * Adds a board update listener.
   *
   * @param listener The listener to add
   */
  public void addBoardUpdateListener(BoardUpdateListener listener) {
    if (listener != null && !updateListeners.contains(listener)) {
      updateListeners.add(listener);
    }
  }

  public void createLocationsFromConfig(MapConfig mapConfig) {
    logger.info("Creating game locations from loaded configuration");

    // Get the actual rendered dimensions
    double mapWidth = mapView.getBoundsInParent().getWidth();
    double mapHeight = mapView.getBoundsInParent().getHeight();

    if (mapWidth <= 0 || mapHeight <= 0) {
      logger.warning("ERROR: Invalid map dimensions: " + mapWidth + "x" + mapHeight);
      return;
    }

    // Clear existing tiles
    overlayPane.getChildren().clear();
    tileCircles.clear();
    specialTileIds.clear();

    tileXPercentages.clear();
    tileYPercentages.clear();

    // Create and position the locations
    for (MapConfig.Location location : mapConfig.getLocations()) {
      int tileId = location.getId();
      String name = location.getName();

      // Calculate actual coordinates based on percentages
      double xPercent = location.getXPercent();
      double yPercent = location.getYPercent();

      // FIX: STORE THE ORIGINAL PERCENTAGES - This is the key fix!
      tileXPercentages.put(tileId, xPercent);
      tileYPercentages.put(tileId, yPercent);

      double x = mapWidth * xPercent;
      double y = mapHeight * yPercent;

      // Create the tile circle
      boolean isSpecial = location.isSpecial();
      Color tileColor = isSpecial ? Color.RED : Color.BLACK;
      Circle tile = createTileCircle(x, y, tileId, tileColor);

      overlayPane.getChildren().add(tile);
      tileCircles.put(tileId, tile);

      if (isSpecial) {
        specialTileIds.add(tileId);
      }
    }
    if (gameController != null) {
      this.tileHighlighter = new TileHighlighter(tileCircles, specialTileIds, gameController);
    }

    // Create connections after adding all tiles
    createConnectionsFromConfig(mapConfig);
  }

  public void createConnectionLine(int fromId, int toId) {
    Circle fromCircle = tileCircles.get(fromId);
    Circle toCircle = tileCircles.get(toId);

    if (fromCircle != null && toCircle != null) {
      Line line = new Line(
          fromCircle.getCenterX(), fromCircle.getCenterY(),
          toCircle.getCenterX(), toCircle.getCenterY()
      );
      line.setStroke(Color.BLACK);
      line.setStrokeWidth(2.5); // Thicker for visibility
      line.setUserData("connection");

      // Important: Add to overlay pane at index 0 so it appears below circles
      overlayPane.getChildren().add(0, line);

      // Log the connection creation
      logger.info("Created visual connection line from " + fromId + " to " + toId);
      logMessage("Created connection from " + fromId + " to " + toId);
    }
  }

  private void createConnectionsFromConfig(MapConfig mapConfig) {
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
        overlayPane.getChildren().add(0, line);
      }
    }
  }

  public void createDefaultLocations() {
    logger.warning("WARNING: Using fallback method to create default game locations");
    logMessage("Warning: Using hardcoded map fallback. JSON loading failed.");

    double mapWidth = mapView.getBoundsInParent().getWidth();
    double mapHeight = mapView.getBoundsInParent().getHeight();

    // Clear existing tiles
    overlayPane.getChildren().clear();
    tileCircles.clear();
    specialTileIds.clear();
    // FIX: Clear percentage maps
    tileXPercentages.clear();
    tileYPercentages.clear();

    // Create some example tiles
    for (int i = 1; i <= 5; i++) {
      double xPercent = 0.1 * i;  // Store as percentage
      double yPercent = 0.5;      // Store as percentage

      tileXPercentages.put(i, xPercent);
      tileYPercentages.put(i, yPercent);

      double x = mapWidth * xPercent;
      double y = mapHeight * yPercent;

      boolean isSpecial = (i % 2 == 0);
      Color color = isSpecial ? Color.RED : Color.BLACK;

      Circle tile = createTileCircle(x, y, i, color);
      overlayPane.getChildren().add(tile);
      tileCircles.put(i, tile);

      if (isSpecial) {
        specialTileIds.add(i);
      }
    }

    // Create connections
    for (int i = 1; i < 5; i++) {
      Circle fromCircle = tileCircles.get(i);
      Circle toCircle = tileCircles.get(i + 1);

      Line line = new Line(
          fromCircle.getCenterX(), fromCircle.getCenterY(),
          toCircle.getCenterX(), toCircle.getCenterY()
      );
      line.setStroke(Color.BLACK);
      line.setStrokeWidth(1.5);
      line.setUserData("connection");

      overlayPane.getChildren().add(0, line);
    }
    if (gameController != null) {
      this.tileHighlighter = new TileHighlighter(tileCircles, specialTileIds, gameController);
    }
  }

  public void synchronizeTilesWithDesigner(MapDesignerManager manager) {
    logger.info("Synchronizing " + tileCircles.size() + " tiles with map designer...");

    for (Map.Entry<Integer, Circle> entry : tileCircles.entrySet()) {
      int tileId = entry.getKey();
      Circle circle = entry.getValue();

      // Calculate percentages
      double xPercent = circle.getCenterX() / mapView.getFitWidth();
      double yPercent = circle.getCenterY() / mapView.getFitHeight();

      // Determine if special based on circle color
      boolean isSpecial = specialTileIds.contains(tileId);
      String pointName = isSpecial ? "SpecialLoc" + tileId : "Location" + tileId;

      // Register with designer
      manager.registerExistingPoint(
          tileId,
          circle.getCenterX(),
          circle.getCenterY(),
          xPercent,
          yPercent,
          pointName,
          isSpecial
      );
    }

    logMessage("Synchronized " + tileCircles.size() + " map locations with designer.");
  }

  // FIX: Use stored percentages instead of recalculating from positions
  private void updateLocationPositions() {
    // Only update if tiles already exist
    if (tileCircles.isEmpty()) {
      return;
    }

    double mapWidth = mapView.getBoundsInParent().getWidth();
    double mapHeight = mapView.getBoundsInParent().getHeight();

    // Update positions using STORED percentages, not recalculated ones
    for (Map.Entry<Integer, Circle> entry : tileCircles.entrySet()) {
      int tileId = entry.getKey();
      Circle circle = entry.getValue();

      if (circle != null) {
        Double xPct = tileXPercentages.get(tileId);
        Double yPct = tileYPercentages.get(tileId);

        if (xPct != null && yPct != null) {
          double x = mapWidth * xPct;
          double y = mapHeight * yPct;

          // Update circle position
          circle.setCenterX(x);
          circle.setCenterY(y);

          // Update any labels associated with this tile
          for (Node node : overlayPane.getChildren()) {
            if (node instanceof Label label && node.getUserData() != null
                && node.getUserData().equals(tileId)) {
              label.setLayoutX(x + 5);
              label.setLayoutY(y - 15);
            }
          }
        }
      }
    }

    // Update connections
    updateConnections();
  }

  private void updateConnections() {
    // Remove existing connections
    overlayPane.getChildren().removeIf(node ->
        node instanceof Line && "connection".equals(node.getUserData()));

    // Recreate connections based on current tile positions
    try {
      MapConfig mapConfig = MapConfigService.loadMapConfig();
      createConnectionsFromConfig(mapConfig);
    } catch (Exception e) {
      logger.warning("Error updating connections: " + e.getMessage());
    }
  }

  private Circle createTileCircle(double x, double y, int tileId, Color color) {
    // Create circle
    Circle tile = new Circle();
    tile.setCenterX(x);
    tile.setCenterY(y);

    // Special locations (red) get a larger radius
    boolean isSpecial = color == Color.RED;
    tile.setRadius(isSpecial ? 12 : 5);  // 12px for special, 5px for regular

    tile.setFill(color);
    tile.setStroke(Color.WHITE);
    tile.setStrokeWidth(1.5);
    tile.setUserData(tileId);

    // Add click event
    tile.setOnMouseClicked(e -> {

      logger.info("Circle clicked directly: " + tileId);

      handleTileClick(tileId);
      e.consume(); // Prevent event bubbling
    });

    return tile;
  }

  public void updateUI() {
    if (gameController == null) return;

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
            Paint.valueOf(player.getColor())
        );
        playerMarker.setStroke(Color.BLACK);
        playerMarker.setStrokeWidth(1.5);

        // Add to overlay pane
        overlayPane.getChildren().add(playerMarker);
        playerMarkers.put(player, playerMarker);
      }
    }

    // Highlight possible moves
    highlightPossibleMoves();
  }

  public void highlightPossibleMoves() {
    if (tileHighlighter != null) {
      tileHighlighter.highlightPossibleMoves();
    } else if (gameController != null) {
      if (!tileCircles.isEmpty()) {
        this.tileHighlighter = new TileHighlighter(tileCircles, specialTileIds, gameController);
        tileHighlighter.highlightPossibleMoves();
      }
    }
  }

  // Helper method to log messages
  private void logMessage(String message) {
    if (gameLog != null) {
      gameLog.appendText(message + "\n");
    }
  }

  public void setMapDesignerManager(MapDesignerManager manager) {
    this.mapDesignerManager = manager;
  }

  // Getters and setters
  public void setGameController(MissingDiamondController controller) {
    this.gameController = controller;

    // Register as an observer if implementing the observer interface
    if (this instanceof PlayerObserver) {
      for (Player player : controller.getPlayers()) {
        player.addObserver((PlayerObserver) this);
      }
    }

    // Add controller as a board update listener if it implements the interface
    if (controller instanceof BoardUpdateListener) {
      addBoardUpdateListener((BoardUpdateListener) controller);
    }

    if (!tileCircles.isEmpty()) {
      this.tileHighlighter = new TileHighlighter(tileCircles, specialTileIds, controller);
    }
  }

  public Pane getOverlayPane() {
    return overlayPane;
  }

  public ImageView getMapView() {
    return mapView;
  }

}