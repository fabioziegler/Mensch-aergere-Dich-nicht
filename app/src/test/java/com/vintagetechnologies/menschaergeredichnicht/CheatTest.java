package com.vintagetechnologies.menschaergeredichnicht;

import com.vintagetechnologies.menschaergeredichnicht.implementation.ActualGame;
import com.vintagetechnologies.menschaergeredichnicht.structure.Cheat;
import com.vintagetechnologies.menschaergeredichnicht.structure.Dice;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Created by Demi on 07.05.2017.
 */

public class CheatTest {

    Dice dice;
    private Cheat schummeln;

    @Before
    public void before() {
        schummeln = new Cheat();
        dice = new Dice();
    }

    @Test
    public void testCheatSetting(){
		ActualGame.getInstance().setLocalGame(true);
        schummeln.setPlayerCheating(true);
        assertEquals(true, schummeln.isPlayerCheating());
    }
}
