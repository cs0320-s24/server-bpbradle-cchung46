package edu.brown.cs.student.main.activity;

import java.util.List;

public class County {
  private String name;
  private int code;

  public County(String name, int code) {
    this.name = name;
    this.code = code;
  }

  public String getCountyName() {
    return this.name;
  }

  public int getCountyCode() {
    return this.code;
  }

}
