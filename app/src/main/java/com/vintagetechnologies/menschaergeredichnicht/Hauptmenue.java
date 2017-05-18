package com.vintagetechnologies.menschaergeredichnicht;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.vintagetechnologies.menschaergeredichnicht.Impl.ActualGame;
import com.vintagetechnologies.menschaergeredichnicht.networking.Network;
import com.vintagetechnologies.menschaergeredichnicht.networking.kryonet.MyClientActivity;
import com.vintagetechnologies.menschaergeredichnicht.networking.kryonet.MyServerActivity;


public class Hauptmenue extends AppCompatActivity {


    private Button btnShowRules;
    private Button btnOpenSettings;
    private Button btnHostGame;
    private Button btnJoinGame;
    private Button btnLocalMultiplayer;
    private ImageButton btnAbout;

    private void btnShowRulesClicked(){
        startActivity(new Intent(Hauptmenue.this, Regeln.class));
    }

    private void btnLocalMultiplayerClicked(){
		startActivity(new Intent(Hauptmenue.this, Spieloberflaeche.class));
		ActualGame.reset();	// reset e.g. when a multiplayer game was played before..
		ActualGame.getInstance().setLocalGame(true);
	}


	private void btnAboutClicked(){
		startActivity(new Intent(Hauptmenue.this, AboutActivity.class));
	}

    /**
     * Called when the button "Neues Spiel" is clicked
     */
    private void btnHostGameClicked(){

		// check wifi connection
		if(!Network.isConnectedToWiFiNetwork(this)){
			Toast.makeText(getApplicationContext(), R.string.noWiFiConnection, Toast.LENGTH_LONG).show();
			return;
		}

		ActualGame.reset();
		ActualGame.getInstance().setLocalGame(false);
        Intent intent = new Intent(this, MyServerActivity.class);
        startActivity(intent);
        finish();
    }


    /**
     * Called when the button "Spiel beitreten" is clicked
     */
    private void btnJoinGameClicked(){

		// check wifi connection
		if(!Network.isConnectedToWiFiNetwork(this)){
			Toast.makeText(getApplicationContext(), R.string.noWiFiConnection, Toast.LENGTH_LONG).show();
			return;
		}

		ActualGame.reset();
		ActualGame.getInstance().setLocalGame(false);
        Intent intent = new Intent(this, MyClientActivity.class);
        startActivity(intent);
        finish();
    }


    /**
     * Called when the button "Spielregeln" is clicked
     */
    private void btnOpenSettingsClicked(){
        startActivity(new Intent(Hauptmenue.this, Einstellungen.class));
    }


    /**
     * Called when the user pressed the back button
     */
    @Override
    public void onBackPressed() {
        startActivity(new Intent(this, MainActivity.class));

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
        btnLocalMultiplayer = (Button)(findViewById(R.id.btnLocalMultiplayer));
        btnAbout = (ImageButton)(findViewById(R.id.imageButton_about));

        btnShowRules.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                btnShowRulesClicked();
            }
        });

        btnLocalMultiplayer.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                btnLocalMultiplayerClicked();
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

        btnAbout.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
				btnAboutClicked();
            }
        });
    }

}
