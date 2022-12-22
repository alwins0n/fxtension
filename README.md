# FXTension

Small JUnit5 Extension to unit test JavaFX views/controllers

# Motivation

This project was created to simplify the unit testing of JavaFX views and controllers.

While there are more complete frameworks like [TestFX](https://github.com/TestFX/TestFX) this project aims to be a
lightweight alternative for simple unit tests.

The usecases are

- check if the view is correctly initialized (components wired up correctly)
- check if properties are correctly bound
- check if concurrency is working correctly (being able to mock `Task`s)

# Usage

An example using `@RunFX` in a test class extended with `FXExtension`:

```java

@ExtendWith(FXtension.class)
class FXtensionBasicTest {

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
```

An example using `@RunFXML`:

The view:

```xml
<?xml version="1.0" encoding="UTF-8"?>

<?import javafx.scene.control.CheckBox?>
<?import javafx.scene.layout.VBox?>
<VBox xmlns="http://javafx.com/javafx"
      xmlns:fx="http://javafx.com/fxml"
      fx:controller="io.github.alwins0n.fxtension.test.MyController"><!-- instance of this must be a field in the test -->
    <CheckBox fx:id="check"/>
</VBox>
```

The test:

```java

@ExtendWith(FXtension.class)
class FXtensionViewTest extends FXtensionBaseTest {

    MyController unit; // the controller used by the FXMLLoader
    // BuilderFactory factory; // we could inject other objects for the FXMLLoader here
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
```
