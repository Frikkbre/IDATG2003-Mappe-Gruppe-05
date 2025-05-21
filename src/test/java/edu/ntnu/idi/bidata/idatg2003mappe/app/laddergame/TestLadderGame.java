package edu.ntnu.idi.bidata.idatg2003mappe.app.laddergame;

import static org.junit.jupiter.api.Assertions.*;

import edu.ntnu.idi.bidata.idatg2003mappe.app.laddergame.model.LadderGame;
import edu.ntnu.idi.bidata.idatg2003mappe.entity.player.Player;
import edu.ntnu.idi.bidata.idatg2003mappe.map.Tile;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TestLadderGame {

  private LadderGame ladderGame;

  @BeforeEach
  void setUp() {
    ladderGame = new LadderGame( false);
    ladderGame.playGame();
  }

  @AfterEach
  void tearDown() {
    ladderGame = null;
  }

  @Test
  void testPlayerMovesAfterRoll() { //TODO - redo test to not run through entire game
    Player currentPlayer = ladderGame.getPlayers().get(0);
    Tile initialTile = currentPlayer.getCurrentTile();

    int roll = 6;
    currentPlayer.movePlayer(roll);
    Tile newTile = currentPlayer.getCurrentTile();

    assertNotEquals(initialTile, newTile, "Player should have moved to a new tile after rolling the die.");
  }
}
