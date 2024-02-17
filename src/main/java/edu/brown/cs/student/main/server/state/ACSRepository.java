package edu.brown.cs.student.main.server.state;

import java.net.URI;
import java.time.LocalTime;
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

  public void populateCodeLists() throws URISyntaxException, IOException, InterruptedException {
    HttpRequest buildACSRequest =
        HttpRequest.newBuilder()
            .uri(new URI("https://api.census.gov/data/2010/dec/sf1?get=NAME&for=state:*"))
            .GET()
            .build();

    HttpResponse<String> sentCountryResponse =
        HttpClient.newBuilder()
            .build()
            .send(buildACSRequest, HttpResponse.BodyHandlers.ofString());

    System.out.println(sentCountryResponse.body());

    // TODO: Populate stateCodes list then use that to populate all the countyCodes

  }

  public List<String> fetch(String state, String county) throws URISyntaxException, IOException, InterruptedException {
    populateCodeLists();

    String stateCode = state;
    String countyCode = county;
    if (!stateCode.equals("*")) stateCode = stateCodes.get(state.toLowerCase());
    if (!countyCode.equals("*")) countyCode = countyCodes.get(county.toLowerCase());

    HttpRequest buildACSRequest =
    HttpRequest.newBuilder()
        .uri(new URI("https://api.census.gov/data/2021/acs/acs1/subject/variables?get=NAME,S2802_C03_022E&for=county:"+countyCode+"&in=state:" + stateCode))
        .GET()
        .build();

    // Unsure why there's unhandled exceptions here which don't pop up in handout

    HttpResponse<String> sentACSApiResponse =
        HttpClient.newBuilder()
            .build()
            .send(buildACSRequest, HttpResponse.BodyHandlers.ofString());

    LocalDate currentDate = LocalDate.now();
    LocalTime currentTime = LocalTime.now();

    // ^ Again, unsure why there's unhandled exceptions here which don't pop up in handout

    String responseText = sentACSApiResponse.body();
    System.out.println(responseText);

    // TODO: Figure out form of this response and parse it to return percent, date/time, state, county

  }

}
