package edu.brown.cs.student.main;

public class ServerState {
    private CSVRepository csvrepo;
    private ACSRepository acsrepo;

    public ServerState() {
        this.csvrepo = new CSVRepository();
        this.acsrepo = new ACSRepository();
    }

    public CSVRepository getCSVRepo() {
        return this.csvrepo;
    }

    public ACSRepository getACSrepo() {
        return this.acsrepo;
    }
}
