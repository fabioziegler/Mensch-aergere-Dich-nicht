package com.vintagetechnologies.menschaergeredichnicht.Impl;

import android.widget.ImageButton;

import com.vintagetechnologies.menschaergeredichnicht.structure.Dice;

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

    private RealDice() {

    }

    public void waitForRoll() {
        synchronized (realDice) {

            //diceButton.setEnabled(true);
            //realDice.notify();

            System.out.println("waiting: "+ realDice);

            try {
                realDice.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            System.out.println("done waiting");
            //diceButton.setEnabled(false);

        }
    }

    public static void setDiceButton(ImageButton db){
        diceButton = db;
    }
}
