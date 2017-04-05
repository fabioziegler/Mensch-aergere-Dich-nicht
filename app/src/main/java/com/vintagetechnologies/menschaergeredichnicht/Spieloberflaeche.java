package com.vintagetechnologies.menschaergeredichnicht;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.vintagetechnologies.menschaergeredichnicht.structure.Dice;

public class Spieloberflaeche extends AppCompatActivity {

    // toDO: alle Spielfunktionen ect. hinzufügen
    private ImageButton btnExit;
    private ImageButton btnWuerfel;
    private ImageView imgViewDice;

    // Dice
    private Dice dice;

    /**
     * wird aufgerufen wenn btnWuerfel betätigt wird
     */
    private void btnWuerfelClicked(){
        imgViewDice.setVisibility(View.VISIBLE);

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spieloberflaeche);

        btnWuerfel = (ImageButton)(findViewById(R.id.imageButton_wuerfel));
        btnExit = (ImageButton)(findViewById(R.id.imageButton_exit));
        imgViewDice = (ImageView) (findViewById(R.id.imgViewDice));

        btnExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // toDO: Warnung das man das Spielverlässt (bzw. es beendet)
                startActivity(new Intent(Spieloberflaeche.this, Hauptmenue.class));
            }
        });

        btnWuerfel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnWuerfelClicked();
            }
        });

        // init dice
        dice = new Dice();
    }
}
