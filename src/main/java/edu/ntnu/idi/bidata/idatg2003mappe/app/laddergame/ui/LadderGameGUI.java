package edu.ntnu.idi.bidata.idatg2003mappe.app.laddergame.ui;

import edu.ntnu.idi.bidata.idatg2003mappe.app.common.ui.NavBar;
import edu.ntnu.idi.bidata.idatg2003mappe.app.laddergame.controller.LadderGameController;
import edu.ntnu.idi.bidata.idatg2003mappe.entity.player.Player;
import edu.ntnu.idi.bidata.idatg2003mappe.filehandling.exceptionhandling.FileHandlingException;
import edu.ntnu.idi.bidata.idatg2003mappe.filehandling.game.BoardFileHandler;
import edu.ntnu.idi.bidata.idatg2003mappe.filehandling.game.GameSaveLoadHandler;
import edu.ntnu.idi.bidata.idatg2003mappe.filehandling.game.GameState;
import edu.ntnu.idi.bidata.idatg2003mappe.map.Tile;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
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
  public boolean randomLadders = false;
  public NavBar navBar;
  private final GameSaveLoadHandler gameSaveLoadHandler = new GameSaveLoadHandler();

  /**
   * Start the game.
   *
   * @param primaryStage the primary stage
   */

  @Override
  public void start(Stage primaryStage) {
    gameController = new LadderGameController(randomLadders);

    BorderPane borderPane = new BorderPane();
    borderPane.setPrefSize(1440, 840); // cubed window

    navBar = new NavBar();
    navBar.setStage(primaryStage);
    navBar.setGameController(gameController);

    borderPane.setTop(navBar.createMenuBar());
    borderPane.setStyle("-fx-background-color: lightblue;");

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
   *
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

      //Positive ladder //TODO: This part is redundant. Can be removed after testing.
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
   * Quick save the game to the default location.
   */
  private void quickSaveGame() {
    try {
      BoardFileHandler fileHandler = new BoardFileHandler();
      GameState gameState = gameController.createGameState();
      fileHandler.saveToDefaultLocation(gameState);

      Alert alert = new Alert(Alert.AlertType.INFORMATION);
      alert.setTitle("Game Saved");
      alert.setHeaderText("Game Saved Successfully");
      alert.setContentText("Your game has been saved to the default location.");
      alert.showAndWait();
    } catch (FileHandlingException ex) {
      Alert alert = new Alert(Alert.AlertType.ERROR);
      alert.setTitle("Error");
      alert.setHeaderText("Save Error");
      alert.setContentText("Could not save the game: " + ex.getMessage());
      alert.showAndWait();
    }
  }

  /**
   * Load the last saved game from the default location.
   */
  private void loadLastSave() {
    BoardFileHandler fileHandler = new BoardFileHandler();

    if (!fileHandler.defaultSaveExists()) {
      Alert alert = new Alert(Alert.AlertType.INFORMATION);
      alert.setTitle("No Save Found");
      alert.setHeaderText("No Save File Found");
      alert.setContentText("There is no saved game to load.");
      alert.showAndWait();
      return;
    }

    try {
      GameState gameState = fileHandler.loadFromDefaultLocation();

      // Create a new game with the loaded state
      this.randomLadders = gameState.isRandomLadders();
      gameController = new LadderGameController(randomLadders);
      gameController.applyGameState(gameState);

      Alert alert = new Alert(Alert.AlertType.INFORMATION);
      alert.setTitle("Game Loaded");
      alert.setHeaderText("Game Loaded Successfully");
      alert.setContentText("Your last saved game has been loaded.");
      alert.showAndWait();

      updateBoardUI();
    } catch (FileHandlingException ex) {
      Alert alert = new Alert(Alert.AlertType.ERROR);
      alert.setTitle("Error");
      alert.setHeaderText("Load Error");
      alert.setContentText("Could not load the game: " + ex.getMessage());
      alert.showAndWait();
    }
  }

  /**
   * Save the current game state to a file.
   *
   * @param primaryStage the primary stage
   */
  private void saveGame(Stage primaryStage) {
    FileChooser fileChooser = new FileChooser();
    fileChooser.setTitle("Save Game");
    fileChooser.getExtensionFilters().add(
        new FileChooser.ExtensionFilter("JSON Files", "*.json"));

    // Set initial directory to the saves folder
    File saveDir = new File("src/main/resources/saves");
    if (saveDir.exists() && saveDir.isDirectory()) {
      fileChooser.setInitialDirectory(saveDir);
    }

    // Set default filename
    fileChooser.setInitialFileName("game_save.json");

    File file = fileChooser.showSaveDialog(primaryStage);

    if (file != null) {
      try {
        BoardFileHandler fileHandler = new BoardFileHandler();
        GameState gameState = gameController.createGameState();
        fileHandler.write(gameState, file.getAbsolutePath());

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Game Saved");
        alert.setHeaderText("Game Saved Successfully");
        alert.setContentText("Your game has been saved to " + file.getName());
        alert.showAndWait();
      } catch (FileHandlingException ex) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText("Save Error");
        alert.setContentText("Could not save the game: " + ex.getMessage());
        alert.showAndWait();
      }
    }
  }

  /**
   * Load a game state from a file.
   *
   * @param primaryStage the primary stage
   */
  private void loadGame(Stage primaryStage) {
    FileChooser fileChooser = new FileChooser();
    fileChooser.setTitle("Load Game");
    fileChooser.getExtensionFilters().add(
        new FileChooser.ExtensionFilter("JSON Files", "*.json"));

    // Set initial directory to the saves folder
    File saveDir = new File("src/main/resources/saves");
    if (saveDir.exists() && saveDir.isDirectory()) {
      fileChooser.setInitialDirectory(saveDir);
    }

    File file = fileChooser.showOpenDialog(primaryStage);

    if (file != null) {
      try {
        BoardFileHandler fileHandler = new BoardFileHandler();
        GameState gameState = fileHandler.read(file.getAbsolutePath());

        // Create a new game with the loaded state
        this.randomLadders = gameState.isRandomLadders();
        gameController = new LadderGameController(randomLadders);
        gameController.applyGameState(gameState);

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Game Loaded");
        alert.setHeaderText("Game Loaded Successfully");
        alert.setContentText("Your game has been loaded from " + file.getName());
        alert.showAndWait();

        updateBoardUI();
      } catch (FileHandlingException ex) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText("Load Error");
        alert.setContentText("Could not load the game: " + ex.getMessage());
        alert.showAndWait();
      }
    }
  }

  /**
   * Create the scoreboard.
   *
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
   *
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
    String s = "Scoreboard:" + "\n" + scoreBoard.getText();
    scoreBoard.setText(s); // Update the scoreBoard
  }

  /**
   * Update the board UI with the current player positions, changes color of tiles and calls to update the scoreboard.
   */
  public void updateBoardUI() {

    if ((boardGrid) == null) {
      return; // Avoid NullPointerException
    }

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

      String playerColor = player.getColor();
      int tileId = player.getCurrentTile().getTileId();
      int index = tileId - 1;
      int row = index / 10;
      int col = index % 10;
      TextField tileField = (TextField) boardGrid.getChildren().get(row * 10 + col);
      if (tileField.getText().contains("Player")) {
        tileField.setText(tileField.getText() + ", " + (player.getID() + 1)); //+1 to account for indexation, player 1 has ID 0 and so on.
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