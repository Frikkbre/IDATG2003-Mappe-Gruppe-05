package edu.ntnu.idi.bidata.idatg2003mappe.app.missingdiamond;

import edu.ntnu.idi.bidata.idatg2003mappe.entity.Player;
import edu.ntnu.idi.bidata.idatg2003mappe.map.Tile;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * GUI class for the Missing Diamond game.
 *
 * @author Simen Gudbrandsen and Frikk Breadsroed
 * @version 0.0.3
 * @since 23.04.2025
 */
public class MissingDiamondGUI extends Application {

  // Game controller
  private MissingDiamondController gameController;
  private int numberOfPlayers = 2;

  // UI components
  private BorderPane mainLayout;
  private Pane boardPane;
  private TextArea scoreBoard;
  private TextArea gameLog;
  private Button rollDieButton;

  // Board data
  private Map<Integer, Circle> tileCircles = new HashMap<>();
  private Map<Player, Circle> playerMarkers = new HashMap<>();

  // Developer mode flags
  private boolean coordinateMode = false;
  private List<CoordinatePoint> capturedPoints = new ArrayList<>();
  private int nextPointId = 1;

  // Status label for coordinate mode
  private Label coordinateModeLabel;

  // Player colors
  private final Color[] playerColors = {
      Color.ORANGE, Color.INDIGO, Color.GREEN, Color.YELLOW, Color.BROWN, Color.PURPLE
  };

  // Location data with percentages of map width/height
  private static final Object[][] LOCATION_DATA = {
      // {id, name, x-percentage, y-percentage}
      {1, "Location1", 0.2386, 0.1492},
      {2, "Location2", 0.0752, 0.1938},
      {3, "Location3", 0.1784, 0.2015},
      {4, "Location4", 0.4363, 0.1615},
      {5, "Location5", 0.6856, 0.2015},
      {6, "Location6", 0.4836, 0.2138},
      {7, "Location7", 0.3482, 0.2708},
      {8, "Location8", 0.0258, 0.3569},
      {9, "Location9", 0.2493, 0.3492},
      {10, "Location10", 0.4728, 0.3585},
      {11, "Location11", 0.6061, 0.4108},
      {12, "Location12", 0.6555, 0.3108},
      {13, "Location13", 0.7845, 0.3538},
      {14, "Location14", 0.9736, 0.4400},
      {15, "Location15", 0.8189, 0.4585},
      {16, "Location16", 0.6620, 0.4708},
      {17, "Location17", 0.0795, 0.4354},
      {18, "Location18", 0.2257, 0.4662},
      {19, "Location19", 0.3396, 0.4769},
      {20, "Location20", 0.1612, 0.6815},
      {21, "Location21", 0.4470, 0.5262},
      {22, "Location22", 0.4298, 0.6354},
      {23, "Location23", 0.5910, 0.6077},
      {24, "Location24", 0.7350, 0.5277},
      {25, "Location25", 0.7974, 0.6046},
      {26, "Location26", 0.8081, 0.6954},
      {27, "Location27", 0.9392, 0.7323},
      {28, "Location28", 0.6125, 0.7369},
      {29, "Location29", 0.4406, 0.7723},
      {30, "Location30", 0.6727, 0.8062},
      {31, "Location31", 0.8489, 0.8246},
      {32, "Location32", 0.4965, 0.8985},
  };

  // Connection data for paths between locations
  private static final int[][] CONNECTIONS = {
      // North Africa
      {1, 2}, {1, 3}, // Tangier to Marrakech, Tripoli
      {2, 19}, // Marrakech to Timbuktu
      {3, 4}, {3, 6}, // Tripoli to Cairo, Sahara
      {4, 5}, // Cairo to Egypt
      {5, 13}, // Egypt to Suez
      {6, 7}, // Sahara to Darfur

      // West Africa
      {7, 9}, {7, 10}, // Darfur to Slave Coast, Gold Coast
      {8, 17}, {8, 20}, // St. Helena to Dakar, Congo
      {9, 18}, {9, 19}, // Slave Coast to Ivory Coast, Timbuktu

      // Central Africa
      {10, 11}, {10, 12}, // Gold Coast to Guinea, Lagos
      {11, 15}, {11, 16}, // Guinea to Wadai, Chad
      {12, 13}, // Lagos to Suez
      {13, 14}, // Suez to Addis Ababa
      {14, 15}, // Addis Ababa to Wadai

      // East Africa
      {15, 16}, {15, 24}, // Wadai to Chad, Mombasa
      {16, 21}, {16, 24}, // Chad to Zanzibar, Mombasa

      // West Coast
      {17, 18}, // Dakar to Ivory Coast
      {18, 19}, // Ivory Coast to Timbuktu
      {19, 21}, // Timbuktu to Zanzibar
      {20, 29}, // Congo to Cape Town

      // Central Paths
      {21, 22}, {21, 23}, // Zanzibar to Victoria Falls, Dar es Salaam
      {22, 29}, // Victoria Falls to Cape Town
      {23, 24}, {23, 25}, // Dar es Salaam to Mombasa, Madagascar
      {25, 26}, {25, 28}, // Madagascar to Mozambique, Walvis Bay
      {26, 27}, // Mozambique to Mauritius
      {27, 31}, // Mauritius to Durban

      // South Africa
      {28, 29}, {28, 30}, // Walvis Bay to Cape Town, Port Elizabeth
      {29, 32}, // Cape Town to Cape of Good Hope
      {30, 31}, // Port Elizabeth to Durban
  };

  // Class to store coordinate points
  private static class CoordinatePoint {
    int id;
    double x, y;
    double xPercent, yPercent;
    Circle circle;
    Label label;
  }

  @Override
  public void start(Stage primaryStage) {
    gameController = new MissingDiamondController(numberOfPlayers);

    mainLayout = new BorderPane();
    mainLayout.setPrefSize(1440, 840);
    mainLayout.setStyle("-fx-background-color: white;");
    primaryStage.setTitle("Missing Diamond");

    MenuBar menuBar = createMenuBar();
    coordinateModeLabel = new Label("COORDINATE MODE: Click on map to place points");
    coordinateModeLabel.setStyle("""
        -fx-background-color: red;
        -fx-text-fill: white;
        -fx-padding: 5px;
        -fx-font-weight: bold;
      """);
    coordinateModeLabel.setVisible(false);
    mainLayout.setTop(new VBox(menuBar, coordinateModeLabel));

    VBox leftSidebar = createLeftPanel();
    leftSidebar.setPrefWidth(250);
    leftSidebar.setMinWidth(250);
    leftSidebar.setMaxWidth(250);
    leftSidebar.setPadding(new Insets(10));
    leftSidebar.setBorder(new Border(new BorderStroke(
        Color.LIGHTGRAY, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT
    )));

    GridPane grid = new GridPane();

    ColumnConstraints col0 = new ColumnConstraints();
    col0.setMinWidth(250);
    col0.setMaxWidth(250);
    col0.setPrefWidth(250);

    ColumnConstraints col1 = new ColumnConstraints();
    col1.setHgrow(Priority.ALWAYS);

    grid.getColumnConstraints().addAll(col0, col1);
    grid.setPadding(Insets.EMPTY);
    grid.setVgap(0);
    grid.setHgap(0);

    grid.add(leftSidebar, 0, 0);

    StackPane mapContainer = new StackPane();
    //mapContainer.setStyle("-fx-background-color: lightgray;");  // debug: see its full area
    mapContainer.setAlignment(Pos.CENTER);

    boardPane = createBoardPane();
    mapContainer.getChildren().add(boardPane);

    grid.add(mapContainer, 1, 0);

    RowConstraints row = new RowConstraints();
    row.setVgrow(Priority.ALWAYS);
    grid.getRowConstraints().add(row);

    mainLayout.setCenter(grid);

    Scene scene = new Scene(mainLayout);
    primaryStage.setScene(scene);
    primaryStage.show();

    updateBoardUI();
  }

  private MenuBar createMenuBar() {
    // File menu
    Menu fileMenu = new Menu("File");
    MenuItem exitItem = new MenuItem("Exit");
    exitItem.setOnAction(e -> System.exit(0));
    fileMenu.getItems().add(exitItem);

    // Developer Tools menu
    Menu devMenu = new Menu("Developer Tools");

    CheckMenuItem coordModeItem = new CheckMenuItem("Coordinate Mode");
    coordModeItem.setOnAction(e -> toggleCoordinateMode(coordModeItem.isSelected()));

    MenuItem copyItem = new MenuItem("Copy Coordinates to Clipboard");
    copyItem.setOnAction(e -> copyCoordinatesToClipboard());

    MenuItem clearItem = new MenuItem("Clear Coordinate Points");
    clearItem.setOnAction(e -> clearCoordinatePoints());

    devMenu.getItems().addAll(coordModeItem, new SeparatorMenuItem(), copyItem, clearItem);

    MenuBar menuBar = new MenuBar();
    menuBar.getMenus().addAll(fileMenu, devMenu);
    menuBar.setStyle("-fx-background-color: #57B9FF;");

    return menuBar;
  }

  private void toggleCoordinateMode(boolean enabled) {
    this.coordinateMode = enabled;
    coordinateModeLabel.setVisible(enabled);

    if (enabled) {
      // Disable game controls when in coordinate mode
      rollDieButton.setDisable(true);
      gameLog.appendText("Coordinate mode enabled. Click on the map to place points.\n");
    } else {
      // Enable game controls when leaving coordinate mode
      rollDieButton.setDisable(false);
      gameLog.appendText("Coordinate mode disabled. Game controls restored.\n");
    }
  }

  private void clearCoordinatePoints() {
    // Remove all coordinate circles and labels
    for (CoordinatePoint point : capturedPoints) {
      boardPane.getChildren().remove(point.circle);
      boardPane.getChildren().remove(point.label);
    }
    capturedPoints.clear();
    nextPointId = 1;
    gameLog.appendText("All coordinate points cleared.\n");
  }

  private void copyCoordinatesToClipboard() {
    if (capturedPoints.isEmpty()) {
      gameLog.appendText("No coordinate points to copy.\n");
      return;
    }

    StringBuilder sb = new StringBuilder();
    sb.append("// Location data with percentages of map width/height\n");
    sb.append("private static final Object[][] LOCATION_DATA = {\n");
    sb.append("    // {id, name, x-percentage, y-percentage}\n");

    for (CoordinatePoint point : capturedPoints) {
      sb.append(String.format("    {%d, \"Location%d\", %.4f, %.4f},\n",
          point.id, point.id, point.xPercent, point.yPercent));
    }

    sb.append("};\n");

    // Copy to clipboard
    Clipboard clipboard = Clipboard.getSystemClipboard();
    ClipboardContent content = new ClipboardContent();
    content.putString(sb.toString());
    clipboard.setContent(content);

    gameLog.appendText("Copied " + capturedPoints.size() + " coordinate points to clipboard.\n");
  }

  private VBox createLeftPanel() {
    VBox panel = new VBox(10);

    // Create scoreboard
    scoreBoard = new TextArea();
    scoreBoard.setPrefHeight(200);
    scoreBoard.setEditable(false);

    // Create roll button
    rollDieButton = new Button("Roll Die");
    rollDieButton.setMaxWidth(Double.MAX_VALUE);
    rollDieButton.setPrefHeight(40);
    rollDieButton.setOnAction(e -> {
      String result = gameController.playTurn();
      gameLog.appendText(result + "\n");
      highlightPossibleMoves();
      updateBoardUI();
    });

    // Create game log
    gameLog = new TextArea();
    gameLog.setPrefHeight(400);
    gameLog.setEditable(false);

    // Add components to panel
    panel.getChildren().addAll(scoreBoard, rollDieButton, gameLog);

    // Update scoreboard initially
    updateScoreBoard();

    return panel;
  }

  //Creates the board pane with the game tiles.
  private Pane createBoardPane() {
    StackPane root = new StackPane();
    root.setPrefSize(900, 700);
    root.setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);

    Image mapImage = new Image(getClass().getResourceAsStream("/images/afrikan_tahti_map.jpg"));
    ImageView mapView = new ImageView(mapImage);
    mapView.setFitWidth(900);
    mapView.setFitHeight(700);
    mapView.setPreserveRatio(true);

    root.getChildren().add(mapView);

    Pane overlay = new Pane();
    overlay.setPickOnBounds(false);
    overlay.setPrefSize(900, 700);
    overlay.setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);

    mapView.boundsInParentProperty().addListener((obs, old, bounds) -> {
      overlay.setPrefSize(bounds.getWidth(), bounds.getHeight());
      overlay.setMaxSize(bounds.getWidth(), bounds.getHeight());
    });

    overlay.setOnMouseClicked(event -> {
      if (coordinateMode) {
        handleCoordinateClick(event.getX(), event.getY(), mapView);
      } else {
        handleGameClick(event.getX(), event.getY());
      }
    });

    root.getChildren().add(overlay);

    createGameLocations(overlay, mapView);

    return root;
  }


  private void handleCoordinateClick(double x, double y, ImageView mapView) {
    // Get actual dimensions
    double actualWidth = mapView.getBoundsInLocal().getWidth();
    double actualHeight = mapView.getBoundsInLocal().getHeight();

    // Ensure click is within map bounds
    if (x > actualWidth || y > actualHeight) {
      gameLog.appendText("Click outside map bounds.\n");
      return;
    }

    // Calculate percentages
    double xPercent = x / actualWidth;
    double yPercent = y / actualHeight;

    // Create new coordinate point
    CoordinatePoint point = new CoordinatePoint();
    point.id = nextPointId++;
    point.x = x;
    point.y = y;
    point.xPercent = xPercent;
    point.yPercent = yPercent;

    // Create and add circle
    Circle circle = new Circle(x, y, 8, Color.ORANGE);
    circle.setStroke(Color.BLACK);
    circle.setStrokeWidth(1.5);
    point.circle = circle;

    // Create and add label
    Label label = new Label("Location" + point.id);
    label.setLayoutX(x + 10);
    label.setLayoutY(y - 10);
    label.setTextFill(Color.WHITE);
    label.setStyle("-fx-background-color: rgba(0,0,0,0.7); -fx-padding: 2px; -fx-font-size: 10pt;");
    point.label = label;

    // Add to board
    boardPane.getChildren().addAll(circle, label);

    // Add to list
    capturedPoints.add(point);

    // Log info
    String info = String.format("Added Location%d at (%.0f, %.0f) - Percentage: (%.4f, %.4f)",
        point.id, x, y, xPercent, yPercent);
    gameLog.appendText(info + "\n");
  }

  private void handleGameClick(double x, double y) {
    // Get tile id from clicked point (if any)
    for (Map.Entry<Integer, Circle> entry : tileCircles.entrySet()) {
      Circle circle = entry.getValue();
      double distance = Math.sqrt(
          Math.pow(circle.getCenterX() - x, 2) +
              Math.pow(circle.getCenterY() - y, 2)
      );

      if (distance <= circle.getRadius()) {
        // Tile was clicked
        handleTileClick(entry.getKey());
        return;
      }
    }
  }

  private void createGameLocations(Pane pane, ImageView mapView) {
    // Get the actual rendered dimensions of the displayed map
    double mapWidth = mapView.getBoundsInLocal().getWidth();
    double mapHeight = mapView.getBoundsInLocal().getHeight();

    // Log actual dimensions for debugging
    System.out.println("Actual map dimensions: " + mapWidth + "x" + mapHeight);

    // Create and position the locations
    for (Object[] data : LOCATION_DATA) {
      int tileId = (int) data[0];
      String name = (String) data[1];

      // Calculate actual coordinates based on percentages
      double xPercent = ((Number) data[2]).doubleValue();
      double yPercent = ((Number) data[3]).doubleValue();
      double x = mapWidth * xPercent;
      double y = mapHeight * yPercent;

      Circle tile = createTileCircle(x, y, tileId);

      // Add name label with better visibility
      Label label = new Label(name);
      label.setLayoutX(x + 5);
      label.setLayoutY(y - 15);
      label.setTextFill(Color.WHITE);
      label.setStyle("-fx-background-color: rgba(0,0,0,0.7); -fx-padding: 2px; -fx-font-size: 8pt;");

      pane.getChildren().addAll(tile, label);
      tileCircles.put(tileId, tile);
    }

    // Create connections after adding all tiles
    createPaths(pane);
  }

  private void createPaths(Pane pane) {
    // Create the paths between locations
    for (int[] connection : CONNECTIONS) {
      if (connection.length < 2) continue; // Skip invalid connections

      int fromId = connection[0];
      int toId = connection[1];

      // Find the circles for these IDs
      Circle fromCircle = tileCircles.get(fromId);
      Circle toCircle = tileCircles.get(toId);

      if (fromCircle != null && toCircle != null) {
        Line line = new Line(
            fromCircle.getCenterX(), fromCircle.getCenterY(),
            toCircle.getCenterX(), toCircle.getCenterY()
        );
        line.setStroke(Color.BLACK);
        line.setStrokeWidth(1.5);

        // Put lines under the circles
        pane.getChildren().add(0, line);
      } else {
        System.out.println("Warning: Missing circle for connection " + fromId + " to " + toId);
      }
    }
  }


  private Circle createTileCircle(double x, double y, int tileId) {
    Circle tile = new Circle(x, y, 10, Color.RED);
    tile.setStroke(Color.BLACK);
    tile.setStrokeWidth(1.5);
    tile.setUserData(tileId);

    // Add click event
    tile.setOnMouseClicked(e -> handleTileClick(tileId));

    return tile;
  }

  //Updates the scoreboard with current player positions.
  private void updateScoreBoard() {
    scoreBoard.clear();
    scoreBoard.appendText("Scoreboard:\n");

    List<Player> players = gameController.getPlayers();
    for (Player player : players) {
      scoreBoard.appendText(player.getName() + ": Tile " +
          player.getCurrentTile().getTileId() + "\n");
    }
  }

  //Highlights the possible moves for the current player.
  private void highlightPossibleMoves() {
    // Reset all tiles to red
    for (Circle tile : tileCircles.values()) {
      tile.setFill(Color.RED);
    }

    // Only highlight possible moves if die has been rolled
    if (gameController.hasRolled()) {
      // Highlight possible moves in yellow
      List<Tile> possibleMoves = gameController.getPossibleMoves();
      for (Tile tile : possibleMoves) {
        Circle tileCircle = tileCircles.get(tile.getTileId());
        if (tileCircle != null) {
          tileCircle.setFill(Color.YELLOW);
        }
      }
    }
  }

  //Updates the board UI with current game state.
  private void updateBoardUI() {
    // First clear existing player markers from the board
    for (Circle marker : playerMarkers.values()) {
      // Find the Pane that contains the game elements
      Pane gamePane = (Pane) boardPane;
      gamePane.getChildren().remove(marker);
    }
    playerMarkers.clear();

    // Add player markers at their current positions
    List<Player> players = gameController.getPlayers();
    for (int i = 0; i < players.size(); i++) {
      Player player = players.get(i);
      int tileId = player.getCurrentTile().getTileId();
      Circle tileCircle = tileCircles.get(tileId);

      if (tileCircle != null) {
        // Create offset for player marker based on player index
        double offsetX = (i % 2 == 0) ? -15 : 15;
        double offsetY = (i < 2) ? -15 : 15;

        // Create player marker
        Circle playerMarker = new Circle(
            tileCircle.getCenterX() + offsetX,
            tileCircle.getCenterY() + offsetY,
            8,
            playerColors[i]
        );
        playerMarker.setStroke(Color.BLACK);
        playerMarker.setStrokeWidth(1.5);

        // Add to board - ensure we're adding to the correct Pane
        Pane gamePane = (Pane) boardPane;
        gamePane.getChildren().add(playerMarker);
        playerMarkers.put(player, playerMarker);
      }
    }

    // Update scoreboard
    updateScoreBoard();
  }

  //Handles a click on a tile.
  private void handleTileClick(int tileId) {
    // Only allow moves if the player has rolled
    if (!gameController.hasRolled()) {
      gameLog.appendText("You must roll the die first.\n");
      return;
    }

    // Check if the tile is a valid move
    List<Tile> possibleMoves = gameController.getPossibleMoves();
    boolean validMove = possibleMoves.stream()
        .anyMatch(tile -> tile.getTileId() == tileId);

    if (validMove) {
      // Move the player to the selected tile
      String moveResult = gameController.movePlayer(tileId);
      gameLog.appendText(moveResult + "\n");

      // Reset highlighting
      for (Circle tile : tileCircles.values()) {
        tile.setFill(Color.RED);
      }

      // Update the board
      updateBoardUI();
    } else {
      gameLog.appendText("Cannot move to tile " + tileId + ".\n");
    }
  }

  //Sets the number of players for the game.
  public void setNumberOfPlayers(int numberOfPlayers) {
    if (numberOfPlayers >= 2 && numberOfPlayers <= 6) {
      this.numberOfPlayers = numberOfPlayers;
    } else {
      throw new IllegalArgumentException("Number of players must be between 2 and 6");
    }
  }

  //Main method to launch the application.
  public static void main(String[] args) {
    launch(args);
  }
}