package com.vintagetechnologies.menschaergeredichnicht.structure;

/**
 * Created by johannesholzl on 06.05.17.
 */

public abstract class Game {
    public abstract void init(String ... names);

    public abstract void play() throws IllegalAccessException;

    public abstract void beginningAction(Player p);

    public abstract void whomsTurn(Player p);

    public abstract void waitForMovePiece();

    public abstract void refreshView();

    public abstract GameLogic getGameLogic();

}
