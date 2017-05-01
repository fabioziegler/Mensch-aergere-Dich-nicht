package com.vintagetechnologies.menschaergeredichnicht.structure;

import android.app.Activity;
import android.graphics.Color;
import android.widget.Button;
import android.widget.TextView;

import com.vintagetechnologies.menschaergeredichnicht.R;
import com.vintagetechnologies.menschaergeredichnicht.Spieloberflaeche;
import com.vintagetechnologies.menschaergeredichnicht.dummies.DummyDice;
import com.vintagetechnologies.menschaergeredichnicht.view.BoardView;

import java.util.ArrayList;

/**
 * Created by johannesholzl on 05.04.17.
 */

public class Game {

    private static Game gameInstance;

    //! CurrentPlayer gibt nur den Spieler dem spieler sein eigenen Player aus. Nicht den Spieler aktuell am Zug.
    private int currentPlayer;
    private Player players[];
    private Board board;
    private Dice dice = new Dice();
    private BoardView bv;

    private GamePiece selectedGamePiece;
    private ArrayList<GamePiece> possibleToMove;

    //init methode already called?
    private boolean initialized = false;


    /**
     * Returns gameInstance()
     *
     * @return
     */
    public static Game getInstance() {
        if (gameInstance == null) {
            gameInstance = new Game();
        }
        return gameInstance;
    }

    /**
     * Called when a client received a new up to date game object from the host.
     *
     * @param game The new game object received by the host.
     */
    public static void refreshGameInstance(Game game) {
        // TODO: 28.04.17 Instead of overriding, manually set only needed attributes? Which are needed?
        gameInstance = game;
    }

    private Game() {
    }


    /**
     * Inits the Game Object, as it is a Singleton.
     *
     * @param names
     */
    public void init(String... names) {

        new Thread() {
            public void run() {
                DummyDice.get();
            }
        }.start();

        players = new Player[names.length];
        board = Board.get();

        for (int i = 0; i < names.length; i++) {
            PlayerColor cColor = PlayerColor.values()[i];
            players[i] = new Player(cColor, names[i]);
        }

        final Spieloberflaeche gameactivity = (Spieloberflaeche) bv.getContext();
        gameactivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                for (Player p : players) {
                    switch (p.getColor()) {
                        case RED: {
                            TextView tv = ((TextView) gameactivity.findViewById(R.id.textView_spielerRot));
                            tv.setTextColor(Color.RED);
                            tv.setText(p.getName());
                            break;
                        }
                        case GREEN: {
                            TextView tv = ((TextView) gameactivity.findViewById(R.id.textView_spielerGruen));
                            tv.setTextColor(Color.GREEN);
                            tv.setText(p.getName());
                            break;
                        }
                        case BLUE: {
                            TextView tv = ((TextView) gameactivity.findViewById(R.id.textView_spielerBlau));
                            tv.setTextColor(Color.BLUE);
                            tv.setText(p.getName());
                            break;
                        }
                        case YELLOW: {
                            TextView tv = ((TextView) gameactivity.findViewById(R.id.textView_spielerGelb));
                            tv.setTextColor(Color.YELLOW);
                            tv.setText(p.getName());
                            break;
                        }
                    }
                }

            }
        });

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
            throw new IllegalAccessError("Game hasn't been initialized. Please run init() first.");
        }

        while (true) {


            final Player cp = players[currentPlayer];
            final Spieloberflaeche gameactivity = (Spieloberflaeche) bv.getContext();
            gameactivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    gameactivity.setStatus(cp.getName() + " ist dran!");

                }
            });

            int attempts = 3;
            boolean moved = false;

            do {
                DummyDice.waitForRoll();
                GamePiece gp;

                moved = false;
                if (DummyDice.get().getDiceNumber() == DiceNumber.SIX && (gp = cp.getStartingPiece()) != null) {


                    StartingSpot s = (StartingSpot) (gp.getSpot());

                    Spot entrance = s.getEntrance();


                    if (entrance.getGamePiece() == null || (entrance.getGamePiece() != null && entrance.getGamePiece().getPlayerColor() != gp.getPlayerColor())) {
                        moved = movePieceToEntrance(gp);
                    }

                }
                if (!cp.isAtStartingPosition() && !moved) { //muss noch "herauswürfeln"


                    //GamePiece gp = cp.getPieces()[0]; //TODO: select piece

                    this.possibleToMove = new ArrayList<>();
                    for (GamePiece piece : cp.getPieces()) {
                        if (board.checkSpot(DummyDice.get().getDiceNumber(), piece) != null) {
                            this.possibleToMove.add(piece);
                        }
                    }


                    if (possibleToMove.size() > 0) {
                        final Button btnFigurSelect = (Button) (gameactivity.findViewById(R.id.Select_Figur));
                        final Button btnMoveFigur = (Button) (gameactivity.findViewById(R.id.Move_Figur));

                        gameactivity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                btnFigurSelect.setEnabled(true);
                                btnMoveFigur.setEnabled(true);
                            }
                        });
                        synchronized (this) {
                            try {
                                wait();
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }

                        gameactivity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                btnFigurSelect.setEnabled(false);
                                btnMoveFigur.setEnabled(false);
                            }
                        });

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

                bv.postInvalidate();
            } while (DummyDice.get().getDiceNumber() == DiceNumber.SIX || (attempts > 0 && !moved));


            currentPlayer = (currentPlayer + 1) % players.length;


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

        Spot s = Board.checkSpot(DummyDice.get().getDiceNumber(), gp);

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


    /**
     * not used at the moment...
     *
     * @throws IllegalAccessException
     * @TODO REFACTOR
     */
    private void waitfordice() throws IllegalAccessException {

        if (!initialized) {
            throw new IllegalAccessError("Game hasn't been initialized. Please run init() first.");
        }


        try {
            this.wait();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


    }

    /**
     *
     */
    private void figureSelected() {

    }

    /**
     * @return
     */
    public Player[] getPlayers() {
        return players;
    }

    /**
     * @return
     */
    public Player getCurrentPlayer() {
        return players[currentPlayer];
    }


    /**
     * @param players
     */
    public void setPlayers(Player[] players) {
        this.players = players;
    }

    /**
     * @return
     */
    public BoardView getBoardView() {
        return bv;
    }

    /**
     * @param bv
     */
    public void setBoardView(BoardView bv) {
        this.bv = bv;
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
}
