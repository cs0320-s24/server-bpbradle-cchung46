package edu.brown.cs.student.main.server.state;

import edu.brown.cs.student.main.acs.ACSAPIUtilities;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

/**
 *  An ACSRepository implementation that allows for the use of mocked data for testing purposes.
 */

public class ACSMockRepository implements ACSRepositoryInterface {

  // the member storing mocked data
  private String mockData;

  /**
   * Constructor that takes in mocke data.
   *
   * @param mockData is a string version of a json list to be deserialized by fetch()
   */
  public ACSMockRepository(String mockData) {
    this.mockData = mockData;
  }

  /**
   * Performs the fetch() function of an ACSRepository based on the given json without querying the ACS API.
   *
   * @param state The state being queried for broadband access percentage of
   * @param county The county being queried for broadband access percentage of
   * @return the same list as would be expected from an actual API query
   * @throws IOException if deserializingBroadbandData() fails
   */
  public List<String> fetch(String state, String county) throws IOException {

    String date = LocalDate.now().toString();
    String time = LocalTime.now().toString();

    List<List<String>> data = ACSAPIUtilities.deserializeBroadbandData(mockData);
    String percentage = data.get(1).get(1);

    List<String> retval = List.of(percentage, date, time);

    return retval;
  }
}
