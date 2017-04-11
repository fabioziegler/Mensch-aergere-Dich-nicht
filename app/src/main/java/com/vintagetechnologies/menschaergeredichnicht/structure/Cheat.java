package com.vintagetechnologies.menschaergeredichnicht.structure;

/**
 * Created by Demi on 11.04.2017.
 */

public class Cheat {

    //Merkt sich ob geschummelt wurde. Muss nach jedem Personen wechsel auf false gesetzt werden

    boolean cheat;
    public Cheat (boolean c){
        this.cheat = c;
    }

    public void setCheat(boolean c){
        this.cheat = c;
    }
    public boolean getCheat() { return cheat; }

    //Man brauch auch einen Speicher für Accusing (hier?!)

}
