package edu.ntnu.idi.bidata.idatg2003mappe.util.map;

import edu.ntnu.idi.bidata.idatg2003mappe.util.CoordinatePoint;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Circle;

/**
 * <p>Manages UI components for the map designer.</p>
 * <p>This class is responsible for creating, managing, and updating the user interface
 * components used in the map designer. It handles mode toggling, visual feedback,
 * and user input for coordinates and connections.</p>
 *
 * @author Simen Gudbrandsen and Frikk Breadsroed
 * @version 0.0.3
 * @since 15.06.2025
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
   * <p>Creates a new MapUIManager with the specified overlay pane and listener.</p>
   * <p>This constructor initializes all UI components needed for the map designer,
   * including labels, selectors, and input fields. The components are configured
   * with appropriate default values and styles.</p>
   *
   * @param overlayPane  The JavaFX pane where visual elements will be displayed
   * @param listener     The listener that will receive UI events
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
   * <p>Adds visual elements for a point to the overlay pane.</p>
   * <p>This method creates and adds visual representations (circle and label)
   * for a coordinate point to the overlay pane. It handles the styling based
   * on whether the point is special or not.</p>
   *
   * @param point  The coordinate point to visualize
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
   * <p>Toggles coordinate mode.</p>
   * <p>This method enables or disables coordinate mode, which allows
   * users to place coordinate points on the map by clicking. It updates
   * the UI to provide visual feedback about the current mode.</p>
   *
   * @param enabled  Whether coordinate mode should be enabled
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
   * <p>Toggles connection mode.</p>
   * <p>This method toggles connection mode, which allows users to create
   * connections between coordinate points by clicking on them in sequence.
   * It updates the UI to provide visual feedback about the current mode.</p>
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
   * <p>Handles a click in connection mode.</p>
   * <p>This method processes clicks on coordinate points when connection mode is active.
   * It alternates between selecting a source point and a target point to create a connection.</p>
   *
   * @param tileId  The ID of the clicked tile
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
   * <p>Logs a message if a listener is available.</p>
   * <p>This method sends log messages to the registered {@link MapDesignerListener}
   * if one exists. It also optionally notifies about export events.</p>
   *
   * @param message   The message to log
   * @param isExport  Whether this message is related to an export operation
   */
  public void logMessage(String message, boolean isExport) {
    if (listener != null) {
      listener.onLogMessage(message);
      if (isExport) {
        listener.onMapDataExported(message, true);
      }
    }
  }

  /**
   * <p>Gets the status label.</p>
   * <p>This method returns the status label component that displays
   * information about the current state of the map designer.</p>
   *
   * @return The JavaFX Label component for status messages
   */
  public Label getStatusLabel() {
    return statusLabel;
  }

  /**
   * <p>Gets the tile type selector.</p>
   * <p>This method returns the choice box component that allows
   * selection of tile types (special or movement).</p>
   *
   * @return The JavaFX ChoiceBox component for tile type selection
   */
  public ChoiceBox<String> getTileTypeSelector() {
    return tileTypeSelector;
  }

  /**
   * <p>Gets the source ID input field.</p>
   * <p>This method returns the text field component for entering
   * the source tile ID when creating connections.</p>
   *
   * @return The JavaFX TextField component for the source ID
   */
  public TextField getSourceIdField() {
    return sourceIdField;
  }

  /**
   * <p>Gets the target ID input field.</p>
   * <p>This method returns the text field component for entering
   * the target tile ID when creating connections.</p>
   *
   * @return The JavaFX TextField component for the target ID
   */
  public TextField getTargetIdField() {
    return targetIdField;
  }

  /**
   * <p>Checks if coordinate mode is active.</p>
   * <p>This method returns whether the map designer is currently in coordinate mode,
   * where clicks on the map create new coordinate points.</p>
   *
   * @return {@code true} if coordinate mode is active, {@code false} otherwise
   */
  public boolean isCoordinateMode() {
    return coordinateMode;
  }

  /**
   * <p>Checks if connection mode is active.</p>
   * <p>This method returns whether the map designer is currently in connection mode,
   * where clicks on points create connections between them.</p>
   *
   * @return {@code true} if connection mode is active, {@code false} otherwise
   */
  public boolean isConnectionMode() {
    return connectionMode;
  }

  /**
   * <p>Gets the overlay pane.</p>
   * <p>This method returns the JavaFX pane where visual elements
   * for the map designer are displayed.</p>
   *
   * @return The JavaFX Pane component for the map overlay
   */
  public Pane getOverlayPane() {
    return overlayPane;
  }
}
