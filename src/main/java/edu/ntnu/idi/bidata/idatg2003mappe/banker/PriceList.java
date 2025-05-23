package edu.ntnu.idi.bidata.idatg2003mappe.banker;

import java.util.HashMap;
import java.util.Map;

/**
 * <p>This class is responsible for holding the prices for different services.</p>
 * <p>It maintains a registry of costs for various game actions and items, including:</p>
 * <ul>
 *   <li>Values of different gem types</li>
 *   <li>Cost of visa documents</li>
 *   <li>Price for token interactions</li>
 * </ul>
 * <p>The class provides a centralized way to access and modify prices
 * throughout the game system.</p>
 *
 * @author Simen Gudbrandsen and Frikk Breadsroed
 * @version 0.0.2
 * @since 07.02.2025
 */
public class PriceList {
  private final Map<String, Integer> priceRegister = new HashMap<>();

  /**
   * <p>Constructor for the PriceList class.</p>
   * <p>Initializes prices for different services with default values:</p>
   * <ul>
   *   <li>RedGem: 100</li>
   *   <li>GreenGem: 300</li>
   *   <li>YellowGem: 500</li>
   *   <li>Visa: 1500</li>
   *   <li>TokenPurchase: 100</li>
   *   <li>TokenFlip: 300</li>
   * </ul>
   */
  public PriceList() {
    // Gem values
    priceRegister.put("RedGem", 300);
    priceRegister.put("GreenGem", 1000);
    priceRegister.put("YellowGem", 500);

    // Visa
    priceRegister.put("Visa", 1500);

    // Token interaction costs
    priceRegister.put("TokenPurchase", 100);
    priceRegister.put("TokenFlip", 300);
  }

  /**
   * <p>Gets the price for a service.</p>
   * <p>Returns the cost associated with the specified service name.
   * If the service is not found in the price register, returns 0.</p>
   *
   * @param service The service to get the price for
   * @return The price of the service, or 0 if the service is not found
   */
  public int getPrice(String service) {
    return priceRegister.getOrDefault(service, 0);
  }

  /**
   * <p>Sets the price for a service.</p>
   * <p>Updates the cost associated with the specified service name.
   * If the service does not exist in the register, it will be added.</p>
   *
   * @param service The service to set the price for
   * @param price   The new price
   * @throws IllegalArgumentException if the price is negative
   */
  public void setPrice(String service, int price) {
    if (price < 0) {
      throw new IllegalArgumentException("Price cannot be negative");
    }
    priceRegister.put(service, price);
  }

  /**
   * <p>Checks if a service exists in the price list.</p>
   * <p>Determines whether the specified service has an entry in the price register.</p>
   *
   * @param service The service to check
   * @return {@code true} if the service exists, {@code false} otherwise
   */
  public boolean hasService(String service) {
    return priceRegister.containsKey(service);
  }

  /**
   * <p>Gets the cost of buying a token flip (guaranteed success).</p>
   * <p>Returns the price specifically for the token flip service,
   * which guarantees successful token interaction.</p>
   *
   * @return The cost of a token flip
   */
  public int getTokenFlipCost() {
    return getPrice("TokenFlip");
  }

  /**
   * <p>Gets the cost of buying a token directly.</p>
   * <p>Returns the price specifically for the token purchase service.</p>
   *
   * @return The cost of direct token purchase
   */
  public int getTokenPurchaseCost() {
    return getPrice("TokenPurchase");
  }
}
