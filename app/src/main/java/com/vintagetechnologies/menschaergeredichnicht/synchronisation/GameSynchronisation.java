package com.vintagetechnologies.menschaergeredichnicht.synchronisation;

import com.google.gson.Gson;
import com.vintagetechnologies.menschaergeredichnicht.GameLogic;
import com.vintagetechnologies.menschaergeredichnicht.structure.Game;
import com.vintagetechnologies.menschaergeredichnicht.structure.Player;

/**
 * Created by Simon on 25.04.2017.
 */

public class GameSynchronisation {

    public static void synchronize(GameLogic gameLogic, Game game){

    }
    private static String encode(Game game){
        Gson gson = new Gson();

        String json = gson.toJson(game);

        return json;
    }

    private static Game decode(String fromJson){

    }

}