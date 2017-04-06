package com.vintagetechnologies.menschaergeredichnicht;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.RadioButton;

public class Einstellungen extends AppCompatActivity {

    private GameSettings gameSettings;

    private Button btnSpeichern;
    private CheckBox cbMusic, cbCheating;
    private RadioButton radioBtnDesignClassic, radioBtnDesignVintage;


    /**
     * Save settings and exit settings activity
     */
    private void btnSaveSettingsClicked(){
        gameSettings.setMusicEnabled(cbMusic.isChecked());
        gameSettings.setCheatingEnabled(cbCheating.isChecked());

        if(radioBtnDesignClassic.isChecked())
            gameSettings.setBoardDesign(GameSettings.BoardDesign.CLASSIC);
        else
            gameSettings.setBoardDesign(GameSettings.BoardDesign.VINTAGE);

        gameSettings.savePermanently(this.getApplicationContext()); // save settings to disk

        finish();   // end settings activity
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_einstellungen);

        btnSpeichern = (Button) findViewById(R.id.BTN_speichern);
        cbMusic = (CheckBox) findViewById(R.id.cbMusic);
        cbCheating = (CheckBox) findViewById(R.id.cbCheating);
        radioBtnDesignClassic = (RadioButton) findViewById(R.id.radioBtnDesignClassic);
        radioBtnDesignVintage = (RadioButton) findViewById(R.id.radioBtnDesignVintage);

        btnSpeichern.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnSaveSettingsClicked();
            }
        });

        gameSettings = (GameSettings) DataHolder.getInstance().retrieve("GAMESETTINGS");

        loadSettings();
    }

    /**
     * Update GUI
     */
    private void loadSettings(){
        cbMusic.setChecked(gameSettings.isMusicEnabled());
        cbCheating.setChecked(gameSettings.isCheatingEnabled());
        radioBtnDesignClassic.setChecked(gameSettings.getBoardDesign() == GameSettings.BoardDesign.CLASSIC ? true : false);
        radioBtnDesignVintage.setChecked(gameSettings.getBoardDesign() == GameSettings.BoardDesign.VINTAGE ? true : false);
    }
}
