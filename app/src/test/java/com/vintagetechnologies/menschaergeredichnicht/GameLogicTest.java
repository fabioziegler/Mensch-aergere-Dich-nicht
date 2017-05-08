package com.vintagetechnologies.menschaergeredichnicht;

import android.os.Bundle;

import com.vintagetechnologies.menschaergeredichnicht.Impl.DiceImpl;
import com.vintagetechnologies.menschaergeredichnicht.Impl.RealDice;
import com.vintagetechnologies.menschaergeredichnicht.structure.*;
import com.vintagetechnologies.menschaergeredichnicht.structure.GameLogic;
import com.vintagetechnologies.menschaergeredichnicht.view.BoardView;

import org.junit.Before;
import org.junit.Test;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

/**
 * Created by johannesholzl on 06.05.17.
 */

public class GameLogicTest {


    private TestDice td = new TestDice();

    private  String names[] = {"Alfred", "Bill", "Marilyn", "Tim"};

    private int c = 0;
    private boolean r = false;

    private class TestGame extends Game{

        private GameLogic gameLogic;


        public TestGame(){
            this.gameLogic = new GameLogic();
        }

        @Override
        public void init(String... names) {
            this.gameLogic.init(td, this, names);
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

            if(c == 100000){
                this.getGameLogic().setPlaying(false);
            }

            assertFalse(r);
            r = true;
            c++;
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
    }


    private class TestDice extends DiceImpl {

        public TestDice(){

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






        TestGame tg = new TestGame();
        tg.init(names[0],names[1],names[2],names[3]);



        for(int i = 0; i < tg.getGameLogic().getPlayers().length; i++){
            Player p = tg.getGameLogic().getPlayers()[i];
            assertEquals(names[i], p.getName());
        }
    }

    @Test
    public void testPlay(){

        TestGame tg = new TestGame();
        tg.init(names[0],names[1],names[2],names[3]);

        r = false;

        try {
            tg.play();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        
    }



}
