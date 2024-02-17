package edu.brown.cs.student.main.server;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import edu.brown.cs.student.main.exceptions.BadJSONException;
import edu.brown.cs.student.main.exceptions.BadRequestException;
import edu.brown.cs.student.main.exceptions.DataSourceException;
import edu.brown.cs.student.main.server.state.ServerState;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import spark.Request;
import spark.Response;
import spark.Route;

public class FetchACSHandler implements Route {
  
  private final ServerState serverState;

  public FetchACSHandler(ServerState state) {
    this.serverState = state;
  }

  @Override
  public Object handle(Request request, Response response) {
    String state = request.queryParams("state");
    String county = request.queryParams("county");

    Map<String, Object> responseMap = new HashMap<>();
    List<String> matches;

    try {
      matches = serverState.fetch(state, county);
      responseMap.put("data", matches);
    } catch (BadJSONException | BadRequestException | DataSourceException | URISyntaxException | IOException | InterruptedException e) {
      return new FetchFailureResponse(e.getMessage()).serialize();
    }

    return new FetchSuccessResponse(responseMap).serialize();
  }

  public record FetchSuccessResponse(
      String response_type, Map<String, Object> responseMap) {
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

  public record FetchFailureResponse(String response_type, String message) {

    // TODO: Implement Error messages
    public FetchFailureResponse(String e) {
      this("error", e);
    }

    String serialize() {
      Moshi moshi = new Moshi.Builder().build();
      return moshi.adapter(FetchFailureResponse.class).toJson(this);
    }
  }



}
