package edu.brown.cs.student.main.server;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import edu.brown.cs.student.main.ServerState;
import edu.brown.cs.student.main.exceptions.BadCSVException;
import spark.Request;
import spark.Response;
import spark.Route;

public class LoadCSVHandler implements Route {
  ServerState state;

  public LoadCSVHandler(ServerState state) {
    this.state = state;
  }

  @Override
  public Object handle(Request request, Response response) throws Exception {
    String filepath = request.queryParams("filepath");
    try {
      state.load(filepath);
      return new LoadSuccessResponse(filepath).serialize();
    } catch (BadCSVException e) {
      // do something
      return new LoadFailureResponse().serialize();
    }
  }

  public record LoadSuccessResponse(String response_type, String filepath) {
    public LoadSuccessResponse(String filepath) {
      this("success", filepath);
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
