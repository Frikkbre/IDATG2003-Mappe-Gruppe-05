package edu.ntnu.idi.bidata.idatg2003mappe.util.map;

import edu.ntnu.idi.bidata.idatg2003mappe.filehandling.map.MapConfig;
import edu.ntnu.idi.bidata.idatg2003mappe.filehandling.map.MapConfigFileHandler;
import edu.ntnu.idi.bidata.idatg2003mappe.util.CoordinatePoint;
import edu.ntnu.idi.bidata.idatg2003mappe.util.PointManager;
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
 * <p>Handles file operations for the map designer.</p>
 * <p>This class is responsible for loading, saving, and exporting map data.
 * It converts between the internal coordinate point representation and the
 * {@link MapConfig} format used for file storage. It also provides functions
 * to copy coordinate data to the clipboard for use in code.</p>
 *
 * @author Simen Gudbrandsen and Frikk Breadsroed
 * @version 0.0.3
 * @since 25.04.2025
 */
public class MapFileHandler {
  private final MapDesignerListener listener;
  private final PointManager pointManager;

  /**
   * <p>Creates a new MapFileHandler with the specified listener and point manager.</p>
   * <p>This constructor initializes the file handler with references to the components
   * needed to access coordinate data and report events.</p>
   *
   * @param listener      The listener to receive events and log messages
   * @param pointManager  The point manager containing coordinate data
   */
  public MapFileHandler(MapDesignerListener listener, PointManager pointManager) {
    this.listener = listener;
    this.pointManager = pointManager;
  }

  /**
   * <p>Saves the current map configuration as the default map.</p>
   * <p>This method converts the current coordinate points and connections to a
   * {@link MapConfig} object and saves it to the default location. If an existing
   * default map exists, it updates it while trying to preserve the positions of
   * existing tiles.</p>
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
   * <p>Loads or creates a map configuration.</p>
   * <p>This private helper method attempts to load the existing default map configuration.
   * If it doesn't exist or can't be loaded, it creates a new one.</p>
   *
   * @return A {@link MapConfig} object, either loaded from the default location or newly created
   * @throws Exception If an error occurs during loading or creation
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
   * <p>Updates locations in a map configuration.</p>
   * <p>This private helper method updates the locations in a map configuration based
   * on the current coordinate points. It tries to preserve the positions of existing
   * locations when possible.</p>
   *
   * @param mapConfig The map configuration to update
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
   * <p>Updates connections in a map configuration.</p>
   * <p>This private helper method updates the connections in a map configuration
   * based on the current connections between coordinate points.</p>
   *
   * @param mapConfig The map configuration to update
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
   * <p>Saves a map configuration to the default location.</p>
   * <p>This private helper method saves a map configuration to the default
   * location using the {@link MapConfigFileHandler}.</p>
   *
   * @param mapConfig The map configuration to save
   * @throws Exception If an error occurs during saving
   */
  private void saveConfigToDefaultLocation(MapConfig mapConfig) throws Exception {
    MapConfigFileHandler fileHandler = new MapConfigFileHandler();
    fileHandler.saveToDefaultLocation(mapConfig);
  }

  /**
   * <p>Checks if a connection already exists in the list.</p>
   * <p>This private helper method checks if a connection between two points
   * already exists in a list of connections.</p>
   *
   * @param connections The list of connections to check
   * @param fromId      The ID of the source point
   * @param toId        The ID of the target point
   * @return {@code true} if the connection exists, {@code false} otherwise
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
   * <p>Copies coordinate data to the clipboard.</p>
   * <p>This method formats the coordinate point data as Java code and copies
   * it to the system clipboard, making it easy to use in code.</p>
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
   * <p>Exports map data to a file.</p>
   * <p>This method creates a {@link MapConfig} from the current coordinate points and
   * connections, then prompts the user to select a file location and saves the data there.</p>
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
   * <p>Logs a message if a listener is available.</p>
   * <p>This private helper method sends log messages to the registered {@link MapDesignerListener}
   * if one exists. It also optionally notifies about export events.</p>
   *
   * @param message   The message to log
   * @param isExport  Whether this message is related to an export operation
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
