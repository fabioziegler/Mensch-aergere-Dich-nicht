package com.vintagetechnologies.menschaergeredichnicht.networking;

/**
 * Created by Fabio on 08.04.17.
 *
 * Devices are players with name, id and they can be host of a game.
 */

public class Device {

    private String id;
    private String name;
    private boolean isHost;

    public Device(String id, String name, boolean isHost){
        this.id = id;
        this.name = name;
        this.isHost = isHost;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isHost() {
        return isHost;
    }

    public void setHost(boolean host) {
        isHost = host;
    }
}
