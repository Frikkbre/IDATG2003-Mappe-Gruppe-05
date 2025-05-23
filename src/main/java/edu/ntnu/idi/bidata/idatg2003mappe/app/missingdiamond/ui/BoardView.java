package edu.ntnu.idi.bidata.idatg2003mappe.app.missingdiamond.ui;

import edu.ntnu.idi.bidata.idatg2003mappe.app.missingdiamond.controller.MissingDiamondController;
import edu.ntnu.idi.bidata.idatg2003mappe.app.missingdiamond.service.MapConfigService;
import edu.ntnu.idi.bidata.idatg2003mappe.entity.player.Player;
import edu.ntnu.idi.bidata.idatg2003mappe.entity.player.PlayerObserver;
import edu.ntnu.idi.bidata.idatg2003mappe.filehandling.map.MapConfig;
import edu.ntnu.idi.bidata.idatg2003mappe.map.Tile;
import javafx.application.Platform;
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
import java.util.stream.IntStream;

/**
 * <p>Component responsible for displaying the game board and handling interactions with it.</p>
 * <p>This class provides the visual representation of the Missing Diamond game board, including:</p>
 * <ul>
 *   <li>The map background image</li>
 *   <li>Location markers (circles) for cities and special locations</li>
 *   <li>Connection lines between locations</li>
 *   <li>Player position markers</li>
 *   <li>Visual highlighting of valid moves</li>
 * </ul>
 * <p>It handles user interactions with the board and manages the synchronization
 * between the visual components and the game state.</p>
 *
 * @author Simen Gudbrandsen and Frikk Breadsroed
 * @version 0.0.2
 * @since 23.05.2025
 */
public class BoardView extends StackPane {

  private static final Logger logger = Logger.getLogger(BoardView.class.getName());
  // Event listeners
  private final Collection<BoardUpdateListener> updateListeners = new ArrayList<>();
  // Board data
  private final Map<Integer, Circle> tileCircles = new HashMap<>();
  private final Map<Player, Circle> playerMarkers = new HashMap<>();
  private final Set<Integer> specialTileIds = new HashSet<>();
  // FIX: Store original percentages to prevent corruption during resize
  private final Map<Integer, Double> tileXPercentages = new HashMap<>();
  private final Map<Integer, Double> tileYPercentages = new HashMap<>();
  // Game controller
  private MissingDiamondController gameController;
  // UI components
  private Pane overlayPane;
  private ImageView mapView;
  private TextArea gameLog;
  private TileHighlighter tileHighlighter;
  private MapDesignerManager mapDesignerManager;
  /**
   * <p>Constructs a new BoardView instance.</p>
   * <p>Initializes the board with the default size and loads the map image.
   * Sets up the overlay pane for interactive elements and configures event handling.</p>
   */
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

  /**
   * <p>Loads the map background image.</p>
   * <p>Initializes the ImageView with the African map image and configures
   * properties such as dimensions and aspect ratio. Also sets up listeners
   * to update the overlay when the image is loaded or resized.</p>
   */
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

  /**
   * <p>Sets up event handling for the board.</p>
   * <p>Configures mouse click handlers to detect when the user interacts
   * with the board and delegates to the appropriate methods.</p>
   */
  private void setupEventHandling() {
    overlayPane.setOnMouseClicked(e -> {
      handleGameClick(e.getX(), e.getY());
    });
  }

  /**
   * <p>Creates the transparent overlay pane.</p>
   * <p>This pane sits on top of the map image and contains all interactive
   * elements such as location markers, connection lines, and player tokens.</p>
   */
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

  /**
   * <p>Handles a mouse click on the game board.</p>
   * <p>Identifies if a tile was clicked by checking if the click coordinates
   * are within any tile circle's bounds. If a tile is clicked, delegates to
   * the tile click handler.</p>
   *
   * @param x The x-coordinate of the click
   * @param y The y-coordinate of the click
   */
  private void handleGameClick(double x, double y) {
    // INFO
    logger.info("Game click at: " + x + ", " + y);

    // Get tile id from clicked point (if any)
    tileCircles.forEach((key, circle) -> {
      double distance = Math.sqrt(
          Math.pow(circle.getCenterX() - x, 2) +
              Math.pow(circle.getCenterY() - y, 2)
      );

      if (distance <= circle.getRadius()) {
        // INFO: Tile was clicked
        logger.info("Tile clicked: " + key);
        handleTileClick(key);
      }
    });
  }

  /**
   * <p>Handles a click on a specific tile.</p>
   * <p>This method determines whether to handle the click as a map design action
   * (if in connection mode) or as a gameplay action (for moving players).</p>
   *
   * @param tileId The ID of the clicked tile
   */
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
   * <p>Handles a tile click during gameplay mode.</p>
   * <p>Checks if the clicked tile is a valid move destination for the current player
   * and moves the player if valid. Shows appropriate feedback messages.</p>
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
   * <p>Shows a dialog when the game is over.</p>
   * <p>Displays a popup alert announcing the winner of the game.</p>
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
   * <p>Notifies listeners that the board has been updated.</p>
   * <p>Calls the {@code onBoardUpdated()} method on all registered BoardUpdateListener instances.</p>
   */
  private void notifyBoardUpdated() {
    updateListeners.forEach(BoardUpdateListener::onBoardUpdated);
  }

  /**
   * <p>Adds a board update listener.</p>
   * <p>Registers a new listener to be notified of board state changes.
   * Each listener is only added once, even if this method is called multiple times
   * with the same listener.</p>
   *
   * @param listener The {@link BoardUpdateListener} to add
   */
  public void addBoardUpdateListener(BoardUpdateListener listener) {
    if (listener != null && !updateListeners.contains(listener)) {
      updateListeners.add(listener);
    }
  }

  /**
   * <p>Creates game locations from a map configuration.</p>
   * <p>Builds the board representation based on the provided map configuration,
   * including location markers and their properties (special vs. regular).</p>
   * <p>This method:</p>
   * <ol>
   *   <li>Clears any existing board elements</li>
   *   <li>Creates location markers at positions defined in the configuration</li>
   *   <li>Stores special tile IDs for token interaction</li>
   *   <li>Creates connections between locations</li>
   * </ol>
   *
   * @param mapConfig The {@link MapConfig} containing location and connection information
   */
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
    mapConfig.getLocations().forEach(location -> {
      int tileId = location.getId();
      String name = location.getName();

      // Store the original percentages
      tileXPercentages.put(tileId, location.getXPercent());
      tileYPercentages.put(tileId, location.getYPercent());

      double x = mapWidth * location.getXPercent();
      double y = mapHeight * location.getYPercent();

      // Create the tile circle
      boolean isSpecial = location.isSpecial();
      Color tileColor = isSpecial ? Color.RED : Color.BLACK;
      Circle tile = createTileCircle(x, y, tileId, tileColor);

      overlayPane.getChildren().add(tile);
      tileCircles.put(tileId, tile);

      if (isSpecial) {
        specialTileIds.add(tileId);
      }
    });

    if (gameController != null) {
      this.tileHighlighter = new TileHighlighter(tileCircles, specialTileIds, gameController);
    }

    // Create connections after adding all tiles
    createConnectionsFromConfig(mapConfig);
  }

  /**
   * <p>Creates a visual connection line between two locations.</p>
   * <p>Draws a line between the specified source and target locations,
   * representing a path that players can travel along.</p>
   *
   * @param fromId The ID of the source location
   * @param toId   The ID of the target location
   */
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

  /**
   * <p>Creates connection lines from a map configuration.</p>
   * <p>Draws lines between locations based on the connections defined
   * in the provided map configuration.</p>
   *
   * @param mapConfig The {@link MapConfig} containing connection information
   */
  private void createConnectionsFromConfig(MapConfig mapConfig) {
    // Create the paths between locations
    mapConfig.getConnections().stream()
        .map(conn -> new Object[]{
            tileCircles.get(conn.getFromId()),
            tileCircles.get(conn.getToId())
        })
        .filter(arr -> arr[0] != null && arr[1] != null)
        .map(arr -> {
          Line line = new Line(
              ((Circle) arr[0]).getCenterX(), ((Circle) arr[0]).getCenterY(),
              ((Circle) arr[1]).getCenterX(), ((Circle) arr[1]).getCenterY()
          );
          line.setStroke(Color.BLACK);
          line.setStrokeWidth(1.5);
          line.setUserData("connection"); // For identification
          return line;
        })
        .forEach(line -> overlayPane.getChildren().add(0, line));

  }

  /**
   * <p>Creates default locations when no map configuration is available.</p>
   * <p>Generates a simple linear board with alternating regular and special
   * tiles as a fallback when configuration loading fails.</p>
   */
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
    IntStream.rangeClosed(1, 5)
        .forEach(i -> {
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
        });


    // Create connections
    IntStream.range(1, 5)
        .mapToObj(i -> {
          Circle fromCircle = tileCircles.get(i);
          Circle toCircle = tileCircles.get(i + 1);
          Line line = new Line(
              fromCircle.getCenterX(), fromCircle.getCenterY(),
              toCircle.getCenterX(), toCircle.getCenterY()
          );
          line.setStroke(Color.BLACK);
          line.setStrokeWidth(1.5);
          line.setUserData("connection");
          return line;
        })
        .forEach(line -> overlayPane.getChildren().add(0, line));

    if (gameController != null) {
      this.tileHighlighter = new TileHighlighter(tileCircles, specialTileIds, gameController);
    }
  }

  /**
   * <p>Synchronizes existing board tiles with the map designer.</p>
   * <p>Registers all existing tile locations with the map designer manager
   * to enable editing of the current board configuration.</p>
   *
   * @param manager The {@link MapDesignerManager} to synchronize with
   */
  public void synchronizeTilesWithDesigner(MapDesignerManager manager) {
    logger.info("Synchronizing " + tileCircles.size() + " tiles with map designer...");

    tileCircles.forEach((tileId, circle) -> {
      double xPercent = circle.getCenterX() / mapView.getFitWidth();
      double yPercent = circle.getCenterY() / mapView.getFitHeight();

      boolean isSpecial = specialTileIds.contains(tileId);
      String pointName = isSpecial ? "SpecialLoc" + tileId : "Location" + tileId;

      manager.registerExistingPoint(
          tileId,
          circle.getCenterX(),
          circle.getCenterY(),
          xPercent,
          yPercent,
          pointName,
          isSpecial
      );
    });

    logMessage("Synchronized " + tileCircles.size() + " map locations with designer.");
  }

  /**
   * <p>Updates the positions of all location markers when the board is resized.</p>
   * <p>Maintains the relative positions of all locations based on their
   * percentage coordinates, ensuring they remain correctly placed when
   * the board dimensions change.</p>
   */
  private void updateLocationPositions() {
    // Only update if tiles already exist
    if (tileCircles.isEmpty()) {
      return;
    }

    double mapWidth = mapView.getBoundsInParent().getWidth();
    double mapHeight = mapView.getBoundsInParent().getHeight();

    // Update positions using STORED percentages, not recalculated ones
    tileCircles.forEach((tileId, circle) -> {
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
          overlayPane.getChildren().stream()
              .filter(node -> node instanceof Label label && node.getUserData() != null
                  && node.getUserData().equals(tileId))
              .forEach(node -> {
                Label label = (Label) node;
                label.setLayoutX(x + 5);
                label.setLayoutY(y - 15);
              });
        }
      }
    });


    // Update connections
    updateConnections();
  }

  /**
   * <p>Updates the connections between locations.</p>
   * <p>Redraws all connection lines based on the current locations of tile markers.
   * This is called after a resize to ensure connections stay aligned with tiles.</p>
   */
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

  /**
   * <p>Creates a circle to represent a tile on the board.</p>
   * <p>Configures the properties of the circle based on the tile type (special or regular)
   * and adds a click handler to handle interactions.</p>
   *
   * @param x      The x-coordinate of the circle center
   * @param y      The y-coordinate of the circle center
   * @param tileId The ID of the tile
   * @param color  The color of the circle (red for special, black for regular)
   * @return A configured {@link Circle} representing the tile
   */
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

  /**
   * <p>Updates the UI to reflect the current game state.</p>
   * <p>Refreshes player markers based on their current positions and
   * highlights possible moves for the current player.</p>
   * <p>This method:</p>
   * <ol>
   *   <li>Removes all existing player markers</li>
   *   <li>Creates new player markers at their current positions</li>
   *   <li>Highlights tiles that are valid move destinations</li>
   * </ol>
   */
  public void updateUI() {
    if (gameController == null) return;

    // Clear existing player markers
    playerMarkers.values().forEach(overlayPane.getChildren()::remove);
    playerMarkers.clear();

    // Add player markers at current positions
    List<Player> players = gameController.getPlayers();

    IntStream.range(0, players.size()).forEach(i -> {
      Player player = players.get(i);
      Circle tileCircle = tileCircles.get(player.getCurrentTile().getTileId());

      if (tileCircle != null) {
        double offsetX = ((i % 4) < 2) ? -15 : 15;
        double offsetY = ((i % 4) == 0 || (i % 4) == 1) ? -15 : 15;

        Circle playerMarker = new Circle(
            tileCircle.getCenterX() + offsetX,
            tileCircle.getCenterY() + offsetY,
            10,
            Paint.valueOf(player.getColor())
        );
        playerMarker.setStroke(Color.BLACK);
        playerMarker.setStrokeWidth(1.5);

        overlayPane.getChildren().add(playerMarker);
        playerMarkers.put(player, playerMarker);
      }
    });

    // Highlight possible moves
    highlightPossibleMoves();
  }

  /**
   * <p>Highlights possible moves for the current player.</p>
   * <p>Visually indicates which tiles the player can move to based on
   * their current position and die roll.</p>
   */
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

  /**
   * <p>Logs a message to the game log.</p>
   * <p>Appends the specified message to the game log text area,
   * providing feedback to the player about game events.</p>
   *
   * @param message The message to log
   */
  private void logMessage(String message) {
    if (gameLog != null) {
      gameLog.appendText(message + "\n");
    }
  }

  /**
   * <p>Sets the map designer manager.</p>
   * <p>Associates a map designer manager with this board view to
   * enable map editing functionality.</p>
   *
   * @param manager The {@link MapDesignerManager} to use
   */
  public void setMapDesignerManager(MapDesignerManager manager) {
    this.mapDesignerManager = manager;
  }

  /**
   * <p>Sets the game controller for this board view.</p>
   * <p>Associates a game controller with this board view, allowing
   * it to interact with the game model and respond to player actions.</p>
   * <p>Also registers the board view as an observer for player events
   * if it implements the PlayerObserver interface.</p>
   *
   * @param controller The {@link MissingDiamondController} to use
   */
  public void setGameController(MissingDiamondController controller) {
    this.gameController = controller;

    // Register as an observer if implementing the observer interface
    if (this instanceof PlayerObserver observer) {
      controller.getPlayers().forEach(player -> player.addObserver(observer));
    }

    // Add controller as a board update listener if it implements the interface
    if (controller instanceof BoardUpdateListener) {
      addBoardUpdateListener((BoardUpdateListener) controller);
    }

    if (!tileCircles.isEmpty()) {
      this.tileHighlighter = new TileHighlighter(tileCircles, specialTileIds, controller);
    }
  }

  /**
   * <p>Gets the overlay pane.</p>
   * <p>Returns the pane that contains all interactive elements of the board.</p>
   *
   * @return The overlay {@link Pane}
   */
  public Pane getOverlayPane() {
    return overlayPane;
  }

  /**
   * <p>Gets the map image view.</p>
   * <p>Returns the ImageView displaying the background map image.</p>
   *
   * @return The map {@link ImageView}
   */
  public ImageView getMapView() {
    return mapView;
  }

  /**
   * <p>Interface for board update notifications.</p>
   * <p>Implementers of this interface will be notified when the game board's state changes,
   * allowing them to update dependent components accordingly.</p>
   */
  public interface BoardUpdateListener {
    /**
     * <p>Called when the board state has been updated.</p>
     * <p>This method is triggered after any significant change to the board state,
     * such as player movement or token interaction.</p>
     */
    void onBoardUpdated();
  }

}
