package com.vintagetechnologies.menschaergeredichnicht.structure;


import android.graphics.Color;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Iterator;

/**
 * Created by johannesholzl on 02.05.17.
 */

public class Theme {

    private HashMap<String, Integer> color;
    private HashMap<String, String> otherAttributes;

    public Theme(InputStream inputStream) {
        this.color = new HashMap<>();
        this.otherAttributes = new HashMap<>();
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));

            String tmp = "";
            String line = "";

            while((line = br.readLine()) != null){
                tmp += line;
            }

            readJson(new JSONObject(tmp));

        } catch (JSONException | IOException e) {
			Log.e("Test", "Fehler", e);
        }
    }

    private void readJson(JSONObject jo){
		Iterator<String> it = jo.keys();

		try {
			while(it.hasNext()){
                String key = it.next();
                Object o = jo.get(key);
                addColor(key, o);
			}
		} catch (JSONException e) {
			Log.e("Test", "Fehler", e);
		}
    }

    private void addColor(String key, Object o){

        if(o instanceof String){
            if(((String) o).startsWith("0x")) {
                setColor(key, (String) o);
            }else{
                this.otherAttributes.put(key, (String) o);
            }
        }else if(o instanceof JSONObject){
            readJson((JSONObject)o);
        }
    }

    private void setColor(String key, String value){
        int c = Integer.decode(value);

        int r = (c >> 16) & 0xFF;
        int g = (c >> 8) & 0xFF;
        int b = c & 0xFF;

        color.put(key, Color.rgb(r,g,b));
    }

    public Integer getColor(String s){
        return color.get(s);
    }

    public String getAttribute(String key){
        return otherAttributes.get(key);
    }
}
