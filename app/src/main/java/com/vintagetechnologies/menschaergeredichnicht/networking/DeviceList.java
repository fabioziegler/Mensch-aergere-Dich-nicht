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

}
