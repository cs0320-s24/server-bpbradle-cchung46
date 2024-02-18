package edu.brown.cs.student.main.server.state;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

/**
 * Defines the shape of all ACS Repository implementations to be responsible for fetching data from
 * ACS using a state and county.
 */
public interface ACSRepositoryInterface {

  /**
   * Fetches data from an ACS data source based on the provided state and county parameters.
   * Implementations should handle the retrieval of data and return a list of strings
   * representing the fetched data. The specific format and contents of the list depend
   * on the implementation and the data source.
   *
   * @param state  The state being queried for broadband access percentage of
   * @param county The county being queried for broadband access percentage of
   * @return A list of strings representing the data fetched from the ACS data source.
   * @throws URISyntaxException       If the request URI is not formatted correctly.
   * @throws IOException              If an I/O error occurs during data retrieval.
   * @throws InterruptedException     If the operation is interrupted while waiting.
   */
  public List<String> fetch(String state, String county)
      throws URISyntaxException, IOException, InterruptedException;
}
