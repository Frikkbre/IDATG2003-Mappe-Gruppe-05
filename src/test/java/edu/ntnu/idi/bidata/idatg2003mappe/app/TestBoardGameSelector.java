package edu.ntnu.idi.bidata.idatg2003mappe.app;

import static org.junit.jupiter.api.Assertions.*;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
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
import java.io.FileReader;
import java.io.IOException;
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

  @Test
  @DisplayName("Test getColor with negative index throws exception")
  void testGetColorWithNegativeIndex() {
    // Arrange
    boardGameSelector.getColorList(); // Initialize color list

    // Act & Assert
    assertThrows(IndexOutOfBoundsException.class,
        () -> boardGameSelector.getColor(-1),
        "Should throw exception for negative index");
  }


  @Test
  @DisplayName("Test writeToFile with invalid game type")
  void testWriteToFileWithInvalidGameType() throws InterruptedException {
    // Arrange
    setNumberOfPlayers(2);

    // Act
    CountDownLatch latch = new CountDownLatch(1);
    Platform.runLater(() -> {
      boardGameSelector.writeToFile("invalidGame");
      latch.countDown();
    });
    latch.await(5, TimeUnit.SECONDS);

    // Assert
    // File might be created but without game-specific position data
    if (playerFile.exists()) {
      try (CSVReader reader = new CSVReader(new FileReader(playerFile))) {
        reader.readNext(); // Skip headers
        String[] playerData = reader.readNext();
        // Position should not be set for invalid game type
        assertNotNull(playerData);
      } catch (Exception e) {
        // Expected for invalid game type
      }
    }
  }

  // ========== Edge Case Tests ==========

  @Test
  @DisplayName("Test maximum number of players (5)")
  void testMaximumNumberOfPlayers() throws IOException, InterruptedException {
    // Arrange
    setNumberOfPlayers(5);

    // Act
    CountDownLatch latch = new CountDownLatch(1);
    Platform.runLater(() -> {
      boardGameSelector.writeToFile("ladderGame");
      latch.countDown();
    });
    latch.await(5, TimeUnit.SECONDS);

    // Assert
    assertTrue(playerFile.exists());

    // Count players in file
    try (CSVReader reader = new CSVReader(new FileReader(playerFile))) {
      reader.readNext(); // Skip headers
      int playerCount = 0;
      while (reader.readNext() != null) {
        playerCount++;
      }
      assertEquals(5, playerCount, "Should create exactly 5 players");
    } catch (CsvValidationException e) {
      fail("CSV validation failed: " + e.getMessage());
    }
  }

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

  private void setNumberOfPlayers(int number) throws InterruptedException {
    CountDownLatch latch = new CountDownLatch(1);
    Platform.runLater(() -> {
      try {
        // Access the numberOfPlayers spinner through reflection or make it accessible
        // For this test, we'll create a new spinner with the desired value
        // In real implementation, you might need to make the spinner accessible
        boardGameSelector.start(testStage);
        Spinner<Integer> spinner = findSpinner();
        if (spinner != null) {
          spinner.getValueFactory().setValue(number);
        }
      } catch (Exception e) {
        // Handle exception
      }
      latch.countDown();
    });
    latch.await(5, TimeUnit.SECONDS);
  }

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