package com.vintagetechnologies.menschaergeredichnicht;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class Einstellungen extends AppCompatActivity {

    Button btnSpeichern;
    //toDo: Ton- und Designauswahl holen

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_einstellungen);

        btnSpeichern = (Button)(findViewById(R.id.BTN_speichern));

        btnSpeichern.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Einstellungen (ton und design) müssen übernommen werden)
                startActivity(new Intent(Einstellungen.this, Hauptmenue.class));
            }
        });
    }
}
