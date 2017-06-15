package com.vintagetechnologies.menschaergeredichnicht.synchronisation;

import android.text.Html;
import android.widget.Toast;

import com.google.gson.Gson;
import com.vintagetechnologies.menschaergeredichnicht.DataHolder;
import com.vintagetechnologies.menschaergeredichnicht.GameLogic;
import com.vintagetechnologies.menschaergeredichnicht.GameLogicClient;
import com.vintagetechnologies.menschaergeredichnicht.GameLogicHost;
import com.vintagetechnologies.menschaergeredichnicht.GameSettings;
import com.vintagetechnologies.menschaergeredichnicht.Spieloberflaeche;
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

		// reset cheated
		for(Player player : ActualGame.getInstance().getGameLogic().getPlayers())
			player.getSchummeln().setCheated(false);

		/* enable/disable controls */
		GameSettings gameSettings = DataHolder.getInstance().retrieve(Network.DATAHOLDER_GAMESETTINGS, GameSettings.class);
		GameLogic gameLogic = DataHolder.getInstance().retrieve(Network.DATAHOLDER_GAMELOGIC, GameLogic.class);

		Spieloberflaeche activity = (Spieloberflaeche) gameLogic.getActivity();
		if (currentPlayerName.equals(gameSettings.getPlayerName())) {
			activity.setDiceEnabled(true); //Würfeln
			activity.setRevealEnabled(false);  //Aufdecken
			activity.setSensorOn(true); //Schummeln und Würfeln durch Schütteln
		} else {
			activity.setDiceEnabled(false);
			activity.setRevealEnabled(true);
			activity.setSensorOn(false);
		}
	}


	public static void sendToast(String message){

		GameLogic gameLogicHost = DataHolder.getInstance().retrieve(Network.DATAHOLDER_GAMELOGIC, GameLogicHost.class);

		// send to clients
		send(Network.TAG_TOAST + Network.MESSAGE_DELIMITER + message);

		// finally, show toast on host device too:
		Toast.makeText(gameLogicHost.getActivity().getApplicationContext(), message, Toast.LENGTH_LONG).show();
	}


    /**
     * Sendet Gamedaten.
     * @param message
     */
    public static void send(Object message){

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
