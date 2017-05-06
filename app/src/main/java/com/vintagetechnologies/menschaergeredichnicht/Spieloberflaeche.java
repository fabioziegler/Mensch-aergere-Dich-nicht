package com.vintagetechnologies.menschaergeredichnicht;


import android.animation.Animator;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Point;
import android.os.SystemClock;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.widget.Button;
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
import android.widget.Toast;

import com.vintagetechnologies.menschaergeredichnicht.structure.Cheat;
import com.vintagetechnologies.menschaergeredichnicht.structure.Game;
import com.vintagetechnologies.menschaergeredichnicht.structure.Player;
import com.vintagetechnologies.menschaergeredichnicht.view.BoardView;


public class Spieloberflaeche extends AppCompatActivity implements SensorEventListener {

    private Sensor LightSensor;
    private SensorManager SM;

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

        Game.getInstance().setBoardView((BoardView) (findViewById(R.id.spielFeld)));

        Game.getInstance().init("Hans", "Peter", "Dieter", "Anneliese");

        state = (TextView)(findViewById(R.id.textView_status));

        Schummeln = Game.getInstance().getCurrentPlayer().getSchummeln();

        //Sensor Manager erstellen
        SM = (SensorManager)getSystemService(SENSOR_SERVICE);

        //Licht Sensor erstellen
        LightSensor = SM.getDefaultSensor(Sensor.TYPE_LIGHT);
        SM.registerListener(this, LightSensor, SensorManager.SENSOR_DELAY_GAME);


        //aktuell spielender Spieler wird des Schummelns verdächtigt
        btnAufdecken = (ImageButton)(findViewById(R.id.imageButton_aufdecken));
        //Disable wenn Spieler gerade spielt
        //ToDo if(Game.getInstance().getCurrentPlayer().isAktive()){ btnAufdecken.setEnabled(false);}
        if (btnAufdecken.isEnabled()) {
            btnAufdecken.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    /**
                     * Alle Spieler durch laufen ob geschummelt wurde (weil nur der aktuell Spielende noch nicht aufgerufen werden kann)
                     * Da nur der Spieler der an der Reihe ist überhaupt schummeln kann.
                     */

            // ToDo: Überarbeiten: aktiver Spieler wird zu currentPlayer. bntDrückSpieler über namen herrausfinden

                    Player[] spieler = Game.getInstance().getPlayers();
                    //ToDo: bessere lösung finden, evt. "globale" Variable
                    /*
                    int aktiveSpieler = 5;

                    //aktiven Spieler finden
                    for (int i = 0; i < spieler.length; i++) {
                        if (spieler[i].isAktive()){
                            aktiveSpieler = i;
                        }
                    }
                    //noch nicht optimal gelöst..
                    if (aktiveSpieler >= 4){
                        throw new IllegalStateException();
                    }
                    */
                    if (Game.getInstance().getCurrentPlayer().getSchummeln().isPlayerCheating()) {
                            //TODO currentPlayer setzt aus (Nachricht an Host, isHost überprüfen)

                        //ToDO: sende an ALLE Meldung: (spieler[aktiveSpieler].getName + "hat geschummelt und muss nächste Runde aussetzen")
                        Context context = getApplicationContext();
                        CharSequence text = Game.getInstance().getCurrentPlayer().getName()+"hat geschummelt und muss nächste Runde aussetzen";
                        int duration = Toast.LENGTH_SHORT;

                        Toast toast = Toast.makeText(context, text, duration);
                        toast.show();

                    }else{
                            //TODo aktiver/drückender Spieler setzt aus (Nachricht an Host, isHost überprüfen)

                        //ToDO: sende an alle Meldung: (spieler[currentSpieler].getName + "hat falsch verdächtigt und muss nächste Runde aussetzen")
                       // Context context = getApplicationContext();
                        //CharSequence text = Player.getName()+"hat falsch verdächtigt und muss nächste Runde aussetzen";
                        //int duration = Toast.LENGTH_SHORT;

                        //Toast toast = Toast.makeText(context, text, duration);
                        //toast.show();
                    }

                    /*
                    //Alternativ
                    boolean schummelt = false;

                    for (int i = 0; i < spieler.length; i++) {
                        if (spieler[i].getSchummeln().isPlayerCheating()) {
                            // TODO Spieler i setzt aus
                            schummelt = true;
                            //ToDO: sende an alle Meldung: (spieler[aktiverSpieler].getName + "hat geschummelt und muss nächste Runde aussetzen")
                        }
                    }
                    if (!schummelt) {
                        // ToDO getSpieler der gerade spielt.
                        // ToDo Spieler, der falsch verdächtigt hat (den Btn gedrückt hat), setzt aus.
                    }

                    */


                }
            });

        }


        btnWuerfel = (ImageButton)(findViewById(R.id.imageButton_wuerfel));

        btnWuerfel.setEnabled(true);

        DummyDice.get();

        DummyDice.setDiceButton(btnWuerfel);
        imgViewDice = (ImageView) (findViewById(R.id.imgViewDice));

        //Wenn Spieler nicht aktiv ist soll der Würfel btn nicht aktiv sein
       //toDo if(!Game.getInstance().getCurrentPlayer().isAktive()){ btnWuerfel.setEnabled(true);}

       // if(btnWuerfel.isEnabled()) {
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
        //}
//        btnFigurSelect.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                // toDO: Zu setzende Figur auswählen
//                startActivity(new Intent(Spieloberflaeche.this, Hauptmenue.class));
//            }
//        });
//
//        btnMoveFigur.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                // toDO: Ausgewählte Figur um gewürfelte Augenzahl weitersetzen
//                startActivity(new Intent(Spieloberflaeche.this, Hauptmenue.class));
//            }
//        });

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
                    Log.e("Spieloberflaeche", "Error in game init", e);
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

    // Warnung bevor man das Spiel verlässt (noch nicht getestet)
    @Override
    public  void onBackPressed(){
          new AlertDialog.Builder(this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("Closing Activity")
                .setMessage("Are you sure you want to close this activity?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }

                })
                .setNegativeButton("No", null)
                .show();
    }
    /**
     * ToDO: Sollte nur aktiviert sein wenn Spieler aktuell spielt.
     * ToDO: Schummelfunktion sollte bei jedem Spielerwechsel auf false gesetzt werden.
     * Da auf änderung reagiert, dürfte nicht wenn bevor man am zug ist verdunkelt wird nicht reagiert werden.
     */
    @Override
    public void onSensorChanged(SensorEvent event) {
        //ToDO schüttelsensor für würfeln impl.

        /** Für Licht
         * Reagiert bei änderung wird entsprechender Wert zwischen 0.0 und 40000 angegeben.
         * wenn schummel funktion ab Dunkel sich einschaltet. Dunkel ab 10.
         */
        if(event.sensor.getType() == Sensor.TYPE_LIGHT) {
            //überprüfen ob Spieler am zug ist
            //ToDo if(Game.getInstance().getCurrentPlayer().isAktive()) {
                float Lichtwert = event.values[0];
                if (Lichtwert <= 10) {
                    //state.setText("Schummeln: " + true);  //Test
                    Schummeln.setPlayerCheating(true);
                }
            //}
        }

    }


    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        //nicht in verwendung
    }

    public void setStatus(String status){
        state.setText(status);
    }

}
