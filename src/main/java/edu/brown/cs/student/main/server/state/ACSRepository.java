package edu.brown.cs.student.main.server.state;

import com.squareup.moshi.JsonDataException;
import edu.brown.cs.student.main.acs.ACSAPIUtilities;
import edu.brown.cs.student.main.acs.County;
import edu.brown.cs.student.main.acs.State;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ACSRepository implements ACSRepositoryInterface {
  private HashMap<String, String> stateCodes;
  private HashMap<String, String> countyCodes;

  private boolean statesPopulated;

  public ACSRepository() {

    this.statesPopulated = false;
    this.stateCodes = new HashMap<>();
    this.countyCodes = new HashMap<>();
  }

  public void populateStateCodes() throws URISyntaxException, IOException, InterruptedException, JsonDataException {

    // Get JSON from API
    HttpRequest buildACSRequest =
        HttpRequest.newBuilder()
            .uri(new URI("https://api.census.gov/data/2010/dec/sf1?get=NAME&for=state:*"))
            .GET()
            .build();

    HttpResponse<String> sentStateResponse =
        HttpClient.newBuilder().build().send(buildACSRequest, HttpResponse.BodyHandlers.ofString());

    // Convert JSON to list of State Objects
    List<State> states = ACSAPIUtilities.deserializeStates(sentStateResponse.body());

    // Add Codes of all the states to the statesCode list to be stored
    for (int i = 1; i < states.size(); i++) {
      State state = states.get(i);

      // System.out.println("on state: " + state.getStateName());

      stateCodes.put(state.getStateName().toLowerCase(), state.getStateCode());
    }

    statesPopulated = true;
  }

  public void populateCountyCodes(String stateCode)
      throws URISyntaxException, IOException, InterruptedException, JsonDataException {

    // Get JSON from API
    HttpRequest buildACSRequest =
        HttpRequest.newBuilder()
            .uri(
                new URI(
                    "https://api.census.gov/data/2010/dec/sf1?get=NAME&for=county:*&in=state:"
                        + stateCode))
            .GET()
            .build();

    HttpResponse<String> sentCountyResponse =
        HttpClient.newBuilder().build().send(buildACSRequest, HttpResponse.BodyHandlers.ofString());

    // Convert JSON to list of State Objects
    // String countiesAsJson = ACSAPIUtilities.readInJson(sentCountyResponse.body());
    List<County> counties = ACSAPIUtilities.deserializeCounties(sentCountyResponse.body());

    // Add Codes of all the states to the countyCodes list to be stored
    for (int i = 1; i < counties.size(); i++) {
      County county = counties.get(i);
      countyCodes.put(county.getCountyName().toLowerCase(), county.getCountyCode());
    }
  }

  public List<String> fetch(String state, String county)
      throws URISyntaxException, IOException, InterruptedException, JsonDataException {

    if (!statesPopulated) {
      populateStateCodes();
    }
    String stateCode = stateCodes.get(state.toLowerCase());

    populateCountyCodes(stateCode);
    String countyCode = countyCodes.get(county.toLowerCase());

    HttpRequest buildACSRequest =
        HttpRequest.newBuilder()
            .uri(
                new URI(
                    "https://api.census.gov/data/2021/acs/acs1/subject/variables?get=NAME,S2802_C03_022E&for=county:"
                        + countyCode
                        + "&in=state:"
                        + stateCode))
            .GET()
            .build();

    HttpResponse<String> sentACSApiResponse =
        HttpClient.newBuilder().build().send(buildACSRequest, HttpResponse.BodyHandlers.ofString());

    String date = LocalDate.now().toString();
    String time = LocalTime.now().toString();

    List<List<String>> data = ACSAPIUtilities.deserializeBroadbandData(sentACSApiResponse.body());
    String percentage = data.get(1).get(1);

    List<String> retval = List.of(percentage, date, time);

    return retval;
  }
}
