package com.mantic.control.fragment;

import android.app.Dialog;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.baidu.iot.sdk.IoTSDKManager;
import com.mantic.control.R;
import com.mantic.control.adapter.ChannelDetailMoreAdapter;
import com.mantic.control.adapter.SearchResultSongAdapter;
import com.mantic.control.api.baidu.BaiduRetrofit;
import com.mantic.control.api.baidu.BaiduServiceApi;
import com.mantic.control.api.baidu.bean.BaiduTrackList;
import com.mantic.control.api.searchresult.bean.SearchRsTrack;
import com.mantic.control.api.searchresult.bean.SongSearchResultBean;
import com.mantic.control.api.searchresult.bean.SongSearchRsBean;
import com.mantic.control.data.Channel;
import com.mantic.control.data.DataFactory;
import com.mantic.control.decoration.AuthorItemDecoration;
import com.mantic.control.decoration.ChanneDetailMoreItemDecoration;
import com.mantic.control.manager.SearchResultManager;
import com.mantic.control.utils.AudioPlayerUtil;
import com.mantic.control.utils.Glog;
import com.mantic.control.utils.Util;
import com.mantic.control.utils.Utility;
import com.ximalaya.ting.android.opensdk.constants.DTransferConstants;
import com.ximalaya.ting.android.opensdk.datatrasfer.CommonRequest;
import com.ximalaya.ting.android.opensdk.datatrasfer.IDataCallBack;
import com.ximalaya.ting.android.opensdk.model.track.SearchTrackList;
import com.ximalaya.ting.android.opensdk.model.track.Track;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * 搜索的音乐列表
 */

public class SongsFragment extends BaseFragment implements DataFactory.OnSearchKeySetListener,
        SearchResultSongAdapter.OnItemMoreClickListener, SearchResultSongAdapter.OnTextMoreClickListener,
        ChannelDetailMoreAdapter.OnItemClickLitener, View.OnClickListener {

    private RecyclerView rcv_search_song;
    private SearchResultSongAdapter searchResultSongAdapter;

    private ChannelDetailMoreAdapter mChannelDetailMoreAdapter;
    private Dialog mDialog;
    private List<String> moreStringList;
    private ArrayList<Channel> channels = new ArrayList<>();
    private ArrayList<Channel> channelList;
    private ArrayList<SongSearchResultBean> songSearchResultBeanList;


    private String key;
    private String searchFrom;
    private boolean isFromMyChannelAddFragment = false;
    private boolean isFirstRefresh = true;
    private boolean isLoadingMopidyData = false;
    private boolean isLoadingBaiduData = false;
    private boolean hasData = false;
    private boolean isFail = false;
    private LinearLayout ll_search_loading;
    private LinearLayout ll_search_result_empty;
    private LinearLayout ll_net_work_fail;
    private ImageView iv_progress_loading;
    private AnimationDrawable netAnimation;

    @Override
    protected void initData(Bundle arguments) {
        super.initData(arguments);
        searchFrom = arguments.getString("searchFrom");
        if ("MyChannelAddFragment".equals(searchFrom)) {
            isFromMyChannelAddFragment = true;
        } else {
            isFromMyChannelAddFragment = false;
        }


        searchResultSongAdapter = new SearchResultSongAdapter(mContext, mActivity);
        searchResultSongAdapter.setOnItemMoreClickListener(this);
        searchResultSongAdapter.setOnTextMoreClickListener(this);
        rcv_search_song.setAdapter(searchResultSongAdapter);
        mDataFactory.registerSearchKeySetListener(this);
        mDialog = Utility.getDialog(mContext, R.layout.channel_detail_more_adapter);
        RecyclerView rv_channel_detail_more = (RecyclerView) mDialog.findViewById(R.id.rv_channel_detail_more);
        rv_channel_detail_more.setLayoutManager(new LinearLayoutManager(mContext));
        rv_channel_detail_more.addItemDecoration(new ChanneDetailMoreItemDecoration(mContext));
        moreStringList = new ArrayList<String>();
        moreStringList.add("");
        if (isFromMyChannelAddFragment) {
            moreStringList.add(getString(R.string.add_album_to_definition_mychannel));
        }

        moreStringList.add(getString(R.string.next_play));
        moreStringList.add(getString(R.string.last_play));
        moreStringList.add(getString(R.string.cancel));
        mChannelDetailMoreAdapter = new ChannelDetailMoreAdapter(mContext, moreStringList);
        mChannelDetailMoreAdapter.setmOnItemClickLitener(this);
        rv_channel_detail_more.setAdapter(mChannelDetailMoreAdapter);
    }


    @Override
    public void onDestroy() {
        mDataFactory.unregisterSearchKeySetListener(this);
        super.onDestroy();
    }

    @Override
    protected void initView(View view) {
        super.initView(view);
        rcv_search_song = (RecyclerView) view.findViewById(R.id.rcv_search_song);
        rcv_search_song.setLayoutManager(new LinearLayoutManager(mContext));
        rcv_search_song.addItemDecoration(new AuthorItemDecoration(mContext));
        iv_progress_loading = (ImageView) view.findViewById(R.id.iv_progress_loading);
        ll_search_loading = (LinearLayout) view.findViewById(R.id.ll_search_loading);
        ll_search_result_empty = (LinearLayout) view.findViewById(R.id.ll_search_result_empty);
        ll_net_work_fail = (LinearLayout) view.findViewById(R.id.ll_net_work_fail);
        ll_net_work_fail.setOnClickListener(this);
        netAnimation = (AnimationDrawable)iv_progress_loading.getBackground();
    }

    @Override
    protected int setLayoutResourceId() {
        return R.layout.fragment_search_song;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_net_work_fail:
                loadData();
                break;
        }
    }

    @Override
    public void setSearchKey(String key) {
        this.key = key;
        isFirstRefresh = true;
        hasData = false;
        isFail = false;
        if (isFromMyChannelAddFragment) {
            onLazyLoad();
        }

        if (TextUtils.isEmpty(key)) {
            searchResultSongAdapter.setChannelList(null);
            return;
        }
    }

    @Override
    protected void onLazyLoad() {
        super.onLazyLoad();
        loadData();
    }

    private void loadData() {
        if (isFirstRefresh) {
            searchResultSongAdapter.setChannelList(null);
            ll_search_result_empty.setVisibility(View.GONE);
            ll_net_work_fail.setVisibility(View.GONE);
            netAnimation.start();
            ll_search_loading.setVisibility(View.VISIBLE);
            if (!Util.isNetworkAvailable(mContext)) {
                ll_net_work_fail.setVisibility(View.VISIBLE);
                ll_search_result_empty.setVisibility(View.GONE);
                ll_search_loading.setVisibility(View.GONE);
                netAnimation.stop();
                return;
            }
            isLoadingBaiduData = false;
            channels.clear();
            songSearchResultBeanList = new ArrayList<SongSearchResultBean>();
            loadBaidu();
            /*Map<String, String> map = new HashMap<String, String>();
            map.put(DTransferConstants.SEARCH_KEY, key);
            map.put(DTransferConstants.PAGE, "1");
            CommonRequest.getSearchedTracks(map, new IDataCallBack<SearchTrackList>() {
                @Override
                public void onSuccess(SearchTrackList searchTrackList) {
                    isFail = false;
                    List<Track> tracks = searchTrackList.getTracks();
                    if (null != tracks && tracks.size() > 0) {
                        hasData = true;
                        SongSearchResultBean songSearchResultBean = new SongSearchResultBean();
                        songSearchResultBean.setResultSize(tracks.size());
                        songSearchResultBean.setResultType("喜马拉雅");
                        songSearchResultBeanList.add(songSearchResultBean);

                        for (int i = 0; i < tracks.size(); i++) {
                            Track track = tracks.get(i);
                            SongSearchResultBean bean = new SongSearchResultBean();
                            Channel channel = new Channel(mContext);
                            channel.setSinger(track.getAnnouncer().getNickname());
                            channel.setDuration(track.getDuration()*1000);
                            channel.setPlayUrl(track.getPlayUrl32());
                            channel.setIconUrl(track.getCoverUrlLarge());
                            channel.setName(track.getTrackTitle());
                            channel.setUri("ximalaya:"+ track.getKind() + ":" + track.getDataId());
                            channel.setMantic_album_name(track.getAlbum().getAlbumTitle());
                            channel.setMantic_album_uri("ximalaya:album:" + track.getAlbum().getAlbumId());
                            if (null != mDataFactory.getCurrChannel() && channel.getUri().equals(mDataFactory.getCurrChannel().getUri())
                                    && channel.getName().equals(mDataFactory.getCurrChannel().getName())) {
                                channel.setPlayState(mDataFactory.getCurrChannel().getPlayState());
                            } else {
                                channel.setPlayState(Channel.PLAY_STATE_STOP);
                            }
                            bean.setChannel(channel);
                            channels.add(channel);

                            if (i <= 2) {
                                songSearchResultBeanList.add(bean);
                            }
                        }

                        searchResultSongAdapter.setChannelList(songSearchResultBeanList);
                        rcv_search_song.scrollToPosition(0);
                    }
                    loadBaidu();
                }

                @Override
                public void onError(int i, String s) {
                    isFail = true;
                    hasData = false;
                    loadBaidu();
                }
            });*/
        }
    }

    private synchronized void loadBaidu() {
        if (isLoadingBaiduData) {
            return;
        }

        isLoadingBaiduData = true;

        isLoadingMopidyData = false;

        String nonce = Util.randomString(16);
        String access_token = IoTSDKManager.getInstance().getAccessToken().getAccessToken();
        String securityAppKey = Util.getSecurityAppKey(nonce, access_token);

        Map<String, String> headersMap = new LinkedHashMap<>();
        headersMap.put("X-IOT-APP", "d3S3SbItdlYDj4KaOB1qIfuM");
        headersMap.put("X-IOT-Signature", nonce + ":" + securityAppKey);
        headersMap.put("X-IOT-Token", access_token);
        Map<String, String> paramsMap = new LinkedHashMap<>();
        paramsMap.put("singer", key);
        paramsMap.put("page",  "1");
        paramsMap.put("page_size", "20");
        Call<BaiduTrackList> getTrackListCall = BaiduRetrofit.getInstance().create(BaiduServiceApi.class).getTrackListBySingerName(headersMap, paramsMap);
        getTrackListCall.enqueue(new Callback<BaiduTrackList>() {
            @Override
            public void onResponse(Call<BaiduTrackList> call, Response<BaiduTrackList> response) {
                if (response.isSuccessful() && null == response.errorBody()) {
                    if (null != response.body() && null != response.body().data) {
                        Glog.i("getTrackListCall", response.body().toString());
                        BaiduTrackList.TrackItem trackItem = response.body().data;
                        if (null != trackItem.list && trackItem.list.size() > 0) {
                            hasData = true;
                            SongSearchResultBean songSearchResultBean = new SongSearchResultBean();
                            songSearchResultBean.setResultSize(trackItem.list.size());
                            songSearchResultBean.setResultType("百度云音乐");
                            songSearchResultBeanList.add(songSearchResultBean);

                            for (int i = 0; i < trackItem.list.size(); i++) {
                                BaiduTrackList.TrackItem.Track track = trackItem.list.get(i);
                                SongSearchResultBean bean = new SongSearchResultBean();
                                Channel channel = new Channel();

                                String artist_name = "";
                                if (track.singer_name != null) {
                                    for (int j = 0; j < track.singer_name.size(); j++) {
                                        String singer = track.singer_name.get(j);
                                        artist_name = singer + " ";
                                    }
                                }
                                channel.setSinger(artist_name);

                                channel.setIconUrl(track.head_image_url);
                                channel.setName(track.name);
                                channel.setUri("baidu:track:" + track.id);
                                if (null != mDataFactory.getCurrChannel() && channel.getUri().equals(mDataFactory.getCurrChannel().getUri())
                                        && channel.getName().equals(mDataFactory.getCurrChannel().getName())) {
                                    channel.setPlayState(mDataFactory.getCurrChannel().getPlayState());
                                } else {
                                    channel.setPlayState(Channel.PLAY_STATE_STOP);
                                }
                                bean.setChannel(channel);
                                channels.add(channel);

                                if (i <= 2) {
                                    songSearchResultBeanList.add(bean);
                                }
                            }

                            searchResultSongAdapter.setChannelList(songSearchResultBeanList);
                            rcv_search_song.scrollToPosition(0);
                        }
                    }
                }
                loadMopidy();
            }

            @Override
            public void onFailure(Call<BaiduTrackList> call, Throwable t) {
                isFail = true;
                hasData = false;
                loadMopidy();
            }
        });
    }


    private synchronized void loadMopidy() {
        if (isLoadingMopidyData) {
            return;
        }
        isLoadingMopidyData = true;
//        List<String> uris = null;
//        List<String> uris = new ArrayList<>();
//        uris.add("netease:");
//        uris.add("qingting:");
//        uris.add("idaddy:");
        SearchResultManager.getInstance().getSongSearchResult(new Callback<SongSearchRsBean>() {
            @Override
            public void onResponse(Call<SongSearchRsBean> call, Response<SongSearchRsBean> response) {
                if (response.isSuccessful() && response.body() != null && null == response.errorBody()) {
                    isFirstRefresh = false;
                    if (null != response.body().getResult() && null != response.body().getResult()) {
                        ll_search_result_empty.setVisibility(View.GONE);
                        for (int j = 0; j < response.body().getResult().size(); j++) {
                            List<SearchRsTrack> tracks = response.body().getResult().get(j).getTracks();
                            String categoryUri = response.body().getResult().get(j).getUri();
                            if (null != tracks && tracks.size() > 0) {
                                hasData = true;
                                ll_search_result_empty.setVisibility(View.GONE);
                                SongSearchResultBean songSearchResultBean = new SongSearchResultBean();
                                songSearchResultBean.setResultSize(tracks.size());
                                String type = categoryUri.substring(0, categoryUri.indexOf(":"));
                                switch (type) {
                                    case "qingting":
                                        songSearchResultBean.setResultType("蜻蜓FM");
                                        break;
                                    case "netease":
                                        songSearchResultBean.setResultType("网易云音乐");
                                        break;
                                    case "beva":
                                        songSearchResultBean.setResultType("贝瓦");
                                        break;
                                    case "idaddy":
                                        songSearchResultBean.setResultType("工程师爸爸");
                                        break;
                                }

                                songSearchResultBeanList.add(songSearchResultBean);
                                for (int i = 0; i < tracks.size(); i++) {
                                    SearchRsTrack searchRsTrack = tracks.get(i);
                                    Channel channel = new Channel();
                                    channel.setName(searchRsTrack.getName());
                                    channel.setDuration(searchRsTrack.getMantic_length());
                                    String singer = "";
                                    for (int z = 0; z < searchRsTrack.getMantic_artists_name().size(); z++) {
                                        if (z != searchRsTrack.getMantic_artists_name().size() - 1) {
                                            singer = singer + searchRsTrack.getMantic_artists_name().get(z).toString() + "，";
                                        } else {
                                            singer = singer + searchRsTrack.getMantic_artists_name().get(z).toString();
                                        }
                                    }
                                    channel.setSinger(singer);
                                    channel.setUri(searchRsTrack.getUri());
                                    channel.setMantic_fee(searchRsTrack.getMantic_fee());
                                    channel.setIconUrl(searchRsTrack.getMantic_image());
                                    channel.setMantic_album_name(searchRsTrack.getMantic_album_name());
                                    channel.setMantic_album_uri(searchRsTrack.getMantic_album_uri());

                                    channels.add(channel);
                                    if (i <= 2) {
                                        SongSearchResultBean bean = new SongSearchResultBean();
                                        bean.setChannel(channel);
                                        songSearchResultBeanList.add(bean);
                                    }
                                }

                            } else {
                                if (!hasData) {
                                    ll_search_result_empty.setVisibility(View.VISIBLE);
                                } else {
                                    ll_search_result_empty.setVisibility(View.GONE);
                                }
                            }
                        }
                    } else {
                        if (!hasData) {
                            ll_search_result_empty.setVisibility(View.VISIBLE);
                        } else {
                            ll_search_result_empty.setVisibility(View.GONE);
                        }
                    }

                    searchResultSongAdapter.setChannelList(songSearchResultBeanList);
                    rcv_search_song.scrollToPosition(0);

                } else {
                    if (isFail) {
                        ll_net_work_fail.setVisibility(View.VISIBLE);
                    }
                }
                ll_search_loading.setVisibility(View.GONE);
                netAnimation.stop();
            }

            @Override
            public void onFailure(Call<SongSearchRsBean> call, Throwable t) {
                isFirstRefresh = true;
                ll_search_loading.setVisibility(View.GONE);
                netAnimation.stop();
                if (isFail) {
                    ll_net_work_fail.setVisibility(View.VISIBLE);
                }
                mDataFactory.notifyOperatorResult(getString(R.string.search_fail_try_again_later), false);
            }
        }, key, 0, 20, getMusicServiceUris());
    }

    private List<String> getMusicServiceUris() {
        if (null == mDataFactory.getMyMusicServiceUriList() || mDataFactory.getMyMusicServiceUriList().size() <= 0) {
            List<String> myMusicServiceUriList = new ArrayList<>();
            myMusicServiceUriList.add("baidu:");
            myMusicServiceUriList.add("qingting:");
            myMusicServiceUriList.add("idaddy:");
            mDataFactory.setMyMusicServiceUriList(myMusicServiceUriList);
        }

        return mDataFactory.getMyMusicServiceUriList();
    }


    @Override
    public void moreTextClickListener(int position) {
        SongSearchResultBean songSearchResultBean = songSearchResultBeanList.get(position);
        SongsMoreFragment songsDetailFragment = new SongsMoreFragment();
        Bundle bundle = new Bundle();
        String resultType = songSearchResultBean.getResultType();
        String type = "";
        switch (resultType) {
            case "蜻蜓FM":
                type = "qingting";
                break;
            case "网易云音乐":
                type = "netease";
                break;
            case "贝瓦":
                type = "beva";
                break;
            case "工程师爸爸":
                type = "idaddy";
                break;
            case "喜马拉雅":
                type = "ximalaya";
                break;
            case "百度云音乐":
                type = "baidu";
                break;
        }
        ArrayList<Channel> list = new ArrayList<>();
        for (int i = 0; i < channels.size(); i++) {
            if (channels.get(i).getUri().startsWith(type)) {
                list.add(channels.get(i));
            }
        }
        
        bundle.putSerializable("songsList", list);
        bundle.putString("searchFrom", searchFrom);
        bundle.putString("searchKey", key);
        bundle.putSerializable("songType", songSearchResultBean.getResultType());
        songsDetailFragment.setArguments(bundle);
        getFragmentManager().beginTransaction().
                replace(R.id.song_layout, songsDetailFragment).addToBackStack(null).commit();
    }

    @Override
    public void onItemClick(View view, int position) {
        switch (position) {
            case 1:
                if (isFromMyChannelAddFragment) {
                    mDataFactory.notifyChannelAddChange(channelList);
                } else {
                    addToNext();
                }

                break;

            case 2:

                if (isFromMyChannelAddFragment) {
                    addToNext();
                } else {
                    addToLast();
                }
                break;

            case 3:
                if (isFromMyChannelAddFragment) {
                    addToLast();
                }
                break;

        }


        mDialog.dismiss();
    }


    private void addToNext() {
        AudioPlayerUtil.insertChannel(mDataFactory, AudioPlayerUtil.NEXT_INSERT, mContext, channelList);
        return;
    }

    private void addToLast() {
        AudioPlayerUtil.insertChannel(mDataFactory, AudioPlayerUtil.LAST_INSERT, mContext, channelList);
        return;
    }


    @Override
    public void moreClickListener(int position) {
        channelList = new ArrayList<Channel>();
        channelList.add(songSearchResultBeanList.get(position).getChannel());
        moreStringList.set(0, songSearchResultBeanList.get(position).getChannel().getName());
        if (isFromMyChannelAddFragment) {
            moreStringList.set(1, getString(R.string.add_music_to_definition_mychannel));
        }

        mChannelDetailMoreAdapter.setMoreStringList(moreStringList);
        mDialog.show();
    }
}
