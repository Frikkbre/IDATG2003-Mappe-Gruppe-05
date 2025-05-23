package edu.ntnu.idi.bidata.idatg2003mappe.filehandling.map;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>Model class representing a Missing Diamond map configuration.</p>
 * <p>This class stores all the data needed to define a game map, including
 * locations (cities) and connections between them. It can be serialized to
 * and deserialized from JSON format for save/load functionality.</p>
 * <p>The configuration includes:</p>
 * <ul>
 *   <li>A map name for identification</li>
 *   <li>A collection of locations with positions and properties</li>
 *   <li>A collection of connections defining paths between locations</li>
 * </ul>
 *
 * @author Simen Gudbrandsen and Frikk Breadsroed
 * @version 0.0.1
 * @since 20.05.2025
 */
public class MapConfig {
  /**
   * <p>Represents a location on the map.</p>
   * <p>Each location has a unique ID, name, position (as percentage of map dimensions),
   * and a flag indicating whether it's a special location (red tile) where tokens can
   * be found.</p>
   */
  public static class Location {
    private int id;
    private String name;
    private double xPercent;
    private double yPercent;
    private boolean isSpecial;

    /**
     * <p>Constructs a new Location instance.</p>
     * <p>Initializes a location with the specified properties.</p>
     *
     * @param id The unique identifier for the location
     * @param name The name of the location (city name)
     * @param xPercent The x-coordinate as a percentage of map width (0.0-1.0)
     * @param yPercent The y-coordinate as a percentage of map height (0.0-1.0)
     * @param isSpecial <code>true</code> if this is a special location (red tile), <code>false</code> otherwise
     */
    public Location(int id, String name, double xPercent, double yPercent, boolean isSpecial) {
      this.id = id;
      this.name = name;
      this.xPercent = xPercent;
      this.yPercent = yPercent;
      this.isSpecial = isSpecial;
    }

    /**
     * <p>Gets the location ID.</p>
     *
     * @return The unique identifier for the location
     */
    public int getId() {
      return id;
    }

    /**
     * <p>Sets the location ID.</p>
     *
     * @param id The unique identifier to set
     */
    public void setId(int id) {
      this.id = id;
    }

    /**
     * <p>Gets the location name.</p>
     *
     * @return The name of the location
     */
    public String getName() {
      return name;
    }

    /**
     * <p>Sets the location name.</p>
     *
     * @param name The name to set
     */
    public void setName(String name) {
      this.name = name;
    }

    /**
     * <p>Gets the x-coordinate as a percentage of map width.</p>
     *
     * @return The x-coordinate (0.0-1.0)
     */
    public double getXPercent() {
      return xPercent;
    }

    /**
     * <p>Gets the y-coordinate as a percentage of map height.</p>
     *
     * @return The y-coordinate (0.0-1.0)
     */
    public double getYPercent() {
      return yPercent;
    }

    /**
     * <p>Checks if this is a special location.</p>
     * <p>Special locations (red tiles) are places where tokens can be found.</p>
     *
     * @return <code>true</code> if this is a special location, <code>false</code> otherwise
     */
    public boolean isSpecial() {
      return isSpecial;
    }

    /**
     * <p>Sets whether this is a special location.</p>
     *
     * @param special <code>true</code> to make this a special location, <code>false</code> otherwise
     */
    public void setSpecial(boolean special) {
      isSpecial = special;
    }
  }

  /**
   * <p>Represents a connection between two locations.</p>
   * <p>Each connection defines a path from one location to another,
   * allowing players to travel between them.</p>
   */
  public static class Connection {
    private int fromId;
    private int toId;

    /**
     * <p>Constructs a new Connection instance.</p>
     * <p>Initializes a connection between the specified source and target locations.</p>
     *
     * @param fromId The ID of the source location
     * @param toId The ID of the target location
     */
    public Connection(int fromId, int toId) {
      this.fromId = fromId;
      this.toId = toId;
    }

    /**
     * <p>Gets the source location ID.</p>
     *
     * @return The ID of the source location
     */
    public int getFromId() {
      return fromId;
    }

    /**
     * <p>Gets the target location ID.</p>
     *
     * @return The ID of the target location
     */
    public int getToId() {
      return toId;
    }
  }

  private String name;
  private final List<Location> locations = new ArrayList<>();
  private final List<Connection> connections = new ArrayList<>();

  /**
   * <p>Gets the map name.</p>
   *
   * @return The name of the map
   */
  public String getName() {
    return name;
  }

  /**
   * <p>Sets the map name.</p>
   *
   * @param name The name to set
   */
  public void setName(String name) {
    this.name = name;
  }

  /**
   * <p>Gets the list of locations.</p>
   *
   * @return The list of {@link Location} objects
   */
  public List<Location> getLocations() {
    return locations;
  }

  /**
   * <p>Gets the list of connections.</p>
   *
   * @return The list of {@link Connection} objects
   */
  public List<Connection> getConnections() {
    return connections;
  }

  /**
   * <p>Adds a location to the map.</p>
   *
   * @param location The {@link Location} to add
   */
  public void addLocation(Location location) {
    locations.add(location);
  }

  /**
   * <p>Adds a connection to the map.</p>
   *
   * @param connection The {@link Connection} to add
   */
  public void addConnection(Connection connection) {
    connections.add(connection);
  }
}
