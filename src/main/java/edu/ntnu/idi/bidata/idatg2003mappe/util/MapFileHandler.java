package edu.ntnu.idi.bidata.idatg2003mappe.util;

import edu.ntnu.idi.bidata.idatg2003mappe.filehandling.map.MapConfig;
import edu.ntnu.idi.bidata.idatg2003mappe.filehandling.map.MapConfigFileHandler;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Handles file operations for the map designer.
 */
public class MapFileHandler {
  private final MapDesignerListener listener;
  private final PointManager pointManager;

  /**
   * Creates a new MapFileHandler.
   */
  public MapFileHandler(MapDesignerListener listener, PointManager pointManager) {
    this.listener = listener;
    this.pointManager = pointManager;
  }

  /**
   * Saves the current map configuration as the default map.
   */
  public void saveAsDefaultMap() {
    if (pointManager.getAllPoints().isEmpty()) {
      logMessage("No map data to save as default.", false);
      return;
    }

    try {
      MapConfig mapConfig = loadOrCreateMapConfig();
      updateLocationsInConfig(mapConfig);
      updateConnectionsInConfig(mapConfig);
      saveConfigToDefaultLocation(mapConfig);

      String message = "Map saved with " + mapConfig.getLocations().size() +
          " locations and " + mapConfig.getConnections().size() + " connections";
      logMessage(message, true);
    } catch (Exception e) {
      logMessage("Error saving default map: " + e.getMessage(), false);
    }
  }

  /**
   * Loads or creates a map configuration.
   */
  private MapConfig loadOrCreateMapConfig() throws Exception {
    MapConfigFileHandler fileHandler = new MapConfigFileHandler();
    if (fileHandler.defaultMapExists()) {
      try {
        logMessage("Updating existing default map configuration", false);
        return fileHandler.loadFromDefaultLocation();
      } catch (Exception e) {
        logMessage("Error loading existing map, creating new configuration", false);
        MapConfig config = new MapConfig();
        config.setName("Default Missing Diamond Map");
        return config;
      }
    } else {
      logMessage("Creating new default map configuration", false);
      MapConfig config = new MapConfig();
      config.setName("Default Missing Diamond Map");
      return config;
    }
  }

  /**
   * Updates locations in a map configuration.
   */
  private void updateLocationsInConfig(MapConfig mapConfig) {
    // Create a map of existing locations by ID for quick lookup
    Map<Integer, MapConfig.Location> existingLocations = new HashMap<>();
    if (mapConfig.getLocations() != null) {
      for (MapConfig.Location loc : mapConfig.getLocations()) {
        existingLocations.put(loc.getId(), loc);
      }
    }

    // Clear existing locations
    mapConfig.getLocations().clear();

    // Add locations, preserving positions for existing tiles
    for (CoordinatePoint point : pointManager.getAllPoints()) {
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
  }

  /**
   * Updates connections in a map configuration.
   */
  private void updateConnectionsInConfig(MapConfig mapConfig) {
    // Create a set to track unique connections we'll add
    Set<String> connectionKeys = new HashSet<>();

    // Add connections from current UI state
    for (CoordinatePoint point : pointManager.getAllPoints()) {
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
  }

  /**
   * Saves a map configuration to the default location.
   */
  private void saveConfigToDefaultLocation(MapConfig mapConfig) throws Exception {
    MapConfigFileHandler fileHandler = new MapConfigFileHandler();
    fileHandler.saveToDefaultLocation(mapConfig);
  }

  /**
   * Checks if a connection already exists in the list.
   */
  private boolean connectionExists(java.util.List<MapConfig.Connection> connections, int fromId, int toId) {
    for (MapConfig.Connection conn : connections) {
      if (conn.getFromId() == fromId && conn.getToId() == toId) {
        return true;
      }
    }
    return false;
  }

  /**
   * Copies coordinate data to the clipboard.
   */
  public void copyCoordinatesToClipboard() {
    if (pointManager.getAllPoints().isEmpty()) {
      logMessage("No coordinate points to copy.", false);
      return;
    }

    StringBuilder sb = new StringBuilder();
    sb.append("// Location data with percentages of map width/height\n");
    sb.append("private static final Object[][] LOCATION_DATA = {\n");
    sb.append("    // {id, name, x-percentage, y-percentage}\n");

    for (CoordinatePoint point : pointManager.getAllPoints()) {
      sb.append(String.format("    {%d, \"%s\", %.4f, %.4f},\n",
          point.getId(), point.getName(), point.getXPercent(), point.getYPercent()));
    }

    sb.append("};\n");

    // Copy to clipboard
    Clipboard clipboard = Clipboard.getSystemClipboard();
    ClipboardContent content = new ClipboardContent();
    content.putString(sb.toString());
    clipboard.setContent(content);

    logMessage("Copied " + pointManager.getAllPoints().size() + " coordinate points to clipboard.", false);
  }

  /**
   * Exports map data.
   */
  public void exportMapData() {
    if (pointManager.getAllPoints().isEmpty()) {
      logMessage("No map data to export.", false);
      return;
    }

    // Create a MapConfig object
    MapConfig mapConfig = new MapConfig();
    mapConfig.setName("Missing Diamond Map");

    // Add locations
    for (CoordinatePoint point : pointManager.getAllPoints()) {
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
    for (CoordinatePoint point : pointManager.getAllPoints()) {
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
        logMessage("Map configuration exported to " + file.getAbsolutePath(), true);
      }
    } catch (Exception e) {
      logMessage("Error exporting map: " + e.getMessage(), false);
    }
  }

  /**
   * Logs a message if a listener is available.
   */
  private void logMessage(String message, boolean isExport) {
    if (listener != null) {
      listener.onLogMessage(message);
      if (isExport) {
        listener.onMapDataExported(message, true);
      }
    }
  }
}