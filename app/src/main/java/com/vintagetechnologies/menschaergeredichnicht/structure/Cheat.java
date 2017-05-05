package com.vintagetechnologies.menschaergeredichnicht.structure;

import com.vintagetechnologies.menschaergeredichnicht.DataHolder;
import com.vintagetechnologies.menschaergeredichnicht.GameLogic;
import com.vintagetechnologies.menschaergeredichnicht.GameLogicClient;
import com.vintagetechnologies.menschaergeredichnicht.GameLogicHost;

import static com.vintagetechnologies.menschaergeredichnicht.networking.Network.MESSAGE_DELIMITER;
import static com.vintagetechnologies.menschaergeredichnicht.networking.Network.TAG_PLAYER_HAS_CHEATED;

/**
 * Created by Demi on 11.04.2017.
 */

public class Cheat {

    //Merkt sich ob geschummelt wurde. Muss nach jedem Personen wechsel auf false gesetzt werden

    boolean playerCheating;

    public Cheat (){
        this.playerCheating = false;
    }

    public void setPlayerCheating(boolean c){
        this.playerCheating = c;

        // host nachricht schicken
        GameLogic gameLogic = (GameLogic) DataHolder.getInstance().retrieve("GAMELOGIC");
        boolean isHost = gameLogic.isHost();

        if (!isHost){
			((GameLogicClient) gameLogic).sendToHost(TAG_PLAYER_HAS_CHEATED + MESSAGE_DELIMITER + "true");
        }
    }


    /**
     * Für WÜRFEL; stellt fest ob lokaler Player gecheatet hat
     * @return
     */
    public boolean isPlayerCheating() { return playerCheating; }


    /**
     * Für AUFDECKEN stellt fest ob aktuell spielender Spieler gecheatet hat
     * @return
     */
    public boolean hasRemotePlayerCheated(){
        // TODO: implement
        return false;
    }

}
