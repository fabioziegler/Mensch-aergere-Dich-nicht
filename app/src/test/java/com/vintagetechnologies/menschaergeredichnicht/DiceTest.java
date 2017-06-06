package com.vintagetechnologies.menschaergeredichnicht;

import com.vintagetechnologies.menschaergeredichnicht.structure.Dice;
import com.vintagetechnologies.menschaergeredichnicht.structure.DiceNumber;

import org.junit.Before;
import org.junit.Test;

import static junit.framework.Assert.*;
import static org.junit.Assert.assertNotEquals;

/**
 * Created by Rainer on 03.05.2017.
 */

public class DiceTest {

    Dice dice;

    @Before
    public void beforeTest(){
        dice = new Dice();
    }

    /**
     * Testet ob die Zahlen, die in der Blacklist enthalten sind, gewürfelt werden können
     */
    @Test
    public void addBlacklist(){
        dice.addToBlacklist(DiceNumber.SIX);
        dice.addToBlacklist(DiceNumber.FOUR);

        for (int i = 0; i < 1000; i++){
            dice.roll();
            assertNotEquals(DiceNumber.SIX, dice.getDiceNumber());
            assertNotEquals(DiceNumber.FOUR, dice.getDiceNumber());
        }
    }

    /**
     * Testet ob die Blacklist tatsächlich gelöscht wird
     */
    @Test
    public void emptyBlacklist(){
        dice.addToBlacklist(DiceNumber.FOUR);
        dice.addToBlacklist(DiceNumber.SIX);

        dice.emptyBlacklist();

        boolean[] rolled = new boolean[DiceNumber.values().length];

        for (int i = 0; i < 1000; i++){
            dice.roll();
            rolled[dice.getDiceNumber().getNumber()-1] = true;
        }

        for(boolean b : rolled){
            assertTrue(b);
        }
    }

    /**
     * Testet ob jede Zahl mindestens 1x gewürfelt wurde
     */
    @Test
    public void roll(){
        boolean[] rolled = new boolean[DiceNumber.values().length];

        for (int i = 0; i < 1000; i++){
            dice.roll();
            rolled[dice.getDiceNumber().getNumber()-1] = true;
        }

        for(boolean b : rolled){
            assertTrue(b);
        }
    }

    /**
     * Testet die Verteilung der Augenzahlen
     * n muss groß gewählt sein
     */
    @Test
    public void rollWithDistribution(){
        int[] rolled = new int[DiceNumber.values().length];

        int n = 1000;

        for (int i = 0; i < n*DiceNumber.values().length; i++){
            dice.roll();
            rolled[dice.getDiceNumber().getNumber()-1] += 1;
        }

        for(int b : rolled){
            boolean inRange = b < 1.1*n && b > 0.9*n;
            assertTrue(inRange);
        }
    }

}
