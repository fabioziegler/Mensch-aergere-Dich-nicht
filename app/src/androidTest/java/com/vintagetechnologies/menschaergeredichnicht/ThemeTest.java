package com.vintagetechnologies.menschaergeredichnicht;

import android.content.res.AssetManager;
import android.graphics.Color;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;


import com.vintagetechnologies.menschaergeredichnicht.structure.Theme;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;

import static android.support.test.InstrumentationRegistry.getInstrumentation;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;

/**
 * Created by johannesholzl on 10.05.17.
 */

@RunWith(AndroidJUnit4.class)
public class ThemeTest {


    @Test
    public void testTheme() {
        AssetManager am = getInstrumentation().getContext().getAssets();

        try {
            String[] s = am.list("/");
            String[] a = am.list("themes");
        } catch (IOException e) {
			Log.e("Test", "Fehler", e);
		}


        Theme t = null;
        try {
            t = new Theme(am.open("themes/classic.json"));
        } catch (IOException e) {
			Log.e("Test", "Fehler", e);
        }

        if(t == null) {
            assertNotNull(t);
        }else{
            assertEquals((int) Color.rgb(255, 0, 0), (int) t.getColor("RED"));
        }
    }
}
