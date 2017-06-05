package com.vintagetechnologies.menschaergeredichnicht.view;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.webkit.WebView;

import com.vintagetechnologies.menschaergeredichnicht.R;

/**
 * Created by Simon on 30.05.2017.
 */

public class Manual extends AppCompatActivity {

    private WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_manual);

        webView = (WebView)(findViewById(R.id.Manual_WV));

        webView.loadUrl("file:///android_asset/anleitung.html");
    }
}
