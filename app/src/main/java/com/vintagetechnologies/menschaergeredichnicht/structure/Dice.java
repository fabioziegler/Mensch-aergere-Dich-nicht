package com.vintagetechnologies.menschaergeredichnicht.structure;

/**
 * Created by Rainer on 03.04.2017.
 */

public class Dice {

    private DiceNumber diceNumber;

    public void roll() {
        int length = DiceNumber.values().length;

        int n = (int) (Math.random() * length);

        this.diceNumber = DiceNumber.values()[n];
    }

    public DiceNumber getDiceNumber() {
        return diceNumber;
    }

}
