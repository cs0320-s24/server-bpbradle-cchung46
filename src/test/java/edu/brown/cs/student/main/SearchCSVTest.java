package edu.brown.cs.student.main;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import com.squareup.moshi.Moshi;
import edu.brown.cs.student.main.exceptions.BadCSVException;
import edu.brown.cs.student.main.server.SearchCSVHandler;
import edu.brown.cs.student.main.server.state.ACSRepository;
import edu.brown.cs.student.main.server.state.ServerState;
import java.io.*;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import okio.Buffer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import spark.Spark;

public class SearchCSVTest {

  @BeforeAll
  public static void setup_before_everything() {
    Spark.port(0);
    Logger.getLogger("").setLevel(Level.WARNING);
  }

  ServerState state = new ServerState(new ACSRepository());

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

  @BeforeEach
  public void setup() {
    this.state = new ServerState(new ACSRepository());

    Spark.get("/searchcsv", new SearchCSVHandler(state));
    Spark.init();
    Spark.awaitInitialization();
  }

  @AfterEach
  public void teardown() {
    Spark.unmap("/searchcsv");
    Spark.stop();
    Spark.awaitStop();
  }

  /* TESTS
    int, str search
      - val exists
      - val doesn't exist
      - val exists in other column
    str, str search
      - val exists
      - val doesn't exist
      - val exists in other column
    str search
      - val exists
      - val doesn't exist
    val not given
    search w/o loading
  */

  @Test
  public void intstrSearchExists() throws BadCSVException, IOException {
    this.state.load(
        "/Users/benbradley/CS Classwork/CS32/server-bpbradle-cchung46/data/census/dol_ri_earnings_disparity.csv",
        "y");

    HttpURLConnection clientConnection = tryRequest("searchcsv?value=White&column=1");
    assertEquals(200, clientConnection.getResponseCode());

    Moshi moshi = new Moshi.Builder().build();

    SearchCSVHandler.SearchSuccessResponse response =
        moshi
            .adapter(SearchCSVHandler.SearchSuccessResponse.class)
            .fromJson(new Buffer().readFrom(clientConnection.getInputStream()));

    List<List<String>> expected =
        List.of(List.of("RI", "White", "\" $1,058.47 \"", "395773.6521", " $1.00 ", "75%"));
    List<List<String>> result = (List<List<String>>) response.responseMap().get("data");

    System.out.println(result);
    assertEquals(expected, result);
    clientConnection.disconnect();
  }

  @Test
  public void intstrSearchDoesntExist() throws BadCSVException, IOException {
    state.load(
        "/Users/benbradley/CS Classwork/CS32/server-bpbradle-cchung46/data/census/dol_ri_earnings_disparity.csv",
        "y");

    HttpURLConnection clientConnection = tryRequest("searchcsv?value=Caucasian&column=1");
    assertEquals(200, clientConnection.getResponseCode());

    Moshi moshi = new Moshi.Builder().build();

    SearchCSVHandler.SearchSuccessResponse response =
        moshi
            .adapter(SearchCSVHandler.SearchSuccessResponse.class)
            .fromJson(new Buffer().readFrom(clientConnection.getInputStream()));

    List<List<String>> expected =
        List.of(List.of("RI", "White", "\" $1,058.47 \"", "395773.6521", " $1.00 ", "75%"));
    List<List<String>> result = (List<List<String>>) response.responseMap().get("data");

    System.out.println(result);
    assertNotEquals(expected, result);
    clientConnection.disconnect();
  }

  @Test
  public void intstrSearchWrongCol() throws BadCSVException, IOException {
    state.load(
        "/Users/benbradley/CS Classwork/CS32/server-bpbradle-cchung46/data/census/dol_ri_earnings_disparity.csv",
        "y");

    HttpURLConnection clientConnection = tryRequest("searchcsv?value=White&column=2");
    assertEquals(200, clientConnection.getResponseCode());

    Moshi moshi = new Moshi.Builder().build();

    SearchCSVHandler.SearchSuccessResponse response =
        moshi
            .adapter(SearchCSVHandler.SearchSuccessResponse.class)
            .fromJson(new Buffer().readFrom(clientConnection.getInputStream()));

    List<List<String>> expected =
        List.of(List.of("RI", "White", "\" $1,058.47 \"", "395773.6521", " $1.00 ", "75%"));
    List<List<String>> result = (List<List<String>>) response.responseMap().get("data");

    System.out.println(result);
    assertNotEquals(expected, result);
    clientConnection.disconnect();
  }

  @Test
  public void strstrSearchExists() throws BadCSVException, IOException {
    state.load(
        "/Users/benbradley/CS Classwork/CS32/server-bpbradle-cchung46/data/census/dol_ri_earnings_disparity.csv",
        "y");

    HttpURLConnection clientConnection = tryRequest("searchcsv?value=White&column=Data%20Type");
    assertEquals(200, clientConnection.getResponseCode());

    Moshi moshi = new Moshi.Builder().build();

    SearchCSVHandler.SearchSuccessResponse response =
        moshi
            .adapter(SearchCSVHandler.SearchSuccessResponse.class)
            .fromJson(new Buffer().readFrom(clientConnection.getInputStream()));

    List<List<String>> expected =
        List.of(List.of("RI", "White", "\" $1,058.47 \"", "395773.6521", " $1.00 ", "75%"));
    List<List<String>> result = (List<List<String>>) response.responseMap().get("data");

    System.out.println(result);
    assertEquals(expected, result);
    clientConnection.disconnect();
  }

  @Test
  public void strstrSearchDoesntExist() throws BadCSVException, IOException {
    state.load(
        "/Users/benbradley/CS Classwork/CS32/server-bpbradle-cchung46/data/census/dol_ri_earnings_disparity.csv",
        "y");

    HttpURLConnection clientConnection = tryRequest("searchcsv?value=Caucasian&column=Data%20Type");
    assertEquals(200, clientConnection.getResponseCode());

    Moshi moshi = new Moshi.Builder().build();

    SearchCSVHandler.SearchSuccessResponse response =
        moshi
            .adapter(SearchCSVHandler.SearchSuccessResponse.class)
            .fromJson(new Buffer().readFrom(clientConnection.getInputStream()));

    List<List<String>> expected =
        List.of(List.of("RI", "White", "\" $1,058.47 \"", "395773.6521", " $1.00 ", "75%"));
    List<List<String>> result = (List<List<String>>) response.responseMap().get("data");

    System.out.println(result);
    assertNotEquals(expected, result);
    clientConnection.disconnect();
  }

  @Test
  public void strstrSearchWrongCol() throws BadCSVException, IOException {
    state.load(
        "/Users/benbradley/CS Classwork/CS32/server-bpbradle-cchung46/data/census/dol_ri_earnings_disparity.csv",
        "y");

    HttpURLConnection clientConnection = tryRequest("searchcsv?value=White&column=State");
    assertEquals(200, clientConnection.getResponseCode());

    Moshi moshi = new Moshi.Builder().build();

    SearchCSVHandler.SearchSuccessResponse response =
        moshi
            .adapter(SearchCSVHandler.SearchSuccessResponse.class)
            .fromJson(new Buffer().readFrom(clientConnection.getInputStream()));

    List<List<String>> expected =
        List.of(List.of("RI", "White", "\" $1,058.47 \"", "395773.6521", " $1.00 ", "75%"));
    List<List<String>> result = (List<List<String>>) response.responseMap().get("data");

    System.out.println(result);
    assertNotEquals(expected, result);
    clientConnection.disconnect();
  }

  @Test
  public void strSearchExists() throws BadCSVException, IOException {
    state.load(
        "/Users/benbradley/CS Classwork/CS32/server-bpbradle-cchung46/data/census/dol_ri_earnings_disparity.csv",
        "y");

    HttpURLConnection clientConnection = tryRequest("searchcsv?value=White");
    assertEquals(200, clientConnection.getResponseCode());

    Moshi moshi = new Moshi.Builder().build();

    SearchCSVHandler.SearchSuccessResponse response =
        moshi
            .adapter(SearchCSVHandler.SearchSuccessResponse.class)
            .fromJson(new Buffer().readFrom(clientConnection.getInputStream()));

    List<List<String>> expected =
        List.of(List.of("RI", "White", "\" $1,058.47 \"", "395773.6521", " $1.00 ", "75%"));
    List<List<String>> result = (List<List<String>>) response.responseMap().get("data");

    System.out.println(result);
    assertEquals(expected, result);
    clientConnection.disconnect();
  }

  @Test
  public void strSearchDoesntExist() throws BadCSVException, IOException {
    state.load(
        "/Users/benbradley/CS Classwork/CS32/server-bpbradle-cchung46/data/census/dol_ri_earnings_disparity.csv",
        "y");

    HttpURLConnection clientConnection = tryRequest("searchcsv?value=Caucasian");
    assertEquals(200, clientConnection.getResponseCode());

    Moshi moshi = new Moshi.Builder().build();

    SearchCSVHandler.SearchSuccessResponse response =
        moshi
            .adapter(SearchCSVHandler.SearchSuccessResponse.class)
            .fromJson(new Buffer().readFrom(clientConnection.getInputStream()));

    List<List<String>> expected =
        List.of(List.of("RI", "White", "\" $1,058.47 \"", "395773.6521", " $1.00 ", "75%"));
    List<List<String>> result = (List<List<String>>) response.responseMap().get("data");

    System.out.println(result);
    assertNotEquals(expected, result);
    clientConnection.disconnect();
  }

  @Test
  public void SearchValNotGiven() throws BadCSVException, IOException {
    state.load(
        "/Users/benbradley/CS Classwork/CS32/server-bpbradle-cchung46/data/census/dol_ri_earnings_disparity.csv",
        "y");

    HttpURLConnection clientConnection = tryRequest("searchcsv?value=");
    assertEquals(200, clientConnection.getResponseCode());

    Moshi moshi = new Moshi.Builder().build();

    SearchCSVHandler.SearchSuccessResponse response =
        moshi
            .adapter(SearchCSVHandler.SearchSuccessResponse.class)
            .fromJson(new Buffer().readFrom(clientConnection.getInputStream()));

    List<List<String>> expected =
        List.of(List.of("RI", "White", "\" $1,058.47 \"", "395773.6521", " $1.00 ", "75%"));
    List<List<String>> result = (List<List<String>>) response.responseMap().get("data");

    System.out.println(result);
    assertNotEquals(expected, result);
    clientConnection.disconnect();
  }

  @Test
  public void SearchWithOutLoading() throws BadCSVException, IOException {
    HttpURLConnection clientConnection = tryRequest("searchcsv?value=");
    assertEquals(200, clientConnection.getResponseCode());

    Moshi moshi = new Moshi.Builder().build();

    SearchCSVHandler.SearchSuccessResponse response =
        moshi
            .adapter(SearchCSVHandler.SearchSuccessResponse.class)
            .fromJson(new Buffer().readFrom(clientConnection.getInputStream()));

    assertEquals("error_datasource", response.response_type());
    clientConnection.disconnect();
  }
}
