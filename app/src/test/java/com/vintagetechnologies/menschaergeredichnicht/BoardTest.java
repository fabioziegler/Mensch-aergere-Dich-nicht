package com.vintagetechnologies.menschaergeredichnicht;

import com.vintagetechnologies.menschaergeredichnicht.structure.Board;
import com.vintagetechnologies.menschaergeredichnicht.structure.DiceNumber;
import com.vintagetechnologies.menschaergeredichnicht.structure.EndSpot;
import com.vintagetechnologies.menschaergeredichnicht.structure.GamePiece;
import com.vintagetechnologies.menschaergeredichnicht.structure.PlayerColor;
import com.vintagetechnologies.menschaergeredichnicht.structure.RegularSpot;
import com.vintagetechnologies.menschaergeredichnicht.structure.Spot;
import com.vintagetechnologies.menschaergeredichnicht.structure.StartingSpot;

import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;

import static org.junit.Assert.assertEquals;

/**
 * Created by johannesholzl on 31.03.17.
 */


public class BoardTest {

    Board board;

    @Before
    public void before() {
        board = Board.get();
    }

    @Test
    public void testCount() {
        int cYE = 0, cGE = 0, cRE = 0, cBE = 0;
        int cYS = 0, cGS = 0, cRS = 0, cBS = 0;

        for (Spot s : board.getBoard()) {
            if (s instanceof EndSpot) {
                switch (((EndSpot) s).getColor()) {
                    case RED: {
                        cRE++;
                        break;
                    }
                    case GREEN: {
                        cGE++;
                        break;
                    }
                    case BLUE: {
                        cBE++;
                        break;
                    }
                    case YELLOW: {
                        cYE++;
                        break;
                    }
                }
            } else if (s instanceof StartingSpot) {
                switch (((StartingSpot) s).getColor()) {
                    case RED:
                        cRS++;
                        break;
                    case GREEN:
                        cGS++;
                        break;
                    case BLUE:
                        cBS++;
                        break;
                    case YELLOW: {
                        cYS++;
                        break;
                    }
                }
            }
        }


        assertEquals(4, cYE);
        assertEquals(4, cRE);
        assertEquals(4, cGE);
        assertEquals(4, cBE);

        assertEquals(4, cYS);
        assertEquals(4, cRS);
        assertEquals(4, cGS);
        assertEquals(4, cBS);


    }


    @Test
    public void testCycle() {
        RegularSpot s = (RegularSpot) board.getBoard()[0];
        RegularSpot n = s.getNextSpot();

        int c = 1;

        while (s != n) {
            n = n.getNextSpot();
            c++;
        }

        assertEquals(s, n);
        assertEquals(40, c);
    }

    @Test
    public void testCheckSpot(){
        GamePiece gp = new GamePiece(PlayerColor.BLUE);
        gp.setSpot(board.getBoard(30));
        Spot s = board.checkSpot(DiceNumber.FIVE, gp);


        assertEquals(board.getBoard(25), s);
    }

    /**
     * Test Startingspots of all colors
     * Incrementally adds new Gamepieces
     */
    @Test
    public void testGetStartingSpot(){

        HashMap<PlayerColor, Integer> sSpots = new HashMap<>();
        sSpots.put(PlayerColor.RED, 1);
        sSpots.put(PlayerColor.BLUE, 44);
        sSpots.put(PlayerColor.YELLOW, 48);
        sSpots.put(PlayerColor.GREEN, 52);

        for(PlayerColor pc : sSpots.keySet()) {
            int ind = sSpots.get(pc);

            for (int i = ind; i < ind+4; i++) {
                board.getBoard(i).nullGamePiece();
            }

            //expected, actual
            assertEquals(board.getBoard(ind), board.getStartingSpot(pc));

            for (int i = ind; i < ind+4; i++) {
                assertEquals(board.getBoard(i), board.getStartingSpot(pc));
                board.getBoard(i).setGamePiece(new GamePiece(pc));
            }

            assertEquals(null, board.getStartingSpot(pc));

        }
    }

}
