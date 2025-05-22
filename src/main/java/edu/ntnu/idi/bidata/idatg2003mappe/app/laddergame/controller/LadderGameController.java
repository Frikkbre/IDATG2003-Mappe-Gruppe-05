package edu.ntnu.idi.bidata.idatg2003mappe.app.laddergame.controller;

import edu.ntnu.idi.bidata.idatg2003mappe.app.common.observer.BoardGameObserver;
import edu.ntnu.idi.bidata.idatg2003mappe.app.laddergame.model.LadderGame;
import edu.ntnu.idi.bidata.idatg2003mappe.entity.player.Player;
import edu.ntnu.idi.bidata.idatg2003mappe.filehandling.game.GameState;
import edu.ntnu.idi.bidata.idatg2003mappe.map.Tile;
import edu.ntnu.idi.bidata.idatg2003mappe.movement.EffectTile;
import edu.ntnu.idi.bidata.idatg2003mappe.movement.LadderAction;

import java.util.ArrayList;
import java.util.List;

/**
 * Controller for the Ladder Game.
 * Acts as the bridge between the game model and the UI view.
 *
 * @author Simen Gudbrandsen and Frikk Breadsroed
 * @version 1.0.0
 * @since 21.05.2025
 */
public class LadderGameController {
  private final LadderGame game;
  private final boolean randomLadders;
  private int currentPlayerIndex;

  // Observer pattern for UI updates
  private final List<BoardGameObserver> observers = new ArrayList<>();

  /**
   * Creates a new controller with the specified ladder configuration.
   *
   * @param randomLadders true for random ladder placement, false for classic
   */
  public LadderGameController(boolean randomLadders) {
    this.randomLadders = randomLadders;
    this.currentPlayerIndex = 0;
    this.game = new LadderGame(randomLadders);
  }

  /**
   * Plays a complete turn for the current player.
   * Handles dice rolling, movement, effects, ladders, and win checking.
   *
   * @return message describing what happened during the turn
   */
  public String playTurn() {
    List<Player> players = game.getPlayers();
    if (players.isEmpty()) {
      return "No players in the game.";
    }

    Player currentPlayer = players.get(currentPlayerIndex);
    StringBuilder message = new StringBuilder();

    // Handle skip turn
    if (currentPlayer.isSkipTurn()) {
      message.append(currentPlayer.getName()).append(" skips their turn!\n");
      currentPlayer.setSkipTurn(false);
      advanceToNextPlayer();
      return message.toString();
    }

    // Roll dice and move
    int roll = game.getDie().rollDie();
    message.append(currentPlayer.getName()).append(" rolled: ").append(roll).append("\n");

    Tile oldTile = currentPlayer.getCurrentTile();
    currentPlayer.movePlayer(roll);
    Tile newTile = currentPlayer.getCurrentTile();

    message.append("Moved to tile ").append(newTile.getTileId()).append("\n");
    notifyPlayerMoved(currentPlayer, oldTile, newTile);

    // Handle tile effects
    handleTileEffects(currentPlayer, message);

    // Handle ladders
    handleLadders(currentPlayer, message);

    // Check win condition
    if (currentPlayer.getCurrentTile().getTileId() == game.getNumberOfTiles()) {
      message.append(currentPlayer.getName()).append(" wins the game!");
      notifyGameEnded(currentPlayer);
      return message.toString();
    }

    // Next player's turn
    advanceToNextPlayer();
    return message.toString();
  }

  /**
   * Handles special tile effects like skip turn or back to start.
   */
  private void handleTileEffects(Player player, StringBuilder message) {
    String effect = player.getCurrentTile().getEffect();
    if (effect == null) return;

    Tile startTile = getTileByIdLinear(1);
    EffectTile effectTile = new EffectTile(player.getCurrentTile(), effect, startTile);

    if ("skipTurn".equals(effect)) {
      message.append("Effect! ").append(player.getName()).append(" will skip next turn\n");
    } else if ("backToStart".equals(effect)) {
      message.append("Effect! ").append(player.getName()).append(" goes back to start\n");
    }

    effectTile.performAction(player);
  }

  /**
   * Handles ladder actions when player lands on a ladder tile.
   */
  private void handleLadders(Player player, StringBuilder message) {
    if (player.getCurrentTile().getDestinationTile() != null) {
      Tile oldTile = player.getCurrentTile();
      LadderAction ladderAction = new LadderAction(player.getCurrentTile());
      ladderAction.performAction(player);

      message.append("Ladder! Moved to tile ").append(player.getCurrentTile().getTileId()).append("\n");
      notifyPlayerMoved(player, oldTile, player.getCurrentTile());
    }
  }

  /**
   * Advances to the next player's turn.
   */
  private void advanceToNextPlayer() {
    currentPlayerIndex = (currentPlayerIndex + 1) % game.getPlayers().size();
    notifyTurnChanged(game.getPlayers().get(currentPlayerIndex));
  }

  /**
   * Adds an observer to receive game events.
   *
   * @param observer the observer to add
   */
  public void addObserver(BoardGameObserver observer) {
    if (observer != null && !observers.contains(observer)) {
      observers.add(observer);
    }
  }

  /**
   * Removes an observer.
   *
   * @param observer the observer to remove
   */
  public void removeObserver(BoardGameObserver observer) {
    observers.remove(observer);
  }

  /**
   * Gets a tile by its ID.
   *
   * @param tileNumber the tile ID
   * @return the tile, or null if not found
   */
  public Tile getTileByIdLinear(int tileNumber) {
    return game.getBoard().getTileByIdLinear(tileNumber);
  }

  /**
   * Gets all players in the game.
   *
   * @return list of players
   */
  public List<Player> getPlayers() {
    return game.getPlayers();
  }

  /**
   * Checks if using random ladders.
   *
   * @return true if random ladders enabled
   */
  public boolean isRandomLadders() {
    return randomLadders;
  }

  /**
   * Creates a save state of the current game.
   *
   * @return GameState object for saving
   */
  public GameState createGameState() {
    return new GameState(currentPlayerIndex, randomLadders, game.getPlayers());
  }

  /**
   * Restores the game from a saved state.
   *
   * @param gameState the state to restore
   */
  public void applyGameState(GameState gameState) {
    if (gameState == null) {
      throw new IllegalArgumentException("GameState cannot be null");
    }

    this.currentPlayerIndex = gameState.getCurrentPlayerIndex();

    // Restore player positions
    if (gameState.getPlayerPositions() != null) {
      List<GameState.PlayerPosition> positions = gameState.getPlayerPositions();
      List<Player> players = game.getPlayers();

      for (int i = 0; i < Math.min(players.size(), positions.size()); i++) {
        GameState.PlayerPosition pos = positions.get(i);
        Player player = players.get(i);

        Tile tile = game.getBoard().getTileByIdLinear(pos.getTileId());
        if (tile != null) {
          player.placePlayer(tile);
        }
      }
    }
  }

  // Observer notification methods

  /**
   * Notifies observers that a player has moved.
   *
   * @param player the player who moved
   * @param from   the tile they moved from
   * @param to     the tile they moved to
   */
  private void notifyPlayerMoved(Player player, Tile from, Tile to) {
    observers.forEach(observer -> observer.onPlayerMoved(player, from, to));
  }

  /**
   * Notifies observers that the game has ended.
   *
   * @param winner the winning player
   */
  private void notifyGameEnded(Player winner) {
    observers.forEach(observer -> observer.onGameEnded(winner));
  }

  /**
   * Notifies observers that the turn has changed.
   *
   * @param newPlayer the player whose turn it is now
   */
  private void notifyTurnChanged(Player newPlayer) {
    observers.forEach(observer -> observer.onTurnChanged(newPlayer));
  }
}