package edu.brown.cs.student.main.server.state;

import edu.brown.cs.student.main.csv.Parser;
import edu.brown.cs.student.main.csv.RowCreator;
import edu.brown.cs.student.main.csv.Searcher;
import edu.brown.cs.student.main.exceptions.BadCSVException;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Class that handles all CSV-related functions such as load(), search() and view().
 * Called on by corresponding Handlers. Utilizes Sprint 1's Parser and Searcher classes.
 */

public class CSVRepository {
  private Parser<List<String>> parser;
  private Searcher searcher;

  public CSVRepository() {}

  /**
   * Constructs and initializes a Parser and a Searcher for that Parser. Upon construction, the Parser
   * parses and loads in all data.
   *
   * @param filepath passed from LoadCSVHandler, the filepath given in the API query
   * @param header passed from LoadCSVHandler, indicates if header or not (y/n)
   * @throws BadCSVException if any errors occur while parsing
   * @throws FileNotFoundException if the reader is unable to open
   */
  public void loadCSV(String filepath, String header)
      throws BadCSVException, FileNotFoundException {
    FileReader reader = new FileReader(filepath);
    RowCreator creator = new RowCreator();

    boolean hasHeader = false;
    if (header.equals("y")) {
      hasHeader = true;
    }

    this.parser = new Parser<>(reader, creator, hasHeader);
    this.searcher = new Searcher(parser);
  }

  /**
   *  All three search functions operate in the same way by calling Searcher's search – there are three
   *  functions to allow for searching in a column given by name, a column given by index, or no column at all.
   *
   * @param val the value to search for
   * @param col the column to search in, given either by name, index, or not given at all
   * @return all rows in the CSV that contain the given value in the given column (or all columns)
   * @throws BadCSVException if the given column doesn't exist
   */
  public List<List<String>> searchCSV(String val, String col) throws BadCSVException {
    return this.searcher.search(val, col);
  }

  public List<List<String>> searchCSV(String val, int col) throws BadCSVException {
    return this.searcher.search(val, col);
  }

  public List<List<String>> searchCSV(String val) {
    return this.searcher.search(val);
  }

  /**
   * The View function returns the CSV contents as given. Concatenates Parser's headers and contents.
   *
   * @return all rows in the Parser's member variables
   */
  public List<List<String>> viewCSV() {
    List<List<String>> retval = new ArrayList<>();

    retval.add(this.parser.getHeaders());

    for (List<String> obj : this.parser.getObjs()) {
      retval.add(obj);
    }

    return retval;
  }
}
