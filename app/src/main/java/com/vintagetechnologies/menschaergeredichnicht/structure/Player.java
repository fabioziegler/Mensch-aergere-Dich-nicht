package com.vintagetechnologies.menschaergeredichnicht.structure;

/**
 * Created by johannesholzl on 05.04.17.
 */

public class Player {

    GamePiece pieces[];
    PlayerColor color;
    String name;
    Cheat Schummeln;
    boolean aktive;

    public Player(PlayerColor color, String name) {
        this.pieces = new GamePiece[4];

        for(int i = 0; i<4;i++){
            this.pieces[i] = new GamePiece(color);
        }

        this.aktive = false; //sollte dann wenn der Spieler am zug ist auf true gesetz werden.
        this.Schummeln = new Cheat();
        this.color = color;
        this.name = name;
    }

    public Cheat getSchummeln() {
        return Schummeln;
    }

    public boolean isAktive() {
        return aktive;
    }

    public void setAktive(boolean aktive) {
        this.aktive = aktive;
    }

    public PlayerColor getColor() {
        return color;
    }

    public String getName() {
        return name;
    }

    public GamePiece[] getPieces() {
        return pieces;
    }

    public void setPieces(GamePiece[] pieces) {
        this.pieces = pieces;
    }


    public boolean isAtStartingPosition(){
        for (GamePiece gp: this.getPieces()){
            if(!(gp.getSpot() instanceof StartingSpot)){
                return false;
            }
        }

        return true;
    }

    public GamePiece getStartingPiece(){

        int index = -1;
        GamePiece gpr = null;

        for (GamePiece gp: this.getPieces()){
            if(gp.getSpot() instanceof StartingSpot){
                for(int i = 0; i<Board.getBoard().length; i++){
                    if(Board.getBoard(i) == gp.getSpot() && index < i){
                        index = i;
                        gpr = gp;
                        break;
                    }
                }
            }
        }
        return  gpr;
    }
}
