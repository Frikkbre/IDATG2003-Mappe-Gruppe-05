package edu.ntnu.idi.bidata.idatg2003mappe.app.laddergame.model;

import edu.ntnu.idi.bidata.idatg2003mappe.entity.die.Die;
import edu.ntnu.idi.bidata.idatg2003mappe.entity.player.Player;
import edu.ntnu.idi.bidata.idatg2003mappe.entity.player.PlayerFactory;
import edu.ntnu.idi.bidata.idatg2003mappe.map.board.BoardLinear;
import edu.ntnu.idi.bidata.idatg2003mappe.map.board.LadderGameBoardFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>Represents a Ladder Game (Snakes and Ladders) with configurable board setup.</p>
 * <p>This class handles the core game logic including board creation, player management,
 * and turn-based gameplay mechanics. It supports both classic and random ladder
 * configurations.</p>
 * <p>The game consists of a 100-tile board where players roll dice to advance.
 * Special ladder tiles move players forward or backward, while effect tiles
 * trigger special actions like skipping turns or returning to start.</p>
 *
 * <p>Represents a Ladder Game (Snakes and Ladders) with configurable board setup.</p>
 * <p>This class handles the core game logic including board creation, player management,
 * and turn-based gameplay mechanics. It supports both classic and random ladder
 * configurations.</p>
 * <p>The game consists of a 100-tile board where players roll dice to advance.
 * Special ladder tiles move players forward or backward, while effect tiles
 * trigger special actions like skipping turns or returning to start.</p>
 *
 * @version 1.0.0
 * @since 21.05.2025
 */
public class LadderGame {

  // Game configuration constants
  /**
   * User data directory for player saves (not in src/main/resources which is for bundled resources).
   */
  private static final String PLAYER_DATA_FILE = "data/saves/playerData/Players.csv";
  private static final int BOARD_SIZE = 100;

  // Core game components
  private final BoardLinear board;
  private final List<Player> players;
  private final Die die;

  /**
   * <p>Creates a new Ladder Game with the specified configuration.</p>
   * <p>Initializes the board, loads players, and sets up the game based on
   * whether a random or classic ladder layout is desired.</p>
   *
   * @param randomLadders <code>true</code> for random ladder placement, <code>false</code> for classic setup
   * @throws IllegalStateException If no players could be loaded for the game
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
   * <p>Creates and configures the game board with tiles, ladders, and effects.</p>
   * <p>Uses the {@link LadderGameBoardFactory} to create either a random or classic board layout.</p>
   *
   * @param randomLadders Whether to use random ladder placement
   * @return Fully configured {@link BoardLinear} instance
   */
  private BoardLinear createBoard(boolean randomLadders) {
    if (randomLadders) {
      return LadderGameBoardFactory.createRandomLadderBoard();
    } else {
      return LadderGameBoardFactory.createClassicLadderBoard();
    }
  }

  /**
   * <p>Loads players from CSV file using PlayerFactory.</p>
   * <p>Players are initialized with their starting positions on the board.</p>
   *
   * @return List of {@link Player} objects for the game
   */
  private List<Player> loadPlayers() {
    return PlayerFactory.createPlayersFromCSV(PLAYER_DATA_FILE, board);
  }

  // Public getters

  /**
   * <p>Gets all players in the game.</p>
   * <p>Returns a defensive copy to prevent external modification of the player list.</p>
   *
   * @return A new ArrayList containing all players
   */
  public List<Player> getPlayers() {
    return new ArrayList<>(players);
  }

  /**
   * <p>Gets the game board.</p>
   *
   * @return The {@link BoardLinear} instance used by this game
   */
  public BoardLinear getBoard() {
    return board;
  }

  /**
   * <p>Gets the game die.</p>
   * <p>This die is used for determining player movement distances.</p>
   *
   * @return The {@link Die} instance used for this game
   */
  public Die getDie() {
    return die;
  }

  /**
   * <p>Gets the total number of tiles on the board.</p>
   *
   * @return The number of tiles (100 for standard game)
   */
  public int getNumberOfTiles() {
    return BOARD_SIZE;
  }

}
