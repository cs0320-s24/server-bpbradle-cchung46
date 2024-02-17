package edu.brown.cs.student.main.acs;

public class County {
  private String name;
  private String code;
  private String stateCode;

  public County(String name, String stateCode, String code) {
    this.name = name;
    this.stateCode = stateCode;
    this.code = code;
  }

  public String getCountyName() {
    return this.name;
  }

  public String getCountyCode() {
    return this.code;
  }
}
