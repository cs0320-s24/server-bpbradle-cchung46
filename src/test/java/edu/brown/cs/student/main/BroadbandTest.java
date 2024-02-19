package edu.brown.cs.student.main;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.squareup.moshi.Moshi;
import edu.brown.cs.student.main.server.BroadbandHandler;
import edu.brown.cs.student.main.server.state.ACSMockRepository;
import edu.brown.cs.student.main.server.state.ACSRepository;
import edu.brown.cs.student.main.server.state.ServerState;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import okio.Buffer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import spark.Spark;

public class BroadbandTest {

  @BeforeAll
  public static void setup_before_everything() {
    Spark.port(0);
    Logger.getLogger("").setLevel(Level.WARNING);
  }

  private static HttpURLConnection tryRequest(String apiCall) throws IOException {
    // Configure the connection (but don't actually send the request yet)
    URL requestURL = new URL("http://localhost:" + Spark.port() + "/" + apiCall);
    HttpURLConnection clientConnection = (HttpURLConnection) requestURL.openConnection();

    // The default method is "GET", which is what we're using here.
    // If we were using "POST", we'd need to say so.
    clientConnection.setRequestMethod("GET");

    clientConnection.connect();
    return clientConnection;
  }

  @AfterEach
  public void teardown() {
    Spark.unmap("/broadband");
    Spark.stop();
    Spark.awaitStop();
  }

  @Test
  public void invalidState() throws IOException {
    ACSRepository repo = new ACSRepository();
    ServerState state = new ServerState(repo);

    Spark.get("/broadband", new BroadbandHandler(state));
    Spark.init();
    Spark.awaitInitialization();

    HttpURLConnection clientConnection = tryRequest("broadband?state=Quebec&county=Kings%20County");
    assertEquals(200, clientConnection.getResponseCode());

    Moshi moshi = new Moshi.Builder().build();

    BroadbandHandler.FetchFailureResponse response =
        moshi
            .adapter(BroadbandHandler.FetchFailureResponse.class)
            .fromJson(new Buffer().readFrom(clientConnection.getInputStream()));

    assertEquals("error_datasource", response.response_type());
    clientConnection.disconnect();
  }

  @Test
  public void invalidCounty()  throws IOException {
    ACSRepository repo = new ACSRepository();
    ServerState state = new ServerState(repo);

    Spark.get("/broadband", new BroadbandHandler(state));
    Spark.init();
    Spark.awaitInitialization();

    HttpURLConnection clientConnection = tryRequest("broadband?state=California&county=Middlesex%20County");
    assertEquals(200, clientConnection.getResponseCode());

    Moshi moshi = new Moshi.Builder().build();

    BroadbandHandler.FetchFailureResponse response =
        moshi
            .adapter(BroadbandHandler.FetchFailureResponse.class)
            .fromJson(new Buffer().readFrom(clientConnection.getInputStream()));

    assertEquals("error_bad_request", response.response_type());
    clientConnection.disconnect();
  }

  @Test
  public void nonexistentState()  throws IOException {
    ACSRepository repo = new ACSRepository();
    ServerState state = new ServerState(repo);

    Spark.get("/broadband", new BroadbandHandler(state));
    Spark.init();
    Spark.awaitInitialization();

    HttpURLConnection clientConnection = tryRequest("broadband?state=&county=Kings%20County");
    assertEquals(200, clientConnection.getResponseCode());

    Moshi moshi = new Moshi.Builder().build();

    BroadbandHandler.FetchFailureResponse response =
        moshi
            .adapter(BroadbandHandler.FetchFailureResponse.class)
            .fromJson(new Buffer().readFrom(clientConnection.getInputStream()));

    assertEquals("error_datasource", response.response_type());
    clientConnection.disconnect();
  }

  @Test
  public void nonexistentCounty()  throws IOException {
    ACSRepository repo = new ACSRepository();
    ServerState state = new ServerState(repo);

    Spark.get("/broadband", new BroadbandHandler(state));
    Spark.init();
    Spark.awaitInitialization();

    HttpURLConnection clientConnection = tryRequest("broadband?state=California&county=");
    assertEquals(200, clientConnection.getResponseCode());

    Moshi moshi = new Moshi.Builder().build();

    BroadbandHandler.FetchFailureResponse response =
        moshi
            .adapter(BroadbandHandler.FetchFailureResponse.class)
            .fromJson(new Buffer().readFrom(clientConnection.getInputStream()));

    assertEquals("error_bad_request", response.response_type());
    clientConnection.disconnect();
  }

  @Test
  public void validStateNCounty()  throws IOException, URISyntaxException, InterruptedException {
    String mockData = "[[\"NAME\",\"S2802_C03_022E\",\"state\",\"county\"],\n"
        + "[\"Kings County, California\",\"83.5\",\"06\",\"031\"]]";
    ACSMockRepository mock = new ACSMockRepository(mockData);
    ServerState state = new ServerState(mock);

    Spark.get("/broadband", new BroadbandHandler(state));
    Spark.init();
    Spark.awaitInitialization();

    HttpURLConnection clientConnection = tryRequest("broadband?state=California&county=Kings%20County");
    assertEquals(200, clientConnection.getResponseCode());

    Moshi moshi = new Moshi.Builder().build();

    BroadbandHandler.FetchSuccessResponse response =
        moshi
            .adapter(BroadbandHandler.FetchSuccessResponse.class)
            .fromJson(new Buffer().readFrom(clientConnection.getInputStream()));

    String result = response.responseMap().toString();
    String expected = "{local date=2024-02-18, percentage=83.5, county=Kings County, state=California, local time=18:00:14.697632}";

    // Trim off time form comparison since that changes
    String expected2 = expected.substring(0, expected.indexOf("local time"));
    String result2 = result.substring(0, result.indexOf("local time"));

    assertEquals(expected2, result2);
    clientConnection.disconnect();
  }

}



