package com.vintagetechnologies.menschaergeredichnicht.dummies;

import com.vintagetechnologies.menschaergeredichnicht.structure.Dice;

/**
 * Created by johannesholzl on 15.04.17.
 */

public class DummyDice extends Dice {

    private static DummyDice dummyDice;

    public static DummyDice get() {
        if (dummyDice == null) {
            dummyDice = new DummyDice();
            dummyDice.r();
        }

        return dummyDice;
    }

    private DummyDice() {

    }

    public static void waitForRoll() {
        synchronized (dummyDice) {
            dummyDice.notify();


            try {
                dummyDice.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }


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

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                notify();
            }
        }
    }
}
