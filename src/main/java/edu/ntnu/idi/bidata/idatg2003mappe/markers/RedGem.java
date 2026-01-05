package edu.ntnu.idi.bidata.idatg2003mappe.markers;

/**
 * <p>A red gem marker in the Missing Diamond game.</p>
 * <p>Red gems are valuable items that players can collect during the game.
 * When a player finds a red gem, they receive its monetary value.</p>
 * <p>Red gems are the least valuable of the gem types, but they are
 * more common, providing smaller but more frequent rewards.</p>
 *
 * @author Simen Gudbrandsen and Frikk Breadsroed
 * @version 0.0.1
 * @since 03.02.2025
 */
public class RedGem extends Marker {
  private static final String TYPE = "RedGem";
  private static final int VALUE = 100;

  /**
   * The actual reward amount when this gem is found.
   */
  private static final int REWARD = 1000;

  /**
   * <p>Constructor for the RedGem class.</p>
   * <p>Initializes a new RedGem marker with predefined type and value.
   * Red gems have a standard value of 100 currency units.</p>
   */
  public RedGem() {
    super(TYPE, VALUE);
  }

  /**
   * {@inheritDoc}
   * <p>Red gems (rubies) reward the player with 1000 currency units.</p>
   */
  @Override
  public TokenEffectResult getEffect() {
    return TokenEffectResult.depositMoney(REWARD);
  }
}