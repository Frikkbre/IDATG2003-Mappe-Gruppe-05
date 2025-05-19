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
 * @version 0.0.1
 * @since 25.04.2025
 */
public class CoordinatePoint {
  private int id;
  private double x, y;
  private double xPercent, yPercent;
  private Circle circle;
  private Label label;
  private String name;
  private boolean isSpecial = false;
  private List<Integer> connections = new ArrayList<>();

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
    // Movement tiles (YELLOW): 5px radius (smaller)
    Circle circle = new Circle(
        x, y,
        isSpecial ? 10 : 5,  // Different sizes
        isSpecial ? Color.RED : Color.YELLOW
    );
    circle.setStroke(Color.BLACK);
    circle.setStrokeWidth(1.5);

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

    // Only create labels for special points
    if (isSpecial) {
      Label label = new Label(name);
      label.setLayoutX(x + 10);
      label.setLayoutY(y - 10);
      label.setTextFill(Color.WHITE);
      label.setStyle("-fx-background-color: rgba(0,0,0,0.7); -fx-padding: 2px; -fx-font-size: 10pt;");

      this.label = label;
      return label;
    }

    return null;
  }

  /**
   * Adds a connection to another point.
   *
   * @param targetId The ID of the point to connect to
   */
  public void addConnection(int targetId) {
    connections.add(targetId);
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

  public Label getLabel() {
    return label;
  }

  public String getName() {
    return name;
  }

  public boolean isSpecial() {
    return isSpecial;
  }

  public List<Integer> getConnections() {
    return connections;
  }
}