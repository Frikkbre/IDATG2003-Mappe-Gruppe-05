package edu.ntnu.idi.bidata.idatg2003mappe.app;

import static org.junit.jupiter.api.Assertions.*;

import edu.ntnu.idi.bidata.idatg2003mappe.entity.Player;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.List;

public class TestLadderGame {

  private LadderGame ladderGame;

  @BeforeEach
  public void setUp() {
    ladderGame = new LadderGame(4);
  }

  @AfterEach
  public void tearDown() {
    ladderGame = null;
  }

}
