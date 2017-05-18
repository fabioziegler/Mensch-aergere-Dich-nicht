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

    /**
     * Spot has to have x and y coordinates.
     * 0 / 0 is the Spot on the upper left corner (red StartingSpot)
     * @param x
     * @param y
     */
    public Spot(int x, int y){
        this.x = x;
        this.y = y;
    }

    public Spot(){}

    /**
     *
     * @return
     */
    public int getX() {
        return x;
    }

    /**
     *
     * @param x
     */
    public void setX(int x) {
        this.x = x;
    }

    /**
     *
     * @return
     */
    public int getY() {
        return y;
    }

    /**
     *
     * @param y
     */
    public void setY(int y) {
        this.y = y;
    }

    /**
     *
     * @return
     */
    public GamePiece getGamePiece() {
        return gamePiece;
    }


    /**
     * Similar to GamePieces setSpot() method.
     *
     * @param gamePiece
     */
    public void setGamePiece(GamePiece gamePiece) {
        if(this.getGamePiece() != null) {
            this.gamePiece.nullSpot();
        }

        this.gamePiece = gamePiece;

        if(this.getGamePiece() != null) {
            this.gamePiece.forceSetSpot(this);
        }
    }

    /**
     * Similar to GamePieces forceSetSpot() method.
     *
     * @param gamePiece
     */
    public void forceSetGamePiece(GamePiece gamePiece){
        this.gamePiece = gamePiece;
    }


    /**
     * Sets the GamePiece to null (the Spot is not occupied)
     */
    public void nullGamePiece(){
        this.gamePiece = null;
    }
}
