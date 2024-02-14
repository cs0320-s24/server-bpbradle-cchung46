package edu.brown.cs.student.main.server;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import edu.brown.cs.student.main.DataProxy;
import spark.Request;
import spark.Response;
import spark.Route;

import java.io.FileNotFoundException;
import java.io.FileReader;

public class LoadCSVHandler implements Route {
    DataProxy state;

    public LoadCSVHandler(DataProxy state)
    {
        this.state = state;
    }

    @Override
    public Object handle(Request request, Response response) throws Exception {
        String filepath = request.queryParams("filepath");
        state.load(filepath);
    }
}
