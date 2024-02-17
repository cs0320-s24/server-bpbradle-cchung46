package edu.brown.cs.student.main.server.state;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

public interface ACSRepositoryInterface {

  public List<String> fetch(String s1, String s2) throws URISyntaxException, IOException, InterruptedException;
}
