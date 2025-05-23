package edu.ntnu.idi.bidata.idatg2003mappe.app.missingdiamond.ui;

import edu.ntnu.idi.bidata.idatg2003mappe.util.map.MapDesignerListener;
import edu.ntnu.idi.bidata.idatg2003mappe.util.map.MapDesignerTool;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;

import java.util.logging.Logger;

/**
 * <p>Manager class for the map designer tool.</p>
 * <p>This class provides a wrapper around the {@link MapDesignerTool} to integrate
 * it with the Missing Diamond game UI. It handles the coordination between the
 * map designer functionality and the game board display.</p>
 * <p>Key responsibilities include:</p>
 * <ul>
 *   <li>Managing connections between map locations</li>
 *   <li>Registering points on the map</li>
 *   <li>Tracking connection source/target information</li>
 *   <li>Providing UI controls for the map designer</li>
 * </ul>
 *
 * @author Simen Gudbrandsen and Frikk Breadsroed
 * @version 0.0.1
 * @since 23.05.2025
 */
public class MapDesignerManager {
  private final MapDesignerTool mapDesigner;
  private int connectionSourceId = -1;

  /**
   * <p>Logger for logging messages.</p>
   */
  private final Logger logger = Logger.getLogger(MapDesignerManager.class.getName());

  /**
   * <p>Creates a new map designer manager.</p>
   * <p>Initializes the manager with the specified overlay pane and dimensions,
   * and sets up the underlying map designer tool.</p>
   *
   * @param overlayPane The {@link Pane} to use for drawing map elements
   * @param width       The width of the map in pixels
   * @param height      The height of the map in pixels
   * @param listener    The {@link MapDesignerListener} to receive map design events
   */
  public MapDesignerManager(Pane overlayPane, double width, double height,
                            MapDesignerListener listener) {
    this.mapDesigner = new MapDesignerTool(overlayPane, width, height, listener);
  }

  /**
   * <p>Creates a connection between the currently selected source and target tiles.</p>
   * <p>Uses the map designer tool to create a connection between the tiles specified
   * in the source and target ID fields.</p>
   */
  public void createConnection() {
    mapDesigner.createConnection();
  }

  /**
   * <p>Creates a direct connection between two specific tiles.</p>
   * <p>This method bypasses the UI fields and directly creates a connection
   * between the specified source and target tile IDs.</p>
   *
   * @param sourceId The ID of the source tile
   * @param targetId The ID of the target tile
   * @return <code>true</code> if the connection was successfully created, <code>false</code> otherwise
   */
  public boolean createDirectConnection(int sourceId, int targetId) {
    boolean success = mapDesigner.createDirectConnection(sourceId, targetId);
    if (success) {
      logger.info("Connection created successfully between " + sourceId + " and " + targetId);
    } else {
      logger.warning("Failed to create connection between " + sourceId + " and " + targetId);
    }
    return success;
  }

  /**
   * <p>Registers an existing point in the map designer.</p>
   * <p>This method is used when synchronizing the board view with the map designer,
   * allowing the designer to track and manipulate existing locations.</p>
   *
   * @param id        The unique ID of the point
   * @param x         The x-coordinate in pixels
   * @param y         The y-coordinate in pixels
   * @param xPercent  The x-coordinate as a percentage of the map width
   * @param yPercent  The y-coordinate as a percentage of the map height
   * @param name      The name of the location
   * @param isSpecial Whether this is a special location (red tile)
   */
  public void registerExistingPoint(int id, double x, double y, double xPercent, double yPercent,
                                    String name, boolean isSpecial) {
    mapDesigner.registerExistingPoint(id, x, y, xPercent, yPercent, name, isSpecial);
  }

  /**
   * <p>Resets the connection source ID.</p>
   * <p>This method clears the currently selected source point for connection creation.</p>
   */
  public void resetConnectionSourceId() {
    connectionSourceId = -1;
  }

  /**
   * <p>Gets the currently selected connection source ID.</p>
   *
   * @return The ID of the currently selected source point, or -1 if none is selected
   */
  public int getConnectionSourceId() {
    return connectionSourceId;
  }

  /**
   * <p>Sets the connection source ID.</p>
   * <p>This method specifies which point should be used as the source
   * when creating a connection.</p>
   *
   * @param id The ID to use as the connection source
   */
  public void setConnectionSourceId(int id) {
    this.connectionSourceId = id;
  }

  /**
   * <p>Gets the status label from the map designer tool.</p>
   *
   * @return The {@link Label} used to display status messages
   */
  public Label getStatusLabel() {
    return mapDesigner.getStatusLabel();
  }

  /**
   * <p>Gets the tile type selector from the map designer tool.</p>
   *
   * @return The {@link ChoiceBox} used to select tile types
   */
  public ChoiceBox<String> getTileTypeSelector() {
    return mapDesigner.getTileTypeSelector();
  }

  /**
   * <p>Gets the source ID field from the map designer tool.</p>
   *
   * @return The {@link TextField} used to enter the source point ID
   */
  public TextField getSourceIdField() {
    return mapDesigner.getSourceIdField();
  }

  /**
   * <p>Gets the target ID field from the map designer tool.</p>
   *
   * @return The {@link TextField} used to enter the target point ID
   */
  public TextField getTargetIdField() {
    return mapDesigner.getTargetIdField();
  }

  /**
   * <p>Checks if coordinate mode is active.</p>
   * <p>In coordinate mode, clicks on the map will create new points.</p>
   *
   * @return <code>true</code> if coordinate mode is active, <code>false</code> otherwise
   */
  public boolean isCoordinateMode() {
    return mapDesigner.isCoordinateMode();
  }

  /**
   * <p>Checks if connection mode is active.</p>
   * <p>In connection mode, clicks on the map will select points to connect.</p>
   *
   * @return <code>true</code> if connection mode is active, <code>false</code> otherwise
   */
  public boolean isConnectionMode() {
    return mapDesigner.isConnectionMode();
  }

  /**
   * <p>Gets the underlying map designer tool.</p>
   *
   * @return The {@link MapDesignerTool} instance being managed
   */
  public MapDesignerTool getMapDesignerTool() {
    return mapDesigner;
  }
}
