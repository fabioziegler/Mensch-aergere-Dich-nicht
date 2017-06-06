package com.vintagetechnologies.menschaergeredichnicht.implementation;

import android.app.Activity;
import android.util.Log;
import android.widget.ImageButton;

import com.vintagetechnologies.menschaergeredichnicht.R;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by johannesholzl on 15.04.17.
 */

public class RealDice extends DiceImpl {

    private static RealDice realDice;
    private static ImageButton diceButton;


    private RealDice() {
    }


    public static RealDice get() {
        if (realDice == null) {
            realDice = new RealDice();
        }

        return realDice;
    }


    public static void reset(){
        realDice = new RealDice();
    }





    public void waitForRoll() {

        Activity s = (Activity) diceButton.findViewById(R.id.imageButton_wuerfel).getContext();

        s.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                RealDice.diceButton.setClickable(true);
                RealDice.diceButton.setAlpha(1f);
            }
        });

        synchronized (realDice) {

            Log.i("Dice", "waiting: "+ realDice);

            try {
                realDice.wait();
            } catch (InterruptedException e) {
                Logger.getLogger(RealDice.class.getName()).log(Level.INFO, "Exception while waiting!", e);
                Thread.currentThread().interrupt();
            }

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
