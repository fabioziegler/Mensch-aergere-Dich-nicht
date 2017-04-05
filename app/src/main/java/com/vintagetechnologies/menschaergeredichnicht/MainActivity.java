package com.vintagetechnologies.menschaergeredichnicht;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {


    private Button btnGo;
    private EditText txtName;

    private GameSettings gameSettings;

    /**
     * called when the user clicks the go button
     */
    private void btnGoClicked(){

        String playerName = txtName.getText().toString().trim();

        if(!playerName.equals("")){   // show main menu

            // save settings
            gameSettings = new GameSettings(playerName);
            DataHolder.getInstance().save("GAMESETTINGS", gameSettings);

            Intent intent = new Intent(this, Hauptmenue.class);
            //intent.putExtra("USERNAME", name);
            startActivity(intent);

        }else{  // display error message
            Toast.makeText(getApplicationContext(), R.string.msgEnterUsername, Toast.LENGTH_LONG).show();
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
    }
}
