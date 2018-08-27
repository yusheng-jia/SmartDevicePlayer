package com.mantic.control.utils;

import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiManager.WifiLock;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;


public class WifiOpenHelper {
    private static final String TAG = "WifiOpenHelper";
    private final Context mContext;
    // 定义WifiManager对象
    private WifiManager mWifiManager;
    // 定义WifiInfo对象
    private WifiInfo mWifiInfo;
    // 扫描出的网络连接列表
    private List<ScanResult> mWifiList;
    // 网络连接列表
    private List<WifiConfiguration> mWifiConfigurations;
    // 定义一个WifiLock
    WifiLock mWifiLock;

    private Handler mHandler = new Handler(Looper.getMainLooper());

    // 构造器
    public WifiOpenHelper(Context context) {

        // 取得WifiManager对象
        mContext = context;
        mWifiManager = (WifiManager) context
                .getSystemService(Context.WIFI_SERVICE);
        // 取得WifiInfo对象
        mWifiInfo = mWifiManager.getConnectionInfo();

//        removeAllStoredNetwork();
    }

    // 打开WIFI
    public void openWifi() {
        if (!mWifiManager.isWifiEnabled()) {
            mWifiManager.setWifiEnabled(true);
        }
    }

    // 断开当前网络
    public void disconnectWifi() {
        if (mWifiManager.isWifiEnabled()) {
            mWifiManager.disconnect();
        }
    }

    // 关闭WIFI
    public void closeWifi() {
        if (mWifiManager.isWifiEnabled()) {
            mWifiManager.setWifiEnabled(false);
        }
    }

    // 检查当前WIFI状态
    public int checkState() {
        return mWifiManager.getWifiState();
    }

    // 锁定WifiLock
    public void acquireWifiLock() {
        mWifiLock.acquire();
    }

    // 解锁WifiLock
    public void releaseWifiLock() {
        // 判断时候锁定
        if (mWifiLock.isHeld()) {
            mWifiLock.acquire();
        }
    }

    // 创建一个WifiLock
    public void creatWifiLock() {
        mWifiLock = mWifiManager.createWifiLock("Test");
    }

    // 得到配置好的网络
    public List<WifiConfiguration> getConfiguration() {
        return mWifiConfigurations;
    }

    // 指定配置好的网络进行连接
    public void connectConfiguration(int index) {

        if (index > mWifiConfigurations.size()) {
            return;
        }
        // 连接配置好的指定ID的网络
        mWifiManager.enableNetwork(mWifiConfigurations.get(index).networkId,
                true);
    }

    public void startScan() {

        mWifiManager.startScan();
        // 得到扫描结果
        mWifiList = mWifiManager.getScanResults();
        // 得到配置好的网络连接
        mWifiConfigurations = mWifiManager.getConfiguredNetworks();
        Log.e(TAG, "scanWifi: " + (mWifiList == null ? "null" : mWifiList.size()));
        if (mWifiList == null) {
            if (mWifiManager.getWifiState() == 3) {
                Toast.makeText(mContext, "当前区域没有无线网络", Toast.LENGTH_SHORT).show();
            } else if (mWifiManager.getWifiState() == 2) {
                Toast.makeText(mContext, "WiFi正在开启，请稍后重新点击扫描", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(mContext, "WiFi没有开启，无法扫描", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // 得到网络列表
    public List<ScanResult> getWifiList() {
        return mWifiList;
    }

    // 查看扫描结果
    public StringBuilder lookUpScan() {
        StringBuilder stringBuilder = new StringBuilder();
//        mWifiList=mWifiManager.getScanResults();
        if (mWifiList == null || mWifiList.isEmpty()) return stringBuilder;
        for (int i = 0; i < mWifiList.size(); i++) {
            stringBuilder.append("Index_" + Integer.valueOf(i + 1).toString()
                    + ":");
            // 将ScanResult信息转换成一个字符串包
            // 其中把包括：BSSID、SSID、capabilities、frequency、level
            stringBuilder.append((mWifiList.get(i)).toString());
            stringBuilder.append("\n\n");
        }
        return stringBuilder;
    }

    // 得到MAC地址
    public String getMacAddress() {
        return (mWifiInfo == null) ? "NULL" : mWifiInfo.getMacAddress();
    }

    // 得到接入点的SSID
    public String getSSID() {
        return ((mWifiInfo = getWifiInfo()) == null) ? "NULL"
                : removeQuotas(mWifiInfo.getSSID());
    }

    // 得到IP地址
    public int getIPAddress() {
        return (mWifiInfo == null) ? 0 : mWifiInfo.getIpAddress();
    }

    // 得到连接的ID
    public int getNetworkId() {
        return (mWifiInfo == null) ? 0 : mWifiInfo.getNetworkId();
    }

    // 得到WifiInfo的所有信息包
    public WifiInfo getWifiInfo() {
        return mWifiManager.getConnectionInfo();
    }

    // 添加一个网络并连接
    public boolean addAndEnableNetwork(WifiConfiguration wcg) {
        WifiConfiguration configuration = getWifiConfigurationFromSSID(wcg.SSID);
        int wcgID;
        if (configuration != null) {
            wcgID = configuration.networkId;
        } else {
            wcgID = mWifiManager.addNetwork(wcg);
            Log.e(TAG, "addAndEnableNetwork addNetwork wcgID: " + wcgID);
        }
        if (wcgID == -1) return false;
        Log.e(TAG, "addAndEnableNetwork wcgID: " + wcgID);
        boolean result = mWifiManager.enableNetwork(wcgID, true);
        Log.e(TAG, "addAndEnableNetwork enableNetwork: " + result);
        return result;

    }

    // 断开指定ID的网络
    public void disconnectWifi(int netId) {
        mWifiManager.disableNetwork(netId);
        mWifiManager.disconnect();
    }

    private WifiConfiguration getWifiConfigurationFromSSID(String SSID) {
        List<WifiConfiguration> existingConfigs = mWifiManager
                .getConfiguredNetworks();
        if (existingConfigs == null) return null;
        String quotasSSID;
        if (SSID.contains("\"")) quotasSSID = SSID;
        else quotasSSID = "\"" + SSID + "\"";
        for (WifiConfiguration existingConfig : existingConfigs) {
            if (existingConfig.SSID.equals(quotasSSID)) {
                return existingConfig;
            }
        }
        return null;
    }

    private boolean isExists(String SSID) {
        List<WifiConfiguration> existingConfigs = mWifiManager
                .getConfiguredNetworks();

        for (WifiConfiguration existingConfig : existingConfigs) {
            if (existingConfig.SSID.equals("\"" + SSID + "\"")) {
                return true;
            }
        }
        return false;
    }

    public String getAPIpAddress() {
        int serverAddress = mWifiManager.getDhcpInfo().serverAddress;
        return intIpToString(serverAddress);
    }

    private String intIpToString(int ip) {
        return String.format("%d.%d.%d.%d",
                (ip & 0xff),
                (ip >> 8 & 0xff),
                (ip >> 16 & 0xff),
                (ip >> 24 & 0xff));
    }

    public List<String> getCoomaanSSID() {
        List<String> allSSID = new ArrayList<>();
        mWifiManager.startScan();
        // 得到扫描结果
        mWifiList = mWifiManager.getScanResults();
        if (mWifiList == null) return allSSID;
        for (ScanResult result : mWifiList) {
            if (result.SSID.contains("Coomaan"))
                allSSID.add(removeQuotas(result.SSID));
        }
        return allSSID;
    }

    public void startScanOnly(){
        mWifiManager.startScan();
    }
    public void startWifiAp() {

        String SSID = "Coomaan" + 987654321;
        String password = String.valueOf(Math.abs(SSID.hashCode()));
        startWifiAp(SSID, password);
//        startWifiAp("Ap Test","123456789");
    }

    public void connectCoomaanAP(String SSID) {

        if (SSID == null) {
            //TODO 送出未找到AP消息；
            Log.e(TAG, "connectCoomaanAP: no find Coomaan SSID ");
            return;
        }
        int password = Math.abs(SSID.hashCode());
        Log.e(TAG, "connectCoomaanAP: " + " SSID" + SSID + " password" + password);
        connect(SSID, String.valueOf(password));
    }

    public enum WifiCipherType {
        WIFICIPHER_WEP, WIFICIPHER_WPA, WIFICIPHER_NOPASS, WIFICIPHER_INVALID
    }

    /**
     * 开启wifi AP
     *
     * @param mSSID
     * @param mPasswd
     */
    public void startWifiAp(String mSSID, String mPasswd) {
        if (mWifiManager.isWifiEnabled()) mWifiManager.setWifiEnabled(false);
        Method method1 = null;
        try {
            method1 = mWifiManager.getClass().getMethod("setWifiApEnabled", WifiConfiguration.class, boolean.class);
            WifiConfiguration netConfig = getWifiApConfiguration();
            if (netConfig == null) return;
            netConfig.SSID = mSSID;
            netConfig.preSharedKey = mPasswd;
            method1.setAccessible(true);
            Log.e(TAG, "startWifiAp: after setAccessible");
            boolean invoke = (boolean) method1.invoke(mWifiManager, netConfig, true);

            Log.e(TAG, "startWifiAp: finish result:" + invoke);
        } catch (IllegalArgumentException e) {
            Log.e(TAG, "startWifiAp: IllegalArgumentException");
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            Log.e(TAG, "startWifiAp: IllegalAccessException");
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            Log.e(TAG, "startWifiAp: InvocationTargetException");
            e.printStackTrace();
        } catch (SecurityException e) {
            Log.e(TAG, "startWifiAp: SecurityException");
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            Log.e(TAG, "startWifiAp: NoSuchMethodException");
            e.printStackTrace();
        }
    }

    @NonNull
    private WifiConfiguration getAPWifiConfiguration(String mSSID, String mPasswd) {
        WifiConfiguration netConfig = new WifiConfiguration();
        Log.e(TAG, "startWifiAp: after getMethod:"
                + " mSSID:" + mSSID + " mPasswd:" + mPasswd);
        netConfig.SSID = mSSID;
        netConfig.preSharedKey = mPasswd;
        netConfig.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
        netConfig.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
        netConfig.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
        netConfig.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
        netConfig.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
        netConfig.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
        netConfig.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
        netConfig.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
        return netConfig;
    }

    /**
     * 关闭wifi AP共享
     */
    public void closeWifiAp() {
        //if (wifiManager.isWifiEnabled())
        {
            try {
                Method method = mWifiManager.getClass().getMethod("getWifiApConfiguration");
                method.setAccessible(true);
                WifiConfiguration config = (WifiConfiguration) method.invoke(mWifiManager);
                Method method2 = mWifiManager.getClass().getMethod("setWifiApEnabled", WifiConfiguration.class,
                        boolean.class);
                method2.invoke(mWifiManager, config, false);
            } catch (NoSuchMethodException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IllegalArgumentException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    public boolean isWifiApEnabled() {
        try {
            Method method = mWifiManager.getClass().getMethod("isWifiApEnabled");
            method.setAccessible(true);
            return (Boolean) method.invoke(mWifiManager);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }


    public int getWifiApState() {
        try {
            Method method = mWifiManager.getClass().getMethod("getWifiApState");
            method.setAccessible(true);
            return (int) method.invoke(mWifiManager);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }


    public WifiConfiguration getWifiApConfiguration() {
        try {
            Method method = mWifiManager.getClass().getMethod("getWifiApConfiguration");
            method.setAccessible(true);
            return (WifiConfiguration) method.invoke(mWifiManager);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    // 打开wifi功能
    private boolean checkAndOpenWifi() {

        if (!mWifiManager.isWifiEnabled()) {
            return mWifiManager.setWifiEnabled(true);
        }
        return true;
    }

    private void removeAllStoredNetwork() {
        List<WifiConfiguration> configuredNetworks = mWifiManager.getConfiguredNetworks();
        if (configuredNetworks == null) return;
        for (WifiConfiguration configuration : configuredNetworks) {
            Log.e(TAG, "removeAllStoredNetwork SSID: " + configuration.SSID);
            mWifiManager.removeNetwork(configuration.networkId);
        }
    }

    //根据SSID重连一个之前连接过的网络
    public boolean connect(final String SSID) {
        WifiConfiguration configuration = getWifiConfigurationFromSSID(SSID);
        if (configuration == null) return false;
        if (!this.checkAndOpenWifi()) return false;
        Log.e(TAG, "connect only SSID: after create wifiConfiguration and checkAndOpenWifi ");
        if (mWifiManager.getWifiState() != WifiManager.WIFI_STATE_ENABLED) {
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    connect(SSID);
                }
            }, 100);
            return false;
        }
        Log.e(TAG, "connect only SSID:before addAndEnableNetwork ");
        return addAndEnableNetwork(configuration);
    }

    //若不提供加密协议方式，默认WPA方式。
    public boolean connect(String SSID, String password) {
        return connect(SSID, password, WifiCipherType.WIFICIPHER_WPA);
    }

    // 提供一个外部接口，传入要连接的无线网
    public boolean connect(final String SSID, final String password, final WifiCipherType type) {
        if (!this.checkAndOpenWifi()) {
            return false;
        }
        // 开启wifi功能需要一段时间(我在手机上测试一般需要1-3秒左右)，所以要等到wifi
        // 状态变成WIFI_STATE_ENABLED的时候才能执行下面的语句
        if (mWifiManager.getWifiState() != WifiManager.WIFI_STATE_ENABLED) {
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    connect(SSID, password, type);
                }
            }, 100);
            return false;
        }

        return executeConnection(SSID, password, type);

    }

    private boolean executeConnection(final String SSID, final String password, final WifiCipherType type) {
        final boolean[] result = new boolean[1];

        WifiConfiguration wifiConfig = CreateWifiInfo(SSID, password, type);
        //
        if (wifiConfig == null) {
            result[0] = false;
            return false;
        }
        return addAndEnableNetwork(wifiConfig);
    }


    /**
     * 配置连接
     *
     * @param SSID
     * @param Password
     * @param Type
     * @return
     */
    public WifiConfiguration CreateWifiInfo(String SSID, String Password, WifiCipherType Type) {
        WifiConfiguration config = new WifiConfiguration();
        config.allowedAuthAlgorithms.clear();
        config.allowedGroupCiphers.clear();
        config.allowedKeyManagement.clear();
        config.allowedPairwiseCiphers.clear();
        config.allowedProtocols.clear();
        config.SSID = "\"" + SSID + "\"";
        switch (Type) {
            case WIFICIPHER_NOPASS:
                config.wepKeys[0] = "";
                config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
                config.wepTxKeyIndex = 0;
                break;
            case WIFICIPHER_WEP:
                //  config.preSharedKey = "\"" + Password + "\"";
                config.hiddenSSID = true;
                config.wepKeys[0] = "\"" + Password + "\"";
                config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.SHARED);
                config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
                config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
                config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
                config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP104);
                config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
                config.wepTxKeyIndex = 0;
                break;
            case WIFICIPHER_WPA:
                config.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
                config.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
                config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
                config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
                config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
                config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
                config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP104);
                config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
                config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
                config.preSharedKey = "\"" + Password + "\"";
                config.status = WifiConfiguration.Status.ENABLED;
                break;
            default:
                return null;
        }
        return config;
    }


    private String removeQuotas(String ssid) {
        if (!ssid.contains("\"")) return ssid;
        return ssid.replace("\"", "").trim();
    }
}