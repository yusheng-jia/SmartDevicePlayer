package com.mantic.control.activity;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.baidu.iot.sdk.HttpStatus;
import com.baidu.iot.sdk.IoTException;
import com.baidu.iot.sdk.model.ControlResult;
import com.baidu.iot.sdk.model.PageInfo;
import com.jaeger.library.StatusBarUtil;
import com.mantic.antservice.util.ToastUtils;
import com.mantic.control.AudioHelper;
import com.mantic.control.ManticApplication;
import com.mantic.control.R;
import com.mantic.control.api.sound.MopidyRsSoundModalBean;
import com.mantic.control.api.sound.MopidyRsSoundProductBean;
import com.mantic.control.data.Channel;
import com.mantic.control.fragment.EditSoundActivity;
import com.mantic.control.listener.DeviceStateController;
import com.mantic.control.utils.GlideImgManager;
import com.mantic.control.utils.Glog;
import com.mantic.control.utils.SharePreferenceUtil;
import com.mantic.control.utils.StatusBarHelper;
import com.mantic.control.utils.TimeUtil;
import com.mantic.control.utils.Util;
import com.mantic.control.widget.TitleBar;

import org.w3c.dom.Text;

import java.io.IOException;

import me.yokeyword.swipebackfragment.SwipeBackActivity;

import static com.mantic.control.fragment.MakeSoundFragment.SOUND_DIR;

/**
 * author: jayson
 * blog: http://blog.csdn.net/jia4525036
 * time: 2018/4/28.
 * desc:
 */

public class SoundPlayActivity extends SwipeBackActivity implements TitleBar.OnButtonClickListener, View.OnClickListener {
    private static final String TAG = SoundPlayActivity.class.getSimpleName();
    private ImageView playerImage;
    private ImageView playButton;
    private TextView playerText;
    private TextView soundPlayDetail;
//    private TextView titleView;
    private TextView contentView;
    private SeekBar volumnSeek;
    private SeekBar progressSeek;
    private TextView currentTime;
    private TextView totalTime;

    String title;
    String content;
    MopidyRsSoundModalBean.Result soundModal;
    MopidyRsSoundProductBean.Result.Tracks trackModel;
    private TitleBar device_detail_titlebar;
    private Button createButton;

    private MediaPlayer mediaPlayer;
    private boolean isPause = false;

    private int totalMsec = 0;
    private int currentMsec = 0;

    private Handler mHandler = new Handler();
    private Context mContext;
    private String playUrl = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        setContentView(R.layout.activity_sound_play);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            StatusBarUtil.setColor(SoundPlayActivity.this, Color.parseColor("#f9f9fa"), 0);
            StatusBarHelper.statusBarLightMode(this);
        }
        soundModal = (MopidyRsSoundModalBean.Result) getIntent().getSerializableExtra("modal");
        trackModel = (MopidyRsSoundProductBean.Result.Tracks) getIntent().getSerializableExtra("track");
        Glog.i(TAG,"trackModel: " + trackModel + "    soundModal: " + soundModal);
        playerImage = (ImageView) findViewById(R.id.player_image);
        playerText = (TextView) findViewById(R.id.player_text);
        soundPlayDetail = (TextView) findViewById(R.id.sound_play_detail);
        device_detail_titlebar = (TitleBar) findViewById(R.id.sound_play_titlebar);
//        titleView = (TextView) findViewById(R.id.sound_play_title);
        contentView = (TextView) findViewById(R.id.sound_play_content);
        volumnSeek = (SeekBar) findViewById(R.id.sound_play_volume_seekbar);
        createButton = (Button) findViewById(R.id.create_module);
        playButton = (ImageView) findViewById(R.id.voice_play);
        currentTime = (TextView) findViewById(R.id.sound_player_current_time);
        totalTime = (TextView) findViewById(R.id.sound_player_total_time);
        progressSeek = (SeekBar) findViewById(R.id.sound_player_progress_seekbar);
        createButton.setOnClickListener(this);
        playButton.setOnClickListener(this);
        initData();
        volumnSeek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
//                mediaPlayer.setVolume((float)progress/100,(float)progress/100);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                int progress = seekBar.getProgress();
                controlVolume(progress);
            }
        });
        // 音箱播放不可滑动
        progressSeek.setEnabled(false);
//        progressSeek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
//            @Override
//            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
//
//            }
//
//            @Override
//            public void onStartTrackingTouch(SeekBar seekBar) {
//
//            }
//
//            @Override
//            public void onStopTrackingTouch(SeekBar seekBar) {
//                if (mediaPlayer != null){
//                    currentMsec = seekBar.getProgress();
//                    Glog.i(TAG,"seekBar.getProgress(): " + currentMsec);
//                    seekTo(currentMsec);
//                    updateCurrentTime(currentMsec);
//                }
//            }
//        });

        mediaPlayer = new MediaPlayer();
        try {
            if (soundModal != null){
                mediaPlayer.setDataSource(soundModal.mantic_real_url);
            }else if (trackModel != null){
                mediaPlayer.setDataSource(trackModel.mantic_real_url);
            }

            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            // 通过异步的方式装载媒体资源
            mediaPlayer.prepareAsync();
            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    // 装载完毕回调
                    totalMsec = mediaPlayer.getDuration();
                    Glog.i(TAG,"totalMsec: " + totalMsec);
                    totalTime.setText(TimeUtil.secondToTime(totalMsec));
                    progressSeek.setMax(totalMsec);

                }});
        } catch (IOException e) {
            Glog.i(TAG,"mediaPlay - > IOException:" + e);
            e.printStackTrace();
        }

        if (soundModal==null){ // 不能作为模板
            createButton.setVisibility(View.INVISIBLE);
        }

        IntentFilter filter = new IntentFilter("sound_completed");
        registerReceiver(completeReceiver,filter);
    }

    private BroadcastReceiver completeReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Glog.i(TAG,"Sound completed");
            playButton.setImageResource(R.drawable.edit_sound_play_button);
            if (playUrl != null){
                devicePlay(playUrl);
            }
        }
    };

    @Override
    public boolean onKeyLongPress(int keyCode, KeyEvent event) {
        return super.onKeyLongPress(keyCode, event);
    }

    @SuppressLint("SetTextI18n")
    private void initData() {
        device_detail_titlebar.setOnButtonClickListener(this);
        if (soundModal!= null){
//            titleView.setText(soundModal.mantic_album_name);
            contentView.setText(soundModal.mantic_album_more + "（" +soundModal.mantic_album_name+"）");
            GlideImgManager.glideLoader(this,soundModal.mantic_podcaster_avater,R.drawable.sound_people,R.drawable.sound_people,playerImage);
            playerText.setText(soundModal.mantic_podcaster_name);
            soundPlayDetail.setText(soundModal.mantic_describe);
            soundPlayDetail.setMovementMethod(ScrollingMovementMethod.getInstance());
            if (soundModal.mantic_album_name.equals(soundModal.mantic_album_more)){// 名字和专辑相同，只显示一个
                contentView.setVisibility(View.GONE);
            }
        }else if (trackModel!=null){
//            titleView.setText(trackModel.name);
            contentView.setText(trackModel.mantic_album_name);
            GlideImgManager.glideLoader(this,trackModel.mantic_podcaster_avater,R.drawable.sound_people,R.drawable.sound_people,playerImage);
            playerText.setText(trackModel.mantic_podcaster_name);
            soundPlayDetail.setText(trackModel.mantic_describe);
            soundPlayDetail.setMovementMethod(ScrollingMovementMethod.getInstance());
//            if (trackModel.name.equals(trackModel.mantic_album_name)){
//                contentView.setVisibility(View.GONE);
//            }
            totalMsec =  trackModel.length.intValue();
            totalTime.setText(TimeUtil.secondToTime(totalMsec));
            progressSeek.setMax(totalMsec);
        }
        volumnSeek.setProgress(SharePreferenceUtil.getDeviceVolume(this));


    }

    private void startCreateModel(){
        Intent intent = new Intent(this, EditSoundActivity.class);
        intent.putExtra("model",soundModal);
        startActivity(intent);
        finish();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.create_module:
                startCreateModel();
                break;
            case R.id.voice_play:
                if (trackModel != null){
                    if (devicePlay){
                        devicePause();
                    }else {
                        devicePlay(trackModel.mantic_real_url);
                    }
//                    if (mediaPlayer.isPlaying()){
//                        mediaPause();
//                    }else if (isPause){
//                        mediaResume();
//                    }else {
//                        mediaPlay(trackModel.mantic_real_url);
//                    }
                }else if (soundModal != null){
                    if (devicePlay){
                        devicePause();
                    }else {
                        devicePlay(soundModal.mantic_real_url);
                    }
//                    if (mediaPlayer.isPlaying()){
//                        mediaPause();
//                    }else if (isPause){
//                        mediaResume();
//                    }else {
//                        mediaPlay(soundModal.mantic_real_url);
//                    }
                }
                break;
            default:
                break;
        }
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
    protected void onDestroy() {
        super.onDestroy();
        destroyMediaPlayer();
        unregisterReceiver(completeReceiver);
        if (devicePlay){
            devicePause();
        }
    }

    private void updateStatus(){
        if (mediaPlayer != null && mediaPlayer.isPlaying()){
            playButton.setImageResource(R.drawable.edit_sound_pause_button);
        }else {
            playButton.setImageResource(R.drawable.edit_sound_play_button);
        }
    }

    private void updateCurrentTime(int msec){
        currentTime.setText(TimeUtil.secondToTime(msec));
    }

    private Runnable progressRun = new Runnable() {
        @Override
        public void run() {
            if (currentMsec >= totalMsec){
                currentMsec = totalMsec;
            }
            progressSeek.setProgress(currentMsec);
            updateCurrentTime(currentMsec);
            currentMsec += 1000;
            if (currentMsec >= totalMsec){
                progressSeek.setProgress(totalMsec);
                progressSeek.removeCallbacks(this);
            }else {
                progressSeek.postDelayed(this,1000);
            }
        }
    };

    private void mediaPlay(String url){
        mediaPlayer.start();
        isPause = false;
        updateStatus();
        mHandler.post(progressRun);
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                updateStatus();
                currentMsec = 0;
                mHandler.removeCallbacks(progressRun);
            }
        });

        mediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                mediaPlayer.reset();
                return false;
            }
        });
    }

    private void mediaPause(){
        if (mediaPlayer != null && mediaPlayer.isPlaying()){
            mediaPlayer.pause();
            isPause = true;
            updateStatus();
            progressSeek.removeCallbacks(progressRun);
        }
    }

    private void mediaResume(){
        if (mediaPlayer != null && isPause){
            mediaPlayer.start();
            isPause = false;
            updateStatus();
            progressSeek.post(progressRun);
        }
    }

    private void setVolume(float volume){
        if (mediaPlayer != null && volume >= 0){
            mediaPlayer.setVolume(volume,volume);
        }
    }

    private void seekTo(int msec){
        if (mediaPlayer != null){
            mediaPlayer.seekTo(msec);
        }
    }

    private void destroyMediaPlayer(){
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }


    /*device control*/
    public void controlVolume(final int process) {

        if (process == 0){
            Util.minimumVolumeDialog(mContext);
        }

        int value = AudioHelper.getVolumeValue(process);

        Glog.i("jys","controlVolume -- > value: " + value);

        Channel.sendPlayVolume(mContext, value, new Channel.ChannelDeviceControlListenerCallBack() {
            @Override
            public void onSuccess(HttpStatus code, ControlResult obj, PageInfo info) {
                Glog.i(TAG, "sendPlayVolume, name: " + obj.name + " context: " + obj.content + " code: " + obj.code);
                SharePreferenceUtil.setDeviceVolume(mContext, process);
            }

            @Override
            public void onFailed(HttpStatus code) {

            }

            @Override
            public void onError(IoTException error) {

            }
        });
    }

    private boolean devicePlay = false;
    private void devicePlay(String url){
        playUrl = url;
        final Channel.ChannelDeviceControlListener<ControlResult> listener = new Channel.ChannelDeviceControlListener<ControlResult>();
        Channel.ChannelDeviceControlListenerCallBack currCallBack = new Channel.ChannelDeviceControlListenerCallBack() {
            @Override
            public void onSuccess(HttpStatus code, ControlResult obj, PageInfo info) {
                devicePlay = true;
                playButton.setImageResource(R.drawable.edit_sound_pause_button);
                currentMsec = 0;
                mHandler.post(progressRun);
//                if (callBack != null) {
//                    callBack.onSuccess(code, obj, info);
//                    if (null != mContext) {
//                        ((ManticApplication)mContext.getApplicationContext()).setChannelStop(false);
//                    }
//
////                DataFactory dataFactory = DataFactory.getInstance();
////                ArrayList<DataFactory.ChannelControlListener> channelControlListeners = dataFactory.getChannelControlListeners();
////                for(int i = 0;i < channelControlListeners.size();i++){
////                    DataFactory.ChannelControlListener listener = channelControlListeners.get(i);
////                    int index = dataFactory.getBeingPlayList().indexOf(channel);
////                    listener.beginChannelControl(index);
////                    listener.afterChannelControl();
////                }
//                }
            }

            @Override
            public void onFailed(HttpStatus code) {
//                if (callBack != null) {
//                    callBack.onFailed(code);
//                }
            }

            @Override
            public void onError(IoTException error) {
//                if(callBack != null) {
//                    callBack.onError(error);
//                }
            }
        };
        listener.setChannelDeviceControlListenerCallBack(currCallBack);
        if (!TextUtils.isEmpty(url)){
            DeviceStateController.getInstance(mContext,SharePreferenceUtil.getDeviceId(mContext)).sendPlayMusic(url, listener);
        }else {
            listener.onFailed(HttpStatus.URL_NOT_FOUND);
            ToastUtils.showShortSafe(mContext.getString(R.string.play_address_empty));
        }
    }

    private void devicePause(){
        DeviceStateController.getInstance(mContext,SharePreferenceUtil.getDeviceId(mContext)).sendPauseMusic();
        playButton.setImageResource(R.drawable.edit_sound_play_button);
        devicePlay = false;
        progressSeek.removeCallbacks(progressRun);
    }

    private void deviceResume(){
        DeviceStateController.getInstance(mContext,SharePreferenceUtil.getDeviceId(mContext)).sendResumeMusic(null);
    }


}
