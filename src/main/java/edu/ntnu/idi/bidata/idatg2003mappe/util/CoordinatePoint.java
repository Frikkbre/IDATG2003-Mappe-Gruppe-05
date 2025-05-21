package edu.ntnu.idi.bidata.idatg2003mappe.util;

import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

import java.util.ArrayList;
import java.util.List;

/**
 * A class representing a coordinate point on the map.
 * Used by the MapDesignerTool for creating and editing game maps.
 *
 * @author Simen Gudbrandsen and Frikk Breadsroed
 * @version 0.0.4
 * @since 25.04.2025
 */
public class CoordinatePoint {
  private final int id;
  private double x, y;
  private final double xPercent;
  private final double yPercent;
  private Circle circle;
  private Label label;
  private String name;
  private boolean isSpecial = false;
  private final List<Integer> connections = new ArrayList<>();

  /**
   * Creates a new coordinate point.
   *
   * @param id The unique identifier for this point
   * @param x The x-coordinate
   * @param y The y-coordinate
   * @param xPercent The x-coordinate as a percentage of the map width
   * @param yPercent The y-coordinate as a percentage of the map height
   */
  public CoordinatePoint(int id, double x, double y, double xPercent, double yPercent) {
    this.id = id;
    this.x = x;
    this.y = y;
    this.xPercent = xPercent;
    this.yPercent = yPercent;
  }

  /**
   * Creates a circle visual representation for this point.
   *
   * @param isSpecial Whether this is a special point (affects appearance)
   * @return The created circle
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
   * Creates a label for this point.
   *
   * @param name The text to display on the label
   * @return The created label, or null if this isn't a special point
   */
  public Label createLabel(String name) {
    this.name = name;

    // Create label for all points, but only show it for special points
    Label label = new Label(name);
    label.setLayoutX(x + 10);
    label.setLayoutY(y - 10);
    label.setTextFill(Color.WHITE);
    label.setStyle("-fx-background-color: rgba(0,0,0,0.7); -fx-padding: 2px; -fx-font-size: 10pt;");
    label.setUserData(id); // Set userData to the point ID

    if (!isSpecial) {
      label.setVisible(false); // Hide labels for movement tiles
    }

    this.label = label;
    return label;
  }

  /**
   * Updates the visual position of this point based on new map dimensions.
   *
   * @param mapWidth Current map width
   * @param mapHeight Current map height
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
   * Adds a connection to another point.
   *
   * @param targetId The ID of the point to connect to
   */
  public void addConnection(int targetId) {
    if (!connections.contains(targetId)) {
      connections.add(targetId);
    }
  }

  // Getters and setters

  public int getId() {
    return id;
  }

  public double getX() {
    return x;
  }

  public double getY() {
    return y;
  }

  public double getXPercent() {
    return xPercent;
  }

  public double getYPercent() {
    return yPercent;
  }

  public Circle getCircle() {
    return circle;
  }

  public void setCircle(Circle circle) {
    this.circle = circle;
  }

  public Label getLabel() {
    return label;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public boolean isSpecial() {
    return isSpecial;
  }

  public void setSpecial(boolean special) {
    this.isSpecial = special;
  }

  public List<Integer> getConnections() {
    return connections;
  }
}