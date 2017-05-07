package com.vintagetechnologies.menschaergeredichnicht.structure;

import java.util.ArrayList;
import java.util.HashSet;

/**
 * Created by Rainer on 03.04.2017.
 */

public class Dice {

    private HashSet<DiceNumber> blackList = new HashSet<>();
    private DiceNumber diceNumber;

    public void roll() {
        if(Game.getInstance().getCurrentPlayer().getSchummeln().isPlayerCheating()){
            this.diceNumber = DiceNumber.SIX;
        }else {
            do {
                int length = DiceNumber.values().length;

                int n = (int) (Math.random() * length);

                this.diceNumber = DiceNumber.values()[n];
            } while (blackList.contains(this.diceNumber));
        }
    }

    public DiceNumber getDiceNumber() {
        return diceNumber;
    }

    public void addToBlacklist(DiceNumber diceNumber){
        this.blackList.add(diceNumber);
    }

    public void emptyBlacklist(){
        this.blackList =  new HashSet<>();
    }
}
