package edu.ntnu.idi.bidata.idatg2003mappe.util;

import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;

import java.util.ArrayList;
import java.util.List;

/**
 * Manages connections between points for the map designer.
 */
public class ConnectionManager {
  private final Pane overlayPane;
  private final PointManager pointManager;
  private final List<Line> connectionLines = new ArrayList<>();
  private final MapDesignerListener listener;

  /**
   * Creates a new ConnectionManager.
   */
  public ConnectionManager(Pane overlayPane, PointManager pointManager) {
    this.overlayPane = overlayPane;
    this.pointManager = pointManager;
    this.listener = null;
  }

  /**
   * Creates a new ConnectionManager with a listener.
   */
  public ConnectionManager(Pane overlayPane, PointManager pointManager, MapDesignerListener listener) {
    this.overlayPane = overlayPane;
    this.pointManager = pointManager;
    this.listener = listener;
  }

  /**
   * Creates a connection between two points.
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
   * Redraws all connections between points.
   */
  public void redrawConnections() {
    // Remove all existing connection lines
    clearConnections();

    // Redraw all connections
    for (CoordinatePoint source : pointManager.getAllPoints()) {
      for (Integer targetId : source.getConnections()) {
        CoordinatePoint target = pointManager.getPointById(targetId);
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
      logMessage("Cannot draw connection: circles not initialized for points " +
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
    connectionLines.add(line);

    logMessage("Created connection from " + source.getId() + " to " + target.getId());
  }

  /**
   * Clears all connection lines.
   */
  public void clearConnections() {
    overlayPane.getChildren().removeAll(connectionLines);
    connectionLines.clear();
  }

  /**
   * Logs a message if a listener is available.
   */
  private void logMessage(String message) {
    if (listener != null) {
      listener.onLogMessage(message);
    }
  }
}