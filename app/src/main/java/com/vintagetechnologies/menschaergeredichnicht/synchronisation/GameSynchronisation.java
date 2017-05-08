package com.vintagetechnologies.menschaergeredichnicht.synchronisation;

import com.google.gson.Gson;
import com.vintagetechnologies.menschaergeredichnicht.DataHolder;
import com.vintagetechnologies.menschaergeredichnicht.GameLogic;
import com.vintagetechnologies.menschaergeredichnicht.Impl.ActualGame;

import static com.vintagetechnologies.menschaergeredichnicht.networking.NetworkTags.TAG_SYNCHRONIZE_GAME;

/**
 * Created by Simon on 25.04.2017.
 */

public class GameSynchronisation {

    /**
     * Synchronisiert Gamedaten der clients
     * @param game
     */
    public static void synchronize(ActualGame game){
        String message = encode(game);
        sendToOtherDevices(message);
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
     * @param json
     */
    private static void sendToOtherDevices(String json){
        GameLogic gameLogic = (GameLogic) DataHolder.getInstance().retrieve("GAMELOGIC");
        gameLogic.sendToClientDevices(TAG_SYNCHRONIZE_GAME, json);
    }

}
