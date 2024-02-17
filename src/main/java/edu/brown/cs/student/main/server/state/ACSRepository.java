package edu.brown.cs.student.main.server.state;

import edu.brown.cs.student.main.acs.County;
import edu.brown.cs.student.main.acs.State;
import edu.brown.cs.student.main.acs.ACSAPIUtilities;
import java.net.URI;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDate;

public class ACSRepository implements ACSRepositoryInterface {
  private HashMap<String, String> stateCodes;
  private HashMap<String, String> countyCodes;

  private boolean statesPopulated;

  public ACSRepository() {
    this.statesPopulated = false;
  }

  public void populateStateCodes() throws URISyntaxException, IOException, InterruptedException {

    // Get JSON from API
    HttpRequest buildACSRequest =
        HttpRequest.newBuilder()
            .uri(new URI("https://api.census.gov/data/2010/dec/sf1?get=NAME&for=state:*"))
            .GET()
            .build();

    HttpResponse<String> sentStateResponse =
        HttpClient.newBuilder()
            .build()
            .send(buildACSRequest, HttpResponse.BodyHandlers.ofString());

    // Convert JSON to list of State Objects
    String menuAsJson = ACSAPIUtilities.readInJson(sentStateResponse.body());
    List<State> states = new ArrayList<>();
    try {
      states = ACSAPIUtilities.deserializeStates(menuAsJson);
    } catch (Exception e) {
      // See note in ActivityHandler about this broad Exception catch... Unsatisfactory, but gets
      // the job done in the gearup where it is not the focus.
      e.printStackTrace();
      System.err.println("Errored while deserializing the menu");
    }

    // Add Codes of all the states to the statesCode list to be stored
    for (int i = 1; i < states.size(); i++) {
      State state = states.get(i);
      stateCodes.put(state.getStateName(), state.getStateCode());
    }

    statesPopulated = true;
  }

  public void populateCountyCodes(String stateCode) throws URISyntaxException, IOException, InterruptedException {

    // Get JSON from API
    HttpRequest buildACSRequest =
        HttpRequest.newBuilder()
            .uri(new URI("https://api.census.gov/data/2010/dec/sf1?get=NAME&for=county:*&in=state:" + stateCode))
            .GET()
            .build();

    HttpResponse<String> sentCountyResponse =
        HttpClient.newBuilder()
            .build()
            .send(buildACSRequest, HttpResponse.BodyHandlers.ofString());

    // Convert JSON to list of State Objects
    String menuAsJson = ACSAPIUtilities.readInJson(sentCountyResponse.body());
    List<County> counties = new ArrayList<>();
    try {
      counties = ACSAPIUtilities.deserializeCounties(menuAsJson);
    } catch (Exception e) {
      // See note in ActivityHandler about this broad Exception catch... Unsatisfactory, but gets
      // the job done in the gearup where it is not the focus.
      e.printStackTrace();
      System.err.println("Errored while deserializing the menu");
    }

    // Add Codes of all the states to the countyCodes list to be stored
    for (int i = 1; i < counties.size(); i++) {
      County county = counties.get(i);
      countyCodes.put(county.getCountyName(), county.getCountyCode());
    }
  }

  public List<String> fetch(String state, String county) throws URISyntaxException, IOException, InterruptedException {

    String stateCode = stateCodes.get(state.toLowerCase());
    String countyCode = countyCodes.get(county.toLowerCase());

    HttpRequest buildACSRequest =
    HttpRequest.newBuilder()
        .uri(new URI("https://api.census.gov/data/2021/acs/acs1/subject/variables?get=NAME,S2802_C03_022E&for=county:"+ countyCode +"&in=state:" + stateCode))
        .GET()
        .build();

    HttpResponse<String> sentACSApiResponse =
        HttpClient.newBuilder()
            .build()
            .send(buildACSRequest, HttpResponse.BodyHandlers.ofString());

    LocalDate currentDate = LocalDate.now();
    LocalTime currentTime = LocalTime.now();

    String responseText = sentACSApiResponse.body();
    System.out.println(responseText);

    // TODO: Figure out form of this response and parse it to return percent, date/time, state, county

  }

}
