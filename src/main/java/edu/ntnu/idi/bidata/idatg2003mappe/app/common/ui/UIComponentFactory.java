package edu.ntnu.idi.bidata.idatg2003mappe.app.common.ui;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;

/**
 * Factory class for creating UI components.
 */
public class UIComponentFactory {

  /**
   * Creates an action button with standard styling.
   *
   * @param text The button text
   * @param action The action to perform when clicked
   * @return The created button
   */
  public static Button createActionButton(String text, EventHandler<ActionEvent> action) {
    Button button = new Button(text);
    button.setMaxWidth(Double.MAX_VALUE);
    button.setPrefHeight(40);
    button.setOnAction(action);
    return button;
  }

  /**
   * Creates a standard game log text area.
   *
   * @return The created text area
   */
  public static TextArea createGameLog() {
    TextArea log = new TextArea();
    log.setPrefHeight(400);
    log.setEditable(false);
    return log;
  }

  /**
   * Creates a standard scoreboard text area.
   *
   * @return The created text area
   */
  public static TextArea createScoreBoard() {
    TextArea scoreBoard = new TextArea();
    scoreBoard.setPrefHeight(200);
    scoreBoard.setEditable(false);
    return scoreBoard;
  }
}