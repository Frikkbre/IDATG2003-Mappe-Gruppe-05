package edu.ntnu.idi.bidata.idatg2003mappe.markers;

/**
 * <p>Represents the effect of revealing a token in the Missing Diamond game.</p>
 *
 * <p>This class encapsulates the various effects that can occur when a player
 * reveals a token, allowing for polymorphic handling of different token types
 * without using instanceof checks.</p>
 *
 * <p>Effects include:</p>
 * <ul>
 *   <li>Money changes (deposits or withdrawals)</li>
 *   <li>Inventory item additions (diamond, visa)</li>
 *   <li>Special flags (diamond found)</li>
 * </ul>
 *
 * @author Simen Gudbrandsen and Frikk Breadsroed
 * @version 1.0.0
 * @since 05.01.2026
 */
public class TokenEffectResult {

  /**
   * Special value indicating the player should lose all their money.
   */
  public static final int LOSE_ALL_MONEY = Integer.MIN_VALUE;

  private final int moneyChange;
  private final String inventoryItem;
  private final boolean isDiamond;

  /**
   * <p>Creates a new TokenEffectResult with the specified effects.</p>
   *
   * @param moneyChange   The amount of money to add (positive) or remove (negative).
   *                      Use {@link #LOSE_ALL_MONEY} to indicate losing all money.
   * @param inventoryItem The item to add to the player's inventory, or null for none
   * @param isDiamond     Whether this effect is from finding the diamond
   */
  public TokenEffectResult(int moneyChange, String inventoryItem, boolean isDiamond) {
    this.moneyChange = moneyChange;
    this.inventoryItem = inventoryItem;
    this.isDiamond = isDiamond;
  }

  /**
   * <p>Creates an effect with no changes.</p>
   *
   * @return A TokenEffectResult with no effects
   */
  public static TokenEffectResult noEffect() {
    return new TokenEffectResult(0, null, false);
  }

  /**
   * <p>Creates an effect that deposits money to the player.</p>
   *
   * @param amount The amount to deposit
   * @return A TokenEffectResult that deposits the specified amount
   */
  public static TokenEffectResult depositMoney(int amount) {
    return new TokenEffectResult(amount, null, false);
  }

  /**
   * <p>Creates an effect that causes the player to lose all money.</p>
   *
   * @return A TokenEffectResult that removes all player money
   */
  public static TokenEffectResult loseAllMoney() {
    return new TokenEffectResult(LOSE_ALL_MONEY, null, false);
  }

  /**
   * <p>Creates an effect that adds an item to the player's inventory.</p>
   *
   * @param item The item to add
   * @return A TokenEffectResult that adds the specified item
   */
  public static TokenEffectResult addItem(String item) {
    return new TokenEffectResult(0, item, false);
  }

  /**
   * <p>Creates an effect for finding the diamond.</p>
   *
   * @return A TokenEffectResult representing the diamond being found
   */
  public static TokenEffectResult diamond() {
    return new TokenEffectResult(0, "diamond", true);
  }

  /**
   * <p>Gets the money change amount.</p>
   *
   * @return The money change (positive for deposit, negative for withdrawal,
   *         {@link #LOSE_ALL_MONEY} to lose all money)
   */
  public int getMoneyChange() {
    return moneyChange;
  }

  /**
   * <p>Gets the inventory item to add.</p>
   *
   * @return The item to add to inventory, or null if none
   */
  public String getInventoryItem() {
    return inventoryItem;
  }

  /**
   * <p>Checks if this effect represents finding the diamond.</p>
   *
   * @return true if the diamond was found, false otherwise
   */
  public boolean isDiamond() {
    return isDiamond;
  }

  /**
   * <p>Checks if this effect causes the player to lose all money.</p>
   *
   * @return true if the player loses all money, false otherwise
   */
  public boolean isLoseAllMoney() {
    return moneyChange == LOSE_ALL_MONEY;
  }

  /**
   * <p>Checks if this effect has any changes.</p>
   *
   * @return true if there are any effects, false if no changes occur
   */
  public boolean hasEffect() {
    return moneyChange != 0 || inventoryItem != null || isDiamond;
  }
}
