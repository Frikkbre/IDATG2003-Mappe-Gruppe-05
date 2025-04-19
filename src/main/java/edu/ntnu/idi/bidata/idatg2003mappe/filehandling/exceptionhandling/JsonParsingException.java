package edu.ntnu.idi.bidata.idatg2003mappe.filehandling.exceptionhandling;

/**
 * Exception class for JSON parsing errors.
 *
 * @author Simen Gudbrandsen and Frikk Breadsroed
 * @version 0.0.1
 * @since 19.04.2025
 */
public class JsonParsingException extends FileHandlingException {

  /**
   * Constructs a new JsonParsingException with the specified detail message.
   *
   * @param message The detail message.
   */
  public JsonParsingException(String message) {
    super(message);
  }

  /**
   * Constructs a new JsonParsingException with the specified detail message and cause.
   *
   * @param message The detail message.
   * @param cause The cause of the exception.
   */
  public JsonParsingException(String message, Throwable cause) {
    super(message, cause);
  }
}