package edu.ntnu.idi.bidata.idatg2003mappe.filehandling.game;

import edu.ntnu.idi.bidata.idatg2003mappe.entity.Player;

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
  private List<Player> players;
  private List<PlayerPosition> playerPositions;

  /**
   * Default constructor.
   */
  public GameState() {
  }

  /**
   * Constructor with all fields.
   *
   * @param currentPlayerIndex The index of the current player.
   * @param randomLadders Whether the game uses random ladders.
   * @param players The list of players.
   */
  public GameState(int currentPlayerIndex, boolean randomLadders, List<Player> players) {
    this.currentPlayerIndex = currentPlayerIndex;
    this.randomLadders = randomLadders;
    this.players = players;
  }

  public static class PlayerPosition {
    private String name;
    private int id;
    private int tileId;

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
   * Gets the list of players.
   *
   * @return The list of players.
   */
  public List<Player> getPlayers() {
    return players;
  }

  /**
   * Sets the list of players.
   *
   * @param players The list of players.
   */
  public void setPlayers(List<Player> players) {
    this.players = players;
  }

  public List<PlayerPosition> getPlayerPositions() {
    return playerPositions;
  }

  public void setPlayerPositions(List<PlayerPosition> playerPositions) {
    this.playerPositions = playerPositions;
  }
}