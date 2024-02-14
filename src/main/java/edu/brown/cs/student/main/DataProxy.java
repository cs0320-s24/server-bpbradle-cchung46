package edu.brown.cs.student.main;

import edu.brown.cs.student.main.csv.Searcher;
import edu.brown.cs.student.main.exceptions.BadCSVException;

public class DataProxy {
    private ServerState state = new ServerState();

    public DataProxy()
    {}

    public void load(String filepath) throws BadCSVException {
        state.getCSVRepo().loadCSV(filepath);
    }

    public void search(String val, String identifier) throws BadCSVException {
        state.getCSVRepo().searchCSV(val, identifier);
    }

    public void view () {

    }
}
