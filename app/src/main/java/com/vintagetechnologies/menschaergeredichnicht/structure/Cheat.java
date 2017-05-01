package com.vintagetechnologies.menschaergeredichnicht.structure;

import com.vintagetechnologies.menschaergeredichnicht.DataHolder;
import com.vintagetechnologies.menschaergeredichnicht.GameLogic;

/**
 * Created by Demi on 11.04.2017.
 */

public class Cheat {

    //Merkt sich ob geschummelt wurde. Muss nach jedem Personen wechsel auf false gesetzt werden

    private static final String TAG = "cheated";

    boolean playerCheating;

    public Cheat (){
        this.playerCheating = false;
    }

    public void setPlayerCheating(boolean c){
        this.playerCheating = c;
        //host nachricht schicken
        GameLogic gameLogic = (GameLogic) DataHolder.getInstance().retrieve("GAMELOGIC");
        boolean isHost = gameLogic.isHost();

        if (!isHost){
            gameLogic.sendToHost(TAG, "true");
        }
    }


    /**
     * Für WÜRFEL; stellt fest ob lokaler Player gecheatet hat
     * @return
     */
    public boolean isPlayerCheating() { return playerCheating; }
}
