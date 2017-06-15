package com.vintagetechnologies.menschaergeredichnicht.networking;

/**
 * Created by Fabio on 08.04.17.
 *
 * Devices are players with name, id and they can be host of a game.
 */

public class Device {

    private int id;	// -1 if not set
    private String name;
    private boolean isHost;

	private int connectionId;

    public Device(int id, String name, boolean isHost){
        this.id = id;
        this.name = name;
        this.isHost = isHost;
    }

	public Device(int id, boolean isHost){
		this.id = id;
		this.name = "name not set";
		this.isHost = isHost;
		this.connectionId = id;
	}

	public Device(String name, boolean isHost){
		this.name = name;
		this.isHost = isHost;
	}


	/**
	 * Get the network ID.
	 * @return
	 */
    public int getId() {
        return id;
    }

    public void setId(int id) {
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

	public int getConnectionId() {
		return connectionId;
	}

	public void setConnectionId(int connectionId) {
		this.connectionId = connectionId;
	}
}
