package edu.ntnu.idi.bidata.idatg2003mappe.markers;

/**
 * <p>A green gem marker in the Missing Diamond game.</p>
 * <p>Green gems are valuable items that players can collect during the game.
 * When a player finds a green gem, they receive its monetary value.</p>
 * <p>Green gems are the medium-value gems, providing a moderate reward
 * that falls between red gems (lowest) and yellow gems (highest).</p>
 *
 * @author Simen Gudbrandsen and Frikk Breadsroed
 * @version 0.0.1
 * @since 03.02.2025
 */
public class GreenGem extends Marker {
  private static final String TYPE = "GreenGem";
  private static final int VALUE = 300;

  /**
   * The actual reward amount when this gem is found.
   */
  private static final int REWARD = 4000;

  /**
   * <p>Constructor for the GreenGem class.</p>
   * <p>Initializes a new GreenGem marker with predefined type and value.
   * Green gems have a standard value of 300 currency units.</p>
   */
  public GreenGem() {
    super(TYPE, VALUE);
  }

  /**
   * {@inheritDoc}
   * <p>Green gems (emeralds) reward the player with 4000 currency units.</p>
   */
  @Override
  public TokenEffectResult getEffect() {
    return TokenEffectResult.depositMoney(REWARD);
  }
}
