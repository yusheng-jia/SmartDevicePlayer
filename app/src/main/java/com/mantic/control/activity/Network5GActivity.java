package com.mantic.control.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.mantic.control.R;

/**
 * Created by Jia on 2017/6/16.
 */

public class Network5GActivity extends BaseActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_5gconfig);
        setTitle(R.string.open_network);
        findViewById(R.id.set_5g_network).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startWifiSet();
            }
        });

    }

    private void startWifiSet(){
        Intent wifiSettingsIntent = new Intent("android.settings.WIFI_SETTINGS");
        startActivity(wifiSettingsIntent);
        finish();
    }
}
