package com.vintagetechnologies.menschaergeredichnicht.synchronisation;

import android.widget.Toast;

import com.google.gson.Gson;
import com.vintagetechnologies.menschaergeredichnicht.DataHolder;
import com.vintagetechnologies.menschaergeredichnicht.GameLogic;
import com.vintagetechnologies.menschaergeredichnicht.GameLogicClient;
import com.vintagetechnologies.menschaergeredichnicht.GameLogicHost;
import com.vintagetechnologies.menschaergeredichnicht.implementation.ActualGame;
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
		/* send player objects */
        Player[] players = ActualGame.getInstance().getGameLogic().getPlayers();

        for (Player player : players)
            send(player);
    }


	/**
	 * Informs all players who's turn it is.
	 */
	public static void nextRound(){

		/* send current player name */
		String currentPlayerName = ActualGame.getInstance().getGameLogic().getCurrentPlayer().getName();

		send(Network.TAG_CURRENT_PLAYER + Network.MESSAGE_DELIMITER + String.valueOf(currentPlayerName));
	}



	public static void sendToast(String message){

		GameLogic gameLogicHost = DataHolder.getInstance().retrieve(Network.DATAHOLDER_GAMELOGIC, GameLogicHost.class);

		// send to clients
		send(Network.TAG_TOAST + Network.MESSAGE_DELIMITER + message);

		// finally, show toast on host device too:
		Toast.makeText(gameLogicHost.getActivity().getApplicationContext(), message, Toast.LENGTH_LONG);
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


	/**
	 * Umwandeln von ActualGame Objekt in String
	 * @param game
	 * @return
	 */
	private static String encode(ActualGame game){
		Gson gson = new Gson();

		return gson.toJson(game);
	}


	/**
	 * Rückumwandlung von String in ActualGame-Objekt
	 * @param fromJson
	 * @return
	 */
	private static ActualGame decode(String fromJson){
		Gson gson = new Gson();

		return gson.fromJson(fromJson, ActualGame.class);
	}
}
