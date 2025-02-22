package edu.ntnu.idi.bidata.idatg2003mappe.app.laddergame;

import edu.ntnu.idi.bidata.idatg2003mappe.entity.Player;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.util.List;

/**
 * Class for the board game selector GUI.
 *
 * @author Simen Gudbrandsen and Frikk Breadsroed
 * @version 0.0.1
 * @since 22.02.2025
 */
public class LadderGameGUI extends Application {
  private LadderGameController gameController;
  private GridPane boardGrid;
  private TextArea gameLog;
  private final String[] playerColor = {"red", "blue", "green", "yellow", "brown", "purple"};

  @Override
  public void start(Stage primaryStage) throws Exception { //TODO: Add method or screen for selecting players
    gameController = new LadderGameController(6); //Hardcoded 2 players for now

    BorderPane borderPane = new BorderPane();
    borderPane.setPrefSize(960, 540);
    borderPane.setTop(createMenuBar());

    VBox centerBox = new VBox(10);
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

    centerBox.getChildren().addAll(boardGrid, rollDieButton, gameLog);
    borderPane.setCenter(centerBox);

    Scene scene = new Scene(borderPane);
    primaryStage.setScene(scene);
    primaryStage.setTitle("Ladder game");
    primaryStage.show();

    updateBoardUI();
  }

  private GridPane createBoardGrid() {
    GridPane grid = new GridPane();
    grid.setHgap(5);
    grid.setVgap(5);

    for (int row = 10; row > 0; row--) {
      for (int col = 0; col < 10; col++) {
        int tileNumber = row * 10 + col + 1;
        TextField tile = new TextField("Tile " + tileNumber);
        tile.setPrefWidth(60);
        tile.setEditable(false);
        grid.add(tile, col, row);
      }
    }
    return grid;
  }

  private MenuBar createMenuBar() {
    MenuItem openMenuItem = new MenuItem("Open");
    MenuItem saveMenuItem = new MenuItem("Save");
    MenuItem closeMenuItem = new MenuItem("Close");
    Menu fileMenu = new Menu("File");
    fileMenu.getItems().addAll(openMenuItem, saveMenuItem, new SeparatorMenuItem(), closeMenuItem);
    MenuBar menuBar = new MenuBar();
    menuBar.getMenus().addAll(fileMenu);
    return menuBar;
  }

  private void updateBoardUI() {
    for (int row = 0; row < 10; row++) {
      for (int col = 0; col < 10; col++) {
        int tileNumber = row * 10 + col + 1;
        TextField tile = (TextField) boardGrid.getChildren().get(row * 10 + col);
        tile.setText("Tile " + tileNumber);
        tile.setStyle("-fx-background-color: white;");
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
      tileField.setText(player.getName());
      tileField.setStyle("-fx-background-color: " + playerColor + ";");    }
  }


/*

  private Pane createCenterPane() {
    Button button1 = new Button("Roll die");
    GridPane centerPane = new GridPane();
    centerPane.add(button1, 0, 0, 10, 1); // Add button spanning 10 columns

    // creates a 10x10 grid of text fields
    for (int row = 1; row <= 10; row++) {
      for (int col = 0; col < 10; col++) {
        TextField tile = new TextField("Tile " + ((row - 1) * 10 + col));
        tile.setPrefWidth(60); // Set preferred width for better layout
        centerPane.add(tile, col, row);
      }
    }

    return centerPane;
  }

 */

  public static void main(String[] args) {
    launch(args);
  }
}

