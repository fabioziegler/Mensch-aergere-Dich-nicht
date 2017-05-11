package com.vintagetechnologies.menschaergeredichnicht.structure;

import com.vintagetechnologies.menschaergeredichnicht.structure.GameLogic;
import com.vintagetechnologies.menschaergeredichnicht.structure.Player;

public abstract class Game {

    public abstract void init(String ... names);

    public abstract void play() throws IllegalAccessException;

    public abstract void beginningAction(Player p);

    public abstract void whomsTurn(Player p);

    public abstract void waitForMovePiece();

    public abstract void refreshView();

    public abstract GameLogic getGameLogic();

}
