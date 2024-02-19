package edu.brown.cs.student.main.server;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import edu.brown.cs.student.main.exceptions.BadCSVException;
import edu.brown.cs.student.main.server.state.ServerState;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;
import spark.Request;
import spark.Response;
import spark.Route;

/**
 * Handles the LoadCSV endpoint. Calls state's load() function.
 */
public class LoadCSVHandler implements Route {
  private final ServerState state;

  /**
   * Stores the shared ServerState object
   *
   * @param state passed from Server, the shared ServerState object
   */
  public LoadCSVHandler(ServerState state) {
    this.state = state;
  }

  /**
   * Handles the request itself. Code modeled from GearUp.
   *
   * @param request
   * @param response
   * @return success or failure response
   */
  @Override
  public Object handle(Request request, Response response) {

    // gets the filepath and header
    String filepath = request.queryParams("filepath");
    String header = request.queryParams("header");

    // if either are null, return a bad request error response
    if (filepath == null || header == null) {
      return new LoadFailureResponse("error_bad_request").serialize();
    }

    // if the file is outside the data directory, return a datasource error response
    if (filepath.matches(".*\\.\\./.*")) {
      return new LoadFailureResponse("error_datasource").serialize();
    }

    String path = "./data/" + filepath;

    Map<String, Object> responseMap = new HashMap<>();

    // call state's load function and deal with the exceptions as necesssary
    try {
      state.load(path, header);
    } catch (BadCSVException | FileNotFoundException e) {
      state.logError(e);
      return new LoadFailureResponse("error_datasource").serialize();
    }

    responseMap.put("header", header);
    responseMap.put("filepath", path);
    return new LoadSuccessResponse(responseMap).serialize();
  }

  public record LoadSuccessResponse(String response_type, Map<String, Object> responseMap) {
    public LoadSuccessResponse(Map<String, Object> responseMap) {
      this("success", responseMap);
    }

    String serialize() {
      try {
        Moshi moshi = new Moshi.Builder().build();
        JsonAdapter<LoadSuccessResponse> adapter = moshi.adapter(LoadSuccessResponse.class);
        return adapter.toJson(this);
      } catch (Exception e) {
        e.printStackTrace();
        throw e;
      }
    }
  }

  public record LoadFailureResponse(String response_type) {
    public LoadFailureResponse() {
      this("error");
    }

    String serialize() {
      Moshi moshi = new Moshi.Builder().build();
      return moshi.adapter(LoadFailureResponse.class).toJson(this);
    }
  }
}
