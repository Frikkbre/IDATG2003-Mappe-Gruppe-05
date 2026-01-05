package edu.ntnu.idi.bidata.idatg2003mappe.markers;

/**
 * <p>Represents a blank marker in the Missing Diamond game.</p>
 * <p>Blank markers serve as "decoys" that have no special effect when discovered.
 * They are distributed among the valuable markers to create uncertainty and risk
 * when players decide whether to investigate a location.</p>
 * <p>Finding a blank marker typically ends the player's turn without providing
 * any benefit, making marker investigation a risk-reward decision.</p>
 *
 * @author Simen Gudbrandsen and Frikk Breadsroed
 * @version 0.0.1
 * @since 23.05.2025
 */
public class BlankMarker extends Marker {
  private static final String TYPE = "Blank";
  private static final int VALUE = 0;

  /**
   * <p>Constructor for the BlankMarker class.</p>
   * <p>Initializes a new BlankMarker with predefined type and zero value.
   * Blank markers provide no monetary reward when discovered.</p>
   */
  public BlankMarker() {
    super(TYPE, VALUE);
  }

  /**
   * {@inheritDoc}
   * <p>Blank markers have no effect when revealed.</p>
   */
  @Override
  public TokenEffectResult getEffect() {
    return TokenEffectResult.noEffect();
  }
}
