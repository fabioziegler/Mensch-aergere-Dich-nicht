package com.vintagetechnologies.menschaergeredichnicht.networking.kryonet;

import android.util.Log;

import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;

import java.io.IOException;
import java.net.InetAddress;
import java.util.ArrayList;

import static com.vintagetechnologies.menschaergeredichnicht.networking.Network.registerKryoClasses;

/**
 * Created by Fabio on 02.05.17.
 */

public class MyClient {

	private static final String TAG = MyServerActivity.class.getSimpleName();

	private Client client;

	private ArrayList<NetworkListener> listeners;


	/**
	 * Called when an object has been received from the remote end of the connection.
	 * @param connection
	 * @param object
	 */
	public void onReceived(Connection connection, Object object){
		Log.i(TAG, "Received message!" + object);

		for(NetworkListener listener : listeners)
			listener.onReceived(connection, object);
	}


	/**
	 * Called when the connection with an endpoint (host) was established.
	 * @param connection
	 */
	public void onConnected(Connection connection){
		Log.i(TAG, "Connected. Sending hello to server...");
		client.sendTCP("hello");

		for(NetworkListener listener : listeners)
			listener.onConnected(connection);
	}


	/**
	 * Called when the remote end is no longer connected.
	 * @param connection
	 */
	public void onDisconnected(Connection connection){
		for(NetworkListener listener : listeners)
			listener.onDisconnected(connection);
	}


	/**
	 * Initialize client.
	 */
	public void initializeClient(){

		client = new Client();
		client.start();

		client.addListener(new Listener(){
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

		registerKryoClasses(client.getKryo());
	}


	/**
	 * Discover host on the local network.
	 */
	public void discoverHost(){

		Thread thread = new Thread(new Runnable() {
			@Override
			public void run() {

				final int discoverTimeout = 1000 * 20;	// def: 5s
				InetAddress hostAddress;

				do{
					hostAddress = client.discoverHost(54777, discoverTimeout);

					// sleep 0.5s
					try { Thread.sleep(500); } catch (InterruptedException e) { Log.e(TAG, "Thread exception.", e); }

				}while (hostAddress == null);

				// connect to host
				try {
					Log.i(TAG, "Connecting to server at " + hostAddress);
					final int connectionTimeout = 1000 * 10;	// def: 5s
					client.connect(connectionTimeout, hostAddress, 54555, 54777);
				} catch (IOException e) {
					Log.e(TAG, "Failed to connect to server or timeout.", e);
				}

			}
		});

		thread.setName("Host Discovery");
		thread.start();
	}


	private void startDiscoveryOLD(){

		final int discoverTimeout = 1000 * 60 * 5;	// def: 5s
		InetAddress address = client.discoverHost(54777, discoverTimeout);

		if(address != null){

			try {
				Log.i(TAG, "Connection to server at " + address);
				client.connect(5000, address, 54555, 54777);
			} catch (IOException e) {
				Log.e(TAG, "Failed to connect to server...", e);
			}

		}else {
			Log.e(TAG, "Could not find server...");
		}
	}


	public void addListener(NetworkListener listener){
		listeners.add(listener);
	}

	public void removeListener(NetworkListener listener){
		listeners.remove(listener);
	}

	public Client getClient(){
		return client;
	}

}
