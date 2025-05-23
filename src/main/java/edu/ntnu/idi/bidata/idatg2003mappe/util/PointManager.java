package edu.ntnu.idi.bidata.idatg2003mappe.util;

import javafx.scene.Node;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Circle;

import java.util.*;
import java.util.logging.Logger;

/**
 * Manages coordinate points for the map designer.
 */
public class PointManager {
  private final List<CoordinatePoint> capturedPoints = new ArrayList<>();
  private final Map<Integer, CoordinatePoint> pointsById = new HashMap<>();
  private int nextPointId = 1;
  private Pane overlayPane; // Reference to the overlay pane

  private static final Logger logger = Logger.getLogger(PointManager.class.getName());

  /**
   * Creates a new point with the given parameters.
   */
  public CoordinatePoint createPoint(double x, double y, double xPercent, double yPercent, boolean isSpecial) {
    // Create new coordinate point
    CoordinatePoint point = new CoordinatePoint(nextPointId++, x, y, xPercent, yPercent);
    point.setSpecial(isSpecial);

    // Create name
    String pointName = isSpecial ? "SpecialLoc" + point.getId() : "Location" + point.getId();
    point.setName(pointName);

    // Add to collections
    capturedPoints.add(point);
    pointsById.put(point.getId(), point);

    return point;
  }

  /**
   * Sets the overlay pane reference for visual element management.
   */
  public void setOverlayPane(Pane overlayPane) {
    this.overlayPane = overlayPane;
  }

  /**
   * Gets a point by its ID.
   */
  public CoordinatePoint getPointById(int id) {
    return pointsById.get(id);
  }

  /**
   * Gets all captured points.
   */
  public List<CoordinatePoint> getAllPoints() {
    return Collections.unmodifiableList(capturedPoints);
  }

  /**
   * Clears all points.
   */
  public void clear() {
    if (overlayPane != null) {
      // Remove all visual elements associated with points
      List<Node> nodesToRemove = new ArrayList<>();

      for (CoordinatePoint point : capturedPoints) {
        // Remove circles
        if (point.getCircle() != null) {
          nodesToRemove.add(point.getCircle());
        }

        // Remove labels (if they exist)
        overlayPane.getChildren().removeIf(node ->
            node.getUserData() != null &&
            node.getUserData().equals("label_" + point.getId()));
      }

      // Bulk remove circles
      overlayPane.getChildren().removeAll(nodesToRemove);
    }

    // Clear data structures
    capturedPoints.clear();
    pointsById.clear();
    nextPointId = 1;

    logger.info("All points and visual elements cleared");
  }

  /**
   * Updates the positions of all points based on new dimensions.
   */
  public void updateAllPointPositions(double width, double height) {
    for (CoordinatePoint point : capturedPoints) {
      point.updatePosition(width, height);
    }
  }

  /**
   * Registers an existing point with the map designer.
   */
  public void registerExistingPoint(int id, double x, double y, double xPercent, double yPercent,
                                    String name, boolean isSpecial, Pane overlayPane) {
    // Check if this ID is already registered to avoid duplicates
    if (pointsById.containsKey(id)) {
      return;
    }

    // Create and register the point
    CoordinatePoint point = new CoordinatePoint(id, x, y, xPercent, yPercent);
    point.setName(name);
    point.setSpecial(isSpecial);

    // If the circle exists in the overlay, link it to the point
    for (Node node : overlayPane.getChildren()) {
      if (node instanceof Circle circle) {
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
  }

}