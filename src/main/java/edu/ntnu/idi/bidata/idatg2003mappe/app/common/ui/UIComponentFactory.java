package edu.ntnu.idi.bidata.idatg2003mappe.app.common.ui;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;

/**
 * <p>Factory class for creating standardized UI components.</p>
 * <p>This utility class provides methods for creating consistently styled
 * UI components used throughout the application.</p>
 *
 * @author Simen Gudbrandsen and Frikk Breadsroed
 * @version 0.0.1
 * @since 21.05.2025
 */
public class UIComponentFactory {

  /**
   * <p>Constructs a new UIComponentFactory instance.</p>
   * <p>Typically, this class would be used via its static methods rather than being instantiated.</p>
   */
  public UIComponentFactory() {
    // Constructor is public for flexibility
  }

  /**
   * <p>Creates an action button with standardized styling.</p>
   * <p>The button will have maximum width, fixed height, and the specified action handler.</p>
   *
   * @param text   The text to display on the button
   * @param action The EventHandler to execute when the button is clicked
   * @return A styled Button instance ready for use
   */
  public static Button createActionButton(String text, EventHandler<ActionEvent> action) {
    Button button = new Button(text);
    button.setMaxWidth(Double.MAX_VALUE);
    button.setPrefHeight(40);
    button.setOnAction(action);
    return button;
  }

  /**
   * <p>Creates a standardized game log text area.</p>
   * <p>The text area will have fixed height and will be read-only to display game events.</p>
   *
   * @return A configured TextArea instance for game logging
   */
  public static TextArea createGameLog() {
    TextArea log = new TextArea();
    log.setPrefHeight(400);
    log.setEditable(false);
    return log;
  }
}
