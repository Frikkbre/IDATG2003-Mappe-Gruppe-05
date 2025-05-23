package edu.ntnu.idi.bidata.idatg2003mappe.app.laddergame.ui;

import edu.ntnu.idi.bidata.idatg2003mappe.app.common.ui.NavBar;
import edu.ntnu.idi.bidata.idatg2003mappe.app.laddergame.controller.LadderGameController;
import edu.ntnu.idi.bidata.idatg2003mappe.entity.player.Player;
import edu.ntnu.idi.bidata.idatg2003mappe.filehandling.game.GameSaveLoadHandler;
import edu.ntnu.idi.bidata.idatg2003mappe.map.Tile;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Enhanced Ladder Game GUI with player circles overlay.
 * This class presents the game in a graphical user interface using circles
 * to represent players similar to the Missing Diamond game.
 *
 * @author Simen Gudbrandsen and Frikk Breadsroed
 * @version 0.4
 * @since 20.02.2025
 */
public class LadderGameGUI extends Application {
  private LadderGameController gameController;
  private GridPane boardGrid;
  private TextArea gameLog;
  private TextArea scoreBoard;
  public boolean randomLadders = false;
  public NavBar navBar;
  private final GameSaveLoadHandler gameSaveLoadHandler = new GameSaveLoadHandler();

  // Player circle management
  private Pane playerOverlay;
  private final Map<Player, Circle> playerCircles = new HashMap<>();
  private final Map<Integer, TextField> tileFields = new HashMap<>();

  // Constants for circle appearance
  private static final double CIRCLE_RADIUS = 15.0;
  private static final double TILE_WIDTH = 80.0;
  private static final double TILE_HEIGHT = 80.0;

  /**
   * Start the game.
   *
   * @param primaryStage the primary stage
   */
  @Override
  public void start(Stage primaryStage) {
    gameController = new LadderGameController(randomLadders);

    BorderPane borderPane = new BorderPane();
    borderPane.setPrefSize(1440, 840);

    navBar = new NavBar();
    navBar.setStage(primaryStage);
    navBar.setGameController(gameController);

    borderPane.setTop(navBar.createMenuBar());
    borderPane.setStyle("-fx-background-color: lightblue;");

    HBox centerBox = new HBox(10);
    centerBox.setAlignment(Pos.CENTER);

    // Create the board with overlay for player circles
    StackPane boardContainer = createBoardWithOverlay();

    Button rollDieButton = new Button("Roll die");
    rollDieButton.setOnAction(e -> {
      String message = gameController.playTurn();
      gameLog.appendText(message + "\n");
      updateBoardUI();

      if (message.contains("won")) {
        rollDieButton.setDisable(true);
      }
    });

    gameLog = new TextArea();
    gameLog.setEditable(false);
    gameLog.setPrefHeight(100);

    scoreBoard = createScoreBoard();

    VBox leftBox = new VBox(10);
    leftBox.setAlignment(Pos.CENTER_LEFT);
    leftBox.getChildren().addAll(scoreBoard, rollDieButton, gameLog);

    centerBox.getChildren().addAll(leftBox, boardContainer);
    borderPane.setCenter(centerBox);

    Scene scene = new Scene(borderPane);
    primaryStage.setScene(scene);
    primaryStage.setTitle("Ladder game");
    primaryStage.show();

    // Initialize player circles and update the board
    initializePlayerCircles();
    updateBoardUI();
  }

  /**
   * Creates the board with an overlay for player circles.
   *
   * @return StackPane containing the board and player overlay
   */
  private StackPane createBoardWithOverlay() {
    StackPane container = new StackPane();

    // Create the game board
    boardGrid = createBoardGrid();

    // Create overlay for player circles
    playerOverlay = new Pane();
    playerOverlay.setPickOnBounds(false); // Allow clicks to pass through to tiles

    // Add both to container
    container.getChildren().addAll(boardGrid, playerOverlay);

    return container;
  }

  /**
   * Create the board grid with correct tile numbering (1 at bottom left).
   *
   * @return the board grid
   */
  private GridPane createBoardGrid() {
    GridPane grid = new GridPane();
    grid.setAlignment(Pos.CENTER);

    boolean leftToRight = true;
    for (int row = 0; row <= 9; row++) {
      if (leftToRight) {
        for (int col = 0; col < 10; col++) {
          // Fixed: Use row instead of (9 - row) for tile numbering
          int tileNumber = row * 10 + col + 1;
          TextField tile = createTile(tileNumber);
          // Keep (9 - row) for grid placement to flip visual representation
          grid.add(tile, col, 9 - row);

          // Store tile reference for position calculations
          tileFields.put(tileNumber, tile);
        }
      } else {
        for (int col = 9; col >= 0; col--) {
          // Fixed: Use row instead of (9 - row) for tile numbering
          int tileNumber = row * 10 + (9 - col) + 1;
          TextField tile = createTile(tileNumber);
          // Keep (9 - row) for grid placement to flip visual representation
          grid.add(tile, col, 9 - row);

          // Store tile reference for position calculations
          tileFields.put(tileNumber, tile);
        }
      }
      leftToRight = !leftToRight;
    }
    return grid;
  }

  /**
   * Create a tile for the board.
   *
   * @param tileNumber the number of the tile
   * @return the tile
   */
  private TextField createTile(int tileNumber) {
    TextField tile = new TextField("" + tileNumber);
    tile.setPrefWidth(TILE_WIDTH);
    tile.setPrefHeight(TILE_HEIGHT);
    tile.setEditable(false);
    tile.setAlignment(Pos.CENTER);

    // Check if the tile has a ladder and style accordingly
    Tile currentTile = gameController.getTileByIdLinear(tileNumber);
    if (currentTile != null && currentTile.getDestinationTile() != null) {
      int destinationTileId = currentTile.getDestinationTile().getTileId();

      if (destinationTileId > tileNumber) {
        // Positive ladder (going up)
        tile.setStyle("-fx-background-color: lightgreen; -fx-font-weight: bold;");
      } else {
        // Negative ladder (going down - snake)
        tile.setStyle("-fx-background-color: lightcoral; -fx-font-weight: bold;");
      }

      tile.setText(tileNumber + " → " + destinationTileId);
    } else {
      // Regular tile styling
      tile.setStyle("-fx-background-color: white; -fx-border-color: black;");
    }

    return tile;
  }

  /**
   * Initialize player circles for all players.
   */
  private void initializePlayerCircles() {
    List<Player> players = gameController.getPlayers();

    for (Player player : players) {
      Circle playerCircle = createPlayerCircle(player);
      playerCircles.put(player, playerCircle);
      playerOverlay.getChildren().add(playerCircle);
    }
  }

  /**
   * Creates a circle for a player.
   *
   * @param player The player to create a circle for
   * @return Circle representing the player
   */
  private Circle createPlayerCircle(Player player) {
    Circle circle = new Circle(CIRCLE_RADIUS);

    // Set circle color based on player color
    try {
      Color color = Color.valueOf(player.getColor().toUpperCase());
      circle.setFill(color);
    } catch (IllegalArgumentException e) {
      // Fallback to a default color if player color is invalid
      circle.setFill(Color.BLUE);
    }

    circle.setStroke(Color.BLACK);
    circle.setStrokeWidth(2.0);

    // Add player name as tooltip or userData
    circle.setUserData(player.getName());

    return circle;
  }

  /**
   * Calculate the screen position for a player circle based on tile position.
   *
   * @param tileId The ID of the tile
   * @param playerIndex The index of the player (for offset calculation)
   * @return array containing [x, y] coordinates
   */
  private double[] calculatePlayerPosition(int tileId, int playerIndex) {
    TextField tileField = tileFields.get(tileId);
    if (tileField == null) {
      return new double[]{0, 0};
    }

    // Get tile position in the scene
    double tileX = tileField.getBoundsInParent().getMinX();
    double tileY = tileField.getBoundsInParent().getMinY();

    // Calculate center of tile
    double centerX = tileX + TILE_WIDTH / 2;
    double centerY = tileY + TILE_HEIGHT / 2;

    // Add small offset for multiple players on same tile
    double offsetX = (playerIndex % 2) * 20 - 10; // Alternate left/right
    double offsetY = (playerIndex / 2) * 20 - 10; // Stack vertically for more players

    return new double[]{centerX + offsetX, centerY + offsetY};
  }

  /**
   * Update the board UI with current player positions using circles.
   */
  public void updateBoardUI() {
    if (boardGrid == null || playerOverlay == null) {
      return;
    }

    List<Player> players = gameController.getPlayers();

    // Update each player's circle position
    for (int i = 0; i < players.size(); i++) {
      Player player = players.get(i);
      Circle playerCircle = playerCircles.get(player);

      if (playerCircle != null) {
        int tileId = player.getCurrentTile().getTileId();
        double[] position = calculatePlayerPosition(tileId, i);

        playerCircle.setCenterX(position[0]);
        playerCircle.setCenterY(position[1]);
      }
    }

    // Update scoreboard
    updateScoreBoard(scoreBoard);
  }

  /**
   * Create the scoreboard.
   *
   * @return the scoreboard
   */
  private TextArea createScoreBoard() {
    TextArea scoreBoard = new TextArea("Scoreboard:");
    scoreBoard.setPrefWidth(200);
    scoreBoard.setPrefHeight(130);
    scoreBoard.setEditable(false);
    return scoreBoard;
  }

  /**
   * Update the scoreBoard with the current player positions.
   *
   * @param scoreBoard takes in the TextArea scoreBoard to update
   */
  private void updateScoreBoard(TextArea scoreBoard) {
    scoreBoard.clear();

    ArrayList<Player> sortedPlayerPositionList = new ArrayList<>(gameController.getPlayers());
    sortedPlayerPositionList.sort((p1, p2) ->
        p2.getCurrentTile().getTileId() - p1.getCurrentTile().getTileId());

    StringBuilder scoreBoardText = new StringBuilder("Scoreboard:\n");
    for (Player player : sortedPlayerPositionList) {
      scoreBoardText.append(player.getName())
          .append(": Tile ")
          .append(player.getCurrentTile().getTileId())
          .append("\n");
    }

    scoreBoard.setText(scoreBoardText.toString());
  }

  private void restartGame(Stage primaryStage) {
    start(primaryStage);
  }

  public static void main(String[] args) {
    launch(args);
  }
}