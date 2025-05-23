package edu.ntnu.idi.bidata.idatg2003mappe.util.map;

import edu.ntnu.idi.bidata.idatg2003mappe.util.ConnectionManager;
import edu.ntnu.idi.bidata.idatg2003mappe.util.CoordinatePoint;
import edu.ntnu.idi.bidata.idatg2003mappe.util.PointManager;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;

/**
 * <p>A utility class for designing game maps.</p>
 * <p>This class provides a facade for the map design system, coordinating specialized components that handle
 * different aspects of map creation such as placing coordinate points, creating connections between tiles,
 * and exporting map data. It manages the overall state of the map design process and provides a unified
 * interface for other components to interact with the map designer.</p>
 *
 * @author Simen Gudbrandsen and Frikk Breadsroed
 * @version 0.0.5
 * @since 29.05.2025
 */
public class MapDesignerTool {
  private final MapUIManager uiManager;
  private final ConnectionManager connectionManager;
  private final PointManager pointManager;
  private final MapFileHandler fileHandler;
  private double mapWidth;
  private double mapHeight;

  /**
   * <p>Creates a new MapDesignerTool with the specified components.</p>
   * <p>This constructor initializes the map designer tool with references to the overlay pane
   * where visual elements will be displayed, the dimensions of the map, and a listener
   * for map design events.</p>
   *
   * @param overlayPane The JavaFX pane where visual elements will be displayed
   * @param mapWidth    The initial width of the map in pixels
   * @param mapHeight   The initial height of the map in pixels
   * @param listener    The listener that will receive map design events
   */
  public MapDesignerTool(Pane overlayPane, double mapWidth, double mapHeight, MapDesignerListener listener) {
    this.mapWidth = mapWidth;
    this.mapHeight = mapHeight;
    this.pointManager = new PointManager();
    this.pointManager.setOverlayPane(overlayPane); // Set overlay pane reference
    this.uiManager = new MapUIManager(overlayPane, listener);
    this.connectionManager = new ConnectionManager(overlayPane, pointManager);
    this.fileHandler = new MapFileHandler(listener, pointManager);
  }

  /**
   * <p>Creates a menu with map designer tools.</p>
   * <p>This method creates a JavaFX Menu containing items for all the map design functions,
   * such as toggling coordinate mode, creating connections, clearing points, and exporting map data.
   * The menu items are connected to the appropriate methods in the map designer components.</p>
   *
   * @return A JavaFX Menu containing map designer tools
   */
  public Menu createDesignerMenu() {
    Menu devMenu = new Menu("Developer Tools");

    // Coordinate mode
    CheckMenuItem coordModeItem = new CheckMenuItem("Coordinate Mode");
    coordModeItem.setOnAction(e -> uiManager.toggleCoordinateMode(coordModeItem.isSelected()));

    // Clipboard operations
    MenuItem copyItem = new MenuItem("Copy Coordinates to Clipboard");
    copyItem.setOnAction(e -> fileHandler.copyCoordinatesToClipboard());

    // Point management
    MenuItem clearItem = new MenuItem("Clear Coordinate Points");
    clearItem.setOnAction(e -> {
      pointManager.clear();
      connectionManager.clearConnections();
      uiManager.logMessage("All coordinate points and connections cleared.", true);
    });

    // Connection mode
    MenuItem toggleConnectionModeItem = new MenuItem("Toggle Connection Mode");
    toggleConnectionModeItem.setOnAction(e -> uiManager.toggleConnectionMode());

    // File operations
    MenuItem exportMapItem = new MenuItem("Export Map Data");
    exportMapItem.setOnAction(e -> fileHandler.exportMapData());

    MenuItem saveAsDefaultItem = new MenuItem("Save as Default Map");
    saveAsDefaultItem.setOnAction(e -> fileHandler.saveAsDefaultMap());

    // Add all items to menu
    devMenu.getItems().addAll(
        coordModeItem, new SeparatorMenuItem(),
        copyItem, clearItem, new SeparatorMenuItem(),
        toggleConnectionModeItem, exportMapItem, new SeparatorMenuItem(),
        saveAsDefaultItem, new SeparatorMenuItem()
        //dumpPointsMapItem
    );

    return devMenu;
  }

  /**
   * <p>Updates the map dimensions and repositions all coordinate points.</p>
   * <p>This method is called when the map is resized to update the stored dimensions
   * and recalculate the positions of all coordinate points relative to the new size.
   * It also redraws all connections to maintain the correct visual representation.</p>
   *
   * @param width  The new width of the map in pixels
   * @param height The new height of the map in pixels
   */
  public void updateMapDimensions(double width, double height) {
    this.mapWidth = width;
    this.mapHeight = height;
    pointManager.updateAllPointPositions(width, height);
    connectionManager.redrawConnections();
  }

  /**
   * <p>Handles a click on the map in coordinate mode.</p>
   * <p>This method processes clicks on the map when coordinate mode is active,
   * creating new coordinate points at the clicked location and handling connection
   * mode if it's also active. It validates that the click is within map bounds.</p>
   *
   * @param x       The x-coordinate of the click
   * @param y       The y-coordinate of the click
   * @param mapView The ImageView representing the map
   */
  public void handleCoordinateClick(double x, double y, ImageView mapView) {
    if (!isCoordinateMode()) {
      return;
    }

    // Ensure click is within map bounds
    if (x < 0 || x > mapWidth || y < 0 || y > mapHeight) {
      uiManager.logMessage("Click outside map bounds.", false);
      return;
    }

    // Calculate percentages for storing relative positions
    double xPercent = x / mapWidth;
    double yPercent = y / mapHeight;

    // Process click based on current mode
    boolean isSpecialTile = uiManager.getTileTypeSelector().getValue().contains("Red");
    CoordinatePoint point = pointManager.createPoint(x, y, xPercent, yPercent, isSpecialTile);

    if (point != null) {
      // Create visuals
      uiManager.addPointVisuals(point);

      // Handle connection mode if active
      if (uiManager.isConnectionMode()) {
        uiManager.handleConnectionModeClick(point.getId());
      }
    }
  }

  /**
   * <p>Creates a connection between two tiles using the UI input fields.</p>
   * <p>This method reads the source and target IDs from the UI input fields
   * and creates a connection between them, then clears the input fields.</p>
   */
  public void createConnection() {
    try {
      int sourceId = Integer.parseInt(uiManager.getSourceIdField().getText().trim());
      int targetId = Integer.parseInt(uiManager.getTargetIdField().getText().trim());
      createDirectConnection(sourceId, targetId);
      // Clear fields
      uiManager.getSourceIdField().clear();
      uiManager.getTargetIdField().clear();
    } catch (NumberFormatException e) {
      uiManager.logMessage("Error: Please enter valid tile IDs.", false);
    }
  }

  /**
   * <p>Creates a connection directly using tile IDs.</p>
   * <p>This method creates a connection between two tiles identified by their IDs,
   * logs the result, and ensures the connection is visually drawn on the map.</p>
   *
   * @param sourceId The ID of the source tile
   * @param targetId The ID of the target tile
   * @return {@code true} if the connection was created successfully, {@code false} otherwise
   */
// In MapDesignerTool.java - ensure createDirectConnection does this
  public boolean createDirectConnection(int sourceId, int targetId) {
    boolean success = connectionManager.createConnection(sourceId, targetId);

    if (success) {
      uiManager.logMessage("Connection created successfully between " + sourceId + " and " + targetId, false);

      // This is important - force a redraw of all connections to ensure visibility
      connectionManager.redrawConnections();
    }

    return success;
  }

  /**
   * <p>Registers an existing point with the map designer.</p>
   * <p>This method is used to recreate coordinate points from saved data,
   * such as when loading a map configuration.</p>
   *
   * @param id        The unique identifier for the point
   * @param x         The absolute x-coordinate on the map
   * @param y         The absolute y-coordinate on the map
   * @param xPercent  The x-coordinate as a percentage of the map width (0.0 to 1.0)
   * @param yPercent  The y-coordinate as a percentage of the map height (0.0 to 1.0)
   * @param name      The name of the location
   * @param isSpecial Whether this is a special point (affects appearance and behavior)
   */
  public void registerExistingPoint(int id, double x, double y, double xPercent, double yPercent,
                                    String name, boolean isSpecial) {
    pointManager.registerExistingPoint(id, x, y, xPercent, yPercent, name, isSpecial, uiManager.getOverlayPane());
  }

  /**
   * <p>Gets the status label from the UI manager.</p>
   * <p>This method provides access to the status label component that displays
   * information about the current state of the map designer.</p>
   *
   * @return The JavaFX Label component for status messages
   */
  public Label getStatusLabel() {
    return uiManager.getStatusLabel();
  }

  /**
   * <p>Gets the tile type selector from the UI manager.</p>
   * <p>This method provides access to the choice box component that allows
   * selection of tile types (special or movement).</p>
   *
   * @return The JavaFX ChoiceBox component for tile type selection
   */
  public ChoiceBox<String> getTileTypeSelector() {
    return uiManager.getTileTypeSelector();
  }

  /**
   * <p>Gets the source ID input field from the UI manager.</p>
   * <p>This method provides access to the text field component for entering
   * the source tile ID when creating connections.</p>
   *
   * @return The JavaFX TextField component for the source ID
   */
  public TextField getSourceIdField() {
    return uiManager.getSourceIdField();
  }

  /**
   * <p>Gets the target ID input field from the UI manager.</p>
   * <p>This method provides access to the text field component for entering
   * the target tile ID when creating connections.</p>
   *
   * @return The JavaFX TextField component for the target ID
   */
  public TextField getTargetIdField() {
    return uiManager.getTargetIdField();
  }

  /**
   * <p>Checks if coordinate mode is active.</p>
   * <p>This method returns whether the map designer is currently in coordinate mode,
   * where clicks on the map create new coordinate points.</p>
   *
   * @return {@code true} if coordinate mode is active, {@code false} otherwise
   */
  public boolean isCoordinateMode() {
    return uiManager.isCoordinateMode();
  }

  /**
   * <p>Checks if connection mode is active.</p>
   * <p>This method returns whether the map designer is currently in connection mode,
   * where clicks on points create connections between them.</p>
   *
   * @return {@code true} if connection mode is active, {@code false} otherwise
   */
  public boolean isConnectionMode() {
    return uiManager.isConnectionMode();
  }
}
