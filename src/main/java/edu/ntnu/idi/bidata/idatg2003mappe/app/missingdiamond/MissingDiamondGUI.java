package edu.ntnu.idi.bidata.idatg2003mappe.app.missingdiamond;

import edu.ntnu.idi.bidata.idatg2003mappe.app.NavBar;
import edu.ntnu.idi.bidata.idatg2003mappe.entity.Player;
import edu.ntnu.idi.bidata.idatg2003mappe.map.Tile;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
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
 * Simplified GUI class for the Missing Diamond game (MVP version).
 *
 * @author Simen Gudbrandsen and Frikk Breadsroed
 * @version 0.0.1
 * @since 23.04.2025
 */
public class MissingDiamondGUI extends Application {

  // Game controller
  private MissingDiamondController gameController;

  // UI components
  private BorderPane mainLayout;
  private Pane boardPane;
  private TextArea scoreBoard;
  private TextArea gameLog;
  private Button rollDieButton;

  // Board data
  private Map<Integer, Circle> tileCircles = new HashMap<>();
  private Map<Player, Circle> playerMarkers = new HashMap<>();

  // Player colors
  private final Color[] playerColors = {
      Color.ORANGE, Color.INDIGO, Color.GREEN, Color.YELLOW, Color.BROWN, Color.PURPLE
  };

  private int numberOfPlayers = 2;

  @Override
  public void start(Stage primaryStage) {
    // Initialize game controller
    gameController = new MissingDiamondController(numberOfPlayers);

    // Create main layout
    mainLayout = new BorderPane();
    mainLayout.setPrefSize(1440, 840);

    // Add menu bar
    NavBar navBar = new NavBar();
    mainLayout.setTop(navBar.createMenuBar());

    // Create left panel (scoreboard, roll button, game log)
    VBox leftPanel = createLeftPanel();

    // Create board
    boardPane = createBoardPane();

    // Set up split layout
    SplitPane splitPane = new SplitPane();
    splitPane.getItems().addAll(leftPanel, boardPane);
    splitPane.setDividerPositions(0.25);
    mainLayout.setCenter(splitPane);

    // Create scene and show
    Scene scene = new Scene(mainLayout);
    primaryStage.setScene(scene);
    primaryStage.setTitle("Missing Diamond");
    primaryStage.show();

    // Initialize UI state
    updateBoardUI();
  }

  //Creates the left panel containing the scoreboard, roll button, and game log.
  private VBox createLeftPanel() {
    VBox panel = new VBox(10);
    panel.setPadding(new Insets(10));

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
    Pane pane = new Pane();
    pane.setStyle("-fx-background-color: lightblue;");

    // Create simple circular board with cross paths
    createSimpleBoard(pane);

    return pane;
  }

  //Creates a simple board with a circular path and a few cross paths.
  private void createSimpleBoard(Pane pane) {
    // Center of the board
    double centerX = 500;
    double centerY = 400;
    double radius = 300;

    // Create main circle of 20 tiles
    List<Circle> outerTiles = new ArrayList<>();
    for (int i = 0; i < 20; i++) {
      int tileId = i + 1;
      double angle = Math.toRadians(i * (360.0 / 20));
      double x = centerX + radius * Math.cos(angle);
      double y = centerY + radius * Math.sin(angle);

      Circle tile = createTileCircle(x, y, tileId);
      pane.getChildren().add(tile);
      outerTiles.add(tile);
      tileCircles.put(tileId, tile);

      // Add tile label
      Label label = new Label(String.valueOf(tileId));
      label.setLayoutX(x - 5);
      label.setLayoutY(y - 5);
      pane.getChildren().add(label);
    }

    // Connect tiles in circle
    for (int i = 0; i < outerTiles.size(); i++) {
      Circle current = outerTiles.get(i);
      Circle next = outerTiles.get((i + 1) % outerTiles.size());

      Line line = new Line(
          current.getCenterX(), current.getCenterY(),
          next.getCenterX(), next.getCenterY()
      );
      line.setStroke(Color.BLACK);
      line.setStrokeWidth(2);
      pane.getChildren().add(line);
    }

    // Create horizontal cross path
    List<Circle> horizontalPath = new ArrayList<>();
    horizontalPath.add(outerTiles.get(5)); // West side

    // Add center tile
    Circle centerTile = createTileCircle(centerX, centerY, 21);
    pane.getChildren().add(centerTile);
    tileCircles.put(21, centerTile);
    Label centerLabel = new Label("21");
    centerLabel.setLayoutX(centerX - 5);
    centerLabel.setLayoutY(centerY - 5);
    pane.getChildren().add(centerLabel);

    horizontalPath.add(centerTile);
    horizontalPath.add(outerTiles.get(15)); // East side

    // Connect horizontal path
    for (int i = 0; i < horizontalPath.size() - 1; i++) {
      Circle current = horizontalPath.get(i);
      Circle next = horizontalPath.get(i + 1);

      Line line = new Line(
          current.getCenterX(), current.getCenterY(),
          next.getCenterX(), next.getCenterY()
      );
      line.setStroke(Color.BLACK);
      line.setStrokeWidth(2);
      pane.getChildren().add(line);
    }

    // Create vertical cross path
    Line verticalLine1 = new Line(
        outerTiles.get(0).getCenterX(), outerTiles.get(0).getCenterY(),
        centerTile.getCenterX(), centerTile.getCenterY()
    );
    verticalLine1.setStroke(Color.BLACK);
    verticalLine1.setStrokeWidth(2);
    pane.getChildren().add(verticalLine1);

    Line verticalLine2 = new Line(
        centerTile.getCenterX(), centerTile.getCenterY(),
        outerTiles.get(10).getCenterX(), outerTiles.get(10).getCenterY()
    );
    verticalLine2.setStroke(Color.BLACK);
    verticalLine2.setStrokeWidth(2);
    pane.getChildren().add(verticalLine2);
  }

  //Creates a tile circle at the specified position.
  private Circle createTileCircle(double x, double y, int tileId) {
    Circle tile = new Circle(x, y, 20, Color.RED);
    tile.setStroke(Color.BLACK);
    tile.setStrokeWidth(2);
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

    // Highlight possible moves in yellow
    List<Tile> possibleMoves = gameController.getPossibleMoves();
    for (Tile tile : possibleMoves) {
      Circle tileCircle = tileCircles.get(tile.getTileId());
      if (tileCircle != null) {
        tileCircle.setFill(Color.YELLOW);
      }
    }
  }

  //Updates the board UI with current game state.
  private void updateBoardUI() {
    // Clear existing player markers
    for (Circle marker : playerMarkers.values()) {
      boardPane.getChildren().remove(marker);
    }
    playerMarkers.clear();

    // Add player markers at their current positions
    List<Player> players = gameController.getPlayers();
    for (int i = 0; i < players.size(); i++) {
      Player player = players.get(i);
      int tileId = player.getCurrentTile().getTileId();
      Circle tileCircle = tileCircles.get(tileId);

      if (tileCircle != null) {
        // Create offset for player marker
        double offsetX = (i == 0) ? -10 : 10;
        double offsetY = (i == 0) ? -10 : 10;

        // Create player marker
        Circle playerMarker = new Circle(
            tileCircle.getCenterX() + offsetX,
            tileCircle.getCenterY() + offsetY,
            10,
            playerColors[i]
        );
        playerMarker.setStroke(Color.BLACK);
        playerMarker.setStrokeWidth(1.5);

        // Add to board
        boardPane.getChildren().add(playerMarker);
        playerMarkers.put(player, playerMarker);
      }
    }

    // Update scoreboard
    updateScoreBoard();
  }

  //Handles a click on a tile.
  private void handleTileClick(int tileId) {
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