package com.vintagetechnologies.menschaergeredichnicht;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.vintagetechnologies.menschaergeredichnicht.Impl.ActualGame;
import com.vintagetechnologies.menschaergeredichnicht.networking.Device;
import com.vintagetechnologies.menschaergeredichnicht.networking.kryonet.MyClient;
import com.vintagetechnologies.menschaergeredichnicht.networking.kryonet.NetworkListener;
import com.vintagetechnologies.menschaergeredichnicht.structure.Game;

import static com.vintagetechnologies.menschaergeredichnicht.networking.Network.DATAHOLDER_GAMELOGIC;
import static com.vintagetechnologies.menschaergeredichnicht.networking.Network.DATAHOLDER_GAMESETTINGS;
import static com.vintagetechnologies.menschaergeredichnicht.networking.Network.MESSAGE_DELIMITER;
import static com.vintagetechnologies.menschaergeredichnicht.networking.Network.TAG_PLAYER_HAS_CHEATED;
import static com.vintagetechnologies.menschaergeredichnicht.networking.Network.TAG_PLAYER_NAME;
import static com.vintagetechnologies.menschaergeredichnicht.networking.Network.TAG_REVEAL;

/**
 * Created by Fabio on 04.05.17.
 *
 * Implements the game rules and logic. Sends and receives messages to/from other players.
 * TODO: implement own classes GameLogicClient, GameLogicHost which extend GameLogic...
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

		// send name to host
		GameSettings gameSettings = (GameSettings) DataHolder.getInstance().retrieve(DATAHOLDER_GAMESETTINGS);
		sendToHost( TAG_PLAYER_NAME + MESSAGE_DELIMITER + gameSettings.getPlayerName());
	}

	@Override
	public void onDisconnected(Connection connection) {
		Toast.makeText(	getActivity().getApplicationContext(),
				getActivity().getString(R.string.hostEndedGame), Toast.LENGTH_LONG).show();

		leaveGame();
	}

	@Override
	protected void parseMessage(Connection connection, Object object) {

		if(object instanceof ActualGame) {

			ActualGame game = (ActualGame) object;

			ActualGame.refreshGameInstance(game);

		} else if (object instanceof  GameSettings) {

			GameSettings remoteGameSettings = (GameSettings) object;

			GameSettings gameSettings = (GameSettings) DataHolder.getInstance().retrieve(DATAHOLDER_GAMESETTINGS);

			gameSettings.apply(remoteGameSettings);

		} else if (object instanceof String){

			String[] data = ((String) object).split(MESSAGE_DELIMITER);
			String tag = data[0];
			String value = data[1];

			if(TAG_PLAYER_NAME.equals(tag)){
				hostDevice.setName(value);

			} else if(TAG_REVEAL.equals(tag)){
				boolean hasRemotePlayerCheated = Boolean.valueOf(value);


			} else if(TAG_PLAYER_HAS_CHEATED.equals(tag)){

			}

		} else {
			Log.w(TAG, "Received unknown message from host.");
		}
	}


	@Override
	public void startGame() {
		setGameStarted(true);

		// show main menu
		Intent intent = new Intent(getActivity(), Spieloberflaeche.class);
		getActivity().startActivity(intent);
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

		/*
		if(message instanceof String){
			String msgString = (String) message;
			if(!((String)message).contains(MESSAGE_DELIMITER))
				msgString += MESSAGE_DELIMITER;
		}
		*/

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
