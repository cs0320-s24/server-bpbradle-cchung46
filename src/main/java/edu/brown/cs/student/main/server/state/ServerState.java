package edu.brown.cs.student.main.server.state;

import edu.brown.cs.student.main.exceptions.BadCSVException;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URISyntaxException;
import java.util.List;

public class ServerState {
  private CSVRepository csvrepo;
  private ACSRepositoryInterface acsrepo;
  public boolean csvloaded;

  public ServerState(ACSRepositoryInterface acstype) {
    this.csvrepo = new CSVRepository();
    this.acsrepo = acstype;
    csvloaded = false;
  }

  public void load(String filepath) throws BadCSVException, FileNotFoundException {
    csvrepo.loadCSV(filepath);
    csvloaded = true;
  }

  public List<List<String>> search(String val, String col) throws BadCSVException {
    return csvrepo.searchCSV(val, col);
  }

  public List<List<String>> search(String val, int col) throws BadCSVException {
    return csvrepo.searchCSV(val, col);
  }

  public List<List<String>> search(String val) {
    return csvrepo.searchCSV(val);
  }

  public List<List<String>> view() {
    return csvrepo.viewCSV();
  }

  public List<String> fetch(String state, String county)
      throws URISyntaxException, IOException, InterruptedException {
    return acsrepo.fetch(state, county);
  }

  public void logError(Exception e) {
    PrintWriter pw = null;
    try {
      FileWriter fw = new FileWriter("errorlog.txt", true);
      pw = new PrintWriter(fw);

      pw.write("ERROR: " + e.getMessage());
      e.printStackTrace(pw);
    } catch (IOException except) {
      System.err.println("Failed to log error: " + except.getMessage());
    } finally {
      if (pw != null) {
        pw.close();
      }
    }
  }
}
