package edu.ntnu.idi.bidata.idatg2003mappe.util.map;

/**
 * Interface for handling events from the MapDesignerTool.
 * Implemented by classes that need to be notified of map design events.
 *
 * @author Simen Gudbrandsen and Frikk Breadsroed
 * @version 0.0.1
 * @since 25.04.2025
 */
public interface MapDesignerListener {

  /**
   * Called when a log message needs to be displayed.
   *
   * @param message The message to log
   */
  void onLogMessage(String message);

  /**
   * Called when the coordinate mode is toggled.
   *
   * @param enabled Whether coordinate mode is enabled
   */
  void onCoordinateModeToggled(boolean enabled);

  /**
   * Called when the connection mode is toggled.
   *
   * @param enabled Whether connection mode is enabled
   */
  void onConnectionModeToggled(boolean enabled);

  /**
   * Called when map data is exported.
   *
   * @param data    The exported map data
   * @param success Whether the export was successful
   */
  void onMapDataExported(String data, boolean success);
}