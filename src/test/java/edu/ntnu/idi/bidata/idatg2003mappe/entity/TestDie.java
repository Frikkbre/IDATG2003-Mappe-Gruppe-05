package edu.ntnu.idi.bidata.idatg2003mappe.entity;

import static org.junit.jupiter.api.Assertions.*;

import edu.ntnu.idi.bidata.idatg2003mappe.entity.die.Die;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TestDie {

  private Die die;

  @BeforeEach
  void setUp() {
    die = new Die();
  }

  @AfterEach
  void tearDown() {
    die = null;
  }

  //Tests the rollDie method

  @Test
  void testRollDie() {
    die.rollDie();
    assertTrue(die.getDieValue() >= 1 && die.getDieValue() <= 6, "Die value should be between 1 and 6.");
  }

  @Test
  void testRollToTurnMarker () {
    die.rollToTurnMarker();
    assertTrue(die.getDieValue() >= 3 && die.getDieValue() <= 6, "Die value should be between 3 and 6.");
  }

  //Tests the getDieValue method

  @Test
  void testGetValue() {
    assertEquals(0, die.getDieValue(),
        "Initial die value should be 0.");
  }

  //Tests the setDieValue method, to see if the value is set correctly

  @Test
  void testSetValue() {
    die.setDieValue(3);
    assertEquals(3, die.getDieValue(),
        "Die value should be set to 3.");
  }

  //Tests the setDieValue method, to see if the value is set correctly when the value is negative

  @Test
  void testSetValueNegative() {
    assertThrows(IllegalArgumentException.class, () -> die.setDieValue(-1),
        "Expected an IllegalArgumentException when setting a negative value.");
  }
}
