package edu.brown.cs.student.main.server.state;

import edu.brown.cs.student.main.csv.Parser;
import edu.brown.cs.student.main.csv.RowCreator;
import edu.brown.cs.student.main.csv.Searcher;
import edu.brown.cs.student.main.exceptions.BadCSVException;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.List;

public class CSVRepository {
  private Parser<List<String>> parser;
  private Searcher searcher;
  private boolean csvloaded;

  public CSVRepository() {
    this.csvloaded = false;
  }

  public void loadCSV(String filepath) throws BadCSVException, FileNotFoundException {
      FileReader reader = new FileReader(filepath);
      RowCreator creator = new RowCreator();

      this.parser = new Parser<>(reader, creator, true);
      this.searcher = new Searcher(parser);
      this.csvloaded = true;
  }

  public void searchCSV(String value, String identifier) throws BadCSVException {
    this.searcher.search(value, identifier);
  }

  public void viewCSV() {}

  public boolean loaded() {
    return this.csvloaded;
  }
}
