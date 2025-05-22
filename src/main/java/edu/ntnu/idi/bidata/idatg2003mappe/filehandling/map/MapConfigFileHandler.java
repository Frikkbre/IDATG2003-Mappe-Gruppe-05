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
 * Handles reading and writing map configurations to/from JSON files.
 */
public class MapConfigFileHandler implements FileReader<MapConfig>, FileWriter<MapConfig> {

  private static final String DEFAULT_MAPS_DIR = "src/main/resources/maps";
  private static final String DEFAULT_MAP_FILE = "missing_diamond_default.json";

  private final Gson gson = new GsonBuilder().setPrettyPrinting().create();

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
   * Saves a map configuration to the default location.
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
   * Loads the default map configuration.
   */
  public MapConfig loadFromDefaultLocation() throws FileHandlingException {
    return read(DEFAULT_MAPS_DIR + "/" + DEFAULT_MAP_FILE);
  }

  /**
   * Checks if the default map file exists.
   */
  public boolean defaultMapExists() {
    File file = new File(DEFAULT_MAPS_DIR + "/" + DEFAULT_MAP_FILE);
    return file.exists() && file.isFile();
  }

}