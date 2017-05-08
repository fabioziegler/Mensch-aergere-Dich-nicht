package com.vintagetechnologies.menschaergeredichnicht.Impl;

import android.graphics.Color;
import android.widget.Button;
import android.widget.TextView;

import com.vintagetechnologies.menschaergeredichnicht.R;
import com.vintagetechnologies.menschaergeredichnicht.Spieloberflaeche;
import com.vintagetechnologies.menschaergeredichnicht.structure.Board;
import com.vintagetechnologies.menschaergeredichnicht.structure.DiceNumber;
import com.vintagetechnologies.menschaergeredichnicht.structure.Game;
import com.vintagetechnologies.menschaergeredichnicht.structure.GameLogic;
import com.vintagetechnologies.menschaergeredichnicht.structure.GamePiece;
import com.vintagetechnologies.menschaergeredichnicht.structure.Player;
import com.vintagetechnologies.menschaergeredichnicht.structure.Spot;
import com.vintagetechnologies.menschaergeredichnicht.view.BoardView;

/**
 * Created by johannesholzl on 05.04.17.
 */

public class ActualGame extends Game {

    private static ActualGame actualGameInstance;

    //! CurrentPlayer gibt nur den Spieler dem spieler sein eigenen Player aus. Nicht den Spieler aktuell am Zug.
    private BoardView bv;

    private GameLogic gameLogic;

    //init methode already called?
    private boolean initialized = false;

    private Spieloberflaeche gameactivity;

    /**
     * Returns actualGameInstance()
     *
     * @return
     */
    public static ActualGame getInstance() {
        if (actualGameInstance == null) {
            actualGameInstance = new ActualGame();
        }
        return actualGameInstance;
    }

    /**
     * Called when a client received a new up to date game object from the host.
     *
     * @param game The new game object received by the host.
     */
    public static void refreshGameInstance(ActualGame game) {
        // TODO: 28.04.17 Instead of overriding, manually set only needed attributes? Which are needed?
        actualGameInstance = game;
    }

    private ActualGame() {
        this.gameLogic = new GameLogic();
    }


    /**
     * Inits the ActualGame Object, as it is a Singleton.
     *
     * @param names
     */
    public void init(String... names) {

        new Thread() {
            public void run() {
                RealDice.get();
            }
        }.start();

        gameLogic.init(RealDice.get(), this, names);

        gameactivity = (Spieloberflaeche) bv.getContext();


        final Spieloberflaeche gameactivity = (Spieloberflaeche) bv.getContext();
        gameactivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                for (Player p : gameLogic.getPlayers()) {
                    switch (p.getColor()) {
                        case RED: {
                            TextView tv = ((TextView) gameactivity.findViewById(R.id.textView_spielerRot));
                            tv.setTextColor(Color.RED);
                            tv.setText(p.getName());
                            break;
                        }
                        case GREEN: {
                            TextView tv = ((TextView) gameactivity.findViewById(R.id.textView_spielerGruen));
                            tv.setTextColor(Color.GREEN);
                            tv.setText(p.getName());
                            break;
                        }
                        case BLUE: {
                            TextView tv = ((TextView) gameactivity.findViewById(R.id.textView_spielerBlau));
                            tv.setTextColor(Color.BLUE);
                            tv.setText(p.getName());
                            break;
                        }
                        case YELLOW: {
                            TextView tv = ((TextView) gameactivity.findViewById(R.id.textView_spielerGelb));
                            tv.setTextColor(Color.YELLOW);
                            tv.setText(p.getName());
                            break;
                        }
                    }
                }

            }
        });

        initialized = true;
    }


    /**
     * play method
     * As the game could be played forever, it features an endless loop, which should be left
     * when there is only one Player who hasn't won.
     * <p>
     * It enables the Dice and waits for the player to roll it.
     *
     * @throws IllegalAccessException
     */
    public void play() throws IllegalAccessException {

        // throw exception when trying to play and init wasn't called yet.
        if (!initialized) {
            throw new IllegalAccessError("ActualGame hasn't been initialized. Please run init() first.");
        }

        gameLogic.play();

    }

    @Override
    public void beginningAction(Player p) {
        printInfo("Bitte würfeln: " + p.getName());
    }

    @Override
    public void whomsTurn(Player p) {
        printInfo(p.getName() + " ist dran!");
    }

    @Override
    public void waitForMovePiece() {
        final Button btnFigurSelect = (Button) (gameactivity.findViewById(R.id.Select_Figur));
        final Button btnMoveFigur = (Button) (gameactivity.findViewById(R.id.Move_Figur));

        gameactivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                btnFigurSelect.setEnabled(true);
                btnMoveFigur.setEnabled(true);
            }
        });
        synchronized (this) {
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        gameactivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                btnFigurSelect.setEnabled(false);
                btnMoveFigur.setEnabled(false);
            }
        });


    }

    @Override
    public void refreshView() {
        bv.postInvalidate();
    }

    private void printInfo(String info) {

        final String finalInfo = info;
        gameactivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                gameactivity.setStatus(finalInfo);

            }
        });
    }


    /**
     * Moves a GamePiece by the number the Dices face shows.
     * Returns true when the GamePiece was able to move.
     *
     * @param gp
     * @return
     */
    private boolean movePiece(GamePiece gp) {

        Spot s = Board.checkSpot(RealDice.get().getDiceNumber(), gp);

        if (s != null) {
            gp.moveTo(s);
            return true;
        }

        return false;
    }

    /**
     * Moves the GamePiece by one step.
     * Returns true when the GamePiece was able to move.
     *
     * @param gp
     * @return
     */
    private boolean movePieceToEntrance(GamePiece gp) {

        Spot s = Board.checkSpot(DiceNumber.ONE, gp);

        if (s != null) {
            gp.moveTo(s);
            return true;
        }

        return false;
    }

    /**
     *
     */
    private void figureSelected() {

    }


    /**
     * @return
     */
    public BoardView getBoardView() {
        return bv;
    }

    /**
     * @param bv
     */
    public void setBoardView(BoardView bv) {
        this.bv = bv;
    }


    public GameLogic getGameLogic() {
        return gameLogic;
    }

    public void setGameLogic(GameLogic gameLogic) {
        this.gameLogic = gameLogic;
    }
}
