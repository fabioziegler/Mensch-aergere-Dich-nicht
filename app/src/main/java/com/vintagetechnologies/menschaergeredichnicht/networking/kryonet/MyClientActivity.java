package com.vintagetechnologies.menschaergeredichnicht.networking.kryonet;

import android.content.res.ColorStateList;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.esotericsoftware.kryonet.Connection;
import com.vintagetechnologies.menschaergeredichnicht.GameLogicClient;
import com.vintagetechnologies.menschaergeredichnicht.R;
import com.vintagetechnologies.menschaergeredichnicht.networking.Network;

import java.util.ArrayList;

/**
 * Created by Fabio on 02.05.17.
 */

public class MyClientActivity extends AppCompatActivity implements NetworkListener {


	/* client */
	private MyClient myClient;

	/* controls */
	private TextView lblStatus;
	private ProgressBar pbLoading;
	private ListView listViewHosts;
	private ColorStateList colorsLabelStatus;

	/* for displaying joined players */
	private ArrayAdapter<String> listAdapter;
	private ArrayList<String> hostNames;

	/* other stuff */
	private GameLogicClient gameLogic;


	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		initGui();

		myClient = new MyClient();

		myClient.addListener(this);

		myClient.initializeClient();

		gameLogic = new GameLogicClient(this, myClient);

		myClient.discoverHost();
	}


	private void startGame(){

		myClient.removeListener(this);

		gameLogic.startGame();

		finish();
	}


	@Override
	public void onReceived(Connection connection, Object object) {
		if(!(object instanceof String))
			return;

		// start game
		if(Network.TAG_START_GAME.equals(object)){
			startGame();
			return;
		}

		// else, add player to game
		String nameOfHost = (String) object;

		hostNames.add(nameOfHost);

		listAdapter.notifyDataSetChanged();

		Toast.makeText(getApplicationContext(),
				getString(R.string.joinedGame, nameOfHost),
				Toast.LENGTH_LONG).show();
	}


	/**
	 * Called when a connection to the host was established
	 * @param connection
	 */
	@Override
	public void onConnected(Connection connection) {
		lblStatus.setText(R.string.waitingForGameStart);
	}


	/**
	 * Nothing to do. See {@link GameLogicClient#onDisconnected(Connection)}.
	 * @param connection
	 */
	@Override
	public void onDisconnected(Connection connection) {
	}


	/**
	 * Called when the users clicks on a host in the host list to connect.
	 * @param hostName
	 */
	private void hostsListItemClicked(String hostName){

		//endpointName = endpointName.substring(0, endpointName.length() - "'s Spiel".length());
	}


	private void initGui(){

		// show activity
		setContentView(R.layout.activity_join_game);

		// get controls
		lblStatus = (TextView) findViewById(R.id.lblJoinStatus);
		listViewHosts = (ListView) findViewById(R.id.listViewHosts);
		pbLoading = (ProgressBar) findViewById(R.id.pbLoading);

		// save text view color for restoring when changed
		colorsLabelStatus = lblStatus.getTextColors();

		// set up list view
		hostNames = new ArrayList<>();
		listAdapter = new ArrayAdapter<>(getApplicationContext(), R.layout.my_listview_item, hostNames);    /* original layout: android.R.layout.simple_spinner_item */
		listViewHosts.setAdapter(listAdapter);

		// set listeners
		listViewHosts.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				String item = (String) parent.getItemAtPosition(position);
				hostsListItemClicked(item);
			}
		});

		// prevent phone from entering sleep mode
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
	}


	@Override
	protected void onStart() {
		super.onStart();

	}

	@Override
	protected void onStop() {
		super.onStop();
	}

	@Override
	protected void onPause() {
		super.onPause();
	}

	@Override
	protected void onResume() {
		super.onResume();
	}
}
