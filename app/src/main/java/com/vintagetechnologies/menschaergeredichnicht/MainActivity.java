package com.vintagetechnologies.menschaergeredichnicht;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.vintagetechnologies.menschaergeredichnicht.networking.Network;

public class MainActivity extends AppCompatActivity {


    private Button btnGo;
    private EditText txtName;

    private GameSettings gameSettings;

    /**
     * called when the user clicks the go button
     */
    private void btnGoClicked(){

        String playerName = txtName.getText().toString().trim();

        if(!playerName.isEmpty()){   // show main menu

            // save settings
            gameSettings.setPlayerName(playerName);
            DataHolder.getInstance().save(Network.DATAHOLDER_GAMESETTINGS, gameSettings);

            Intent intent = new Intent(this, Hauptmenue.class);
            startActivity(intent);
            finish();

        }else{  // display error message
            txtName.setError(getString(R.string.msgEnterUsername));
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        txtName = (EditText)(findViewById(R.id.txtName));
        btnGo = (Button)(findViewById(R.id.btnOpenMainMenue));

        btnGo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               btnGoClicked();
            }
        });

        // create game settings instance (loads settings from disk if available)
        gameSettings = new GameSettings(getApplicationContext());

    }


    /**
     * Called when the user pressed the back button
     */
    @Override
    public void onBackPressed() {
        finish();
    }
}
