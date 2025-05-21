package edu.ntnu.idi.bidata.idatg2003mappe.markers;

import edu.ntnu.idi.bidata.idatg2003mappe.map.Tile;

/**
 * Main superclass of the marker classes.
 * This class is used to represent a marker on the map.
 *
 * @author Simen Gudbrandsen and Frikk Breadsroed
 * @version 0.0.1
 * @since 03.02.2025
 */
public abstract class Marker {
  private final String type;
  private final int value;
  private Tile location;
  private boolean isRevealed;

  /**
   * Constructor for the Marker class.
   *
   * @param type The type of marker
   * @param value The value of the marker
   */
  public Marker(String type, int value) {
    this.type = type;
    this.value = value;
    this.isRevealed = false;
  }

  /**
   * Gets the type of marker.
   *
   * @return The type of marker
   */
  public String getType() {
    return type;
  }

  /**
   * Gets the value of the marker.
   *
   * @return The value of the marker
   */
  public int getValue() {
    return value;
  }

  /**
   * Gets the location of the marker.
   *
   * @return The location of the marker
   */
  public Tile getLocation() {
    return location;
  }

  /**
   * Sets the location of the marker.
   *
   * @param location The new location
   */
  public void setLocation(Tile location) {
    this.location = location;
  }

  /**
   * Checks if the marker is revealed.
   *
   * @return true if the marker is revealed, false otherwise
   */
  public boolean isRevealed() {
    return isRevealed;
  }

  /**
   * Reveals the marker.
   */
  public void reveal() {
    this.isRevealed = true;
  }

  /**
   * Hides the marker.
   */
  public void hide() {
    this.isRevealed = false;
  }

  /**
   * Removes the marker from its current location.
   */
  public void removeFromLocation() {
    this.location = null;
  }
}