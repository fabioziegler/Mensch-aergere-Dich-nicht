package com.vintagetechnologies.menschaergeredichnicht.networking.kryonet;

import android.app.Activity;
import android.util.Log;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.FrameworkMessage;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import static com.vintagetechnologies.menschaergeredichnicht.networking.Network.registerKryoClasses;

/**
 * Created by Fabio on 02.05.17.
 */

public class MyServer {

	private static final String TAG = MyServerActivity.class.getSimpleName();

	private Server server;

	private ArrayList<NetworkListener> listeners;

	private Activity callingActivity;

	public MyServer(Activity callingActivity){
		this.callingActivity = callingActivity;
		listeners = new ArrayList<>(2);
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
				Iterator<NetworkListener> iterator = listeners.iterator();
				while (iterator.hasNext()){
					iterator.next().onReceived(connection, object);
				}
			}
		});
	}


	/**
	 * Called when the connection with an endpoint (host) was established.
	 * @param connection
	 */
	public void onConnected(final Connection connection){

		Log.i(TAG, "Connected to client: " + connection.getRemoteAddressTCP().getAddress());
		callingActivity.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				Iterator<NetworkListener> iterator = listeners.iterator();
				while (iterator.hasNext()){
					iterator.next().onConnected(connection);
				}
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
				Iterator<NetworkListener> iterator = listeners.iterator();
				while (iterator.hasNext()){
					iterator.next().onDisconnected(connection);
				}
			}
		});
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


	public void addListener(NetworkListener listener){
		listeners.add(listener);
	}


	public void removeListener(NetworkListener listener){
		listeners.remove(listener);
	}


	public Server getServer(){
		return server;
	}
}
