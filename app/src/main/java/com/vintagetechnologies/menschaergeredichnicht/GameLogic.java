package com.vintagetechnologies.menschaergeredichnicht;

import android.app.Activity;

import com.esotericsoftware.kryonet.Connection;
import com.vintagetechnologies.menschaergeredichnicht.impl.ActualGame;
import com.vintagetechnologies.menschaergeredichnicht.networking.Device;
import com.vintagetechnologies.menschaergeredichnicht.networking.DeviceList;
import com.vintagetechnologies.menschaergeredichnicht.networking.Network;
import com.vintagetechnologies.menschaergeredichnicht.structure.Player;

import static com.vintagetechnologies.menschaergeredichnicht.networking.Network.DATAHOLDER_GAMESETTINGS;

/**
 * Created by Fabio on 08.04.17.
 *
 * Implements the game rules and logic. Sends and receives messages to/from other players.
 */
public abstract class GameLogic {

    /* If the the game started/ended */
    private boolean gameStarted;
    private boolean gameEnded;

    /* A list which maps each player name to a corresponding ID */
    private DeviceList devices;

    /* The activity which sends/receives messages. Used to change layouts. */
    private Activity activity;

	private GameSettings gameSettings;

	private boolean isHost;

    public GameLogic(Activity activity, boolean isHost){
        this.activity = activity;
        gameStarted = false;
		gameSettings = (GameSettings) DataHolder.getInstance().retrieve(DATAHOLDER_GAMESETTINGS);
		this.isHost = isHost;
        devices = new DeviceList();
    }


	protected abstract void parseMessage(Connection connection, Object object);

	public abstract void startGame();

	public abstract void leaveGame();


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
		String encodedMessage = String.valueOf(tagLength) + Network.MESSAGE_DELIMITER + tag + Network.MESSAGE_DELIMITER + message;
		return encodedMessage;
	}

	/**
	 * Decode a message received over the netwrok
	 * @param message The message to be decoded
	 * @return A String array containing the tag at index 0 and the decoded message at index 1.
	 */
	public String[] decodeMessage(String message){
		// split message (format: <tag length>;<tag>;<message>
		String[] split = message.split(Network.MESSAGE_DELIMITER);
		int tagLength = Integer.parseInt(split[0]);
		String tag = split[1];

		// get plain message
		String plainMessage = message.substring(String.valueOf(tagLength).length() + Network.MESSAGE_DELIMITER.length() + tagLength + Network.MESSAGE_DELIMITER.length());

		return new String[]{ tag, plainMessage };
	}


    /**
     * Check if the game started
     * @return True if the game started
     */
    public boolean hasGameStarted() {
        return gameStarted;
    }

    /**
     * Get a list of player names with their corresponding ID
     * @return A list of player names with their corresponding ID
     */
    public DeviceList getDevices(){
        return devices;
    }

    public boolean isHost(){
		return isHost;
	}

	public boolean isGameEnded() {
		return gameEnded;
	}

	public void setGameEnded(boolean gameEnded) {
		this.gameEnded = gameEnded;
	}

	public void setGameStarted(boolean gameStarted) {
		this.gameStarted = gameStarted;
	}

	public Activity getActivity() {
		return activity;
	}

	public void setActivity(Activity activity) {
		this.activity = activity;
	}

	public GameSettings getGameSettings() {
		return gameSettings;
	}

	/**
	 *  Associates each player with the corresponding network connection ID.
	 */
	public void generateUniqueIds(){
		Player[] players = ActualGame.getInstance().getGameLogic().getPlayers();

		for(int i = 0; i < players.length; i++){
			Player player = players[i];
			Device device = getDevices().getPlayer(player.getName());
			player.setUniqueId(device.getId());
		}
	}
}
