package com.vintagetechnologies.menschaergeredichnicht;

import android.content.res.AssetManager;
import android.graphics.Color;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;


import com.vintagetechnologies.menschaergeredichnicht.structure.Theme;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;

import static android.support.test.InstrumentationRegistry.getInstrumentation;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

/**
 * Created by johannesholzl on 10.05.17.
 */

@RunWith(AndroidJUnit4.class)
public class ThemeTest {

    private  AssetManager am;

    @Before
    public void beforeTest(){
        am = getInstrumentation().getContext().getAssets();
    }

    /**
     * testet Existenz und Attribute des Classic Theme
     */
    @Test
    public void testClassic() {


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

    /**
     * testet Existenz und Attribute des Vintage Themes
     */
    @Test
    public void testVintage(){

        Theme t = null;
        try {
            t = new Theme(am.open("themes/vintage.json"));
        } catch (IOException e) {
            Log.e("Test", "Fehler", e);
        }

        assertNotNull(t);

        assertEquals((int) Color.rgb(255, 0, 0), (int) t.getColor("RED"));
        assertEquals("vintage_holz02", t.getAttribute("BackgroundImage"));

    }

    @Test
    public void testFail(){

        Theme t = null;
        try {
            t = new Theme(am.open("themes/retro.json"));
        } catch (IOException e) {
            Log.e("Test", "Fehler", e);
        }

        assertNull(t);


    }

}
