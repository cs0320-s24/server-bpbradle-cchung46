package edu.brown.cs.student.main.server.state;

import edu.brown.cs.student.main.acs.ACSAPIUtilities;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public class ACSMockRepository implements ACSRepositoryInterface {

  private String mockData;

  public ACSMockRepository(String mockData) {
    this.mockData = mockData;
  }

  public List<String> fetch(String state, String county) throws IOException {

    String date = LocalDate.now().toString();
    String time = LocalTime.now().toString();

    List<List<String>> data = ACSAPIUtilities.deserializeBroadbandData(mockData);
    String percentage = data.get(1).get(1);

    List<String> retval = List.of(percentage, date, time);

    return retval;
  }
}
