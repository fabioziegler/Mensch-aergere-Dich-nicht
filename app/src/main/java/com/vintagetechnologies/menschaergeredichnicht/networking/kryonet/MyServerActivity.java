package com.vintagetechnologies.menschaergeredichnicht.networking.kryonet;

import android.content.res.ColorStateList;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.esotericsoftware.kryonet.Connection;
import com.vintagetechnologies.menschaergeredichnicht.GameLogicHost;
import com.vintagetechnologies.menschaergeredichnicht.R;

import java.util.ArrayList;

/**
 * Created by Fabio on 02.05.17.
 */

public class MyServerActivity extends AppCompatActivity implements NetworkListener {

	/* server */
	private MyServer myServer;


	/* controls */
	private Button btnStartGame;
	private TextView lblStatus;
	private ColorStateList colorsLabelStatus;
	private ListView listViewPlayersJoined;

	/* for displaying joined players */
	private ArrayAdapter<String> listAdapter;
	private ArrayList<String> playerNames;

	/* other stuff */
	private GameLogicHost gameLogic;


	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		initGui();

		myServer = new MyServer();

		myServer.addListener(this);

		myServer.initializeServer();

		gameLogic = new GameLogicHost(this, myServer);
	}


	private void btnStartGameClicked(){

		if(!enoughPlayersConnected()){
			return;
		}

		myServer.removeListener(this);

		gameLogic.startGame();

		finish();
	}


	@Override
	public void onReceived(Connection connection, Object object) {
		if(!(object instanceof String))
			return;

		// add player to game
		String playerName = (String) object;

		playerNames.add(playerName);

		listAdapter.notifyDataSetChanged();

		if(enoughPlayersConnected())
			btnStartGame.setEnabled(true);

		Toast.makeText(getApplicationContext(), playerName + getString(R.string.msgPlayerJustJoinedTheGame),
				Toast.LENGTH_LONG).show();
	}


	@Override
	public void onConnected(Connection connection) {
	}


	@Override
	public void onDisconnected(Connection connection) {

		// remove player from list
		String playerName = gameLogic.getDevices().getDevice(connection).getName();

		for (int i = 0; i < playerNames.size(); i++) {
			if(playerNames.get(i).equals(playerName)){
				playerNames.remove(i);
				listAdapter.notifyDataSetChanged();
				break;
			}
		}

		if(!enoughPlayersConnected())
			btnStartGame.setEnabled(false);
	}


	private void initGui(){
		setContentView(R.layout.activity_gamehost);

		// get controls
		btnStartGame = (Button) findViewById(R.id.btnStartGame);
		lblStatus = (TextView) findViewById(R.id.lblStatus);
		listViewPlayersJoined = (ListView) findViewById(R.id.listViewPlayers);

		// init list view
		playerNames = new ArrayList<>(3);
		listAdapter = new ArrayAdapter<>(getApplicationContext(), R.layout.my_listview_item, playerNames);  /* original layout: android.R.layout.simple_spinner_item */
		listViewPlayersJoined.setAdapter(listAdapter);

		// save def. color
		colorsLabelStatus = lblStatus.getTextColors();   // save textview color for restoring when changed

		// listeners
		btnStartGame.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				btnStartGameClicked();
			}
		});

		// prevent phone from entering sleep mode
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

		btnStartGame.setEnabled(false);
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


	private boolean enoughPlayersConnected(){
		return playerNames.size() > 0;
	}
}
