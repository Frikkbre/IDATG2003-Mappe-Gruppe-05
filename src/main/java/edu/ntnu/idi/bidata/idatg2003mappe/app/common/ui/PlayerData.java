package edu.ntnu.idi.bidata.idatg2003mappe.app.common.ui;

/**
 * Helper class to store player data from CSV file.
 * This class is used to store the player data
 * read from the CSV file.
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

  public PlayerData(String name, int id, String color, int position) {
    this.name = name;
    this.id = id;
    this.color = color;
    this.position = position;
  }

  public String getName() {
    return name;
  }

  public int getId() {
    return id;
  }

  public String getColor() {
    return color;
  }

  public int getPosition() {
    return position;
  }
}