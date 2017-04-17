package com.vintagetechnologies.menschaergeredichnicht;

import android.animation.Animator;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Point;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Display;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.vintagetechnologies.menschaergeredichnicht.structure.Dice;

import java.util.Random;

public class Spieloberflaeche extends AppCompatActivity {

    // toDO: alle Spielfunktionen ect. hinzufügen
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
    private final int ANIMATION_DURATION = 1600;

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

        dice.roll();

        final int result = dice.getDiceNumber().getNumber() - 1;

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
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                // Update UI elements
                imgViewDice.setImageResource(diceImages[result]);
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

                                btnWuerfel.setImageResource(diceImages[result]);

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
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spieloberflaeche);

        btnWuerfel = (ImageButton)(findViewById(R.id.imageButton_wuerfel));
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

}
