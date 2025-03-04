package edu.ntnu.idi.bidata.idatg2003mappe.app.laddergame;

import edu.ntnu.idi.bidata.idatg2003mappe.entity.Player;
import edu.ntnu.idi.bidata.idatg2003mappe.map.Tile;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;

/**
 * Class for the LadderGameGUI.
 * This class presents the game in a graphical user interface.
 * Also handles game logic.
 *
 * @author Simen Gudbrandsen and Frikk Breadsroed
 * @version 0.3
 * @since 20.02.2025
 */
public class LadderGameGUI extends Application {
  private LadderGameController gameController;
  private GridPane boardGrid;
  private TextArea gameLog;
  private TextArea scoreBoard; // Declare scoreBoard as a class-level variable
  private final String[] playerColor = {"orange", "indigo", "green", "yellow", "brown", "purple"};
  private boolean randomLadders = false;

  /**
   * Start the game.
   * @param primaryStage the primary stage
   */

  @Override
  public void start(Stage primaryStage){
    gameController = new LadderGameController(6, randomLadders);

    BorderPane borderPane = new BorderPane();
    borderPane.setPrefSize(1440, 840); // cubed window
    borderPane.setTop(createMenuBar(primaryStage));

    HBox centerBox = new HBox(10);
    centerBox.setAlignment(Pos.CENTER);
    boardGrid = createBoardGrid();
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

    scoreBoard = createScoreBoard(); // Initialize scoreBoard

    VBox leftBox = new VBox(10);
    leftBox.setAlignment(Pos.CENTER_LEFT);
    leftBox.getChildren().addAll(scoreBoard, rollDieButton, gameLog);

    centerBox.getChildren().addAll(leftBox, boardGrid);
    borderPane.setCenter(centerBox);

    Scene scene = new Scene(borderPane);
    primaryStage.setScene(scene);
    primaryStage.setTitle("Ladder game");
    primaryStage.show();

    updateBoardUI();
  }

  /**
   * Create the board grid.
   * @return the board grid
   */

  private GridPane createBoardGrid() {
    GridPane grid = new GridPane();
    grid.setAlignment(Pos.CENTER);

    boolean leftToRight = true;
    for (int row = 0; row <= 9; row++) {
      if (leftToRight) {
        for (int col = 0; col < 10; col++) {
          int tileNumber = (9 - row) * 10 + col + 1;
          TextField tile = createTile(tileNumber);
          grid.add(tile, col, 9 - row);
        }
      } else {
        for (int col = 9; col >= 0; col--) {
          int tileNumber = (9 - row) * 10 + (9 - col) + 1;
          TextField tile = createTile(tileNumber);
          grid.add(tile, col, 9 - row);
        }
      }
      leftToRight = !leftToRight;
    }
    return grid;
  }

  /**
   * Create a tile for the board.
   * @param tileNumber the number of the tile
   * @return the tile
   */

  private TextField createTile(int tileNumber) {
    TextField tile = new TextField("" + tileNumber);
    tile.setPrefWidth(80);
    tile.setPrefHeight(80);
    tile.setEditable(false);
    tile.setAlignment(Pos.CENTER);

    // Check if the tile has a ladder
    Tile currentTile = gameController.getTileByIdLinear(tileNumber);
    if (currentTile != null && currentTile.getDestinationTile() != null) {
      int destinationTileId = currentTile.getDestinationTile().getTileId();

      //Positive ladder
      if (destinationTileId > tileNumber) {
        tile.setStyle("-fx-background-color: blue; -fx-font-weight: bold;");
      } else {
        //Negative ladder
        tile.setStyle("-fx-background-color: red; -fx-font-weight: bold;");
      }

      tile.setText(tileNumber + " → " + destinationTileId);
    }
    return tile;
  }

  /**
   * Create the menu bar.
   * @param primaryStage the primary stage
   * @return the menu bar
   */

  private MenuBar createMenuBar(Stage primaryStage) {
    MenuBar menuBar = new MenuBar();

    // File Menu
    Menu fileMenu = new Menu("File");
    MenuItem openMenuItem = new MenuItem("Open");
    MenuItem saveMenuItem = new MenuItem("Save");
    MenuItem closeMenuItem = new MenuItem("Close");
    fileMenu.getItems().addAll(openMenuItem, saveMenuItem, new SeparatorMenuItem(), closeMenuItem);

    // Settings Menu
    Menu settingsMenu = new Menu("Settings");
    MenuItem toggleModeItem = new MenuItem("Toggle Classic/Random Mode");
    MenuItem restartGameItem = new MenuItem("Restart Game");
    toggleModeItem.setOnAction(e -> toggleGameMode(primaryStage));
    restartGameItem.setOnAction(e -> restartGame(primaryStage));
    settingsMenu.getItems().addAll(toggleModeItem, restartGameItem);

    // Add both menus to the menu bar
    menuBar.getMenus().addAll(fileMenu, settingsMenu);
    return menuBar;
  }

  /**
   * Toggles the game mode between classic and random ladders.
   */

  private void toggleGameMode(Stage primaryStage) {
    randomLadders = !randomLadders; // Toggle mode

    Alert alert = new Alert(Alert.AlertType.INFORMATION);
    alert.setTitle("Game Mode Changed");
    alert.setHeaderText("Ladder Mode Updated");
    alert.setContentText(randomLadders ? "Switched to Randomized Ladders" : "Switched to Classic Mode");
    alert.showAndWait();

    restartGame(primaryStage);
  }

  /**
   * Create the scoreboard.
   * @return the scoreboard
   */
  private TextArea createScoreBoard() {
    TextArea scoreBoard = new TextArea();

    String s = "Scoreboard:";

    return new TextArea(s);
  }

  /**
   * Update the scoreBoard with the current player positions.
   * ranks player base on position in sortedPlayerPositionList and displays this in TextArea scoreBoard.
   * @param scoreBoard takes in the TextArea scoreBoard to update
   */
  private void updateScoreBoard(TextArea scoreBoard) {
    scoreBoard.clear(); // Clear the scoreBoard

    scoreBoard.setPrefWidth(80);
    scoreBoard.setPrefHeight(130); //TODO - Make dynamic basied on number of players
    scoreBoard.setEditable(false);
    //scoreBoard.setAlignment(Pos.CENTER_LEFT);

    ArrayList<Player> sortedPlayerPositionList = new ArrayList<>(gameController.getPlayers()); // adds all players to the list
    sortedPlayerPositionList.sort((p1, p2) -> p2.getCurrentTile().getTileId() - p1.getCurrentTile().getTileId()); // sorts the list based on the players current tile


    for (Player player : sortedPlayerPositionList) {
      scoreBoard.appendText(player.getName() + ": " + player.getCurrentTile().getTileId() + "\n");
    }
    String s = "Scoreboard:"+ "\n" + scoreBoard.getText();
    scoreBoard.setText(s); // Update the scoreBoard
  }

  /**
   * Update the board UI with the current player positions, changes color of tiles and calls to update the scoreboard.
   */
  private void updateBoardUI() {
    for (int row = 0; row < 10; row++) {
      for (int col = 0; col < 10; col++) {
        int tileNumber = row * 10 + col + 1;
        TextField tile = (TextField) boardGrid.getChildren().get(row * 10 + col);
        tile.setText("" + tileNumber);
        tile.setStyle("-fx-background-color: white; -fx-background-insets: 0, 1 ;");

        // Keep ladder indicators
        Tile currentTile = gameController.getTileByIdLinear(tileNumber);
        if (currentTile != null && currentTile.getDestinationTile() != null) {
          int destinationTileId = currentTile.getDestinationTile().getTileId();

          //Positive ladder
          if (destinationTileId > tileNumber) {
            tile.setStyle("-fx-background-color: blue; -fx-font-weight: bold; -fx-text-fill: white;");
          } else {
            //Negative ladder
            tile.setStyle("-fx-background-color: red; -fx-font-weight: bold; -fx-text-fill: white;");
          }

          tile.setText(tileNumber + " → " + destinationTileId);
        }
      }
    }

    List<Player> players = gameController.getPlayers();
    for (int i = 0; i < players.size(); i++) {
      Player player = players.get(i);

      String playerColor = this.playerColor[i];
      int tileId = player.getCurrentTile().getTileId();
      int index = tileId - 1;
      int row = index / 10;
      int col = index % 10;
      TextField tileField = (TextField) boardGrid.getChildren().get(row * 10 + col);
      if (tileField.getText().contains("Player")) {
        tileField.setText(tileField.getText() + ", " + player.getID());
      } else {
        tileField.setText(player.getName());
        tileField.setStyle("-fx-background-color: " + playerColor + ";");
      }
    }

    updateScoreBoard(scoreBoard); // Use the class-level scoreBoard
  }

  private void restartGame(Stage primaryStage) {
    start(primaryStage); // Restart the game with new mode
  }

  public static void main(String[] args) {
    launch(args);
  }
}