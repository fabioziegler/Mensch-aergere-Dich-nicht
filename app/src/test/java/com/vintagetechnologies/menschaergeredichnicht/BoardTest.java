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
        gp.setSpot(board.getBoard(0));
        board.checkSpot(DiceNumber.FIVE, gp);
    }

}
