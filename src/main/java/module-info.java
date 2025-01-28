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
}