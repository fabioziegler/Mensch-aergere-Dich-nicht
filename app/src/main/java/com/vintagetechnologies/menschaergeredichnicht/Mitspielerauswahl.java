package com.vintagetechnologies.menschaergeredichnicht;

import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.nearby.Nearby;
import com.google.android.gms.nearby.connection.AppIdentifier;
import com.google.android.gms.nearby.connection.AppMetadata;
import com.google.android.gms.nearby.connection.Connections;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Mitspielerauswahl extends AppCompatActivity implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        View.OnClickListener,
        Connections.MessageListener/*,
        Connections.ConnectionRequestListener,
        Connections.EndpointDiscoveryListener */ {


    private GameSettings gameSettings;
    private Button btnStartGame;
    private TextView lblStatus;
    private ListView listViewPlayers;
    private ArrayAdapter<String> listAdapter;

    // Identify if the device is the host
    private boolean mIsHost = false;

    private static final String TAG = MainActivity.class.getSimpleName();

    private HashMap<String, String> players;
    private ArrayList<String> playerNames;

    /* GoogleApiClient for connecting to the Nearby Connections API */
    private GoogleApiClient mGoogleApiClient;

    private Connections.ConnectionRequestListener myConnectionRequestListener;
    private Connections.EndpointDiscoveryListener myEndpointDiscoveryListener;


    private void btnStartGameClicked(){
        // toDo: Bedingung "mind 1 Mitspieler ist ausgewählt" hinzufügen, sonst fehler meldung "mind. 1 muss ausgewählt werden"
        // toDo: Mitspielerauswahl übernehmen
        startActivity(new Intent(Mitspielerauswahl.this, Spieloberflaeche.class));
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mitspielerauswahl);

        btnStartGame = (Button) findViewById(R.id.btnStartGame);
        lblStatus = (TextView) findViewById(R.id.lblStatus);
        listViewPlayers = (ListView) findViewById(R.id.listViewPlayers);

        playerNames = new ArrayList<>(4);
        listAdapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_spinner_item, playerNames);
        listViewPlayers.setAdapter(listAdapter);

        btnStartGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnStartGameClicked();
            }
        });

        gameSettings = (GameSettings) DataHolder.getInstance().retrieve("GAMESETTINGS");

        players = new HashMap<>(4);

        mIsHost = true;

        myConnectionRequestListener =
                new Connections.ConnectionRequestListener() {
                    @Override
                    public void onConnectionRequest(String remoteEndpointId, String
                            remoteEndpointName, byte[] bytes) {
                        Mitspielerauswahl.this.onConnectionRequest(remoteEndpointId,
                                remoteEndpointName, bytes);
                    }
                };

        myEndpointDiscoveryListener =
                new Connections.EndpointDiscoveryListener() {
                    @Override
                    public void onEndpointFound(String endpointId,
                                                String serviceId,
                                                String name) {
                        Mitspielerauswahl.this.onEndpointFound(endpointId,serviceId,
                                name);
                    }

                    @Override
                    public void onEndpointLost(String remoteEndpointId) {
                        Mitspielerauswahl.this.onEndpointLost(remoteEndpointId);
                    }
                };


        // init google api client
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Nearby.CONNECTIONS_API)
                .build();


        // prevent phone from entering sleep mode
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }


    @Override
    public void onStart() {
        super.onStart();

        lblStatus.setText("Laden...");

        // connect to Google Play services
        mGoogleApiClient.connect();

        Log.i(TAG, "Connecting to Google Play services...");
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }


    @Override
    public void onClick(View v) {

    }

    /**
     * Called when the connection to Google Play service was successful
     * @param bundle
     */
    @Override
    public void onConnected(@Nullable Bundle bundle) {
        // start advertising (game hosting)
        startAdvertising();

        lblStatus.setText("Suche Mitspieler...");
        Log.i(TAG, "Started advertising...");
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    /**
     * Called when a player tries to connect to the host
     * @param remoteEndpointId The ID of the remote endpoint requesting a connection.
     * @param remoteEndpointName The human readable name of the remote endpoint.
     * @param handshakeData Bytes of a custom message sent with the connection request.
     */
    /*@Override*/
    public void onConnectionRequest(final String remoteEndpointId, final String remoteEndpointName, byte[] handshakeData) {

        if (mIsHost) {
            byte[] myPayload = null;

            // Automatically accept all requests
            Nearby.Connections.acceptConnectionRequest(mGoogleApiClient, remoteEndpointId,
                    myPayload, this).setResultCallback(new ResultCallback<Status>() {
                @Override
                public void onResult(Status status) {

                    if (status.isSuccess()) {

                        Log.i(TAG, "Connected to player: " + remoteEndpointName);
                        players.put(remoteEndpointId, remoteEndpointName);

                        // update player list view
                        playerNames.add(remoteEndpointName);
                        listAdapter.notifyDataSetChanged();

                    } else {
                        Toast.makeText(getApplicationContext(), "Failed to connect to: " + remoteEndpointName,
                                Toast.LENGTH_SHORT).show();
                    }

                }
            });

        } else {
            // Clients should not be advertising and will reject all connection requests.
            Nearby.Connections.rejectConnectionRequest(mGoogleApiClient, remoteEndpointId);
        }
    }


    /**
     * Called when a host is found
     * @param endpointId The ID of the remote endpoint that was discovered.
     * @param serviceId The ID of the service of the remote endpoint.
     * @param endpointName The human readable name of the remote endpoint.
     */
    /*@Override*/
    public void onEndpointFound(final String endpointId, String serviceId, final String endpointName) {
        // This device is discovering endpoints and has located an advertiser.
        // Write your logic to initiate a connection with the device at
        // the endpoint ID

        // TODO: create new layout with list for hosts and button to connect, then use this method
        // to display the endpointName in the list and a button to connect (call connectTo()).

        Log.i(TAG, "Found endpoint: " + endpointName);
    }


    /**
     * Called when a remote endpoint is no longer discoverable; only called for endpoints that previously had been passed to onEndpointFound()
     * @param endpointid
     */
    /*@Override*/
    public void onEndpointLost(String endpointid) {

    }

    @Override
    public void onMessageReceived(String s, byte[] bytes, boolean b) {

    }

    @Override
    public void onDisconnected(String s) {

    }


    /**
     * Called when a player connects to the host
     * @param remoteEndpointId The ID to which connect to
     * @param endpointName
     */
    private void connectTo(String remoteEndpointId, final String endpointName) {
        // Send a connection request to a remote endpoint. By passing 'null' for
        // the name, the Nearby Connections API will construct a default name
        // based on device model such as 'LGE Nexus 5'.
        String myName = gameSettings.getPlayerName();
        byte[] myPayload = null;

        Nearby.Connections.sendConnectionRequest(mGoogleApiClient, myName,
                remoteEndpointId, myPayload, new Connections.ConnectionResponseCallback() {
                    @Override
                    public void onConnectionResponse(String remoteEndpointId, Status status,
                                                     byte[] bytes) {
                        if (status.isSuccess()) {
                            // Successful connection
                        } else {
                            // Failed connection
                        }
                    }
                }, this);
    }


    /**
     * Called by the host to start advertising itself on the network
     */
    private void startAdvertising() {
        if (!isConnectedToWiFiNetwork()) {
            // Implement logic when device is not connected to a network
            lblStatus.setText("Kein Netzwerk!");
            lblStatus.setTextColor(Color.RED);
            Toast.makeText(getApplicationContext(), "Bitte stelle vorher eine WiFi Verbindung her", Toast.LENGTH_LONG).show();
            return;
        }

        // Identify that this device is the host
        mIsHost = true;

        // Advertising with an AppIdentifer lets other devices on the
        // network discover this application and prompt the user to
        // install the application.
        List<AppIdentifier> appIdentifierList = new ArrayList<>();
        appIdentifierList.add(new AppIdentifier(getPackageName()));
        AppMetadata appMetadata = new AppMetadata(appIdentifierList);

        // The advertising timeout is set to run indefinitely
        // Positive values represent timeout in milliseconds
        long NO_TIMEOUT = 0L;

        String name = null;
        Nearby.Connections.startAdvertising(mGoogleApiClient, name, appMetadata, NO_TIMEOUT,
                myConnectionRequestListener).setResultCallback(new ResultCallback<Connections.StartAdvertisingResult>() {
            @Override
            public void onResult(Connections.StartAdvertisingResult result) {
                if (result.getStatus().isSuccess()) {
                    // Device is advertising
                } else {
                    int statusCode = result.getStatus().getStatusCode();
                    // Advertising failed - see statusCode for more details
                }
            }
        });
    }


    /**
     * Called by the clients/players to discover the host(s) in the WiFi network
     */
    private void startDiscovery() {

        if (!isConnectedToWiFiNetwork()) {
            // Implement logic when device is not connected to a network
            Toast.makeText(getApplicationContext(), "Bitte stelle vorher eine WiFi Verbindung her", Toast.LENGTH_LONG).show();
            return;
        }

        String serviceId = getString(R.string.service_id);

        // Set an appropriate timeout length in milliseconds
        long DISCOVER_TIMEOUT = 1000L;

        // Discover nearby apps that are advertising with the required service ID.
        Nearby.Connections.startDiscovery(mGoogleApiClient, serviceId, DISCOVER_TIMEOUT, myEndpointDiscoveryListener)
                .setResultCallback(new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        if (status.isSuccess()) {
                            // Device is discovering
                        } else {
                            int statusCode = status.getStatusCode();
                            // Advertising failed - see statusCode for more details
                        }
                    }
                });
    }


    /**
     * Check if the device is connected (and not currently connecting) to a WiFi network.
     * @return true if connected, false otherwise.
     */
    private boolean isConnectedToWiFiNetwork() {
        ConnectivityManager connManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo info = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

        //return (info != null && info.isConnectedOrConnecting());
        return (info != null && info.isConnected());
    }

}
