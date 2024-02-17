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

public class ViewCSVHandler implements Route {
    private final ServerState state;

    public ViewCSVHandler(ServerState state) {
        this.state = state;
    }

    @Override
    public Object handle(Request request, Response response) {

        if (!state.csvloaded) {
            state.logError(new FileNotFoundException("No CSV loaded."));
            return new ViewFailureResponse("error_datasource").serialize();
        }

        Map<String, Object> responseMap = new HashMap<>();

        responseMap.put("data", state.view());
        return new ViewSuccessResponse(responseMap).serialize();
    }

    public record ViewSuccessResponse(String response_type, Map<String, Object> responseMap) {
        public ViewSuccessResponse(Map<String, Object> responseMap) {
            this("success", responseMap);
        }

        String serialize() {
            try {
                Moshi moshi = new Moshi.Builder().build();
                JsonAdapter<ViewSuccessResponse> adapter = moshi.adapter(ViewSuccessResponse.class);
                return adapter.toJson(this);
            } catch (Exception e) {
                e.printStackTrace();
                throw e;
            }
        }
    }

    public record ViewFailureResponse(String response_type) {
        public ViewFailureResponse() {
            this("error");
        }

        String serialize() {
            Moshi moshi = new Moshi.Builder().build();
            return moshi.adapter(ViewFailureResponse.class).toJson(this);
        }
    }
}
