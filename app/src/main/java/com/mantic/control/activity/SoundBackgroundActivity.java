package com.mantic.control.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.jaeger.library.StatusBarUtil;
import com.mantic.control.R;
import com.mantic.control.adapter.SoundBackgroundAdapter;
import com.mantic.control.api.mopidy.MopidyRetrofit;
import com.mantic.control.api.mopidy.MopidyServiceApi;
import com.mantic.control.api.mopidy.MopidyTools;
import com.mantic.control.api.sound.MopidyRsSoundBgMusicBean;
import com.mantic.control.listener.SoundSelPlayClickListener;
import com.mantic.control.utils.DownloadManagerUtils;
import com.mantic.control.utils.Glog;
import com.mantic.control.utils.ResourceUtils;
import com.mantic.control.utils.StatusBarHelper;
import com.mantic.control.widget.TitleBar;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import me.yokeyword.swipebackfragment.SwipeBackActivity;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.mantic.control.fragment.MakeSoundFragment.SOUND_DIR;

/**
 * author: jayson
 * blog: http://blog.csdn.net/jia4525036
 * time: 2018/4/26.
 * desc:
 */

public class SoundBackgroundActivity extends SwipeBackActivity implements SoundSelPlayClickListener, TitleBar.OnButtonClickListener, SoundBackgroundAdapter.BgMusicItemClickListener, DownloadManagerUtils.OnDownloadCompleted {
    private final String TAG = "SoundBackgroundActivity";
    private final static String PATH = Environment.getExternalStorageDirectory().getPath();
    private RecyclerView anchorView;
    private SoundBackgroundAdapter bgMusicAdapter;
    private List<MopidyRsSoundBgMusicBean.Result> listBgMusic = new ArrayList<>();
    private TitleBar device_detail_titlebar;
    public static final int BACKGROUND_CLICK = 0;
    public static final int BACKGROUND_PLAY = 1;
    public static final int BACKGROUND_PAUSE = 2;
    private int item;
    private MopidyServiceApi mpServiceApi;
    DownloadManagerUtils downloadManagerUtils;
    MediaPlayer mediaPlayer;
    private int currItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.anchor_select_activity);
        mpServiceApi = MopidyRetrofit.getInstance().create(MopidyServiceApi.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            StatusBarUtil.setColor(SoundBackgroundActivity.this, Color.parseColor("#f9f9fa"), 0);
            StatusBarHelper.statusBarLightMode(this);
        }
        device_detail_titlebar = (TitleBar) findViewById(R.id.anchor_select_titlebar);
        device_detail_titlebar.setOnButtonClickListener(this);
        anchorView = (RecyclerView)findViewById(R.id.anchor_recyclerview);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        anchorView.setLayoutManager(linearLayoutManager);
        bgMusicAdapter = new SoundBackgroundAdapter(this);
        bgMusicAdapter.setAnchorList(listBgMusic);
        anchorView.setAdapter(bgMusicAdapter);
        anchorView.addItemDecoration(new SoundBackgroundActivity.AnchoristItemDecoration(this));
        bgMusicAdapter.setPlayClickListener(this);
        bgMusicAdapter.setBgMusicItemClickListener(this);
        getBgMusicData("advert:background");
        registerDownReceiver();
    }

    private void registerDownReceiver() {
        downloadManagerUtils = new DownloadManagerUtils(this);
        downloadManagerUtils.registerReceiver(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        downloadManagerUtils.unregisterReceiver();
        destroyMediaPlayer();
    }

    public void getBgMusicData(String uri) {
        RequestBody body = MopidyTools.createRequestPageBgBrowse(uri,0);
        Call<MopidyRsSoundBgMusicBean> call = mpServiceApi.postMopidysoundBgMusic(MopidyTools.getHeaders(),body);
        call.enqueue(new Callback<MopidyRsSoundBgMusicBean>() {
            @Override
            public void onResponse(Call<MopidyRsSoundBgMusicBean> call, final Response<MopidyRsSoundBgMusicBean> response) {
                MopidyRsSoundBgMusicBean bean = response.body();
                listBgMusic = bean.results;
                Glog.i(TAG,"Anchor - > resultList: " + listBgMusic);
                if (listBgMusic != null && listBgMusic.size()!= 0){
                    bgMusicAdapter.setAnchorList(listBgMusic);
                    bgMusicAdapter.notifyDataSetChanged();
                }else {

                }
            }

            @Override
            public void onFailure(Call<MopidyRsSoundBgMusicBean> call, Throwable t) {
                Glog.i(TAG,"onFailure...");
            }
        });
    }

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler(){

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case BACKGROUND_CLICK:
                    updateAuthor(msg.arg1);
                    break;
//                case BACKGROUND_PLAY:
//                    Glog.i("ffmpeg","message -> BACKGROUND_PLAY");
//                    item = msg.arg1;
//                    playStart(item);
//                    break;
                case BACKGROUND_PAUSE:
                    item = msg.arg1;
                default:
                    break;

            }
        }
    };

    private void updateAuthor(int item){
        Intent intent = new Intent();
        //头像后续在加
        intent.putExtra("background_name",listBgMusic.get(item).name);
        intent.putExtra("music_file_name",listBgMusic.get(item).mantic_real_url);
//        intent.putExtra("author_value",listAnchor.get(item).getValue());
        setResult(10,intent);
        finish();
    }

    private void mediaPlay(String fileName){
        mediaPlayer = new MediaPlayer();
        try {
            mediaPlayer.setDataSource(SOUND_DIR+fileName);
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            // 通过异步的方式装载媒体资源
            mediaPlayer.prepareAsync();
            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                 @Override
                 public void onPrepared(MediaPlayer mp) {
                // 装载完毕回调
                     mediaPlayer.start();
                     bgMusicAdapter.updatePlayState(2,currItem);
                     bgMusicAdapter.notifyItemChanged(currItem);
                 }});
        } catch (IOException e) {
            Glog.i(TAG,"mediaPlay - > IOException:" + e);
            e.printStackTrace();
        }

        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                bgMusicAdapter.updatePlayState(1,currItem);
                bgMusicAdapter.notifyItemChanged(currItem);
            }
        });
    }

    private void destroyMediaPlayer(){
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    private void mediaPause(int index){
        Glog.i(TAG,"mediaPause.................");
        mediaPlayer.pause();
        bgMusicAdapter.updatePlayState(1,currItem);
        bgMusicAdapter.notifyItemChanged(currItem);
    }

    @Override
    public void onPlayClick(int position,String url) {
        currItem = position;
        String fileName = null;
        String[] fileTemp = url.split("[?]")[0].split("/");
        Glog.i(TAG,"fileTemp.length: " + fileTemp.length);
        if (fileTemp.length>1){
            fileName = fileTemp[fileTemp.length-1];
        }
        Glog.i(TAG,"fileName: " + fileName);
        File file = new File(SOUND_DIR+fileName);
        if (!file.exists()){
            downLoadMusic(url,fileName);
        }else {
            Glog.i(TAG,"文件存在，直接播放:" + fileName);
            mediaPlay(fileName);
        }
    }

    @Override
    public void onPauseClick(int positon) {
        mediaPause(positon);
    }

    private void downLoadMusic(String url,String name){
        downloadManagerUtils.download(url,name,"音乐下载","背景音乐下载");
        bgMusicAdapter.updatePlayState(3,currItem);
        bgMusicAdapter.notifyItemChanged(currItem);
    }
    @Override
    public void onLeftClick() {
        finish();
        overridePendingTransition(R.anim.push_right_in, R.anim.push_left_out);
    }

    @Override
    public void onRightClick() {

    }

    @Override
    public void musicItemClick(int index) {
        updateAuthor(index);
    }

    @Override
    public void onDownloadCompleted(long completeDownloadId) {
        Glog.i(TAG,"onDownloadCompleted: " + completeDownloadId);
        bgMusicAdapter.updatePlayState(1,currItem);
        bgMusicAdapter.notifyItemChanged(currItem);
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
