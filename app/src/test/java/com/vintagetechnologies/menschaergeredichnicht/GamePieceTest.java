package com.vintagetechnologies.menschaergeredichnicht;

import com.vintagetechnologies.menschaergeredichnicht.structure.Board;
import com.vintagetechnologies.menschaergeredichnicht.structure.EndSpot;
import com.vintagetechnologies.menschaergeredichnicht.structure.GamePiece;
import com.vintagetechnologies.menschaergeredichnicht.structure.Player;
import com.vintagetechnologies.menschaergeredichnicht.structure.PlayerColor;
import com.vintagetechnologies.menschaergeredichnicht.structure.RegularSpot;
import com.vintagetechnologies.menschaergeredichnicht.structure.Spot;
import com.vintagetechnologies.menschaergeredichnicht.structure.GamePiece;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

/**
 * Created by Simon on 06.05.2017.
 */



public class GamePieceTest {

    Board board;

    @Before
    public void before() {
        board = Board.get();

    }

    @Test
    public void testMove(){

        PlayerColor pc = PlayerColor.RED;
        GamePiece gp = new GamePiece(pc);
        Spot occupiedSpot = new RegularSpot(4,4, null);
        occupiedSpot.setGamePiece(gp);

        Spot targetSpot = new RegularSpot(4,4, null);


        //noch frei
        assertEquals(true, gp.moveTo(targetSpot));

        //schon belegt
        assertEquals(false, gp.moveTo(targetSpot));

    }

}
