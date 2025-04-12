package edu.ntnu.idi.bidata.idatg2003mappe.filehandling.game;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import edu.ntnu.idi.bidata.idatg2003mappe.app.laddergame.LadderGame;
import edu.ntnu.idi.bidata.idatg2003mappe.entity.Player;
import edu.ntnu.idi.bidata.idatg2003mappe.filehandling.FileHandlingException;
import edu.ntnu.idi.bidata.idatg2003mappe.filehandling.FileReader;
import edu.ntnu.idi.bidata.idatg2003mappe.filehandling.FileWriter;
import edu.ntnu.idi.bidata.idatg2003mappe.map.Tile;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * This class is responsible for saving and loading game states to/from JSON files.
 * It implements the FileReader and FileWriter interfaces.
 *
 * @author Simen Gudbrandsen and Frikk Breadsroed
 * @version 0.0.1
 * @since 12.04.2025
 */
public class BoardFileHandler implements edu.ntnu.idi.bidata.idatg2003mappe.filehandling.FileReader<GameState>,
    edu.ntnu.idi.bidata.idatg2003mappe.filehandling.FileWriter<GameState> {

  private static final String DEFAULT_SAVE_DIR = "src/main/resources/saves";
  private static final String DEFAULT_SAVE_FILE = "last_save.json";

  /**
   * Writes a game state to a file in JSON format.
   *
   * @param gameState The game state to write.
   * @param filePath The path to the file.
   * @throws FileHandlingException If an error occurs while writing the file.
   */
  @Override
  public void write(GameState gameState, String filePath) throws FileHandlingException {
    try (BufferedWriter writer = new BufferedWriter(new java.io.FileWriter(filePath))) {
      Gson gson = new GsonBuilder().setPrettyPrinting().create();
      JsonObject jsonObject = new JsonObject();

      // Add current player index
      jsonObject.addProperty("currentPlayerIndex", gameState.getCurrentPlayerIndex());

      // Add random ladders flag
      jsonObject.addProperty("randomLadders", gameState.isRandomLadders());

      // Add players
      JsonArray playersArray = new JsonArray();
      for (Player player : gameState.getPlayers()) {
        JsonObject playerObject = new JsonObject();
        playerObject.addProperty("name", player.getName());
        playerObject.addProperty("id", player.getID());
        playerObject.addProperty("currentTileId", player.getCurrentTile().getTileId());
        playersArray.add(playerObject);
      }
      jsonObject.add("players", playersArray);

      // Add timestamp
      LocalDateTime now = LocalDateTime.now();
      DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
      jsonObject.addProperty("saveTime", now.format(formatter));

      // Write JSON to file
      writer.write(gson.toJson(jsonObject));
    } catch (IOException e) {
      throw new FileHandlingException("Error writing game state to file: " + filePath, e);
    }
  }

  /**
   * Reads a game state from a JSON file.
   *
   * @param filePath The path to the file.
   * @return The game state read from the file.
   * @throws FileHandlingException If an error occurs while reading the file.
   */
  @Override
  public GameState read(String filePath) throws FileHandlingException {
    try {
      String jsonContent = new String(Files.readAllBytes(Paths.get(filePath)));
      JsonObject jsonObject = JsonParser.parseString(jsonContent).getAsJsonObject();

      int currentPlayerIndex = jsonObject.get("currentPlayerIndex").getAsInt();
      boolean randomLadders = jsonObject.get("randomLadders").getAsBoolean();

      GameState gameState = new GameState();
      gameState.setCurrentPlayerIndex(currentPlayerIndex);
      gameState.setRandomLadders(randomLadders);

      // We don't actually reconstruct the players here, as we need the full game context
      // The LadderGameGUI will handle loading the players based on this info

      return gameState;
    } catch (IOException e) {
      throw new FileHandlingException("Error reading game state from file: " + filePath, e);
    }
  }

  /**
   * Saves a game state to the default location (src/main/resources/saves/last_save.json)
   *
   * @param gameState The game state to save.
   * @throws FileHandlingException If an error occurs while saving the file.
   */
  public void saveToDefaultLocation(GameState gameState) throws FileHandlingException {
    ensureSaveDirectoryExists();
    write(gameState, getDefaultSaveFilePath());
  }

  /**
   * Loads a game state from the default location (src/main/resources/saves/last_save.json)
   *
   * @return The loaded game state.
   * @throws FileHandlingException If an error occurs while loading the file.
   */
  public GameState loadFromDefaultLocation() throws FileHandlingException {
    return read(getDefaultSaveFilePath());
  }

  /**
   * Checks if a save file exists at the default location.
   *
   * @return True if a save file exists, false otherwise.
   */
  public boolean defaultSaveExists() {
    File file = new File(getDefaultSaveFilePath());
    return file.exists() && file.isFile();
  }

  /**
   * Gets the default save file path.
   *
   * @return The default save file path.
   */
  public String getDefaultSaveFilePath() {
    return DEFAULT_SAVE_DIR + "/" + DEFAULT_SAVE_FILE;
  }

  /**
   * Ensures that the save directory exists.
   *
   * @throws FileHandlingException If an error occurs while creating the directory.
   */
  private void ensureSaveDirectoryExists() throws FileHandlingException {
    Path saveDir = Paths.get(DEFAULT_SAVE_DIR);
    if (!Files.exists(saveDir)) {
      try {
        Files.createDirectories(saveDir);
      } catch (IOException e) {
        throw new FileHandlingException("Failed to create save directory: " + DEFAULT_SAVE_DIR, e);
      }
    }
  }
}