package com.vintagetechnologies.menschaergeredichnicht.implementation;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
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
import com.vintagetechnologies.menschaergeredichnicht.structure.PlayerColor;
import com.vintagetechnologies.menschaergeredichnicht.structure.Spot;
import com.vintagetechnologies.menschaergeredichnicht.structure.Theme;
import com.vintagetechnologies.menschaergeredichnicht.synchronisation.GameSynchronisation;
import com.vintagetechnologies.menschaergeredichnicht.view.BoardView;

import java.io.IOException;
import java.util.HashMap;
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

    private ActualGame() {
        this.gameLogic = new GameLogic();
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

		if(!getInstance().isInitialized())
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



    /**
     * Inits the ActualGame Object, as it is a Singleton.
     *
     * @param names
     */
    public void init(String... names) {

        new Thread() {
            @Override
            public void run() {
                RealDice.get();
            }
        }.start();

        gameLogic.init(RealDice.get(), this, names);

        gameactivity = (Spieloberflaeche) bv.getContext();

        this.setPlayerNames();

        initialized = true;
    }


    private void setPlayerNames(){
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

        final Spieloberflaeche finalGameactivity = (Spieloberflaeche) bv.getContext();
        final Theme finalTheme = theme;

        final HashMap<PlayerColor, Integer> tfs = new HashMap<>();
        tfs.put(PlayerColor.RED, R.id.textView_spielerRot);
        tfs.put(PlayerColor.GREEN, R.id.textView_spielerGruen);
        tfs.put(PlayerColor.BLUE, R.id.textView_spielerBlau);
        tfs.put(PlayerColor.YELLOW, R.id.textView_spielerGelb);

        finalGameactivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {

                for(PlayerColor pc : tfs.keySet()){
                    ((TextView) finalGameactivity.findViewById(tfs.get(pc))).setVisibility(View.INVISIBLE);
                }

                for (Player p : gameLogic.getPlayers()) {
                    int id = tfs.get(p.getColor());
                    TextView tv = ((TextView) finalGameactivity.findViewById(id));
                    tv.setTextColor(finalTheme.getColor(p.getColor().toString()));
                    tv.setText(p.getName());

                    tv.setVisibility(View.VISIBLE);
                }
            }
        });
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

		GameSettings gameSettings = DataHolder.getInstance().retrieve(Network.DATAHOLDER_GAMESETTINGS, GameSettings.class);

		if(!p.getName().equals(gameSettings.getPlayerName())){
			// enable buttons
			final ImageButton btnWuerfel = (ImageButton) (gameactivity.findViewById(R.id.imageButton_wuerfel));

			gameactivity.runOnUiThread(new Runnable() {
				@Override
				public void run() {
					bv.invalidate();
					btnWuerfel.setEnabled(false);
				}
			});
		}
    }

    @Override
    public void waitForMovePiece() {

		com.vintagetechnologies.menschaergeredichnicht.GameLogic gl = DataHolder.getInstance().retrieve(Network.DATAHOLDER_GAMELOGIC, com.vintagetechnologies.menschaergeredichnicht.GameLogic.class);
		GameSettings gameSettings = DataHolder.getInstance().retrieve(Network.DATAHOLDER_GAMESETTINGS, GameSettings.class);

		// check if we are in multiplayer game and it's a client's turn - then inform the client that he should choose which game piece to move.
		if(!isLocalGame() && gl.isHost() && !getGameLogic().getCurrentPlayer().getName().equals(gameSettings.getPlayerName())){

			GameLogicHost gameLogicHost = (GameLogicHost) gl;

			gameLogicHost.sendMessageToClient(getGameLogic().getCurrentPlayer().getName(), Network.TAG_WAIT_FOR_MOVE);

			_waitForMove();

			return;
		}

        this.bv.setHighlightedGamePiece(this.gameLogic.getPossibleToMove().get(0));


		// enable buttons
        final Button btnMoveFigur = (Button) (gameactivity.findViewById(R.id.Move_Figur));

        gameactivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                bv.invalidate();
                btnMoveFigur.setEnabled(true);
                btnMoveFigur.setVisibility(View.VISIBLE);
            }
        });

        _waitForMove();

		// send result to host?
		if(!isLocalGame() && !gl.isHost()){	// send Player (with Gamepieces) to host
			getGameLogic()._movePiece();
		}

        // disable buttons
        gameactivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                btnMoveFigur.setEnabled(false);
            }
        });
    }


	/**
	 * Let thread wait.
	 */
	private void _waitForMove(){

		com.vintagetechnologies.menschaergeredichnicht.GameLogic gl = DataHolder.getInstance().retrieve(Network.DATAHOLDER_GAMELOGIC, com.vintagetechnologies.menschaergeredichnicht.GameLogic.class);

		if(isLocalGame() || gl.isHost()) {
			synchronized (this) {
				try {
					wait();
				} catch (InterruptedException e) {
					Logger.getLogger(RealDice.class.getName()).log(Level.INFO, "Exception while waiting!", e);
					Thread.currentThread().interrupt();
				}
			}
		} else {

			Thread clientPlayThread = getGameLogic().getClientPlayThread();
			synchronized (clientPlayThread){
				try {
					clientPlayThread.wait();
				} catch (InterruptedException e) {
					Thread.currentThread().interrupt();
				}
			}
		}

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
			com.vintagetechnologies.menschaergeredichnicht.GameLogic gl = (com.vintagetechnologies.menschaergeredichnicht.GameLogic) DataHolder.getInstance().retrieve(Network.DATAHOLDER_GAMELOGIC);
            if(gl.isHost())
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

    @Override
    public void regularGameStarted() {
        Log.i("Game", "Starting regular game.");
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

	public boolean isInitialized(){
		return initialized;
	}
}
