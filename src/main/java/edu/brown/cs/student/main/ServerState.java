package edu.brown.cs.student.main;

import edu.brown.cs.student.main.exceptions.BadCSVException;

public class ServerState {
  private CSVRepository csvrepo;
  private ACSRepositoryInterface acsrepo;

  public ServerState(ACSRepositoryInterface acstype) {
    this.csvrepo = new CSVRepository();
    this.acsrepo = acstype;
  }

  public void load(String filepath) throws BadCSVException {
    csvrepo.loadCSV(filepath);
  }

  public void search(String val, String identifier) throws BadCSVException {
    csvrepo.searchCSV(val, identifier);
  }

  public void view() {}
}
