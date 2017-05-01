package com.vintagetechnologies.menschaergeredichnicht.networking;

/**
 * Created by Fabio on 28.04.17.
 */

public class NetworkTags {

	/**
	 * Sender: Used to indicate that the host should send his up to date Game class to the clients.
	 * Receiver: The message data contains/is an up to date Game class.
	 */
	public static final String TAG_SYNCHRONIZE_GAME = "sync";


	/**
	 * Sender/Receiver: Indicates if a player has cheated (message: "true" or "false).
	 */
	public static final String TAG_PLAYER_HAS_CHEATED = "cheated";

}
