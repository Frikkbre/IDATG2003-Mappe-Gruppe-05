package edu.ntnu.idi.bidata.idatg2003mappe.util.map;

import edu.ntnu.idi.bidata.idatg2003mappe.util.CoordinatePoint;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Circle;

/**
 * Manages UI components for the map designer.
 */
public class MapUIManager {
  private final Pane overlayPane;
  private final Label statusLabel;
  private final ChoiceBox<String> tileTypeSelector;
  private final TextField sourceIdField;
  private final TextField targetIdField;
  private final MapDesignerListener listener;

  // UI state flags
  private boolean coordinateMode = false;
  private boolean connectionMode = false;
  private int selectedSourceId = -1;

  /**
   * Creates a new MapUIManager.
   */
  public MapUIManager(Pane overlayPane, MapDesignerListener listener) {
    this.overlayPane = overlayPane;
    this.listener = listener;

    // Initialize UI components
    statusLabel = new Label("COORDINATE MODE: Click on map to place points");
    statusLabel.setStyle("-fx-background-color: red; -fx-text-fill: white; -fx-padding: 5px; -fx-font-weight: bold;");
    statusLabel.setVisible(false);

    tileTypeSelector = new ChoiceBox<>();
    tileTypeSelector.getItems().addAll("Black (Movement)", "Red (Special)");
    tileTypeSelector.setValue("Black (Movement)");

    sourceIdField = new TextField();
    sourceIdField.setPrefWidth(50);

    targetIdField = new TextField();
    targetIdField.setPrefWidth(50);
  }

  /**
   * Adds visual elements for a point to the overlay pane.
   */
  public void addPointVisuals(CoordinatePoint point) {
    // Create and add circle
    Circle circle = point.createCircle(point.isSpecial());
    overlayPane.getChildren().add(circle);

    // Create and add label
    Label label = point.createLabel(point.getName());
    if (label != null) {
      overlayPane.getChildren().add(label);
    }

    // Log info about what was added
    String typeStr = point.isSpecial() ? "Special" : "Movement";
    logMessage(String.format("Added %s %s at (%.0f, %.0f)",
        typeStr, point.getName(), point.getX(), point.getY()), false);
  }

  /**
   * Toggles coordinate mode.
   */
  public void toggleCoordinateMode(boolean enabled) {
    this.coordinateMode = enabled;
    statusLabel.setVisible(enabled);

    if (listener != null) {
      listener.onCoordinateModeToggled(enabled);
      logMessage(enabled ?
          "Coordinate mode enabled. Select tile type and click on the map." :
          "Coordinate mode disabled. Game controls restored.", false);
    }
  }

  /**
   * Toggles connection mode.
   */
  public void toggleConnectionMode() {
    connectionMode = !connectionMode;

    if (listener != null) {
      listener.onConnectionModeToggled(connectionMode);
      logMessage(connectionMode ?
          "Connection Mode enabled. Click on source tile, then target tile." :
          "Connection Mode disabled.", false);

      if (!connectionMode) {
        selectedSourceId = -1;
      }
    }
  }

  /**
   * Handles a click in connection mode.
   */
  public void handleConnectionModeClick(int tileId) {
    if (selectedSourceId == -1) {
      // First click - select source
      selectedSourceId = tileId;
      sourceIdField.setText(String.valueOf(tileId));
      logMessage("Selected source ID: " + tileId + ". Now click on target tile.", false);
    } else {
      // Second click - select target and create connection
      targetIdField.setText(String.valueOf(tileId));
      logMessage("Selected target ID: " + tileId, false);
      // Connection creation is handled by MapDesignerTool

      // Reset for next connection
      selectedSourceId = -1;
    }
  }

  /**
   * Logs a message if a listener is available.
   */
  public void logMessage(String message, boolean isExport) {
    if (listener != null) {
      listener.onLogMessage(message);
      if (isExport) {
        listener.onMapDataExported(message, true);
      }
    }
  }

  // Getters for UI components
  public Label getStatusLabel() { return statusLabel; }
  public ChoiceBox<String> getTileTypeSelector() { return tileTypeSelector; }
  public TextField getSourceIdField() { return sourceIdField; }
  public TextField getTargetIdField() { return targetIdField; }
  public boolean isCoordinateMode() { return coordinateMode; }
  public boolean isConnectionMode() { return connectionMode; }
  public Pane getOverlayPane() { return overlayPane; }
}