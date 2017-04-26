package com.vintagetechnologies.menschaergeredichnicht;


import android.animation.Animator;
import android.content.res.Configuration;
import android.graphics.Point;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.vintagetechnologies.menschaergeredichnicht.dummies.DummyDice;
import com.vintagetechnologies.menschaergeredichnicht.structure.Dice;

import java.util.Random;

import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.widget.TextView;

import com.vintagetechnologies.menschaergeredichnicht.structure.Cheat;
import com.vintagetechnologies.menschaergeredichnicht.structure.Game;
import com.vintagetechnologies.menschaergeredichnicht.view.BoardView;


public class Spieloberflaeche extends AppCompatActivity implements SensorEventListener {

    Sensor LightSensor;
    SensorManager SM;

    TextView state;
    // toDO: alle Spielfunktionen ect. hinzufügen
    Cheat Schummeln = null;
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
    private final int ANIMATION_DURATION = 1000;


    /**
     * wird aufgerufen wenn btnWuerfel betätigt wird
     * UI Updates finden auf dem Main Thread statt
     */
    private void btnWuerfelClicked(){
        // ui elementes must be updated on main thread:
        runOnUiThread(new Runnable() {
            public void run() {
                // Update UI elements
                imgViewDice.setX(screenWidth/2f - (imgViewDice.getWidth()/2));
                imgViewDice.setY(screenHeight/2f - (imgViewDice.getHeight()/2));

                imgViewDice.setVisibility(View.VISIBLE);
                btnWuerfel.setEnabled(false);
                btnWuerfel.setImageResource(R.drawable.dice_undefined);
                imgViewDice.setImageResource(R.drawable.dice_undefined);
            }
        });

        //dice.roll();
        DummyDice.get().roll();

        int result;
        if (Schummeln.isPlayerCheating()) {
            result = 5;
        }else if (!Schummeln.isPlayerCheating()){
            result = DummyDice.get().getDiceNumber().getNumber() - 1;
        }else {
            throw new  IllegalStateException();
        }

        // dice rolls for 3 seconds, and changes 5x a second it's number

        // "roll" animation
        for (int i = 0; i < 3; i++) {       // 3 seconds
            for (int j = 0; j < 5; j++) {   // 1 second (5 changes)
                final int randomIndex = rand.nextInt(6);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() { imgViewDice.setImageResource(diceImages[randomIndex]); }
                });

                SystemClock.sleep(200);
            }
        }


        // setze Egebnis des Würfelns
        final int finalResult = result;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                // Update UI elements
                imgViewDice.setImageResource(diceImages[finalResult]);
            }
        });


        // zeige Ergebnis für 1 Sekunde
        SystemClock.sleep(1000);

        // Würfel mit Animation ausblenden:
        runOnUiThread(new Runnable() {
            public void run() {

                int[] locationOfBtnWuerfeln = new int[2];
                btnWuerfel.getLocationOnScreen(locationOfBtnWuerfeln);
                int toX = locationOfBtnWuerfeln[0]; // x
                int toY = locationOfBtnWuerfeln[1]; // y

                final float scaleX = (float)btnWuerfel.getWidth() / (float)imgViewDice.getWidth();
                final float scaleY = (float)btnWuerfel.getHeight() / (float)imgViewDice.getHeight();

                imgViewDice.animate()
                        .x(toX -imgViewDice.getWidth()*scaleX)
                        .y(toY -imgViewDice.getHeight()*scaleY)
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

                                imgViewDice.setX(screenWidth/2f - (imgViewDice.getWidth()/2));
                                imgViewDice.setY(screenHeight/2f - (imgViewDice.getHeight()/2));

                                imgViewDice.setAlpha(1f);
                                imgViewDice.setVisibility(View.INVISIBLE);

                                btnWuerfel.setImageResource(diceImages[finalResult]);

                                btnWuerfel.setEnabled(true);
                            }
                            @Override
                            public void onAnimationStart(Animator animation) {}
                            @Override
                            public void onAnimationCancel(Animator animation) {}
                            @Override
                            public void onAnimationRepeat(Animator animation) {}
                        })
                        .start();
            }
        });


        synchronized (DummyDice.get()){
            System.out.println("notifying: "+DummyDice.get());
            DummyDice.get().notify();
        }


    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spieloberflaeche);

        Game.getInstance().init("Hans", "Peter", "Dieter", "Anneliese");

        Game.getInstance().setBoardView((BoardView) (findViewById(R.id.spielFeld)));

        state = (TextView)(findViewById(R.id.textView_status));


        Schummeln = new Cheat(false);
        Schummeln.setPlayerCheating(false);

        //Sensor Manager erstellen
        SM = (SensorManager)getSystemService(SENSOR_SERVICE);

        //Licht Sensor erstellen
        LightSensor = SM.getDefaultSensor(Sensor.TYPE_LIGHT);
        SM.registerListener(this, LightSensor, SensorManager.SENSOR_DELAY_GAME);

        //aktuell spielender Spieler wird des Schummelns verdächtigt
        btnAufdecken = (ImageButton)(findViewById(R.id.imageButton_aufdecken)); // ToDO: Disable für gerade spielenden Spieler
        btnAufdecken.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(Schummeln.hasRemotePlayerCheated()){
                    // Spieler, der geschummelt hat, setzt aus
                } else if (!Schummeln.hasRemotePlayerCheated()){
                    // Spieler, der falsch verdächtigt hat (den Btn gedrückt hat), setzt aus.
                }
            }
        });


        btnWuerfel = (ImageButton)(findViewById(R.id.imageButton_wuerfel)); //ToDo: Disable für Spieler die nicht am Zug sind

        btnWuerfel.setEnabled(true);

        DummyDice.get();

        DummyDice.setDiceButton(btnWuerfel);
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

        // init dice
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
                    Game.getInstance().play();
                } catch (IllegalAccessException e){
                    e.printStackTrace();

                }
            }
        }.start();
        }




    /**
     * Determines the screen height and width
     */
    private void getScreenDimensions(){
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        screenWidth = size.x;
        screenHeight = size.y;
    }


    /**
     * Called when the orientation changes (i.e. from portrait to landscape mode)
     * @param newConfig
     */
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        // refresh screen dimensions when screen orientation changes
        getScreenDimensions();
    }


    /**
     * Schummelfunktion sollte bei jedem Spielerwechsel auf false gesetzt werden.
     * Da auf änderung reagiert, dürfte nicht wenn bevor man am zug ist verdunkelt wird nicht reagiert werden.
     */
    @Override
    public void onSensorChanged(SensorEvent event) {
        //ToDO schüttelsensor für würfeln impl.

        /** Für Licht
         * Reagiert bei änderung wird entsprechender Wert zwischen 0.0 und 40000 angegeben.
         * wenn schummel funktion ab Dunkel sich einschaltet. Annahme Dunkel ab 1000.
         */
        if(event.sensor.getType() == Sensor.TYPE_LIGHT) {
            float Lichtwert = event.values[0];
            if(Lichtwert <= 50){
                //state.setText("Schummeln: " + true);  //Test
                Schummeln.setPlayerCheating(true);
            }
            //Kein else da nach spieler wechsel allgemein auf false zurückgesetz wird
        }

    }


    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        //nicht in verwendung
    }

}
