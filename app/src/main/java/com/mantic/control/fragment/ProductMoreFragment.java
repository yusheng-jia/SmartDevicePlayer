package com.mantic.control.fragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.jaeger.library.StatusBarUtil;
import com.mantic.antservice.util.ToastUtils;
import com.mantic.control.R;
import com.mantic.control.activity.SoundPlayActivity;
import com.mantic.control.adapter.MySoundProductAdapter;
import com.mantic.control.api.mopidy.MopidyRetrofit;
import com.mantic.control.api.mopidy.MopidyServiceApi;
import com.mantic.control.api.mopidy.MopidyTools;
import com.mantic.control.api.sound.MopidyRsSoundProductBean;
import com.mantic.control.api.sound.ProductDeleteRsBean;
import com.mantic.control.data.DataFactory;
import com.mantic.control.utils.Glog;
import com.mantic.control.utils.ResourceUtils;
import com.mantic.control.utils.SharePreferenceUtil;
import com.mantic.control.utils.StatusBarHelper;
import com.mantic.control.widget.TitleBar;

import java.util.ArrayList;
import java.util.List;

import me.yokeyword.swipebackfragment.SwipeBackActivity;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * author: jayson
 * blog: http://blog.csdn.net/jia4525036
 * time: 2018/5/26.
 * desc: 更多作品
 */
public class ProductMoreFragment extends SwipeBackActivity implements MySoundProductAdapter.ProductItemClickListener, TitleBar.OnButtonClickListener, MySoundProductAdapter.ProductDelClickListener {
    private static final String TAG = "SoundProductMore";
    private List<MopidyRsSoundProductBean.Result.Tracks> productTracks = new ArrayList<>();
    private MySoundProductAdapter productAdapter;
    private RecyclerView produceView;
    private TitleBar titleBar;
    private MopidyServiceApi mpServiceApi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.product_more_fragment);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            StatusBarUtil.setColor(ProductMoreFragment.this, Color.parseColor("#f9f9fa"), 0);
            StatusBarHelper.statusBarLightMode(this);
        }
        initView();
        getProdectData();
    }

    protected void initData() {
        getProdectData();
    }

    protected void initView() {
        mpServiceApi = MopidyRetrofit.getInstance().create(MopidyServiceApi.class);
        produceView = (RecyclerView) findViewById(R.id.product_more_recyclerview);
        titleBar = (TitleBar) findViewById(R.id.product_more_titlebar);
        titleBar.setOnButtonClickListener(this);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        produceView.setLayoutManager(linearLayoutManager);
        productAdapter = new MySoundProductAdapter(this,true);
        productAdapter.setItemClickListener(this);
        productAdapter.setDelClickListener(this);
        produceView.setAdapter(productAdapter);
        produceView.addItemDecoration(new AnchoristItemDecoration(this));

    }

    private void getProdectData() {
        String request = "{\n" +
                "\t\"method\": \"core.playlists.mantic_get_mywork\",\n" +
                "\t\"params\": {\n" +
                "\t\t\"include_tracks\": true,\n" +
                "\t\t\"uri_scheme\": \"mongodb\"\n" +
                "\t},\n" +
                "\t\"device_id\": \""+ SharePreferenceUtil.getUserId(this)+"\",\n" +
                "\t\"jsonrpc\": \"2.0\",\n" +
                "\t\"id\": 50\n" +
                "}\n";
        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("Content-Type, application/json; charset=utf-8"),request);

        Call<MopidyRsSoundProductBean> call = mpServiceApi.postMopidysoundProduct(MopidyTools.getHeaders(),body);
        call.enqueue(new Callback<MopidyRsSoundProductBean>() {
            @Override
            public void onResponse(Call<MopidyRsSoundProductBean> call, final Response<MopidyRsSoundProductBean> response) {
                MopidyRsSoundProductBean bean = response.body();
                if (bean.result.tracks != null ){
                    productTracks = bean.result.tracks;
                    productAdapter.setData(productTracks);
                    productAdapter.notifyDataSetChanged();
                }else {
                    productTracks.clear();
                    productAdapter.setData(productTracks);
                    productAdapter.notifyDataSetChanged();
                }

            }

            @Override
            public void onFailure(Call<MopidyRsSoundProductBean> call, Throwable t) {
                Glog.i(TAG,"onFailure...");
            }
        });
    }

    @Override
    public void onItemClick(int index) {
        MopidyRsSoundProductBean.Result.Tracks trackModel = productTracks.get(index);
        Intent intent = new Intent(this, SoundPlayActivity.class);
        intent.putExtra("track",trackModel);
        this.startActivity(intent);
    }

    @Override
    public void onLeftClick() {
        finish();
        overridePendingTransition(R.anim.push_right_in, R.anim.push_left_out);
    }

    @Override
    public void onRightClick() {
        if (titleBar.getRightTextView().getText().toString().equals("编辑")){
            titleBar.getRightTextView().setText("退出");
            productAdapter.setEditMode(true);
            productAdapter.notifyDataSetChanged();
            titleBar.getLeftImageView().setVisibility(View.GONE);
        }else {
            productAdapter.setEditMode(false);
            titleBar.getRightTextView().setText("编辑");
            productAdapter.notifyDataSetChanged();
            titleBar.getLeftImageView().setVisibility(View.VISIBLE);
        }

    }

    @Override
    public void onDelClick(int index) {

        String uri = productTracks.get(index).uri;
        List<String> uris = new ArrayList<>();
        uris.add(uri);
        RequestBody body = MopidyTools.createProductDeleteRqBean(uris,this);
        Call<ProductDeleteRsBean> call = mpServiceApi.postMopidyDeleteSound(MopidyTools.getHeaders(),body);
        call.enqueue(new Callback<ProductDeleteRsBean>() {
            @Override
            public void onResponse(Call<ProductDeleteRsBean> call, Response<ProductDeleteRsBean> response) {
                ProductDeleteRsBean bean = response.body();
                Glog.i(TAG,bean.toString());
                if (bean.getResult() != null){
                    //删除成功
                    getProdectData();
                    ToastUtils.showShortSafe(R.string.delete_product);
                    sendBroadcast(new Intent("product_add_success"));
                }
            }

            @Override
            public void onFailure(Call<ProductDeleteRsBean> call, Throwable t) {
                ToastUtils.showShortSafe(R.string.delete_product_failed);
            }
        });
    }

    public class AnchoristItemDecoration extends RecyclerView.ItemDecoration{
        private Context ctx;private int dividerHeight;
        private int divideMarginLeft;
        private Paint dividerPaint;

        AnchoristItemDecoration(Context context){
            this.ctx = context;
            this.dividerHeight = 1;
            this.divideMarginLeft = ResourceUtils.dip2px(this.ctx,ResourceUtils.getXmlDef(this.ctx,R.dimen.fragmentChannelDetailCoverMarginLeft));
            this.dividerPaint = new Paint();
            this.dividerPaint.setColor(this.ctx.getResources().getColor(R.color.mainTabBottomLineColor));
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
