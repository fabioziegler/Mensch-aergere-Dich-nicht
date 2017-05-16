package com.vintagetechnologies.menschaergeredichnicht;


import android.animation.Animator;

import android.content.DialogInterface;
import android.content.res.Configuration;
import android.graphics.Point;
import android.os.SystemClock;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.vintagetechnologies.menschaergeredichnicht.Impl.RealDice;
import com.vintagetechnologies.menschaergeredichnicht.Impl.ActualGame;
import com.vintagetechnologies.menschaergeredichnicht.structure.Dice;

import java.util.ArrayList;
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
import com.vintagetechnologies.menschaergeredichnicht.structure.Player;
import com.vintagetechnologies.menschaergeredichnicht.view.BoardView;

import static com.vintagetechnologies.menschaergeredichnicht.networking.Network.DATAHOLDER_GAMELOGIC;
import static com.vintagetechnologies.menschaergeredichnicht.networking.Network.DATAHOLDER_GAMESETTINGS;


public class Spieloberflaeche extends AppCompatActivity implements SensorEventListener {


    private GameLogic gameLogic;
    private GameSettings gameSettings;

    private SensorManager SM;
    private Sensor ShakeSensor;
    private static final int SHAKE_THRESHOLD = 1400;
    private long lastUpdate;
    float last_x;
    float last_y;
    float last_z;
    boolean shook = false;

    private Sensor LightSensor;


    private TextView state;
    // toDO: alle Spielfunktionen ect. hinzufügen
    private Cheat Schummeln;

    private Button btnFigurSelect;
    private Button btnMoveFigur;

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


    /**
     * wird aufgerufen wenn btnWuerfel betätigt wird
     * UI Updates finden auf dem Main Thread statt
     */
    private void btnWuerfelClicked() {
        // ui elementes must be updated on main thread:
        runOnUiThread(new Runnable() {
            public void run() {
                // Update UI elements
                imgViewDice.setX(screenWidth / 2f - (imgViewDice.getWidth() / 2));
                imgViewDice.setY(screenHeight / 2f - (imgViewDice.getHeight() / 2));

                imgViewDice.setVisibility(View.VISIBLE);
                btnWuerfel.setEnabled(false);
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


        // setze Egebnis des Würfelns
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


        synchronized (RealDice.get()) {
            System.out.println("notifying: " + RealDice.get());
            RealDice.get().notify();
        }

        //jetzt kann durch erneutes schütteln wieder ein Würfeln ausgelöst werden.
        //TODo: Anpassen an wie oft darf man würfeln. erst wenn neuer zug erlaubt ist für den Spieler auf true setzen.
        shook = false;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spieloberflaeche);

        //gameLogic = (GameLogic) DataHolder.getInstance().retrieve(DATAHOLDER_GAMELOGIC);
        //gameLogic.setActivity(this);

        gameSettings = (GameSettings) DataHolder.getInstance().retrieve(DATAHOLDER_GAMESETTINGS);

        final BoardView bv = (BoardView) (findViewById(R.id.spielFeld));
        bv.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {


                if (ActualGame.getInstance().getGameLogic().getPossibleToMove() != null && event.getAction() == MotionEvent.ACTION_UP) {

                    float xx = event.getX();
                    float yy = event.getY();

                    for(GamePiece gp : ActualGame.getInstance().getGameLogic().getPossibleToMove()){
                        double x =  xx-(2*gp.getSpot().getX()+1)*(bv.getSpotRadius()+bv.getAbstand());
                        double y =  yy-(2*gp.getSpot().getY()+1)*(bv.getSpotRadius()+bv.getAbstand());



                        if(Math.sqrt(x*x+y*y)<100){
                            bv.setHighlightedGamePiece(gp);
                            bv.invalidate();
                        }
                    }

                    System.out.println(event.getAction() + ": (" + event.getX() + " / " + event.getY() + " )");

                }
                return true;
            }
        });


        ActualGame.getInstance().setBoardView(bv);

        ActualGame.getInstance().init("Hans", "Peter", "Dieter", "Anneliese");

        ActualGame.getInstance().setBoardView((BoardView) findViewById(R.id.spielFeld));

        //Game.getInstance().init("Hans", "Peter", "Dieter", "Anneliese");
        //ActualGame.getInstance().init(gameLogic.getDevices().getNameList());


        state = (TextView) findViewById(R.id.textView_status);

        Schummeln = ActualGame.getInstance().getGameLogic().getCurrentPlayer().getSchummeln();

        //Sensor Manager erstellen
        SM = (SensorManager) getSystemService(SENSOR_SERVICE);

        //Licht Sensor erstellen
        LightSensor = SM.getDefaultSensor(Sensor.TYPE_LIGHT);
        SM.registerListener(this, LightSensor, SensorManager.SENSOR_DELAY_GAME);

        //Sensor für Bewegung
        ShakeSensor = SM.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        SM.registerListener(this, ShakeSensor, SensorManager.SENSOR_DELAY_GAME);

        //ToDo: Disable wenn Spieler gerade spielt
        //aktuell spielender Spieler wird des Schummelns verdächtigt
        btnAufdecken = (ImageButton) (findViewById(R.id.imageButton_aufdecken)); // ToDO: Disable für gerade spielenden Spieler
        btnAufdecken.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean schummelt = false;
                Player[] Suspechts = ActualGame.getInstance().getGameLogic().getPlayers();

                /**
                 * Alle Spieler durch laufen ob geschummelt wurde (weil nur der aktuell Spielende noch nicht aufgerufen werden kann)
                 * Da nur der Spieler der an der Reihe ist überhaupt schummeln kann.
                 */
                for (int i = 0; i < Suspechts.length; i++) {
                    if (Suspechts[i].getSchummeln().isPlayerCheating()) {
                        // TODO Spieler i setzt aus
                        schummelt = true;
                    }
                }
                if (!schummelt) {
                    // ToDO getSpieler der gerade spielt.
                    // ToDo Spieler, der falsch verdächtigt hat (den Btn gedrückt hat), setzt aus.
                }

                //ToDO: dem currentPlayer (der button gedrückt hat) Feedback geben. [oder allen?]

            }
        });


        btnWuerfel = (ImageButton) (findViewById(R.id.imageButton_wuerfel)); //ToDo: Disable für Spieler die nicht am Zug sind

        btnWuerfel.setEnabled(true);

        RealDice.get();

        RealDice.setDiceButton(btnWuerfel);
        imgViewDice = (ImageView) (findViewById(R.id.imgViewDice));


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


        //zwischen zu bewegenden Figuren wählen
        btnFigurSelect = (Button) (findViewById(R.id.Select_Figur));
        btnFigurSelect.setEnabled(false);
        btnFigurSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // toDO: Zu setzende Figur auswählen
                ArrayList<GamePiece> possibleGamePieces = ActualGame.getInstance().getGameLogic().getPossibleToMove();
                if (bv.getHighlightedGamePiece() == null) {
                    bv.setHighlightedGamePiece(possibleGamePieces.get(0));
                    bv.invalidate();
                } else {
                    GamePiece gp = bv.getHighlightedGamePiece();
                    int i = possibleGamePieces.indexOf(gp);
                    i = (i + 1) % possibleGamePieces.size();
                    bv.setHighlightedGamePiece(possibleGamePieces.get(i));
                    bv.invalidate();
                }
            }
        });


        //bestätigt Eingabe
        btnMoveFigur = (Button) (findViewById(R.id.Move_Figur));
        btnMoveFigur.setEnabled(false);
        btnMoveFigur.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // toDO: Ausgewählte Figur um gewürfelte Augenzahl weitersetzen

                ActualGame.getInstance().getGameLogic().selectGamePiece(bv.getHighlightedGamePiece());
                bv.setHighlightedGamePiece(null);
                btnWuerfel.setEnabled(true);

                RealDice.get();

                RealDice.setDiceButton(btnWuerfel);
                imgViewDice = (ImageView) (findViewById(R.id.imgViewDice));

                synchronized (ActualGame.getInstance()) {
                    ActualGame.getInstance().notify();
                }
            }
        });

        // initialize dice
        dice = new Dice();

        // load image list
        diceImages = new int[6];

        diceImages[0] = R.drawable.dice1;
        diceImages[1] = R.drawable.dice2;
        diceImages[2] = R.drawable.dice3;
        diceImages[3] = R.drawable.dice4;
        diceImages[4] = R.drawable.dice5;
        diceImages[5] = R.drawable.dice6;

        rand = new Random(System.currentTimeMillis());

        // get screen size
        getScreenDimensions();


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


    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("Geh nicht!! :-( ")
                .setMessage("Willst du das Spiel wirklich verlassen?")
                .setPositiveButton("Ja", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }

                })
                .setNegativeButton("Nein", null)
                .show();
    }

    /**
     * ToDO: Sollte nur aktiviert sein wenn Spieler aktuell spielt.
     * ToDO: Schummelfunktion sollte bei jedem Spielerwechsel auf false gesetzt werden.
     * Da auf änderung reagiert, dürfte nicht wenn bevor man am zug ist verdunkelt wird nicht reagiert werden.
     */
    @Override
    public void onSensorChanged(SensorEvent event) {
        /**
         * SchüttelSensor: löst Würfeln aus. Nur einmal dann wird shook auf false gesetzt. (nach dem Würfeln wieder auf true)
         */

        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            //TODo ist das mit shook sinnvoll?! Gibt es bessere lösung
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
            if (Lichtwert <= 10) {
                //state.setText("Schummeln: " + true);  //Test
                Schummeln.setPlayerCheating(true);
            }
            //Kein else da nach spieler wechsel allgemein auf false zurückgesetz wird
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
    }

}
