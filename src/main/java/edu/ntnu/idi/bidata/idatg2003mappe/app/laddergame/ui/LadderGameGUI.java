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
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>Enhanced Ladder Game GUI with improved color scheme and design.</p>
 * <p>This class provides a modern, visually appealing interface for the Ladder Game
 * with better contrast and readability. It handles rendering the game board,
 * player tokens, and game controls.</p>
 * <p>The interface features:</p>
 * <ul>
 *   <li>A 10x10 grid board with distinctive styling for different tile types</li>
 *   <li>Animated player tokens that move between tiles</li>
 *   <li>A game log that displays turn results</li>
 *   <li>A dynamic scoreboard showing player rankings</li>
 *   <li>Styled buttons with hover effects</li>
 * </ul>
 *
 * <p>Enhanced Ladder Game GUI with improved color scheme and design.
 * Features a modern, visually appealing interface with better contrast and readability.</p>
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
   * <p>Starts the game interface.</p>
   * <p>This method initializes the game controller, creates the user interface,
   * and sets up event handlers for user interactions.</p>
   *
   * @param primaryStage The primary stage for this JavaFX application
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
   * <p>Creates the board with an overlay for player circles.</p>
   * <p>Uses a StackPane to layer the player tokens on top of the game board,
   * allowing independent movement of tokens without affecting the board.</p>
   *
   * @return A {@link StackPane} containing the board grid and player overlay
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
   * <p>Creates the board grid with correct tile numbering (1 at bottom left).</p>
   * <p>Configures the grid to follow the classic snakes and ladders pattern where
   * numbers snake back and forth across rows.</p>
   *
   * @return A configured {@link GridPane} representing the game board
   */
  private GridPane createBoardGrid() {
    GridPane grid = new GridPane();
    grid.setAlignment(Pos.CENTER);
    grid.setHgap(2);
    grid.setVgap(2);

    boolean leftToRight = true;
    for (int row = 0; row <= 9; row++) {
      if (leftToRight) {
        for (int col = 0; col < 10; col++) {
          int tileNumber = row * 10 + col + 1;
          TextField tile = createTile(tileNumber);
          grid.add(tile, col, 9 - row);
          tileFields.put(tileNumber, tile);
        }
      } else {
        for (int col = 9; col >= 0; col--) {
          int tileNumber = row * 10 + (9 - col) + 1;
          TextField tile = createTile(tileNumber);
          grid.add(tile, col, 9 - row);
          tileFields.put(tileNumber, tile);
        }
      }
      leftToRight = !leftToRight;
    }
    return grid;
  }

  /**
   * <p>Creates a tile for the board with improved styling.</p>
   * <p>Styles the tile according to its type:</p>
   * <ul>
   *   <li>Regular tiles - Light gray</li>
   *   <li>Ladder up tiles - Green with up arrow</li>
   *   <li>Ladder down tiles - Red with down arrow</li>
   *   <li>Skip turn tiles - Orange with pause symbol</li>
   *   <li>Back to start tiles - Purple with return symbol</li>
   * </ul>
   *
   * @param tileNumber The number of the tile to create
   * @return A styled {@link TextField} representing the tile
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
   * <p>Initializes player circles for all players.</p>
   * <p>Creates a visual token for each player and adds it to the player overlay.</p>
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
   * <p>Creates a circle for a player with enhanced visual effects.</p>
   * <p>Each player's circle is styled with their chosen color and includes
   * a white border and drop shadow for visual emphasis.</p>
   *
   * @param player The {@link Player} to create a circle for
   * @return A styled {@link Circle} representing the player
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
   * <p>Calculates the screen position for a player circle based on tile position.</p>
   * <p>Determines where on the screen a player token should be placed to align with
   * their current board position. Applies offsets when multiple players occupy the same tile.</p>
   *
   * @param tileId The ID of the tile the player is on
   * @param playerIndex The index of the player (used for offset calculation)
   * @return An array containing [x, y] coordinates
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
   * <p>Updates the board UI with current player positions using circles.</p>
   * <p>Repositions all player tokens to match their current positions on the board,
   * and refreshes the scoreboard to show updated rankings.</p>
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
   * <p>Creates the scoreboard with improved styling.</p>
   * <p>The scoreboard displays the relative positions of all players on the board,
   * sorted by tile number in descending order.</p>
   *
   * @return A styled {@link TextArea} showing the current player rankings
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
   * <p>Updates the scoreBoard with the current player positions.</p>
   * <p>Sorts players by position and formats the scoreboard with medal emoji for the top three players.</p>
   *
   * @param scoreBoard The {@link TextArea} scoreboard to update
   */
  public void updateScoreBoard(TextArea scoreBoard) {
    scoreBoard.clear();

    ArrayList<Player> sortedPlayerPositionList = new ArrayList<>(gameController.getPlayers());
    sortedPlayerPositionList.sort((p1, p2) ->
        p2.getCurrentTile().getTileId() - p1.getCurrentTile().getTileId());

    StringBuilder scoreBoardText = new StringBuilder("🏆 SCOREBOARD 🏆\n");
    scoreBoardText.append("─────────────────────\n");

    int position = 1;
    for (Player player : sortedPlayerPositionList) {
      String medal = position == 1 ? "🥇" : position == 2 ? "🥈" : position == 3 ? "🥉" : "  ";
      scoreBoardText.append(String.format("%s %s: Tile %d\n",
          medal,
          player.getName(),
          player.getCurrentTile().getTileId()));
      position++;
    }

    scoreBoard.setText(scoreBoardText.toString());
  }

  /**
   * <p>Restarts the game with a fresh board and player positions.</p>
   *
   * @param primaryStage The primary stage for the application
   */
  private void restartGame(Stage primaryStage) {
    start(primaryStage);
  }

  /**
   * <p>Main entry point for the application when run standalone.</p>
   *
   * @param args Command line arguments
   */
  public static void main(String[] args) {
    launch(args);
  }
}
