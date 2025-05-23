package edu.ntnu.idi.bidata.idatg2003mappe.util;

import javafx.scene.Node;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Circle;

import java.util.*;
import java.util.logging.Logger;

/**
 * <p>Manages coordinate points for the map designer.</p>
 * <p>This class is responsible for creating, tracking, and maintaining coordinate points
 * that represent locations on a game board. It provides methods to create new points,
 * retrieve points by ID, and update point positions when the map dimensions change.</p>
 *
 * @author Simen Gudbrandsen and Frikk Breadsroed
 * @version 0.0.4
 * @since 25.04.2025
 */
public class PointManager {
  private final List<CoordinatePoint> capturedPoints = new ArrayList<>();
  private final Map<Integer, CoordinatePoint> pointsById = new HashMap<>();
  private int nextPointId = 1;
  private Pane overlayPane; // Reference to the overlay pane

  private static final Logger logger = Logger.getLogger(PointManager.class.getName());

  /**
   * <p>Creates a new point with the given parameters.</p>
   * <p>This method creates a new {@link CoordinatePoint} with the specified coordinates and properties,
   * assigns it a unique ID, and adds it to the internal collections for tracking.</p>
   *
   * @param x         The absolute x-coordinate on the map
   * @param y         The absolute y-coordinate on the map
   * @param xPercent  The x-coordinate as a percentage of the map width (0.0 to 1.0)
   * @param yPercent  The y-coordinate as a percentage of the map height (0.0 to 1.0)
   * @param isSpecial Whether this is a special point (affects appearance and behavior)
   * @return The newly created coordinate point
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
   * <p>Sets the overlay pane reference for visual element management.</p>
   * <p>This method associates the point manager with a JavaFX pane where visual elements
   * for coordinate points will be displayed. This reference is needed for operations
   * that add or remove visual elements.</p>
   *
   * @param overlayPane The JavaFX pane where point visuals will be displayed
   */
  public void setOverlayPane(Pane overlayPane) {
    this.overlayPane = overlayPane;
  }

  /**
   * <p>Gets a point by its ID.</p>
   * <p>This method retrieves a coordinate point from the internal collection
   * based on its unique identifier.</p>
   *
   * @param id The unique identifier of the point to retrieve
   * @return The coordinate point with the specified ID, or null if not found
   */
  public CoordinatePoint getPointById(int id) {
    return pointsById.get(id);
  }

  /**
   * <p>Gets all captured points.</p>
   * <p>This method returns an unmodifiable view of all coordinate points
   * that have been created and are being tracked by this manager.</p>
   *
   * @return An unmodifiable list of all coordinate points
   */
  public List<CoordinatePoint> getAllPoints() {
    return Collections.unmodifiableList(capturedPoints);
  }

  /**
   * <p>Clears all points and their visual representations.</p>
   * <p>This method removes all coordinate points from the internal collections
   * and also removes their visual elements (circles and labels) from the overlay pane.
   * It resets the ID counter to start fresh.</p>
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
   * <p>Updates the positions of all points based on new dimensions.</p>
   * <p>This method recalculates the absolute positions of all coordinate points
   * when the map dimensions change, using their stored percentage values to maintain
   * relative positions. It's typically called when the map is resized.</p>
   *
   * @param width  The new width of the map
   * @param height The new height of the map
   */
  public void updateAllPointPositions(double width, double height) {
    for (CoordinatePoint point : capturedPoints) {
      point.updatePosition(width, height);
    }
  }

  /**
   * <p>Registers an existing point with the map designer.</p>
   * <p>This method is used to recreate coordinate points from saved data,
   * such as when loading a map configuration. It preserves the original IDs
   * and properties of the points.</p>
   *
   * @param id         The unique identifier for the point
   * @param x          The absolute x-coordinate on the map
   * @param y          The absolute y-coordinate on the map
   * @param xPercent   The x-coordinate as a percentage of the map width (0.0 to 1.0)
   * @param yPercent   The y-coordinate as a percentage of the map height (0.0 to 1.0)
   * @param name       The name of the location
   * @param isSpecial  Whether this is a special point (affects appearance and behavior)
   * @param overlayPane The JavaFX pane where visual elements are displayed
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
