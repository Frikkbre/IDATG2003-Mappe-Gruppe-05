package edu.ntnu.idi.bidata.idatg2003mappe.movement;

import edu.ntnu.idi.bidata.idatg2003mappe.entity.player.Player;
import edu.ntnu.idi.bidata.idatg2003mappe.map.Tile;
import edu.ntnu.idi.bidata.idatg2003mappe.markers.Marker;

/**
 * Action for special tiles in the Missing Diamond game.
 *
 * @author Simen Gudbrandsen and Frikk Breadsroed
 * @version 0.1.0
 * @since 22.05.2025
 */
public class MissingDiamondAction implements TileAction {
  private final Tile currentTile;
  private final String actionType;
  private Marker marker;

  /**
   * Constructor for the MissingDiamondAction class.
   *
   * @param currentTile The tile where the action occurs
   * @param actionType The type of action to perform
   */
  public MissingDiamondAction(Tile currentTile, String actionType) {
    this.currentTile = currentTile;
    this.actionType = actionType;
  }

  /**
   * Constructor for the MissingDiamondAction class with a marker.
   *
   * @param currentTile The tile where the action occurs
   * @param actionType The type of action to perform
   * @param marker The marker involved in the action
   */
  public MissingDiamondAction(Tile currentTile, String actionType, Marker marker) {
    this.currentTile = currentTile;
    this.actionType = actionType;
    this.marker = marker;
  }

  @Override
  public void performAction(Player player) {
    switch (actionType) {
      case "RevealMarker":
        if (marker != null) {
          marker.reveal();
        }
        break;
      case "PurchaseMarker":
        // This would require the banker for actual implementation
        System.out.println("You have opened a marker!");
        break;
      case "Transport":
        // This would require the banker for actual implementation
        System.out.println("You have been transported to a new tile!");
        break;
      case "Diamond":
        // Found the diamond - win the game
        System.out.println("Congratulations! You found the diamond! Get back to the start tile!");
        break;
      case "Bandit":
        // Bandit takes all your markers
        System.out.println("A bandit has stolen all your markers!");
        break;
      case "Visa":
        // When a player reveals a Visa, it allows free airplane travel
        System.out.println("You found a Visa! You can now travel by airplane for free.");
        break;
      default:
        System.out.println("Unknown action type: " + actionType);
        break;
    }
  }

  /**
   * Gets the current tile for this action.
   *
   * @return The current tile
   */
  public Tile getCurrentTile() {
    return currentTile;
  }

  /**
   * Gets the action type.
   *
   * @return The action type
   */
  public String getActionType() {
    return actionType;
  }

  /**
   * Gets the marker for this action.
   *
   * @return The marker
   */
  public Marker getMarker() {
    return marker;
  }
}