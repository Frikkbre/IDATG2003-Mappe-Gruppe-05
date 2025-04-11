package edu.ntnu.idi.bidata.idatg2003mappe.filehandling;

/**
 * Exception class for file handling errors.
 *
 * @author Simen Gudbrandsen and Frikk Breadsroed
 * @version 0.0.1
 * @since 11.04.2025
 */
public class FileHandlingException extends Exception {

  /**
   * Constructs a new FileHandlingException with the specified detail message.
   *
   * @param message The detail message.
   */

  public FileHandlingException(String message) {
    super(message);
  }

  /**
   * Constructs a new FileHandlingException with the specified detail message and cause.
   *
   * @param message The detail message.
   * @param cause The cause of the exception.
   */

  public FileHandlingException(String message, Throwable cause) {
    super(message, cause);
  }

}
