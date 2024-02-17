package edu.brown.cs.student.main.exceptions;

public class DataSourceException extends Exception {

  public DataSourceException(String message) {
    super(message);
  }

  public DataSourceException(String message, Throwable e) {
    super(message, e);
  }
}