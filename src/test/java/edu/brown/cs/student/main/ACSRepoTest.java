package edu.brown.cs.student.main;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.google.common.cache.CacheStats;
import edu.brown.cs.student.main.acs.*;
import edu.brown.cs.student.main.server.state.*;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Test deserializing soup recipes
 *
 * <p>Because we're using JUnit here, we needed to add JUnit to pom.xml.
 *
 * <p>In a real application, we'd want to test better---e.g., if it's part of our spec that
 * SoupHandler throws an IOException on invalid JSON, we'd want to test that.
 */
public class ACSRepoTest {

  @BeforeEach
  public void setup() {
    // No setup
  }

  @AfterEach
  public void teardown() {
    // No setup
  }

  @Test
  public void deserializeStatesTest() throws IOException {
    String statesList =
        "[[\"NAME\",\"state\"],\n"
            + "[\"Alabama\",\"01\"],\n"
            + "[\"Alaska\",\"02\"],\n"
            + "[\"Arizona\",\"04\"],\n"
            + "[\"Arkansas\",\"05\"],\n"
            + "[\"California\",\"06\"]]";

    List<State> deserialized = ACSAPIUtilities.deserializeStates(statesList);

    List<State> exp =
        List.of(
            new State("Alabama", "01"),
            new State("Alaska", "02"),
            new State("Arizona", "04"),
            new State("Arkansas", "05"),
            new State("California", "06"));

    assertEquals(deserialized, exp);
  }

  @Test
  public void deserializedCountiesTest() throws IOException {
    String countiesList =
        "[[\"NAME\",\"state\",\"county\"],\n"
            + "[\"Colusa County, California\",\"06\",\"011\"],\n"
            + "[\"Butte County, California\",\"06\",\"007\"],\n"
            + "[\"Alameda County, California\",\"06\",\"001\"],\n"
            + "[\"Alpine County, California\",\"06\",\"003\"],\n"
            + "[\"Amador County, California\",\"06\",\"005\"]]";

    List<County> deserialized = ACSAPIUtilities.deserializeCounties(countiesList);

    List<County> exp =
        List.of(
            new County("Colusa County", "06", "011"),
            new County("Butte County", "06", "007"),
            new County("Alameda County", "06", "001"),
            new County("Alpine County", "06", "003"),
            new County("Amador County", "06", "005"));

    assertEquals(deserialized, exp);
  }

  @Test
  public void deserializedDataTest() throws IOException {
    String data =
        "[[\"NAME\",\"S2802_C03_022E\",\"state\",\"county\"],\n"
            + "[\"Kings County, California\",\"83.5\",\"06\",\"031\"]]";

    List<List<String>> deserialized = ACSAPIUtilities.deserializeBroadbandData(data);

    List<List<String>> exp =
        List.of(
            List.of("NAME", "S2802_C03_022E", "state", "county"),
            List.of("Kings County, California", "83.5", "06", "031"));

    assertEquals(deserialized, exp);
  }

  @Test
  public void cachingTest() throws URISyntaxException, IOException, InterruptedException {
    String mockData =
        "[[\"NAME\",\"S2802_C03_022E\",\"state\",\"county\"],\n"
            + "[\"Kings County, California\",\"83.5\",\"06\",\"031\"]]";

    ACSMockRepository mock = new ACSMockRepository(mockData);
    ServerState state = new ServerState(new ACSCachingRepository(mock, 1000, 10, TimeUnit.MINUTES));

    String exp = "83.5";
    List<String> response1 = state.fetch("06", "031");

    assertEquals(exp, response1.get(0));

    ACSCachingRepository repo = (ACSCachingRepository) state.getACSrepo();
    CacheStats stats = repo.getStats();

    assertTrue(stats.hitCount() == 0);
    assertTrue(stats.missCount() == 1);
    assertTrue(stats.loadCount() == 1);

    List<String> response2 = state.fetch("06", "031");

    assertEquals(exp, response1.get(0));

    CacheStats updated = repo.getStats();

    assertTrue(updated.hitCount() == 1);
    assertTrue(updated.missCount() == 1);
    assertTrue(updated.loadCount() == 1);
  }
}
