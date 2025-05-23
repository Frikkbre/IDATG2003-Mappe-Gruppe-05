package edu.ntnu.idi.bidata.idatg2003mappe.filehandling;

import edu.ntnu.idi.bidata.idatg2003mappe.filehandling.exceptionhandling.FileHandlingException;

/**
 * <p>Interface for reading objects from files.</p>
 * <p>This interface defines a method for reading files and converting their content
 * into objects of a specified type. It is part of a generic file handling system
 * that supports various file formats and object types.</p>
 * <p>Implementations of this interface should handle:</p>
 * <ul>
 *   <li>Reading file content from the specified path</li>
 *   <li>Parsing the content into the appropriate object structure</li>
 *   <li>Validating the parsed data for correctness</li>
 *   <li>Converting the validated data into objects of type T</li>
 * </ul>
 *
 * @param <T> The type of object that will be returned after reading the file
 * @author Simen Gudbrandsen and Frikk Breadsroed
 * @version 0.0.1
 * @since 11.04.2025
 */
public interface FileReader<T> {
  /**
   * <p>Reads a file and returns its content as an object of type T.</p>
   * <p>Implementations should read the file at the specified path, parse its content,
   * and convert it into an object of the appropriate type. The method should handle
   * file access, parsing, and data validation.</p>
   *
   * @param filePath The path to the file to be read
   * @return An object of type T representing the content of the file
   * @throws FileHandlingException If an error occurs while reading the file, such as
   *                               file not found, permission denied, or invalid format
   */
  T read(String filePath) throws FileHandlingException;
}
