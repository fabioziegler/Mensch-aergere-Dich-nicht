package com.vintagetechnologies.menschaergeredichnicht.structure;

import com.vintagetechnologies.menschaergeredichnicht.DataHolder;
import com.vintagetechnologies.menschaergeredichnicht.GameLogic;
import com.vintagetechnologies.menschaergeredichnicht.GameLogicClient;
import com.vintagetechnologies.menschaergeredichnicht.GameLogicHost;
import com.vintagetechnologies.menschaergeredichnicht.networking.Network;

import static com.vintagetechnologies.menschaergeredichnicht.networking.Network.MESSAGE_DELIMITER;
import static com.vintagetechnologies.menschaergeredichnicht.networking.Network.TAG_PLAYER_HAS_CHEATED;

/**
 * Created by Demi on 11.04.2017.
 * Merkt sich ob geschummelt wurde. Muss nach jedem Personen wechsel auf false gesetzt werden
 */

public class Cheat {

    boolean playerCheating = false;

    public Cheat (){}

    public void setPlayerCheating(boolean c){
        this.playerCheating = c;
        //TODo unterscheidung ob vom Host oder Client bearbeitet? Wird in GamLogicHost&Client bearbeitet..
        //informHost(c);
    }

    public void informHost(boolean c){
        // host nachricht schicken
        GameLogic gameLogic = (GameLogic) DataHolder.getInstance().retrieve(Network.DATAHOLDER_GAMELOGIC);
        boolean isHost = gameLogic.isHost();

        if (!isHost){
			((GameLogicClient) gameLogic).sendToHost(TAG_PLAYER_HAS_CHEATED + MESSAGE_DELIMITER + String.valueOf(c));
        }
    }

    /**
     * Für WÜRFEL; stellt fest ob lokaler Player gecheatet hat
     * @return
     */
    public boolean isPlayerCheating() { return playerCheating; }
}
