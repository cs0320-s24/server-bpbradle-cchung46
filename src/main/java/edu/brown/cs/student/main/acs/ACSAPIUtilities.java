package edu.brown.cs.student.main.acs;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.JsonDataException;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * Provides a way to convert JSON strings into lists of state, county, and broadband data objects.
 */
public class ACSAPIUtilities {

  /**
   * Private constructor to prevent instantiation.
   */
  private ACSAPIUtilities() {}

  /**
   * Convert a JSON string into a list of State objects.
   * @param jsonList The JSON string representing a list of states, each state is expected
   *                 to be a list with at least two elements: the state name and its code.
   * @return A List of State objects converted from a JSON string.
   * @throws IOException If an I/O error occurs during reading the JSON string.
   * @throws JsonDataException If the JSON string does not match the expected format or structure.
   */
  public static List<State> deserializeStates(String jsonList)
      throws IOException, JsonDataException {

    List<State> deserializedStates = new ArrayList<>();

    Moshi moshi = new Moshi.Builder().build();

    Type listType = Types.newParameterizedType(List.class, List.class, String.class);
    JsonAdapter<List<List<String>>> adapter = moshi.adapter(listType);

    List<List<String>> data = adapter.fromJson(jsonList);

    for (int i = 1; i < data.size(); i++) {
      List<String> stateData = data.get(i);
      if (stateData.size() >= 2) { // Assuming at least two elements: name and code
        String name = stateData.get(0);
        String code = stateData.get(1);
        deserializedStates.add(new State(name, code));
      }
    }

    return deserializedStates;
  }

  /**
   * Converts a JSON string into a list of County objects.
   * @param jsonList The JSON string representing a list of counties, each county is expected
   *                 to be a list with at least three elements: the county name, state code,
   *                 and the county code. The county name can include the state name, separated by a comma.
   * @return A List of County objects converted from a JSON string.
   * @throws IOException If an I/O error occurs during reading the JSON string.
   * @throws JsonDataException If the JSON string does not match the expected format or structure.
   */
  public static List<County> deserializeCounties(String jsonList)
      throws IOException, JsonDataException {
    List<County> deserializedCounties = new ArrayList<>();
    Moshi moshi = new Moshi.Builder().build();

    Type listType = Types.newParameterizedType(List.class, List.class, String.class);
    JsonAdapter<List<List<String>>> adapter = moshi.adapter(listType);

    List<List<String>> data = adapter.fromJson(jsonList);

    for (int i = 1; i < data.size(); i++) {
      List<String> countyData = data.get(i);
      if (countyData.size() >= 2) { // Assuming at least two elements: name and code
        String name = countyData.get(0);
        String countyName = name.split(",")[0];

        String stateCode = countyData.get(1);
        String code = countyData.get(2);
        deserializedCounties.add(new County(countyName, stateCode, code));
      }
    }

    return deserializedCounties;
  }

  /**
   * Converts a JSON string into a list of lists, each containing broadband data as strings.
   * Differs from previous deserialize methods by not mapping to a specific Object type.
   * @param jsonList The JSON string representing a list of broadband data, with each
   *                 item in the list expected to be another list of strings.
   * @return A list of list of Strings, representing the deserialized broadband data.
   * @throws IOException If an I/O error occurs during reading the JSON string.
   * @throws JsonDataException If the JSON string does not match the expected format or structure.
   */
  public static List<List<String>> deserializeBroadbandData(String jsonList)
      throws IOException, JsonDataException {
    List<List<String>> deserializedData = new ArrayList<>();

    Moshi moshi = new Moshi.Builder().build();

    Type listType = Types.newParameterizedType(List.class, List.class, String.class);
    JsonAdapter<List<List<String>>> adapter = moshi.adapter(listType);

    List<List<String>> data = adapter.fromJson(jsonList);

    return data;
  }
}
