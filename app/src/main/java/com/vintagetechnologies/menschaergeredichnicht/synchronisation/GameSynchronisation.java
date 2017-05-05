package com.vintagetechnologies.menschaergeredichnicht.synchronisation;

import com.google.gson.Gson;
import com.vintagetechnologies.menschaergeredichnicht.DataHolder;
import com.vintagetechnologies.menschaergeredichnicht.GameLogicHost;
import com.vintagetechnologies.menschaergeredichnicht.structure.Game;

import static com.vintagetechnologies.menschaergeredichnicht.networking.Network.MESSAGE_DELIMITER;
import static com.vintagetechnologies.menschaergeredichnicht.networking.Network.TAG_SYNCHRONIZE_GAME;

/**
 * Created by Simon on 25.04.2017.
 */

public class GameSynchronisation {

    /**
     * Synchronisiert Gamedaten der clients
     */
    public static void synchronize(){
		Game game = Game.getInstance();
        String message = encode(game);
        sendToOtherDevices(message);
    }

    /**
     * Umwandeln von Game Objekt in String
     * @param game
     * @return
     */
    private static String encode(Game game){
        Gson gson = new Gson();

        String json = gson.toJson(game);

        return json;
    }

    /**
     * Rückumwandlung von String in Game-Objekt
     * @param fromJson
     * @return
     */
    public static Game decode(String fromJson){
        Gson gson = new Gson();

        Game game = gson.fromJson(fromJson, Game.class);

        return game;
    }

    /**
     * Sendet Gamedaten an clients in String-Format
     * @param json
     */
    private static void sendToOtherDevices(String json){
        GameLogicHost gameLogic = (GameLogicHost) DataHolder.getInstance().retrieve("GAMELOGIC");
		gameLogic.sendToAllClientDevices(TAG_SYNCHRONIZE_GAME + MESSAGE_DELIMITER + json);
    }

}
