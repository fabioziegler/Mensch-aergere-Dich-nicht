package com.vintagetechnologies.menschaergeredichnicht;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RadioGroup;

public class Hauptmenue extends AppCompatActivity {


    private Button btnSpielregeln;
    private Button btnEinstellung;
    private Button btnNeuesSpiel;

    private GameSettings gameSettings;

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
                startActivity(new Intent(Hauptmenue.this, Regeln.class));
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

        // retrieve game settings (from data holder)
        gameSettings = (GameSettings) DataHolder.getInstance().retrieve("GAMESETTINGS");
    }

}
