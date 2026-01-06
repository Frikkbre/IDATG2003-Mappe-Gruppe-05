package edu.ntnu.idi.bidata.idatg2003mappe.util;

import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;

import java.util.ArrayList;
import java.util.Collection;
import java.util.logging.Logger;

/**
 * <p>Manages connections between points for the map designer.</p>
 * <p>This class is responsible for creating, drawing, and managing the visual representation
 * of connections between coordinate points on the game map. It works closely with the
 * {@link PointManager} to access point data and maintains a collection of connection lines.</p>
 *
 * @author Simen Gudbrandsen and Frikk Breadsroed
 * @version 0.0.3
 * @since 19.05.2025
 */
public class ConnectionManager {
  private static final Logger logger = Logger.getLogger(ConnectionManager.class.getName());
  private final Pane overlayPane;
  private final PointManager pointManager;
  private final Collection<Line> connectionLines = new ArrayList<>();

  /**
   * <p>Creates a new ConnectionManager with the specified overlay pane and point manager.</p>
   * <p>This constructor initializes the connection manager with references to the overlay pane
   * where connection lines will be drawn and the point manager that provides access to coordinate points.</p>
   *
   * @param overlayPane  The JavaFX pane where connection lines will be rendered
   * @param pointManager The point manager containing coordinate points to connect
   */
  public ConnectionManager(Pane overlayPane, PointManager pointManager) {
    this.overlayPane = overlayPane;
    this.pointManager = pointManager;
  }

  /**
   * <p>Creates a connection between two points identified by their IDs.</p>
   * <p>This method establishes a connection between the source and target points by:
   * <ul>
   *   <li>Finding the points from their IDs</li>
   *   <li>Adding connection data to the source point</li>
   *   <li>Drawing a visual line between the points</li>
   * </ul>
   * </p>
   *
   * @param sourceId The ID of the source point
   * @param targetId The ID of the target point
   * @return {@code true} if the connection was created successfully, {@code false} otherwise
   */
  public boolean createConnection(int sourceId, int targetId) {
    // Find the points
    CoordinatePoint source = pointManager.getPointById(sourceId);
    CoordinatePoint target = pointManager.getPointById(targetId);

    if (source == null || target == null) {
      logMessage("Error: Could not find points with IDs " + sourceId + " and " + targetId);
      return false;
    }

    // Add connection data
    source.addConnection(targetId);

    // Draw the connection line
    drawConnection(source, target);
    logMessage("Created connection: " + sourceId + " → " + targetId);
    return true;
  }

  /**
   * <p>Redraws all connections between points.</p>
   * <p>This method clears all existing connection lines and redraws them based on
   * the current state of connections stored in the coordinate points. It's useful
   * when the map is resized or coordinate points are moved.</p>
   */
  public void redrawConnections() {
    // Remove all existing connection lines
    clearConnections();

    // Redraw all connections
    pointManager.getAllPoints().forEach(source ->
        source.getConnections().forEach(targetId -> {
          CoordinatePoint target = pointManager.getPointById(targetId);
          if (target != null) {
            drawConnection(source, target);
          }
        })
    );

  }

  /**
   * <p>Draws a connection line between two points.</p>
   * <p>This method creates and adds a visual line between the source and target points
   * to the overlay pane. The line is styled appropriately and added at index 0 to ensure
   * it appears beneath any circles representing points.</p>
   *
   * @param source The source coordinate point
   * @param target The target coordinate point
   */
// In ConnectionManager.java - modify the drawConnection method
  public void drawConnection(CoordinatePoint source, CoordinatePoint target) {
    if (source.getCircle() == null || target.getCircle() == null) {
      logMessage("Cannot draw connection: circles not initialized for points " +
          source.getId() + " or " + target.getId());
      return;
    }

    // Create line with correct coordinates
    Line line = new Line(
        source.getCircle().getCenterX(), source.getCircle().getCenterY(),
        target.getCircle().getCenterX(), target.getCircle().getCenterY()
    );

    // Make sure the line is visible
    line.setStroke(Color.BLACK);
    line.setStrokeWidth(2.5); // Increase width for better visibility
    line.setUserData("connection"); // For identification

    // Add line to overlay pane - IMPORTANT: Add at index 0 to ensure it's below circles
    overlayPane.getChildren().add(0, line);
    connectionLines.add(line);

    // Debug
    logger.info("Connection line created at: (" +
        source.getCircle().getCenterX() + "," + source.getCircle().getCenterY() + ") to (" +
        target.getCircle().getCenterX() + "," + target.getCircle().getCenterY() + ")");

    logMessage("Created connection from " + source.getId() + " to " + target.getId());
  }

  /**
   * <p>Clears all connection lines from the overlay pane.</p>
   * <p>This method removes all visual connection lines from the display and clears
   * the internal list of connection lines. It uses two approaches to ensure complete removal:
   * <ul>
   *   <li>Removing all lines from the tracked list</li>
   *   <li>Filtering the overlay pane to remove any lines with "connection" userData</li>
   * </ul>
   * </p>
   */
  public void clearConnections() {
    overlayPane.getChildren().removeAll(connectionLines);

    // Also use a more aggressive approach to ensure ALL connection lines are removed
    // by filtering by userData
    overlayPane.getChildren().removeIf(node ->
        node.getUserData() != null &&
            "connection".equals(node.getUserData()));

    // Clear our internal list
    connectionLines.clear();
  }

  /**
   * <p>Logs a message using the class logger.</p>
   * <p>This private helper method logs messages about connection operations
   * for debugging and monitoring purposes.</p>
   *
   * @param message The message to log
   */
  private void logMessage(String message) {
    logger.info(message);
  }
}
