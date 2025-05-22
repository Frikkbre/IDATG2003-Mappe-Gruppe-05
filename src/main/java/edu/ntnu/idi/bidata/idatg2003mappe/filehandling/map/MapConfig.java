package edu.ntnu.idi.bidata.idatg2003mappe.filehandling.map;

import java.util.ArrayList;
import java.util.List;

/**
 * Model class representing a Missing Diamond map configuration.
 * Can be serialized to and deserialized from JSON.
 *
 * @author Simen Gudbrandsen and Frikk Breadsroed
 * @version 0.0.1
 * @since 20.05.2025
 */
public class MapConfig {
  // A location on the map
  public static class Location {
    private int id;
    private String name;
    private double xPercent;
    private double yPercent;
    private boolean isSpecial;

    public Location(int id, String name, double xPercent, double yPercent, boolean isSpecial) {
      this.id = id;
      this.name = name;
      this.xPercent = xPercent;
      this.yPercent = yPercent;
      this.isSpecial = isSpecial;
    }

    // Getters and setters
    public int getId() {
      return id;
    }

    public void setId(int id) {
      this.id = id;
    }

    public String getName() {
      return name;
    }

    public void setName(String name) {
      this.name = name;
    }

    public double getXPercent() {
      return xPercent;
    }

    public void setXPercent(double xPercent) {
      this.xPercent = xPercent;
    }

    public double getYPercent() {
      return yPercent;
    }

    public void setYPercent(double yPercent) {
      this.yPercent = yPercent;
    }

    public boolean isSpecial() {
      return isSpecial;
    }

    public void setSpecial(boolean special) {
      isSpecial = special;
    }
  }

  // A connection between two locations
  public static class Connection {
    private int fromId;
    private int toId;

    public Connection(int fromId, int toId) {
      this.fromId = fromId;
      this.toId = toId;
    }

    // Getters and setters
    public int getFromId() {
      return fromId;
    }

    public void setFromId(int fromId) {
      this.fromId = fromId;
    }

    public int getToId() {
      return toId;
    }

    public void setToId(int toId) {
      this.toId = toId;
    }
  }

  private String name;
  private List<Location> locations = new ArrayList<>();
  private List<Connection> connections = new ArrayList<>();

  // Getters and setters
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public List<Location> getLocations() {
    return locations;
  }

  public List<Connection> getConnections() {
    return connections;
  }

  // Helper methods
  public void addLocation(Location location) {
    locations.add(location);
  }

  public void addConnection(Connection connection) {
    connections.add(connection);
  }
}