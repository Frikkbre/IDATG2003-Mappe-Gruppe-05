package edu.ntnu.idi.bidata.idatg2003mappe.util;

import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;

/**
 * A utility class for designing game maps.
 * Provides tools for placing coordinates, creating connections, and exporting map data.
 * Acts as a facade that delegates to specialized components.
 */
public class MapDesignerTool {
  private final MapUIManager uiManager;
  private final ConnectionManager connectionManager;
  private final PointManager pointManager;
  private final MapFileHandler fileHandler;
  private double mapWidth;
  private double mapHeight;

  /**
   * Creates a new MapDesignerTool.
   */
  public MapDesignerTool(Pane overlayPane, double mapWidth, double mapHeight, MapDesignerListener listener) {
    this.mapWidth = mapWidth;
    this.mapHeight = mapHeight;
    this.pointManager = new PointManager();
    this.uiManager = new MapUIManager(overlayPane, listener);
    this.connectionManager = new ConnectionManager(overlayPane, pointManager);
    this.fileHandler = new MapFileHandler(listener, pointManager);
  }

  /**
   * Creates a menu with map designer tools.
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
    });

    // Connection mode
    MenuItem toggleConnectionModeItem = new MenuItem("Toggle Connection Mode");
    toggleConnectionModeItem.setOnAction(e -> uiManager.toggleConnectionMode());

    // File operations
    MenuItem exportMapItem = new MenuItem("Export Map Data");
    exportMapItem.setOnAction(e -> fileHandler.exportMapData());

    MenuItem saveAsDefaultItem = new MenuItem("Save as Default Map");
    saveAsDefaultItem.setOnAction(e -> fileHandler.saveAsDefaultMap());

    // Debug
    MenuItem dumpPointsMapItem = new MenuItem("Debug: Dump Points Map");
    dumpPointsMapItem.setOnAction(e -> pointManager.dumpPointsMap());

    // Add all items to menu
    devMenu.getItems().addAll(
        coordModeItem, new SeparatorMenuItem(),
        copyItem, clearItem, new SeparatorMenuItem(),
        toggleConnectionModeItem, exportMapItem, new SeparatorMenuItem(),
        saveAsDefaultItem, new SeparatorMenuItem(),
        dumpPointsMapItem
    );

    return devMenu;
  }

  /**
   * Updates the map dimensions and repositions all coordinate points.
   */
  public void updateMapDimensions(double width, double height) {
    this.mapWidth = width;
    this.mapHeight = height;
    pointManager.updateAllPointPositions(width, height);
    connectionManager.redrawConnections();
  }

  /**
   * Handles a click on the map in coordinate mode.
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
   * Creates a connection between two tiles.
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
   * Creates a connection directly using IDs.
   */
  public boolean createDirectConnection(int sourceId, int targetId) {
    return connectionManager.createConnection(sourceId, targetId);
  }

  /**
   * Registers an existing point with the map designer.
   */
  public void registerExistingPoint(int id, double x, double y, double xPercent, double yPercent,
                                    String name, boolean isSpecial) {
    pointManager.registerExistingPoint(id, x, y, xPercent, yPercent, name, isSpecial, uiManager.getOverlayPane());
  }

  // Delegate methods to expose UI components
  public Label getStatusLabel() { return uiManager.getStatusLabel(); }
  public ChoiceBox<String> getTileTypeSelector() { return uiManager.getTileTypeSelector(); }
  public TextField getSourceIdField() { return uiManager.getSourceIdField(); }
  public TextField getTargetIdField() { return uiManager.getTargetIdField(); }
  public boolean isCoordinateMode() { return uiManager.isCoordinateMode(); }
  public boolean isConnectionMode() { return uiManager.isConnectionMode(); }
}