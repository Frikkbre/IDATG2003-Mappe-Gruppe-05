# IDATG2003 - Mappe

## Authors
Simen Gudbrandsen - candidatenumber: \
Frikk Brændsrød - candidatenumber:

## Projects description
Using many of the same classes the project is to build the classic "Ladder game" and "Forsvunne diamant" in java with a GUI. \
The project is a part of the course IDATG2003 at NTNU Gjøvik Spring 2025.

## Project structure
```
├── README.md
├── mvnw
├── mvnw.cmd
├── pom.xml
├── src
│   ├── main
│   │   ├── java
│   │   │   ├── edu
│   │   │   │   └── ntnu
│   │   │   │       └── idi
│   │   │   │           └── bidata
│   │   │   │               └── idatg2003mappe
│   │   │   │                   ├── Main.java
│   │   │   │                   ├── app
│   │   │   │                   │   ├── BoardGameSelector.java
│   │   │   │                   │   ├── BoardGameSelectorController.java
│   │   │   │                   │   ├── BoardGameSelectorGUI.java
│   │   │   │                   │   ├── NavBar.java
│   │   │   │                   │   ├── laddergame
│   │   │   │                   │   │   ├── LadderGame.java
│   │   │   │                   │   │   ├── LadderGameController.java
│   │   │   │                   │   │   └── LadderGameGUI.java
│   │   │   │                   │   └── missingdiamond
│   │   │   │                   │       ├── MissingDiamond.java
│   │   │   │                   │       └── MissingDiamondGUI.java
│   │   │   │                   ├── banker
│   │   │   │                   │   ├── Banker.java
│   │   │   │                   │   └── PriceList.java
│   │   │   │                   ├── entity
│   │   │   │                   │   ├── Action.java
│   │   │   │                   │   ├── Die.java
│   │   │   │                   │   └── Player.java
│   │   │   │                   ├── map
│   │   │   │                   │   ├── Board.java
│   │   │   │                   │   ├── BoardBranching.java
│   │   │   │                   │   ├── BoardLinear.java
│   │   │   │                   │   ├── BoardSquare.java
│   │   │   │                   │   └── Tile.java
│   │   │   │                   ├── markers
│   │   │   │                   │   ├── Bandit.java
│   │   │   │                   │   ├── Diamond.java
│   │   │   │                   │   ├── GreenGem.java
│   │   │   │                   │   ├── Marker.java
│   │   │   │                   │   ├── RedGem.java
│   │   │   │                   │   ├── Visa.java
│   │   │   │                   │   └── YellowGem.java
│   │   │   │                   └── movement
│   │   │   │                       ├── LadderAction.java
│   │   │   │                       └── TileAction.java
│   │   │   └── module-info.java
│   │   └── resources
│   │       └── edu
│   │           └── ntnu
│   │               └── idi
│   │                   └── bidata
│   │                       └── idatg2003mappe
│   │                           └── hello-view.fxml
│   └── test
│       └── java
│           └── edu
│               └── ntnu
│                   └── idi
│                       └── bidata
│                           └── idatg2003mappe
│                               ├── app
│                               │   ├── TestBoardGameSelector.java
│                               │   ├── laddergame
│                               │   │   └── TestLadderGame.java
│                               │   └── missingdiamond
│                               │       └── TestMissingDiamond.java
│                               ├── banker
│                               │   ├── TestBanker.java
│                               │   └── TestPriceList.java
│                               ├── entity
│                               │   ├── TestDie.java
│                               │   └── TestPlayer.java
│                               ├── map
│                               │   ├── TestBoard.java
│                               │   ├── TestBoardLinear.java
│                               │   └── TestTile.java
│                               ├── markers
│                               │   └── TestMarker.java
│                               └── movement
│                                   └── TestLadderAction.java
```

## How to run the project
clone the repository to your preferred IDE with java support.
Make sure to use JDK 21 as its the version used in the project and its LTS(Long Term Support).
compile with Apache Maven x.x.x VERSION!
Run the App.java file in the app package.
Or run with ```mvn clean install compile javafx:run```

## How to run the tests
Navigate to the test package as shown in the [Project structure](#project-structure) section of the readme.
Make sure to have the test dependencies installed and run the tests. This includes maven surefire (VERSION) plugin and JUnit 5.
Or run with ```mvn test``` in the terminal.

## Refrences