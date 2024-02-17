package edu.brown.cs.student.main.exceptions;

public class BadJSONException extends Exception {

  public BadJSONException(String message) {
    super(message);
  }

  public BadJSONException(String message, Throwable e) {
    super(message, e);
  }
}

