package com.vintagetechnologies.menschaergeredichnicht;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageButton;

import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.widget.TextView;

import com.vintagetechnologies.menschaergeredichnicht.structure.Cheat;


public class Spieloberflaeche extends AppCompatActivity implements SensorEventListener {

    Sensor LightSensor;
    SensorManager SM;

    TextView state;
    // toDO: alle Spielfunktionen ect. hinzufügen
    ImageButton btnWuerfel;
    Cheat Schummeln = null;

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


    }

    /**
     * Schummelfunktion sollte bei jedem Spielerwechsel auf false gesetzt werden.
     * Da auf änderung reagiert, dürfte nicht wenn bevor man am zug ist verdunkelt wird nicht reagiert werden.
     */
    @Override
    public void onSensorChanged(SensorEvent event) {
        //ToDO schüttelsensor für würfeln impl.


        /** Für Licht
         * Reagiert bei änderung wird entsprechender Wert zwischen 0.0 und 40000 angegeben.
         * wenn schummel funktion ab Dunkel sich einschaltet. Annahme Dunkel ab 1000.
         */
        if(event.sensor.getType() == Sensor.TYPE_LIGHT) {
            float Lichtwert = event.values[0];
            if(Lichtwert <= 1000){
                state.setText("Schummeln: " + true);
                Schummeln.setPlayerCheating(true);
            }
            //Kein else da nach spieler wechsel allgemein auf false zurückgesetz wird
        }

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        //nicht in verwendung
    }
}
