package com.vintagetechnologies.menschaergeredichnicht.synchronisation;

import com.google.gson.Gson;
import com.vintagetechnologies.menschaergeredichnicht.GameLogic;
import com.vintagetechnologies.menschaergeredichnicht.structure.Game;
import com.vintagetechnologies.menschaergeredichnicht.structure.Player;

/**
 * Created by Simon on 25.04.2017.
 */

public class GameSynchronisation {

    private static final String TAG = "sync";

    /**
     * Send game data to client
     * @param gameLogic
     * @param game
     */
    public static void synchronize(GameLogic gameLogic, Game game){
        String message = encode(game);
        sendToOtherDevices(gameLogic, message);
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

    private static Game decode(String fromJson){
        Gson gson = new Gson();

        Game game = gson.fromJson(fromJson, Game.class);

        return game;
    }

    private static void sendToOtherDevices(GameLogic gameLogic, String json){
        gameLogic.sendMessage(TAG, json);
    }

}
