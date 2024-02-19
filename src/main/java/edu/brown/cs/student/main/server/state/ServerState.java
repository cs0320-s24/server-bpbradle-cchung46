package edu.brown.cs.student.main.server.state;

import edu.brown.cs.student.main.exceptions.BadCSVException;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URISyntaxException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

/**
 * Dependency Injection class created for a shared state between all four Handlers. Passed to Server
 * then passed to Handlers. Allows CSV handlers to access the same CSV data and grants an error logging
 * method to all four Handlers.
 *
 * Contains a CSVRepository object and an ACSRepositoryInterface object that respectively perform the
 * corresponding actions. ServerState is created with an ACSRepository implementation, allowing for
 * flexibility in fetching, caching, etc.
 */

public class ServerState {
  private CSVRepository csvrepo;
  private ACSRepositoryInterface acsrepo;
  public boolean csvloaded;

  /**
   * Constructor that initializes a new CSVRepository to handle all CSV-related functions
   * and assigns the passed-in ACSRepositoryInterface implementation. Sets the csvloaded flag to false.
   *
   * @param acstype the desired ACSRepositoryInterface implementation
   */
  public ServerState(ACSRepositoryInterface acstype) {
    this.csvrepo = new CSVRepository();
    this.acsrepo = acstype;
    csvloaded = false;
  }

  /**
   * Calls CSVRepo's load function which initializes a Parser and Searcher objects and immediately
   * parses all data from the given filepath. Sets csvloaded flag to true. Called by LoadCSVHandler.
   *
   * @param filepath the given filepath from the loadcsv query
   * @param header given header flag from the loadcsv query (y/n)
   * @throws BadCSVException if any errors occur while parsing
   * @throws FileNotFoundException if any errors occur trying to open the file
   */
  public void load(String filepath, String header) throws BadCSVException, FileNotFoundException {
    csvrepo.loadCSV(filepath, header);
    csvloaded = true;
  }

  /**
   * Calls CSVRepo's search which calls Searcher's search function which returns a list of any rows
   * that match the given value in a given column, if specified. All three search functions operate the same
   * but given different parameters (a string column, an indexed column, or no column at all). Called by
   * SearchCSVHandler.
   *
   * @param val the value to search for
   * @param col the column to search in, given by name, index, or not at all
   * @return a list of rows that contain the given value
   * @throws BadCSVException if the column doesn't exist
   */
  public List<List<String>> search(String val, String col) throws BadCSVException {
    return csvrepo.searchCSV(val, col);
  }

  public List<List<String>> search(String val, int col) throws BadCSVException {
    return csvrepo.searchCSV(val, col);
  }

  public List<List<String>> search(String val) {
    return csvrepo.searchCSV(val);
  }

  /**
   * Calls CSVRepo's view which returns the contents of its Parser. Called by ViewCSVHandler.
   *
   * @return the list of all rows in the loaded CSV.
   */
  public List<List<String>> view() {
    return csvrepo.viewCSV();
  }

  /**
   * Calls ACSRepo's fetch which queries the appropriate datasource based on the ACSRepositoryInterface
   * implementation. Returns a list of strings containing the percentage of broadband access in the
   * given location as well as the date and time of this information's retrieval. Called by BroadbandHandler.
   *
   * @param state passed by BroadbandHandler, the state given in the query
   * @param county passed by BroadbandHandler, the coutny given in the query
   * @return a list containing the percentage of access and date and time of retrieval
   * @throws URISyntaxException
   * @throws IOException
   * @throws InterruptedException
   */
  public List<String> fetch(String state, String county)
      throws URISyntaxException, IOException, InterruptedException {
    return acsrepo.fetch(state, county);
  }

  /**
   * An error logging function for graceful handling of errors. Stores the error's message, the
   * date and time it was thrown, and the error's stack trace. Appends to one large file. Called by
   * any of the Handlers if they encounter an error.
   *
   * @param e the exception thrown
   */

  public void logError(Exception e) {
    PrintWriter pw = null;
    try {
      FileWriter fw = new FileWriter("errorlog.txt", true);
      pw = new PrintWriter(fw);

      pw.write("ERROR: " + e.getMessage() + "\n");
      pw.write("DATE: " + LocalDate.now().toString() + "\n");
      pw.write("TIME: " + LocalTime.now().toString() + "\n");
      e.printStackTrace(pw);
    } catch (IOException except) {
      System.err.println("Failed to log error: " + except.getMessage());
    } finally {
      if (pw != null) {
        pw.close();
      }
    }
  }

  /**
   * Returns a reference to the ACSRepo for testing purposes to get access to the ACSCachingRepository's
   * caching stats.
   *
   * @return ACSRepo reference
   */
  public ACSRepositoryInterface getACSrepo() {
    return acsrepo;
  }
}
