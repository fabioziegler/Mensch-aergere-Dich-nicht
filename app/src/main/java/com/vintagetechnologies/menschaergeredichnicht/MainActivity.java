package com.vintagetechnologies.menschaergeredichnicht;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {

    Button btnGo;
    EditText name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        name = (EditText)(findViewById(R.id.editText_name));
        btnGo = (Button)(findViewById(R.id.BTN_go_hauptmenue));

        btnGo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // toDO: Einfügen der Bedingung "nur wenn name != null" sonst Fehlermeldung
                startActivity(new Intent(MainActivity.this, Hauptmenue.class));
            }
        });

    }
}
