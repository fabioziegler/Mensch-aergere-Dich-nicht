package com.vintagetechnologies.menschaergeredichnicht.structure;

/**
 * Created by johannesholzl on 05.04.17.
 */

public class Player {
  
  
    //The players GamePieces
    private GamePiece pieces[];

    //The Players Color
    private PlayerColor color;

    //The shown name
    private String name;

    //Saves if Player Cheated
    private Cheat Schummeln;

    //nicht mehr benötigt toDo Löschen
    private boolean aktive;


    /**
     * creates a new Player Object with a specified PlayerColor and player name.
     * This constructor also creates the players GamePieces (fixed number: 4) and sets its other attributes.
     *
     * @param color
     * @param name
     */
    public Player(PlayerColor color, String name) {
        this.pieces = new GamePiece[4];

        for(int i = 0; i<4;i++){
            this.pieces[i] = new GamePiece(color);
        }

       // this.aktive = false; //sollte dann wenn der Spieler am zug ist auf true gesetz werden ->current Spieler
        this.Schummeln = new Cheat();
        this.color = color;
        this.name = name;
    }

    /**
     * Getter
     * @return Schummeln
     */
    public Cheat getSchummeln() {
        return Schummeln;
    }

    //Wird nicht mehr benötigt ToDo Löschen
    public boolean isAktive() {
        return aktive;
    }
    public void setAktive(boolean aktive) {
        this.aktive = aktive;
    }
  
    /**
     * Get player color
     * @return color
     */
    public PlayerColor getColor() {
        return color;
    }

    /**
     * Getter
     * @return name
     */
    public String getName() {
        return name;
    }

    /**
     * Getter
     * @return pieces
     */
    public GamePiece[] getPieces() {
        return pieces;
    }

    /**
     * Setter
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
    public boolean isAtStartingPosition(){
        for (GamePiece gp: this.getPieces()){
            if(!(gp.getSpot() instanceof StartingSpot)){
                return false;
            }
        }

        return true;
    }

    /**
     * Returns the GamePiece which is on the last Spot of the Boards Spot list.
     * This is important, because in this way GamePieces leave the house in a more elegant way
     *
     * This method returns null if the Player doesn't have any GamePieces in his/her house.
     *
     * @return gamePiece
     */
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
