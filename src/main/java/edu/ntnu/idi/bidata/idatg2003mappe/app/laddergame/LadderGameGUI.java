package edu.ntnu.idi.bidata.idatg2003mappe.app.laddergame;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
    import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

/**
 * Class for the board game selector GUI.
 */
public class LadderGameGUI extends Application {
  @Override
  public void start(Stage primaryStage) throws Exception {
    BorderPane borderPane = new BorderPane();
    borderPane.setMinHeight(540); //16:9 aspect ratio   (1920x1080)/2
    borderPane.setMaxHeight(540);
    borderPane.setMinWidth(960);
    borderPane.setMaxWidth(960);
    borderPane.setPrefHeight(540);
    borderPane.setPrefWidth(960);
    borderPane.setCenter(createCenterPane());
    borderPane.setTop(createMenuBar());
    Scene scene = new Scene(borderPane);
    primaryStage.setScene(scene);
    primaryStage.setTitle("Ladder game");
    primaryStage.show();
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

  private Pane createCenterPane() {
    Button button1 = new Button("Roll die");
    FlowPane centerPane = new FlowPane();
    centerPane.getChildren().addAll(button1);
    centerPane.setAlignment(Pos.CENTER_LEFT);

    //creates a 10x10 grid of tiles
    for(int i = 0; i < 100; i++) {
      Button tile = new Button("Tile " + i);
      centerPane.getChildren().add(tile);
    }

    return centerPane;
  }

  public static void main(String[] args) {
    launch(args);
  }
}

