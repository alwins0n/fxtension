package io.github.alwins0n.fxtension.test;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

public class MyModel {

  private final BooleanProperty check = new SimpleBooleanProperty();

  public BooleanProperty checkProperty() {
    return check;
  }

}
