package edu.ntnu.idi.bidata.idatg2003mappe.filehandling;

import edu.ntnu.idi.bidata.idatg2003mappe.filehandling.exceptionhandling.FileHandlingException;

/**
 * Interface for writing objects to a file.
 *
 * @param <T> The type of object to write.
 */

public interface FileWriter<T> {
  /**
   * Writes an object of type T to a file.
   *
   * @param object   The object to write.
   * @param filePath The path to the file.
   * @throws FileHandlingException If an error occurs while writing the file.
   */
  void write(T object, String filePath) throws FileHandlingException;
}
