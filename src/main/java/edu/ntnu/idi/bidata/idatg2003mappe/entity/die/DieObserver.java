package edu.ntnu.idi.bidata.idatg2003mappe.entity.die;

/**
 * Observer interface for die events.
 *
 * @author Simen Gudbrandsen and Frikk Breadsroed
 * @version 0.0.1
 * @since 21.05.2025
 */
public interface DieObserver {

  /**
   * Called when a die has been rolled.
   *
   * @param value The value rolled.
   */
  void onDieRolled(int value);
}