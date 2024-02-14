package edu.brown.cs.student.main.csv;

import edu.brown.cs.student.main.exceptions.BadCSVException;
import edu.brown.cs.student.main.exceptions.FactoryFailureException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Class designed to process data from a given CSV through any Reader object.
 *
 * <p>Parses each line based on RegEx given in write-up and turns them into objects based on a given
 * pattern by user of the program.
 *
 * @param <T> generic object type to turn rows into at will of user
 */
public class Parser<T> {

  private Reader reader;
  private CreatorFromRow<T> creator;
  private Boolean header;

  // stores the list of custom Objects of type T
  private List<T> objs;

  // stores the headers of the CSV if they exist.
  private List<String> colHeads;

  /** RegEx copied from sprint write-up. */
  private static final Pattern regexSplitCSVRow =
      Pattern.compile(",(?=([^\\\"]*\\\"[^\\\"]*\\\")*(?![^\\\"]*\\\"))");

  /**
   * Constructs the Parser object by assigning the given parameters as well as creates empty Lists
   * to hold the Objects that get parsed as well as the headers.
   *
   * <p>Calls its own parseData() function immediately and begins parsing.
   *
   * @param reader the given Reader object, allows for the CSV to be input in any form
   * @param creator describes how to turn a row into a given Object type
   * @param header true if the CSV has a header, false otherwise
   * @throws BadCSVException custom exception to handle any errors while parsing
   */
  public Parser(Reader reader, CreatorFromRow<T> creator, Boolean header) throws BadCSVException {
    this.reader = reader;
    this.creator = creator;
    this.header = header;
    this.objs = new ArrayList<>();
    this.colHeads = new ArrayList<>();

    parseData();
  }

  /**
   * Parses the data in the given CSV.
   *
   * <p>Uses a BufferedReader object as suggested in write-up. Gets the column headers if they
   * exist, then turns each row into an Object as specified by the creator member and stores.
   *
   * <p>Utilizes helper function parseLine().
   *
   * @throws BadCSVException if any errors occur while parsing, opening the file, etc.
   */
  public List<T> parseData() throws BadCSVException {
    BufferedReader data = new BufferedReader(this.reader);
    String line;

    // reads and stores the column headers if they exist.
    if (this.header) {
      try {
        line = data.readLine();
        List<String> headers = parseLine(line);

        for (String word : headers) {
          colHeads.add(word);
        }
      } catch (IOException e) {
        throw new BadCSVException("Failed to parse header.", e);
      }
    }

    // iterates through CSV and reads and processes each row using parseLine().
    try {
      while ((line = data.readLine()) != null) {
        List<String> currRow = parseLine(line);
        T obj = creator.create(currRow);
        this.objs.add(obj);
      }
    } catch (FactoryFailureException e) {
      throw new BadCSVException("Failed to create object.", e);
    } catch (IOException e) {
      throw new BadCSVException("Failed to parse.", e);
    }

    return this.objs;
  }

  /**
   * Helper function that utilizes the RegEx to split on the comma and returns a List of each word.
   *
   * <p>Functionality modeled after example given in write-up.
   *
   * @param currLine the row to be processed
   * @return the row as a List with each entry split on the comma
   */
  private List<String> parseLine(String currLine) {
    String[] split = regexSplitCSVRow.split(currLine);
    List<String> retVal = new ArrayList<>();

    for (String field : split) {
      retVal.add(field);
    }

    return retVal;
  }

  /**
   * Finds and returns the index of the column with the given header.
   *
   * <p>Only returns if the header is an exact, non-case-sensitive match, i.e. substrings are not
   * considered a match.
   *
   * @param header the name of the column to be indexed
   * @return the index of the column that matches that header
   * @throws BadCSVException if the given column doesn't exist
   */
  public int getIdx(String header) throws BadCSVException {
    for (int i = 0; i < this.colHeads.size(); i++) {
      String toMatch = this.colHeads.get(i).toLowerCase();
      if (toMatch.equals(header.toLowerCase())) {
        return i;
      }
    }

    throw new BadCSVException("Invalid column.");
  }

  /**
   * Acessor method that returns a list of the Objects.
   *
   * @return the internal list of Objects of type T
   */
  public List<T> getObjs() {
    return this.objs;
  }

  /**
   * Accessor method that returns a list of the CSV column headers.
   *
   * @return the internal list of headers, may be empty if they don't exist
   */
  public List<String> getHeaders() {
    return this.colHeads;
  }
}
