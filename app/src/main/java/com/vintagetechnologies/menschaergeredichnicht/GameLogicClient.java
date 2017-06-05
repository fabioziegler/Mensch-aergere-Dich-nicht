package com.vintagetechnologies.menschaergeredichnicht;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.vintagetechnologies.menschaergeredichnicht.Impl.ActualGame;
import com.vintagetechnologies.menschaergeredichnicht.networking.Device;
import com.vintagetechnologies.menschaergeredichnicht.networking.DeviceList;
import com.vintagetechnologies.menschaergeredichnicht.networking.kryonet.MyClient;
import com.vintagetechnologies.menschaergeredichnicht.networking.kryonet.NetworkListener;
import com.vintagetechnologies.menschaergeredichnicht.structure.Player;

import static com.vintagetechnologies.menschaergeredichnicht.networking.Network.DATAHOLDER_GAMELOGIC;
import static com.vintagetechnologies.menschaergeredichnicht.networking.Network.DATAHOLDER_GAMESETTINGS;
import static com.vintagetechnologies.menschaergeredichnicht.networking.Network.MESSAGE_DELIMITER;
import static com.vintagetechnologies.menschaergeredichnicht.networking.Network.TAG_CLIENT_PLAYER_NAME;
import static com.vintagetechnologies.menschaergeredichnicht.networking.Network.TAG_CURRENT_PLAYER;
import static com.vintagetechnologies.menschaergeredichnicht.networking.Network.TAG_PLAYER_HAS_CHEATED;
import static com.vintagetechnologies.menschaergeredichnicht.networking.Network.TAG_PLAYER_NAME;
import static com.vintagetechnologies.menschaergeredichnicht.networking.Network.TAG_STATUS_MESSAGE;
import static com.vintagetechnologies.menschaergeredichnicht.networking.Network.TAG_TOAST;
import static com.vintagetechnologies.menschaergeredichnicht.networking.Network.TAG_WAIT_FOR_MOVE;

/**
 * Created by Fabio on 04.05.17.
 *
 * Implements the game rules and logic. Sends and receives messages to/from other players.
 */
public class GameLogicClient extends GameLogic implements NetworkListener {


	private final String TAG = GameLogic.class.getSimpleName();

	private MyClient myClient;

	private Client client;

	private Device hostDevice;


	public GameLogicClient(Activity activity, MyClient myClient){
		super(activity, false);
		this.myClient = myClient;
		this.client = myClient.getClient();

		myClient.addListener(this);

		DataHolder.getInstance().save(DATAHOLDER_GAMELOGIC, this);
	}


	@Override
	public void onReceived(Connection connection, Object object) {
		parseMessage(connection, object);
	}

	/**
	 * Called when the connection to the host was established.
	 * @param connection
	 */
	@Override
	public void onConnected(Connection connection) {

		hostDevice = new Device(connection.getID(), true);
		getDevices().add(hostDevice);

		// send name to host
		GameSettings gameSettings = (GameSettings) DataHolder.getInstance().retrieve(DATAHOLDER_GAMESETTINGS);
		sendToHost( TAG_PLAYER_NAME + MESSAGE_DELIMITER + gameSettings.getPlayerName());
	}

	@Override
	public void onDisconnected(Connection connection) {
		Toast.makeText(	getActivity().getApplicationContext(),
				getActivity().getString(R.string.hostEndedGame), Toast.LENGTH_LONG).show();

		getDevices().clear();

		leaveGame();
	}

	@Override
	protected void parseMessage(Connection connection, Object object) {

		//if(!hasGameStarted()) return;

		if(object instanceof Player) {	// client received game update

			Player player = (Player) object;

			ActualGame.refreshPlayer(player);

		} else if (object instanceof  GameSettings) {

			GameSettings remoteGameSettings = (GameSettings) object;

			getGameSettings().apply(remoteGameSettings);

		} else if (object instanceof String){

			String[] data = ((String) object).split(MESSAGE_DELIMITER);
			String tag = data[0];
			String value = null;

			try{
				value = data[1];
			} catch (Exception e){
				Log.e(TAG, "Error", e);
			}

			if(TAG_PLAYER_NAME.equals(tag)){	// when receiving the name of the host

				hostDevice.setName(value);

			} else if(TAG_PLAYER_HAS_CHEATED.equals(tag)) {
				//wird vom Host empfangen wenn Spieler aufdeckt?!
				boolean hasCheated = Boolean.parseBoolean(value);
				Context context = getActivity().getApplicationContext();


				if(hasCheated) {
					Toast.makeText(context, "Das Schummeln wurde enttarnt, Schummler stetzt nächste Runde aus", Toast.LENGTH_LONG).show();
				}else{
					Toast.makeText(context, "Es wurde falsch verdächtigt, verdacht äußernder setzt nächste Runde aus", Toast.LENGTH_LONG).show();
				}


				// TODO: not needed: (delete)

				/*
				// if the remote player cheated
				boolean hasCheated = Boolean.valueOf(value);

				if(hasCheated){
					Toast.makeText(getActivity().getApplicationContext(), , Toast.LENGTH_LONG).show();
				}
				*/
			} else if(TAG_TOAST.equals(tag)) {	// show a toast

				String message = value;
				Toast.makeText(getActivity().getApplicationContext(), message, Toast.LENGTH_LONG).show();

			} else if(TAG_CLIENT_PLAYER_NAME.equals(tag)) {	// if hosts send the name of a client

				DeviceList deviceList = getDevices();
				String deviceName = data[1];
				int id = Integer.parseInt(data[2]);

				if(!deviceList.contains(deviceName))
					deviceList.add(new Device(id, deviceName, false));

				deviceList.getPlayer(deviceName).setId(id);

			} else if(TAG_STATUS_MESSAGE.equals(tag)){

				if(hasGameStarted()) {
					Spieloberflaeche activity = (Spieloberflaeche) getActivity();
					activity.setStatus(value);
				}

			} else if(TAG_CURRENT_PLAYER.equals(tag)) {

				Spieloberflaeche activity = (Spieloberflaeche) getActivity();

				// network id of the players who's turn it is.
				String currentPlayerName = value;

				//Currentplayer startet bei "null" als nicht Cheater.
				Player currentPlayer = ActualGame.getInstance().getGameLogic().getCurrentPlayer();
				currentPlayer.getSchummeln().setPlayerCheating(false); //Damit der Würfel weiß, dass noch nicht geschummelt wurde.
				currentPlayer.getSchummeln().informHost(false); //Damit der Host auch weiß, dass (noch) nicht geschummelt wurde.

				if(currentPlayerName.equals(getGameSettings().getPlayerName())){
					activity.setDiceEnabled(true); //Würfeln
					activity.setRevealEnabled(false);  //Aufdecken
					activity.setSensorOn(true); //Schummeln und Würfeln durch Schütteln
				} else {
					activity.setDiceEnabled(false);
					activity.setRevealEnabled(true);
					activity.setSensorOn(false);
				}

			} else if(TAG_WAIT_FOR_MOVE.equals(tag)){

				Thread thread = new Thread(new Runnable() {
					@Override
					public void run() {
						// wait for move piece
						ActualGame.getInstance().getGameLogic()._findPossibleToMove();
						ActualGame.getInstance().waitForMovePiece();

						// send changed to host
						//Player me = ActualGame.getInstance().getGameLogic().getPlayerByName(getGameSettings().getPlayerName());
						//sendToHost(me);
					}
				}, "PlayThread");
				ActualGame.getInstance().getGameLogic().setClientPlayThread(thread);
				thread.start();

			}

		} else {
			Log.w(TAG, "Received unknown message from host.");
		}
	}


	@Override
	public void startGame() {
		// show main menu
		Intent intent = new Intent(getActivity(), Spieloberflaeche.class);
		getActivity().startActivity(intent);

		setGameStarted(true);
	}

	@Override
	public void leaveGame() {
		Log.i(TAG, "Stopping client...");
		client.stop();

		// show main menu
		Intent intent = new Intent(getActivity(), Hauptmenue.class);
		getActivity().startActivity(intent);

		getActivity().finish();
	}


	/**
	 * Send a message to the host.
	 * @param message The message to be sent.
	 */
	public void sendToHost(final Object message) throws IllegalArgumentException {
		if (message == null)
			throw new IllegalArgumentException("Message must not be null.");


		// send in own thread
		Thread sendingThread = new Thread(new Runnable() {
			@Override
			public void run() {
				client.sendTCP(message);
			}
		}, "Sending");

		sendingThread.start();
	}


	@Override
	public void setActivity(Activity activity) {
		super.setActivity(activity);
		myClient.setCallingActivity(activity);
	}
}
