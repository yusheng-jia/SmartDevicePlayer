package com.mantic.control.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.iot.sdk.IoTSDKManager;
import com.google.gson.Gson;
import com.mantic.control.ManticApplication;
import com.mantic.control.R;
import com.mantic.control.api.account.AccountRetrofit;
import com.mantic.control.api.account.AccountServiceApi;
import com.mantic.control.api.account.AccountUrl;
import com.mantic.control.data.ManticSharedPreferences;
import com.mantic.control.fragment.BluetoothListFragment;
import com.mantic.control.utils.Glog;
import com.mantic.control.utils.SharePreferenceUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by wujiangxia on 2017/4/7.
 */
public class SelectInterestActivity extends AppCompatActivity implements View.OnClickListener {
    private static String TAG = "SelectInterestActivity";
    private InterestContentItemAdapter adapter;
    private TextView tvTilte, tvNext;
    private ImageView ivBack;
    protected ArrayList<String> selectId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_interest);
        selectId = new ArrayList<String>();
        String[] titleArray = getResources().getStringArray(R.array.select_list_item);
        TypedArray categoryArray = getResources().obtainTypedArray(R.array.select_list_item_bg);//读取drawable数组
        int arraylen = categoryArray.length();
        int[] iconArray = new int[arraylen];
        for (int i = 0; i < categoryArray.length(); i++) {
            iconArray[i] = categoryArray.getResourceId(i, 0);
        }
        categoryArray.recycle();
        adapter = new InterestContentItemAdapter(SelectInterestActivity.this, titleArray, iconArray);
        GridView lv = (GridView) findViewById(R.id.girdview_interest);
        lv.setAdapter(adapter);
        tvTilte = (TextView) findViewById(R.id.toolbar_title);
        tvNext = (TextView) findViewById(R.id.toolbar_next);
        ivBack = (ImageView) findViewById(R.id.toolbar_back);
        tvTilte.setText(R.string.title_interest);
        tvNext.setText(R.string.start);
        tvNext.setVisibility(View.VISIBLE);
        ivBack.setVisibility(View.VISIBLE);
        tvNext.setOnClickListener(this);
        ivBack.setOnClickListener(this);

        SharePreferenceUtil.setNetwork(this); //配网成功
        SharePreferenceUtil.setDeviceName(this,SharePreferenceUtil.getUserName(this)+"的音箱");

        NetworkConfigActivity.btList.clear();
        NetworkConfigActivity.connected = false;
        BluetoothListFragment.connecting = false;

        coomaanBindDevice();



    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.toolbar_back:
                finish();
                break;
            case R.id.toolbar_next:
                Glog.v("wujx", "selectId:" + selectId);
                if (selectId.size() < 2) {
                    Toast.makeText(getApplicationContext(), R.string.title_interest, Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    if(selectId != null && selectId.size() > 0) {
                        SharedPreferences sp = ManticSharedPreferences.getInstance(this);
                        StringBuilder sb = new StringBuilder();
                        for(int i = 0;i < selectId.size();i++){
                            sb.append(selectId.get(i));
                            if(i != (selectId.size() - 1)){
                                sb.append(",");
                            }
                        }
                        Glog.i(TAG,"INTEREST = "+sb.toString());
                        sp.edit().putString(ManticSharedPreferences.KEY_INTEREST, sb.toString()).apply();
                    }
                    Intent it = new Intent(this, MainActivity.class);
                    startActivity(it);
                    finish();
                }
                break;

        }
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
        map.put("Lc", ManticApplication.channelId);

        String request = "{\"user_id\":\""+ user_id+"\",\"device_uuid\":\""+ uuid+"\",\"device_token\":\""+ token+"\"}";
        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"),request);

        Call<ResponseBody> call = accountServiceApi.deviceBind(map,body);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful() && null == response.errorBody()){
                    Glog.i(TAG,"coomaanBindDevice sucess: ");
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
                        Glog.i(TAG,"coomaanBindDevice: " + data);
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


    @Override
    protected void onResume() {
        super.onResume();
           /* if (ToolUtil.getCurrentDeviceName(this) != null)
                finish();
        */
    }

    private class InterestContentItemAdapter extends BaseAdapter {

        String[] titleArray;
        int[] iconArray;
        Context mContext;

        public InterestContentItemAdapter(Context context, String[] titleArray, int[] iconArray) {
            this.mContext = context;
            this.titleArray = titleArray;
            this.iconArray = iconArray;
        }

        @Override
        public int getCount() {
            return titleArray.length;
        }

        @Override
        public Object getItem(int position) {
            return titleArray[position];
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            final ViewHolder holder;

            if (convertView == null) {
                convertView = LayoutInflater.from(mContext).inflate(R.layout.select_interest_item, parent, false);
                holder = new ViewHolder();
                holder.interest_name = (TextView) convertView.findViewById(R.id.interest_name);
                holder.interest_bg = (ImageView) convertView.findViewById(R.id.interest_bg);
                holder.interest_checkbox = (CheckBox) convertView.findViewById(R.id.interest_checkbox);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            holder.interest_bg.setBackgroundResource(iconArray[position]);
            holder.interest_bg.setAlpha(0.2f);
            holder.interest_name.setText(titleArray[position]);
            holder.interest_bg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (holder.interest_checkbox.isChecked()) {
                        Glog.v("wujx", "position isChecked:" + position);
                        holder.interest_checkbox.setChecked(false);
                        holder.interest_bg.setAlpha(0.2f);
                        holder.interest_name.setTextColor(getResources().getColor(R.color.white_4));
                        selectId.remove(String.valueOf(position));
                        enableSubmitIfReady();
                    } else {

                        Glog.v("wujx", "position no isChecked:" + position);
                        holder.interest_checkbox.setChecked(true);
                        holder.interest_bg.setAlpha(0.7f);
                        holder.interest_name.setTextColor(getResources().getColor(R.color.white));
                        selectId.add(String.valueOf(position));
                        enableSubmitIfReady();
                    }
                }
            });


            return convertView;
        }
    }


    public class ViewHolder {
        private ImageView interest_bg;
        private TextView interest_name;
        private CheckBox interest_checkbox;

    }

    private void enableSubmitIfReady() {

        if (selectId.size() >= 2) {
            tvNext.setEnabled(true);
        } else {
            tvNext.setEnabled(false);
        }

    }
}
