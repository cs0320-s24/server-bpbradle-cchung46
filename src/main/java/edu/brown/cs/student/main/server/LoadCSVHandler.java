package edu.brown.cs.student.main.server;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import edu.brown.cs.student.main.ServerState;
import spark.Request;
import spark.Response;
import spark.Route;

import java.io.FileNotFoundException;
import java.io.FileReader;

public class LoadCSVHandler implements Route {
    ServerState state;

    public LoadCSVHandler(ServerState state)
    {
        this.state = state;
    }

    @Override
    public Object handle(Request request, Response response) throws Exception {
        String filepath = request.queryParams("filepath");
        state.load(filepath);
    }
}
