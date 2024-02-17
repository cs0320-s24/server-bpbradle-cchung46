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

public class ACSAPIUtilities {

  private ACSAPIUtilities() {}

  public static List<State> deserializeStates(String jsonList)
      throws IOException, JsonDataException {

    System.out.println("in deserializeStates");

    List<State> deserializedStates = new ArrayList<>();
    try {
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

      System.out.println("returning from deserialize states");

      return deserializedStates;
    } catch (IOException e) {
      // In a real system, we wouldn't println like this, but it's useful for demonstration:
      System.err.println("string wasn't valid JSON.");
      throw e;
    } catch (JsonDataException e) {
      // In a real system, we wouldn't println like this, but it's useful for demonstration:
      System.err.println("JSON wasn't in the right format.");
      throw e;
    }
  }

  public static List<County> deserializeCounties(String jsonList)
      throws IOException, JsonDataException {
    List<County> deserializedCounties = new ArrayList<>();
    try {
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
    } catch (IOException e) {
      // In a real system, we wouldn't println like this, but it's useful for demonstration:
      System.err.println("string wasn't valid JSON.");
      throw e;
    } catch (JsonDataException e) {
      // In a real system, we wouldn't println like this, but it's useful for demonstration:
      System.err.println("JSON wasn't in the right format.");
      throw e;
    }
  }

  public static List<List<String>> deserializeBroadbandData(String jsonList)
      throws IOException, JsonDataException {
    List<List<String>> deserializedData = new ArrayList<>();
    try {
      Moshi moshi = new Moshi.Builder().build();

      Type listType = Types.newParameterizedType(List.class, List.class, String.class);
      JsonAdapter<List<List<String>>> adapter = moshi.adapter(listType);

      List<List<String>> data = adapter.fromJson(jsonList);

      return data;
    } catch (IOException e) {
      // In a real system, we wouldn't println like this, but it's useful for demonstration:
      System.err.println("string wasn't valid JSON.");
      throw e;
    } catch (JsonDataException e) {
      // In a real system, we wouldn't println like this, but it's useful for demonstration:
      System.err.println("JSON wasn't in the right format.");
      throw e;
    }
  }

  public static String readInJson(String filepath) {
    try {
      return new String(Files.readAllBytes(Paths.get(filepath)));
    } catch (IOException e) {
      return "Error in reading JSON";
    }
  }
}
