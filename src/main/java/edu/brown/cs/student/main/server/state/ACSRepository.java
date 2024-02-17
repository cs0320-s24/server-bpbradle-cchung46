package edu.brown.cs.student.main.server.state;

import edu.brown.cs.student.main.activity.State;
import edu.brown.cs.student.main.activity.ACSAPIUtilities;
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
import java.util.Map;
import java.util.Set;
import spark.Request;
import spark.Response;
import spark.Route;

public class ACSRepository implements ACSRepositoryInterface {
  private HashMap<String, String> stateCodes;
  private HashMap<String, String> countyCodes;

  public ACSRepository() {
    try {
      populateStateCodes();
    } catch(Exception e) {
      System.err.println("Failed to populate state codes list: " + e.getMessage());
    }
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
    String menuAsJson = ACSAPIUtilities.readInJson(sentStateResponse);
    List<State> states = new ArrayList<>();
    try {
      states = ACSAPIUtilities.deserializeMenu(menuAsJson);
    } catch (Exception e) {
      // See note in ActivityHandler about this broad Exception catch... Unsatisfactory, but gets
      // the job done in the gearup where it is not the focus.
      e.printStackTrace();
      System.err.println("Errored while deserializing the menu");
    }

    // TODO: Populate stateCodes list then use that to populate all the countyCodes

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
