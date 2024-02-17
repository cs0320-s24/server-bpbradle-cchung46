package edu.brown.cs.student.main.server;

import static spark.Spark.after;

import edu.brown.cs.student.main.server.state.ACSRepository;
import edu.brown.cs.student.main.server.state.ServerState;
import spark.Spark;

public class Server {

  static final int port = 3232;
  private final ServerState state;

  public Server(ServerState state) {

    this.state = state;

    Spark.port(port);

    after(
        (request, response) -> {
          response.header("Access-Control-Allow-Origin", "*");
          response.header("Access-Control-Allow-Methods", "*");
        });

    Spark.get("/loadcsv", new LoadCSVHandler(state));
    Spark.get("/searchcsv", new SearchCSVHandler(state));
    //        Spark.get("/viewcsv", new ViewCSVHandler(state));
    //        Spark.get("/broadband", new FetchACSHandler(state));

    Spark.awaitInitialization();
  }

  public static void main(String[] args) {
    Server server = new Server(new ServerState(new ACSRepository()));
    System.out.println("Server started at http://localhost:" + port);
  }
}
