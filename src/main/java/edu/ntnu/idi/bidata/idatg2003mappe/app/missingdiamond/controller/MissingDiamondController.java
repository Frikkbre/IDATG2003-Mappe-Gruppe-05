package edu.ntnu.idi.bidata.idatg2003mappe.app.missingdiamond.controller;

import edu.ntnu.idi.bidata.idatg2003mappe.app.common.observer.BoardGameObserver;
import edu.ntnu.idi.bidata.idatg2003mappe.app.missingdiamond.model.MissingDiamond;
import edu.ntnu.idi.bidata.idatg2003mappe.banker.Banker;
import edu.ntnu.idi.bidata.idatg2003mappe.entity.die.Die;
import edu.ntnu.idi.bidata.idatg2003mappe.entity.player.Player;
import edu.ntnu.idi.bidata.idatg2003mappe.filehandling.game.GameState;
import edu.ntnu.idi.bidata.idatg2003mappe.map.Tile;
import edu.ntnu.idi.bidata.idatg2003mappe.markers.Marker;
import edu.ntnu.idi.bidata.idatg2003mappe.util.map.MapDesignerListener;

import java.util.*;

/**
 * <p>Controller class for the Missing Diamond game.</p>
 * <p>This class handles the game logic and player interactions, serving as
 * the intermediary between the game model and the user interface. It manages
 * the game state machine, processes player actions, and updates observers
 * when significant game events occur.</p>
 * <p>Key responsibilities include:</p>
 * <ul>
 *   <li>Managing turn sequence and player actions</li>
 *   <li>Handling dice rolling and movement validation</li>
 *   <li>Processing token interactions</li>
 *   <li>Maintaining game state through defined action states</li>
 *   <li>Notifying observers of game events</li>
 * </ul>
 *
 * @author Simen Gudbrandsen and Frikk Breadsroed
 * @version 0.0.3
 * @since 23.05.2025
 */
public class MissingDiamondController {
  private final List<BoardGameObserver> observers = new ArrayList<>();
  private final MissingDiamond game;
  private boolean hasRolled = false;
  private MapDesignerListener view;

  // Action state tracking
  private enum ActionState {
    AWAITING_ROLL,
    AWAITING_MOVE,
    AWAITING_TOKEN_DECISION
  }

  private ActionState currentState = ActionState.AWAITING_ROLL;

  // Available actions for the current state (Skip action removed)
  private final Map<ActionState, List<String>> availableActions = new HashMap<>();

  /**
   * <p>Constructor for MissingDiamondController.</p>
   * <p>Initializes a new game instance and sets up the available actions
   * for each game state.</p>
   */
  public MissingDiamondController() {
    this.game = new MissingDiamond();
    initializeAvailableActions();
  }

  /**
   * <p>Initializes the available actions for each game state.</p>
   * <p>This method defines which actions are valid during each phase of a turn,
   * creating a state machine to manage the game flow.</p>
   */
  private void initializeAvailableActions() {
    // Actions for each state
    List<ActionState> states = new ArrayList<>();
    states.add(ActionState.AWAITING_ROLL);
    states.add(ActionState.AWAITING_MOVE);
    states.add(ActionState.AWAITING_TOKEN_DECISION);

    for (ActionState state : states) {
      availableActions.put(state, new ArrayList<>());
    }

    availableActions.get(ActionState.AWAITING_ROLL).add("rollDie");

    availableActions.get(ActionState.AWAITING_MOVE).add("moveToTile");

    availableActions.get(ActionState.AWAITING_TOKEN_DECISION).add("openToken");
    availableActions.get(ActionState.AWAITING_TOKEN_DECISION).add("buyTokenFlip");
  }

  /**
   * <p>Rolls the die and updates the game state.</p>
   * <p>When a player rolls the die, this method:</p>
   * <ul>
   *   <li>Validates that the roll action is allowed in the current state</li>
   *   <li>Delegates to the game model to execute the roll</li>
   *   <li>Updates the controller state to await movement</li>
   *   <li>Checks if there are valid moves available</li>
   * </ul>
   *
   * @return A message describing the roll result and any additional information
   */
  public String playTurn() {
    if (currentState != ActionState.AWAITING_ROLL) {
      return "You need to complete your current action first.";
    }

    // Roll the die
    String result = game.playTurn();
    hasRolled = true;
    currentState = ActionState.AWAITING_MOVE;

    // Check if there are any valid moves
    List<Tile> possibleMoves = getPossibleMoves();
    if (possibleMoves.isEmpty()) {
      result += "\nNo valid moves available. Turn passed to next player.";
      endTurn();
    }

    return result;
  }

  /**
   * <p>Moves the player to the selected tile.</p>
   * <p>This method:</p>
   * <ul>
   *   <li>Validates that the move is allowed in the current state</li>
   *   <li>Checks if the destination tile is a valid move based on the dice roll</li>
   *   <li>Moves the player to the destination tile</li>
   *   <li>Updates the game state based on the destination tile type</li>
   *   <li>Checks for game-ending conditions</li>
   * </ul>
   *
   * @param tileId The ID of the tile to move to
   * @return A message describing the move result and any subsequent options
   */
  public String movePlayer(int tileId) {
    // Check if player has rolled
    if (currentState != ActionState.AWAITING_MOVE) {
      return "You need to roll the die first.";
    }

    // Get destination tile
    Tile destinationTile = game.getBoard().getTileById(tileId);
    if (destinationTile == null) {
      return "Invalid tile ID.";
    }

    // Check if the destination tile is a valid move
    Set<Tile> validMoves = game.getPossibleMovesForCurrentRoll();
    if (!validMoves.contains(destinationTile)) {
      return "Cannot move to tile " + tileId + " - it's not exactly " +
          game.getCurrentRoll() + " steps away.";
    }

    // Move the player
    String moveResult = game.movePlayerToTile(destinationTile);

    // Check if game is finished (win condition)
    if (game.isGameFinished()) {
      // Don't end turn - game is over
      return moveResult;
    }

    // Check if the destination is a special tile with a token
    if (isSpecialTile(destinationTile.getTileId())) {
      // Special tile - check if it has a token
      if (game.hasTokenAtTile(destinationTile)) {
        currentState = ActionState.AWAITING_TOKEN_DECISION;
        return moveResult + "\nYou've reached a location with a token. You can:" +
            "\n• Try to get it free (roll 4-6 to succeed)" +
            "\n• Buy a guaranteed token flip for £300" +
            "\n• Use 'End Turn' to continue your journey";
      } else {
        // Special tile without token - player can choose to end turn
        return moveResult + "\nYou've reached a special location (no token). Use 'End Turn' when ready.";
      }
    } else {
      // Black tile (regular movement tile) - automatically end turn
      return moveResult + "\nTurn ended automatically. Next player's turn.";
    }
  }

  /**
   * <p>Resets the roll state to allow the next player to roll.</p>
   * <p>This should be called when ending turns to ensure the next player
   * can roll the die in their turn.</p>
   */
  public void resetRollState() {
    this.hasRolled = false;
  }

  /**
   * <p>Buys a token flip for 300 coins (guaranteed success).</p>
   * <p>This method lets a player pay coins to automatically flip a token
   * without needing to roll the die.</p>
   *
   * @param tile The tile with the token to flip
   * @return <code>true</code> if the purchase was successful, <code>false</code> otherwise
   */
  public boolean buyTokenFlip(Tile tile) {
    if (currentState != ActionState.AWAITING_TOKEN_DECISION) {
      return false;
    }

    Player currentPlayer = game.getCurrentPlayer();
    Banker banker = game.getBanker();

    // Use the token system's buyTokenFlip method
    boolean success = game.getTokenSystem().buyTokenFlip(currentPlayer, tile, banker);

    if (success) {
      // Reset state and end turn after successful token flip
      currentState = ActionState.AWAITING_ROLL;
      endTurn();
    }

    return success;
  }

  /**
   * <p>Checks if the specified tile is a special tile with a token.</p>
   * <p>Special tiles are locations where tokens can be placed and interacted with.</p>
   *
   * @param tileId The ID of the tile to check
   * @return <code>true</code> if the tile is special, <code>false</code> otherwise
   */
  public boolean isSpecialTile(int tileId) {
    Tile tile = getTileById(tileId);
    if (tile != null) {
      return hasTokenAtTile(tile);
    }
    return false;
  }

  /**
   * <p>Checks if a token is present at the specified tile.</p>
   *
   * @param tile The tile to check
   * @return <code>true</code> if a token is present, <code>false</code> otherwise
   */
  public boolean hasTokenAtTile(Tile tile) {
    if (tile == null) {
      return false;
    }

    // Delegate to the game model
    return game.hasTokenAtTile(tile);
  }

  /**
   * <p>Ends the current player's turn and moves to the next player.</p>
   * <p>This method:</p>
   * <ul>
   *   <li>Advances to the next player in sequence</li>
   *   <li>Resets the controller state to await a new roll</li>
   *   <li>Notifies observers about the turn change</li>
   * </ul>
   */
  public void endTurn() {
    // Store current player for notification
    Player previousPlayer = game.getCurrentPlayer();

    // Switch to next player (this updates the game state)
    game.nextPlayer();

    // Reset controller state completely
    hasRolled = false;
    currentState = ActionState.AWAITING_ROLL;

    // Notify observers about turn change
    Player newPlayer = game.getCurrentPlayer();
    for (BoardGameObserver observer : observers) {
      observer.onTurnChanged(newPlayer);
    }
  }

  /**
   * <p>Applies a game state to this controller.</p>
   * <p>Restores a saved game by updating player positions and controller state.</p>
   *
   * @param gameState The {@link GameState} to apply
   */
  public void applyGameState(GameState gameState) {
    game.setCurrentPlayerIndex(gameState.getCurrentPlayerIndex());

    // Restore player positions
    if (gameState.getPlayerPositions() != null) {
      List<GameState.PlayerPosition> positions = gameState.getPlayerPositions();
      List<Player> players = game.getPlayers();

      for (int i = 0; i < players.size() && i < positions.size(); i++) {
        GameState.PlayerPosition pos = positions.get(i);
        Player player = players.get(i);

        // Find the tile with the saved ID and place player there
        Tile tile = game.getBoard().getTileById(pos.getTileId());
        if (tile != null) {
          player.placePlayer(tile);
        }
      }
    }

    // Reset controller state
    hasRolled = false;
    currentState = ActionState.AWAITING_ROLL;
  }

  /**
   * <p>Gets the underlying game model.</p>
   *
   * @return The {@link MissingDiamond} game model
   */
  public MissingDiamond getGame() {
    return this.game;
  }

  /**
   * <p>Removes a token from the specified tile.</p>
   *
   * @param tile The tile to remove the token from
   * @return The removed {@link Marker}, or <code>null</code> if no token was present
   */
  public Marker removeTokenFromTile(Tile tile) {
    return game.getTokenSystem().removeTokenFromTile(tile);
  }

  /**
   * <p>Gets a list of possible moves based on the current roll.</p>
   * <p>These are the tiles that are exactly the rolled number of steps away.</p>
   *
   * @return A list of tiles that the player can move to
   */
  public List<Tile> getPossibleMoves() {
    if (!hasRolled) {
      return new ArrayList<>();
    }
    return new ArrayList<>(game.getPossibleMovesForCurrentRoll());
  }

  /**
   * <p>Gets the token at a specific tile ID.</p>
   *
   * @param tileId The ID of the tile to check
   * @return The {@link Marker} at the tile, or <code>null</code> if no marker exists
   */
  public Marker getTokenAtTileId(int tileId) {
    Tile tile = game.getBoard().getTileById(tileId);
    if (tile != null) {
      return game.getTokenAtTile(tile);
    }
    return null;
  }

  /**
   * <p>Registers a view to receive notifications about game events.</p>
   *
   * @param view The {@link MapDesignerListener} to register
   */
  public void registerView(MapDesignerListener view) {
    this.view = view;
  }

  /**
   * <p>Checks if the player has rolled the die.</p>
   *
   * @return <code>true</code> if the player has rolled, <code>false</code> otherwise
   */
  public boolean hasRolled() {
    return hasRolled;
  }

  /**
   * <p>Gets the list of players.</p>
   *
   * @return The list of {@link Player} objects in the game
   */
  public List<Player> getPlayers() {
    return game.getPlayers();
  }

  /**
   * <p>Gets the current player.</p>
   *
   * @return The {@link Player} whose turn it currently is
   */
  public Player getCurrentPlayer() {
    return game.getCurrentPlayer();
  }

  /**
   * <p>Checks if the game is finished.</p>
   *
   * @return <code>true</code> if the game is finished, <code>false</code> otherwise
   */
  public boolean isGameFinished() {
    return game.isGameFinished();
  }

  /**
   * <p>Gets a tile by its ID.</p>
   *
   * @param tileId The ID of the tile to get
   * @return The {@link Tile} with the specified ID, or <code>null</code> if not found
   */
  public Tile getTileById(int tileId) {
    return game.getBoard().getTileById(tileId);
  }

  /**
   * <p>Gets the banker.</p>
   * <p>The banker manages all financial transactions in the game.</p>
   *
   * @return The {@link Banker} instance
   */
  public Banker getBanker() {
    return game.getBanker();
  }

  /**
   * <p>Gets the die used for movement rolls.</p>
   *
   * @return The {@link Die} instance
   */
  public Die getDie() {
    return game.getDie();
  }
}
