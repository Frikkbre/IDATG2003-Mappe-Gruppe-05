package edu.ntnu.idi.bidata.idatg2003mappe.app.missingdiamond.ui;

import edu.ntnu.idi.bidata.idatg2003mappe.app.missingdiamond.controller.MissingDiamondController;
import edu.ntnu.idi.bidata.idatg2003mappe.app.missingdiamond.service.MapConfigService;
import edu.ntnu.idi.bidata.idatg2003mappe.entity.player.Player;
import edu.ntnu.idi.bidata.idatg2003mappe.filehandling.map.MapConfig;
import edu.ntnu.idi.bidata.idatg2003mappe.map.Tile;
import javafx.application.Platform;
import javafx.scene.Node;
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

/**
 * Component responsible for displaying the game board and handling interactions with it.
 */
public class BoardView extends StackPane {
  // Game controller
  private MissingDiamondController gameController;

  // UI components
  private Pane overlayPane;
  private ImageView mapView;
  private TextArea gameLog;

  // Board data
  private final Map<Integer, Circle> tileCircles = new HashMap<>();
  private final Map<Player, Circle> playerMarkers = new HashMap<>();
  private final Set<Integer> specialTileIds = new HashSet<>();

  // Connection tracking
  private int connectionSourceId = -1;

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

  private void createOverlayPane() {
    // Create a transparent pane for game elements
    overlayPane = new Pane();
    overlayPane.setPickOnBounds(true);
    overlayPane.setPrefSize(900, 700);
    overlayPane.setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
    getChildren().add(overlayPane);
  }

  private void setupEventHandling() {
    overlayPane.setOnMouseClicked(e -> {
      if (gameController == null) return;

      // Handle game clicks - in a full implementation, this would check
      // for map designer modes and delegate accordingly
      handleGameClick(e.getX(), e.getY());
    });
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

  private void handleTileClick(int tileId) {
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
    } else {
      logMessage("Cannot move to tile " + tileId + ".");
    }
  }

  public void createLocationsFromConfig(MapConfig mapConfig) {
    System.out.println("Creating game locations from loaded configuration");

    // Get the actual rendered dimensions
    double mapWidth = mapView.getBoundsInParent().getWidth();
    double mapHeight = mapView.getBoundsInParent().getHeight();

    if (mapWidth <= 0 || mapHeight <= 0) {
      System.out.println("ERROR: Invalid map dimensions: " + mapWidth + "x" + mapHeight);
      return;
    }

    // Clear existing tiles
    overlayPane.getChildren().clear();
    tileCircles.clear();
    specialTileIds.clear();

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

      overlayPane.getChildren().add(tile);
      tileCircles.put(tileId, tile);

      if (isSpecial) {
        specialTileIds.add(tileId);
      }
    }

    // Create connections after adding all tiles
    createConnectionsFromConfig(mapConfig);
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
    System.out.println("WARNING: Using fallback method to create default game locations");
    logMessage("Warning: Using hardcoded map fallback. JSON loading failed.");

    // A simple implementation would create some basic locations here
    // For simplicity, we'll create a few example tiles

    double mapWidth = mapView.getBoundsInParent().getWidth();
    double mapHeight = mapView.getBoundsInParent().getHeight();

    // Clear existing tiles
    overlayPane.getChildren().clear();
    tileCircles.clear();
    specialTileIds.clear();

    // Create some example tiles
    for (int i = 1; i <= 5; i++) {
      double x = mapWidth * 0.1 * i;
      double y = mapHeight * 0.5;

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
  }

  public void synchronizeTilesWithDesigner(MapDesignerManager manager) {
    System.out.println("Synchronizing " + tileCircles.size() + " tiles with map designer...");

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

  private void updateLocationPositions() {
    // Only update if tiles already exist
    if (tileCircles.isEmpty()) {
      System.out.println("updateLocationPositions: No tiles to update");
      return;
    }

    double mapWidth = mapView.getBoundsInParent().getWidth();
    double mapHeight = mapView.getBoundsInParent().getHeight();

    System.out.println("Updating location positions. Map size: " + mapWidth + "x" + mapHeight);

    // Update positions based on percentages
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
          if (node instanceof Label label && node.getUserData() != null
              && node.getUserData().equals(tileId)) {
            label.setLayoutX(x + 5);
            label.setLayoutY(y - 15);
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
      System.err.println("Error updating connections: " + e.getMessage());
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
      System.out.println("Circle clicked directly: " + tileId);
      handleTileClick(tileId);
      e.consume(); // Prevent event bubbling
    });

    return tile;
  }

  public void highlightPossibleMoves() {
    if (gameController == null) return;

    // Reset all tiles to original colors
    for (Map.Entry<Integer, Circle> entry : tileCircles.entrySet()) {
      int tileId = entry.getKey();
      Circle tile = entry.getValue();

      // Check if it's a special tile
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

  // Helper method to log messages
  private void logMessage(String message) {
    if (gameLog != null) {
      gameLog.appendText(message + "\n");
    }
  }

  // Getters and setters
  public void setGameController(MissingDiamondController controller) {
    this.gameController = controller;
  }

  public void setGameLog(TextArea gameLog) {
    this.gameLog = gameLog;
  }

  public Pane getOverlayPane() {
    return overlayPane;
  }

  public ImageView getMapView() {
    return mapView;
  }

  public void setConnectionSourceId(int id) {
    this.connectionSourceId = id;
  }

  public int getConnectionSourceId() {
    return connectionSourceId;
  }

  public Map<Integer, Circle> getTileCircles() {
    return Collections.unmodifiableMap(tileCircles);
  }
}