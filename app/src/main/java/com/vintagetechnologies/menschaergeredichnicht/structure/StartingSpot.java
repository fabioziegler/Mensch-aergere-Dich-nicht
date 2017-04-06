package com.vintagetechnologies.menschaergeredichnicht.structure;

/**
 * Created by johannesholzl on 30.03.17.
 */

public class StartingSpot extends Spot implements Colorful{

    private PlayerColor color;
    private Spot entrance;

    public StartingSpot(int x, int y, PlayerColor color, Spot entrance) {
        super(x, y);
        this.color = color;
        this.entrance = entrance;

    }

    public Spot getEntrance() {
        return entrance;
    }

    public void setEntrance(Spot entrance) {
        this.entrance = entrance;
    }

    public PlayerColor getColor() {
        return color;
    }

    public void setColor(PlayerColor color) {
        this.color = color;
    }
}
