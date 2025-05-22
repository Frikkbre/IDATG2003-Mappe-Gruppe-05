package edu.ntnu.idi.bidata.idatg2003mappe.app.missingdiamond.service;

import edu.ntnu.idi.bidata.idatg2003mappe.filehandling.exceptionhandling.FileHandlingException;
import edu.ntnu.idi.bidata.idatg2003mappe.filehandling.map.MapConfig;
import edu.ntnu.idi.bidata.idatg2003mappe.filehandling.map.MapConfigFileHandler;

/**
 * Service class for handling map configuration operations.
 */
public class MapConfigService {

  /**
   * Loads a map configuration from the default location.
   * If no configuration exists, creates a default one.
   *
   * @return The loaded or created map configuration
   * @throws FileHandlingException If an error occurs during loading
   */
  public static MapConfig loadMapConfig() throws FileHandlingException {
    MapConfigFileHandler fileHandler = new MapConfigFileHandler();

    if (fileHandler.defaultMapExists()) {
      return fileHandler.loadFromDefaultLocation();
    } else {
      MapConfig defaultConfig = createDefaultMapConfig();
      fileHandler.saveToDefaultLocation(defaultConfig);
      return defaultConfig;
    }
  }

  /**
   * Creates a default map configuration.
   * <p>
   * If no map configuration is found, this method will create a simple
   * map with 5 locations and connections.
   *
   * @return A basic map configuration
   */
  private static MapConfig createDefaultMapConfig() {
    MapConfig mapConfig = new MapConfig();
    mapConfig.setName("Default Missing Diamond Map");

    // Create a simple path with 5 locations
    for (int i = 1; i <= 5; i++) {
      boolean isSpecial = (i % 2 == 0);

      mapConfig.addLocation(
          new MapConfig.Location(
              i,
              isSpecial ? "SpecialLoc" + i : "Location" + i,
              0.1 * i,
              0.5,
              isSpecial
          )
      );

      // Add connections
      if (i > 1) {
        mapConfig.addConnection(new MapConfig.Connection(i - 1, i));
      }
    }

    return mapConfig;
  }
}