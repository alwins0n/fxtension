package io.github.alwins0n.fxtension.test;

import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;

public class MyController {
  @FXML
  CheckBox check;

  private final MyModel model;

  public MyController(MyModel model) {
    this.model = model;
  }

  public void initialize() {
    check.selectedProperty().bindBidirectional(model.checkProperty());
  }

}
