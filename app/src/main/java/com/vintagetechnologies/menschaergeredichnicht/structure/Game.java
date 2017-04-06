package com.vintagetechnologies.menschaergeredichnicht.structure;

import java.util.ArrayList;

/**
 * Created by johannesholzl on 05.04.17.
 */

public class Game {

    int currentPlayer;
    Player players[];

    public Game(String... names) {
        players = new Player[names.length];


        for(int i = 0; i < names.length; i++){
            players[i] = new Player(PlayerColor.values()[i], names[i]);
        }
    }


    public void nextTurn() {

        Player cp = players[currentPlayer];

        currentPlayer = (currentPlayer+1)%players.length;

        



    }

}
