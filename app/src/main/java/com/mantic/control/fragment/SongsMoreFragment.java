package com.mantic.control.fragment;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.baidu.iot.sdk.IoTSDKManager;
import com.mantic.control.R;
import com.mantic.control.adapter.ChannelDetailMoreAdapter;
import com.mantic.control.adapter.SearchResultSongDetailAdapter;
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
 * 搜索的音乐全部列表
 */

public class SongsMoreFragment extends BaseFragment implements SearchResultSongDetailAdapter.OnItemMoreClickListener,
        SearchResultSongDetailAdapter.OnTextMoreClickListener, ChannelDetailMoreAdapter.OnItemClickLitener,
        DataFactory.OnSearchKeySetListener {

    private RecyclerView rcv_search_song;
    private SearchResultSongDetailAdapter searchResultSongDetailAdapter;

    private ChannelDetailMoreAdapter mChannelDetailMoreAdapter;
    private Dialog mDialog;
    private List<String> moreStringList;
    private ArrayList<Channel> channels = new ArrayList<>();
    private ArrayList<Channel> channelList;
    private List<String> uris = new ArrayList<>();
    private String songType;

    private String searchFrom;
    //    private boolean isFromMainFragment = false;
    private boolean isFromMyChannelAddFragment = false;
    private String searchKey;
    private boolean isRefresh = false;//是否正在加载或者刷新
    private boolean isNeedLoadMore = false;

    @Override
    protected void initData(Bundle arguments) {
        super.initData(arguments);
        searchFrom = arguments.getString("searchFrom");
        searchKey = arguments.getString("searchKey");
        songType = arguments.getString("songType");
        channels = (ArrayList<Channel>) arguments.getSerializable("songsList");

        if ("MyChannelAddFragment".equals(searchFrom)) {
            isFromMyChannelAddFragment = true;
        } else {
            isFromMyChannelAddFragment = false;
        }

        switch (songType) {
            case "蜻蜓FM":
                uris.add("qingting:");
                break;
            case "网易云音乐":
                uris.add("netease:");
                break;
            case "工程师爸爸":
                uris.add("idaddy:");
                break;
        }
        searchResultSongDetailAdapter = new SearchResultSongDetailAdapter(mContext, mActivity, channels, songType);
        searchResultSongDetailAdapter.setOnItemMoreClickListener(this);
        searchResultSongDetailAdapter.setOnTextMoreClickListener(this);
        rcv_search_song.setAdapter(searchResultSongDetailAdapter);


        if (null != channels && channels.size() >= 20) {
            isNeedLoadMore = true;
        } else {
            isNeedLoadMore = false;
            if (null == channels || channels.size() < 8) {
                searchResultSongDetailAdapter.showLoadEmpty();
            } else if (channels.size() < 20) {
                searchResultSongDetailAdapter.showLoadComplete();
            }
        }

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
        mDataFactory.registerSearchKeySetListener(this);

        this.rcv_search_song.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                LinearLayoutManager lm = (LinearLayoutManager) recyclerView.getLayoutManager();
                int totalItemCount = recyclerView.getAdapter().getItemCount();
                int lastVisibleItemPosition = lm.findLastVisibleItemPosition();
                int visibleItemCount = recyclerView.getChildCount();
                if (lastVisibleItemPosition == totalItemCount - 1
                        && visibleItemCount > 0) {
                    if (!isRefresh && isNeedLoadMore) {
                        isRefresh = true;
                        loadMore();
                    }
                }
            }


        });

    }


    private void loadMore() {
        if ("喜马拉雅".equals(songType)) {
            loadXimalayaData();
            return;
        } else if ("百度云音乐".equals(songType)) {
            loadBaiduData();
            return;
        }

        loadMopidyMore();
    }


    private void loadXimalayaData() {
        Map<String, String> map = new HashMap<String, String>();
        map.put(DTransferConstants.SEARCH_KEY, searchKey);
        map.put(DTransferConstants.PAGE, (channels.size() / 20 + 1) + "");
        CommonRequest.getSearchedTracks(map, new IDataCallBack<SearchTrackList>() {
            @Override
            public void onSuccess(SearchTrackList searchTrackList) {
                isRefresh = false;
                List<Track> tracks = searchTrackList.getTracks();
                if (null == tracks || tracks.size() < 20) {
                    isNeedLoadMore = false;
                    searchResultSongDetailAdapter.showLoadComplete();
                } else {
                    isNeedLoadMore = true;
                    searchResultSongDetailAdapter.showLoadMore();
                }

                if (null != tracks && tracks.size() > 0) {
                    for (int i = 0; i < tracks.size(); i++) {
                        Track track = tracks.get(i);
                        SongSearchResultBean bean = new SongSearchResultBean();
                        Channel channel = new Channel();
                        channel.setSinger(track.getAnnouncer().getNickname());
                        channel.setDuration(track.getDuration() * 1000);
                        channel.setPlayUrl(track.getPlayUrl32());
                        channel.setIconUrl(track.getCoverUrlLarge());
                        channel.setName(track.getTrackTitle());
                        channel.setUri("ximalaya:" + track.getKind() + ":" + track.getDataId());
                        channel.setMantic_album_name(track.getAlbum().getAlbumTitle());
                        channel.setMantic_album_uri("ximalaya:album:" + track.getAlbum().getAlbumId());
                        if (mDataFactory.channelIsCurrent(channel)) {
                            channel.setPlayState(mDataFactory.getCurrChannel().getPlayState());
                        } else {
                            channel.setPlayState(Channel.PLAY_STATE_STOP);
                        }
                        bean.setChannel(channel);
                        channels.add(channel);
                    }
                    searchResultSongDetailAdapter.setChannelList(channels);
                }
            }

            @Override
            public void onError(int i, String s) {
                isRefresh = false;
            }
        });
    }


    private void loadBaiduData() {
        String nonce = Util.randomString(16);
        String access_token = IoTSDKManager.getInstance().getAccessToken().getAccessToken();
        String securityAppKey = Util.getSecurityAppKey(nonce, access_token);

        Map<String, String> headersMap = new LinkedHashMap<>();
        headersMap.put("X-IOT-APP", "d3S3SbItdlYDj4KaOB1qIfuM");
        headersMap.put("X-IOT-Signature", nonce + ":" + securityAppKey);
        headersMap.put("X-IOT-Token", access_token);
        Map<String, String> paramsMap = new LinkedHashMap<>();
        paramsMap.put("singer", searchKey);
        paramsMap.put("page", (channels.size() / 20 + 1) + "");
        paramsMap.put("page_size", "20");
        Call<BaiduTrackList> getTrackListCall = BaiduRetrofit.getInstance().create(BaiduServiceApi.class).getTrackListBySingerName(headersMap, paramsMap);
        getTrackListCall.enqueue(new Callback<BaiduTrackList>() {
            @Override
            public void onResponse(Call<BaiduTrackList> call, Response<BaiduTrackList> response) {
                isRefresh = false;
                if (response.isSuccessful() && null == response.errorBody()) {
                    if (null != response.body() && null != response.body().data) {
                        Glog.i("getTrackListCall", response.body().toString());
                        BaiduTrackList.TrackItem trackItem = response.body().data;
                        if (null == trackItem.list || trackItem.list.size() < 20) {
                            isNeedLoadMore = false;
                            searchResultSongDetailAdapter.showLoadComplete();
                        } else {
                            isNeedLoadMore = true;
                            searchResultSongDetailAdapter.showLoadMore();
                        }
                        if (null != trackItem.list && trackItem.list.size() > 0) {
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
                                if (mDataFactory.channelIsCurrent(channel)) {
                                    channel.setPlayState(mDataFactory.getCurrChannel().getPlayState());
                                } else {
                                    channel.setPlayState(Channel.PLAY_STATE_STOP);
                                }
                                bean.setChannel(channel);
                                channels.add(channel);
                            }
                            searchResultSongDetailAdapter.setChannelList(channels);
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<BaiduTrackList> call, Throwable t) {
                isRefresh = false;
            }
        });
    }


    private void loadMopidyMore() {
        SearchResultManager.getInstance().getSongSearchResult(new Callback<SongSearchRsBean>() {
            @Override
            public void onResponse(Call<SongSearchRsBean> call, Response<SongSearchRsBean> response) {
                isRefresh = false;
                if (response.isSuccessful() && null != response.body() && null == response.errorBody()) {
                    if (null != response.body().getResult() && null != response.body().getResult().get(0)) {
                        if (null == response.body().getResult().get(0).getTracks() || response.body().getResult().get(0).getTracks().size() < 20) {
                            isNeedLoadMore = false;
                            searchResultSongDetailAdapter.showLoadComplete();
                        } else {
                            isNeedLoadMore = true;
                            searchResultSongDetailAdapter.showLoadMore();
                        }

                        List<SearchRsTrack> tracks = response.body().getResult().get(0).getTracks();
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
                        }
                        searchResultSongDetailAdapter.setChannelList(channels);
                    }
                }
            }

            @Override
            public void onFailure(Call<SongSearchRsBean> call, Throwable t) {
                isRefresh = false;
            }
        }, searchKey, channels.size() / 20, 20, uris);
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
    }

    @Override
    protected int setLayoutResourceId() {
        return R.layout.fragment_search_song;
    }


    @Override
    public void moreTextClickListener() {
        getFragmentManager().popBackStack();
    }

    @Override
    public void setSearchKey(String key) {
        getFragmentManager().popBackStack();
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
        channelList.add(channels.get(position));
        moreStringList.set(0, channelList.get(0).getName());
        if (isFromMyChannelAddFragment) {
            moreStringList.set(1, getString(R.string.add_music_to_definition_mychannel));
        }

        mChannelDetailMoreAdapter.setMoreStringList(moreStringList);
        mDialog.show();
    }
}
