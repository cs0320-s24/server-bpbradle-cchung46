package edu.brown.cs.student.main.activity;

public class State {
  private String name;
  private int code;

  public State(String name, int code) {
    this.name = name;
    this.code = code;
  }

  public String getStateName() {
    return this.name;
  }

  public int getStateCode() {
    return this.code;
  }

}
