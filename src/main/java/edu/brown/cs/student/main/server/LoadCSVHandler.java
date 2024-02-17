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

public class LoadCSVHandler implements Route {
  private final ServerState state;

  public LoadCSVHandler(ServerState state) {
    this.state = state;
  }

  @Override
  public Object handle(Request request, Response response) {
    String filepath = request.queryParams("filepath");

    if (filepath.matches(".*\\.\\./.*")) {
      return new LoadFailureResponse("error_datasource").serialize();
    }

    String path = "./data/" + filepath;

    Map<String, Object> responseMap = new HashMap<>();

    try {
      state.load(path);
    } catch (BadCSVException | FileNotFoundException e) {
      state.logError(e);
      return new LoadFailureResponse("error_datasource").serialize();
    }

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
