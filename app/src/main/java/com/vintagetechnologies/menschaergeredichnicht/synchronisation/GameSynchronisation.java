package com.vintagetechnologies.menschaergeredichnicht.synchronisation;

import com.google.gson.Gson;
import com.vintagetechnologies.menschaergeredichnicht.DataHolder;
import com.vintagetechnologies.menschaergeredichnicht.GameLogicHost;
import com.vintagetechnologies.menschaergeredichnicht.Impl.ActualGame;


/**
 * Created by Simon on 25.04.2017.
 */

public class GameSynchronisation {

    /**
     * Synchronisiert Gamedaten der clients
     */
    public static void synchronize(ActualGame game){
		//Game game = Game.getInstance();
        //String message = encode(game);
        sendToOtherDevices(game);
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

        GameLogicHost gameLogic = (GameLogicHost) DataHolder.getInstance().retrieve("GAMELOGIC");
		//gameLogic.sendToAllClientDevices(TAG_SYNCHRONIZE_GAME + MESSAGE_DELIMITER + json);
		gameLogic.sendToAllClientDevices(message);
    }

}
