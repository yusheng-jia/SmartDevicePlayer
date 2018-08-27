package com.mantic.control.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Rect;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechSynthesizer;
import com.iflytek.cloud.SynthesizerListener;
import com.mantic.antservice.util.ToastUtils;
import com.mantic.control.R;
import com.mantic.control.activity.AnchorSelActivity;
import com.mantic.control.activity.SeniorEditSoundActivity;
import com.mantic.control.activity.SoundBackgroundActivity;
import com.mantic.control.api.sound.MopidyRsSoundModalBean;
import com.mantic.control.ffmpeg.FFmpegCmd;
import com.mantic.control.ffmpeg.FFmpegUtil;
import com.mantic.control.utils.DownloadManagerUtils;
import com.mantic.control.utils.GlideImgManager;
import com.mantic.control.utils.Glog;
import com.mantic.control.widget.RoundProgressBarWidthNumber;

import java.io.File;
import java.io.IOException;

import me.yokeyword.swipebackfragment.SwipeBackActivity;

import static com.mantic.control.fragment.MakeSoundFragment.SOUND_DIR;

/**
 * author: jayson
 * blog: http://blog.csdn.net/jia4525036
 * time: 2018/4/23.
 * desc:
 */

public class EditSoundActivity extends SwipeBackActivity implements View.OnClickListener, DownloadManagerUtils.OnDownloadCompleted {
    private static final String TAG = EditSoundActivity.class.getSimpleName();
    private static final int AUTHOR_SELECT = 0;
    private static final int BACKGROUND_SELECT = 1;
    private static final int MSG_PROGRESS_UPDATE = 0x110;
    public static final int REQUEST_CODE_IFCOMPLETE = 100;
    public static final int RESULT_CODE_COMPLETE = 102;
    public static final String PREFER_NAME = "com.iflytek.setting";
    private String ttsFileName = "current.wav";
    private String tempFileName = "test.wav";
    private String curFile = SOUND_DIR + "tempBgCut";
    private String tempMixFile = SOUND_DIR + "tempmix.aac";

    private String bgMusic;
    private String bgFile;
    private String voicerPeople;
    // 语音合成对象
    private SpeechSynthesizer mTts;
    // 引擎类型
    private String mEngineType = SpeechConstant.TYPE_CLOUD;
    //参数设置
    private SharedPreferences mSharedPreferences;
    // 默认发音人
    private String voicer = "xiaoyan";

    private String authorName = "小燕";

    private String voicerImage = "http://advert.image.alimmdn.com/avatar/xxxx.png";

    // 缓冲进度
    private int mPercentForBuffering = 0;
    // 播放进度
    private int mPercentForPlaying = 0;

    private LinearLayout backLayout;
    private EditText voiceText;
    private ImageButton voicePlay;
    private TextView anchorText;
    private LinearLayout soundBackgroundView;
    private TextView soundBackgroundText;
    private Toast mToast;
    private TextView nextText;
    private TextView editCount;
    private ImageView anchorIcon;
    private LinearLayout anchorView;

    private Context mContext;

    boolean pauseFlag = false;

    //软键盘设置相关参数
    RelativeLayout rootView;
    int usableHeightPrevious;
    View mChildOfContent;

    //进度条相关
    LinearLayout processView ;
    private RoundProgressBarWidthNumber roundProgressBar;
    MopidyRsSoundModalBean.Result soundModal;

    MediaPlayer mediaPlayer;
    long bgDuration;
    DownloadManagerUtils downloadManagerUtils;
    private ProgressDialog downloadProgress;

    @SuppressLint("ShowToast")
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_edit_sound);
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            StatusBarUtil.setColor(SeniorEditSoundActivity.this, Color.parseColor("#f9f9fa"), 0);
//            StatusBarHelper.statusBarLightMode(this);
//        }
        mContext = this;
        rootView = (RelativeLayout) findViewById(R.id.edit_sound_root_view);
        backLayout = (LinearLayout) findViewById(R.id.edit_sound_title_back);
        nextText = (TextView) findViewById(R.id.edit_sound_next);
        soundBackgroundText = (TextView) findViewById(R.id.sound_background_text);
        voicePlay = (ImageButton) findViewById(R.id.voice_play);
        voiceText = (EditText)findViewById(R.id.sound_text);
        anchorText = (TextView) findViewById(R.id.anchor_text);
        anchorView = (LinearLayout) findViewById(R.id.anchor_view);
        editCount = (TextView)findViewById(R.id.edit_count);
        processView = (LinearLayout) findViewById(R.id.process_bar_view);
        anchorIcon = (ImageView) findViewById(R.id.anchor_icno);
        soundBackgroundView = (LinearLayout) findViewById(R.id.sound_background_view);
        roundProgressBar = (RoundProgressBarWidthNumber) findViewById(R.id.process_roundProgressBar);
        processView.setVisibility(View.GONE);
        String countString = String.format(getString(R.string.sound_input_text),0);
        editCount.setText(countString);
        soundModal = (MopidyRsSoundModalBean.Result) getIntent().getSerializableExtra("model");
        Glog.i(TAG,"soundModal: " + soundModal);
        Glog.i(TAG,"bgMusic: " + bgMusic);
        if (soundModal != null){
            bgMusic = soundModal.name;
            anchorText.setText(soundModal.mantic_podcaster_name);
            soundBackgroundText.setText(bgMusic);
            bgFile = soundModal.mantic_background_url;
            String text = soundModal.mantic_describe.replace("\n","");
            voiceText.setText(text);
            voiceText.setSelection(text.length());
            String soundCount = String.format(getString(R.string.sound_input_text),text.length());
            editCount.setText(soundCount);
            GlideImgManager.glideCircle(this,soundModal.mantic_podcaster_avater,R.drawable.sound_people,R.drawable.sound_people,anchorIcon);
            Glog.i(TAG,"bgFile: " + bgFile);
        }
        voiceText.addTextChangedListener(textWatcher);
        backLayout.setOnClickListener(this);
        nextText.setOnClickListener(this);
        voicePlay.setOnClickListener(this);
        anchorView.setOnClickListener(this);
        soundBackgroundView.setOnClickListener(this);
        // 初始化合成对象
        mContext = this;
        mTts = SpeechSynthesizer.createSynthesizer(this, mTtsInitListener);
        mSharedPreferences = getSharedPreferences(PREFER_NAME, MODE_PRIVATE);
        Glog.i(TAG,"onCreate..." + mTts);
        mToast = Toast.makeText(this,"",Toast.LENGTH_SHORT);

        mChildOfContent = rootView.getChildAt(0);
        mChildOfContent.getViewTreeObserver().addOnGlobalLayoutListener(onGlobalLayoutListener);

        registerDownReceiver();
        if (bgFile != null){
            getFileNameFromUrl(bgFile);
        }

        downloadProgress = new ProgressDialog(this);
        downloadProgress.setMessage(getString(R.string.music_bg_downing));
        downloadProgress.setCancelable(false);

        mediaPlayer = new MediaPlayer();

    }

    ViewTreeObserver.OnGlobalLayoutListener onGlobalLayoutListener = new ViewTreeObserver.OnGlobalLayoutListener() {
        @Override
        public void onGlobalLayout() {
            possiblyResizeChildOfContent();
        }
    };

    TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            String countString = String.format(getString(R.string.sound_input_text),s.length());
            editCount.setText(countString);
        }
    };

    private long computeMusicDuration(String fileName){
        Glog.i(TAG,"computeBgMusic...");
        if (fileName != null){
            long duration = 0;
            try {
                mediaPlayer.reset();
                mediaPlayer = new MediaPlayer();
                mediaPlayer.setDataSource(SOUND_DIR+fileName);
                mediaPlayer.prepare();
                duration = mediaPlayer.getDuration();
                Glog.i(TAG,"duration: " + duration);
                return duration;

            } catch (IOException e) {
                e.printStackTrace();
                return  duration;
            }
        }else {
            return 0;
        }
    }

    private void registerDownReceiver() {
        downloadManagerUtils = new DownloadManagerUtils(this);
        downloadManagerUtils.registerReceiver(this);
    }

    private void downLoadMusic(String url,String name){
        downloadManagerUtils.download(url,name,"音乐下载","背景音乐下载");
        if (downloadProgress != null){
            downloadProgress.show();
        }
//        showTip("正在下载背景音乐！");
    }

    private void possiblyResizeChildOfContent(){
        int usableHeightNow = computeUsableHeight();
        if (usableHeightNow != usableHeightPrevious) {
            int usableHeightSansKeyboard = mChildOfContent.getRootView().getHeight();
            int heightDifference = usableHeightSansKeyboard - usableHeightNow;
            if (heightDifference > (usableHeightSansKeyboard / 4)) {
                // 键盘弹出
                nextText.setText(R.string.complete);
            } else {
                // 键盘收起
                nextText.setText(R.string.next);
            }
            mChildOfContent.requestLayout();
            usableHeightPrevious = usableHeightNow;
        }
    }

    private int computeUsableHeight() {
        Rect r = new Rect();
        mChildOfContent.getWindowVisibleDisplayFrame(r);
        return (r.bottom - r.top);
    }


    private void ttsPlay(){
        setParam();
        String text = voiceText.getText().toString();
        //读出来 默认路径在 Param 里
        if (text.length() == 0 ){
            ToastUtils.showShort("请输入文字");
            return;
        }
        int code = mTts.startSpeaking(text,mTtsListener);
        //只保存
        if (code != ErrorCode.SUCCESS) {
            showTip("语音合成失败,错误码: " + code);
        }
    }

    //生成语音文件
    private void saveTtsFile(){
        setParam();
        String text = voiceText.getText().toString();
        //读出来 默认路径在 Param 里
        if (text.length() == 0 ){
            ToastUtils.showShort("请输入文字");
            return;
        }
        String path = SOUND_DIR + ttsFileName;
        int code = mTts.synthesizeToUri(text, path, mTtsListener);
        if (code != ErrorCode.SUCCESS) {
            showTip("语音合成失败,错误码: " + code);
        }
    }

    private void mediaPlay(String fileName){
        String text = voiceText.getText().toString();
        if (text.length() == 0 ){
            ToastUtils.showShort("请输入文字");
            return;
        }
        Glog.i(TAG,"mediaPlay...");
        try {
            mediaPlayer.reset();
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setDataSource(fileName);
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            // 通过异步的方式装载媒体资源
            mediaPlayer.prepareAsync();
            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    // 装载完毕回调
                    mediaPlayer.start();
                    voicePlay.setImageResource(R.drawable.edit_sound_pause_button);
//                    bgMusicAdapter.updatePlayState(2,currItem);
//                    bgMusicAdapter.notifyItemChanged(currItem);
                }});
        } catch (IOException e) {
            Glog.i(TAG,"mediaPlay - > IOException:" + e);
            e.printStackTrace();
        }

        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                voicePlay.setImageResource(R.drawable.edit_sound_play_button);
//                bgMusicAdapter.updatePlayState(1,currItem);
//                bgMusicAdapter.notifyItemChanged(currItem);
            }
        });
    }


    private void mediaPause(){
        Glog.i(TAG,"mediaPause.................");
        if (mediaPlayer.isPlaying()){
            mediaPlayer.pause();
            voicePlay.setImageResource(R.drawable.edit_sound_play_button);
        }
    }

    private void mediaResume(){
        Glog.i(TAG,"mediaPause.................");
        mediaPlayer.start();
        voicePlay.setImageResource(R.drawable.edit_sound_pause_button);
    }

    private void pauseSpeaking(){
        Glog.i(TAG,"pauseSpeaking...");
        mTts.pauseSpeaking();
    }

    private void resumeSpeaking(){
        Glog.i(TAG,"resumeSpeaking...");
        mTts.resumeSpeaking();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.edit_sound_title_back:
                hideSoftKeyboard(voiceText,this);
                finish();
                break;
            case R.id.edit_sound_next:
                    if (nextText.getText().toString().equals(getString(R.string.complete))){
                        hideSoftKeyboard(voiceText,this);
                    }else {
                        Glog.i(TAG, " 启动高级界面。。。。。" );
                        showProcessDialog();
                    }
                break;

            case R.id.voice_play: // 播放前需要合成
                Glog.i(TAG,"play -> percent: " + mPercentForPlaying);
//                if (bgMusic == null){ //没有背景音乐 直接播放声音
                    if (mPercentForPlaying == 0){
                         ttsPlay();
                    }else {
                        if (!pauseFlag){
                            pauseSpeaking();
                        }else {
                            resumeSpeaking();
                        }
                    }
//                }
                if (bgMusic != null){
                    if (mediaPlayer.isPlaying()){
                        mediaPause();
                    }else {
                        mediaPlay(bgFile);
                    }
                }

//                    if (mediaPlayed){
//                        if (mediaPlayer.isPlaying()){
//                            mediaPause();
//                        }else {
//                            mediaResume();
//                        }
//                    }else {
//                        needMix = true;
//                        saveTtsFile();
//                    }


                break;
            case R.id.anchor_view:
                startAnchor();
                break;
            case R.id.sound_background_view:
                startSoundBackground();
                break;
            default:
                break;

        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 10){
            if (requestCode == AUTHOR_SELECT){
                authorName = data.getStringExtra("author_name");
                voicer = data.getStringExtra("author_value");
                anchorText.setText(authorName);
                GlideImgManager.glideCircle(this,data.getStringExtra("author_icon"),R.drawable.sound_people,R.drawable.sound_people,anchorIcon);
                updateVoicerImage(voicer);
            }else if(requestCode == BACKGROUND_SELECT){
                registerDownReceiver();
                String backgroundName = data.getStringExtra("background_name");
                bgFile = data.getStringExtra("music_file_name");
                bgMusic = backgroundName;
                soundBackgroundText.setText(backgroundName);
                bgDuration = computeMusicDuration(getFileNameFromUrl(bgFile));
                Glog.i(TAG,"bgFile: " + bgFile + "    backgroundName：" + backgroundName);
            }
        }else if(requestCode == REQUEST_CODE_IFCOMPLETE){
            if (resultCode == RESULT_CODE_COMPLETE){
                finish();
            }
        }
    }

    private void updateVoicerImage(String voicer) {
        voicerImage = voicerImage.replace("xxxx",voicer);
    }

    private void startAnchor(){
        mPercentForPlaying = 0;
        pauseSpeaking();
        mediaPause();
        Intent intent = new Intent(this, AnchorSelActivity.class);
        startActivityForResult(intent,AUTHOR_SELECT);
    }

    private void startSoundBackground(){
        mPercentForPlaying = 0;
        pauseSpeaking();
        mediaPause();
        if (downloadManagerUtils.hasRegister()){
            downloadManagerUtils.unregisterReceiver();
        }
        Intent intent = new Intent(this, SoundBackgroundActivity.class);
        startActivityForResult(intent,BACKGROUND_SELECT);
    }

    private String getFileNameFromUrl(String url){
        String fileName = null;
        String fileTemp[] = url.split("[?]")[0].split("/");
        if (fileTemp.length>1){
            fileName = fileTemp[fileTemp.length-1];
        }
        File file = new File(SOUND_DIR+fileName);
        if (!file.exists()){
//            showTip("正在下载背景音乐！");
            downLoadMusic(url,fileName);
            return null;
        }
        return fileName;
    }

    private void startSeniorActivity(){
        Thread ttsThread = new Thread(new Runnable() {
            @Override
            public void run() {
                long soundDuration = computeMusicDuration(ttsFileName);
                Glog.i(TAG,"soundDuration: " + (int) (soundDuration/1000) + "===bgDuration:" + bgDuration);
//                if (bgDuration > soundDuration){
//                    mixAudio();
                    String srcFile = SOUND_DIR+getFileNameFromUrl(bgFile);
                    String appendFile = SOUND_DIR+ttsFileName;
                    Intent intent = new Intent(mContext, SeniorEditSoundActivity.class);
                    intent.putExtra("bgFile",srcFile);
                    intent.putExtra("appFile",appendFile);
                    intent.putExtra("srcDuration",bgDuration);
                    intent.putExtra("soundDuration",soundDuration);
                    intent.putExtra("voiceText",voiceText.getText().toString());
                    intent.putExtra("voicer",voicer);
                    intent.putExtra("voiceName",authorName);
                    intent.putExtra("voiceImage",voicerImage);
                    startActivityForResult(intent,REQUEST_CODE_IFCOMPLETE);
//                }

            }
        });
        mHandler.postDelayed(ttsThread,300);

//
    }

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what){
                case MSG_PROGRESS_UPDATE:
                    int roundProgress = roundProgressBar.getProgress();
                    roundProgressBar.setProgress(++roundProgress);
                    if (roundProgress >= 100) {
                        mHandler.removeMessages(MSG_PROGRESS_UPDATE);
                        processView.setVisibility(View.GONE);
                        startSeniorActivity();
                    }else {
                        mHandler.sendEmptyMessageDelayed(MSG_PROGRESS_UPDATE, 100);
                    }
                    break;
                default:
                    break;
            }

        };
    };

    private void showProcessDialog(){
        if (voiceText.getText().length() < 1){
            showTip("请输入合成文字！");
            return;
        }
        if (bgMusic == null){
            showTip("请选择背景音！");
            return;
        }
        setViewEnable(false);
        saveTtsFile();
        processView.setVisibility(View.VISIBLE);
        roundProgressBar.setProgress(1);
        mHandler.removeMessages(MSG_PROGRESS_UPDATE);
        mHandler.sendEmptyMessage(MSG_PROGRESS_UPDATE);

    }

    private void setViewEnable(boolean enable){
        voiceText.setEnabled(enable);
        backLayout.setEnabled(enable);
        nextText.setEnabled(enable);
        voicePlay.setEnabled(enable);
        anchorView.setEnabled(enable);
        soundBackgroundView.setEnabled(enable);
    }

    public void hideSoftKeyboard(EditText editText, Context context) {
        if (editText != null && context != null) {
            InputMethodManager imm = (InputMethodManager) context
                    .getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(editText.getApplicationWindowToken(), 0);
        }
    }

    public void showSoftKeyboard(EditText editText, Context context) {
        if (editText != null && context != null) {
            InputMethodManager imm = (InputMethodManager) context
                    .getSystemService(Activity.INPUT_METHOD_SERVICE);
            imm.showSoftInput(editText, 0);
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if( null != mTts ){
            mTts.stopSpeaking();
            // 退出时释放连接
            mTts.destroy();
        }
        if (mediaPlayer != null){
            mediaPause();
            mediaPlayer = null;
        }
        if (downloadManagerUtils.hasRegister()){
            downloadManagerUtils.unregisterReceiver();
        }
        mChildOfContent.getViewTreeObserver().removeOnGlobalLayoutListener(onGlobalLayoutListener);
    }

    @Override
    protected void onResume() {
        super.onResume();
        testArray();
        setViewEnable(true);
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
            voicePlay.setImageResource(R.drawable.edit_sound_play_button);

        }

        @Override
        public void onSpeakResumed() {
//            showTip("继续播放");
            pauseFlag = false;
            voicePlay.setImageResource(R.drawable.edit_sound_pause_button);

        }

        @Override
        public void onBufferProgress(int percent, int beginPos, int endPos,
                                     String info) {
            // 合成进度
            Glog.i(TAG,"onBufferProgress........." + percent);
            roundProgressBar.setProgress(percent);
//            mPercentForBuffering = percent;
//            showTip(String.format(getString(R.string.tts_toast_format),
//                    mPercentForBuffering, mPercentForPlaying));

        }

        @Override
        public void onSpeakProgress(int percent, int beginPos, int endPos) {
            // 播放进度
//            Glog.i(TAG,"onSpeakProgress........."+percent);
            mPercentForPlaying = percent;
//            showTip(String.format(getString(R.string.tts_toast_format),
//                    mPercentForBuffering, mPercentForPlaying));
            if (mPercentForPlaying>0 && mPercentForPlaying!=100){
                voicePlay.setImageResource(R.drawable.edit_sound_pause_button);
            }

        }

        @Override
        public void onCompleted(SpeechError error) {
            if (error == null) {
//                showTip("播放完成");
                mPercentForPlaying = 0;
                voicePlay.setImageResource(R.drawable.edit_sound_play_button);
                if (mediaPlayer.isPlaying()){
                    mediaPause();
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

    private void showDownloadProgress(final String str) {
        mToast.setText(str);
        mToast.show();
    }

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
        mTts.setParameter(SpeechConstant.TTS_AUDIO_PATH, tempFileName);
    }

    private void testArray(){
//        String splitStr = "/storage/emulated/0/coomaan/cm20180503001.mp3";
//        String[] ccc = splitStr.split("\\.");
//        short [] aaa = {1,2,3,4,5,6,7,8,9,10};
//        short [] add = {22,33,44,55,66};
//        short [] ccc = new short[aaa.length + add.length];
//        System.arraycopy(aaa,0,ccc,0,aaa.length);
//        System.arraycopy(add,0,ccc,aaa.length,add.length);

//        short [] bbb = new short[10];
//        ShortBuffer buffer = ShortBuffer.allocate(100);
//        buffer.put(aaa,0,aaa.length);

//        buffer.put(aaa);
//        buffer.position(0);
//        buffer.put(add);
//        buffer.position(0);


//        Glog.i("jys","==" + buffer.limit() + "=" +  buffer.capacity() +"=" + buffer.mark() +"=" + buffer.position());
//        byte[][] allAudioBytes = new byte[2][];
//        byte[] buffer1= {1,2,3,4,5,6,7,8,9,10};
//        byte[] buffer2 = {21,22,23,24,25,26,27,28,29,30};
//
//        allAudioBytes[0] = Arrays.copyOf(buffer2,buffer2.length);
//        allAudioBytes[1] = Arrays.copyOf(buffer1,buffer1.length);
//
//        for (int i = 0; i < allAudioBytes.length;i++){
//            for (int j=0; j < allAudioBytes[i].length; j++){
//                Glog.i("jys","-" + allAudioBytes[i][j]);
//            }
//        }
//        short a = 6;
//        short b = (short) (a<<8);
//        buffer.get(bbb);
//        Glog.i("jys","测试数据：" + bbb[6]);
//        Glog.i("jys","splitStr：" + ccc.length);

//        for (int i=0; i<ccc.length; i++){
//            Glog.i("jys","ccc数据：" + ccc[i]);
//        }

    }

    @Override
    public void onDownloadCompleted(long completeDownloadId) {
        bgDuration = computeMusicDuration(getFileNameFromUrl(bgFile));
//        showTip("背景音乐下载完成后！" + bgDuration);
        if (downloadProgress != null && downloadProgress.isShowing()){
            downloadProgress.dismiss();
        }
    }


}
