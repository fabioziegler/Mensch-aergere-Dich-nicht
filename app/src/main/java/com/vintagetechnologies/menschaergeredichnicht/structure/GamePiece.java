package com.vintagetechnologies.menschaergeredichnicht.structure;

/**
 * Created by johannesholzl on 30.03.17.
 */

public class GamePiece {
    private PlayerColor playerColor;

    private Spot spot;

    public GamePiece(PlayerColor playerColor) {
        this.playerColor = playerColor;
    }

    public PlayerColor getPlayerColor() {
        return playerColor;
    }

    public void setPlayerColor(PlayerColor playerColor) {
        this.playerColor = playerColor;
    }

    public Spot getSpot() {
        return spot;
    }

    public void setSpot(Spot spot) {
        if(this.getSpot() != null) {
            this.spot.setGamePiece(null);
        }
        this.spot = spot;

        if(this.getSpot() != null) {
            this.spot.setGamePiece(this);
        }
    }
}
