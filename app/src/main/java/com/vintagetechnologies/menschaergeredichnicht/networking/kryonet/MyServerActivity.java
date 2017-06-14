package com.vintagetechnologies.menschaergeredichnicht.networking.kryonet;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
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
import com.vintagetechnologies.menschaergeredichnicht.Hauptmenue;
import com.vintagetechnologies.menschaergeredichnicht.R;
import com.vintagetechnologies.menschaergeredichnicht.networking.Network;

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

		myServer = new MyServer(this);

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
	public void onReceived(final Connection connection, final Object object) {

		if(!(object instanceof String))
			return;

		String[] data = ((String) object).split(Network.MESSAGE_DELIMITER);
		String tag = data[0];

		if(Network.TAG_PLAYER_NAME.equals(tag)) {	// add player to game

			String playerName = data[1];

			// check if name is already taken, if taken rename to "Username 2", "Username 3", ...
			if(playerNames.contains(playerName)) {
				int c = 2;
				while (playerNames.contains(playerName + " " + c)) { c++; }

				playerName += " " + c;
			}

			playerNames.add(playerName);

			listAdapter.notifyDataSetChanged();

			if (enoughPlayersConnected())
				btnStartGame.setEnabled(true);

			Toast.makeText(getApplicationContext(), playerName + getString(R.string.msgPlayerJustJoinedTheGame),
					Toast.LENGTH_LONG).show();
		}
	}


	@Override
	public void onConnected(Connection connection) {
	}


	@Override
	public void onDisconnected(final Connection connection) {

		// remove player from list
		String playerName = "";
		try {
			playerName = gameLogic.getDevices().getDevice(connection).getName();
		}catch (NullPointerException e){
			e.printStackTrace();
		}


		for (int i = 0; i < playerNames.size(); i++) {
			if(playerNames.get(i).equals(playerName)){
				playerNames.remove(i);
				listAdapter.notifyDataSetChanged();
				break;
			}
		}

		gameLogic.getDevices().remove(connection);

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


	/**
	 * Called when the user pressed the back button.
	 */
	@Override
	public void onBackPressed() {
		if(!enoughPlayersConnected())
			exit();
		else
			showConfirmExitDialog();
	}


	/**
	 * Exits the hosting activity.
	 */
	private void exit(){
		myServer.getServer().stop();

		// show main menu
		Intent intent = new Intent(this, Hauptmenue.class);
		startActivity(intent);

		finish();
	}


	/**
	 * Show dialog if the user really wants to exit.
	 */
	private void showConfirmExitDialog(){
		String message = getString(R.string.msgConfirmExitHost);

		DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				switch (which){
					case DialogInterface.BUTTON_POSITIVE:
						exit();
						break;

					case DialogInterface.BUTTON_NEGATIVE:
						//No button clicked
						break;
				}
			}
		};

		AlertDialog.Builder builder = new AlertDialog.Builder(MyServerActivity.this);
		builder.setMessage(message).setPositiveButton("Ja", dialogClickListener)
				.setNegativeButton("Nein", dialogClickListener).show();
	}
}
