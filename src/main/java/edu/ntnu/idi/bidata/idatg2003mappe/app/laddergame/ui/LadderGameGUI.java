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
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import java.util.stream.IntStream;

/**
 * <p>Enhanced Ladder Game GUI with improved color scheme and design.</p>
 * <p>Features a modern, visually appealing interface with better contrast and readability.</p>
 * <p>This class implements the user interface for the classic ladder game (also known as
 * Snakes and Ladders). The interface includes:</p>
 * <ul>
 *   <li>A game board grid with numbered tiles from 1 to 100</li>
 *   <li>Special visual indicators for ladders (up) and snakes (down)</li>
 *   <li>Player tokens that move across the board</li>
 *   <li>A roll button for dice simulation</li>
 *   <li>A scoreboard to track player positions</li>
 *   <li>A game log to record important events</li>
 * </ul>
 *
 * <p>The GUI supports both classic mode with fixed ladder positions and random mode
 * where ladder positions are generated randomly at the start of the game.</p>
 *
 * @version 0.5
 * @since 20.02.2025
 */
public class LadderGameGUI extends Application {
  // Constants for circle appearance
  private static final double CIRCLE_RADIUS = 15.0;
  private static final double TILE_WIDTH = 80.0;
  private static final double TILE_HEIGHT = 80.0;
  private static final Logger logger = Logger.getLogger(LadderGameGUI.class.getName());
  private final GameSaveLoadHandler gameSaveLoadHandler = new GameSaveLoadHandler();
  private final Map<Player, Circle> playerCircles = new HashMap<>();
  private final Map<Integer, TextField> tileFields = new HashMap<>();
  private TextArea scoreBoard;
  private boolean randomLadders = false;
  private NavBar navBar;
  private LadderGameController gameController;
  private GridPane boardGrid;
  private TextArea gameLog;
  // Player circle management
  private Pane playerOverlay;

  /**
   * <p>The main entry point for the application.</p>
   * <p>This method launches the JavaFX application.</p>
   *
   * @param args Command line arguments (not used)
   */
  public static void main(String[] args) {
    launch(args);
  }

  /**
   * <p>Sets whether random ladders mode is enabled.</p>
   * <p>When enabled, ladder positions are randomized at game start.</p>
   *
   * @param randomLadders true to enable random ladders mode, false for classic mode
   */
  public void setRandomLadders(boolean randomLadders) {
    this.randomLadders = randomLadders;
  }

  /**
   * <p>Returns whether random ladders mode is enabled.</p>
   *
   * @return true if random ladders mode is enabled, false otherwise
   */
  public boolean isRandomLadders() {
    return randomLadders;
  }

  /**
   * <p>Initializes and starts the Ladder Game GUI.</p>
   * <p>This method sets up the entire game interface including:</p>
   * <ul>
   *   <li>Creating the game controller</li>
   *   <li>Setting up the board layout</li>
   *   <li>Initializing the player tokens</li>
   *   <li>Creating the control buttons and score display</li>
   * </ul>
   * <p>The method applies consistent styling to all UI elements according to
   * the game's color scheme.</p>
   *
   * @param primaryStage the primary stage for this application
   */
  @Override
  public void start(Stage primaryStage) {
    gameController = new LadderGameController(randomLadders);

    BorderPane borderPane = new BorderPane();
    borderPane.setPrefSize(1440, 840);
    borderPane.getStyleClass().add("md-game-background");

    navBar = new NavBar();
    navBar.setStage(primaryStage);
    navBar.setGameController(gameController);

    borderPane.setTop(navBar.createMenuBar());

    HBox centerBox = new HBox(24);
    centerBox.setAlignment(Pos.CENTER);
    centerBox.setPadding(new Insets(24));

    // Create the board with overlay for player circles
    StackPane boardContainer = createBoardWithOverlay();

    // Create Material Design sidebar
    VBox leftBox = createSidebar();

    centerBox.getChildren().addAll(leftBox, boardContainer);
    borderPane.setCenter(centerBox);

    Scene scene = new Scene(borderPane);
    loadCSS(scene);
    primaryStage.setScene(scene);
    primaryStage.setTitle("Ladder Game - " + (randomLadders ? "Random Mode" : "Classic Mode"));
    primaryStage.show();

    // Initialize player circles and update the board
    initializePlayerCircles();
    updateBoardUI();
  }

  /**
   * <p>Creates the Material Design sidebar with controls and scoreboard.</p>
   *
   * @return VBox containing sidebar elements
   */
  private VBox createSidebar() {
    VBox sidebar = new VBox(20);
    sidebar.setAlignment(Pos.TOP_CENTER);
    sidebar.setPadding(new Insets(20));
    sidebar.setPrefWidth(280);
    sidebar.getStyleClass().add("md-sidebar");

    // Scoreboard section
    scoreBoard = createScoreBoard();

    // Roll Die button as Material FAB extended
    Button rollDieButton = new Button("🎲  Roll Die");
    rollDieButton.setPrefSize(220, 56);
    rollDieButton.getStyleClass().add("md-fab-extended");

    rollDieButton.setOnAction(e -> {
      String message = gameController.playTurn();
      gameLog.appendText(message + "\n");
      updateBoardUI();

      if (message.contains("won")) {
        rollDieButton.setDisable(true);
      }
    });

    // Game log section
    VBox logSection = new VBox(8);
    logSection.getStyleClass().add("md-card-filled");
    logSection.setPadding(new Insets(12));

    javafx.scene.control.Label logLabel = new javafx.scene.control.Label("Game Log");
    logLabel.getStyleClass().add("md-title-small");

    gameLog = new TextArea();
    gameLog.setEditable(false);
    gameLog.setPrefHeight(180);
    gameLog.getStyleClass().add("md-game-log");

    logSection.getChildren().addAll(logLabel, gameLog);

    sidebar.getChildren().addAll(scoreBoard, rollDieButton, logSection);
    return sidebar;
  }

  /**
   * <p>Loads the CSS stylesheet for the application.</p>
   *
   * @param scene The scene to apply CSS styling to
   */
  private void loadCSS(Scene scene) {
    try {
      String cssFile = getClass().getResource("/game-style/game-styles.css").toExternalForm();
      scene.getStylesheets().add(cssFile);
    } catch (Exception e) {
      logger.warning("Could not load CSS file. Using default styling.");
    }
  }

  /**
   * <p>Creates a Material Design container for the game board with player overlay.</p>
   * <p>The container uses elevated card styling with proper shadows.</p>
   *
   * @return StackPane containing the board grid and player overlay
   */
  private StackPane createBoardWithOverlay() {
    StackPane container = new StackPane();
    container.getStyleClass().add("md-board-container");
    container.setPadding(new Insets(16));

    // Material Design elevation shadow
    DropShadow dropShadow = new DropShadow();
    dropShadow.setRadius(20.0);
    dropShadow.setOffsetX(0);
    dropShadow.setOffsetY(8.0);
    dropShadow.setColor(Color.color(0, 0, 0, 0.25));
    container.setEffect(dropShadow);

    // Create the game board
    boardGrid = createBoardGrid();

    // Create overlay for player circles
    playerOverlay = new Pane();
    playerOverlay.setPickOnBounds(false);

    // Add both to container
    container.getChildren().addAll(boardGrid, playerOverlay);

    return container;
  }

  /**
   * <p>Creates the game board grid with correctly numbered tiles.</p>
   * <p>The board follows these rules:</p>
   * <ul>
   *   <li>Tile 1 is positioned at the bottom left</li>
   *   <li>Tiles are arranged in a zig-zag pattern (left-to-right, then right-to-left)</li>
   *   <li>The highest tile (100) is at the top left</li>
   * </ul>
   * <p>Each tile is represented as a TextField styled according to its function
   * (regular, ladder up, ladder down, or special effect).</p>
   *
   * @return GridPane containing all the game tiles
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
   * <p>Creates a Material Design styled tile for the game board.</p>
   * <p>Tiles are visually distinguished by their function using Material colors:</p>
   * <ul>
   *   <li>Regular tiles - Light surface with subtle shadow</li>
   *   <li>Ladder up tiles - Material green with up arrow</li>
   *   <li>Ladder down tiles - Material red with down arrow</li>
   *   <li>Special effect tiles - Material accent colors</li>
   * </ul>
   *
   * @param tileNumber the number to display on the tile
   * @return a styled TextField representing the tile
   */
  private TextField createTile(int tileNumber) {
    TextField tile = new TextField("" + tileNumber);
    tile.setPrefWidth(TILE_WIDTH);
    tile.setPrefHeight(TILE_HEIGHT);
    tile.setEditable(false);
    tile.setAlignment(Pos.CENTER);

    // Add Material Design base tile style
    tile.getStyleClass().add("md-tile");

    // Check if the tile has a ladder and style accordingly
    Tile currentTile = gameController.getTileByIdLinear(tileNumber);
    if (currentTile != null && currentTile.getDestinationTile() != null) {
      int destinationTileId = currentTile.getDestinationTile().getTileId();

      if (destinationTileId > tileNumber) {
        // Positive ladder (going up) - Material green
        tile.getStyleClass().add("md-tile-ladder-up");
        tile.setText(tileNumber + " ↑ " + destinationTileId);
      } else {
        // Negative ladder (going down - snake) - Material red
        tile.getStyleClass().add("md-tile-ladder-down");
        tile.setText(tileNumber + " ↓ " + destinationTileId);
      }
    } else if (currentTile != null && currentTile.getEffect() != null) {
      // Special effect tiles with Material accent colors
      String effect = currentTile.getEffect();
      if ("skipTurn".equals(effect)) {
        tile.getStyleClass().add("md-tile-skip-turn");
        tile.setText(tileNumber + " ⏸");
      } else if ("backToStart".equals(effect)) {
        tile.getStyleClass().add("md-tile-back-to-start");
        tile.setText(tileNumber + " ⟲");
      }
    } else if (tileNumber == 1) {
      // Start tile - Material primary blue
      tile.getStyleClass().add("md-tile-start");
      tile.setText("START");
    } else if (tileNumber == 100) {
      // End tile - Gold/success
      tile.getStyleClass().add("md-tile-end");
      tile.setText("FINISH");
    } else {
      // Regular tile styling
      tile.getStyleClass().add("md-tile-regular");
    }

    // Add subtle hover effect
    tile.setOnMouseEntered(e -> {
      tile.setScaleX(1.03);
      tile.setScaleY(1.03);
    });
    tile.setOnMouseExited(e -> {
      tile.setScaleX(1.0);
      tile.setScaleY(1.0);
    });

    return tile;
  }

  /**
   * <p>Initializes circular tokens for all players on the board.</p>
   * <p>This method creates a visual circle representation for each player and adds
   * it to the player overlay pane. Each circle is positioned according to the player's
   * current tile position.</p>
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
   * <p>Creates a styled circle token to represent a player on the board.</p>
   * <p>The circle has these visual characteristics:</p>
   * <ul>
   *   <li>Color matching the player's assigned color</li>
   *   <li>White border stroke for definition</li>
   *   <li>Drop shadow effect for visual depth</li>
   *   <li>The player's name stored as user data for identification</li>
   * </ul>
   *
   * @param player The player to create a circle token for
   * @return Circle object representing the player
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
   * <p>Calculates the screen position for placing a player token.</p>
   * <p>This method determines the x,y coordinates for a player circle based on:</p>
   * <ul>
   *   <li>The position of the tile in the grid</li>
   *   <li>The player's index (for offset when multiple players occupy the same tile)</li>
   * </ul>
   * <p>Players on the same tile are arranged in a small grid pattern around
   * the center of the tile.</p>
   *
   * @param tileId      The ID of the tile where the player is located
   * @param playerIndex The index of the player (for calculating offset)
   * @return double array containing [x, y] coordinates
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
   * <p>Updates the visual representation of all player positions on the board.</p>
   * <p>This method:</p>
   * <ul>
   *   <li>Repositions all player circles according to their current tile positions</li>
   *   <li>Updates the scoreboard to reflect current player rankings</li>
   * </ul>
   * <p>This method should be called after any change to player positions,
   * such as after a die roll or special tile effect.</p>
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
   * <p>Creates a Material Design styled scoreboard to display player rankings.</p>
   * <p>The scoreboard shows players in order of their progress on the board
   * with card-based styling for visual consistency.</p>
   *
   * @return TextArea containing the formatted scoreboard
   */
  private TextArea createScoreBoard() {
    TextArea scoreBoardArea = new TextArea("Scoreboard:");
    scoreBoardArea.setPrefWidth(240);
    scoreBoardArea.setPrefHeight(200);
    scoreBoardArea.setEditable(false);
    scoreBoardArea.getStyleClass().add("md-scoreboard");
    return scoreBoardArea;
  }

  /**
   * <p>Updates the scoreboard with current player positions.</p>
   * <p>This method:</p>
   * <ul>
   *   <li>Sorts players by their position on the board (highest tile first)</li>
   *   <li>Adds medal emojis for the top three players</li>
   *   <li>Displays each player's name and current tile position</li>
   * </ul>
   *
   * @param scoreBoard The TextArea component to update with current rankings
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

  /**
   * <p>Restarts the game with a fresh board and player positions.</p>
   * <p>This method resets the entire game state and recreates the UI.</p>
   *
   * @param primaryStage The primary stage to restart the game in
   */
  private void restartGame(Stage primaryStage) {
    start(primaryStage);
  }
}
