package com.vintagetechnologies.menschaergeredichnicht;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Server;
import com.vintagetechnologies.menschaergeredichnicht.implementation.ActualGame;
import com.vintagetechnologies.menschaergeredichnicht.implementation.RealDice;
import com.vintagetechnologies.menschaergeredichnicht.networking.Device;
import com.vintagetechnologies.menschaergeredichnicht.networking.kryonet.MyServer;
import com.vintagetechnologies.menschaergeredichnicht.networking.kryonet.NetworkListener;
import com.vintagetechnologies.menschaergeredichnicht.structure.DiceNumber;
import com.vintagetechnologies.menschaergeredichnicht.structure.GamePiece;
import com.vintagetechnologies.menschaergeredichnicht.structure.Player;
import com.vintagetechnologies.menschaergeredichnicht.synchronisation.GameSynchronisation;

import java.util.List;

import static com.vintagetechnologies.menschaergeredichnicht.networking.Network.DATAHOLDER_GAMELOGIC;
import static com.vintagetechnologies.menschaergeredichnicht.networking.Network.DATAHOLDER_GAMESETTINGS;
import static com.vintagetechnologies.menschaergeredichnicht.networking.Network.MESSAGE_DELIMITER;
import static com.vintagetechnologies.menschaergeredichnicht.networking.Network.TAG_CLIENT_PLAYER_NAME;
import static com.vintagetechnologies.menschaergeredichnicht.networking.Network.TAG_MOVE_PIECE;
import static com.vintagetechnologies.menschaergeredichnicht.networking.Network.TAG_PLAYER_HAS_CHEATED;
import static com.vintagetechnologies.menschaergeredichnicht.networking.Network.TAG_PLAYER_NAME;
import static com.vintagetechnologies.menschaergeredichnicht.networking.Network.TAG_REVEAL;
import static com.vintagetechnologies.menschaergeredichnicht.networking.Network.TAG_START_GAME;

/**
 * Created by Fabio on 04.05.17.
 *
 * Implements the game rules and logic. Sends and receives messages to/from other players.
 */
public class GameLogicHost extends GameLogic implements NetworkListener {


	private final String TAG = GameLogic.class.getSimpleName();

	private MyServer myServer;

	private Server server;

	private GameSettings gameSettings;


	public GameLogicHost(Activity activity, MyServer myServer){
		super(activity, true);
		this.myServer = myServer;
		this.server = myServer.getServer();

		myServer.addListener(this);

		DataHolder.getInstance().save(DATAHOLDER_GAMELOGIC, this);

		gameSettings = (GameSettings) DataHolder.getInstance().retrieve(DATAHOLDER_GAMESETTINGS);

		// add host to device list
		Device hostDevice = new Device(gameSettings.getPlayerName(), true);
		getDevices().add(hostDevice);
	}


	/**
	 * Called when the player receives a message from another player.
	 * @param connection The remote endpoint.
	 * @param object The received object.
	 */
	@Override
	public void onReceived(Connection connection, Object object) {
		parseMessage(connection, object);
	}


	@Override
	public void onConnected(Connection connection) {
		// send name to client
		sendMessageToClient(connection.getID(), TAG_PLAYER_NAME + MESSAGE_DELIMITER + gameSettings.getPlayerName());

		// add device
		getDevices().add(new Device(connection.getID(), false));
	}


	@Override
	public void onDisconnected(Connection connection) {

		if(!hasGameStarted())
			return;

		Toast.makeText(	getActivity().getApplicationContext(), getDevices().getDevice(connection).getName() +
						getActivity().getString(R.string.msgPlayerJustLeftTheGame), Toast.LENGTH_LONG).show();

		getDevices().remove(connection);

		// end game if no player is left
		if(getDevices().getCount() < 2){
			leaveGame();
		}
	}


	/**
	 * Sends a message to a player.
	 * @param playerID The ID of the receiver.
	 * @param message The message to be sent.
	 */
	public void sendMessageToClient(final int playerID, final Object message) {
		Thread sendingThread = new Thread(new Runnable() {
			@Override
			public void run() {
				server.sendToTCP(playerID, message);
			}
		}, "Sending");

		sendingThread.start();
	}


	/**
	 * Sends a message to a player.
	 * @param playerName The name of the player.
	 * @param message The message to be sent.
	 */
	public void sendMessageToClient(String playerName, Object message){
		sendMessageToClient(getDevices().getPlayer(playerName).getId(), message);
	}


	/**
	 * Called by the host to broadcast a message to all clients.
	 * @param message The message to be sent.
	 */
	public void sendToAllClientDevices(final Object message){
		Thread sendingThread = new Thread(new Runnable() {
			@Override
			public void run() {
				server.sendToAllTCP(message);
			}
		}, "Sending");

		sendingThread.start();
	}


	/**
	 * Called when the player wants to exit the game.
	 */
	@Override
	public void leaveGame(){
		Log.i(TAG, "Closing connection to all clients and stopping server...");
		server.stop();

		// show main menu
		Intent intent = new Intent(getActivity(), Hauptmenue.class);
		getActivity().startActivity(intent);

		getActivity().finish();
	}


	/**
	 * Called if the host started the game. Devices will no longer be able to connect.
	 */
	public void startGame(){
		setGameStarted(true);

		// send host settings
		sendToAllClientDevices(gameSettings);

		// send name of players to clients
		List<Device> devices = getDevices().getList();

		int uniqueId = 10;
		for(int i = 0; i < devices.size(); i++){
			Device device = devices.get(i);
			String message = TAG_CLIENT_PLAYER_NAME + MESSAGE_DELIMITER + device.getName() + MESSAGE_DELIMITER + uniqueId++;
			sendToAllClientDevices(message);
		}

		// show main menu
		Intent intent = new Intent(getActivity(), Spieloberflaeche.class);
		getActivity().startActivity(intent);

		sendToAllClientDevices(TAG_START_GAME);
	}


	/**
	 * Parse and execute a received message.
	 * @param connection The remote endpoint.
	 * @param object The received object.
	 */
	@Override
	protected void parseMessage(Connection connection, Object object){
		// TODO: extend for other types of message (e.g. aufdecken, ...)

		Device clientDevice = getDevices().getDevice(connection);

		// execute message based on tag
		if (object instanceof Player) {

			// this is called when the client had to choose which figure to move...
			// the moved figure is sent to host, which sends it to all other clients

			Player player = (Player) object;
			ActualGame.refreshPlayer(player);    // replace current game class with new one

			GameSynchronisation.synchronize();

		} else if(object instanceof DiceNumber) {

			// client hat gewuerfelt
			synchronized (RealDice.get()) {
				RealDice.get().setDiceNumber((DiceNumber) object);
				RealDice.get().notify();
			}

		} else if(object instanceof GamePiece){

			GamePiece gamePiece = (GamePiece) object;
			ActualGame.getInstance().getBoardView().setHighlightedGamePiece(gamePiece);
			ActualGame.getInstance().getBoardView().invalidate();

		} else if(object instanceof String) {	// string message

			String[] data = ((String) object).split(MESSAGE_DELIMITER);
			String tag = data[0];
			String value = data[1];

			if(TAG_PLAYER_NAME.equals(tag)){

				getDevices().getDevice(connection).setName(value);

				//GameSynchronisation.synchronize(); noch nicht..

			} else if(TAG_PLAYER_HAS_CHEATED.equals(tag)) {

				boolean hasCheated = Boolean.parseBoolean(value);	// is always true at that stage..

				if(hasCheated){
					// display who has to skip

					/*
					Toast.makeText(getActivity().getApplicationContext(),
							playerName + " hat geschummelt und muss aussetzen!",
							Toast.LENGTH_LONG).show();
					*/
				}

				// TODO: implement, is implemented?
				// set player cheating/or not
				ActualGame.getInstance().getGameLogic().getPlayerByName(clientDevice.getName()).getSchummeln().setPlayerCheating(hasCheated);

				// send status update to others
				GameSynchronisation.synchronize();

			} else if(TAG_REVEAL.equals(tag)){	// a player clicked "aufdecken"

				// check if current player has cheated!
				Player currentPlayer = ActualGame.getInstance().getGameLogic().getCurrentPlayer();
				boolean isCheating = currentPlayer.getSchummeln().isPlayerCheating();

				// send to others if player has cheated, just to display informations
				sendToAllClientDevices(TAG_PLAYER_HAS_CHEATED + MESSAGE_DELIMITER + String.valueOf(isCheating));

				currentPlayer.setHasToSkip(isCheating);
				currentPlayer.getSchummeln().setPlayerCheating(false);

				// player who clicked "aufdecken"
				String revealerName = getDevices().getDevice(connection).getName();
				Player playerWhoRevealed = ActualGame.getInstance().getGameLogic().getPlayerByName(revealerName);
				playerWhoRevealed.setHasToSkip(!isCheating);

				GameSynchronisation.synchronize();

				// send toast
				String message = currentPlayer.getName() + " hat gecheated & muss aussetzen!";
				GameSynchronisation.sendToast(message);

			} else if(TAG_MOVE_PIECE.equals(tag)) {

				int number = Integer.parseInt(value);

				synchronized (RealDice.get()) {
					RealDice.get().getDiceNumber().setNumber(number);
					RealDice.get().notify();
				}

				Button btnMoveFigur = ((Spieloberflaeche) getActivity()).getBtnMoveFigur();
				btnMoveFigur.callOnClick();

				ActualGame.getInstance().getBoardView().invalidate();

			}


		}else {
			Log.w(TAG, String.format("Received unknown message '%s' from player '%s'", object, clientDevice.getName()));
		}
	}

	@Override
	public void setActivity(Activity activity) {
		super.setActivity(activity);
		myServer.setCallingActivity(activity);
	}

}
