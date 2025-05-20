package edu.ntnu.idi.bidata.idatg2003mappe.util;

import edu.ntnu.idi.bidata.idatg2003mappe.filehandling.map.MapConfig;
import edu.ntnu.idi.bidata.idatg2003mappe.filehandling.map.MapConfigFileHandler;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.TextField;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.image.ImageView;
import javafx.scene.Node;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.util.*;

/**
 * A utility class for designing game maps.
 * Provides tools for placing coordinates, creating connections, and exporting map data.
 *
 * @author Simen Gudbrandsen and Frikk Breadsroed
 * @version 0.0.5
 * @since 25.04.2025
 */
public class MapDesignerTool {

  // UI Components
  private Label statusLabel;
  private ChoiceBox<String> tileTypeSelector;
  private TextField sourceIdField;
  private TextField targetIdField;

  // Design mode flags
  private boolean coordinateMode = false;
  private boolean connectionMode = false;

  // Data storage
  private List<CoordinatePoint> capturedPoints = new ArrayList<>();
  private Map<Integer, CoordinatePoint> pointsById = new HashMap<>();
  private int nextPointId = 1;
  private int selectedSourceId = -1;

  // References
  private Pane overlayPane;
  private double mapWidth;
  private double mapHeight;
  private MapDesignerListener listener;

  /**
   * Creates a new MapDesignerTool.
   *
   * @param overlayPane The pane where map elements will be drawn
   * @param mapWidth The width of the map
   * @param mapHeight The height of the map
   * @param listener A listener for map designer events
   */
  public MapDesignerTool(Pane overlayPane, double mapWidth, double mapHeight, MapDesignerListener listener) {
    this.overlayPane = overlayPane;
    this.mapWidth = mapWidth;
    this.mapHeight = mapHeight;
    this.listener = listener;

    // Initialize UI components
    statusLabel = new Label("COORDINATE MODE: Click on map to place points");
    statusLabel.setStyle("""
            -fx-background-color: red;
            -fx-text-fill: white;
            -fx-padding: 5px;
            -fx-font-weight: bold;
          """);
    statusLabel.setVisible(false);

    tileTypeSelector = new ChoiceBox<>();
    tileTypeSelector.getItems().addAll("Black (Movement)", "Red (Special)");
    tileTypeSelector.setValue("Black (Movement)");

    sourceIdField = new TextField();
    sourceIdField.setPrefWidth(50);

    targetIdField = new TextField();
    targetIdField.setPrefWidth(50);
  }

  /**
   * Creates a menu with map designer tools.
   *
   * @return A menu containing map designer actions
   */
  public Menu createDesignerMenu() {
    Menu devMenu = new Menu("Developer Tools");

    CheckMenuItem coordModeItem = new CheckMenuItem("Coordinate Mode");
    coordModeItem.setOnAction(e -> toggleCoordinateMode(coordModeItem.isSelected()));

    MenuItem copyItem = new MenuItem("Copy Coordinates to Clipboard");
    copyItem.setOnAction(e -> copyCoordinatesToClipboard());

    MenuItem clearItem = new MenuItem("Clear Coordinate Points");
    clearItem.setOnAction(e -> clearCoordinatePoints());

    MenuItem toggleConnectionModeItem = new MenuItem("Toggle Connection Mode");
    toggleConnectionModeItem.setOnAction(e -> toggleConnectionMode());

    MenuItem exportMapItem = new MenuItem("Export Map Data");
    exportMapItem.setOnAction(e -> exportMapData());

    MenuItem dumpPointsMapItem = new MenuItem("Debug: Dump Points Map");
    dumpPointsMapItem.setOnAction(e -> dumpPointsMap());

    devMenu.getItems().addAll(coordModeItem, new SeparatorMenuItem(),
        copyItem, clearItem, new SeparatorMenuItem(),
        toggleConnectionModeItem, exportMapItem, new SeparatorMenuItem(),
        dumpPointsMapItem);

    MenuItem saveAsDefaultItem = new MenuItem("Save as Default Map");
    saveAsDefaultItem.setOnAction(e -> saveAsDefaultMap());

    devMenu.getItems().addAll(
        new SeparatorMenuItem(),
        saveAsDefaultItem
    );

    return devMenu;
  }

  /**
   * Saves the current map configuration as the default map.
   * This is primarily for developer use during map creation.
   */
  public void saveAsDefaultMap() {
    if (capturedPoints.isEmpty()) {
      if (listener != null) {
        listener.onLogMessage("No map data to save as default.");
      }
      return;
    }

    try {
      MapConfigFileHandler fileHandler = new MapConfigFileHandler();
      MapConfig mapConfig;
      Map<Integer, MapConfig.Location> existingLocations = new HashMap<>();

      // Check if default map already exists and load it
      if (fileHandler.defaultMapExists()) {
        try {
          // Load existing configuration to preserve positions
          mapConfig = fileHandler.loadFromDefaultLocation();

          // Create a map of existing locations by ID for quick lookup
          if (mapConfig.getLocations() != null) {
            for (MapConfig.Location loc : mapConfig.getLocations()) {
              existingLocations.put(loc.getId(), loc);
            }
          }
          listener.onLogMessage("Updating existing default map configuration");
        } catch (Exception e) {
          // If loading fails, create a new configuration
          mapConfig = new MapConfig();
          mapConfig.setName("Default Missing Diamond Map");
          listener.onLogMessage("Error loading existing map, creating new configuration");
        }
      } else {
        // Create new configuration if none exists
        mapConfig = new MapConfig();
        mapConfig.setName("Default Missing Diamond Map");
        listener.onLogMessage("Creating new default map configuration");
      }

      // Store existing connections before clearing them
      List<MapConfig.Connection> existingConnections = new ArrayList<>(mapConfig.getConnections());

      // Clear existing locations
      mapConfig.getLocations().clear();

      // Do NOT clear existing connections
      // mapConfig.getConnections().clear();

      // Add locations, preserving positions for existing tiles
      for (CoordinatePoint point : capturedPoints) {
        MapConfig.Location location;

        // Check if this location already exists in the previous configuration
        if (existingLocations.containsKey(point.getId())) {
          // Use the existing location's position data
          MapConfig.Location existingLoc = existingLocations.get(point.getId());
          location = new MapConfig.Location(
              point.getId(),
              point.getName(),
              existingLoc.getXPercent(),  // Use existing X position
              existingLoc.getYPercent(),  // Use existing Y position
              point.isSpecial()
          );
          listener.onLogMessage("Preserving position for tile " + point.getId());
        } else {
          // Create new location with current position
          location = new MapConfig.Location(
              point.getId(),
              point.getName(),
              point.getXPercent(),
              point.getYPercent(),
              point.isSpecial()
          );
        }
        mapConfig.addLocation(location);
      }

      // Create a set to track unique connections we'll add
      Set<String> connectionKeys = new HashSet<>();

      // Add connections from current UI state
      for (CoordinatePoint point : capturedPoints) {
        for (Integer targetId : point.getConnections()) {
          // Create a unique key for this connection
          String connectionKey = point.getId() + "-" + targetId;

          // Only add if we haven't already added this connection
          if (!connectionKeys.contains(connectionKey)) {
            connectionKeys.add(connectionKey);

            // Check if connection already exists
            if (!connectionExists(mapConfig.getConnections(), point.getId(), targetId)) {
              MapConfig.Connection connection = new MapConfig.Connection(
                  point.getId(),
                  targetId
              );
              mapConfig.addConnection(connection);
            }
          }
        }
      }

      // Save to default location
      fileHandler.saveToDefaultLocation(mapConfig);

      if (listener != null) {
        String message = existingLocations.isEmpty()
            ? "Map saved as default map configuration"
            : "Map updated and saved as default map configuration, preserving existing connections";
        listener.onMapDataExported(message, true);
        listener.onLogMessage(message);
        listener.onLogMessage("Map contains " + mapConfig.getLocations().size() +
            " locations and " + mapConfig.getConnections().size() + " connections");
      }
    } catch (Exception e) {
      if (listener != null) {
        listener.onMapDataExported("Error saving default map: " + e.getMessage(), false);
        listener.onLogMessage("Error saving default map: " + e.getMessage());
      }
    }
  }

  /**
   * Checks if a connection already exists in the list
   * @param connections The list of connections to check
   * @param fromId The source tile ID
   * @param toId The target tile ID
   * @return true if the connection exists, false otherwise
   */
  private boolean connectionExists(List<MapConfig.Connection> connections, int fromId, int toId) {
    for (MapConfig.Connection conn : connections) {
      if (conn.getFromId() == fromId && conn.getToId() == toId) {
        return true;
      }
    }
    return false;
  }

  /**
   * Gets the status label for the map designer.
   *
   * @return The status label
   */
  public Label getStatusLabel() {
    return statusLabel;
  }

  /**
   * Gets the tile type selector.
   *
   * @return The tile type selector
   */
  public ChoiceBox<String> getTileTypeSelector() {
    return tileTypeSelector;
  }

  /**
   * Gets the source ID field.
   *
   * @return The source ID field
   */
  public TextField getSourceIdField() {
    return sourceIdField;
  }

  /**
   * Gets the target ID field.
   *
   * @return The target ID field
   */
  public TextField getTargetIdField() {
    return targetIdField;
  }

  /**
   * Checks if coordinate mode is enabled.
   *
   * @return True if coordinate mode is enabled, false otherwise
   */
  public boolean isCoordinateMode() {
    return coordinateMode;
  }

  /**
   * Checks if connection mode is enabled.
   *
   * @return True if connection mode is enabled, false otherwise
   */
  public boolean isConnectionMode() {
    return connectionMode;
  }

  /**
   * Updates the map dimensions and repositions all coordinate points.
   *
   * @param width The new width
   * @param height The new height
   */
  public void updateMapDimensions(double width, double height) {
    this.mapWidth = width;
    this.mapHeight = height;

    // Update all point positions based on new dimensions
    for (CoordinatePoint point : capturedPoints) {
      point.updatePosition(width, height);
    }

    // Recreate connections to ensure they follow the updated points
    redrawConnections();
  }

  /**
   * Redraws all connections between points.
   */
  private void redrawConnections() {
    // Remove all existing connection lines
    // (we'll identify lines by user data property)
    List<Line> linesToRemove = new ArrayList<>();
    for (Node node : overlayPane.getChildren()) {
      if (node instanceof Line && "connection".equals(node.getUserData())) {
        linesToRemove.add((Line) node);
      }
    }
    overlayPane.getChildren().removeAll(linesToRemove);

    // Redraw all connections
    for (CoordinatePoint source : capturedPoints) {
      for (Integer targetId : source.getConnections()) {
        CoordinatePoint target = pointsById.get(targetId);
        if (target != null) {
          drawConnection(source, target);
        }
      }
    }
  }

  /**
   * Draws a connection line between two points.
   */
  public void drawConnection(CoordinatePoint source, CoordinatePoint target) {
    if (source.getCircle() == null || target.getCircle() == null) {
      System.err.println("Cannot draw connection: circles not initialized for points " +
          source.getId() + " or " + target.getId());
      return;
    }

    Line line = new Line(
        source.getCircle().getCenterX(), source.getCircle().getCenterY(),
        target.getCircle().getCenterX(), target.getCircle().getCenterY()
    );
    line.setStroke(Color.BLACK);
    line.setStrokeWidth(1.5);
    line.setUserData("connection"); // For identification

    // Add the line to the overlay (below circles)
    overlayPane.getChildren().add(0, line);

    System.out.println("Drew connection from " + source.getId() + " to " + target.getId());
  }

  /**
   * Toggles coordinate mode.
   *
   * @param enabled Whether coordinate mode should be enabled
   */
  public void toggleCoordinateMode(boolean enabled) {
    this.coordinateMode = enabled;
    statusLabel.setVisible(enabled);

    if (listener != null) {
      listener.onCoordinateModeToggled(enabled);

      if (enabled) {
        listener.onLogMessage("Coordinate mode enabled. Select tile type and click on the map.");
      } else {
        listener.onLogMessage("Coordinate mode disabled. Game controls restored.");
      }
    }
  }

  /**
   * Toggles connection mode.
   */
  public void toggleConnectionMode() {
    connectionMode = !connectionMode;

    if (listener != null) {
      listener.onConnectionModeToggled(connectionMode);

      if (connectionMode) {
        listener.onLogMessage("Connection Mode enabled. Click on source tile, then target tile.");
      } else {
        listener.onLogMessage("Connection Mode disabled.");
        selectedSourceId = -1;
      }
    }
  }

  /**
   * Clears all coordinate points.
   */
  public void clearCoordinatePoints() {
    // Remove all coordinate circles and labels
    for (CoordinatePoint point : capturedPoints) {
      if (point.getCircle() != null) {
        overlayPane.getChildren().remove(point.getCircle());
      }
      if (point.getLabel() != null) {
        overlayPane.getChildren().remove(point.getLabel());
      }
    }
    capturedPoints.clear();
    pointsById.clear();
    nextPointId = 1;

    if (listener != null) {
      listener.onLogMessage("All coordinate points cleared.");
    }
  }

  /**
   * Copies coordinate data to the clipboard.
   */
  public void copyCoordinatesToClipboard() {
    if (capturedPoints.isEmpty()) {
      if (listener != null) {
        listener.onLogMessage("No coordinate points to copy.");
      }
      return;
    }

    StringBuilder sb = new StringBuilder();
    sb.append("// Location data with percentages of map width/height\n");
    sb.append("private static final Object[][] LOCATION_DATA = {\n");
    sb.append("    // {id, name, x-percentage, y-percentage}\n");

    for (CoordinatePoint point : capturedPoints) {
      sb.append(String.format("    {%d, \"%s\", %.4f, %.4f},\n",
          point.getId(), point.getName(), point.getXPercent(), point.getYPercent()));
    }

    sb.append("};\n");

    // Copy to clipboard
    Clipboard clipboard = Clipboard.getSystemClipboard();
    ClipboardContent content = new ClipboardContent();
    content.putString(sb.toString());
    clipboard.setContent(content);

    if (listener != null) {
      listener.onLogMessage("Copied " + capturedPoints.size() + " coordinate points to clipboard.");
    }
  }

  /**
   * Exports map data.
   */
  public void exportMapData() {
    if (capturedPoints.isEmpty()) {
      if (listener != null) {
        listener.onLogMessage("No map data to export.");
      }
      return;
    }

    // Create a MapConfig object
    MapConfig mapConfig = new MapConfig();
    mapConfig.setName("Missing Diamond Map");

    // Add locations
    for (CoordinatePoint point : capturedPoints) {
      MapConfig.Location location = new MapConfig.Location(
          point.getId(),
          point.getName(),
          point.getXPercent(),
          point.getYPercent(),
          point.isSpecial()
      );
      mapConfig.addLocation(location);
    }

    // Add connections
    for (CoordinatePoint point : capturedPoints) {
      for (Integer targetId : point.getConnections()) {
        MapConfig.Connection connection = new MapConfig.Connection(
            point.getId(),
            targetId
        );
        mapConfig.addConnection(connection);
      }
    }

    try {
      // Create a file chooser dialog
      FileChooser fileChooser = new FileChooser();
      fileChooser.setTitle("Save Map Configuration");
      fileChooser.getExtensionFilters().add(
          new FileChooser.ExtensionFilter("JSON Files", "*.json"));
      fileChooser.setInitialDirectory(new File("src/main/resources/maps"));
      fileChooser.setInitialFileName("missing_diamond_map.json");

      // Get file from dialog
      Stage stage = new Stage();
      File file = fileChooser.showSaveDialog(stage);

      if (file != null) {
        // Save to file
        MapConfigFileHandler fileHandler = new MapConfigFileHandler();
        fileHandler.write(mapConfig, file.getAbsolutePath());

        // Show success message
        if (listener != null) {
          listener.onMapDataExported("Map saved to " + file.getName(), true);
          listener.onLogMessage("Map configuration exported to " + file.getAbsolutePath());
        }
      }
    } catch (Exception e) {
      if (listener != null) {
        listener.onMapDataExported("Error exporting map: " + e.getMessage(), false);
        listener.onLogMessage("Error exporting map: " + e.getMessage());
      }
    }
  }

  /**
   * Handles a click on the map in coordinate mode.
   *
   * @param x The x-coordinate of the click
   * @param y The y-coordinate of the click
   * @param mapView The map image view
   */
  public void handleCoordinateClick(double x, double y, ImageView mapView) {
    if (!coordinateMode) {
      System.out.println("Coordinate mode is not active");
      return;
    }

    System.out.println("Creating coordinate at: " + x + ", " + y);

    if (listener != null) {
      listener.onLogMessage("Click detected at: (" + x + ", " + y + ")");
    }

    // Ensure click is within map bounds
    if (x < 0 || x > mapWidth || y < 0 || y > mapHeight) {
      if (listener != null) {
        listener.onLogMessage("Click outside map bounds.");
      }
      return;
    }

    // Calculate percentages for storing relative positions
    double xPercent = x / mapWidth;
    double yPercent = y / mapHeight;

    // Create new coordinate point
    CoordinatePoint point = new CoordinatePoint(nextPointId++, x, y, xPercent, yPercent);

    // Set tile type based on selector
    boolean isSpecialTile = tileTypeSelector.getValue().contains("Red");

    // Create and add circle
    Circle circle = point.createCircle(isSpecialTile);
    overlayPane.getChildren().add(circle);

    // Create name and label
    String pointName;
    if (isSpecialTile) {
      pointName = "SpecialLoc" + point.getId();
    } else {
      pointName = "Location" + point.getId();
    }

    // Create and add label
    Label label = point.createLabel(pointName);
    if (label != null) {
      overlayPane.getChildren().add(label);
    }

    // Add to collections
    capturedPoints.add(point);
    pointsById.put(point.getId(), point);

    // Log info about what was added
    if (listener != null) {
      String typeStr = isSpecialTile ? "Special" : "Movement";
      listener.onLogMessage(String.format("Added %s %s at (%.0f, %.0f)",
          typeStr, point.getName(), x, y));
    }

    // Handle connection mode if active
    if (connectionMode) {
      handleConnectionModeClick(point.getId());
    }
  }

  /**
   * Handles a click in connection mode.
   *
   * @param tileId The ID of the clicked tile
   */
  public void handleConnectionModeClick(int tileId) {
    if (selectedSourceId == -1) {
      // First click - select source
      selectedSourceId = tileId;
      sourceIdField.setText(String.valueOf(tileId));

      if (listener != null) {
        listener.onLogMessage("Selected source ID: " + tileId + ". Now click on target tile.");
      }
    } else {
      // Second click - select target and create connection
      targetIdField.setText(String.valueOf(tileId));

      if (listener != null) {
        listener.onLogMessage("Selected target ID: " + tileId);
      }

      createConnection();

      // Reset for next connection
      selectedSourceId = -1;
    }
  }

  /**
   * Creates a connection between two tiles.
   */
  public void createConnection() {
    try {
      int sourceId = Integer.parseInt(sourceIdField.getText().trim());
      int targetId = Integer.parseInt(targetIdField.getText().trim());

      createDirectConnection(sourceId, targetId);

      // Clear fields
      sourceIdField.clear();
      targetIdField.clear();
    } catch (NumberFormatException e) {
      if (listener != null) {
        listener.onLogMessage("Error: Please enter valid tile IDs.");
      }
    }
  }

  /**
   * Creates a connection directly using IDs.
   */
  public boolean createDirectConnection(int sourceId, int targetId) {
    System.out.println("Creating direct connection from " + sourceId + " to " + targetId);

    // Find the points
    CoordinatePoint source = pointsById.get(sourceId);
    CoordinatePoint target = pointsById.get(targetId);

    if (source == null || target == null) {
      System.err.println("Source or target point not found: " +
          (source == null ? "sourceId=" + sourceId + " missing" : "") +
          (target == null ? "targetId=" + targetId + " missing" : ""));

      // Debug what's in the map
      System.out.println("Available pointsById keys: " + pointsById.keySet());

      if (listener != null) {
        listener.onLogMessage("Error: Could not find points with IDs " + sourceId + " and " + targetId);
      }
      return false;
    }

    // Add connection data
    source.addConnection(targetId);

    // Draw the connection line
    drawConnection(source, target);

    if (listener != null) {
      listener.onLogMessage("Created connection: " + sourceId + " → " + targetId);
    }

    return true;
  }

  /**
   * Registers an existing point with the map designer.
   * Used to synchronize points created elsewhere with the designer.
   */
  public void registerExistingPoint(int id, double x, double y, double xPercent, double yPercent,
                                    String name, boolean isSpecial) {

    // Check if this ID is already registered to avoid duplicates
    if (pointsById.containsKey(id)) {
      System.out.println("Point ID " + id + " already registered, skipping.");
      return;
    }

    // Create and register the point
    CoordinatePoint point = new CoordinatePoint(id, x, y, xPercent, yPercent);
    point.setName(name);
    point.setSpecial(isSpecial);

    // If the circle exists in the overlay, link it to the point
    for (Node node : overlayPane.getChildren()) {
      if (node instanceof Circle) {
        Circle circle = (Circle) node;
        if (circle.getUserData() != null && circle.getUserData().equals(id)) {
          point.setCircle(circle);
          break;
        }
      }
    }

    // Register the point
    pointsById.put(id, point);
    capturedPoints.add(point);

    // Update nextPointId to avoid ID conflicts
    if (id >= nextPointId) {
      nextPointId = id + 1;
    }

    System.out.println("Registered existing point " + id + ": " + name + " at " + x + "," + y);
  }

  /**
   * Dump the contents of the pointsById map for debugging.
   */
  public void dumpPointsMap() {
    System.out.println("========= POINTS MAP DUMP =========");
    System.out.println("Total points: " + pointsById.size());
    for (Map.Entry<Integer, CoordinatePoint> entry : pointsById.entrySet()) {
      CoordinatePoint point = entry.getValue();
      System.out.println("ID " + entry.getKey() + ": " + point.getName() +
          " at (" + point.getX() + "," + point.getY() + ")");
    }
    System.out.println("==================================");

    if (listener != null) {
      listener.onLogMessage("Points map dumped to console (" + pointsById.size() + " points)");
    }
  }
}