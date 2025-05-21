package edu.ntnu.idi.bidata.idatg2003mappe.app.missingdiamond.ui;

import edu.ntnu.idi.bidata.idatg2003mappe.util.MapDesignerListener;
import edu.ntnu.idi.bidata.idatg2003mappe.util.MapDesignerTool;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;

/**
 * Manager class for the map designer tool.
 */
public class MapDesignerManager {
  private final MapDesignerTool mapDesigner;
  private int connectionSourceId = -1;

  public MapDesignerManager(Pane overlayPane, double width, double height,
                            MapDesignerListener listener) {
    this.mapDesigner = new MapDesignerTool(overlayPane, width, height, listener);
  }

  public Menu createDesignerMenu() {
    return mapDesigner.createDesignerMenu();
  }

  public void createConnection() {
    mapDesigner.createConnection();
  }

  public boolean createDirectConnection(int sourceId, int targetId) {
    return mapDesigner.createDirectConnection(sourceId, targetId);
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
}