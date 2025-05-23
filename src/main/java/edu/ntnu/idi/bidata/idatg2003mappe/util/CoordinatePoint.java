package edu.ntnu.idi.bidata.idatg2003mappe.util;

import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>A class representing a coordinate point on the map.</p>
 * <p>Used by the MapDesignerTool for creating and editing game maps.
 * Each point represents a tile location on the game board and can have
 * visual representations and connections to other points.</p>
 *
 * @author Simen Gudbrandsen and Frikk Breadsroed
 * @version 0.0.4
 * @since 25.04.2025
 */
public class CoordinatePoint {
  private final int id;
  private final double xPercent;
  private final double yPercent;
  private final List<Integer> connections = new ArrayList<>();
  private double x, y;
  private Circle circle;
  private Label label;
  private String name;
  private boolean isSpecial = false;

  /**
   * <p>Creates a new coordinate point.</p>
   * <p>Initializes a point with the specified coordinates and percentage values.
   * The percentage values allow the point to maintain its relative position
   * when the map is resized.</p>
   *
   * @param id       The unique identifier for this point
   * @param x        The absolute x-coordinate on the map
   * @param y        The absolute y-coordinate on the map
   * @param xPercent The x-coordinate as a percentage of the map width (0.0 to 1.0)
   * @param yPercent The y-coordinate as a percentage of the map height (0.0 to 1.0)
   */
  public CoordinatePoint(int id, double x, double y, double xPercent, double yPercent) {
    this.id = id;
    this.x = x;
    this.y = y;
    this.xPercent = xPercent;
    this.yPercent = yPercent;
  }

  /**
   * <p>Creates a circle visual representation for this point.</p>
   * <p>The appearance of the circle depends on whether the point is special:
   * <ul>
   *   <li>Special points (red tiles) have larger circles with red fill</li>
   *   <li>Regular points (black tiles) have smaller circles with black fill</li>
   * </ul>
   * </p>
   *
   * @param isSpecial Whether this is a special point (affects appearance)
   * @return The created circle with appropriate styling
   */
  public Circle createCircle(boolean isSpecial) {
    this.isSpecial = isSpecial;

    // Special tiles (RED): 10px radius
    // Movement tiles (BLACK): 5px radius (smaller)
    double radius = isSpecial ? 10.0 : 5.0;

    Circle circle = new Circle(
        x, y,
        radius,
        isSpecial ? Color.RED : Color.BLACK
    );

    circle.setStroke(Color.WHITE);
    circle.setStrokeWidth(1.5);
    circle.setUserData(id); // Add this line to set the ID as user data

    this.circle = circle;
    return circle;
  }

  /**
   * <p>Creates a label for this point.</p>
   * <p>All points have labels created, but only labels for special points
   * are visible by default. The label displays the point's name and is
   * positioned near the point's visual representation.</p>
   *
   * @param name The text to display on the label
   * @return The created label, positioned next to the point
   */
  public Label createLabel(String name) {
    this.name = name;

    // Create label for all points, but only show it for special points
    Label label = new Label(name);
    label.setLayoutX(x + 10);
    label.setLayoutY(y - 10);
    label.setTextFill(Color.WHITE);
    label.setStyle("-fx-background-color: rgba(0,0,0,0.7); -fx-padding: 2px; -fx-font-size: 10pt;");
    label.setUserData("label_" + id); // Set userData to identify the label

    if (!isSpecial) {
      label.setVisible(false); // Hide labels for movement tiles
    }

    this.label = label;
    return label;
  }

  /**
   * <p>Updates the visual position of this point based on new map dimensions.</p>
   * <p>When the map is resized, this method recalculates the absolute coordinates
   * based on the stored percentage values to maintain the point's relative position.
   * It also updates the positions of the visual circle and label.</p>
   *
   * @param mapWidth  Current map width in pixels
   * @param mapHeight Current map height in pixels
   */
  public void updatePosition(double mapWidth, double mapHeight) {
    // Calculate new absolute coordinates from percentages
    double newX = xPercent * mapWidth;
    double newY = yPercent * mapHeight;

    // Update stored absolute coordinates
    this.x = newX;
    this.y = newY;

    // Update circle position if it exists
    if (circle != null) {
      circle.setCenterX(newX);
      circle.setCenterY(newY);
    }

    // Update label position if it exists
    if (label != null) {
      label.setLayoutX(newX + 10);
      label.setLayoutY(newY - 10);
    }
  }

  /**
   * <p>Adds a connection to another point.</p>
   * <p>Creates a one-way connection from this point to another point
   * identified by its ID. Duplicate connections are prevented.</p>
   *
   * @param targetId The ID of the point to connect to
   */
  public void addConnection(int targetId) {
    if (!connections.contains(targetId)) {
      connections.add(targetId);
    }
  }

  // Getters and setters

  /**
   * <p>Gets the unique identifier of this point.</p>
   *
   * @return The point's ID
   */
  public int getId() {
    return id;
  }

  /**
   * <p>Gets the absolute x-coordinate of this point.</p>
   *
   * @return The x-coordinate in pixels
   */
  public double getX() {
    return x;
  }

  /**
   * <p>Gets the absolute y-coordinate of this point.</p>
   *
   * @return The y-coordinate in pixels
   */
  public double getY() {
    return y;
  }

  /**
   * <p>Gets the x-coordinate as a percentage of the map width.</p>
   *
   * @return The x-coordinate percentage (0.0 to 1.0)
   */
  public double getXPercent() {
    return xPercent;
  }

  /**
   * <p>Gets the y-coordinate as a percentage of the map height.</p>
   *
   * @return The y-coordinate percentage (0.0 to 1.0)
   */
  public double getYPercent() {
    return yPercent;
  }

  /**
   * <p>Gets the circle visual representation of this point.</p>
   *
   * @return The JavaFX Circle object, or null if not created yet
   */
  public Circle getCircle() {
    return circle;
  }

  /**
   * <p>Sets the circle visual representation of this point.</p>
   *
   * @param circle The JavaFX Circle object to associate with this point
   */
  public void setCircle(Circle circle) {
    this.circle = circle;
  }

  /**
   * <p>Gets the name of this point.</p>
   *
   * @return The point's name
   */
  public String getName() {
    return name;
  }

  /**
   * <p>Sets the name of this point.</p>
   *
   * @param name The new name for the point
   */
  public void setName(String name) {
    this.name = name;
  }

  /**
   * <p>Checks if this is a special point.</p>
   * <p>Special points represent red tiles on the game board that have
   * special effects or properties.</p>
   *
   * @return {@code true} if this is a special point, {@code false} otherwise
   */
  public boolean isSpecial() {
    return isSpecial;
  }

  /**
   * <p>Sets whether this is a special point.</p>
   *
   * @param special {@code true} to mark as special, {@code false} otherwise
   */
  public void setSpecial(boolean special) {
    this.isSpecial = special;
  }

  /**
   * <p>Gets the list of connections from this point.</p>
   * <p>Returns a list of point IDs that this point connects to.</p>
   *
   * @return The list of target point IDs
   */
  public List<Integer> getConnections() {
    return connections;
  }
}
