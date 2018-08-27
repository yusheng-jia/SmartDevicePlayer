package com.mantic.control.activity;

import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.jaeger.library.StatusBarUtil;
import com.mantic.antservice.util.ToastUtils;
import com.mantic.control.R;
import com.mantic.control.api.mopidy.MopidyRetrofit;
import com.mantic.control.api.mopidy.MopidyServiceApi;
import com.mantic.control.api.mopidy.MopidyTools;
import com.mantic.control.data.DataFactory;
import com.mantic.control.utils.Glog;
import com.mantic.control.utils.SharePreferenceUtil;
import com.mantic.control.utils.StatusBarHelper;
import com.mantic.control.widget.TitleBar;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import me.yokeyword.swipebackfragment.SwipeBackActivity;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by jayson on 2017/6/21.
 */

public class DeviceRenameActivity extends SwipeBackActivity implements TitleBar.OnButtonClickListener{
    private static final String TAG = "DeviceRenameActivity";
    private Context mContext;
    private EditText nameText;
    private TitleBar device_rename_titlebar;
    private DataFactory mDataFactory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rename);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            StatusBarUtil.setColor(DeviceRenameActivity.this, Color.parseColor("#f9f9fa"), 0);
            StatusBarHelper.statusBarLightMode(this);
        }
        mContext = this;
        device_rename_titlebar = (TitleBar)findViewById(R.id.device_rename_titlebar);
        device_rename_titlebar.getRightTextView().setVisibility(View.GONE);
        device_rename_titlebar.setOnButtonClickListener(this);
        String name = SharePreferenceUtil.getDeviceName(this);
        nameText = (EditText)findViewById(R.id.edit_mantic_name);
        nameText.setText(name);
        nameText.addTextChangedListener(mTextWatcher);
        nameText.setSelection(nameText.getText().length());
//        nameText.setSelectAllOnFocus(true); //全选

        mDataFactory = DataFactory.newInstance(this);

//        NetworkConfigActivity.net_fail = false;
    }

    private void updateDeviceName(){
        String user_id = SharePreferenceUtil.getUserId(this);
        MopidyServiceApi mopidyServiceApi = MopidyRetrofit.getInstance().create(MopidyServiceApi.class);
        String request = "{\n" +
                "    \"method\": \"core.playlists.mantic_set_device_name\",\n" +
                "    \"jsonrpc\": \"2.0\",\n" +
                "    \"device_id\": \" " +user_id+"\",\n" +
                "    \"params\": {\n" +
                "        \"device_name\": \" "+ nameText.getText().toString()+ "\"\n" +
                "    },\n" +
                "    \"id\": 1\n" +
                "}";
        Glog.i(TAG," updateDeviceName - > request:" + request);
        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("Content-Type, application/json; charset=utf-8"),request);
        Call<ResponseBody> call = mopidyServiceApi.postMopidySetDeviceName(MopidyTools.getHeaders(),body);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful() && null == response.errorBody()) {
                    try {
                        JSONObject mainObject = new JSONObject(response.body().string()); //json 主体
                        String result = mainObject.getString("result");// result json 主体
                        if (result!=null) {
                            ToastUtils.showShort("名字修改成功");
                            SharePreferenceUtil.setDeviceName(mContext,nameText.getText().toString());
                            mDataFactory.notifyUpdateDeviceName(nameText.getText().toString());
                            hideSoftKey();
                            finish();
                        } else {
                            ToastUtils.showShort("确认网络是否连接");
                        }

                    } catch (JSONException | IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Glog.i(TAG,"postMopidyGetDeviceName... onFailure");
            }
        });
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

    private void hideOrShawSave(){

    }
    private void enableSubmitIfReady() {

        if (!TextUtils.isEmpty(nameText.getText())&&!nameText.getText().toString().contains(" ")) {
            device_rename_titlebar.getRightTextView().setText(R.string.save);
            device_rename_titlebar.getRightTextView().setVisibility(View.VISIBLE);
        } else {
            device_rename_titlebar.getRightTextView().setVisibility(View.GONE);
        }

    }

    @Override
    public void onLeftClick() {
        hideSoftKey();
        finish();
        overridePendingTransition(R.anim.push_right_in, R.anim.push_left_out);
    }

    @Override
    public void onRightClick() {
        updateDeviceName();

    }

    private void hideSoftKey() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), 0);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        hideSoftKey();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK){
            this.finish();
            hideSoftKey();
            overridePendingTransition(R.anim.push_right_in, R.anim.push_left_out);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}