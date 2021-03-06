package com.vintagetechnologies.menschaergeredichnicht;


import android.animation.Animator;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Point;
import android.media.MediaPlayer;
import android.os.SystemClock;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.vintagetechnologies.menschaergeredichnicht.implementation.RealDice;
import com.vintagetechnologies.menschaergeredichnicht.implementation.ActualGame;
import com.vintagetechnologies.menschaergeredichnicht.networking.Network;
import com.vintagetechnologies.menschaergeredichnicht.structure.Board;

import java.util.Random;

import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.widget.TextView;
import android.widget.Toast;

import com.vintagetechnologies.menschaergeredichnicht.structure.Cheat;
import com.vintagetechnologies.menschaergeredichnicht.structure.DiceNumber;
import com.vintagetechnologies.menschaergeredichnicht.structure.Player;
import com.vintagetechnologies.menschaergeredichnicht.synchronisation.GameSynchronisation;
import com.vintagetechnologies.menschaergeredichnicht.view.BoardView;
import com.vintagetechnologies.menschaergeredichnicht.view.BoardViewOnClickListener;

import static com.vintagetechnologies.menschaergeredichnicht.networking.Network.DATAHOLDER_GAMELOGIC;
import static com.vintagetechnologies.menschaergeredichnicht.networking.Network.DATAHOLDER_GAMESETTINGS;
import static com.vintagetechnologies.menschaergeredichnicht.networking.Network.MESSAGE_DELIMITER;
import static com.vintagetechnologies.menschaergeredichnicht.networking.Network.TAG_REVEAL;
import static com.vintagetechnologies.menschaergeredichnicht.networking.Network.TAG_STATUS_MESSAGE;


public class Spieloberflaeche extends AppCompatActivity implements SensorEventListener {

    private GameLogic gameLogic;
    private GameSettings gameSettings;

    private SensorManager sensorManager;
    private Sensor shakeSensor;
    private Sensor lightSensor;

    private static final int SHAKE_THRESHOLD = 1400;
    private long lastUpdate;
    private float lastX;
    private float lastY;
    private float lastZ;
    private boolean shook = false;

	private boolean sensorOn = true;

    private TextView state;
    private Cheat schummeln;

    private Button btnMoveFigur;

    private ImageButton btnAufdecken;
    private ImageButton btnWuerfel;
    private ImageView imgViewDice;

    private int[] diceImages;

    private BoardView bv;


    // für Zufallszahlen
    private Random rand;

    // screen dimensions
    private int screenWidth;
    private int screenHeight;

    // duration of the animation in ms
    private static final int ANIMATION_DURATION = 500;
    private static final int DICE_VISIBLE_DURATION = 800;

    private MediaPlayer moveSound;
    private MediaPlayer diceSound;


	/**
	 * Manipulates the dice (get a six) if user is cheating.
	 */
    private void manipulateDiceIfCheating() {

        // Test if Cheated - Six or random
        if (schummeln.isPlayerCheating()) {
            RealDice.get().setDiceNumber(DiceNumber.SIX);
            schummeln.setPlayerCheating(false);
        } else {
            RealDice.get().roll();
        }
    }


	/**
	 * Shows an animated dice on the screen.
	 */
	private void animateDice() {
        // dice rolls for 2 seconds, and changes 5x a second it's number

        // "roll" animation
        for (int i = 0; i < 1; i++) {       // 2 seconds
            for (int j = 0; j < 6; j++) {   // 1 second (5 changes)
                final int randomIndex = rand.nextInt(6);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        imgViewDice.setImageResource(diceImages[randomIndex]);
                    }
                });

                SystemClock.sleep(50);
            }
        }
    }


    // Würfel mit Animation ausblenden:

    private void endDiceAnimation(int r) {
        final int result = r;

        int[] locationOfBtnWuerfeln = new int[2];
        btnWuerfel.getLocationOnScreen(locationOfBtnWuerfeln);
        int toX = locationOfBtnWuerfeln[0]; // x
        int toY = locationOfBtnWuerfeln[1]; // y

        final float scaleX = (float) btnWuerfel.getWidth() / (float) imgViewDice.getWidth();
        final float scaleY = (float) btnWuerfel.getHeight() / (float) imgViewDice.getHeight();

        imgViewDice.animate()
                .x(toX - imgViewDice.getWidth() * scaleX)
                .y(toY - imgViewDice.getHeight() * scaleY)
                .setDuration(ANIMATION_DURATION)
                .scaleX(scaleX * 0.0f)
                .scaleY(scaleY * 0.0f)
                .alpha(0.8f)
                .setListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationEnd(Animator animation) {

                        // reset image size and location & enable dice button
                        imgViewDice.setScaleX(1f);
                        imgViewDice.setScaleY(1f);

                        imgViewDice.setX(screenWidth / 2f - (imgViewDice.getWidth() / 2));
                        imgViewDice.setY(screenHeight / 2f - (imgViewDice.getHeight() / 2));

                        imgViewDice.setAlpha(1f);
                        imgViewDice.setVisibility(View.INVISIBLE);

                        btnWuerfel.setImageResource(diceImages[result]);

                        btnWuerfel.setEnabled(true);

                        synchronized (RealDice.get()) {
                            RealDice.get().notify();
                        }
                    }


                    @Override
                    public void onAnimationStart(Animator animation) {
                        //empty
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {
                        //empty
                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {
                        //empty
                    }

                }).start();

    }


    private void btnWuerfelClickedUpdateUIElements() {
        // ui elementes must be updated on main thread:
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                imgViewDice.setX(screenWidth / 2f - (imgViewDice.getWidth() / 2));
                imgViewDice.setY(screenHeight / 2f - (imgViewDice.getHeight() / 2));

                imgViewDice.setVisibility(View.VISIBLE);
                btnWuerfel.setImageResource(R.drawable.dice_undefined);
                imgViewDice.setImageResource(R.drawable.dice_undefined);
            }
        });

    }

    /**
     * wird aufgerufen wenn btnWuerfel betätigt wird
     * UI Updates finden auf dem Main Thread statt
     */
    private void btnWuerfelClicked() {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                setDiceEnabled(false);
            }
        });

        playDice();
        btnWuerfelClickedUpdateUIElements();

		// does user cheat?
        manipulateDiceIfCheating();

        final int indexOfDiceImage = RealDice.get().getDiceNumber().getNumber() - 1;

        animateDice();

        // set final dice image according to what was diced (update UI on special UI Thread)
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                imgViewDice.setImageResource(diceImages[indexOfDiceImage]);
            }
        });

        // zeige Ergebnis für 1 Sekunde
        SystemClock.sleep(DICE_VISIBLE_DURATION);

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                endDiceAnimation(indexOfDiceImage);
            }
        });

        // damit kann durch erneutes schütteln wieder gewürfelt werden.
        shook = false;
    }


    private void boardViewSetTouchListener(final BoardView bv) {
        bv.setOnTouchListener(new BoardViewOnClickListener());
    }

    private void btnMoveFigurSetOnClickListener() {
        //!!ToDO: Fehlerbeheben: nach auswahl kann nur mit schütteln weiter gewürfelt werden..
        btnMoveFigur.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //sound für setzen einer figur
                playMove();
                ActualGame.getInstance().getGameLogic().selectGamePiece(bv.getHighlightedGamePiece());

                if (ActualGame.getInstance().isLocalGame() || gameLogic.isHost()) {    // host or local game:

                } else {    // client in mp game:

                    ((GameLogicClient) gameLogic).sendToHost(bv.getHighlightedGamePiece());

                }

                bv.setHighlightedGamePiece(null);

                RealDice.get();

                RealDice.setDiceButton(btnWuerfel);
                imgViewDice = (ImageView) (findViewById(R.id.imgViewDice));





                synchronized (ActualGame.getInstance()) {
                    ActualGame.getInstance().notify();
                }

            }
        });

    }

    private void networkGameOnCreate() {
        /* Check if local or network game */
        if (!ActualGame.getInstance().isLocalGame()) {    // network game:

            gameLogic = (GameLogic) DataHolder.getInstance().retrieve(DATAHOLDER_GAMELOGIC);
            gameLogic.setActivity(this);

            ActualGame.getInstance().init(gameLogic.getDevices().getPlayerNames());

            if (gameLogic.isHost()) {
                gameLogic.generateUniqueIds();
                GameSynchronisation.synchronize();    // sync for the first time (e.g. after player were setup)
            } else {
                gameLogic.generateUniqueIds();
            }

        } else {    // local game:
            ActualGame.getInstance().init("Hans", "Peter", "Dieter", "Anneliese");
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_spieloberflaeche);

        gameSettings = (GameSettings) DataHolder.getInstance().retrieve(DATAHOLDER_GAMESETTINGS);

        Board.resetBoard();
        RealDice.reset();

        bv = (BoardView) (findViewById(R.id.spielFeld));
        boardViewSetTouchListener(bv);

        ActualGame.getInstance().setBoardView(bv);


        networkGameOnCreate();


        state = (TextView) findViewById(R.id.textView_status);

        schummeln = ActualGame.getInstance().getGameLogic().getCurrentPlayer().getSchummeln();

        // Sensor Manager erstellen
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

        // Licht Sensor erstellen
        lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
        sensorManager.registerListener(this, lightSensor, SensorManager.SENSOR_DELAY_GAME);

        // Sensor für Bewegung
        shakeSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorManager.registerListener(this, shakeSensor, SensorManager.SENSOR_DELAY_GAME);

        // aktuell spielender Spieler wird des Schummelns verdächtigt
        btnAufdecken = (ImageButton) (findViewById(R.id.imageButton_aufdecken));
        btnAufdecken.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnAufdeckenClicked();
            }
        });

        btnWuerfel = (ImageButton) (findViewById(R.id.imageButton_wuerfel));
        btnWuerfel.setEnabled(true);
        setDiceEnabled(true);

        RealDice.get();

        RealDice.setDiceButton(btnWuerfel);
        imgViewDice = (ImageView) (findViewById(R.id.imgViewDice));

        //DiceSound imitating dice roll.
        diceSound = MediaPlayer.create(getApplicationContext(), R.raw.dice2);


        btnWuerfel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new Thread() {
                    @Override
                    public void run() {
                        btnWuerfelClicked();
                    }
                }.start();
            }
        });

        //MoveSound is played when a player moves
        moveSound = MediaPlayer.create(getApplicationContext(), R.raw.click);

        // bestätigt Eingabe
        btnMoveFigur = (Button) (findViewById(R.id.Move_Figur));
        btnMoveFigur.setEnabled(false);
        btnMoveFigur.setVisibility(View.INVISIBLE);

        btnMoveFigurSetOnClickListener();

        // load dice images into array
        loadDiceImages();

        rand = new Random(System.currentTimeMillis());

        // get screen size
        getScreenDimensions();

        // prevent phone from entering sleep mode
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        // start playing
        if (ActualGame.getInstance().isLocalGame() || gameLogic.isHost())
            startPlayThread();
    }


    private void startPlayThread() {
        new Thread() {
            @Override
            public void run() {
                try {
                    ActualGame.getInstance().play();
                } catch (IllegalAccessException e) {
                    Log.e("Spieloberflaeche", "Error in game initialize", e);
                }
            }
        }.start();
    }


    /**
     * Soundeffekt wird aufgerufen wenn eine spielfigur bewegt wird.
     */
    public void playMove() {
        if (gameSettings.isMusicEnabled()) {
            moveSound.start();
        }
    }

    /**
     * Soundeffekt wird aufgerufen wenn der Würfel gewürfelt wird.
     */
    public void playDice() {
        if (gameSettings.isMusicEnabled()) {
            diceSound.start();
        }
    }

    private void loadDiceImages() {
        diceImages = new int[6];

        diceImages[0] = R.drawable.dice1;
        diceImages[1] = R.drawable.dice2;
        diceImages[2] = R.drawable.dice3;
        diceImages[3] = R.drawable.dice4;
        diceImages[4] = R.drawable.dice5;
        diceImages[5] = R.drawable.dice6;
    }


    private void btnAufdeckenClicked() {

		if(ActualGame.getInstance().isLocalGame()){
			// TODO: 15.06.17 implement
			return;
		}

		if(gameLogic.isHost()) {

			GameSettings gameSet = DataHolder.getInstance().retrieve(Network.DATAHOLDER_GAMESETTINGS, GameSettings.class);

			Player possibleCheater = ActualGame.getInstance().getGameLogic().getCurrentPlayer();
			Player revealer = ActualGame.getInstance().getGameLogic().getPlayerByName(gameSet.getPlayerName());

			reveal(possibleCheater, revealer);

		} else {	// client:
			GameLogicClient gameLogicClient = (GameLogicClient) gameLogic;
			gameLogicClient.sendToHost(TAG_REVEAL + MESSAGE_DELIMITER);
		}
    }


	/**
	 * Reveals if a user cheated.
	 * @param possibleCheater The player accused of cheating.
	 * @param revealer The player who suspects cheating of the current player.
	 */
	public void reveal(Player possibleCheater, Player revealer){

		// because all data about players is synchronized here,
		// we can just access the "cheated" property for any player:
		boolean hasCurrentPlayerCheated = possibleCheater.getSchummeln().hasCheated();

		String message;
		String nameOfCurrentPlayer = possibleCheater.getName();
		String nameOfRevealer = revealer.getName();

		if(hasCurrentPlayerCheated) {
            message = "Schummeln enttarnt! " + nameOfCurrentPlayer + " setzt nächste Runde aus.";
            possibleCheater.setHasToSkip(true);
        }
		else {
            message = nameOfRevealer + " hat falsch verdächtigt, und setzt nächste Runde aus!";
            revealer.setHasToSkip(true);
        }

		GameSynchronisation.synchronize();
		GameSynchronisation.sendToast(message);
	}


    /**
     * Determines the screen height and width
     */
    private void getScreenDimensions() {
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        screenWidth = size.x;
        screenHeight = size.y;
    }


    /**
     * Called when the orientation changes (i.e. from portrait to landscape mode)
     *
     * @param newConfig
     */
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        // refresh screen dimensions when screen orientation changes
        getScreenDimensions();
    }


    /**
     * Called when the user presses the back button.
     */
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("Geh nicht!! :-(")
                .setMessage("Willst du das Spiel wirklich verlassen?")
                .setPositiveButton("Ja", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (ActualGame.getInstance().isLocalGame()) {
                            startActivity(new Intent(Spieloberflaeche.this, Hauptmenue.class));
                            finish();
                        } else {
                            gameLogic.leaveGame();
                        }
                    }

                })
                .setNegativeButton("Nein", null)
                .show();
    }


    /**
     * The Sensor reactions for shaking the Dice and for Cheating
     */
    @Override
    public void onSensorChanged(SensorEvent event) {

        // If Player is Currentplayer sensor is true and you can shake and cheat.
        if (sensorOn) {

            // SchüttelSensor: löst Würfeln aus. Nur einmal dann wird shook auf false gesetzt. (nach dem Würfeln wieder auf true)
            if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {

                System.out.println("Hallo!");
                //Shook wird nach dem würfeln wieder auf false gesetzt. bzw wenn dich der Spieler status ändert geändert.
                if (!shook) {

                    long curTime = System.currentTimeMillis();
                    // only allow one update every 100ms.
                    if ((curTime - lastUpdate) > 100) {
                        long diffTime = curTime - lastUpdate;
                        lastUpdate = curTime;

                        float x = event.values[0];
                        float y = event.values[1];
                        float z = event.values[2];

                        float speed = Math.abs(x + y + z - lastX - lastY - lastZ) / diffTime * 10000;

                        if (speed > SHAKE_THRESHOLD) {
                            shook = true;

							// imitate click on dice button
                            runOnUiThread(new Runnable() {
								@Override
								public void run() {
                                    new Thread(){
                                        @Override
                                        public void run() {
                                            btnWuerfelClicked();
                                        }
                                    }.start();

								}
							});
                        }

                        lastX = x;
                        lastY = y;
                        lastZ = z;
                    }
                }
            }


            /** Für Licht
             * Reagiert bei änderung wird entsprechender Wert zwischen 0.0 und 40000 angegeben.
             * wenn schummel funktion ab Dunkel sich einschaltet. Annahme Dunkel ab 1000.
             */
            if (event.sensor.getType() == Sensor.TYPE_LIGHT) {
                float lichtwert = event.values[0];

                if (lichtwert <= 10) {
                    schummeln.setPlayerCheating(true);
                    schummeln.setCheated(true); //Für das Aufdecken
                    schummeln.informHost();
                }
            }

        }
    }


    @Override
    public void onStart() {
        super.onStart();

        Toast.makeText(getApplicationContext(), gameSettings.isCheatingEnabled() ? R.string.schummelnEin : R.string.schummelnAus, Toast.LENGTH_LONG).show();
    }


    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        //nicht in verwendung
    }


    public void setStatus(String status) {
        state.setText(status);

        if (ActualGame.getInstance().isLocalGame())
            return;

        if (gameLogic.isHost()) {    // is anyway always host?
            GameLogicHost gameLogicHost = (GameLogicHost) gameLogic;
            gameLogicHost.sendToAllClientDevices(TAG_STATUS_MESSAGE + MESSAGE_DELIMITER + status);
            GameSynchronisation.nextRound();
        }
    }

    /**
     * Getting updated with the change auf the player status to currentPlayer.
     * Sensors are only used when player == currentPlayer
     * @param enabled
     */
    public void setSensorOn(boolean enabled) {
        sensorOn = enabled;
    }

    public void setDiceEnabled(boolean enabled) {
        btnWuerfel.setEnabled(true);
        btnWuerfel.setClickable(enabled);
        btnWuerfel.setAlpha(enabled ? 1f : 0.5f);
        this.setSensorOn(enabled);
    }

    public void setRevealEnabled(boolean enabled) {
        btnAufdecken.setEnabled(enabled);
		btnAufdecken.setVisibility(enabled ? View.VISIBLE : View.INVISIBLE);
    }

    public int getDiceImage(int n){
        return this.diceImages[n];
    }
}

