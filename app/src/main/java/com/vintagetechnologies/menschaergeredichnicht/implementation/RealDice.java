package com.vintagetechnologies.menschaergeredichnicht.implementation;

import android.app.Activity;
import android.util.Log;
import android.widget.ImageButton;

import com.vintagetechnologies.menschaergeredichnicht.DataHolder;
import com.vintagetechnologies.menschaergeredichnicht.GameLogic;
import com.vintagetechnologies.menschaergeredichnicht.GameSettings;
import com.vintagetechnologies.menschaergeredichnicht.R;
import com.vintagetechnologies.menschaergeredichnicht.Spieloberflaeche;
import com.vintagetechnologies.menschaergeredichnicht.networking.Network;
import com.vintagetechnologies.menschaergeredichnicht.structure.DiceNumber;
import com.vintagetechnologies.menschaergeredichnicht.synchronisation.GameSynchronisation;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by johannesholzl on 15.04.17.
 */

public class RealDice extends DiceImpl {

    private static RealDice realDice;
    private static ImageButton diceButton;


    private RealDice() {
    }


    public static RealDice get() {
        if (realDice == null) {
            realDice = new RealDice();
        }

        return realDice;
    }


    public static void reset() {
        realDice = new RealDice();
    }


    @Override
    public void emptyBlacklist() {
        super.emptyBlacklist();

        if (!ActualGame.getInstance().isLocalGame()) { //has to be host
            GameSynchronisation.send(RealDice.get());
        }
    }

    @Override
    public void addToBlacklist(DiceNumber diceNumber) {
        super.addToBlacklist(diceNumber);

        if (!ActualGame.getInstance().isLocalGame()) { //has to be host
            GameSynchronisation.send(RealDice.get());
        }
    }

    public void waitForRoll() {

        GameSettings gameSettings = DataHolder.getInstance().retrieve(Network.DATAHOLDER_GAMESETTINGS, GameSettings.class);

        if (!ActualGame.getInstance().isLocalGame() && DataHolder.getInstance().retrieve(Network.DATAHOLDER_GAMELOGIC, GameLogic.class).isHost() && !ActualGame.getInstance().getGameLogic().getCurrentPlayer().getName().equals(gameSettings.getPlayerName())) {
            GameSynchronisation.send(Network.TAG_WAIT_FOR_ROLL + Network.MESSAGE_DELIMITER + ActualGame.getInstance().getGameLogic().getCurrentPlayer().getName());

            synchronized (realDice) {

                Log.i("Dice", "waiting: " + realDice);

                try {
                    realDice.wait();
                } catch (InterruptedException e) {
                    Logger.getLogger(RealDice.class.getName()).log(Level.INFO, "Exception while waiting!", e);
                    Thread.currentThread().interrupt();
                }

            }
        } else {


            final Spieloberflaeche s = (Spieloberflaeche) diceButton.findViewById(R.id.imageButton_wuerfel).getContext();

            s.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    s.setDiceEnabled(true);
                }
            });

            synchronized (realDice) {

                Log.i("Dice", "waiting: " + realDice);

                try {
                    realDice.wait();
                } catch (InterruptedException e) {
                    Logger.getLogger(RealDice.class.getName()).log(Level.INFO, "Exception while waiting!", e);
                    Thread.currentThread().interrupt();
                }

            }


            s.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    s.setDiceEnabled(false);
                }
            });

            if (!ActualGame.getInstance().isLocalGame()) {
                GameSynchronisation.send(realDice.getDiceNumber());
            }
        }
    }

    public static void setDiceButton(ImageButton db) {
        diceButton = db;
    }

    public static void setRealDice(RealDice rd) {
        realDice = rd;
    }
}
