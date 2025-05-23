package edu.ntnu.idi.bidata.idatg2003mappe.app.missingdiamond.gamelogic;

import edu.ntnu.idi.bidata.idatg2003mappe.banker.Banker;
import edu.ntnu.idi.bidata.idatg2003mappe.entity.player.Player;
import edu.ntnu.idi.bidata.idatg2003mappe.map.Tile;
import edu.ntnu.idi.bidata.idatg2003mappe.markers.*;

import java.util.*;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * <p>Manages the token system for the Missing Diamond game.</p>
 * <p>This class handles all aspects of tokens (markers) in the game, including:</p>
 * <ul>
 *   <li>Token creation and random distribution on the board</li>
 *   <li>Token interactions and effects when revealed</li>
 *   <li>Managing victory conditions related to tokens</li>
 *   <li>Special token actions like purchasing guaranteed token flips</li>
 * </ul>
 * <p>The token system includes various types of markers:</p>
 * <ul>
 *   <li>The Diamond (Star of Africa) - the main prize</li>
 *   <li>Gems (Rubies, Emeralds, Topazes) - provide money</li>
 *   <li>Bandits - cause players to lose all money</li>
 *   <li>Visas - alternative way to win if another player finds the diamond</li>
 *   <li>Blank markers - no effect</li>
 * </ul>
 *
 * @author Simen Gudbrandsen and Frikk Breadsroed
 * @version 0.0.2
 * @since 23.05.2025
 */
public class TokenSystem {
  // Constants for token quantities
  private static final int NUM_RED_GEMS = 5;    // Rubies
  private static final int NUM_GREEN_GEMS = 5;  // Emeralds
  private static final int NUM_YELLOW_GEMS = 5; // Topazes
  private static final int NUM_BANDITS = 4;     // Robbers
  private static final int NUM_VISAS = 3;
  // Token interaction costs
  private static final int TOKEN_FLIP_COST = 300;  // NEW: Cost to buy a token flip
  // Map of tokens placed on tiles
  private final Map<Tile, Marker> tokenMap = new HashMap<>();
  private final Map<Integer, Marker> tokensByTileId = new HashMap<>();
  // Starting locations (Cairo and Tangiers)
  private final Collection<Tile> startingTiles = new ArrayList<>();
  // Token state tracking
  private boolean diamondFound = false;
  private Tile diamondLocation = null;

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
  public void setStartingTiles(Collection<Tile> tiles) {
    this.startingTiles.clear();
    this.startingTiles.addAll(tiles);
  }

  /**
   * Initializes and distributes tokens randomly on city tiles.
   *
   * @param cityTiles List of city tiles to place tokens on
   */
  public void initializeTokens(Collection<Tile> cityTiles) {
    // Clear existing token mappings
    tokenMap.clear();
    tokensByTileId.clear();

    // Create all tokens
    List<Marker> tokens = createAllTokens(cityTiles.size());

    // Shuffle the tokens
    Collections.shuffle(tokens);

    // Place tokens on city tiles
    IntStream.range(0, Math.min(cityTiles.size(), tokens.size()))
        .forEach(i -> {
          List<Tile> cityTileList = new ArrayList<>(cityTiles);
          Tile cityTile = cityTileList.get(i);
          Marker token = tokens.get(i);
          token.setLocation(cityTile);
          tokenMap.put(cityTile, token);
          tokensByTileId.put(cityTile.getTileId(), token);

          // Keep track of diamond location
          if (token instanceof Diamond) {
            diamondLocation = cityTile;
          }
        });
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
    Stream.generate(RedGem::new).limit(NUM_RED_GEMS).forEach(tokens::add);
    Stream.generate(GreenGem::new).limit(NUM_GREEN_GEMS).forEach(tokens::add);
    Stream.generate(YellowGem::new).limit(NUM_YELLOW_GEMS).forEach(tokens::add);

    // Add bandits (robbers)
    Stream.generate(Bandit::new).limit(NUM_BANDITS).forEach(tokens::add);

    // Add visas
    Stream.generate(Visa::new).limit(NUM_VISAS).forEach(tokens::add);

    // Add blank tokens based on city count
    int blankTokensNeeded = Math.max(0, cityCount - tokens.size());
    Stream.generate(BlankMarker::new).limit(blankTokensNeeded).forEach(tokens::add);

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
   * NEW: Buys a token flip for 300 coins and automatically reveals the token.
   * This is a guaranteed way to get the token without rolling dice.
   *
   * @param player The player buying the token flip
   * @param tile   The tile with the token
   * @param banker The banker handling the transaction
   * @return True if the purchase was successful, false otherwise
   */
  public boolean buyTokenFlip(Player player, Tile tile, Banker banker) {
    // Check if tile has a token
    Marker token = getTokenAtTile(tile);
    if (token == null) {
      return false;
    }

    // Check if player has enough money
    if (banker.getBalance(player) < TOKEN_FLIP_COST) {
      return false; // Not enough money
    }

    // Attempt to pay for the token flip
    if (!banker.withdraw(player, TOKEN_FLIP_COST)) {
      return false; // Transaction failed
    }

    // Process the token (guaranteed success)
    processToken(token, player, banker);

    // Remove the token from the board
    removeTokenFromTile(tile);

    return true;
  }

  /**
   * Processes a token's effects for a player.
   *
   * @param token  The token to process
   * @param player The player receiving the effects
   * @param banker The banker for financial transactions
   */
  private void processToken(Marker token, Player player, Banker banker) {
    token.reveal(); // Reveal the token

    if (token instanceof Diamond diamond) {
      diamond.find();
      this.diamondFound = true;

      // Add the diamond to player's inventory
      player.addInventoryItem("diamond");
    } else if (token instanceof RedGem) {
      // Ruby worth £1000
      banker.deposit(player, 1000);
    } else if (token instanceof GreenGem) {
      // Emerald worth £4000
      banker.deposit(player, 4000);
    } else if (token instanceof YellowGem) {
      // Topaz worth £2000
      banker.deposit(player, 2000);
    } else if (token instanceof Bandit) {
      // Robber - lose all money (only if player has money)
      int currentBalance = banker.getBalance(player);
      if (currentBalance > 0) {
        banker.withdraw(player, currentBalance);
      }
    } else if (token instanceof Visa) {
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
   * Checks the victory condition for a player.
   * Checks if the player has met the conditions to win:
   * 1. Be at a starting tile (Cairo or Tangiers)
   * 2. Have the diamond OR have a visa card (if diamond found)
   *
   * @param player      The player to check
   * @param currentTile The current tile of the player
   * @return True if the victory condition is met, false otherwise
   */
  public boolean checkVictoryCondition(Player player, Tile currentTile) {
    return isStartingTile(currentTile) &&
        (playerHasDiamond(player) || (diamondFound && playerHasVisa(player)));
  }

}