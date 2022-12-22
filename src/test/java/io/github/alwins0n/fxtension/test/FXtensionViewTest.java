package io.github.alwins0n.fxtension.test;

import io.github.alwins0n.fxtension.FXtension;
import io.github.alwins0n.fxtension.RunFXML;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(FXtension.class)
class FXtensionViewTest extends FXtensionBaseTest {

  MyController unit;
  MyModel model;

  @BeforeEach
  void setUp() {
    model = new MyModel();
    unit = new MyController(model);
  }

  @Test
  @RunFXML("views/test.fxml")
  void runView_shouldUpdateModel() {
    assertFalse(model.checkProperty().get());
    unit.check.setSelected(true);
    assertTrue(model.checkProperty().get());
  }

}
