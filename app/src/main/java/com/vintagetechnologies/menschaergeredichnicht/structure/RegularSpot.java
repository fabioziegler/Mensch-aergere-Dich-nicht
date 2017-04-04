package com.vintagetechnologies.menschaergeredichnicht.structure;

/**
 * Created by johannesholzl on 30.03.17.
 */

public class RegularSpot extends Spot {
    private RegularSpot nextSpot;
    private EndSpot endSpot;

    public RegularSpot(int x, int y, RegularSpot nextSpot) {
        super(x, y);
        this.nextSpot = nextSpot;
    }

    public RegularSpot getNextSpot() {
        return nextSpot;
    }

    public void setNextSpot(RegularSpot nextSpot) {
        this.nextSpot = nextSpot;
    }

    public EndSpot getEndSpot() {
        return endSpot;
    }

    public void setEndSpot(EndSpot endSpot) {
        this.endSpot = endSpot;
    }
}
