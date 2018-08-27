package com.mantic.control.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;

import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechSynthesizer;
import com.iflytek.cloud.SynthesizerListener;
import com.jaeger.library.StatusBarUtil;
import com.mantic.control.R;
import com.mantic.control.adapter.AnchorAdapter;
import com.mantic.control.api.mopidy.MopidyRetrofit;
import com.mantic.control.api.mopidy.MopidyServiceApi;
import com.mantic.control.api.mopidy.MopidyTools;
import com.mantic.control.api.sound.MopidyRsAnchorBean;
import com.mantic.control.utils.Glog;
import com.mantic.control.utils.ResourceUtils;
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
 * time: 2018/4/25.
 * desc:
 */

public class AnchorSelActivity extends SwipeBackActivity implements TitleBar.OnButtonClickListener {
    private final String TAG = "AnchorSelActivity";
    private AnchorAdapter anchorAdapter;
    private List<MopidyRsAnchorBean.Result> listAnchor = new ArrayList<>();
    public static final int ANCHOR_CLICK = 0;
    public static final int ANCHOR_PLAY = 1;
    private MopidyServiceApi mpServiceApi;

    /** 讯飞相关*/
    public static final String PREFER_NAME = "com.iflytek.setting";
    // 语音合成对象
    private SpeechSynthesizer mTts;
    // 引擎类型
    private String mEngineType = SpeechConstant.TYPE_CLOUD;
    //参数设置
    private SharedPreferences mSharedPreferences;
    // 默认发音人
    private String voicer = "xiaoyan";
    private Toast mToast;
    boolean pauseFlag = false;
    private int currPlayIndex = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.anchor_select_activity);
        mpServiceApi = MopidyRetrofit.getInstance().create(MopidyServiceApi.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            StatusBarUtil.setColor(AnchorSelActivity.this, Color.parseColor("#f9f9fa"), 0);
            StatusBarHelper.statusBarLightMode(this);
        }
        TitleBar device_detail_titlebar = (TitleBar) findViewById(R.id.anchor_select_titlebar);
        device_detail_titlebar.setOnButtonClickListener(this);
        device_detail_titlebar.setTitleText("选择主播");
        RecyclerView anchorView = (RecyclerView) findViewById(R.id.anchor_recyclerview);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        anchorView.setLayoutManager(linearLayoutManager);
        anchorAdapter = new AnchorAdapter(this,handler);
//        initData();
        anchorView.setAdapter(anchorAdapter);
        anchorView.addItemDecoration(new AnchoristItemDecoration(this));
        getModalData("advert:podcasters");
        mTts = SpeechSynthesizer.createSynthesizer(this, mTtsInitListener);
        mSharedPreferences = getSharedPreferences(PREFER_NAME, MODE_PRIVATE);
        mToast = Toast.makeText(this,"",Toast.LENGTH_SHORT);

    }

    public void getModalData(String uri) {
        RequestBody body = MopidyTools.createRequestPageBrowse(uri,0);
        Call<MopidyRsAnchorBean> call = mpServiceApi.postMopidysoundAnchor(MopidyTools.getHeaders(),body);
        call.enqueue(new Callback<MopidyRsAnchorBean>() {
            @Override
            public void onResponse(Call<MopidyRsAnchorBean> call, final Response<MopidyRsAnchorBean> response) {
                MopidyRsAnchorBean bean = response.body();
                listAnchor = bean.results;
                Glog.i(TAG,"Anchor - > resultList: " + listAnchor);
                if (listAnchor != null && listAnchor.size()!= 0){
                  anchorAdapter.setAnchorList(listAnchor);
                  anchorAdapter.notifyDataSetChanged();
                }else {

                }
            }

            @Override
            public void onFailure(Call<MopidyRsAnchorBean> call, Throwable t) {
                Glog.i(TAG,"onFailure...");
            }
        });
    }

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler(){
        int item;
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case ANCHOR_CLICK:
                    updateAuthor(msg.arg1);
                    break;
                case ANCHOR_PLAY:
                    item = msg.arg1;
                    authorPlay(item);
                    break;
                default:
                    break;

            }
        }
    };

    private void authorPlay(int index){
        Glog.i(TAG,"authorPlay...");
        currPlayIndex = index;
        MopidyRsAnchorBean.Result Ancher = listAnchor.get(index);

        setParam(Ancher.mantic_podcaster_key);
        int code = mTts.startSpeaking("欢迎使用酷曼语音合成", mTtsListener);
        if (code != ErrorCode.SUCCESS) {
            showTip("语音合成失败,错误码: " + code);
        }
    }
    private void updateAuthor(int item){
        Intent intent = new Intent();
        //头像后续在加
        intent.putExtra("author_icon",listAnchor.get(item).mantic_podcaster_avater);
        intent.putExtra("author_name",listAnchor.get(item).name);
        intent.putExtra("author_value",listAnchor.get(item).mantic_podcaster_key);
        setResult(10,intent);
        finish();
    }

    @Override
    public void onLeftClick() {
        finish();
        overridePendingTransition(R.anim.push_right_in, R.anim.push_left_out);
    }

    @Override
    public void onRightClick() {

    }


    /**
     * 合成回调监听。
     */
    private SynthesizerListener mTtsListener = new SynthesizerListener() {

        @Override
        public void onSpeakBegin() {
            showTip("开始播放");
            anchorAdapter.setItemPlay(true,currPlayIndex);
            anchorAdapter.notifyItemChanged(currPlayIndex);
            pauseFlag = false;
        }

        @Override
        public void onSpeakPaused() {
            showTip("暂停播放");
            pauseFlag = true;

        }

        @Override
        public void onSpeakResumed() {
            showTip("继续播放");
            pauseFlag = false;

        }

        @Override
        public void onBufferProgress(int percent, int beginPos, int endPos,
                                     String info) {
            // 合成进度
            Glog.i(TAG,"onBufferProgress.........");

        }

        @Override
        public void onSpeakProgress(int percent, int beginPos, int endPos) {
            Glog.i(TAG,"onSpeakProgress........."+percent);

        }

        @Override
        public void onCompleted(SpeechError error) {
            if (error == null) {
                showTip("播放完成");
                anchorAdapter.setItemPlay(false,currPlayIndex);
                anchorAdapter.notifyItemChanged(currPlayIndex);
            } else if (error != null) {
                showTip(error.getPlainDescription(true));
            }
        }

        @Override
        public void onEvent(int eventType, int arg1, int arg2, Bundle obj) {
            // 以下代码用于获取与云端的会话id，当业务出错时将会话id提供给技术支持人员，可用于查询会话日志，定位出错原因
            // 若使用本地能力，会话id为null
            //	if (SpeechEvent.EVENT_SESSION_ID == eventType) {
            //		String sid = obj.getString(SpeechEvent.KEY_EVENT_SESSION_ID);
            //		Log.d(TAG, "session id =" + sid);
            //	}
        }
    };

    private void showTip(final String str) {
        mToast.setText(str);
        mToast.show();
    }

    public class AnchoristItemDecoration extends RecyclerView.ItemDecoration{
        private Context ctx;private int dividerHeight;
        private int divideMarginLeft;
        private Paint dividerPaint;

        public AnchoristItemDecoration(Context context){
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

    /**
     * 初始化监听。
     */
    private InitListener mTtsInitListener = new InitListener() {
        @Override
        public void onInit(int code) {
            Glog.d(TAG, "InitListener init() code = " + code);
            if (code != ErrorCode.SUCCESS) {
                showTip("初始化失败,错误码："+code);
            } else {
                // 初始化成功，之后可以调用startSpeaking方法
                // 注：有的开发者在onCreate方法中创建完合成对象之后马上就调用startSpeaking进行合成，
                // 正确的做法是将onCreate中的startSpeaking调用移至这里
            }
        }
    };

    /**
     * 参数设置
     * @return
     */
    private void setParam(String anchor){
        // 清空参数
        mTts.setParameter(SpeechConstant.PARAMS, null);
        // 根据合成引擎设置相应参数
        if(mEngineType.equals(SpeechConstant.TYPE_CLOUD)) {
            mTts.setParameter(SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_CLOUD);
            // 设置在线合成发音人
            mTts.setParameter(SpeechConstant.VOICE_NAME, anchor);
            //设置合成语速
            mTts.setParameter(SpeechConstant.SPEED, mSharedPreferences.getString("speed_preference", "50"));
            //设置合成音调
            mTts.setParameter(SpeechConstant.PITCH, mSharedPreferences.getString("pitch_preference", "50"));
            //设置合成音量
            mTts.setParameter(SpeechConstant.VOLUME, mSharedPreferences.getString("volume_preference", "50"));
        }else {
            mTts.setParameter(SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_LOCAL);
            // 设置本地合成发音人 voicer为空，默认通过语记界面指定发音人。
            mTts.setParameter(SpeechConstant.VOICE_NAME, "");
            /**
             * TODO 本地合成不设置语速、音调、音量，默认使用语记设置
             * 开发者如需自定义参数，请参考在线合成参数设置
             */
        }
        //设置播放器音频流类型
        mTts.setParameter(SpeechConstant.STREAM_TYPE, mSharedPreferences.getString("stream_preference", "3"));
        // 设置播放合成音频打断音乐播放，默认为true
        mTts.setParameter(SpeechConstant.KEY_REQUEST_FOCUS, "true");

        // 设置音频保存路径，保存音频格式支持pcm、wav，设置路径为sd卡请注意WRITE_EXTERNAL_STORAGE权限
        // 注：AUDIO_FORMAT参数语记需要更新版本才能生效
        mTts.setParameter(SpeechConstant.AUDIO_FORMAT, "wav");
        mTts.setParameter(SpeechConstant.TTS_AUDIO_PATH, Environment.getExternalStorageDirectory()+"/coomaan/test.wav");
    }

}
