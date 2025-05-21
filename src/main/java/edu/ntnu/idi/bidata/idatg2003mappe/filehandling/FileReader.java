package edu.ntnu.idi.bidata.idatg2003mappe.filehandling;

import edu.ntnu.idi.bidata.idatg2003mappe.filehandling.exceptionhandling.FileHandlingException;

/**
 * This interface defines a method for reading files and returning their content as an object of type T.
 *
 * @param <T> the type of the object that will be returned after reading the file
 */

public interface FileReader<T> {
  /**
   * Reads a file and returns its content as an object of type T.
   *
   * @param filePath the path to the file to be read
   * @return an object of type T representing the content of the file
   * @throws FileHandlingException if an error occurs while reading the file
   */
  T read(String filePath) throws FileHandlingException;
}
