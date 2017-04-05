package com.vintagetechnologies.menschaergeredichnicht;

import java.io.Serializable;

/**
 * Created by Fabio on 05.04.17.
 * Class holds settings like player name, music, cheating, which board design (classic, vintage)...
 */
public class GameSettings {

    //TODO: make methods to save some settings (music, ...) permanently to disc (using google gson lib)

    private String playerName;
    private boolean musicEnabled;
    private boolean cheatingEnabled;

    private BoardDesign boardDesign;

    public enum BoardDesign {
        CLASSIC, VINTAGE
    }

    public GameSettings(String playerName){
        this.playerName = playerName;
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
     * Save settings to disk
     */
    public void savePermanently(){
        // TODO: implement
    }

}
