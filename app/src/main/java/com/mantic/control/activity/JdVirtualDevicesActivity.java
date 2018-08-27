package com.mantic.control.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.jaeger.library.StatusBarUtil;
import com.jd.smartcloudmobilesdk.devicecontrol.DeviceControlManager;
import com.jd.smartcloudmobilesdk.net.ResponseCallback;
import com.mantic.control.R;
import com.mantic.control.adapter.JdVirtualDeviceAdapter;
import com.mantic.control.data.jd.JdAddress;
import com.mantic.control.data.jd.JdDeviceResult;
import com.mantic.control.utils.Glog;
import com.mantic.control.utils.ResourceUtils;
import com.mantic.control.utils.StatusBarHelper;
import com.mantic.control.widget.TitleBar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.yokeyword.swipebackfragment.SwipeBackActivity;

/**
 * author: jayson
 * blog: http://blog.csdn.net/jia4525036
 * time: 2018/6/1.
 * desc:
 */
public class JdVirtualDevicesActivity extends SwipeBackActivity implements TitleBar.OnButtonClickListener {
    private static final String TAG = JdVirtualDevicesActivity.class.getSimpleName();
    private List<JdDeviceResult.JdDevice> listDevice = new ArrayList<>();
    private JdVirtualDeviceAdapter deviceAdapter;
    private static final int REFRESH_SUCCESS = 0;
    private static final int REFRESH_FAIL = 1;
    @BindView(R.id.device_list)
    RecyclerView listview;
    @BindView(R.id.no_device_text)
    TextView noText;
    @BindView(R.id.jd_devices_titlebar)
    TitleBar titleBar;
    @BindView(R.id.loading_view)
    LinearLayout loadView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jd_virtual_devices);
        ButterKnife.bind(this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            StatusBarUtil.setColor(this, Color.parseColor("#f9f9fa"), 0);
            StatusBarHelper.statusBarLightMode(this);
        }
        initView();
        getDevices();
    }

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case REFRESH_SUCCESS:
                    loadView.setVisibility(View.GONE);
                    Glog.i(TAG,"listDevice: " + listDevice);
                    if (listDevice!=null && listDevice.size() != 0){
                        deviceAdapter.setData(listDevice);
                        deviceAdapter.notifyDataSetChanged();
                        noText.setVisibility(View.GONE);
                    }
                    break;
                case REFRESH_FAIL:
                    loadView.setVisibility(View.GONE);
                    break;
            }
        }
    };

    private void initView() {
        deviceAdapter = new JdVirtualDeviceAdapter(this);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        listview.setLayoutManager(linearLayoutManager);
        listview.setAdapter(deviceAdapter);
        listview.addItemDecoration(new DeviceListItemDecoration(this));
        titleBar.setOnButtonClickListener(this);
    }

    public void getDevices(){
        DeviceControlManager.getDeviceList(new ResponseCallback() {
            @Override
            public void onSuccess(String s) {
                Glog.i(TAG,"获取设备列表：" + s);
                if (s != null){
                    try {
                        JSONObject jsonObject = new JSONObject(s);
                        JSONArray jsonArray = jsonObject.getJSONArray("result");
                        if (jsonArray != null && jsonArray.length() != 0){
                            JSONObject resultObject = jsonArray.getJSONObject(0);
                            JdDeviceResult jdDeviceResult = new Gson().fromJson(resultObject.toString(),JdDeviceResult.class);
                            Glog.i(TAG,"jdDeviceResult: " + jdDeviceResult);
                            listDevice = jdDeviceResult.getList();
                        }
                        mHandler.sendEmptyMessage(REFRESH_SUCCESS);
                    } catch (JSONException e) {
                        mHandler.sendEmptyMessage(REFRESH_FAIL);
                        e.printStackTrace();
                    }
                }
                noText.setVisibility(View.VISIBLE);
            }

            @Override
            public void onFailure(String s) {
                Glog.i(TAG,"获取失败：" + s);
                mHandler.sendEmptyMessage(REFRESH_FAIL);
            }
        });
    }

    @Override
    public void onLeftClick() {
        finish();
    }

    @Override
    public void onRightClick() {
        startVirtualManager();
    }

    private void startVirtualManager() {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://smart.jd.com/app/download"));
        startActivity(intent);

    }

    public class DeviceListItemDecoration extends RecyclerView.ItemDecoration{
        private Context ctx;
        private int dividerHeight;
        private int divideMarginLeft;
        private Paint dividerPaint;

        DeviceListItemDecoration(Context context){
            this.ctx = context;
            this.dividerHeight = 1;
            this.divideMarginLeft = ResourceUtils.dip2px(this.ctx,ResourceUtils.getXmlDef(this.ctx,R.dimen.fragmentChannelDetailCoverMarginLeft));
            this.dividerPaint = new Paint();
            this.dividerPaint.setColor(this.ctx.getResources().getColor(R.color.fragment_my_channel_background));
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            int position = parent.getChildAdapterPosition(view);
            outRect.bottom = this.dividerHeight;
        }

        @Override
        public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
            int childCount = parent.getChildCount();
            int left = parent.getPaddingLeft() + this.divideMarginLeft;
            int right = parent.getWidth() - parent.getPaddingRight();

            for (int i = 0; i < childCount ; i++) {
                View view = parent.getChildAt(i);
                float top = view.getBottom();
                float bottom = view.getBottom() + dividerHeight;
                c.drawRect(left, top, right, bottom, dividerPaint);
            }
        }
    }
}
