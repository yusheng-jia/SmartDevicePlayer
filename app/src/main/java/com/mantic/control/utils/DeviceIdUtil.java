package com.mantic.control.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

/**
 * Created by wujiangxia on 2017/4/20.
 */
public class DeviceIdUtil {
    public static final String ILLEGAL_DEVICE_ID = "illegal_device_id";
    private static final String DEVICES_ID = "com_hll_log_devices_id";
    private static final String ILLEGAL_BLUETOOTH_ADDRESS = "00:00:00:00:00:00";
    private static String devices_id;

    public static String getPhoneDeviceId(Context context) {
        if (!TextUtils.isEmpty(devices_id)) {
            return devices_id;
        }
        devices_id = android.provider.Settings.System.getString(context.getContentResolver(), DEVICES_ID);
        if (TextUtils.isEmpty(devices_id)) {
            devices_id = generatePhoneDeviceId(context);
            android.provider.Settings.System.putString(context.getContentResolver(), DEVICES_ID, devices_id);
        }
        return devices_id;
    }

    @SuppressLint("NewApi")
    public static String generatePhoneDeviceId(Context activity) {
        try {
            WifiManager wifi = (WifiManager) activity.getSystemService(Context.WIFI_SERVICE);
            TelephonyManager telephonyManager = (TelephonyManager) activity.getSystemService(Context.TELEPHONY_SERVICE);
            StringBuffer buffer = new StringBuffer();
            buffer.append(wifi.getConnectionInfo().getMacAddress());
            buffer.append(telephonyManager.getDeviceId());
          //  return SHA1Util.SHA1(buffer.toString()).substring(0, 32);
            return buffer.toString();
        } catch (Exception e) {
            Glog.d("DeviceIdUtil", "generateDeviceId exception", e);
            return Build.SERIAL;
        }
    }

}