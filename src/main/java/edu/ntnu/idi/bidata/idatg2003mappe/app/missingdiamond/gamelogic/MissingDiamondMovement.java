package edu.ntnu.idi.bidata.idatg2003mappe.app.missingdiamond.gamelogic;

import edu.ntnu.idi.bidata.idatg2003mappe.map.Tile;
import java.util.*;

/**
 * This class handles the movement logic for the Missing Diamond game,
 * including calculating valid moves and special tile interactions.
 *
 * @author Your Name
 * @version 1.0.0
 * @since 22.05.2025
 */
public class MissingDiamondMovement {

  private final Set<Integer> specialTileIds;

  /**
   * Constructs a MissingDiamondMovement object with the specified special tile IDs.
   *
   * @param specialTileIds Set of tile IDs that are considered special (token locations)
   */
  public MissingDiamondMovement(Set<Integer> specialTileIds) {
    this.specialTileIds = specialTileIds;
  }

  /**
   * Gets all tiles that are reachable with a die roll, allowing stops at special tiles.
   * This allows players to stop at special tiles even if they're not at exactly N steps.
   *
   * @param startTile The starting tile
   * @param dieRoll The die roll value
   * @return Set of tiles that can be reached with the roll
   */
  public Set<Tile> getValidMoves(Tile startTile, int dieRoll) {
    // No valid moves if roll is invalid
    if (dieRoll <= 0) {
      return new HashSet<>();
    }

    Set<Tile> result = new HashSet<>();
    Set<Tile> visited = new HashSet<>();

    // Add starting tile to visited set
    visited.add(startTile);

    // Use breadth-first search to find all reachable tiles
    findReachableTiles(startTile, dieRoll, visited, result, 0);

    return result;
  }

  /**
   * Helper method for finding all reachable tiles, stopping at special tiles.
   * Uses breadth-first search to explore the board.
   *
   * @param currentTile The current tile in the search
   * @param maxSteps The maximum number of steps to take
   * @param visited Set of visited tiles to avoid cycles
   * @param result The set of result tiles
   * @param currentDepth The current search depth
   */
  private void findReachableTiles(Tile currentTile, int maxSteps,
                                  Set<Tile> visited, Set<Tile> result, int currentDepth) {
    // Skip starting tile (depth 0)
    if (currentDepth > 0) {
      // For special tiles, always add to result regardless of depth
      if (isSpecialTile(currentTile)) {
        result.add(currentTile);
        // Continue search from special tiles only if we haven't reached max steps
        if (currentDepth >= maxSteps) {
          return;
        }
      }
      // For normal tiles, only add if we've used exactly maxSteps
      else if (currentDepth == maxSteps) {
        result.add(currentTile);
        return;
      }
    }

    // Continue search if we haven't reached max steps
    if (currentDepth < maxSteps) {
      // Explore each unvisited neighbor
      for (Tile neighbor : currentTile.getNextTiles()) {
        if (!visited.contains(neighbor)) {
          visited.add(neighbor);
          findReachableTiles(neighbor, maxSteps, visited, result, currentDepth + 1);
        }
      }
    }
  }

  /**
   * Determines if a tile is a special tile (has a token that can be flipped).
   *
   * @param tile The tile to check
   * @return True if the tile is a special tile
   */
  public boolean isSpecialTile(Tile tile) {
    return specialTileIds.contains(tile.getTileId());
  }

  /**
   * Alternative implementation that returns all tiles along paths up to N steps,
   * highlighting special tiles as important stopping points.
   *
   * @param startTile The starting tile
   * @param dieRoll The die roll value
   * @return Map of tiles to their path length from start
   */
  public Map<Tile, Integer> getAllReachableTiles(Tile startTile, int dieRoll) {
    Map<Tile, Integer> distances = new HashMap<>();
    Set<Tile> visited = new HashSet<>();
    Queue<Tile> queue = new LinkedList<>();

    // Initialize with start tile
    queue.add(startTile);
    visited.add(startTile);
    distances.put(startTile, 0);

    while (!queue.isEmpty()) {
      Tile current = queue.poll();
      int distance = distances.get(current);

      // Stop exploring this path if we've reached maximum steps
      if (distance >= dieRoll) {
        continue;
      }

      // Explore neighbors
      for (Tile neighbor : current.getNextTiles()) {
        if (!visited.contains(neighbor)) {
          visited.add(neighbor);
          distances.put(neighbor, distance + 1);
          queue.add(neighbor);
        }
      }
    }

    // Remove start tile from results
    distances.remove(startTile);

    return distances;
  }
}