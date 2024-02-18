package edu.brown.cs.student.main.server.state;

import edu.brown.cs.student.main.csv.Parser;
import edu.brown.cs.student.main.csv.RowCreator;
import edu.brown.cs.student.main.csv.Searcher;
import edu.brown.cs.student.main.exceptions.BadCSVException;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

public class CSVRepository {
  private Parser<List<String>> parser;
  private Searcher searcher;

  public CSVRepository() {}

  public void loadCSV(String filepath) throws BadCSVException, FileNotFoundException {
    FileReader reader = new FileReader(filepath);
    RowCreator creator = new RowCreator();

    this.parser = new Parser<>(reader, creator, true);
    this.searcher = new Searcher(parser);
  }

  public List<List<String>> searchCSV(String val, String col) throws BadCSVException {
    return this.searcher.search(val, col);
  }

  public List<List<String>> searchCSV(String val, int col) throws BadCSVException {
    return this.searcher.search(val, col);
  }

  public List<List<String>> searchCSV(String val) {
    return this.searcher.search(val);
  }

  public List<List<String>> viewCSV() {
    List<List<String>> retval = new ArrayList<>();

    retval.add(this.parser.getHeaders());

    for (List<String> obj : this.parser.getObjs()) {
      retval.add(obj);
    }

    return retval;
  }
}
