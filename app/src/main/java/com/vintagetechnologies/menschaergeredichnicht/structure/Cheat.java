package com.vintagetechnologies.menschaergeredichnicht.structure;

/**
 * Created by Demi on 11.04.2017.
 */

public class Cheat {

    //Merkt sich ob geschummelt wurde. Muss nach jedem Personen wechsel auf false gesetzt werden

    boolean playerCheating;
    public Cheat (boolean c){
        this.playerCheating = c;
    }

    public void setPlayerCheating(boolean c){
        this.playerCheating = c;
    }


    /**
     * Für WÜRFEL; stellt fest ob lokaler Player gecheatet hat
     * @return
     */
    public boolean isPlayerCheating() { return playerCheating; }


    /**
     * Für AUFDECKEN stellt fest ob aktuell spielender Spieler gecheatet hat
     * @return
     */
    public boolean hasRemotePlayerCheated(){
        // TODO: implement
        return false;
    }

}
