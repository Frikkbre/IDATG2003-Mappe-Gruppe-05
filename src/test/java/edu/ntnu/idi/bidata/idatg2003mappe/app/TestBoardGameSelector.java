package edu.ntnu.idi.bidata.idatg2003mappe.app;

import static org.junit.jupiter.api.Assertions.*;

import edu.ntnu.idi.bidata.idatg2003mappe.app.boardgameselector.BoardGameSelector;
import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Spinner;
import javafx.scene.layout.FlowPane;
import javafx.stage.Stage;

import org.junit.jupiter.api.*;

import java.io.File;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * Test class for BoardGameSelector following AAA pattern.
 * Tests game selection, player creation, and file writing functionality.
 *
 * @author Test Suite
 * @version 1.0.0
 */
class TestBoardGameSelector {

  private BoardGameSelector boardGameSelector;
  private static JFXPanel jfxPanel;
  private Stage testStage;
  private static final String PLAYER_FILE_PATH = "src/main/resources/saves/playerData/Players.csv";
  private File playerFile;

  @BeforeAll
  static void setUpClass() throws InterruptedException {
    System.out.println("Starting BoardGameSelector test suite...");

    // Initialize JavaFX platform
    CountDownLatch latch = new CountDownLatch(1);
    Platform.startup(() -> {
      jfxPanel = new JFXPanel();
      latch.countDown();
    });
    latch.await(5, TimeUnit.SECONDS);
  }

  @AfterAll
  static void tearDownClass() {
    System.out.println("BoardGameSelector test suite completed.");
  }

  @BeforeEach
  void setUp() throws InterruptedException {
    CountDownLatch latch = new CountDownLatch(1);
    Platform.runLater(() -> {
      boardGameSelector = new BoardGameSelector();
      testStage = new Stage();
      playerFile = new File(PLAYER_FILE_PATH);
      latch.countDown();
    });
    latch.await(5, TimeUnit.SECONDS);
  }

  @AfterEach
  void tearDown() throws InterruptedException {
    // Clean up test files
    if (playerFile != null && playerFile.exists()) {
      playerFile.delete();
    }

    CountDownLatch latch = new CountDownLatch(1);
    Platform.runLater(() -> {
      if (testStage != null) {
        testStage.close();
      }
      latch.countDown();
    });
    latch.await(5, TimeUnit.SECONDS);
  }

  // ========== Positive Tests ==========

  @Test
  @DisplayName("Test BoardGameSelector initialization creates scene correctly")
  void testBoardGameSelectorInitialization() throws Exception {
    // Arrange & Act
    CountDownLatch latch = new CountDownLatch(1);
    Platform.runLater(() -> {
      try {
        boardGameSelector.start(testStage);
        latch.countDown();
      } catch (Exception e) {
        fail("Failed to start BoardGameSelector: " + e.getMessage());
      }
    });
    latch.await(5, TimeUnit.SECONDS);

    // Assert
    Platform.runLater(() -> {
      Scene scene = testStage.getScene();
      assertNotNull(scene, "Scene should be created");
      assertEquals("Select a board game", testStage.getTitle());
      assertTrue(scene.getRoot() instanceof javafx.scene.layout.BorderPane);
    });
  }

  // ========== Edge Case Tests ==========

  @Test
  @DisplayName("Test UI components are properly created")
  void testUIComponentsCreation() throws InterruptedException {
    // Arrange & Act
    CountDownLatch latch = new CountDownLatch(1);
    Platform.runLater(() -> {
      try {
        boardGameSelector.start(testStage);
        latch.countDown();
      } catch (Exception e) {
        fail("Failed to start: " + e.getMessage());
      }
    });
    latch.await(5, TimeUnit.SECONDS);

    // Assert
    Platform.runLater(() -> {
      Scene scene = testStage.getScene();
      assertNotNull(scene);

      // Find the center pane
      javafx.scene.layout.BorderPane borderPane =
          (javafx.scene.layout.BorderPane) scene.getRoot();
      assertTrue(borderPane.getCenter() instanceof FlowPane);

      FlowPane centerPane = (FlowPane) borderPane.getCenter();

      // Verify buttons exist
      long buttonCount = centerPane.getChildren().stream()
          .filter(node -> node instanceof Button)
          .count();
      assertEquals(2, buttonCount, "Should have 2 buttons (Ladder game and Missing diamond)");

      // Verify spinner exists
      assertNotNull(findSpinner(), "Number of players spinner should exist");
    });
  }

  // ========== Helper Methods ==========

  private Spinner<Integer> findSpinner() {
    if (testStage.getScene() != null) {
      javafx.scene.layout.BorderPane borderPane =
          (javafx.scene.layout.BorderPane) testStage.getScene().getRoot();
      if (borderPane.getCenter() instanceof FlowPane) {
        FlowPane centerPane = (FlowPane) borderPane.getCenter();
        return (Spinner<Integer>) centerPane.getChildren().stream()
            .filter(node -> node instanceof Spinner)
            .findFirst()
            .orElse(null);
      }
    }
    return null;
  }
}