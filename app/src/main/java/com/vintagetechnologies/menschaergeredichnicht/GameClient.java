package com.vintagetechnologies.menschaergeredichnicht;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.nfc.Tag;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
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

import static com.google.android.gms.nearby.connection.Connections.DURATION_INDEFINITE;

/**
 * Created by Fabio on 08.04.17.
 *
 * Class that searches for available hosts nearby and displays them in a list.
 * The user can click on a host in the list to join the game of the host.
 */
public class GameClient extends AppCompatActivity implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        Connections.MessageListener/*,
        Connections.ConnectionRequestListener,
        Connections.EndpointDiscoveryListener */ {


    private GameLogic gameLogic;

    private GameSettings gameSettings;
    private TextView lblStatus, lblSelectGame;
    private ProgressBar pbLoading;
    private ListView listViewHosts;
    private ColorStateList colorsLabelStatus;
    private ArrayAdapter<String> listAdapter;

    private static final String TAG = MainActivity.class.getSimpleName();

    /* used to display a list of hosts */
    private ArrayList<String> hostNames;

    /* GoogleApiClient for connecting to the Nearby Connections API */
    private GoogleApiClient mGoogleApiClient;

    /* For host discovery */
    private Connections.EndpointDiscoveryListener myEndpointDiscoveryListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_game);

        // get controls
        lblStatus = (TextView) findViewById(R.id.lblJoinStatus);
        lblSelectGame = (TextView) findViewById(R.id.lblSelectHostFromList);
        listViewHosts = (ListView) findViewById(R.id.listViewHosts);
        pbLoading = (ProgressBar) findViewById(R.id.pbLoading);

        //lblSelectGame.setVisibility(View.INVISIBLE);
        lblSelectGame.setText(getString(R.string.strSelectGameFromList));
        colorsLabelStatus = lblStatus.getTextColors();   // save textview color for restoring when changed

        hostNames = new ArrayList<>();
        listAdapter = new ArrayAdapter<>(getApplicationContext(), R.layout.my_listview_item, hostNames);    /* original layout: android.R.layout.simple_spinner_item */
        listViewHosts.setAdapter(listAdapter);

        // prevent phone from entering sleep mode
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        // create new game logic for the host
        gameLogic = new GameLogic(this, mGoogleApiClient, false);

        gameSettings = (GameSettings) DataHolder.getInstance().retrieve("GAMESETTINGS");

        listViewHosts.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String item = (String) parent.getItemAtPosition(position);
                hostsListItemClicked(item);
            }
        });

        myEndpointDiscoveryListener =
                new Connections.EndpointDiscoveryListener() {
                    @Override
                    public void onEndpointFound(String endpointId,
                                                String serviceId,
                                                String name) {
                        GameClient.this.onEndpointFound(endpointId,serviceId, name);
                    }

                    @Override
                    public void onEndpointLost(String remoteEndpointId) {
                        GameClient.this.onEndpointLost(remoteEndpointId);
                    }
                };


        // init google api client
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Nearby.CONNECTIONS_API)
                .build();

        // for testing:
        hostNames.add(gameSettings.getPlayerName());
        listAdapter.notifyDataSetChanged();
    }


    /**
     * Called when the users clicks on a host in the host list to connect
     * @param endpointName
     */
    private void hostsListItemClicked(String endpointName){

        Log.i(TAG, "Connecting to host: " + endpointName);

        String endpointId = gameLogic.getDevices().getDeviceByPlayerName(endpointName).getId();

        // initiate connection to the host
        connectTo(endpointId, endpointName);
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


    /**
     * Called when the connection to Google Play service was successful
     * @param bundle
     */
    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.i(TAG, "Connected to Google Play services.");

        if(isDiscovering)
            return;

        // search for hosts
        startDiscovery();
    }

    private boolean isDiscovering = false;


    @Override
    public void onConnectionSuspended(int i) {
        Log.e(TAG, "onConnectionSuspended()");
    }


    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.e(TAG, "onConnectionFailed()");
    }


    /**
     * Called when a host is found. Adds the host to the list of available hosts.
     * @param endpointId The ID of the remote endpoint that was discovered.
     * @param serviceId The ID of the service of the remote endpoint.
     * @param endpointName The human readable name of the remote endpoint.
     */
    /*@Override*/
    public void onEndpointFound(final String endpointId, String serviceId, final String endpointName) {
        // This device is discovering endpoints and has located an advertiser.

        // TODO: create new layout with list for hosts and button to connect, then use this method
        // to display the endpointName in the list and a button to connect (call connectTo()).

        Log.i(TAG, "Found host: " + endpointName);
        hostNames.add(endpointName);
        listAdapter.notifyDataSetChanged();
        lblSelectGame.setVisibility(View.VISIBLE);
    }


    /**
     * Called when a remote endpoint is no longer discoverable; only called for endpoints that previously had been passed to onEndpointFound()
     * @param endpointid
     */
    /*@Override*/
    public void onEndpointLost(String endpointid) {

        if(!gameLogic.isGameStarted()){

            // get device
            Device disconnectedDevice = gameLogic.getDevices().getDeviceByPlayerID(endpointid);
            String deviceName = disconnectedDevice.getName();

            // remove from host list
            if(disconnectedDevice != null){
                for (int i = 0; i < hostNames.size(); i++) {
                    if(hostNames.get(i).equals(deviceName)){
                        hostNames.remove(i);
                        listAdapter.notifyDataSetChanged();
                        break;
                    }
                }
            }

            if(hostNames.isEmpty())
                lblSelectGame.setVisibility(View.INVISIBLE);
        }
    }


    @Override
    public void onMessageReceived(String remoteEndpointId, byte[] payload, boolean isReliable) {

        // send received message for processing to 'game logic'
        gameLogic.receivedMessage(remoteEndpointId, new String(payload));
    }


    @Override
    public void onDisconnected(String s) {
        Log.d(TAG, "onDisconnected()");
    }


    /**
     * Called by a player to connect to the host
     * @param remoteEndpointId The ID to which connect to
     * @param endpointName
     */
    private void connectTo(String remoteEndpointId, final String endpointName) {

        // Send a connection request to a remote endpoint. By passing 'null' for the name, the Nearby Connections API will construct a default name
        // based on device model such as 'LGE Nexus 5'.
        String myName = gameSettings.getPlayerName();
        byte[] myPayload = null;

        Nearby.Connections.sendConnectionRequest(mGoogleApiClient, myName,
                remoteEndpointId, myPayload, new Connections.ConnectionResponseCallback() {
                    @Override
                    public void onConnectionResponse(String remoteEndpointId, Status status, byte[] bytes) {

                        if (status.isSuccess()) {
                            // Successful connection
                            gameLogic.getDevices().addDevice(new Device(remoteEndpointId, endpointName, true));
                            stopDiscovery();

                        } else {
                            // Failed connection
                            lblStatus.setText("Verbindung fehlgeschlagen");
                            lblStatus.setTextColor(Color.RED);
                            pbLoading.setVisibility(View.INVISIBLE);
                            Log.e(TAG, "Failed to connect to host");
                        }
                    }
                }, this);
    }


    /**
     * Called by the clients/players to discover the host(s) in the WiFi network
     */
    private void startDiscovery() {

        if (!isConnectedToWiFiNetwork()) {
            // Implement logic when device is not connected to a network
            Toast.makeText(getApplicationContext(), "Bitte stelle eine WiFi Verbindung her!", Toast.LENGTH_LONG).show();
            return;
        }

        String serviceId = getString(R.string.service_id);

        // Set an appropriate timeout length in milliseconds
        long DISCOVER_TIMEOUT = DURATION_INDEFINITE;    //1000L;

        // Discover nearby apps that are advertising with the required service ID.
        Nearby.Connections.startDiscovery(mGoogleApiClient,     // The GoogleApiClient to service the call.
                serviceId,                                      // The ID for the service to be discovered, as specified in its manifest.
                DISCOVER_TIMEOUT,       // The duration of discovery in milliseconds, unless stopDiscovery() is called first. If DURATION_INDEFINITE is passed in, discovery will continue indefinitely until stopDiscovery() is called.
                myEndpointDiscoveryListener)                    // A listener notified when a remote endpoint is discovered.
                .setResultCallback(new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        if (status.isSuccess()) {
                            // Device is discovering
                            lblStatus.setText(getString(R.string.msgSearchingGameHost));
                            lblStatus.setTextColor(colorsLabelStatus);
                            pbLoading.setVisibility(View.VISIBLE);
                            Log.i(TAG, "Started discovery...");
                            isDiscovering = true;

                        } else {
                            int statusCode = status.getStatusCode();
                            // Advertising failed - see statusCode for more details
                            Log.e(TAG, "Discovery failed with status code: " + statusCode);
                            lblStatus.setText(getString(R.string.msgHostSearchFailed));
                            lblStatus.setTextColor(Color.RED);
                            pbLoading.setVisibility(View.INVISIBLE);
                            isDiscovering = false;
                        }
                    }
                });
    }


    /**
     * Call to end the discovery
     */
    private void stopDiscovery(){
        if(isDiscovering){
            Nearby.Connections.stopDiscovery(mGoogleApiClient, getString(R.string.service_id));
            isDiscovering = false;
            Log.i(TAG, "Stopped discovery");
        }
    }


    /**
     * Called when the user pressed the back button
     */
    @Override
    public void onBackPressed() {
        //moveTaskToBack(true);

        stopDiscovery();

        // disconnect from Google Play services
        if(mGoogleApiClient != null && mGoogleApiClient.isConnected())
            mGoogleApiClient.disconnect();

        startActivity(new Intent(this, Hauptmenue.class));

        finish();
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
