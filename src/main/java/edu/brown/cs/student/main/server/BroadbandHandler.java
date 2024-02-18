package edu.brown.cs.student.main.server;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.JsonDataException;
import com.squareup.moshi.Moshi;
import edu.brown.cs.student.main.server.state.ServerState;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import spark.Request;
import spark.Response;
import spark.Route;

/**
 * Encapsulates all the necessary functions for responding to state, county queries to the ACS API
 */
public class BroadbandHandler implements Route {

  private final ServerState state;

  /**
   * builds BroadbandHandler instance with local variable to access a ServerState object
   * @param state, a ServerState containing a ACSRepositoryInterface object which can process
   * requests to be made to the ACS API
   */
  public BroadbandHandler(ServerState state) {
    this.state = state;
  }

  /**
   * Fetches data based on state and county query parameters to return a serialized JSON string with
   * the state, county, broadband access percentage, and date and time of the request.
   * If any errors occur during data fetching or if parameters are missing, an appropriate error
   * response is serialized and returned.
   *
   * @param request  The HTTP request object containing query parameters.
   * @param response The HTTP response object used to send back data or errors.
   * @return A serialized JSON string representing either a success or failure response.
   *         The success response includes fetched data, while the failure response includes
   *         an error message indicating the type of failure.
   */
  @Override
  public Object handle(Request request, Response response) {

    String queryState = request.queryParams("state");
    String queryCounty = request.queryParams("county");

    if (queryState == null || queryCounty == null) {
      return new FetchFailureResponse("error_bad_request").serialize();
    }

    Map<String, Object> responseMap = new HashMap<>();

    try {
      List<String> data = state.fetch(queryState, queryCounty);

      responseMap.put("state", queryState);
      responseMap.put("county", queryCounty);
      responseMap.put("percentage", data.get(0));
      responseMap.put("local date", data.get(1));
      responseMap.put("local time", data.get(2));

    } catch (URISyntaxException | IOException | InterruptedException e) {
      state.logError(e);
      return new FetchFailureResponse("error_datasource").serialize();
    }
    catch (JsonDataException e) {
      state.logError(e);
      return new FetchFailureResponse("error_bad_json").serialize();
    }

    return new FetchSuccessResponse(responseMap).serialize();
  }

  /**
   * Represents a successful fetch operation, containing the response type and a map
   * of response data.
   * @param response_type The type of response, always "success" for this record.
   * @param responseMap   A map containing key-value pairs of response data. Typically includes
   *                      information such as state, county, percentage, local date, and local time.
   */
  public record FetchSuccessResponse(String response_type, Map<String, Object> responseMap) {

    /**
     * Constructor for creating a FetchSuccessResponse with a specified map of response data.
     * Sets the response type to "success".
     * @param responseMap A map containing the data to be included in the response.
     */
    public FetchSuccessResponse(Map<String, Object> responseMap) {
      this("success", responseMap);
    }

    /**
     * Converts the response into a JSON string with the Moshi library
     * @return A JSON string representing the serialized form of the FetchSuccessResponse instance.
     * @throws Exception If serialization fails, an exception is thrown.
     */
    String serialize() {
      try {
        Moshi moshi = new Moshi.Builder().build();
        JsonAdapter<FetchSuccessResponse> adapter = moshi.adapter(FetchSuccessResponse.class);
        return adapter.toJson(this);
      } catch (Exception e) {
        e.printStackTrace();
        throw e;
      }
    }
  }

  /**
   * Represents a failure response for fetch operations containing a response type indicating
   * the nature of the failure and the default response type of "error".
   * @param response_type The type of response, indicating the nature of the error.
   */

  public record FetchFailureResponse(String response_type) {

    /**
     * Constructor for FetchFailureResponse. Sets the response type to "error".
     */
    public FetchFailureResponse() {
      this("error");
    }

    /**
     * Converts the response into a JSON string indicating an error response using the Moshi library.
     * @return A JSON string representing the serialized form of the FetchFailureResponse instance.
     */

    String serialize() {
      Moshi moshi = new Moshi.Builder().build();
      return moshi.adapter(FetchFailureResponse.class).toJson(this);
    }
  }
}
