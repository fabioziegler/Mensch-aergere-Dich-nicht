package com.vintagetechnologies.menschaergeredichnicht.Impl;

import android.app.ActionBar;
import android.app.Activity;
import android.util.Log;
import android.widget.ImageButton;

import com.vintagetechnologies.menschaergeredichnicht.R;
import com.vintagetechnologies.menschaergeredichnicht.Spieloberflaeche;
import com.vintagetechnologies.menschaergeredichnicht.structure.Dice;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by johannesholzl on 15.04.17.
 */

public class RealDice extends DiceImpl {

    private static RealDice realDice;
    private static ImageButton diceButton;


    public static RealDice get() {
        if (realDice == null) {
            realDice = new RealDice();
        }

        return realDice;
    }


    public static void reset(){
        realDice = new RealDice();
    }


    private RealDice() {
    }

    public void waitForRoll() {

        //Spieloberflaeche s = (Spieloberflaeche) RealDice.diceButton.getContext();
        Activity s = (Activity) diceButton.findViewById(R.id.imageButton_wuerfel).getContext();

        s.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                RealDice.diceButton.setClickable(true);
                RealDice.diceButton.setAlpha(1f);
            }
        });

        synchronized (realDice) {

            //realDice.notify();

            Log.i("Dice", "waiting: "+ realDice);

            try {
                realDice.wait();
            } catch (InterruptedException e) {
                Logger.getLogger(RealDice.class.getName()).log(Level.INFO, "Exception while waiting!", e);
                Thread.currentThread().interrupt();
            }

            System.out.println("done waiting");
            //diceButton.setEnabled(false);

        }

        s.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                RealDice.diceButton.setClickable(false);
                RealDice.diceButton.setAlpha(0.5f);

            }
        });
    }

    public static void setDiceButton(ImageButton db){
        diceButton = db;
    }
}
