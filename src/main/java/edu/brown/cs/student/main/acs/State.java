package edu.brown.cs.student.main.acs;

/**
 * An object encapsulating the information received from the ACS API for a state and a few accessor
 * methods
 */
public class State {
  private String name;
  private String code;

  /**
   * Constructs a new State instance with the specified name and code.
   * @param name The name of the state. This is a human-readable name, such as "California".
   * @param code The code of the state. This is typically a standardized abbreviation or identifier
   *             used in data sources, such as "CA" for California.
   */
  public State(String name, String code) {
    this.name = name;
    this.code = code;
  }

  /**
   * Returns the name of the state.
   * @return A string representing the name of the state.
   */

  public String getStateName() {
    return this.name;
  }

  /**
   * Returns the code of the state.
   * @return A string representing the code of the state. This is typically an abbreviation
   *         or a unique identifier used in databases and APIs.
   */

  public String getStateCode() {
    return this.code;
  }
}
