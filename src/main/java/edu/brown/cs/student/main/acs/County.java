package edu.brown.cs.student.main.acs;

/**
 * An object encapsulating the information received from the ACS API for a county and a few accessor
 * methods
 */
public class County {
  private String name;
  private String code;
  private String stateCode;

  /**
   * Constructs a new County instance with the specified name, state code, and county code.
   * @param name      The name of the county.
   * @param stateCode The code of the state to which the county belongs.
   * @param code      The code representing the county.
   */
  public County(String name, String stateCode, String code) {
    this.name = name;
    this.stateCode = stateCode;
    this.code = code;
  }

  /**
   * Retrieves the name of the county.
   * @return The name of the county.
   */
  public String getCountyName() {
    return this.name;
  }

  /**
   * Retrieves the code of the county.
   * @return The code representing the county.
   */
  public String getCountyCode() {
    return this.code;
  }
}
