package edu.ntnu.idi.bidata.idatg2003mappe.app.missingdiamond.gamelogic;

import edu.ntnu.idi.bidata.idatg2003mappe.entity.player.Player;
import edu.ntnu.idi.bidata.idatg2003mappe.map.Tile;

import java.util.HashSet;
import java.util.Set;

/**
 * <p>Calculates valid movement options for players in the Missing Diamond game.</p>
 *
 * <p>This class handles the complex logic of determining which tiles a player can
 * move to based on their die roll and special tile rules. Special tiles allow
 * players to stop before reaching their exact die roll distance.</p>
 *
 * @author Simen Gudbrandsen and Frikk Breadsroed
 * @version 1.0.0
 * @since 05.01.2026
 */
public class MovementCalculator {

  private final Set<Integer> specialTileIds;

  /**
   * <p>Creates a new MovementCalculator with the specified special tile IDs.</p>
   *
   * @param specialTileIds Set of tile IDs that are considered special (allow early stopping)
   */
  public MovementCalculator(Set<Integer> specialTileIds) {
    this.specialTileIds = specialTileIds != null ? specialTileIds : new HashSet<>();
  }

  /**
   * <p>Gets all possible destination tiles for a player based on their die roll.</p>
   *
   * <p>This method calculates all valid tiles that the player can move to, considering:</p>
   * <ul>
   *   <li>The player must move exactly their die roll value (for normal tiles)</li>
   *   <li>The player may stop early on special tiles if encountered within the roll distance</li>
   * </ul>
   *
   * @param player      The player to calculate moves for
   * @param currentRoll The die roll value
   * @return A set of tiles that the player can legally move to
   */
  public Set<Tile> getPossibleMoves(Player player, int currentRoll) {
    Set<Tile> possibleMoves = new HashSet<>();
    if (currentRoll < 1 || player == null || player.getCurrentTile() == null) {
      return possibleMoves;
    }

    Tile startTile = player.getCurrentTile();
    Set<Tile> visitedForThisCall = new HashSet<>();

    // Add the start tile itself to visited so the recursion starts by exploring its neighbors
    visitedForThisCall.add(startTile);

    // Call the recursive helper, starting at depth 0 for the player's current tile.
    recursiveMoveFinder(startTile, currentRoll, visitedForThisCall, possibleMoves, 0);

    return possibleMoves;
  }

  /**
   * <p>Validates if a move to a destination tile is valid.</p>
   *
   * @param player          The player attempting to move
   * @param destinationTile The target tile
   * @param currentRoll     The die roll value
   * @return {@code true} if the move is valid, {@code false} otherwise
   */
  public boolean isValidMove(Player player, Tile destinationTile, int currentRoll) {
    if (destinationTile == null) {
      return false;
    }
    Set<Tile> validMoves = getPossibleMoves(player, currentRoll);
    return validMoves.contains(destinationTile);
  }

  /**
   * <p>Determines if a tile is a special tile where players can optionally stop.</p>
   *
   * @param tile The tile to check
   * @return {@code true} if the tile is designated as special, {@code false} otherwise
   */
  public boolean isSpecialTile(Tile tile) {
    if (tile == null) {
      return false;
    }
    return specialTileIds.contains(tile.getTileId());
  }

  /**
   * <p>Recursive helper method to find all valid destination tiles for a player's move.</p>
   *
   * <p>This method explores the board recursively to identify all tiles that a player
   * can legally move to based on their die roll and the special tile rules.</p>
   *
   * @param currentTile   The current tile being explored in the recursion
   * @param dieRoll       The original die roll value
   * @param visitedInCall Set of tiles already visited in this recursive call branch
   * @param resultOutput  Set to be populated with valid destination tiles
   * @param currentDepth  Current recursion depth (steps taken so far)
   */
  private void recursiveMoveFinder(Tile currentTile, int dieRoll, Set<Tile> visitedInCall,
                                   Set<Tile> resultOutput, int currentDepth) {

    // Logic for adding to resultOutput (based on currentTile, which is reached at currentDepth)
    if (currentDepth > 0) { // Only consider tiles reached after at least one step
      if (isSpecialTile(currentTile)) {
        resultOutput.add(currentTile); // Special tiles are valid stops if reached within dieRoll.
      } else { // Not a special tile
        if (currentDepth == dieRoll) {
          resultOutput.add(currentTile); // Non-special tiles only valid if exactly at dieRoll.
        }
      }
    }

    // Stop condition for recursion: if current depth has reached die roll, no more steps can be taken.
    if (currentDepth >= dieRoll) {
      return;
    }

    // Recursive step: explore neighbors
    currentTile.getNextTiles().stream()
        .filter(neighbor -> !visitedInCall.contains(neighbor))
        .peek(visitedInCall::add) // Mark as visited
        .forEach(neighbor -> recursiveMoveFinder(neighbor, dieRoll, visitedInCall, resultOutput, currentDepth + 1));
  }
}
