package edu.ntnu.idi.bidata.idatg2003mappe.app.common.ui;

/**
 * <p>Helper class to store player data from CSV file.</p>
 * <p>This immutable class encapsulates player information read from external data sources
 * and provides access methods to retrieve the stored properties.</p>
 *
 * @author Simen Gudbrandsen and Frikk Breadsroed
 * @version 0.0.1
 * @since 22.05.2025
 */
public class PlayerData {
  private final String name;
  private final int id;
  private final String color;
  private final int position;

  /**
   * <p>Constructs a new PlayerData instance with the specified properties.</p>
   * <p>All fields are final and can only be set through this constructor.</p>
   *
   * @param name     The player's name
   * @param id       The player's unique identifier
   * @param color    The player's color representation
   * @param position The player's initial position on the board
   */
  public PlayerData(String name, int id, String color, int position) {
    this.name = name;
    this.id = id;
    this.color = color;
    this.position = position;
  }

  /**
   * <p>Returns the player's name.</p>
   *
   * @return The player's name as a String
   */
  public String getName() {
    return name;
  }

  /**
   * <p>Returns the player's unique identifier.</p>
   *
   * @return The player's ID as an integer
   */
  public int getId() {
    return id;
  }

  /**
   * <p>Returns the player's color.</p>
   * <p>The color is typically represented as a CSS color string.</p>
   *
   * @return The player's color as a String
   */
  public String getColor() {
    return color;
  }

  /**
   * <p>Returns the player's initial position on the board.</p>
   *
   * @return The player's position as an integer
   */
  public int getPosition() {
    return position;
  }
}
