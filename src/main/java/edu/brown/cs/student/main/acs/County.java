package edu.brown.cs.student.main.acs;

public class County {
  private String name;
  private String code;

  public County(String name, String code) {
    this.name = name;
    this.code = code;
  }

  public String getCountyName() {
    return this.name;
  }

  public String getCountyCode() {
    return this.code;
  }

}
