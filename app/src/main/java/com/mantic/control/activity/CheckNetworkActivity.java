package com.mantic.control.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.mantic.control.R;

/**
 * Created by Jia on 2017/6/15.
 */

public class CheckNetworkActivity extends Activity{
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checknetwork);
        findViewById(R.id.network_login).setOnClickListener(new View.OnClickListener() {
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
