package com.vintagetechnologies.menschaergeredichnicht.structure;

import static com.vintagetechnologies.menschaergeredichnicht.structure.PlayerColor.RED;

/**
 * Created by johannesholzl on 30.03.17.
 */

public class GamePiece {
    private PlayerColor playerColor;
    private Spot spot;

    public GamePiece(PlayerColor playerColor) {
        this.playerColor = playerColor;
        this.returnToStart();
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
        if (this.getSpot() != null) {
            this.spot.nullGamePiece();
        }
        this.spot = spot;

        if (this.getSpot() != null) {
            this.spot.setGamePiece(this);
        }
    }

    public void nullSpot() {
        this.spot = null;
    }

    public void moveTo(Spot targetSpot) {  //Voraussetzung: check returned not null
        if(targetSpot != null){
            if(targetSpot.getGamePiece() == null){
               this.setSpot(targetSpot);
            }else{
                if(targetSpot.getGamePiece().getPlayerColor() == this.getPlayerColor()){
                    //ungültig
                }else{
                    targetSpot.getGamePiece().returnToStart();
                    this.setSpot(targetSpot);
                }
            }
        }
    }

    public void returnToStart(){
        //...

        this.setSpot(Board.getStartingSpot(this.getPlayerColor()));



    }
}
