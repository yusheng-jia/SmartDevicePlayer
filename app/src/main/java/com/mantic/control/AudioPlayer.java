package com.mantic.control;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PagerSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.ScaleAnimation;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.baidu.iot.sdk.HttpStatus;
import com.baidu.iot.sdk.IoTException;
import com.baidu.iot.sdk.IoTRequestListener;
import com.baidu.iot.sdk.IoTSDKManager;
import com.baidu.iot.sdk.model.ControlResult;
import com.baidu.iot.sdk.model.PageInfo;
import com.baidu.iot.sdk.net.RequestMethod;
import com.mantic.antservice.util.ToastUtils;
import com.mantic.control.activity.MainActivity;
import com.mantic.control.adapter.AudioPlayerMoreAdapter;
import com.mantic.control.adapter.AudioPlayerPlayListItemAdapter;
import com.mantic.control.adapter.AudioSleepAdapter;
import com.mantic.control.api.channelplay.bean.AddResult;
import com.mantic.control.api.channelplay.bean.ChannelPlayAddRsBean;
import com.mantic.control.api.channelplay.bean.ChannelPlayListRsBean;
import com.mantic.control.api.channelplay.bean.GetCurrentChannelPlayRsBean;
import com.mantic.control.api.channelplay.bean.SetCurrentChannelPlayRsBean;
import com.mantic.control.api.mopidy.MopidyTools;
import com.mantic.control.api.mychannel.MyChannelOperatorRetrofit;
import com.mantic.control.api.mychannel.MyChannelOperatorServiceApi;
import com.mantic.control.api.mychannel.bean.MyChannelAddRsBean;
import com.mantic.control.api.mychannel.bean.MyChannelDeleteRsBean;
import com.mantic.control.api.mylike.bean.MyLikeAddRsBean;
import com.mantic.control.api.mylike.bean.MyLikeDeleteRsBean;
import com.mantic.control.data.Channel;
import com.mantic.control.data.DataFactory;
import com.mantic.control.data.MyChannel;
import com.mantic.control.decoration.AudioPlayerPlayListItemDecoration;
import com.mantic.control.decoration.ChanneDetailMoreItemDecoration;
import com.mantic.control.entiy.SleepInfo;
import com.mantic.control.fragment.ChannelAddFragment;
import com.mantic.control.fragment.ChannelDetailsFragment;
import com.mantic.control.fragment.FragmentEntrust;
import com.mantic.control.fragment.SearchResultFragment;
import com.mantic.control.itemtouch.DefaultItemTouchHelpCallback;
import com.mantic.control.itemtouch.DefaultItemTouchHelper;
import com.mantic.control.manager.ChannelPlayListManager;
import com.mantic.control.manager.DragLayoutManager;
import com.mantic.control.manager.MyChannelManager;
import com.mantic.control.manager.MyLikeManager;
import com.mantic.control.musicservice.MyMusicService;
import com.mantic.control.musicservice.XimalayaSoundData;
import com.mantic.control.utils.AudioPlayerUtil;
import com.mantic.control.utils.DensityUtils;
import com.mantic.control.utils.GlideImgManager;
import com.mantic.control.utils.Glog;
import com.mantic.control.utils.SharePreferenceUtil;
import com.mantic.control.utils.SleepTimeSetUtils;
import com.mantic.control.utils.TimeUtil;
import com.mantic.control.utils.Util;
import com.mantic.control.utils.Utility;
import com.mantic.control.widget.CustomDialog;
import com.ximalaya.ting.android.opensdk.constants.DTransferConstants;
import com.ximalaya.ting.android.opensdk.datatrasfer.CommonRequest;
import com.ximalaya.ting.android.opensdk.datatrasfer.IDataCallBack;
import com.ximalaya.ting.android.opensdk.model.album.Album;
import com.ximalaya.ting.android.opensdk.model.album.BatchAlbumList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.baidu.iot.sdk.HttpStatus.PUT_OR_POST_SUCCESS;
import static com.mantic.control.data.Channel.PLAY_STATE_PAUSE;
import static com.mantic.control.data.Channel.PLAY_STATE_PLAYING;
import static com.mantic.control.data.Channel.PLAY_STATE_STOP;

/**
 * Created by root on 17-4-17.
 */
public class AudioPlayer {
    private static final String TAG = "AudioPlayer";


    public static final int PLAY_PRE = 1;
    public static final int PLAY_NEXT = 2;
    public static final int PLAY_COOMAAN_PRE = 3;
    public static final int PLAY_COOMAAN_NEXT = 4;


    private RelativeLayout mAudioPlayer;
    private RelativeLayout mDefaultAudioPlayer;
    private RelativeLayout mAudioPlayerBottomSheet;
    private RelativeLayout mAudioPlayerBar;
    private ImageButton mAudioPlayerBarPlayPause;
    private ImageButton mAudioPlayerPlayPauseBtn;
    private ImageButton mAudioPlayerPreBtn;
    private ImageButton mAudioPlayerNextBtn;
    private TextView mAudioPlayerChannelName;
    private TextView mAudioPlayerSongName;
    private TextView mAudoPlayerSingerName;
    private SeekBar mVolumeSeekBar;
    private TextView mPlaySeekBarDuration;
    private TextView mPlaySeekBarCurTime;
    private SeekBar mPlaySeekBar;
    private ImageButton audio_player_love_btn;
    private LinearLayout ll_audio_alarm;
    private TextView tv_audio_alarm;
    public BottomSheetBehavior mBottomSheetBehavior;
    private RecyclerView mBeingPlayListView;
    private RecyclerView audioPlayerPlaylist = null;
    private RecyclerView rcv_sleep_list;
    private AudioSleepAdapter mAudioSleepAdapter;
    public AudioPlayerPlayListItemAdapter audioPlayerPlayListItemAdapter;
    /**
     * 滑动拖拽的帮助类
     */
    private DefaultItemTouchHelper itemTouchHelper;

    private ScrollEnabledLinearLayoutManager mLinearLayoutManager;
    private BeingPlayAudioListAdapter mBeingPlayAudioListAdapter;
    private TextView audioPlayerPlaylistLoopCount;
    private DataFactory mDataFactory;
    private DataFactory.ChannelControlListener mChannelControlListener;
    private ArrayList<Channel> mBeingPlayList;
    private Channel mCurrChannel;
    private int mCurrChannelIndex = -1;

    private String mAudioPlayerAlbumCoverUrl;
    private ImageView mAudioPlayerAlbumCover;
    //    private ImageView mAudioPlayerAlbumCoverBlurBackground; //不要albumcover
    private Bitmap mAudioPlayerAlbumCoverBlurBackgroundBitmap;

    private Activity mActivity;
    private Context mContext;

    private boolean isNeedMoveUpdate = false;
    private Animation animation = null;
    private boolean isAnimationEnd = true;
    private int currentVolume;
    private int sleep_remaining = 0;//睡眠剩余时间

    private Dialog mDialog;
    private Dialog mPlayListDialog;
    private Dialog mSleepDialog;
    private ScaleAnimation pauseAnimation;
    private ScaleAnimation resumeAnimation;
    public Handler prgoressHandler = new Handler();
    private ImageButton audio_player_playlist_loop_btn;
    private ImageButton audio_player_playlist_empty_btn;
    private ImageButton audio_player_playlist_add_btn;

    public AudioPlayer(Activity activity, Context context) {
        this.mActivity = activity;
        this.mContext = context;
        this.mDataFactory = DataFactory.newInstance(this.mActivity.getApplicationContext());
        this.mBeingPlayList = this.mDataFactory.getBeingPlayList();

        mDataFactory.registerBeingPlayListListener(new DataFactory.BeingPlayListListener() {
            @Override
            public void callback(ArrayList<Channel> channels) {
                audioPlayerPlayListItemAdapter.setAudioPlayList(channels);
                if (null != audioPlayerPlaylistLoopCount) {
                    audioPlayerPlaylistLoopCount.setText("(" + channels.size() + ")");
                }
            }
        });

        mDataFactory.registerUpdateAudioPlayerByBaiduDurationListener(new DataFactory.OnUpdateAudioPlayerByBaiduDurationListener() {
            @Override
            public void updateAudioPlayerByBaiduDuration() {
                updateMainAudioPlayerFromChannel(mDataFactory.getCurrChannel());
            }
        });

        mDataFactory.registerSleepStateChangeListener(new DataFactory.OnSleepStateChangeListener() {
            @Override
            public void sleepStateOrClearTimeChange(boolean isSleepState) {
                if (isSleepState) {
                    ((ManticApplication)mActivity.getApplication()).setSleepTimeOn(false);
                    SleepTimeSetUtils.onlyClearSleepTimeSet(mActivity, mDataFactory);
                } else {
                    if (null != mAudioSleepAdapter) {
                        sleep_remaining = 0;
                        mAudioSleepAdapter.setSleepInfoList(mDataFactory.getSleepInfoList());
                        if (null != tv_audio_alarm) {
                            tv_audio_alarm.setVisibility(View.GONE);
                        }
                    }
                }
            }

            @Override
            public void resetTimeChange(int time) {
                prgoressHandler.removeCallbacks(updateSleepRemainRunnable);
                if (time >= 0) {
                    sleep_remaining = time * 1000;
                    prgoressHandler.post(updateSleepRemainRunnable);
                } else if (time == -1) {
                    sleep_remaining = 0;
                    if (null != tv_audio_alarm) {
                        tv_audio_alarm.setText(TimeUtil.secondToTime((int)mDataFactory.getCurrChannel().getDuration() - progress));
                        tv_audio_alarm.setVisibility(View.VISIBLE);
                    }
                }
            }
        });
    }

    public Runnable updateSleepRemainRunnable = new Runnable() {
        @Override
        public void run() {
            prgoressHandler.postDelayed(this, TIME_UPDATE);
            if (mDataFactory.getCurrChannel() != null && (((ManticApplication)mActivity.getApplication()).isSleepTimeOn())) {
                if (sleep_remaining > 1000) {
                    sleep_remaining = sleep_remaining - 1000;
                    tv_audio_alarm.setVisibility(View.VISIBLE);
                    tv_audio_alarm.setText(TimeUtil.secondToTime(sleep_remaining));
                } else {
                    tv_audio_alarm.setVisibility(View.GONE);
                    prgoressHandler.removeCallbacks(this);
                }
            } else {
                tv_audio_alarm.setVisibility(View.GONE);
                prgoressHandler.removeCallbacks(this);
            }
        }
    };

    public void setAudioPlayerBarVisable(boolean isVisible) {
        if (isVisible) {
            if (null != mDataFactory.getCurrChannel()) {
                mAudioPlayerBottomSheet.setVisibility(View.VISIBLE);
                mDefaultAudioPlayer.setVisibility(View.GONE);
            } else {
                mDefaultAudioPlayer.setVisibility(View.VISIBLE);
                mAudioPlayerBottomSheet.setVisibility(View.GONE);
            }

        } else {
            if (mAudioPlayerBottomSheet.getVisibility() == View.VISIBLE) {
                mAudioPlayerBottomSheet.setVisibility(View.GONE);
            } else {
                mDefaultAudioPlayer.setVisibility(View.GONE);
            }
        }
    }

    public void setAudioPlayerBar(boolean isVisible) {
        if (animation != null) {
            animation.cancel();
            animation = null;
        }
        if (isVisible) {
            if (mAudioPlayerBottomSheet.getVisibility() == View.GONE && isAnimationEnd) {
                animation = AnimationUtils.loadAnimation(mActivity, R.anim.translate_visible_audio_player_bar);
                mAudioPlayerBottomSheet.startAnimation(animation);
                animation.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {
                        mAudioPlayerBottomSheet.setVisibility(View.VISIBLE);
                        isAnimationEnd = false;
                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        isAnimationEnd = true;
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });
            }

        } else {
            if (mAudioPlayerBottomSheet.getVisibility() == View.VISIBLE && isAnimationEnd) {
                animation = AnimationUtils.loadAnimation(mActivity, R.anim.translate_gone_audio_player_bar);
                animation.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {
                        isAnimationEnd = false;
                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        mAudioPlayerBottomSheet.setVisibility(View.GONE);
                        isAnimationEnd = true;
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {
                    }
                });
                mAudioPlayerBottomSheet.startAnimation(animation);
            }

        }
    }

    public void init() {
        Glog.i(TAG, "init");
        this.mAudioPlayer = (RelativeLayout) this.mActivity.findViewById(R.id.audio_player);
        this.mAudioPlayer.setAlpha(0);
        this.mAudioPlayer.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // 屏蔽界面长按 触发seekbar的点击效果
                return true;
            }
        });

        this.mDefaultAudioPlayer = (RelativeLayout) this.mActivity.findViewById(R.id.audio_player_bottom_sheet_default);
        //我喜欢
        audio_player_love_btn = (ImageButton) this.mAudioPlayer.findViewById(R.id.audio_player_love_btn);
        audio_player_love_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (null != mDataFactory.getCurrChannel() && mDataFactory.getCurrChannel().getUri().contains("radio:")) {
                    MyChannelManager.getInstance().getAlbumDetail(new Callback<ResponseBody>() {
                        @Override
                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                            if (response.isSuccessful() && null == response.errorBody()) {

                                final MyChannel mychannel = new MyChannel();
                                try {
                                    JSONObject mainObject = new JSONObject(response.body().string()); //json 主体
                                    JSONObject resultObject = mainObject.getJSONObject("result"); // result json 主体
                                    JSONArray jsonArray = resultObject.getJSONArray(mDataFactory.getCurrChannel().getUri());
                                    JSONObject jsonObject = jsonArray.getJSONObject(0);
                                    if (null != jsonObject.optJSONArray("mantic_artists_name")) {
                                        String singer = "";
                                        for (int j = 0; j < jsonObject.optJSONArray("mantic_artists_name").length(); j++) {
                                            if (j != jsonObject.optJSONArray("mantic_artists_name").length() - 1) {
                                                singer = singer + jsonObject.optJSONArray("mantic_artists_name").get(j).toString() + ",";
                                            } else {
                                                singer = singer + jsonObject.optJSONArray("mantic_artists_name").get(j).toString();
                                            }
                                        }
                                        mychannel.setSingerName(singer);
                                    }
                                    mychannel.setPlayCount(jsonObject.optString("mantic_play_count"));
                                    mychannel.setChannelName(jsonObject.optString("name"));
                                    mychannel.setChannelId(jsonObject.optString("mantic_image"));
                                    mychannel.setChannelType(2);
                                    mychannel.setmTotalCount(jsonObject.optInt("mantic_num_tracks"));
                                    mychannel.setUrl("qingting:album:" + jsonObject.optString("uri"));
                                    if (mDataFactory.getCurrChannel().getUri().contains("qingting")) {
                                        mychannel.setMusicServiceId("qingting");
                                        mychannel.setAlbumId(jsonObject.optString("uri"));
                                    }
                                    mychannel.setChannelCoverUrl(jsonObject.optString("mantic_image"));
                                    mychannel.setChannelIntro(jsonObject.optString("mantic_describe"));
                                    if (mDataFactory.isExistMyRadio(mDataFactory.getCurrChannel().getUri())) {
                                        mychannel.setUrl(mDataFactory.radioMyChannelUrl(mDataFactory.getCurrChannel().getUri()));

                                        Call<MyChannelDeleteRsBean> deleteCall = MyChannelOperatorRetrofit.getInstance().create(MyChannelOperatorServiceApi.class).postMyChannelDeleteQuest(MopidyTools.getHeaders(),Util.createDeleteRqBean(mychannel, mActivity));
                                        MyChannelManager.getInstance().deleteMyChannel(deleteCall, new Callback() {
                                            @Override
                                            public void onResponse(Call call, Response response) {
                                                if (response.isSuccessful() && null == response.errorBody()) {
                                                    mDataFactory.notifyOperatorResult(mActivity.getString(R.string.success_delete_this_channel), true);
                                                    mDataFactory.removeMyChannel(mychannel);
                                                    audio_player_love_btn.setBackgroundResource(R.drawable.audio_player_fm_love_btn_nor);
                                                    mDataFactory.notifyMyChannelStateChange(false);
                                                } else {
                                                    mDataFactory.notifyOperatorResult(mActivity.getString(R.string.failed_delete_this_channel), false);
                                                }
                                            }

                                            @Override
                                            public void onFailure(Call call, Throwable t) {
                                                mDataFactory.notifyOperatorResult(mActivity.getString(R.string.failed_delete_this_channel), false);
                                            }
                                        }, mychannel);

                                    } else {
                                        Call<MyChannelAddRsBean> addCall = MyChannelOperatorRetrofit.getInstance().create(MyChannelOperatorServiceApi.class).
                                                postMyChannelAddQuest(MopidyTools.getHeaders(),Util.createAddRqBean(mychannel, mActivity));
                                        MyChannelManager.addMyChannel(addCall, new Callback<MyChannelAddRsBean>() {
                                            @Override
                                            public void onResponse(Call<MyChannelAddRsBean> call, Response<MyChannelAddRsBean> response) {
                                                if (response.isSuccessful() && null == response.errorBody()) {
                                                    mychannel.setUrl(response.body().result.uri);
                                                    mDataFactory.notifyOperatorResult(mActivity.getString(R.string.success_add_this_channel), true);
                                                    mDataFactory.addMyChannel(mychannel);
                                                    audio_player_love_btn.setBackgroundResource(R.drawable.audio_player_fm_love_btn_pre);
                                                    mDataFactory.notifyMyChannelStateChange(true);
                                                } else {
                                                    mDataFactory.notifyOperatorResult(mActivity.getString(R.string.failed_add_this_channel), true);
                                                }
                                            }

                                            @Override
                                            public void onFailure(Call call, Throwable t) {
                                                mDataFactory.notifyOperatorResult(mActivity.getString(R.string.failed_add_this_channel), true);
                                            }
                                        }, mychannel);
                                    }

                                } catch (Exception e) {
                                    Glog.i(TAG, "onResponse: " + e.getMessage());
                                }
                            } else {

                            }
                        }

                        @Override
                        public void onFailure(Call<ResponseBody> call, Throwable t) {

                        }
                    }, mDataFactory.getCurrChannel().getUri());

                    return;
                }


                if (null != mDataFactory.getCurrChannel() && mDataFactory.getCurrChannel().getUri().contains(":audio:")) {
                    ToastUtils.showShortSafe(mActivity.getString(R.string.no_copyright_collection));
                    return;
                }

                if (mDataFactory.isExistMyLikeMusic(mDataFactory.getCurrChannel())) {
                    MyLikeManager.getInstance().deleteMyLike(new Callback<MyLikeDeleteRsBean>() {
                        @Override
                        public void onResponse(Call<MyLikeDeleteRsBean> call, Response<MyLikeDeleteRsBean> response) {
                            if (response.isSuccessful() && null == response.errorBody()) {
                                removeLikeMusic(mDataFactory.getCurrChannel());
                                audio_player_love_btn.setBackgroundResource(R.drawable.audio_player_love_btn_nor);
                                ToastUtils.showShortSafe(mActivity.getString(R.string.delete_favourite));
                                mDataFactory.notifyMyLikeMusicListChange();
                                mDataFactory.notifyMyLikeMusicStatusChange();
                            } else {
                                ToastUtils.showShortSafe(mActivity.getString(R.string.failed_delete_favourite));
                            }
                        }

                        @Override
                        public void onFailure(Call<MyLikeDeleteRsBean> call, Throwable t) {
                            ToastUtils.showShortSafe(mActivity.getString(R.string.failed_delete_favourite));
                        }
                    }, mDataFactory.getCurrChannel(), mActivity);


                } else {
                    MyLikeManager.getInstance().addMyLike(new Callback<MyLikeAddRsBean>() {
                        @Override
                        public void onResponse(Call<MyLikeAddRsBean> call, Response<MyLikeAddRsBean> response) {
                            if (response.isSuccessful() && null == response.errorBody()) {
                                addLikeMusic(mDataFactory.getCurrChannel());
                                audio_player_love_btn.setBackgroundResource(R.drawable.audio_player_love_btn_pre);
                                ToastUtils.showShortSafe(mActivity.getString(R.string.add_favourite));
                                mDataFactory.notifyMyLikeMusicListChange();
                                mDataFactory.notifyMyLikeMusicStatusChange();
                            } else {
                                ToastUtils.showShortSafe(mActivity.getString(R.string.failed_add_favourite));
                            }
                        }

                        @Override
                        public void onFailure(Call<MyLikeAddRsBean> call, Throwable t) {
                            ToastUtils.showShortSafe(mActivity.getString(R.string.failed_add_favourite));
                        }
                    }, mDataFactory.getCurrChannel(), mActivity);


                }

            }
        });

        ll_audio_alarm = (LinearLayout) this.mAudioPlayer.findViewById(R.id.ll_audio_alarm);
        tv_audio_alarm = (TextView) this.mAudioPlayer.findViewById(R.id.tv_audio_alarm);
        ll_audio_alarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSleepDialog.show();
            }
        });

        mSleepDialog = Utility.getDialog(this.mActivity, R.layout.audio_sleep_timelist);

        RelativeLayout rl_sleep_cancel = (RelativeLayout) mSleepDialog.findViewById(R.id.rl_sleep_cancel);
        rl_sleep_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSleepDialog.dismiss();
            }
        });

        rcv_sleep_list = (RecyclerView) mSleepDialog.findViewById(R.id.rcv_sleep_list);
        mAudioSleepAdapter = new AudioSleepAdapter(mActivity);

        rcv_sleep_list.setAdapter(mAudioSleepAdapter);
        rcv_sleep_list.setLayoutManager(new LinearLayoutManager(this.mActivity));
        rcv_sleep_list.addItemDecoration(new AudioPlayerPlayListItemDecoration(this.mActivity));

        //toolbar
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            RelativeLayout toolBar = (RelativeLayout) this.mAudioPlayer.findViewById(R.id.audio_player_toolbar);
            int height = DensityUtils.getStatusBarHeight(mActivity);
            FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) toolBar.getLayoutParams();
            lp.setMargins(0, height, 0, 0);
        }

//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
//            View dectorView = mActivity.getWindow().getDecorView();
//            int option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN |
//                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
//            dectorView.setSystemUiVisibility(option);
//            mActivity.getWindow().setStatusBarColor(Color.RED);
//
//        }else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT){
//            WindowManager.LayoutParams localLayoutParams = mActivity.getWindow().getAttributes();
//            localLayoutParams.flags = (WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS | localLayoutParams.flags);
//        }
        //底部AudioPlayerBar
        initAudioPlayerBar();

        //
        this.mAudioPlayerPreBtn = (ImageButton) this.mActivity.findViewById(R.id.audio_player_pre_btn);
        this.mAudioPlayerPreBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (((ManticApplication)mActivity.getApplication()).isPlaySrcZero()) {
                    AudioPlayerUtil.playCodeByPlayScrZero(mActivity, null, PLAY_PRE);
                } else {
                    AudioPlayerUtil.playCode(mDataFactory, PLAY_COOMAAN_PRE, mActivity);
                    if (mDataFactory.getCurrChannel().getPlayState() == 1){ //暂停状态由于点击上下首要播放，所以需要动画
                        startAnimation = true;
                    }
                }

                setControlEnable(false);

            }
        });
        this.mAudioPlayerNextBtn = (ImageButton) this.mActivity.findViewById(R.id.audio_player_next_btn);
        this.mAudioPlayerNextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (((ManticApplication)mActivity.getApplication()).isPlaySrcZero()) {
                    AudioPlayerUtil.playCodeByPlayScrZero(mActivity, null, PLAY_NEXT);
                } else {
                    AudioPlayerUtil.playCode(mDataFactory, PLAY_COOMAAN_NEXT, mActivity);
                    if (mDataFactory.getCurrChannel().getPlayState() == 1){ //暂停状态由于点击上下首要播放，所以需要动画
                        startAnimation = true;
                    }
                }

                setControlEnable(false);
            }
        });
        this.mAudioPlayerSongName = (TextView) this.mActivity.findViewById(R.id.audio_player_song_name);
        this.mAudoPlayerSingerName = (TextView) this.mActivity.findViewById(R.id.audo_player_singer_name);
        this.mAudioPlayerChannelName = (TextView) this.mActivity.findViewById(R.id.audio_player_channel_name);
        AudioHelper.setTextMarquee(this.mAudioPlayerSongName);
        AudioHelper.setTextMarquee(this.mAudoPlayerSingerName);

        //底部滚动RecycleView
        this.mAudioPlayerBottomSheet = (RelativeLayout) this.mActivity.findViewById(R.id.audio_player_bottom_sheet);
        this.mBottomSheetBehavior = BottomSheetBehavior.from(this.mAudioPlayerBottomSheet);
        this.mBeingPlayListView = (RecyclerView) this.mActivity.findViewById(R.id.being_play_audio_list);
        this.mLinearLayoutManager = new ScrollEnabledLinearLayoutManager(this.mActivity);
        this.mLinearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        this.mBeingPlayListView.setLayoutManager(this.mLinearLayoutManager);

        this.mBeingPlayAudioListAdapter = new BeingPlayAudioListAdapter(this.mActivity);

        //播放进度条
        mPlaySeekBarDuration = (TextView) mAudioPlayer.findViewById(R.id.audio_player_totle_time);
        mPlaySeekBarCurTime = (TextView) mAudioPlayer.findViewById(R.id.audio_player_curr_time);
        mPlaySeekBar = (SeekBar) mAudioPlayer.findViewById(R.id.audio_player_progress_seekbar);
        //屏蔽拖动事件
        mPlaySeekBar.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });
//        mPlaySeekBar.setEnabled(false);//暂时禁止拖动

        //this.mBeingPlayListView.setAdapter(this.mBeingPlayAudioListAdapter);
        this.updateAudioPlayerBottomSheet();

        /*
        if(this.mBeingPlayAudioListAdapter.getItemCount() == 0 && this.mAudioPlayerBottomSheet.getVisibility() != View.GONE){
            this.mAudioPlayerBottomSheet.setVisibility(View.GONE);
        }
        */

        //底部滑动 处理 播放歌曲
        initBeingListPagerSnap();
        this.mBottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                Glog.i(TAG, "onStateChanged .. newState" + newState);
                if (newState != BottomSheetBehavior.STATE_COLLAPSED && mAudioPlayerBar.getVisibility() == View.VISIBLE) {
                    mAudioPlayerBar.setVisibility(View.GONE);
//                    mBottomSheetBehavior.setHideable(false);
//                    DragLayoutManager.getAppManager().addDragLyout(TAG);
                    ((MainActivity) mActivity).setDrawerLayoutMode(false);
                } else if (newState == BottomSheetBehavior.STATE_COLLAPSED && mAudioPlayerBar.getVisibility() == View.GONE) {
                    mAudioPlayerBar.setVisibility(View.VISIBLE);
//                    mBottomSheetBehavior.setHideable(true);

//                    DragLayoutManager.getAppManager().removeLastDragLyout();
                    if (!DragLayoutManager.getAppManager().getDragLayoutLockeEnable()) {
                        ((MainActivity) mActivity).setDrawerLayoutMode(true);
                    }
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                Glog.i(TAG, "onStateChanged .. slideOffset" + slideOffset);
                mAudioPlayer.setAlpha(slideOffset);
                if (slideOffset == 1.0) {
                    mAudioPlayerBar.setVisibility(View.GONE);
                } else if (slideOffset == 0.0){
                    mAudioPlayerBar.setVisibility(View.VISIBLE);
                }
            }
        });

//        this.mAudioPlayerAlbumCoverBlurBackground = (ImageView) this.mActivity.findViewById(R.id.audio_player_album_cover_blur_background);
        this.mAudioPlayerAlbumCover = (ImageView) this.mActivity.findViewById(R.id.audio_player_album_cover);
        ViewGroup.LayoutParams para;
        para = this.mAudioPlayerAlbumCover.getLayoutParams();

        this.mAudioPlayerAlbumCover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAudioPlayerMore();
            }
        });

        //
        ImageButton audioPlayerFoldBtn = (ImageButton) this.mActivity.findViewById(R.id.audio_player_fold_btn);
        audioPlayerFoldBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            }
        });


        ImageButton audioPlayerPlaylistBtn = (ImageButton) this.mActivity.findViewById(R.id.audio_player_playlist_btn);
        audioPlayerPlaylistBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAudioPlayerPlaylist();
            }
        });

        mPlayListDialog = Utility.getDialog(this.mActivity, R.layout.audio_player_playlist);
        audio_player_playlist_loop_btn = (ImageButton) mPlayListDialog.findViewById(R.id.audio_player_playlist_loop_btn);
        audio_player_playlist_empty_btn = (ImageButton) mPlayListDialog.findViewById(R.id.audio_player_playlist_empty_btn);
        audio_player_playlist_add_btn = (ImageButton) mPlayListDialog.findViewById(R.id.audio_player_playlist_add_btn);
        audioPlayerPlaylist = (RecyclerView) mPlayListDialog.findViewById(R.id.audio_player_playlist);
        audioPlayerPlayListItemAdapter = new AudioPlayerPlayListItemAdapter(mActivity);
        audioPlayerPlayListItemAdapter.setOnAudioPlayerListDismissListener(onAudioPlayerListDismissListener);
        audioPlayerPlaylist.setAdapter(audioPlayerPlayListItemAdapter);
        audioPlayerPlaylist.setLayoutManager(new LinearLayoutManager(this.mActivity));
        audioPlayerPlaylist.addItemDecoration(new AudioPlayerPlayListItemDecoration(this.mActivity));
        audioPlayerPlaylistLoopCount = (TextView) mPlayListDialog.findViewById(R.id.audio_player_playlist_loop_count);
        audioPlayerPlaylistLoopCount.setText("(" + this.mBeingPlayList.size() + ")");
        mPlayListDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                if (isNeedMoveUpdate) {
                    ChannelPlayListManager.getInstance().moveUpdateChannelPlayList(new Callback<ResponseBody>() {
                        @Override
                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                            if (response.isSuccessful() && null == response.errorBody()) {
                                isNeedMoveUpdate = false;
                            } else {
                                ToastUtils.showShortSafe(mActivity.getString(R.string.fail_change_play_list_position));
                            }
                        }

                        @Override
                        public void onFailure(Call<ResponseBody> call, Throwable t) {
                            ToastUtils.showShortSafe(mActivity.getString(R.string.fail_change_play_list_position));
                        }
                    }, mBeingPlayList, mActivity);
                }
            }
        });

        // 把ItemTouchHelper和itemTouchHelper绑定
        itemTouchHelper = new DefaultItemTouchHelper(onItemTouchCallbackListener);
        itemTouchHelper.attachToRecyclerView(audioPlayerPlaylist);
        audioPlayerPlayListItemAdapter.setItemTouchHelper(itemTouchHelper);
        itemTouchHelper.setDragEnable(false);
        itemTouchHelper.setSwipeEnable(true);
        itemTouchHelper.setDragBackGround(R.drawable.being_play_item_background);
        itemTouchHelper.setDragedBackGroundColor(Color.parseColor("#00dfdfdf"));


        ImageButton audioPlayerMoreBtn = (ImageButton) this.mActivity.findViewById(R.id.audio_player_more_btn);

        mVolumeSeekBar = (SeekBar) this.mAudioPlayer.findViewById(R.id.audio_player_more_volume_seekbar);
        mVolumeSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                final int process = seekBar.getProgress();
//                Glog.i(TAG,"progress  : " + process);

//                Glog.i(TAG,"value  : " + value);
                controlVolume(process);

            }
        });

        audioPlayerMoreBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAudioPlayerMore();
            }
        });

        this.mChannelControlListener = new DataFactory.ChannelControlListener() {
            @Override
            public void preChannelControl() {
//                setControlEnable(false);
            }

            @Override
            public void afterChannelControl() {

//                setControlEnable(true);
            }

            @Override
            public void beginChannelControl(int index) {
                SharePreferenceUtil.setSharePreferenceData(mActivity, "Mantic", "lastIndex", index + "");
//                mBeingPlayListView.scrollToPosition(index);
                mLinearLayoutManager.scrollToPositionWithOffset(index, 0);
            }
        };
        this.mDataFactory.addChannelControlListener(this.mChannelControlListener);

    }

    AudioPlayerPlayListItemAdapter.OnAudioPlayerListDismissListener onAudioPlayerListDismissListener = new AudioPlayerPlayListItemAdapter.OnAudioPlayerListDismissListener() {
        @Override
        public void dismiss() {
            mPlayListDialog.dismiss();
            collapsed();
        }
    };

    private DefaultItemTouchHelpCallback.OnItemTouchCallbackListener onItemTouchCallbackListener = new DefaultItemTouchHelpCallback.OnItemTouchCallbackListener() {
        @Override
        public void onSwiped(int adapterPosition) {
            return;
        }

        @Override
        public boolean onMove(int srcPosition, int targetPosition) {
            if (mBeingPlayList != null) {
                isNeedMoveUpdate = true;
                // 更换数据源中的数据Item的位置
                Collections.swap(mBeingPlayList, srcPosition, targetPosition);
                // 更新UI中的Item的位置，主要是给用户看到交互效果
                audioPlayerPlayListItemAdapter.notifyItemMoved(srcPosition, targetPosition);
                ((ManticApplication) mActivity.getApplication()).setIsReplacePlayingList(true);
                return true;
            }
            return false;
        }

        @Override
        public void onMoveEnd() {
            if (isNeedMoveUpdate) {
                mDataFactory.notifyBeingPlayListChange();

            }
        }
    };


    private void audioPlayerBarPlayPause() {
        final Channel currChannel = mDataFactory.getCurrChannel();
        final int position = mDataFactory.getCurrChannelIndex();

        if (currChannel != null) {
            currChannel.playPause(mContext, currChannel, new Channel.ChannelDeviceControlListenerCallBack() {
                @Override
                public void onSuccess(HttpStatus code, ControlResult obj, PageInfo info) {
                    startAnimation = true;
                    Glog.i(TAG, "HttpStatus: " + code + " name: " + obj.name + " context: " + obj.content);
                    if (code == PUT_OR_POST_SUCCESS) {
                        mDataFactory.setCurrChannel(currChannel);
                        if (null != obj.content && obj.content.equals("2")) {
                            currChannel.setPlayState(PLAY_STATE_PAUSE);
                        } else if (null != obj.content && obj.content.equals("1")) {
                            currChannel.setPlayState(PLAY_STATE_PLAYING);
                            if (currChannel.getUri().contains("baidu")) {
                                if (!TextUtils.isEmpty(SharePreferenceUtil.getSharePreferenceData(mActivity, "Mantic", "baiduTempDuration"))) {
                                    currChannel.setDuration(Long.parseLong(SharePreferenceUtil.getSharePreferenceData(mActivity, "Mantic", "baiduTempDuration")));
                                }
                            }
                            mDataFactory.addRecentPlay(mContext, currChannel);
                        } else {
                            currChannel.setPlayState(PLAY_STATE_STOP);
                        }
                        Glog.i(TAG, " get state: " + currChannel.getPlayState() + " pos: " + position);
                        mDataFactory.getBeingPlayList().set(position, currChannel);
                        int size = mDataFactory.getBeingPlayList().size();
                        for (int i = 0; i < size; i++) {
                            if (i != position) {
                                if (mDataFactory.getBeingPlayList().get(i).getPlayState() != PLAY_STATE_STOP) {
                                    mDataFactory.getBeingPlayList().get(i).setPlayState(PLAY_STATE_STOP);
                                    mDataFactory.getBeingPlayList().set(i, mDataFactory.getBeingPlayList().get(i));
                                }
                            }
                        }
                        mDataFactory.notifyBeingPlayListChange();
                        mDataFactory.notifyRecentPlayListChange(mDataFactory.getRecentPlayList());
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
    }

    public boolean startAnimation = false;

    private boolean lastPlayStatus = false;

    private void initAudioPlayerBar() {
        this.mAudioPlayerBar = (RelativeLayout) this.mActivity.findViewById(R.id.audio_player_bar);
        this.mAudioPlayerBarPlayPause = (ImageButton) this.mActivity.findViewById(R.id.audio_player_bar_play_pause);
        this.mAudioPlayerBarPlayPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                audioPlayerBarPlayPause();
            }
        });
        this.mAudioPlayerPlayPauseBtn = (ImageButton) this.mActivity.findViewById(R.id.audio_player_play_pause_btn);
        this.mAudioPlayerPlayPauseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                playPause();
            }
        });

    }

    //播放或者暂停接口
    private void playPause() {
        setControlEnable(false);
        startAnimation = true;
        final Channel currChannel = mDataFactory.getCurrChannel();
        final int position = mDataFactory.getCurrChannelIndex();

        currChannel.playPause(mContext, currChannel, new Channel.ChannelDeviceControlListenerCallBack() {
            @Override
            public void onSuccess(HttpStatus code, ControlResult obj, PageInfo info) {
                Glog.i(TAG, "onSuccess --> HttpStatus: " + code + " name: " + obj.name + " context: " + obj.content);
                if (code == PUT_OR_POST_SUCCESS) {
                    mDataFactory.setCurrChannel(currChannel);
                    if (obj.content != null && obj.content.equals("2")) {
                        currChannel.setPlayState(PLAY_STATE_PAUSE);
                    } else if (obj.content != null && obj.content.equals("1")) {
                        currChannel.setPlayState(PLAY_STATE_PLAYING);
                        if (currChannel.getUri().contains("baidu")) {
                            if (!TextUtils.isEmpty(SharePreferenceUtil.getSharePreferenceData(mActivity, "Mantic", "baiduTempDuration"))) {
                                currChannel.setDuration(Long.parseLong(SharePreferenceUtil.getSharePreferenceData(mActivity, "Mantic", "baiduTempDuration")));
                            }
                        }
                        mDataFactory.addRecentPlay(mContext, currChannel);
                    } else {
                        currChannel.setPlayState(PLAY_STATE_STOP);
                    }
                    Glog.i(TAG, " get state: " + currChannel.getPlayState() + " pos: " + position);
                    mDataFactory.getBeingPlayList().set(position, currChannel);
                    int size = mDataFactory.getBeingPlayList().size();
                    for (int i = 0; i < size; i++) {
                        if (i != position) {
                            if (mDataFactory.getBeingPlayList().get(i).getPlayState() != PLAY_STATE_STOP) {
                                mDataFactory.getBeingPlayList().get(i).setPlayState(PLAY_STATE_STOP);
                                mDataFactory.getBeingPlayList().set(i, mDataFactory.getBeingPlayList().get(i));
                            }
                        }
                    }
                    mDataFactory.notifyBeingPlayListChange();
                }
            }

            @Override
            public void onFailed(HttpStatus code) {
                Glog.i(TAG, "onFailed --> HttpStatus: " + code);
            }

            @Override
            public void onError(IoTException error) {
                Glog.i(TAG, "onError --> IoTException: " + error);
            }
        });
    }

    private void initBeingListPagerSnap() {
        PagerSnapHelper pagerSnapHelper = new PagerSnapHelper();
        pagerSnapHelper.attachToRecyclerView(this.mBeingPlayListView);
        this.mBeingPlayListView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                Glog.i(TAG, "mBeingPlayListView onScrollStateChanged newState = " + newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    final int firstPosition = ((LinearLayoutManager) recyclerView.getLayoutManager()).findFirstVisibleItemPosition();
                    Glog.i(TAG, "mBeingPlayListView onScrollStateChanged firstPosition = " + firstPosition);
                    if (firstPosition != mDataFactory.getCurrChannelIndex()) {
//                        final boolean[] devControlResult = {false};
                        final Channel channel = mBeingPlayList.get(firstPosition);
                        Glog.i(TAG, "mBeingPlayListView onScrollStateChanged channel.channel.getPlayState() = " + channel.getPlayState());
                        if (channel.getPlayState() != PLAY_STATE_PLAYING) {
                            channel.playPause(mContext, channel, new Channel.ChannelDeviceControlListenerCallBack() {
                                @Override
                                public void onSuccess(HttpStatus code, ControlResult obj, PageInfo info) {
                                    Glog.e(TAG, "HttpStatus: " + code + " code " + obj.code + " name: " + obj.name + " context: " + obj.content);
                                    if (code == PUT_OR_POST_SUCCESS && null != obj.content) {
                                        ChannelPlayListManager.getInstance().setCurrentChannelPlay(new Callback<SetCurrentChannelPlayRsBean>() {
                                            @Override
                                            public void onResponse(Call<SetCurrentChannelPlayRsBean> call, Response<SetCurrentChannelPlayRsBean> response) {
                                                if (response.isSuccessful() && null == response.errorBody()) {
                                                    if (channel.getUri().contains("baidu")) {
                                                        if (!TextUtils.isEmpty(SharePreferenceUtil.getSharePreferenceData(mActivity, "Mantic", "baiduTempDuration"))) {
                                                            channel.setDuration(Long.parseLong(SharePreferenceUtil.getSharePreferenceData(mActivity, "Mantic", "baiduTempDuration")));
                                                        }
                                                    }
                                                    channel.setPlayState(PLAY_STATE_PLAYING);
                                                    mDataFactory.setCurrChannel(channel);
                                                    mDataFactory.addRecentPlay(mContext, channel);
                                                    mDataFactory.getBeingPlayList().set(firstPosition, channel);
                                                    Glog.i(TAG, " get state: " + channel.getPlayState() + " pos: " + firstPosition);
                                                    int size = mDataFactory.getBeingPlayList().size();
                                                    for (int i = 0; i < size; i++) {
                                                        if (i != firstPosition) {
                                                            mDataFactory.getBeingPlayList().get(i).setPlayState(PLAY_STATE_STOP);
                                                            mDataFactory.getBeingPlayList().set(i, mDataFactory.getBeingPlayList().get(i));
                                                        }
                                                    }

                                                    mDataFactory.notifyBeingPlayListChange();
                                                    mDataFactory.notifyRecentPlayListChange(mDataFactory.getRecentPlayList());
                                                }

                                            }

                                            @Override
                                            public void onFailure(Call<SetCurrentChannelPlayRsBean> call, Throwable t) {

                                            }

                                        }, channel.getTlid(), mActivity);
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
                    }
                }
            }
        });
        /*
        this.mAudioPlayerBottomSheet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            }
        });
        */
    }

    public void addLikeMusic(Channel channel) {
        if (null != channel) {
            mDataFactory.getMyLikeMusicList().add(channel);
        }
    }

    public void removeLikeMusic(Channel channel) {
        if (null != channel) {
            ArrayList<Channel> myLikeMusicList = mDataFactory.getMyLikeMusicList();
            for (int i = myLikeMusicList.size() - 1; i >= 0; i--) {
                String likeUri = myLikeMusicList.get(i).getUri();
                if (likeUri != null && likeUri.equals(channel.getUri()) && myLikeMusicList.get(i).getName().equals(channel.getName())) {
                    mDataFactory.getMyLikeMusicList().remove(i);
                }
            }

        }
    }

    public void onStart() {
        Glog.i(TAG, "onStart..............");
        this.mBeingPlayListView.setAdapter(this.mBeingPlayAudioListAdapter);

        String lastIndexStr = SharePreferenceUtil.getSharePreferenceData(mActivity, "Mantic", "lastIndex");
        int lastIndex = -1;
        if (!TextUtils.isEmpty(lastIndexStr)) {
            lastIndex = Integer.parseInt(lastIndexStr);
        }

        if (lastIndex != -1) {
            this.mBeingPlayListView.scrollToPosition(lastIndex);
        }
    }

    public void onStop() {
        this.mBeingPlayListView.setAdapter(null);
    }

    public void setControlEnable(boolean enable) {
        mLinearLayoutManager.setScrollEnabled(enable);
        mAudioPlayerNextBtn.setEnabled(enable);
        mAudioPlayerPreBtn.setEnabled(enable);
        mAudioPlayerPlayPauseBtn.setEnabled(enable);
        mAudioPlayerBarPlayPause.setEnabled(enable);
        if (this.mVolumeSeekBar != null) {
            this.mVolumeSeekBar.setEnabled(enable);
        }
        if (audioPlayerPlayListItemAdapter.getFmChannel()) {
            mAudioPlayerNextBtn.setEnabled(false);
            mAudioPlayerPreBtn.setEnabled(false);
            mLinearLayoutManager.setScrollEnabled(false);
        }
    }

    private boolean isEastMusic() {
        if (!((ManticApplication)mActivity.getApplication()).isPlaySrcZero()) {
            return false;
        }
        return null != mDataFactory.getCurrChannel() && !TextUtils.isEmpty(mDataFactory.getCurrChannel().getUri())
                && mDataFactory.getCurrChannel().getUri().contains("east");
    }

    public boolean isCollapsed() {
        boolean result = false;
        if (this.mBottomSheetBehavior != null && this.mBottomSheetBehavior.getState() == BottomSheetBehavior.STATE_COLLAPSED) {
            result = true;
        }
        return result;
    }

    public void collapsed() {
        if (this.mBottomSheetBehavior != null && this.mBottomSheetBehavior.getState() != BottomSheetBehavior.STATE_COLLAPSED) {
            mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        }
    }

    public void expanded() {
        if (this.mBottomSheetBehavior != null && this.mBottomSheetBehavior.getState() == BottomSheetBehavior.STATE_COLLAPSED) {
            mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        }
    }

    public void exit() {
        if (this.mChannelControlListener != null) {
            this.mDataFactory.removeChannelControlListener(this.mChannelControlListener);
        }
    }


    private void showAudioPlayerPlaylist() {

        final RelativeLayout layout = (RelativeLayout) mPlayListDialog.findViewById(R.id.audio_player_playlist_content);

        mPlayListDialog.findViewById(R.id.playlist_cancel_content).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPlayListDialog.dismiss();
            }
        });

        switch (((ManticApplication) mActivity.getApplication()).getChannelPlayMode()) {
            case 0:
                audio_player_playlist_loop_btn.setImageDrawable(mActivity.getResources().getDrawable(R.drawable.audio_player_list_loop_nor));
                break;
            case 1:
                audio_player_playlist_loop_btn.setImageDrawable(mActivity.getResources().getDrawable(R.drawable.audio_player_single_loop_nor));
                break;
            case 2:
                audio_player_playlist_loop_btn.setImageDrawable(mActivity.getResources().getDrawable(R.drawable.audio_player_random_loop_nor));
                break;
        }

        audio_player_playlist_loop_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ChannelPlayListManager.getInstance().setChannelPlayMode(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        if (response.isSuccessful() && null == response.errorBody()) {
                            String playMode = "";
                            switch ((((ManticApplication) mActivity.getApplication()).getChannelPlayMode() + 1) % 3) {
                                case 0:
                                    playMode = mActivity.getString(R.string.list_loop);
                                    ((ManticApplication) mActivity.getApplication()).setChannelPlayMode(0);
                                    audio_player_playlist_loop_btn.setImageDrawable(mActivity.getResources().getDrawable(R.drawable.audio_player_list_loop_nor));
                                    break;
                                case 1:
                                    playMode = mActivity.getString(R.string.single_loop);
                                    ((ManticApplication) mActivity.getApplication()).setChannelPlayMode(1);
                                    audio_player_playlist_loop_btn.setImageDrawable(mActivity.getResources().getDrawable(R.drawable.audio_player_single_loop_nor));
                                    break;
                                case 2:
                                    playMode = mActivity.getString(R.string.random_loop);
                                    ((ManticApplication) mActivity.getApplication()).setChannelPlayMode(2);
                                    audio_player_playlist_loop_btn.setImageDrawable(mActivity.getResources().getDrawable(R.drawable.audio_player_random_loop_nor));
                                    break;
                            }
                            ToastUtils.showShortSafe(String.format(mActivity.getString(R.string.success_set_the_current_play_mode), playMode));
                        } else {
                            ToastUtils.showShortSafe(mActivity.getString(R.string.failed_set_the_current_play_mode));
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        ToastUtils.showShortSafe(mActivity.getString(R.string.failed_set_the_current_play_mode));
                    }
                }, mActivity, (((ManticApplication) mActivity.getApplication()).getChannelPlayMode() + 1) % 3);
            }
        });
        audio_player_playlist_empty_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CustomDialog.Builder mBuilder = new CustomDialog.Builder(mActivity);
                mBuilder.setTitle(mActivity.getString(R.string.dialog_btn_prompt));
                mBuilder.setMessage(mActivity.getString(R.string.sure_clear_play_list));
                mBuilder.setPositiveButton(mActivity.getString(R.string.dialog_btn_confirm), new CustomDialog.Builder.DialogPositiveClickListener() {
                    @Override
                    public void onPositiveClick(CustomDialog dialog) {
                        ChannelPlayListManager.getInstance().clearChannelPlayList(new Callback<ResponseBody>() {
                            @Override
                            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                if (response.isSuccessful() && null == response.errorBody()) {
                                    if (null != mDataFactory.getCurrChannel()) {
                                        mDataFactory.getCurrChannel().deviceStop(mContext);
                                    }
                                    ToastUtils.showShortSafe(mActivity.getString(R.string.delete_success));
                                    mBeingPlayList.clear();
                                    audioPlayerPlayListItemAdapter.setAudioPlayList(mBeingPlayList);
                                    mDataFactory.setBeingPlayList(mBeingPlayList);
                                    mDataFactory.setCurrChannel(null);
                                    mDataFactory.notifyBeingPlayListChange();
                                    mDataFactory.notifyMyLikeMusicStatusChange();
                                    mPlayListDialog.dismiss();
                                    collapsed();
                                } else {
                                    ToastUtils.showShortSafe(mActivity.getString(R.string.delete_fail));
                                }
                            }

                            @Override
                            public void onFailure(Call<ResponseBody> call, Throwable t) {
                                ToastUtils.showShortSafe(mActivity.getString(R.string.delete_fail));
                            }
                        }, mActivity);

                        dialog.dismiss();
                    }
                });
                mBuilder.setNegativeButton(mActivity.getString(R.string.dialog_btn_cancel), new CustomDialog.Builder.DialogNegativeClickListener() {
                    @Override
                    public void onNegativeClick(CustomDialog dialog) {
                        dialog.dismiss();
                    }
                });


                mBuilder.create().show();
            }
        });
        audio_player_playlist_add_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ChannelAddFragment channelAddFragment = new ChannelAddFragment();
                Bundle bundle = new Bundle();
                bundle.putSerializable("channelList", mBeingPlayList);
                channelAddFragment.setArguments(bundle);
                if (mActivity instanceof FragmentEntrust) {
                    ((FragmentEntrust) mActivity).pushFragment(channelAddFragment, "ChannelAddFragment");
                }
                mPlayListDialog.dismiss();
                collapsed();
            }
        });

        mPlayListDialog.show();


    }

    // progress  : 1 - 150  value : 1 - 15 级别
    public void controlVolume(final int process) {

        mVolumeSeekBar.setProgress(process);

        if (process == 0){
            Util.minimumVolumeDialog(mActivity);
        }

        int value = AudioHelper.getVolumeValue(process);

        Glog.i("jys","controlVolume -- > value: " + value);

        Channel.sendPlayVolume(mContext, value, new Channel.ChannelDeviceControlListenerCallBack() {
            @Override
            public void onSuccess(HttpStatus code, ControlResult obj, PageInfo info) {
                Glog.i(TAG, "sendPlayVolume, name: " + obj.name + " context: " + obj.content + " code: " + obj.code);
                SharePreferenceUtil.setDeviceVolume(mActivity, process);
            }

            @Override
            public void onFailed(HttpStatus code) {

            }

            @Override
            public void onError(IoTException error) {

            }
        });
    }

    public void updateVolumeFromDevice(int value) {
        Glog.i(TAG, "updateVolumeFromDevice -- > value: " + value);
        mVolumeSeekBar.setProgress(value * 10 - 10);
        SharePreferenceUtil.setDeviceVolume(mActivity, value * 10-10);
    }

    public void onVolumeKeyUp() {
        int currentVol = SharePreferenceUtil.getDeviceVolume(mActivity);
        Glog.i("jys", "onVolumeKeyUp:    " + currentVol);
        if (currentVol >= 150) {
            return;
        }
        currentVol += 10;
        controlVolume(currentVol);
    }

    public void onVolumeDown() {
        int currentVol = SharePreferenceUtil.getDeviceVolume(mActivity);
        Glog.i("jys", "onVolumeDown:    " + currentVol);
        currentVol -= 10;
        if (currentVol <=0){
            currentVol = 0;
        }
        controlVolume(currentVol);
    }


    public void updateVolume() {
        Channel.getPlayVolume(mContext, new Channel.ChannelDeviceControlListenerCallBack() {
            @Override
            public void onSuccess(HttpStatus code, ControlResult obj, PageInfo info) {
                Glog.i(TAG, "getPlayVolume, name: " + obj.name + " context: " + obj.content + " code: " + obj.code);
                if (obj.content != null) {
                    try {
                        JSONObject json = new JSONObject(obj.content);
                        JSONObject vol = json.getJSONObject("vollevel");
                        int curlevel = vol.getInt("cur");
                        int maxlevel = vol.getInt("max");
                        if (curlevel <= maxlevel) {
                            mVolumeSeekBar.setMax(150);
                            mVolumeSeekBar.setProgress(curlevel * 10 -10);
                            SharePreferenceUtil.setDeviceVolume(mActivity, curlevel * 10 -10);

                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailed(HttpStatus code) {
                Glog.i(TAG, "Volume ... onFailed");

                mVolumeSeekBar.setProgress(SharePreferenceUtil.getDeviceVolume(mActivity));

            }

            @Override
            public void onError(IoTException error) {
                Glog.i(TAG, "Volume ... onError");
                mVolumeSeekBar.setProgress(SharePreferenceUtil.getDeviceVolume(mActivity));
            }
        });

        //临时修改 有时候拿不到音量数据 也没有返回...
        mVolumeSeekBar.setProgress(SharePreferenceUtil.getDeviceVolume(mActivity));

    }


    /**
     * 查询播放状态
     * @param online
     */
    public void updatePlayState(final boolean online, final boolean isSyncVoice) {
        final Channel currChannel = mDataFactory.getCurrChannel();
//        if (null == currChannel) {
//            return;
//        }
     /*   if (!online || (((ManticApplication)mActivity.getApplication()).isChannelStop()) && !currChannel.getUri().contains("未知")) {
            mActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    startAnimation = true;
                    if (!online) {
                        progress = 0;
                    }
                    currChannel.setPlayState(PLAY_STATE_STOP);
                    mDataFactory.setCurrChannel(currChannel);
                    updateMainAudioPlayerFromChannel(currChannel);
                }
            });
            return;
        }*/

        ManticApplication application = (ManticApplication)mActivity.getApplication();
        final int index = getResourceListIndex("DeviceinfoQuery", application);
        if (index != -1) {
            IoTSDKManager.getInstance().createDeviceAPI().controlDevice(SharePreferenceUtil.getDeviceId(mActivity), application.getResourceList().get(index), RequestMethod.GET, null, new IoTRequestListener<ControlResult>() {
                @Override
                public void onSuccess(HttpStatus code, ControlResult obj, PageInfo info) {
                    if (obj.content != null) {
                        try {
                            JSONObject jsonObject = new JSONObject(obj.content);
                            JSONObject infoQuery = jsonObject.getJSONObject("DeviceinfoQuery");
                            Glog.i("DeviceinfoQuery", obj.content);
                            int playing = infoQuery.optInt("playing");
                            if (isSyncVoice) {
                                String music_url = infoQuery.optString("music_url");
                                String music_name = infoQuery.optString("music_name");
                                if (null != currChannel) {
                                    if (currChannel.getName().equals(music_name) && currChannel.getUri().equals(music_url)) {
                                        Glog.i("lbj", "一样");
                                    } else {
                                        if (TextUtils.isEmpty(music_url)) {

                                        } else {
                                            Glog.i("lbj", "不一样");
                                            ((MainActivity)mActivity).getBeingPlayList();
                                        }
                                    }
                                } else {
                                    if (!TextUtils.isEmpty(music_url)) {
                                        Glog.i("lbj", "不一样");
                                        ((MainActivity)mActivity).getBeingPlayList();
                                    }
                                }
                            }

                            if (null == currChannel) {
                                return;
                            }

                            switch (playing) {
                                case 0:  // 0:stop
                                    startAnimation = true;
                                    currChannel.setPlayState(PLAY_STATE_STOP);
                                    mDataFactory.setCurrChannel(currChannel);
                                    updateMainAudioPlayerFromChannel(currChannel);
                                    break;
                                case 2:  //2:pause
                                    startAnimation = true;
                                    currChannel.setPlayState(PLAY_STATE_PAUSE);
                                    mDataFactory.setCurrChannel(currChannel);
                                    updateMainAudioPlayerFromChannel(currChannel);
                                    break;
                                case 1:  //1:playing
                                    currChannel.setPlayState(PLAY_STATE_PLAYING);
                                    mDataFactory.setCurrChannel(currChannel);
                                    updateMainAudioPlayerFromChannel(currChannel);
                                    break;
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }

                @Override
                public void onFailed(HttpStatus code) {
                    Glog.i("jys", "updateDeviceInfo -> onFailed: ");
                }

                @Override
                public void onError(IoTException error) {
                    Glog.i("jys", "updateDeviceInfo -> onError: ");
                }
            });
        }
    }


    //同步歌曲,暂时弃用，用这个还会导致播放状态暂停一次，后续可以优化
    public void syncCurrentChannel() {
        final Channel currChannel = mDataFactory.getCurrChannel();
        final ArrayList<Channel> beingPlayList = mDataFactory.getBeingPlayList();
        if (null == currChannel || null == beingPlayList || beingPlayList.size() <= 0) {
            return;
        }
        ManticApplication application = (ManticApplication)mActivity.getApplication();
        final int index = getResourceListIndex("DeviceinfoQuery", application);
        if (index != -1) {
            IoTSDKManager.getInstance().createDeviceAPI().controlDevice(SharePreferenceUtil.getDeviceId(mActivity), application.getResourceList().get(index), RequestMethod.GET, null, new IoTRequestListener<ControlResult>() {
                @Override
                public void onSuccess(HttpStatus code, ControlResult obj, PageInfo info) {
                    if (obj.content != null) {
                        try {
                            JSONObject jsonObject = new JSONObject(obj.content);
                            JSONObject infoQuery = jsonObject.getJSONObject("DeviceinfoQuery");
                            Glog.i("syncCurrentChannel", obj.content);
                            int playing = infoQuery.optInt("playing");
                            String music_url = infoQuery.optString("music_url");
                            String music_name = infoQuery.optString("music_name");
                            int position = -1;
                            for (int i = 0; i < beingPlayList.size(); i++) {
                                if (beingPlayList.get(i).getUri().equals(music_url) && beingPlayList.get(i).getName().equals(music_name)) {
                                    position = i;
                                    beingPlayList.get(i).setPlayState(Channel.PLAY_STATE_PLAYING);
                                } else {
                                    beingPlayList.get(i).setPlayState(Channel.PLAY_STATE_STOP);
                                }
                            }
                            if (position != -1) {
                                Channel channel = beingPlayList.get(position);
                                mDataFactory.setCurrChannel(beingPlayList.get(position));
                                SharePreferenceUtil.setSharePreferenceData(mContext, "Mantic", "lastIndex", position + "");
                                if (!beingPlayList.get(position).getUri().contains("radio:")) {
                                    mDataFactory.addRecentPlay(mContext, beingPlayList.get(position));
                                    mDataFactory.notifyRecentPlayListChange(mDataFactory.getRecentPlayList());
                                }
                                Channel.playGetBaiduUri(mContext, mDataFactory.getCurrChannel().getUri(), null, false, mDataFactory, mActivity);

                            }

                            mDataFactory.setBeingPlayList(beingPlayList);
                            mDataFactory.notifyBeingPlayListChange();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }

                @Override
                public void onFailed(HttpStatus code) {
                    Glog.i("jys", "updateDeviceInfo -> onFailed: ");
                }

                @Override
                public void onError(IoTException error) {
                    Glog.i("jys", "updateDeviceInfo -> onError: ");
                }
            });
        }
    }

    private int getResourceListIndex(String node_name, ManticApplication application) {

        int index = -1;
        if (application.getResourceList() != null) {
            for (int i = 0; i < application.getResourceList().size(); i++) {
                if (application.getResourceList().get(i).getName().equals(node_name)) {
                    index = i;
                    break;
                }
            }
        }
        return index;
    }

    private void updateProgressFromDevice(float scale) {
        Glog.i("MainActivity", "scale: " + scale);
        int currentDuration = (int) (totalDuration * scale);
        Glog.i("MainActivity", "totalDuration: " + totalDuration + "currentDuration: " + currentDuration);
//        progress = currentDuration - 1000 * 14;//缓存数据 先按照15S计算
        progress = currentDuration - 1000 * 5;
        if (progress <= 0) {
            progress = 0;
        }
        mPlaySeekBarCurTime.setText(TimeUtil.secondToTime(progress));
    }

    public void getAudioPlayerProgress(final boolean onlineStatus, final boolean isSyncVoice) {
        Channel.getPlayProgress(mContext, new Channel.ChannelDeviceControlListenerCallBack() {
            @Override
            public void onSuccess(HttpStatus code, ControlResult obj, PageInfo info) {
                Glog.i("MainActivity", "getPlayProgress, name: " + obj.name + " context: " + obj.content + " code: " + obj.code);
                if (obj.content != null) {
                    try {
                        JSONObject json = new JSONObject(obj.content);
                        JSONObject vol = json.getJSONObject("progress");
                        long total = vol.getLong("total");
                        long curr = vol.getLong("recv");
                        if (total != 0 && curr != 0) {
                            ((ManticApplication)mActivity.getApplication()).setChannelStop(false);
                            updateProgressFromDevice((float) curr / total);

                            if ((((ManticApplication)mActivity.getApplication()).isSleepTimeOn())
                                    && mActivity.getString(R.string.after_current_audio).equals(SleepTimeSetUtils.getSleepTime(mDataFactory))) {
//                                tv_audio_alarm.setText(TimeUtil.secondToTime((int)(total-curr)));
                            }

                            if (null != mDataFactory.getCurrChannel() && mDataFactory.getCurrChannel().getDuration() == 0) {
                                Channel.playGetBaiduUri(mContext, mDataFactory.getCurrChannel().getUri(), null, false, mDataFactory, mActivity);
                            }
                        } else if (total == 0 && curr == 0){//应该设置为暂停状态
                            ((ManticApplication)mActivity.getApplication()).setChannelStop(true);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                updatePlayState(onlineStatus, isSyncVoice);
            }

            @Override
            public void onFailed(HttpStatus code) {
                Glog.i("MainActivity", "Progress  ... onFailed");
                if (null != mDataFactory.getCurrChannel() && mDataFactory.getCurrChannel().getDuration() == 0) {
                    Channel.playGetBaiduUri(mContext, mDataFactory.getCurrChannel().getUri(), null, false, mDataFactory, mActivity);
                }
//                mVolumeSeekBar.setProgress(SharePreferenceUtil.getDeviceVolume(mActivity));
                ((ManticApplication)mActivity.getApplication()).setChannelStop(true);
                mActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Channel currChannel = mDataFactory.getCurrChannel();
                        if (null == currChannel) {
                            return;
                        }
                        startAnimation = true;
                        currChannel.setPlayState(PLAY_STATE_STOP);
                        mDataFactory.setCurrChannel(currChannel);
                        updateMainAudioPlayerFromChannel(currChannel);
                    }
                });

                updatePlayState(onlineStatus, isSyncVoice);
            }

            @Override
            public void onError(IoTException error) {
                Glog.i("MainActivity", "Progress ... onError");
//                mVolumeSeekBar.setProgress(SharePreferenceUtil.getDeviceVolume(mActivity));
            }
        });
    }

    public void getDeviceSleepTime() {
        SleepInfo.getSleepTime(new Channel.ChannelDeviceControlListenerCallBack() {
            @Override
            public void onSuccess(HttpStatus code, ControlResult obj, PageInfo info) {
                Glog.i("MainActivity", "getDeviceSleepTime, name: " + obj.name + " context: " + obj.content + " code: " + obj.code);
                if (obj.content != null) {
                    try {
                        JSONObject json = new JSONObject(obj.content);
                        JSONObject vol = json.getJSONObject("SleepTime");
                        int sleep_mode = vol.getInt("sleep_mode");
                        int sleep_current = vol.getInt("sleep_current");
                        sleep_remaining = vol.getInt("sleep_remaining") * 1000;
                        int position = 0;
                        if (sleep_mode == 1) {
                            switch (sleep_current) {
                                case 0:
                                    position = 1;
                                    ((ManticApplication)mActivity.getApplication()).setSleepTimeOn(true);
                                    if (null != tv_audio_alarm) {
                                        tv_audio_alarm.setText(TimeUtil.secondToTime((int)mDataFactory.getCurrChannel().getDuration() - progress));
                                    }
                                    break;
                                case 600:
                                    position = 2;
                                    ((ManticApplication)mActivity.getApplication()).setSleepTimeOn(true);
                                    if (null != tv_audio_alarm) {
                                        tv_audio_alarm.setText(TimeUtil.secondToTime(sleep_remaining));
                                    }
                                    break;
                                case 1200:
                                    position = 3;
                                    ((ManticApplication)mActivity.getApplication()).setSleepTimeOn(true);
                                    if (null != tv_audio_alarm) {
                                        tv_audio_alarm.setText(TimeUtil.secondToTime(sleep_remaining));
                                    }
                                    break;
                                case 1800:
                                    position = 4;
                                    ((ManticApplication)mActivity.getApplication()).setSleepTimeOn(true);
                                    if (null != tv_audio_alarm) {
                                        tv_audio_alarm.setText(TimeUtil.secondToTime(sleep_remaining));
                                    }
                                    break;
                                case 3600:
                                    position = 5;
                                    ((ManticApplication)mActivity.getApplication()).setSleepTimeOn(true);
                                    if (null != tv_audio_alarm) {
                                        tv_audio_alarm.setText(TimeUtil.secondToTime(sleep_remaining));
                                    }
                                    break;
                                case 5400:
                                    position = 6;
                                    ((ManticApplication)mActivity.getApplication()).setSleepTimeOn(true);
                                    if (null != tv_audio_alarm) {
                                        tv_audio_alarm.setText(TimeUtil.secondToTime(sleep_remaining));
                                    }
                                    break;
                            }

                            if (null != mDataFactory.getSleepInfoList()) {
                                for (int i = 0; i < mDataFactory.getSleepInfoList().size(); i++) {
                                    if (i != position) {
                                        mDataFactory.getSleepInfoList().get(i).setSetting(false);
                                    } else {
                                        mDataFactory.getSleepInfoList().get(i).setSetting(true);
                                    }
                                }
                                if (sleep_current != 0 && sleep_remaining > 0) {
                                    mDataFactory.notifyResetTimeChange(sleep_remaining / 1000);
                                }
                            }
                        } else if (sleep_mode == 0) {
                            SleepTimeSetUtils.onlyClearSleepTimeSetByCurrent(mActivity, mDataFactory);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailed(HttpStatus code) {

            }

            @Override
            public void onError(IoTException error) {

            }
        }, mActivity);
    }


    public void getCurrentChannel() {//10秒获取一次当前歌曲，如果当前歌曲与服务器获取的当前歌曲不统一就拉去服务器数据替换

        ChannelPlayListManager.getInstance().getCurrentChannelPlay(new Callback<GetCurrentChannelPlayRsBean>() {
            @Override
            public void onResponse(Call<GetCurrentChannelPlayRsBean> call, Response<GetCurrentChannelPlayRsBean> response) {
                if(response.isSuccessful() && null == response.errorBody()) {
                    GetCurrentChannelPlayRsBean body = response.body();
                    if (null != body && null != body.getResult()) {
                        ChannelPlayListRsBean.Result.Track track = body.getResult().track;
                        if (null != track) {
                            if (null != mDataFactory.getCurrChannel()) {
                                if (TextUtils.isEmpty(track.getName()) || TextUtils.isEmpty(track.getUri()) || TextUtils.isEmpty(mDataFactory.getCurrChannel().getName()) || TextUtils.isEmpty(mDataFactory.getCurrChannel().getUri())) {
                                    return;
                                }
                                if (track.getName().equals(mDataFactory.getCurrChannel().getName()) && track.getUri().equals(mDataFactory.getCurrChannel().getUri())) {
                                    Glog.i("lbj", "一样");
                                } else {
                                    ((MainActivity)mActivity).getBeingPlayList();
                                    Glog.i("lbj", "不一样");
                                }
                            } else {
                                ((MainActivity)mActivity).getBeingPlayList();
                            }
                        } else if (null != mDataFactory.getCurrChannel()){
                            ((MainActivity)mActivity).getBeingPlayList();
                        }
                    } else {
                        Glog.i("lbj", "body.getResult()为空");
                        mBeingPlayList.clear();
                        mDataFactory.setBeingPlayList(mBeingPlayList);
                        mDataFactory.setCurrChannel(null);
                        mDataFactory.notifyBeingPlayListChange();
                        mDataFactory.notifyMyLikeMusicStatusChange();
                        if(null != mPlayListDialog && mPlayListDialog.isShowing()) {
                            mPlayListDialog.dismiss();
                        }
                        collapsed();
                    }
                } else {
                }
            }

            @Override
            public void onFailure(Call<GetCurrentChannelPlayRsBean> call, Throwable t) {

            }
        }, mActivity);
    }


    private void showAudioPlayerMore() {
        final Channel currChannel = mDataFactory.getCurrChannel();
        String albumString;
        if (audioPlayerPlayListItemAdapter.getFmChannel()) {
            albumString = this.mActivity.getString(R.string.fm_attr);
        } else {
            albumString = this.mActivity.getString(R.string.album_attr);
        }
//        String albumFormat = String.format(albumString, currChannel.getAlbum());
        String albumFormat = String.format(albumString, TextUtils.isEmpty(currChannel.getMantic_album_name()) ? mActivity.getString(R.string.coomaan_music) : currChannel.getMantic_album_name());
        String searchString = this.mActivity.getString(R.string.search_arrt);
        String searchFormat = "";
        String titleString = "";
        if (TextUtils.isEmpty(currChannel.getSinger())) {
            searchFormat = String.format(searchString, "");
            titleString = currChannel.getName();
        } else {
            searchFormat = String.format(searchString, currChannel.getSinger());
            titleString = currChannel.getName() + " " + "-" + " " + currChannel.getSinger();
        }



        mDialog = Utility.getDialog(this.mActivity, R.layout.channel_detail_more_adapter);
        RecyclerView rv_channel_detail_more = (RecyclerView) mDialog.findViewById(R.id.rv_channel_detail_more);
        rv_channel_detail_more.setLayoutManager(new LinearLayoutManager(this.mActivity));
        rv_channel_detail_more.addItemDecoration(new ChanneDetailMoreItemDecoration(this.mActivity));
        List<String> moreStringList = new ArrayList<String>();
        moreStringList.add(titleString);
        moreStringList.add(albumFormat);
        if (!audioPlayerPlayListItemAdapter.getFmChannel()) {
            moreStringList.add(this.mActivity.getString(R.string.add_to_mychannel));
            moreStringList.add(searchFormat);
        }
        moreStringList.add(this.mActivity.getString(R.string.cancel));
        AudioPlayerMoreAdapter mPlaylistMoreAdapter = new AudioPlayerMoreAdapter(this.mActivity, moreStringList, currChannel.getServiceId());
        mPlaylistMoreAdapter.setmOnItemClickLitener(new AudioPlayerMoreAdapter.OnItemClickLitener() {
            public void onItemClick(View view, int position) {
                Glog.i(TAG, "POSIONT:   " + position);
                switch (position) {
                    case 0:
                        break;
                    case 1:
                       /* if (audioPlayerPlayListItemAdapter.getFmChannel()) {
                            mDialog.dismiss();
                        } else{

                        } */
                            if (null != currChannel.getMantic_album_uri() && currChannel.getMantic_album_uri().contains("ximalaya")) {
                                String album_ids = currChannel.getMantic_album_uri().substring(currChannel.getMantic_album_uri().lastIndexOf(":") + 1);
                                Map<String, String> map = new HashMap<String, String>();
                                map.put(DTransferConstants.ALBUM_IDS, album_ids);
                                CommonRequest.getBatch(map, new IDataCallBack<BatchAlbumList>() {
                                    @Override
                                    public void onSuccess(BatchAlbumList batchAlbumList) {
                                        List<Album> albums = batchAlbumList.getAlbums();
                                        for (int i = 0; i < albums.size(); i++) {
                                            Album album = albums.get(i);
                                            ChannelDetailsFragment cdFragment = new ChannelDetailsFragment();
                                            String channelName = album.getAlbumTitle();
                                            String channnelId = album.getCoverUrlLarge();
                                            Bundle bundle = new Bundle();
                                            bundle.putString(MyMusicService.MY_MUSIC_SERVICE_ID, XimalayaSoundData.mAppSecret);
                                            bundle.putString(ChannelDetailsFragment.CHANNEL_ID, channnelId);
                                            bundle.putString(ChannelDetailsFragment.CHANNEL_NAME, channelName);
                                            bundle.putString(ChannelDetailsFragment.CHANNEL_FROM, "AudioPlayer");
                                            bundle.putLong(ChannelDetailsFragment.CHANNEL_TOTAL_COUNT, album.getIncludeTrackCount());
                                            bundle.putInt(MyMusicService.PRE_DATA_TYPE, 3);
                                            bundle.putInt(ChannelDetailsFragment.CHANNEL_TYPE, 3);
                                            bundle.putString(ChannelDetailsFragment.ALBUM_ID, album.getId() + "");
                                            bundle.putString(ChannelDetailsFragment.MAIN_ID, "");
                                            bundle.putString(ChannelDetailsFragment.CHANNEL_COVER_URL, album.getCoverUrlLarge());
                                            bundle.putString(ChannelDetailsFragment.CHANNEL_INTRO, album.getAlbumIntro());
                                            bundle.putString(ChannelDetailsFragment.CHANNEL_PLAY_COUNT, album.getPlayCount() + "");
                                            bundle.putString(ChannelDetailsFragment.CHANNEL_TAGS, album.getAlbumTags());
                                            bundle.putLong(ChannelDetailsFragment.CHANNEL_UPDATEAT, album.getUpdatedAt());
                                            bundle.putString(ChannelDetailsFragment.CHALLEL_SINGER, album.getAnnouncer().getNickname());
                                            bundle.putString(ChannelDetailsFragment.MAIN_ID, "分类");

                                            cdFragment.setArguments(bundle);
                                            if(mActivity instanceof FragmentEntrust){
                                                ((FragmentEntrust) mActivity).pushFragment(cdFragment, XimalayaSoundData.mAppSecret + channnelId + channelName);
                                            }
                                        }

                                        mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                                        mDialog.dismiss();
                                    }

                                    @Override
                                    public void onError(int i, String s) {
                                        mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                                        mDialog.dismiss();
                                    }
                                });
                            } else {
                                MyChannelManager.getInstance().getAlbumDetail(new Callback<ResponseBody>() {
                                    @Override
                                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                        if (response.isSuccessful() && null == response.errorBody()) {
                                            try {
                                                JSONObject mainObject = new JSONObject(response.body().string()); //json 主体
                                                JSONObject resultObject = mainObject.getJSONObject("result"); // result json 主体
                                                JSONArray jsonArray = resultObject.getJSONArray(currChannel.getMantic_album_uri());
                                                JSONObject jsonObject = jsonArray.getJSONObject(0);

                                                String uri = jsonObject.optString("uri");
                                                uri = uri.substring(0, uri.indexOf(":"));
                                                String serviceId = "";
                                                switch (uri) {
                                                    case "netease":
                                                        serviceId = "wangyi";
                                                        break;
                                                    case "qingting":
                                                        serviceId = "qingting";
                                                        break;
                                                    case "beva":
                                                        serviceId = "beiwa";
                                                        break;
                                                    case "idaddy":
                                                        serviceId = "idaddy";
                                                        break;
                                                }
                                                ChannelDetailsFragment cdFragment = new ChannelDetailsFragment();
                                                String channelName = jsonObject.optString("name");
                                                String channnelId = jsonObject.optString("mantic_image");
                                                Bundle bundle = new Bundle();
                                                bundle.putString(MyMusicService.MY_MUSIC_SERVICE_ID, serviceId);
                                                bundle.putString(ChannelDetailsFragment.CHANNEL_ID, channnelId);
                                                bundle.putString(ChannelDetailsFragment.CHANNEL_NAME, channelName);
                                                bundle.putString(ChannelDetailsFragment.CHANNEL_FROM, "AudioPlayer");
                                                bundle.putLong(ChannelDetailsFragment.CHANNEL_TOTAL_COUNT, jsonObject.optInt("mantic_num_tracks"));
                                                bundle.putInt(MyMusicService.PRE_DATA_TYPE, 3);
                                                if (jsonObject.optString("uri").contains("radio")) {
                                                    bundle.putInt(ChannelDetailsFragment.CHANNEL_TYPE, 2);
                                                } else {
                                                    bundle.putInt(ChannelDetailsFragment.CHANNEL_TYPE, 3);
                                                }
                                                bundle.putString(ChannelDetailsFragment.ALBUM_ID, jsonObject.optString("uri"));
                                                bundle.putString(ChannelDetailsFragment.MAIN_ID, "");
                                                bundle.putString(ChannelDetailsFragment.CHANNEL_COVER_URL, jsonObject.optString("mantic_image"));
                                                bundle.putString(ChannelDetailsFragment.CHANNEL_INTRO, jsonObject.optString("mantic_describe"));
                                                bundle.putString(ChannelDetailsFragment.CHANNEL_PLAY_COUNT, jsonObject.optString("mantic_play_count"));
                                                if (null != jsonObject.optJSONArray("mantic_artists_name")) {
                                                    String singer = "";
                                                    for (int j = 0; j < jsonObject.optJSONArray("mantic_artists_name").length(); j++) {
                                                        if (j != jsonObject.optJSONArray("mantic_artists_name").length() - 1) {
                                                            singer = singer + jsonObject.optJSONArray("mantic_artists_name").get(j).toString() + ",";
                                                        } else {
                                                            singer = singer + jsonObject.optJSONArray("mantic_artists_name").get(j).toString();
                                                        }
                                                    }
                                                    bundle.putString(ChannelDetailsFragment.CHALLEL_SINGER, singer);
                                                }

                                                cdFragment.setArguments(bundle);
                                                if(mActivity instanceof FragmentEntrust){
                                                    ((FragmentEntrust) mActivity).pushFragment(cdFragment, serviceId + channnelId + channelName);
                                                }
                                            } catch (Exception e){
                                                Glog.i(TAG, "Exception: " + e.getMessage());
                                            }
                                        }
                                        mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                                        mDialog.dismiss();
                                    }

                                    @Override
                                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                                        mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                                        mDialog.dismiss();
                                    }
                                }, currChannel.getMantic_album_uri());
                            }

                        break;
                    case 2:
                        if (audioPlayerPlayListItemAdapter.getFmChannel()) {
                            mDialog.dismiss();
                        } else if (currChannel.getUri().contains(":audio:")) {
                            ToastUtils.showShortSafe(mActivity.getString(R.string.no_copyright_collection));
                            mDialog.dismiss();
                        } else {
                            ArrayList<Channel> channelList = new ArrayList<Channel>();
                            channelList.add(currChannel);
                            ChannelAddFragment channelAddFragment = new ChannelAddFragment();
                            Bundle bundle = new Bundle();
                            bundle.putSerializable("channelList", channelList);
                            channelAddFragment.setArguments(bundle);
                            if (mActivity instanceof FragmentEntrust) {
                                ((FragmentEntrust) mActivity).pushFragment(channelAddFragment, "ChannelAddFragment");
                            }

                            mDialog.dismiss();
                            collapsed();
                        }

                        break;
                    case 3:
                        SearchResultFragment searchResultFragment = new SearchResultFragment();
                        Bundle bundle = new Bundle();
                        bundle.putString("searchFrom", "AudioPlayer");
                        bundle.putString("searchKey", currChannel.getSinger());
                        searchResultFragment.setArguments(bundle);
                        if (mActivity instanceof FragmentEntrust) {
                            ((FragmentEntrust) mActivity).pushFragment(searchResultFragment, searchResultFragment.getClass().getSimpleName());
                        }
                        mDialog.dismiss();
                        collapsed();
                        break;
                    case 4:
                        mDialog.dismiss();
                        break;
                }
            }
        });
        rv_channel_detail_more.setAdapter(mPlaylistMoreAdapter);
        mDialog.show();
    }


    private void updateAudioPlayerBottomSheet() {
        if (this.mBeingPlayAudioListAdapter.getItemCount() == 0 && this.mAudioPlayerBottomSheet.getVisibility() != View.GONE) {
            Glog.i(TAG, "updateAudioPlayerBottomSheet .. gone.......");
            this.mAudioPlayerBottomSheet.setVisibility(View.GONE);
            this.mDefaultAudioPlayer.setVisibility(View.VISIBLE);
        } else if (this.mBeingPlayAudioListAdapter.getItemCount() > 0 && this.mAudioPlayerBottomSheet.getVisibility() != View.VISIBLE) {
            this.mAudioPlayerBottomSheet.setVisibility(View.VISIBLE);
            this.mDefaultAudioPlayer.setVisibility(View.GONE);
            Glog.i(TAG, "updateAudioPlayerBottomSheet .. visible.......");
        } else if (this.mBeingPlayAudioListAdapter.getItemCount() > 0 && this.mAudioPlayerBottomSheet.getVisibility() == View.VISIBLE) {
            this.mDefaultAudioPlayer.setVisibility(View.GONE);
            Glog.i(TAG, "mDefaultAudioPlayer .. visible.......");
        }
    }


    //刷新专辑图片 start
    private void resetAudioPlayerAlbumCover() {
        this.mAudioPlayerAlbumCover.setImageResource(R.drawable.audio_player_album_cover_default);
        if (this.mAudioPlayerAlbumCoverBlurBackgroundBitmap != null) {
//            this.mAudioPlayerAlbumCoverBlurBackground.setImageBitmap(this.mAudioPlayerAlbumCoverBlurBackgroundBitmap);
        } else {
            Bitmap defaultCover = BitmapFactory.decodeResource(this.mActivity.getResources(), R.drawable.audio_player_album_cover_default);
//            this.mAudioPlayerAlbumCoverBlurBackgroundBitmap = blur(defaultCover,this.mAudioPlayerAlbumCoverBlurBackground);
        }
    }


    private void updateAudioPlayerAlbumCover(Channel channel) {
        final String channelUrl = channel.getIconUrl();
        Glog.i(TAG, "channelUrl :" + channelUrl);
        Glog.i(TAG, "mAudioPlayerAlbumCoverUrl :" + mAudioPlayerAlbumCoverUrl);
        if (this.mAudioPlayerAlbumCoverUrl == null || !this.mAudioPlayerAlbumCoverUrl.equals(channelUrl)) {
//            this.resetAudioPlayerAlbumCover();
            if (!mActivity.isDestroyed()) {
                GlideImgManager.glideLoaderCircleCorner(this.mActivity, channelUrl, R.drawable.audio_player_album_cover_default, R.drawable.audio_player_album_cover_default, 8, mAudioPlayerAlbumCover);
            }

            mAudioPlayerAlbumCoverUrl = channelUrl;
        }
    }
    //刷新专辑图片 end

    private void onPauseAlbumCover(boolean pause) {
        Glog.i(TAG, "onPauseAlbumCover......" + pause);
        //暂停封面动画
        pauseAnimation = new ScaleAnimation(1, 0.8f, 1, 0.8f,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        pauseAnimation.setDuration(300);
        pauseAnimation.setFillAfter(true);
        //播放封面动画
        resumeAnimation = new ScaleAnimation(0.8f, 1, 0.8f, 1,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        resumeAnimation.setDuration(300);
        resumeAnimation.setFillAfter(true);
//        if (startAnimation) {
            if (pause) {
                mAudioPlayerAlbumCover.setAnimation(pauseAnimation);
                pauseAnimation.start();

            } else {
                mAudioPlayerAlbumCover.setAnimation(resumeAnimation);
                resumeAnimation.start();
            }
//        }
        startAnimation = false;
    }

    //更新播放界面
    public void updateMainAudioPlayerFromChannel(Channel channel) {

//        this.mAudioPlayerSongName.setText(channel.getName());
//        this.mAudoPlayerSingerName.setText(channel.getSinger());
//        this.mAudioPlayerChannelName.setText(AudioHelper.getServiceNameById(channel.getServiceId()));

        updateAudioProgress(channel);

        Glog.i(TAG, "updateMainAudioPlayerFromChannel channel.getPlayState() = " + channel.getPlayState());
        if (channel.getPlayState() == PLAY_STATE_PLAYING) {
            onPlayAndResume();
            this.updateAudioPlayerAlbumCover(channel);
            if (!lastPlayStatus){
            onPauseAlbumCover(false);
            }
            lastPlayStatus = true;
            prgoressHandler.removeCallbacks(updateRunnable);
            prgoressHandler.post(updateRunnable);
        } else {
            onPause();
            this.updateAudioPlayerAlbumCover(channel);
            if (lastPlayStatus){
                onPauseAlbumCover(true);
            }
            lastPlayStatus = false;
            prgoressHandler.removeCallbacks(updateRunnable);
            if (audioPlayerPlayListItemAdapter.getFmChannel()) {
                prgoressHandler.post(updateRunnable);
            }

        }

        if (audioPlayerPlayListItemAdapter.getFmChannel()) {
            mPlayListDialog.findViewById(R.id.audio_player_playlist_line1).setVisibility(View.GONE);
            mPlayListDialog.findViewById(R.id.audio_player_playlist_line2).setVisibility(View.GONE);
            audioPlayerPlaylistLoopCount.setVisibility(View.INVISIBLE);
            audio_player_playlist_add_btn.setVisibility(View.INVISIBLE);
            audio_player_playlist_empty_btn.setVisibility(View.INVISIBLE);
            audio_player_playlist_loop_btn.setVisibility(View.INVISIBLE);
            mAudioPlayerNextBtn.setEnabled(false);
            mAudioPlayerPreBtn.setEnabled(false);

        } else {
            mPlayListDialog.findViewById(R.id.audio_player_playlist_line1).setVisibility(View.VISIBLE);
            mPlayListDialog.findViewById(R.id.audio_player_playlist_line2).setVisibility(View.VISIBLE);
            audioPlayerPlaylistLoopCount.setVisibility(View.VISIBLE);
            audio_player_playlist_add_btn.setVisibility(View.VISIBLE);
            audio_player_playlist_empty_btn.setVisibility(View.VISIBLE);
            audio_player_playlist_loop_btn.setVisibility(View.VISIBLE);
            mAudioPlayerNextBtn.setEnabled(true);
            mAudioPlayerPreBtn.setEnabled(true);
        }

        if (((MainActivity) mActivity).onlineStatus) {
            setControlEnable(true);
        } else {
//            prgoressHandler.removeCallbacks(updateRunnable);
            setControlEnable(false);
        }
    }

    private void onPause() {
        SharePreferenceUtil.setDevicePlayState(mActivity, 1); //1 pause
        this.mAudioPlayerBarPlayPause.setImageDrawable(this.mActivity.getResources().getDrawable(R.drawable.btn_audio_bottom_bar_play));
        this.mAudioPlayerPlayPauseBtn.setImageDrawable(this.mActivity.getResources().getDrawable(R.drawable.btn_audio_player_play));
        if (audioPlayerPlayListItemAdapter.getFmChannel()) {
            mPlaySeekBarCurTime.setText(TimeUtil.getCurrentFmStartTime());
            mPlaySeekBar.setProgress(progress + currentFmProgress);
        } else {
            mPlaySeekBarCurTime.setText(TimeUtil.secondToTime(progress));
            if (null != mDataFactory.getCurrChannel() && (((ManticApplication)mActivity.getApplication()).isSleepTimeOn())) {
                if (mActivity.getString(R.string.after_current_audio).equals(SleepTimeSetUtils.getSleepTime(mDataFactory))) {
                    tv_audio_alarm.setText(TimeUtil.secondToTime((int)mDataFactory.getCurrChannel().getDuration() - progress));
                    tv_audio_alarm.setVisibility(View.VISIBLE);
                }
            } else {
                tv_audio_alarm.setVisibility(View.GONE);
            }
            mPlaySeekBar.setProgress(progress);
        }
    }

    private void onPlayAndResume() {
        SharePreferenceUtil.setDevicePlayState(mActivity, 0); //0 play
        this.mAudioPlayerBarPlayPause.setImageDrawable(this.mActivity.getResources().getDrawable(R.drawable.btn_audio_bottom_bar_pause));
        this.mAudioPlayerPlayPauseBtn.setImageDrawable(this.mActivity.getResources().getDrawable(R.drawable.btn_audio_player_pause));
    }

    private int totalDuration;
    private String currentChannelUri;
    private boolean currentFinish = false;
    private static final long TIME_UPDATE = 1000L;
    private int progress = 0;
    private int currentFmProgress = 0;

    public void setCurrentFinish(boolean currentFinish) {
        this.currentFinish = currentFinish;
    }

    private void updateAudioProgress(Channel channel) {
        Glog.i(TAG, "channel.getUri(): " + channel.getUri());
        if (audioPlayerPlayListItemAdapter.getFmChannel() && channel.getTimePeriods() != null) {
            totalDuration = TimeUtil.getCurrentFmDuration(channel.getTimePeriods());
        } else {
            totalDuration = (int) channel.getDuration();
        }
        if (currentChannelUri != channel.getUri() || currentFinish) {
            currentChannelUri = channel.getUri();
            if (audioPlayerPlayListItemAdapter.getFmChannel() && channel.getTimePeriods() != null) {
                progress = 0;
                if ("00:00".equals(channel.getTimePeriods().split("-")[1])) {
                    mPlaySeekBarDuration.setText("23:59" + ":00");//channel.getTimePeriods().split("-")[1] + ":00"
                } else {
                    mPlaySeekBarDuration.setText(channel.getTimePeriods().split("-")[1] + ":00");//channel.getTimePeriods().split("-")[1] + ":00"
                }

                mPlaySeekBarCurTime.setText(TimeUtil.getCurrentFmStartTime());
                mPlaySeekBar.setMax(TimeUtil.getCurrentFmDuration(channel.getTimePeriods()));
                currentFmProgress = TimeUtil.getCurrentFmProgress(channel.getTimePeriods());
            } else {
                mPlaySeekBarDuration.setText(TimeUtil.secondToTime(totalDuration));
                mPlaySeekBar.setMax(totalDuration);
                if ((((ManticApplication)mActivity.getApplication()).getResetProgress())) {
                    mPlaySeekBarCurTime.setText(TimeUtil.secondToTime(0));
                    progress = 0;
                } else {
                    ((ManticApplication)mActivity.getApplication()).setResetProgress(true);
                }
            }
            prgoressHandler.removeCallbacks(updateRunnable);
            currentFinish = false;
            this.mAudioPlayerSongName.setText(channel.getName());
            this.mAudoPlayerSingerName.setText(channel.getSinger());
            this.mAudioPlayerChannelName.setText(AudioHelper.getServiceNameByUri(channel.getUri()));
        }
    }

    public Runnable updateRunnable = new Runnable() {
        @Override
        public void run() {
            prgoressHandler.postDelayed(this, TIME_UPDATE);
            updateProgress();
        }
    };

    boolean isChangeFm = false;
    private void updateProgress() {
        progress = progress + 1000;
//        Glog.i(TAG,"curr progress:" +progress + "totalDuration" + totalDuration);
        if (progress >= totalDuration) {
            progress = totalDuration;
            mPlaySeekBar.setProgress(totalDuration);
            prgoressHandler.removeCallbacks(updateRunnable);
            currentFinish = true;

        } else {
            if (audioPlayerPlayListItemAdapter.getFmChannel()) {
                mPlaySeekBar.setProgress(currentFmProgress + progress);
                mPlaySeekBarCurTime.setText(TimeUtil.getCurrentFmStartTime());
              /*  Glog.i(TAG,"currentFmProgress: "+ currentFmProgress + "  progress: " + progress + "  totalDuration：" + totalDuration);
                if (currentFmProgress + progress >= totalDuration) {//FM时间到了 切到下一首
                    AudioPlayerUtil.playCode(mDataFactory, PLAY_NEXT, mActivity);
                }*/
                if (mDataFactory.getCurrChannel() != null && (((ManticApplication)mActivity.getApplication()).isSleepTimeOn())) {
                    if (mActivity.getString(R.string.after_current_audio).equals(SleepTimeSetUtils.getSleepTime(mDataFactory))) {
                        SleepTimeSetUtils.addSleepTimeSet(mActivity.getString(R.string.not_open), mActivity, mDataFactory, null);
                    }
                }
            } else {
                mPlaySeekBar.setMax(totalDuration);
                mPlaySeekBarCurTime.setText(TimeUtil.secondToTime(progress));
                mPlaySeekBarDuration.setText(TimeUtil.secondToTime(totalDuration));
                if (mDataFactory.getCurrChannel() != null && (((ManticApplication)mActivity.getApplication()).isSleepTimeOn())) {
                    if (mActivity.getString(R.string.after_current_audio).equals(SleepTimeSetUtils.getSleepTime(mDataFactory))) {
                        tv_audio_alarm.setText(TimeUtil.secondToTime((int)mDataFactory.getCurrChannel().getDuration() - progress));
                        tv_audio_alarm.setVisibility(View.VISIBLE);
                    }
                } else {
                    tv_audio_alarm.setVisibility(View.GONE);
                }
                mPlaySeekBar.setProgress(progress);
            }
        }

        if (!audioPlayerPlayListItemAdapter.getFmChannel()) {
            return;
        }
        //判断当前时间是否超过广播的时间
        long currentFmTime = TimeUtil.getFmTime(TimeUtil.getCurrentFmStartTime());
        if (null != mDataFactory.getCurrChannel() && !TextUtils.isEmpty(mDataFactory.getCurrChannel().getTimePeriods())) {
            long endFmTime = TimeUtil.getEndFmDuration(mDataFactory.getCurrChannel().getTimePeriods());
            long startFmTime = TimeUtil.getStartFmDuration(mDataFactory.getCurrChannel().getTimePeriods());
            Glog.i(TAG, "mDataFactory.getCurrChannel().getTimePeriods(): " + mDataFactory.getCurrChannel().getTimePeriods());
            Glog.i(TAG, "startFmTime: " + startFmTime);
            Glog.i(TAG, "currentFmTime: " + currentFmTime);
            Glog.i(TAG, "endFmTime: " + endFmTime);

            if (endFmTime >= currentFmTime && currentFmTime >= startFmTime) {
                return;
            } else {
                if (null == mDataFactory.getBeingPlayList() || mDataFactory.getBeingPlayList().size() <= 0) {
                    return;
                }
                if (!isChangeFm) {
                    isChangeFm = true;
                    boolean isCurrentFM = false;
                    final List<Channel> channelList = new ArrayList<Channel>();
                    List<Channel> preChannels = new ArrayList<Channel>();
                    List<Channel> nextChannels = new ArrayList<Channel>();
                    for (int i = 0; i < mDataFactory.getBeingPlayList().size(); i++) {
                        if (TimeUtil.iscurFmPeriods(mDataFactory.getBeingPlayList().get(i).getTimePeriods())) {
                            isCurrentFM = true;
                        }

                        if (isCurrentFM) {
                            nextChannels.add(mDataFactory.getBeingPlayList().get(i));
                        } else {
                            preChannels.add(mDataFactory.getBeingPlayList().get(i));
                        }
                    }

                    channelList.addAll(nextChannels);
                    channelList.addAll(preChannels);

                    if (channelList.size() == 1) {
                        mDataFactory.notifyBeingPlayListChange();
                        mDataFactory.notifyMyLikeMusicStatusChange();
                        isChangeFm = false;
                    } else {
                        ChannelPlayListManager.getInstance().updateChannelPlayList(new Callback<ChannelPlayAddRsBean>() {
                            @Override
                            public void onResponse(Call<ChannelPlayAddRsBean> call, Response<ChannelPlayAddRsBean> response) {
                                if (response.isSuccessful() && null == response.errorBody()) {
                                    ((MainActivity)mActivity).setFmChannel(true);
                                    List<AddResult> result = response.body().getResult();

                                    List<Channel> channels = new ArrayList<Channel>();
                                    for (int i = 0; i < result.size(); i++) {
                                        channelList.get(i).setTlid(result.get(i).getTlid());
                                        channels.add(channelList.get(i));
                                    }

                                    mDataFactory.setBeingPlayList((ArrayList<Channel>) channels);
                                    mDataFactory.setCurrChannel(channels.get(0));
                                    mDataFactory.notifyBeingPlayListChange();
                                    mDataFactory.notifyMyLikeMusicStatusChange();
                                }
                                isChangeFm = false;
                            }

                            @Override
                            public void onFailure(Call<ChannelPlayAddRsBean> call, Throwable t) {
                                isChangeFm = false;
                            }
                        }, channelList, mActivity);
                    }
                }
            }
        }
    }

    //更新底部播放列表
    public void updateBeingPlayList(ArrayList<Channel> channels) {
        Glog.i(TAG, "updateBeingPlayList...");
        this.mBeingPlayList = channels;

        this.mBeingPlayAudioListAdapter.notifyDataSetChanged();
        this.updateAudioPlayerBottomSheet();
        Glog.i(TAG, "this.mBeingPlayList.size():     " + this.mBeingPlayList.size());
        /*
        for(int i = 0;i < this.mBeingPlayList.size();i++){
            DataFactory.Channel channel = this.mBeingPlayList.get(i);
            if(channel.getIsSelected()) {
                //this.mLinearLayoutManager.smoothScrollToPosition(this.mBeingPlayListView, new RecyclerView.State(), i);
                Glog.i(TAG,"updateBeingPlayList smoothScrollToPosition i = "+i);
                this.mBeingPlayListView.smoothScrollToPosition(i);
                this.mCurrChannel = channel;
                this.mCurrChannelIndex = i;
                this.updateMainAudioPlayerFromChannel(channel);
                break;
            }
        }
        */
        Channel currChannel = this.mDataFactory.getCurrChannel();
        if (currChannel != null) {
            final int currIndex = mDataFactory.getCurrChannelIndex();
//            Glog.i(TAG, "updateBeingPlayList: " + mBeingPlayList.get(currIndex).getName() + "  " + mBeingPlayList.get(currIndex).getSinger());
            Glog.i(TAG, "updateBeingPlayList currIndex = " + currIndex);
            if (currIndex >= 0) {
                if (currChannel.getPlayState() == PLAY_STATE_PLAYING) {
                    Glog.i(TAG, "updateBeingPlayList mBeingPlayListView.smoothScrollToPosition = " + currIndex);
                    new Handler().postDelayed(new Runnable() {//延迟0.5秒，因为要重绘布局要一段时间
                        @Override
                        public void run() {
                            mBeingPlayListView.scrollToPosition(currIndex);
//                    mLinearLayoutManager.scrollToPositionWithOffset(currIndex, 0);
                        }
                    }, 500);

                }
                this.updateMainAudioPlayerFromChannel(currChannel);

            }
        }
    }


    private void updateMyLikeBtnStatus() {
        Channel currChannel = mDataFactory.getCurrChannel();
        if (null == currChannel) {
            return;
        }

        if (currChannel.getUri().contains("radio:")) {
            if (mDataFactory.isExistMyRadio(mDataFactory.getCurrChannel().getUri())) {
                audio_player_love_btn.setBackgroundResource(R.drawable.audio_player_fm_love_btn_pre);
            } else {
                audio_player_love_btn.setBackgroundResource(R.drawable.audio_player_fm_love_btn_nor);
            }

            return;
        }

        ArrayList<Channel> myLikeMusicList = mDataFactory.getMyLikeMusicList();
        for (int i = 0; i < myLikeMusicList.size(); i++) {
            Channel channel = myLikeMusicList.get(i);
            if (channel.getName().equals(currChannel.getName()) && channel.getUri().equals(currChannel.getUri())) {
                audio_player_love_btn.setBackgroundResource(R.drawable.audio_player_love_btn_pre);
                return;
            }
        }

        audio_player_love_btn.setBackgroundResource(R.drawable.audio_player_love_btn_nor);
    }

    private class BeingPlayAudioListAdapter extends RecyclerView.Adapter {
        private Context beContext;
        private DataFactory.BeingPlayListListener beingPlayListListener;
        private DataFactory.OnMyLikeMusicStateListener mMyLikeMusicStateListener;

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position, List payloads) {
            super.onBindViewHolder(holder, position, payloads);
        }

        BeingPlayAudioListAdapter(Context context) {
            this.beContext = context;
            this.beingPlayListListener = new DataFactory.BeingPlayListListener() {
                @Override
                public void callback(ArrayList<Channel> channels) {
                    Glog.i(TAG, "beingPlayListListener callback");
                    updateBeingPlayList(channels);
                }
            };
            this.mMyLikeMusicStateListener = new DataFactory.OnMyLikeMusicStateListener() {
                @Override
                public void changeMyLikeMusicState() {
                    updateMyLikeBtnStatus();
                }
            };
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new BeingPlayAudioViewHolder(LayoutInflater.from(this.beContext).inflate(R.layout.bottom_curr_audio_item, parent, false));
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            ((BeingPlayAudioViewHolder) holder).showItem(position);
        }

        @Override
        public int getItemCount() {
            return mBeingPlayList.size();
        }

        @Override
        public void onAttachedToRecyclerView(RecyclerView recyclerView) {
            super.onAttachedToRecyclerView(recyclerView);
            //mDataFactory.registerBeingPlayListSubscriber(this.beingPlayAudioListSubscriber);
            mDataFactory.registerBeingPlayListListener(this.beingPlayListListener);
            mDataFactory.registerMyLikeMusiceStatusListener(mMyLikeMusicStateListener);
            Glog.i(TAG, "onAttachedToRecyclerView");
        }

        @Override
        public void onDetachedFromRecyclerView(RecyclerView recyclerView) {
            super.onDetachedFromRecyclerView(recyclerView);
            //mDataFactory.unRegisterBeingPlayListSubscriber();
            //mDataFactory.unRegisterBeingPlayListSubscriber(this.beingPlayAudioListSubscriber);
            mDataFactory.unregisterBeingPlayListListener(this.beingPlayListListener);
            mDataFactory.unregisterMyLikeMusiceStatusListener(mMyLikeMusicStateListener);
            Glog.i(TAG, "onDetachedFromRecyclerView");
        }

        class BeingPlayAudioViewHolder extends RecyclerView.ViewHolder {
            private ImageView audio_bottom_bar_album_cover;
            private TextView audio_bottom_bar_channel_name;
            private TextView audio_bottom_bar_channel_singer;

            public BeingPlayAudioViewHolder(View itemView) {
                super(itemView);
                this.audio_bottom_bar_album_cover = (ImageView) itemView.findViewById(R.id.audio_bottom_bar_album_cover);
                this.audio_bottom_bar_channel_name = (TextView) itemView.findViewById(R.id.audio_bottom_bar_channel_name);
                this.audio_bottom_bar_channel_singer = (TextView) itemView.findViewById(R.id.audio_bottom_bar_channel_singer);

                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                    }
                });
            }

            public void showItem(int position) {
                Channel channel = mBeingPlayList.get(position);
                Glog.i(TAG, "BeingPlayAudioViewHolder showItem Glide position = " + position + "---channel.getIconUrl() = " + channel.getIconUrl());
                GlideImgManager.glideLoaderCircle(beContext, channel.getIconUrl(), R.drawable.fragment_channel_detail_cover,
                        R.drawable.fragment_channel_detail_cover, audio_bottom_bar_album_cover);

                this.audio_bottom_bar_channel_name.setText(channel.getName());
                this.audio_bottom_bar_channel_singer.setText(TextUtils.isEmpty(channel.getSinger()) ? mActivity.getString(R.string.unknown) : channel.getSinger());

                mDataFactory.notifyMyLikeMusicStatusChange();
            }
        }
    }

    public class ScrollEnabledLinearLayoutManager extends LinearLayoutManager {
        private boolean isScrollEnabled = true;

        public ScrollEnabledLinearLayoutManager(Context context) {
            super(context);
        }

        public void setScrollEnabled(boolean flag) {
            this.isScrollEnabled = flag;
        }

        @Override
        public boolean canScrollHorizontally() {
            return isScrollEnabled && super.canScrollHorizontally();
        }

        @Override
        public boolean canScrollVertically() {
            return isScrollEnabled && super.canScrollVertically();
        }
    }
}
