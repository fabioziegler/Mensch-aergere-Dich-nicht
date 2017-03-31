package com.vintagetechnologies.menschaergeredichnicht;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class Hauptmenue extends AppCompatActivity {

    Button btnSpielregeln;
    Button btnEinstellung;
    Button btnNeuesSpiel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hauptmenue);

        btnSpielregeln = (Button)(findViewById(R.id.BTN_spielregeln));
        btnEinstellung = (Button)(findViewById(R.id.BTN_einstellungen));
        btnNeuesSpiel = (Button)(findViewById(R.id.BTN_neues_spiel));

        btnSpielregeln.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Hauptmenue.this, Spielregeln.class));
            }
        });
        btnEinstellung.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Hauptmenue.this, Einstellungen.class));
            }
        });

        btnNeuesSpiel.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Hauptmenue.this, Mitspielerauswahl.class));
            }
        });




    }
}
