package com.vintagetechnologies.menschaergeredichnicht.structure;

import com.vintagetechnologies.menschaergeredichnicht.dummies.DummyDice;
import com.vintagetechnologies.menschaergeredichnicht.view.BoardView;

import java.util.ArrayList;

/**
 * Created by johannesholzl on 05.04.17.
 */

public class Game {

    private static Game gameInstance;

    private int currentPlayer;
    private Player players[];
    private Board board;
    private Dice dice = new Dice();
    private BoardView bv;


    private boolean initialized = false;

    public static Game getInstance() {
        if (gameInstance == null) {
            gameInstance = new Game();
        }
        return gameInstance;
    }

    private Game() {
    }

    public void init(String... names) {

        new Thread() {
            public void run() {
                DummyDice.get();
            }
        }.start();

        players = new Player[names.length];
        board = Board.get();

        for (int i = 0; i < names.length; i++) {
            PlayerColor cColor = PlayerColor.values()[i];
            players[i] = new Player(cColor, names[i]);
        }
        initialized = true;
    }


    public void play() throws IllegalAccessException {

        if (!initialized) {
            throw new IllegalAccessError("Game hasn't been initialized. Please run init() first.");
        }

        while (true) {

            DummyDice.waitForRoll();

            Player cp = players[currentPlayer];

            GamePiece gp;
            if (DummyDice.get().getDiceNumber() == DiceNumber.SIX && (gp = cp.getStartingPiece()) != null) {
                

                StartingSpot s = (StartingSpot) (gp.getSpot());

                Spot entrance = s.getEntrance();


                if(entrance.getGamePiece() == null || (entrance.getGamePiece() != null && entrance.getGamePiece().getPlayerColor() != gp.getPlayerColor())) {
                    gp.moveTo(s.getEntrance());
                }


                bv.postInvalidate();

                DummyDice.waitForRoll(); // Spieler darf nochmal würfeln

                movePiece(gp);

            }

            else if (!cp.isAtStartingPosition()) { //muss noch "herauswürfeln"


                //GamePiece gp = cp.getPieces()[0]; //TODO: select piece

                for (GamePiece piece : cp.getPieces()) {
                    if (movePiece(piece)) {
                        break;
                    }
                }
            }


            currentPlayer = (currentPlayer + 1) % players.length;

            bv.postInvalidate();

        }


    }

    private boolean movePiece(GamePiece gp) {

        Spot s = Board.checkSpot(DummyDice.get().getDiceNumber(), gp);

        if (s != null) {
            gp.moveTo(s);
            return true;
        }

        return false;
    }


    private void waitfordice() throws IllegalAccessException {

        if (!initialized) {
            throw new IllegalAccessError("Game hasn't been initialized. Please run init() first.");
        }


        try {
            this.wait();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


    }

    private void figureSelected() {

    }

    public Player[] getPlayers() {
        return players;
    }

    public void setPlayers(Player[] players) {
        this.players = players;
    }

    public BoardView getBoardView() {
        return bv;
    }

    public void setBoardView(BoardView bv) {
        this.bv = bv;
    }
}
