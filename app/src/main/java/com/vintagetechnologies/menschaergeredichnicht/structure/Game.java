package com.vintagetechnologies.menschaergeredichnicht.structure;

import java.util.ArrayList;

/**
 * Created by johannesholzl on 05.04.17.
 */

public class Game {

    int currentPlayer;
    Player players[];
    Board board;

    public Game(String... names) {
        players = new Player[names.length];
        board = Board.get();

        for(int i = 0; i < names.length; i++){
            PlayerColor cColor = PlayerColor.values()[i];
            players[i] = new Player(cColor, names[i]);

            int c = 0;

            for(Spot spot : board.getBoard()){
                if(spot instanceof StartingSpot){
                    StartingSpot sSpot  = (StartingSpot)spot;
                    if(sSpot.getColor() == cColor){
                        players[i].getPieces()[c].setSpot(spot);
                        c++;
                    }
                }
            }


        }



    }


    public void nextTurn() {

        Player cp = players[currentPlayer];

        currentPlayer = (currentPlayer+1)%players.length;





    }

}
