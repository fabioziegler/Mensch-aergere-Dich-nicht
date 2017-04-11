package com.vintagetechnologies.menschaergeredichnicht;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.widget.TextView;

public class Spieloberflaeche extends AppCompatActivity implements SensorEventListener{

    Sensor LightSensor;
    SensorManager SM;

    TextView state;
    ImageButton btnExit;
    // toDO: alle Spielfunktionen ect. hinzufügen
    ImageButton btnWuerfel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spieloberflaeche);

        //Sensor Manager erstellen
        SM = (SensorManager)getSystemService(SENSOR_SERVICE);
        //Licht Sensor erstellen
        LightSensor = SM.getDefaultSensor(Sensor.TYPE_LIGHT);
        SM.registerListener(this,LightSensor,SensorManager.SENSOR_DELAY_GAME);

        state = (TextView)(findViewById(R.id.textView_status));
        btnWuerfel = (ImageButton)(findViewById(R.id.imageButton_wuerfel));
        btnExit = (ImageButton)(findViewById(R.id.imageButton_exit));

        btnExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // toDO: Warnung das man das Spielverlässt (bzw. es beendet)
                startActivity(new Intent(Spieloberflaeche.this, Hauptmenue.class));
            }
        });
    }

    // float alterWert
    @Override
    public void onSensorChanged(SensorEvent event) {
        // zum testen:
        if(event.sensor.getType() == Sensor.TYPE_LIGHT) {
            state.setText("Licht: " + event.values[0]);
        }
        //ToDO: Funktioniert es??
        //wenn ja:
        /*
        if (alterWert == null){
            alterWert = event.values zu float konvertiert
        } else {
            differenz = alterWert (heller) - event.values[0] //eventuell differenz mit betrag analysieren
            if ( differenz >= ab welcher änderung reagiert wird) {
                setSummeln(true);
            }
            alterWert = event.values zu float konvertiert
        }
         */
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        //nicht in verwendung
    }
}
