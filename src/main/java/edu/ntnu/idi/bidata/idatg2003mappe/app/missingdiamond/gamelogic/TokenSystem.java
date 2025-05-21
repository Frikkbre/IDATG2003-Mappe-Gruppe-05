package edu.ntnu.idi.bidata.idatg2003mappe.app.missingdiamond.gamelogic;

import edu.ntnu.idi.bidata.idatg2003mappe.banker.Banker;
import edu.ntnu.idi.bidata.idatg2003mappe.entity.player.Player;
import edu.ntnu.idi.bidata.idatg2003mappe.map.Tile;
import edu.ntnu.idi.bidata.idatg2003mappe.markers.*;

import java.util.*;

/**
 * Manages the token system for the Missing Diamond game.
 * Handles token creation, distribution, and interactions.
 *
 * @author Simen Gudbrandsen and Frikk Breadsroed
 * @version 0.0.1
 * @since 23.05.2025
 */
public class TokenSystem {
  // Map of tokens placed on tiles
  private final Map<Tile, Marker> tokenMap = new HashMap<>();
  private final Map<Integer, Marker> tokensByTileId = new HashMap<>();

  // Token state tracking
  private boolean diamondFound = false;
  private Tile diamondLocation = null;

  // Starting locations (Cairo and Tangiers)
  private final List<Tile> startingTiles = new ArrayList<>();

  // Constants for token quantities
  private static final int NUM_RED_GEMS = 5;    // Rubies
  private static final int NUM_GREEN_GEMS = 5;  // Emeralds
  private static final int NUM_YELLOW_GEMS = 5; // Topazes
  private static final int NUM_BANDITS = 7;     // Robbers

  /**
   * Constructor for TokenSystem.
   */
  public TokenSystem() {
    // Empty constructor
  }

  /**
   * Sets the starting tiles for the game (Cairo and Tangiers).
   *
   * @param tiles The tiles to set as starting locations
   */
  public void setStartingTiles(List<Tile> tiles) {
    this.startingTiles.clear();
    this.startingTiles.addAll(tiles);
  }

  /**
   * Initializes and distributes tokens randomly on city tiles.
   *
   * @param cityTiles List of city tiles to place tokens on
   */
  public void initializeTokens(List<Tile> cityTiles) {
    // Clear existing token mappings
    tokenMap.clear();
    tokensByTileId.clear();

    // Create all tokens
    List<Marker> tokens = createAllTokens(cityTiles.size());

    // Shuffle the tokens
    Collections.shuffle(tokens);

    // Place tokens on city tiles
    for (int i = 0; i < cityTiles.size() && i < tokens.size(); i++) {
      Tile cityTile = cityTiles.get(i);
      Marker token = tokens.get(i);
      token.setLocation(cityTile);
      tokenMap.put(cityTile, token);
      tokensByTileId.put(cityTile.getTileId(), token);

      // Keep track of diamond location
      if (token instanceof Diamond) {
        diamondLocation = cityTile;
      }
    }
  }

  /**
   * Creates all the tokens for the game.
   *
   * @param cityCount The number of cities to create tokens for
   * @return List of all created markers
   */
  private List<Marker> createAllTokens(int cityCount) {
    List<Marker> tokens = new ArrayList<>();

    // Add the diamond (Star of Africa)
    tokens.add(new Diamond());

    // Add gems
    for (int i = 0; i < NUM_RED_GEMS; i++) {
      tokens.add(new RedGem());
    }

    for (int i = 0; i < NUM_GREEN_GEMS; i++) {
      tokens.add(new GreenGem());
    }

    for (int i = 0; i < NUM_YELLOW_GEMS; i++) {
      tokens.add(new YellowGem());
    }

    // Add bandits (robbers)
    for (int i = 0; i < NUM_BANDITS; i++) {
      tokens.add(new Bandit());
    }

    // Calculate how many blank tokens we need
    int blankTokensNeeded = Math.max(0, cityCount - tokens.size());

    // Add blank tokens
    for (int i = 0; i < blankTokensNeeded; i++) {
      tokens.add(new BlankMarker());
    }

    return tokens;
  }

  /**
   * Gets the token at a specific tile.
   *
   * @param tile The tile to check
   * @return The marker at the tile, or null if no marker exists
   */
  public Marker getTokenAtTile(Tile tile) {
    return tokenMap.get(tile);
  }

  /**
   * Gets the token at a tile by the tile's ID.
   *
   * @param tileId The ID of the tile to check
   * @return The marker at the tile, or null if no marker exists
   */
  public Marker getTokenAtTileId(int tileId) {
    return tokensByTileId.get(tileId);
  }

  /**
   * Removes a token from a tile.
   *
   * @param tile The tile to remove the token from
   * @return The removed marker, or null if no marker existed
   */
  public Marker removeTokenFromTile(Tile tile) {
    Marker marker = tokenMap.remove(tile);
    if (marker != null) {
      tokensByTileId.remove(tile.getTileId());
      marker.removeFromLocation();
    }
    return marker;
  }

  /**
   * Processes buying a token and its effects.
   *
   * @param player The player buying the token
   * @param tile The tile with the token
   * @param banker The banker handling the transaction
   * @return True if the purchase was successful, false otherwise
   */
  public boolean buyToken(Player player, Tile tile, Banker banker) {
    // Check if tile has a token
    Marker token = getTokenAtTile(tile);
    if (token == null) {
      return false;
    }

    // Attempt to pay for the token
    if (!banker.withdraw(player, 100)) {
      return false; // Not enough money
    }

    // Process the token
    processToken(token, player, banker);

    // Remove the token from the board
    removeTokenFromTile(tile);

    return true;
  }

  /**
   * Attempts to win a token with a dice roll.
   *
   * @param player The player attempting to win the token
   * @param tile The tile with the token
   * @param diceRoll The dice roll result
   * @param banker The banker for financial transactions
   * @return True if the token was won, false otherwise
   */
  public boolean tryWinToken(Player player, Tile tile, int diceRoll, Banker banker) {
    // Check if tile has a token
    Marker token = getTokenAtTile(tile);
    if (token == null) {
      return false;
    }

    // Need a 4, 5, or 6 to win
    if (diceRoll < 4) {
      return false;
    }

    // Process the token
    processToken(token, player, banker);

    // Remove the token from the board
    removeTokenFromTile(tile);

    return true;
  }

  /**
   * Processes a token's effects for a player.
   *
   * @param token The token to process
   * @param player The player receiving the effects
   * @param banker The banker for financial transactions
   */
  private void processToken(Marker token, Player player, Banker banker) {
    token.reveal(); // Reveal the token

    if (token instanceof Diamond) {
      Diamond diamond = (Diamond) token;
      diamond.find();
      this.diamondFound = true;

      // Add the diamond to player's inventory
      player.addInventoryItem("diamond");
    }
    else if (token instanceof RedGem) {
      // Ruby worth £1000
      banker.deposit(player, 1000);
    }
    else if (token instanceof GreenGem) {
      // Emerald worth £600
      banker.deposit(player, 4000);
    }
    else if (token instanceof YellowGem) {
      // Topaz worth £300
      banker.deposit(player, 2000);
    }
    else if (token instanceof Bandit) {
      // Robber - lose all money
      int currentBalance = banker.getBalance(player);
      banker.withdraw(player, currentBalance);
    }
    else if (token instanceof Visa) {
      // Visa card
      player.addInventoryItem("visa");
    }
  }

  /**
   * Checks if a tile is a starting tile (Cairo or Tangiers).
   *
   * @param tile The tile to check
   * @return True if it's a starting tile, false otherwise
   */
  public boolean isStartingTile(Tile tile) {
    return startingTiles.contains(tile);
  }

  /**
   * Checks if a tile is a starting tile by ID.
   *
   * @param tileId The tile ID to check
   * @return True if it's a starting tile, false otherwise
   */
  public boolean isStartingTileById(int tileId) {
    for (Tile startingTile : startingTiles) {
      if (startingTile.getTileId() == tileId) {
        return true;
      }
    }
    return false;
  }

  /**
   * Checks if a player has the diamond.
   *
   * @param player The player to check
   * @return True if the player has the diamond, false otherwise
   */
  public boolean playerHasDiamond(Player player) {
    return player.hasInventoryItem("diamond");
  }

  /**
   * Checks if a player has a visa card.
   *
   * @param player The player to check
   * @return True if the player has a visa card, false otherwise
   */
  public boolean playerHasVisa(Player player) {
    return player.hasInventoryItem("visa");
  }

  /**
   * Checks if the diamond has been found.
   *
   * @return True if the diamond has been found, false otherwise
   */
  public boolean isDiamondFound() {
    return diamondFound;
  }

  /**
   * Sets whether the diamond has been found.
   *
   * @param diamondFound True if the diamond has been found
   */
  public void setDiamondFound(boolean diamondFound) {
    this.diamondFound = diamondFound;
  }

  /**
   * Gets the location of the diamond.
   *
   * @return The tile where the diamond is located
   */
  public Tile getDiamondLocation() {
    return diamondLocation;
  }

  /**
   * Checks the victory condition for a player.
   *
   * @param player The player to check
   * @param currentTile The current tile of the player
   * @return True if the victory condition is met, false otherwise
   */
  public boolean checkVictoryCondition(Player player, Tile currentTile) {
    // To win, player needs:
    // 1. To be at a starting tile (Cairo or Tangiers)
    // 2. AND either have the diamond OR have a visa card (if diamond found)
    return isStartingTile(currentTile) &&
        (playerHasDiamond(player) || (diamondFound && playerHasVisa(player)));
  }
}