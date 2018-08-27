package com.mantic.control.fragment;

import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.mantic.control.R;
import com.mantic.control.adapter.AlbumAdapter;
import com.mantic.control.adapter.AuthorAdapter;
import com.mantic.control.api.searchresult.bean.AlbumSearchResult;
import com.mantic.control.api.searchresult.bean.AlbumSearchResultBean;
import com.mantic.control.api.searchresult.bean.AlbumSearchRsBean;
import com.mantic.control.api.searchresult.bean.SearchRsArtist;
import com.mantic.control.data.DataFactory;
import com.mantic.control.data.MyChannel;
import com.mantic.control.decoration.AuthorItemDecoration;
import com.mantic.control.manager.SearchResultManager;
import com.mantic.control.musicservice.XimalayaSoundData;
import com.mantic.control.utils.Util;
import com.ximalaya.ting.android.opensdk.constants.DTransferConstants;
import com.ximalaya.ting.android.opensdk.datatrasfer.CommonRequest;
import com.ximalaya.ting.android.opensdk.datatrasfer.IDataCallBack;
import com.ximalaya.ting.android.opensdk.model.album.Album;
import com.ximalaya.ting.android.opensdk.model.album.SearchAlbumList;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by wujiangxia on 2017/5/9.
 */
public class AlbumFragment extends BaseFragment implements DataFactory.OnSearchKeySetListener,
        AlbumAdapter.OnTextMoreClickListener, View.OnClickListener {
    private RecyclerView rcv_search_album;
    private AlbumAdapter albumAdapter;
    private String key;
    private ArrayList<MyChannel> albumMyChannelList;
    private ArrayList<AlbumSearchResultBean> albumSearchResultBeanList;


    private LinearLayout ll_search_loading;
    private LinearLayout ll_search_result_empty;
    private LinearLayout ll_net_work_fail;
    private ImageView iv_progress_loading;
    private AnimationDrawable netAnimation;

    private boolean isFirstRefresh = true;
    private boolean hasData = false;
    private boolean isFail = false;
    private boolean isLoadingMopidyData = false;

    @Override
    protected void initData(Bundle arguments) {
        super.initData(arguments);
        albumAdapter = new AlbumAdapter(mContext, mActivity);
        albumAdapter.setOnTextMoreClickListener(this);
        rcv_search_album.setAdapter(albumAdapter);
        mDataFactory.registerSearchKeySetListener(this);
    }

    @Override
    protected void onLazyLoad() {
        super.onLazyLoad();
        loadData();
    }

    public void loadData() {
        if (isFirstRefresh) {
            isLoadingMopidyData = false;
            albumAdapter.setMyChannelList(null);
            ll_search_result_empty.setVisibility(View.GONE);
            ll_net_work_fail.setVisibility(View.GONE);
            ll_search_loading.setVisibility(View.VISIBLE);
            netAnimation.start();
            if (!Util.isNetworkAvailable(mContext)) {
                ll_net_work_fail.setVisibility(View.VISIBLE);
                ll_search_result_empty.setVisibility(View.GONE);
                ll_search_loading.setVisibility(View.GONE);
                netAnimation.stop();
                return;
            }

            albumMyChannelList = new ArrayList<MyChannel>();
            albumSearchResultBeanList = new ArrayList<AlbumSearchResultBean>();
            loadMopidyData();
            /*Map<String, String> map = new HashMap<String, String>();
            map.put(DTransferConstants.SEARCH_KEY, key);
            map.put(DTransferConstants.PAGE, "1");
            CommonRequest.getSearchedAlbums(map, new IDataCallBack<SearchAlbumList>() {
                @Override
                public void onSuccess(SearchAlbumList searchAlbumList) {
                    isFail = false;
                    List<Album> albums = searchAlbumList.getAlbums();
                    if (null != albums && albums.size() > 0) {
                        hasData = true;
                        AlbumSearchResultBean bean = new AlbumSearchResultBean();
                        bean.setResultSize(albums.size());
                        bean.setResultType("喜马拉雅");
                        albumSearchResultBeanList.add(bean);
                        List<SearchRsArtist> artists = new ArrayList<SearchRsArtist>();
                        for (int i = 0; i < albums.size(); i++) {
                            Album album = albums.get(i);
                            SearchRsArtist artist = new SearchRsArtist();
                            artist.set__model__("Ref");
                            List<String> mantic_artists_name = new ArrayList<String>();
                            mantic_artists_name.add(album.getAnnouncer().getNickname());
                            if (null != mantic_artists_name && mantic_artists_name.size() > 0) {
                                artist.setMantic_artists_name(mantic_artists_name);
                            }
                            artist.setMantic_describe(album.getAlbumIntro());
                            artist.setMantic_image(album.getCoverUrlLarge());
                            artist.setMantic_num_tracks((int) album.getIncludeTrackCount());

                            artist.setName(album.getAlbumTitle());
                            artist.setType("3");
                            artist.setUri(album.getId() + "");
                            artists.add(artist);


                            if (i <= 2) {
                                MyChannel myChannel = new MyChannel();
                                myChannel.setmTotalCount((int) album.getIncludeTrackCount());
                                myChannel.setUrl(album.getId() + "");
                                myChannel.setChannelCoverUrl(album.getCoverUrlLarge());
                                myChannel.setChannelName(album.getAlbumTitle());
                                myChannel.setChannelIntro(album.getAlbumIntro());
                                myChannel.setSingerName(album.getAnnouncer().getNickname());
                                myChannel.setMainId("分类");
                                myChannel.setAlbumId(album.getId() + "");
                                myChannel.setMusicServiceId(XimalayaSoundData.mAppSecret);
                                myChannel.setChannelId(album.getCoverUrlLarge());
                                myChannel.setChannelType(3);
                                albumMyChannelList.add(myChannel);
                                AlbumSearchResultBean bean1 = new AlbumSearchResultBean();
                                bean1.setMyChannel(myChannel);
                                albumSearchResultBeanList.add(bean1);
                            }
                        }

                        albumSearchResultBeanList.get(0).setArtists(artists);
                        albumAdapter.setMyChannelList(albumSearchResultBeanList);
                        rcv_search_album.scrollToPosition(0);
                    }

                    loadMopidyData();
                }

                @Override
                public void onError(int i, String s) {
                    isFail = true;
                    hasData = false;
                    loadMopidyData();
                }
            });*/
        }
    }


    private void loadMopidyData() {
        if (isLoadingMopidyData) {
            return;
        }
        isLoadingMopidyData = true;
//        List<String> uris = null;
        List<String> uris = new ArrayList<>();
//        uris.add("netease:");
//        uris.add("qingting:");
//        uris.add("idaddy:");
        SearchResultManager.getInstance().getAlbumSearchResult(new Callback<AlbumSearchRsBean>() {
            @Override
            public void onResponse(Call<AlbumSearchRsBean> call, Response<AlbumSearchRsBean> response) {

                if (response.isSuccessful() && null != response.body() && null == response.errorBody()) {
                    isFirstRefresh = false;
                    if (null != response.body().getResult()) {
                        ll_search_result_empty.setVisibility(View.GONE);
                        List<AlbumSearchResult> albumSearchResults = response.body().getResult();
                        if (null != albumSearchResults) {
                            for (int j = 0; j < albumSearchResults.size(); j++) {
                                List<SearchRsArtist> albums = albumSearchResults.get(j).getAlbums();
                                String categoryUri = albumSearchResults.get(j).getUri();
                                if (null != albums && albums.size() > 0) {
                                    hasData = true;
                                    ll_search_result_empty.setVisibility(View.GONE);
                                    AlbumSearchResultBean bean = new AlbumSearchResultBean();
                                    bean.setResultSize(albums.size());
                                    String type = categoryUri.substring(0, categoryUri.indexOf(":"));
                                    switch (type) {
                                        case "qingting":
                                            bean.setResultType("蜻蜓FM");
                                            break;
                                        case "netease":
                                            bean.setResultType("网易云音乐");
                                            break;
                                        case "beva":
                                            bean.setResultType("贝瓦");
                                            break;
                                        case "idaddy":
                                            bean.setResultType("工程师爸爸");
                                            break;
                                    }
                                    bean.setArtists(albums);
                                    albumSearchResultBeanList.add(bean);


                                    for (int i = 0; i < albums.size(); i++) {
                                        SearchRsArtist artist = albums.get(i);
                                        MyChannel myChannel = new MyChannel();
                                        myChannel.setmTotalCount(artist.getMantic_num_tracks());
                                        myChannel.setUrl(artist.getUri());
                                        myChannel.setChannelCoverUrl(artist.getMantic_image());
                                        myChannel.setChannelName(artist.getName());
                                        myChannel.setChannelIntro(artist.getMantic_describe());
                                        List<String> mantic_artists_name = artist.getMantic_artists_name();
                                        if (null != mantic_artists_name && mantic_artists_name.size() > 0) {
                                            String singer = "";
                                            for (int z = 0; z < artist.getMantic_artists_name().size(); z++) {
                                                if (z != artist.getMantic_artists_name().size() - 1) {
                                                    singer = singer + artist.getMantic_artists_name().get(z).toString() + "，";
                                                } else {
                                                    singer = singer + artist.getMantic_artists_name().get(z).toString();
                                                }
                                            }
                                            myChannel.setSingerName(singer);

                                        }

                                        String uri = artist.getUri().substring(0, artist.getUri().indexOf(":"));
                                        String serviceId = "";
                                        switch (uri) {
                                            case "netease":
                                                serviceId = "wangyi";
                                                myChannel.setAlbumId(artist.getUri());
                                                myChannel.setMainId("");
                                                break;
                                            case "qingting":
                                                serviceId = "qingting";
                                                myChannel.setAlbumId(artist.getUri());
                                                myChannel.setMainId("");
                                                break;
                                            case "beva":
                                                serviceId = "beiwa";
                                                myChannel.setAlbumId(artist.getUri());
                                                myChannel.setMainId("");
                                                break;
                                            case "idaddy":
                                                serviceId = "idaddy";
                                                myChannel.setAlbumId(artist.getUri());
                                                myChannel.setMainId("");
                                                break;
                                        }
                                        myChannel.setMusicServiceId(serviceId);
                                        myChannel.setChannelId(artist.getMantic_image());
                                        myChannel.setChannelType(3);
                                        albumMyChannelList.add(myChannel);

                                        if (i <= 2) {
                                            AlbumSearchResultBean bean1 = new AlbumSearchResultBean();
                                            bean1.setMyChannel(myChannel);
                                            albumSearchResultBeanList.add(bean1);
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
                        }
                    } else {
                        if (!hasData) {
                            ll_search_result_empty.setVisibility(View.VISIBLE);
                        } else {
                            ll_search_result_empty.setVisibility(View.GONE);
                        }
                    }
                    albumAdapter.setMyChannelList(albumSearchResultBeanList);
                    rcv_search_album.scrollToPosition(0);
                } else {
                    isFirstRefresh = true;
                    if (isFail) {
                        ll_net_work_fail.setVisibility(View.VISIBLE);
                    }
                    mDataFactory.notifyOperatorResult(getString(R.string.search_fail_try_again_later), false);
                }
                ll_search_loading.setVisibility(View.GONE);
                netAnimation.stop();
            }

            @Override
            public void onFailure(Call<AlbumSearchRsBean> call, Throwable t) {
                ll_search_loading.setVisibility(View.GONE);
                netAnimation.stop();
                isFirstRefresh = true;
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
    public void onDestroy() {
        mDataFactory.unregisterSearchKeySetListener(this);
        super.onDestroy();
    }

    @Override
    protected void initView(View view) {
        super.initView(view);
        rcv_search_album = (RecyclerView) view.findViewById(R.id.rcv_search_album);
        rcv_search_album.setLayoutManager(new LinearLayoutManager(mContext));
        rcv_search_album.addItemDecoration(new AuthorItemDecoration(mContext));
        ll_search_loading = (LinearLayout) view.findViewById(R.id.ll_search_loading);
        ll_search_result_empty = (LinearLayout) view.findViewById(R.id.ll_search_result_empty);
        ll_net_work_fail = (LinearLayout) view.findViewById(R.id.ll_net_work_fail);
        ll_net_work_fail.setOnClickListener(this);
        iv_progress_loading = (ImageView) view.findViewById(R.id.iv_progress_loading);
        netAnimation = (AnimationDrawable)iv_progress_loading.getBackground();
    }

    @Override
    protected int setLayoutResourceId() {
        return R.layout.fragment_search_album;
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
    public void moreTextClickListener(int position) {
        AlbumMoreFragment albumMoreFragment = new AlbumMoreFragment();
        Bundle bundle = new Bundle();
        AlbumSearchResultBean albumSearchResultBean = albumSearchResultBeanList.get(position);
        List<SearchRsArtist> artists = albumSearchResultBean.getArtists();
        ArrayList<MyChannel> myChannelList = new ArrayList<MyChannel>();
        for (int i = 0; i < artists.size(); i++) {
            SearchRsArtist artist = artists.get(i);
            MyChannel myChannel = new MyChannel();
            myChannel.setmTotalCount(artist.getMantic_num_tracks());
            myChannel.setUrl(artist.getUri());
            myChannel.setChannelCoverUrl(artist.getMantic_image());
            myChannel.setChannelName(artist.getName());
            myChannel.setChannelIntro(artist.getMantic_describe());
            List<String> mantic_artists_name = artist.getMantic_artists_name();
            if (null != mantic_artists_name && mantic_artists_name.size() > 0) {
                String singer = "";
                for (int z = 0; z < artist.getMantic_artists_name().size(); z++) {
                    if (z != artist.getMantic_artists_name().size() - 1) {
                        singer = singer + artist.getMantic_artists_name().get(z).toString() + "，";
                    } else {
                        singer = singer + artist.getMantic_artists_name().get(z).toString();
                    }
                }
                myChannel.setSingerName(singer);
            }

            int index = artist.getUri().indexOf(":");
            String uri = "ximalaya";
            if (index != -1) {
                uri = artist.getUri().substring(0, artist.getUri().indexOf(":"));
            }
            String serviceId = "";
            switch (uri) {
                case "netease":
                    serviceId = "wangyi";
                    myChannel.setAlbumId(artist.getUri());
                    myChannel.setMainId("");
                    break;
                case "qingting":
                    serviceId = "qingting";
                    myChannel.setAlbumId(artist.getUri());
                    break;
                case "beva":
                    serviceId = "beiwa";
                    myChannel.setAlbumId(artist.getUri());
                    break;
                case "idaddy":
                    serviceId = "idaddy";
                    myChannel.setAlbumId(artist.getUri());
                    break;
                case "ximalaya":
                    serviceId = XimalayaSoundData.mAppSecret;
                    myChannel.setMainId("分类");
                    myChannel.setAlbumId(artist.getUri());
                    break;
            }
            myChannel.setMusicServiceId(serviceId);
            myChannel.setChannelId(artist.getMantic_image());
            myChannel.setChannelType(3);
            myChannelList.add(myChannel);
        }

        bundle.putSerializable("albumList", myChannelList);
        bundle.putSerializable("albumType", albumSearchResultBean.getResultType());
        bundle.putString("searchKey", key);
        albumMoreFragment.setArguments(bundle);
        getFragmentManager().beginTransaction().
                replace(R.id.album_layout, albumMoreFragment).addToBackStack(null).commit();
    }

    @Override
    public void setSearchKey(String key) {
        this.key = key;
        isFirstRefresh = true;
        hasData = false;
        isFail = false;
        if (TextUtils.isEmpty(key)) {
            albumAdapter.setMyChannelList(null);
            return;
        }
    }

}
