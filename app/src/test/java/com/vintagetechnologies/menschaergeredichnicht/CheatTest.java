package com.vintagetechnologies.menschaergeredichnicht;

import com.vintagetechnologies.menschaergeredichnicht.structure.Cheat;
import com.vintagetechnologies.menschaergeredichnicht.structure.Dice;
import com.vintagetechnologies.menschaergeredichnicht.structure.DiceNumber;
import com.vintagetechnologies.menschaergeredichnicht.structure.Player;
import com.vintagetechnologies.menschaergeredichnicht.structure.PlayerColor;

import org.junit.Before;
import org.junit.Test;

import dalvik.annotation.TestTarget;

import static org.junit.Assert.assertEquals;

/**
 * Created by Demi on 07.05.2017.
 */

public class CheatTest {

    //private Spieloberflaeche spieloberflaeche;
    Dice dice;
    private Cheat schummeln;
   // private Player player;

    @Before
    public void before() {
        //spieloberflaeche = new Spieloberflaeche();
        schummeln = new Cheat();
        dice = new Dice();
       // player = new Player(PlayerColor.BLUE, "Test");
    }

    @Test
    public void testCheatSetting(){
        schummeln.setPlayerCheating(true);
        assertEquals(true,schummeln.isPlayerCheating());
    }

    @Test
    public void testCheatFunction(){
        schummeln.setPlayerCheating(true);
        dice.roll();
        assertEquals(DiceNumber.SIX, dice.getDiceNumber());
    }

}
