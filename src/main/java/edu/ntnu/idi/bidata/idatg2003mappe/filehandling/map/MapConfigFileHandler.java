package edu.ntnu.idi.bidata.idatg2003mappe.filehandling.map;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import edu.ntnu.idi.bidata.idatg2003mappe.filehandling.FileReader;
import edu.ntnu.idi.bidata.idatg2003mappe.filehandling.FileWriter;
import edu.ntnu.idi.bidata.idatg2003mappe.filehandling.exceptionhandling.FileHandlingException;
import edu.ntnu.idi.bidata.idatg2003mappe.filehandling.exceptionhandling.JsonParsingException;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Collectors;

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

  /**
   * Classpath resource path for bundled default map (works in JAR).
   */
  private static final String DEFAULT_MAP_RESOURCE = "/maps/missing_diamond_default.json";

  /**
   * File system path for user-saved maps (for writing custom maps).
   */
  private static final String USER_MAPS_DIR = "data/maps";
  private static final String DEFAULT_MAP_FILE = "missing_diamond_default.json";

  private final Gson gson = new GsonBuilder().setPrettyPrinting().create();

  /**
   * <p>Reads a map configuration from a JSON file on the file system.</p>
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
   * <p>Reads a map configuration from a classpath resource.</p>
   * <p>This method loads bundled resources that work both in development and when
   * packaged as a JAR file.</p>
   *
   * @param resourcePath The classpath resource path (e.g., "/maps/default.json")
   * @return A {@link MapConfig} object containing the deserialized map configuration
   * @throws FileHandlingException If the resource cannot be found or read
   * @throws JsonParsingException  If the JSON content cannot be parsed correctly
   */
  public MapConfig readFromResource(String resourcePath) throws FileHandlingException {
    try (InputStream is = getClass().getResourceAsStream(resourcePath)) {
      if (is == null) {
        throw new FileHandlingException("Resource not found: " + resourcePath, null);
      }
      String jsonContent = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))
          .lines()
          .collect(Collectors.joining("\n"));
      return gson.fromJson(jsonContent, MapConfig.class);
    } catch (IOException e) {
      throw new FileHandlingException("Error reading map resource: " + resourcePath, e);
    } catch (FileHandlingException e) {
      throw e;
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
   * <p>Saves a map configuration to the user data directory.</p>
   * <p>Writes the {@link MapConfig} object to the user data location
   * (<code>data/maps/missing_diamond_default.json</code>).
   * Creates the maps directory if it doesn't exist.</p>
   *
   * @param mapConfig The {@link MapConfig} object to save
   * @throws FileHandlingException If an error occurs while writing the file
   */
  public void saveToDefaultLocation(MapConfig mapConfig) throws FileHandlingException {
    Path mapsDir = Paths.get(USER_MAPS_DIR);
    if (!Files.exists(mapsDir)) {
      try {
        Files.createDirectories(mapsDir);
      } catch (IOException e) {
        throw new FileHandlingException("Failed to create maps directory", e);
      }
    }
    write(mapConfig, USER_MAPS_DIR + "/" + DEFAULT_MAP_FILE);
  }

  /**
   * <p>Loads the default map configuration.</p>
   * <p>First attempts to load from the classpath (bundled resource), which works
   * both in development and when packaged as a JAR. Falls back to file system
   * if classpath resource is not found.</p>
   *
   * @return A {@link MapConfig} object containing the default map configuration
   * @throws FileHandlingException If an error occurs while reading the file
   */
  public MapConfig loadFromDefaultLocation() throws FileHandlingException {
    // First try classpath resource (works in JAR)
    try {
      return readFromResource(DEFAULT_MAP_RESOURCE);
    } catch (FileHandlingException e) {
      // Fall back to user data directory
      return read(USER_MAPS_DIR + "/" + DEFAULT_MAP_FILE);
    }
  }

  /**
   * <p>Gets the classpath resource path for the default map.</p>
   *
   * @return The classpath resource path for the default map
   */
  public static String getDefaultMapResource() {
    return DEFAULT_MAP_RESOURCE;
  }

  /**
   * <p>Checks if the default map resource exists.</p>
   * <p>Checks both the classpath resource and the user data directory.</p>
   *
   * @return <code>true</code> if the default map exists, <code>false</code> otherwise
   */
  public boolean defaultMapExists() {
    // Check classpath resource first
    if (getClass().getResourceAsStream(DEFAULT_MAP_RESOURCE) != null) {
      return true;
    }
    // Fall back to file system check
    File file = new File(USER_MAPS_DIR + "/" + DEFAULT_MAP_FILE);
    return file.exists() && file.isFile();
  }

}
