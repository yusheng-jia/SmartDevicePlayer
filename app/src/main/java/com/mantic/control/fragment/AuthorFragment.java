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
import com.mantic.control.adapter.AuthorAdapter;
import com.mantic.control.api.searchresult.bean.AuthorSearchResult;
import com.mantic.control.api.searchresult.bean.AuthorSearchResultBean;
import com.mantic.control.api.searchresult.bean.AuthorSearchRsBean;
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
import com.ximalaya.ting.android.opensdk.model.album.Announcer;
import com.ximalaya.ting.android.opensdk.model.announcer.AnnouncerList;

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
public class AuthorFragment extends BaseFragment implements DataFactory.OnSearchKeySetListener,
        AuthorAdapter.OnTextMoreClickListener, View.OnClickListener {
    private final String TAG = "AuthorFragment";
    private RecyclerView rcv_search_author;
    private AuthorAdapter authorAdapter;
    private ArrayList<MyChannel> authorMyChannelList;
    private ArrayList<AuthorSearchResultBean> authorSearchResultBeanList;
    private LinearLayout ll_search_loading;
    private LinearLayout ll_search_result_empty;
    private LinearLayout ll_net_work_fail;
    private ImageView iv_progress_loading;
    private AnimationDrawable netAnimation;

    private boolean hasData = false;
    private boolean isFail = false;
    private boolean isLoadingMopidyData = false;

    private String key;

    @Override
    protected void initData(Bundle arguments) {
        super.initData(arguments);
        authorAdapter = new AuthorAdapter(mContext, mActivity);
        authorAdapter.setOnTextMoreClickListener(this);
        rcv_search_author.setAdapter(authorAdapter);
        mDataFactory.registerSearchKeySetListener(this);
    }


    @Override
    public void onDestroy() {
        mDataFactory.unregisterSearchKeySetListener(this);
        super.onDestroy();
    }

    @Override
    protected void initView(View view) {
        super.initView(view);
        rcv_search_author = (RecyclerView) view.findViewById(R.id.rcv_search_author);
        rcv_search_author.setLayoutManager(new LinearLayoutManager(mContext));
        rcv_search_author.addItemDecoration(new AuthorItemDecoration(mContext));
        iv_progress_loading = (ImageView) view.findViewById(R.id.iv_progress_loading);
        ll_search_loading = (LinearLayout) view.findViewById(R.id.ll_search_loading);
        ll_search_result_empty = (LinearLayout) view.findViewById(R.id.ll_search_result_empty);
        ll_net_work_fail = (LinearLayout) view.findViewById(R.id.ll_net_work_fail);
        ll_net_work_fail.setOnClickListener(this);
        netAnimation = (AnimationDrawable)iv_progress_loading.getBackground();
    }

    @Override
    protected int setLayoutResourceId() {
        return R.layout.fragment_search_author;
    }

    @Override
    protected void onLazyLoad() {
        super.onLazyLoad();
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
        AuthorMoreFragment authorMoreFragment = new AuthorMoreFragment();
        Bundle bundle = new Bundle();
        AuthorSearchResultBean authorSearchResultBean = authorSearchResultBeanList.get(position);
        List<SearchRsArtist> artists = authorSearchResultBean.getArtists();
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
                    break;
                case "ximalaya":
                    serviceId = XimalayaSoundData.mAppSecret;
                    break;
                case "beva":
                    serviceId = "beiwa";
                    break;
                case "idaddy":
                    serviceId = "idaddy";
                    break;

            }
            myChannel.setMusicServiceId(serviceId);
            myChannel.setChannelId(artist.getMantic_image());
            myChannel.setChannelType(3);
            myChannelList.add(myChannel);
        }
        bundle.putSerializable("authorList", myChannelList);
        bundle.putSerializable("authorType", authorSearchResultBean.getResultType());
        bundle.putSerializable("searchKey", key);
        authorMoreFragment.setArguments(bundle);
        getFragmentManager().beginTransaction().
                replace(R.id.author_layout, authorMoreFragment).addToBackStack(null).commit();
    }

    @Override
    public void setSearchKey(String key) {
        authorAdapter.setMyChannelList(null);
        hasData = false;
        isFail = false;
        this.key = key;
        if (TextUtils.isEmpty(key)) {
            authorAdapter.setMyChannelList(null);
            return;
        }

        loadData();
    }

    private void loadData() {
        ll_net_work_fail.setVisibility(View.GONE);
        ll_search_result_empty.setVisibility(View.GONE);
        ll_search_loading.setVisibility(View.VISIBLE);
        netAnimation.start();
        if (!Util.isNetworkAvailable(mContext)) {
            ll_net_work_fail.setVisibility(View.VISIBLE);
            ll_search_result_empty.setVisibility(View.GONE);
            ll_search_loading.setVisibility(View.GONE);
            netAnimation.stop();
            return;
        }

        authorMyChannelList = new ArrayList<MyChannel>();
        authorSearchResultBeanList = new ArrayList<AuthorSearchResultBean>();
        isLoadingMopidyData = false;
        loadMopidyData();
        /*Map<String, String> map = new HashMap<String, String>();
        map.put(DTransferConstants.SEARCH_KEY, key);
        map.put(DTransferConstants.PAGE, "1");
        map.put(DTransferConstants.PAGE_SIZE, "20");

        CommonRequest.getSearchAnnouncers(map, new IDataCallBack<AnnouncerList>() {
            @Override
            public void onSuccess(AnnouncerList announcerList) {
                isFail = false;
                List<Announcer> authorList = announcerList.getAnnouncerList();
                if (null != authorList && authorList.size() > 0) {
                    hasData = true;
                    AuthorSearchResultBean bean = new AuthorSearchResultBean();
                    bean.setResultSize(authorList.size());
                    bean.setResultType("喜马拉雅");
                    authorSearchResultBeanList.add(bean);
                    List<SearchRsArtist> artists = new ArrayList<SearchRsArtist>();
                    for (int i = 0; i < authorList.size(); i++) {
                        Announcer announcer = authorList.get(i);
                        SearchRsArtist artist = new SearchRsArtist();
                        artist.set__model__("Ref");
                        List<String> mantic_artists_name = new ArrayList<String>();
                        mantic_artists_name.add(announcer.getAnnouncerPosition());
                        if (null != mantic_artists_name && mantic_artists_name.size() > 0) {
                            artist.setMantic_artists_name(mantic_artists_name);
                        }
                        artist.setMantic_describe(announcer.getVdesc());
                        artist.setMantic_image(announcer.getAvatarUrl());
                        artist.setMantic_num_tracks((int)announcer.getReleasedTrackCount());

                        artist.setName(announcer.getNickname());
                        artist.setType("3");
                        artist.setUri(announcer.getAnnouncerId() + "");
                        artists.add(artist);


                        if (i <= 2) {
                            MyChannel myChannel = new MyChannel();
                            myChannel.setmTotalCount(announcer.getReleasedTrackCount());
                            myChannel.setUrl(announcer.getAnnouncerId() + "");
                            myChannel.setChannelCoverUrl(announcer.getAvatarUrl());
                            myChannel.setChannelName(announcer.getNickname());
                            myChannel.setChannelIntro(announcer.getVdesc());
                            myChannel.setSingerName(announcer.getAnnouncerPosition());
                            myChannel.setMainId("主播");
                            myChannel.setMusicServiceId(XimalayaSoundData.mAppSecret);
                            myChannel.setChannelId(announcer.getAvatarUrl());
                            myChannel.setChannelType(3);
                            authorMyChannelList.add(myChannel);
                            AuthorSearchResultBean bean1 = new AuthorSearchResultBean();
                            bean1.setMyChannel(myChannel);
                            authorSearchResultBeanList.add(bean1);
                        }
                    }

                    authorSearchResultBeanList.get(0).setArtists(artists);
                    authorAdapter.setMyChannelList(authorSearchResultBeanList);
                    rcv_search_author.scrollToPosition(0);
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


    private void loadMopidyData() {

        if (isLoadingMopidyData) {
            return;
        }
        isLoadingMopidyData = true;
//        List<String> uris = null;
//        List<String> uris = new ArrayList<>();
//        uris.add("netease:");
//        uris.add("qingting:");
//        uris.add("idaddy:");

        SearchResultManager.getInstance().getAuthorSearchResult(new Callback<AuthorSearchRsBean>() {
            @Override
            public void onResponse(Call<AuthorSearchRsBean> call, Response<AuthorSearchRsBean> response) {

                if (response.isSuccessful() && null != response.body() && null == response.errorBody()) {
                    if (null != response.body().getResult()) {
                        List<AuthorSearchResult> authorSearchResults = response.body().getResult();
                        if (null != authorSearchResults) {
                            for (int j = 0; j < authorSearchResults.size(); j++) {
                                List<SearchRsArtist> artists = response.body().getResult().get(j).getArtists();
                                String categoryUri = response.body().getResult().get(j).getUri();
                                if (null != artists && artists.size() > 0) {
                                    hasData = true;
                                    ll_search_result_empty.setVisibility(View.GONE);
                                    AuthorSearchResultBean bean = new AuthorSearchResultBean();
                                    bean.setResultSize(artists.size());
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
                                    bean.setArtists(artists);
                                    authorSearchResultBeanList.add(bean);

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
                                        authorMyChannelList.add(myChannel);

                                        if (i <= 2) {
                                            AuthorSearchResultBean bean1 = new AuthorSearchResultBean();
                                            bean1.setMyChannel(myChannel);
                                            authorSearchResultBeanList.add(bean1);
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

                    authorAdapter.setMyChannelList(authorSearchResultBeanList);
                    rcv_search_author.scrollToPosition(0);
                } else {
                    mDataFactory.notifyOperatorResult(getString(R.string.search_fail_try_again_later), false);
                    if (isFail) {
                        ll_net_work_fail.setVisibility(View.VISIBLE);
                    }
                }
                ll_search_loading.setVisibility(View.GONE);
                netAnimation.stop();
            }

            @Override
            public void onFailure(Call<AuthorSearchRsBean> call, Throwable t) {
                ll_search_loading.setVisibility(View.GONE);
                netAnimation.stop();
                if (isFail) {
                    ll_net_work_fail.setVisibility(View.VISIBLE);
                }
                if(mActivity instanceof FragmentEntrust){
                    mDataFactory.notifyOperatorResult(getString(R.string.search_fail_try_again_later), false);
                }
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
}
