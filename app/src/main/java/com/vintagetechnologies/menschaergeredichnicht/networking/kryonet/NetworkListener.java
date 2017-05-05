package com.vintagetechnologies.menschaergeredichnicht.networking.kryonet;

import com.esotericsoftware.kryonet.Connection;

/**
 * Created by Fabio on 04.05.17.
 */

public interface NetworkListener {


	/**
	 * Called when an object has been received from the remote end of the connection.
	 * @param connection
	 * @param object
	 */
	void onReceived(Connection connection, Object object);


	/**
	 * Called when the connection with an endpoint (host) was established.
	 * @param connection
	 */
	void onConnected(Connection connection);


	/**
	 * Called when the remote end is no longer connected.
	 * @param connection
	 */
	void onDisconnected(Connection connection);

}
