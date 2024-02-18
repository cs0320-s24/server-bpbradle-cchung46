package edu.brown.cs.student.main.exceptions;

/** Custom exception designed to handle any exception thrown while parsing. Handled in Main, always.
 */
public class BadCSVException extends Exception {

  public BadCSVException(String message) {
    super(message);
  }

  public BadCSVException(String message, Throwable e) {
    super(message, e);
  }
}
