package com.mantic.control.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.iot.sdk.IoTSDKManager;
import com.baidu.iot.sdk.iam.AccessToken;
import com.google.gson.Gson;
import com.ingenic.music.agent.IngenicAbstractEasyLinkAgent;
import com.ingenic.music.agent.IngenicBroadcomEasyLinkAgent;
import com.mantic.antservice.DeviceManager;
import com.mantic.antservice.util.ToastUtils;
import com.mantic.control.ManticApplication;
import com.mantic.control.R;
import com.mantic.control.activity.LoadingActivity;
import com.mantic.control.activity.MainActivity;
import com.mantic.control.activity.NetworkConfigActivity;
import com.mantic.control.api.account.AccountRetrofit;
import com.mantic.control.api.account.AccountServiceApi;
import com.mantic.control.api.account.AccountUrl;
import com.mantic.control.entiy.WifiInformation;
import com.mantic.control.utils.Glog;
import com.mantic.control.utils.IoTAppConfigMgr;
import com.mantic.control.utils.MD5Util;
import com.mantic.control.utils.NetworkUtils;
import com.mantic.control.utils.SharePreferenceUtil;
import com.mantic.control.utils.Util;
import com.mantic.control.websocket.WebSocketController;
import com.mantic.control.widget.CustomDialog;
import com.mantic.control.widget.CustomPopWindow;
import com.rdaressif.iot.rdatouch.demo_activity.RdaWifiAdminSimple;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.microedition.khronos.opengles.GL;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * author: wujiangxia
 * blog: http://blog.csdn.net/jia4525036
 * time: 2017/4/1.
 * desc:
 */
public class WifiConnectUIFrament extends Fragment implements View.OnClickListener {
    private TextView tvConnetWifi, tvTitle,tvNext;
    private View view;
    private ImageView ivBack;
    private EditText tvWifiName;
    private EditText mEditWifiPwd;
    private String TAG = "WifiConnectUIFrament";
//    protected ISmartLinker mSnifferSmartLinker;
    private BroadcastReceiver mWifiChangedReceiver;
    private boolean isNetConn;
    InputMethodManager imm;
    ImageView selectWifi;
    ArrayList<WifiInformation> wifiArray;

    private OnFragmentInteractionListener mListener;

    //x1000 wifi connect
    public static IngenicAbstractEasyLinkAgent mEasyAgent;

    //rda new
    public static boolean rdaSmartConfig = true;

    private boolean btNetwork = true;

    private CustomPopWindow mCustomPopWindow;

    private String[] listWifiSsid;

    private WifiManager wifiManager;

    private String finalSsid;

    private boolean wifiIs5g = false;

    //x1000 wifi connect
    private TextWatcher mTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {


        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
//            enableSubmitIfReady();
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    private TextWatcher mTextWifiNameWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {


        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            Glog.i("jys","s-========: " + s);
            Glog.i("jys","getssid " + getSSid());
            if (s.toString().length()==0){
                selectWifi.setVisibility(View.GONE);
                tvConnetWifi.setEnabled(false);
            }else {
                selectWifi.setVisibility(View.VISIBLE);
                tvConnetWifi.setEnabled(true);
            }
            if (!getSSid().contains("unknown ssid")){
                if (s.toString().equals(getSSid())){
                    selectWifi.setVisibility(View.GONE);
                }else {
                    selectWifi.setVisibility(View.VISIBLE);
                }
            }

        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Glog.v("wujx", "WifiConnectUIFrament initView");
        if (view == null) {
            view = inflater.inflate(R.layout.wificonnect_ui_frag,container,false);
            initLayout();
        }
        wifiManager = (WifiManager) getActivity().getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        assert wifiManager != null;
        List<ScanResult> listWifi = wifiManager.getScanResults();
        //数组初始化要注意
        Glog.i(TAG,"listWifi:" + listWifi.size());
        listWifiSsid=new String[listWifi.size()];
        for(int i = 0; i< listWifi.size(); i++){
            ScanResult scanResult = listWifi.get(i);
            listWifiSsid[i]=scanResult.SSID;
            Glog.i(TAG,"scanResult： " + scanResult.SSID);
        }
        return view;
    }

    @Override
    public void onResume() {
        Glog.i(TAG,"WifiConnectUIFragment -> onResume");
        for (int i=0;i<wifiArray.size();i++){
            WifiInformation wifiInfo = wifiArray.get(i);
            Glog.i(TAG,"wifiInfo: " + wifiInfo.toString());
            if (wifiInfo.getWifiName().equals(getSSid())){
                mEditWifiPwd.setText(wifiInfo.getWifiPwd());
                mEditWifiPwd.setSelection(wifiInfo.getWifiPwd().length());
                break;
            }else {
                mEditWifiPwd.setText("");
            }
        }
        if (!NetworkUtils.isWifiConnected(getActivity())){
            showConfigDialog();
        }
        wifi_5g_has_same_24 = false;
        checkAccessToken();
        super.onResume();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }


    private void initLayout() {

        tvWifiName = (EditText) view.findViewById(R.id.wifi_name);
        mEditWifiPwd = (EditText) view.findViewById(R.id.wifi_pwd);
        tvConnetWifi = (TextView) view.findViewById(R.id.btn_connet_wifi);
        tvTitle = (TextView) view.findViewById(R.id.toolbar_title);
        tvTitle.setText(R.string.config_network);
        ivBack = (ImageView) view.findViewById(R.id.toolbar_back);
        ivBack.setVisibility(View.VISIBLE);
        ivBack.setOnClickListener(this);
        tvNext = (TextView)view.findViewById(R.id.toolbar_next);
        tvNext.setText(R.string.skip);
        if (Util.isApkDebugable(getContext())){
            tvNext.setVisibility(View.VISIBLE);
        }else {
            tvNext.setVisibility(View.GONE);
        }
        tvNext.setOnClickListener(this);

        tvConnetWifi.setOnClickListener(this);

        selectWifi = (ImageView) view.findViewById(R.id.select_wifi);
        selectWifi.setOnClickListener(this);
        selectWifi.setVisibility(View.GONE);

        view.findViewById(R.id.change_wifi).setOnClickListener(this);

        mEditWifiPwd.requestFocus();
        imm = (InputMethodManager) mEditWifiPwd.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(0, InputMethodManager.SHOW_FORCED);
//        mSnifferSmartLinker = MulticastSmartLinker.getInstance();
        //x1000 wifi connect
        mEasyAgent = new IngenicBroadcomEasyLinkAgent(getActivity().getApplicationContext(), new WifiConnectProgressUIFragment());
        //x1000 wifi connect

        if (getSSid().equals("")){
            tvConnetWifi.setEnabled(false);
        }else {
            tvConnetWifi.setEnabled(true);
        }
        mEditWifiPwd.addTextChangedListener(mTextWatcher);
        tvWifiName.addTextChangedListener(mTextWifiNameWatcher);

        mWifiChangedReceiver = new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent) {
                ConnectivityManager connectivityManager = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo networkInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
                if (networkInfo != null && networkInfo.isConnected()) {
                    tvWifiName.setText(getSSid());
                    isNetConn = true;
                } else {
                    tvWifiName.setText("");
                    isNetConn = false;
                }
//                enableSubmitIfReady();
            }
        };
//        tvConnetWifi.setEnabled(false);
        getActivity().registerReceiver(mWifiChangedReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));

        //检查是否支持ble
//        if (!getActivity().getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE))
//        {
//            Toast.makeText(getActivity(), R.string.ble_not_supported, Toast.LENGTH_SHORT).show();
//        }

        //初始化wifi密码 如果有保存 就不用输入了
        wifiArray = SharePreferenceUtil.loadWifiArray(getActivity());

    }

    private void enableSubmitIfReady() {

        if (isNetConn) {
            tvConnetWifi.setEnabled(true);
        } else {
            tvConnetWifi.setEnabled(true);
        }

    }


    private void showSelectWifiMenu(){
        View contentView = LayoutInflater.from(getActivity()).inflate(R.layout.pop_menu_select_wifi,null);
        //处理popWindow 显示内容
        handleLogic(contentView);

        //创建并显示popWindow
        mCustomPopWindow= new CustomPopWindow.PopupWindowBuilder(getActivity())
                .setView(contentView)
                .size(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT)//显示大小
                .create()
                .showAsDropDown(tvWifiName,0,5);

        mCustomPopWindow.getPopupWindow().setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                selectWifi.setImageResource(R.drawable.wifi_select_down);
            }
        });
        selectWifi.setImageResource(R.drawable.wifi_select_up);

    }

    /**
     * 处理弹出显示内容、点击事件等逻辑
     * @param contentView
     */
    private void handleLogic(View contentView){
        tvWifiName.setEnabled(false);

        LinearLayout itemOne = (LinearLayout) contentView.findViewById(R.id.menu1);
        LinearLayout itemTwo = (LinearLayout) contentView.findViewById(R.id.menu2);
        LinearLayout itemThree = (LinearLayout) contentView.findViewById(R.id.menu3);
        LinearLayout itemFour = (LinearLayout) contentView.findViewById(R.id.menu4);
        LinearLayout itemFive = (LinearLayout) contentView.findViewById(R.id.menu5);
        final TextView textOne = (TextView) contentView.findViewById(R.id.wifi_text_one);
        final TextView textTwo = (TextView) contentView.findViewById(R.id.wifi_text_two);
        final TextView textThree = (TextView) contentView.findViewById(R.id.wifi_text_three);
        final TextView textFour = (TextView) contentView.findViewById(R.id.wifi_text_four);
        switch (wifiArray.size()){
            case 0: //说明当前没用保存任何wifi
                textOne.setText(getSSid());
                itemTwo.setVisibility(View.GONE);
                itemThree.setVisibility(View.GONE);
                itemFour.setVisibility(View.GONE);
                break;
            case 1:
                WifiInformation wifiInfo = wifiArray.get(0);
                if (wifiInfo.getWifiName().equals(getSSid())){ //保存就是当前的网络
                    textOne.setText(getSSid());
                    itemTwo.setVisibility(View.GONE);
                    itemThree.setVisibility(View.GONE);
                    itemFour.setVisibility(View.GONE);
                }else {
                    textOne.setText(getSSid());
                    textTwo.setText(wifiInfo.getWifiName());
                    itemThree.setVisibility(View.GONE);
                    itemFour.setVisibility(View.GONE);
                }
                break;
            case 2:
                WifiInformation wifiInfo1 = wifiArray.get(0);
                WifiInformation wifiInfo2 = wifiArray.get(1);
                if (wifiInfo1.getWifiName().equals(getSSid())){
                    textOne.setText(wifiInfo1.getWifiName());
                    textTwo.setText(wifiInfo2.getWifiName());
                    itemThree.setVisibility(View.GONE);
                    itemFour.setVisibility(View.GONE);
                }else if (wifiInfo2.getWifiName().equals(getSSid())){
                    textOne.setText(wifiInfo2.getWifiName());
                    textTwo.setText(wifiInfo1.getWifiName());
                    itemThree.setVisibility(View.GONE);
                    itemFour.setVisibility(View.GONE);
                }else {
                    textOne.setText(getSSid());
                    textTwo.setText(wifiInfo1.getWifiName());
                    textThree.setText(wifiInfo2.getWifiName());
                    itemFour.setVisibility(View.GONE);
                }
                break;
            case 3:
                WifiInformation wifiInfo11 = wifiArray.get(0);
                WifiInformation wifiInfo21 = wifiArray.get(1);
                WifiInformation wifiInfo31 = wifiArray.get(2);
                if (wifiInfo11.getWifiName().equals(getSSid()))//当前网络在保存数据中
                {
                    textOne.setText(wifiInfo11.getWifiName());
                    textTwo.setText(wifiInfo21.getWifiName());
                    textThree.setText(wifiInfo31.getWifiName());
                    itemFour.setVisibility(View.GONE);
                }else if (wifiInfo21.getWifiName().equals(getSSid())){
                    textOne.setText(wifiInfo21.getWifiName());
                    textTwo.setText(wifiInfo11.getWifiName());
                    textThree.setText(wifiInfo31.getWifiName());
                    itemFour.setVisibility(View.GONE);
                }else if(wifiInfo31.getWifiName().equals(getSSid())){
                    textOne.setText(wifiInfo31.getWifiName());
                    textTwo.setText(wifiInfo11.getWifiName());
                    textThree.setText(wifiInfo21.getWifiName());
                    itemFour.setVisibility(View.GONE);
                }else {
                    textOne.setText(getSSid());
                    textTwo.setText(wifiInfo11.getWifiName());
                    textThree.setText(wifiInfo21.getWifiName());
                    textFour.setText(wifiInfo31.getWifiName());
                }
                break;
        }

        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mCustomPopWindow!=null){
                    mCustomPopWindow.dissmiss();
                    selectWifi.setImageResource(R.drawable.wifi_select_down);
                }
                LinearLayout linearLayout = (LinearLayout)v;
                String showName = "";
                switch (v.getId()){
                    case R.id.menu1: //index 0
                        tvWifiName.setText(textOne.getText());
                        TextView view1 = (TextView) linearLayout.getChildAt(0);
                        if (wifiArray.size() == 0){
                            mEditWifiPwd.setText("");
                            mEditWifiPwd.requestFocus();
                        }else {
                            for (int i=0;i<wifiArray.size();i++){
                                WifiInformation wifiInfo = wifiArray.get(i);
                                if(wifiInfo.getWifiName().equals(view1.getText())){
                                    mEditWifiPwd.setText(wifiInfo.getWifiPwd());
                                    mEditWifiPwd.setSelection(wifiInfo.getWifiPwd().length());
                                    mEditWifiPwd.requestFocus();
                                    break;
                                }else {
                                    mEditWifiPwd.setText("");
                                    mEditWifiPwd.requestFocus();
                                }
                            }
                        }
                        break;
                    case R.id.menu2:
                        tvWifiName.setText(textTwo.getText());
                        TextView view2 = (TextView) linearLayout.getChildAt(0);
                        if (wifiArray.size() == 0){
                            mEditWifiPwd.setText("");
                            mEditWifiPwd.requestFocus();
                        }else {
                            for (int i=0;i<wifiArray.size();i++){
                                WifiInformation wifiInfo = wifiArray.get(i);
                                if(wifiInfo.getWifiName().equals(view2.getText())){
                                    mEditWifiPwd.setText(wifiInfo.getWifiPwd());
                                    mEditWifiPwd.setSelection(wifiInfo.getWifiPwd().length());
                                    mEditWifiPwd.requestFocus();
                                    break;
                                }else {
                                    mEditWifiPwd.setText("");
                                    mEditWifiPwd.requestFocus();
                                }
                            }
                        }
                        break;
                    case R.id.menu3:
                        tvWifiName.setText(textThree.getText());
                        TextView view3 = (TextView) linearLayout.getChildAt(0);
                        if (wifiArray.size() == 0){
                            mEditWifiPwd.setText("");
                            mEditWifiPwd.requestFocus();
                        }else {
                            for (int i=0;i<wifiArray.size();i++){
                                WifiInformation wifiInfo = wifiArray.get(i);
                                if(wifiInfo.getWifiName().equals(view3.getText())){
                                    mEditWifiPwd.setText(wifiInfo.getWifiPwd());
                                    mEditWifiPwd.setSelection(wifiInfo.getWifiPwd().length());
                                    mEditWifiPwd.requestFocus();
                                    break;
                                }else {
                                    mEditWifiPwd.setText("");
                                    mEditWifiPwd.requestFocus();
                                }
                            }
                        }
                        break;
                    case R.id.menu4:
                        tvWifiName.setText(textFour.getText());
                        TextView view4 = (TextView) linearLayout.getChildAt(0);
                        if (wifiArray.size() == 0){
                            mEditWifiPwd.setText("");
                            mEditWifiPwd.requestFocus();
                        }else {
                            for (int i=0;i<wifiArray.size();i++){
                                WifiInformation wifiInfo = wifiArray.get(i);
                                if(wifiInfo.getWifiName().equals(view4.getText())){
                                    mEditWifiPwd.setText(wifiInfo.getWifiPwd());
                                    mEditWifiPwd.setSelection(wifiInfo.getWifiPwd().length());
                                    mEditWifiPwd.requestFocus();
                                    break;
                                }else {
                                    mEditWifiPwd.setText("");
                                    mEditWifiPwd.requestFocus();
                                }
                            }
                        }
                        break;
                    case R.id.menu5:
                        mEditWifiPwd.setText("");
                        tvWifiName.setEnabled(true);
                        tvWifiName.setText("");
                        tvWifiName.requestFocus();
                        break;
                }
//                Toast.makeText(getActivity(),showContent,Toast.LENGTH_SHORT).show();
            }
        };
        itemOne.setOnClickListener(listener);
        itemTwo.setOnClickListener(listener);
        itemThree.setOnClickListener(listener);
        itemFour.setOnClickListener(listener);
        itemFive.setOnClickListener(listener);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.toolbar_back:
                if (getActivity() instanceof FragmentEntrust) {
                    ((FragmentEntrust) getActivity()).popFragment(getTag());
                }
                break;
            case R.id.btn_connet_wifi:
//                if(isWifi5G()){
//                    show5gDialog();
//                    return;
//                }
                process5gWifi();
                break;
            case R.id.toolbar_next:
            startEditName();
            break;
            case R.id.select_wifi:
                // 2.11 更改功能为还原wifi
//                showSelectWifiMenu();
                restoreWifi();
                break;
            case R.id.change_wifi:
                Intent wifiSettingsIntent = new Intent("android.settings.WIFI_SETTINGS");
                startActivity(wifiSettingsIntent);
                break;

        }
    }

    private void restoreWifi(){
        Glog.i("jys","ssid:" + getSSid());
        if (getSSid().equals("")||getSSid().contains("unknown ssid")){
            tvWifiName.setText("");
            mEditWifiPwd.setText("");
        }else {
            tvWifiName.setText(getSSid());
            tvWifiName.setSelection(tvWifiName.getText().length());
            for (int i=0;i<wifiArray.size();i++){
                WifiInformation wifiInfo = wifiArray.get(i);
                Glog.i(TAG,"wifiInfo: " + wifiInfo.toString());
                if (wifiInfo.getWifiName().equals(getSSid())){
                    mEditWifiPwd.setText(wifiInfo.getWifiPwd());
                    mEditWifiPwd.setSelection(wifiInfo.getWifiPwd().length());
                    break;
                }else {
                    mEditWifiPwd.setText("");
                }
            }
        }

    }
    /**
     * 启动配网 调用服务器接口通知~
     * return:{
     * "router_id": "router_id",
     * "user_id": "b5231c42c5f7431caaa5076883634d92"
     * }
     */
    private void deviceNetworkStart(){
        String accessToken = IoTSDKManager.getInstance().getAccessToken().getAccessToken();
        Glog.i(TAG,"post - > deviceNetworkStart  accessToken:" + accessToken + "---" + "user_id:" + NetworkConfigActivity.user_id);
        AccountServiceApi accountServiceApi = AccountRetrofit.getInstance().create(AccountServiceApi.class);
        Map<String,String> map = new HashMap<String, String>();
        map.put("Content-Type","application/json");
        map.put("Authorization","Bearer " + accessToken);
        map.put("Lc", ManticApplication.channelId);

        String request = "{\"user_id\":\""+ NetworkConfigActivity.user_id+"\",\"router_id\":\""+ MD5Util.encrypt(finalSsid+mEditWifiPwd.getText().toString().trim())+"\"}";
        Glog.i(TAG," deviceNetworkStart - > request:" + request);
        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("Content-Type, application/json; charset=utf-8"),request);

        Call<ResponseBody> call = accountServiceApi.deviceStartNetwork(map,body);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()){
                    JSONObject mainObject; //json 主体
                    try {
                        mainObject = new JSONObject(response.body().string());
                        Glog.i(TAG,"post -> deviceNetworkStart sucess: " + response.body().string());
                        JSONObject dataObject;
                        if (!AccountUrl.BASE_URL.contains("v2")){
                            dataObject = mainObject;
                        }else {// server v2 接口
                            dataObject = mainObject.getJSONObject("data");
                        }

                        String userId = dataObject.getString("user_id");
                        String rooterId = dataObject.getString("router_id");
                        if (userId != null && rooterId != null){
                            tvConnetWifi.setEnabled(true);
                            startNetworkPair();
                        }else {
                            tvConnetWifi.setEnabled(true);
                            ToastUtils.showShort("账号出错，请退出应用后重试");
                        }
                        String  data  = new Gson().toJson(mainObject);
                        Glog.i(TAG,"post -> deviceNetworkStart: " + data);

                    } catch (JSONException | IOException e) {
                        tvConnetWifi.setEnabled(true);
                        ToastUtils.showShort("账号出错，请退出应用后重试");
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Glog.i(TAG,"post -> deviceNetworkStart fail: ");
                Log.e(TAG, "onFailure: "+t.toString() );
                tvConnetWifi.setEnabled(true);
                ToastUtils.showShort("请检查网络后重试");
            }
        });
    }

    // 新配网方式 ble 和 smargconfig
    private void startNetworkPair(){
        if (DeviceManager.idRda) {
            if (btNetwork){ // 蓝牙配网启动
                ((NetworkConfigActivity)getActivity()).wifiName = finalSsid;
                ((NetworkConfigActivity)getActivity()).pwd = mEditWifiPwd.getText().toString().trim();

                if (NetworkConfigActivity.bleNetwork){
                    mListener.onFragmentInteraction(Uri.parse("start_ble"));
                }else {
                    mListener.onFragmentInteraction(Uri.parse("send_wifi"));
                }

                if (!rdaSmartConfig){
                    if (this.getActivity() instanceof FragmentEntrust) {
                        SharePreferenceUtil.setDeviceWifi(getActivity(),finalSsid);
                        ((FragmentEntrust) this.getActivity()).pushFragment(new WifiConnectProgressUIFragment(), "f3");
                    }
                }
            }
           if (rdaSmartConfig){ //Smartconfig 启动
                WifiConnectProgressUIFragment progressUIFragmentFragment = new WifiConnectProgressUIFragment();
                RdaWifiAdminSimple mWifiAdmin  = new RdaWifiAdminSimple(getActivity());
                Bundle bundle = new Bundle();
                bundle.putString("param1",finalSsid);
                bundle.putString("param2",mEditWifiPwd.getText().toString().trim());
                bundle.putString("param3",mWifiAdmin.getWifiConnectedBssid());
                bundle.putString("param4","NO");
                bundle.putString("param5","1");
                bundle.putBoolean("param6",wifiIs5g);
                progressUIFragmentFragment.setArguments(bundle);
                if (this.getActivity() instanceof FragmentEntrust) {
                    SharePreferenceUtil.setDeviceWifi(getActivity(),finalSsid);
                    ((FragmentEntrust) this.getActivity()).pushFragment(progressUIFragmentFragment, "f3");
                }
            }else {
                //设置要配置的ssid 和pswd
//                try {
//                    mSnifferSmartLinker.start(getActivity().getApplicationContext(), mEditWifiPwd.getText().toString().trim(),
//                            tvWifiName.getText().toString().trim());
//                    if (this.getActivity() instanceof FragmentEntrust) {
//                        SharePreferenceUtil.setDeviceWifi(getActivity(),tvWifiName.getText().toString().trim());
//                        ((FragmentEntrust) this.getActivity()).pushFragment(new WifiConnectProgressUIFragment(), "f3");
//                    }
//                } catch (Exception e) {
//                    // TODO Auto-generated catch block
//                    e.printStackTrace();
//                }
            }

            //x1000 wifi connect
        }else {
            WifiManager wifiManager = (WifiManager) getActivity().getApplicationContext().getSystemService(Context.WIFI_SERVICE);
            WifiInfo info = wifiManager.getConnectionInfo();
            int localIp = info.getIpAddress();
            mEasyAgent.startConfig(finalSsid, mEditWifiPwd.getText().toString().trim(), localIp);

            if (this.getActivity() instanceof FragmentEntrust) {
                SharePreferenceUtil.setDeviceWifi(getActivity(),finalSsid);
                ((FragmentEntrust) this.getActivity()).pushFragment(new WifiConnectProgressUIFragment(), "f3");
            }
        }
        //x1000 wifi connect
    }



    private void startEditName() { //点击忽略 直接绑定
        Glog.i(TAG,"isBind: " + SharePreferenceUtil.getBind(getActivity()));
        if (SharePreferenceUtil.getBind(getActivity()) == 1){ // 已经绑定的设备 配网完成后直接返回
            getActivity().finish();
        }else {
            WifiConnectProgressUIFragment wifiConnectProgressUIFrament = new WifiConnectProgressUIFragment();
            WifiConnectProgressUIFragment.BaiduBindRequest baiduBindRequest = wifiConnectProgressUIFrament.new BaiduBindRequest(getActivity());
            DeviceManager.getInstance().bindDevice(SharePreferenceUtil.getDeviceId(getActivity()),SharePreferenceUtil.getDeviceToken(getActivity()),baiduBindRequest);
//        Intent intent = new Intent(getActivity(), EditManticNameActivity.class);
//        startActivity(intent);
//        getActivity().finish();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Glog.v("wujx", "WifiConnectUIFrament onDestroyView");

        View mv = getActivity().getWindow().peekDecorView();
        if (mv != null) {
            InputMethodManager inputmanger = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            inputmanger.hideSoftInputFromWindow(mv.getWindowToken(), 0);
        }
        try {
            getActivity().unregisterReceiver(mWifiChangedReceiver);
        } catch (Exception e) {
            e.printStackTrace();
        }
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

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private boolean isWifi5G(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP ){
            assert wifiManager != null;
            WifiInfo wifiInfo = wifiManager.getConnectionInfo();
            String frequency = String.valueOf(wifiInfo.getFrequency());
            if (!frequency.isEmpty()){
                String first = frequency.substring(0,1);
                if (first.equals("5")){
                    return true;
                }else {
                    return false;
                }
            }else {
                return false;
            }
        }else {
            return false;
        }
    }

    private boolean wifi_5g_has_same_24 = false;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void process5gWifi(){
        String currSsid = tvWifiName.getText().toString().trim();
        if (isWifi5G()&&currSsid.length()>3){
            if (currSsid.contains("5G")||currSsid.contains("5g")){ //Ex: xxxxx_5G or xxxxx_5g
                Glog.i(TAG,"当前5G wifi 包含了 5g~~~~~~~~~~~~~~");
                finalSsid = currSsid.substring(0,currSsid.length()-3);
                for (String uuid:listWifiSsid){
                    if (uuid.equals(finalSsid)){
                        Glog.i(TAG,"匹配到了 --- 》 " + uuid);
                        wifi_5g_has_same_24 = true;
                        break;
                    }
                }
                if (!wifi_5g_has_same_24){
                    Glog.i(TAG,"没有完全匹配的2.4G网络~~~~~~~~~~~");
                    float similarity = 0;
                    int index = 0;
                    for (int i=0;i<listWifiSsid.length;i++){
                        String uuid = listWifiSsid[i];
                        if(!uuid.equals(currSsid)){ // 不能等于当前5Gwifi
                            float aaa = Util.getSimilarityRatio(uuid,finalSsid);
                            if (aaa > similarity){
                                index = i;
                                similarity = aaa;
                            }
                        }

                    }

                    finalSsid = listWifiSsid[index];
                    Glog.i(TAG,"没有完全匹配的2.4G网络~~~~~最相似的是：" + finalSsid);

                }
                wifiIs5g = true;
                Glog.i(TAG,"5g wifi 优化后：" + finalSsid);
                tvConnetWifi.setEnabled(false);
                deviceNetworkStart();
            }else { //Ex:xxxxx 直接发送
                Glog.i(TAG,"当前5G wifi 没有包含  5g~~~~~~~~~~~~");
                wifiIs5g = true;
                finalSsid = currSsid;
                show5gDialog();
            }

        }else {
            finalSsid = currSsid;
            tvConnetWifi.setEnabled(false);
            deviceNetworkStart();

        }
    }

    private void show5gDialog(){
        CustomDialog.Builder mBuilder = new CustomDialog.Builder(getContext());
        mBuilder.setTitle(getContext().getString(R.string.dialog_btn_prompt));
        mBuilder.setMessage(getContext().getString(R.string.network_5g_next));
        mBuilder.setPositiveButton(getContext().getString(R.string.goto_next), new CustomDialog.Builder.DialogPositiveClickListener() {
            @Override
            public void onPositiveClick(final CustomDialog dialog) {
//                Intent wifiSettingsIntent = new Intent("android.settings.WIFI_SETTINGS");
//                startActivity(wifiSettingsIntent);
                dialog.dismiss();
                tvConnetWifi.setEnabled(false);
                deviceNetworkStart();
            }
        });
        mBuilder.setNegativeButton(getContext().getString(R.string.cancel), new CustomDialog.Builder.DialogNegativeClickListener() {
            @Override
            public void onNegativeClick(CustomDialog dialog) {
                dialog.dismiss();
                tvConnetWifi.setEnabled(true);
            }
        });
        mBuilder.create().show();
    }

    private void showConfigDialog() {
        CustomDialog.Builder mBuilder = new CustomDialog.Builder(getActivity());
        mBuilder.setTitle("提示");
        mBuilder.setMessage("打开网络来允许手机连接路由器");
        mBuilder.setPositiveButton("是", new CustomDialog.Builder.DialogPositiveClickListener() {
            @Override
            public void onPositiveClick(CustomDialog dialog) {
                startWifiSet();
                dialog.dismiss();
            }
        });
        mBuilder.setNegativeButton("手动输入", new CustomDialog.Builder.DialogNegativeClickListener() {
            @Override
            public void onNegativeClick(CustomDialog dialog) {
                tvWifiName.setFocusable(true);
                tvWifiName.requestFocus();
                tvWifiName.setText("");
                tvWifiName.setFocusableInTouchMode(true);
                dialog.dismiss();
            }
        });
        mBuilder.create().show();
    }

    private void startWifiSet(){
        Intent wifiSettingsIntent = new Intent("android.settings.WIFI_SETTINGS");
        startActivity(wifiSettingsIntent);
    }

    private void checkAccessToken(){
        String accessToken = IoTSDKManager.getInstance().getAccessToken().getAccessToken();
        Map<String,String> map = new HashMap<String, String>();
        map.put("Authorization","Bearer " + accessToken);
        map.put("Lc", ManticApplication.channelId);
        AccountServiceApi accountServiceApi = AccountRetrofit.getInstance().create(AccountServiceApi.class);
        Call<ResponseBody> call = accountServiceApi.checkToken(map);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()){
                    try {
                        JSONObject mainObject = new JSONObject(response.body().string());
                        JSONObject dataObject = mainObject.getJSONObject("data");
                        Glog.i(TAG,"accessToken 剩余 ->: " + dataObject.toString());
                        if (Objects.equals(mainObject.getString("retcode"), "1") ||dataObject.getInt("count_down")<=7200){ //refreshToken 小于20小时重新登录
                            Glog.i(TAG,"需要刷新AccessToken！");
                            refreshToken();
                        }

                    }catch (JSONException | IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });
    }

    private void refreshToken(){
        Glog.i(TAG,"refreshToken.........");
        String refreshToken = IoTSDKManager.getInstance().getAccessToken().getRefreshToken(); //使用refreshToken更新AccessToken
        String header = "Bearer " + refreshToken;
        Map<String,String> map = new HashMap<String, String>();
        map.put("Authorization",header);
        map.put("Lc", ManticApplication.channelId);
        AccountServiceApi accountServiceApi = AccountRetrofit.getInstance().create(AccountServiceApi.class);
        Call<ResponseBody> call = accountServiceApi.refreshToken(map);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    Glog.i(TAG, "post -> refreshToken sucess: ");
                    JSONObject mainObject = null; //json 主体
                    try {
                        mainObject = new JSONObject(response.body().string());
                        if (!AccountUrl.BASE_URL.contains("v2")){
                            String access_token = mainObject.getString("access_token");
                            String expires_in = mainObject.getString("expires_in");
                            String refresh_token = mainObject.getString("refresh_token");
                            Glog.i(TAG, "post -> refreshToken: " + "access_token:" + access_token + "  expires_in:" + expires_in + "  refresh_token" + refresh_token);
                            if (access_token != null && expires_in != null && refresh_token != null) {
                                IoTAppConfigMgr.putString(getContext(), AccessToken.KEY_ACCESS_TOKEN, access_token);
                                IoTAppConfigMgr.putString(getContext(), AccessToken.KEY_REFRESH_TOKEN, refresh_token);
                                IoTAppConfigMgr.putString(getContext(), AccessToken.KEY_EXPIRES_IN, expires_in);
                            }
                        }else {// server v2 接口
                            JSONObject dataObject = mainObject.getJSONObject("data");
                            if (Objects.equals(mainObject.getString("retcode"), "1")){ // refreshtoken失败 需要登录
                                showLogoutDialog();
                            }else {
                                String access_token = dataObject.getString("access_token");
                                String expires_in = dataObject.getString("expires_in");
                                String refresh_token = dataObject.getString("refresh_token");
                                Glog.i(TAG, "post -> refreshToken: " + "access_token:" + access_token + "  expires_in:" + expires_in + "  refresh_token" + refresh_token);
                                if (access_token != null && expires_in != null && refresh_token != null) {
                                    IoTAppConfigMgr.putString(getContext(), AccessToken.KEY_ACCESS_TOKEN, access_token);
                                    IoTAppConfigMgr.putString(getContext(), AccessToken.KEY_REFRESH_TOKEN, refresh_token);
                                    IoTAppConfigMgr.putString(getContext(), AccessToken.KEY_EXPIRES_IN, expires_in);
                                }
                            }

                        }

                    } catch (JSONException | IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Glog.i(TAG, "post -> refreshToken fail: " + t.getMessage());
            }
        });
    }

    private void showLogoutDialog() {
        final CustomDialog.Builder mBuilder;
        mBuilder = new CustomDialog.Builder(getActivity());
        mBuilder.setTitle(this.getString(R.string.dialog_btn_prompt));
        mBuilder.setMessage(this.getString(R.string.account_is_outdated));
        mBuilder.setPositiveButton(getString(R.string.relogin), new CustomDialog.Builder.DialogPositiveClickListener() {
            @Override
            public void onPositiveClick(final CustomDialog dialog) {
                SharePreferenceUtil.clearSettingsData(getContext());
                SharePreferenceUtil.clearUserData(getContext());
                IoTSDKManager.getInstance().logout();
                dialog.dismiss();
                restartLoading();
            }
        });
        mBuilder.setNegativeButton(getString(R.string.cancel), new CustomDialog.Builder.DialogNegativeClickListener() {
            @Override
            public void onNegativeClick(CustomDialog dialog) {
                dialog.dismiss();
            }
        });
        mBuilder.create().show();
    }

    private void restartLoading() {
        Intent intent = new Intent(getContext(), LoadingActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        if (WebSocketController.getInstance().mConnection.isConnected()){
            WebSocketController.getInstance().mConnection.disconnect();
        }
        startActivity(intent);
    }

    private void requestBaidu(String url,okhttp3.Callback callback){
        OkHttpClient client=new OkHttpClient();
        Request request=new Request.Builder()
                .url(url)
                .build();
        client.newCall(request).enqueue(callback);
    }
}
