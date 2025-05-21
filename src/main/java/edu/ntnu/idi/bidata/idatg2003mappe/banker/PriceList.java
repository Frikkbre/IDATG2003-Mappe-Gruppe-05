package edu.ntnu.idi.bidata.idatg2003mappe.banker;

import java.util.HashMap;
import java.util.Map;

/**
 * This class is responsible for holding the prices for different services.
 * For example flipping a marker or using a plane route.
 *
 * @author Simen Gudbrandsen and Frikk Breadsroed
 * @version 0.0.1
 * @since 07.02.2025
 */
public class PriceList {
  private final Map<String, Integer> priceRegister = new HashMap<>();

  /**
   * Constructor for the PriceList class.
   * Initializes prices for different services.
   */
  public PriceList() {
    priceRegister.put("RedGem", 1000);
    priceRegister.put("GreenGem", 4000);
    priceRegister.put("YellowGem", 2000);
    priceRegister.put("PlaneTicket", 3000);
    priceRegister.put("ShipTicket", 1500);
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
}