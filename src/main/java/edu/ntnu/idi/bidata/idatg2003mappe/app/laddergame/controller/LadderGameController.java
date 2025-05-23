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
import java.util.Optional;
import java.util.stream.IntStream;

/**
 * <p>Controller for the Ladder Game.</p>
 * <p>Acts as the bridge between the game model ({@link LadderGame}) and the UI view.
 * This controller manages game state, processes player actions, and notifies
 * observers about game events using the Observer pattern.</p>
 *
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
   * <p>Creates a new controller with the specified ladder configuration.</p>
   * <p>Initializes the game model with either random or classic ladder placement.</p>
   *
   * @param randomLadders <code>true</code> for random ladder placement, <code>false</code> for classic
   */
  public LadderGameController(boolean randomLadders) {
    this.randomLadders = randomLadders;
    this.currentPlayerIndex = 0;
    this.game = new LadderGame(randomLadders);
  }

  /**
   * <p>Plays a complete turn for the current player.</p>
   * <p>This method handles the entire turn sequence including:</p>
   * <ul>
   *   <li>Dice rolling</li>
   *   <li>Player movement</li>
   *   <li>Special tile effects</li>
   *   <li>Ladder interactions</li>
   *   <li>Win condition checking</li>
   * </ul>
   *
   * @return A detailed message describing what happened during the turn
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
   * <p>Handles special tile effects like skip turn or back to start.</p>
   * <p>When a player lands on a tile with special effects, this method
   * processes the effect and applies it to the player.</p>
   *
   * @param player The player affected by the tile effect
   * @param message StringBuilder to append effect messages to
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
   * <p>Handles ladder actions when player lands on a ladder tile.</p>
   * <p>Moves the player to the destination of the ladder and updates
   * the message log with the resulting movement.</p>
   *
   * @param player The player to move via ladder
   * @param message StringBuilder to append ladder movement messages to
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
   * <p>Advances to the next player's turn.</p>
   * <p>Updates the current player index and notifies observers about the turn change.</p>
   */
  private void advanceToNextPlayer() {
    currentPlayerIndex = (currentPlayerIndex + 1) % game.getPlayers().size();
    notifyTurnChanged(game.getPlayers().get(currentPlayerIndex));
  }

  /**
   * <p>Adds an observer to receive game events.</p>
   * <p>Observers will be notified about player movements, turn changes, and game end events.</p>
   *
   * @param observer The {@link BoardGameObserver} to add
   */
  public void addObserver(BoardGameObserver observer) {
    if (observer != null && !observers.contains(observer)) {
      observers.add(observer);
    }
  }

  /**
   * <p>Removes an observer.</p>
   * <p>The observer will no longer receive game event notifications.</p>
   *
   * @param observer The {@link BoardGameObserver} to remove
   */
  public void removeObserver(BoardGameObserver observer) {
    observers.remove(observer);
  }

  /**
   * <p>Gets a tile by its ID using linear search.</p>
   * <p>Delegates to the board's linear tile lookup method.</p>
   *
   * @param tileNumber The tile ID to search for
   * @return The {@link Tile} with the specified ID, or <code>null</code> if not found
   */
  public Tile getTileByIdLinear(int tileNumber) {
    return game.getBoard().getTileByIdLinear(tileNumber);
  }

  /**
   * <p>Gets all players in the game.</p>
   *
   * @return Unmodifiable list of all {@link Player} objects in the game
   */
  public List<Player> getPlayers() {
    return game.getPlayers();
  }

  /**
   * <p>Checks if the game is using random ladders.</p>
   *
   * @return <code>true</code> if random ladders are enabled, <code>false</code> if using classic layout
   */
  public boolean isRandomLadders() {
    return randomLadders;
  }

  /**
   * <p>Creates a save state of the current game.</p>
   * <p>Captures all essential information about the current game state
   * for later restoration.</p>
   *
   * @return A {@link GameState} object containing the current game state
   */
  public GameState createGameState() {
    return new GameState(currentPlayerIndex, randomLadders, game.getPlayers());
  }

  /**
   * <p>Restores the game from a saved state.</p>
   * <p>Updates the controller and model with the saved game information.</p>
   *
   * @param gameState The {@link GameState} to restore from
   * @throws IllegalArgumentException If the provided game state is null
   */
  public void applyGameState(GameState gameState) {
    if (gameState == null) {
      throw new IllegalArgumentException("GameState cannot be null");
    }

    this.currentPlayerIndex = gameState.getCurrentPlayerIndex();

    Optional.ofNullable(gameState.getPlayerPositions()).ifPresent(positions -> {
      List<Player> players = game.getPlayers();

      IntStream.range(0, Math.min(players.size(), positions.size()))
          .forEach(i -> {
            Tile tile = game.getBoard().getTileByIdLinear(positions.get(i).getTileId());
            if (tile != null) {
              players.get(i).placePlayer(tile);
            }
          });
    });
  }


  // Observer notification methods

  /**
   * <p>Notifies observers that a player has moved.</p>
   *
   * @param player The {@link Player} who moved
   * @param from The starting {@link Tile}
   * @param to The destination {@link Tile}
   */
  private void notifyPlayerMoved(Player player, Tile from, Tile to) {
    observers.forEach(observer -> observer.onPlayerMoved(player, from, to));
  }

  /**
   * <p>Notifies observers that the game has ended.</p>
   *
   * @param winner The winning {@link Player}
   */
  private void notifyGameEnded(Player winner) {
    observers.forEach(observer -> observer.onGameEnded(winner));
  }

  /**
   * <p>Notifies observers that the turn has changed.</p>
   *
   * @param newPlayer The {@link Player} whose turn it is now
   */
  private void notifyTurnChanged(Player newPlayer) {
    observers.forEach(observer -> observer.onTurnChanged(newPlayer));
  }
}
