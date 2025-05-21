package edu.ntnu.idi.bidata.idatg2003mappe.banker;

import edu.ntnu.idi.bidata.idatg2003mappe.entity.player.Player;
import java.util.HashMap;
import java.util.Map;

/**
 * This class is responsible for transactions of currency during the game,
 * as well as keeping track of players bank account.
 * Each player should start out with 5000 each.
 *
 * @author Simen Gudbrandsen and Frikk Breadsroed
 * @version 0.0.1
 * @since 03.02.2025
 */
public class Banker {
  private static final int STARTING_BALANCE = 5000;
  private final Map<Player, Integer> playerAccounts = new HashMap<>();
  private final PriceList priceList;

  /**
   * Constructor for the Banker class.
   */
  public Banker() {
    this.priceList = new PriceList();
  }

  /**
   * Registers a player with the banker and gives them their starting balance.
   *
   * @param player The player to register
   */
  public void registerPlayer(Player player) {
    playerAccounts.put(player, STARTING_BALANCE);
  }

  /**
   * Gets the balance of a player.
   *
   * @param player The player to get the balance for
   * @return The player's balance
   */
  public int getBalance(Player player) {
    return playerAccounts.getOrDefault(player, 0);
  }

  /**
   * Deposits money into a player's account.
   *
   * @param player The player to deposit money to
   * @param amount The amount to deposit
   */
  public void deposit(Player player, int amount) {
    if (amount <= 0) {
      throw new IllegalArgumentException("Amount must be positive");
    }

    int currentBalance = getBalance(player);
    playerAccounts.put(player, currentBalance + amount);
  }

  /**
   * Withdraws money from a player's account.
   *
   * @param player The player to withdraw money from
   * @param amount The amount to withdraw
   * @return true if the withdrawal was successful, false if the player has insufficient funds
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

  /**
   * Transfers money from one player to another.
   *
   * @param fromPlayer The player to transfer money from
   * @param toPlayer The player to transfer money to
   * @param amount The amount to transfer
   * @return true if the transfer was successful, false if the fromPlayer has insufficient funds
   */
  public boolean transfer(Player fromPlayer, Player toPlayer, int amount) {
    if (withdraw(fromPlayer, amount)) {
      deposit(toPlayer, amount);
      return true;
    }
    return false;
  }

  /**
   * Gets the price for a gem.
   *
   * @param gemType The type of gem
   * @return The price of the gem
   */
  public int getGemPrice(String gemType) {
    return priceList.getPrice(gemType);
  }

  /**
   * Checks if a player can afford to buy a gem.
   *
   * @param player The player
   * @param gemType The type of gem
   * @return true if the player can afford the gem, false otherwise
   */
  public boolean canAffordGem(Player player, String gemType) {
    int price = getGemPrice(gemType);
    return getBalance(player) >= price;
  }

  /**
   * Buys a gem for a player.
   *
   * @param player The player
   * @param gemType The type of gem
   * @return true if the purchase was successful, false otherwise
   */
  public boolean buyGem(Player player, String gemType) {
    int price = getGemPrice(gemType);
    return withdraw(player, price);
  }
}