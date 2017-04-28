package com.vintagetechnologies.menschaergeredichnicht;

import com.vintagetechnologies.menschaergeredichnicht.dummies.DummyDice;
import com.vintagetechnologies.menschaergeredichnicht.structure.Board;
import com.vintagetechnologies.menschaergeredichnicht.structure.EndSpot;
import com.vintagetechnologies.menschaergeredichnicht.structure.GamePiece;
import com.vintagetechnologies.menschaergeredichnicht.structure.Player;
import com.vintagetechnologies.menschaergeredichnicht.structure.PlayerColor;
import com.vintagetechnologies.menschaergeredichnicht.structure.RegularSpot;
import com.vintagetechnologies.menschaergeredichnicht.structure.Spot;
import com.vintagetechnologies.menschaergeredichnicht.structure.StartingSpot;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

/**
 * Created by johannesholzl on 31.03.17.
 */


public class PlayerTest {

    Board board;

    @Before
    public void before() {
        board = Board.get();
        new Thread(){
            public void run(){
                DummyDice.get();
            }
        }.start();

    }

    @Test
    public void testCount() {
        PlayerColor pc = PlayerColor.BLUE;
        Player player = new Player(pc, "Alfred");

        assertEquals(pc, player.getColor());
    }

    @Test
    public void  testGamePiece(){
        PlayerColor pc = PlayerColor.BLUE;
        GamePiece gp = new GamePiece(pc);

        assertEquals(pc, gp.getPlayerColor());

        Spot s = new RegularSpot(1,2, null);

        gp.setSpot(s);

        assertEquals(s, gp.getSpot());
        assertEquals(gp, s.getGamePiece());


        Spot t = new RegularSpot(42, 42, null);

        gp.setSpot(t);

        assertEquals(t, gp.getSpot());
        assertEquals(gp, t.getGamePiece());
        assertEquals(null, s.getGamePiece());
    }

    /*
    @Test
    public void dummyDiceTest(){
        for(int i = 0; i<10; i++) {
            DummyDice.waitForRoll();
            System.out.println(DummyDice.get().getDiceNumber());
        }

        assertNotEquals(null, DummyDice.get().getDiceNumber());

    }
       */

}
