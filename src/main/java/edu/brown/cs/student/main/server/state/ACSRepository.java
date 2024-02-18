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

/**
 * Constructs and handles HTTP request to ACS API based on ACS state and county codes.
 */

public class ACSRepository implements ACSRepositoryInterface {
  private HashMap<String, String> stateCodes;
  private HashMap<String, String> countyCodes;

  private boolean statesPopulated;

  /**
   * Initializes a new instance of the ACSRepositoryInterface. Sets up fields for storing state and
   * county codes and flags the repository as not yet populated with state codes.
   */
  public ACSRepository() {

    this.statesPopulated = false;
    this.stateCodes = new HashMap<>();
    this.countyCodes = new HashMap<>();
  }

  /**
   * Fetches ACS codes for all the states and populates a HashMap for retrieving the code of any
   * state, which is necessary to convert the user queried state into a proper request to the ACS
   * HTTP.
   *
   * @throws URISyntaxException        If the URI for the ACS API is incorrectly formatted.
   * @throws IOException               If an I/O error occurs when sending or receiving from the ACS API.
   * @throws InterruptedException      If the operation is interrupted during the HTTP request.
   * @throws JsonDataException         If parsing the JSON response from the ACS API fails.
   */
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

  /**
   * Fetches ACS codes for all the counties in the given state and populates a HashMap for
   * retrieving the code for all of those counties, which is necessary to convert the user queried
   * counties into a proper request to the ACS HTTP.
   *
   * @param stateCode The code of the state for which county codes are to be fetched.
   * @throws URISyntaxException        If the URI for the ACS API is incorrectly formatted.
   * @throws IOException               If an I/O error occurs when sending or receiving from the ACS API.
   * @throws InterruptedException      If the operation is interrupted during the HTTP request.
   * @throws JsonDataException         If parsing the JSON response from the ACS API fails.
   */
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

  /**
   * Fetches data from ACS API based on user query for state, county pair returning a list
   * containing the percentage, and the date and time of the request.
   *
   * @param state   The name of the state for which data is to be fetched.
   * @param county  The name of the county within the specified state for which data is to be fetched.
   * @return A list containing the fetched percentage, the current local date, and the current local time.
   * @throws URISyntaxException        If the URI for the ACS API is incorrectly formatted.
   * @throws IOException               If an I/O error occurs when sending or receiving from the ACS API.
   * @throws InterruptedException      If the operation is interrupted during the HTTP request.
   * @throws JsonDataException         If parsing the JSON response from the ACS API fails.
   */
  public List<String> fetch(String state, String county)
      throws URISyntaxException, IOException, InterruptedException, JsonDataException, IllegalArgumentException {

    if (!statesPopulated) {
      populateStateCodes();
    }
    String stateCode = stateCodes.get(state.toLowerCase());

    populateCountyCodes(stateCode);
    String countyCode = countyCodes.get(county.toLowerCase());

    if (stateCode == null || countyCode == null) {
      throw new IllegalArgumentException("Invalid state or county name.");
    }

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
