package com.mantic.control.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.google.gson.Gson;
import com.jaeger.library.StatusBarUtil;
import com.mantic.antservice.util.ToastUtils;
import com.mantic.control.R;
import com.mantic.control.data.jd.JDClass;
import com.mantic.control.data.jd.JdAddress;
import com.mantic.control.data.jd.JdDefConfResponseData;
import com.mantic.control.data.jd.Md5Util;
import com.mantic.control.data.jd.NetUtil;
import com.mantic.control.data.jd.SpUtils;
import com.mantic.control.utils.Glog;
import com.mantic.control.utils.SharePreferenceUtil;
import com.mantic.control.utils.StatusBarHelper;
import com.mantic.control.widget.TitleBar;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.yokeyword.swipebackfragment.SwipeBackActivity;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * author: jayson
 * blog: http://blog.csdn.net/jia4525036
 * time: 2018/6/1.
 * desc:
 */
public class JdShoppingInfoActivity extends SwipeBackActivity implements TitleBar.OnButtonClickListener {
    private static final String TAG = JdShoppingInfoActivity.class.getSimpleName();
    public static final int REQUEST_ADDRESS = 0;
    public static final int RESULT_ADDRESS = 1;
    private static final int GET_CONF_SUCCESS = 100;
    private static final int GET_CONF_FAIL = 101;
    private static final int SET_SHOPPING_SUCCESS = 102;
    private static final int SET_SHOPPING_FAIL = 103;
    private String accessToken = null;
    private JdDefConfResponseData confResponseData = null;
    private long addressId = 0;
    private int enableShopping = -1;
    private String uuid;

    @BindView(R.id.select_address)
    LinearLayout selectView;
    @BindView(R.id.jd_current_address)
    TextView addressText;
    @BindView(R.id.jd_shopping_info_titlebar)
    TitleBar titleBar;
    @BindView(R.id.shopping_switch)
    Switch shoppingSwitch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jd_shopping_info);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            StatusBarUtil.setColor(this, Color.parseColor("#f9f9fa"), 0);
            StatusBarHelper.statusBarLightMode(this);
        }
        ButterKnife.bind(this);
        uuid = SharePreferenceUtil.getDeviceId(this);
        titleBar.setOnButtonClickListener(this);
//        shoppingSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                if (isChecked){
//                    enableShopping = 1;
//                }else {
//                    enableShopping = 2;
//                }
////                postDefaultShoppingConf();
//            }
//        });

        shoppingSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (enableShopping == 1){
                    enableShopping = 2;
                }else {
                    enableShopping = 1;
                }
                postDefaultShoppingConf();
            }
        });
        initAccessToken();
        getDefaultShoppingConf();
    }

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler(){
        @SuppressLint("SetTextI18n")
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case GET_CONF_SUCCESS:
                    if (confResponseData != null){
                        enableShopping = confResponseData.getConfigInfo().getSwitch_btn();
                        if (enableShopping == 1){//开启
                            shoppingSwitch.setChecked(true);
                        }else {//关闭
                            shoppingSwitch.setChecked(false);
                        }
                        if (confResponseData.getAddress() != null){//有默认地址
                            JdAddress.Address address = confResponseData.getAddress();
                            addressId = address.getId();
                            addressText.setText(address.getFull_address() + address.getAddress_detail());
                        }
                    }
                    break;
                case GET_CONF_FAIL:
                case SET_SHOPPING_SUCCESS:
                    if (enableShopping == 1){//开启
                        shoppingSwitch.setChecked(true);
                    }else {//关闭
                        shoppingSwitch.setChecked(false);
                    }
                    break;
                case SET_SHOPPING_FAIL:
                    break;
            }
        }
    };



    @OnClick(R.id.select_address)
    public void startAddress(){
        Intent intent = new Intent(this,JdAddressActivity.class);
        intent.putExtra("addressId",addressId);
        startActivityForResult(intent,REQUEST_ADDRESS);
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_ADDRESS){
            if (resultCode == RESULT_ADDRESS){
                if (data.getSerializableExtra("address") != null){
                    Glog.i(TAG,"有选择地址..." + data.getSerializableExtra("address"));
                    JdAddress.Address address = (JdAddress.Address) data.getSerializableExtra("address");
                    if (addressId != address.getId()){
                        addressId = address.getId();
                        addressText.setText(address.getFull_address() + address.getAddress_detail());
                        postDefaultShoppingConf();
                    }
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void initAccessToken() {
        String name = Md5Util.md5(JDClass.appKey + "");
        accessToken = SpUtils.getFromLocal(this, name, "access_token", "");
    }

    private void getDefaultShoppingConf(){
        String url = JDClass.AddressUrl + "sign=" + getConfSign() +
                "&timestamp=" + NetUtil.getCurrentDateTime() +"&v=2.0&app_key="+JDClass.appKey +
                "&access_token=" + accessToken +"&method=jd.smart.open.alpha.config.getshoppingconf" + "&360buy_param_json={\"device_id\":\"" +uuid + "\",\"client_ip\":\"172.34.56.78\"}";
        NetUtil.post(url, " ", new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                mHandler.sendEmptyMessage(GET_CONF_FAIL);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String body = response.body().string();
                Glog.i(TAG, "onResponse body: "+body );
                JSONObject json;
                try {
                    json = new JSONObject(body);
                    JSONObject resultObject = json.getJSONObject("jd_smart_open_alpha_config_getshoppingconf_response");
                    JSONObject dataOjbect = resultObject.getJSONObject("result");
                    confResponseData =   new Gson().fromJson(dataOjbect.getJSONObject("data").toString(), JdDefConfResponseData.class);
//                    Glog.i(TAG,"confResponseData: " + confResponseData.toString());
                    mHandler.sendEmptyMessage(GET_CONF_SUCCESS);
                } catch (JSONException e) {
                    mHandler.sendEmptyMessage(GET_CONF_FAIL);
                    e.printStackTrace();
                }
            }
        });
    }

    private void postDefaultShoppingConf(){
        Glog.i(TAG,"addressId: " + addressId + "---------enableShopping: " + enableShopping);
        String url = JDClass.AddressUrl + "sign=" + getSaveSign() +
                "&timestamp=" + NetUtil.getCurrentDateTime() +"&v=2.0&app_key="+JDClass.appKey +
                "&access_token=" + accessToken +"&method=jd.smart.open.alpha.config.saveshoppingconf" + "&360buy_param_json=" +
                "{\"device_id\":\"" + uuid + "\",\"client_ip\":\"172.34.56.78\",\"address_id\":"+addressId+",\"switch_btn\":"+enableShopping+"}";
        Glog.i(TAG,"地址请求url：" + url);
        NetUtil.post(url, " ", new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                ToastUtils.showShortSafe("修改成功");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String body = response.body().string();
                Glog.i(TAG, "onResponse body: "+body );
                JSONObject json;
                try {
                    json = new JSONObject(body);
                    JSONObject mainObject = json.getJSONObject("jd_smart_open_alpha_config_saveshoppingconf_response");
                    JSONObject resultObject = mainObject.getJSONObject("result");
                    if (resultObject.getString("err_code").equals("0")){
                        ToastUtils.showShortSafe("设置成功");
                        mHandler.sendEmptyMessage(SET_SHOPPING_SUCCESS);
                    }else {
                        mHandler.sendEmptyMessage(SET_SHOPPING_FAIL);
                        ToastUtils.showShortSafe("设置失败");
                    }
                } catch (JSONException e) {
                    mHandler.sendEmptyMessage(SET_SHOPPING_FAIL);
                    ToastUtils.showShortSafe("设置失败");
                    e.printStackTrace();
                }
            }
        });
    }

    private String getConfSign(){
        String tempSign = JDClass.appSecret + JDClass.signContentdefConf(uuid)
                +"access_token"+ accessToken + "app_keyHUR698I6I5GK5WRYP3SRBRIJ8CS6UWYKmethodjd.smart.open.alpha.config.getshoppingconftimestamp"+
                NetUtil.getCurrentDateTime()+"v2.0" + JDClass.appSecret;
        Glog.i(TAG,"getConfSign -> 加密前子串：" + tempSign);
        return Md5Util.md5Up(tempSign);
    }

    private String getSaveSign(){
        String tempSign = JDClass.appSecret + "360buy_param_json" +
                "{\"device_id\":\"" + uuid + "\",\"client_ip\":\"172.34.56.78\",\"address_id\":"+addressId+",\"switch_btn\":"+enableShopping+"}"
                +"access_token"+ accessToken + "app_keyHUR698I6I5GK5WRYP3SRBRIJ8CS6UWYKmethodjd.smart.open.alpha.config.saveshoppingconftimestamp"+
                NetUtil.getCurrentDateTime()+"v2.0" + JDClass.appSecret;
        Glog.i(TAG,"getSaveSign -> 加密前子串：" + tempSign);
        return Md5Util.md5Up(tempSign);
    }



    @Override
    public void onLeftClick() {
        finish();
    }

    @Override
    public void onRightClick() {

    }
}
