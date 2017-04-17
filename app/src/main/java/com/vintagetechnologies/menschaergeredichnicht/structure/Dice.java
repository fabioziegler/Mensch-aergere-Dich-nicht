package com.vintagetechnologies.menschaergeredichnicht.structure;

/**
 * Created by Rainer on 03.04.2017.
 */

public class Dice {

    //public static final int min = 1;
    //public static final int max = 6;

    private DiceNumber diceNumber;

    public Dice() {

    }

    public void roll() {
        int length = DiceNumber.values().length;

        int n = (int) (Math.random() * length);

        this.diceNumber = DiceNumber.values()[n];
    }

    public DiceNumber getDiceNumber() {
        return diceNumber;
    }

    public void setDiceNumber(DiceNumber diceNumber) {
        this.diceNumber = diceNumber;
    }

}
