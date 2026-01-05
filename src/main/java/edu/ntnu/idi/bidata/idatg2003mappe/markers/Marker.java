package edu.ntnu.idi.bidata.idatg2003mappe.markers;

import edu.ntnu.idi.bidata.idatg2003mappe.map.Tile;

/**
 * <p>Main superclass of the marker classes.</p>
 * <p>This class is used to represent a marker on the map. Markers are items that
 * can be placed on tiles and discovered by players during gameplay.</p>
 * <p>Markers have various properties:</p>
 * <ul>
 *   <li>Type - identifies the kind of marker</li>
 *   <li>Value - the monetary worth of the marker</li>
 *   <li>Location - the tile where the marker is placed</li>
 *   <li>Reveal status - whether the marker has been discovered</li>
 * </ul>
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
   * <p>Constructor for the Marker class.</p>
   * <p>Initializes a new marker with the specified type and value.
   * The marker starts hidden (not revealed) and without a location.</p>
   *
   * @param type  The type of marker (e.g., "Diamond", "RedGem")
   * @param value The monetary value of the marker
   */
  public Marker(String type, int value) {
    this.type = type;
    this.value = value;
    this.isRevealed = false;
  }

  /**
   * <p>Gets the type of marker.</p>
   * <p>Returns the string identifier for this marker type, which
   * can be used to determine its behavior and appearance.</p>
   *
   * @return The type of marker
   */
  public String getType() {
    return type;
  }

  /**
   * <p>Gets the value of the marker.</p>
   * <p>Returns the monetary worth of this marker, which may be
   * awarded to players when they discover it.</p>
   *
   * @return The value of the marker in currency units
   */
  public int getValue() {
    return value;
  }

  /**
   * <p>Gets the location of the marker.</p>
   * <p>Returns the tile where this marker is currently placed,
   * or <code>null</code> if it's not on the board.</p>
   *
   * @return The {@link Tile} where the marker is located, or <code>null</code>
   */
  public Tile getLocation() {
    return location;
  }

  /**
   * <p>Sets the location of the marker.</p>
   * <p>Places this marker on the specified tile. This is used during
   * game setup and when markers are moved during gameplay.</p>
   *
   * @param location The new {@link Tile} location
   */
  public void setLocation(Tile location) {
    this.location = location;
  }

  /**
   * <p>Checks if the marker is revealed.</p>
   * <p>Returns whether this marker has been discovered by a player.
   * Revealed markers typically have different behaviors than hidden ones.</p>
   *
   * @return <code>true</code> if the marker is revealed, <code>false</code> otherwise
   */
  public boolean isRevealed() {
    return isRevealed;
  }

  /**
   * <p>Reveals the marker.</p>
   * <p>Sets the marker's state to revealed, typically called when a
   * player discovers the marker during gameplay.</p>
   */
  public void reveal() {
    this.isRevealed = true;
  }

  /**
   * <p>Hides the marker.</p>
   * <p>Sets the marker's state to hidden, which may be used when
   * resetting the game or for certain gameplay mechanics.</p>
   */
  public void hide() {
    this.isRevealed = false;
  }

  /**
   * <p>Removes the marker from its current location.</p>
   * <p>Clears the marker's location, effectively removing it from
   * the board. This is typically called when a marker is collected.</p>
   */
  public void removeFromLocation() {
    this.location = null;
  }

  /**
   * <p>Gets the effect that occurs when this marker is revealed.</p>
   * <p>Each marker type defines its own effect, which may include:
   * money changes, inventory additions, or special game state changes.</p>
   *
   * @return A {@link TokenEffectResult} describing the effect of revealing this marker
   */
  public abstract TokenEffectResult getEffect();
}
