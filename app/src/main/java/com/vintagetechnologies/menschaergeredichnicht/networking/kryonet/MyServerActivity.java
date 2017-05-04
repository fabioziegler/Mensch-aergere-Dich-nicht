package com.vintagetechnologies.menschaergeredichnicht.networking.kryonet;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.vintagetechnologies.menschaergeredichnicht.R;

/**
 * Created by Fabio on 02.05.17.
 */

public class MyServerActivity extends AppCompatActivity {

	private MyServer myServer;

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_gamehost);

		myServer = new MyServer();

		//new Thread(myServer).start();
		myServer.initializeServer();

	}




	@Override
	protected void onStart() {
		super.onStart();
	}

	@Override
	protected void onStop() {
		super.onStop();
	}

	@Override
	protected void onPause() {
		super.onPause();
	}

	@Override
	protected void onResume() {
		super.onResume();
	}
}
