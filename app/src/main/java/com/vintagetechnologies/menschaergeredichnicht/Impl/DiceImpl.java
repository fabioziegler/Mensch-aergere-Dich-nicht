package com.vintagetechnologies.menschaergeredichnicht.Impl;

import com.vintagetechnologies.menschaergeredichnicht.structure.Dice;
import com.vintagetechnologies.menschaergeredichnicht.structure.DiceNumber;

/**
 * Created by johannesholzl on 08.05.17.
 */

public abstract class DiceImpl extends Dice {

    public abstract void waitForRoll();

    public void setDiceNumber(DiceNumber dn){
        this.diceNumber = dn;
    }

}
