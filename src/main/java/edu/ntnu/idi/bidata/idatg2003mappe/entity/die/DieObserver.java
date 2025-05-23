package edu.ntnu.idi.bidata.idatg2003mappe.entity.die;

/**
 * <p>Observer interface for die events.</p>
 * <p>This interface defines methods that are called when specific die events occur,
 * allowing components to react to changes in die state.</p>
 * <p>Classes implementing this interface can receive notifications when:</p>
 * <ul>
 *   <li>A die is rolled</li>
 *   <li>The die value changes</li>
 * </ul>
 * <p>This is part of the Observer design pattern implementation for die-related events.</p>
 *
 * @author Simen Gudbrandsen and Frikk Breadsroed
 * @version 0.0.1
 * @since 21.05.2025
 */
public interface DieObserver {

  /**
   * <p>Called when a die has been rolled.</p>
   * <p>This method is invoked by the observed {@link Die} object whenever
   * its value changes due to a roll. The new value is provided as a parameter.</p>
   *
   * @param value The value rolled.
   */
  void onDieRolled(int value);
}
