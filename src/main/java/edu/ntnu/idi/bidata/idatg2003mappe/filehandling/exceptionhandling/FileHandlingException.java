package edu.ntnu.idi.bidata.idatg2003mappe.filehandling.exceptionhandling;

/**
 * <p>Exception class for file handling errors.</p>
 * <p>This exception is thrown when errors occur during file operations such as
 * reading, writing, or parsing files. It serves as the base class for more
 * specific file handling exceptions.</p>
 * <p>Common scenarios where this exception is thrown include:</p>
 * <ul>
 *   <li>File not found</li>
 *   <li>Permission denied</li>
 *   <li>Invalid file format</li>
 *   <li>I/O errors during read/write operations</li>
 * </ul>
 *
 * @author Simen Gudbrandsen and Frikk Breadsroed
 * @version 0.0.1
 * @since 11.04.2025
 */
public class FileHandlingException extends Exception {

  /**
   * <p>Constructs a new FileHandlingException with the specified detail message.</p>
   * <p>The cause is not initialized, and may subsequently be initialized by
   * a call to {@link #initCause}.</p>
   *
   * @param message The detail message that describes the error
   */

  public FileHandlingException(String message) {
    super(message);
  }

  /**
   * <p>Constructs a new FileHandlingException with the specified detail message and cause.</p>
   * <p>Note that the detail message associated with <code>cause</code> is <i>not</i>
   * automatically incorporated in this exception's detail message.</p>
   *
   * @param message The detail message that describes the error
   * @param cause   The underlying cause of this exception
   */

  public FileHandlingException(String message, Throwable cause) {
    super(message, cause);
  }

}
