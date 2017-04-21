package com.vintagetechnologies.menschaergeredichnicht;

import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.webkit.WebView;

import com.vintagetechnologies.menschaergeredichnicht.R;

import java.io.File;

/**
 * Created by Simon on 21.04.2017.
 */

public class Regeln extends AppCompatActivity {

    private WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_spielregeln);

        webView = (WebView)(findViewById(R.id.Regeln_WV));
        webView.loadUrl("file:///android_asset/regeln.html");
    }
}
