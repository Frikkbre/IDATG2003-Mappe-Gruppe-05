package edu.ntnu.idi.bidata.idatg2003mappe.app.common.ui;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;

/**
 * <p>Factory class for creating Material Design styled UI components.</p>
 * <p>This utility class provides methods for creating consistently styled
 * UI components following Material Design 3 principles.</p>
 *
 * @author Simen Gudbrandsen and Frikk Breadsroed
 * @version 0.2.0
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
   * <p>Creates an action button with Material Design styling.</p>
   * <p>The button will have maximum width, fixed height, and the specified action handler.</p>
   *
   * @param text   The text to display on the button
   * @param action The EventHandler to execute when the button is clicked
   * @return A styled Button instance ready for use
   */
  public static Button createActionButton(String text, EventHandler<ActionEvent> action) {
    Button button = new Button(text);
    button.setMaxWidth(Double.MAX_VALUE);
    button.setPrefHeight(44);
    button.getStyleClass().add("md-button-tonal");
    button.setOnAction(action);
    return button;
  }

  /**
   * <p>Creates a Material Design filled button (primary action).</p>
   *
   * @param text   The button text
   * @param action The click handler
   * @return A Material Design filled button
   */
  public static Button createFilledButton(String text, EventHandler<ActionEvent> action) {
    Button button = new Button(text);
    button.setMaxWidth(Double.MAX_VALUE);
    button.setPrefHeight(44);
    button.getStyleClass().add("md-button-filled");
    button.setOnAction(action);
    return button;
  }

  /**
   * <p>Creates a Material Design outlined button (secondary action).</p>
   *
   * @param text   The button text
   * @param action The click handler
   * @return A Material Design outlined button
   */
  public static Button createOutlinedButton(String text, EventHandler<ActionEvent> action) {
    Button button = new Button(text);
    button.setMaxWidth(Double.MAX_VALUE);
    button.setPrefHeight(44);
    button.getStyleClass().add("md-button-outlined");
    button.setOnAction(action);
    return button;
  }

  /**
   * <p>Creates a Material Design FAB (Floating Action Button).</p>
   *
   * @param text   The button text with icon
   * @param action The click handler
   * @return A Material Design FAB button
   */
  public static Button createFabButton(String text, EventHandler<ActionEvent> action) {
    Button button = new Button(text);
    button.setPrefHeight(56);
    button.getStyleClass().add("md-fab-extended");
    button.setOnAction(action);
    return button;
  }

  /**
   * <p>Creates a Material Design styled game log text area.</p>
   *
   * @return A configured TextArea instance for game logging
   */
  public static TextArea createGameLog() {
    TextArea log = new TextArea();
    log.setPrefHeight(200);
    log.setEditable(false);
    log.setWrapText(true);
    log.getStyleClass().add("md-game-log");
    return log;
  }

  /**
   * <p>Creates a Material Design card container.</p>
   *
   * @param styleClass The card style class (md-card, md-card-elevated, md-card-outlined, md-card-filled)
   * @param children   The child nodes to add to the card
   * @return A styled VBox container as a Material card
   */
  public static VBox createCard(String styleClass, Node... children) {
    VBox card = new VBox(12);
    card.getStyleClass().add(styleClass);
    card.setAlignment(Pos.TOP_LEFT);
    card.getChildren().addAll(children);
    return card;
  }

  /**
   * <p>Creates a Material Design label with specified typography style.</p>
   *
   * @param text       The label text
   * @param styleClass The typography style class
   * @return A styled Label instance
   */
  public static Label createLabel(String text, String styleClass) {
    Label label = new Label(text);
    label.getStyleClass().add(styleClass);
    return label;
  }

  /**
   * <p>Creates a Material Design headline label.</p>
   *
   * @param text The headline text
   * @return A headline styled Label
   */
  public static Label createHeadline(String text) {
    return createLabel(text, "md-headline-medium");
  }

  /**
   * <p>Creates a Material Design title label.</p>
   *
   * @param text The title text
   * @return A title styled Label
   */
  public static Label createTitle(String text) {
    return createLabel(text, "md-title-medium");
  }

  /**
   * <p>Creates a Material Design body text label.</p>
   *
   * @param text The body text
   * @return A body styled Label
   */
  public static Label createBody(String text) {
    return createLabel(text, "md-body-medium");
  }
}
