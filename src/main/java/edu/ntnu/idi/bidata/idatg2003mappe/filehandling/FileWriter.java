package edu.ntnu.idi.bidata.idatg2003mappe.filehandling;

import edu.ntnu.idi.bidata.idatg2003mappe.filehandling.exceptionhandling.FileHandlingException;

/**
 * <p>Interface for writing objects to files.</p>
 * <p>This interface defines a method for serializing objects of a specified type
 * and writing them to files. It is part of a generic file handling system that
 * supports various file formats and object types.</p>
 * <p>Implementations of this interface should handle:</p>
 * <ul>
 *   <li>Converting the object to an appropriate file format</li>
 *   <li>Creating any necessary parent directories</li>
 *   <li>Writing the formatted data to the specified file path</li>
 *   <li>Handling file system errors and access permissions</li>
 * </ul>
 *
 * @param <T> The type of object to write
 *
 * @author Simen Gudbrandsen and Frikk Breadsroed
 * @version 0.0.1
 * @since 11.04.2025
 */
public interface FileWriter<T> {
  /**
   * <p>Writes an object of type T to a file.</p>
   * <p>Implementations should convert the provided object to an appropriate file format
   * and write it to the specified file path. The method should handle serialization,
   * directory creation if needed, and file system access.</p>
   *
   * @param object The object to write
   * @param filePath The path to the file
   * @throws FileHandlingException If an error occurs while writing the file, such as
   *                              permission denied, disk full, or serialization failure
   */
  void write(T object, String filePath) throws FileHandlingException;
}
