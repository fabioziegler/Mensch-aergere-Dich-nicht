package com.vintagetechnologies.menschaergeredichnicht.Impl;

import android.graphics.Color;
import android.widget.Button;
import android.widget.TextView;

import com.vintagetechnologies.menschaergeredichnicht.DataHolder;
import com.vintagetechnologies.menschaergeredichnicht.GameSettings;
import com.vintagetechnologies.menschaergeredichnicht.R;
import com.vintagetechnologies.menschaergeredichnicht.Spieloberflaeche;
import com.vintagetechnologies.menschaergeredichnicht.structure.Board;
import com.vintagetechnologies.menschaergeredichnicht.structure.DiceNumber;
import com.vintagetechnologies.menschaergeredichnicht.structure.Game;
import com.vintagetechnologies.menschaergeredichnicht.structure.GameLogic;
import com.vintagetechnologies.menschaergeredichnicht.structure.GamePiece;
import com.vintagetechnologies.menschaergeredichnicht.structure.Player;
import com.vintagetechnologies.menschaergeredichnicht.structure.Spot;
import com.vintagetechnologies.menschaergeredichnicht.structure.Theme;
import com.vintagetechnologies.menschaergeredichnicht.synchronisation.GameSynchronisation;
import com.vintagetechnologies.menschaergeredichnicht.view.BoardView;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

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

    private boolean isNetwork = false;

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

        GameSettings gameSettings = (GameSettings) DataHolder.getInstance().retrieve("GAMESETTINGS");
        Theme theme = null;
        try {
            if(gameSettings.getBoardDesign() == GameSettings.BoardDesign.CLASSIC){
                theme = new Theme(gameactivity.getAssets().open("themes/classic.json"));
            }else if(gameSettings.getBoardDesign() == GameSettings.BoardDesign.VINTAGE){
                theme = new Theme(gameactivity.getAssets().open("themes/vintage.json"));
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        final Spieloberflaeche gameactivity = (Spieloberflaeche) bv.getContext();
        final Theme finalTheme = theme;
        gameactivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                for (Player p : gameLogic.getPlayers()) {
                    TextView tv = null;
                    switch (p.getColor()) {
                        case RED: {
                            tv = ((TextView) gameactivity.findViewById(R.id.textView_spielerRot));
                            break;
                        }
                        case GREEN: {
                            tv = ((TextView) gameactivity.findViewById(R.id.textView_spielerGruen));
                            break;
                        }
                        case BLUE: {
                            tv = ((TextView) gameactivity.findViewById(R.id.textView_spielerBlau));
                            tv.setText(p.getName());
                            break;
                        }
                        case YELLOW: {
                            tv = ((TextView) gameactivity.findViewById(R.id.textView_spielerGelb));
                            break;
                        }
                    }
                    tv.setTextColor(finalTheme.getColor(p.getColor().toString()));
                    tv.setText(p.getName());


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

        this.bv.setHighlightedGamePiece(this.gameLogic.getPossibleToMove().get(0));




        final Button btnFigurSelect = (Button) (gameactivity.findViewById(R.id.Select_Figur));
        final Button btnMoveFigur = (Button) (gameactivity.findViewById(R.id.Move_Figur));

        gameactivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                bv.invalidate();
                btnFigurSelect.setEnabled(true);
                btnMoveFigur.setEnabled(true);
            }
        });
        synchronized (this) {
            try {
                wait();
            } catch (InterruptedException e) {
                Logger.getLogger(RealDice.class.getName()).log(Level.INFO, "Exception while waiting!", e);

                Thread.currentThread().interrupt();
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

    public void enableNetwork() {
        isNetwork = true;
    }


    public void disableNetwork() {
        isNetwork = false;
    }


    @Override
    public void refreshView() {
        if (isNetwork){
            GameSynchronisation.synchronize(this);
        }
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
