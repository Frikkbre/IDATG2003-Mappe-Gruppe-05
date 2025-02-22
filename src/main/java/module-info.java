module edu.ntnu.idi.bidata.idatg2003mappe {
  requires javafx.controls;
  requires javafx.fxml;
  requires javafx.web;

  requires org.controlsfx.controls;
  requires com.dlsc.formsfx;
  requires net.synedra.validatorfx;
  requires org.kordamp.ikonli.javafx;
  requires org.kordamp.bootstrapfx.core;
  requires eu.hansolo.tilesfx;
  requires com.almasb.fxgl.all;

  opens edu.ntnu.idi.bidata.idatg2003mappe to javafx.fxml;
  exports edu.ntnu.idi.bidata.idatg2003mappe;
  exports edu.ntnu.idi.bidata.idatg2003mappe.app;
  opens edu.ntnu.idi.bidata.idatg2003mappe.app to javafx.fxml;
  exports edu.ntnu.idi.bidata.idatg2003mappe.entity;
  opens edu.ntnu.idi.bidata.idatg2003mappe.entity to javafx.fxml;
  exports edu.ntnu.idi.bidata.idatg2003mappe.map;
  opens edu.ntnu.idi.bidata.idatg2003mappe.map to javafx.fxml;
  exports edu.ntnu.idi.bidata.idatg2003mappe.app.laddergame;
  opens edu.ntnu.idi.bidata.idatg2003mappe.app.laddergame to javafx.fxml;
  exports edu.ntnu.idi.bidata.idatg2003mappe.app.missingdiamond;
  opens edu.ntnu.idi.bidata.idatg2003mappe.app.missingdiamond to javafx.fxml;
}