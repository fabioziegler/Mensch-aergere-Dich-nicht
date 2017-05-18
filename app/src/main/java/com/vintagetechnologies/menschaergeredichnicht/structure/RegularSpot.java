package com.vintagetechnologies.menschaergeredichnicht.structure;

/**
 * Created by johannesholzl on 30.03.17.
 */

public class RegularSpot extends Spot {
    private RegularSpot nextSpot;
    private EndSpot endSpot;


    /**
     *
     * @param x
     * @param y
     * @param nextSpot
     */
    public RegularSpot(int x, int y, RegularSpot nextSpot) {
        super(x, y);
        this.nextSpot = nextSpot;
    }

    public RegularSpot(){}

    /**
     * Returns the Spot to which the current Spot is pointing to.
     * @return
     */
    public RegularSpot getNextSpot() {
        return nextSpot;
    }

    /**
     * Sets the next Spot.
     *
     * @param nextSpot
     */
    public void setNextSpot(RegularSpot nextSpot) {
        this.nextSpot = nextSpot;
    }

    /**
     * Gets the EndSpot
     * is nnull if there is no junction
     * returns the next EndSpot if there is a junction.
     *
     * @return
     */
    public EndSpot getEndSpot() {
        return endSpot;
    }

    /**
     * Sets the EndSpot. null by default
     * @param endSpot
     */
    public void setEndSpot(EndSpot endSpot) {
        this.endSpot = endSpot;
    }
}
