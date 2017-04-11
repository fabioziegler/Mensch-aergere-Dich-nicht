package com.vintagetechnologies.menschaergeredichnicht;

import android.content.Intent;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

    private Random rand;

    /**
     * wird aufgerufen wenn btnWuerfel betätigt wird
     */
    private void btnWuerfelClicked(){
        imgViewDice.setVisibility(View.VISIBLE);

        dice.roll();

        int result = dice.getDiceNumber().getNumber();

        // dice rolls for 3 seconds, and changes 5x a second it's number

        // animateion:
        for (int i = 0; i < 3; i++) {       // 3 seconds
            for (int j = 0; j < 5; j++) {   // 1 second (5 changes)
                int randomIndex = rand.nextInt(6);
                imgViewDice.setImageResource(diceImages[randomIndex]);
                SystemClock.sleep(200);
            }
        }

        // setze Egebnis des Würfelns
        imgViewDice.setImageResource(diceImages[result]);

        btnWuerfel.setImageResource(diceImages[result]);

        // zeige Ergebnis für 1 Sekunde
        SystemClock.sleep(1000);

        // ausblenden
        imgViewDice.setVisibility(View.INVISIBLE);
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
                btnWuerfelClicked();
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
    }
}
