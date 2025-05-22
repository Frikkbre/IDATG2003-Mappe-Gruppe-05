package edu.ntnu.idi.bidata.idatg2003mappe.app.missingdiamond.ui;

import edu.ntnu.idi.bidata.idatg2003mappe.util.map.MapDesignerListener;
import edu.ntnu.idi.bidata.idatg2003mappe.util.map.MapDesignerTool;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;

import java.util.logging.Logger;

/**
 * Manager class for the map designer tool.
 */
public class MapDesignerManager {
  private final MapDesignerTool mapDesigner;
  private int connectionSourceId = -1;

  /*
    * Logger for logging messages.
   */
  private final Logger logger = Logger.getLogger(MapDesignerManager.class.getName());

  public MapDesignerManager(Pane overlayPane, double width, double height,
                            MapDesignerListener listener) {
    this.mapDesigner = new MapDesignerTool(overlayPane, width, height, listener);
  }

  public void createConnection() {
    mapDesigner.createConnection();
  }

  public boolean createDirectConnection(int sourceId, int targetId) {
    boolean success = mapDesigner.createDirectConnection(sourceId, targetId);
    if (success) {
      logger.info("Connection created successfully between " + sourceId + " and " + targetId);
    } else {
      logger.warning("Failed to create connection between " + sourceId + " and " + targetId);
    }
    return success;
  }

  public void registerExistingPoint(int id, double x, double y, double xPercent, double yPercent,
                                    String name, boolean isSpecial) {
    mapDesigner.registerExistingPoint(id, x, y, xPercent, yPercent, name, isSpecial);
  }

  public void resetConnectionSourceId() {
    connectionSourceId = -1;
  }

  public int getConnectionSourceId() {
    return connectionSourceId;
  }

  public void setConnectionSourceId(int id) {
    this.connectionSourceId = id;
  }

  public Label getStatusLabel() {
    return mapDesigner.getStatusLabel();
  }

  public ChoiceBox<String> getTileTypeSelector() {
    return mapDesigner.getTileTypeSelector();
  }

  public TextField getSourceIdField() {
    return mapDesigner.getSourceIdField();
  }

  public TextField getTargetIdField() {
    return mapDesigner.getTargetIdField();
  }

  public boolean isCoordinateMode() {
    return mapDesigner.isCoordinateMode();
  }

  public boolean isConnectionMode() {
    return mapDesigner.isConnectionMode();
  }

  public MapDesignerTool getMapDesignerTool() {
    return mapDesigner;
  }
}