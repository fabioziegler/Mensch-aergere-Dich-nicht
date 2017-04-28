package com.vintagetechnologies.menschaergeredichnicht.structure;

import android.app.Activity;
import android.graphics.Color;
import android.widget.TextView;

import com.vintagetechnologies.menschaergeredichnicht.R;
import com.vintagetechnologies.menschaergeredichnicht.Spieloberflaeche;
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

        final Spieloberflaeche gameactivity = (Spieloberflaeche)bv.getContext();
        gameactivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                for(Player p : players){
                    switch (p.getColor()){
                        case RED: {
                            TextView tv = ((TextView)gameactivity.findViewById(R.id.textView_spielerRot));
                            tv.setTextColor(Color.RED);
                            tv.setText(p.getName());
                            break;}
                        case GREEN: {
                            TextView tv = ((TextView)gameactivity.findViewById(R.id.textView_spielerGruen));
                            tv.setTextColor(Color.GREEN);
                            tv.setText(p.getName());
                            break;}
                        case BLUE: {
                            TextView tv = ((TextView)gameactivity.findViewById(R.id.textView_spielerBlau));
                            tv.setTextColor(Color.BLUE);
                            tv.setText(p.getName());
                            break;}
                        case YELLOW: {
                            TextView tv = ((TextView)gameactivity.findViewById(R.id.textView_spielerGelb));
                            tv.setTextColor(Color.YELLOW);
                            tv.setText(p.getName());
                            break;}

                    }
                }


            }
        });

        initialized = true;
    }


    public void play() throws IllegalAccessException {

        if (!initialized) {
            throw new IllegalAccessError("Game hasn't been initialized. Please run init() first.");
        }

        while (true) {



            final Player cp = players[currentPlayer];
            final Spieloberflaeche gameactivity = (Spieloberflaeche)bv.getContext();
            gameactivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    gameactivity.setStatus(cp.getName()+" ist dran!");

                }
            });

            DummyDice.waitForRoll();
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

    public Player getCurrentPlayer() {
        return players[currentPlayer];
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
