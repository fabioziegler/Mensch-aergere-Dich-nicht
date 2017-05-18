package com.vintagetechnologies.menschaergeredichnicht.structure;

/**
 * Created by johannesholzl on 30.03.17.
 */

public class StartingSpot extends Spot implements Colorful{

    private PlayerColor color;
    private Spot entrance;


    /**
     * Constructor of StartingSpot
     *
     * x/y: super() of Spot is called
     * color: Spot is Colorful
     * entrance: Spot on which the GamePiece moves when a SIX is rolled.
     *
     *
     * @param x
     * @param y
     * @param color
     * @param entrance
     */
    public StartingSpot(int x, int y, PlayerColor color, Spot entrance) {
        super(x, y);
        this.color = color;
        this.entrance = entrance;

    }

    public StartingSpot(){}

    /**
     * Gets the Entrance to the board.
     *
     * @return
     */
    public Spot getEntrance() {
        return entrance;
    }

    /**
     * Sets the Entrance of the StartingSpot
     *
     * @param entrance
     */
    public void setEntrance(Spot entrance) {
        this.entrance = entrance;
    }

    /**
     * Gets the StartingSpots PlayerColor.
     *
     * @return
     */
    public PlayerColor getColor() {
        return color;
    }

    /**
     * Sets the StartingSpots PlayerColor.
     *
     * @param color
     */
    public void setColor(PlayerColor color) {
        this.color = color;
    }
}
