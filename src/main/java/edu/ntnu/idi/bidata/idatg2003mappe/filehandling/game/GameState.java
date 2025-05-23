package edu.ntnu.idi.bidata.idatg2003mappe.filehandling.game;

import edu.ntnu.idi.bidata.idatg2003mappe.entity.player.Player;

import java.util.ArrayList;
import java.util.List;

/**
 * This class represents the state of a game that can be saved to a file and loaded later.
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
   * Inner class to represent a player's position on the board.
   */
  public static class PlayerPosition {
    private final String name;
    private final int id;
    private final int tileId;

    public PlayerPosition(String name, int id, int tileId) {
      this.name = name;
      this.id = id;
      this.tileId = tileId;
    }

    public String getName() {
      return name;
    }

    public int getId() {
      return id;
    }

    public int getTileId() {
      return tileId;
    }
  }

  /**
   * Default constructor.
   */
  public GameState() {
    this.playerPositions = new ArrayList<>();
  }

  /**
   * Constructor with player information.
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
   * Gets the index of the current player.
   *
   * @return The index of the current player.
   */
  public int getCurrentPlayerIndex() {
    return currentPlayerIndex;
  }

  /**
   * Sets the index of the current player.
   *
   * @param currentPlayerIndex The index of the current player.
   */
  public void setCurrentPlayerIndex(int currentPlayerIndex) {
    this.currentPlayerIndex = currentPlayerIndex;
  }

  /**
   * Gets whether the game uses random ladders.
   *
   * @return Whether the game uses random ladders.
   */
  public boolean isRandomLadders() {
    return randomLadders;
  }

  /**
   * Sets whether the game uses random ladders.
   *
   * @param randomLadders Whether the game uses random ladders.
   */
  public void setRandomLadders(boolean randomLadders) {
    this.randomLadders = randomLadders;
  }

  /**
   * Gets the list of player positions.
   *
   * @return The list of player positions.
   */
  public List<PlayerPosition> getPlayerPositions() {
    return playerPositions;
  }

  /**
   * Sets the list of player positions.
   *
   * @param playerPositions The list of player positions.
   */
  public void setPlayerPositions(List<PlayerPosition> playerPositions) {
    this.playerPositions = playerPositions;
  }

  /**
   * Sets the save time of the game state.
   *
   * @param saveTime The save time.
   */
  public void setSaveTime(String saveTime) {
    this.saveTime = saveTime;
  }
}