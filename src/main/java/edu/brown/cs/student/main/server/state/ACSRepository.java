package edu.brown.cs.student.main.server.state;

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

  public void populateStateCodes() throws URISyntaxException, IOException, InterruptedException {

    System.out.println("in populateStateCodes()");

    // Get JSON from API
    HttpRequest buildACSRequest =
        HttpRequest.newBuilder()
            .uri(new URI("https://api.census.gov/data/2010/dec/sf1?get=NAME&for=state:*"))
            .GET()
            .build();

    HttpResponse<String> sentStateResponse =
        HttpClient.newBuilder().build().send(buildACSRequest, HttpResponse.BodyHandlers.ofString());

    System.out.println("successfully built + sent response");

    // Convert JSON to list of State Objects
    // String statesAsJson = ACSAPIUtilities.readInJson(sentStateResponse.body());
    List<State> states = new ArrayList<>();
    try {

      System.out.println("calling deserializeStates");

      states = ACSAPIUtilities.deserializeStates(sentStateResponse.body());

      System.out.println("successfully deserialized states");

    } catch (Exception e) {
      // See note in ActivityHandler about this broad Exception catch... Unsatisfactory, but gets
      // the job done in the gearup where it is not the focus.
      e.printStackTrace();
      System.err.println("Errored while deserializing states.");
    }

    System.out.println("turning states into map");

    System.out.println("states size is: " + states.size());

    // Add Codes of all the states to the statesCode list to be stored
    for (int i = 1; i < states.size(); i++) {
      State state = states.get(i);

      // System.out.println("on state: " + state.getStateName());

      stateCodes.put(state.getStateName().toLowerCase(), state.getStateCode());
    }

    statesPopulated = true;

    System.out.println("returning from populateStates");
  }

  public void populateCountyCodes(String stateCode)
      throws URISyntaxException, IOException, InterruptedException {

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
    List<County> counties = new ArrayList<>();
    try {
      counties = ACSAPIUtilities.deserializeCounties(sentCountyResponse.body());
    } catch (Exception e) {
      // See note in ActivityHandler about this broad Exception catch... Unsatisfactory, but gets
      // the job done in the gearup where it is not the focus.
      e.printStackTrace();
      System.err.println("Errored while deserializing counties.");
    }

    // Add Codes of all the states to the countyCodes list to be stored
    for (int i = 1; i < counties.size(); i++) {
      County county = counties.get(i);
      countyCodes.put(county.getCountyName().toLowerCase(), county.getCountyCode());
    }
  }

  public List<String> fetch(String state, String county)
      throws URISyntaxException, IOException, InterruptedException {

    System.out.println("in fetchi in ACSrepo");

    if (!statesPopulated) {
      populateStateCodes();
    }

    String stateCode = stateCodes.get(state.toLowerCase());

    populateCountyCodes(stateCode);

    System.out.println("populated states successfully");

    System.out.println("state is: " + state);
    System.out.println("county is: " + county);

    String countyCode = countyCodes.get(county.toLowerCase());

    System.out.println("stateCode: " + stateCode);
    System.out.println("countyCode: " + countyCode);

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

    System.out.println("built + received ACI responses");

    System.out.println("response is: " + sentACSApiResponse.body());

    String date = LocalDate.now().toString();
    String time = LocalTime.now().toString();

    System.out.println("date: " + date);
    System.out.println("time: " + time);

    List<List<String>> data = ACSAPIUtilities.deserializeBroadbandData(sentACSApiResponse.body());

    System.out.println("deserialized data successfully");

    String percentage = data.get(1).get(1);

    System.out.println("% is: " + percentage);

    List<String> retval = List.of(percentage, date, time);

    System.out.println("returning from ACSrepo");

    return retval;
  }
}
