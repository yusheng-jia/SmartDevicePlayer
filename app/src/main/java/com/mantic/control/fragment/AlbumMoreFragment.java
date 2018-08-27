package com.mantic.control.fragment;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.mantic.control.R;
import com.mantic.control.adapter.AlbumMoreAdapter;
import com.mantic.control.api.searchresult.bean.AlbumSearchRsBean;
import com.mantic.control.api.searchresult.bean.SearchRsArtist;
import com.mantic.control.data.DataFactory;
import com.mantic.control.data.MyChannel;
import com.mantic.control.decoration.AuthorItemDecoration;
import com.mantic.control.manager.SearchResultManager;
import com.mantic.control.musicservice.XimalayaSoundData;
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
public class AlbumMoreFragment extends BaseFragment implements DataFactory.OnSearchKeySetListener,
        AlbumMoreAdapter.OnTextMoreClickListener {
    private RecyclerView rcv_search_album;
    private AlbumMoreAdapter albumMoreAdapter;
    private ArrayList<MyChannel> albumList;

    private List<String> uris = new ArrayList<>();
    private String searchKey;
    private String albumType;
    private boolean isRefresh = false;//是否正在加载或者刷新
    private boolean isNeedLoadMore = false;


    @Override
    protected void initData(Bundle arguments) {
        super.initData(arguments);
        albumList = (ArrayList<MyChannel>) arguments.getSerializable("albumList");
        if (albumList.size() >= 20) {
            isNeedLoadMore = true;
        }
        searchKey = arguments.getString("searchKey");
        albumType = arguments.getString("albumType");
        switch (albumType) {
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
        }

        albumMoreAdapter = new AlbumMoreAdapter(mContext, mActivity, albumList, albumType);
        albumMoreAdapter.setOnTextMoreClickListener(this);
        rcv_search_album.setAdapter(albumMoreAdapter);

        if (null == albumList || albumList.size() < 8) {
            albumMoreAdapter.showLoadEmpty();
        } else if (albumList.size() < 20) {
            albumMoreAdapter.showLoadComplete();
        }

        mDataFactory.registerSearchKeySetListener(this);
        this.rcv_search_album.addOnScrollListener(new RecyclerView.OnScrollListener() {
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
        if ("喜马拉雅".equals(albumType)) {
            loadXimalayaData();
            return;
        }

        SearchResultManager.getInstance().getAlbumSearchResult(new Callback<AlbumSearchRsBean>() {
            @Override
            public void onResponse(Call<AlbumSearchRsBean> call, Response<AlbumSearchRsBean> response) {
                if (response.isSuccessful() && null != response.body() && null == response.errorBody()) {
                    if (null != response.body().getResult() && null != response.body().getResult().get(0)) {
                        List<SearchRsArtist> albums = response.body().getResult().get(0).getAlbums();
                        if (null == albums || albums.size() < 20) {
                            isNeedLoadMore = false;
                            albumMoreAdapter.showLoadComplete();
                        } else {
                            isNeedLoadMore = true;
                            albumMoreAdapter.showLoadMore();
                        }

                        if (null != albums) {
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
                                albumList.add(myChannel);
                            }
                        }
                    }

                    albumMoreAdapter.setMyChannelList(albumList);
                } else {
                    mDataFactory.notifyOperatorResult("搜索失败,请稍后再试", false);
                }
                isRefresh = false;
            }

            @Override
            public void onFailure(Call<AlbumSearchRsBean> call, Throwable t) {
                isRefresh = false;
                mDataFactory.notifyOperatorResult("搜索失败,请稍后再试", false);
            }
        }, searchKey, albumList.size() / 20, 20, uris);
    }

    private void loadXimalayaData() {
        Map<String, String> map = new HashMap<String, String>();
        map.put(DTransferConstants.SEARCH_KEY, searchKey);
        map.put(DTransferConstants.PAGE, (albumList.size() / 20 + 1) + "");
        CommonRequest.getSearchedAlbums(map, new IDataCallBack<SearchAlbumList>() {
            @Override
            public void onSuccess(SearchAlbumList searchAlbumList) {
                List<Album> albums = searchAlbumList.getAlbums();
                isRefresh = false;
                if (null == albums || albums.size() < 20) {
                    isNeedLoadMore = false;
                    albumMoreAdapter.showLoadComplete();
                } else {
                    isNeedLoadMore = true;
                    albumMoreAdapter.showLoadMore();
                }
                if (null != albums && albums.size() > 0) {
                    for (int i = 0; i < albums.size(); i++) {
                        Album album = albums.get(i);

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
                        albumList.add(myChannel);

                    }

                    albumMoreAdapter.setMyChannelList(albumList);
                }
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
        rcv_search_album = (RecyclerView) view.findViewById(R.id.rcv_search_album);
        rcv_search_album.setLayoutManager(new LinearLayoutManager(mContext));
        rcv_search_album.addItemDecoration(new AuthorItemDecoration(mContext));
    }

    @Override
    protected int setLayoutResourceId() {
        return R.layout.fragment_search_album;
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
