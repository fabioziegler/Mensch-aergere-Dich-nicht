package com.vintagetechnologies.menschaergeredichnicht;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class Mitspielerauswahl extends AppCompatActivity {

    Button btnSpielstart;
    // toDO: Mitspielerliste erstellen

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mitspielerauswahl);

        btnSpielstart = (Button)(findViewById(R.id.BTN_spielstarten));

        btnSpielstart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // toDo: Bedingung "mind 1 Mitspieler ist ausgewählt" hinzufügen, sonst fehler meldung "mind. 1 muss ausgewählt werden"
                // toDo: Mitspielerauswahl übernehmen
                startActivity(new Intent(Mitspielerauswahl.this, Spieloberflaeche.class));
            }
        });
    }
}
