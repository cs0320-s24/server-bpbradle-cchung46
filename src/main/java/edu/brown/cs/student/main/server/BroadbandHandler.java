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

public class BroadbandHandler implements Route {

  private final ServerState state;

  public BroadbandHandler(ServerState state) {
    this.state = state;
  }

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
    catch (IllegalArgumentException e) {
      state.logError(e);
      return new FetchFailureResponse("error_bad_request").serialize();
    }

    return new FetchSuccessResponse(responseMap).serialize();
  }

  public record FetchSuccessResponse(String response_type, Map<String, Object> responseMap) {
    public FetchSuccessResponse(Map<String, Object> responseMap) {
      this("success", responseMap);
    }

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

  public record FetchFailureResponse(String response_type) {
    public FetchFailureResponse() {
      this("error");
    }

    String serialize() {
      Moshi moshi = new Moshi.Builder().build();
      return moshi.adapter(FetchFailureResponse.class).toJson(this);
    }
  }
}
