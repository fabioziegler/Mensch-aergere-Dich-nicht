package com.vintagetechnologies.menschaergeredichnicht.networking.kryonet;

import android.app.Activity;
import android.util.Log;

import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.FrameworkMessage;
import com.esotericsoftware.kryonet.Listener;

import java.io.IOException;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.vintagetechnologies.menschaergeredichnicht.networking.Network.OBJECT_BUFFER_SIZE;
import static com.vintagetechnologies.menschaergeredichnicht.networking.Network.WRITE_BUFFER_SIZE;
import static com.vintagetechnologies.menschaergeredichnicht.networking.Network.registerKryoClasses;

/**
 * Created by Fabio on 02.05.17.
 */

public class MyClient {

	private static final String TAG = MyServerActivity.class.getSimpleName();

	private Client client;

	private List<NetworkListener> listeners;

	private Activity callingActivity;


	public MyClient(Activity callingActivity){
		this.callingActivity = callingActivity;
		listeners = Collections.synchronizedList(new ArrayList<NetworkListener>(2));
	}


	/**
	 * Called when an object has been received from the remote end of the connection.
	 * @param connection
	 * @param object
	 */
	public void onReceived(final Connection connection, final Object object){

		if(object instanceof FrameworkMessage.KeepAlive)	// skip keep alive messages
			return;

		Log.i(TAG, "Received message! Msg: " + object + ". From player ID: " + connection.getID());

		callingActivity.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				for (int i = 0; i < listeners.size(); i++)
					listeners.get(i).onReceived(connection, object);
			}
		});
	}


	/**
	 * Called when the connection with an endpoint (host) was established.
	 * @param connection
	 */
	public void onConnected(final Connection connection){
		Log.i(TAG, "Connected to server.");

		callingActivity.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				for (int i = 0; i < listeners.size(); i++)
					listeners.get(i).onConnected(connection);
			}
		});
	}


	/**
	 * Called when the remote end is no longer connected.
	 * @param connection
	 */
	public void onDisconnected(final Connection connection){
		callingActivity.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				for (int i = 0; i < listeners.size(); i++)
					listeners.get(i).onDisconnected(connection);
			}
		});
	}


	/**
	 * Initialize client.
	 */
	public void initializeClient(){

		client = new Client(WRITE_BUFFER_SIZE, OBJECT_BUFFER_SIZE);
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
					try { Thread.sleep(500); } catch (InterruptedException e) {
						Log.e(TAG, "Thread exception.", e);
						Thread.currentThread().interrupt();
					}

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


	public synchronized void addListener(NetworkListener listener){
		listeners.add(listener);
	}


	public synchronized void removeListener(NetworkListener listener){
		listeners.remove(listener);
	}


	public void setCallingActivity(Activity callingActivity) {
		this.callingActivity = callingActivity;
	}


	public Client getClient(){
		return client;
	}
}
