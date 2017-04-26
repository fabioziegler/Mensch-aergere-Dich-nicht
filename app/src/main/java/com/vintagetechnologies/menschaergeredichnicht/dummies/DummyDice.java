package com.vintagetechnologies.menschaergeredichnicht.dummies;

import android.widget.ImageButton;

import com.vintagetechnologies.menschaergeredichnicht.structure.Dice;

/**
 * Created by johannesholzl on 15.04.17.
 */

public class DummyDice extends Dice {

    private static DummyDice dummyDice;
    private static ImageButton diceButton;

    public static DummyDice get() {
        if (dummyDice == null) {
            dummyDice = new DummyDice();
            //dummyDice.r();
        }

        return dummyDice;
    }

    private DummyDice() {

    }

    public static void waitForRoll() {
        synchronized (dummyDice) {

            //diceButton.setEnabled(true);
            //dummyDice.notify();

            System.out.println("waiting: "+dummyDice);

            try {
                dummyDice.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            System.out.println("done waiting");
            //diceButton.setEnabled(false);

        }
    }

    public void r() {
        synchronized (this) {

            while (true) {
                try {
                    wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                roll();



                notify();
            }
        }
    }

    public static void setDiceButton(ImageButton db){
        diceButton = db;
    }
}
