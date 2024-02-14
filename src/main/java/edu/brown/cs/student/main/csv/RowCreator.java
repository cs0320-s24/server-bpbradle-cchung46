package edu.brown.cs.student.main.csv;

import java.util.List;

public class RowCreator implements CreatorFromRow<List<String>> {
  public List<String> create(List<String> row) {
    return row;
  }
}
