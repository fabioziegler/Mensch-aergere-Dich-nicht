package com.vintagetechnologies.menschaergeredichnicht.structure;

import com.vintagetechnologies.menschaergeredichnicht.DataHolder;
import com.vintagetechnologies.menschaergeredichnicht.GameLogic;
import com.vintagetechnologies.menschaergeredichnicht.GameLogicClient;
import com.vintagetechnologies.menschaergeredichnicht.GameLogicHost;
import com.vintagetechnologies.menschaergeredichnicht.implementation.ActualGame;
import com.vintagetechnologies.menschaergeredichnicht.networking.Network;

import static com.vintagetechnologies.menschaergeredichnicht.networking.Network.MESSAGE_DELIMITER;
import static com.vintagetechnologies.menschaergeredichnicht.networking.Network.TAG_PLAYER_HAS_CHEATED;

/**
 * Created by Demi on 11.04.2017.
 * Merkt sich ob geschummelt wurde. Muss nach jedem Personen wechsel auf false gesetzt werden
 */

public class Cheat {

    boolean playerCheating = false;

    public Cheat (){
        //empty constructor
    }

    //Dient nur dem Würfel auf 6 bei Schummeln
    public void setPlayerCheating(boolean c){
        this.playerCheating = c;

        //Fällt weg damit bei der Status schummeln wärend des ganzen Zuges erhalten bleibt.
        //Für den Würfel wird die Methode nämlich schon früher aufgerufen und auf false gesetzt.
        /*
		if(playerCheating)
			informHost(c);
		*/
    }

    //wird seperat aufgerufen in Spieleroberfläche wenn geschummelt wird und vom Host wenn neue Runde um wieder auf false zu setzen.
    //-> Dient nur der Schummeln-Aufdecken funktion.
    public void informHost(boolean c){

		if(ActualGame.getInstance().isLocalGame())
			return;

        // host nachricht schicken
        GameLogic gameLogic = (GameLogic) DataHolder.getInstance().retrieve(Network.DATAHOLDER_GAMELOGIC);
        boolean isHost = gameLogic.isHost();

        if (!isHost){
			((GameLogicClient) gameLogic).sendToHost(TAG_PLAYER_HAS_CHEATED + MESSAGE_DELIMITER + String.valueOf(c));
        }else{
            //Wird vom Host über set Playercheating übernommen; Problem nur dass so bald das Würfeln vorbei
            // ist der Cheating auf false gesetzt wird, und er somit nicht mehr aufgedeckt werden kann..:/
        }
    }

    /**
     * Für WÜRFEL; stellt fest ob lokaler Player gecheatet hat
     * @return
     */
    public boolean isPlayerCheating() { return playerCheating; }
}
