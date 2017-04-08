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


    private void btnShowRulesClicked(){
        // TODO: implement
        //startActivity(new Intent(Hauptmenue.this, Spielregeln.class));
    }

    /**
     * Called when the button "Spielregeln" is clicked
     */
    private void btnOpenSettingsClicked(){
        startActivity(new Intent(Hauptmenue.this, Einstellungen.class));
    }

    /**
     * Called when the button "Neues Spiel" is clicked
     */
    private void btnHostGameClicked(){
        Intent intent = new Intent(this, GameHost.class);
        startActivity(intent);
        finish();
    }


    /**
     * Called when the button "Spiel beitreten" is clicked
     */
    private void btnJoinGameClicked(){
        Intent intent = new Intent(this, GameClient.class);
        startActivity(intent);
        finish();
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
    }

}
