package com.vintagetechnologies.menschaergeredichnicht;

import android.util.Log;

import com.vintagetechnologies.menschaergeredichnicht.implementation.DiceImpl;
import com.vintagetechnologies.menschaergeredichnicht.structure.*;
import com.vintagetechnologies.menschaergeredichnicht.structure.GameLogic;

import org.junit.Before;
import org.junit.Test;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import static junit.framework.Assert.fail;


/**
 * Created by johannesholzl on 06.05.17.
 */


public class GameLogicTest {

    private static final int MAXCOUNT = 1000000;

    private String[] names = {"Alfred", "Bill", "Marilyn", "Tim"};

    private int c = 0;

    private boolean r = false;

    private class TestGame extends Game {

        private GameLogic gameLogic;

        private TestDice td = new TestDice();

        public TestGame() {
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
            if (c == MAXCOUNT) {
                this.getGameLogic().setPlaying(false);
            }

            assertFalse(r);
            r = true;

        }

        @Override
        public void waitForMovePiece() {
            //es wird immer mit dem 0-ten Piece gefahren
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
            this.getGameLogic().setPlaying(true);
        }

        public TestDice getTd() {
            return td;
        }

        public void setTd(TestDice td) {
            this.td = td;
        }
    }

    private class TestDice extends DiceImpl {

        public TestDice() {
            this.diceNumber = DiceNumber.ONE;
        }

        @Override
        public void waitForRoll() {
            roll();
        }
    }

    @Before
    public void beforeTest() {
        Board.get();
    }

    @Test
    public void testInit() {
        Board.resetBoard();

        TestGame tg = new TestGame();
        tg.init(names[0], names[1], names[2], names[3]);

        for (int i = 0; i < tg.getGameLogic().getPlayers().length; i++) {
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
    public void testGetPlayerByName() {
        Board.resetBoard();

        TestGame tg = new TestGame();
        tg.init(names[0], names[1], names[2], names[3]);

        for (int i = 0; i < 4; i++) {
            assertEquals(tg.getGameLogic().getPlayers()[i], tg.getGameLogic().getPlayerByName(names[i]));
        }
    }

    /**
     * testet Richtigkeit beim Einwürfeln
     */

    @Test
    public void testEinwuerfeln() {
        Board.resetBoard();


        TestGame tg = new TestGame() {
            @Override
            public void regularGameStarted() {
                assertEquals(names[3], this.getGameLogic().getCurrentPlayer().getName());
            }
        };
        tg.setTd(new TestDice() {
            @Override
            public void waitForRoll() {
                int dice = (this.diceNumber.getNumber()) % DiceNumber.values().length;
                this.diceNumber = DiceNumber.values()[dice];
            }
        });
        tg.init(names[0], names[1], names[2], names[3]);
        tg.getGameLogic().setPlaying(false);
        try {
            tg.play();
        } catch (IllegalAccessException e) {
            Log.e("Test Einwürfeln", "Fehler", e);
        }

    }

    @Test
    public void testGameLoop() {
        Board.resetBoard();

        final int[] c = {0};
        final int max = 40;

        TestGame tg = new TestGame() {

            @Override
            public void regularGameStarted() {

            }

            @Override
            public void whomsTurn(Player currentplayer) {

                if (c[0] < 4) { // erste Runde, würfeln von 6 nicht möglich (3x, zählender Würfel ist bereits auf 6)
                    this.getTd().setDiceNumber(DiceNumber.SIX);
                    assertTrue(currentplayer.isAtStartingPosition());
                } else {
                    this.getTd().setDiceNumber(DiceNumber.ONE);
                }
                assertEquals(names[c[0] % 4], currentplayer.getName());

                if (c[0] > max) {
                    fail();
                } else {
                    if (c[0] == max) {
                        this.getGameLogic().setPlaying(false);
                    }
                    c[0] += 1;
                }
            }

            @Override
            public void refreshView() {
                if (c[0] > 4) {
                    assertFalse(this.getGameLogic().getCurrentPlayer().isAtStartingPosition());
                }
            }
        };


        tg.setTd(new TestDice() {
            @Override
            public void waitForRoll() {
                int dice = (this.diceNumber.getNumber() + DiceNumber.values().length - 2) % DiceNumber.values().length;
                this.diceNumber = DiceNumber.values()[dice];
            }
        });

        tg.init(names[0], names[1], names[2], names[3]);
        try {
            tg.play();
        } catch (IllegalAccessException e) {
            Log.e("Test GameLoop", "Fehler", e);
        }


    }


    @Test
    public void testHinausfahren() {
        Board.resetBoard();


        TestGame tg = new TestGame() {

            private int c = 0;

            @Override
            public void whomsTurn(Player p) {
                this.getTd().setDiceNumber(DiceNumber.ONE);
                this.getGameLogic().setPlaying(false);
            }

            @Override
            public void refreshView() {
                if (c == 0) {
                    assertEquals(Board.getBoard(12).getGamePiece().getPlayerColor(), PlayerColor.RED); //Roter Spieler ist am Startfeld
                } else if (c == 1) {
                    assertEquals(Board.getBoard(7).getGamePiece().getPlayerColor(), PlayerColor.RED); //Roter Spieler fährt um 5 weiter
                }

                c+=1;
            }
        };

        tg.setTd(new TestDice() {


            @Override
            public void waitForRoll() {
                int dice = (this.diceNumber.getNumber() + DiceNumber.values().length - 2) % DiceNumber.values().length;
                this.diceNumber = DiceNumber.values()[dice];
            }
        });

        tg.getTd().setDiceNumber(DiceNumber.ONE);

        tg.init(names[0], names[1], names[2], names[3]);
        try {
            tg.play();
        } catch (IllegalAccessException e) {
            Log.e("Test GameLoop", "Fehler", e);
        }
    }

}
