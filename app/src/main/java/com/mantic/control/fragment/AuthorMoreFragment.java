package com.mantic.control.fragment;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.mantic.control.R;
import com.mantic.control.adapter.AuthorAdapter;
import com.mantic.control.adapter.AuthorMoreAdapter;
import com.mantic.control.api.searchresult.bean.AuthorSearchResultBean;
import com.mantic.control.api.searchresult.bean.AuthorSearchRsBean;
import com.mantic.control.api.searchresult.bean.SearchRsArtist;
import com.mantic.control.data.DataFactory;
import com.mantic.control.data.MyChannel;
import com.mantic.control.decoration.AuthorItemDecoration;
import com.mantic.control.manager.SearchResultManager;
import com.mantic.control.musicservice.XimalayaSoundData;
import com.mantic.control.utils.Glog;
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
public class AuthorMoreFragment extends BaseFragment implements AuthorMoreAdapter.OnTextMoreClickListener, DataFactory.OnSearchKeySetListener {
    private final String TAG = "AuthorFragment";
    private RecyclerView rcv_search_author;
    private AuthorMoreAdapter authorAdapter;
    private ArrayList<MyChannel> myChannels;
    private List<String> uris = new ArrayList<>();

    private String searchKey;
    private String authorType;
    private boolean isRefresh = false;//是否正在加载或者刷新
    private boolean isNeedLoadMore = false;

    @Override
    protected void initData(Bundle arguments) {
        super.initData(arguments);
        myChannels = (ArrayList<MyChannel>) arguments.getSerializable("authorList");
        if (myChannels.size() >= 20) {
            isNeedLoadMore = true;
        }
        searchKey = arguments.getString("searchKey");
        authorType = arguments.getString("authorType");

        switch (authorType) {
            case "蜻蜓FM":
                uris.add("qingting:");
                break;
            case "网易云音乐":
                uris.add("netease:");
                break;
            case "贝瓦":
                uris.add("beva:");
                break;
            case "工程师爸爸":
                uris.add("idaddy:");
                break;
            case "喜马拉雅":
                break;
        }

        authorAdapter = new AuthorMoreAdapter(mContext, mActivity, myChannels, authorType);
        authorAdapter.setOnTextMoreClickListener(this);
        rcv_search_author.setAdapter(authorAdapter);

        if (null == myChannels || myChannels.size() < 8) {
            authorAdapter.showLoadEmpty();
        } else if (myChannels.size() < 20) {
            authorAdapter.showLoadComplete();
        }

        mDataFactory.registerSearchKeySetListener(this);
        this.rcv_search_author.addOnScrollListener(new RecyclerView.OnScrollListener() {
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
        if ("喜马拉雅".equals(authorType)) {
            loadXimalayaData();
            return;
        }

        SearchResultManager.getInstance().getAuthorSearchResult(new Callback<AuthorSearchRsBean>() {
            @Override
            public void onResponse(Call<AuthorSearchRsBean> call, Response<AuthorSearchRsBean> response) {
                if (response.isSuccessful() && null != response.body() && null == response.errorBody()) {
                    isRefresh = false;
                    if (null != response.body().getResult() && null != response.body().getResult().get(0)) {
                        List<SearchRsArtist> artists = response.body().getResult().get(0).getArtists();
                        if (null == artists || artists.size() < 20) {
                            isNeedLoadMore = false;
                            authorAdapter.showLoadComplete();
                        } else {
                            isNeedLoadMore = true;
                            authorAdapter.showLoadMore();
                        }

                        if (null != artists) {
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
                                myChannels.add(myChannel);
                            }
                        }
                    }
                    authorAdapter.setMyChannelList(myChannels);
                } else {
                    isRefresh = false;
                    mDataFactory.notifyOperatorResult("搜索失败,请稍后再试", false);
                }


            }

            @Override
            public void onFailure(Call<AuthorSearchRsBean> call, Throwable t) {
                isRefresh = false;
                Glog.i(TAG, "onFailure: " + t.getMessage());
                mDataFactory.notifyOperatorResult("搜索失败,请稍后再试", false);
            }
        }, searchKey, myChannels.size() / 20, 20, uris);
    }

    private void loadXimalayaData() {
        Map<String, String> map = new HashMap<String, String>();
        map.put(DTransferConstants.SEARCH_KEY, searchKey);
        map.put(DTransferConstants.PAGE, (myChannels.size() / 20 + 1) + "");
        map.put(DTransferConstants.PAGE_SIZE, "20");

        CommonRequest.getSearchAnnouncers(map, new IDataCallBack<AnnouncerList>() {
            @Override
            public void onSuccess(AnnouncerList announcerList) {
                isRefresh = false;
                List<Announcer> authorList = announcerList.getAnnouncerList();
                if (null == authorList || authorList.size() < 20) {
                    isNeedLoadMore = false;
                    authorAdapter.showLoadComplete();
                } else {
                    isNeedLoadMore = true;
                    authorAdapter.showLoadMore();
                }
                for (int i = 0; i < authorList.size(); i++) {
                    Announcer announcer = authorList.get(i);
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
                    myChannels.add(myChannel);
                }
                authorAdapter.setMyChannelList(myChannels);
            }

            @Override
            public void onError(int i, String s) {

            }
        });
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
    }

    @Override
    protected int setLayoutResourceId() {
        return R.layout.fragment_search_author;
    }


    @Override
    public void moreTextClickListener() {
        getFragmentManager().popBackStack();
    }

    @Override
    public void setSearchKey(String key) {
        getFragmentManager().popBackStack();
    }
}
