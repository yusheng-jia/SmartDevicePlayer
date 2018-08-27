package com.mantic.control.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mantic.antservice.util.ToastUtils;
import com.mantic.control.R;
import com.mantic.control.activity.SoundPlayActivity;
import com.mantic.control.adapter.DubbingWorksAdapter;
import com.mantic.control.adapter.MySoundProductAdapter;
import com.mantic.control.api.mopidy.MopidyRetrofit;
import com.mantic.control.api.mopidy.MopidyServiceApi;
import com.mantic.control.api.mopidy.MopidyTools;
import com.mantic.control.api.sound.MopidyRsSoundModalBean;
import com.mantic.control.api.sound.MopidyRsSoundProductBean;
import com.mantic.control.utils.Glog;
import com.mantic.control.utils.ResourceUtils;
import com.mantic.control.utils.SharePreferenceUtil;
import com.mantic.control.widget.CustomViewPager;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * author: jayson
 * blog: http://blog.csdn.net/jia4525036
 * time: 2018/4/23.
 * desc:
 */

public class MakeSoundFragment extends BaseFragment implements View.OnClickListener ,MySoundProductAdapter.ProductItemClickListener{
    private static final String TAG = "MakeSoundFragment";
    public static final String SOUND_DIR = Environment.getExternalStorageDirectory()+"/coomaan/";
//    static {
//        System.loadLibrary("native-lib");
//    }
//    private List<SoundModal> modalList = new ArrayList<SoundModal>();
    private List<MopidyRsSoundProductBean.Result.Tracks> productTracks = new ArrayList<>();
    private MopidyServiceApi mpServiceApi;
    ImageView editButton ;
    private CustomViewPager vp_dubbing_works;
    private LinearLayout noWorkLayout;
    private DubbingWorksAdapter dubbingWorksAdapter;
    private MySoundProductAdapter productAdapter;
    private RecyclerView produceView;
    private TextView moreWorks;

    @Override
    protected void initView(View view) {
        super.initView(view);
        mpServiceApi = MopidyRetrofit.getInstance().create(MopidyServiceApi.class);
        editButton = (ImageView) view.findViewById(R.id.image_make_sound);
        vp_dubbing_works = (CustomViewPager) view.findViewById(R.id.vp_dubbing_works);
        noWorkLayout = (LinearLayout) view.findViewById(R.id.no_works);
        produceView = (RecyclerView) view.findViewById(R.id.prodect_view);
        moreWorks = (TextView) view.findViewById(R.id.more_works);
        moreWorks.setOnClickListener(this);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        produceView.setLayoutManager(linearLayoutManager);
        productAdapter = new MySoundProductAdapter(getContext(),false);
        productAdapter.setItemClickListener(this);
        produceView.setAdapter(productAdapter);
        produceView.addItemDecoration(new MakeSoundFragment.AnchoristItemDecoration(getContext()));
        editButton.setOnClickListener(this);
        vp_dubbing_works.setAdapter(dubbingWorksAdapter);
        vp_dubbing_works.setOffscreenPageLimit(3);
        vp_dubbing_works.setPageMargin((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,  6, getResources().getDisplayMetrics()));
        getModalData("advert:scene");
        getProdectData();
        registerAddProductReceiver();
    }

    private BroadcastReceiver addReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals("product_add_success")){
                getProdectData();
            }
        }
    };

    private void registerAddProductReceiver() {
        IntentFilter filter = new IntentFilter("product_add_success");
        getContext().registerReceiver(addReceiver,filter);
    }

    public void getModalData(String uri) {
        RequestBody body = MopidyTools.createRequestPageBrowse(uri,0);
        Call<MopidyRsSoundModalBean> call = mpServiceApi.postMopidysoundModal(MopidyTools.getHeaders(),body);
        call.enqueue(new Callback<MopidyRsSoundModalBean>() {
            @Override
            public void onResponse(Call<MopidyRsSoundModalBean> call, final Response<MopidyRsSoundModalBean> response) {
                MopidyRsSoundModalBean bean = response.body();
                List<MopidyRsSoundModalBean.Result> resultList = new ArrayList<>();
                if (bean.results!=null){
                    resultList = bean.results;
                    Glog.i(TAG,"resultList: " + resultList);
                    dubbingWorksAdapter.setModalData(resultList);
                    dubbingWorksAdapter.notifyDataSetChanged();
                }else {
                    dubbingWorksAdapter.setModalData(resultList);
                    dubbingWorksAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<MopidyRsSoundModalBean> call, Throwable t) {
                Glog.i(TAG,"onFailure...");
            }
        });
    }

    private void getProdectData() {
        String request = "{\n" +
                "\t\"method\": \"core.playlists.mantic_get_mywork\",\n" +
                "\t\"params\": {\n" +
                "\t\t\"include_tracks\": true,\n" +
                "\t\t\"uri_scheme\": \"mongodb\"\n" +
                "\t},\n" +
                "\t\"device_id\": \""+ SharePreferenceUtil.getUserId(getContext())+"\",\n" +
                "\t\"jsonrpc\": \"2.0\",\n" +
                "\t\"id\": 50\n" +
                "}\n";
        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("Content-Type, application/json; charset=utf-8"),request);

        Call<MopidyRsSoundProductBean> call = mpServiceApi.postMopidysoundProduct(MopidyTools.getHeaders(),body);
        call.enqueue(new Callback<MopidyRsSoundProductBean>() {
            @Override
            public void onResponse(Call<MopidyRsSoundProductBean> call, final Response<MopidyRsSoundProductBean> response) {
                MopidyRsSoundProductBean bean = response.body();
                if (bean!=null && bean.result!=null && bean.result.tracks != null){
                    productTracks = bean.result.tracks;
                    noWorkLayout.setVisibility(View.GONE);
                    produceView.setVisibility(View.VISIBLE);
                    productAdapter.setData(productTracks);
                    productAdapter.notifyDataSetChanged();
                }else {
                    productTracks.clear();
                    productAdapter.setData(productTracks);
                    productAdapter.notifyDataSetChanged();
                    noWorkLayout.setVisibility(View.VISIBLE);
                    produceView.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(Call<MopidyRsSoundProductBean> call, Throwable t) {
                Glog.i(TAG,"onFailure...");
            }
        });
    }

    @Override
    protected int setLayoutResourceId() {
        return R.layout.fragment_make_sound;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        Glog.i("jys","jni->" + stringFromJni());
        dubbingWorksAdapter = new DubbingWorksAdapter(getContext());
        boolean fileDir = isFolderExists(SOUND_DIR);
        Glog.i(TAG,"fileDir: " + fileDir);
    }

    @Override
    public void onResume() {

        super.onResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (addReceiver != null){
            getContext().unregisterReceiver(addReceiver);
        }
    }

    private void startMoreFragment(){
        Intent intent = new Intent(mContext,ProductMoreFragment.class);
        mContext.startActivity(intent);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id){
            case R.id.image_make_sound:
                Intent intent = new Intent(getContext(),EditSoundActivity.class);
                startActivity(intent);
                getActivity().overridePendingTransition(R.anim.push_right_in, R.anim.push_left_out);
                break;
            case R.id.more_works:
                if (productTracks.size()>0){
                    startMoreFragment();
                }else {
                    ToastUtils.showShortSafe("没有更多");
                }
                break;
            default:
                break;
        }
    }

//    public native String stringFromJni();
    /* 检查coomaan文件夹是否存在，不存在则创建*/
    private boolean isFolderExists(String strFolder) {
        File file = new File(strFolder);
        if (!file.exists()) {
            if (file.mkdir()) {
                return true;
            } else {
                return false;
            }
        }
        return true;
    }

    @Override
    public void onItemClick(int index) {
        MopidyRsSoundProductBean.Result.Tracks trackModel = productTracks.get(index);
        Intent intent = new Intent(mContext, SoundPlayActivity.class);
        intent.putExtra("track",trackModel);
        mContext.startActivity(intent);
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
