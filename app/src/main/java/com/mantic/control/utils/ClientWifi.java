package com.mantic.control.utils;

import android.annotation.TargetApi;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.mantic.control.fragment.WifiConnectProgressUIFragment;

import java.util.List;

/**
 * Created by PP on 2017/10/10.
 */

public class ClientWifi {
    private static final String TAG = "ClientWifi";
    public static final String SSID_KEY = "ssid_key";
    public static final String PASSWORD_KEY = "password_key";
    private static boolean isConnected;
    private Context mContext;
    private WifiOpenHelper wifiOpenHelper;
    private SocketHelper socketHelper;
    private String currentSSID;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SocketHelper.SOCKET_ClIENT_MSG:
                    String result = (String) msg.obj;
                    if (result.contains("successful")) {
                        Toast.makeText(mContext, "数据发送完毕，开始重连之前的Wifi："
                                + currentSSID, Toast.LENGTH_SHORT).show();
                        setProgress("数据发送完毕，开始重连之前的WI-FI：" + currentSSID);
                    } else {
                        Toast.makeText(mContext, "未能成功发送SSID和密码，配网失败，开始重连之前的Wifi："
                                + currentSSID, Toast.LENGTH_SHORT).show();
                        setProgress("未能成功发送SSID和密码，配网失败，开始重连之前的WI-FI："
                                + currentSSID);
                    }
                    reconnectPreviousWifi();

                    break;

            }
        }
    };
    private long startTimeMillis;
    private List<String> coomaanSSIDs;

    public ClientWifi(Context context) {
        mContext = context;
        wifiOpenHelper = new WifiOpenHelper(context);
        socketHelper = new SocketHelper(handler);
    }

    public void connectAP(String SSID) {
        if (SSID == null) return;
        wifiOpenHelper.connectCoomaanAP(SSID);
    }


    public void scanWifi() {
        wifiOpenHelper.startScanOnly();
    }

    public void connectAPAndSendSSIDAndPassword(String SSID, String password) {
        if (TextUtils.isEmpty(SSID) || TextUtils.isEmpty(password)) return;
        isConnected = false;
        if (!prepareConnect(SSID)) return;
        connectAP();
        startTimeMillis = System.currentTimeMillis();
        checkIdEqualsCoomaanAndSend(SSID, password);
    }

    private boolean prepareConnect(String SSID) {
        if (!TextUtils.isEmpty(wifiOpenHelper.getSSID()))
            currentSSID = wifiOpenHelper.getSSID();
        if (TextUtils.isEmpty(currentSSID) || currentSSID.equals("0x"))
            currentSSID = SSID;
        coomaanSSIDs = getAllCoomaanSSID();
        Log.e(TAG, "connectAP coomaanSSIDs.size: " + coomaanSSIDs.size());
        if (coomaanSSIDs.size() == 0) {
            setProgress("未找到Coomaan设备，音箱未进入或刚进入配网模式，请回退");
            return false;
        }
        return true;
    }

    public void connectAP() {

        Toast.makeText(mContext, "开始连接音箱：" + coomaanSSIDs.get(0), Toast.LENGTH_SHORT).show();
        setProgress("开始连接音箱：" + coomaanSSIDs.get(0));
        wifiOpenHelper.connectCoomaanAP(coomaanSSIDs.get(0));
    }

    private void checkIdEqualsCoomaanAndSend(final String SSID, final String password) {
        long currentTimeMillis = System.currentTimeMillis();
        if (currentTimeMillis - startTimeMillis > 35 * 1000) {
            Toast.makeText(mContext, "抱歉，连接超时，请再试一次。", Toast.LENGTH_SHORT).show();
            setProgress("抱歉，连接超时，请再试一次.");
            return;
        }
        Log.e(TAG, "checkIdEqualsCoomaanAndSend: " + isConnected);
        if (!isConnected) {
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    checkIdEqualsCoomaanAndSend(SSID, password);
                }
            }, 500);
            return;
        }
        sendSSIDAndPassword(SSID, password);
    }

    public void sendSSIDAndPassword(final String SSID, final String password) {
        Toast.makeText(mContext, "音箱连接成功，开始传输数据。", Toast.LENGTH_SHORT).show();
        setProgress("音箱连接成功，开始传输数据.");
        Log.e(TAG, "sendSSIDAndPassword: " );
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (Build.VERSION.SDK_INT >= 21) {
                    setDefaultNetwork();
                }
                socketHelper.startSendSSIDandPassword(wifiOpenHelper.getAPIpAddress(), SSID + "&" + password);
            }
        }, 1000);
    }

    public List<String> getAllCoomaanSSID() {
        return wifiOpenHelper.getCoomaanSSID();
    }

    public String getCurrentSSID() {
        return wifiOpenHelper.getSSID();
    }

    private void reconnectPreviousWifi() {
        if (currentSSID == null) return;
        wifiOpenHelper.disconnectWifi();
        boolean connect = wifiOpenHelper.connect(currentSSID);
        Log.e(TAG, "reconnectPreviousWifi: " + connect);

    }

    public static void setConnected() {
        isConnected = true;
    }

    private void setProgress(String content) {
        if (mStatus != null)
            mStatus.progress(content);
    }

    private StatusListener mStatus;

    public void setStatusListener(StatusListener status) {
        mStatus = status;
    }

    public interface StatusListener {
        void progress(String string);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public void setDefaultNetwork() {
        ConnectivityManager manager = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        Network[] networks = manager.getAllNetworks();
        for (int i = 0; i < networks.length; i++) {
            NetworkInfo netInfo = manager.getNetworkInfo(networks[i]);
            Log.d(TAG,netInfo.toString()+netInfo.getExtraInfo());
            boolean result = false;
            boolean result0 = false;
            if (netInfo.getType() == ConnectivityManager.TYPE_WIFI) {

                Log.d(TAG, "Found wifi network: setting default...");
                if (Build.VERSION.SDK_INT >= 23) {
                    Log.d(TAG, "Build.VERSION.SDK_INT >= 23 , "+networks[i].describeContents());
                     result = manager.bindProcessToNetwork(networks[i]);
                } else {
                    Log.d(TAG, "Build.VERSION.SDK_INT >= 21 , "+networks[i].toString());

                    result = manager.setProcessDefaultNetwork(networks[i]);
                }
                Log.e(TAG, "Result: " + result+" Result0: " + result0);
            }
            if (result) {
                Log.e(TAG, "Success! Restricted to: " + netInfo.toString());
                break;
            }
        }
    }
}
