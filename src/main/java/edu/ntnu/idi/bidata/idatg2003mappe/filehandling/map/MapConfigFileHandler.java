package edu.ntnu.idi.bidata.idatg2003mappe.filehandling.map;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import edu.ntnu.idi.bidata.idatg2003mappe.filehandling.FileReader;
import edu.ntnu.idi.bidata.idatg2003mappe.filehandling.FileWriter;
import edu.ntnu.idi.bidata.idatg2003mappe.filehandling.exceptionhandling.FileHandlingException;
import edu.ntnu.idi.bidata.idatg2003mappe.filehandling.exceptionhandling.JsonParsingException;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * <p>Handles reading and writing map configurations to/from JSON files.</p>
 * <p>This class implements the {@link FileReader} and {@link FileWriter} interfaces
 * to provide functionality for serializing and deserializing {@link MapConfig} objects
 * to and from JSON files.</p>
 * <p>Key features include:</p>
 * <ul>
 *   <li>Reading map configurations from JSON files</li>
 *   <li>Writing map configurations to JSON files</li>
 *   <li>Support for default map locations</li>
 *   <li>Pretty-printed JSON output for readability</li>
 * </ul>
 *
 * @author Simen Gudbrandsen and Frikk Breadsroed
 * @version 0.0.1
 * @since 20.05.2025
 */
public class MapConfigFileHandler implements FileReader<MapConfig>, FileWriter<MapConfig> {

  private static final String DEFAULT_MAPS_DIR = "src/main/resources/maps";
  private static final String DEFAULT_MAP_FILE = "missing_diamond_default.json";

  private final Gson gson = new GsonBuilder().setPrettyPrinting().create();

  /**
   * <p>Reads a map configuration from a JSON file.</p>
   * <p>Parses the JSON content of the specified file and deserializes it into
   * a {@link MapConfig} object.</p>
   *
   * @param filePath The path to the JSON file to read
   * @return A {@link MapConfig} object containing the deserialized map configuration
   * @throws FileHandlingException If an error occurs while reading the file
   * @throws JsonParsingException  If the JSON content cannot be parsed correctly
   */
  @Override
  public MapConfig read(String filePath) throws FileHandlingException {
    try {
      String jsonContent = Files.readString(Paths.get(filePath));
      return gson.fromJson(jsonContent, MapConfig.class);
    } catch (IOException e) {
      throw new FileHandlingException("Error reading map file: " + filePath, e);
    } catch (Exception e) {
      throw new JsonParsingException("Error parsing map JSON: " + e.getMessage(), e);
    }
  }

  /**
   * <p>Writes a map configuration to a JSON file.</p>
   * <p>Serializes the {@link MapConfig} object to JSON format and writes it
   * to the specified file path. Creates any necessary parent directories.</p>
   *
   * @param mapConfig The {@link MapConfig} object to write
   * @param filePath  The path to the output file
   * @throws FileHandlingException If an error occurs while writing the file
   */
  @Override
  public void write(MapConfig mapConfig, String filePath) throws FileHandlingException {
    try {
      // Create directory if it doesn't exist
      Path path = Paths.get(filePath);
      Files.createDirectories(path.getParent());

      // Convert to JSON and write to file
      String jsonContent = gson.toJson(mapConfig);
      Files.writeString(path, jsonContent);
    } catch (IOException e) {
      throw new FileHandlingException("Error writing map to file: " + filePath, e);
    }
  }

  /**
   * <p>Saves a map configuration to the default location.</p>
   * <p>Writes the {@link MapConfig} object to the standard location
   * (<code>src/main/resources/maps/missing_diamond_default.json</code>).
   * Creates the maps directory if it doesn't exist.</p>
   *
   * @param mapConfig The {@link MapConfig} object to save
   * @throws FileHandlingException If an error occurs while writing the file
   */
  public void saveToDefaultLocation(MapConfig mapConfig) throws FileHandlingException {
    Path mapsDir = Paths.get(DEFAULT_MAPS_DIR);
    if (!Files.exists(mapsDir)) {
      try {
        Files.createDirectories(mapsDir);
      } catch (IOException e) {
        throw new FileHandlingException("Failed to create maps directory", e);
      }
    }
    write(mapConfig, DEFAULT_MAPS_DIR + "/" + DEFAULT_MAP_FILE);
  }

  /**
   * <p>Loads the default map configuration.</p>
   * <p>Reads the map configuration from the standard location
   * (<code>src/main/resources/maps/missing_diamond_default.json</code>).</p>
   *
   * @return A {@link MapConfig} object containing the default map configuration
   * @throws FileHandlingException If an error occurs while reading the file
   */
  public MapConfig loadFromDefaultLocation() throws FileHandlingException {
    return read(DEFAULT_MAPS_DIR + "/" + DEFAULT_MAP_FILE);
  }

  /**
   * <p>Checks if the default map file exists.</p>
   * <p>Verifies that the standard map configuration file
   * (<code>src/main/resources/maps/missing_diamond_default.json</code>)
   * exists and is a regular file.</p>
   *
   * @return <code>true</code> if the default map file exists, <code>false</code> otherwise
   */
  public boolean defaultMapExists() {
    File file = new File(DEFAULT_MAPS_DIR + "/" + DEFAULT_MAP_FILE);
    return file.exists() && file.isFile();
  }

}
