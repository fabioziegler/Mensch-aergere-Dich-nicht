package com.vintagetechnologies.menschaergeredichnicht;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
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
import com.google.android.gms.nearby.connection.Connections;
import com.vintagetechnologies.menschaergeredichnicht.networking.Device;

import java.util.ArrayList;

/**
 * Created by Fabio on 08.04.17.
 */

public class GameHost extends AppCompatActivity implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        Connections.MessageListener/*,
        Connections.ConnectionRequestListener,
        Connections.EndpointDiscoveryListener */ {


    private GameLogic gameLogic;

    private GameSettings gameSettings;
    private Button btnStartGame;
    private TextView lblStatus;
    private ColorStateList colorsLabelStatus;
    private ListView listViewPlayers;
    private ArrayAdapter<String> listAdapter;

    private static final String TAG = MainActivity.class.getSimpleName();

    /* for displaying connected devices in the layout */
    private ArrayList<String> playerNames;

    /* GoogleApiClient for connecting to the Nearby Connections API */
    private GoogleApiClient mGoogleApiClient;

    private Connections.ConnectionRequestListener myConnectionRequestListener;


    /**
     * Called when the user clicks "Spiel starten"
     */
    private void btnStartGameClicked(){

        // there must be at least one player connected to the host
        if(gameLogic.getDevices().getCountConnectedDevices() < 1){
            // display error message
            Toast.makeText(getApplicationContext(), "Warte auf weitere Spieler...", Toast.LENGTH_LONG).show();
            return;
        }

        // stop advertising
        Nearby.Connections.stopAdvertising(mGoogleApiClient);

        gameLogic.startGame();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gamehost);

        // get controls
        btnStartGame = (Button) findViewById(R.id.btnStartGame);
        lblStatus = (TextView) findViewById(R.id.lblStatus);
        listViewPlayers = (ListView) findViewById(R.id.listViewPlayers);

        colorsLabelStatus = lblStatus.getTextColors();   // save textview color for restoring when changed
        
        playerNames = new ArrayList<>(3);
        listAdapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_spinner_item, playerNames);
        listViewPlayers.setAdapter(listAdapter);

        btnStartGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnStartGameClicked();
            }
        });

        // prevent phone from entering sleep mode
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        // create new game logic for the host
        gameLogic = new GameLogic(this, mGoogleApiClient, true);

        // retrieve game settings (from data holder)
        gameSettings = (GameSettings) DataHolder.getInstance().retrieve("GAMESETTINGS");

        myConnectionRequestListener =
                new Connections.ConnectionRequestListener() {
                    @Override
                    public void onConnectionRequest(String remoteEndpointId, String
                            remoteEndpointName, byte[] bytes) {
                        GameHost.this.onConnectionRequest(remoteEndpointId,
                                remoteEndpointName, bytes);
                    }
                };


        // init google api client
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Nearby.CONNECTIONS_API)
                .build();
    }


    @Override
    public void onStart() {
        super.onStart();

        lblStatus.setText("Lade...");
        Log.i(TAG, "Connecting to Google Play services...");

        // connect to Google Play services
        mGoogleApiClient.connect();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }


    /**
     * Called when the connection to Google Play service was successful
     * @param bundle
     */
    @Override
    public void onConnected(@Nullable Bundle bundle) {
        // start advertising (game hosting)
        startAdvertising();
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

        byte[] myPayload = null;

        // Automatically accept all requests
        Nearby.Connections.acceptConnectionRequest(mGoogleApiClient, remoteEndpointId,
                myPayload, this).setResultCallback(new ResultCallback<Status>() {
            @Override
            public void onResult(Status status) {

                if (status.isSuccess()) {

                    Log.i(TAG, "Connected to player: " + remoteEndpointName);
                    gameLogic.getDevices().addDevice(new Device(remoteEndpointId, remoteEndpointName, false));

                    if(!gameLogic.isGameStarted()) {
                        // update player list view
                        playerNames.add(remoteEndpointName);
                        listAdapter.notifyDataSetChanged();
                    }

                } else {
                    Log.e(TAG, "Failed to connect to client: " + remoteEndpointName);
                    Toast.makeText(getApplicationContext(), "Failed to connect to: " + remoteEndpointName, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    /**
     * Called when a message is received from a remote endpoint.
     * @param remoteEndpointId The identifier for the remote endpoint that sent the message.
     * @param payload The bytes of the message sent by the remote endpoint. This array will not exceed MAX_RELIABLE_MESSAGE_LEN bytes for reliable messages, or MAX_UNRELIABLE_MESSAGE_LEN for unreliable ones.
     * @param isReliable True if the message was sent reliably, false otherwise.
     */
    @Override
    public void onMessageReceived(String remoteEndpointId, byte[] payload, boolean isReliable) {

        // send received message for processing to 'game logic'
        gameLogic.receivedMessage(remoteEndpointId, new String(payload));
    }


    /**
     * Called when a remote endpoint is disconnected / becomes unreachable.
     * @param remoteEndpointId The identifier for the remote endpoint that disconnected.
     */
    @Override
    public void onDisconnected(String remoteEndpointId) {
        // forward to game logic
        gameLogic.playerDisconnected(remoteEndpointId);
    }


    private boolean isAdvertising = false;

    /**
     * Called by the host to start advertising itself on the network
     */
    private void startAdvertising() {

        if (!isConnectedToWiFiNetwork()) {  // When device is not connected to a network:
            lblStatus.setText("Kein Netzwerk!");
            lblStatus.setTextColor(Color.RED);
            Toast.makeText(getApplicationContext(), "Bitte stelle eine WiFi Verbindung her!", Toast.LENGTH_LONG).show();
            return;
        }

        // Advertising with an AppIdentifer lets other devices on the network discover this application and prompt the user to install the application.
        /*
        List<AppIdentifier> appIdentifierList = new ArrayList<>();
        appIdentifierList.add(new AppIdentifier(getPackageName()));
        AppMetadata appMetadata = new AppMetadata(appIdentifierList);
        */
        // The advertising timeout is set to run indefinitely, positive values represent timeout in milliseconds
        long NO_TIMEOUT = 0L;

        // name of the host (appears on other devices)
        String name = gameSettings.getPlayerName() + "'s Spiel";

        Nearby.Connections.startAdvertising(mGoogleApiClient,   // The GoogleApiClient to service the call.
                                            name,               // A human readable name for this endpoint, to appear on other devices. If null or empty, a name will be generated based on the device name or model.
                                            null, /*appMetadata,*/  // Metadata which can be used to prompt the user to launch or install the application. If null, only applications looking for the specified service ID will be able to discover this endpoint.
                                            NO_TIMEOUT,         // The duration of the advertisement in milliseconds, unless stopAdvertising() is called first.
                                                                // If DURATION_INDEFINITE is passed in, the advertisement will continue indefinitely until stopAdvertising() is called.
                                            myConnectionRequestListener)    // A listener notified when remote endpoints request a connection to this endpoint.
                                            .setResultCallback(new ResultCallback<Connections.StartAdvertisingResult>() {
            @Override
            public void onResult(Connections.StartAdvertisingResult result) {
                if (result.getStatus().isSuccess()) {
                    // Device is advertising
                    Log.i(TAG, "Started advertising...");
                    isAdvertising = true;
                    lblStatus.setTextColor(colorsLabelStatus);
                    lblStatus.setText("Suche Mitspieler...");

                } else {

                    // Advertising failed - see statusCode for more details
                    int statusCode = result.getStatus().getStatusCode();

                    Log.e(TAG, "Advertising failed with status code: " + statusCode);
                    lblStatus.setTextColor(Color.RED);
                    lblStatus.setText("Fehler: " + statusCode);
                }
            }
        });
    }


    /**
     * Stop advertising
     */
    private void stopAdvertising(){
        if(isAdvertising){
            Nearby.Connections.stopAdvertising(mGoogleApiClient);
        }
    }

    /**
     * Called when the user pressed the back button
     */
    @Override
    public void onBackPressed() {

        if(gameLogic.isGameStarted()){

            // ask user if he really wants to exit
            boolean sure = false;
            DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    switch (which){
                        case DialogInterface.BUTTON_POSITIVE:

                            // disconnect from Google Play services
                            if(mGoogleApiClient != null && mGoogleApiClient.isConnected())
                                mGoogleApiClient.disconnect();

                            startActivity(new Intent(GameHost.this, Hauptmenue.class));

                            finish();
                            break;

                        case DialogInterface.BUTTON_NEGATIVE:
                            //No button clicked
                            break;
                    }
                }
            };

            AlertDialog.Builder builder = new AlertDialog.Builder(GameHost.this);

            builder.setMessage(getString(R.string.msgConfirmExit)).setPositiveButton("Ja", dialogClickListener)
                    .setNegativeButton("Nein", dialogClickListener).show();
        }else {
            stopAdvertising();
        }
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
