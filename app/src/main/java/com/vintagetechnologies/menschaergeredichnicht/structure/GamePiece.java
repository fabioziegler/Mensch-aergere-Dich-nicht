package com.vintagetechnologies.menschaergeredichnicht.structure;

import static com.vintagetechnologies.menschaergeredichnicht.structure.PlayerColor.RED;

/**
 * Created by johannesholzl on 30.03.17.
 */

public class GamePiece {

    //The GamePieces Color
    private PlayerColor playerColor;

    //Spot, on which the GamePiece is located.
    private Spot spot;


    /**
     * Sets the GamePieces PlayerColor and locates it on a StartingSpot
     * @param playerColor
     */
    public GamePiece(PlayerColor playerColor) {
        this.playerColor = playerColor;
        this.returnToStart();
    }

	/**
	 * Empty constructor for kryo deserialization.
	 */
	public GamePiece(){}

    /**
     *
     * @return playerColor
     */
    public PlayerColor getPlayerColor() {
        return playerColor;
    }

    /**
     *
     * @param playerColor
     */
    public void setPlayerColor(PlayerColor playerColor) {
        this.playerColor = playerColor;
    }

    /**
     *
     * @return spot
     */
    public Spot getSpot() {
        return spot;
    }


    /**
     * Sets Spot on which the GamePiece is located.
     * This methods sets the Spots GamePiece to null and sets the new  Spots GamePiece to the current GamePiece.
     * @param spot
     */
    public void setSpot(Spot spot) {
        if (this.getSpot() != null) {
            this.spot.nullGamePiece();
        }
        this.spot = spot;

        if (this.getSpot() != null) {
            this.spot.forceSetGamePiece(this);
        }
    }


    /**
     * Sets the GamePieces Spot to null
     */
    public void nullSpot() {
        this.spot = null;
    }


    /**
     * Sets the GamePieces Spot without syncing the changes with the Spots
     * @param spot
     */
    public void forceSetSpot(Spot spot){
        this.spot = spot;
    }


    /**
     * Moves the GamePiece to a specific Spot.
     * @param targetSpot
     */
    public boolean moveTo(Spot targetSpot) {  //Voraussetzung: check returned not null
        if(targetSpot != null){
            if(targetSpot.getGamePiece() == null){
               this.setSpot(targetSpot);
                return true;
            }else{
                if(targetSpot.getGamePiece().getPlayerColor() == this.getPlayerColor()){
                    return false;
                }else{
                    targetSpot.getGamePiece().returnToStart();
                    this.setSpot(targetSpot);
                    return true;
                }
            }
        }else{
            return false;
        }
    }

    /**
     * Returns the GamePiece to a StartingSpot
     */
    public void returnToStart(){
        this.setSpot(Board.getStartingSpot(this.getPlayerColor()));
    }
}
