package io.github.alwins0n.fxtension.test;

import io.github.alwins0n.fxtension.FXtension;
import io.github.alwins0n.fxtension.RunFX;
import javafx.application.Platform;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FXtensionBasicTest extends FXtensionBaseTest{

  @Test
  void withoutAnnotation_shouldRunWithoutFX() {
    assertFalse(Platform.isFxApplicationThread());
  }

  @Test
  @RunFX
  void withAnnotation_shouldRunInFX() {
    assertTrue(Platform.isFxApplicationThread());
  }

}
