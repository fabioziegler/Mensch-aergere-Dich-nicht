package com.vintagetechnologies.menschaergeredichnicht.synchronisation;

import android.util.Log;

import com.google.gson.Gson;
import com.vintagetechnologies.menschaergeredichnicht.DataHolder;
import com.vintagetechnologies.menschaergeredichnicht.GameLogic;
import com.vintagetechnologies.menschaergeredichnicht.GameLogicClient;
import com.vintagetechnologies.menschaergeredichnicht.GameLogicHost;
import com.vintagetechnologies.menschaergeredichnicht.Impl.ActualGame;
import com.vintagetechnologies.menschaergeredichnicht.GameLogicHost;
import com.vintagetechnologies.menschaergeredichnicht.Impl.ActualGame;
import com.vintagetechnologies.menschaergeredichnicht.networking.Network;
import com.vintagetechnologies.menschaergeredichnicht.structure.Player;


/**
 * Created by Simon on 25.04.2017.
 */

public class GameSynchronisation {

    /**
     * Synchronizes game data from the host with clients.
     */
    public static void synchronize(){

        Player[] players = ActualGame.getInstance().getGameLogic().getPlayers();

        for (Player player : players) {
            sendToOtherDevices(player);
        }
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
     * Sendet Gamedaten an clients in String-Format
     * @param message
     */
    private static void sendToOtherDevices(Object message){

		// TODO: nacheinander nur die Klassen schicken die geändet wurden! Danach z.B. Signal schicken "neue Runde beginnt" oder so...
		// TODO: Methode in dieser Klasse die, die empfangenen Klassen ausliest und die Game Klasse entsprechend aktualisiert.


		//Log.i("SYNC", "Sending sync to clients...");

        GameLogic gameLogic = (GameLogic) DataHolder.getInstance().retrieve(Network.DATAHOLDER_GAMELOGIC);

		if(gameLogic.isHost()) {
			((GameLogicHost)gameLogic).sendToAllClientDevices(message);
		} else {
			((GameLogicClient)gameLogic).sendToHost(message);
		}
    }

}
