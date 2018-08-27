package com.mantic.control.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.baidu.iot.sdk.IoTSDKManager;
import com.google.gson.Gson;
import com.mantic.control.ManticApplication;
import com.mantic.control.R;
import com.mantic.control.api.account.AccountRetrofit;
import com.mantic.control.api.account.AccountServiceApi;
import com.mantic.control.fragment.BluetoothListFragment;
import com.mantic.control.listener.Device;
import com.mantic.control.utils.Glog;
import com.mantic.control.utils.MD5Util;
import com.mantic.control.utils.SharePreferenceUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * Created by wujiangxia on 2017/4/1.
 */
public class EditManticNameActivity extends AppCompatActivity implements View.OnClickListener {
    private TextView ivNext,tvTitle;
    private EditText mEditManticName;
    private String TAG = "EditManticNameFrament";
    

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_mantic_name);
        mEditManticName=(EditText)findViewById(R.id.edit_mantic_name);
        String defalutName = SharePreferenceUtil.getDeviceId(this);
        mEditManticName.setText(defalutName);
        tvTitle = (TextView) findViewById(R.id.toolbar_title);
        tvTitle.setText(R.string.speaker_name);
        findViewById(R.id.toolbar_back).setVisibility(View.GONE);

        mEditManticName.setSelection(mEditManticName.getText().length());
        mEditManticName.addTextChangedListener(mTextWatcher);
        ivNext = (TextView) findViewById(R.id.toolbar_next);
        ivNext.setVisibility(View.VISIBLE);
        ivNext.setOnClickListener(this);
        if(TextUtils.isEmpty(mEditManticName.getText())){
            ivNext.setEnabled(false);
        }

        SharePreferenceUtil.setNetwork(this); //配网成功

        NetworkConfigActivity.btList.clear();
        NetworkConfigActivity.connected = false;
        BluetoothListFragment.connecting = false;

        coomaanBindDevice();
    }


    @Override
    public void onClick(View view) {
        SharePreferenceUtil.setDeviceName(this,mEditManticName.getText().toString());
        if (((ManticApplication) getApplicationContext()).isAreadyAddInterestData()) {
            Intent intent1 = new Intent(this, MainActivity.class);
            startActivity(intent1);
            finish();
        } else {
            Intent intent1 = new Intent(this, SelectInterestActivity.class);
            startActivity(intent1);
            finish();
        }
    }


    private TextWatcher mTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            enableSubmitIfReady();
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    private void enableSubmitIfReady() {

        if (!TextUtils.isEmpty(mEditManticName.getText())&&!mEditManticName.getText().toString().contains(" ")) {
            ivNext.setEnabled(true);
        } else {
            ivNext.setEnabled(false);
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
    }


    /**
     * 酷曼绑定接口
     * return:{
     * "router_id": "router_id",
     * "user_id": "b5231c42c5f7431caaa5076883634d92"
     * }
     */
    private void coomaanBindDevice(){
        String accessToken = IoTSDKManager.getInstance().getAccessToken().getAccessToken();
        Glog.i(TAG,"accessToken: " + accessToken);
        String user_id = SharePreferenceUtil.getUserId(getApplicationContext());
        String uuid = SharePreferenceUtil.getDeviceId(getApplicationContext());
        String token = SharePreferenceUtil.getDeviceToken(getApplicationContext());

        AccountServiceApi accountServiceApi = AccountRetrofit.getInstance().create(AccountServiceApi.class);
        Map<String,String> map = new HashMap<String, String>();
        map.put("Content-Type","application/json");
        map.put("Authorization","Bearer " + accessToken);
        map.put("Lc",ManticApplication.channelId);

        String request = "{\"user_id\":\""+ user_id+"\",\"device_uuid\":\""+ uuid+"\",\"device_token\":\""+ token+"\"}";
        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"),request);

        Call<ResponseBody> call = accountServiceApi.deviceBind(map,body);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful() && null == response.errorBody()){
                    Glog.i(TAG,"coomaanBindDevice sucess: ");
                    JSONObject mainObject = null; //json 主体
                    try {
                        mainObject = new JSONObject(response.body().string());
                        JSONObject dataObject = mainObject.getJSONObject("data");
                        String  data  = new Gson().toJson(dataObject);
                        Glog.i(TAG,"coomaanBindDevice: " + data);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
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

}
