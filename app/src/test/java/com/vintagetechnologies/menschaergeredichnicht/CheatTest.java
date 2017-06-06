package com.vintagetechnologies.menschaergeredichnicht;

import com.vintagetechnologies.menschaergeredichnicht.impl.ActualGame;
import com.vintagetechnologies.menschaergeredichnicht.structure.Cheat;
import com.vintagetechnologies.menschaergeredichnicht.structure.Dice;

import org.junit.Before;
import org.junit.Test;

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
		ActualGame.getInstance().setLocalGame(true);
        schummeln.setPlayerCheating(true);
        assertEquals(true, schummeln.isPlayerCheating());
    }

    /*
    @Test
    public void testCheatFunction(){
        //nicht Testbar weil CurrentPlayer nicht Definiert
        Game.getInstance().getCurrentPlayer().getSchummeln().setPlayerCheating(true);
        dice.roll();
        assertEquals(DiceNumber.SIX, dice.getDiceNumber());
    }
    */
}
