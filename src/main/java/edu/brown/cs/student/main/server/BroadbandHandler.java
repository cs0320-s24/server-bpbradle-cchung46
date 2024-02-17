package edu.brown.cs.student.main.server;

import com.squareup.moshi.JsonAdapter;
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
  
  private final ServerState serverState;

  public BroadbandHandler(ServerState state) {
    this.serverState = state;
  }

  @Override
  public Object handle(Request request, Response response) {
    String state = request.queryParams("state");
    String county = request.queryParams("county");

    Map<String, Object> responseMap = new HashMap<>();

    try {
      List<String> data = serverState.fetch(state, county);

      responseMap.put("state", state);
      responseMap.put("county", county);
      responseMap.put("percentage", data.get(0));
      responseMap.put("local date", data.get(1));
      responseMap.put("local time", data.get(2));

    } catch (URISyntaxException | IOException | InterruptedException e) {
      state.logError(e);
      return new FetchFailureResponse().serialize();
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
