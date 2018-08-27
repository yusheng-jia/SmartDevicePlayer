package com.mantic.control.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Log;

import com.mantic.control.utils.ClientWifi;

public class AppReceiver extends BroadcastReceiver {
    private static final String TAG = "AppReceiver";
    @Override
    public void onReceive(Context context, Intent intent) {
        NetworkInfo networkInfo = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);

        if (networkInfo.getState() == NetworkInfo.State.CONNECTED) {
            WifiInfo wifiInfo = intent.getParcelableExtra(WifiManager.EXTRA_WIFI_INFO);
            Log.e(TAG, "onReceive: successful" );
            String ssid = wifiInfo.getSSID();
            if (ssid != null && ssid.contains("Coomaan")) {
                ClientWifi.setConnected();
            }
        }
    }
}
