package com.vintagetechnologies.menschaergeredichnicht.structure;

/**
 * Created by johannesholzl on 30.03.17.
 */

public class EndSpot extends Spot {

    private PlayerColor color;
    private EndSpot nextEndSpot;

    public EndSpot(int x, int y, PlayerColor color, EndSpot nextEndSpot) {
        super(x, y);
        this.color = color;
        this.nextEndSpot = nextEndSpot;
    }

    public PlayerColor getColor() {
        return color;
    }

    public void setColor(PlayerColor color) {
        this.color = color;
    }

    public EndSpot getNextEndSpot() {
        return nextEndSpot;
    }

    public void setNextEndSpot(EndSpot nextEndSpot) {
        this.nextEndSpot = nextEndSpot;
    }
}
