package com.mantic.control.activity;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.iot.sdk.HttpStatus;
import com.baidu.iot.sdk.IoTException;
import com.baidu.iot.sdk.IoTRequestListener;
import com.baidu.iot.sdk.IoTSDKManager;
import com.baidu.iot.sdk.iam.AccessToken;
import com.baidu.iot.sdk.model.AudioTrack;
import com.baidu.iot.sdk.model.ControlResult;
import com.baidu.iot.sdk.model.DeviceInfo;
import com.baidu.iot.sdk.model.MusicSong;
import com.baidu.iot.sdk.model.PageInfo;
import com.baidu.iot.sdk.model.PropertyData;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.jaeger.library.StatusBarUtil;
import com.mantic.antservice.DeviceManager;
import com.mantic.antservice.m2m.WebSocketCommand;
import com.mantic.antservice.util.ToastUtils;
import com.mantic.control.AudioPlayer;
import com.mantic.control.ManticApplication;
import com.mantic.control.MyLifecycleHandler;
import com.mantic.control.R;
import com.mantic.control.api.account.AccountRetrofit;
import com.mantic.control.api.account.AccountServiceApi;
import com.mantic.control.api.account.AccountUrl;
import com.mantic.control.api.baidu.BaiduRetrofit;
import com.mantic.control.api.baidu.BaiduServiceApi;
import com.mantic.control.api.baidu.BaiduVoiceRetrofit;
import com.mantic.control.api.baidu.BaiduVoiceServiceApi;
import com.mantic.control.api.channelplay.bean.AddResult;
import com.mantic.control.api.channelplay.bean.ChannelPlayAddRsBean;
import com.mantic.control.api.channelplay.bean.ChannelPlayListRsBean;
import com.mantic.control.api.channelplay.bean.ChannelPlayModeGetRsBean;
import com.mantic.control.api.mopidy.MopidyRetrofit;
import com.mantic.control.api.mopidy.MopidyServiceApi;
import com.mantic.control.api.mopidy.MopidyTools;
import com.mantic.control.api.myservice.MyServiceOperatorRetrofit;
import com.mantic.control.api.myservice.MyServiceOperatorServiceApi;
import com.mantic.control.api.myservice.bean.MusicServiceBean;
import com.mantic.control.data.Channel;
import com.mantic.control.data.DataFactory;
import com.mantic.control.device.ControlTaskService;
import com.mantic.control.entiy.MusicService;
import com.mantic.control.fragment.DefinitionChannelEditFragment;
import com.mantic.control.fragment.FragmentEntrust;
import com.mantic.control.fragment.MainFragment;
import com.mantic.control.listener.DeviceStateController;
import com.mantic.control.manager.ActivityManager;
import com.mantic.control.manager.ChannelPlayListManager;
import com.mantic.control.manager.DragLayoutManager;
import com.mantic.control.musicservice.BaiduSoundData;
import com.mantic.control.musicservice.IdaddyMusicService;
import com.mantic.control.musicservice.QingtingMusicService;
import com.mantic.control.musicservice.WangyiMusicService;
import com.mantic.control.musicservice.XimalayaSoundData;
import com.mantic.control.receiver.NetworkReceiver;
import com.mantic.control.utils.AutomaticUtil;
import com.mantic.control.utils.DensityUtils;
import com.mantic.control.utils.FileUtil;
import com.mantic.control.utils.Glog;
import com.mantic.control.utils.IoTAppConfigMgr;
import com.mantic.control.utils.NetworkUtils;
import com.mantic.control.utils.SharePreferenceUtil;
import com.mantic.control.utils.SleepTimeSetUtils;
import com.mantic.control.utils.StatusBarHelper;
import com.mantic.control.utils.TimeUtil;
import com.mantic.control.utils.Util;
import com.mantic.control.utils.Utility;
import com.mantic.control.websocket.WebSocketController;
import com.mantic.control.widget.CustomDialog;
import com.mantic.control.widget.ReboundScrollView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

import io.crossbar.autobahn.WebSocketConnection;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.mantic.control.data.Channel.PLAY_STATE_PAUSE;
import static com.mantic.control.data.Channel.PLAY_STATE_PLAYING;
import static com.mantic.control.data.Channel.PLAY_STATE_STOP;
import static com.mantic.control.device.ControlTaskService.DEVICE_STATUS;
import static com.mantic.control.device.ControlTaskService.ONLINE;

import com.alibaba.sdk.android.feedback.impl.FeedbackAPI;
import com.tencent.bugly.beta.Beta;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener,
        MainFragment.ClickNavButton, FragmentEntrust, DeviceStateController.DeviceReportCallback,
        View.OnClickListener, DefinitionChannelEditFragment.IFragmentBackHandled, DataFactory.OnOperatorResultListener,WebSocketConnection.WebSocketListener {
    private static final String TAG = "MainActivity";
    private static final int signalTime = 20*1000;
    private static final int refreshTokenTime = 60 * 60 * 1000;//2小时刷新一次token
    private DrawerLayout mDrawerLayout;

    private AudioPlayer mAudioPlayer;
    private ImageView navIconIv;
    private TextView mNickname;
    private DataFactory mDataFactory;
    private ArrayList<Channel> mBeingPlayList;


    private LinearLayout ll_operator_result;
    private ImageButton btn_operator_success;
    private ImageButton btn_operator_fail;
    private View view_status_bar;
    private TextView tv_operator_content;
    private Animation animation;
    private Timer mTimer;
    private TimerTask timerTask;

    //设备控制相关
    private Intent deviceIntent;

    private BroadcastReceiver netReceiver;
    private BroadcastReceiver deviceStateReceiver;

    private Dialog mDialog;

    public static MopidyServiceApi mpServiceApi; //点击获取播放地址

    private static final String PHOTO_BASE_URL = "http://tb.himg.baidu.com/sys/portrait/item/";

    private Handler handler = new Handler();

    // 1:连接 0：断开 断开20秒重连
    private int webSocketState = 1;

    private int playSrc = 1;// 1：酷曼APP 0：语音搜歌

    private int firstPlayState = 0;//第一次进入的播放状态

    private boolean isLoadingData = false;

    private MainFragment mMainFragment;

    private ManticApplication manticApplication;

    private Context mContext;

    private boolean isSyncVoice = false;//是否同步过语音点播的歌曲

    private boolean unbindShow = false;

    private boolean isFirstLoad = true;

    private boolean isFirstSyncVoice = true;

    private LinearLayout pwdLayout;

    private Fragment currentFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.setTitle("");
        mContext = this;
        this.mDrawerLayout = (DrawerLayout) this.findViewById(R.id.drawer_layout);

        view_status_bar = findViewById(R.id.view_status_bar);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            StatusBarUtil.setColorForDrawerLayout(MainActivity.this, mDrawerLayout, Color.parseColor("#f9f9fa"), 0);
            StatusBarHelper.statusBarLightMode(this);
            ViewGroup.LayoutParams layoutParams = view_status_bar.getLayoutParams();
            layoutParams.height = DensityUtils.getStatusBarHeight(this);
            layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
            view_status_bar.setLayoutParams(layoutParams);
            view_status_bar.setBackgroundColor(ContextCompat.getColor(this, R.color.operator_success_color));
            view_status_bar.setVisibility(View.VISIBLE);
        }

        //播放器初始化
        this.mAudioPlayer = new AudioPlayer(this, mContext);
        this.mAudioPlayer.init();

        SharePreferenceUtil.setStartedFlag(this);

        //设备监听服务
        deviceIntent = new Intent(this, ControlTaskService.class);
        startService(deviceIntent);

        Glog.i(TAG, "deviceId : " + SharePreferenceUtil.getDeviceId(mContext));
        DeviceStateController.getInstance(this, SharePreferenceUtil.getDeviceId(mContext)).addDevReportCallback(this);

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.findViewById(R.id.nav_clock).setOnClickListener(this);
        navigationView.findViewById(R.id.help_menu).setOnClickListener(this);
        navigationView.findViewById(R.id.nav_suggestions).setOnClickListener(this);
        navigationView.findViewById(R.id.nav_about).setOnClickListener(this);
        navigationView.findViewById(R.id.nav_logout).setOnClickListener(this);
        navigationView.findViewById(R.id.update_app).setOnClickListener(this);
        pwdLayout = (LinearLayout) navigationView.findViewById(R.id.account_setting);
        navIconIv = (ImageView) navigationView.getHeaderView(0).findViewById(R.id.iv_nav_head);
        mNickname = (TextView) navigationView.getHeaderView(0).findViewById(R.id.tv_nav_name);
        pwdLayout.setOnClickListener(this);

        ll_operator_result = (LinearLayout) findViewById(R.id.ll_operator_result);
        ll_operator_result.setVisibility(View.GONE);
        btn_operator_success = (ImageButton) findViewById(R.id.btn_operator_success);
        btn_operator_fail = (ImageButton) findViewById(R.id.btn_operator_fail);
        tv_operator_content = (TextView) findViewById(R.id.tv_operator_content);

        mDataFactory = DataFactory.newInstance(getApplicationContext());
        getBeingPlayList();
        getChannelPlayMode();
        getMusicServiceUris();

        this.initMainFragment();
        //网络监听
        initReceiver();

        mDataFactory.registerOperatorResultListener(this);

//        updateUserInfo();

        updateLoginStatus();

        mpServiceApi = MopidyRetrofit.getInstance().create(MopidyServiceApi.class);

        Util.getRecentPlay(this, mDataFactory);

        this.mAudioPlayer.onStart();

        // webSocket
        connectWebSocket();

        handler.postDelayed(signalRunnable,signalTime);
        handler.postDelayed(refreshTokenRunnable, refreshTokenTime);
        manticApplication = (ManticApplication)this.getApplicationContext();

        getDeviceName();

        //自动播放暂时用不到了
//        getAutomaticPlay();

//        if (SharePreferenceUtil.getKeyAutoKey(mContext).equals("")){
//            pwdLayout.setVisibility(View.VISIBLE);
//        }else {
//            pwdLayout.setVisibility(View.GONE);
//        }
    }


    @Override
    public void operatorResult(String content, boolean success) {
        if (null != animation) {
            animation.cancel();
            ll_operator_result.clearAnimation();
        }

        if (null != timerTask) {
            timerTask.cancel();
            timerTask = null;

        }

        if (null != mTimer) {
            mTimer.cancel();
            mTimer.purge();
            mTimer = null;
        }

        ll_operator_result.setVisibility(View.GONE);
        if (!success) {
            ll_operator_result.setBackgroundColor(getResources().getColor(R.color.operator_fail_color));
            view_status_bar.setBackgroundColor(getResources().getColor(R.color.operator_fail_color));
            btn_operator_success.setVisibility(View.INVISIBLE);
            btn_operator_fail.setVisibility(View.VISIBLE);
        } else {
            btn_operator_success.setVisibility(View.VISIBLE);
            ll_operator_result.setBackgroundColor(getResources().getColor(R.color.operator_success_color));
            view_status_bar.setBackgroundColor(getResources().getColor(R.color.operator_success_color));
            btn_operator_fail.setVisibility(View.GONE);
        }

        animation = AnimationUtils.loadAnimation(this, R.anim.operator_result_visiable);
        tv_operator_content.setText(content);
        ll_operator_result.startAnimation(animation);

        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                ll_operator_result.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animation animation) {

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        operator_last();
    }

    private void operator_last() {
        mTimer = new Timer();
        timerTask = new TimerTask() {
            @Override
            public void run() {
                ll_operator_result.post(new Runnable() {
                    @Override
                    public void run() {
                        animation = AnimationUtils.loadAnimation(MainActivity.this, R.anim.operator_result_gone);
                        ll_operator_result.startAnimation(animation);
                        animation.setAnimationListener(new Animation.AnimationListener() {
                            @Override
                            public void onAnimationStart(Animation animation) {

                            }

                            @Override
                            public void onAnimationEnd(Animation animation) {
                                ll_operator_result.setVisibility(View.GONE);
                            }

                            @Override
                            public void onAnimationRepeat(Animation animation) {

                            }
                        });
                    }
                });
            }
        };
        mTimer.schedule(timerTask, 2000);

    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    @Override
    protected void onPause() {
        super.onPause();
        unbindShow = false;
        Glog.i(TAG,"-----onPause--------");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Glog.i(TAG,"-----onStop--------");
    }

    @Override
    public void onAttachFragment(Fragment fragment) {
        Glog.i(TAG,"onAttachFragment.............");
        currentFragment = fragment;
        super.onAttachFragment(fragment);

    }

    @Override
    protected void onDestroy() {
        this.mAudioPlayer.onStop();
        this.mAudioPlayer.exit();
        unregisterReceiver(netReceiver);
        unregisterReceiver(deviceStateReceiver);
        stopService(deviceIntent);
        DeviceStateController.getInstance(this, SharePreferenceUtil.getDeviceId(mContext)).delStateChanngedCallback();
        mDataFactory.unregisterOperatorResultListener(this);
        //webSocket
        if (WebSocketController.getInstance().mConnection.isConnected()){
            WebSocketController.getInstance().mConnection.disconnect();
        }
        handler.removeCallbacks(signalRunnable);
        if (needExit){
            System.exit(0);
        }
        super.onDestroy();
    }


    @Override
    protected void onResume() {
        super.onResume();
        if (isFirstLoad) {
            isFirstLoad = false;
        } else {
            mAudioPlayer.updateVolume();
//            mAudioPlayer.getAudioPlayerProgress(onlineStatus, true);
//            mAudioPlayer.getCurrentChannel();
        }

        if (AccountUrl.BASE_URL.contains("v2")){
            checkRefreshToken();
        }
    }

    @Override
    public void onBackPressed() {
        Glog.i(TAG, "onBackPressed: ");
        if (selectedFragment != null) {
            if (selectedFragment.onBackPressed() && getSupportFragmentManager()
                    .getBackStackEntryCount() > 0) {
                selectedFragment.cancelEdit();
            }
        } else {
            if (!this.mAudioPlayer.isCollapsed()) {
                this.mAudioPlayer.collapsed();
            } else {
                Glog.i(TAG, "DragLayoutManager.getAppManager().getDragLayoutLockeEnable() = " + DragLayoutManager.getAppManager().getDragLayoutLockeEnable());

                if (!DragLayoutManager.getAppManager().getDragLayoutLockeEnable()) {
                    exit();
                } else {
                    super.onBackPressed();
                }
            }
        }
    }

    private void initMainFragment() {
        FragmentManager fragmentManager = this.getSupportFragmentManager();
        Fragment fragment = fragmentManager.findFragmentByTag(MainFragment.TAG);
        if (null == fragment) {
            mMainFragment = new MainFragment();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.add(R.id.id_fragment_content, mMainFragment, MainFragment.TAG).commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        /*
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        */

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem menuItem) {
        return false;
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
//        this.mAudioPlayer.onWindowFocusChanged(hasFocus);
    }

    @Override
    public void onClickNavButton() {
        if (this.mDrawerLayout.isDrawerOpen(Gravity.LEFT)) {
            this.mDrawerLayout.closeDrawer(Gravity.LEFT);
        } else {
            this.mDrawerLayout.openDrawer(Gravity.LEFT);
        }
    }

    @Override
    public void pushFragment(Fragment fragment, String tag) {
        Glog.i(TAG, "pushFragment fragment = " + fragment + "---tag = " + tag);
        FragmentTransaction transaction = this.getSupportFragmentManager().beginTransaction();
        if ("com.mantic.control.fragment.ChannelManagementFragment".equals(tag) ||
                "com.mantic.control.fragment.MyChannelAddFragment".equals(tag) ||
                "com.mantic.control.fragment.DeviceAllFragment".equals(tag)) {
            transaction.setCustomAnimations(R.anim.push_up_in, R.anim.push_down_out, R.anim.push_up_in, R.anim.push_down_out);
        } else if ("SearchResultFragment".equals(tag)) {
            transaction.setCustomAnimations(R.anim.push_right_in, R.anim.push_left_out, R.anim.push_right_in, R.anim.push_left_out);
        } else if ("ChannelDetailsDescribeFragment".equals(tag)) {
//            transaction.setCustomAnimations(R.anim.unzoom_in, R.anim.unzoom_out, R.anim.unzoom_in, R.anim.unzoom_out);
        } else {
            transaction.setCustomAnimations(R.anim.push_right_in, R.anim.push_left_out, R.anim.push_right_in, R.anim.push_left_out);
        }

        transaction.add(R.id.id_fragment_content, fragment, tag);
        transaction.addToBackStack(tag);
        transaction.commit();
    }

    @Override
    public void popFragment(String tag) {
        this.getSupportFragmentManager().popBackStackImmediate(tag, FragmentManager.POP_BACK_STACK_INCLUSIVE);
    }

    @Override
    public void popAllFragment() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        List<Fragment> fragments = fragmentManager.getFragments();
        for (int i = fragments.size() - 1; i >= 0; i--) {
            if (null != fragments.get(i)) {
                if (!"MainFragment".equals(fragments.get(i).getTag())) {
                    fragmentManager.popBackStack();
                }
            }
        }
//        this.getSupportFragmentManager().popBackStackImmediate(this.getSupportFragmentManager().getFragments().get(1).getTag(),FragmentManager.POP_BACK_STACK_INCLUSIVE);
    }
    
    @Override
    public void onDeviceReport(String key, String value, String timestamp) {
        Glog.i(TAG, "PlaySwitchReport, key: " + key + " ,respone: " + value + " timestamp: " + timestamp + " onlineStatus = " + onlineStatus);
        if ((key.equals("voice_result") || key.equals("PlaySwitchReport")) && onlineStatus) {

            int currChannelIndex = mDataFactory.getCurrChannelIndex();
            mBeingPlayList = mDataFactory.getBeingPlayList();
            if (mBeingPlayList.size() > 0) {
                if (currChannelIndex < mBeingPlayList.size() && currChannelIndex >= 0) {
                    final Channel channel = mBeingPlayList.get(currChannelIndex);
                    mDataFactory.setCurrChannel(channel);
                    String state = "2"; //默认暂停
                    try {
                        JSONObject jsonObject = new JSONObject(value);
                        state = ((Integer) jsonObject.get("state")).toString();
                        playSrc = (Integer) jsonObject.get("src");
                        firstPlayState = jsonObject.optInt("state");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
//                String state = ReportTools.valueToState(value);
                    Glog.i(TAG, "PlaySwitchReport state: " + state + "Src: " + playSrc);
                    //当前睡眠模式为播放结束后休眠，停止手机设置的睡眠时间
//                SleepTimeSetUtils.onlyClearSleepTimeSetByCurrent(MainActivity.this, mDataFactory);
                   if (playSrc == 0) {
                        ((ManticApplication)getApplication()).setPlaySrcZero(true);
                   } else if (playSrc == 1) {
                        ((ManticApplication)getApplication()).setPlaySrcZero(false);
                    }

                    if (key.equals("PlaySwitchReport") && isFirstSyncVoice) {
                        isFirstSyncVoice = false;
                        new Thread(DevicePlayRunnable).start();
                        return;
                    }

                    switch (state) {
                        case "0":  // 0:stop
                            mAudioPlayer.startAnimation = true;
                            channel.setPlayState(PLAY_STATE_STOP);
                            mDataFactory.setCurrChannel(channel);
                            mAudioPlayer.updateMainAudioPlayerFromChannel(channel);
                            sendSoundCompleted();
                            break;
                        case "2":  //2:pause
                            mAudioPlayer.startAnimation = true;
                            channel.setPlayState(PLAY_STATE_PAUSE);
                            mDataFactory.setCurrChannel(channel);
                            mAudioPlayer.updateMainAudioPlayerFromChannel(channel);
                            break;
                        case "1":  // 1:playing
                            mAudioPlayer.startAnimation = true;
                            channel.setPlayState(PLAY_STATE_PLAYING);
                            mDataFactory.setCurrChannel(channel);
                            mAudioPlayer.updateMainAudioPlayerFromChannel(channel);
                            isSyncVoice = false;
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    new Thread(DevicePlayRunnable).start();
                                }
                            }, 800);

                           /* if (null != mAudioPlayer) {
                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        mAudioPlayer.getAudioPlayerProgress(onlineStatus, false);
                                    }
                                }, 1000);

                            }*/
                            break;
                    }

//                mDataFactory.getBeingPlayList().set(currChannelIndex, channel);
//                int size = mDataFactory.getBeingPlayList().size();
//                for (int i = 0; i < size; i++) {
//                    if (i != currChannelIndex) {
//                        if (mDataFactory.getBeingPlayList().get(i).getPlayState() != PLAY_STATE_PAUSE) {
//                            mDataFactory.getBeingPlayList().get(i).setPlayState(PLAY_STATE_PAUSE);
//                            mDataFactory.getBeingPlayList().set(i, mDataFactory.getBeingPlayList().get(i));
//                        }
//                    }
//                }
//                mDataFactory.notifyBeingPlayListChange();
//                mDataFactory.notifyMyLikeMusicStatusChange();
                }
            } else {
                String state = "2"; //默认暂停
                try {
                    JSONObject jsonObject = new JSONObject(value);
                    state = ((Integer) jsonObject.get("state")).toString();
                    playSrc = (Integer) jsonObject.get("src");
                    firstPlayState = jsonObject.optInt("state");
                    Glog.i(TAG, "onDeviceReport: " + state);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                if (key.equals("PlaySwitchReport") && isFirstSyncVoice) {
                    isFirstSyncVoice = false;
                    new Thread(DevicePlayRunnable).start();
                    return;
                }

                if (isLoadingData) {
                    isLoadingData = false;
                    switch (state) {
                        case "1":  // 1:playing
                           handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    new Thread(DevicePlayRunnable).start();
                                }
                            }, 800);
                            break;
                    }
                }

                if ("0".equals(state)) {
                    isLoadingData = true;
                } else {
                    isLoadingData = false;
                }

            }

        } else if (key.equals("PlayVolumeReport")  && onlineStatus) {
            mAudioPlayer.updateVolumeFromDevice(Integer.valueOf(value));
        }

    }


    private Channel getCurrentChannel(){
        int currChannelIndex = mDataFactory.getCurrChannelIndex();
        mBeingPlayList = mDataFactory.getBeingPlayList();
        if (currChannelIndex < mBeingPlayList.size() && currChannelIndex >= 0) {
            return mBeingPlayList.get(currChannelIndex);
        }else {
            return null;
        }
    }

    private void connectWebSocket(){
        Glog.i("WebSocket","connected: " + WebSocketController.getInstance().mConnection.isConnected());
        if(null != WebSocketController.getInstance().mConnection && !WebSocketController.getInstance().mConnection.isConnected()) {
            WebSocketController.getInstance().startWebSocket(SharePreferenceUtil.getDeviceId(mContext));
            ((WebSocketConnection)WebSocketController.getInstance().mConnection).setMessageListener(this);
            Glog.i("WebSocket","connected: " + WebSocketController.getInstance().mConnection.isConnected());
            if (!WebSocketController.getInstance().mConnection.isConnected()){
                handler.postDelayed(bindRunnable,1000);
            }else {
                handler.removeCallbacks(bindRunnable);
            }
        }


    }

    private Runnable bindRunnable = new Runnable() {
        @Override
        public void run() {
            Glog.i("WebSocket","bindRunnable...");
//            WebSocketController.getInstance().mConnection.sendTextMessage(WebSocketCommand.getInstance(deviceId).bindDeviceCommand());
            if (null != WebSocketController.getInstance().mConnection && !WebSocketController.getInstance().mConnection.isConnected()){
                handler.postDelayed(this,1000);
            }

        }
    };

    @Override
    public void onClick(View view) {
        Intent intent = null;
        switch (view.getId()) {
            case R.id.nav_clock:
                intent=new Intent(MainActivity.this,ClockActivity.class);
               break;
            case R.id.help_menu:
                intent = new Intent(MainActivity.this, HelpActivity.class);
                break;
            case R.id.nav_suggestions:
                //intent = new Intent(MainActivity.this, FeedBackActivity.class);
                showSuggestions();
                break;
            case R.id.nav_about:
                intent = new Intent(MainActivity.this, AboutActivity.class);
                break;
            case R.id.update_app:
                Beta.checkUpgrade(true,false);
//                intent = new Intent(MainActivity.this, SettingActivity.class);
                break;
            case R.id.nav_logout:
                showLogoutDialog();
                break;
            case R.id.account_setting:
                intent = new Intent(MainActivity.this, AccountSettingActivity.class);
//                intent.putExtra("comfrom", "MainActivity");
                break;

        }
        if (intent != null) {
            startActivity(intent);
            overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
            TimerTask task = new TimerTask() {
                @Override
                public void run() {
                    mDrawerLayout.post(new Runnable() {
                        @Override
                        public void run() {
                            mDrawerLayout.closeDrawer(GravityCompat.START);
                        }
                    });

                }
            };
            Timer timer = new Timer();
            timer.schedule(task, 300);

        }


    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_VOLUME_DOWN://音量减小
                if (mAudioPlayer.mBottomSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
                    mAudioPlayer.onVolumeDown();
                } else {
                    showVolumeDialog();
                }
                return true;
            case KeyEvent.KEYCODE_VOLUME_UP://音量增大
                if (mAudioPlayer.mBottomSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
                    mAudioPlayer.onVolumeKeyUp();
                } else {
                    showVolumeDialog();
                }
                return true;
            default:
                break;
        }
        return super.onKeyDown(keyCode, event);
    }

    private Handler volHandler = new Handler();

    public void showVolumeDialog() {
        mDialog = Utility.getDialog(this, R.layout.volume_bottom_dialog);
        final SeekBar volSeek = (SeekBar) mDialog.findViewById(R.id.audio_player_volume_dialog_seek);
        volSeek.setMax(150);
        volSeek.setProgress(SharePreferenceUtil.getDeviceVolume(this));
        mDialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
            int curVol = SharePreferenceUtil.getDeviceVolume(mContext);

            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    switch (keyCode) {
                        case KeyEvent.KEYCODE_VOLUME_DOWN:

                            curVol -= 10;
                            volSeek.setProgress(curVol);
                            mAudioPlayer.onVolumeDown();
                            volHandler.removeCallbacks(volRun);
                            volDismiss();
                            return true;
                        case KeyEvent.KEYCODE_VOLUME_UP:
                            if (curVol >= 150) {
                                return true;
                            }
                            curVol += 10;
                            volSeek.setProgress(curVol);
                            mAudioPlayer.onVolumeKeyUp();
                            volHandler.removeCallbacks(volRun);
                            volDismiss();
                            return true;
                    }
                    return false;
                }
                return false;
            }
        });

        volSeek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
//                Glog.i(TAG,"onProgressChanged -> progress: " + progress + "  seekBar:" + seekBar);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                volHandler.removeCallbacks(volRun);
//                Glog.i(TAG,"onStartTrackingTouch -> : " + "  seekBar:" + seekBar);
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
//                Glog.i(TAG,"onStopTrackingTouch -> : " + "  seekBar:" + seekBar);
                volDismiss();
                int process = seekBar.getProgress();
                if (process<=10){
                    seekBar.setProgress(10);
                }
                mAudioPlayer.controlVolume(process);
            }
        });
        mDialog.show();
        volDismiss();
    }

    private void volDismiss() {
        int volTime = 3000;
        volHandler.postDelayed(volRun, volTime);
    }

    Runnable volRun = new Runnable() {
        @Override
        public void run() {
            if (mDialog.isShowing()) {
                mDialog.dismiss();
            }
        }
    };

    private void initReceiver() {
        netReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
//                判断当前网络状态
                if (action.equals(NetworkReceiver.NET_CHANGE)) {
                    if (intent.getIntExtra(NetworkReceiver.NET_TYPE, 0) == 0) {
                        Glog.i(TAG, "无网络...");
                    } else {
                        Glog.i(TAG, "有网络...");
                    }
                } else if (action.equals("close_main")) {
                    needExit = false;
                    finish();
                } else if (action.equals("PassResourceDone")) {
                    mAudioPlayer.updateVolume();
//                    mAudioPlayer.getAudioPlayerProgress(onlineStatus, true);
                    mAudioPlayer.getDeviceSleepTime();
                    getDevicePlayMode();
                }
            }
        };
        IntentFilter filter = new IntentFilter(NetworkReceiver.NET_CHANGE);
        filter.addAction("close_main");
        filter.addAction("PassResourceDone");
        registerReceiver(netReceiver, filter);

        deviceStateReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals(DEVICE_STATUS)) {

					refreshDeviceList();
//                    checkIfNeedLogin();
                    final boolean status = intent.getBooleanExtra(ONLINE, false);
                    Glog.i("PlaySwitchReport", "online status:  " + status);
                    onlineStatus = status;

                    mAudioPlayer.setControlEnable(status);
                    if (!status){//离线
                        //退出睡眠模式start
                        if (((ManticApplication) mContext.getApplicationContext()).isSleepTimeOn()) {
                            ((ManticApplication) mContext.getApplicationContext()).setSleepTimeOn(false);
                            SleepTimeSetUtils.resetSleepTime(mDataFactory);
                        }
                        //退出睡眠模式end
                        Channel channel = getCurrentChannel();
                        if (channel != null){
                            mAudioPlayer.startAnimation = true;
                            channel.setPlayState(PLAY_STATE_PAUSE);
                            mAudioPlayer.prgoressHandler.removeCallbacks(mAudioPlayer.updateRunnable);
                            mAudioPlayer.updateMainAudioPlayerFromChannel(channel);
                        }
//                        if (mMainFragment != null){
//                            mMainFragment.mSelectDevice.setImageResource(R.drawable.select_device_selector_offline);
//                        }
                        mDataFactory.notifyDeviceOnlineStatus(false);
                    }else {
//                        if (mMainFragment != null) {
//                            mMainFragment.mSelectDevice.setImageResource(R.drawable.select_device_selector_online);
//                        }
                        mDataFactory.notifyDeviceOnlineStatus(true);
                    }

                    //获取播放进度
                    if (null != mAudioPlayer && MyLifecycleHandler.isApplicationInForeground()) {
                        mAudioPlayer.updateVolume();
                        if (!((ManticApplication)getApplication()).isPlaySrcZero()) {
                            mAudioPlayer.getAudioPlayerProgress(onlineStatus, true);
                        } else {
                            if (!isSyncVoice) {
                                new Thread(DevicePlayRunnable).start();
                            } else {
                                mAudioPlayer.getAudioPlayerProgress(onlineStatus, true);
                            }
                        }

//                        mAudioPlayer.getCurrentChannel();
                    }
                }
            }
        };

        IntentFilter statusFilter = new IntentFilter(DEVICE_STATUS);
        registerReceiver(deviceStateReceiver, statusFilter);
    }

    public boolean onlineStatus = false;


    private void refreshDeviceList() {
        DeviceManager.getInstance().deviceApi.getUserAllDevices(new IoTRequestListener<List<DeviceInfo>>() {

            @Override
            public void onSuccess(HttpStatus code, List<DeviceInfo> obj, PageInfo info) {
                Glog.i(TAG,"onSuccess()... + code: " + code + "obj: " + obj + "info: " + info);
                if (obj != null && !obj.isEmpty()) {
                    for (int i = 0; i<obj.size(); i++){
//                        DeviceInfo deviceInfo = obj.get(i);
//                        String deviceId = deviceInfo.getDeviceUuid();
//                        SharePreferenceUtil.setDeviceId(context,deviceId);
                        //获取用户设备信息 直接进入Main 用户信息 进入更新 后续处理
                        //在主界面获取到设备 不做任何处理
                      DeviceInfo deviceInfo = obj.get(i);
                        String deviceId = deviceInfo.getDeviceUuid();
                        String deviceToken = deviceInfo.getToken();
                        Glog.i(TAG,"已绑定设备：uuid = " + deviceId + " token = " + deviceToken);
                        SharePreferenceUtil.setBind(mContext);
                        if (!deviceId.equals(SharePreferenceUtil.getDeviceId(mContext))){
                            Glog.i(TAG,"需要更新设备信息到本地。。。。。。。。");
                            SharePreferenceUtil.setDeviceId(mContext,deviceId);
                            SharePreferenceUtil.setDeviceToken(mContext,deviceToken);
                        }
                    }
                } else {
                    //账号下没有设备，做第一次处理
                    Glog.i(TAG,"该账号下没有设备，准备退出操作~ + unbindShow：" + unbindShow);
                    if (!unbindShow){
                        Glog.i(TAG, "isActivityRunning :" + MainActivity.class.getName() + "=" + ActivityManager.isActivityRunning(mContext, MainActivity.class.getName()));
                        if (ActivityManager.isActivityRunning(mContext, MainActivity.class.getName())) {
                            showDeviceUnbindDialog(mContext);
                        }

                    }
                }

            }

            @Override
            public void onFailed(HttpStatus code) {
                Glog.i(TAG,"onFailed ->code: " + code.getResponseCode());
                if (code.getResponseCode() == 1001 || code.getResponseCode() == 1002){ //1001:auth fail 1002:app auth fail
                    SharePreferenceUtil.clearSettingsData(mContext);
                    SharePreferenceUtil.clearUserData(mContext);
                    IoTSDKManager.getInstance().logout();
                    needExit = false;
                    restartLoading();
                }
            }

            @Override
            public void onError(IoTException error) {
                Glog.i(TAG,"onFailed ->error: " + error);
            }
        }, null);
    }

    public  void showDeviceUnbindDialog(Context mContext){
        CustomDialog.Builder mBuilder = new CustomDialog.Builder(mContext);
        String deviceString = "\"" + SharePreferenceUtil.getDeviceName(this) + "\"";
        mBuilder.setTitle(mContext.getString(R.string.dialog_btn_prompt));
        String test = String.format(getResources().getString(R.string.device_had_unbind),  deviceString);
        mBuilder.setMessage(test);
        mBuilder.setPositiveButton(mContext.getString(R.string.qr_code_positive_button_know), new CustomDialog.Builder.DialogPositiveClickListener() {
            @Override
            public void onPositiveClick(final CustomDialog dialog) {
                dialog.dismiss();
                needExit = false;
                SharePreferenceUtil.clearSettingsData(MainActivity.this);
                restartLoading();
            }
        });
        mBuilder.create().show();
        unbindShow = true;

    }

    private void showSuggestions(){
        /**
         * 在Activity的onCreate中执行的代码
         * 可以设置状态栏背景颜色和图标颜色，这里使用com.githang:status-bar-compat来实现
         */
//        FeedbackAPI.setActivityCallback(new IActivityCallback() {
//            @Override
//            public void onCreate(Activity activity) {
//                StatusBarCompat.setStatusBarColor(activity,getResources().getColor(R.color.aliwx_setting_bg_nor),true);
//            }
//        });

        /**
         * 自定义参数演示
         */
//        JSONObject jsonObject = new JSONObject();
//        try {
//            jsonObject.put("loginTime", "登录时间");
//            jsonObject.put("visitPath", "登陆，关于，反馈");
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//        FeedbackAPI.setAppExtInfo(jsonObject);

        /**
         * 以下是设置UI
         */
        //设置默认联系方式
        //FeedbackAPI.setDefaultUserContactInfo("13800000000");
        //沉浸式任务栏，控制台设置为true之后此方法才能生效
        FeedbackAPI.setTranslucent(true);
        //设置返回按钮图标
        FeedbackAPI.setBackIcon(R.drawable.ali_feedback_common_back_btn_bg);
        //设置标题栏"历史反馈"的字号，需要将控制台中此字号设置为0
        FeedbackAPI.setHistoryTextSize(20);
        //设置标题栏高度，单位为像素
        FeedbackAPI.setTitleBarHeight(180);

        FeedbackAPI.openFeedbackActivity();
    }

    private void showLogoutDialog() {
        final CustomDialog.Builder mBuilder;
        mBuilder = new CustomDialog.Builder(this);
        mBuilder.setTitle(this.getString(R.string.dialog_btn_prompt));
        mBuilder.setMessage(this.getString(R.string.loginout_content));
        mBuilder.setPositiveButton(getString(R.string.logout), new CustomDialog.Builder.DialogPositiveClickListener() {
            @Override
            public void onPositiveClick(final CustomDialog dialog) {
                SharePreferenceUtil.clearSettingsData(mContext);
                SharePreferenceUtil.clearUserData(mContext);
                IoTSDKManager.getInstance().logout();
                needExit = false;
//                needExit = true;
                dialog.dismiss();
                restartLoading();
            }
        });
        mBuilder.setNegativeButton(getString(R.string.cancel), new CustomDialog.Builder.DialogNegativeClickListener() {
            @Override
            public void onNegativeClick(CustomDialog dialog) {
                dialog.dismiss();
            }
        });
        mBuilder.create().show();
    }

        private boolean needExit  = true;
    private void restartLoading() {
        Intent intent = new Intent(MainActivity.this, LoadingActivity.class);
        startActivity(intent);
        if (WebSocketController.getInstance().mConnection.isConnected()){
            WebSocketController.getInstance().mConnection.disconnect();
        }
        finish();
    }



    private void updateLoginStatus() {
        navIconIv.setBackgroundResource(R.drawable.ic_default_icon);
        mNickname.setText(SharePreferenceUtil.getUserName(this));
        Glide.with(this)
            .load(SharePreferenceUtil.getUserPhoto(this))
            .asBitmap()
            .centerCrop()
            .into(new BitmapImageViewTarget(navIconIv) {
                @Override
                protected void setResource(Bitmap resource) {
                    RoundedBitmapDrawable circularBitmapDrawable =
                            RoundedBitmapDrawableFactory.create(getResources(), resource);
                    circularBitmapDrawable.setCircular(true);
//                    circularBitmapDrawable.setCornerRadius(12);
                    navIconIv.setImageDrawable(circularBitmapDrawable);
                }
            });
    }

    private void getDeviceName(){
        String user_id = SharePreferenceUtil.getUserId(this);
        Glog.i(TAG," getDeviceName - > user_id:" + user_id);
        MopidyServiceApi mopidyServiceApi = MopidyRetrofit.getInstance().create(MopidyServiceApi.class);
        String request = "{\n" +
                "    \"method\": \"core.playlists.mantic_get_device_name\",\n" +
                "    \"jsonrpc\": \"2.0\",\n" +
                "    \"device_id\":\"" + user_id +"\",\n" +
                "    \"id\": 1\n" +
                "}";
        Glog.i(TAG," getDeviceName - > request:" + request);
        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("Content-Type, application/json; charset=utf-8"),request);
        Call<ResponseBody> call = mopidyServiceApi.postMopidyGetDeviceName(MopidyTools.getHeaders(),body);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful() && null == response.errorBody()) {
                    try {
                        JSONObject mainObject = new JSONObject(response.body().string()); //json 主体
                        String result = mainObject.getString("result");// result json 主体
                        Glog.i(TAG," postMopidyGetDeviceName - > result:" + result);
                        if (!TextUtils.isEmpty(result)) {
                            SharePreferenceUtil.setDeviceName(mContext,result);
                            mDataFactory.notifyUpdateDeviceName(result);
                        } else {
                            String tempName = SharePreferenceUtil.getUserName(mContext)+"的音箱";
                            SharePreferenceUtil.setDeviceName(mContext,tempName);
                            mDataFactory.notifyUpdateDeviceName(tempName);
                        }

                    } catch (JSONException | IOException e) {
                        String tempName = SharePreferenceUtil.getUserName(mContext)+"的音箱";
                        SharePreferenceUtil.setDeviceName(mContext,tempName);
                        mDataFactory.notifyUpdateDeviceName(tempName);
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Glog.i(TAG,"postMopidyGetDeviceName... onFailure");
            }
        });
    }

    public void getBeingPlayList() {
        ChannelPlayListManager.getInstance().getChannelPlayList(new Callback<ChannelPlayListRsBean>() {
            @Override
            public void onResponse(Call<ChannelPlayListRsBean> call, Response<ChannelPlayListRsBean> response) {
                Glog.i(TAG, "getBeingPlayList: " + response.isSuccessful());
                if (response.isSuccessful() && null == response.errorBody()) {
                    int position = 0;
                    List<ChannelPlayListRsBean.Result> resultList = response.body().getResult();
                    if (resultList == null || resultList.size() <= 0) {
                        return;
                    }
                    ArrayList<Channel> channelList = new ArrayList<Channel>();
                    for (int i = 0; i < resultList.size(); i++) {
                        ChannelPlayListRsBean.Result result = resultList.get(i);
                        Channel channel = new Channel();
                        if (null != result.track.getMantic_artists_name()) {
                            String singer = "";
                            for (int z = 0; z < result.track.getMantic_artists_name().size(); z++) {
                                if (z != result.track.getMantic_artists_name().size() - 1) {
                                    singer = singer + result.track.getMantic_artists_name().get(z).toString() + "，";
                                } else {
                                    singer = singer + result.track.getMantic_artists_name().get(z).toString();
                                }
                            }
                            channel.setSinger(singer);
                        }
                        channel.setDuration(result.track.getLength());
                        channel.setIconUrl(result.track.getMantic_image());
                        channel.setPlayUrl(result.track.getMantic_real_url());
                        channel.setName(result.track.getName());
                        channel.setUri(result.track.getUri());
                        channel.setTlid(result.tlid);
                        channel.setMantic_album_name(result.track.getMantic_album_name());
                        channel.setMantic_album_uri(result.track.getMantic_album_uri());
                        if (result.track.getMantic_radio_length() != null) {
                            channel.setTimePeriods(result.track.getMantic_radio_length());
                        }
                        if (result.is_playing) {
                            position = i;
                            channel.setPlayState(Channel.PLAY_STATE_PLAYING);
                        } else {
                            channel.setPlayState(Channel.PLAY_STATE_STOP);
                        }
                        channelList.add(channel);

                    }

                    if (position != -1) {
                        mDataFactory.setCurrChannel(channelList.get(position));
                        SharePreferenceUtil.setSharePreferenceData(MainActivity.this, "Mantic", "lastIndex", position + "");
                        if (!channelList.get(position).getUri().contains("radio:")) {
                            mDataFactory.addRecentPlay(mContext, channelList.get(position));
                            mDataFactory.notifyRecentPlayListChange(mDataFactory.getRecentPlayList());
                        }
                        Channel.playGetBaiduUri(mContext, mDataFactory.getCurrChannel().getUri(), null, false, mDataFactory, MainActivity.this);
                    }

                    mDataFactory.setBeingPlayList(channelList);
                    mDataFactory.notifyBeingPlayListChange();
                    mDataFactory.notifyMyLikeMusicStatusChange();
                    mAudioPlayer.updateBeingPlayList(channelList);



                    ArrayList<DataFactory.ChannelControlListener> channelControlListeners = mDataFactory.getChannelControlListeners();
                    for (int i = 0; i < channelControlListeners.size(); i++) {
                        Glog.i(TAG, "onResponse: " + channelControlListeners.size());
                        DataFactory.ChannelControlListener listener = channelControlListeners.get(i);
                        listener.afterChannelControl();
                        listener.beginChannelControl(position);
                    }
                } else {
                    if (!NetworkUtils.isAvailableByPing(MainActivity.this)) {
                        mDataFactory.notifyOperatorResult(getString(R.string.network_suck), false);
                    } else {
                        mDataFactory.notifyOperatorResult(getString(R.string.service_problem), false);
                    }
                }

            }

            @Override
            public void onFailure(Call<ChannelPlayListRsBean> call, Throwable t) {
                mDataFactory.notifyOperatorResult(getString(R.string.network_suck), false);
                if (!NetworkUtils.isAvailableByPing(MainActivity.this)) {
                    mDataFactory.notifyOperatorResult(getString(R.string.network_suck), false);
                } else {
                    mDataFactory.notifyOperatorResult(getString(R.string.service_problem), false);
                }
            }
        }, MainActivity.this);
    }

    private void getAutomaticPlay(){//第一次绑定后自动播放歌曲
        handler.postDelayed(getAutomaticRunnable, 5000);
    }

    private Runnable getAutomaticRunnable = new Runnable() {
        @Override
        public void run() {
            Glog.i(TAG, "getAutomaticRunnable: " + SharePreferenceUtil.getSharePreferenceData(mContext, "Mantic", "isAutoMaticPlay"));
            // TODO Auto-generated method stub
            if (null == mDataFactory.getCurrChannel() && "true".equals(SharePreferenceUtil.getSharePreferenceData(mContext, "Mantic", "isAutoMaticPlay"))) {
                if (onlineStatus){
                    AutomaticUtil.getAutomticChannelLsit(mContext, mDataFactory, MainActivity.this, new AutomaticCallback() {
                        @Override
                        public void callback() {
                            handler.removeCallbacks(getAutomaticRunnable);
                        }
                    });
                } else {
                    handler.postDelayed(this, 5000);
                }
            } else {
                handler.removeCallbacks(getAutomaticRunnable);
            }
        }
    };

    public interface AutomaticCallback {
        void callback();
    }
    
    //获取播放列表的播放模式
    private void getChannelPlayMode() {
        ChannelPlayListManager.getInstance().getChannelPlayMode(new Callback<ChannelPlayModeGetRsBean>() {
            @Override
            public void onResponse(Call<ChannelPlayModeGetRsBean> call, Response<ChannelPlayModeGetRsBean> response) {
                if (response.isSuccessful() && null == response.errorBody()) {
                    if (null != response.body()) {
                        ((ManticApplication) getApplication()).setChannelPlayMode(response.body().getResult());
                    }
                }
            }

            @Override
            public void onFailure(Call<ChannelPlayModeGetRsBean> call, Throwable t) {

            }
        }, this);

    }

    /**
     * 获取当前服务uris
     */
    private void getMusicServiceUris() {
        MyServiceOperatorServiceApi myServiceOperatorServiceApi = MyServiceOperatorRetrofit.getInstance().create(MyServiceOperatorServiceApi.class);
        Call<MusicServiceBean> getListCall = myServiceOperatorServiceApi.getMyServiceList();
        getListCall.enqueue(new Callback<MusicServiceBean>() {
            @Override
            public void onResponse(Call<MusicServiceBean> call, Response<MusicServiceBean> response) {
                if (response.isSuccessful() && null == response.errorBody()) {
                    MusicServiceBean musicServiceBean = response.body();
                    if (null != musicServiceBean) {
                        List<MusicServiceBean.ServiceBean> serviceBeanList = musicServiceBean.getMUSIC();
                        if (null != serviceBeanList && serviceBeanList.size() > 0) {
                            List<String> myMusicServiceUriList = mDataFactory.getMyMusicServiceUriList();
                            myMusicServiceUriList.clear();
                            MusicServiceBean.ServiceBean serviceBean = serviceBeanList.get(0);
                            List<MusicServiceBean.ServiceBean.MyServiceBean> myServiceBeanList = serviceBean.getSERVICE();
                            if (null != myServiceBeanList && myServiceBeanList.size() > 0) {
                                for (int i = 0; i < myServiceBeanList.size(); i++) {
                                    MusicService musicService = new MusicService();
                                    musicService.setIconUrl(myServiceBeanList.get(i).getPIC_URL());
                                    musicService.setName(myServiceBeanList.get(i).getNAME());
                                    musicService.setActive(true);
                                    musicService.setIntroduction(myServiceBeanList.get(i).getDES());
                                    switch (myServiceBeanList.get(i).getNAME()) {
                                        case "百度云音乐":
                                            musicService.setMyMusicService(new BaiduSoundData(mContext));
                                            myMusicServiceUriList.add("baidu:");
                                            break;
                                        case "喜马拉雅":
                                            musicService.setMyMusicService(new XimalayaSoundData(mContext));
                                            myMusicServiceUriList.add("ximalaya:");
                                            break;
                                        case "网易云音乐":
                                            musicService.setMyMusicService(new WangyiMusicService());
                                            myMusicServiceUriList.add("netease:");
                                            break;
                                        case "蜻蜓FM":
                                            musicService.setMyMusicService(new QingtingMusicService());
                                            myMusicServiceUriList.add("qingting:");
                                            break;
                                        case "工程师爸爸":
                                            musicService.setMyMusicService(new IdaddyMusicService());
                                            myMusicServiceUriList.add("idaddy:");
                                            break;
                                        default:
                                            break;
                                    }
                                }

                                mDataFactory.setMyMusicServiceUriList(myMusicServiceUriList);
                            }
                        }
                    }
                } else {

                }
            }

            @Override
            public void onFailure(Call<MusicServiceBean> call, Throwable t) {

            }
        });

    }

    /**
     * 获取音箱的设备模式
     */
    private void getDevicePlayMode() {
        Channel.getDevicePlayMode(mContext, new Channel.ChannelDeviceControlListenerCallBack() {
            @Override
            public void onSuccess(HttpStatus code, ControlResult obj, PageInfo info) {

                try {
                    JSONObject jsonObject = new JSONObject(obj.content);
                    Glog.i("getDevicePlayMode", "deviceMode = " + jsonObject.optInt("mode"));
                    SharePreferenceUtil.setDeviceMode(MainActivity.this, jsonObject.optInt("mode"));
                } catch (Exception e) {

                }

            }

            @Override
            public void onFailed(HttpStatus code) {

            }

            @Override
            public void onError(IoTException error) {

            }
        });
    }

    /**
     * WebSocket 消息回传接口
     * @param message
     */
    @Override
    public void onMessage(String message) {
        Glog.i("WebSocket","message: " + message);
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(message);
            JSONObject dataObject = jsonObject.optJSONObject("data");
            if (null == dataObject) {
                if ("pong".equals(jsonObject.optString("action"))) {
                    String ed = jsonObject.optString("ed");
                    if (TextUtils.isEmpty(ed) || !ed.equals(SharePreferenceUtil.getDeviceId(MainActivity.this))) {
                        WebSocketController.getInstance().mConnection.disconnect();
                    }
                }
                return;
            }
            Iterator<String> iterator = dataObject.keys();
            while (iterator.hasNext()){
                String key = iterator.next();
                switch (key) {
                    case "PlayVolumeReport":
                        onDeviceReport(key, ((Integer) dataObject.get(key)).toString(), "");
                        break;
                    case "PlaySwitchReport":
                        String value = ((JSONObject) dataObject.get(key)).toString();
                        Glog.i("WebSocket", "value: " + value);
                        onDeviceReport(key, value, "");
                        break;
                    case "ModeReport":
                        int mode = (Integer) dataObject.get(key);

                        SharePreferenceUtil.setDeviceMode(this, mode);

                        mDataFactory.notifyDeviceModeChange(mode);
//                        if (mode == 1){
//                            Util.bluetoothModeDialog(this);
//                        }
                        Glog.i("WebSocket", "mode: " + mode);
                        break;
                    case "ScheduleTimeReport":
                        ((ManticApplication) getApplicationContext()).setSleepTimeOn(false);
                        SleepTimeSetUtils.resetSleepTime(mDataFactory);
                        ArrayList<Channel> beingPlayList = mDataFactory.getBeingPlayList();
                        Channel currChannel = mDataFactory.getCurrChannel();
                        if (null != currChannel) {
                            currChannel.setPlayState(Channel.PLAY_STATE_STOP);
                            mDataFactory.setCurrChannel(currChannel);
                        }
                        if (mDataFactory.getCurrChannelIndex() != -1) {
                            beingPlayList.set(mDataFactory.getCurrChannelIndex(), currChannel);
                        }
                        mAudioPlayer.updateBeingPlayList(beingPlayList);
                        break;
                }

            }
        } catch (JSONException e) {
            Glog.i("WebSocket","onMessage Exception!");
            try {
                jsonObject = new JSONObject(message);
                int state = (Integer) jsonObject.get("state");
                webSocketState = state;
                Glog.i("WebSocket","state: " + state);
            } catch (JSONException e1) {
                e1.printStackTrace();
            }

            e.printStackTrace();
        }

    }

    Runnable DevicePlayRunnable = new Runnable() {
        @Override
        public void run() {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
//                    SleepTimeSetUtils.onlyClearSleepTimeSetByCurrent(MainActivity.this, mDataFactory);
                    mAudioPlayer.getDeviceSleepTime();
                }
            });
            Glog.i(TAG,"PlaySwitchReport: playSrc = " + playSrc);
//            if (playSrc == 0) {
            if (((ManticApplication)getApplication()).isPlaySrcZero()) {
                DeviceManager.getInstance().getPropertyKey(SharePreferenceUtil.getDeviceId(MainActivity.this), "play_status", new IoTRequestListener<PropertyData>() {
                    @Override
                    public void onSuccess(HttpStatus code, PropertyData obj, PageInfo info) {
                        if (obj != null) {
                            try {
                                JSONObject jsonObject = new JSONObject(obj.getValue());
                                Glog.i("obj.getValue()", obj.getValue());
                                if ("play_start".equals(jsonObject.optString("status")) || "play_progress".equals(jsonObject.optString("status"))) {
                                    JSONObject payloadJson = jsonObject.optJSONObject("payload");
                                    if (null == payloadJson) {
                                        return;
                                    }
                                    JSONObject dataJson = payloadJson.optJSONObject("data");

                                    if (null == dataJson) {
                                        return;
                                    }

                                    final String token = dataJson.optString("token");
                                    if (TextUtils.isEmpty(token)) {
                                        return;
                                    }
                                    Glog.i("play_status", "token = " + token + "  source_type = " + payloadJson.optString("source_type"));

                                    if (null != mDataFactory.getCurrChannel() && null != mDataFactory.getBeingPlayList()
                                            && mDataFactory.getBeingPlayList().size() == 1) {
                                        String uri = mDataFactory.getCurrChannel().getUri();
                                        String[] uris = uri.split(":");
                                        if (uris[uris.length - 1].equals(token)) {
                                            isSyncVoice = true;
                                            return;
                                        }
                                    }
                                    getBaiduVoiceInfo(token, payloadJson.optString("source_type"));
                                } else {
                                    String nonce = Util.randomString(16);
                                    String access_token = IoTSDKManager.getInstance().getAccessToken().getAccessToken();
                                    String securityAppKey = Util.getSecurityAppKey(nonce, access_token);

                                    Map<String, String> headersMap = new LinkedHashMap<>();
                                    headersMap.put("X-IOT-APP", "d3S3SbItdlYDj4KaOB1qIfuM");
                                    headersMap.put("X-IOT-Signature", nonce + ":" + securityAppKey);
                                    headersMap.put("X-IOT-Token", access_token);
                                    Map<String, String> paramsMap = new LinkedHashMap<>();
                                    paramsMap.put("deviceUuid", SharePreferenceUtil.getDeviceId(mContext));
                                    paramsMap.put("propertyKey", "play_status");
                                    Date date = new Date();
                                    long time = date.getTime();
                                    date.setTime(time);
                                    paramsMap.put("endTime", TimeUtil.Local2UTC(date));
                                    date.setTime(time - 8000);

                                    paramsMap.put("startTime", TimeUtil.Local2UTC(date));
                                    paramsMap.put("limit", "1");

                                    Call<ResponseBody> currentPlayingVoiceInfo = BaiduVoiceRetrofit.getInstance().create(BaiduVoiceServiceApi.class).getCurrentPlayingVoiceInfo(headersMap, paramsMap);
                                    currentPlayingVoiceInfo.enqueue(new Callback<ResponseBody>() {
                                        @Override
                                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                            if (response.isSuccessful() && null == response.errorBody()) {
                                                try {
                                                    String bodyStr = response.body().string();
                                                    Glog.i("currentPlayingVoiceInfo", "response.body().string() = " + bodyStr);
                                                    JSONObject mainJson = new JSONObject(bodyStr);
                                                    JSONArray dataJsonArray = mainJson.optJSONArray("data");
                                                    if (null == dataJsonArray || dataJsonArray.length() <= 0) {
                                                        return;
                                                    }
                                                    JSONObject dataJson = dataJsonArray.optJSONObject(0);
                                                    if (null == dataJson) {
                                                        return;
                                                    }

                                                    String value = dataJson.optString("value");
                                                    int tokenIndex = value.indexOf("token=");
                                                    String token = "";
                                                    if (tokenIndex != -1) {
                                                        String tokenString = value.substring(tokenIndex + 6);
                                                        int rightParenthesisIndex = tokenString.indexOf("}");
                                                        token = tokenString.substring(0, rightParenthesisIndex);
                                                    }

                                                    Glog.i("currentPlayingVoiceInfo", "token = " + value);
                                                    if (TextUtils.isEmpty(token)) {
                                                        return;
                                                    }

                                                    int sourceTypeIndex = value.indexOf("source_type=");
                                                    String sourceType = "";
                                                    if (sourceTypeIndex != -1) {
                                                        String sourceTypeString = value.substring(sourceTypeIndex + 12);
                                                        int commaIndex = sourceTypeString.indexOf(",");
                                                        sourceType = sourceTypeString.substring(0, commaIndex);
                                                    }

                                                    Glog.i("play_status", "token = " + token + "  source_type = " + sourceType);

                                                    if (null != mDataFactory.getCurrChannel() && null != mDataFactory.getBeingPlayList()
                                                            && mDataFactory.getBeingPlayList().size() == 1 && mDataFactory.getCurrChannel().getUri().contains(token)) {
                                                        isSyncVoice = true;
                                                        return;
                                                    }

                                                    getBaiduVoiceInfo(token, sourceType);
                                                } catch (Exception e) {
                                                    isSyncVoice = false;
                                                    Glog.i("currentPlayingVoiceInfo", e.getMessage());
                                                    e.printStackTrace();
                                                }

                                            } else {
                                                isSyncVoice = false;
                                                try {
                                                    Glog.i("currentPlayingVoiceInfo", response.errorBody().string());
                                                } catch (Exception e) {
                                                    e.printStackTrace();
                                                }

                                            }
                                        }

                                        @Override
                                        public void onFailure(Call<ResponseBody> call, Throwable t) {
                                            isSyncVoice = false;
                                        }
                                    });

                                }

                            } catch (Exception e) {
                                isSyncVoice = false;
                                e.printStackTrace();
                            }


                        }
                    }

                    @Override
                    public void onFailed(HttpStatus code) {
                        isSyncVoice = false;
                    }

                    @Override
                    public void onError(IoTException error) {
                        isSyncVoice = false;
                    }
                });
            } else if (playSrc == 1) {//同步下一首数据
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mAudioPlayer.getCurrentChannel();
//                        mAudioPlayer.syncCurrentChannel();
                    }
                }, 1000);

            } else {
                /*final Channel channel = mDataFactory.getCurrChannel();

                if (null != channel) {
                    if (channel.getPlayState() == PLAY_STATE_STOP) { //上一次是停止状态，playing 需要重新拉取
                        mAudioPlayer.startAnimation = true;
                        getBeingPlayList();
                    } else if (channel.getPlayState() == PLAY_STATE_PAUSE) {////上一次是暂停状态，恢复playing状态即可

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mAudioPlayer.startAnimation = true;
                                channel.setPlayState(PLAY_STATE_PLAYING);
                                mDataFactory.addRecentPlay(channel);
                                mDataFactory.notifyRecentPlayListChange(mDataFactory.getRecentPlayList());
                                mAudioPlayer.updateMainAudioPlayerFromChannel(channel);
                            }
                        });
                    }
                }*/
            }
        }
    };

    private void getBaiduVoiceInfo(final String token, String source_type) {
        if (token.contains("organized:east:track")) {//东方音乐
            List<String> uris = new ArrayList<>();
            uris.add(token);
            RequestBody body = MopidyTools.createTrackDetail(uris);
            Call<ResponseBody> call = MainActivity.mpServiceApi.postMopidyTrackDetails(MopidyTools.getHeaders(),body);
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    if (response.isSuccessful() && null == response.errorBody() && null != response.body()) {
                        try {
                            JSONObject mainObject = new JSONObject(response.body().string()); //json 主体
                            JSONObject resuleObject = mainObject.getJSONObject("result"); // result json 主体
                            JSONArray trackArray = resuleObject.getJSONArray(token); //获取第一个item 作为整个TRACKLIST封面
                            if (null != trackArray && trackArray.length() > 0) {
                                JSONObject trackObject = trackArray.getJSONObject(0);
                                final Channel channel = new Channel();
                                channel.setUri(trackObject.optString("uri"));
                                channel.setName(trackObject.optString("name"));
                                channel.setIconUrl(trackObject.getString("mantic_image"));
                                channel.setDuration(trackObject.optLong("length"));
                                if (null != trackObject.optJSONArray("mantic_artists_name")) {
                                    String singer = "";
                                    for (int j = 0; j < trackObject.optJSONArray("mantic_artists_name").length(); j++) {
                                        if (j != trackObject.optJSONArray("mantic_artists_name").length() - 1) {
                                            singer = singer + trackObject.optJSONArray("mantic_artists_name").get(j).toString() + ",";
                                        } else {
                                            singer = singer + trackObject.optJSONArray("mantic_artists_name").get(j).toString();
                                        }
                                    }
                                    channel.setSinger(singer);
                                }
                                channel.setServiceId("east");
                                final List<Channel> channels = new ArrayList<Channel>();
                                if (firstPlayState == 2) {
                                    channel.setPlayState(Channel.PLAY_STATE_PAUSE);
                                } else if (firstPlayState == 1) {
                                    channel.setPlayState(Channel.PLAY_STATE_PLAYING);
                                } else {
                                    channel.setPlayState(Channel.PLAY_STATE_STOP);
                                }
                                channels.add(channel);

                                ChannelPlayListManager.getInstance().updateChannelPlayList(new Callback<ChannelPlayAddRsBean>() {
                                    @Override
                                    public void onResponse(Call<ChannelPlayAddRsBean> call, Response<ChannelPlayAddRsBean> response) {
                                        if (response.isSuccessful() && null == response.errorBody()) {
                                            isSyncVoice = true;
                                            List<AddResult> result = response.body().getResult();
                                            for (int i = 0; i < result.size(); i++) {
                                                channels.get(i).setTlid(result.get(i).getTlid());
                                            }
                                            mDataFactory.setCurrChannel(channel);
                                            mDataFactory.setBeingPlayList((ArrayList<Channel>) channels);
                                            mDataFactory.notifyBeingPlayListChange();
                                            mDataFactory.notifyMyLikeMusicStatusChange();
                                            setFmChannel(false);
                                            mAudioPlayer.updateMainAudioPlayerFromChannel(channel);
                                        } else {
                                            isSyncVoice = false;
                                        }
                                    }

                                    @Override
                                    public void onFailure(Call<ChannelPlayAddRsBean> call, Throwable t) {
                                        isSyncVoice = false;

                                    }
                                }, channels, MainActivity.this);

                            }

                        } catch (JSONException | IOException e) {
                            e.printStackTrace();
                        }
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    ToastUtils.showShortSafe(mContext.getString(R.string.play_failed));
                }
            });
        } else {
            if ("audio_music".equals(source_type) || TextUtils.isEmpty(source_type)) {
                Call<ResponseBody> getMusicDetailCall= BaiduRetrofit.getInstance().create(BaiduServiceApi.class).getTrackInfoBySongId("http://s.xiaodu.baidu.com/v20161223/resource/musicdetail?song_id=" + token);
                getMusicDetailCall.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        ResponseBody errorBody = response.errorBody();
                        if (response.isSuccessful() && null == errorBody) {
                            try {
                                String responseBodyStr = response.body().string();
                                Glog.i("getMusicDetailCall", "responseBodyStr = " + responseBodyStr);
                                JSONObject mainObject = new JSONObject(responseBodyStr);
                                JSONObject dataObject = mainObject.optJSONObject("data");

                                if (TextUtils.isEmpty(dataObject.optString("name"))) {//
                                    getAudioDetail(token);
                                    return;
                                }

                                final Channel channel = new Channel();
                                channel.setName(dataObject.optString("name"));
                                channel.setIconUrl(dataObject.optString("head_image_url"));
                                channel.setDuration(Integer.parseInt(dataObject.optString("duration")) * 1000);
                                channel.setUri("baidu:track:" + token);
                                String artist_name = "";
                                if (dataObject.optJSONArray("singer_name") != null){
                                    if (dataObject.optJSONArray("singer_name").length() == 1) {
                                        artist_name = dataObject.optJSONArray("singer_name").get(0).toString();
                                    } else {
                                        for (int i = 0; i< dataObject.optJSONArray("singer_name").length(); i++){
                                            String singer = dataObject.optJSONArray("singer_name").get(i).toString();
                                            if (i < dataObject.optJSONArray("singer_name").length() - 1) {
                                                artist_name = artist_name + singer + ",";
                                            } else {
                                                artist_name = artist_name + singer;
                                            }
                                        }
                                    }

                                }
                                channel.setSinger(artist_name);

                                final List<Channel> channels = new ArrayList<Channel>();
                                if (firstPlayState == 2) {
                                    channel.setPlayState(Channel.PLAY_STATE_PAUSE);
                                } else if (firstPlayState == 1) {
                                    channel.setPlayState(Channel.PLAY_STATE_PLAYING);
                                } else {
                                    channel.setPlayState(Channel.PLAY_STATE_STOP);
                                }
                                channels.add(channel);

                                ChannelPlayListManager.getInstance().updateChannelPlayList(new Callback<ChannelPlayAddRsBean>() {
                                    @Override
                                    public void onResponse(Call<ChannelPlayAddRsBean> call, Response<ChannelPlayAddRsBean> response) {
                                        if (response.isSuccessful() && null == response.errorBody()) {
                                            isSyncVoice = true;
                                            List<AddResult> result = response.body().getResult();
                                            for (int i = 0; i < result.size(); i++) {
                                                channels.get(i).setTlid(result.get(i).getTlid());
                                            }
                                            mDataFactory.setCurrChannel(channel);
                                            mDataFactory.setBeingPlayList((ArrayList<Channel>) channels);
                                            mDataFactory.notifyBeingPlayListChange();
                                            mDataFactory.notifyMyLikeMusicStatusChange();
                                            setFmChannel(false);
                                            mAudioPlayer.updateMainAudioPlayerFromChannel(channel);
                                        } else {
                                            isSyncVoice = false;
                                        }
                                    }

                                    @Override
                                    public void onFailure(Call<ChannelPlayAddRsBean> call, Throwable t) {
                                        isSyncVoice = false;

                                    }
                                }, channels, MainActivity.this);

                                Glog.i("audio_music", dataObject.optString("name"));
                            } catch (Exception e) {
                                isSyncVoice = false;
                            }
                        } else {
                            isSyncVoice = false;
                            try{
                                Glog.i("getMusicDetailCall", "responseBodyStr = " + errorBody.string());
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        isSyncVoice = false;
                    }
                });

            } else if ("audio_unicast".equals(source_type)) {
                getAudioDetail(token);
            } else if ("audio_live".equals(source_type)) {
                getRadioDetail();
            }
        }
    }



    private void getAudioDetail(final String token) {
        Call<ResponseBody> getMusicDetailCall= BaiduRetrofit.getInstance().create(BaiduServiceApi.class).getTrackInfoByAudioId("http://xiaodu.baidu.com/unicast/api/track?trackid=" + token);
        getMusicDetailCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful() && null == response.errorBody()) {
                    try {
                        JSONObject mainObject = new JSONObject(response.body().string());
                        JSONObject dataObject = mainObject.optJSONObject("data");

                        if (TextUtils.isEmpty(dataObject.optString("name"))) {//
                            getRadioDetail();
                            return;
                        }
                        Glog.i("audio_music", dataObject.optString("name"));
                        final Channel channel = new Channel();
                        channel.setName(dataObject.optString("name"));
                        channel.setIconUrl(dataObject.optString("head_image_url"));
                        channel.setDuration(Integer.parseInt(dataObject.optString("duration")) * 1000);
                        channel.setUri("baidu:audio:" + token);
                        String artist_name = "";
                        JSONArray artistArray = dataObject.optJSONArray("artist");
                        if (artistArray != null){
                            if (artistArray.length() == 1) {
                                artist_name = artistArray.get(0).toString();
                            } else {
                                for (int i = 0; i< artistArray.length(); i++){
                                    String singer = artistArray.get(i).toString();
                                    if (i < artistArray.length() - 1) {
                                        artist_name = artist_name + singer + ",";
                                    } else {
                                        artist_name = artist_name + singer;
                                    }
                                }
                            }

                        }
                        channel.setSinger(artist_name);

                        final List<Channel> channels = new ArrayList<Channel>();
                        if (firstPlayState == 2) {
                            channel.setPlayState(Channel.PLAY_STATE_PAUSE);
                        } else if (firstPlayState == 1) {
                            channel.setPlayState(Channel.PLAY_STATE_PLAYING);
                        } else {
                            channel.setPlayState(Channel.PLAY_STATE_STOP);
                        }
                        channels.add(channel);

                        ChannelPlayListManager.getInstance().updateChannelPlayList(new Callback<ChannelPlayAddRsBean>() {
                            @Override
                            public void onResponse(Call<ChannelPlayAddRsBean> call, Response<ChannelPlayAddRsBean> response) {
                                if (response.isSuccessful() && null == response.errorBody()) {
                                    isSyncVoice = true;
                                    List<AddResult> result = response.body().getResult();
                                    for (int i = 0; i < result.size(); i++) {
                                        channels.get(i).setTlid(result.get(i).getTlid());
                                    }
                                    mDataFactory.setCurrChannel(channel);
                                    mDataFactory.setBeingPlayList((ArrayList<Channel>) channels);
                                    mDataFactory.notifyBeingPlayListChange();
                                    mDataFactory.notifyMyLikeMusicStatusChange();
                                    setFmChannel(false);
                                    mAudioPlayer.updateMainAudioPlayerFromChannel(channel);
                                } else {
                                    isSyncVoice = false;
                                }
                            }

                            @Override
                            public void onFailure(Call<ChannelPlayAddRsBean> call, Throwable t) {
                                isSyncVoice = false;

                            }
                        }, channels, MainActivity.this);


                    } catch (Exception e) {
                        isSyncVoice = false;
                    }
                } else {
                    isSyncVoice = false;
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                isSyncVoice = false;
            }
        });
    }

    private void getRadioDetail() {
        final List<Channel> channels = new ArrayList<Channel>();
        final Channel channel = new Channel();
        channel.setDuration(0);
        channel.setIconUrl("");
        channel.setName("未知");
        channel.setUri("baidu" + ":audio:radio:未知");
        channel.setTimePeriods("00:00-23:59");
        if (firstPlayState == 2) {
            channel.setPlayState(Channel.PLAY_STATE_PAUSE);
        } else if (firstPlayState == 1) {
            channel.setPlayState(Channel.PLAY_STATE_PLAYING);
        } else {
            channel.setPlayState(Channel.PLAY_STATE_STOP);
        }
        channels.add(channel);
        ChannelPlayListManager.getInstance().updateChannelPlayList(new Callback<ChannelPlayAddRsBean>() {
            @Override
            public void onResponse(Call<ChannelPlayAddRsBean> call, Response<ChannelPlayAddRsBean> response) {
                if (response.isSuccessful() && null == response.errorBody()) {
                    isSyncVoice = true;
                    setFmChannel(true);
                    List<AddResult> result = response.body().getResult();
                    for (int i = 0; i < result.size(); i++) {
                        channels.get(i).setTlid(result.get(i).getTlid());
                    }
                    mDataFactory.setCurrChannel(channel);
                    mDataFactory.setBeingPlayList((ArrayList<Channel>) channels);
                    mDataFactory.notifyBeingPlayListChange();
                    mDataFactory.notifyMyLikeMusicStatusChange();

                } else {
                    isSyncVoice = false;
                }
            }

            @Override
            public void onFailure(Call<ChannelPlayAddRsBean> call, Throwable t) {
                isSyncVoice = false;
            }
        }, channels, MainActivity.this);
    }


    /**
     * 刷新token
     */
    public Runnable refreshTokenRunnable = new Runnable() {
        @Override
        public void run() {

            String refreshToken = IoTSDKManager.getInstance().getAccessToken().getRefreshToken();
            String header = "Bearer " + refreshToken;
            AccountServiceApi accountServiceApi = AccountRetrofit.getInstance().create(AccountServiceApi.class);
            Map<String,String> map = new HashMap<String, String>();
            map.put("Authorization",header);
            map.put("Lc", ManticApplication.channelId);
            Call<ResponseBody> call = accountServiceApi.refreshToken(map);
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    if (response.isSuccessful()){
                        Glog.i(TAG,"post -> refreshToken sucess: ");
                        JSONObject mainObject = null; //json 主体
                        try {
                            mainObject = new JSONObject(response.body().string());
                            JSONObject dataObject;
                            if (!AccountUrl.BASE_URL.contains("v2")){
                                dataObject = mainObject;
                            }else {// server v2 接口
                                dataObject = mainObject.getJSONObject("data");
                            }
                            String access_token = dataObject.getString("access_token");
                            String expires_in = dataObject.getString("expires_in");
                            String refresh_token = dataObject.getString("refresh_token");
                            Glog.i(TAG,"post -> refreshToken: " + "access_token:" + access_token + "  expires_in:" + expires_in + "  refresh_token" + refresh_token);
                            if(access_token !=null && expires_in !=null && refresh_token != null){
                                IoTAppConfigMgr.putString(mContext, AccessToken.KEY_ACCESS_TOKEN, access_token);
                                IoTAppConfigMgr.putString(mContext, AccessToken.KEY_REFRESH_TOKEN, refresh_token);
                                IoTAppConfigMgr.putString(mContext, AccessToken.KEY_EXPIRES_IN, expires_in);
                            }
                        } catch (JSONException e) {
                            try {
                                if (mainObject.getString("error")!=null &&mainObject.getString("error").contains("The token expired, was revoked, or the token ID is incorrect")){
                                    //需要重新登录
                                    Glog.i(TAG,"Token 过期需要重新登录");
                                    SharePreferenceUtil.clearSettingsData(mContext);
                                    SharePreferenceUtil.clearUserData(mContext);
                                    IoTSDKManager.getInstance().logout();
                                    needExit = false;
                                    restartLoading();
                                }
                                e.printStackTrace();
                            } catch (JSONException e1) {
                                e1.printStackTrace();
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    Glog.i(TAG,"post -> refreshToken fail: " + t.getMessage());
                }
            });
        }
    };


    public Runnable signalRunnable = new Runnable() {
        @Override
        public void run() {
//            connectWebSocket();

            getDevicePlayMode();

            if (WebSocketController.getInstance().isDisconnect) {
                WebSocketController.getInstance().mConnection.disconnect();
                WebSocketController.getInstance().startWebSocket(SharePreferenceUtil.getDeviceId(mContext));
                ((WebSocketConnection)WebSocketController.getInstance().mConnection).setMessageListener(MainActivity.this);
            } else  {

                String signal = WebSocketCommand.getInstance(SharePreferenceUtil.getDeviceId(mContext)).signalDeviceCommand();
                Glog.i("WebSocket","send message: " + signal);
                WebSocketController.getInstance().mConnection.sendTextMessage(signal);
            }


            handler.postDelayed(this,signalTime);
        }
    };
    private DefinitionChannelEditFragment selectedFragment;

    @Override
    public void setSelectedFragment(DefinitionChannelEditFragment selectedFragment) {
        this.selectedFragment = selectedFragment;
    }

    public void setAudioPlayerStatus(int status) {
        if (status == ReboundScrollView.SCROLL_UP) {
            mAudioPlayer.setAudioPlayerBar(false);
        } else {
            mAudioPlayer.setAudioPlayerBar(true);
        }
    }

    public void setAudioPlayerVisible(boolean isVisible) {
        if (isVisible) {
            mAudioPlayer.setAudioPlayerBarVisable(true);
        } else {
            mAudioPlayer.setAudioPlayerBarVisable(false);
        }
    }


    public void setBottomSheetExpanded() {
        mAudioPlayer.expanded();
    }


    public void setDrawerLayoutMode(boolean canSlideslipe) {
        if (!canSlideslipe) {
            mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        } else {
            mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
        }
    }


    private long exitTime = 0;

    public void exit() {
        if ((System.currentTimeMillis() - exitTime) > 2000) {
            Toast.makeText(mContext, "再按一次退出程序",
                    Toast.LENGTH_SHORT).show();
            exitTime = System.currentTimeMillis();
        } else {
            finish();
//            System.exit(0);
//            android.os.Process.killProcess(android.os.Process.myPid());
        }
    }

    public void setFmChannel(boolean isFmChannel) {
        mAudioPlayer.audioPlayerPlayListItemAdapter.setFmChannel(isFmChannel);
    }

    public void setCurrentFinish() {
        mAudioPlayer.setCurrentFinish(true);
    }


    // server v2 接口
    private void checkRefreshToken(){
        String refreshToken = IoTSDKManager.getInstance().getAccessToken().getRefreshToken();
        Map<String,String> map = new HashMap<String, String>();
        map.put("Authorization","Bearer " + refreshToken);
        map.put("Lc", ManticApplication.channelId);
        AccountServiceApi accountServiceApi = AccountRetrofit.getInstance().create(AccountServiceApi.class);
        Call<ResponseBody> call = accountServiceApi.checkToken(map);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()){
                    try {
                        JSONObject mainObject = new JSONObject(response.body().string());
                        JSONObject dataObject = mainObject.getJSONObject("data");
                        Glog.i(TAG,"dataObject ---->: " + dataObject.toString());

                        if (Objects.equals(mainObject.getString("retcode"), "1") ||dataObject.getInt("count_down")<=7200){ //refreshToken 小于20小时重新登录
                            Glog.i(TAG,"需要重新登录！");
                            SharePreferenceUtil.clearSettingsData(mContext);
                            SharePreferenceUtil.clearUserData(mContext);
                            IoTSDKManager.getInstance().logout();
                            needExit = false;
                            restartLoading();
                        }

                    }catch (JSONException | IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });
    }
    // server v2 接口


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void sendSoundCompleted(){
        this.sendBroadcast(new Intent("sound_completed"));
    }
}
