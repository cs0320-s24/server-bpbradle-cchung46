package edu.brown.cs.student.main.server.state;

import edu.brown.cs.student.main.exceptions.BadCSVException;

import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class ServerState {
  private CSVRepository csvrepo;
  private ACSRepositoryInterface acsrepo;

  public ServerState(ACSRepositoryInterface acstype) {
    this.csvrepo = new CSVRepository();
    this.acsrepo = acstype;
  }

  public void load(String filepath) throws BadCSVException, FileNotFoundException {
    csvrepo.loadCSV(filepath);
  }

  public void search(String val, String identifier) throws BadCSVException {
    csvrepo.searchCSV(val, identifier);
  }

  public void view() {}

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
