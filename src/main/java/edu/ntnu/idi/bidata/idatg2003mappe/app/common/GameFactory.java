package edu.ntnu.idi.bidata.idatg2003mappe.app.common;

import edu.ntnu.idi.bidata.idatg2003mappe.app.laddergame.model.LadderGame;
import edu.ntnu.idi.bidata.idatg2003mappe.app.missingdiamond.model.MissingDiamond;

/**
 * Factory class for creating different types of board games.
 *
 * @author Simen Gudbrandsen and Frikk Breadsroed
 * @version 0.0.1
 * @since 21.05.2025
 */
public class GameFactory {

  /**
   * Game types supported by the factory.
   */
  public enum GameType {
    LADDER_GAME,
    MISSING_DIAMOND
  }

  /**
   * Creates a game of the specified type with the given number of players.
   *
   * @param type The type of game to create.
   * @param numberOfPlayers The number of players.
   * @return The created game.
   */
  public static Object createGame(GameType type, int numberOfPlayers) {
    switch (type) {
      case LADDER_GAME:
        return new LadderGame(false);
      case MISSING_DIAMOND:
        return new MissingDiamond(numberOfPlayers);
      default:
        throw new IllegalArgumentException("Unknown game type: " + type);
    }
  }

  /**
   * Creates a game of the specified type using player data from a CSV file.
   *
   * @param type The type of game to create.
   * @return The created game.
   */
  public static Object createGameFromCSV(GameType type) {
    switch (type) {
      case LADDER_GAME:
        return new LadderGame(false);
      case MISSING_DIAMOND:
        return new MissingDiamond();
      default:
        throw new IllegalArgumentException("Unknown game type: " + type);
    }
  }

  /**
   * Creates a random ladder game.
   *
   * @param numberOfPlayers The number of players.
   * @return A LadderGame with random ladders.
   */
  public static LadderGame createRandomLadderGame(int numberOfPlayers) {
    return new LadderGame(true);
  }

  /**
   * Creates a missing diamond game with a custom map.
   *
   * @param numberOfPlayers The number of players.
   * @param mapFilePath The path to the map file.
   * @return A MissingDiamond game with the specified map.
   */
  public static MissingDiamond createCustomMissingDiamondGame(int numberOfPlayers, String mapFilePath) {
    return new MissingDiamond(numberOfPlayers, mapFilePath);
  }
}