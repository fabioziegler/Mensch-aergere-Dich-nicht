package com.vintagetechnologies.menschaergeredichnicht.structure;

import android.util.Pair;

/**
 * Created by johannesholzl on 05.04.17.
 */

public class Player {


    //The players GamePieces
    private GamePiece[] pieces;

    //The Players Color
    private PlayerColor color;

    //The shown name
    private String name;

    //Saves if Player Cheated
    private Cheat schummeln;

    // An id which associates the player with the corresponding network connection
    private int networkId;

    private int uniqueId;

    // if the player has to skip in the next round TODO: implement logic
    private boolean hasToSkip;

    /**
     * creates a new Player Object with a specified PlayerColor and player name.
     * This constructor also creates the players GamePieces (fixed number: 4) and sets its other attributes.
     *
     * @param color
     * @param name
     */
    public Player(PlayerColor color, String name) {
        this.pieces = new GamePiece[4];

        for (int i = 0; i < 4; i++) {
            this.pieces[i] = new GamePiece(color);
        }

        this.schummeln = new Cheat();
        this.color = color;
        this.name = name;
    }

    /**
     * Empty constructor
     */
    public Player() {
        //Empty constructor for kryo deserialization.
    }

    /**
     * Getter
     *
     * @return schummeln
     */
    public Cheat getSchummeln() {
        return schummeln;
    }

    /**
     * Get player color
     *
     * @return color
     */
    public PlayerColor getColor() {
        return color;
    }

    /**
     * Getter
     *
     * @return name
     */
    public String getName() {
        return name;
    }

    /**
     * Getter
     *
     * @return pieces
     */
    public GamePiece[] getPieces() {
        return pieces;
    }

    /**
     * Setter
     *
     * @param pieces
     */
    public void setPieces(GamePiece[] pieces) {
        this.pieces = pieces;
    }

    /**
     * Returns true when the player doesn't have any pieces out of his/her house / start.
     *
     * @return
     */
    public boolean isAtStartingPosition() {
        for (GamePiece gp : this.getPieces()) {
            if (!(gp.getSpot() instanceof StartingSpot)) {
                return false;
            }
        }

        return true;
    }

    /**
     * Returns the GamePiece which is on the last Spot of the Boards Spot list.
     * This is important, because in this way GamePieces leave the house in a more elegant way
     * <p>
     * This method returns null if the Player doesn't have any GamePieces in his/her house.
     *
     * @return gamePiece
     */
    public GamePiece getStartingPiece() {

        int index = -1;
        GamePiece gpr = null;

        for (GamePiece gp : this.getPieces()) {
            if (gp.getSpot() instanceof StartingSpot) {
                Pair<GamePiece, Integer> p = getPieceFromBoard(gp, index);
                gpr = p != null ? p.first : gpr;
                index = p != null ? p.second : index;

            }
        }
        return gpr;
    }

    private Pair<GamePiece, Integer> getPieceFromBoard(GamePiece gp, int ind) {
        int index = ind;
        GamePiece gpr;
        for (int i = 0; i < Board.getBoard().length; i++) {
            if (Board.getBoard(i) == gp.getSpot() && index < i) {
                index = i;
                gpr = gp;
                return new Pair<>(gpr, index);
            }
        }
        return null;
    }

    public void setUniqueId(int id) {
        this.uniqueId = id;
    }

    public boolean hasToSkip() {
        return hasToSkip;
    }

    public void setHasToSkip(boolean hasToSkip) {
        this.hasToSkip = hasToSkip;
    }

    public int getUniqueId() {
        return uniqueId;
    }

    public int getNetworkId() {
        return networkId;
    }

    public void setNetworkId(int networkId) {
        this.networkId = networkId;
    }
}
