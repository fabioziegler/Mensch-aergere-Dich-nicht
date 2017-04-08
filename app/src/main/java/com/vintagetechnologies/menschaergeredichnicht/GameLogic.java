package com.vintagetechnologies.menschaergeredichnicht;

import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.nearby.Nearby;
import com.vintagetechnologies.menschaergeredichnicht.networking.DeviceList;

/**
 * Created by Fabio on 08.04.17.
 *
 * Implements the game rules and logic. Sends and receives messages to/from other players.
 * TODO: implement own classes GameLogicClient, GameLogicHost which extend GameLogic...
 */
public class GameLogic {

    /* Holds game settings like music enabled, cheat mode.. */
    private GameSettings gameSettings;

    /* identifies the device as host (if true) or client otherwise */
    private boolean isHost;

    /* True if the game started */
    private boolean gameStarted;

    /* A list which maps each player name to a corresponding ID */
    private DeviceList connectedDevices;

    /* The activity which sends/receives messages. Used to change layouts. */
    private AppCompatActivity activity;

    /* Google API client, used to stop connections, ... */
    private GoogleApiClient googleApiClient;


    public GameLogic(AppCompatActivity activity, GoogleApiClient googleApiClient, boolean isHost){
        this.activity = activity;
        this.googleApiClient = googleApiClient;
        this.isHost = isHost;
        gameStarted = false;
        connectedDevices = new DeviceList();
        gameSettings = (GameSettings) DataHolder.getInstance().retrieve("GAMESETTINGS");
    }


    /**
     * Calle when the player receives a message from another player
     * @param player
     * @param messsage
     */
    public void receivedMessage(String player, String messsage){

    }


    /**
     * Sends a message to a player
     * @param playerID The ID of the receiver
     * @param messsage A message with the max. length of 4096
     */
    public void sendMessage(String playerID, String messsage){
        Nearby.Connections.sendReliableMessage(googleApiClient, playerID, messsage.getBytes());
    }


    /**
     * Called if the host started the game. Devices are no longer able to connect.
     */
    public void startGame(){
        gameStarted = true;

        // show game layout
        activity.setContentView(R.layout.activity_spieloberflaeche);

        // TODO: implement game rules/logic
    }


    /**
     * Called when the player wants to exit the game before it is over.
     */
    public void endGame(){
        // TODO: implement

        if(isHost){
            // if host -> stop all connectedDevices
            Log.i(TAG, "Host: disconnecting all devices.");
            Nearby.Connections.stopAllEndpoints(googleApiClient);

        } else {
            Log.i(TAG, "Disconnecting from host");
            Nearby.Connections.disconnectFromEndpoint(googleApiClient, connectedDevices.getHost().getId());
        }
    }


    /**
     * Called when the game is over
     */
    public void gameOver(){
        // TODO: implement: return to main or menu/show stats/points/winner
    }


    /**
     * Called when a player disconnected/becomes unreachable
     * @param playerID The ID of the player
     */
    public void playerDisconnected(String playerID){

    }



    /* Getter and Setter: */

    /**
     * Check if the device is host in the game
     * @return True if the device is a game host
     */
    public boolean isHost() {
        return isHost;
    }

    /**
     * Check if the game started
     * @return True if the game started
     */
    public boolean isGameStarted() {
        return gameStarted;
    }

    /**
     * Get a list of player names with their corresponding ID
     * @return A list of player names with their corresponding ID
     */
    public DeviceList getDevices(){
        return connectedDevices;
    }

    private static final String TAG = MainActivity.class.getSimpleName();
}
