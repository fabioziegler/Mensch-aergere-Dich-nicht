package com.vintagetechnologies.menschaergeredichnicht.networking;

import com.esotericsoftware.kryonet.Connection;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Fabio on 08.04.17.
 *
 * A list of devices.
 */

public class DeviceList {


    private ArrayList<Device> devices;


    public DeviceList(){
        devices = new ArrayList<>(4);
    }

    /**
     * Get the host endpoint
     * @return The endpoint which is the host or null if there is no host
     */
    public Device getHost(){
        for (int i = 0; i < devices.size(); i++) {
            if(devices.get(i).isHost())
                return devices.get(i);
        }
        return null;
    }


	/**
	 * If the list contains the player with the specified name.
	 * @return The endpoint which is the host or null if there is no host
	 */
	public boolean contains(String playerName){
		for (int i = 0; i < devices.size(); i++) {
			if(devices.get(i).getName().equals(playerName))
				return true;
		}
		return false;
	}


    /**
     * Add an device
     * @param device
     */
    public void add(Device device){
        devices.add(device);
    }


    /**
     * Get the number of connected devices
     * @return The number of connected devices (excluding the host)
     */
    public int getCount(){
        return devices.size();
    }


    /**
     * Check if a ID belongs to the host of the game
     * @param playerID The id of the player
     * @return True if the ID belongs to the host
     */
    public boolean isHost(int playerID){
        Device dev = getHost();

        if(dev != null && dev.getId() == playerID)
            return true;

        return false;
    }


    /**
     * Get device by player name.
     * @param playerName A player name.
     * @return The device associated with the player name or null if there is no such device.
     */
    public Device getPlayer(String playerName){
        for (int i = 0; i < devices.size(); i++) {
            if(playerName.equals(devices.get(i).getName())){
                return devices.get(i);
            }
        }

        return null;
    }


    /**
     * Get device by ID.
     * @param playerID A player name.
     * @return The device associated with the player ID or null if there is no such device.
     */
    public Device getPlayer(int playerID){
        for (int i = 0; i < devices.size(); i++) {
            if(playerID == devices.get(i).getId()){
                return devices.get(i);
            }
        }

        return null;
    }


	/**
	 * Removes all devices.
	 */
	public void clear(){
		devices.clear();
	}


	/**
	 * Get device by connection
	 * @param connection
	 * @return
	 */
	public Device getDevice(Connection connection){
		if(connection == null)
			return null;
		return getPlayer(connection.getID());
	}

    /**
     * Get devices as an array list
     * @return
     */
    public List<Device> getList(){
        return devices;
    }


	/**
	 * Get a list of player names.
	 * @return
	 */
	public String[] getPlayerNames(){

		if(devices.isEmpty())
			return null;

		/*
		String[] names = new String[devices.size()];

		int j = 0;
		for (int i = 0; i < devices.size(); i++) {
			Device device = devices.get(i);
			if(!device.isHost()) {
				names[j++] = device.getName();
			}
		}

		return names;
		*/
		ArrayList<String> names = new ArrayList<>(devices.size());
		for(Device device : devices)
			names.add(device.getName());

		return names.toArray(new String[0]);
	}


	/**
	 * Remove device by player id.
	 * @param playerID
	 */
	public void remove(int playerID){
		for (int i = 0; i < devices.size(); i++) {
			if(devices.get(i).getId() == playerID){
				devices.remove(i);
				break;
			}
		}
	}


    /**
     * Remove a device.
     * @param connection Connection of the player to be removed.
     */
    public void remove(Connection connection) throws IllegalArgumentException {
        if (connection == null)
        	throw new IllegalArgumentException("Connection must not be null");

		int playerID = connection.getID();

		remove(playerID);
    }
}
