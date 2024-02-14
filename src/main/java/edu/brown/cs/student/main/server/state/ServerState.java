package edu.brown.cs.student.main.server.state;

import edu.brown.cs.student.main.exceptions.BadCSVException;
import edu.brown.cs.student.main.server.state.ACSRepositoryInterface;
import edu.brown.cs.student.main.server.state.CSVRepository;

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
