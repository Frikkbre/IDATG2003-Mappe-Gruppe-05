package edu.ntnu.idi.bidata.idatg2003mappe.app.laddergame.ui;

import edu.ntnu.idi.bidata.idatg2003mappe.app.common.ui.NavBar;
import edu.ntnu.idi.bidata.idatg2003mappe.app.laddergame.controller.LadderGameController;
import edu.ntnu.idi.bidata.idatg2003mappe.entity.player.Player;
import edu.ntnu.idi.bidata.idatg2003mappe.filehandling.game.GameSaveLoadHandler;
import edu.ntnu.idi.bidata.idatg2003mappe.map.Tile;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

/**
 * Enhanced Ladder Game GUI with improved color scheme and design.
 * Features a modern, visually appealing interface with better contrast and readability.
 *
 * @author Simen Gudbrandsen and Frikk Breadsroed
 * @version 0.5
 * @since 20.02.2025
 */
public class LadderGameGUI extends Application {
  private LadderGameController gameController;
  private GridPane boardGrid;
  private TextArea gameLog;
  public TextArea scoreBoard;
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

  // Color scheme constants
  private static final String BACKGROUND_COLOR = "#2C3E50"; // Dark blue-gray
  private static final String BOARD_BACKGROUND = "#34495E"; // Lighter blue-gray
  private static final String TILE_COLOR = "#ECF0F1"; // Light gray
  private static final String LADDER_UP_COLOR = "#27AE60"; // Green
  private static final String LADDER_DOWN_COLOR = "#E74C3C"; // Red
  private static final String BUTTON_COLOR = "#3498DB"; // Blue
  private static final String BUTTON_HOVER_COLOR = "#2980B9"; // Darker blue
  private static final String TEXT_COLOR = "#2C3E50"; // Dark text
  private static final String LOG_BACKGROUND = "#BDC3C7"; // Light gray for logs

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
    borderPane.setStyle("-fx-background-color: " + BACKGROUND_COLOR + ";");

    navBar = new NavBar();
    navBar.setStage(primaryStage);
    navBar.setGameController(gameController);

    borderPane.setTop(navBar.createMenuBar());

    HBox centerBox = new HBox(20);
    centerBox.setAlignment(Pos.CENTER);
    centerBox.setPadding(new Insets(20));

    // Create the board with overlay for player circles
    StackPane boardContainer = createBoardWithOverlay();

    // Style the roll die button
    Button rollDieButton = new Button("Roll Die");
    rollDieButton.setPrefSize(200, 50);
    rollDieButton.setStyle(
        "-fx-background-color: " + BUTTON_COLOR + ";" +
            "-fx-text-fill: white;" +
            "-fx-font-size: 18px;" +
            "-fx-font-weight: bold;" +
            "-fx-background-radius: 10;" +
            "-fx-cursor: hand;"
    );
    rollDieButton.setOnMouseEntered(e ->
        rollDieButton.setStyle(
            "-fx-background-color: " + BUTTON_HOVER_COLOR + ";" +
                "-fx-text-fill: white;" +
                "-fx-font-size: 18px;" +
                "-fx-font-weight: bold;" +
                "-fx-background-radius: 10;" +
                "-fx-cursor: hand;"
        )
    );
    rollDieButton.setOnMouseExited(e ->
        rollDieButton.setStyle(
            "-fx-background-color: " + BUTTON_COLOR + ";" +
                "-fx-text-fill: white;" +
                "-fx-font-size: 18px;" +
                "-fx-font-weight: bold;" +
                "-fx-background-radius: 10;" +
                "-fx-cursor: hand;"
        )
    );

    rollDieButton.setOnAction(e -> {
      String message = gameController.playTurn();
      gameLog.appendText(message + "\n");
      updateBoardUI();

      if (message.contains("won")) {
        rollDieButton.setDisable(true);
      }
    });

    // Style the game log
    gameLog = new TextArea();
    gameLog.setEditable(false);
    gameLog.setPrefHeight(150);
    gameLog.setStyle(
        "-fx-control-inner-background: " + LOG_BACKGROUND + ";" +
            "-fx-font-family: 'Consolas', monospace;" +
            "-fx-font-size: 14px;" +
            "-fx-text-fill: " + TEXT_COLOR + ";" +
            "-fx-border-color: #95A5A6;" +
            "-fx-border-radius: 5;" +
            "-fx-background-radius: 5;"
    );

    scoreBoard = createScoreBoard();

    VBox leftBox = new VBox(20);
    leftBox.setAlignment(Pos.CENTER_LEFT);
    leftBox.setPadding(new Insets(10));
    leftBox.setStyle(
        "-fx-background-color: rgba(52, 73, 94, 0.7);" +
            "-fx-background-radius: 10;" +
            "-fx-padding: 20;"
    );
    leftBox.getChildren().addAll(scoreBoard, rollDieButton, gameLog);

    centerBox.getChildren().addAll(leftBox, boardContainer);
    borderPane.setCenter(centerBox);

    Scene scene = new Scene(borderPane);
    primaryStage.setScene(scene);
    primaryStage.setTitle("Ladder Game - " + (randomLadders ? "Random Mode" : "Classic Mode"));
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
    container.setStyle(
        "-fx-background-color: " + BOARD_BACKGROUND + ";" +
            "-fx-background-radius: 15;" +
            "-fx-padding: 20;"
    );

    // Add drop shadow effect
    DropShadow dropShadow = new DropShadow();
    dropShadow.setRadius(15.0);
    dropShadow.setOffsetX(5.0);
    dropShadow.setOffsetY(5.0);
    dropShadow.setColor(Color.color(0, 0, 0, 0.3));
    container.setEffect(dropShadow);

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
    grid.setHgap(2);
    grid.setVgap(2);

    boolean leftToRight = true;
    IntStream.rangeClosed(0, 9).forEach(row -> {
      IntStream.rangeClosed(0, 9)
          .map(col -> leftToRight ? col : 9 - col)
          .forEach(col -> {
            int tileNumber = row * 10 + col + 1;
            TextField tile = createTile(tileNumber);
            grid.add(tile, col, 9 - row);
            tileFields.put(tileNumber, tile);
          });
    });

    return grid;
  }

  /**
   * Create a tile for the board with improved styling.
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

    // Base tile style
    String baseStyle = "-fx-font-weight: bold;" +
        "-fx-font-size: 14px;" +
        "-fx-border-radius: 5;" +
        "-fx-background-radius: 5;" +
        "-fx-border-width: 2;";

    // Check if the tile has a ladder and style accordingly
    Tile currentTile = gameController.getTileByIdLinear(tileNumber);
    if (currentTile != null && currentTile.getDestinationTile() != null) {
      int destinationTileId = currentTile.getDestinationTile().getTileId();

      if (destinationTileId > tileNumber) {
        // Positive ladder (going up)
        tile.setStyle(baseStyle +
            "-fx-background-color: " + LADDER_UP_COLOR + ";" +
            "-fx-text-fill: white;" +
            "-fx-border-color: #229954;"
        );
        tile.setText(tileNumber + " ↑ " + destinationTileId);
      } else {
        // Negative ladder (going down - snake)
        tile.setStyle(baseStyle +
            "-fx-background-color: " + LADDER_DOWN_COLOR + ";" +
            "-fx-text-fill: white;" +
            "-fx-border-color: #CB4335;"
        );
        tile.setText(tileNumber + " ↓ " + destinationTileId);
      }
    } else if (currentTile != null && currentTile.getEffect() != null) {
      // Special effect tiles
      String effect = currentTile.getEffect();
      if ("skipTurn".equals(effect)) {
        tile.setStyle(baseStyle +
            "-fx-background-color: #F39C12;" + // Orange
            "-fx-text-fill: white;" +
            "-fx-border-color: #D68910;"
        );
        tile.setText(tileNumber + " ⏸");
      } else if ("backToStart".equals(effect)) {
        tile.setStyle(baseStyle +
            "-fx-background-color: #9B59B6;" + // Purple
            "-fx-text-fill: white;" +
            "-fx-border-color: #7D3C98;"
        );
        tile.setText(tileNumber + " ⟲");
      }
    } else {
      // Regular tile styling
      tile.setStyle(baseStyle +
          "-fx-background-color: " + TILE_COLOR + ";" +
          "-fx-text-fill: " + TEXT_COLOR + ";" +
          "-fx-border-color: #95A5A6;"
      );
    }

    // Add hover effect
    tile.setOnMouseEntered(e -> {
      tile.setScaleX(1.05);
      tile.setScaleY(1.05);
    });
    tile.setOnMouseExited(e -> {
      tile.setScaleX(1.0);
      tile.setScaleY(1.0);
    });

    return tile;
  }

  /**
   * Initialize player circles for all players.
   */
  private void initializePlayerCircles() {
    List<Player> players = gameController.getPlayers();


    players.forEach(player -> {
      Circle playerCircle = createPlayerCircle(player);
      playerCircles.put(player, playerCircle);
      playerOverlay.getChildren().add(playerCircle);

    });
  }

  /**
   * Creates a circle for a player with enhanced visual effects.
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
      circle.setFill(Color.DODGERBLUE);
    }

    circle.setStroke(Color.WHITE);
    circle.setStrokeWidth(2.5);

    // Add drop shadow effect
    DropShadow dropShadow = new DropShadow();
    dropShadow.setRadius(5.0);
    dropShadow.setOffsetX(2.0);
    dropShadow.setOffsetY(2.0);
    dropShadow.setColor(Color.color(0, 0, 0, 0.5));
    circle.setEffect(dropShadow);

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

    gameController.getPlayers().forEach(player -> {
      Circle playerCircle = playerCircles.get(player);

      if (playerCircle != null) {
        int tileId = player.getCurrentTile().getTileId();
        double[] position = calculatePlayerPosition(tileId, gameController.getPlayers().indexOf(player));

        playerCircle.setCenterX(position[0]);
        playerCircle.setCenterY(position[1]);
      }
    });

    // Update scoreboard
    updateScoreBoard(scoreBoard);
  }

  /**
   * Create the scoreboard with improved styling.
   *
   * @return the scoreboard
   */
  private TextArea createScoreBoard() {
    TextArea scoreBoard = new TextArea("Scoreboard:");
    scoreBoard.setPrefWidth(250);
    scoreBoard.setPrefHeight(200);
    scoreBoard.setEditable(false);
    scoreBoard.setStyle(
        "-fx-control-inner-background: " + LOG_BACKGROUND + ";" +
            "-fx-font-family: 'Consolas', monospace;" +
            "-fx-font-size: 14px;" +
            "-fx-text-fill: " + TEXT_COLOR + ";" +
            "-fx-font-weight: bold;" +
            "-fx-border-color: #95A5A6;" +
            "-fx-border-radius: 5;" +
            "-fx-background-radius: 5;"
    );
    return scoreBoard;
  }

  /**
   * Update the scoreBoard with the current player positions.
   *
   * @param scoreBoard takes in the TextArea scoreBoard to update
   */
  public void updateScoreBoard(TextArea scoreBoard) {
    scoreBoard.clear();

    ArrayList<Player> sortedPlayerPositionList = new ArrayList<>(gameController.getPlayers());
    sortedPlayerPositionList.sort((p1, p2) ->
        p2.getCurrentTile().getTileId() - p1.getCurrentTile().getTileId());

    StringBuilder scoreBoardText = new StringBuilder("🏆 SCOREBOARD 🏆\n");
    scoreBoardText.append("─────────────────────\n");

    IntStream.range(0, sortedPlayerPositionList.size()).forEach(i -> {
      Player player = sortedPlayerPositionList.get(i);
      int position = i + 1;
      String medal = position == 1 ? "🥇" : position == 2 ? "🥈" : position == 3 ? "🥉" : "  ";
      scoreBoardText.append(String.format("%s %s: Tile %d\n",
          medal,
          player.getName(),
          player.getCurrentTile().getTileId()));
    });

    scoreBoard.setText(scoreBoardText.toString());
  }

  private void restartGame(Stage primaryStage) {
    start(primaryStage);
  }

  public static void main(String[] args) {
    launch(args);
  }
}