package edu.brown.cs.student.main.server.state;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

public interface ACSRepositoryInterface {

  public List<String> fetch(String state, String county)
      throws URISyntaxException, IOException, InterruptedException;
}
