package edu.brown.cs.student.main;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.squareup.moshi.Moshi;
import edu.brown.cs.student.main.server.LoadCSVHandler;
import edu.brown.cs.student.main.server.state.ACSRepository;
import edu.brown.cs.student.main.server.state.ServerState;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import okio.Buffer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import spark.Spark;

public class LoadCSVTest {

  @BeforeAll
  public static void setup_before_everything() {
    Spark.port(0);
    Logger.getLogger("").setLevel(Level.WARNING);
  }

  ServerState state = new ServerState(new ACSRepository());

  @BeforeEach
  public void setup() {
    state = new ServerState(new ACSRepository());

    Spark.get("/loadcsv", new LoadCSVHandler(state));
    Spark.init();
    Spark.awaitInitialization();
  }

  @AfterEach
  public void teardown() {
    Spark.unmap("/loadcsv");
    Spark.stop();
    Spark.awaitStop();
  }

  private static HttpURLConnection tryRequest(String apiCall) throws IOException {
    URL requestURL = new URL("http://localhost:" + Spark.port() + "/" + apiCall);
    HttpURLConnection clientConnection = (HttpURLConnection) requestURL.openConnection();

    clientConnection.setRequestMethod("GET");

    clientConnection.connect();
    return clientConnection;
  }

  @Test
  public void loadValidCSV() throws IOException {
    HttpURLConnection clientConnection =
        tryRequest("loadcsv?filepath=census/dol_ri_earnings_disparity.csv&header=y");

    assertEquals(200, clientConnection.getResponseCode());

    Moshi moshi = new Moshi.Builder().build();

    System.out.println(clientConnection.getInputStream());
    LoadCSVHandler.LoadSuccessResponse response =
        moshi
            .adapter(LoadCSVHandler.LoadSuccessResponse.class)
            .fromJson(new Buffer().readFrom(clientConnection.getInputStream()));

    String exp = "./data/census/dol_ri_earnings_disparity.csv";
    String actual = (String) response.responseMap().get("filepath");

    assertEquals(actual, exp);

    clientConnection.disconnect();
  }

  @Test
  public void loadEmptyCSV() throws IOException {
    HttpURLConnection clientConnection = tryRequest("loadcsv?filepath=empty.csv&header=n");

    assertEquals(200, clientConnection.getResponseCode());

    Moshi moshi = new Moshi.Builder().build();

    System.out.println(clientConnection.getInputStream());
    LoadCSVHandler.LoadSuccessResponse response =
        moshi
            .adapter(LoadCSVHandler.LoadSuccessResponse.class)
            .fromJson(new Buffer().readFrom(clientConnection.getInputStream()));

    String exp = "./data/empty.csv";
    String actual = (String) response.responseMap().get("filepath");

    assertEquals(actual, exp);

    clientConnection.disconnect();
  }

  @Test
  public void loadNonexistentCSV() throws IOException {
    HttpURLConnection clientConnection =
        tryRequest("loadcsv?filepath=census/dol_ri_earnings.csv&header=y");

    assertEquals(200, clientConnection.getResponseCode());

    Moshi moshi = new Moshi.Builder().build();

    System.out.println(clientConnection.getInputStream());
    LoadCSVHandler.LoadFailureResponse response =
        moshi
            .adapter(LoadCSVHandler.LoadFailureResponse.class)
            .fromJson(new Buffer().readFrom(clientConnection.getInputStream()));

    assertEquals("error_datasource", response.response_type());

    clientConnection.disconnect();
  }

  @Test
  public void loadMalformedCSV() throws IOException {
    HttpURLConnection clientConnection =
        tryRequest("loadcsv?filepath=malformed/malformed_signs.csv&header=y");

    assertEquals(200, clientConnection.getResponseCode());

    Moshi moshi = new Moshi.Builder().build();

    System.out.println(clientConnection.getInputStream());
    LoadCSVHandler.LoadFailureResponse response =
        moshi
            .adapter(LoadCSVHandler.LoadFailureResponse.class)
            .fromJson(new Buffer().readFrom(clientConnection.getInputStream()));

    assertEquals("error_datasource", response.response_type());

    clientConnection.disconnect();
  }

  @Test
  public void loadInaccessibleCSV() throws IOException {
    HttpURLConnection clientConnection =
        tryRequest(
            "loadcsv?filepath=/Users/cecily_chung/dev/cs32/server-bpbradle-cchung46/data/census/dol_ri_earnings_disparity.csv&header=y");

    assertEquals(200, clientConnection.getResponseCode());

    Moshi moshi = new Moshi.Builder().build();

    System.out.println(clientConnection.getInputStream());
    LoadCSVHandler.LoadFailureResponse response =
        moshi
            .adapter(LoadCSVHandler.LoadFailureResponse.class)
            .fromJson(new Buffer().readFrom(clientConnection.getInputStream()));

    assertEquals("error_datasource", response.response_type());

    clientConnection.disconnect();
  }

  @Test
  public void noCSVGiven() throws IOException {
    HttpURLConnection clientConnection = tryRequest("loadcsv");

    assertEquals(200, clientConnection.getResponseCode());

    Moshi moshi = new Moshi.Builder().build();

    System.out.println(clientConnection.getInputStream());
    LoadCSVHandler.LoadFailureResponse response =
        moshi
            .adapter(LoadCSVHandler.LoadFailureResponse.class)
            .fromJson(new Buffer().readFrom(clientConnection.getInputStream()));

    assertEquals("error_bad_request", response.response_type());

    clientConnection.disconnect();
  }
}
