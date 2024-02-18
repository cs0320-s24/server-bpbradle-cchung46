package edu.brown.cs.student.main;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.squareup.moshi.Moshi;
import edu.brown.cs.student.main.exceptions.BadCSVException;
import edu.brown.cs.student.main.server.ViewCSVHandler;
import edu.brown.cs.student.main.server.state.ACSRepository;
import edu.brown.cs.student.main.server.state.ServerState;
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

public class ViewCSVTest {

  @BeforeAll
  public static void setup_before_everything() {
    Spark.port(0);
    Logger.getLogger("").setLevel(Level.WARNING);
  }

  ServerState state = new ServerState(new ACSRepository());

  @BeforeEach
  public void setup() {
    state = new ServerState(new ACSRepository());

    Spark.get("/viewcsv", new ViewCSVHandler(state));
    Spark.init();
    Spark.awaitInitialization();
  }

  @AfterEach
  public void teardown() {
    Spark.unmap("/viewcsv");
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
  public void viewWithLoadedCSV() throws IOException, BadCSVException {
    state.load(
        "/Users/cecily_chung/dev/cs32/server-bpbradle-cchung46/data/census/dol_ri_earnings_disparity.csv",
        "y");
    HttpURLConnection clientConnection = tryRequest("viewcsv");

    assertEquals(200, clientConnection.getResponseCode());

    Moshi moshi = new Moshi.Builder().build();

    System.out.println(clientConnection.getInputStream());
    ViewCSVHandler.ViewSuccessResponse response =
        moshi
            .adapter(ViewCSVHandler.ViewSuccessResponse.class)
            .fromJson(new Buffer().readFrom(clientConnection.getInputStream()));

    List<List<String>> exp =
        List.of(
            List.of(
                "State",
                "Data Type",
                "Average Weekly Earnings",
                "Number of Workers",
                "Earnings Disparity",
                "Employed Percent"),
            List.of("RI", "White", "\" $1,058.47 \"", "395773.6521", " $1.00 ", "75%"),
            List.of("RI", "Black", " $770.26 ", "30424.80376", " $0.73 ", "6%"),
            List.of(
                "RI",
                "Native American/American Indian",
                " $471.07 ",
                "2315.505646",
                " $0.45 ",
                "0%"),
            List.of(
                "RI", "Asian-Pacific Islander", "\" $1,080.09 \"", "18956.71657", " $1.02 ", "4%"),
            List.of("RI", "Hispanic/Latino", " $673.14 ", "74596.18851", " $0.64 ", "14%"),
            List.of("RI", "Multiracial", " $971.89 ", "8883.049171", " $0.92 ", "2%"));

    List<List<String>> actual = (List<List<String>>) response.responseMap().get("data");

    assertEquals(actual, exp);
    clientConnection.disconnect();
  }

  @Test
  public void viewWithoutLoadedCSV() throws IOException {
    HttpURLConnection clientConnection = tryRequest("viewcsv");

    assertEquals(200, clientConnection.getResponseCode());

    Moshi moshi = new Moshi.Builder().build();

    System.out.println(clientConnection.getInputStream());
    ViewCSVHandler.ViewFailureResponse response =
        moshi
            .adapter(ViewCSVHandler.ViewFailureResponse.class)
            .fromJson(new Buffer().readFrom(clientConnection.getInputStream()));

    assertEquals("error_datasource", response.response_type());

    clientConnection.disconnect();
  }
}
