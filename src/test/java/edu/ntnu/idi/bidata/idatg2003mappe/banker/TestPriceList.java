package edu.ntnu.idi.bidata.idatg2003mappe.banker;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for the PriceList class.
 * Tests the initialization, retrieval and setting of prices for various services.
 *
 * @version 1.0.0
 * @since 1.0.0
 */
public class TestPriceList {

  private PriceList priceList;

  /**
   * Set up test environment before each test.
   */
  @BeforeEach
  void setUp() {
    priceList = new PriceList();
  }

  /**
   * Tear down test environment after each test.
   */
  @AfterEach
  void tearDown() {
    priceList = null;
  }

  /**
   * Test that getting the price of a non-existent service returns 0.
   */
  @Test
  void testGetPriceOfNonExistentService() {
    // Arrange
    String nonExistentService = "NonExistentService";

    // Act
    int price = priceList.getPrice(nonExistentService);

    // Assert
    assertEquals(0, price, "Price for a non-existent service should be 0");
  }

  /**
   * Test that setting a price for an existing service works correctly.
   */
  @Test
  void testSetPriceForExistingService() {
    // Arrange
    String service = "RedGem";
    int newPrice = 1500;

    // Act
    priceList.setPrice(service, newPrice);

    // Assert
    assertEquals(newPrice, priceList.getPrice(service), "Price should be updated to the new value");
  }

  /**
   * Test that setting a price for a new service works correctly.
   */
  @Test
  void testSetPriceForNewService() {
    // Arrange
    String newService = "NewService";
    int price = 2500;

    // Act
    priceList.setPrice(newService, price);

    // Assert
    assertEquals(price, priceList.getPrice(newService), "Price for the new service should be set correctly");
    assertTrue(priceList.hasService(newService), "The service should be added to the price list");
  }

  /**
   * Test that setting a negative price throws an IllegalArgumentException.
   */
  @Test
  void testSetNegativePrice() {
    // Arrange
    String service = "RedGem";
    int negativePrice = -100;

    // Act & Assert
    assertThrows(IllegalArgumentException.class, () -> priceList.setPrice(service, negativePrice),
        "Setting a negative price should throw an IllegalArgumentException");
  }

  /**
   * Test that setting a zero price is allowed.
   */
  @Test
  void testSetZeroPrice() {
    // Arrange
    String service = "RedGem";
    int zeroPrice = 0;

    // Act
    priceList.setPrice(service, zeroPrice);

    // Assert
    assertEquals(zeroPrice, priceList.getPrice(service), "Price should be updated to zero");
  }

  /**
   * Test the hasService method returns true for existing services.
   */
  @Test
  void testHasServiceForExistingService() {
    // Assert
    assertTrue(priceList.hasService("RedGem"), "hasService should return true for an existing service");
    assertTrue(priceList.hasService("GreenGem"), "hasService should return true for an existing service");
    assertTrue(priceList.hasService("TokenFlip"), "hasService should return true for an existing service");
  }

  /**
   * Test the hasService method returns false for non-existent services.
   */
  @Test
  void testHasServiceForNonExistentService() {
    // Assert
    assertFalse(priceList.hasService("NonExistentService"), "hasService should return false for a non-existent service");
  }

  /**
   * Test the getTokenFlipCost method returns the correct value.
   */
  @Test
  void testGetTokenFlipCost() {
    // Arrange
    int expectedCost = 300;

    // Act
    int actualCost = priceList.getTokenFlipCost();

    // Assert
    assertEquals(expectedCost, actualCost, "getTokenFlipCost should return the correct cost");
  }

  /**
   * Test the getTokenPurchaseCost method returns the correct value.
   */
  @Test
  void testGetTokenPurchaseCost() {
    // Arrange
    int expectedCost = 100;

    // Act
    int actualCost = priceList.getTokenPurchaseCost();

    // Assert
    assertEquals(expectedCost, actualCost, "getTokenPurchaseCost should return the correct cost");
  }

  /**
   * Test that changing the price of TokenFlip updates the value returned by getTokenFlipCost.
   */
  @Test
  void testUpdateTokenFlipCost() {
    // Arrange
    int newCost = 500;

    // Act
    priceList.setPrice("TokenFlip", newCost);

    // Assert
    assertEquals(newCost, priceList.getTokenFlipCost(), "getTokenFlipCost should return the updated cost");
  }

  /**
   * Test that changing the price of TokenPurchase updates the value returned by getTokenPurchaseCost.
   */
  @Test
  void testUpdateTokenPurchaseCost() {
    // Arrange
    int newCost = 200;

    // Act
    priceList.setPrice("TokenPurchase", newCost);

    // Assert
    assertEquals(newCost, priceList.getTokenPurchaseCost(), "getTokenPurchaseCost should return the updated cost");
  }
}