package edu.ntnu.idi.bidata.idatg2003mappe.app.laddergame.model;

import edu.ntnu.idi.bidata.idatg2003mappe.entity.die.Die;
import edu.ntnu.idi.bidata.idatg2003mappe.entity.player.Player;
import edu.ntnu.idi.bidata.idatg2003mappe.entity.player.PlayerFactory;
import edu.ntnu.idi.bidata.idatg2003mappe.map.board.BoardFactory;
import edu.ntnu.idi.bidata.idatg2003mappe.map.board.BoardLinear;

import java.util.*;

/**
 * Represents a Ladder Game (Snakes and Ladders) with configurable board setup.
 * This class handles the core game logic including board creation, player management,
 * and turn-based gameplay mechanics.
 *
 * @author Simen Gudbrandsen and Frikk Breadsroed
 * @version 1.0.0
 * @since 21.05.2025
 */
public class LadderGame {

  // Game configuration constants
  private static final String PLAYER_DATA_FILE = "src/main/resources/saves/playerData/Players.csv";
  private static final int BOARD_SIZE = 100;

  // Core game components
  private final BoardLinear board;
  private final List<Player> players;
  private final Die die;

  /**
   * Creates a new Ladder Game with the specified configuration.
   *
   * @param randomLadders true for random placement, false for classic setup
   * @throws IllegalStateException if game initialization fails
   */
  public LadderGame(boolean randomLadders) {
    this.die = new Die();

    // Create board using BoardFactory
    this.board = createBoard(randomLadders);
    this.players = loadPlayers();

    if (players.isEmpty()) {
      throw new IllegalStateException("No players could be loaded for the game");
    }
  }

  /**
   * Creates and configures the game board with tiles, ladders, and effects.
   *
   * @return configured BoardLinear instance
   */
  private BoardLinear createBoard(boolean randomLadders) {
    if (randomLadders) {
      return BoardFactory.createRandomLadderBoard();
    } else {
      return BoardFactory.createClassicLadderBoard();
    }
  }

  /**
   * Loads players from CSV file using PlayerFactory.
   *
   * @return list of players for the game
   */
  private List<Player> loadPlayers() {
    return PlayerFactory.createPlayersFromCSV(PLAYER_DATA_FILE, board);
  }

  // Public getters

  /**
   * Gets all players in the game.
   *
   * @return unmodifiable list of players
   */
  public List<Player> getPlayers() {
    return new ArrayList<>(players);
  }

  /**
   * Gets the game board.
   *
   * @return the board instance
   */
  public BoardLinear getBoard() {
    return board;
  }

  /**
   * Gets the game die.
   *
   * @return the die instance
   */
  public Die getDie() {
    return die;
  }

  /**
   * Gets the total number of tiles on the board.
   *
   * @return number of tiles
   */
  public int getNumberOfTiles() {
    return BOARD_SIZE;
  }

}
