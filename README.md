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

##  Game Rules

### Ladder Game
1. Players start at the first tile and agree on who rolls the die first. The player moves the number of steps shown on the die, then the next player takes their turn.
2. The first row is followed to the far right, then players move up to the next row and continue to the left. This pattern repeats until reaching the top.
3. **Snakes & Ladders**:
    - Landing on a **snake head** means following the snake down to its tail.
    - Landing on a **ladder** means climbing up to the top.
4. The first player to reach the top **wins the game**.

### The Missing Diamond (Simplified)
1. The goal is to **find the missing diamond**, which is hidden on the board.
2. Players start with **800 money units** to use in the game.
3. Players roll a die and move **the same number of steps** as shown on the die.
    - **Black tiles** are normal movement spaces.
    - **Red tiles** have a token that can be flipped for a price or with a die roll.
4. **Red Tiles – Choices:**
    - Pay **300 money** to flip the tile.
    - Roll the die (**4 or higher**) to flip it for free.
5. Tiles may contain:
    - **Gemstones** that grant players money (**300, 500, or 1000**).
    - **Bandits** who steal all the money.
    - **Visas**, useless until the diamond is found.
    - **Blank tiles** with no effect.
6. When a player **finds the diamond** and reaches **the starting tile**, they **win the game**.
7. Opponents can still win if they find a **visa** and reach **the starting tile** before the diamond holder.