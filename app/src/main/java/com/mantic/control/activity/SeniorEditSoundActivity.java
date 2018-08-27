package com.mantic.control.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.sdk.android.media.upload.UploadListener;
import com.alibaba.sdk.android.media.upload.UploadOptions;
import com.alibaba.sdk.android.media.upload.UploadTask;
import com.alibaba.sdk.android.media.utils.FailReason;
import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechSynthesizer;
import com.iflytek.cloud.SynthesizerListener;
import com.mantic.control.ManticApplication;
import com.mantic.control.R;
import com.mantic.control.api.mopidy.MopidyRetrofit;
import com.mantic.control.api.mopidy.MopidyServiceApi;
import com.mantic.control.api.mopidy.MopidyTools;
import com.mantic.control.api.sound.MopidyRsSoundProductBean;
import com.mantic.control.api.sound.SoundTrack;
import com.mantic.control.ffmpeg.FFmpegCmd;
import com.mantic.control.ffmpeg.FFmpegUtil;
import com.mantic.control.utils.Glog;
import com.mantic.control.utils.SharePreferenceUtil;
import com.mantic.control.utils.Util;
import com.mantic.control.widget.TitleBar;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.mantic.control.fragment.EditSoundActivity.RESULT_CODE_COMPLETE;
import static com.mantic.control.fragment.MakeSoundFragment.SOUND_DIR;

/**
 * author: jayson
 * blog: http://blog.csdn.net/jia4525036
 * time: 2018/5/8.
 * desc:
 */
public class SeniorEditSoundActivity extends Activity implements TitleBar.OnButtonClickListener, View.OnClickListener {
    private static final String TAG = "SeniorEditSoundActivity";
    private final static String PATH = Environment.getExternalStorageDirectory().getPath();
    private String token = "UPLOAD_AK_TOP MjQ3NDAwMTE6ZXlKemFYcGxUR2x0YVhRaU9pQXdMQ0FpYVc1elpYSjBUMjVzZVNJNklEQXNJQ0p1WVcxbGMzQmhZMlVpT2lBaVlXUjJaWEowSWl3Z0ltVjRjR2x5WVhScGIyNGlPaUF0TVN3Z0ltUmxkR1ZqZEUxcGJXVWlPaUF4ZlE9PTozY2FmYzFlNDRjZGQ0Yzg2MGQzYzQzZGQ1MjMyNWM0NDc0OWZkOTk0";
    private final static int MSG_MIX_BEGIN = 11;
    private final static int MSG_MIX_FINISH = 12;
    private final static int MSG_CUT_BEGIN = 13;
    private final static int MSG_CUT_FINISH = 14;
    private final static int MSG_CONCAT_BEGIN = 15;
    private final static int MSG_CONCAT_FINISH = 16;
    private final static int MSG_VOLUME_CHANGE_FINISH = 17;
    private String voicerImage = "http://advert.image.alimmdn.com/avatar/xxxx.png";

    private TextView cycleCountView;
    private TextView cycleIntervalView;
    private TextView playDelayView;
    private TextView playDurationView;
    private SeekBar anchorSeek;
    private Switch cycleSwitch;
    private Switch lowerSwitch;
    private int textCount = 1;
    private int textInterval = 10;
    private int playDelay = 5;
    private int playDuration = 5;
    private int defaultVolume = 40;

    private ImageButton playButton;
    private MopidyServiceApi mpServiceApi;

    /** 合成相关 */
    private String bgFileName;
    private boolean mIsPlaying;
    private String mixFileName = "mix.aac";
    private String exportFileName = "mix.mp3";
    private String concatFile = "concat";
    private ProgressDialog mixProgressDialog;

    private MediaPlayer mediaPlayer;
    String srcFile;
    String appendFile;
    String mixFile;
    String exportFile;
    String concatAppendFile;
    private long bgDuration;
    private long soundDuration;
    private String curFile = SOUND_DIR + "tempCut";
    private String curVolumeFile=SOUND_DIR+"changeVolume";

    private String voicer;
    private String voiceText;
    private String voiceName;
    private String defaultPostName; // 默认上传名字
    private String uri = "advert:mywork:track:";

    //TTS祥光
    // 语音合成对象
    public static final String PREFER_NAME = "com.iflytek.setting";
    private SpeechSynthesizer mTts;
    private Toast mToast;
    // 引擎类型
    private String mEngineType = SpeechConstant.TYPE_CLOUD;
    //参数设置
    private SharedPreferences mSharedPreferences;

    private String tempSoundName = "test.wav";

    // 缓冲进度
    private int mPercentForBuffering = 0;
    // 播放进度
    private int mPercentForPlaying = 0;

    boolean pauseFlag = false;

    private boolean isVolumeChanged=false;
    private boolean isCutFirst=false;
    private float startProgress=0;
    private float volume=1f;
    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case MSG_CUT_BEGIN:
                    mixProgressDialog.show();
                    break;
                case MSG_MIX_BEGIN:
                    mixProgressDialog.show();
                    break;
                case MSG_CUT_FINISH:
                  //  mixProgressDialog.dismiss();
                    Glog.i(TAG,"isVolumeChanged "+isVolumeChanged);
                    if (isVolumeChanged){
                        changeVolumefile(curFile,volume);
                    }else {
                        mixFile(curFile,appendFile,mixFile);
                    }
                    break;
                case MSG_VOLUME_CHANGE_FINISH:
                    if (isCutFirst){
                        mixFile(curVolumeFile,appendFile,mixFile);
                    }
                    break;
                case MSG_MIX_FINISH:
                    mixProgressDialog.dismiss();
                    //开始上传
                    uploadFile(defaultPostName);
                    break;
                case MSG_CONCAT_BEGIN:
                    mixProgressDialog.show();
                    break;
                case MSG_CONCAT_FINISH:// 合并结束 需要对文件进行处理
                    mixProgressDialog.dismiss();
                    bgDuration += bgDuration; //时长加倍
                    srcFile = concatFile;// 目标文件替换
                default:
                    break;
            }
        }
    };

    @SuppressLint("ShowToast")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mpServiceApi = MopidyRetrofit.getInstance().create(MopidyServiceApi.class);
        loadGui();
        srcFile = getIntent().getStringExtra("bgFile");
        appendFile = getIntent().getStringExtra("appFile");
        bgDuration = getIntent().getLongExtra("srcDuration",0);
        soundDuration = getIntent().getLongExtra("soundDuration",0);
        mixFile = SOUND_DIR+mixFileName;
        voicer = getIntent().getStringExtra("voicer");
        voiceText = getIntent().getStringExtra("voiceText");
        voiceName = getIntent().getStringExtra("voiceName");
        Glog.i(TAG,"srcFile: " + srcFile);
        Glog.i(TAG,"appendFile: " + appendFile);
        Glog.i(TAG,"mixFile: " + mixFile);
        Glog.i(TAG,"bgDuration: " + bgDuration);
        Glog.i(TAG,"soundDuration: " + soundDuration);
        Glog.i(TAG,"voicer: " + voicer);
        Glog.i(TAG,"voiceText: " + voiceText);
        Glog.i(TAG,"voiceName: " + voiceName);
        soundDuration = soundDuration + 5000 ; // 默认延长5秒
        if (voiceText.length()>6){
            defaultPostName = voiceText.substring(0,6);
        }else {
            defaultPostName = voiceText;
        }
        mediaPlayer = new MediaPlayer();

        mTts = SpeechSynthesizer.createSynthesizer(this, mTtsInitListener);
        mSharedPreferences = getSharedPreferences(PREFER_NAME, MODE_PRIVATE);
        mToast = Toast.makeText(this,"",Toast.LENGTH_SHORT);
        ifNeedConcatAudio();

    }

    private void loadGui() {
        setContentView(R.layout.activity_edit_sound);
        TitleBar senior_sound_titlebar = (TitleBar) findViewById(R.id.senior_sound_titlebar);
        senior_sound_titlebar.setOnButtonClickListener(this);
        cycleCountView = (TextView) findViewById(R.id.text_count);
        cycleIntervalView = (TextView) findViewById(R.id.text_interval);
        playDelayView = (TextView) findViewById(R.id.text_delay);
        playDurationView = (TextView) findViewById(R.id.text_duration);
        cycleSwitch = (Switch) findViewById(R.id.text_cycle_switch);
        lowerSwitch = (Switch) findViewById(R.id.lower_bg_switch);
        playButton = (ImageButton) findViewById(R.id.senior_voice_play);
        anchorSeek = (SeekBar) findViewById(R.id.anchor_volume_seek_bar);
        findViewById(R.id.text_count_Decrease).setOnClickListener(this);
        findViewById(R.id.text_count_Increase).setOnClickListener(this);
        findViewById(R.id.text_interval_Decrease).setOnClickListener(this);
        findViewById(R.id.text_interval_Increase).setOnClickListener(this);
        findViewById(R.id.text_delay_Decrease).setOnClickListener(this);
        findViewById(R.id.text_delay_Increase).setOnClickListener(this);
        findViewById(R.id.text_duration_Decrease).setOnClickListener(this);
        findViewById(R.id.text_duration_Increase).setOnClickListener(this);
        findViewById(R.id.reset_view).setOnClickListener(this);
        findViewById(R.id.export_view).setOnClickListener(this);
        lowerSwitch.setOnClickListener(this);
        cycleSwitch.setOnClickListener(this);
        playButton.setOnClickListener(this);

        bgFileName = getIntent().getStringExtra("bgFile");
        if (bgFileName!=null){
            bgFileName = SOUND_DIR + bgFileName;
//            Glog.i(TAG,"bgFileName : " + bgFileName);
        }

        anchorSeek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                Glog.i(TAG,"onProgressChanged :" + progress );

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                Glog.i(TAG,"onStartTrackingTouch :" + seekBar.getProgress() );
                startProgress=(float) seekBar.getProgress();
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                Glog.i(TAG,"onStopTrackingTouch :" + (float)seekBar.getProgress()/100 );
                if (mediaPlayer !=null){
                    mediaPlayer.setVolume((float)seekBar.getProgress()/100,(float)seekBar.getProgress()/100);
                }
                isVolumeChanged=true;
                Glog.i(TAG,"isVolumeChanged :"+isVolumeChanged);
                float deltpercent=(seekBar.getProgress()-startProgress)/startProgress;
                Glog.i(TAG,"deltpercent:"+deltpercent);
                volume=1+deltpercent;
                Glog.i(TAG,"volume:"+volume);
            }
        });

        mixProgressDialog = new ProgressDialog(this);
        mixProgressDialog.setMessage(getString(R.string.mixing));
        mixProgressDialog.setCancelable(false);
    }

    private void ifNeedConcatAudio() {
        if (soundDuration > bgDuration){ //需要背景音需要拼接
            concatAppendFile = srcFile;
            concatFile();
        }
    }

    private void startMixFile(){
        if (soundDuration >= bgDuration){//第一阶段 如果声音较长，直接合并，后期会把背景音叠加
            mixFile(srcFile,appendFile,mixFile);
        }else {
            isCutFirst=true;
            cutFile(srcFile,0, (int) soundDuration/1000);
        }
    }

    private void mixFile(String srcFile,String appendFile,String mixFile) {
        Glog.i(TAG,"srcFile..." + srcFile);
        Glog.i(TAG,"appendFile..." + appendFile);
        Glog.i(TAG,"mixFile..." + mixFile);
        String[] commandLine = null;
        if (!new File(srcFile).exists()){
            showTip("背景文件不存在");
            return;
        }
        if (!new File(appendFile).exists()){
            showTip("声音文件不存在");
            return;
        }
        if (isVolumeChanged&&!isCutFirst){
            changeVolumefile(srcFile,volume);
            commandLine = FFmpegUtil.mixAudio(curVolumeFile, appendFile, mixFile);
        }else {
            commandLine=FFmpegUtil.mixAudio(srcFile,appendFile,mixFile);
        }

        executeFFmpegCmd(commandLine,1);
    }

    private void cutFile(String srcFile,int startTime,int duration) {
        String fileType = srcFile.split("\\.")[1];
        curFile = curFile + "." + fileType;
        Glog.i(TAG,"cutFile..." + duration + "    curFile: " + curFile);
        String[] commandLine = null;
        if (!new File(srcFile).exists()){
            showTip("背景文件不存在");
            return;
        }
        commandLine = FFmpegUtil.cutAudio(srcFile, startTime,duration, curFile);
        executeFFmpegCmd(commandLine,0);
    }
    private void changeVolumefile(String srcFile,float volume){
       /* String fileType = srcFile.split("\\.")[1];
        if (!curVolumeFile.contains(".")){
            curVolumeFile=curVolumeFile+"."+fileType;
        }*/
       if (!curVolumeFile.contains(".")){
           curVolumeFile=curVolumeFile+".aac";
       }
        Glog.i(TAG,    "curVolumeFile: " + curVolumeFile);
        String[] commandLine=null;
        if (!new File(srcFile).exists()){
            showTip("背景文件不存在");
            return;
        }
        commandLine=FFmpegUtil.volumeChange(srcFile,volume,curVolumeFile);

        Glog.i(TAG,Arrays.toString(commandLine));
        executeFFmpegCmd(commandLine,4);
    }

    private void transformAudio(String srcFile){
        String[] commandLine = null;
        if (!new File(srcFile).exists()){
            showTip("背景文件不存在");
            return;
        }

        exportFile = SOUND_DIR + exportFileName;
        Glog.i(TAG,"transformAudio: " + srcFile + "=== " + exportFile);
        commandLine = FFmpegUtil.transformAudio(srcFile,exportFile);
        executeFFmpegCmd(commandLine,2);

    }

    private void concatFile(){
        Glog.i(TAG,"concatAppendFile :" + concatAppendFile);
        String[] commandLine = null;
        String fileType = srcFile.split("\\.")[1];
        concatFile = SOUND_DIR + concatFile + "." + fileType;
        Glog.i(TAG,"concatFile :" + concatFile);
        commandLine = FFmpegUtil.concatAudio(srcFile,concatAppendFile,concatFile);
        executeFFmpegCmd(commandLine,3);
    }

    /**
     * 执行ffmpeg命令行
     * @param commandLine commandLine
     */
    private void executeFFmpegCmd(final String[] commandLine,final int type){
        if(commandLine == null){
            return;
        }
        FFmpegCmd.execute(commandLine, new FFmpegCmd.OnHandleListener() {
            @Override
            public void onBegin() {
                if (type == 1){// 混合
                    Glog.i(TAG, "handle mix audio onBegin...");
                    mHandler.obtainMessage(MSG_MIX_BEGIN).sendToTarget();
                }else if (type == 0){ //裁剪
                    Glog.i(TAG, "handle cut audio onBegin...");
                    mHandler.obtainMessage(MSG_CUT_BEGIN).sendToTarget();
                }else if (type == 3){ //合并
                    Glog.i(TAG, "handle concat audio onBegin...");
                    mHandler.obtainMessage(MSG_CONCAT_BEGIN).sendToTarget();
                }else if (type==4){
                    Glog.i(TAG,"handle change audio onBegin...");
                }
            }

            @Override
            public void onEnd(int result) {
                if (type == 1) {// 混合
                    Glog.i(TAG, "handle mix audio onEnd...");
                    mHandler.obtainMessage(MSG_MIX_FINISH).sendToTarget();
                }else if (type == 0){ //裁剪
                    Glog.i(TAG, "handle cut audio onEnd..."+result);
                    mHandler.obtainMessage(MSG_CUT_FINISH).sendToTarget();
                }else if (type == 3){//合并
                    Glog.i(TAG, "handle concat audio onEnd...");
                    mHandler.obtainMessage(MSG_CONCAT_FINISH).sendToTarget();
                }else if (type==4){
                    Glog.i(TAG,"handle change audio onEnd...");
                    mHandler.obtainMessage(MSG_VOLUME_CHANGE_FINISH).sendToTarget();
                }
            }
        });
    }

    private void mediaPlay(){
        if (srcFile == null){
            return;
        }
        try {
            mediaPlayer.setDataSource(srcFile);
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            // 通过异步的方式装载媒体资源
            mediaPlayer.prepareAsync();
            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    // 装载完毕回调
                    mIsPlaying = true;
                    mediaPlayer.setVolume(0.4f,0.4f);
                    mediaPlayer.start();
                    playButton.setImageResource(R.drawable.edit_sound_pause_button);
                }});
        } catch (IOException e) {
            Glog.i(TAG,"mediaPlay - > IOException:" + e);
            e.printStackTrace();
        }
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                Glog.i(TAG,"mediaPlayer -- > onCompletion");
                mIsPaused = true;
                playButton.setImageResource(R.drawable.edit_sound_play_button);
            }
        });
    }

    private boolean mIsPaused = false;
    private void mediaPause(){
        Glog.i(TAG,"mediaPause.................");
        if (mIsPaused){
            mediaPlayer.start();
            mIsPaused = false;
        }else {
            mediaPlayer.pause();
            mIsPaused = true;
        }

        updateDisplay();
    }


    private void uploadFile(final String name){
        mixProgressDialog.setMessage("作品正在上传中，请稍等...");
        mixProgressDialog.show();
        long time = System.currentTimeMillis();
        uri = uri + time;
        final String fileName = SharePreferenceUtil.getDeviceId(this)+"_" + time+".aac"; // 合成文件一律mp3格式处理
        final UploadOptions options = new UploadOptions.Builder()
                .tag(String.valueOf(SystemClock.elapsedRealtime()))
                .dir("/Upload/Product")
                .aliases(fileName).build();

        File file = new File(mixFile);
        if (file.exists()){
            ManticApplication.mediaService.upload(file, options, new UploadListener() {
                @Override
                public void onUploading(UploadTask uploadTask) {
                    Glog.i(TAG,"onUploading ... ");
                }

                @Override
                public void onUploadFailed(UploadTask uploadTask, FailReason failReason) {
                    Glog.i(TAG,"onUploadFailed ... ");
                }

                @Override
                public void onUploadComplete(UploadTask uploadTask) {
                    String url = uploadTask.getResult().url;
                    Glog.i(TAG,"onUploadComplete ... "+ url);
                    postFile(name,url);
                }

                @Override
                public void onUploadCancelled(UploadTask uploadTask) {
                    Glog.i(TAG,"onUploadCancelled ... ");
                }
            },token);
        }
    }

    private void postFile(String name,String url){
        SoundTrack soundTrack = new SoundTrack();
        soundTrack.model = "Track";
        soundTrack.name = name;
        soundTrack.uri = uri;
        soundTrack.mantic_real_url = url;
        soundTrack.mantic_image = "http://p1.music.126.net/h3mB5Pz3cH7baa7zMyLfpg==/18559756278791740.jpg";
        soundTrack.length = (int) soundDuration;
        soundTrack.mantic_podcaster_avater = voicerImage.replace("xxxx",voicer);
        soundTrack.mantic_podcaster_key = voicer;
        soundTrack.mantic_podcaster_name = voiceName;
        soundTrack.mantic_album_name = name;
        soundTrack.mantic_describe = voiceText;
        RequestBody body = Util.createAddSoundRqBean(soundTrack,this);
        Call<MopidyRsSoundProductBean> call = mpServiceApi.postMopidyaddSound(MopidyTools.getHeaders(),body);
        call.enqueue(new Callback<MopidyRsSoundProductBean>() {
            @Override
            public void onResponse(Call<MopidyRsSoundProductBean> call, Response<MopidyRsSoundProductBean> response) {
                MopidyRsSoundProductBean bean = response.body();
                if (bean != null){
                    Glog.i(TAG,"MopidyRsSoundProductBean : " + bean);
                    if (bean.result != null){
                        Glog.i(TAG,"添加成功 : ");
                        sendBroadcast(new Intent("product_add_success"));
                        showTip("上传成功");
                        mixProgressDialog.dismiss();
                        completeProduct();
                    }
                }

            }

            @Override
            public void onFailure(Call<MopidyRsSoundProductBean> call, Throwable t) {
                Glog.i(TAG,"onFailure...");
            }
        });
    }

    private void completeProduct(){
        setResult(RESULT_CODE_COMPLETE);
        finish();
    }


    private void mediaResume(){

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void updateDisplay(){
        if (mIsPaused){
            playButton.setImageResource(R.drawable.edit_sound_play_button);
        }else {
            playButton.setImageResource(R.drawable.edit_sound_pause_button);
        }
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.text_count_Decrease:
                setTextCount(false);
                break;
            case R.id.text_count_Increase:
                setTextCount(true);
                break;
            case R.id.text_interval_Decrease:
                setTextInterval(false);
                break;
            case R.id.text_interval_Increase:
                setTextInterval(true);
                break;
            case R.id.text_delay_Decrease:
                setTextDelay(false);
                break;
            case R.id.text_delay_Increase:
                setTextDelay(true);
                break;
            case R.id.text_duration_Decrease:
                setTextDuration(false);
                break;
            case R.id.text_duration_Increase:
                setTextDuration(true);
                break;
            case R.id.reset_view:
                resetSettingData();
                break;
            case R.id.text_cycle_switch:
                break;
            case R.id.lower_bg_switch:
                break;
            case R.id.senior_voice_play:
                /* 背景音播放*/
                if (!mIsPlaying){
                    mediaPlay();
                }else {
                    mediaPause();
                }
                /*语音播放*/
                if (mPercentForPlaying == 0){
                    ttsPlay();
                }else {
                    if (!pauseFlag){
                        pauseSpeaking();
                    }else {
                        resumeSpeaking();
                    }
                }
                break;
            case R.id.export_view:
                exportFile();
                break;
            default:
                break;
        }
    }

    @SuppressLint("SetTextI18n")
    private void setTextCount(boolean add){
        if (add){
            if (textCount<= 59){
                textCount ++;
                cycleCountView.setText(textCount + "次");
            }
        }else {
            if (textCount>=2){
                textCount --;
                cycleCountView.setText(textCount + "次");
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private void setTextInterval(boolean add){
        if (add){
            if (textInterval<= 59){
                textInterval ++;
                cycleIntervalView.setText(textInterval + "秒");
            }
        }else {
            if (textInterval>=2){
                textInterval --;
                cycleIntervalView.setText(textInterval + "秒");
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private void setTextDelay(boolean add){
        if (add){
            if (playDelay<= 59){
                playDelay ++;
                playDelayView.setText(playDelay + "秒");
            }
        }else {
            if (playDelay>=2){
                playDelay --;
                playDelayView.setText(playDelay + "秒");
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private void setTextDuration(boolean add){
        if (add){
            if (playDuration < 20){
                playDuration +=5;
                playDurationView.setText(playDuration + "秒");
            }
        }else {
            if (playDuration > 5){
                playDuration -=5;
                playDurationView.setText(playDuration + "秒");
            }
        }
    }

    private void exportFile(){
        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        LayoutInflater inflater = LayoutInflater.from(this);
        View v = inflater.inflate(R.layout.save_products_dialog,null);
        final EditText saveName = (EditText) v.findViewById(R.id.dialog_save_name);
        saveName.setText(defaultPostName);
        saveName.setSelection(defaultPostName.length());
        Button cancelButton = (Button) v.findViewById(R.id.save_cancel_button);
        Button sureButton = (Button) v.findViewById(R.id.save_sure_button);
        final AlertDialog dialog =  alertDialog.create();
        dialog.setView(new EditText(this));
        dialog.show();
        dialog.getWindow().setContentView(v);

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        sureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                defaultPostName = saveName.getText().toString();
                if (defaultPostName.isEmpty() ||saveName.getText().toString().equals(" ")){
                    showTip("名字不能为空");
                }else {
                    Glog.i(TAG,"startMixFile");
                    startMixFile();
                }
                dialog.dismiss();
            }
        });

    }

    @SuppressLint("SetTextI18n")
    private void resetSettingData(){
        textCount = 1;
        textInterval = 10;
        playDelay = 5;
        playDuration = 5;
        cycleCountView.setText(textCount + "次");
        cycleIntervalView.setText(textInterval + "秒");
        playDelayView.setText(playDelay + "秒");
        playDurationView.setText(playDuration + "秒");
        anchorSeek.setProgress(defaultVolume);
        mediaPlayer.setVolume(0.4f,0.4f);
        lowerSwitch.setChecked(true);
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
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }

        if( null != mTts ){
            mTts.stopSpeaking();
            // 退出时释放连接
            mTts.destroy();
        }
        super.onDestroy();
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

    private void showTip(final String str) {
        mToast.setText(str);
        mToast.show();
    }


    private void ttsPlay(){
        setParam();
        //读出来 默认路径在 Param 里
        if (voiceText.length() == 0 ){
            showTip("文字不能为空！");
            return;
        }
        int code = mTts.startSpeaking(voiceText,mTtsListener);
        //只保存
        if (code != ErrorCode.SUCCESS) {
            showTip("语音合成失败,错误码: " + code);
        }
    }

    private void pauseSpeaking(){
        Glog.i(TAG,"pauseSpeaking...");
        mTts.pauseSpeaking();
    }

    private void resumeSpeaking(){
        Glog.i(TAG,"resumeSpeaking...");
        mTts.resumeSpeaking();
    }

    /**
     * 合成回调监听。
     */
    private SynthesizerListener mTtsListener = new SynthesizerListener() {

        @Override
        public void onSpeakBegin() {
//            showTip("开始播放");
            pauseFlag = false;
        }

        @Override
        public void onSpeakPaused() {
//            showTip("暂停播放");
            pauseFlag = true;

        }

        @Override
        public void onSpeakResumed() {
//            showTip("继续播放");
            pauseFlag = false;

        }

        @Override
        public void onBufferProgress(int percent, int beginPos, int endPos,
                                     String info) {
            // 合成进度
            Glog.i(TAG,"onBufferProgress........." + percent);
        }

        @Override
        public void onSpeakProgress(int percent, int beginPos, int endPos) {
            // 播放进度
//            Glog.i(TAG,"onSpeakProgress........."+percent);
            mPercentForPlaying = percent;
//            showTip(String.format(getString(R.string.tts_toast_format),
//                    mPercentForBuffering, mPercentForPlaying));
            if (mPercentForPlaying>0 && mPercentForPlaying!=100){
//                voicePlay.setImageResource(R.drawable.edit_sound_pause_button);
            }

        }

        @Override
        public void onCompleted(SpeechError error) {
            if (error == null) {
//                showTip("播放完成");
                mPercentForPlaying = 0;
//                voicePlay.setImageResource(R.drawable.edit_sound_play_button);
                if (mediaPlayer.isPlaying()){
                    mHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mediaPause();
                        }
                    },playDuration*1000);
                }
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

    /**
     * 参数设置
     * @return
     */
    private void setParam(){
        // 清空参数
        mTts.setParameter(SpeechConstant.PARAMS, null);
        // 根据合成引擎设置相应参数
        if(mEngineType.equals(SpeechConstant.TYPE_CLOUD)) {
            mTts.setParameter(SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_CLOUD);
            // 设置在线合成发音人
            mTts.setParameter(SpeechConstant.VOICE_NAME, voicer);
            //设置合成语速
            mTts.setParameter(SpeechConstant.SPEED, mSharedPreferences.getString("speed_preference", "50"));
            //设置合成音调
            mTts.setParameter(SpeechConstant.PITCH, mSharedPreferences.getString("pitch_preference", "50"));
            //设置合成音量
            mTts.setParameter(SpeechConstant.VOLUME, mSharedPreferences.getString("volume_preference", "90"));
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
        mTts.setParameter(SpeechConstant.TTS_AUDIO_PATH, tempSoundName);
    }

}
