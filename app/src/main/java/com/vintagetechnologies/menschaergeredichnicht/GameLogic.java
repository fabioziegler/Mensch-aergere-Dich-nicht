package com.vintagetechnologies.menschaergeredichnicht;

import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.nearby.Nearby;
import com.vintagetechnologies.menschaergeredichnicht.networking.Device;
import com.vintagetechnologies.menschaergeredichnicht.networking.DeviceList;
import com.vintagetechnologies.menschaergeredichnicht.structure.Game;
import com.vintagetechnologies.menschaergeredichnicht.synchronisation.GameSynchronisation;

import static com.vintagetechnologies.menschaergeredichnicht.networking.Network.TAG_PLAYER_HAS_CHEATED;
import static com.vintagetechnologies.menschaergeredichnicht.networking.Network.TAG_SYNCHRONIZE_GAME;

/**
 * Created by Fabio on 08.04.17.
 *
 * Implements the game rules and logic. Sends and receives messages to/from other players.
 * TODO: implement own classes GameLogicClient, GameLogicHost which extend GameLogic...
 */
public class GameLogic {

	private static final String TAG = GameLogic.class.getSimpleName();

	/* message segmentation separator */
	private static final String messageDelimiter = ";";

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
     * @param playerID
     * @param message
     */
    public void receivedMessage(String playerID, String message){
		Log.i(TAG, "Received message from " + playerID + ": " + message);
        Toast.makeText(activity.getApplicationContext(), "Received message: " + message, Toast.LENGTH_LONG).show();

		String[] data = decodeMessage(message);

		String tag = data[0];
		String decodedMessage = data[1];

		// parse message
		parseMessage(playerID, tag, decodedMessage);
    }


	/**
	 * Parse and execute a received message.
	 * @param playerID	The player who sent the message.
	 * @param tag		A tag identifying the purpose of the message.
	 * @param message	The received message.
	 */
	private void parseMessage(String playerID, String tag, String message){
		// TODO: extend for other types of message (e.g. aufdecken, ...)

		// execute message based on tag
		switch (tag){
			case TAG_SYNCHRONIZE_GAME:

				// replace current game class with new one
				Game game = GameSynchronisation.decode(message);
				Game.refreshGameInstance(game);

				break;

			case TAG_PLAYER_HAS_CHEATED:

				// get boolean from message
				boolean playerHasCheated = Boolean.parseBoolean(message);
				String playerName = getDevices().getDeviceByPlayerID(playerID).getName();

				// set player cheating/or not
				Game.getInstance().getPlayerByName(playerName).getSchummeln().setPlayerCheating(playerHasCheated);

				// send changes to others
				GameSynchronisation.synchronize(Game.getInstance());

				break;

			default:
				Log.w(TAG, String.format("Received unknown tag '%s' from player '%s'", tag, playerID));
		}
	}


    /**
     * Sends a message to a player.
     * @param playerID The ID of the receiver
	 * @param tag  A tag identifying the purpose of the message.
     * @param message A message with the max. length of 4096
     */
    public void sendMessage(String playerID, String tag, String message) throws IllegalArgumentException {

		// tag must not contain the message delimiter
		if(tag.contains(messageDelimiter))
			throw new IllegalArgumentException("Argument tag must not contain the character '" + messageDelimiter + "'.");

		String encodedMessage = encodeMessage(tag, message);

        Nearby.Connections.sendReliableMessage(googleApiClient, playerID, encodedMessage.getBytes());	// transmits over TCP
    }


	/**
	 * Encode a message for sending over the network.
	 * @param tag
	 * @param message
	 * @return The encoded message
	 */
	public String encodeMessage(String tag, String message){

		/**
		 * Send Message in format: <tag length>;<tag>;<message>
		 *
		 *     For example: 4;SYNC;<game obj as json String>
		 */
		int tagLength = tag.length();
		String encodedMessage = String.valueOf(tagLength) + messageDelimiter + tag + messageDelimiter + message;
		return encodedMessage;
	}

	/**
	 * Decode a message received over the netwrok
	 * @param message The message to be decoded
	 * @return A String array containing the tag at index 0 and the decoded message at index 1.
	 */
	public String[] decodeMessage(String message){
		// split message (format: <tag length>;<tag>;<message>
		String[] split = message.split(messageDelimiter);
		int tagLength = Integer.parseInt(split[0]);
		String tag = split[1];

		// get plain message
		String plainMessage = message.substring(String.valueOf(tagLength).length() + messageDelimiter.length() + tagLength + messageDelimiter.length());

		return new String[]{ tag, plainMessage };
	}


	/**
	 * Sends a message to a device.
	 * @param device The device
	 * @param tag A tag identifying the purpose of the message.
	 * @param message The message to be sent.
	 */
    public void sendMessage(Device device, String tag, String message){
		sendMessage(device.getId(), tag, message);
	}


	/**
	 * Called by the host to broadcast a message to all clients.
	 * @param tag A tag identifying the purpose of the message.
	 * @param message The message to be sent.
	 */
    public void sendToClientDevices(String tag, String message){
		for(Device device : connectedDevices.getList()){
			if(!device.isHost())
				sendMessage(device, tag, message);
		}
	}


	/**
	 * Send a message to the host
	 * @param tag
	 * @param message
	 */
	public void sendToHost(String tag, String message){
		sendMessage(getDevices().getHost(), tag, message);
    }


    /**
     * Called if the host started the game. Devices are no longer able to connect.
     */
    public void startGame(){
        gameStarted = true;

        // show game layout
        activity.setContentView(R.layout.activity_spieloberflaeche);

        // TODO: implement game rules/logic

        //TODO: remove!!!!
        //activity.startActivity(new Intent(activity, Spieloberflaeche.class));

		// test:
        sendMessage(getDevices().getList().get(0).getId(), "start game", "true");
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
     * Called when the wifi connection was lost during a game
     */
    public void onWifiConnectionLost(){
		// TODO: add logic when wifi connection was lost
		Log.i(TAG, "WiFi connection lost.");
	}


    /**
     * Called when the wifi connection was lost during a game and reconnected again
     */
    public void onWifiConnectionReestablished(){
		// TODO: add logic when wifi connection was reestablished
		Log.i(TAG, "WiFi connection reestablished.");
    }


    /**
     * Called when the game is over
     */
    public void gameOver(){
        // TODO: implement: return to main or menu/show stats/points/winner; host stop client connections
    }


    /**
     * Called when a player disconnected/becomes unreachable
     * @param playerID The ID of the player
     */
    public void playerDisconnected(String playerID){

        // show info that a player left
        Toast.makeText(activity.getApplicationContext(),
                connectedDevices.getDeviceByPlayerID(playerID).getName() +
                activity.getString(R.string.msgPlayerJustLeftTheGame), Toast.LENGTH_LONG).show();

        connectedDevices.removeDeviceByID(playerID);
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

}
