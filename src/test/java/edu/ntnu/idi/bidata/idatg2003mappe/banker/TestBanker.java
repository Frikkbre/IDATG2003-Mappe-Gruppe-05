package edu.ntnu.idi.bidata.idatg2003mappe.banker;

import static org.junit.jupiter.api.Assertions.*;

import edu.ntnu.idi.bidata.idatg2003mappe.entity.player.Player;
import edu.ntnu.idi.bidata.idatg2003mappe.map.Tile;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Test class for the Banker class.
 * Tests all banking operations including registration, deposits, withdrawals,
 * and balance inquiries with both positive and negative test cases.
 *
 * @author Simen Gudbrandsen and Frikk Breadsroed
 * @version 1.0.0
 * @since 23.5.2025
 */
@DisplayName("Banker Test Suite")
class TestBanker {

  private Banker banker;
  private Player testPlayer1;
  private Player testPlayer2;
  private Tile startingTile;

  @BeforeEach
  void setUp() {
    // Arrange - Set up fresh instances for each test
    banker = new Banker();
    startingTile = new Tile(1);
    testPlayer1 = new Player("Alice", 1, "Blue", startingTile);
    testPlayer2 = new Player("Bob", 2, "Red", startingTile);
  }

  @AfterEach
  void tearDown() {
    // Clean up after each test
    banker = null;
    testPlayer1 = null;
    testPlayer2 = null;
    startingTile = null;
  }

  @Test
  @DisplayName("Should register a new player with starting balance of 500")
  void testRegisterPlayer_Success() {
    // Arrange - Player created in setUp()

    // Act
    banker.registerPlayer(testPlayer1);

    // Assert
    assertEquals(500, banker.getBalance(testPlayer1),
        "New player should have starting balance of 500");
  }

  @Test
  @DisplayName("Should handle multiple player registrations independently")
  void testRegisterMultiplePlayers_IndependentBalances() {

    banker.registerPlayer(testPlayer1);
    banker.registerPlayer(testPlayer2);

    assertEquals(500, banker.getBalance(testPlayer1),
        "First player should have starting balance of 500");
    assertEquals(500, banker.getBalance(testPlayer2),
        "Second player should have starting balance of 500");
  }

  @Test
  @DisplayName("Should allow re-registration of same player (overwrites balance)")
  void testRegisterPlayer_ReRegistration() {
    // Arrange
    banker.registerPlayer(testPlayer1);
    banker.deposit(testPlayer1, 200);

    banker.registerPlayer(testPlayer1);

    assertEquals(500, banker.getBalance(testPlayer1),
        "Re-registered player should have balance reset to starting amount");
  }

  @Test
  @DisplayName("Should return zero balance for unregistered player")
  void testGetBalance_UnregisteredPlayer() {
    // Arrange - Player not registered

    // Act
    int balance = banker.getBalance(testPlayer1);

    // Assert
    assertEquals(0, balance,
        "Unregistered player should have zero balance");
  }

  @Test
  @DisplayName("Should return correct balance after transactions")
  void testGetBalance_AfterTransactions() {
    // Arrange
    banker.registerPlayer(testPlayer1);
    banker.deposit(testPlayer1, 300);
    banker.withdraw(testPlayer1, 100);

    // Act
    int balance = banker.getBalance(testPlayer1);

    // Assert
    assertEquals(700, balance,
        "Balance should be 500 + 300 - 100 = 700");
  }

  @Test
  @DisplayName("Should successfully deposit positive amount")
  void testDeposit_ValidAmount() {
    // Arrange
    banker.registerPlayer(testPlayer1);

    // Act
    banker.deposit(testPlayer1, 250);

    // Assert
    assertEquals(750, banker.getBalance(testPlayer1),
        "Balance should be 500 + 250 = 750 after deposit");
  }

  @Test
  @DisplayName("Should handle large deposits correctly")
  void testDeposit_LargeAmount() {
    banker.registerPlayer(testPlayer1);

    banker.deposit(testPlayer1, 10000);

    assertEquals(10500, banker.getBalance(testPlayer1),
        "Balance should handle large deposits correctly");
  }

  @Test
  @DisplayName("Should throw exception for zero deposit amount")
  void testDeposit_ZeroAmount_ThrowsException() {
    // Arrange
    banker.registerPlayer(testPlayer1);

    // Act & Assert
    assertThrows(IllegalArgumentException.class,
        () -> banker.deposit(testPlayer1, 0),
        "Depositing zero should throw IllegalArgumentException");
  }

  @Test
  @DisplayName("Should throw exception for negative deposit amount")
  void testDeposit_NegativeAmount_ThrowsException() {
    // Arrange
    banker.registerPlayer(testPlayer1);

    // Act & Assert
    IllegalArgumentException exception = assertThrows(
        IllegalArgumentException.class,
        () -> banker.deposit(testPlayer1, -100),
        "Depositing negative amount should throw IllegalArgumentException"
    );

    assertEquals("Amount must be positive", exception.getMessage(),
        "Exception should have correct message");
  }

  @Test
  @DisplayName("Should handle deposit for unregistered player")
  void testDeposit_UnregisteredPlayer() {
    // Arrange - Player not registered

    // Act
    banker.deposit(testPlayer1, 100);

    // Assert
    assertEquals(100, banker.getBalance(testPlayer1),
        "Deposit to unregistered player should create account with deposit amount");
  }

  // ==================== Withdrawal Tests ====================

  @Test
  @DisplayName("Should successfully withdraw when sufficient funds")
  void testWithdraw_SufficientFunds_Success() {
    // Arrange
    banker.registerPlayer(testPlayer1);

    // Act
    boolean result = banker.withdraw(testPlayer1, 200);

    // Assert
    assertTrue(result, "Withdrawal should succeed when sufficient funds");
    assertEquals(300, banker.getBalance(testPlayer1),
        "Balance should be 500 - 200 = 300 after withdrawal");
  }

  @Test
  @DisplayName("Should withdraw entire balance successfully")
  void testWithdraw_EntireBalance_Success() {
    // Arrange
    banker.registerPlayer(testPlayer1);

    // Act
    boolean result = banker.withdraw(testPlayer1, 500);

    // Assert
    assertTrue(result, "Should be able to withdraw entire balance");
    assertEquals(0, banker.getBalance(testPlayer1),
        "Balance should be zero after withdrawing all funds");
  }

  @Test
  @DisplayName("Should fail withdrawal when insufficient funds")
  void testWithdraw_InsufficientFunds_Failure() {
    // Arrange
    banker.registerPlayer(testPlayer1);

    // Act
    boolean result = banker.withdraw(testPlayer1, 600);

    // Assert
    assertFalse(result, "Withdrawal should fail when insufficient funds");
    assertEquals(500, banker.getBalance(testPlayer1),
        "Balance should remain unchanged after failed withdrawal");
  }

  @Test
  @DisplayName("Should throw exception for zero withdrawal amount")
  void testWithdraw_ZeroAmount_ThrowsException() {
    // Arrange
    banker.registerPlayer(testPlayer1);

    // Act & Assert
    assertThrows(IllegalArgumentException.class,
        () -> banker.withdraw(testPlayer1, 0),
        "Withdrawing zero should throw IllegalArgumentException");
  }

  @Test
  @DisplayName("Should throw exception for negative withdrawal amount")
  void testWithdraw_NegativeAmount_ThrowsException() {
    // Arrange
    banker.registerPlayer(testPlayer1);

    // Act & Assert
    IllegalArgumentException exception = assertThrows(
        IllegalArgumentException.class,
        () -> banker.withdraw(testPlayer1, -50),
        "Withdrawing negative amount should throw IllegalArgumentException"
    );

    assertEquals("Amount must be positive", exception.getMessage(),
        "Exception should have correct message");
  }

  @Test
  @DisplayName("Should fail withdrawal for unregistered player")
  void testWithdraw_UnregisteredPlayer_Failure() {
    // Arrange - Player not registered

    // Act
    boolean result = banker.withdraw(testPlayer1, 100);

    // Assert
    assertFalse(result,
        "Withdrawal should fail for unregistered player (zero balance)");
  }

  @Test
  @DisplayName("Should handle multiple sequential transactions correctly")
  void testMultipleTransactions_MaintainsCorrectBalance() {
    // Arrange
    banker.registerPlayer(testPlayer1);

    // Act - Perform multiple transactions
    banker.deposit(testPlayer1, 1000);  // Balance: 1500
    banker.withdraw(testPlayer1, 300);  // Balance: 1200
    banker.deposit(testPlayer1, 500);   // Balance: 1700
    banker.withdraw(testPlayer1, 700);  // Balance: 1000

    // Assert
    assertEquals(1000, banker.getBalance(testPlayer1),
        "Balance should be correct after multiple transactions");
  }

  @Test
  @DisplayName("Should maintain independent balances for multiple players")
  void testIndependentPlayerBalances() {
    // Arrange
    banker.registerPlayer(testPlayer1);
    banker.registerPlayer(testPlayer2);

    // Act
    banker.deposit(testPlayer1, 500);   // Player1: 1000
    banker.withdraw(testPlayer2, 100);  // Player2: 400
    banker.deposit(testPlayer2, 200);   // Player2: 600

    // Assert
    assertEquals(1000, banker.getBalance(testPlayer1),
        "Player 1 balance should not be affected by Player 2 transactions");
    assertEquals(600, banker.getBalance(testPlayer2),
        "Player 2 balance should reflect only their transactions");
  }

  // ==================== Boundary Tests ====================

  @Test
  @DisplayName("Should handle withdrawal of exactly 1 unit")
  void testWithdraw_MinimumValidAmount() {
    // Arrange
    banker.registerPlayer(testPlayer1);

    // Act
    boolean result = banker.withdraw(testPlayer1, 1);

    // Assert
    assertTrue(result, "Should be able to withdraw minimum valid amount");
    assertEquals(499, banker.getBalance(testPlayer1),
        "Balance should be 500 - 1 = 499");
  }

  @Test
  @DisplayName("Should handle deposit of exactly 1 unit")
  void testDeposit_MinimumValidAmount() {
    // Arrange
    banker.registerPlayer(testPlayer1);

    // Act
    banker.deposit(testPlayer1, 1);

    // Assert
    assertEquals(501, banker.getBalance(testPlayer1),
        "Balance should be 500 + 1 = 501");
  }
}