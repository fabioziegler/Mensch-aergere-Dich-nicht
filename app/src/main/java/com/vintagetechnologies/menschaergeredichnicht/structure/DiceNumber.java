package com.vintagetechnologies.menschaergeredichnicht.structure;

/**
 * Created by Rainer on 03.04.2017.
 */

public enum DiceNumber {

    ONE(1), TWO(2), THREE(3), FOUR(4), FIVE(5), SIX(6);

    private int number;

    DiceNumber(int n) {
        this.number = n;
    }

    public int getNumber(){
        return number;
    }

    public void setNumber(int number){
		this.number = number;
	}
}
