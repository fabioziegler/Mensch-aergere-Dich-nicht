package com.vintagetechnologies.menschaergeredichnicht.Impl;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.vintagetechnologies.menschaergeredichnicht.DataHolder;
import com.vintagetechnologies.menschaergeredichnicht.GameLogicHost;
import com.vintagetechnologies.menschaergeredichnicht.GameSettings;
import com.vintagetechnologies.menschaergeredichnicht.R;
import com.vintagetechnologies.menschaergeredichnicht.Spieloberflaeche;
import com.vintagetechnologies.menschaergeredichnicht.networking.Network;
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

	// if the game is played locally, disabled by default
    private boolean isLocalGame = false;


    /**
     * Returns actualGame Instance
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
     * @param player The new player object received by the host.
     */
    public static void refreshPlayer(Player player) {
        // TODO: 28.04.17 Instead of overriding, manually set only needed attributes? Which are needed?

		if(!((com.vintagetechnologies.menschaergeredichnicht.GameLogic) DataHolder.getInstance().retrieve(Network.DATAHOLDER_GAMELOGIC)).hasGameStarted())
			return;

		String name = player.getName();
		Player[] players = getInstance().getGameLogic().getPlayers();

		for (int i = 0; i < players.length; i++) {
			Player oldPlayer = players[i];
			if(oldPlayer.getName().equals(name)){
				players[i] = player;
				break;
			}
		}

		// refresh board
		getInstance().getBoardView().postInvalidate();
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

        GameSettings gameSettings = (GameSettings) DataHolder.getInstance().retrieve(Network.DATAHOLDER_GAMESETTINGS);

        Theme theme = null;
        try {
            if(gameSettings.getBoardDesign() == GameSettings.BoardDesign.CLASSIC){
                theme = new Theme(gameactivity.getAssets().open("themes/classic.json"));
            }else if(gameSettings.getBoardDesign() == GameSettings.BoardDesign.VINTAGE){
                theme = new Theme(gameactivity.getAssets().open("themes/vintage.json"));
            }

        } catch (IOException e) {
			Log.e("Actual Game init", "Fehler", e);
        }

        final Spieloberflaeche gameactivity = (Spieloberflaeche) bv.getContext();
        final Theme finalTheme = theme;

        gameactivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {

				boolean red = false, green = false, blue = false, yellow = false;

                for (Player p : gameLogic.getPlayers()) {

                    TextView tv = null;
                    switch (p.getColor()) {
                        case RED: {
                            tv = ((TextView) gameactivity.findViewById(R.id.textView_spielerRot));
							red = true;
                            break;
                        }
                        case GREEN: {
                            tv = ((TextView) gameactivity.findViewById(R.id.textView_spielerGruen));
							green = true;
                            break;
                        }
                        case BLUE: {
                            tv = ((TextView) gameactivity.findViewById(R.id.textView_spielerBlau));
							blue = true;
                            break;
                        }
                        case YELLOW: {
                            tv = ((TextView) gameactivity.findViewById(R.id.textView_spielerGelb));
							yellow = true;
                            break;
                        }
                    }

                    tv.setTextColor(finalTheme.getColor(p.getColor().toString()));
                    tv.setText(p.getName());
                }

                // hide not used fields
                if(!red)
					((TextView) gameactivity.findViewById(R.id.textView_spielerRot)).setVisibility(View.INVISIBLE);

                if(!green)
					((TextView) gameactivity.findViewById(R.id.textView_spielerGruen)).setVisibility(View.INVISIBLE);

                if(!blue)
					((TextView) gameactivity.findViewById(R.id.textView_spielerBlau)).setVisibility(View.INVISIBLE);

                if(!yellow)
					((TextView) gameactivity.findViewById(R.id.textView_spielerGelb)).setVisibility(View.INVISIBLE);

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


        final Button btnMoveFigur = (Button) (gameactivity.findViewById(R.id.Move_Figur));

        gameactivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                bv.invalidate();
                btnMoveFigur.setEnabled(true);
                btnMoveFigur.setVisibility(View.VISIBLE);
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
                btnMoveFigur.setEnabled(false);
                btnMoveFigur.setVisibility(View.INVISIBLE);
            }
        });
    }

    public void setLocalGame(boolean isLocalGame) {
        this.isLocalGame = isLocalGame;
    }

    public boolean isLocalGame() {
        return isLocalGame;
    }

    @Override
    public void refreshView() {
        bv.postInvalidate();

        if (!isLocalGame){	// needed here?
			com.vintagetechnologies.menschaergeredichnicht.GameLogic gameLogic = (com.vintagetechnologies.menschaergeredichnicht.GameLogic) DataHolder.getInstance().retrieve(Network.DATAHOLDER_GAMELOGIC);
            if(gameLogic.isHost())
            	GameSynchronisation.synchronize();
        }
    }

    private void printInfo(final String info) {
        gameactivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                gameactivity.setStatus(info);
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

    public Spieloberflaeche getGameactivity() {
        return gameactivity;
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

	/**
	 * Reset game instance.
	 */
	public static void reset(){
		actualGameInstance = new ActualGame();
	}
}
