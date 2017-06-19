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

    // Für den Würfel pro schummeln
    boolean playerCheating = false;

    // Pro Spielzug. Pro Runde für aufdecken
    boolean cheated = false;

    public Cheat (){
    }

    //Dient nur dem Würfel auf 6 bei Schummeln
    public void setPlayerCheating(boolean c){
        this.playerCheating = c;
    }

    /**
     * Für WÜRFEL; stellt fest ob lokaler Player gecheatet hat
     * @return
     */
    public boolean isPlayerCheating() { return playerCheating; }


    /**
     * Für das Aufdecken... stellt fest ob wärend des gesamten zuges geschummelt wurde.
     * @param cheated
     */

    public void setCheated(boolean cheated) {
        this.cheated = cheated;
    }

    public boolean hasCheated() {
        return cheated;
    }

	/**
	 * wird seperat aufgerufen in Spieleroberfläche wenn geschummelt wird und vom Host wenn neue Runde um wieder auf false zu setzen.
	 * -> Dient nur der Schummeln-Aufdecken funktion.
	 */
	public void informHost(){

		if(ActualGame.getInstance().isLocalGame())
			return;

        //* Falsch! Wenn spieler cheat status auf falsch gesetzt wird soll der Host auch informiert werden !
		//if(!cheated)
		//	return;

        // host nachricht schicken
        GameLogic gameLogic = (GameLogic) DataHolder.getInstance().retrieve(Network.DATAHOLDER_GAMELOGIC);
        boolean isHost = gameLogic.isHost();

        if (!isHost){	// is client
			((GameLogicClient) gameLogic).sendToHost(TAG_PLAYER_HAS_CHEATED + MESSAGE_DELIMITER + String.valueOf(cheated));
        }
    }


}
