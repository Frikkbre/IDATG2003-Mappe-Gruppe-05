package edu.ntnu.idi.bidata.idatg2003mappe.filehandling.exceptionhandling;

/**
 * <p>Exception class for JSON parsing errors.</p>
 * <p>This specialized exception is thrown when errors occur during the parsing
 * of JSON data. It extends {@link FileHandlingException} to fit within the
 * file handling exception hierarchy.</p>
 * <p>Common scenarios where this exception is thrown include:</p>
 * <ul>
 *   <li>Malformed JSON syntax</li>
 *   <li>Missing required fields</li>
 *   <li>Incompatible data types</li>
 *   <li>Invalid field values</li>
 * </ul>
 *
 * @author Simen Gudbrandsen and Frikk Breadsroed
 * @version 0.0.1
 * @since 19.04.2025
 */
public class JsonParsingException extends FileHandlingException {

  /**
   * <p>Constructs a new JsonParsingException with the specified detail message.</p>
   * <p>The cause is not initialized, and may subsequently be initialized by
   * a call to {@link #initCause}.</p>
   *
   * @param message The detail message that describes the error
   */
  public JsonParsingException(String message) {
    super(message);
  }

  /**
   * <p>Constructs a new JsonParsingException with the specified detail message and cause.</p>
   * <p>Note that the detail message associated with <code>cause</code> is <i>not</i>
   * automatically incorporated in this exception's detail message.</p>
   *
   * @param message The detail message that describes the error
   * @param cause   The underlying cause of this exception
   */
  public JsonParsingException(String message, Throwable cause) {
    super(message, cause);
  }
}
