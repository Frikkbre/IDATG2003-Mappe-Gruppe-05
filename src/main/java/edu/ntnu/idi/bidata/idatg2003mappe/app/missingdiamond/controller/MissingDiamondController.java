package edu.ntnu.idi.bidata.idatg2003mappe.app.missingdiamond.controller;

import edu.ntnu.idi.bidata.idatg2003mappe.app.common.observer.BoardGameObserver;
import edu.ntnu.idi.bidata.idatg2003mappe.banker.Banker;
import edu.ntnu.idi.bidata.idatg2003mappe.filehandling.game.GameState;
import edu.ntnu.idi.bidata.idatg2003mappe.app.missingdiamond.model.MissingDiamond;
import edu.ntnu.idi.bidata.idatg2003mappe.entity.player.Player;
import edu.ntnu.idi.bidata.idatg2003mappe.map.Tile;
import edu.ntnu.idi.bidata.idatg2003mappe.markers.Marker;
import edu.ntnu.idi.bidata.idatg2003mappe.util.map.MapDesignerListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Controller class for the Missing Diamond game.
 * This class handles the game logic and player interactions.
 *
 * @author Simen Gudbrandsen and Frikk Breadsroed
 * @version 0.1.0
 * @since 23.05.2025
 */
public class MissingDiamondController {
  private List<BoardGameObserver> observers = new ArrayList<>();
  private final MissingDiamond game;
  private boolean hasRolled = false;
  private MapDesignerListener view;

  public void addObserver(BoardGameObserver observer) {
    if (!observers.contains(observer)) {
      observers.add(observer);
    }
  }

  public void removeObserver(BoardGameObserver observer) {
    observers.remove(observer);
  }

  private void notifyPlayerMoved(Player player, Tile oldTile, Tile newTile) {
    for (BoardGameObserver observer : observers) {
      observer.onPlayerMoved(player, oldTile, newTile);
    }
  }

  // Action state tracking
  private enum ActionState {
    AWAITING_ROLL,
    AWAITING_MOVE,
    AWAITING_TOKEN_DECISION
  }

  private ActionState currentState = ActionState.AWAITING_ROLL;

  // Available actions for the current state
  private final Map<ActionState, List<String>> availableActions = new HashMap<>();

  /**
   * Constructor for MissingDiamondController.
   */
  public MissingDiamondController() {
    this.game = new MissingDiamond();
    initializeAvailableActions();
  }

  /**
   * Initializes the available actions for each game state.
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
    availableActions.get(ActionState.AWAITING_TOKEN_DECISION).add("skipTokenAction");
  }

  /**
   * Rolls the die and updates the game state.
   *
   * @return A message describing the roll result
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
   * Moves the player to the selected tile.
   *
   * @param tileId The ID of the tile to move to
   * @return A message describing the move result
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

    // Check if the destination is a special tile with a token
    if (isSpecialTile(destinationTile.getTileId()) && game.hasTokenAtTile(destinationTile)) {
      currentState = ActionState.AWAITING_TOKEN_DECISION;
      return moveResult + "\nYou've reached a location with a token. You can buy it for £" +
          game.getTokenPurchaseCost() + ", try to win it with a dice roll, or skip.";
    }

    // End turn only if game is finished
    if (game.isGameFinished()) {
    }

    return moveResult;
  }

  public boolean isSpecialTile(int tileId) {
    // Check if the tile contains a token
    Tile tile = getTileById(tileId);
    if (tile != null) {
      return hasTokenAtTile(tile);
    }
    return false;
  }

  public boolean hasTokenAtTile(Tile tile) {
    if (tile == null) {
      return false;
    }

    // Delegate to the game model
    return game.hasTokenAtTile(tile);
  }

  /**
   * Opens a token at the current player's location, with a chance to get gems, bandit, visa, or diamond.
   * This combines the former "buyToken" and "tryWinToken" functionality.
   *
   * @return A message describing the result of the token interaction
   */
  public String openToken() {
    if (currentState != ActionState.AWAITING_TOKEN_DECISION) {
      return "There is no token to open at your current location.";
    }

    String result = game.openToken();

    // End turn after token action if game is not finished
    if (!game.isGameFinished()) {
      endTurn();
    }

    currentState = ActionState.AWAITING_ROLL;

    return result;
  }

  /**
   * Skips the token action and ends the turn.
   *
   * @return A message indicating the action was skipped
   */
  public String skipTokenAction() {
    if (currentState != ActionState.AWAITING_TOKEN_DECISION) {
      return "No token action to skip.";
    }

    currentState = ActionState.AWAITING_ROLL;

    endTurn();
    return "You chose to ignore the token and continue your journey.";
  }

  /**
   * Ends the current player's turn and moves to the next player.
   */
  public void endTurn() {
    // Switch to next player
    game.nextPlayer();

    // Reset roll state
    hasRolled = false;

    // Reset action state
    currentState = ActionState.AWAITING_ROLL;

    // Reinitialize the action list instead of trying to update it
    initializeAvailableActions();

    // Notify observers about turn change only
    for (BoardGameObserver observer : observers) {
      observer.onTurnChanged(game.getCurrentPlayer());
    }
  }

  /**
   * Applies a game state to this controller.
   *
   * @param gameState The game state to apply
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
   * Creates a game state from the current game.
   *
   * @return A game state object
   */
  public GameState createGameState() {
    return new GameState(game.getCurrentPlayerIndex(), false, game.getPlayers());
  }

  /**
   * Gets a list of possible moves based on the current roll.
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
   * Gets the token at a specific tile ID.
   *
   * @param tileId The ID of the tile to check
   * @return The marker at the tile, or null if no marker exists
   */
  public Marker getTokenAtTileId(int tileId) {
    Tile tile = game.getBoard().getTileById(tileId);
    if (tile != null) {
      return game.getTokenAtTile(tile);
    }
    return null;
  }

  /**
   * Registers a view to receive notifications about game events.
   *
   * @param view The view to register
   */
  public void registerView(MapDesignerListener view) {
    this.view = view;
  }

  /**
   * Checks if the player has rolled the die.
   *
   * @return True if the player has rolled, false otherwise
   */
  public boolean hasRolled() {
    return hasRolled;
  }

  /**
   * Gets the current action state.
   *
   * @return The current action state
   */
  public ActionState getCurrentState() {
    return currentState;
  }

  /**
   * Gets a list of available actions for the current state.
   *
   * @return A list of available actions
   */
  public List<String> getAvailableActions() {
    return new ArrayList<>(availableActions.getOrDefault(currentState, new ArrayList<>()));
  }

  // Delegation methods to the game model

  /**
   * Gets the list of players.
   *
   * @return The list of players
   */
  public List<Player> getPlayers() {
    return game.getPlayers();
  }

  /**
   * Gets the current player.
   *
   * @return The current player
   */
  public Player getCurrentPlayer() {
    return game.getCurrentPlayer();
  }

  /**
   * Gets the current player index.
   *
   * @return The current player index
   */
  public int getCurrentPlayerIndex() {
    return game.getCurrentPlayerIndex();
  }

  /**
   * Gets the current roll value.
   *
   * @return The current roll value
   */
  public int getCurrentRoll() {
    return game.getCurrentRoll();
  }

  /**
   * Checks if the game is finished.
   *
   * @return True if the game is finished, false otherwise
   */
  public boolean isGameFinished() {
    return game.isGameFinished();
  }

  /**
   * Gets a tile by its ID.
   *
   * @param tileId The ID of the tile to get
   * @return The tile with the specified ID, or null if not found
   */
  public Tile getTileById(int tileId) {
    return game.getBoard().getTileById(tileId);
  }

  /**
   * Gets the banker.
   *
   * @return The banker
   */
  public Banker getBanker() {
    return game.getBanker();
  }
}