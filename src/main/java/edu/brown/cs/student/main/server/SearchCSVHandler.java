package edu.brown.cs.student.main.server;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import edu.brown.cs.student.main.exceptions.BadCSVException;
import edu.brown.cs.student.main.server.state.ServerState;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import spark.Request;
import spark.Response;
import spark.Route;

public class SearchCSVHandler implements Route {

  private final ServerState state;

  public SearchCSVHandler(ServerState state) {
    this.state = state;
  }

  @Override
  public Object handle(Request request, Response response) {

    if (!state.csvloaded) {
      state.logError(new FileNotFoundException("No CSV loaded."));
      return new SearchFailureResponse("error_datasource").serialize();
    }

    String val = request.queryParams("value");
    String colStr = request.queryParams("column");

    Integer colInt = null;
    if (colStr != null) {
      try {
        colInt = Integer.parseInt(colStr);

        // System.out.println("was an integer");

      } catch (NumberFormatException e) {
      }
    }

    Map<String, Object> responseMap = new HashMap<>();

    List<List<String>> matches;
    try {
      if (colStr != null) {
        if (colInt != null) {
          matches = state.search(val, (int) colInt);
        } else {
          matches = state.search(val, colStr);
        }
      } else {
        matches = state.search(val);
      }

      responseMap.put("data", matches);
    } catch (BadCSVException e) {
      state.logError(e);
      return new SearchFailureResponse("error_datasource").serialize();
    }

    return new SearchSuccessResponse(val, colStr, responseMap).serialize();
  }

  public record SearchSuccessResponse(
      String response_type, String value, String column, Map<String, Object> responseMap) {
    public SearchSuccessResponse(String value, String column, Map<String, Object> responseMap) {
      this("success", value, column, responseMap);
    }

    String serialize() {
      try {
        Moshi moshi = new Moshi.Builder().build();
        JsonAdapter<SearchSuccessResponse> adapter = moshi.adapter(SearchSuccessResponse.class);
        return adapter.toJson(this);
      } catch (Exception e) {
        e.printStackTrace();
        throw e;
      }
    }
  }

  public record SearchFailureResponse(String response_type) {
    public SearchFailureResponse() {
      this("error");
    }

    String serialize() {
      Moshi moshi = new Moshi.Builder().build();
      return moshi.adapter(SearchFailureResponse.class).toJson(this);
    }
  }
}
