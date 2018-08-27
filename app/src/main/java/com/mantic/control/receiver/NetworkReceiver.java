package com.mantic.control.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by Jia on 2017/6/15.
 */

public class NetworkReceiver extends BroadcastReceiver {
    public static final String NET_CHANGE = "net_change";
    //标记当前网络状态，0为无可用网络状态，1表示 Wifi 2表示 Mobile
    public static final String NET_TYPE = "net_type";

    @Override
    public void onReceive(Context context, Intent intent) {

        ConnectivityManager connectivityManager=(ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
        if (activeNetwork != null) { // connected to the internet
            if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI) {
                // connected to wifi
                Intent netIntent = new Intent(NET_CHANGE);
                netIntent.putExtra(NET_TYPE,1);
                context.sendBroadcast(netIntent);
            } else if (activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE) {
                // connected to the mobile provider's data plan
                Intent netIntent = new Intent(NET_CHANGE);
                netIntent.putExtra(NET_TYPE,2);
                context.sendBroadcast(netIntent);
            }
        } else {
            // not connected to the internet
            Intent netIntent = new Intent(NET_CHANGE);
            netIntent.putExtra(NET_TYPE,0);
            context.sendBroadcast(netIntent);
        }


    }
}
