package com.vintagetechnologies.menschaergeredichnicht;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStreamReader;

/**
 * Created by Fabio on 05.04.17.
 * Class holds settings like player name, music, cheating, which board design (classic, vintage)...
 */
public class GameSettings {

	private final String TAG = "GameSettings";
	private final String settingsFilename = "settings.txt";
    private String playerName;
    private boolean musicEnabled;
    private boolean cheatingEnabled;

    private BoardDesign boardDesign;

    public enum BoardDesign {
        CLASSIC, VINTAGE
    }

    /**
     * Create a new instance of GameSettings and load saved settings from disk
     * @param context An application context
     */
    public GameSettings(Context context){
        if(!loadFromDisk(context))
            setDefaults();
    }

    /**
     * Creates a new instance of GameSettings with default settings and without loading settings from disk
     */
    public GameSettings(){
        setDefaults();
    }

    /**
     * Set default setting values
     */
    private void setDefaults(){
        musicEnabled = true;
        cheatingEnabled = true;
        boardDesign = BoardDesign.CLASSIC;
    }

    public boolean isMusicEnabled() {
        return musicEnabled;
    }

    public void setMusicEnabled(boolean musicEnabled) {
        this.musicEnabled = musicEnabled;
    }

    public boolean isCheatingEnabled() {
        return cheatingEnabled;
    }

    public void setCheatingEnabled(boolean cheatingEnabled) {
        this.cheatingEnabled = cheatingEnabled;
    }

    public BoardDesign getBoardDesign() {
        return boardDesign;
    }

    public void setBoardDesign(BoardDesign boardDesign) {
        this.boardDesign = boardDesign;
    }

    public String getPlayerName() {
        return playerName;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }


	/**
	 * Applys the same settings as a given remote settings class.
	 * @param remoteGameSettings
	 */
	public void apply(GameSettings remoteGameSettings){
		setCheatingEnabled(remoteGameSettings.isCheatingEnabled());	// only force cheat
		//setMusicEnabled(remoteGameSettings.isMusicEnabled());
		//setBoardDesign(remoteGameSettings.getBoardDesign());
	}

    /**
     * Save settings to disk
     */
    public void savePermanently(Context context){

        FileOutputStream outputStream;
        String json = new Gson().toJson(this);

        try {
            outputStream = context.openFileOutput(settingsFilename, Context.MODE_PRIVATE);
            outputStream.write(json.getBytes());
        } catch (Exception e) {
            Log.e(TAG, "Failed to save settings file.", e);	// instead of e.printStackTrace();
		}
    }


    /**
     * Load settings from disk
     * @param context An application context
     * @return True if settings were loaded or false if there are no settings saved on the disk.
     */
    private boolean loadFromDisk(Context context){

        FileInputStream inputStream;
        InputStreamReader streamReader;
        BufferedReader bufferedReader;
        StringBuilder stringBuilder;
        GameSettings gameSettings;

        try {
            inputStream = context.openFileInput(settingsFilename);
            streamReader = new InputStreamReader(inputStream);
            bufferedReader = new BufferedReader(streamReader);
            stringBuilder = new StringBuilder();

            String line;
            while ((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line);
            }

            gameSettings = new Gson().fromJson(stringBuilder.toString(), GameSettings.class);   // generate object from json string (takes ≈67ms)

            musicEnabled = gameSettings.isMusicEnabled();
            cheatingEnabled = gameSettings.isCheatingEnabled();
            boardDesign = gameSettings.getBoardDesign();

            return true;

        } catch (FileNotFoundException e){
            return false;
        } catch (Exception e){
            Log.e(TAG, "Failed to load settings file.", e);
        }

        return false;
    }
}
