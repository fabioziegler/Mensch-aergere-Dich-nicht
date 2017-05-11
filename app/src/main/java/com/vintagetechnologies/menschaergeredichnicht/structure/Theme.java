package com.vintagetechnologies.menschaergeredichnicht.structure;


import android.graphics.Color;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.Buffer;
import java.util.HashMap;
import java.util.Iterator;
import java.util.StringTokenizer;

/**
 * Created by johannesholzl on 02.05.17.
 */

public class Theme {

    private HashMap<String, Integer> color;

    public Theme(InputStream inputStream) {
        this.color = new HashMap<>();
        JSONObject jo = null;
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));

            String tmp = "";
            String line = "";

            while((line = br.readLine()) != null){
                tmp += line;
            }

            readJson(new JSONObject(tmp));

        } catch (JSONException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void readJson(JSONObject jo){



            int i = 0;
            Iterator<String> it = jo.keys();

            try {
                while(it.hasNext()){
                    String key = it.next();
                    Object o = jo.get(key);
                    if(o instanceof String){
                        setColor(key, (String)o);
                    }else if(o instanceof JSONObject){
                        readJson((JSONObject)o);
                    }else{
                        System.err.println("NOT A VALID FIELD!");
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
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

}
