package com.vintagetechnologies.menschaergeredichnicht;

import com.vintagetechnologies.menschaergeredichnicht.implementation.RealDice;
import com.vintagetechnologies.menschaergeredichnicht.structure.Board;
import com.vintagetechnologies.menschaergeredichnicht.structure.GamePiece;
import com.vintagetechnologies.menschaergeredichnicht.structure.Player;
import com.vintagetechnologies.menschaergeredichnicht.structure.PlayerColor;
import com.vintagetechnologies.menschaergeredichnicht.structure.RegularSpot;
import com.vintagetechnologies.menschaergeredichnicht.structure.Spot;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by johannesholzl on 31.03.17.
 */


public class PlayerTest {

    Board board;

    @Before
    public void before() {
        board = Board.get();

        new Thread(){
			@Override
            public void run(){
                RealDice.get();
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

    /**
     * testet ob Spieler in der Startpositon ist.
     */
    @Test
    public void testIsAtStartingPosition() {
        Player p = new Player(PlayerColor.BLUE, "Rainer");
        assertTrue(p.isAtStartingPosition());
        p.getPieces()[0].setSpot(Board.getBoard(20));
        assertFalse(p.isAtStartingPosition());
    }

    /**
     * testet ob getStartingPiece-Methode das richtige zurück gibt.
     */
    @Test
    public void testGetStartingPiece(){
        Board.resetBoard();
        Player p = new Player(PlayerColor.BLUE, "Rainer");
        assertEquals(Board.getBoard(47).getX(), p.getStartingPiece().getSpot().getX());
        assertEquals(Board.getBoard(47).getY(), p.getStartingPiece().getSpot().getY());
    }
}
