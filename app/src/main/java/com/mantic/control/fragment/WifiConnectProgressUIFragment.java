package com.mantic.control.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.iot.sdk.HttpStatus;
import com.baidu.iot.sdk.IoTException;
import com.baidu.iot.sdk.IoTRequestListener;
import com.baidu.iot.sdk.IoTSDKManager;
import com.baidu.iot.sdk.model.DeviceInfo;
import com.baidu.iot.sdk.model.PageInfo;
import com.google.gson.Gson;
import com.ingenic.music.agent.IngenicAbstractEasyLinkAgent;
import com.mantic.antservice.DeviceManager;
import com.mantic.antservice.listener.BindRequestListener;
import com.mantic.antservice.util.ToastUtils;
import com.mantic.control.ManticApplication;
import com.mantic.control.R;
import com.mantic.control.activity.LoadingActivity;
import com.mantic.control.activity.MainActivity;
import com.mantic.control.activity.NetworkConfigActivity;
import com.mantic.control.activity.SelectInterestActivity;
import com.mantic.control.api.account.AccountRetrofit;
import com.mantic.control.api.account.AccountServiceApi;
import com.mantic.control.api.account.AccountUrl;
import com.mantic.control.entiy.WifiInformation;
import com.mantic.control.ui.view.RoundProgressBar;
import com.mantic.control.utils.ClientWifi;
import com.mantic.control.utils.Glog;
import com.mantic.control.utils.MD5Util;
import com.mantic.control.utils.SharePreferenceUtil;
import com.mantic.control.utils.WifiOpenHelper;
import com.rdaressif.iot.rdatouch.IRdatouchResult;
import com.rdaressif.iot.rdatouch.IRdatouchTask;
import com.rdaressif.iot.rdatouch.RdatouchTask;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by wujiangxia on 2017/4/1.
 */
public class WifiConnectProgressUIFragment extends Fragment implements View.OnClickListener ,IngenicAbstractEasyLinkAgent.EasyLinkCallBack {
    private static final String TAG = WifiConnectProgressUIFragment.class.getName();
    public static final int BLE_CONNECT_SUCCESS = 203;
    public static final int BLE_CONNECT_FAILED = 204;
    private Button btn_cancel;
    private View view;
    public RoundProgressBar roundProgressBar;
    int precent = 0;
    private PowerManager.WakeLock mWakeLock;
    private TextView wifiConnectText;
    public Handler handler = new Handler();
    private int timeTicket = 0;
    String apSsid;
    String apPassword;
    private boolean httpBind  = false;
    private int connectCount = 43;
    ArrayList<WifiInformation> wifiArray = new ArrayList<WifiInformation>();
    private boolean needAddWifi = true;

    private boolean showUnbind = false;
    boolean is5gWifi;

    private ClientWifi clientWifi;
    private String currentSSID;
    private boolean reconnectWifi;
    private String SSID_AP = "Coomaan" + 1;
    private ConnectionBroadcastReceiver connectionBroadcastReceiver;
    private static final int RECONNECT=123;
    private WifiOpenHelper wifiOpenHelper;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Glog.i(TAG, "WifiConnectProgressUIFragment initView");
        if (view == null) {
            view = inflater.inflate(R.layout.wificonnect_ui_progress_frag,container,false);
            initLayout();
        }
        if (WifiConnectUIFrament.rdaSmartConfig){
            Bundle bundle = getArguments();
            apSsid = bundle.getString("param1");
            apPassword = bundle.getString("param2");
            String apBssid = bundle.getString("param3");
            String isSsidHiddenStr = bundle.getString("param4");
            String taskResultCountStr = bundle.getString("param5");
            is5gWifi = bundle.getBoolean("param6");
            Glog.i(TAG,"apSsid:" + apSsid + "  apPassword:" + apPassword + "  apBssid:" + apBssid +"  isSsidHiddenStr:" + isSsidHiddenStr +"  taskResultCountStr:" + taskResultCountStr);
            if (apSsid.equals(getSSid())){
                if(wifiOpenHelper.getCoomaanSSID().size()>0){
                    startSetupNetwork(apSsid,apPassword);
                    NetworkConfigActivity.net_success = true;
                }else {
                    new RdatouchAsyncTask3().execute(apSsid,apBssid,apPassword,isSsidHiddenStr,taskResultCountStr);
                }

            }

            wifiArray = SharePreferenceUtil.loadWifiArray(getContext());
            for (int i = 0;i<wifiArray.size();i++){
                WifiInformation tempInfo = wifiArray.get(i);
                if (tempInfo.getWifiName().equals(apSsid)){
                    needAddWifi = false;
                    break;
                }
            }
            if (needAddWifi){
                WifiInformation wifiInfo = new WifiInformation();
                wifiInfo.setWifiName(apSsid);
                wifiInfo.setWifiPwd(apPassword);
                wifiArray.add(wifiInfo);
            }

            if (is5gWifi){
                ToastUtils.showLong(R.string.wifi_5g_toast);
            }
        }
        return view;
    }

    private void startSetupNetwork(String ssid,String password) {
        clientWifi = new ClientWifi(getContext());
        Log.e(TAG, "startSetupNetwork ssid: " + ssid + " password:" + password);
        currentSSID = clientWifi.getCurrentSSID();

        reconnectWifi = false;
        clientWifi.setStatusListener(new ClientWifi.StatusListener() {
            @Override
            public void progress(final String content) {

                if (content.contains("重连") && !SSID_AP.equals(currentSSID)) {
                    Log.e(TAG, "progress: 开始重连" );
                            reconnectWifi = true;
                        }
                        if (content.contains("重连") && SSID_AP.equals(currentSSID)) {
                            Log.e(TAG, "progress: 配网结束");
                        }
                        if (content.contains("抱歉") || content.contains("未找到Coomaan设备")) {
                            Log.e(TAG, "progress: 配网失败" );
                        }
                    }
                });

        Log.e(TAG, "startSetupNetwork currentSSID: " + currentSSID);
        if (SSID_AP.equals(currentSSID)) {
            clientWifi.sendSSIDAndPassword(ssid, password);
        } else
            clientWifi.connectAPAndSendSSIDAndPassword(ssid, password);
    }

    private String getSSid() {

        WifiManager wm = (WifiManager) getActivity().getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        if (wm != null) {
            WifiInfo wi = wm.getConnectionInfo();
            if (wi != null) {
                String ssid = wi.getSSID();
                if (ssid.length() > 2 && ssid.startsWith("\"") && ssid.endsWith("\"")) {
                    return ssid.substring(1, ssid.length() - 1);
                } else {
                    return ssid;
                }
            }
        }

        return "";
    }
    private void initLayout() {
        btn_cancel = (Button) view.findViewById(R.id.btn_cancel);
        btn_cancel.setOnClickListener(this);
        roundProgressBar = (RoundProgressBar) view.findViewById(R.id.roundProgressBar);
        wifiConnectText = (TextView) view.findViewById(R.id.wificonnect_text);
        roundProgressBar.setCurrentValue(98);
        mWakeLock = ((PowerManager) getActivity().getSystemService(Context.POWER_SERVICE)).newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, "wifi");
        mWakeLock.acquire(120 * 1000L);
        wifiOpenHelper=new WifiOpenHelper(getContext());
    }

    @Override
    public void onPause() {
        super.onPause();
        getActivity().unregisterReceiver(connectionBroadcastReceiver);
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.e(TAG, "onResume: ------onResume-----");
        connectionBroadcastReceiver=new ConnectionBroadcastReceiver();
        IntentFilter intentFilter = new IntentFilter("android.net.wifi.STATE_CHANGE");
        getActivity().registerReceiver(connectionBroadcastReceiver,intentFilter);

        handler.postDelayed(progressRun,5000);
    }
    @SuppressLint("HandlerLeak")
    public Handler reconnectHandle=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case RECONNECT:
                    if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.LOLLIPOP){
                        clientWifi.setDefaultNetwork();

                    }
                    break;
            }
        }
    };
    @SuppressLint("HandlerLeak")
    public Handler bleHandler = new Handler(){

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case BLE_CONNECT_SUCCESS:
                    Glog.i(TAG,"bleHandler -> BLE_CONNECT_SUCCESS.......");
                    break;
                case BLE_CONNECT_FAILED:
                    Glog.i(TAG,"bleHandler -> BLE_CONNECT_FAILED.......");
                    WifiConnectFailUIFrament wifiConnectFailUIFrament = new WifiConnectFailUIFrament();
                    Bundle bundle = new Bundle();
                    bundle.putBoolean("param1",is5gWifi);
                    bundle.putString("param2","blue_connected_failed");
                    wifiConnectFailUIFrament.setArguments(bundle);
                    if (getActivity() instanceof FragmentEntrust) {
                        handler.removeCallbacks(progressRun);
                        ((FragmentEntrust) getActivity()).pushFragment(wifiConnectFailUIFrament, "f3");
                    }
                    break;
            }
        }
    };
    public Runnable progressRun = new Runnable() {
        @Override
        public void run() {
            Glog.i(TAG,"UIprogressRun ... sucess: " + NetworkConfigActivity.net_success + "--" + timeTicket + "--httpBind:" + httpBind);
            if (httpBind){ //配网成功
                handler.removeCallbacks(progressRun);
                if (SharePreferenceUtil.getBind(getActivity()) == 1){ // 已经绑定的设备 配网完成后直接返回
                    SharePreferenceUtil.saveWifiArray(getActivity(),wifiArray);
                    if (wifiConnectText != null && btn_cancel!=null && roundProgressBar != null){
                        wifiConnectText.setText(getText(R.string.wifi_connect_sucess));
                        btn_cancel.setVisibility(View.GONE);
                        roundProgressBar.setTickPath(true);
                    }
                    if (showUnbind){
                        ToastUtils.showLong("请解绑当前设备在进行新设备绑定！");
                    }
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            getActivity().finish();
                        }
                    },2000);
                }else {
                    handler.post(bindRunnable);
//                    DeviceManager.getInstance().bindDevice(SharePreferenceUtil.getDeviceId(getActivity()),SharePreferenceUtil.getDeviceToken(getActivity()),new BaiduBindRequest(getActivity()));
                }
            }else {
//                if (NetworkConfigActivity.net_fail){
//                    if (getActivity() instanceof FragmentEntrust) {
//                        ((FragmentEntrust) getActivity()).pushFragment(new WifiConnectFailUIFrament(), "f3");
//                    }
//                }else {

                deviceNetworkCheck();
                    timeTicket++;
                    if (timeTicket >= connectCount){ // 超时处理
                            WifiConnectFailUIFrament wifiConnectFailUIFrament = new WifiConnectFailUIFrament();
                            Bundle bundle = new Bundle();
                            bundle.putBoolean("param1",is5gWifi);
                            bundle.putString("param2","time-out");
                            wifiConnectFailUIFrament.setArguments(bundle);
                            if (getActivity() instanceof FragmentEntrust) {
                                ((FragmentEntrust) getActivity()).pushFragment(wifiConnectFailUIFrament, "f3");
                            }
                        }

                    }
//                    else {
//                        handler.postDelayed(progressRun,5000);
//                    }
//                }
            }

    };

    private int bindCount = 0;
    private Runnable bindRunnable = new Runnable() {
        @Override
        public void run() {
            DeviceManager.getInstance().bindDevice(SharePreferenceUtil.getDeviceId(getActivity()),SharePreferenceUtil.getDeviceToken(getActivity()),new BaiduBindRequest(getActivity()));

        }
    };


    /**
     * {
     "router_id": "router_id",
     "bind_type": "2",
     "device_token": "343434",
     "user_id": "b5231c42c5f7431caaa5076883634d92",
     "device_uuid": "122332"
     }

     }

     */
    private void deviceNetworkCheck(){
        String accessToken = IoTSDKManager.getInstance().getAccessToken().getAccessToken();

        AccountServiceApi accountServiceApi = AccountRetrofit.getInstance().create(AccountServiceApi.class);
        Map<String,String> map = new HashMap<String, String>();
        map.put("Content-Type","application/json");
        map.put("Authorization","Bearer " + accessToken);
        map.put("Lc",ManticApplication.channelId);

        String request = "{\"user_id\":\""+ NetworkConfigActivity.user_id+"\",\"router_id\":\""+MD5Util.encrypt(apSsid+apPassword)+"\"}";
        Glog.i(TAG," deviceNetworkCheck - > request:" + request);
        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"),request);

        Call<ResponseBody> call = accountServiceApi.deviceNetworkCheck(map,body);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()){
                    JSONObject mainObject; //json 主体
                    try {
                        mainObject = new JSONObject(response.body().string());
                        JSONObject dataObject;
                        if (!AccountUrl.BASE_URL.contains("v2")){
                            dataObject = mainObject;
                        }else {// server v2 接口
                            dataObject = mainObject.getJSONObject("data");
                        }
                        String  data  = new Gson().toJson(dataObject);
                        Glog.i(TAG,"deviceNetworkCheck: " + data);
                        String bind_type = dataObject.getString("bind_type");
                        if (bind_type.equals("2")) {//绑定成功了
                            if (!Objects.equals(SharePreferenceUtil.getDeviceId(getContext()), "")&& !Objects.equals(SharePreferenceUtil.getDeviceToken(getContext()), "")){//说明已经绑定了设备
                                if (SharePreferenceUtil.getDeviceId(getContext()).equals(dataObject.getString("device_uuid"))){//同一个设备
                                    Glog.i("jys","同一个设备配网。。。。。。。。。。。");
                                    SharePreferenceUtil.setDeviceId(getContext(),dataObject.getString("device_uuid"));
                                    SharePreferenceUtil.setDeviceToken(getContext(),dataObject.getString("device_token"));
                                    httpBind = true;
                                    if (timeTicket < connectCount){
                                        handler.postDelayed(progressRun,1000);
                                    }
                                }else {//不同设备
                                    Glog.i("jys","不同一个设备配网。。。。。。。。。。");
                                    showUnbind = true;
                                    httpBind = true;
                                    if (timeTicket < connectCount){
                                        handler.postDelayed(progressRun,1000);
                                    }
                                }
                            } else {
                                Glog.i("jys","新设备配网。。。。。。。。。。。");
                                SharePreferenceUtil.setDeviceId(getContext(),dataObject.getString("device_uuid"));
                                SharePreferenceUtil.setDeviceToken(getContext(),dataObject.getString("device_token"));
                                httpBind = true;
                                if (timeTicket < connectCount){
                                    handler.postDelayed(progressRun,1000);
                                }
                            }


                        }else {
                            if (timeTicket < connectCount){
                                handler.postDelayed(progressRun,5000);
                            }
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                if (timeTicket < connectCount){
                    handler.post(progressRun);
                }
                Glog.i(TAG,"deviceNetworkCheck fail: ");
            }
        });
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.btn_cancel:
                if (getActivity() instanceof FragmentEntrust) {
                    ((NetworkConfigActivity)getActivity()).need_sendData = true;
                    ((FragmentEntrust) getActivity()).popAllFragment();
                }

                break;

        }
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Glog.i(TAG, "WifiConnectProgressUIFragment - onDestroyView");
        handler.removeCallbacks(progressRun);
    }

    private void coomaanBindDevice(Context context){
        String accessToken = IoTSDKManager.getInstance().getAccessToken().getAccessToken();
        Glog.i(TAG,"accessToken: " + accessToken);
        String user_id = SharePreferenceUtil.getUserId(context);
        String uuid = SharePreferenceUtil.getDeviceId(context);
        String token = SharePreferenceUtil.getDeviceToken(context);

        AccountServiceApi accountServiceApi = AccountRetrofit.getInstance().create(AccountServiceApi.class);
        Map<String,String> map = new HashMap<String, String>();
        map.put("Content-Type","application/json");
        map.put("Authorization","Bearer " + accessToken);
        map.put("Lc", ManticApplication.channelId);

        String request = "{\"user_id\":\""+ user_id+"\",\"device_uuid\":\""+ uuid+"\",\"device_token\":\""+ token+"\"}";
        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"),request);

        Call<ResponseBody> call = accountServiceApi.deviceBind(map,body);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful() && null == response.errorBody()){
                    JSONObject mainObject; //json 主体
                    try {
                        mainObject = new JSONObject(response.body().string());
                        Glog.i(TAG,"coomaanBindDevice sucess: " + new Gson().toJson(mainObject));
                        JSONObject dataObject;
                        if (!AccountUrl.BASE_URL.contains("v2")){
                            dataObject = mainObject;
                        }else {// server v2 接口
                            dataObject  = mainObject.getJSONObject("data");
                        }

                        if(mainObject.getString("retcode").equals("0")){
                            getDeviceInfo();
                        }
                        String  data  = new Gson().toJson(dataObject);
                    } catch (JSONException | IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Glog.i(TAG,"coomaanBindDevice fail: ");
            }
        });
    }

    //x1000 wifi connect
    @Override
    public void onSending(int i, int i1) {
        Glog.i(TAG,"X1000 -->onSending ... ");
    }

    @Override
    public void onConfigSuccess() {
        Glog.i(TAG,"X1000 -->onConfigSuccess ... ");
        DeviceManager.getInstance().bindDevice(SharePreferenceUtil.getDeviceId(getActivity()),SharePreferenceUtil.getDeviceToken(getActivity()),new BaiduBindRequest(getActivity()));
    }

    @Override
    public void onConfigFail() {
        WifiConnectUIFrament.mEasyAgent.exit();
        Glog.i(TAG,"X1000 -->onConfigFail ... ");
    }

    @Override
    public void onConfigTimeout() {
//        WifiConnectUIFrament.mEasyAgent.exit();
//        Glog.i(TAG,"X1000 -->onConfigTimeout ... ");
//        WifiConnectFailUIFrament wifiConnectFailUIFrament = new WifiConnectFailUIFrament();
//        Bundle bundle = new Bundle();
//        bundle.putBoolean("param1",is5gWifi);
//        wifiConnectFailUIFrament.setArguments(bundle);
//        if (this.getActivity() instanceof FragmentEntrust) {
//            ((FragmentEntrust) this.getActivity()).pushFragment(wifiConnectFailUIFrament, "f3");
//        }
    }

    @Override
    public void onNoWifiError() {
        WifiConnectUIFrament.mEasyAgent.exit();
        Glog.i(TAG,"X1000 -->onNoWifiError ... ");
    }

    @Override
    public void onPasswordError() {
        WifiConnectUIFrament.mEasyAgent.exit();
        Glog.i(TAG,"X1000 -->onPasswordError ... ");
    }

    @Override
    public void onConfigStart() {

    }

    @Override
    public void onConfigStop() {

    }
    //x1000 wifi connect

    //rda new
    private class RdatouchAsyncTask3 extends AsyncTask<String, Void, List<IRdatouchResult>> {

        private ProgressDialog mProgressDialog;

        private IRdatouchTask mRdatouchTask;
        // without the lock, if the user tap confirm and cancel quickly enough,
        // the bug will arise. the reason is follows:
        // 0. task is starting created, but not finished
        // 1. the task is cancel for the task hasn't been created, it do nothing
        // 2. task is created
        // 3. Oops, the task should be cancelled, but it is running
        private final Object mLock = new Object();

        @Override
        protected void onPreExecute() {

        }

        @Override
        protected List<IRdatouchResult> doInBackground(String... params) {
            int taskResultCount = -1;
            synchronized (mLock) {
                // !!!NOTICE
                String apSsid = params[0];
                String apBssid = params[1];
                String apPassword = params[2];
                String isSsidHiddenStr = params[3];
                String taskResultCountStr = params[4];
                boolean isSsidHidden = false;
                if (isSsidHiddenStr.equals("YES")) {
                    isSsidHidden = true;
                }
                if (TextUtils.isEmpty(taskResultCountStr)) {
                    return new ArrayList<IRdatouchResult>();
                }
                taskResultCount = Integer.parseInt(taskResultCountStr);
                if (null == getActivity()) {
                    return new ArrayList<IRdatouchResult>();
                }
                Glog.i(TAG,"RdatouchAsyncTask3 -> doInBackground: " + apSsid + "===: "+apPassword);
                mRdatouchTask = new RdatouchTask(apSsid, apBssid, apPassword,
                        isSsidHidden, getActivity());
//                mRdatouchTask.setRdatouchListener(myListener);
            }
            List<IRdatouchResult> resultList = mRdatouchTask.executeForResults(taskResultCount);
            return resultList;
        }

        @Override
        protected void onPostExecute(List<IRdatouchResult> result) {
            if (null != result && result.size() > 0) {
                IRdatouchResult firstResult = result.get(0);
                // check whether the task is cancelled and no results received
                if (!firstResult.isCancelled()) {
                    int count = 0;
                    // max results to be displayed, if it is more than maxDisplayCount,
                    // just show the count of redundant ones
                    final int maxDisplayCount = 5;
                    // the task received some results including cancelled while
                    // executing before receiving enough results
                    if (firstResult.isSuc()) {
                        Glog.i(TAG,"Smartconfig pair network success..........");
                        //sucess
                        if (SharePreferenceUtil.getBind(getActivity()) == 1) { // 已经绑定的设备 配网完成后直接返回
                            NetworkConfigActivity.net_success = true;
//                            if (wifiConnectText != null && btn_cancel != null && roundProgressBar != null) {
//                                wifiConnectText.setText(getText(R.string.wifi_connect_sucess));
//                                btn_cancel.setVisibility(View.GONE);
//                                roundProgressBar.setTickPath(true);
//                            }
//                            handler.postDelayed(new Runnable() {
//                                @Override
//                                public void run() {
//                                    getActivity().finish();
//                                }
//                            }, 2000);
                        }else {
                            NetworkConfigActivity.net_success = true;
                        }
                    } else {
                        //fail
                    }
                }
            }
        }
    }
    //rad new

    private void netWorkAndBindSucess(){
        getActivity().sendBroadcast(new Intent("close_bind_activity"));
        new Handler().postDelayed(
                new Runnable() {
                    @Override
                    public void run() {
                        SharePreferenceUtil.setSharePreferenceData(getActivity(), "Mantic", "isAutoMaticPlay", "true");
                        if (wifiArray.size()>=4){
                            wifiArray.remove(0);
                            SharePreferenceUtil.saveWifiArray(getActivity(),wifiArray);
                        }else {
                            SharePreferenceUtil.saveWifiArray(getActivity(),wifiArray);
                        }
                        if (((ManticApplication) getActivity().getApplicationContext()).isAreadyAddInterestData()) {
                            Intent intent1 = new Intent(getActivity(), MainActivity.class);
                            getActivity().startActivity(intent1);
//                                mContext.finish();
                        } else {
                            Intent intent1 = new Intent(getContext(), SelectInterestActivity.class);
                            getActivity().startActivity(intent1);
//                                mContext.finish();
                        }
                        if(getActivity()!=null){
                            getActivity().finish();
                        }
                    }
                }, 2000);
    }

    private void getDeviceInfo(){
        AccountServiceApi accountServiceApi = AccountRetrofit.getInstance().create(AccountServiceApi.class);
        String user_id = SharePreferenceUtil.getUserId(getContext());
        String deviceId = SharePreferenceUtil.getDeviceId(getContext());
        String accessToken = IoTSDKManager.getInstance().getAccessToken().getAccessToken();
        String request = "{\"user_id\":\""+ user_id+"\",\"device_uuid\":\""+ deviceId+"\"}";
        Glog.i(TAG,"request: " + request);
        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"),request);
        Map<String,String> map = new HashMap<>();
        map.put("Content-Type","application/json");
        map.put("Authorization","Bearer " + accessToken);
        map.put("Lc",ManticApplication.channelId);
        Call<ResponseBody> call = accountServiceApi.deviceGetInfo(map,body);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response!=null && response.body() != null){
                    try {
                        JSONObject mainObject = new JSONObject(response.body().string());
                        JSONObject dataObject = mainObject.getJSONObject("data");
                        Glog.i(TAG,"dataObject: " + dataObject.toString());
                        Boolean is_advert = dataObject.getBoolean("is_advert");
                        Boolean is_jd_skill = dataObject.getBoolean("is_jd_skill");
                        String device_lc = dataObject.getString("device_lc");
                        String user_id = dataObject.getString("user_id");
                        Boolean jd_skill_smart_home_switch = dataObject.getBoolean("jd_skill_smart_home_switch");
                        String device_uuid = dataObject.getString("device_uuid");
                        Boolean jd_skill_shopping_switch = dataObject.getBoolean("jd_skill_shopping_switch");
                        Glog.i(TAG,"is_advert: " + is_advert);
                        Glog.i(TAG,"device_lc: " + device_lc);
                        Glog.i(TAG,"user_id: " + user_id);
                        Glog.i(TAG,"jd_skill_smart_home_switch: " + jd_skill_smart_home_switch);
                        Glog.i(TAG,"device_uuid: " + device_uuid);
                        Glog.i(TAG,"jd_skill_shopping_switch: " + jd_skill_shopping_switch);
                        SharePreferenceUtil.setAdvertSwitch(getContext(),is_advert);
                        SharePreferenceUtil.setSmartHomeOpen(getContext(),jd_skill_smart_home_switch);
                        SharePreferenceUtil.setVoiceShoppingOpen(getContext(),jd_skill_shopping_switch);
                        SharePreferenceUtil.setJdSkillSwitch(getContext(),is_jd_skill);
                        netWorkAndBindSucess();
                    } catch (JSONException | IOException e) {
                        netWorkAndBindSucess();
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                netWorkAndBindSucess();
            }
        });
    }

    class  BaiduBindRequest implements BindRequestListener{
        private static final String TAG = "BaiduBindRequest";
        private Activity mContext;

        public BaiduBindRequest(Activity context) {
            mContext = context;

        }
        @Override
        public void onSuccess(String id ) {
            SharePreferenceUtil.setBind(mContext);
            NetworkConfigActivity.net_success = false;
            //设置界面状态
            if (wifiConnectText != null && btn_cancel!=null && roundProgressBar != null){
                wifiConnectText.setText(mContext.getText(R.string.wifi_connect_sucess));
                btn_cancel.setVisibility(View.GONE);
                roundProgressBar.setTickPath(true);
            }
            //与Coomaan绑定,做记录
            coomaanBindDevice(mContext);
            //启动命名Activity
//            netWorkAndBindSucess();
//        Toast.makeText(mContext, mContext.getString(R.string.bind_succ), Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onFailed(final String uuid, HttpStatus code) {
            Glog.i(TAG, "bind failed code=" + code);
            if (code.getResponseCode() == 1210) {
                DeviceManager.getInstance().deviceApi.getUserAllDevices(new IoTRequestListener<List<DeviceInfo>>() {
                    @Override
                    public void onSuccess(HttpStatus code, List<com.baidu.iot.sdk.model.DeviceInfo> obj, PageInfo info) {
                        for (int i = 0; i < obj.size(); i++) {
                            if (obj.get(i).getDeviceUuid().equals(uuid)) {
                                Toast.makeText(mContext, mContext.getString(R.string.device_bind_owner), Toast.LENGTH_SHORT).show();
                                return;
                            }
                        }
                        Toast.makeText(mContext, mContext.getString(R.string.device_bind_other), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailed(HttpStatus code) {
                        Glog.i(TAG, "get all devices failed code=" + code);
                        Toast.makeText(mContext, mContext.getString(R.string.bind_fail), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onError(IoTException error) {
                        Glog.i(TAG, "get all devices error=" + error);
                        Toast.makeText(mContext, mContext.getString(R.string.bind_fail), Toast.LENGTH_SHORT).show();
                    }
                }, null);
            } else if (code.getResponseCode() == 1223) {
                Toast.makeText(mContext, mContext.getString(R.string.app_not_bind_project), Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(mContext, mContext.getString(R.string.bind_fail), Toast.LENGTH_SHORT).show();
                Glog.i(TAG, "onFailed: " + mContext.getString(R.string.bind_fail));
            }

            if (bindCount<3){
                handler.postDelayed(bindRunnable,2000);
                bindCount++;
            }else {
                WifiConnectFailUIFrament wifiConnectFailUIFrament = new WifiConnectFailUIFrament();
                Bundle bundle = new Bundle();
                bundle.putBoolean("param1",is5gWifi);
                bundle.putString("param2","baidu-bind-failed");
                wifiConnectFailUIFrament.setArguments(bundle);
                if (getActivity() instanceof FragmentEntrust) {
                    ((FragmentEntrust) getActivity()).pushFragment(wifiConnectFailUIFrament, "f3");
                }
            }



        }

        @Override
        public void onError() {
            Toast.makeText(mContext, mContext.getString(R.string.bind_fail), Toast.LENGTH_SHORT).show();
            Glog.i(TAG, "onError: " + mContext.getString(R.string.bind_fail));
            WifiConnectFailUIFrament wifiConnectFailUIFrament = new WifiConnectFailUIFrament();
            Bundle bundle = new Bundle();
            bundle.putBoolean("param1",is5gWifi);
            bundle.putString("param2","baidu-bind-error");
            wifiConnectFailUIFrament.setArguments(bundle);
            if (getActivity() instanceof FragmentEntrust) {
                ((FragmentEntrust) getActivity()).pushFragment(wifiConnectFailUIFrament, "f3");
            }
        }


    }
    class ConnectionBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            NetworkInfo networkInfo = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
            if (networkInfo.getState() == NetworkInfo.State.CONNECTED) {
                WifiInfo wifiInfo = intent.getParcelableExtra(WifiManager.EXTRA_WIFI_INFO);
                String ssid = wifiInfo.getSSID();
                Log.e(TAG, "onReceive: successful ssid:" + ssid + " originalSSID:" + currentSSID);
                if (reconnectWifi && ssid != null && ssid.contains(currentSSID)) {
                    Log.e(TAG, "onReceive: 重连成功" );
                    reconnectHandle.sendEmptyMessage(RECONNECT);

                }
            }
        }
    }
}
