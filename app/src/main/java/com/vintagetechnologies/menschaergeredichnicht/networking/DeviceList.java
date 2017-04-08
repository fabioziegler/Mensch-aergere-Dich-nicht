package com.vintagetechnologies.menschaergeredichnicht.networking;

import java.util.ArrayList;

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
     * Add an device
     * @param device
     */
    public void addDevice(Device device){
        devices.add(device);
    }

    /**
     * Get the number of connected devices
     * @return The number of connected devices (excluding the host)
     */
    public int getCountConnectedDevices(){
        return devices.size();
    }

    /**
     * Check if a ID belongs to the host of the game
     * @param playerID The id of the player
     * @return True if the ID belongs to the host
     */
    public boolean isHost(String playerID){
        Device dev = getHost();

        if(dev != null && dev.getId().equals(playerID))
            return true;

        return false;
    }

    /**
     * Get device by player name.
     * @param playerName A player name.
     * @return The device associated with the player name or null if there is no such device.
     */
    public Device getDeviceByPlayerName(String playerName){
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
    public Device getDeviceByPlayerID(String playerID){
        for (int i = 0; i < devices.size(); i++) {
            if(playerID.equals(devices.get(i).getId())){
                return devices.get(i);
            }
        }

        return null;
    }

    /**
     * Remove a device by ID.
     * @param playerID ID of the player to be removed.
     */
    public void removeDeviceByID(String playerID){
        if (playerID == null || playerID.equals("")) return;

        for (int i = 0; i < devices.size(); i++) {
            if(devices.get(i).getId().equals(playerID)){
                devices.remove(i);
                break;
            }
        }
    }
}
