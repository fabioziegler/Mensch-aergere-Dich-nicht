package com.vintagetechnologies.menschaergeredichnicht;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class Hauptmenue extends AppCompatActivity {


    private Button btnShowRules;
    private Button btnOpenSettings;
    private Button btnHostGame;
    private Button btnJoinGame;

    private GameSettings gameSettings;


    private void btnShowRulesClicked(){
        // TODO: implement
        //startActivity(new Intent(Hauptmenue.this, Spielregeln.class));
    }

    private void btnOpenSettingsClicked(){
        startActivity(new Intent(Hauptmenue.this, Einstellungen.class));
    }

    private void btnHostGameClicked(){
        startActivity(new Intent(Hauptmenue.this, Mitspielerauswahl.class));
    }

    private void btnJoinGameClicked(){
        // TODO: implement
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hauptmenue);

        btnShowRules = (Button)(findViewById(R.id.btnShowRules));
        btnOpenSettings = (Button)(findViewById(R.id.btnOpenSettings));
        btnHostGame = (Button)(findViewById(R.id.btnHostGame));
        btnJoinGame = (Button)(findViewById(R.id.btnJoinGame));

        btnShowRules.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                btnShowRulesClicked();
            }
        });

        btnOpenSettings.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                btnOpenSettingsClicked();
            }
        });

        btnHostGame.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                btnHostGameClicked();
            }
        });

        btnJoinGame.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                btnJoinGameClicked();
            }
        });

        // retrieve game settings (from data holder)
        gameSettings = (GameSettings) DataHolder.getInstance().retrieve("GAMESETTINGS");
    }

}
