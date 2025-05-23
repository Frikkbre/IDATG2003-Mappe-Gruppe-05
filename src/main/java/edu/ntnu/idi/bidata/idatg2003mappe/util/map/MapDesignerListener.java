package edu.ntnu.idi.bidata.idatg2003mappe.util.map;

/**
 * <p>Interface for handling events from the MapDesignerTool.</p>
 * <p>This interface defines methods that must be implemented by classes
 * that need to be notified of map design events. It follows the Observer
 * pattern, allowing components like the UI to react to changes in the
 * map designer state.</p>
 *
 * @author Simen Gudbrandsen and Frikk Breadsroed
 * @version 0.0.1
 * @since 19.05.2025
 */
public interface MapDesignerListener {

  /**
   * <p>Called when a log message needs to be displayed.</p>
   * <p>This method is invoked when the map designer generates a message
   * that should be shown to the user or logged for debugging purposes.</p>
   *
   * @param message The message to log
   */
  void onLogMessage(String message);

  /**
   * <p>Called when the coordinate mode is toggled.</p>
   * <p>This method is invoked when coordinate mode is turned on or off.
   * Implementations can use this to adjust UI elements or game controls
   * based on the current mode.</p>
   *
   * @param enabled Whether coordinate mode is enabled
   */
  void onCoordinateModeToggled(boolean enabled);

  /**
   * <p>Called when the connection mode is toggled.</p>
   * <p>This method is invoked when connection mode is turned on or off.
   * Implementations can use this to adjust UI elements or game controls
   * based on the current mode.</p>
   *
   * @param enabled Whether connection mode is enabled
   */
  void onConnectionModeToggled(boolean enabled);

  /**
   * <p>Called when map data is exported.</p>
   * <p>This method is invoked when map data is exported to a file or clipboard.
   * It provides feedback about the success of the operation and the exported data.</p>
   *
   * @param data    The exported map data or a message about the export
   * @param success Whether the export was successful
   */
  void onMapDataExported(String data, boolean success);
}
