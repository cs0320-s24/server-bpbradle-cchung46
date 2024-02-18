package edu.brown.cs.student.main.csv;

import edu.brown.cs.student.main.exceptions.BadCSVException;
import java.util.ArrayList;
import java.util.List;

/**
 * Class designed to handle searching through previously parsed CSV data.
 *
 * <p>Iterates through data and prints all rows to the terminal that match the search criteria if
 * found. Otherwise prints "No match found."
 */
public class Searcher {
  Parser<List<String>> parser;

  /**
   * Simple constructor that takes in a parser returning List<String> as its Object Type.
   *
   * @param parser converts each row into a List<String>
   */
  public Searcher(Parser<List<String>> parser) {
    this.parser = parser;
  }

  /**
   * One of three search functions that takes in a value and a column title to search within.
   *
   * @param value what the user is searching for
   * @param column the name of the column the user is searching within
   * @throws BadCSVException if any errors occur while parsing, primarily if the given column
   *     doesn't exist
   */
  public List<List<String>> search(String value, String column) throws BadCSVException {
    int idx = parser.getIdx(column);

    // once the index is found, calls search with the index
    return search(value, idx);
  }

  /**
   * Second of three search functions that takes a value to search for and a column index to search
   * within.
   *
   * <p>When the value is found, the row it was found in is printed to the terminal in entirety.
   * "Found" refers to any substring match, non-case-sensitive.
   *
   * @param value what the user is searching for
   * @param idx the index of the column the user wants to search within
   */
  public List<List<String>> search(String value, int idx) throws BadCSVException {
    List<List<String>> retval = new ArrayList<>();

    if (idx >= this.parser.getObjs().size()) {
      throw new BadCSVException("Invalid column.");
    }

    // iterates through the parsed data, converts both strings to compare to lowercase,
    // and checks if value is a substr of the data value.
    for (List<String> row : this.parser.getObjs()) {
      String toMatch = row.get(idx).toLowerCase();

      if (toMatch.contains(value.toLowerCase())) {
        retval.add(row);
      }
    }

    return retval;
  }

  /**
   * Last of three search funtions that takes in a value with no column identifier.
   *
   * <p>Operates very similarly to the search with a column index but performs search on all values
   * of a row.
   *
   * @param value what the user is searching for
   */
  public List<List<String>> search(String value) {
    List<List<String>> retval = new ArrayList<>();

    // iterates through rows, then each word in the row and checks for a match based on rules above.
    for (List<String> row : this.parser.getObjs()) {
      for (String word : row) {
        String toMatch = word.toLowerCase();
        if (toMatch.contains(value.toLowerCase())) {
          retval.add(row);

          // avoids printing the same row twice if a match is found twice in one row.
          break;
        }
      }
    }

    return retval;
  }
}
