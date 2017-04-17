package com.vintagetechnologies.menschaergeredichnicht;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.nfc.Tag;
import android.os.Bundle;
import android.os.Handler;
import android.os.UserManager;
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
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

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

	/* used to store a host's (value) with it's name (key) as it is in the list view */
	private Map<String, String> hosts;

    private static final String TAG = MainActivity.class.getSimpleName();

    /* used to display found hosts in a list view */
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

        lblSelectGame.setText(getString(R.string.strSelectGameFromList));
        colorsLabelStatus = lblStatus.getTextColors();   // save textview color for restoring when changed

        hostNames = new ArrayList<>();
        listAdapter = new ArrayAdapter<>(getApplicationContext(), R.layout.my_listview_item, hostNames);    /* original layout: android.R.layout.simple_spinner_item */
        listViewHosts.setAdapter(listAdapter);

		hosts = new HashMap<>(1);

        // prevent phone from entering sleep mode
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

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

        // create new game logic for the host
        gameLogic = new GameLogic(this, mGoogleApiClient, false);
    }


    /**
     * Called when the users clicks on a host in the host list to connect
     * @param endpointName
     */
    private void hostsListItemClicked(String endpointName){

		endpointName = endpointName.substring(0, endpointName.length() - "'s Spiel".length());

        Log.i(TAG, "Connecting to host: " + endpointName);

        String endpointId = hosts.get(endpointName);

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
     * @param connectionHint
     */
    @Override
    public void onConnected(@Nullable Bundle connectionHint) {
        Log.i(TAG, "Connected to Google Play services.");

        if(isDiscovering)
            return;

		hostNames.clear();
		listViewHosts.deferNotifyDataSetChanged();
		hosts.clear();

        // search for hosts
        startDiscovery();
    }

    private boolean isDiscovering = false;


    /**
     * Called when the client is temporarily in a disconnected state.
     * This can happen if there is a problem with the remote service
     * (e.g. a crash or resource problem causes it to be killed by the system).
     * When called, all requests have been canceled and no outstanding listeners will be executed.
     * GoogleApiClient will automatically attempt to restore the connection.
     * Applications should disable UI components that require the service,
     * and wait for a call to onConnected(Bundle) to re-enable them.
	 * https://developers.google.com/android/reference/com/google/android/gms/common/api/GoogleApiClient.ConnectionCallbacks.html#onConnectionSuspended(int)
	 * CAUSE_NETWORK_LOST = 2			A suspension cause informing you that a peer device connection was lost.
	 * CAUSE_SERVICE_DISCONNECTED = 1	A suspension cause informing that the service has been killed.
     * @param cause The reason for the disconnection. Defined by constants CAUSE_*.
     */
    @Override
    public void onConnectionSuspended(int cause) {
        Log.i(TAG, "Temporarily disconnected from Google Play service (cause = " + cause + ").");
        Toast.makeText(getApplicationContext(), "Verbindung zu Play Service temporär unterbrochen (Grund " + cause + ")", Toast.LENGTH_LONG).show();

		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				mGoogleApiClient.connect();
			}
		}, 1000);
    }


    /**
     * Called when there was an error connecting the client to the service.
     * @param connectionResult A ConnectionResult that can be used for resolving the error, and deciding what sort of error occurred.
     */
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.e(TAG, "onConnectionFailed()");
		Toast.makeText(getApplicationContext(), "Verbindung zum Host fehlgeschlagen. Bitte erneut versuchen.", Toast.LENGTH_LONG).show();
    }


    /**
     * Called when a host is found. Adds the host to the list of available hosts.
     * @param remoteEndpointId The ID of the remote endpoint that was discovered.
     * @param serviceId The ID of the service of the remote endpoint.
     * @param endpointName The human readable name of the remote endpoint.
     */
    /*@Override*/
    public void onEndpointFound(final String remoteEndpointId, String serviceId, final String endpointName) {
        // This device is discovering endpoints and has located an advertiser.

		// add to visual list
		hostNames.add(endpointName);
		listAdapter.notifyDataSetChanged();
		lblSelectGame.setVisibility(View.VISIBLE);

        // make "Max's Spiel" to "Max"
        String realName = endpointName.substring(0, endpointName.length() - "'s Spiel".length());

        Log.i(TAG, "Found host: " + realName);

		// add to host list
		hosts.put(realName, remoteEndpointId);
    }


    /**
     * Called when a remote endpoint is no longer discoverable; only called for endpoints that previously had been passed to onEndpointFound()
     * @param endpointid
     */
    /*@Override*/
    public void onEndpointLost(String endpointid) {

		// get device name from id
		String deviceName = null;

		Iterator it = hosts.keySet().iterator();
		while (it.hasNext()){
			String value = String.valueOf(it.next());
			String key = hosts.get(value);
			if(key.equals(endpointid)){
				deviceName = value;
				break;
			}
		}

		hosts.remove(deviceName);

		if(!gameLogic.isGameStarted()){		// game started

			Toast.makeText(getApplicationContext(), "Verbindung zum Host '" + deviceName + "' verloren! Spiel abgebrochen.", Toast.LENGTH_LONG).show();

			gameLogic.endGame();

			// start discovery again
			if(hostNames.isEmpty()){
				lblSelectGame.setText("Gefundene Spiele:");
				if(!isDiscovering)
					startDiscovery();
			}

		} else {	// game not started

			// remove from host list
			String listName = deviceName + "'s Spiel";
			for (int i = 0; i < hostNames.size(); i++) {
				if(hostNames.get(i).equals(listName)){
					hostNames.remove(i);
					listAdapter.notifyDataSetChanged();
					break;
				}
			}
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

                        if (status.isSuccess()) {	// Successfully connected to host

                            gameLogic.getDevices().addDevice(new Device(remoteEndpointId, endpointName, true));
                            stopDiscovery();
							//Toast.makeText(getApplicationContext(), "Verbunden", Toast.LENGTH_LONG).show();
							Log.i(TAG, "Connected to host: " + endpointName);
							lblSelectGame.setText("Verbunden mit " + endpointName);
							lblStatus.setTextColor(colorsLabelStatus);
							lblStatus.setText("Warte auf Spielstart...");
							pbLoading.setVisibility(View.VISIBLE);


                        } else {	// Failed connection

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

                        if (status.isSuccess()) {   // Device is discovering

                            lblStatus.setText(getString(R.string.msgSearchingGameHost));
                            lblStatus.setTextColor(colorsLabelStatus);
                            pbLoading.setVisibility(View.VISIBLE);
                            Log.i(TAG, "Started discovery...");
                            isDiscovering = true;

                        } else {    // Advertising failed - see statusCode for more details

                            int statusCode = status.getStatusCode();
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

		// disconnect from host
		if(gameLogic.isGameStarted()){
			//Nearby.Connections.disconnectFromEndpoint(mGoogleApiClient, gameLogic.getDevices().getHost().getId());	// only for host??
		}

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


    /**
     * Called when the orientation changes (i.e. from portrait to landscape mode)
     * @param newConfig
     */
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        // check https://developer.android.com/guide/components/activities/activity-lifecycle.html
        // on how to recover activity state -> What when activity destroyed while discovering? So... don't support landscape for game search/join?

        // Checks the orientation of the screen
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            //setContentView(R.layout.activity_join_game);
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT){
            //setContentView(R.layout.activity_join_game);
        }
    }

}
