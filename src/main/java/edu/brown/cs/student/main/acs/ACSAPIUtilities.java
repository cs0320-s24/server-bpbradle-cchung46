package edu.brown.cs.student.main.acs;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.JsonDataException;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class ACSAPIUtilities {

  private ACSAPIUtilities() {}

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
