package com.vintagetechnologies.menschaergeredichnicht;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

public class Spieloberflaeche extends AppCompatActivity {

    ImageButton btnExit;
    // toDO: alle Spielfunktionen ect. hinzufügen
    ImageButton btnWuerfel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spieloberflaeche);

        btnWuerfel = (ImageButton)(findViewById(R.id.imageButton_wuerfel));
        btnExit = (ImageButton)(findViewById(R.id.imageButton_exit));

        btnExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // toDO: Warnung das man das Spielverlässt (bzw. es beendet)
                startActivity(new Intent(Spieloberflaeche.this, Hauptmenue.class));
            }
        });
    }
}
