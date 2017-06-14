package com.vintagetechnologies.menschaergeredichnicht.structure;


import android.support.v4.util.Pair;

import com.vintagetechnologies.menschaergeredichnicht.DataHolder;
import com.vintagetechnologies.menschaergeredichnicht.GameSettings;
import com.vintagetechnologies.menschaergeredichnicht.implementation.ActualGame;
import com.vintagetechnologies.menschaergeredichnicht.implementation.DiceImpl;
import com.vintagetechnologies.menschaergeredichnicht.networking.Network;
import com.vintagetechnologies.menschaergeredichnicht.synchronisation.GameSynchronisation;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by johannesholzl on 06.05.17.
 */

public class GameLogic {

    private int currentPlayer;
    private Player[] players;

    private Thread clientPlayThread;

    private GamePiece selectedGamePiece;
    private ArrayList<GamePiece> possibleToMove;

    //init methode already called?
    private boolean initialized = false;

    private Game game;

    private DiceImpl dice;

    private boolean playing = true;


    public void init(DiceImpl dice, Game game, String[] names) {
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

        // TODO: adjust for multiplayer

        for (int p = 0; p < players.length; p++) {

            currentPlayer = p;

            game.beginningAction(players[p]);

            dice.waitForRoll();

            dice.addToBlacklist(dice.getDiceNumber());

            int number = dice.getDiceNumber().getNumber();

            if (number > bestNumber) {
                bestNumber = number;
                bestPlayer = p;
            }
        }

        currentPlayer = bestPlayer;
        dice.emptyBlacklist();

        game.regularGameStarted();
        regularGame();
    }

    private boolean startingPos() {
        boolean moved = false;
        final Player cp = players[currentPlayer];
        GamePiece gp;
        if (dice.getDiceNumber() == DiceNumber.SIX && (gp = cp.getStartingPiece()) != null) {

            StartingSpot s = (StartingSpot) (gp.getSpot());

            Spot entrance = s.getEntrance();

            if (entrance.getGamePiece() == null || (entrance.getGamePiece() != null && entrance.getGamePiece().getPlayerColor() != gp.getPlayerColor())) {
                moved = movePieceToEntrance(gp);
            }
        }
        return moved;
    }


    private boolean move() {
        final Player cp = players[currentPlayer];

        boolean moved = false;
        this.possibleToMove = new ArrayList<>();
        for (GamePiece piece : cp.getPieces()) {
            boolean free = Board.get().checkSpot(dice.getDiceNumber(), piece) != null;
            boolean isStartingPiece = piece.getSpot() instanceof StartingSpot;

            if (free && !isStartingPiece) {
                if (Board.getEntrance(piece.getPlayerColor()) == piece.getSpot()) {
                    possibleToMove = new ArrayList<>();
                    this.possibleToMove.add(piece);
                    break;
                }
                this.possibleToMove.add(piece);
            }
        }


        if (!possibleToMove.isEmpty()) {

            if (possibleToMove.size() > 1) {
                game.waitForMovePiece();
            } else {
                this.selectedGamePiece = this.possibleToMove.get(0);
            }


            if (possibleToMove.contains(this.selectedGamePiece)) {
                movePiece(this.selectedGamePiece);
                moved = true;
                selectedGamePiece = null;
                possibleToMove = null;
            }
        }

        return moved;

    }

    private Pair<Boolean, Integer> playerMoveLoop(int a) {

        final Player cp = players[currentPlayer];
        int attempts = a;

        dice.waitForRoll();


        boolean moved = startingPos();


        if (!cp.isAtStartingPosition() && !moved) {
            moved = move();
        }

        if (!moved) {
            attempts--;
        }

        game.refreshView();

        Pair<Boolean, Integer> p = new Pair<>(moved, attempts);

        return p;

    }

    private void gameLoop() {

        final Player cp = players[currentPlayer];

        game.whomsTurn(cp);

        int attempts = 3;
        boolean moved;

        do {
            Pair<Boolean, Integer> pair = playerMoveLoop(attempts);
            moved = pair.first;
            attempts = pair.second;
        } while (dice.getDiceNumber() == DiceNumber.SIX || (attempts > 0 && !moved));


        currentPlayer = (currentPlayer + 1) % players.length;

        while (players[currentPlayer].hasToSkip()) {
            players[currentPlayer].setHasToSkip(false);
            currentPlayer = (currentPlayer + 1) % players.length;
        }

        // sync
        if (game instanceof ActualGame && !ActualGame.getInstance().isLocalGame()) {
            com.vintagetechnologies.menschaergeredichnicht.GameLogic gameLogic = (com.vintagetechnologies.menschaergeredichnicht.GameLogic) DataHolder.getInstance().retrieve(Network.DATAHOLDER_GAMELOGIC);
            if (gameLogic.isHost())    // check not needed, because only the host runs the game
                GameSynchronisation.synchronize();
        }
    }


    private void regularGame() {
        while (playing) {
            gameLoop();
        }
    }


    public void selectGamePiece(GamePiece gp) {
        this.selectedGamePiece = gp;
    }

    public List<GamePiece> getPossibleToMove() {
        return possibleToMove;
    }

    public void _findPossibleToMove() {
        Player cp = getPlayerByName(DataHolder.getInstance().retrieve(Network.DATAHOLDER_GAMESETTINGS, GameSettings.class).getPlayerName());

        this.possibleToMove = new ArrayList<>();
        for (GamePiece piece : cp.getPieces()) {
            boolean free = Board.get().checkSpot(dice.getDiceNumber(), piece) != null;
            boolean isStartingPiece = piece.getSpot() instanceof StartingSpot;

            if (free && !isStartingPiece) {
                if (Board.getEntrance(piece.getPlayerColor()) == piece.getSpot()) {
                    possibleToMove = new ArrayList<>();
                    this.possibleToMove.add(piece);
                    break;
                }
                this.possibleToMove.add(piece);
            }
        }
    }


    public void resetSelected() {
        if (possibleToMove.contains(this.selectedGamePiece)) {
            selectedGamePiece = null;
            possibleToMove = null;
        }

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

            if(this.game instanceof  ActualGame) {
                ActualGame.getInstance().getGameactivity().playMove();
            }
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

    public Thread getClientPlayThread() {
        return clientPlayThread;
    }

    public void setClientPlayThread(Thread clientPlayThread) {
        this.clientPlayThread = clientPlayThread;
    }
}
