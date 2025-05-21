package edu.ntnu.idi.bidata.idatg2003mappe.app.laddergame.model;

import edu.ntnu.idi.bidata.idatg2003mappe.app.common.observer.BoardGameObserver;
import edu.ntnu.idi.bidata.idatg2003mappe.entity.die.Die;
import edu.ntnu.idi.bidata.idatg2003mappe.entity.player.Player;
import edu.ntnu.idi.bidata.idatg2003mappe.entity.player.PlayerFactory;
import edu.ntnu.idi.bidata.idatg2003mappe.map.board.BoardLinear;
import edu.ntnu.idi.bidata.idatg2003mappe.map.Tile;
import edu.ntnu.idi.bidata.idatg2003mappe.movement.LadderAction;
import edu.ntnu.idi.bidata.idatg2003mappe.movement.TileActionFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

/**
 * Class to represent a game of Ladder.
 * The game consists of a board, a number of players, a dice and a number of tiles.
 * The game is played by the players taking turns to roll the dice and move their markers on the board.
 * The game is won by the first player to reach the last tile on the board.
 *
 * @author Simen Gudbrandsen and Frikk Breadsroed
 * @version 0.4
 * @since 21.05.2025
 */

public class LadderGame {

  private static final String PLAYER_DATA_FILE = "src/main/resources/saves/playerData/Players.csv";
  private final BoardLinear board;
  private final List<Player> players;
  private Map<Integer, String> tileEffects = new HashMap<>();
  private final Die die;
  private final int numberOfTiles;
  private final boolean randomLadders;
  private Player winner;
  private boolean gameFinished = false;

  // Observer pattern support
  private final List<BoardGameObserver> observers = new ArrayList<>();

  public LadderGame(boolean randomLadders) {
    System.out.println("Starting Ladder Game with players from file.");

    this.randomLadders = randomLadders;
    this.numberOfTiles = 100;
    this.board = createBoard(numberOfTiles);
    this.players = readPlayersFromCSV();
    this.die = new Die();
  }

  /**
   * Adds an observer to the game.
   *
   * @param observer The observer to add.
   */
  public void addObserver(BoardGameObserver observer) {
    observers.add(observer);
  }

  /**
   * Removes an observer from the game.
   *
   * @param observer The observer to remove.
   */
  public void removeObserver(BoardGameObserver observer) {
    observers.remove(observer);
  }

  /**
   * Notifies observers that a player has moved.
   *
   * @param player The player who moved.
   * @param fromTile The tile the player moved from.
   * @param toTile The tile the player moved to.
   */
  private void notifyPlayerMoved(Player player, Tile fromTile, Tile toTile) {
    for (BoardGameObserver observer : observers) {
      observer.onPlayerMoved(player, fromTile, toTile);
    }
  }

  /**
   * Notifies observers that a die has been rolled.
   *
   * @param player The player who rolled.
   * @param rollValue The value rolled.
   */
  private void notifyDieRolled(Player player, int rollValue) {
    for (BoardGameObserver observer : observers) {
      observer.onDieRolled(player, rollValue);
    }
  }

  /**
   * Notifies observers that the game has ended.
   *
   * @param winner The winning player.
   */
  private void notifyGameEnded(Player winner) {
    for (BoardGameObserver observer : observers) {
      observer.onGameEnded(winner);
    }
  }

  /**
   * Notifies observers that the turn has changed.
   *
   * @param newCurrentPlayer The new current player.
   */
  private void notifyTurnChanged(Player newCurrentPlayer) {
    for (BoardGameObserver observer : observers) {
      observer.onTurnChanged(newCurrentPlayer);
    }
  }

  protected BoardLinear createBoard(int numberOfTiles) {
    BoardLinear board = new BoardLinear();
    Tile[] tiles = new Tile[numberOfTiles];

    for (int i = 0; i < numberOfTiles; i++) {
      tiles[i] = new Tile(i + 1);
      board.addTileToBoard(tiles[i]);
    }

    for (int i = 0; i < numberOfTiles - 1; i++) {
      tiles[i].setNextTile(tiles[i + 1]);
    }

    if (randomLadders) {
      generateRandomLadders(tiles);
      setupTileEffects(board, tiles);
    } else {
      setClassicLadders(tiles);
      setupTileEffects(board, tiles);
    }

    return board;
  }

  /**
   * Method to set the classic ladders and snakes on the board.
   * The ladders and snakes are hardcoded to the board. For a classic game of Ladder,
   */

  private void setClassicLadders(Tile[] tiles) {
    if (numberOfTiles >= 100) {
      // Create ladders using the TileActionFactory
      TileActionFactory.createLadderAction(tiles[2], tiles[38]);
      TileActionFactory.createLadderAction(tiles[5], tiles[15]);
      TileActionFactory.createLadderAction(tiles[10], tiles[32]);
      TileActionFactory.createLadderAction(tiles[22], tiles[43]);
      TileActionFactory.createLadderAction(tiles[29], tiles[85]);
      TileActionFactory.createLadderAction(tiles[52], tiles[67]);
      TileActionFactory.createLadderAction(tiles[73], tiles[92]);
      TileActionFactory.createLadderAction(tiles[80], tiles[99]);

      // Create snakes (also using ladder action but with downward movement)
      TileActionFactory.createLadderAction(tiles[18], tiles[8]);
      TileActionFactory.createLadderAction(tiles[62], tiles[12]);
      TileActionFactory.createLadderAction(tiles[55], tiles[35]);
      TileActionFactory.createLadderAction(tiles[65], tiles[61]);
      TileActionFactory.createLadderAction(tiles[88], tiles[37]);
      TileActionFactory.createLadderAction(tiles[94], tiles[74]);
      TileActionFactory.createLadderAction(tiles[98], tiles[80]);
    }
  }

  /**
   * sets hardcoded tile effects for the game.
   * Does so by adding the tile number and the effect to a map.
   */
  private void setupTileEffects(BoardLinear board, Tile[] tiles) {
    tileEffects.put(13, "skipTurn");
    tileEffects.put(25, "skipTurn");
    tileEffects.put(57, "skipTurn");
    tileEffects.put(70, "skipTurn");
    tileEffects.put(96, "skipTurn");

    tileEffects.put(45, "backToStart");

    // Apply effects directly to the tiles array
    for (Map.Entry<Integer, String> entry : tileEffects.entrySet()) {
      int tileId = entry.getKey();
      String effect = entry.getValue();
      // Use the tiles array directly since we have it
      if (tileId > 0 && tileId <= tiles.length) {
        tiles[tileId - 1].setEffect(effect);
      }
    }
  }

  /**
   * Method to generate random ladders and snakes on the board.
   * The ladders and snakes are randomly generated on the board.
   * The number of ladders and snakes are hardcoded for now.
   */
  private void generateRandomLadders(Tile[] tiles) {
    Random random = new Random();
    int numLadders = 8; // Number of ladders and snakes
    int maxAttempts = 50;

    // Clear existing ladders first
    for (Tile tile : tiles) {
      tile.setDestinationTile(null);
    }

    // Track used start tiles to prevent multiple ladders from same tile
    Set<Integer> usedStartTiles = new HashSet<>();

    for (int i = 0; i < numLadders; i++) {
      int attempts = 0;
      while (attempts < maxAttempts) {
        // Choose a random start tile, avoiding first and last few tiles
        int startIndex = random.nextInt(tiles.length - 10) + 5;

        // Ensure this tile hasn't been used before
        if (usedStartTiles.contains(startIndex)) {
          attempts++;
          continue;
        }

        // Randomly decide upward or downward ladder
        if (random.nextBoolean()) {
          // Upward ladder
          int endIndex = startIndex + random.nextInt(10) + 5;
          if (endIndex < tiles.length &&
              tiles[startIndex].getDestinationTile() == null &&
              tiles[endIndex].getDestinationTile() == null) {

            TileActionFactory.createLadderAction(tiles[startIndex], tiles[endIndex]);
            usedStartTiles.add(startIndex);
            break;
          }
        } else {
          // Downward ladder (snake)
          int endIndex = startIndex - random.nextInt(10) - 5;
          if (endIndex > 0 &&
              tiles[startIndex].getDestinationTile() == null &&
              tiles[endIndex].getDestinationTile() == null) {

            TileActionFactory.createLadderAction(tiles[startIndex], tiles[endIndex]);
            usedStartTiles.add(startIndex);
            break;
          }
        }
        attempts++;
      }
    }
  }

  /**
   * Method to create a list of players for the game from the CSV file.
   *
   * @return the list of players
   */
  protected List<Player> readPlayersFromCSV() {
    // Use PlayerFactory to create players from CSV
    return PlayerFactory.createPlayersFromCSV(PLAYER_DATA_FILE, board);
  }


  /**
   * Method to play the game.
   * The game is played by the players taking turns
   * to roll the dice and move their markers on the board.
   * If a player lands on a tile with a ladder, the player
   * is moved to the destination tile of the ladder.
   * The game is won by the first player to
   * reach the last tile on the board.
   */
  void playGame() {
    boolean hasWon = false;
    int indexCurrentPlayer = 0;
    int roll = 0;

    while (!hasWon) {
      Player currentPlayer = players.get(indexCurrentPlayer);
      System.out.println("Player " + (indexCurrentPlayer + 1) + " turn.");

      roll = die.rollDie();
      // Notify observers about die roll
      notifyDieRolled(currentPlayer, roll);

      System.out.println("Die : die rolled: " + roll);

      Tile oldTile = currentPlayer.getCurrentTile();
      System.out.println("Tile Current : Tile before moving " + oldTile.getTileId());
      currentPlayer.movePlayer(roll);
      Tile newTile = currentPlayer.getCurrentTile();
      System.out.println("Tile Moved :  Tile after moving " + newTile.getTileId());

      // Notify observers about player movement
      notifyPlayerMoved(currentPlayer, oldTile, newTile);

      // Check if the tile has a ladder destination
      Tile currentTile = currentPlayer.getCurrentTile();
      if (currentTile.getDestinationTile() != null) {

        // Create and perform the ladder action
        LadderAction ladderAction = new LadderAction(currentTile);

        oldTile = currentTile; // Before ladder movement
        ladderAction.performAction(currentPlayer);
        newTile = currentPlayer.getCurrentTile(); // After ladder movement

        // Notify observers about ladder movement
        notifyPlayerMoved(currentPlayer, oldTile, newTile);

        System.out.println("After ladder action: moved to tile " + currentPlayer.getCurrentTile().getTileId());
        System.out.println(currentPlayer.getName() + " is now at tile " + currentPlayer.getCurrentTile().getTileId());
      }

      // Check win condition (reached the last tile)
      if (currentPlayer.getCurrentTile().getTileId() == numberOfTiles) {
        System.out.println(currentPlayer.getName() + " wins the game!");
        hasWon = true;
        winner = currentPlayer;
        gameFinished = true;

        // Notify observers about game end
        notifyGameEnded(winner);
      }

      // Next player's turn
      indexCurrentPlayer = (indexCurrentPlayer + 1) % players.size();

      // Notify observers about turn change
      if (!hasWon) {
        notifyTurnChanged(players.get(indexCurrentPlayer));
      }
    }
  }

  /**
   * Method to get the list of players in the game.
   *
   * @return the list of players
   */

  public List<Player> getPlayers() {
    return players;
  }

  /**
   * Method to get the board of the game.
   *
   * @return the board
   */

  public BoardLinear getBoard() {
    return board;
  }

  /**
   * Method to get the die of the game.
   *
   * @return the die
   */

  public Die getDie() {
    return die;
  }

  /**
   * Method to get the number of tiles on the board.
   *
   * @return the number of tiles
   */

  public int getNumberOfTiles() {
    return numberOfTiles;
  }

  public boolean isFinished() {
    return gameFinished;
  }

  public Player getWinner() {
    return winner;
  }
}