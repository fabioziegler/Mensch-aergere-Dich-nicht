package com.vintagetechnologies.menschaergeredichnicht.synchronisation;

import com.google.gson.Gson;
import com.vintagetechnologies.menschaergeredichnicht.DataHolder;
import com.vintagetechnologies.menschaergeredichnicht.GameLogic;
import com.vintagetechnologies.menschaergeredichnicht.GameLogicClient;
import com.vintagetechnologies.menschaergeredichnicht.GameLogicHost;
import com.vintagetechnologies.menschaergeredichnicht.GameSettings;
import com.vintagetechnologies.menschaergeredichnicht.Impl.ActualGame;
import com.vintagetechnologies.menschaergeredichnicht.networking.Device;
import com.vintagetechnologies.menschaergeredichnicht.networking.Network;
import com.vintagetechnologies.menschaergeredichnicht.structure.Game;
import com.vintagetechnologies.menschaergeredichnicht.structure.Player;


/**
 * Created by Simon on 25.04.2017.
 */

public class GameSynchronisation {

    /**
     * Synchronizes game data from the host with clients.
     */
    public static void synchronize(){

		com.vintagetechnologies.menschaergeredichnicht.structure.GameLogic gameLogic = ActualGame.getInstance().getGameLogic();

		/* send player objects */
        Player[] players = gameLogic.getPlayers();

        for (Player player : players) {
            send(player);
        }

        /* send current player id (=network id) */
        int currentPlayerNetworkId = gameLogic.getCurrentPlayer().getUniqueId();
        send(Network.TAG_CURRENT_PLAYER + Network.MESSAGE_DELIMITER + String.valueOf(currentPlayerNetworkId));
    }


    /**
     * Umwandeln von ActualGame Objekt in String
     * @param game
     * @return
     */
    private static String encode(ActualGame game){
        Gson gson = new Gson();

        String json = gson.toJson(game);

        return json;
    }


    /**
     * Rückumwandlung von String in ActualGame-Objekt
     * @param fromJson
     * @return
     */
    public static ActualGame decode(String fromJson){
        Gson gson = new Gson();

        ActualGame game = gson.fromJson(fromJson, ActualGame.class);

        return game;
    }


    /**
     * Sendet Gamedaten.
     * @param message
     */
    private static void send(Object message){

		// TODO: nacheinander nur die Klassen schicken die geändet wurden! Danach z.B. Signal schicken "neue Runde beginnt" oder so...
		// TODO: Methode in dieser Klasse die, die empfangenen Klassen ausliest und die Game Klasse entsprechend aktualisiert.

        GameLogic gameLogic = (GameLogic) DataHolder.getInstance().retrieve(Network.DATAHOLDER_GAMELOGIC);

		if(gameLogic.isHost()) {
			((GameLogicHost)gameLogic).sendToAllClientDevices(message);
		} else {	// client needed??
			((GameLogicClient)gameLogic).sendToHost(message);
		}
    }

}
