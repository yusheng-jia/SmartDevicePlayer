package com.mantic.control.activity;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;

import com.mantic.control.R;
import com.mantic.control.bt.BtDevice;
import com.mantic.control.fragment.BackHandledFragment;
import com.mantic.control.fragment.BackHandledInterface;
import com.mantic.control.fragment.BluetoothListFragment;
import com.mantic.control.fragment.ExDeviceGuideFragment;
import com.mantic.control.fragment.FragmentEntrust;
import com.mantic.control.fragment.WifiConnectFailUIFrament;
import com.mantic.control.fragment.WifiConnectUIFrament;
import com.mantic.control.utils.Glog;
import com.mantic.control.utils.SharePreferenceUtil;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.UUID;


/**
 * author: jayson
 * blog: http://blog.csdn.net/jia4525036
 * time: 2017/8/8.
 * desc: 配网Activity
 */
public class NetworkConfigActivity extends AppCompatActivity implements FragmentEntrust,
        BluetoothListFragment.OnFragmentInteractionListener, WifiConnectUIFrament.OnFragmentInteractionListener,
        WifiConnectFailUIFrament.OnFragmentInteractionListener,BackHandledInterface {

    private static final String TAG = "NetworkConfigActivity";
    public static final String ACTION_DISCOVERY_FINISHED =
            "android.bluetooth.adapter.action.DISCOVERY_FINISHED";
    public static final int REQUEST_ENABLE_BT = 3;
    public static final int START_CONNECT = 2;
    public static final int START_SENDWIFI= 4;

    public  static  List<BtDevice> btList = new ArrayList<BtDevice>();
    private BluetoothSocket socket = null;
    private BluetoothDevice mBluetoothDevice = null;
    private ReadThread readThread = null;
    private ConnectThread connectThread = null;
    private SendThread sendThread = null;
    private String bTAddress;
    public String pwd = null;
    public String wifiName = null;
    public static boolean net_success = false;
    public   boolean need_sendData = true;
    public String btScan = "start";

    private Fragment currentFragment;

    private BackHandledFragment mBackHandedFragment;

    /* 取得默认的蓝牙适配器 */
    private BluetoothAdapter mBtAdapter = BluetoothAdapter.getDefaultAdapter();

    /*ble start*/
    private BluetoothAdapter mBluetoothAdapter;
    public static final int STOP_BLE= 5;
    public static final boolean bleNetwork = true;
    private BluetoothGatt mBluetoothGatt;
    private BluetoothGattCallback mBluetoothGattCb;
    private BluetoothGattCharacteristic mGattCharApInfo = null;
    private BluetoothGattCharacteristic mGattCharWifiState = null;
    private boolean configSuccess = false;
    private boolean bleConnected = false;
    private String wifiState;
    private List<String> bleList = new ArrayList<String>();
    private String bleUuid;
    private String bleToken;
    private int bleTime = 0;
    private String bleAddress;
    public static String user_id;
//    public static boolean net_fail = false;
    /*ble end*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_network_config);
        FragmentManager fragmentManager = this.getSupportFragmentManager();
        Fragment fragment = fragmentManager.findFragmentByTag("f1");
        if (null == fragment) {
            ExDeviceGuideFragment fragment1 = new ExDeviceGuideFragment();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.add(R.id.network_container, fragment1, "f1").commit();
        }

        // Register for broadcasts when a device is discovered
        IntentFilter discoveryFilter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        this.registerReceiver(mReceiver, discoveryFilter);

        // Register for broadcasts when discovery has finished
        IntentFilter foundFilter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        this.registerReceiver(mReceiver, foundFilter);

        /*ble start*/
        if (bleNetwork){
            BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
            assert bluetoothManager != null;
            mBluetoothAdapter = bluetoothManager.getAdapter();

            bleUuid = SharePreferenceUtil.getDeviceId(this);
            mBluetoothGattCb = new  BluetoothGattCallback()
            {
                @Override
                public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState)
                {
                    Glog.i(TAG,"onConnectionStateChange -> newState: " + newState);
                    super.onConnectionStateChange(gatt, status, newState);
                    if (newState == BluetoothProfile.STATE_CONNECTED)
                    {
                        //discover all services.
                        if(!configSuccess)
                        {
                            gatt.discoverServices();
                        }
                        Glog.d(TAG,"connect ble device success");
                        bleConnected = true;
                    }
                    else if (newState == BluetoothProfile.STATE_DISCONNECTED)
                    {
                        runOnUiThread(new Runnable()
                        {
                            @Override
                            public void run()
                            {
                                if(!configSuccess)
                                {
                                    if(!bleConnected)
                                    {
                                        Glog.d(TAG,"connect to ble device failed");
//                                    mTextConfigResult.setText("连接设备失败");
                                        if (bleTime < 3){
                                            connectBleDevice(bleAddress);
                                            bleTime ++;
                                        }

                                    }
//                                ShowLeDeviceList();
                                }

                            }
                        });
                        bleConnected = false;
                    }
                }

                @Override
                public void onServicesDiscovered(BluetoothGatt gatt, int status)
                {
                    Glog.i(TAG,"onServicesDiscovered -> status: " + status);
                    boolean foundService = false;
                    super.onServicesDiscovered(gatt, status);
                    if (BluetoothGatt.GATT_SUCCESS == status)
                    {
                        String uuid = null;
                        boolean bWrite = false;
                        for (BluetoothGattService gattService : gatt.getServices())
                        {
                            uuid = gattService.getUuid().toString();
                            Glog.d(TAG,"got service " + uuid);
                            if(uuid.equals("0000fee7-0000-1000-8000-00805f9b34fb"))
                            {
                                foundService = true;
                                List<BluetoothGattCharacteristic> gattCharacteristics = gattService.getCharacteristics();
                                // get all characteristics in the service
                                for (BluetoothGattCharacteristic gattCharacteristic : gattCharacteristics)
                                {
                                    bWrite = false;
                                    uuid = gattCharacteristic.getUuid().toString();
                                    Glog.d(TAG, "got characteristic " + uuid);
                                    if (uuid.equals("0000fec7-0000-1000-8000-00805f9b34fb"))
                                    {
                                        bWrite = true;
                                        mGattCharApInfo = gattCharacteristic;
                                        //// ////
                                        Glog.i(TAG,"wifiName: " + wifiName + "\n" + "pwd: " + pwd);
                                        gattCharacteristic.setValue(wifiName +"\n"+ pwd);

                                    }
                                    else if (uuid.equals("0000fec8-0000-1000-8000-00805f9b34fb"))
                                    {
                                        mGattCharWifiState = gattCharacteristic;
                                    }

                                    if (bWrite)
                                    {
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                if (mGattCharApInfo != null)
                                                {
                                                    Glog.d(TAG, "going to write SSID ");
                                                    mBluetoothGatt.writeCharacteristic(mGattCharApInfo);
                                                }
                                            }
                                        });
                                    }
                                }
                            }
                        }

                        if(!foundService)
                        {
                            mBluetoothGatt.disconnect();
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run()
                                {
                                    Glog.d(TAG,"找不到对应的服务，请重新选择设备");
//                                mTextConfigResult.setText("找不到对应的服务，请重新选择设备");
//                                ShowLeDeviceList();
                                }
                            });
                        }
                    }
                    else
                    {
//                    ShowLeDeviceList();
                    }
                }

                @Override
                public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status)
                {
                    Glog.i(TAG,"onCharacteristicWrite -> status: " + status);
                    super.onCharacteristicWrite(gatt, characteristic, status);
                    if (BluetoothGatt.GATT_SUCCESS == status)
                    {
                        Glog.d(TAG, "onCharacteristicWrite success!");
                        runOnUiThread(new Runnable()
                        {
                            @Override
                            public void run()
                            {
                                //Toast.makeText(MainActivity.this, "写入成功", Toast.LENGTH_SHORT).show();
                                configSuccess = true;
                                if(mGattCharWifiState != null) {
                                    Glog.d(TAG, "enable wifi's state notification");
                                    mBluetoothGatt.setCharacteristicNotification(mGattCharWifiState, true);
                                    BluetoothGattDescriptor descriptor = mGattCharWifiState.getDescriptor(UUID.fromString("00002902-0000-1000-8000-00805f9b34fb"));

                                    mGattCharWifiState.getDescriptors();
                                    if (descriptor != null)
                                    {
                                        descriptor.setValue(BluetoothGattDescriptor.ENABLE_INDICATION_VALUE);
                                        //Toast.makeText(MainActivity.this, "write descriptor", Toast.LENGTH_SHORT).show();
                                        mBluetoothGatt.writeDescriptor(descriptor);
                                        mGattCharWifiState = null;
                                    }

                                }
                            }
                        });
                    }
                    else
                    {
                        runOnUiThread(new Runnable()
                        {
                            @Override
                            public void run()
                            {
                                Glog.d(TAG, "配置网络信息失败");
//                            mTextConfigResult.setText("配置网络信息失败");
                            }
                        });
                    }
                }

                @Override
                public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
                    Glog.i(TAG, "onCharacteristicChanged -->  ");
                    super.onCharacteristicChanged(gatt, characteristic);

                    wifiState = characteristic.getStringValue(0);
                    Glog.d(TAG, "got result: " + wifiState);
                    if (wifiState != null && wifiState.length() > 0)
                    {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                String str;
                                if(wifiState.length() > 7)
                                    str = "设备连接成功: "+wifiState;
                                else
                                    str = "设备连接失败: "+wifiState;
                                Glog.d(TAG, str);
//                            mTextConfigResult.setText(str);
                                if(wifiState.contains("t2:")){ //收到最后一组数据 关闭连接
                                    bleToken = bleToken + wifiState.substring(3,wifiState.length());
                                    Glog.i(TAG,"ble pair sucess ..... token:" + bleToken);
                                    SharePreferenceUtil.setDeviceToken(NetworkConfigActivity.this,bleToken);
                                    mBluetoothGatt.disconnect();
                                    net_success = true;
//                                    net_fail = true;
                                }else if(wifiState.contains("-3003")){ //配置失败
                                    mBluetoothGatt.disconnect();
//                                    net_fail = true;
//                                    if (currentFragment instanceof WifiConnectProgressUIFragment){
//                                        ((WifiConnectProgressUIFragment) currentFragment).bleHandler.sendEmptyMessage(WifiConnectProgressUIFragment.BLE_CONNECT_FAILED);
//                                    }
                                }else if (wifiState.contains("u:")){
                                    bleUuid = wifiState.substring(2,wifiState.length());
                                    Glog.i(TAG,"uuid:" + bleUuid);
                                    SharePreferenceUtil.setDeviceId(NetworkConfigActivity.this,bleUuid);
                                }else if (wifiState.contains("t1:")){
                                    bleToken = wifiState.substring(3,wifiState.length());
                                }
                            }
                        });
                    }
                }
            };
        }

        user_id = SharePreferenceUtil.getUserId(getApplicationContext());
        /*ble end*/

    }

    /*ble start*/
    BluetoothAdapter.LeScanCallback mLeScanCallback = new BluetoothAdapter.LeScanCallback()
    {
        @Override
        public void onLeScan(final BluetoothDevice device, int rssi, byte[] scanRecord)
        {
            runOnUiThread(new Runnable()
            {
                @Override
                public void run()
                {
                    if( device.getName() != null )
                    {
                        addBleItem(device.getName(), device.getAddress());
                    }
                }
            });
        }
    };

    public void addBleItem(String name, String addr)
    {
        Glog.i(TAG,"ble Name: " + name + "addr:" + addr);
        int j;
        for( j = 0; j<bleList.size(); j++)
        {
            String info = bleList.get(j);
            if( info.equals(addr) )
            {
                break;
            }
        }
        if( j>= bleList.size() )
        {
            bleList.add(addr);
        }

        if(name.contains("C_")){ // 匹配BLE设备
            bleAddress = addr;
            connectBleDevice(bleAddress);
        }
    }

    private void connectBleDevice(String  strBleAddr){
        if(strBleAddr != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Glog.i(TAG,"connectBleDevice(................)");
            try {
                mBluetoothAdapter.stopLeScan(mLeScanCallback);
                BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(strBleAddr);
                mBluetoothGatt = device.connectGatt(NetworkConfigActivity.this, false, mBluetoothGattCb, BluetoothDevice.TRANSPORT_LE);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void startBlePair(){
        bleList.clear();
        mBluetoothAdapter.startLeScan(mLeScanCallback);
        handler.sendEmptyMessageDelayed(STOP_BLE,30*1000);

//        deviceNetworkStart();
//        runOnUiThread();


    }

    /*ble end*/

    public void startSppPair(){
        if (!isBindDevice()){ //没有配对 再去搜索
            mBtAdapter.startDiscovery();
        }
        connectTime = 0;
    }

    @Override
    protected void onResume() {
        Glog.i("jys","NetworkConfigActivity->onResume()");
        need_sendData = true;
        bleTime = 0;
        super.onResume();
    }

    @Override
    public void pushFragment(Fragment fragment, String tag) {
        FragmentTransaction transaction = this.getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.network_container, fragment, tag);
        transaction.addToBackStack(tag);
        transaction.commitAllowingStateLoss();
    }

    @Override
    public void popFragment(String tag) {
        this.getSupportFragmentManager().popBackStackImmediate(tag, FragmentManager.POP_BACK_STACK_INCLUSIVE);
    }

    @Override
    public void popAllFragment() {
        int num = this.getSupportFragmentManager().getBackStackEntryCount();
        Glog.i(TAG,"++++++++++++++++++++++++++++++++++Fragment回退栈数量"+num);
        this.getSupportFragmentManager().popBackStackImmediate(null, android.support.v4.app.FragmentManager.POP_BACK_STACK_INCLUSIVE);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK){
            onBackPressed();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onBackPressed() {
        if(mBackHandedFragment == null || !mBackHandedFragment.onBackPressed()){
            if(getSupportFragmentManager().getBackStackEntryCount() == 0){
                btList.clear();
                BluetoothListFragment.connecting = false;
                connected = false;
                this.finish();
                overridePendingTransition(R.anim.push_right_in, R.anim.push_left_out);
            }else{
                if (currentFragment instanceof BluetoothListFragment){
                    btList.clear();
                }
                getSupportFragmentManager().popBackStack();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeMessages(START_SENDWIFI);
        unregisterReceiver(mReceiver);
        shutdownThread();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if(outState != null) {
            String FRAGMENTS_TAG = "android:support:fragments";
            // remove掉保存的Fragment
            outState.remove(FRAGMENTS_TAG);
        }
    }

    @Override
    public void onAttachFragment(Fragment fragment) {
        super.onAttachFragment(fragment);
        currentFragment = fragment;

//        getFragmentManager().findFragmentById()
    }

    private boolean getComaanDevice(){
        for (int i = 0; i < btList.size(); i++) {
            BtDevice btDevice = btList.get(i);
            if (compareDevice(btDevice.getName())) {//发现酷曼设备
                bTAddress = btDevice.getMac();
                return true;
            }
        }
        return false;
    }

    private void getComaanDeviceByIndex(int index){
        BtDevice btDevice = btList.get(index);
        bTAddress = btDevice.getMac();
    }

    // 11.09 改成匹配"C_" 通过SPP服务传过来
    private boolean compareDevice(String name) {
        return name != null && name.contains("CMBT_");
//        String uuid = SharePreferenceUtil.getDeviceId(this);
//        Glog.i("jys", "uuid: " + uuid);
//        int compareInt = 6;
//        return uuid.length() >= 6 && name != null && name.length() > 6 && name.substring((name.length() - compareInt), name.length()).equals(uuid.substring((uuid.length() - compareInt), uuid.length()));

    }

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
        if (msg.what == START_CONNECT){
            int index = msg.arg1;
            Glog.i("jys"," bt connect start ... " + index);
            if (btList.size() == 0){
                Glog.i("jys",getString(R.string.coomaan_device_not_found));
                return;
            }
            getComaanDeviceByIndex(index);
            Glog.i("jys","start connect ... ");
            boolean btOpen = true;
            if(bTAddress != null && !bTAddress.equals("")) {
                connectThread = new ConnectThread(bTAddress);
                connectThread.start();
            }
            else {
                Glog.i("jys",getString(R.string.coomaan_device_bt_null));
            }

        }else if (msg.what == START_SENDWIFI){
            if (pwd != null && wifiName != null){
                String pair_string = "*#SSID:" + wifiName + ",PWD:" + pwd +";";//+  ",IP:" + getIp()+",PORT:" + PORT +
                sendMessageHandle(pair_string);

            }
        /*ble start*/
        }else if (msg.what == STOP_BLE){
            mBluetoothAdapter.stopLeScan(mLeScanCallback);
            Glog.i(TAG,"Ble list:" + bleList);
        }
        /*ble end*/
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // User chose not to enable Bluetooth.
        if (requestCode == REQUEST_ENABLE_BT && resultCode == Activity.RESULT_CANCELED) {
            finish();
            overridePendingTransition(R.anim.push_right_in, R.anim.push_left_out);
            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * 发送配网数据
     * @param pair_string
     */
    public void sendMessageHandle(String pair_string) {
        if (socket == null) {
            Glog.i("jys","没有连接");
            return;
        }
        sendThread = new SendThread(pair_string);
        sendThread.start();
    }

    /**
     * 判断设备是否已经配对
     * @return
     */
    private boolean isBindDevice(){
        Set<BluetoothDevice> devices = mBtAdapter.getBondedDevices();
        Iterator it=devices.iterator();
        while (it.hasNext()){
            BluetoothDevice device = (BluetoothDevice)it.next();
            BtDevice btDevice = new BtDevice();
            if (compareDevice(device.getName())){// 酷曼设备
                String [] address = device.getAddress().split(":");
                btDevice.setName(device.getName());
                btDevice.setMac(device.getAddress());
                btDevice.setBind(true);
                for (int i = 0; i< btList.size();i++){
                    BtDevice tempDevice = btList.get(i);
                    if (tempDevice.getName().equals(btDevice.getName())){
                        return false;
                    }
                }
                btList.add(btDevice);
                Glog.i("jys"," -- 发现已配对的酷曼设备 --: " + device.getName());
            }

//            if (compareDevice(btDevice.getName())){
//                Glog.i("jys","酷曼设备已配对，开始连接...");
//                handler.sendEmptyMessage(START_CONNECT);
//                return true;
//            }
            Glog.i("jys",btDevice.toString());
        }
        return false;
    }

    /**
     * 获取本地IP
     * @return
     */
    private String getIp(){
        WifiManager wifiManager = (WifiManager)getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        int ipAddress = wifiInfo.getIpAddress();
        //  Glog.d(Tag, "int ip "+ipAddress);
        if(ipAddress==0)return null;
        return ((ipAddress & 0xff)+"."+(ipAddress>>8 & 0xff)+"."
                +(ipAddress>>16 & 0xff)+"."+(ipAddress>>24 & 0xff));
    }

    /**
     * 监听搜索蓝牙设备
     */
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            // When discovery finds a device
            if (BluetoothDevice.ACTION_FOUND.equals(action))
            {
                // Get the BluetoothDevice object from the Intent
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                // If it's already paired, skip it, because it's been listed already
                if (device.getBondState() != BluetoothDevice.BOND_BONDED)
                {
                    if (compareDevice(device.getName())){ // compareDevice(device.getName())
                        BtDevice btDevice = new BtDevice();
                        String [] address = device.getAddress().split(":");
                        btDevice.setName(device.getName());
                        btDevice.setMac(device.getAddress());
                        btDevice.setBind(false);
                        for (int i = 0; i< btList.size();i++){
                            BtDevice tempDevice = btList.get(i);
                            if (tempDevice.getName().equals(btDevice.getName())){
                                return;
                            }
                        }
                        btList.add(btDevice);
                    }
                    Glog.i("jys","BT:  " + device.getName() + "-" + device.getAddress());
//                    if (compareDevice(btDevice.getName())){
//                        handler.sendEmptyMessage(START_CONNECT);
//                        mBtAdapter.cancelDiscovery();
//                    }
                }
                // When discovery is finished, change the Activity title
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
//                Glog.i("jys","bluetooth discovery finish... " );
                Glog.i("jys","btList： " + btList);
                btScan = "stop";
                if (currentFragment instanceof BluetoothListFragment){
                    ((BluetoothListFragment) currentFragment).btHandler.sendEmptyMessage(BluetoothListFragment.BT_REFRESH_FINISHED);
                }
//                if (!getComaanDevice()){
//                    Glog.i("jys","没有发现酷曼设备");
////                    ToastUtils.showLongSafe("没有发现未配对的设备");
//                }
            }
        }
    };

    /**
     * 蓝牙连接Thread
     */
    public static boolean connected = false;
    private int connectTime = 0;

    @Override
    public void onFragmentInteraction(Uri uri) {
//        Toast.makeText(this,"和NetworkConfigActivity 通信" + uri,Toast.LENGTH_LONG).show();
        if (uri.toString().equals("scan")){
            btScan = "start";
            startSppPair();
//            Toast.makeText(this,"启动搜索蓝牙",Toast.LENGTH_LONG).show();
        }else if (uri.toString().contains("connect")){
            connectTime = 0; //重置配网次数
            Message msg = new Message();
            msg.what = START_CONNECT;
            msg.arg1 =   Integer.parseInt(uri.toString().split("/")[1]);
            Glog.i("jys","connect bluetooth index :" + msg.arg1);
            handler.sendMessage(msg);
        }else if (uri.toString().equals("send_wifi")){
            Glog.i("jys","send_wifi...");
            handler.sendEmptyMessage(START_SENDWIFI);
            //启动接受数据
            readThread = new ReadThread();
            readThread.start();
        }else if (uri.toString().equals("start_ble")){
            startBlePair();
        }else if (uri.toString().equals("re-ble-pair")){
            Glog.i(TAG,"re-ble-pair");
            bleTime = 0;
            if (mBluetoothGatt!=null){
                mBluetoothGatt.disconnect();
            }
        }

    }

    @Override
    public void setSelectedFragment(BackHandledFragment selectedFragment) {
        this.mBackHandedFragment = selectedFragment;
    }

    private class ConnectThread extends Thread {
        String macAddress = "";

        ConnectThread(String mac) {
            macAddress = mac;
        }

        public void run() {
            Glog.i("jys","ConnectThread -> run() + ip" + macAddress);
            connected = false;
            if(mBtAdapter == null){
                mBtAdapter = BluetoothAdapter.getDefaultAdapter();
            }
            mBluetoothDevice = mBtAdapter.getRemoteDevice(macAddress);
            mBtAdapter.cancelDiscovery();

            while (!connected && connectTime < 9) {
                pairDevice();
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        public void cancel() {
            try {
                socket.close();
                socket = null;
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
            }
        }
    }

    /**
     * 配对设备
     */
    protected void pairDevice() {
        try {
            // 连接建立之前的先配对
            Glog.i("jys","mBluetoothDevice: " + mBluetoothDevice + "time: " + connectTime);
            if (mBluetoothDevice.getBondState() == BluetoothDevice.BOND_NONE) {
                Method creMethod = BluetoothDevice.class
                        .getMethod("createBond");
                Glog.i("jys","开始配对！");
                creMethod.invoke(mBluetoothDevice);

//                mBluetoothDevice.createBond();
                connectTime++;
                if (connectTime == 5){
                    if (currentFragment instanceof BluetoothListFragment){
                        ((BluetoothListFragment) currentFragment).btHandler.sendEmptyMessage(BluetoothListFragment.BT_CONNECT_FAILED);
                    }
                }
            } else if(mBluetoothDevice.getBondState() == BluetoothDevice.BOND_BONDED){
                connectDevice();
            }
        } catch (Exception e) {
            // TODO: handle exception
            //DisplayMessage("无法配对！");
            Glog.i("jys","配对失败！");
            e.printStackTrace();
        }

    }

    /**
     * 发起socket连接
     */
    private void connectDevice(){
        Glog.i("jys","connectDevice...");
        try {
            socket = mBluetoothDevice.createRfcommSocketToServiceRecord(UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"));
        } catch (IOException e) {
            // TODO Auto-generated catch block
            Glog.i("jys","socket 创建失败。。。。。");
            e.printStackTrace();
        }
        try {
            Glog.i("jys","socket.connecting...............");
//            if (socket.isConnected()){
//                connected = true;
//                Glog.i("jys","socket is connected...............");
//            }else {
                socket.connect();
                connected = true;
                Glog.i("jys","socket connected...............");
//            }

            Glog.i("jys","连接成功!");

            if (currentFragment instanceof BluetoothListFragment){
                ((BluetoothListFragment) currentFragment).btHandler.sendEmptyMessage(BluetoothListFragment.BT_CONNECT_SUCESS);
            }
//            handler.sendEmptyMessage(START_SENDWIFI);
            //启动接受数据
            readThread = new ReadThread();
            readThread.start();


        } catch (IOException e) {
            // TODO: handle exception
            Glog.i("jys","连接失败!");
            connected = false;
            if(connectTime == 4){
                if (currentFragment instanceof BluetoothListFragment){
                    ((BluetoothListFragment) currentFragment).btHandler.sendEmptyMessage(BluetoothListFragment.BT_CONNECT_FAILED);
                }
            }

//            try {
//                socket.close();
//                socket = null;
//            } catch (IOException e2) {
//                // TODO: handle exception
//                Glog.e(TAG, "Cannot close connection when connection failed");
//            }
        } finally {
            connectTime++;
        }
    }

    private void saveUuidAndToken(String sppString){
        String[] uuidAndToken = sppString.split("&");
        Glog.i("jys","uuidAndToken:" + Arrays.toString(uuidAndToken));
        if (uuidAndToken.length == 3){
            String uuid = uuidAndToken[1];
            String token = uuidAndToken[2];
            Glog.i("jys","uuid: " + uuid + "  token: " + token);
            SharePreferenceUtil.setDeviceId(this,uuid);
            SharePreferenceUtil.setDeviceToken(this,token);
        }else {
            Glog.i("jys","uuid and token error!");
        }
    }
    //读取数据
    private class ReadThread extends Thread {
        public void run() {

            byte[] buffer = new byte[1024];
            int bytes;
            InputStream mmInStream = null;

            try {
                if (socket != null){
                    mmInStream = socket.getInputStream();
                }
            } catch (IOException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
            while (!net_success) { //根据是否配网成功来判断线程
                try {
                    // Read from the InputStream
                    if ((bytes = mmInStream.read(buffer)) > 0) {
                        byte[] buf_data = new byte[bytes];
                        for (int i = 0; i < bytes; i++) {
                            buf_data[i] = buffer[i];
                        }
                        String s = new String(buf_data);
                        Glog.i("jys", "readData: " + s);

                        if (s.contains("CNF")){ //设备收到消息，取消发送Data
                            Glog.i("jys","data set sucess ............");
                            need_sendData = false;
                        }else {
                            need_sendData = true;
                        }
                        if (s.contains("IND")) // 配网成功
                        {
                            Glog.i("jys","network  sucess............");
                            saveUuidAndToken(s);
                            net_success = true;
                        }
                    }
                } catch (IOException e) {
                    try {
                        mmInStream.close();
                    } catch (IOException e1) {
                        // TODO Auto-generated catch block
                        e1.printStackTrace();
                    }
                    break;
                }
            }
        }
    }

    //蓝牙发送Thread
    private class SendThread extends Thread{
        String message;
        SendThread(String msg) {
            message = msg;
        }

        @Override
        public void run() {
            while (need_sendData){
                Glog.i("jys","sendMessageHandle.......: " + message);
                try {
                    if (socket != null){
                        OutputStream os = socket.getOutputStream();
                        os.write(message.getBytes());
                    }
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    private void shutdownThread(){
        new Thread(){
            @Override
            public void run() {
                if (connectThread != null){
                    connectThread.interrupt();
                    connectThread = null;
                }
                if (readThread != null){
                    readThread.interrupt();
                    readThread = null;
                }
                if (sendThread != null){
                    sendThread.interrupt();
                    sendThread = null;
                }
                if (socket != null) {
                    try {
                        socket.close();
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    socket = null;
                }
            }
        }.start();
    }



}
