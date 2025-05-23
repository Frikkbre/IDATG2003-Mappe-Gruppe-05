package edu.ntnu.idi.bidata.idatg2003mappe.banker;

import edu.ntnu.idi.bidata.idatg2003mappe.entity.player.Player;

import java.util.HashMap;
import java.util.Map;

/**
 * <p>This class is responsible for transactions of currency during the game,
 * as well as keeping track of players bank account.</p>
 * <p>Each player starts with 500 currency units, and the banker manages all
 * financial transactions between players and the game system.</p>
 * <p>The class provides methods for:</p>
 * <ul>
 *   <li>Registering players and initializing their accounts</li>
 *   <li>Checking account balances</li>
 *   <li>Depositing money into accounts</li>
 *   <li>Withdrawing money from accounts</li>
 * </ul>
 *
 * @author Simen Gudbrandsen and Frikk Breadsroed
 * @version 0.0.1
 * @since 03.02.2025
 */
public class Banker {
  private static final int STARTING_BALANCE = 500;
  private final Map<Player, Integer> playerAccounts = new HashMap<>();
  private final PriceList priceList;

  /**
   * <p>Constructor for the Banker class.</p>
   * <p>Initializes a new banker with an empty set of player accounts and
   * a default price list for various game services.</p>
   */
  public Banker() {
    this.priceList = new PriceList();
  }

  /**
   * <p>Registers a player with the banker and gives them their starting balance.</p>
   * <p>Each player is added to the account tracking system and initialized with
   * the standard starting balance (500 units).</p>
   *
   * @param player The player to register
   */
  public void registerPlayer(Player player) {
    playerAccounts.put(player, STARTING_BALANCE);
  }

  /**
   * <p>Gets the balance of a player.</p>
   * <p>Returns the current amount of money in the player's account, or 0 if
   * the player is not registered with the banker.</p>
   *
   * @param player The player to get the balance for
   * @return The player's balance
   */
  public int getBalance(Player player) {
    return playerAccounts.getOrDefault(player, 0);
  }

  /**
   * <p>Deposits money into a player's account.</p>
   * <p>Increases the player's balance by the specified amount. The amount
   * must be positive.</p>
   *
   * @param player The player to deposit money to
   * @param amount The amount to deposit
   * @throws IllegalArgumentException if the amount is zero or negative
   */
  public void deposit(Player player, int amount) {
    if (amount <= 0) {
      throw new IllegalArgumentException("Amount must be positive");
    }

    int currentBalance = getBalance(player);
    playerAccounts.put(player, currentBalance + amount);
  }

  /**
   * <p>Withdraws money from a player's account.</p>
   * <p>Decreases the player's balance by the specified amount if they have
   * sufficient funds. The amount must be positive.</p>
   *
   * @param player The player to withdraw money from
   * @param amount The amount to withdraw
   * @return {@code true} if the withdrawal was successful, {@code false} if the player has insufficient funds
   * @throws IllegalArgumentException if the amount is zero or negative
   */
  public boolean withdraw(Player player, int amount) {
    if (amount <= 0) {
      throw new IllegalArgumentException("Amount must be positive");
    }

    int currentBalance = getBalance(player);
    if (currentBalance < amount) {
      return false; // Insufficient funds
    }

    playerAccounts.put(player, currentBalance - amount);
    return true;
  }

}
