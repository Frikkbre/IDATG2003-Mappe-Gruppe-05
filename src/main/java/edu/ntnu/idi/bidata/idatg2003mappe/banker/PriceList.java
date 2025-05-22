package edu.ntnu.idi.bidata.idatg2003mappe.banker;

import java.util.HashMap;
import java.util.Map;

/**
 * This class is responsible for holding the prices for different services.
 * For example flipping a marker or using a plane route.
 * Now includes the new token flip service for 300 coins.
 *
 * @author Simen Gudbrandsen and Frikk Breadsroed
 * @version 0.0.2
 * @since 07.02.2025
 */
public class PriceList {
  private final Map<String, Integer> priceRegister = new HashMap<>();

  /**
   * Constructor for the PriceList class.
   * Initializes prices for different services.
   */
  public PriceList() {
    // Gem values
    priceRegister.put("RedGem", 1000);
    priceRegister.put("GreenGem", 4000);
    priceRegister.put("YellowGem", 2000);

    // Transportation costs
    priceRegister.put("PlaneTicket", 3000);
    priceRegister.put("ShipTicket", 1500);
    priceRegister.put("Visa", 1500);

    // Token interaction costs
    priceRegister.put("TokenPurchase", 100);      // Direct token purchase
    priceRegister.put("TokenFlip", 300);          // NEW: Guaranteed token flip
  }

  /**
   * Gets the price for a service.
   *
   * @param service The service to get the price for
   * @return The price of the service, or 0 if the service is not found
   */
  public int getPrice(String service) {
    return priceRegister.getOrDefault(service, 0);
  }

  /**
   * Sets the price for a service.
   *
   * @param service The service to set the price for
   * @param price The new price
   */
  public void setPrice(String service, int price) {
    if (price < 0) {
      throw new IllegalArgumentException("Price cannot be negative");
    }
    priceRegister.put(service, price);
  }

  /**
   * Checks if a service exists in the price list.
   *
   * @param service The service to check
   * @return true if the service exists, false otherwise
   */
  public boolean hasService(String service) {
    return priceRegister.containsKey(service);
  }

  /**
   * Gets the cost of buying a token flip (guaranteed success).
   *
   * @return The cost of a token flip
   */
  public int getTokenFlipCost() {
    return getPrice("TokenFlip");
  }

  /**
   * Gets the cost of buying a token directly.
   *
   * @return The cost of direct token purchase
   */
  public int getTokenPurchaseCost() {
    return getPrice("TokenPurchase");
  }
}