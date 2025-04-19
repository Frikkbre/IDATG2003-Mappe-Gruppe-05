package edu.ntnu.idi.bidata.idatg2003mappe.map.board;

import edu.ntnu.idi.bidata.idatg2003mappe.map.board.BoardLinear;
import edu.ntnu.idi.bidata.idatg2003mappe.map.Tile;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Factory class for creating different types of game boards.
 *
 * @author Simen Gudbrandsen and Frikk Breadsroed
 * @version 0.0.1
 * @since 19.04.2025
 */
public class BoardFactory {
  private static final String BOARDS_DIRECTORY = "src/main/resources/boards";

  /**
   * Creates a classic ladder game board with predefined ladders.
   */
  public static BoardLinear createClassicLadderBoard() {
    BoardLinear board = new BoardLinear();
    Tile[] tiles = new Tile[100];

    // Create and connect all tiles
    for (int i = 0; i < 100; i++) {
      tiles[i] = new Tile(i + 1);
      board.addTileToBoard(tiles[i]);
    }
    for (int i = 0; i < 99; i++) {
      tiles[i].setNextTile(tiles[i + 1]);
    }

    // Add classic ladders (up)
    tiles[1].setDestinationTile(tiles[37]); // 2 -> 38
    tiles[4].setDestinationTile(tiles[14]); // 5 -> 15
    tiles[9].setDestinationTile(tiles[31]); // 10 -> 32
    tiles[21].setDestinationTile(tiles[42]); // 22 -> 43
    tiles[28].setDestinationTile(tiles[84]); // 29 -> 85

    // Add snakes (down)
    tiles[17].setDestinationTile(tiles[7]); // 18 -> 8
    tiles[61].setDestinationTile(tiles[11]); // 62 -> 12
    tiles[87].setDestinationTile(tiles[36]); // 88 -> 37

    return board;
  }

  /**
   * Lists available board files from the boards directory.
   */
  public static List<String> getAvailableBoardFiles() {
    List<String> boardFiles = new ArrayList<>();
    File boardsDir = new File(BOARDS_DIRECTORY);

    if (boardsDir.exists() && boardsDir.isDirectory()) {
      File[] files = boardsDir.listFiles((dir, name) -> name.toLowerCase().endsWith(".json"));
      if (files != null) {
        for (File file : files) {
          boardFiles.add(file.getName());
        }
      }
    }

    return boardFiles;
  }
}