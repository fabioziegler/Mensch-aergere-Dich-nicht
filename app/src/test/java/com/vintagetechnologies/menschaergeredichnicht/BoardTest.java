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
import static org.junit.Assert.assertNotEquals;

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
        HashMap<PlayerColor, Integer> endCount = new HashMap<>();
        endCount.put(PlayerColor.BLUE, 0);
        endCount.put(PlayerColor.RED, 0);
        endCount.put(PlayerColor.GREEN, 0);
        endCount.put(PlayerColor.YELLOW, 0);

        HashMap<PlayerColor, Integer> startCount = new HashMap<>();
        startCount.put(PlayerColor.BLUE, 0);
        startCount.put(PlayerColor.RED, 0);
        startCount.put(PlayerColor.GREEN, 0);
        startCount.put(PlayerColor.YELLOW, 0);

        for (Spot s : board.getBoard()) {
            if (s instanceof EndSpot) {
                PlayerColor pc = ((EndSpot) s).getColor();
                endCount.put(pc, endCount.get(pc) + 1);
            } else if (s instanceof StartingSpot) {
                PlayerColor pc = ((StartingSpot) s).getColor();
                startCount.put(pc, startCount.get(pc) + 1);
            }
        }


        for (PlayerColor pc : PlayerColor.values()) {
            assertEquals(4, (int)endCount.get(pc));
            assertEquals(4, (int)startCount.get(pc));
        }
    }


    @Test
    public void testCycle() {
        Board.resetBoard();
        board = Board.get();

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
    public void testCheckSpot() {
        Board.resetBoard();
        board = Board.get();

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
    public void testGetStartingSpot() {

        Board.resetBoard();
        board = Board.get();

        HashMap<PlayerColor, Integer> sSpots = new HashMap<>();
        sSpots.put(PlayerColor.RED, 1);
        sSpots.put(PlayerColor.BLUE, 44);
        sSpots.put(PlayerColor.YELLOW, 48);
        sSpots.put(PlayerColor.GREEN, 52);

        for (PlayerColor pc : sSpots.keySet()) {
            int ind = sSpots.get(pc);

            for (int i = ind; i < ind + 4; i++) {
                board.getBoard(i).nullGamePiece();
            }

            //expected, actual
            assertEquals(board.getBoard(ind), board.getStartingSpot(pc));

            for (int i = ind; i < ind + 4; i++) {
                assertEquals(board.getBoard(i), board.getStartingSpot(pc));
                board.getBoard(i).setGamePiece(new GamePiece(pc));
            }

            assertEquals(null, board.getStartingSpot(pc));

        }
    }

    @Test
    public void testResetBoard() {
        Board.resetBoard();
        assertNotEquals(Board.get(), board);

        for (Spot s : Board.getBoard()) {
            assertEquals(null, s.getGamePiece());
        }
    }

}
