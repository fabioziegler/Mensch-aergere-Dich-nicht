package com.vintagetechnologies.menschaergeredichnicht.structure;

/**
 * Created by johannesholzl on 05.04.17.
 */

public class Player {

    GamePiece pieces[];
    PlayerColor color;
    String name;

    public Player(PlayerColor color, String name) {
        this.pieces = new GamePiece[4];

        for(int i = 0; i<4;i++){
            this.pieces[i] = new GamePiece(color);
        }


        this.color = color;
        this.name = name;
    }

    public PlayerColor getColor() {
        return color;
    }

    public String getName() {
        return name;
    }


}
