package com.mantic.control.fragment;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mantic.antservice.util.ToastUtils;
import com.mantic.control.R;
import com.mantic.control.activity.MainActivity;
import com.mantic.control.adapter.ChannelDetailMoreAdapter;
import com.mantic.control.adapter.EntertainmentChildEducationAdapter;
import com.mantic.control.adapter.EntertainmentChildEducationMoreAdapter;
import com.mantic.control.adapter.EntertainmentNetizenAdapter;
import com.mantic.control.adapter.EntertainmentNewSongAdapter;
import com.mantic.control.adapter.EntertainmentRecommendAdapter;
import com.mantic.control.adapter.EntertainmentVoicedAdapter;
import com.mantic.control.adapter.EntertainmentVoicedMoreAdapter;
import com.mantic.control.adapter.NewSongAdapter;
import com.mantic.control.api.entertainment.bean.BannerListBean;
import com.mantic.control.api.entertainment.bean.BannerListResultBean;
import com.mantic.control.api.entertainment.bean.NewSongListBean;
import com.mantic.control.api.entertainment.bean.NewSongResultBean;
import com.mantic.control.api.entertainment.bean.PopularSongListBean;
import com.mantic.control.api.entertainment.bean.PopularSongResultBean;
import com.mantic.control.api.mopidy.MopidyTools;
import com.mantic.control.data.Channel;
import com.mantic.control.data.MyChannel;
import com.mantic.control.decoration.ChanneDetailMoreItemDecoration;
import com.mantic.control.manager.EntertainmentResultManager;
import com.mantic.control.manager.MyChannelManager;
import com.mantic.control.musicservice.MyMusicService;
import com.mantic.control.utils.AudioPlayerUtil;
import com.mantic.control.utils.Glog;
import com.mantic.control.utils.GsonUtil;
import com.mantic.control.utils.NetworkUtils;
import com.mantic.control.utils.SharePreferenceUtil;
import com.mantic.control.utils.Utility;
import com.mantic.control.widget.CustomViewPager;
import com.mantic.control.widget.VpSwipeRefreshLayout;
import com.recker.flybanner.FlyBanner;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * 兴趣界面
 * Created by lin on 2018/1/15.
 */

public class EntertainmentFragment extends BaseFragment implements View.OnClickListener,
        EntertainmentVoicedAdapter.OnEntertainmentVoicedItemClickListener,
        EntertainmentChildEducationAdapter.OnEntertainmentChildEducationItemClickListener,
        NewSongAdapter.OnItemMoreClickListener, ChannelDetailMoreAdapter.OnItemClickLitener ,
        SwipeRefreshLayout.OnRefreshListener, FlyBanner.OnItemClickListener {

    private final String TAG = EntertainmentFragment.class.getSimpleName();

    private VpSwipeRefreshLayout srl_entertainment;

    private FlyBanner fb_entertainment_banner;
    private LinearLayout ll_song_sheet;
    private LinearLayout ll_music_rank;
    private LinearLayout ll_popular_singer;
    private LinearLayout ll_fm_radio;

    private LinearLayout ll_show_entertainment_voice_more;
    private ImageView iv_show_entertainment_voice_more;
    private TextView tv_show_entertainment_voice_more;
    private ImageView iv_entertainment_recommend_more;
    private ImageView iv_entertainment_netizen_more;

    private RecyclerView rv_entertainment_recommend;
    //    private RecyclerView rv_entertainment_new_song;
    private CustomViewPager vp_entertainment_new_song;
    private RecyclerView rv_entertainment_netizen;
    private RecyclerView rv_entertainment_voiced;
    private RecyclerView rv_entertainment_voiced_more;
    private RecyclerView rv_entertainment_child_education;
    private RecyclerView rv_entertainment_child_education_more;
    private EntertainmentRecommendAdapter mEntertainmentRecommendAdapter;
    private EntertainmentNewSongAdapter mEntertainmentNewSongAdapter;
    private EntertainmentNetizenAdapter mEntertainmentNetizenAdapter;
    private EntertainmentVoicedAdapter mEntertainmentVoicedAdapter;
    private EntertainmentVoicedMoreAdapter mEntertainmentVoicedMoreAdapter;
    private EntertainmentChildEducationAdapter mEntertainmentChildEducationAdapter;
    private EntertainmentChildEducationMoreAdapter mEntertainmentChildEducationMoreAdapter;

    private Dialog mDialog;
    private List<String> moreStringList;
    private List<Channel> newSongList;
    private ArrayList<Channel> channelList;
    private List<BannerListBean> bannerBeanList = new ArrayList<>();
    private ChannelDetailMoreAdapter mChannelDetailMoreAdapter;

    private EntertainmentResultManager mEntertainmentResultManager;

    @Override
    protected void initData(Bundle arguments) {
        super.initData(arguments);
        //banner

        //推荐
        mEntertainmentRecommendAdapter = new EntertainmentRecommendAdapter(mContext);
        rv_entertainment_recommend.setAdapter(mEntertainmentRecommendAdapter);
        rv_entertainment_recommend.setNestedScrollingEnabled(false);

        //新歌
        mEntertainmentNewSongAdapter = new EntertainmentNewSongAdapter(mContext, mActivity, null);
        mEntertainmentNewSongAdapter.setOnItemMoreClickListener(this);
        vp_entertainment_new_song.setAdapter(mEntertainmentNewSongAdapter);

        //网友歌单
        //推荐
        mEntertainmentNetizenAdapter = new EntertainmentNetizenAdapter(mContext);
        rv_entertainment_netizen.setAdapter(mEntertainmentNetizenAdapter);
        rv_entertainment_netizen.setNestedScrollingEnabled(false);
        List<MyChannel> netizenList = new ArrayList<>();
        mEntertainmentNetizenAdapter.setNetizenList(netizenList);

        //有声
        mEntertainmentVoicedAdapter = new EntertainmentVoicedAdapter(mContext);
        mEntertainmentVoicedAdapter.setOnEntertainmentVoicedItemClickListener(this);
        rv_entertainment_voiced.setAdapter(mEntertainmentVoicedAdapter);
        rv_entertainment_voiced.setNestedScrollingEnabled(false);
        mEntertainmentVoicedMoreAdapter = new EntertainmentVoicedMoreAdapter(mContext);
        mEntertainmentVoicedMoreAdapter.setOnEntertainmentVoicedItemClickListener(this);
        rv_entertainment_voiced_more.setAdapter(mEntertainmentVoicedMoreAdapter);
        rv_entertainment_voiced_more.setNestedScrollingEnabled(false);


        //儿童
        mEntertainmentChildEducationAdapter = new EntertainmentChildEducationAdapter(mContext);
        mEntertainmentChildEducationAdapter.setOnEntertainmentChildEducationItemClickListener(this);
        rv_entertainment_child_education.setAdapter(mEntertainmentChildEducationAdapter);
        rv_entertainment_child_education.setNestedScrollingEnabled(false);
        mEntertainmentChildEducationMoreAdapter = new EntertainmentChildEducationMoreAdapter(mContext);
        mEntertainmentChildEducationMoreAdapter.setOnEntertainmentChildEducationItemClickListener(this);
        rv_entertainment_child_education_more.setAdapter(mEntertainmentChildEducationMoreAdapter);
        rv_entertainment_child_education_more.setNestedScrollingEnabled(false);


        getCacheData();
        srl_entertainment.setRefreshing(true);
        getEntertainmentData();
    }

    @Override
    protected void initView(View view) {
        super.initView(view);
        srl_entertainment = (VpSwipeRefreshLayout) view.findViewById(R.id.srl_entertainment);
        fb_entertainment_banner = (FlyBanner) view.findViewById(R.id.fb_entertainment_banner);
        ll_song_sheet = (LinearLayout) view.findViewById(R.id.ll_song_sheet);
        ll_music_rank = (LinearLayout) view.findViewById(R.id.ll_music_rank);
        ll_popular_singer = (LinearLayout) view.findViewById(R.id.ll_popular_singer);
        ll_fm_radio = (LinearLayout) view.findViewById(R.id.ll_fm_radio);
        ll_show_entertainment_voice_more = (LinearLayout) view.findViewById(R.id.ll_show_entertainment_voice_more);
        iv_show_entertainment_voice_more = (ImageView) view.findViewById(R.id.iv_show_entertainment_voice_more);
        tv_show_entertainment_voice_more = (TextView) view.findViewById(R.id.tv_show_entertainment_voice_more);
        rv_entertainment_recommend = (RecyclerView) view.findViewById(R.id.rv_entertainment_recommend);
        rv_entertainment_recommend.setLayoutManager(new GridLayoutManager(mContext, 3));
        vp_entertainment_new_song = (CustomViewPager) view.findViewById(R.id.vp_entertainment_new_song);
        rv_entertainment_netizen = (RecyclerView) view.findViewById(R.id.rv_entertainment_netizen);
        rv_entertainment_netizen.setLayoutManager(new GridLayoutManager(mContext, 3));
        rv_entertainment_voiced = (RecyclerView) view.findViewById(R.id.rv_entertainment_voiced);
        rv_entertainment_voiced.setLayoutManager(new GridLayoutManager(mContext, 3));
        rv_entertainment_voiced_more = (RecyclerView) view.findViewById(R.id.rv_entertainment_voiced_more);
        rv_entertainment_voiced_more.setLayoutManager(new GridLayoutManager(mContext, 2));
        rv_entertainment_child_education = (RecyclerView) view.findViewById(R.id.rv_entertainment_child_education);
        rv_entertainment_child_education.setLayoutManager(new GridLayoutManager(mContext, 2));
        rv_entertainment_child_education_more = (RecyclerView) view.findViewById(R.id.rv_entertainment_child_education_more);
        rv_entertainment_child_education_more.setLayoutManager(new GridLayoutManager(mContext, 2));

        iv_entertainment_recommend_more = (ImageView) view.findViewById(R.id.iv_entertainment_recommend_more);
        iv_entertainment_netizen_more = (ImageView) view.findViewById(R.id.iv_entertainment_netizen_more);

        rv_entertainment_recommend.setFocusable(false);
        rv_entertainment_netizen.setFocusable(false);
        rv_entertainment_voiced.setFocusable(false);
        rv_entertainment_voiced_more.setFocusable(false);
        rv_entertainment_child_education.setFocusable(false);
        rv_entertainment_child_education_more.setFocusable(false);

        srl_entertainment.setOnRefreshListener(this);
        ll_song_sheet.setOnClickListener(this);
        ll_music_rank.setOnClickListener(this);
        ll_popular_singer.setOnClickListener(this);
        ll_fm_radio.setOnClickListener(this);
        ll_show_entertainment_voice_more.setOnClickListener(this);
        iv_entertainment_recommend_more.setOnClickListener(this);
        iv_entertainment_netizen_more.setOnClickListener(this);
        fb_entertainment_banner.setOnItemClickListener(this);

        mDialog = Utility.getDialog(mContext, R.layout.channel_detail_more_adapter);
        RecyclerView rv_channel_detail_more = (RecyclerView) mDialog.findViewById(R.id.rv_channel_detail_more);
        rv_channel_detail_more.setLayoutManager(new LinearLayoutManager(mContext));
        rv_channel_detail_more.addItemDecoration(new ChanneDetailMoreItemDecoration(mContext));
        moreStringList = new ArrayList<String>();
        moreStringList.add("");
        moreStringList.add(getString(R.string.add_music_to_definition_mychannel));
        moreStringList.add(getString(R.string.next_play));
        moreStringList.add(getString(R.string.last_play));
        moreStringList.add(getString(R.string.cancel));
        mChannelDetailMoreAdapter = new ChannelDetailMoreAdapter(mContext, moreStringList);
        mChannelDetailMoreAdapter.setmOnItemClickLitener(this);
        rv_channel_detail_more.setAdapter(mChannelDetailMoreAdapter);

    }

    //获取缓存数据
    private void getCacheData() {
        String entertainmentRecommendSongListStr = SharePreferenceUtil.getSharePreferenceData(mContext, "Mantic", "EntertainmentRecommendSongList");
        if (!TextUtils.isEmpty(entertainmentRecommendSongListStr)) {
            ArrayList<MyChannel> myChannels = GsonUtil.stringToMyChannelList(entertainmentRecommendSongListStr);
           mEntertainmentRecommendAdapter.setRecommendList(myChannels);
        }

        String entertainmentNewSongListStr = SharePreferenceUtil.getSharePreferenceData(mContext, "Mantic", "EntertainmentNewSongList");
        if (!TextUtils.isEmpty(entertainmentNewSongListStr)) {
            List<Channel> channels = GsonUtil.stringToChannelList(entertainmentNewSongListStr);
            mEntertainmentNewSongAdapter.setNewSongList(channels);
        }

        String entertainmentNetizenSongListStr = SharePreferenceUtil.getSharePreferenceData(mContext, "Mantic", "EntertainmentNetizenSongList");
        if (!TextUtils.isEmpty(entertainmentNetizenSongListStr)) {
            ArrayList<MyChannel> myChannels = GsonUtil.stringToMyChannelList(entertainmentNetizenSongListStr);
            mEntertainmentNetizenAdapter.setNetizenList(myChannels);
        }
    }


    //获取歌单数据
    public void getEntertainmentData() {
        mEntertainmentResultManager = EntertainmentResultManager.getInstance();
        getEntertainmentBannerData();
        getEntertainmentPopularSongListData();
        getEntertainmentNewSongListData();
        getEntertainmentNetizenListData();
    }

    /**
     * 获取banner数据
     */
    private void getEntertainmentBannerData() {
        mEntertainmentResultManager.getBannerListResult(new Callback<BannerListResultBean>() {
            @Override
            public void onResponse(Call<BannerListResultBean> call, Response<BannerListResultBean> response) {
                if (response.isSuccessful() && null == response.errorBody()) {
                    BannerListResultBean bannerListResultBean = response.body();
                    if (null != bannerListResultBean && null != bannerListResultBean.getResult() && bannerListResultBean.getResult().size()>0) {
                        bannerBeanList.clear();
                        final List<BannerListBean> bannerListBeanList = bannerListResultBean.getResult();
                        Glog.i(TAG,"bannerListBeanList:" + bannerListBeanList);
                        List<String> bannerUris = new ArrayList<String>();
                        for (int i = 0; i < bannerListBeanList.size(); i++) {
                            bannerUris.add(bannerListBeanList.get(i).getMantic_image());
                        }
                        fb_entertainment_banner.setImagesUrl(bannerUris);
                        bannerBeanList = bannerListBeanList;
                    }
                }
            }

            @Override
            public void onFailure(Call<BannerListResultBean> call, Throwable t) {

            }
        }, mContext, 0, 20);
    }

    /**
     * 推荐专辑
     */
    private void getEntertainmentPopularSongListData() {
        mEntertainmentResultManager.getPopularSongListResult(new Callback<PopularSongResultBean>() {
            @Override
            public void onResponse(Call<PopularSongResultBean> call, Response<PopularSongResultBean> response) {
                if (response.isSuccessful() && null == response.errorBody()) {
                    PopularSongResultBean popularSongResultBean = response.body();
                    if (null != popularSongResultBean && null != popularSongResultBean.getResult()) {
                        List<PopularSongListBean> popularSongListResult = popularSongResultBean.getResult();
                        if (popularSongListResult.size() > 0) {
                            List<MyChannel> myChannelList = new ArrayList<MyChannel>();
                            for (int i = 0; i < popularSongListResult.size(); i++) {
                                PopularSongListBean popularSongListBean = popularSongListResult.get(i);
                                MyChannel myChannel = new MyChannel();
                                myChannel.setMusicServiceId("entertainment");
                                myChannel.setChannelName(popularSongListBean.getName());
                                myChannel.setChannelIntro(popularSongListBean.getMantic_describe());
                                myChannel.setChannelCoverUrl(popularSongListBean.getMantic_image());
                                if (null != popularSongListBean.getMantic_artists_name() && popularSongListBean.getMantic_artists_name().size() > 0) {
                                    String singerName = "";
                                    for (int j = 0; j < popularSongListBean.getMantic_artists_name().size(); j++) {
                                        singerName = singerName + popularSongListBean.getMantic_artists_name().get(j);
                                    }
                                    myChannel.setSingerName(singerName);
                                }
                                myChannel.setChannelType(3);
                                myChannel.setUrl(popularSongListBean.getUri());
                                myChannel.setPlayCount(popularSongListBean.getMantic_play_count());
                                myChannel.setmTotalCount(popularSongListBean.getMantic_num_tracks());
                                myChannel.setChannelId(popularSongListBean.getMantic_image());
                                myChannel.setAlbumId(popularSongListBean.getUri());
                                myChannelList.add(myChannel);
                            }

                            mEntertainmentRecommendAdapter.setRecommendList(myChannelList);
                            if (myChannelList.size() <= 6) {
                                iv_entertainment_recommend_more.setVisibility(View.GONE);
                            } else {
                                iv_entertainment_recommend_more.setVisibility(View.VISIBLE);
                            }

                            SharePreferenceUtil.setSharePreferenceData(mContext, "Mantic", "EntertainmentRecommendSongList", GsonUtil.myChannelListToString((ArrayList<MyChannel>) myChannelList));
                        }
                    }
                    srl_entertainment.setRefreshing(false);
                    mDataFactory.notifyOperatorResult(mContext.getString(R.string.refresh_data_success), true);
                } else {
                    srl_entertainment.setRefreshing(false);
                    if (!NetworkUtils.isAvailableByPing(mContext)) {
                        mDataFactory.notifyOperatorResult(mContext.getString(R.string.network_suck), false);
                    } else {
                        mDataFactory.notifyOperatorResult(mContext.getString(R.string.net_failed), false);
                    }
                }
            }

            @Override
            public void onFailure(Call<PopularSongResultBean> call, Throwable t) {
                srl_entertainment.setRefreshing(false);
                if (!NetworkUtils.isAvailableByPing(mContext)) {
                    mDataFactory.notifyOperatorResult(mContext.getString(R.string.network_suck), false);
                } else {
                    mDataFactory.notifyOperatorResult(mContext.getString(R.string.net_failed), false);
                }
            }
        }, mContext, 0, 10);
    }

    //获取新歌首发
    private void getEntertainmentNewSongListData() {
        mEntertainmentResultManager.getNewSongListResult(new Callback<NewSongResultBean>() {
            @Override
            public void onResponse(Call<NewSongResultBean> call, Response<NewSongResultBean> response) {
                if (response.isSuccessful() && null == response.errorBody()) {
                    if (null != response.body() && null != response.body().getResult()) {
                        newSongList = new ArrayList<Channel>();
                        List<NewSongListBean> newSongListBeanList = response.body().getResult();
                        for (int i = 0; i < newSongListBeanList.size(); i++) {
                            NewSongListBean newSongListBean = newSongListBeanList.get(i);
                            Channel channel = new Channel();
                            channel.setIconUrl(newSongListBean.getMantic_image());
                            if (null != newSongListBean.getMantic_artists_name() && newSongListBean.getMantic_artists_name().size() > 0) {
                                String singerName = "";
                                for (int j = 0; j < newSongListBean.getMantic_artists_name().size(); j++) {
                                    singerName = singerName + newSongListBean.getMantic_artists_name().get(j);
                                }
                                channel.setSinger(singerName);
                            }
                            channel.setName(newSongListBean.getName());
                            channel.setDuration(newSongListBean.getMantic_length());
                            channel.setPlayUrl(newSongListBean.getMantic_real_url());
                            channel.setMantic_album_name(newSongListBean.getMantic_album_name());
                            channel.setMantic_album_uri(newSongListBean.getMantic_album_uri());
                            channel.setUri(newSongListBean.getUri());
                            newSongList.add(channel);
                        }

                        mEntertainmentNewSongAdapter.setNewSongList(newSongList);
                        SharePreferenceUtil.setSharePreferenceData(mContext, "Mantic", "EntertainmentNewSongList", GsonUtil.channellistToString(newSongList));
                    }
                }
            }

            @Override
            public void onFailure(Call<NewSongResultBean> call, Throwable t) {

            }
        }, mContext, 0, 20);

    }

    //获取网友歌单
    private void getEntertainmentNetizenListData() {
        mEntertainmentResultManager.getNetzenListResult(new Callback<PopularSongResultBean>() {
            @Override
            public void onResponse(Call<PopularSongResultBean> call, Response<PopularSongResultBean> response) {
                if (response.isSuccessful() && null == response.errorBody()) {
                    PopularSongResultBean popularSongResultBean = response.body();
                    if (null != popularSongResultBean && null != popularSongResultBean.getResult()) {
                        List<PopularSongListBean> popularSongListResult = popularSongResultBean.getResult();
                        if (popularSongListResult.size() > 0) {
                            List<MyChannel> myChannelList = new ArrayList<MyChannel>();
                            for (int i = 0; i < popularSongListResult.size(); i++) {
                                PopularSongListBean popularSongListBean = popularSongListResult.get(i);
                                MyChannel myChannel = new MyChannel();
                                myChannel.setMusicServiceId("qingting");
                                myChannel.setChannelName(popularSongListBean.getName());
                                myChannel.setChannelIntro(popularSongListBean.getMantic_describe());
                                myChannel.setChannelCoverUrl(popularSongListBean.getMantic_image());
                                if (null != popularSongListBean.getMantic_artists_name() && popularSongListBean.getMantic_artists_name().size() > 0) {
                                    String singerName = "";
                                    for (int j = 0; j < popularSongListBean.getMantic_artists_name().size(); j++) {
                                        singerName = singerName + popularSongListBean.getMantic_artists_name().get(j);
                                    }
                                    myChannel.setSingerName(singerName);
                                }
                                myChannel.setChannelType(3);
                                myChannel.setUrl(popularSongListBean.getUri());
                                myChannel.setPlayCount(popularSongListBean.getMantic_play_count());
                                myChannel.setmTotalCount(popularSongListBean.getMantic_num_tracks());
                                myChannel.setChannelId(popularSongListBean.getMantic_image());
                                myChannel.setAlbumId(popularSongListBean.getUri());
                                myChannelList.add(myChannel);
                            }

                            mEntertainmentNetizenAdapter.setNetizenList(myChannelList);
                            if (myChannelList.size() <= 3) {
                                iv_entertainment_netizen_more.setVisibility(View.GONE);
                            } else {
                                iv_entertainment_netizen_more.setVisibility(View.VISIBLE);
                            }

                            SharePreferenceUtil.setSharePreferenceData(mContext, "Mantic", "EntertainmentNetizenSongList", GsonUtil.myChannelListToString((ArrayList<MyChannel>) myChannelList));
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<PopularSongResultBean> call, Throwable t) {

            }
        }, mContext, 0, 10);
    }

    @Override
    public void onItemClick(int position) {//banner的点击事件
        if (null != bannerBeanList && bannerBeanList.size() > position) {
            final BannerListBean bannerListBean = bannerBeanList.get(position);
            if ("playlist".equals(bannerListBean.getMantic_describe())) {//banner是专辑
                MyChannelManager.getInstance().getAlbumDetail(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        if (response.isSuccessful() && null == response.errorBody() && null != response.body()) {
                            if (response.isSuccessful() && null == response.errorBody()) {
                                try {
                                    JSONObject mainObject = new JSONObject(response.body().string()); //json 主体
                                    JSONObject resultObject = mainObject.getJSONObject("result"); // result json 主体
                                    JSONArray jsonArray = resultObject.getJSONArray(bannerListBean.getUri());
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
                                        case "organized":
                                            serviceId = "entertainment";
                                            break;
                                    }
                                    ChannelDetailsFragment cdFragment = new ChannelDetailsFragment();
                                    String channelName = jsonObject.optString("name");
                                    String channnelId = jsonObject.optString("mantic_image");
                                    Bundle bundle = new Bundle();
                                    bundle.putString(MyMusicService.MY_MUSIC_SERVICE_ID, serviceId);
                                    bundle.putString(ChannelDetailsFragment.CHANNEL_ID, channnelId);
                                    bundle.putString(ChannelDetailsFragment.CHANNEL_NAME, channelName);
                                    bundle.putString(ChannelDetailsFragment.CHANNEL_FROM, "Entertainment");
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
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {

                    }
                }, bannerListBean.getUri());

            } else if ("track".equals(bannerListBean.getMantic_describe())) {//单首歌
                List<String> uris = new ArrayList<>();
                uris.add(bannerListBean.getUri());
                RequestBody body = MopidyTools.createTrackDetail(uris);
                Call<ResponseBody> call = MainActivity.mpServiceApi.postMopidyTrackDetails(MopidyTools.getHeaders(),body);
                call.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        if (response.isSuccessful() && null == response.errorBody() && null != response.body()) {
                            try {
                                JSONObject mainObject = new JSONObject(response.body().string()); //json 主体
                                JSONObject resuleObject = mainObject.getJSONObject("result"); // result json 主体
                                JSONArray trackArray = resuleObject.getJSONArray(bannerListBean.getUri()); //获取第一个item 作为整个TRACKLIST封面
                                if (null != trackArray && trackArray.length() > 0) {
                                    JSONObject trackObject = trackArray.getJSONObject(0);
                                    Channel channel = new Channel();
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
                                    channel.setServiceId("baidu");

                                    AudioPlayerUtil.playOrPause(mContext, channel, null, mActivity, mDataFactory);
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
            }
        }
    }

    @Override
    public void onRefresh() {
        getEntertainmentData();
    }

    @Override
    public void entertainmentVoicedItemClick(String name, int pre_data_type, String uri) {
        Bundle bundle = new Bundle();
        bundle.putString(MyMusicService.MY_MUSIC_SERVICE_ID, "qingting");
        bundle.putString(MusicServiceSubItemFragment.SUB_ITEM_TITLE, name);
        bundle.putInt(MyMusicService.PRE_DATA_TYPE, pre_data_type);
        if (pre_data_type == 0) {
            bundle.putString("category_id", uri);
        } else if (pre_data_type == 1) {
            bundle.putString("tag_name", uri);
        }
        MusicServiceSubItemFragment musicServiceSubItemFragment = new MusicServiceSubItemFragment();
        musicServiceSubItemFragment.setArguments(bundle);
        if (mActivity instanceof FragmentEntrust) {
            ((FragmentEntrust) getActivity()).pushFragment(musicServiceSubItemFragment, EntertainmentFragment.class.getName());
        }
    }

    @Override
    public void entertainmentChildEducationItemClick(String name, int pre_data_type, String uri) {
        Bundle bundle = new Bundle();
        bundle.putString(MyMusicService.MY_MUSIC_SERVICE_ID, "qingting");
        bundle.putString(MusicServiceSubItemFragment.SUB_ITEM_TITLE, name);
        bundle.putInt(MyMusicService.PRE_DATA_TYPE, pre_data_type);
        if (pre_data_type == 2) {
            bundle.putString("tag_name", uri);
        }
        MusicServiceSubItemFragment musicServiceSubItemFragment = new MusicServiceSubItemFragment();
        musicServiceSubItemFragment.setArguments(bundle);
        if (mActivity instanceof FragmentEntrust) {
            ((FragmentEntrust) getActivity()).pushFragment(musicServiceSubItemFragment, EntertainmentFragment.class.getName());
        }
    }

    @Override
    protected int setLayoutResourceId() {
        return R.layout.fragment_entertainment;
    }

    @Override
    protected void onVisibleToUser() {
        super.onVisibleToUser();
    }

    @Override
    public void onItemMoreClick(int position) {
        channelList = new ArrayList<Channel>();
        channelList.add(newSongList.get(position));
        moreStringList.set(0, newSongList.get(position).getName());
        mChannelDetailMoreAdapter.setMoreStringList(moreStringList);
        mDialog.show();
    }

    @Override
    public void onItemClick(View view, int position) {
        switch (position) {
            case 1:
                ChannelAddFragment channelAddFragment = new ChannelAddFragment();
                Bundle bundle = new Bundle();
                bundle.putSerializable("channelList", channelList);
                channelAddFragment.setArguments(bundle);
                if (mActivity instanceof FragmentEntrust) {
                    ((FragmentEntrust) mActivity).pushFragment(channelAddFragment, "EntertainmentFragment");
                }
                break;
            case 2:
                AudioPlayerUtil.insertChannel(mDataFactory, AudioPlayerUtil.NEXT_INSERT, mContext, channelList);
                break;
            case 3:
                AudioPlayerUtil.insertChannel(mDataFactory, AudioPlayerUtil.LAST_INSERT, mContext, channelList);
                break;
            case 4:
                break;
            default:
                break;
        }

        mDialog.dismiss();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ll_song_sheet:
                ClassificationSongFragment mClassificationSongFragment = new ClassificationSongFragment();
                if (mActivity instanceof FragmentEntrust) {
                    ((FragmentEntrust) getActivity()).pushFragment(mClassificationSongFragment, ClassificationSongFragment.class.getName());
                }
                break;
            case R.id.ll_music_rank:
                MusicRankFragment mMusicRankFragment = new MusicRankFragment();
                if (mActivity instanceof FragmentEntrust) {
                    ((FragmentEntrust) getActivity()).pushFragment(mMusicRankFragment, MusicRankFragment.class.getName());
                }
                break;
            case R.id.ll_popular_singer:
                Bundle bundle = new Bundle();
                bundle.putString(MyMusicService.MY_MUSIC_SERVICE_ID, "baidu");
                bundle.putInt(MyMusicService.PRE_DATA_TYPE, 0);
                bundle.putString("main_id", "热门歌手");
                MusicServiceSubItemFragment musicServiceSubItemFragment = new MusicServiceSubItemFragment();
                musicServiceSubItemFragment.setArguments(bundle);
                if (mActivity instanceof FragmentEntrust) {
                    ((FragmentEntrust) getActivity()).pushFragment(musicServiceSubItemFragment, EntertainmentFragment.class.getName());
                }
                break;
            case R.id.ll_fm_radio:
                FMRadioFragment mFMRadioFragment = new FMRadioFragment();
                if (mActivity instanceof FragmentEntrust) {
                    ((FragmentEntrust) getActivity()).pushFragment(mFMRadioFragment, FMRadioFragment.class.getName());
                }
                break;
            case R.id.ll_show_entertainment_voice_more:
                if ("查看全部".equals(tv_show_entertainment_voice_more.getText())) {
                    mEntertainmentVoicedMoreAdapter.setShowMore(true);
                    tv_show_entertainment_voice_more.setText("收回全部");
                    iv_show_entertainment_voice_more.setImageResource(R.drawable.entertainment_voice_more_up);
                } else if ("收回全部".equals(tv_show_entertainment_voice_more.getText())) {
                    mEntertainmentVoicedMoreAdapter.setShowMore(false);
                    tv_show_entertainment_voice_more.setText("查看全部");
                    iv_show_entertainment_voice_more.setImageResource(R.drawable.entertainment_voice_more_down);
                }
                break;
            case R.id.iv_entertainment_recommend_more:
                Bundle recommendMoreBundle = new Bundle();
                recommendMoreBundle.putString(MyMusicService.MY_MUSIC_SERVICE_ID, "entertainment");
                recommendMoreBundle.putString(MusicServiceSubItemFragment.SUB_ITEM_TITLE, "推荐歌单");
                recommendMoreBundle.putInt(MyMusicService.PRE_DATA_TYPE, 2);
                recommendMoreBundle.putString("album_id", "organized:songlist:hot");
                MusicServiceSubItemFragment musicServiceSubItemFragment1 = new MusicServiceSubItemFragment();
                musicServiceSubItemFragment1.setArguments(recommendMoreBundle);
                if (mActivity instanceof FragmentEntrust) {
                    ((FragmentEntrust) getActivity()).pushFragment(musicServiceSubItemFragment1, EntertainmentFragment.class.getName());
                }
                break;
            case R.id.iv_entertainment_netizen_more:
                Bundle netizenMoreBundle = new Bundle();
                netizenMoreBundle.putString(MyMusicService.MY_MUSIC_SERVICE_ID, "qingting");
                netizenMoreBundle.putString(MusicServiceSubItemFragment.SUB_ITEM_TITLE, "网友歌单");
                netizenMoreBundle.putString("comFrom", "NetizenSong");
                netizenMoreBundle.putInt(MyMusicService.PRE_DATA_TYPE, 2);
                netizenMoreBundle.putString("tag_name", "qingting:ondemand:categories:523:2928");
                MusicServiceSubItemFragment musicServiceSubItemFragment2 = new MusicServiceSubItemFragment();
                musicServiceSubItemFragment2.setArguments(netizenMoreBundle);
                if (mActivity instanceof FragmentEntrust) {
                    ((FragmentEntrust) getActivity()).pushFragment(musicServiceSubItemFragment2, EntertainmentFragment.class.getName());
                }
                break;
            default:
                break;
        }
    }
}
