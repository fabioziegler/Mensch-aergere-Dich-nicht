package com.vintagetechnologies.menschaergeredichnicht.structure;

import android.util.Log;

import com.vintagetechnologies.menschaergeredichnicht.DataHolder;
import com.vintagetechnologies.menschaergeredichnicht.Impl.ActualGame;
import com.vintagetechnologies.menschaergeredichnicht.Impl.DiceImpl;
import com.vintagetechnologies.menschaergeredichnicht.networking.Network;
import com.vintagetechnologies.menschaergeredichnicht.synchronisation.GameSynchronisation;

import java.util.ArrayList;

/**
 * Created by johannesholzl on 06.05.17.
 */

public class GameLogic {

    private int currentPlayer;
    private Player players[];

    private GamePiece selectedGamePiece;
    private ArrayList<GamePiece> possibleToMove;

    //init methode already called?
    private boolean initialized = false;

    private Game game;

    private DiceImpl dice;

    private boolean playing = true;

    public void init(DiceImpl dice, Game game, String names []) {
        players = new Player[names.length];


        for (int i = 0; i < names.length; i++) {
            PlayerColor cColor = PlayerColor.values()[i];
            players[i] = new Player(cColor, names[i]);
        }

        this.game = game;
        this.dice = dice;

        initialized = true;
    }

    /**
     * play method
     * As the game could be played forever, it features an endless loop, which should be left
     * when there is only one Player who hasn't won.
     * <p>
     * It enables the Dice and waits for the player to roll it.
     *
     * @throws IllegalAccessException
     */
    public void play() throws IllegalAccessException {

        // throw exception when trying to play and init wasn't called yet.
        if (!initialized) {
            throw new IllegalAccessException("ActualGame hasn't been initialized. Please run init() first.");
        }

        int bestPlayer = -1;
        int bestNumber = 0;

        for(int p = 0; p < players.length; p++){

            //
            game.beginningAction(players[p]);

            dice.waitForRoll();

            dice.addToBlacklist(dice.getDiceNumber());

            int number = dice.getDiceNumber().getNumber();

            if(number > bestNumber){
                bestNumber = number;
                bestPlayer = p;
            }
        }

        currentPlayer = bestPlayer;
        dice.emptyBlacklist();

		Log.i("Game", "Starting regular game.");
		regularGame();
    }



    private void regularGame(){
        while (playing) {

            final Player cp = players[currentPlayer];

            game.whomsTurn(cp);

            int attempts = 3;
            boolean moved = false;

            do {
                dice.waitForRoll();
                GamePiece gp;

                moved = false;
                if (dice.getDiceNumber() == DiceNumber.SIX && (gp = cp.getStartingPiece()) != null) {

                    StartingSpot s = (StartingSpot) (gp.getSpot());

                    Spot entrance = s.getEntrance();

                    if (entrance.getGamePiece() == null || (entrance.getGamePiece() != null && entrance.getGamePiece().getPlayerColor() != gp.getPlayerColor())) {
                        moved = movePieceToEntrance(gp);
                    }
                }

                if (!cp.isAtStartingPosition() && !moved) { //muss noch "herauswürfeln"

                    //GamePiece gp = cp.getPieces()[0]; //TODO: select piece

                    this.possibleToMove = new ArrayList<>();
                    selectingLoop: for (GamePiece piece : cp.getPieces()) {
                        boolean free = Board.get().checkSpot(dice.getDiceNumber(), piece) != null;
                        boolean isStartingPiece = piece.getSpot()instanceof StartingSpot;
                        if (free && !isStartingPiece) {
                            if(Board.getEntrance(piece.getPlayerColor()) == piece.getSpot()){
                                possibleToMove = new ArrayList<>();
                                this.possibleToMove.add(piece);
                                break selectingLoop;
                            }
                            this.possibleToMove.add(piece);
                        }
                    }


                    if (possibleToMove.size() > 0) {

                        if(possibleToMove.size() > 1){
                            game.waitForMovePiece();
                        }else{
                            this.selectedGamePiece = this.possibleToMove.get(0);
                        }


                        if (possibleToMove.contains(this.selectedGamePiece)) {
                            movePiece(this.selectedGamePiece);
                            moved = true;
                            selectedGamePiece = null;
                            possibleToMove = null;
                        }
                    }

                    //Für automatisches Spielen
//                    for (GamePiece piece : cp.getPieces()) {
//                        if (!(piece.getSpot() instanceof StartingSpot)) {
//
//
//                            if (movePiece(piece)) {
//                                moved = true;
//                                break;
//                            }
//                        }
//                    }
                }

                if (!moved) {
                    attempts--;
                }

                game.refreshView();

            } while (dice.getDiceNumber() == DiceNumber.SIX || (attempts > 0 && !moved));


            currentPlayer = (currentPlayer + 1) % players.length;

            while (players[currentPlayer].hasToSkip()){
                players[currentPlayer].setHasToSkip(false);
                currentPlayer = (currentPlayer + 1) % players.length;
            }

			// sync
			if(!ActualGame.getInstance().isLocalGame()) {
				com.vintagetechnologies.menschaergeredichnicht.GameLogic gameLogic = (com.vintagetechnologies.menschaergeredichnicht.GameLogic) DataHolder.getInstance().retrieve(Network.DATAHOLDER_GAMELOGIC);
				if(gameLogic.isHost())	// check not needed, because only the host runs the game
					GameSynchronisation.synchronize();
			}

        }
    }


    public void selectGamePiece(GamePiece gp) {
        this.selectedGamePiece = gp;
    }

    public ArrayList<GamePiece> getPossibleToMove() {
        return this.possibleToMove;
    }

    /**
     * Moves a GamePiece by the number the Dices face shows.
     * Returns true when the GamePiece was able to move.
     *
     * @param gp
     * @return
     */
    private boolean movePiece(GamePiece gp) {

        Spot s = Board.checkSpot(dice.getDiceNumber(), gp);

        if (s != null) {
            gp.moveTo(s);
            return true;
        }

        return false;
    }

    /**
     * Moves the GamePiece by one step.
     * Returns true when the GamePiece was able to move.
     *
     * @param gp
     * @return
     */
    private boolean movePieceToEntrance(GamePiece gp) {

        Spot s = Board.checkSpot(DiceNumber.ONE, gp);

        if (s != null) {
            gp.moveTo(s);
            return true;
        }

        return false;
    }


    public Player[] getPlayers() {
        return players;
    }

    /**
     * Get current player.
     */
    public Player getCurrentPlayer() {
        return players[currentPlayer];
    }


	/**
	 * Get the index of the current player.
	 */
	public int getCurrentPlayerIndex() {
		return currentPlayer;
	}


    /**
     * @param players
     */
    public void setPlayers(Player[] players) {
        this.players = players;
    }



    /**
     * Get the Player by providing the Players name
     *
     * @param name
     * @return
     */
    public Player getPlayerByName(String name) {
        for (int i = 0; i < players.length; i++) {
            if (name.equals(players[i].getName()))
                return players[i];
        }
        return null;
    }

    public boolean isPlaying() {
        return playing;
    }

    public void setPlaying(boolean playing) {
        this.playing = playing;
    }
}
