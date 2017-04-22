package com.vintagetechnologies.menschaergeredichnicht.networking;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Fabio on 17.04.17.
 * Notifies listeners of when a wifi connection was established or lost.
 */
public class WifiReceiver extends BroadcastReceiver {

	private static final String TAG = WifiReceiver.class.getSimpleName();

	private List<WifiListener> listeners = new ArrayList<>();

	public void addReceiver(WifiListener listener){
		listeners.add(listener);
	}

	@Override
	public void onReceive(Context context, Intent intent) {

		final String action = intent.getAction();

		if (action.equals(WifiManager.SUPPLICANT_CONNECTION_CHANGE_ACTION)) {

			if (intent.getBooleanExtra(WifiManager.EXTRA_SUPPLICANT_CONNECTED, false)){

				// notify listeners about established wifi connection
				for(WifiListener listener : listeners){
					listener.hasWiFiConnectionEstablished();
				}

			} else {	// wifi connection was lost

				// notify about lost connection
				for(WifiListener listener : listeners){
					listener.hasWiFiConnectionLost();
				}
			}
		}
	}

}
