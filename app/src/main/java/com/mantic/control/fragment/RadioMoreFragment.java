package com.mantic.control.fragment;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.mantic.control.R;
import com.mantic.control.adapter.AuthorAdapter;
import com.mantic.control.adapter.AuthorMoreAdapter;
import com.mantic.control.adapter.RadioMoreAdapter;
import com.mantic.control.api.searchresult.bean.AuthorSearchRsBean;
import com.mantic.control.api.searchresult.bean.RadioSearchRsBean;
import com.mantic.control.api.searchresult.bean.SearchRsArtist;
import com.mantic.control.api.searchresult.bean.SearchRsRadio;
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
public class RadioMoreFragment extends BaseFragment implements RadioMoreAdapter.OnTextMoreClickListener, DataFactory.OnSearchKeySetListener {
    private final String TAG = "AuthorFragment";
    private RecyclerView rcv_search_radio;
    private RadioMoreAdapter radioMoreAdapter;
    private ArrayList<MyChannel> myChannels;
    private List<String> uris = new ArrayList<>();

    private String searchKey;
    private String radioType;
    private boolean isRefresh = false;//是否正在加载或者刷新
    private boolean isNeedLoadMore = false;

    @Override
    protected void initData(Bundle arguments) {
        super.initData(arguments);
        myChannels = (ArrayList<MyChannel>) arguments.getSerializable("radioList");
        if (myChannels.size() >= 20) {
            isNeedLoadMore = true;
        }
        searchKey = arguments.getString("searchKey");
        radioType = arguments.getString("radioType");

        switch (radioType) {
            case "蜻蜓FM":
                uris.add("qingting:");
                break;
            case "网易云音乐":
                uris.add("netease:");
                break;
            case "喜马拉雅":
                break;
        }

        radioMoreAdapter = new RadioMoreAdapter(mContext, mActivity, myChannels, radioType);
        radioMoreAdapter.setOnTextMoreClickListener(this);
        rcv_search_radio.setAdapter(radioMoreAdapter);

        if (null == myChannels || myChannels.size() < 8) {
            radioMoreAdapter.showLoadEmpty();
        } else if (myChannels.size() < 20) {
            radioMoreAdapter.showLoadComplete();
        }

        mDataFactory.registerSearchKeySetListener(this);
        this.rcv_search_radio.addOnScrollListener(new RecyclerView.OnScrollListener() {
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
        if ("喜马拉雅".equals(radioType)) {
            loadXimalayaData();
            return;
        }

        SearchResultManager.getInstance().getRadioSearchResult(new Callback<RadioSearchRsBean>() {
            @Override
            public void onResponse(Call<RadioSearchRsBean> call, Response<RadioSearchRsBean> response) {
                if (response.isSuccessful() && null != response.body() && null == response.errorBody()) {
                    isRefresh = false;
                    if (null != response.body().getResult() && null != response.body().getResult().get(0)) {
                        List<SearchRsRadio> radios = response.body().getResult().get(0).getRadios();
                        if (null == radios || radios.size() < 20) {
                            isNeedLoadMore = false;
                            radioMoreAdapter.showLoadComplete();
                        } else {
                            isNeedLoadMore = true;
                            radioMoreAdapter.showLoadMore();
                        }

                        if (null != radios) {
                            for (int i = 0; i < radios.size(); i++) {
                                SearchRsRadio searchRsRadio = radios.get(i);
                                MyChannel myChannel = new MyChannel();
                                myChannel.setmTotalCount(searchRsRadio.getMantic_num_tracks());
                                myChannel.setUrl(searchRsRadio.getUri());
                                myChannel.setChannelCoverUrl(searchRsRadio.getMantic_image());
                                myChannel.setChannelName(searchRsRadio.getName());
                                myChannel.setChannelIntro(searchRsRadio.getMantic_describe());
                                myChannel.setPlayCount(searchRsRadio.getMantic_play_count());
                                List<String> mantic_artists_name = searchRsRadio.getMantic_artists_name();
                                if (null != mantic_artists_name && mantic_artists_name.size() > 0) {
                                    String singer = "";
                                    for (int z = 0; z < searchRsRadio.getMantic_artists_name().size(); z++) {
                                        if (z != searchRsRadio.getMantic_artists_name().size() - 1) {
                                            singer = singer + searchRsRadio.getMantic_artists_name().get(z).toString() + "，";
                                        } else {
                                            singer = singer + searchRsRadio.getMantic_artists_name().get(z).toString();
                                        }
                                    }
                                    myChannel.setSingerName(singer);
                                }

                                String uri = searchRsRadio.getUri().substring(0, searchRsRadio.getUri().indexOf(":"));
                                int pointIndex = searchRsRadio.getUri().indexOf(".");
                                String serviceId = "";
                                switch (uri) {
                                    case "netease":
                                        serviceId = "wangyi";
                                        myChannel.setAlbumId(searchRsRadio.getUri());
                                        myChannel.setMainId("");
                                        break;
                                    case "qingting":
                                        serviceId = "qingting";
                                        if (pointIndex != -1) {
                                            myChannel.setAlbumId(searchRsRadio.getUri().substring(0, pointIndex));
                                        } else {
                                            myChannel.setAlbumId(searchRsRadio.getUri());
                                        }
                                        break;
                                    case "ximalaya":
                                        serviceId = XimalayaSoundData.mAppSecret;
                                        break;
                                }
                                myChannel.setMusicServiceId(serviceId);
                                myChannel.setChannelId(searchRsRadio.getMantic_image());
                                myChannel.setChannelType(2);
                                myChannels.add(myChannel);
                            }
                        }
                    }
                    radioMoreAdapter.setMyChannelList(myChannels);
                } else {
                    isRefresh = false;
                    mDataFactory.notifyOperatorResult("搜索失败,请稍后再试", false);
                }


            }

            @Override
            public void onFailure(Call<RadioSearchRsBean> call, Throwable t) {
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
                    radioMoreAdapter.showLoadComplete();
                } else {
                    isNeedLoadMore = true;
                    radioMoreAdapter.showLoadMore();
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
                    myChannel.setMainId("广播");
                    myChannel.setMusicServiceId(XimalayaSoundData.mAppSecret);
                    myChannel.setChannelId(announcer.getAvatarUrl());
                    myChannel.setChannelType(2);
                    myChannels.add(myChannel);
                }
                radioMoreAdapter.setMyChannelList(myChannels);
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
        rcv_search_radio = (RecyclerView) view.findViewById(R.id.rcv_search_radio);
        rcv_search_radio.setLayoutManager(new LinearLayoutManager(mContext));
        rcv_search_radio.addItemDecoration(new AuthorItemDecoration(mContext));
    }

    @Override
    protected int setLayoutResourceId() {
        return R.layout.fragment_search_radio;
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
