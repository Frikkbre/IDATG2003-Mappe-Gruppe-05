package edu.ntnu.idi.bidata.idatg2003mappe.app;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;

public class NavBar {

  public MenuBar createMenuBar() {
    MenuItem openMenuItem = new MenuItem("Open");
    openMenuItem.setOnAction(openFile());

    MenuItem saveMenuItem = new MenuItem("Save");
    saveMenuItem.setOnAction(saveFile());

    MenuItem closeMenuItem = new MenuItem("Close");
    closeMenuItem.setOnAction(closeFile());

    Menu fileMenu = new Menu("File");

    fileMenu.getItems().addAll(openMenuItem, saveMenuItem, new SeparatorMenuItem(), closeMenuItem);
    MenuBar menuBar = new MenuBar();
    menuBar.getMenus().addAll(fileMenu);
    return menuBar;
  }

  private EventHandler<ActionEvent> openFile() {
    // Implement file opening logic here
    return null;
  }
  private EventHandler<ActionEvent> saveFile() {
    // Implement file saving logic here
    return null;
  }
  private EventHandler<ActionEvent> closeFile() {
    return event -> {
      System.exit(0);
    };
  }
}
