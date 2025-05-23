# IDATG2003 - Mappe

## Authors
Simen Gudbrandsen - 10017 \
Frikk Brændsrød - 10011

## Projects description
Using many of the same classes the project is to build the classic "Ladder game" and "Forsvunne diamant" in java with a GUI. \
The project is a part of the course IDATG2003 at NTNU Gjøvik Spring 2025.

## Project structure
```
.
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
│   │   │   │                   │   ├── boardgameselector
│   │   │   │                   │   │   └── BoardGameSelector.java
│   │   │   │                   │   ├── common
│   │   │   │                   │   │   ├── observer
│   │   │   │                   │   │   │   └── BoardGameObserver.java
│   │   │   │                   │   │   └── ui
│   │   │   │                   │   │       ├── NavBar.java
│   │   │   │                   │   │       ├── PlayerData.java
│   │   │   │                   │   │       └── UIComponentFactory.java
│   │   │   │                   │   ├── laddergame
│   │   │   │                   │   │   ├── controller
│   │   │   │                   │   │   │   └── LadderGameController.java
│   │   │   │                   │   │   ├── model
│   │   │   │                   │   │   │   └── LadderGame.java
│   │   │   │                   │   │   └── ui
│   │   │   │                   │   │       └── LadderGameGUI.java
│   │   │   │                   │   └── missingdiamond
│   │   │   │                   │       ├── controller
│   │   │   │                   │       │   └── MissingDiamondController.java
│   │   │   │                   │       ├── gamelogic
│   │   │   │                   │       │   ├── MissingDiamondMovement.java
│   │   │   │                   │       │   └── TokenSystem.java
│   │   │   │                   │       ├── model
│   │   │   │                   │       │   └── MissingDiamond.java
│   │   │   │                   │       ├── service
│   │   │   │                   │       │   └── MapConfigService.java
│   │   │   │                   │       └── ui
│   │   │   │                   │           ├── BoardView.java
│   │   │   │                   │           ├── BoardViewUpdates.java
│   │   │   │                   │           ├── GameControlPanel.java
│   │   │   │                   │           ├── MapDesignerManager.java
│   │   │   │                   │           ├── MissingDiamondGUI.java
│   │   │   │                   │           ├── PlayerStatusPanel.java
│   │   │   │                   │           └── TileHighlighter.java
│   │   │   │                   ├── banker
│   │   │   │                   │   ├── Banker.java
│   │   │   │                   │   └── PriceList.java
│   │   │   │                   ├── entity
│   │   │   │                   │   ├── die
│   │   │   │                   │   │   ├── Die.java
│   │   │   │                   │   │   └── DieObserver.java
│   │   │   │                   │   └── player
│   │   │   │                   │       ├── Player.java
│   │   │   │                   │       ├── PlayerFactory.java
│   │   │   │                   │       └── PlayerObserver.java
│   │   │   │                   ├── filehandling
│   │   │   │                   │   ├── FileReader.java
│   │   │   │                   │   ├── FileWriter.java
│   │   │   │                   │   ├── exceptionhandling
│   │   │   │                   │   │   ├── FileHandlingException.java
│   │   │   │                   │   │   └── JsonParsingException.java
│   │   │   │                   │   ├── game
│   │   │   │                   │   │   ├── BoardFileHandler.java
│   │   │   │                   │   │   ├── GameRegistry.java
│   │   │   │                   │   │   ├── GameSaveLoadHandler.java
│   │   │   │                   │   │   └── GameState.java
│   │   │   │                   │   └── map
│   │   │   │                   │       ├── MapConfig.java
│   │   │   │                   │       └── MapConfigFileHandler.java
│   │   │   │                   ├── map
│   │   │   │                   │   ├── Tile.java
│   │   │   │                   │   └── board
│   │   │   │                   │       ├── Board.java
│   │   │   │                   │       ├── BoardBranching.java
│   │   │   │                   │       ├── BoardLinear.java
│   │   │   │                   │       └── LadderGameBoardFactory.java
│   │   │   │                   ├── markers
│   │   │   │                   │   ├── Bandit.java
│   │   │   │                   │   ├── BlankMarker.java
│   │   │   │                   │   ├── Diamond.java
│   │   │   │                   │   ├── GreenGem.java
│   │   │   │                   │   ├── Marker.java
│   │   │   │                   │   ├── RedGem.java
│   │   │   │                   │   ├── Visa.java
│   │   │   │                   │   └── YellowGem.java
│   │   │   │                   ├── movement
│   │   │   │                   │   ├── EffectTile.java
│   │   │   │                   │   ├── LadderAction.java
│   │   │   │                   │   ├── TileAction.java
│   │   │   │                   │   └── TileActionFactory.java
│   │   │   │                   └── util
│   │   │   │                       ├── ConnectionManager.java
│   │   │   │                       ├── CoordinatePoint.java
│   │   │   │                       ├── PointManager.java
│   │   │   │                       └── map
│   │   │   │                           ├── MapDesignerListener.java
│   │   │   │                           ├── MapDesignerTool.java
│   │   │   │                           ├── MapFileHandler.java
│   │   │   │                           └── MapUIManager.java
│   │   │   └── module-info.java
│   │   └── resources
│   │       ├── game-style
│   │       │   └── game-styles.css
│   │       ├── images
│   │       │   └── afrikan_tahti_map.jpg
│   │       ├── maps
│   │       │   └── missing_diamond_default.json
│   │       └── saves
│   │           ├── LastSave.csv
│   │           ├── last_save.json
│   │           └── playerData
│   │               └── Players.csv
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
│                               │       ├── TestMissingDiamond.java
│                               │       └── gamelogic
│                               │           └── TestTokenSystem.java
│                               ├── banker
│                               │   ├── TestBanker.java
│                               │   └── TestPriceList.java
│                               ├── entity
│                               │   ├── TestDie.java
│                               │   ├── TestPlayer.java
│                               │   └── TestPlayerFactory.java
│                               ├── map
│                               │   ├── TestTile.java
│                               │   └── board
│                               │       ├── TestBoard.java
│                               │       ├── TestBoardBranching.java
│                               │       ├── TestBoardLinear.java
│                               │       └── TestLadderGameBoardFactory.java
│                               ├── markers
│                               │   ├── TestDiamond.java
│                               │   └── TestMarker.java
│                               └── movement
│                                   ├── TestEffectTile.java
│                                   ├── TestLadderAction.java
│                                   └── TestTileActionFactory.java
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
