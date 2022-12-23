package io.github.alwins0n.fxtension;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.util.BuilderFactory;
import org.junit.jupiter.api.extension.*;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Logger;

import static javafx.fxml.FXMLLoader.DEFAULT_CHARSET_NAME;

public class FXtension implements Extension,
  TestInstancePreConstructCallback,
  InvocationInterceptor {

  private static final Logger log = Logger.getLogger(FXtension.class.getName());

  private static final AtomicBoolean initialized = new AtomicBoolean();

  @Override
  public void preConstructTestInstance(TestInstanceFactoryContext testInstanceFactoryContext, ExtensionContext extensionContext) throws Exception {
    if (!initialized.getAndSet(true)) {
      log.info("Starting JavaFX");
      var startLatch = new CountDownLatch(1);
      Platform.startup(startLatch::countDown);
      startLatch.await();
      log.info("Started JavaFX");
    }
  }

  @Override
  public void interceptTestMethod(Invocation<Void> invocation, ReflectiveInvocationContext<Method> invocationContext, ExtensionContext extensionContext) throws Throwable {
    var testLatch = new CountDownLatch(1);
    var testMethod = invocationContext.getExecutable();
    if (testMethod.isAnnotationPresent(RunFX.class) || testMethod.isAnnotationPresent(RunFXML.class)) {
      var thrown = new AtomicReference<Throwable>();
      Platform.runLater(() -> {
        try {
          if (testMethod.isAnnotationPresent(RunFXML.class)) {
            setUpView(extensionContext, testMethod.getAnnotation(RunFXML.class));
          }
          invocation.proceed();
        } catch (Throwable throwable) {
          thrown.set(throwable);
        } finally {
          testLatch.countDown();
        }
      });
      testLatch.await();
      if (thrown.get() != null) {
        throw thrown.get();
      }
    } else {
      invocation.proceed();
    }
  }

  private static void setUpView(ExtensionContext extensionContext, RunFXML annotation) {
    var view = annotation.value();
    var viewUrl = getModulePathResourceURL(view);
    if (viewUrl == null) {
      throw new RuntimeException("View not found: " + view);
    }

    var testClass = extensionContext.getTestClass().orElseThrow();
    var testInstance = extensionContext.getTestInstance().orElseThrow();
    var resourceBundle = resolveFieldOfType(testClass, testInstance, ResourceBundle.class);
    var builderFactory = resolveFieldOfType(testClass, testInstance, BuilderFactory.class);
    var charset = resolveFieldOfType(testClass, testInstance, Charset.class);

    try {
      FXMLLoader.load(
        viewUrl,
        resourceBundle.orElse(null),
        builderFactory.orElse(null),
        controllerClass -> resolveFieldOfType(testClass, testInstance, controllerClass).orElseThrow(),
        charset.orElse(Charset.forName(DEFAULT_CHARSET_NAME))
      );
    } catch (IOException e) {
      throw new RuntimeException("Could not load view", e);
    }
  }

  private static <T> Optional<T> resolveFieldOfType(Class<?> testClass, Object testInstance, Class<T> clazz) {
    for (Field field : testClass.getDeclaredFields()) {
      if (field.getType() == clazz) {
        try {
          field.setAccessible(true);
          return Optional.of(field.get(testInstance)).map(clazz::cast);
        } catch (IllegalAccessException e) {
          throw new RuntimeException(e);
        }
      }
    }
    return Optional.empty();
  }

  private static URL getModulePathResourceURL(String path) {
    return FXtension.class.getClassLoader().getResource(path);
  }
}
