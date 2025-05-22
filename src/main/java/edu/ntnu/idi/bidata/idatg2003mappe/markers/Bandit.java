package edu.ntnu.idi.bidata.idatg2003mappe.markers;

import edu.ntnu.idi.bidata.idatg2003mappe.entity.player.Player;
import edu.ntnu.idi.bidata.idatg2003mappe.map.Tile;

/**
 * Represents a Bandit marker in the Missing Diamond game.
 * Bandits can interact with players, potentially causing negative effects.
 *
 * @author Simen Gudbrandsen and Frikk Breadsroed
 * @version 0.0.2
 * @since 03.02.2025
 */
public class Bandit extends Marker {
  /**
   * The default penalty when a player encounters a bandit.
   */
  private static final int DEFAULT_PENALTY = 2;

  /**
   * The amount of steps the player will be moved back.
   */
  private final int penaltySteps;

  /**
   * Constructs a Bandit marker with default penalty.
   */
  public Bandit() {
    this(DEFAULT_PENALTY);
  }

  /**
   * Constructs a Bandit marker with a specific penalty.
   *
   * @param penaltySteps The number of steps the player will be moved back
   * @throws IllegalArgumentException if penalty steps is negative
   */
  public Bandit(int penaltySteps) {
    super("Bandit", 0);
    if (penaltySteps < 0) {
      throw new IllegalArgumentException("Penalty steps cannot be negative");
    }
    this.penaltySteps = penaltySteps;
  }

  /**
   * Applies the bandit's penalty to a player.
   * Moves the player back a specified number of steps.
   *
   * @param player The player encountering the bandit
   * @param currentTile The tile where the bandit is located
   * @return A description of the bandit's action
   */
  public String interact(Player player, Tile currentTile) {
    // Get the tile to move back to
    Tile penaltyTile = getPenaltyTile(currentTile);

    // Place the player on the penalty tile
    Tile oldTile = player.getCurrentTile();
    player.placePlayer(penaltyTile);

    return String.format("Bandit encountered! %s moved back from tile %d to tile %d",
        player.getName(),
        oldTile.getTileId(),
        penaltyTile.getTileId());
  }

  /**
   * Calculates the tile to move back to based on the current tile and penalty steps.
   *
   * @param currentTile The current tile of the player
   * @return The tile to move the player back to
   */
  private Tile getPenaltyTile(Tile currentTile) {
    Tile penaltyTile = currentTile;
    for (int i = 0; i < penaltySteps; i++) {
      if (penaltyTile.getPreviousTile() != null) {
        penaltyTile = penaltyTile.getPreviousTile();
      } else {
        // If we can't go back further, stay on the current tile
        break;
      }
    }
    return penaltyTile;
  }

  /**
   * Gets the number of penalty steps for this bandit.
   *
   * @return The number of steps the player will be moved back
   */
  public int getPenaltySteps() {
    return penaltySteps;
  }

  @Override
  public String toString() {
    return String.format("Bandit (Penalty: %d steps)", penaltySteps);
  }
}