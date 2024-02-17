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

  public static List<State> deserializeStates(String jsonList) throws IOException {
    List<State> states = new ArrayList<State>();
    try {
      Moshi moshi = new Moshi.Builder().build();
      // notice the type and JSONAdapter parameterized type match the return type of the method
      // Since List is generic, we shouldn't just pass List.class to the adapter factory.
      // Instead, let's be more precise. Java has built-in classes for talking about generic types
      // programmatically.
      // Building libraries that use them is outside the scope of this class, but we'll follow the
      // Moshi docs'
      // template by creating a Type object corresponding to List<Ingredient>:
      Type listType =
          Types.newParameterizedType(
              List.class, State.class); // Takes in class of the object for JSON to be made from
      JsonAdapter<List<State>> adapter =
          moshi.adapter(listType); // Make adapter based on those classes

      List<State> deserializedStates =
          adapter.fromJson(jsonList); // Does the deserializing w/ adapter into List of JSON Class

      return deserializedStates;
    }
    // From the Moshi Docs (https://github.com/square/moshi):
    //   "Moshi always throws a standard java.io.IOException if there is an error reading the JSON
    // document, or if it is malformed. It throws a JsonDataException if the JSON document is
    // well-formed, but doesn't match the expected format."
    catch (IOException e) {
      // In a real system, we wouldn't println like this, but it's useful for demonstration:
      System.err.println("OrderHandler: string wasn't valid JSON.");
      throw e;
    } catch (JsonDataException e) {
      // In a real system, we wouldn't println like this, but it's useful for demonstration:
      System.err.println("OrderHandler: JSON wasn't in the right format.");
      throw e;
    }
  }

  public static List<County> deserializeCounties(String jsonList) throws IOException {
    List<State> states = new ArrayList<State>();
    try {
      Moshi moshi = new Moshi.Builder().build();
      // notice the type and JSONAdapter parameterized type match the return type of the method
      // Since List is generic, we shouldn't just pass List.class to the adapter factory.
      // Instead, let's be more precise. Java has built-in classes for talking about generic types
      // programmatically.
      // Building libraries that use them is outside the scope of this class, but we'll follow the
      // Moshi docs'
      // template by creating a Type object corresponding to List<Ingredient>:
      Type listType =
          Types.newParameterizedType(
              List.class, County.class); // Takes in class of the object for JSON to be made from
      JsonAdapter<List<County>> adapter =
          moshi.adapter(listType); // Make adapter based on those classes

      List<County> deserializedCounties =
          adapter.fromJson(jsonList); // Does the deserializing w/ adapter into List of JSON Class

      return deserializedCounties;
    }
    // From the Moshi Docs (https://github.com/square/moshi):
    //   "Moshi always throws a standard java.io.IOException if there is an error reading the JSON
    // document, or if it is malformed. It throws a JsonDataException if the JSON document is
    // well-formed, but doesn't match the expected format."
    catch (IOException e) {
      // In a real system, we wouldn't println like this, but it's useful for demonstration:
      System.err.println("OrderHandler: string wasn't valid JSON.");
      throw e;
    } catch (JsonDataException e) {
      // In a real system, we wouldn't println like this, but it's useful for demonstration:
      System.err.println("OrderHandler: JSON wasn't in the right format.");
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

