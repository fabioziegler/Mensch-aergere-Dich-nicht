package com.vintagetechnologies.menschaergeredichnicht.structure;

/**
 * Created by johannesholzl on 30.03.17.
 *
 * This class represents a spot on which a pl
 */

public abstract class Spot {
    private int x;
    private int y;

    private GamePiece gamePiece;

    public Spot(int x, int y){
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public GamePiece getGamePiece() {
        return gamePiece;
    }

    public void setGamePiece(GamePiece gamePiece) {
        if(this.getGamePiece() != null) {
            this.gamePiece.setSpot(null);
        }

        this.gamePiece = gamePiece;

        if(this.getGamePiece() != null) {
            this.gamePiece.setSpot(this);
        }
    }
}
