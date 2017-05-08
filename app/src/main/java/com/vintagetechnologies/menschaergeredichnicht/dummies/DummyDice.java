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

        }

        return dummyDice;
    }

    private DummyDice() {

    }

    public static void waitForRoll() {

    }


}
