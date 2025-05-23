package edu.ntnu.idi.bidata.idatg2003mappe.filehandling.game;

import edu.ntnu.idi.bidata.idatg2003mappe.entity.player.Player;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>Represents the state of a game that can be saved to a file and loaded later.</p>
 * <p>This class encapsulates all the information needed to restore a game to its
 * previous state, including player positions, current player, and game configuration
 * options like whether random ladders are used.</p>
 * <p>The state includes:</p>
 * <ul>
 *   <li>The index of the current player</li>
 *   <li>Game configuration settings (e.g., randomLadders)</li>
 *   <li>Positions of all players on the board</li>
 *   <li>Timestamp of when the state was saved</li>
 * </ul>
 *
 * @author Simen Gudbrandsen and Frikk Breadsroed
 * @version 0.0.1
 * @since 12.04.2025
 */
public class GameState {
  private int currentPlayerIndex;
  private boolean randomLadders;
  private List<PlayerPosition> playerPositions;
  private String saveTime;

  /**
   * <p>Inner class to represent a player's position on the board.</p>
   * <p>This class stores the essential information about a player that needs to be saved,
   * including their name, ID, and the ID of the tile they are currently on.</p>
   */
  public static class PlayerPosition {
    private final String name;
    private final int id;
    private final int tileId;

    /**
     * <p>Constructs a new PlayerPosition instance.</p>
     * <p>Initializes a player position record with the specified properties.</p>
     *
     * @param name The player's name
     * @param id The player's unique identifier
     * @param tileId The ID of the tile the player is currently on
     */
    public PlayerPosition(String name, int id, int tileId) {
      this.name = name;
      this.id = id;
      this.tileId = tileId;
    }

    /**
     * <p>Gets the player's name.</p>
     *
     * @return The player's name
     */
    public String getName() {
      return name;
    }

    /**
     * <p>Gets the player's ID.</p>
     *
     * @return The player's unique identifier
     */
    public int getId() {
      return id;
    }

    /**
     * <p>Gets the ID of the tile the player is on.</p>
     *
     * @return The tile ID
     */
    public int getTileId() {
      return tileId;
    }
  }

  /**
   * <p>Default constructor.</p>
   * <p>Initializes a new empty game state with no player positions.</p>
   */
  public GameState() {
    this.playerPositions = new ArrayList<>();
  }

  /**
   * <p>Constructor with player information.</p>
   * <p>Initializes a game state with the specified current player index,
   * randomLadders setting, and a list of players. The constructor extracts
   * the necessary position information from each player object.</p>
   *
   * @param currentPlayerIndex The index of the current player
   * @param randomLadders Whether the game uses random ladders
   * @param players The list of {@link Player} objects
   */
  public GameState(int currentPlayerIndex, boolean randomLadders, List<Player> players) {
    this.currentPlayerIndex = currentPlayerIndex;
    this.randomLadders = randomLadders;
    this.playerPositions = new ArrayList<>();

    if (players != null) {
      for (Player player : players) {
        this.playerPositions.add(new PlayerPosition(
            player.getName(),
            player.getID(),
            player.getCurrentTile().getTileId()
        ));
      }
    }
  }

  /**
   * <p>Gets the index of the current player.</p>
   *
   * @return The index of the current player
   */
  public int getCurrentPlayerIndex() {
    return currentPlayerIndex;
  }

  /**
   * <p>Sets the index of the current player.</p>
   *
   * @param currentPlayerIndex The index of the current player
   */
  public void setCurrentPlayerIndex(int currentPlayerIndex) {
    this.currentPlayerIndex = currentPlayerIndex;
  }

  /**
   * <p>Gets whether the game uses random ladders.</p>
   *
   * @return <code>true</code> if the game uses random ladders, <code>false</code> otherwise
   */
  public boolean isRandomLadders() {
    return randomLadders;
  }

  /**
   * <p>Sets whether the game uses random ladders.</p>
   *
   * @param randomLadders <code>true</code> to use random ladders, <code>false</code> otherwise
   */
  public void setRandomLadders(boolean randomLadders) {
    this.randomLadders = randomLadders;
  }

  /**
   * <p>Gets the list of player positions.</p>
   *
   * @return The list of {@link PlayerPosition} objects
   */
  public List<PlayerPosition> getPlayerPositions() {
    return playerPositions;
  }

  /**
   * <p>Sets the list of player positions.</p>
   *
   * @param playerPositions The list of {@link PlayerPosition} objects
   */
  public void setPlayerPositions(List<PlayerPosition> playerPositions) {
    this.playerPositions = playerPositions;
  }

  /**
   * <p>Sets the save time of the game state.</p>
   * <p>This timestamp indicates when the game state was created or saved.</p>
   *
   * @param saveTime The save time as a string
   */
  public void setSaveTime(String saveTime) {
    this.saveTime = saveTime;
  }
}
