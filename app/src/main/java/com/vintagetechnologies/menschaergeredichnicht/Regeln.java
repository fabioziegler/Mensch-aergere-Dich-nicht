package com.vintagetechnologies.menschaergeredichnicht;

import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;


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

		optimizeWebView(webView);

		webView.loadUrl("file:///android_asset/regeln.html");
	}



	/**
	 * Make webview faster
	 * http://stackoverflow.com/questions/7422427/android-webview-slow
	 * @param webView
	 */
	private void optimizeWebView(WebView webView){
		webView.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT);

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
			// chromium, enable hardware acceleration
			webView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
		} else {
			// older android version, disable hardware acceleration
			webView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
		}
	}

}
