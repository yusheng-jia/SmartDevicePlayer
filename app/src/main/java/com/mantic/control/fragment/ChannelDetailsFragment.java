package com.mantic.control.fragment;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.AnimationDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mantic.control.R;
import com.mantic.control.activity.MainActivity;
import com.mantic.control.adapter.ChannelDetailMoreAdapter;
import com.mantic.control.adapter.ChannelDetailsItemAdapter;
import com.mantic.control.api.mopidy.MopidyTools;
import com.mantic.control.api.mychannel.MyChannelOperatorRetrofit;
import com.mantic.control.api.mychannel.MyChannelOperatorServiceApi;
import com.mantic.control.api.mychannel.bean.AddTrack;
import com.mantic.control.api.mychannel.bean.MyChannelAddRsBean;
import com.mantic.control.api.mychannel.bean.MyChannelDeleteRsBean;
import com.mantic.control.api.mychannel.bean.MyChannelDetailRsBean;
import com.mantic.control.data.Channel;
import com.mantic.control.data.DataFactory;
import com.mantic.control.data.MyChannel;
import com.mantic.control.decoration.ChanneDetailMoreItemDecoration;
import com.mantic.control.manager.DragLayoutManager;
import com.mantic.control.manager.MyChannelManager;
import com.mantic.control.musicservice.IMusicServiceAlbum;
import com.mantic.control.musicservice.IMusicServiceTrackContent;
import com.mantic.control.musicservice.MyMusicService;
import com.mantic.control.musicservice.XimalayaSoundData;
import com.mantic.control.utils.AudioPlayerUtil;
import com.mantic.control.utils.DensityUtils;
import com.mantic.control.utils.GlideImgManager;
import com.mantic.control.utils.Glog;
import com.mantic.control.utils.GsonUtil;
import com.mantic.control.utils.NetworkUtils;
import com.mantic.control.utils.ResourceUtils;
import com.mantic.control.utils.SharePreferenceUtil;
import com.mantic.control.utils.TextUtil;
import com.mantic.control.utils.TimeUtil;
import com.mantic.control.utils.Util;
import com.mantic.control.utils.Utility;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by root on 17-4-13.
 */
public class ChannelDetailsFragment extends BaseSlideFragment implements IMusicServiceAlbum, View.OnClickListener,
        ChannelDetailMoreAdapter.OnItemClickLitener, ChannelDetailsItemAdapter.OnItemMoreClickListener,
        DefinitionChannelEditFragment.OnRefreshMyChannelListener, DataFactory.OnMyChannelStateChangeListener {
    private static final String TAG = "ChannelDetailsFragment";
    //public static final String MUSIC_SERVICE_ID = "music_service_id";
    public static final String CHANNEL_ID = "channel_id";
    public static final String ALBUM_ID = "album_id";
    public static final String MAIN_ID = "main_id";
    public static final String CHANNEL_NAME = "channel_name";
    public static final String CHANNEL_TAGS = "channel_tags";
    public static final String CHANNEL_FROM = "channel_from";
    public static final String CHANNEL_COVER_URL = "channel_cover_url";
    public static final String CHANNEL_INTRO = "channel_INTRO";
    public static final String CHANNEL_TOTAL_COUNT = "channel_total_count";
    public static final String CHANNEL_UPDATEAT = "channel_updateat";
    public static final String CHALLEL_SINGER = "channel_singer";
    public static final String CHANNEL_TYPE = "channel_type";
    public static final String CHANNEL_PLAY_COUNT = "channel_play_count";

    private LinearLayout ll_request_view;
    private ImageView iv_request_progress;
    private TextView tv_request_progress;
    private AnimationDrawable netAnimation;

    private TextView tv_channel_detail_title;
    private ImageButton mFragmentChannelDetailsMoreButton;
    private ImageView mDetailChannelCover;
    private LinearLayout ll_channel_details_back;
    private TextView tv_channel_detail_back;
    private TextView mFragmentChannelDetailsCloseButton;
    private TextView mDetailChannelName;
    private TextView albumTags;
    private TextView tv_channel_last_sync_time;
    private TextView singerName;
    private ImageButton btn_fragment_channel_detail_add_enable;
    private RelativeLayout rl_channel_added_success;
    private RelativeLayout fragment_channel_detail_random_linearlayout;
    private RelativeLayout rl_channel_operator_refresh;
    private ImageView iv_channel_detail_operator_refresh;
    private TextView tv_channel_album_size;
    private Animation mRefreshAnim;
    private RelativeLayout rl_channel_detail_operator;
    private RecyclerView rcv_channel_detail;
    private Dialog mDialog;
    private ChannelDetailsItemAdapter mChannelDetailsItemAdapter;
    private MyChannel mMyChannel;
    private List<Channel> channels = new ArrayList<Channel>();
    private boolean isRefresh = true;//是否正在加载或者刷新
    private boolean isNeedRefresh = true;
    private boolean isFirstRefresh = true;
    private boolean mAlbumIsInMyChannelList = false;

    //    private TextView mExpandableTextView;

    private ArrayList<Channel> channelList;
    private List<String> moreStringList;
    private ChannelDetailMoreAdapter mChannelDetailMoreAdapter;
    private String mChannelFrom = "";
    private boolean isExpend = false;
    private MyMusicService mMyMusicService;
    private MyChannelOperatorServiceApi mMyChannelOperatorServiceApi;
    private Call<MyChannelAddRsBean> addCall;
    private Call<MyChannelDeleteRsBean> deleteCall;
    private Call<MyChannelDetailRsBean> updateCall;
    private RelativeLayout mScrollerView;
    private RelativeLayout rl_channel_detail_header_view;
    private String mServiceId;

    private String currFmProgram = "";

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 0) {
                if (isRefresh) {
                    if (null != channels) {
                        mChannelDetailsItemAdapter.setChannelList(channels);
                        mMyChannel.setChannelList((ArrayList<Channel>) channels);
                    }

                    mChannelDetailsItemAdapter.showLoadComplete();
                }
            } else if (msg.what == 1) {
                if (null != channels) {
                    mChannelDetailsItemAdapter.setChannelList(channels);
                    mMyChannel.setChannelList((ArrayList<Channel>) channels);
                }

                mChannelDetailsItemAdapter.showLoadComplete();
            }
        }
    };

    @Override
    protected void initView(View view) {
        super.initView(view);
        Glog.i(TAG, "initView: ");
        rl_channel_detail_header_view = (RelativeLayout) LayoutInflater.from(mContext).inflate(R.layout.channel_detail_header_view, null, false);
        initHeadView(rl_channel_detail_header_view);
        initOperatorView(view);
        ll_request_view = (LinearLayout) view.findViewById(R.id.ll_request_view);
        iv_request_progress = (ImageView) view.findViewById(R.id.iv_request_progress);
        tv_request_progress = (TextView) view.findViewById(R.id.tv_request_progress);
        tv_channel_detail_title = (TextView) view.findViewById(R.id.tv_channel_detail_title);
        ll_channel_details_back = (LinearLayout) view.findViewById(R.id.ll_channel_details_back);
        tv_channel_detail_back = (TextView) view.findViewById(R.id.tv_channel_detail_back);
        tv_channel_detail_back.setClickable(false);
        ll_channel_details_back.setClickable(true);
        tv_channel_detail_back.setFocusable(false);
        ll_channel_details_back.setFocusable(true);
        ll_channel_details_back.setOnClickListener(this);

        this.mFragmentChannelDetailsCloseButton = (TextView) view.findViewById(R.id.fragment_channel_details_close_button);
        mFragmentChannelDetailsMoreButton = (ImageButton) view.findViewById(R.id.fragment_channel_details_more);
        mFragmentChannelDetailsMoreButton.setOnClickListener(this);
        mScrollerView = (RelativeLayout) view.findViewById(R.id.channel_scroll_view);
        rcv_channel_detail = (RecyclerView) view.findViewById(R.id.rcv_channel_detail);

         /*this.mExpandableTextView = (TextView) view.findViewById(R.id.fragment_channel_detail_desc_expandabletextview);
        MyGlobalLayoutListener myGlobalLayoutListener = new MyGlobalLayoutListener(mExpandableTextView, R.drawable.expandabletextview_expanddrawable, 2);
        mExpandableTextView.getViewTreeObserver().addOnGlobalLayoutListener(myGlobalLayoutListener);
        mExpandableTextView.setOnClickListener(this);*/
    }

    private void initHeadView(RelativeLayout view) {
        fragment_channel_detail_random_linearlayout = (RelativeLayout) view.findViewById(R.id.fragment_channel_detail_random_linearlayout);
        mDetailChannelName = (TextView) view.findViewById(R.id.fragment_channel_detail_channel_name);
        albumTags = (TextView) view.findViewById(R.id.fragment_channel_detail_album_name);
        tv_channel_last_sync_time = (TextView) view.findViewById(R.id.tv_channel_last_sync_time);
        singerName = (TextView) view.findViewById(R.id.fragment_channel_detail_singer_name);
        mDetailChannelCover = (ImageView) view.findViewById(R.id.fragment_channel_detail_cover);
        rl_channel_detail_operator = (RelativeLayout) view.findViewById(R.id.rl_channel_detail_operator);
        btn_fragment_channel_detail_add_enable = (ImageButton) view.findViewById(R.id.btn_fragment_channel_detail_add_enable);
        rl_channel_added_success = (RelativeLayout) view.findViewById(R.id.rl_channel_added_success);
        rl_channel_operator_refresh = (RelativeLayout) view.findViewById(R.id.rl_channel_operator_refresh);
        iv_channel_detail_operator_refresh = (ImageView) view.findViewById(R.id.iv_channel_detail_operator_refresh);
        tv_channel_album_size = (TextView) view.findViewById(R.id.tv_channel_album_size);
        fragment_channel_detail_random_linearlayout.setOnClickListener(this);
        mDetailChannelCover.setOnClickListener(this);
        btn_fragment_channel_detail_add_enable.setOnClickListener(this);
        rl_channel_added_success.setOnClickListener(this);
    }

    private void initOperatorView(View view) {

    }

    private boolean isFmChannel = false;

    @Override
    protected void initData(Bundle bundle) {

        super.initData(bundle);
        height = DensityUtils.dip2px(mContext, 219.33f);
        ((MainActivity)mActivity).setAudioPlayerVisible(true);
        mMyChannelOperatorServiceApi = MyChannelOperatorRetrofit.getInstance().create(MyChannelOperatorServiceApi.class);

        mServiceId = this.getArguments().getString(MyMusicService.MY_MUSIC_SERVICE_ID);

        //String musicServiceId = bundle.getString(MUSIC_SERVICE_ID);
        String channelId = bundle.getString(CHANNEL_ID);
        String channelName = bundle.getString(CHANNEL_NAME);
        this.mChannelFrom = bundle.getString(CHANNEL_FROM);

        Glog.i(TAG, "mServiceId: " + mServiceId + ", ChannelId: " + channelId + ", ChannelName: " + channelName);
        Glog.i(TAG, "albumId: " + bundle.getString(ALBUM_ID) + ", intro: " + bundle.getString(CHANNEL_INTRO) +
                ", mainId: " + bundle.getString(MAIN_ID));
        this.mMyMusicService = mDataFactory.getMyMusicServiceFromServiceId(mServiceId);
        this.mMyChannel = mDataFactory.getMyChannelFrom(mServiceId, channelId, channelName);
        Glog.i(TAG, "mMyChannel == null: " + (null == mMyChannel));
        if (this.mMyChannel != null) {//
            this.mAlbumIsInMyChannelList = true;
        } else {
            this.mMyChannel = new MyChannel();
            this.mMyChannel.setChannelCoverUrl(bundle.getString(CHANNEL_COVER_URL));
            this.mMyChannel.setChannelName(bundle.getString(CHANNEL_NAME));
            this.mMyChannel.setChannelTags(bundle.getString(CHANNEL_TAGS));
            this.mMyChannel.setChannelIntro(bundle.getString(CHANNEL_INTRO));
            this.mMyChannel.setmTotalCount(bundle.getLong(CHANNEL_TOTAL_COUNT));
            this.mMyChannel.setmUpdateAt(bundle.getLong(CHANNEL_UPDATEAT));
            this.mMyChannel.setSingerName(bundle.getString(CHALLEL_SINGER));
            this.mMyChannel.setChannelId(bundle.getString(CHANNEL_ID));
            this.mMyChannel.setAlbumId(bundle.getString(ALBUM_ID));
            if (bundle.getInt(CHANNEL_TYPE) == 2) {
                this.mMyChannel.setChannelType(2);//0 1 2 音乐 电台 广播
            }else {
                this.mMyChannel.setChannelType(3);//0 1 2 音乐 电台 广播
            }
            this.mMyChannel.setPlayCount(bundle.getString(CHANNEL_PLAY_COUNT)); //收听总数
            this.mMyChannel.setMainId(bundle.getString(MAIN_ID));
            this.mMyChannel.setMusicServiceId(mServiceId);
        }


        if (!TextUtils.isEmpty(this.mChannelFrom)) {
            if ("SearchResultFragment".equals(mChannelFrom)) {
                this.tv_channel_detail_back.setText(getString(R.string.channel_detail_back));
            } else if ("MyChannelFragment".equals(this.mChannelFrom) || "EntertainmentFragment".equals(this.mChannelFrom)) {
                this.tv_channel_detail_back.setText(getString(R.string.channel_detail_first));
            }
            this.mFragmentChannelDetailsCloseButton.setVisibility(View.GONE);
        } else {
            this.mFragmentChannelDetailsCloseButton.setVisibility(View.VISIBLE);
            this.tv_channel_detail_back.setText(getString(R.string.channel_detail_back));
        }

        if (this.mAlbumIsInMyChannelList) {
            btn_fragment_channel_detail_add_enable.setVisibility(View.GONE);
            rl_channel_added_success.setVisibility(View.VISIBLE);
        } else {
            btn_fragment_channel_detail_add_enable.setVisibility(View.VISIBLE);
            rl_channel_added_success.setVisibility(View.GONE);
        }

        mDetailChannelName.setText(mMyChannel.getChannelName());

        if (TextUtils.isEmpty(mMyChannel.getChannelTags())) {// 标签
            albumTags.setVisibility(View.GONE);
        } else {
            albumTags.setVisibility(View.VISIBLE);
            albumTags.setText(this.mMyChannel.getChannelTags());
        }

        tv_channel_last_sync_time.setText(String.format(getString(R.string.channel_last_sync_time), TimeUtil.getDateFromMillisecond(mMyChannel.getmUpdateAt())));


        if (!TextUtils.isEmpty(this.mMyChannel.getSingerName())) { //歌手
            singerName.setText(mMyChannel.getSingerName());
            singerName.setVisibility(View.VISIBLE);
        } else {
            singerName.setVisibility(View.GONE);
        }

        if (mMyChannel.getmUpdateAt() == 0) {// 更新于
            tv_channel_last_sync_time.setVisibility(View.GONE);
        } else {
            tv_channel_last_sync_time.setVisibility(View.VISIBLE);
        }


        if (this.mMyChannel.getmTotalCount() == 0) {
            tv_channel_album_size.setVisibility(View.GONE);
        } else {
            tv_channel_album_size.setVisibility(View.VISIBLE);
        }

        tv_channel_album_size.setText(String.format(getString(R.string.channel_album_total_size), mMyChannel.getmTotalCount()));

        String coverUrl = mMyChannel.getChannelCoverUrl();
        if (coverUrl != null && !coverUrl.isEmpty()) {
            GlideImgManager.glideLoaderCircle(mContext, coverUrl, R.drawable.fragment_channel_detail_cover,
                    R.drawable.fragment_channel_detail_cover, mDetailChannelCover);
        }


        mDialog = Utility.getDialog(mContext, R.layout.channel_detail_more_adapter);
        RecyclerView rv_channel_detail_more = (RecyclerView) mDialog.findViewById(R.id.rv_channel_detail_more);
        rv_channel_detail_more.setLayoutManager(new LinearLayoutManager(mContext));
        rv_channel_detail_more.addItemDecoration(new ChanneDetailMoreItemDecoration(mContext));
        moreStringList = new ArrayList<String>();

        mChannelDetailMoreAdapter = new ChannelDetailMoreAdapter(mContext, moreStringList);
        mChannelDetailMoreAdapter.setmOnItemClickLitener(this);
        rv_channel_detail_more.setAdapter(mChannelDetailMoreAdapter);

        mDataFactory.registerBeingPlayListListener(this.beingPlayListListener);
        mDataFactory.registerMyChannelStateChangeListener(this);

        if (mMyChannel.getChannelType() == 2){ //广播
            isFmChannel = true;
            mFragmentChannelDetailsMoreButton.setVisibility(View.INVISIBLE);
            updateFm();
        }

        Glog.i(TAG,"isFmChannel:   " + isFmChannel);
    }

    private void updateFm(){
        singerName.setVisibility(View.GONE);
        tv_channel_last_sync_time.setVisibility(View.GONE);
        albumTags.setVisibility(View.VISIBLE);
        String album_fm_playcount = getString(R.string.album_fm_playcount);
        if(this.mMyChannel.getPlayCount() != null){
            String album_fm_format = String.format(album_fm_playcount,this.mMyChannel.getPlayCount());
            albumTags.setText(album_fm_format);
        }else {
            albumTags.setVisibility(View.GONE);
        }
        fragment_channel_detail_random_linearlayout.setVisibility(View.GONE);


    }

    @Override
    protected void onVisibleToUser() {
        super.onVisibleToUser();
    }

    private DataFactory.BeingPlayListListener beingPlayListListener = new DataFactory.BeingPlayListListener() {
        @Override
        public void callback(ArrayList<Channel> beingPlayList) {
            Glog.i(TAG, "callback: " + beingPlayList.size());
            ArrayList<Channel> list = mMyChannel.getChannelList();
                for (int i = 0; i < list.size(); i++) {
                    Channel channel = list.get(i);
                    if (null == mDataFactory.getCurrChannel()) {
                        channel.setPlayState(Channel.PLAY_STATE_STOP);
                    } else {
                        if (channel.getName().equals(mDataFactory.getCurrChannel().getName()) && channel.getUri().equals(mDataFactory.getCurrChannel().getUri())) {
                            channel.setPlayState(mDataFactory.getCurrChannel().getPlayState());
                        } else {
                            channel.setPlayState(Channel.PLAY_STATE_STOP);
                        }
                    }

                    list.set(i, channel);
                }

            mChannelDetailsItemAdapter.setChannelList(list);
        }
    };

    @Override
    public void myChannelStateChange(boolean isAdd) {
        if (isAdd) {
            mRefreshAnim.reset();
            iv_channel_detail_operator_refresh.clearAnimation();
            rl_channel_operator_refresh.setVisibility(View.GONE);
            btn_fragment_channel_detail_add_enable.setVisibility(View.GONE);
            rl_channel_detail_operator.setBackground(null);
            rl_channel_added_success.setVisibility(View.VISIBLE);
            mAlbumIsInMyChannelList = true;
        } else {
            mRefreshAnim.reset();
            iv_channel_detail_operator_refresh.clearAnimation();
            rl_channel_operator_refresh.setVisibility(View.GONE);
            btn_fragment_channel_detail_add_enable.setVisibility(View.VISIBLE);
            rl_channel_detail_operator.setBackground(null);
            rl_channel_added_success.setVisibility(View.GONE);
            mAlbumIsInMyChannelList = false;
        }
    }

    @Override
    protected void onLazyLoad() {
        super.onLazyLoad();
        mRefreshAnim = AnimationUtils.loadAnimation(mContext, R.anim.anim_rotate_refresh);
        this.mChannelDetailsItemAdapter = new ChannelDetailsItemAdapter(mContext, mActivity, null, rl_channel_detail_header_view, isFmChannel);
        this.rcv_channel_detail.addItemDecoration(new ChannelDetailsItemDecoration(mActivity));
        this.rcv_channel_detail.setLayoutManager(new LinearLayoutManager(mContext));
        this.rcv_channel_detail.setAdapter(this.mChannelDetailsItemAdapter);
        this.rcv_channel_detail.setNestedScrollingEnabled(false);
        this.rcv_channel_detail.addOnScrollListener(onScrollListener);
        this.mChannelDetailsItemAdapter.setOnItemMoreClickListener(this);

        /*new Thread(new Runnable() {
            @Override
            public void run() {
//                String channelListStr = ACacheUtil.getData(mContext, "channels" + mMyChannel.getChannelId());
                String channelListStr = SharePreferenceUtil.getSharePreferenceData(mContext, "Mantic", "channels" + mMyChannel.getChannelId());
                channels = GsonUtil.stringToChannelList(channelListStr);
                mHandler.sendEmptyMessage(0);
            }
        }).start();*/

        iv_request_progress.setBackgroundResource(R.drawable.net_loading);
        netAnimation = (AnimationDrawable)iv_request_progress.getBackground();
        netAnimation.start();
        if(iv_request_progress != null && iv_request_progress.getVisibility() != View.VISIBLE){
            iv_request_progress.setVisibility(View.VISIBLE);
        }

        if(ll_request_view != null && ll_request_view.getVisibility() != View.VISIBLE){
            ll_request_view.setVisibility(View.VISIBLE);
        }



        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                getChannelListData();
            }
        }, 500);
    }


    private void getChannelListData() {
        Glog.i(TAG, "onLazyLoad: " + (null != mMyMusicService));
        if (null != mMyMusicService) {
            mMyMusicService.setIMusicServiceAlbum(this);
            mMyMusicService.exec(getArguments());
        } else {
            updateCall = MyChannelOperatorRetrofit.getInstance().create(MyChannelOperatorServiceApi.class).
                    postMyChannelDetailQuest(MopidyTools.getHeaders(),Util.createDetailRqBean(mMyChannel, mContext));

            MyChannelManager.detailMyChannel(updateCall, new Callback<MyChannelDetailRsBean>() {
                @Override
                public void onResponse(Call<MyChannelDetailRsBean> call, Response<MyChannelDetailRsBean> response) {
                    if (response.isSuccessful() && null != response.body() && null != response.body().result && null == response.errorBody()) {
                        if(ll_request_view != null && ll_request_view.getVisibility() == View.VISIBLE){
                            netAnimation.stop();
                            ll_request_view.setVisibility(View.GONE);
                        }
                        List<AddTrack> tracks = response.body().result.tracks;
                        mMyChannel.getChannelList().clear();
                        mMyChannel.setChannelIntro(response.body().result.mantic_describe);
                        if (null != tracks) {
                            for (int i = 0; i < tracks.size(); i++) {
                                AddTrack track = tracks.get(i);
                                Channel channel = new Channel();
                                channel.setName(track.getName());
                                channel.setIconUrl(track.getMantic_image());

                                if (null != track.getMantic_artists_name()) {
                                    String singer = "";
                                    for (int z = 0; z < track.getMantic_artists_name().size(); z++) {
                                        if (z != track.getMantic_artists_name().size() - 1) {
                                            singer = singer + track.getMantic_artists_name().get(z).toString() + "，";
                                        } else {
                                            singer = singer + track.getMantic_artists_name().get(z).toString();
                                        }
                                    }
                                    channel.setSinger(singer);
                                }


                                channel.setDuration(track.getLength());
//                            channel.setLastSyncTime(track.getMantic_last_modified());
//                            channel.setServiceId(mServiceId);
                                channel.setPlayUrl(track.getMantic_real_url());
                                channel.setAlbum(mMyChannel.getChannelName());
                                channel.setUri(track.getUri());
                                channel.setMantic_album_name(track.getMantic_album_name());
                                channel.setMantic_album_uri(track.getMantic_album_uri());
                                mMyChannel.addChannel(channel);
                            }
                        }

                        mChannelDetailsItemAdapter.setChannelList(mMyChannel.getChannelList());

                        if (mMyChannel.getChannelList().size() <= 6) {
                            mChannelDetailsItemAdapter.showLoadEmpty();
                        }
                    } else {
                        setRequestView(R.drawable.search_result_empty, R.string.cannot_find);
                    }
                }

                @Override
                public void onFailure(Call<MyChannelDetailRsBean> call, Throwable t) {
                    if (!NetworkUtils.isAvailableByPing(mContext)) {
                        setRequestView(R.drawable.net_failed, R.string.network_suck);
                    } else {
                        setRequestView(R.drawable.net_failed, R.string.service_problem);
                    }
                }
            }, mMyChannel, mContext);
        }
    }

    private void setRequestView(int backgroundResourceId, int  textResourceId) {
        netAnimation.stop();
        ll_request_view.setVisibility(View.VISIBLE);
        iv_request_progress.setBackgroundResource(backgroundResourceId);
        iv_request_progress.setVisibility(View.VISIBLE);
        tv_request_progress.setText(textResourceId);
        tv_request_progress.setVisibility(View.VISIBLE);
    }

    @Override
    protected int setLayoutResourceId() {
        return R.layout.fragment_channel_details;
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        this.mFragmentChannelDetailsCloseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (null != addCall) {
                    addCall.cancel();
                    addCall = null;
                }
                if (null != deleteCall) {
                    deleteCall.cancel();
                    deleteCall = null;
                }
                ((FragmentEntrust) mActivity).popAllFragment();
                DragLayoutManager.getAppManager().removeAllDragLyout();
            }
        });
        super.onActivityCreated(savedInstanceState);
    }


    public int getScollYDistance(LinearLayoutManager layoutManager) {
        int position = layoutManager.findFirstVisibleItemPosition();
        View firstVisibleItem = rcv_channel_detail.getChildAt(0);
        int headerHeight = firstVisibleItem.getHeight();
        return headerHeight;
    }



    private int height = 0;
    RecyclerView.OnScrollListener onScrollListener = new RecyclerView.OnScrollListener() {

        private int overallXScroll = 0;
        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);
            LinearLayoutManager lm = (LinearLayoutManager) recyclerView.getLayoutManager();
            int totalItemCount = recyclerView.getAdapter().getItemCount();
            int lastVisibleItemPosition = lm.findLastVisibleItemPosition();
            int visibleItemCount = recyclerView.getChildCount();

            if (lastVisibleItemPosition == totalItemCount - 1
                    && visibleItemCount > 0) {
//                    boolean canRefresh = mMyMusicService.isRefresh();
                if (!isRefresh && isNeedRefresh) {
                    isRefresh = true;
                    Glog.i(TAG, "srollToBottom...");
                    if (null != mMyMusicService) {
                        mMyMusicService.RefreshTrackList();
                    }

                }
            } else {
                if (null != mMyChannel && null != mMyChannel.getMusicServiceId() && !TextUtils.isEmpty(mMyChannel.getMusicServiceId()) && "definition".equals(mMyChannel.getMusicServiceId())) {
                    mChannelDetailsItemAdapter.showLoadEmpty();
                }
            }
        }

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
            if(null != mMyChannel) {
                tv_channel_detail_title.setText(mMyChannel.getChannelName());
            }
            overallXScroll = overallXScroll + dy;// 累加y值 解决滑动一半y值为0
            Glog.i(TAG, "overallXScroll: " + overallXScroll);
            if (overallXScroll <= 0) {   //设置标题的背景颜色
//                tv_channel_detail_title.setTextColor(Color.argb((int) 0, 41, 193, 246));
//                tv_channel_detail_title.getBackground().setAlpha(100);
                tv_channel_detail_title.setVisibility(View.GONE);
            } else if (overallXScroll > 0 && overallXScroll <= height) { //滑动距离小于banner图的高度时，设置背景和字体颜色颜色透明度渐变
                tv_channel_detail_title.setVisibility(View.VISIBLE);
                float scale = (float) overallXScroll / height;
                float alpha = (255 * scale);
                Glog.i(TAG, "onScrolled: " + alpha);
                tv_channel_detail_title.setTextColor(Color.argb((int) alpha, 0, 0, 0));
            } else {
                tv_channel_detail_title.setTextColor(Color.argb((int) 255, 0, 0, 0));
            }
        }
    };


    @Override
    public void onDestroy() {
        if (null != addCall && !addCall.isCanceled()) {
            addCall.cancel();
        }
        if (null != deleteCall && !deleteCall.isCanceled()) {
            deleteCall.cancel();
        }
        if (null != updateCall && !updateCall.isCanceled()) {
            updateCall.cancel();
        }

        if (null != mMyMusicService) {
            IMusicServiceAlbum iMSA = this.mMyMusicService.getIMusicServiceAlbum();
            if (iMSA != null && iMSA.equals(this)) {
                this.mMyMusicService.setIMusicServiceAlbum(null);
            }
        }

        mDataFactory.unregisterBeingPlayListListener(this.beingPlayListListener);
        mDataFactory.unregisterMyChannelStateChangeListener(this);

//        mScrollerView.unRegisterOnScrollViewScrollToBottom();

        if (this.mAlbumIsInMyChannelList) {
            ArrayList<Channel> channelList = mMyChannel.getChannelList();
            Glog.i(TAG, "onDestroy: " + channelList.size());
            if (channelList.size() >= 20 && !mMyChannel.isSelfDefinition()) {
                List<Channel> channels = channelList.subList(0, 20);
//                ACacheUtil.putData(mContext, "channels" + mMyChannel.getChannelId(), GsonUtil.channellistToString(channels));
                SharePreferenceUtil.setSharePreferenceData(mContext, "Mantic", "channels" + mMyChannel.getChannelId(), GsonUtil.channellistToString(channels));

            } else {
//                ACacheUtil.putData(mContext, "channels" + mMyChannel.getChannelId(), GsonUtil.channellistToString(channelList));
                SharePreferenceUtil.setSharePreferenceData(mContext, "Mantic", "channels" + mMyChannel.getChannelId(), GsonUtil.channellistToString(channelList));
            }
        } else {
//            ACacheUtil.removeData(mContext, "channels" + mMyChannel.getChannelId());
           SharePreferenceUtil.deleteSharePreferenceData(mContext, "Mantic", "channels" + mMyChannel.getChannelId());
        }

        mMyChannel = null;

        super.onDestroy();
    }

    @Override
    public void onStart() {
        super.onStart();
        Glog.i(TAG, "onStart");

    }

    @Override
    public void onStop() {
        super.onStop();
        Glog.i(TAG, "onStop");
//        this.mChannelDetailsItemAdapter.onStop();
//        this.rcv_channel_detail.setAdapter(null);
    }

    @Override
    public void createMusicServiceAlbum(ArrayList<IMusicServiceTrackContent> items) {
        Glog.i(TAG, "createMusicServiceAlbum: " + mMyChannel.getAlbumId());
        if (isFirstRefresh) {
            isFirstRefresh = false;
            if (null != channels) {
                channels.clear();
            } else {
                channels = new ArrayList<>();
            }

            if (null == items || items.size() <= 0) {
                setRequestView(R.drawable.search_result_empty, R.string.cannot_find);
            } else {
                if(ll_request_view != null && ll_request_view.getVisibility() == View.VISIBLE){
                    netAnimation.stop();
                    ll_request_view.setVisibility(View.GONE);
                }
            }

        } else {
            if(ll_request_view != null && ll_request_view.getVisibility() == View.VISIBLE){
                netAnimation.stop();
                ll_request_view.setVisibility(View.GONE);
            }
        }

        mChannelDetailsItemAdapter.showLoadMore();
        int changePotionStart = this.mMyChannel.getChannelList().size() + 1;
        int itemCount = items.size();

        if (items.size() == 0) {
            mChannelDetailsItemAdapter.showLoadComplete();
        } else {
            Channel currChannel = mDataFactory.getCurrChannel();
            List<Channel> fmStartChannels = new ArrayList<Channel>();
            List<Channel> fmEndChannels = new ArrayList<Channel>();
            boolean fmStart = false;
            if (isFmChannel){
                for (int i = 0; i < items.size(); i++) {
                    IMusicServiceTrackContent item = items.get(i);
                    Channel channel = new Channel();
                    channel.setName(item.getTrackTitle());
                    if (item.getCoverUrlMiddle().equals("album")) {
                        channel.setIconUrl(mMyChannel.getChannelCoverUrl());
                    } else {
                        channel.setIconUrl(item.getCoverUrlMiddle());
                    }
                    channel.setSinger(item.getSinger());
                    channel.setDuration(item.getDuration());
                    channel.setLastSyncTime(item.getUpdateAt());
                    channel.setServiceId(mServiceId);
                    channel.setAlbum(mMyChannel.getChannelName());
                    channel.setUri(item.getUri());

                    channel.setTimePeriods(item.getTimePeriods());
                    channel.setMantic_album_name(mMyChannel.getChannelName());
                    channel.setMantic_album_uri(mMyChannel.getAlbumId());

                    int urlType = item.getUrlType();
                    if (urlType == IMusicServiceTrackContent.DYNAMIC_URL) {
                        channel.setIMusicServiceTrackContent(item);
                    } else if (urlType == IMusicServiceTrackContent.STATIC_URL) {
                        channel.setPlayUrl(item.getPlayUrl());
                    }

                    if (currChannel != null && currChannel.getUri().equals(channel.getUri())) {
                        channel.setPlayState(currChannel.getPlayState());
                    } else {
                        channel.setPlayState(Channel.PLAY_STATE_STOP);
                    }
                    if (TimeUtil.iscurFmPeriods(item.getTimePeriods()) || fmStart) {
                        fmStartChannels.add(channel);
                        fmStart = true;
                    } else {
                        fmEndChannels.add(channel);
                    }

                }
                channels.addAll(fmStartChannels);
                channels.addAll(fmEndChannels);
            }else {
                for (int i = 0; i < items.size(); i++) {
                    IMusicServiceTrackContent item = items.get(i);
                    Channel channel = new Channel();
                    channel.setName(item.getTrackTitle());
                    if (!TextUtils.isEmpty(item.getCoverUrlMiddle()) && item.getCoverUrlMiddle().equals("album")){
                        channel.setIconUrl(mMyChannel.getChannelCoverUrl());
                    }else {
                        channel.setIconUrl(item.getCoverUrlMiddle());
                    }
                    channel.setSinger(item.getSinger());
                    channel.setDuration(item.getDuration());
                    channel.setLastSyncTime(item.getUpdateAt());
                    channel.setServiceId(mServiceId);
                    channel.setAlbum(mMyChannel.getChannelName());
                    channel.setUri(item.getUri());
                    channel.setTimePeriods(item.getTimePeriods());
                    channel.setMantic_album_name(mMyChannel.getChannelName());
                    if (XimalayaSoundData.mAppSecret.equals(mMyChannel.getMusicServiceId())) {
                        channel.setMantic_album_uri("ximalaya:album:" + mMyChannel.getAlbumId());
                    } else {
                        if (mMyChannel.getAlbumId().contains("album:")) {
                            channel.setMantic_album_uri(mMyChannel.getAlbumId());
                        } else {
                            Glog.i(TAG, "mMyChannel.getAlbumId(): " + mMyChannel.getAlbumId());
                            if ("kaola".equals(mMyChannel.getMusicServiceId())) {
                                channel.setMantic_album_uri("kaola:album:" + mMyChannel.getAlbumId());
                            } else {
                                channel.setMantic_album_uri(item.getAlbumId());
                            }
                        }
                    }

                    int urlType = item.getUrlType();
                    if (urlType == IMusicServiceTrackContent.DYNAMIC_URL) {
                        channel.setIMusicServiceTrackContent(item);
                    } else if (urlType == IMusicServiceTrackContent.STATIC_URL) {
                        channel.setPlayUrl(item.getPlayUrl());
                    }

                    if (currChannel != null && currChannel.getUri().equals(channel.getUri())) {
                        channel.setPlayState(currChannel.getPlayState());
                    } else {
                        channel.setPlayState(Channel.PLAY_STATE_STOP);
                    }
                    channels.add(channel);
                }
            }
            tv_channel_album_size.setText(String.format(getString(R.string.channel_album_total_size), mMyChannel.getmTotalCount()));
            mMyChannel.setChannelList((ArrayList<Channel>) channels);

            if (changePotionStart == 1) {
                mChannelDetailsItemAdapter.setChannelList(channels);
            } else {
                this.mChannelDetailsItemAdapter.notifyItemRangeChanged(changePotionStart, itemCount);
            }
            mDataFactory.notifyBeingPlayListChange();

            if (fmStartChannels.size() > 0){
                currFmProgram = fmStartChannels.get(0).getName();
            }
        }

        if (items.size() < 20) {
            isNeedRefresh = false;
            mChannelDetailsItemAdapter.showLoadComplete();
        } else {
            isNeedRefresh = true;
        }

        if (channels.size() <= 6) {
            mChannelDetailsItemAdapter.showLoadEmpty();
        }

        isRefresh = false;
    }

    @Override
    public void onError(int code, String message) {
        Glog.i(TAG, "onError code = " + code + "---message = " + message);

        if (isRefresh) {
            isRefresh = false;
        }
        mChannelDetailsItemAdapter.showLoadComplete();

       /* if  (isFirstRefresh) {
            new Thread(new Runnable() {
                @Override
                public void run() {
//                    String channelListStr = ACacheUtil.getData(mContext, "channels" + mMyChannel.getChannelId());
                    String channelListStr = SharePreferenceUtil.getSharePreferenceData(mContext, "Mantic", "channels" + mMyChannel.getChannelId());
                    channels = GsonUtil.stringToChannelList(channelListStr);
                    mHandler.sendEmptyMessage(1);
                }
            }).start();
        }*/
        if (!NetworkUtils.isAvailableByPing(mContext)) {
            setRequestView(R.drawable.net_failed, R.string.network_suck);
        } else {
            setRequestView(R.drawable.net_failed, R.string.service_problem);
        }
    }


    public static class ChannelDetailsItemDecoration extends RecyclerView.ItemDecoration {
        private Context ctx;
        private int divideHeight = 1;
        private int divideMarginLeft;
        private Paint dividerPaint;

        public ChannelDetailsItemDecoration(Context context) {
            this.ctx = context;
            this.divideMarginLeft = ResourceUtils.dip2px(this.ctx, ResourceUtils.getXmlDef(this.ctx, R.dimen.channel_detail_more_list_item_left_padding));
            this.dividerPaint = new Paint();
            this.dividerPaint.setColor(this.ctx.getResources().getColor(R.color.mainTabBottomLineColor));
            divideHeight = 1;
        }

        @Override
        public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
            int childCount = parent.getChildCount();
            int left = parent.getPaddingLeft() + this.divideMarginLeft;
            int right = parent.getWidth() - parent.getPaddingRight();

            for (int i = 0; i < childCount - 1; i++) {
                View view = parent.getChildAt(i);
                float top = view.getBottom();
                float bottom = view.getBottom() + divideHeight;
                if (i == childCount - 1) {
                    c.drawRect(0, top, right, bottom, dividerPaint);
                } else {
                    c.drawRect(left, top, right, bottom, dividerPaint);
                }
            }
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            outRect.bottom = divideHeight;
        }
    }


    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.ll_channel_details_back:
                if (null != addCall) {
                    addCall.cancel();
                    addCall = null;
                }
                if (null != deleteCall) {
                    deleteCall.cancel();
                    deleteCall = null;
                }

                if (mActivity instanceof FragmentEntrust) {
                    ((FragmentEntrust) mActivity).popFragment(getTag());
                }
                break;
            case R.id.fragment_channel_detail_random_linearlayout:
                if (null != mMyChannel.getChannelList() && mMyChannel.getChannelList().size() > 0) {
                    int randomSize = (int) (Math.random() * mMyChannel.getChannelList().size());
                    mChannelDetailsItemAdapter.playPause(randomSize, null);
                    rcv_channel_detail.scrollToPosition(randomSize + 1);
                }

                break;
//            case R.id.fragment_channel_detail_desc_expandabletextview:
//                isExpend = !isExpend;
//                TextUtil.addImage(mContext, isExpend, mExpandableTextView, R.drawable.expandabletextview_expanddrawable, 2, mMyChannel.getChannelIntro());
//                break;

            case R.id.fragment_channel_detail_cover:
                ChannelDetailsDescribeFragment channelDetailsDescribeFragment = new ChannelDetailsDescribeFragment();
                Bundle bundle = new Bundle();
                bundle.putString("url", this.mMyChannel.getChannelCoverUrl());
                bundle.putString("desc", this.mMyChannel.getChannelIntro());
                channelDetailsDescribeFragment.setArguments(bundle);
                if (mActivity instanceof FragmentEntrust) {
                    ((FragmentEntrust) mActivity).pushFragment(channelDetailsDescribeFragment, "ChannelDetailsDescribeFragment");
                }
                break;
            case R.id.fragment_channel_details_more:
                moreStringList.clear();
                moreStringList.add(mMyChannel.getChannelName());
                moreStringList.add(getString(R.string.add_album_to_definition_mychannel));
                if (mMyChannel.isSelfDefinition()) {
                    moreStringList.add(getString(R.string.edit));
                }
                moreStringList.add(getString(R.string.cancel));
                mChannelDetailMoreAdapter.setMoreStringList(moreStringList);
                mDialog.show();
                break;
            case R.id.btn_fragment_channel_detail_add_enable:
                addChannel();
                break;
            case R.id.rl_channel_added_success:
                deleteChannel();
                break;
            default:
                break;
        }
    }

    @Override
    public void moreClickListener(int position) {
        channelList = new ArrayList<Channel>();
        channelList.add(mMyChannel.getChannelList().get(position));
        moreStringList.clear();
        moreStringList.add(channelList.get(0).getName());
        moreStringList.add(getString(R.string.add_music_to_definition_mychannel));
        moreStringList.add(getString(R.string.next_play));
        moreStringList.add(getString(R.string.last_play));
        if (mMyChannel.isSelfDefinition()) {
            moreStringList.add(getString(R.string.edit));
        }
        moreStringList.add(getString(R.string.cancel));
        mChannelDetailMoreAdapter.setMoreStringList(moreStringList);
        mDialog.show();
    }

    private int getChannelIndex() {
        ArrayList<MyChannel> list = mDataFactory.getMyChannelList();
        for (int i = 0; i < list.size(); i++) {
            if (mMyChannel.getUrl().equals(list.get(i).getUrl()) && mMyChannel.getChannelName().equals(list.get(i).getChannelName())) {
                return i;
            }
        }
        return -1;
    }

    private void addChannel() {
        if (null != deleteCall) {
            deleteCall.cancel();
        }
        mRefreshAnim.reset();
        iv_channel_detail_operator_refresh.clearAnimation();
        rl_channel_operator_refresh.setVisibility(View.VISIBLE);
        rl_channel_detail_operator.setBackground(getResources().getDrawable(R.drawable.rl_channel_detail_operator_bg));
        btn_fragment_channel_detail_add_enable.setVisibility(View.GONE);
        rl_channel_added_success.setVisibility(View.GONE);
        iv_channel_detail_operator_refresh.startAnimation(mRefreshAnim);

        addCall = mMyChannelOperatorServiceApi.postMyChannelAddQuest(MopidyTools.getHeaders(),Util.createAddRqBean(mMyChannel, mContext));
        MyChannelManager.addMyChannel(addCall, new Callback<MyChannelAddRsBean>() {
            @Override
            public void onResponse(Call<MyChannelAddRsBean> call, Response<MyChannelAddRsBean> response) {
                Glog.i(TAG, "onResponse: " + response.isSuccessful());
                if (response.isSuccessful() && null == response.errorBody()) {
                    if (isAdded()) {
                        mDataFactory.notifyOperatorResult(getString(R.string.success_add_this_channel), true);
                    }
                    mMyChannel.setUrl(response.body().result.uri);
                    mRefreshAnim.reset();
                    iv_channel_detail_operator_refresh.clearAnimation();
                    rl_channel_operator_refresh.setVisibility(View.GONE);
                    btn_fragment_channel_detail_add_enable.setVisibility(View.GONE);
                    rl_channel_detail_operator.setBackground(null);
                    rl_channel_added_success.setVisibility(View.VISIBLE);
                    if (mMyChannel.getChannelType() == 2){
                        mMyChannel.setChannelIntro(currFmProgram);
                    }
                    mDataFactory.addMyChannel(mMyChannel);
                    mAlbumIsInMyChannelList = true;
                    mDataFactory.notifyMyLikeMusicStatusChange();
                } else {
                    if (isAdded()) {
                        mDataFactory.notifyOperatorResult(getString(R.string.failed_add_this_channel), false);
                    }
                    mRefreshAnim.reset();
                    iv_channel_detail_operator_refresh.clearAnimation();
                    rl_channel_operator_refresh.setVisibility(View.GONE);
                    btn_fragment_channel_detail_add_enable.setVisibility(View.VISIBLE);
                    rl_channel_detail_operator.setBackground(null);
                    rl_channel_added_success.setVisibility(View.GONE);
                    mAlbumIsInMyChannelList = false;
                }
            }

            @Override
            public void onFailure(Call<MyChannelAddRsBean> call, Throwable t) {
                if (isAdded()) {
                    mDataFactory.notifyOperatorResult(getString(R.string.failed_add_this_channel), false);
                }

                mRefreshAnim.reset();
                iv_channel_detail_operator_refresh.clearAnimation();
                rl_channel_operator_refresh.setVisibility(View.GONE);
                btn_fragment_channel_detail_add_enable.setVisibility(View.VISIBLE);
                rl_channel_detail_operator.setBackground(null);
                rl_channel_added_success.setVisibility(View.GONE);
                mAlbumIsInMyChannelList = false;
            }
        }, mMyChannel);
    }

    private void deleteChannel() {
        if (null != addCall) {
            addCall.cancel();
        }

        mRefreshAnim.reset();
        iv_channel_detail_operator_refresh.clearAnimation();
        rl_channel_operator_refresh.setVisibility(View.VISIBLE);
        rl_channel_detail_operator.setBackground(getResources().getDrawable(R.drawable.rl_channel_detail_operator_bg));
        btn_fragment_channel_detail_add_enable.setVisibility(View.GONE);
        rl_channel_added_success.setVisibility(View.GONE);
        iv_channel_detail_operator_refresh.startAnimation(mRefreshAnim);

        if (getChannelIndex() != -1) {
            deleteCall = mMyChannelOperatorServiceApi.postMyChannelDeleteQuest(MopidyTools.getHeaders(),Util.createDeleteRqBean(mMyChannel, mContext));
            MyChannelManager.getInstance().deleteMyChannel(deleteCall, new Callback<MyChannelDeleteRsBean>() {
                @Override
                public void onResponse(Call<MyChannelDeleteRsBean> call, Response<MyChannelDeleteRsBean> response) {
                    if (response.isSuccessful() && null == response.errorBody()) {
                        if (isAdded()) {
                            mDataFactory.notifyOperatorResult(getString(R.string.success_delete_this_channel), true);
                        }
                        mRefreshAnim.reset();
                        iv_channel_detail_operator_refresh.clearAnimation();
                        rl_channel_operator_refresh.setVisibility(View.GONE);
                        btn_fragment_channel_detail_add_enable.setVisibility(View.VISIBLE);
                        rl_channel_detail_operator.setBackground(null);
                        rl_channel_added_success.setVisibility(View.GONE);
                        mDataFactory.removeMyChannel(mMyChannel);
                        mDataFactory.removeDefinnitionMyChannel(mContext, mMyChannel);
                        mAlbumIsInMyChannelList = false;
                        mDataFactory.notifyMyLikeMusicStatusChange();
                    } else {
                        if (isAdded()) {
                            mDataFactory.notifyOperatorResult(getString(R.string.failed_delete_this_channel), false);
                        }
                        mRefreshAnim.reset();
                        iv_channel_detail_operator_refresh.clearAnimation();
                        rl_channel_operator_refresh.setVisibility(View.GONE);
                        btn_fragment_channel_detail_add_enable.setVisibility(View.GONE);
                        rl_channel_detail_operator.setBackground(null);
                        rl_channel_added_success.setVisibility(View.VISIBLE);
                        mAlbumIsInMyChannelList = true;
                    }
                }

                @Override
                public void onFailure(Call<MyChannelDeleteRsBean> call, Throwable t) {
                    if (isAdded()) {
                        mDataFactory.notifyOperatorResult(getString(R.string.failed_delete_this_channel), false);
                    }

                    mRefreshAnim.reset();
                    iv_channel_detail_operator_refresh.clearAnimation();
                    rl_channel_operator_refresh.setVisibility(View.GONE);
                    btn_fragment_channel_detail_add_enable.setVisibility(View.GONE);
                    rl_channel_detail_operator.setBackground(null);
                    rl_channel_added_success.setVisibility(View.VISIBLE);
                    mAlbumIsInMyChannelList = true;
                }
            }, mMyChannel);
        }
    }

    @Override
    public void onItemClick(View view, int position) {
        switch (position) {
            case 1:
                if (mMyChannel.isSelfDefinition()) {
                    ChannelAddFragment channelAddFragment = new ChannelAddFragment();
                    Bundle bundle = new Bundle();
                    bundle.putString("myChannelName", mMyChannel.getChannelName());
                    if (moreStringList.get(1).equals(getString(R.string.add_album_to_definition_mychannel))) {
                        bundle.putSerializable("channelList", mMyChannel.getChannelList());
                    } else {
                        bundle.putSerializable("channelList", channelList);
                    }
                    channelAddFragment.setArguments(bundle);
                    if (mActivity instanceof FragmentEntrust) {
                        ((FragmentEntrust) mActivity).pushFragment(channelAddFragment, "ChannelAddFragment");
                    }
                } else if ("SearchResultFragment".equals(mChannelFrom)) {
                    if (moreStringList.get(1).equals(getString(R.string.add_album_to_definition_mychannel))) {
                        mDataFactory.notifyChannelAddChange(mMyChannel.getChannelList());
                    } else {
                        mDataFactory.notifyChannelAddChange(channelList);
                    }
                } else {
                    ChannelAddFragment channelAddFragment = new ChannelAddFragment();
                    Bundle bundle = new Bundle();
                    if (moreStringList.get(1).equals(getString(R.string.add_album_to_definition_mychannel))) {
                        bundle.putSerializable("channelList", mMyChannel.getChannelList());
                    } else {
                        bundle.putSerializable("channelList", channelList);
                    }
                    channelAddFragment.setArguments(bundle);
                    if (mActivity instanceof FragmentEntrust) {
                        ((FragmentEntrust) mActivity).pushFragment(channelAddFragment, "ChannelAddFragment");
                    }
                }

                break;
            case 2:
                if (moreStringList.get(2).equals(getString(R.string.edit))) {
                    enterEditFragment();
                    break;
                }

                AudioPlayerUtil.insertChannel(mDataFactory, AudioPlayerUtil.NEXT_INSERT, mContext, channelList);
                break;
            case 3:
                AudioPlayerUtil.insertChannel(mDataFactory, AudioPlayerUtil.LAST_INSERT, mContext, channelList);
                break;
            case 4:
                enterEditFragment();
                break;
            case 5:
                break;
            default:
                break;
        }
        mDialog.dismiss();
    }


    private void enterEditFragment() {
        if (mMyChannel.isSelfDefinition()) {
            DefinitionChannelEditFragment fragment = new DefinitionChannelEditFragment();
            fragment.setRefreshMyChannelListener(this);
            Bundle bundle = new Bundle();

            bundle.putString("channelName", mMyChannel.getChannelName());
            bundle.putString("singerName", mMyChannel.getSingerName());
            if (mMyChannel.getmUpdateAt() != 0) {
                bundle.putString("syncTime", TimeUtil.getDateFromMillisecond(mMyChannel.getmUpdateAt()));
            }
            bundle.putString("channelCoverUrl", mMyChannel.getChannelCoverUrl());
            bundle.putString("channelInfo", mMyChannel.getChannelIntro());
            bundle.putString("albumTags", mMyChannel.getChannelTags());
            bundle.putString("mainId", mMyChannel.getMainId());
            bundle.putString("url", mMyChannel.getUrl());
            bundle.putString("albumId", mMyChannel.getAlbumId());
            bundle.putSerializable("channelList", mMyChannel.getChannelList());

            fragment.setArguments(bundle);
            if (mActivity instanceof FragmentEntrust) {
                ((FragmentEntrust) mActivity).pushFragment(fragment, "DefinitionChannelEditFragment");
            }
        } else {

        }
    }


    @Override
    public void refreshMyChannel(MyChannel myChannel) {
        mMyChannel.setChannelName(myChannel.getChannelName());
        mDetailChannelName.setText(myChannel.getChannelName());
        mMyChannel.setChannelIntro(myChannel.getChannelIntro());
        mMyChannel.setChannelList(myChannel.getChannelList());
        mChannelDetailsItemAdapter.setChannelList(myChannel.getChannelList());
        mMyChannel.setmTotalCount(myChannel.getmTotalCount());
        if (this.mMyChannel.getmTotalCount() == 0) {
            tv_channel_album_size.setVisibility(View.GONE);
        } else {
            tv_channel_album_size.setVisibility(View.VISIBLE);
        }

        tv_channel_album_size.setText(String.format(getString(R.string.channel_album_total_size), mMyChannel.getmTotalCount()));
        String coverUrl = myChannel.getChannelCoverUrl();
        if (coverUrl != null && !coverUrl.isEmpty()) {
                GlideImgManager.glideLoaderCircle(mContext, coverUrl, R.drawable.fragment_channel_detail_cover,
                    R.drawable.fragment_channel_detail_cover, mDetailChannelCover);
        }

        if (isAdded()) {
            mDataFactory.notifyOperatorResult(getString(R.string.save_success), true);
        }

        ArrayList<MyChannel> myChannelList = mDataFactory.getMyChannelList();
        for (int i = 0; i < myChannelList.size(); i++) {
            MyChannel mychannel1 = myChannelList.get(i);
            if (mychannel1.getChannelName().equals(myChannel.getChannelName()) && mychannel1.getUrl().equals(myChannel.getUrl())) {
                mychannel1.setChannelName(myChannel.getChannelName());
                mychannel1.setmTotalCount(myChannel.getmTotalCount());
            }
        }
        mDataFactory.notifyMyChannelListChanged();
    }

    @Override
    public void refreshFail(String content) {
        if (isAdded()) {
            mDataFactory.notifyOperatorResult(getString(R.string.save_fail), false);
        }
    }

    private class MyGlobalLayoutListener implements ViewTreeObserver.OnGlobalLayoutListener {
        private TextView textView;
        private int res;
        private int maxLines;

        public MyGlobalLayoutListener(TextView textView, int res, int maxLines) {
            this.textView = textView;
            this.res = res;
            this.maxLines = maxLines;
        }

        @Override
        public void onGlobalLayout() {
            TextUtil.setExpandTextView(mContext, textView, res, maxLines, TextUtil.ToDBC(mMyChannel.getChannelIntro()), 0);
            if (Build.VERSION.SDK_INT >= 16) {
                textView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            } else {
                textView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
            }
        }
    }


}