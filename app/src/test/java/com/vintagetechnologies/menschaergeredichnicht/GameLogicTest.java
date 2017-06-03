package com.vintagetechnologies.menschaergeredichnicht;

import android.os.Bundle;
import android.util.Log;

import com.vintagetechnologies.menschaergeredichnicht.Impl.DiceImpl;
import com.vintagetechnologies.menschaergeredichnicht.Impl.RealDice;
import com.vintagetechnologies.menschaergeredichnicht.structure.*;
import com.vintagetechnologies.menschaergeredichnicht.structure.GameLogic;
import com.vintagetechnologies.menschaergeredichnicht.view.BoardView;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;


import java.util.logging.Level;
import java.util.logging.Logger;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;


/**
 * Created by johannesholzl on 06.05.17.
 */


public class GameLogicTest {

    private static final int MAXCOUNT = 1000000;

    private TestDice td = new TestDice();

    private  String names[] = {"Alfred", "Bill", "Marilyn", "Tim"};

    private int c = 0;

    private boolean r = false;


    private class TestGame extends Game{

        private GameLogic gameLogic;

        private TestDice td = new TestDice();

        public TestGame(){
            this.gameLogic = new GameLogic();
        }

        @Override
        public void init(String... names) {
            this.gameLogic.init(this.td, this, names);
        }

        @Override
        public void play() throws IllegalAccessException {
            this.gameLogic.play();
        }

        @Override
        public void beginningAction(Player p) {
            assertTrue(p.getName().equals(names[c]));
            c++;
        }

        @Override
        public void whomsTurn(Player p) {
            c++;
            if(c == MAXCOUNT){
                this.getGameLogic().setPlaying(false);
            }

            assertFalse(r);
            r = true;

        }

        @Override
        public void waitForMovePiece() {
            this.gameLogic.selectGamePiece(this.gameLogic.getPossibleToMove().get(0));
        }

        @Override
        public void refreshView() {
            r = false;
        }

        @Override
        public GameLogic getGameLogic() {
            return this.gameLogic;
        }

        @Override
        public void regularGameStarted() {

        }

        public TestDice getTd() {
            return td;
        }

        public void setTd(TestDice td) {
            this.td = td;
        }
    }

    private class TestDice extends DiceImpl {

        public TestDice(){
            this.diceNumber = DiceNumber.ONE;
        }

        @Override
        public void waitForRoll() {
            roll();
        }
    }

    @Before
    public void beforeTest(){
        Board.get();
    }

    @Test
    public void testInit(){
        Board.resetBoard();

        TestGame tg = new TestGame();
        tg.init(names[0],names[1],names[2],names[3]);

        for(int i = 0; i < tg.getGameLogic().getPlayers().length; i++){
            Player p = tg.getGameLogic().getPlayers()[i];
            assertEquals(names[i], p.getName());
        }
    }

    @Test(expected = IllegalAccessException.class)
    public void testFailInit() throws IllegalAccessException {
        TestGame tg = new TestGame();
        tg.play();
    }

    @Test
    public void testPlay(){
		/*
        Board.resetBoard();

        TestGame tg = new TestGame();
        tg.init(names[0],names[1],names[2],names[3]);

        r = false;

        try {
            tg.play();
        } catch (IllegalAccessException e) {
            Logger.getLogger(RealDice.class.getName()).log(Level.INFO, "Exception while playing!", e);
            Thread.currentThread().interrupt();
        }

        assertEquals(MAXCOUNT, c);
		*/
    }


    @Test
    public void testGetPlayerByName(){
        Board.resetBoard();

        TestGame tg = new TestGame();
        tg.init(names[0],names[1],names[2],names[3]);

        for(int i = 0; i<4; i++){
            assertEquals(tg.getGameLogic().getPlayers()[i], tg.getGameLogic().getPlayerByName(names[i]));
        }
    }

    /**
     * testet Richtigkeit beim Einwürfeln
     */

    @Test
    public void testEinwuerfeln(){
        Board.resetBoard();



        TestGame tg = new TestGame(){
            @Override
            public void whomsTurn(Player p){
                assertEquals(names[3], p.getName());
                this.getGameLogic().setPlaying(false);
            }
        };
        tg.setTd(new TestDice(){
            @Override
            public void waitForRoll() {
                int dice  = (this.diceNumber.getNumber() )% DiceNumber.values().length;
                this.diceNumber =  DiceNumber.values()[dice];
            }
        });
        tg.init(names[0],names[1],names[2],names[3]);
        try {
            tg.play();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

    }


}
