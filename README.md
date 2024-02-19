> **GETTING STARTED:** You must start from some combination of the CSV Sprint code that you and your partner ended up with. Please move your code directly into this repository so that the `pom.xml`, `/src` folder, etc, are all at this base directory.

> **IMPORTANT NOTE**: In order to run the server, run `mvn package` in your terminal then `./run` (using Git Bash for Windows users). This will be the same as the first Sprint. Take notice when transferring this run sprint to your Sprint 2 implementation that the path of your Server class matches the path specified in the run script. Currently, it is set to execute Server at `edu/brown/cs/student/main/server/Server`. Running through terminal will save a lot of computer resources (IntelliJ is pretty intensive!) in future sprints.

# Project Details

Server Sprint
Cecily Chung, cchung46, cecily_chung@brown.edu
Benjamin Bradley, bpbradle, benjamin_bradley@brown.edu

Total Est. Time: 22 hours

Repo Link:  https://github.com/cs0320-s24/server-bpbradle-cchung46/tree/main

# Design Choices

Our main design choice for this sprint was based around the ServerState class. The ServerState class is passed into our Server object which is then passed to each of the Handler objects who call on ServerState to perform their various functions (load(), search(), view(), fetch()). The ServerState takes in an ACSRepositoryInterface object and has two data members: a CSVRepository and an ACSRepositoryInterface. There are three implementations of the ACSRepositoryInterface that we implemented: ACSRepository, ACSCachingRepository, and ACSMockRepository. The Handlers will call one of ServerState’s functions which will call one of the repository’s functions. CSVRepository handles all CSV-related functions: load, search, and view, while ACSRepository handles any fetching from the ACS API.

This structure was done for several reasons. The first is the project called for a shared state class to primarily store the CSV data if any is loaded in. Once we developed the ServerState class for the CSV operations, it made sense to expand this to include ACS operations, after which we began to understand the need for an ACSRepositoryInterface. This Interface allows us to create multiple types of ACSRepositories for different purposes: non-caching, caching, testing, or any other fetching/storing methods a developer may want. The ACSCachingRepository takes in parameters which allows for flexible cache builder configurations as well.

choices -- high level design of your program
Explain the relationships between classes/interfaces.
Discuss any specific data structures you used, why you created it, and other high level explanations.
Runtime/ space optimizations you made (if applicable).

# Errors/Bugs

Write reproduction steps for all the bugs in your program. If the mentor TA finds an error and knows how to reproduce it, they will be able to leave better feedback. If the mentor TA finds the bug without proper documentation, they will assume you did not test your program properly.
Explanations for checkstyle errors (hopefully none)

# Tests

Our test suite breaks down in the following way:
1. Integration Tests \
   1.1. Tests for the loadCSV endpoint\
   1.1.1. loading a valid csv\
   1.1.2. loading a nonexistent csv\
   1.1.3. loading a malformed csv\
   1.1.4. loading a csv outside the working directory\
   1.1.5. loading when no csv filepath was given\
   1.1.6. loading a empty csv\
   1.2. Tests for the searchCSV endpoint\
   1.2.1. searching for existent, nonexistent, and values which exist in other columns for (int, str) inputs\
   1.2.2. searching for existent, nonexistent, and values which exist in other columns for (str, str) inputs\
   1.2.3. searching for existent, nonexistent, and values which exist in other columns for (str) inputs\
   1.2.4. searching when a value to search for wasn’t given\
   1.2.5. searching without loading the csv in beforehand\
   1.3. Tests for the viewCSV endpoint\
   1.3.1. viewing without loading file\
   1.3.2. viewing a valid output\
   1.4. Tests for the broadband endpoint\
   1.4.1. fetching for an invalid state\
   1.4.2. fetching for an invalid county\
   1.4.3. fetching for a nonexistent state\
   1.4.4. fetching for a nonexistent county\
   1.4.5. fetching for a valid state and county
2. Unit Tests\
   2.1. whether our populateStateCodes() and populateCountyCodes() methods work as expected\
   2.2. whether our caching is working\
   2.3. whether giving a state that doesn’t exist returns the correct error\
   2.4. whether our ACSAPIUtilities class’ deserializeStates(), deserializeCounties(), and deserializeBroadbandData() methods are all working as expected

# How to

1. Run the program: mvn package → ./run → then use your browser to enter queries in the format http://localhost:3232/{endpoint}? followed by…\
   1.1. loadcsv\
   1.2. searchcsv\
   1.3. viewcsv\
   1.4. broadband
2. Run the tests: Either run the testing files manually through IntelliJ or run automatically when calling mvn package
3. Change caching configurations: When initializing a new ACSCachingRepository, input the desired caching configurations in the parameters
