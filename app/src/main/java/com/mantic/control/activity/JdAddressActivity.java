package com.mantic.control.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
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
import com.mantic.control.R;
import com.mantic.control.adapter.JdSelAddressAdapter;
import com.mantic.control.data.jd.JDClass;
import com.mantic.control.data.jd.JdAddress;
import com.mantic.control.data.jd.Md5Util;
import com.mantic.control.data.jd.NetUtil;
import com.mantic.control.data.jd.SpUtils;
import com.mantic.control.utils.Glog;
import com.mantic.control.utils.ResourceUtils;
import com.mantic.control.utils.StatusBarHelper;
import com.mantic.control.widget.TitleBar;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
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
public class JdAddressActivity extends SwipeBackActivity implements JdSelAddressAdapter.RadioClickListener, TitleBar.OnButtonClickListener {
    private static final String TAG = "JdAddressActivity";
    private static final int REFRESH_SUCCESS = 0;
    private static final int REFRESH_FAIL = 1;
    private Context mContext;
    String accessToken = null;
    private List<JdAddress.Address> listAddress = new ArrayList<>();
    private JdSelAddressAdapter jdSelAddressAdapter;
    private int currentPosition = -1;
    private long defAddressId;

    @BindView(R.id.no_address)
    TextView noAddress;
    @BindView(R.id.loading_view)
    LinearLayout loadView;
    @BindView(R.id.jd_address_list)
    RecyclerView addressView;
    @BindView(R.id.jd_shopping_info_titlebar)
    TitleBar titleBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jd_address);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            StatusBarUtil.setColor(this, Color.parseColor("#f9f9fa"), 0);
            StatusBarHelper.statusBarLightMode(this);
        }
        mContext = this;
        ButterKnife.bind(this);
        initView();
        initAccessToken();
        getAddress();
    }

    private void initView() {
        defAddressId = getIntent().getLongExtra("addressId",0);
        Glog.i(TAG,"defAddressId: " + defAddressId);
        loadView.setVisibility(View.VISIBLE);
        jdSelAddressAdapter = new JdSelAddressAdapter(mContext);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        addressView.setLayoutManager(linearLayoutManager);
        jdSelAddressAdapter = new JdSelAddressAdapter(mContext);
        jdSelAddressAdapter.setRadioClickListener(this);
        addressView.addItemDecoration(new AddressListItemDecoration(this));
        addressView.setAdapter(jdSelAddressAdapter);
        titleBar.setOnButtonClickListener(this);
    }

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case REFRESH_SUCCESS:
                    loadView.setVisibility(View.GONE);
                    Glog.i(TAG,"listAddress: " + listAddress);
                    if (listAddress!=null && listAddress.size() != 0){
                        jdSelAddressAdapter.setData(listAddress);
                        jdSelAddressAdapter.setDefAddress(defAddressId);
                        jdSelAddressAdapter.notifyDataSetChanged();
                    }
                    break;
                case REFRESH_FAIL:
                    loadView.setVisibility(View.GONE);
                    break;
            }
        }
    };

    private void initAccessToken() {
        String name = Md5Util.md5(JDClass.appKey + "");
        accessToken = SpUtils.getFromLocal(mContext, name, "access_token", "");
    }

    private void getAddress(){
        String url = JDClass.AddressUrl + "sign=" + getSign() +
                "&timestamp=" + NetUtil.getCurrentDateTime() +"&v=2.0&app_key="+JDClass.appKey +
                "&access_token=" + accessToken +"&method=jd.smart.open.alpha.config.getAddrs" + "&360buy_param_json={\"client_ip\":\"172.34.56.78\",\"device_id\":\"AB-CD-EF-FF-FF-DC\", \"version\":\"1\"}";
//        Glog.i(TAG,"地址请求url：" + url);
        NetUtil.post(url, " ", new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                mHandler.sendEmptyMessage(REFRESH_FAIL);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String body = response.body().string();
                Glog.i(TAG, "onResponse body: "+body );
                JSONObject json = null;
                try {
                    json = new JSONObject(body);
                    JSONObject codeJson = json.getJSONObject("jd_smart_open_alpha_config_getAddrs_response");
                    JSONObject result = codeJson.getJSONObject("result");
                    JdAddress jdAddress = new Gson().fromJson(result.toString(),JdAddress.class);
//                    Glog.i(TAG,"jdAddress: " + jdAddress.toString());
                    listAddress = jdAddress.getAddressList();
                    mHandler.sendEmptyMessage(REFRESH_SUCCESS);
                } catch (JSONException e) {
                    mHandler.sendEmptyMessage(REFRESH_FAIL);
                    e.printStackTrace();
                }
            }
        });
    }

    private String getSign(){
        String tempSign = JDClass.appSecret + JDClass.signContent +"access_token"+ accessToken + "app_keyHUR698I6I5GK5WRYP3SRBRIJ8CS6UWYKmethodjd.smart.open.alpha.config.getAddrstimestamp"+
                NetUtil.getCurrentDateTime()+"v2.0" + JDClass.appSecret;
//        Glog.i(TAG,"加密前子串：" + tempSign);
        return Md5Util.md5Up(tempSign);
    }

    @Override
    public void onClick(int index) {
        currentPosition = index;
        jdSelAddressAdapter.setDefAddress(listAddress.get(index).getId());
        jdSelAddressAdapter.notifyDataSetChanged();
    }

    private void setActivityResult(){
        if (currentPosition != -1){
            JdAddress.Address addressObject = listAddress.get(currentPosition);
            Intent intent = new Intent();
            intent.putExtra("address",addressObject);
            setResult(JdShoppingInfoActivity.RESULT_ADDRESS,intent);
        }
    }
    @Override
    public void onBackPressed() {
        Glog.i(TAG,"onBackPressed...");
        setActivityResult();
        super.onBackPressed();
    }

    @Override
    public void onLeftClick() {
        setActivityResult();
        finish();
    }

    @Override
    public void onRightClick() {

    }


    public class AddressListItemDecoration extends RecyclerView.ItemDecoration{
        private Context ctx;
        private int dividerHeight;
        private int divideMarginLeft;
        private Paint dividerPaint;

        AddressListItemDecoration(Context context){
            this.ctx = context;
            this.dividerHeight = 10;
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
