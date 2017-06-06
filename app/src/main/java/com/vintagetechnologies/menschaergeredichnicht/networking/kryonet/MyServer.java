package com.vintagetechnologies.menschaergeredichnicht.networking.kryonet;

import android.app.Activity;
import android.util.Log;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.FrameworkMessage;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.vintagetechnologies.menschaergeredichnicht.networking.Network.OBJECT_BUFFER_SIZE;
import static com.vintagetechnologies.menschaergeredichnicht.networking.Network.WRITE_BUFFER_SIZE;
import static com.vintagetechnologies.menschaergeredichnicht.networking.Network.registerKryoClasses;

/**
 * Created by Fabio on 02.05.17.
 */

public class MyServer {

    private static final String TAG = MyServerActivity.class.getSimpleName();

    private Server server;

    private List<NetworkListener> listeners;

    private Activity callingActivity;

    public MyServer(Activity callingActivity) {
        this.callingActivity = callingActivity;
        listeners = Collections.synchronizedList(new ArrayList<NetworkListener>(2));
    }


    /**
     * Called when an object has been received from the remote end of the connection.
     *
     * @param connection
     * @param object
     */
    public void onReceived(final Connection connection, final Object object) {

        if (object instanceof FrameworkMessage.KeepAlive)    // skip keep alive messages
            return;

        Log.i(TAG, "Received message! Msg: " + object + ". From player ID: " + connection.getID());

        callingActivity.runOnUiThread(() -> {
            for (int i = 0; i < listeners.size(); i++)
                listeners.get(i).onReceived(connection, object);

        });
    }


    /**
     * Called when the connection with an endpoint (host) was established.
     *
     * @param connection
     */
    public void onConnected(final Connection connection) {

        Log.i(TAG, "Connected to client: " + connection.getRemoteAddressTCP().getAddress());
        callingActivity.runOnUiThread(() -> {
            for (int i = 0; i < listeners.size(); i++)
                listeners.get(i).onConnected(connection);

        });
    }


    /**
     * Called when the remote end is no longer connected.
     *
     * @param connection
     */
    public void onDisconnected(final Connection connection) {
        callingActivity.runOnUiThread(() -> {
            for (int i = 0; i < listeners.size(); i++)
                listeners.get(i).onDisconnected(connection);

        });
    }


    /**
     * Initialize server.
     */
    public void initializeServer() {

        server = new Server(WRITE_BUFFER_SIZE, OBJECT_BUFFER_SIZE);
        server.start();

        try {
            server.bind(54555, 54777);
        } catch (IOException e) {
            Log.i(TAG, "Fehler beim Server Start.", e);
        }

        server.addListener(new Listener() {
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


    public synchronized void addListener(NetworkListener listener) {
        listeners.add(listener);
    }


    public synchronized void removeListener(NetworkListener listener) {
        listeners.remove(listener);
    }


    public void setCallingActivity(Activity callingActivity) {
        this.callingActivity = callingActivity;
    }


    public Server getServer() {
        return server;
    }
}
