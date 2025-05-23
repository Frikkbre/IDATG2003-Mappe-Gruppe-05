package edu.ntnu.idi.bidata.idatg2003mappe.filehandling.game;

import com.google.gson.*;
import edu.ntnu.idi.bidata.idatg2003mappe.filehandling.exceptionhandling.FileHandlingException;
import edu.ntnu.idi.bidata.idatg2003mappe.filehandling.exceptionhandling.JsonParsingException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>This class is responsible for saving and loading game states to/from JSON files.</p>
 * <p>It implements the FileReader and FileWriter interfaces to provide standardized
 * file handling capabilities for game states.</p>
 * <p>The class provides functionality for:</p>
 * <ul>
 *   <li>Serializing game state objects to JSON format</li>
 *   <li>Deserializing JSON data back into game state objects</li>
 *   <li>Saving game states to default or custom locations</li>
 *   <li>Loading game states from default or custom locations</li>
 * </ul>
 *
 * @author Simen Gudbrandsen and Frikk Breadsroed
 * @version 0.0.1
 * @since 12.04.2025
 */
public class BoardFileHandler implements edu.ntnu.idi.bidata.idatg2003mappe.filehandling.FileReader<GameState>,
    edu.ntnu.idi.bidata.idatg2003mappe.filehandling.FileWriter<GameState> {

  /**
   * <p>Writes a game state to a file in JSON format.</p>
   * <p>The method serializes the game state object into a JSON structure and
   * writes it to the specified file path. It includes:</p>
   * <ul>
   *   <li>Current player index</li>
   *   <li>Board configuration details</li>
   *   <li>Player positions and states</li>
   *   <li>Timestamp of when the save was created</li>
   * </ul>
   *
   * @param gameState The {@link GameState} object to write
   * @param filePath  The path to the output file
   * @throws FileHandlingException If an error occurs while writing the file
   */
  @Override
  public void write(GameState gameState, String filePath) throws FileHandlingException {
    try {
      // Create directory if it doesn't exist
      Files.createDirectories(Paths.get(filePath).getParent());

      // Create the main JSON object
      JsonObject jsonObject = new JsonObject();

      // Add current player index
      jsonObject.addProperty("currentPlayerIndex", gameState.getCurrentPlayerIndex());

      // Add random ladders flag
      jsonObject.addProperty("randomLadders", gameState.isRandomLadders());

      // Add players array
      JsonArray playersArray = new JsonArray();
      List<GameState.PlayerPosition> positions = gameState.getPlayerPositions();
      if (positions != null) {
        for (GameState.PlayerPosition position : positions) {
          JsonObject playerObject = new JsonObject();
          playerObject.addProperty("name", position.getName());
          playerObject.addProperty("id", position.getId());
          playerObject.addProperty("currentTileId", position.getTileId());
          playersArray.add(playerObject);
        }
      }
      jsonObject.add("players", playersArray);

      // Add timestamp
      String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
      jsonObject.addProperty("saveTime", timestamp);

      // Write to file
      Gson gson = new GsonBuilder().setPrettyPrinting().create();
      Files.writeString(Paths.get(filePath), gson.toJson(jsonObject));

    } catch (IOException e) {
      throw new FileHandlingException("Error writing game state to file: " + filePath, e);
    }
  }

  /**
   * <p>Reads a game state from a JSON file.</p>
   * <p>The method parses a JSON file and constructs a GameState object from its contents.
   * It performs validation to ensure all required fields are present in the JSON data.</p>
   * <p>The following data is extracted:</p>
   * <ul>
   *   <li>Current player index</li>
   *   <li>Board configuration details (random ladders flag)</li>
   *   <li>Player positions and states</li>
   *   <li>Save timestamp if available</li>
   * </ul>
   *
   * @param filePath The path to the input file
   * @return A fully constructed {@link GameState} object
   * @throws FileHandlingException If an error occurs while reading the file
   * @throws JsonParsingException If the JSON content is invalid or missing required fields
   */
  @Override
  public GameState read(String filePath) throws FileHandlingException {
    try {
      // Read file content
      String jsonContent = Files.readString(Paths.get(filePath));

      // Parse JSON content
      JsonObject jsonObject = JsonParser.parseString(jsonContent).getAsJsonObject();

      // Extract fields with basic validation
      if (!jsonObject.has("currentPlayerIndex") || !jsonObject.has("randomLadders") || !jsonObject.has("players")) {
        throw new JsonParsingException("Invalid JSON: Missing required fields");
      }

      int currentPlayerIndex = jsonObject.get("currentPlayerIndex").getAsInt();
      boolean randomLadders = jsonObject.get("randomLadders").getAsBoolean();

      GameState gameState = new GameState();
      gameState.setCurrentPlayerIndex(currentPlayerIndex);
      gameState.setRandomLadders(randomLadders);

      // Extracting player positions
      JsonArray playersArray = jsonObject.getAsJsonArray("players");
      List<GameState.PlayerPosition> playerPositions = new ArrayList<>();

      for (int i = 0; i < playersArray.size(); i++) {
        JsonObject playerObj = playersArray.get(i).getAsJsonObject();
        String name = playerObj.get("name").getAsString();
        int id = playerObj.get("id").getAsInt();
        int tileId = playerObj.get("currentTileId").getAsInt();

        playerPositions.add(new GameState.PlayerPosition(name, id, tileId));
      }

      gameState.setPlayerPositions(playerPositions);

      // Parse save timestamp if present
      if (jsonObject.has("saveTime")) {
        gameState.setSaveTime(jsonObject.get("saveTime").getAsString());
      }

      return gameState;

    } catch (IOException e) {
      throw new FileHandlingException("Error reading file: " + filePath, e);
    } catch (Exception e) {
      throw new JsonParsingException("Error parsing JSON: " + e.getMessage(), e);
    }
  }

}
