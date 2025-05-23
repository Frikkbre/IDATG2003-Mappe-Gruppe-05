package edu.ntnu.idi.bidata.idatg2003mappe.app.missingdiamond.service;

import edu.ntnu.idi.bidata.idatg2003mappe.filehandling.exceptionhandling.FileHandlingException;
import edu.ntnu.idi.bidata.idatg2003mappe.filehandling.map.MapConfig;
import edu.ntnu.idi.bidata.idatg2003mappe.filehandling.map.MapConfigFileHandler;

import java.util.stream.IntStream;

/**
 * <p>Service class for handling map configuration operations.</p>
 * <p>This class provides methods for loading, creating, and managing map configurations
 * for the Missing Diamond game. It abstracts the details of file handling and
 * provides a clean interface for working with map configurations.</p>
 * <p>The service handles:</p>
 * <ul>
 *   <li>Loading existing map configurations</li>
 *   <li>Creating default configurations when none exist</li>
 *   <li>Ensuring map configurations are properly formatted</li>
 * </ul>
 *
 * @author Simen Gudbrandsen and Frikk Breadsroed
 * @version 0.0.1
 * @since 23.05.2025
 */
public class MapConfigService {

  /**
   * <p>Loads a map configuration from the default location.</p>
   * <p>This method attempts to load an existing configuration file. If no configuration
   * exists, it creates a default one and saves it to the default location.</p>
   * <p>The process follows these steps:</p>
   * <ol>
   *   <li>Check if a default map configuration file exists</li>
   *   <li>If it exists, load and return it</li>
   *   <li>If it doesn't exist, create a default configuration</li>
   *   <li>Save the default configuration to the default location</li>
   *   <li>Return the newly created configuration</li>
   * </ol>
   *
   * @return The loaded or created map configuration
   * @throws FileHandlingException If an error occurs during loading or saving
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
   * <p>Creates a default map configuration.</p>
   * <p>This method generates a simple map configuration with 5 locations and connections
   * between them. It's used as a fallback when no configuration file exists.</p>
   * <p>The default configuration includes:</p>
   * <ul>
   *   <li>5 locations arranged in a line</li>
   *   <li>Alternating regular and special tiles</li>
   *   <li>Connections between adjacent locations</li>
   * </ul>
   *
   * @return A basic map configuration with default settings
   */
  private static MapConfig createDefaultMapConfig() {
    MapConfig mapConfig = new MapConfig();
    mapConfig.setName("Default Missing Diamond Map");

    IntStream.rangeClosed(1, 5).forEach(i -> {
      boolean isSpecial = (i % 2 == 0);
      mapConfig.addLocation(new MapConfig.Location(
          i,
          isSpecial ? "SpecialLoc" + i : "Location" + i,
          0.1 * i,
          0.5,
          isSpecial
      ));

      if (i > 1) {
        mapConfig.addConnection(new MapConfig.Connection(i - 1, i));
      }
    });

    return mapConfig;
  }

}
