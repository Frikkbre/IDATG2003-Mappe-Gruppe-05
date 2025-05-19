package edu.ntnu.idi.bidata.idatg2003mappe.app.missingdiamond;

import edu.ntnu.idi.bidata.idatg2003mappe.entity.Player;
import edu.ntnu.idi.bidata.idatg2003mappe.map.Tile;
import edu.ntnu.idi.bidata.idatg2003mappe.util.MapDesignerListener;
import edu.ntnu.idi.bidata.idatg2003mappe.util.MapDesignerTool;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.stage.Stage;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * GUI class for the Missing Diamond game.
 *
 * @author Simen Gudbrandsen and Frikk Breadsroed
 * @version 0.0.4
 * @since 23.04.2025
 */
public class MissingDiamondGUI extends Application implements MapDesignerListener {

  // Game controller
  private MissingDiamondController gameController;
  private int numberOfPlayers = 2;

  // UI components
  private BorderPane mainLayout;
  private StackPane boardPane;
  private Pane overlayPane;
  private ImageView mapView;
  private TextArea scoreBoard;
  private TextArea gameLog;
  private Button rollDieButton;

  // Board data
  private Map<Integer, Circle> tileCircles = new HashMap<>();
  private Map<Player, Circle> playerMarkers = new HashMap<>();

  // Map designer tool
  private MapDesignerTool mapDesigner;

  // Player colors
  private final Color[] playerColors = {
      Color.ORANGE, Color.INDIGO, Color.GREEN, Color.YELLOW, Color.BROWN, Color.PURPLE
  };

  // Location data with percentages of map width/height
  private static final Object[][] LOCATION_DATA = {
      // {id, name, x-percentage, y-percentage}

  };

  // Connection data for paths between locations
  private static final int[][] CONNECTIONS = {

  };

  @Override
  public void start(Stage primaryStage) {
    gameController = new MissingDiamondController(numberOfPlayers);

    mainLayout = new BorderPane();
    mainLayout.setPrefSize(1440, 840);
    mainLayout.setStyle("-fx-background-color: white;");
    primaryStage.setTitle("Missing Diamond");

    // Create board pane first to get overlay reference
    boardPane = createBoardPane();

    // Initialize the map designer (after overlay is created)
    mapDesigner = new MapDesignerTool(overlayPane, mapView.getFitWidth(), mapView.getFitHeight(), this);

    // Create the menu bar with designer menu
    MenuBar menuBar = createMenuBar();

    // Create developer controls HBox
    HBox devControls = new HBox(10);
    devControls.setPadding(new Insets(5));
    devControls.setAlignment(Pos.CENTER_LEFT);

    // Add map designer components
    devControls.getChildren().addAll(
        new Label("Tile Type:"), mapDesigner.getTileTypeSelector(),
        new Label("Source ID:"), mapDesigner.getSourceIdField(),
        new Label("Target ID:"), mapDesigner.getTargetIdField(),
        new Button("Create Connection") {{ setOnAction(e -> mapDesigner.createConnection()); }}
    );

    // Hide developer controls initially
    mapDesigner.getTileTypeSelector().setVisible(false);
    mapDesigner.getSourceIdField().setVisible(false);
    mapDesigner.getTargetIdField().setVisible(false);

    // Set up the top container with all elements
    VBox topContainer = new VBox(menuBar, mapDesigner.getStatusLabel(), devControls);
    mainLayout.setTop(topContainer);

    // Create left panel
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

    // Get designer menu from the map designer
    Menu devMenu = mapDesigner.createDesignerMenu();

    MenuBar menuBar = new MenuBar();
    menuBar.getMenus().addAll(fileMenu, devMenu);
    menuBar.setStyle("-fx-background-color: #57B9FF;");

    return menuBar;
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
  private StackPane createBoardPane() {
    StackPane root = new StackPane();
    root.setPrefSize(900, 700);
    root.setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);

    Image mapImage = new Image(getClass().getResourceAsStream("/images/afrikan_tahti_map.jpg"));
    mapView = new ImageView(mapImage);
    mapView.setFitWidth(900);
    mapView.setFitHeight(700);
    mapView.setPreserveRatio(true);

    root.getChildren().add(mapView);

    // Create a transparent pane for game elements
    overlayPane = new Pane();
    overlayPane.setPickOnBounds(true); // Make sure this is true to receive all clicks
    overlayPane.setPrefSize(900, 700);
    overlayPane.setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
    root.getChildren().add(overlayPane);

    overlayPane.setOnMouseClicked(e -> {
      // Only process clicks if in designer mode
      if (mapDesigner.isCoordinateMode()) {
        mapDesigner.handleCoordinateClick(e.getX(), e.getY(), mapView);
        e.consume(); // Prevent the event from bubbling up
      } else {
        handleGameClick(e.getX(), e.getY());
      }
    });

    mapView.boundsInParentProperty().addListener((obs, old, bounds) -> {
      overlayPane.setPrefSize(bounds.getWidth(), bounds.getHeight());
      overlayPane.setMaxSize(bounds.getWidth(), bounds.getHeight());

      // Update map designer dimensions
      if (mapDesigner != null) {
        mapDesigner.updateMapDimensions(bounds.getWidth(), bounds.getHeight());
      }
    });

    // Create game locations on the overlay
    createGameLocations(overlayPane, mapView);

    return root;
  }

  private void handleGameClick(double x, double y) {
    // Debug log
    System.out.println("Game click at: " + x + ", " + y);

    // Get tile id from clicked point (if any)
    for (Map.Entry<Integer, Circle> entry : tileCircles.entrySet()) {
      Circle circle = entry.getValue();
      double distance = Math.sqrt(
          Math.pow(circle.getCenterX() - x, 2) +
              Math.pow(circle.getCenterY() - y, 2)
      );

      if (distance <= circle.getRadius()) {
        // Tile was clicked
        System.out.println("Tile clicked: " + entry.getKey());
        handleTileClick(entry.getKey());
        return;
      }
    }
  }

  private void createGameLocations(Pane pane, ImageView mapView) {
    // Get the actual rendered dimensions of the displayed map
    double mapWidth = mapView.getFitWidth();
    double mapHeight = mapView.getFitHeight();

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

      // Check if the name contains "Special" to determine color
      boolean isSpecial = name.contains("Special");
      Color tileColor = isSpecial ? Color.RED : Color.BLACK;

      Circle tile = createTileCircle(x, y, tileId, tileColor);

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
        line.setUserData("connection"); // For identification

        // Put lines under the circles
        pane.getChildren().add(0, line);
      } else {
        System.out.println("Warning: Missing circle for connection " + fromId + " to " + toId);
      }
    }
  }

  private Circle createTileCircle(double x, double y, int tileId, Color color) {
    Circle tile = new Circle(x, y, 10, color);
    tile.setStroke(Color.WHITE);
    tile.setStrokeWidth(1.5);
    tile.setUserData(tileId);

    // Add click event
    tile.setOnMouseClicked(e -> {
      System.out.println("Circle clicked directly: " + tileId);
      handleTileClick(tileId);
      e.consume(); // Prevent event bubbling
    });

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
    // Reset all tiles to original colors (RED for special, BLACK for movement)
    for (Map.Entry<Integer, Circle> entry : tileCircles.entrySet()) {
      int tileId = entry.getKey();
      Circle tile = entry.getValue();

      // Determine original color based on location data
      boolean isSpecial = false;
      for (Object[] data : LOCATION_DATA) {
        if ((int)data[0] == tileId && ((String)data[1]).contains("Special")) {
          isSpecial = true;
          break;
        }
      }

      tile.setFill(isSpecial ? Color.RED : Color.BLACK);
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
      overlayPane.getChildren().remove(marker);
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

        // Add to overlay pane
        overlayPane.getChildren().add(playerMarker);
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

      // Reset highlighting and update the board
      highlightPossibleMoves();
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

  // MapDesignerListener implementation

  @Override
  public void onLogMessage(String message) {
    gameLog.appendText(message + "\n");
    System.out.println("Log: " + message); // Debug output
  }

  @Override
  public void onCoordinateModeToggled(boolean enabled) {
    // Show/hide UI elements
    mapDesigner.getTileTypeSelector().setVisible(enabled);
    mapDesigner.getSourceIdField().setVisible(enabled);
    mapDesigner.getTargetIdField().setVisible(enabled);

    // Disable/enable game controls
    rollDieButton.setDisable(enabled);

    // Debug output
    System.out.println("Coordinate mode toggled: " + enabled);
  }

  @Override
  public void onConnectionModeToggled(boolean enabled) {
    // Debug output
    System.out.println("Connection mode toggled: " + enabled);
  }

  @Override
  public void onMapDataExported(String data, boolean success) {
    // Debug output
    System.out.println("Map data exported: " + success);
  }

  //Main method to launch the application.
  public static void main(String[] args) {
    launch(args);
  }
}