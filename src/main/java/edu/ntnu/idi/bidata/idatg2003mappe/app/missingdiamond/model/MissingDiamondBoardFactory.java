package edu.ntnu.idi.bidata.idatg2003mappe.app.missingdiamond.model;

import edu.ntnu.idi.bidata.idatg2003mappe.filehandling.map.MapConfig;
import edu.ntnu.idi.bidata.idatg2003mappe.map.Tile;
import edu.ntnu.idi.bidata.idatg2003mappe.map.board.BoardBranching;

import java.util.Optional;
import java.util.Set;
import java.util.stream.IntStream;

/**
 * <p>Factory class for creating Missing Diamond game boards.</p>
 *
 * <p>This factory handles the creation of game boards from either:</p>
 * <ul>
 *   <li>A {@link MapConfig} loaded from a JSON file</li>
 *   <li>A default board structure when no configuration is available</li>
 * </ul>
 *
 * @author Simen Gudbrandsen and Frikk Breadsroed
 * @version 1.0.0
 * @since 05.01.2026
 */
public class MissingDiamondBoardFactory {

  /**
   * <p>Creates a game board from a map configuration.</p>
   *
   * <p>This method processes the map configuration to create tiles and connections between
   * them according to the specified layout in the configuration.</p>
   *
   * @param mapConfig The map configuration containing location and connection information
   * @return A fully initialized branching board with all tiles and connections
   */
  public BoardBranching createBoardFromConfig(MapConfig mapConfig) {
    BoardBranching board = new BoardBranching();
    board.setBoardName(mapConfig.getName());

    // Create all tiles
    mapConfig.getLocations().forEach(location -> board.addTileToBoard(new Tile(location.getId())));

    // Add all connections
    mapConfig.getConnections().forEach(connection -> {
      Tile fromTile = board.getTileById(connection.getFromId());
      Tile toTile = board.getTileById(connection.getToId());

      if (fromTile != null && toTile != null) {
        board.connectTiles(fromTile, toTile);
      }
    });

    return board;
  }

  /**
   * <p>Creates a default game board with a predefined structure when no map configuration
   * is available.</p>
   *
   * <p>This method generates a simple network of connected tiles to serve as a fallback
   * when the map configuration cannot be loaded.</p>
   *
   * @param specialTileIdsSet Set to populate with default special tile IDs
   * @return A default branching board with a basic network of connected tiles
   */
  public BoardBranching createDefaultBoard(Set<Integer> specialTileIdsSet) {
    BoardBranching board = new BoardBranching();
    board.setBoardName("Default Missing Diamond Map");

    // Create basic city tiles
    IntStream.rangeClosed(1, 20)
        .mapToObj(Tile::new)
        .forEach(board::addTileToBoard);

    // Connect the tiles in a simple network
    // Starting tiles (Cairo and Tangiers)
    Tile cairo = board.getTileById(1);
    Tile tangiers = board.getTileById(2);

    // Create connections between cities
    board.connectTiles(cairo, board.getTileById(3));
    board.connectTiles(cairo, board.getTileById(4));
    board.connectTiles(tangiers, board.getTileById(5));
    board.connectTiles(tangiers, board.getTileById(6));

    // Add more connections to create a network
    IntStream.rangeClosed(3, 18).forEach(i -> {
      Tile current = board.getTileById(i);
      Tile next = board.getTileById(i + 1);

      if (current != null && next != null) {
        board.connectTiles(current, next);
      }

      // Add some cross-connections
      if (i % 3 == 0 && i + 4 <= 20) {
        Optional.ofNullable(board.getTileById(i + 4))
            .ifPresent(crossTile -> board.connectTiles(current, crossTile));
      }
    });

    // Add default special tiles for the default board
    if (specialTileIdsSet.isEmpty()) {
      specialTileIdsSet.add(5);
      specialTileIdsSet.add(10);
      specialTileIdsSet.add(15);
    }

    return board;
  }
}
