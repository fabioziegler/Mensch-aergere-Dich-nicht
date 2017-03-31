package com.vintagetechnologies.menschaergeredichnicht;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class Spielregeln extends AppCompatActivity {

    Button btnOk;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spielregeln);

        btnOk = (Button)(findViewById(R.id.BTN_ok));

        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Spielregeln.this, Hauptmenue.class));
            }
        });
    }
}
