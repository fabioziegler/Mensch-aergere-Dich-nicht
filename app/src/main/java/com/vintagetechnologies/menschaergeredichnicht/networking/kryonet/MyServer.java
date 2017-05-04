package com.vintagetechnologies.menschaergeredichnicht.networking.kryonet;

import android.os.AsyncTask;
import android.util.Log;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;

import java.io.IOException;

import static com.vintagetechnologies.menschaergeredichnicht.networking.Network.registerKryoClasses;

/**
 * Created by Fabio on 02.05.17.
 */

public class MyServer {

	private static final String TAG = MyServerActivity.class.getSimpleName();

	private Server server;


	/**
	 * Called when an object has been received from the remote end of the connection.
	 * @param connection
	 * @param object
	 */
	public void onReceived(Connection connection, Object object){
		Log.i(TAG, "Received message! Msg: " + object);
	}


	/**
	 * Called when the connection with an endpoint (host) was established.
	 * @param connection
	 */
	public void onConnected(Connection connection){
		Log.i(TAG, "Connected to clinet: " + connection.getRemoteAddressTCP().getHostName());
	}


	/**
	 * Called when the remote end is no longer connected.
	 * @param connection
	 */
	public void onDisconnected(Connection connection){

	}


	/**
	 * Initialize server.
	 */
	public void initializeServer(){

		server = new Server();
		server.start();

		try {
			server.bind(54555, 54777);
		} catch (IOException e) {
			Log.i(TAG, "Fehler beim Server Start.", e);
		}

		server.addListener(new Listener(){
			@Override
			public void connected(Connection connection) {
				super.connected(connection);
				onConnected(connection);
			}

			@Override
			public void disconnected(Connection connection) {
				super.disconnected(connection);
				onDisconnected(connection);
			}

			@Override
			public void received(Connection connection, Object object) {
				super.received(connection, object);

				onReceived(connection, object);
			}

			@Override
			public void idle(Connection connection) {
				super.idle(connection);
			}
		});

		registerKryoClasses(server.getKryo());
	}


}
