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
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.vintagetechnologies.menschaergeredichnicht.impl.RealDice;
import com.vintagetechnologies.menschaergeredichnicht.impl.ActualGame;
import com.vintagetechnologies.menschaergeredichnicht.networking.Network;
import com.vintagetechnologies.menschaergeredichnicht.structure.Board;
import com.vintagetechnologies.menschaergeredichnicht.structure.Dice;

import java.util.Random;

import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.widget.TextView;
import android.widget.Toast;

import com.vintagetechnologies.menschaergeredichnicht.structure.Cheat;
import com.vintagetechnologies.menschaergeredichnicht.structure.DiceNumber;
import com.vintagetechnologies.menschaergeredichnicht.structure.GamePiece;
import com.vintagetechnologies.menschaergeredichnicht.synchronisation.GameSynchronisation;
import com.vintagetechnologies.menschaergeredichnicht.view.BoardView;

import static com.vintagetechnologies.menschaergeredichnicht.networking.Network.DATAHOLDER_GAMELOGIC;
import static com.vintagetechnologies.menschaergeredichnicht.networking.Network.DATAHOLDER_GAMESETTINGS;
import static com.vintagetechnologies.menschaergeredichnicht.networking.Network.MESSAGE_DELIMITER;
import static com.vintagetechnologies.menschaergeredichnicht.networking.Network.TAG_REVEAL;
import static com.vintagetechnologies.menschaergeredichnicht.networking.Network.TAG_STATUS_MESSAGE;


public class Spieloberflaeche extends AppCompatActivity implements SensorEventListener {

    private GameLogic gameLogic;
    private GameSettings gameSettings;

    private SensorManager SM;
    private Sensor ShakeSensor;
    private Sensor LightSensor;

    private static final int SHAKE_THRESHOLD = 1400;
    private long lastUpdate;
    private float last_x;
	private float last_y;
	private float last_z;
	private boolean shook = false;

    private TextView state;
    private Cheat Schummeln;

    private Button btnFigurSelect;
    public Button btnMoveFigur;

    private ImageButton btnAufdecken;
    private ImageButton btnWuerfel;
    private ImageView imgViewDice;

    // Dice
    private Dice dice;

    private int[] diceImages;


    // für Zufallszahlen
    private Random rand;

    // screen dimensions
    private int screenWidth;
    private int screenHeight;

    // duration of the animation in ms
    private final int ANIMATION_DURATION = 500;

    private MediaPlayer moveSound;
    private MediaPlayer diceSound;


    /**
     * wird aufgerufen wenn btnWuerfel betätigt wird
     * UI Updates finden auf dem Main Thread statt
     */
    private void btnWuerfelClicked() {

        playDice();
        // ui elementes must be updated on main thread:
        runOnUiThread(new Runnable() {
            public void run() {
                // Update UI elements
                imgViewDice.setX(screenWidth / 2f - (imgViewDice.getWidth() / 2));
                imgViewDice.setY(screenHeight / 2f - (imgViewDice.getHeight() / 2));

                imgViewDice.setVisibility(View.VISIBLE);
                btnWuerfel.setImageResource(R.drawable.dice_undefined);
                imgViewDice.setImageResource(R.drawable.dice_undefined);
            }
        });


        //Test if Cheated - Six or random
        if (Schummeln.isPlayerCheating()) {
            RealDice.get().setDiceNumber(DiceNumber.SIX);
            Schummeln.setPlayerCheating(false);
        } else {
            RealDice.get().roll();
        }

        //Sets the Result
        final int result = RealDice.get().getDiceNumber().getNumber() - 1;


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


        // setze Ergebnis des Würfelns
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                // Update UI elements
                imgViewDice.setImageResource(diceImages[result]);
            }
        });


        // zeige Ergebnis für 1 Sekunde
        SystemClock.sleep(800);

        // Würfel mit Animation ausblenden:
        runOnUiThread(new Runnable() {
            public void run() {

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
                            }

                            @Override
                            public void onAnimationStart(Animator animation) {
                            }

                            @Override
                            public void onAnimationCancel(Animator animation) {
                            }

                            @Override
                            public void onAnimationRepeat(Animator animation) {
                            }
                        })
                        .start();
            }
        });


		if(ActualGame.getInstance().isLocalGame() || gameLogic.isHost()) {
			synchronized (RealDice.get()) {
				System.out.println("notifying: " + RealDice.get());
				RealDice.get().notify();
			}
		} else {	// client:
			((GameLogicClient)gameLogic).sendToHost(RealDice.get().getDiceNumber());	// send diceNumber to host
		}

        //jetzt kann durch erneutes schütteln wieder ein Würfeln ausgelöst werden.
        shook = false;
    }


	/**
	 * Called when a client
	 * @param diceNumber
	 */
	public void remoteDiceClicked(DiceNumber diceNumber){
		RealDice.get().setDiceNumber(diceNumber);
	}


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_spieloberflaeche);

        gameSettings = (GameSettings) DataHolder.getInstance().retrieve(DATAHOLDER_GAMESETTINGS);

        Board.resetBoard();
        RealDice.reset();

		final BoardView bv = (BoardView) (findViewById(R.id.spielFeld));
		bv.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {

				if (ActualGame.getInstance().getGameLogic().getPossibleToMove() != null && event.getAction() == MotionEvent.ACTION_UP) {

					float xx = event.getX();
					float yy = event.getY();

					for (GamePiece gp : ActualGame.getInstance().getGameLogic().getPossibleToMove()) {

						double x = xx - (2 * gp.getSpot().getX() + 1) * (bv.getSpotRadius() + bv.getAbstand());
						double y = yy - (2 * gp.getSpot().getY() + 1) * (bv.getSpotRadius() + bv.getAbstand());

						if (Math.sqrt(x * x + y * y) < 100) {
							bv.setHighlightedGamePiece(gp);
							bv.invalidate();
						}
					}

					Log.i("BoardView", event.getAction() + ": (" + event.getX() + " / " + event.getY() + " )");

				}
				return true;
			}
		});

        ActualGame.getInstance().setBoardView(bv);


		/* Check if local or network game */
		if(!ActualGame.getInstance().isLocalGame()){	// network game:

			gameLogic = (GameLogic) DataHolder.getInstance().retrieve(DATAHOLDER_GAMELOGIC);
			gameLogic.setActivity(this);

			ActualGame.getInstance().init(gameLogic.getDevices().getPlayerNames());

			if(gameLogic.isHost()) {
				gameLogic.generateUniqueIds();
				GameSynchronisation.synchronize();    // sync for the first time (e.g. after player were setup)
			} else {
				gameLogic.generateUniqueIds();
			}

		} else {	// local game:
			ActualGame.getInstance().init("Hans", "Peter", "Dieter", "Anneliese");
		}

		//ActualGame.getInstance().setBoardView((BoardView) findViewById(R.id.spielFeld));


        state = (TextView) findViewById(R.id.textView_status);

        Schummeln = ActualGame.getInstance().getGameLogic().getCurrentPlayer().getSchummeln();

        // Sensor Manager erstellen
        SM = (SensorManager) getSystemService(SENSOR_SERVICE);

        // Licht Sensor erstellen
        LightSensor = SM.getDefaultSensor(Sensor.TYPE_LIGHT);
        SM.registerListener(this, LightSensor, SensorManager.SENSOR_DELAY_GAME);

        // Sensor für Bewegung
        ShakeSensor = SM.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        SM.registerListener(this, ShakeSensor, SensorManager.SENSOR_DELAY_GAME);

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

        RealDice.get();


        RealDice.setDiceButton(btnWuerfel);
        imgViewDice = (ImageView) (findViewById(R.id.imgViewDice));

        //DiceSound imitating dice roll.
        //diceSound = MediaPlayer.create(getApplicationContext(), R.drawable.dice2);

        btnWuerfel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Runnable myRunnable = new Runnable() {
                    @Override
                    public void run() {
                        btnWuerfelClicked();

                    }
                };
                new Thread(myRunnable).start();
            }
        });


        // zwischen zu bewegenden Figuren wählen
		/*
        btnFigurSelect = (Button) (findViewById(R.id.Select_Figur));
        btnFigurSelect.setEnabled(false);
        btnFigurSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

				gameLogic = (GameLogic) DataHolder.getInstance().retrieve(DATAHOLDER_GAMELOGIC);

                // Zu setzende Figur auswählen
                ArrayList<GamePiece> possibleGamePieces = ActualGame.getInstance().getGameLogic().getPossibleToMove();

                if (bv.getHighlightedGamePiece() == null) {
                    bv.setHighlightedGamePiece(possibleGamePieces.get(0));
                    bv.invalidate();

					if(!ActualGame.getInstance().isLocalGame() && !gameLogic.isHost()){	// am I client in mp game?
						((GameLogicClient)gameLogic).sendToHost(possibleGamePieces.get(0));
					}

                } else {

                    GamePiece gp = bv.getHighlightedGamePiece();
                    int i = possibleGamePieces.indexOf(gp);
                    i = (i + 1) % possibleGamePieces.size();
                    bv.setHighlightedGamePiece(possibleGamePieces.get(i));
                    bv.invalidate();

					if(!ActualGame.getInstance().isLocalGame() && !gameLogic.isHost()){	// am I client in mp game?
						((GameLogicClient)gameLogic).sendToHost(possibleGamePieces.get(i));
					}
                }
            }
        });
		*/

//        moveSound = MediaPlayer.create(getApplicationContext(), R.drawable.dice2);

               // bestätigt Eingabe
        btnMoveFigur = (Button) (findViewById(R.id.Move_Figur));
        btnMoveFigur.setEnabled(false);
        btnMoveFigur.setVisibility(View.INVISIBLE);

        //!!ToDO: Fehlerbeheben: nach auswahl kann nur mit schütteln weiter gewürfelt werden..
        btnMoveFigur.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // toDO: Ausgewählte Figur um gewürfelte Augenzahl weitersetzen

                //sound für setzen einer figur
                playMove();
                ActualGame.getInstance().getGameLogic().selectGamePiece(bv.getHighlightedGamePiece());
                bv.setHighlightedGamePiece(null);
                //btnWuerfel.setEnabled(true);	// TODO: remove? (multiplayer)

                RealDice.get();

                RealDice.setDiceButton(btnWuerfel);
                imgViewDice = (ImageView) (findViewById(R.id.imgViewDice));

				if(ActualGame.getInstance().isLocalGame() || gameLogic.isHost()) {	// host or local game:
					synchronized (ActualGame.getInstance()) {
						ActualGame.getInstance().notify();
					}
				} else {	// client in mp game:

					((GameLogicClient) gameLogic).sendToHost(Network.TAG_MOVE_PIECE + Network.MESSAGE_DELIMITER + RealDice.get().getDiceNumber().getNumber());

					/*
					Thread clientPlayThread = ActualGame.getInstance().getGameLogic().getClientPlayThread();

					// move piece manually instead using the regularGame() routine, which is only called on the host device

					synchronized (clientPlayThread) {
						// notify own client thread
						clientPlayThread.notify();
					}

					ActualGame.getInstance().getGameLogic()._movePiece();

					Player me = ActualGame.getInstance().getGameLogic().getPlayerByName(gameSettings.getPlayerName());
					((GameLogicClient)gameLogic).sendToHost(me);
					((GameLogicClient)gameLogic).sendToHost(RealDice.get().getDiceNumber());

					ActualGame.getInstance().refreshView();
					*/
				}

            }
        });

        // initialize dice
        dice = new Dice();

        // load dice images into array
        loadDiceImages();

        rand = new Random(System.currentTimeMillis());

        // get screen size
        getScreenDimensions();

		// prevent phone from entering sleep mode
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

		// start playing
		if(ActualGame.getInstance().isLocalGame() || gameLogic.isHost())
			startPlayThread();
    }


    private void startPlayThread(){
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


	//ToDo: soundeffekte ausschalten wenn musik disabled
    /**
     * Soundeffekt wird aufgerufen wenn eine spielfigur bewegt wird.
     */
    public void playMove(){
        if(gameSettings.isMusicEnabled()) {
            //moveSound.start();
        }
    }
    /**
     * Soundeffekt wird aufgerufen wenn der Würfel gewürfelt wird.
     */
    public void playDice(){
        if(gameSettings.isMusicEnabled()) {
            //diceSound.start();
        }
    }

    private void loadDiceImages(){
		diceImages = new int[6];

		diceImages[0] = R.drawable.dice1;
		diceImages[1] = R.drawable.dice2;
		diceImages[2] = R.drawable.dice3;
		diceImages[3] = R.drawable.dice4;
		diceImages[4] = R.drawable.dice5;
		diceImages[5] = R.drawable.dice6;
	}


    private void btnAufdeckenClicked() {

        if (gameLogic.isHost()) {

			// TODO: implement

        } else {

            GameLogicClient gameLogicClient = (GameLogicClient) gameLogic;
            gameLogicClient.sendToHost(TAG_REVEAL);
        }
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
						if(ActualGame.getInstance().isLocalGame()){
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



    boolean sensorOn = true;
    /**
     * The Sensor reactions for shaking the Dice and for Cheating
     */
    @Override
    public void onSensorChanged(SensorEvent event) {
        //If Player == Currentplayer sensor is true and you can shake and cheat.
        if (sensorOn) {

            /**
             * SchüttelSensor: löst Würfeln aus. Nur einmal dann wird shook auf false gesetzt. (nach dem Würfeln wieder auf true)
             */
            if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
                //Shook wird nach dem würfeln wieder auf false gesetzt. bzw wenn dich der Spieler status ändert geändert.
                if (!shook) {
                    long curTime = System.currentTimeMillis();
                    // only allow one update every 100ms.
                    if ((curTime - lastUpdate) > 100) {
                        long diffTime = (curTime - lastUpdate);
                        lastUpdate = curTime;

                        float x = event.values[0];
                        float y = event.values[1];
                        float z = event.values[2];

                        float speed = Math.abs(x + y + z - last_x - last_y - last_z) / diffTime * 10000;

                        if (speed > SHAKE_THRESHOLD) {
                            shook = true;
                            Runnable myRunnable = new Runnable() {
                                @Override
                                public void run() {
                                    btnWuerfelClicked();
                                }
                            };
                            new Thread(myRunnable).start();
                        }

                        last_x = x;
                        last_y = y;
                        last_z = z;
                    }
                }
            }


            /** Für Licht
             * Reagiert bei änderung wird entsprechender Wert zwischen 0.0 und 40000 angegeben.
             * wenn schummel funktion ab Dunkel sich einschaltet. Annahme Dunkel ab 1000.
             */
            if (event.sensor.getType() == Sensor.TYPE_LIGHT) {
                float Lichtwert = event.values[0];
                if (Lichtwert <= 2) {
                    //state.setText("Schummeln: " + true);  //Test
                    Schummeln.setPlayerCheating(true);
                    Schummeln.informHost(true);
                }

            }

        }
    }


    @Override
    public void onStart() {
        super.onStart();

        Toast.makeText(getApplicationContext(), (gameSettings.isCheatingEnabled() ? R.string.schummelnEin : R.string.schummelnAus), Toast.LENGTH_LONG).show();
    }


    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        //nicht in verwendung
    }


    public void setStatus(String status) {
        state.setText(status);

		if(ActualGame.getInstance().isLocalGame())
			return;

		if(gameLogic.isHost()) {	// is anyway always host?
			GameLogicHost gameLogicHost = (GameLogicHost) gameLogic;
			gameLogicHost.sendToAllClientDevices(TAG_STATUS_MESSAGE + MESSAGE_DELIMITER + status);
			GameSynchronisation.nextRound();
		}
    }

    /**
     * Getting updated with the change auf the player status to currentPlayer.
     * @param enabled
     */
    //Sensors are only used when player == currentPlayer
    public void setSensorOn(boolean enabled) {
        sensorOn = enabled;
    }
    public void setDiceEnabled(boolean enabled){
		btnWuerfel.setEnabled(enabled);
	}
    public void setRevealEnabled(boolean enabled){
        btnAufdecken.setEnabled(enabled);
    }
}
