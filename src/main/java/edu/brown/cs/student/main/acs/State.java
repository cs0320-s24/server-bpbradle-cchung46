package edu.brown.cs.student.main.acs;

public class State {
  private String name;
  private String code;

  public State(String name, String code) {
    this.name = name;
    this.code = code;
  }

  public String getStateName() {
    return this.name;
  }

  public String getStateCode() {
    return this.code;
  }
}
