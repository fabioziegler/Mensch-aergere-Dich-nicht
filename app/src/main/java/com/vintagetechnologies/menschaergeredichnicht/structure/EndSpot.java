package com.vintagetechnologies.menschaergeredichnicht.structure;

/**
 * Created by johannesholzl on 30.03.17.
 */

public class EndSpot extends Spot implements Colorful {

    private PlayerColor color;
    private EndSpot nextEndSpot;


    /**
     * Constructor of EndSpot
     * <p>
     * x/y: super() method of Spot is called
     * color: EndSpot is Colorful
     * nextEndSpot: the EndSpot to move next. Is null on the last EndSpot
     *
     * @param x
     * @param y
     * @param color
     * @param nextEndSpot
     */
    public EndSpot(int x, int y, PlayerColor color, EndSpot nextEndSpot) {
        super(x, y);
        this.color = color;
        this.nextEndSpot = nextEndSpot;
    }

    /**
     *
     */
    public EndSpot() {
        //maybe it's important
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
