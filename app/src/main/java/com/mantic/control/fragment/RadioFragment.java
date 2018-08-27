package com.mantic.control.fragment;

import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.mantic.control.R;
import com.mantic.control.adapter.RadioAdapter;
import com.mantic.control.api.searchresult.bean.RadioSearchResult;
import com.mantic.control.api.searchresult.bean.RadioSearchResultBean;
import com.mantic.control.api.searchresult.bean.RadioSearchRsBean;
import com.mantic.control.api.searchresult.bean.SearchRsRadio;
import com.mantic.control.data.DataFactory;
import com.mantic.control.data.MyChannel;
import com.mantic.control.decoration.AuthorItemDecoration;
import com.mantic.control.manager.SearchResultManager;
import com.mantic.control.musicservice.XimalayaSoundData;
import com.mantic.control.utils.Util;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * 广播搜索结果
 */
public class RadioFragment extends BaseFragment implements View.OnClickListener,
        DataFactory.OnSearchKeySetListener, RadioAdapter.OnTextMoreClickListener {
    private RecyclerView rcv_search_radio;
    private LinearLayout ll_search_loading;
    private LinearLayout ll_search_result_empty;
    private LinearLayout ll_net_work_fail;
    private ImageView iv_progress_loading;
    private AnimationDrawable netAnimation;

    private RadioAdapter radioAdapter;

    private boolean isFirstRefresh = true;
    private boolean hasData = false;

    private String key;
    private ArrayList<MyChannel> radioMyChannelList;
    private ArrayList<RadioSearchResultBean> radioSearchResultBeanList;

    @Override
    protected int setLayoutResourceId() {
        return R.layout.fragment_search_radio;
    }

    @Override
    protected void initView(View view) {
        super.initView(view);
        rcv_search_radio = (RecyclerView) view.findViewById(R.id.rcv_search_radio);
        rcv_search_radio.setLayoutManager(new LinearLayoutManager(mContext));
        rcv_search_radio.addItemDecoration(new AuthorItemDecoration(mContext));
        ll_search_loading = (LinearLayout) view.findViewById(R.id.ll_search_loading);
        ll_search_result_empty = (LinearLayout) view.findViewById(R.id.ll_search_result_empty);
        ll_net_work_fail = (LinearLayout) view.findViewById(R.id.ll_net_work_fail);
        ll_net_work_fail.setOnClickListener(this);
        iv_progress_loading = (ImageView) view.findViewById(R.id.iv_progress_loading);
        netAnimation = (AnimationDrawable)iv_progress_loading.getBackground();
    }

    @Override
    protected void initData(Bundle arguments) {
        super.initData(arguments);

        radioAdapter = new RadioAdapter(mContext, mActivity);
        radioAdapter.setOnTextMoreClickListener(this);
        rcv_search_radio.setAdapter(radioAdapter);
        mDataFactory.registerSearchKeySetListener(this);
    }

    @Override
    protected void onLazyLoad() {
        super.onLazyLoad();
        loadData();
    }


    @Override
    public void moreTextClickListener(int position) {
        RadioMoreFragment radioMoreFragment = new RadioMoreFragment();
        Bundle bundle = new Bundle();
        RadioSearchResultBean radioSearchResultBean = radioSearchResultBeanList.get(position);
        List<SearchRsRadio> radios = radioSearchResultBean.getRadios();
        ArrayList<MyChannel> myChannelList = new ArrayList<MyChannel>();
        for (int i = 0; i < radios.size(); i++) {
            SearchRsRadio artist = radios.get(i);
            MyChannel myChannel = new MyChannel();
            myChannel.setmTotalCount(0);
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
            int pointIndex = artist.getUri().indexOf(".");
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
                    if (pointIndex != -1) {
                        myChannel.setAlbumId(artist.getUri().substring(0, pointIndex));
                    } else {
                        myChannel.setAlbumId(artist.getUri());
                    }
                    break;
                case "ximalaya":
                    serviceId = XimalayaSoundData.mAppSecret;
                    break;
            }
            myChannel.setMusicServiceId(serviceId);
            myChannel.setChannelId(artist.getMantic_image());
            myChannel.setChannelType(2);
            myChannel.setPlayCount(artist.getMantic_play_count());
            myChannelList.add(myChannel);
        }

        bundle.putSerializable("radioList", myChannelList);
        bundle.putSerializable("radioType", radioSearchResultBean.getResultType());
        bundle.putString("searchKey", key);
        radioMoreFragment.setArguments(bundle);
        getFragmentManager().beginTransaction().
                replace(R.id.radio_layout, radioMoreFragment).addToBackStack(null).commit();
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
        isFirstRefresh = true;
        hasData = false;
        this.key = key;
        if (TextUtils.isEmpty(key)) {
            radioAdapter.setMyChannelList(null);
            return;
        }
    }

    private void loadData() {
        if (isFirstRefresh) {
            radioAdapter.setMyChannelList(null);
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

            radioMyChannelList = new ArrayList<MyChannel>();
            radioSearchResultBeanList = new ArrayList<RadioSearchResultBean>();


//            List<String> uris = null;
//            List<String> uris = new ArrayList<>();
//            uris.add("netease:");
//            uris.add("qingting:");
//            uris.add("idaddy:");
            SearchResultManager.getInstance().getRadioSearchResult(new Callback<RadioSearchRsBean>() {
                @Override
                public void onResponse(Call<RadioSearchRsBean> call, Response<RadioSearchRsBean> response) {

                    if (response.isSuccessful() && null != response.body() && null == response.errorBody()) {
                        isFirstRefresh = false;
                        if (null != response.body().getResult()) {
                            List<RadioSearchResult> radioSearchResults = response.body().getResult();
                            if (null != radioSearchResults && radioSearchResults.size() > 0) {
                                for (int j = 0; j < radioSearchResults.size(); j++) {
                                    List<SearchRsRadio> radios = radioSearchResults.get(j).getRadios();

                                    String categoryUri = radioSearchResults.get(j).getUri();
                                    if (null != radios && radios.size() > 0) {
                                        hasData = true;
                                        ll_search_result_empty.setVisibility(View.GONE);
                                        RadioSearchResultBean bean = new RadioSearchResultBean();
                                        bean.setResultSize(radios.size());
                                        String type = categoryUri.substring(0, categoryUri.indexOf(":"));
                                        switch (type) {
                                            case "qingting":
                                                bean.setResultType("蜻蜓FM");
                                                break;
                                            case "netease":
                                                bean.setResultType("网易云音乐");
                                                break;
                                        }
                                        bean.setRadios(radios);
                                        radioSearchResultBeanList.add(bean);


                                        for (int i = 0; i < radios.size(); i++) {
                                            SearchRsRadio artist = radios.get(i);
                                            MyChannel myChannel = new MyChannel();
                                            myChannel.setmTotalCount(0);
                                            myChannel.setUrl(artist.getUri());
                                            myChannel.setChannelCoverUrl(artist.getMantic_image());
                                            myChannel.setChannelName(artist.getName());
                                            myChannel.setChannelIntro(artist.getMantic_describe());
                                            myChannel.setPlayCount(artist.getMantic_play_count());
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
                                            int index = artist.getUri().indexOf(".");
                                            String serviceId = "";
                                            switch (uri) {
                                                case "netease":
                                                    serviceId = "wangyi";
                                                    if (index != -1) {
                                                        myChannel.setAlbumId(artist.getUri().substring(0, index));
                                                    } else {
                                                        myChannel.setAlbumId(artist.getUri());
                                                    }
                                                    myChannel.setMainId("");
                                                    break;
                                                case "qingting":
                                                    serviceId = "qingting";
                                                    if (index != -1) {
                                                        myChannel.setAlbumId(artist.getUri().substring(0, index));
                                                    } else {
                                                        myChannel.setAlbumId(artist.getUri());
                                                    }
                                                    myChannel.setMainId("");
                                                    break;
                                            }
                                            myChannel.setPlayCount(artist.getMantic_play_count());
                                            myChannel.setMusicServiceId(serviceId);
                                            myChannel.setChannelId(artist.getMantic_image());
                                            myChannel.setChannelType(2);
                                            radioMyChannelList.add(myChannel);

                                            if (i <= 2) {
                                                RadioSearchResultBean bean1 = new RadioSearchResultBean();
                                                bean1.setMyChannel(myChannel);
                                                radioSearchResultBeanList.add(bean1);
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
                        } else {
                            if (!hasData) {
                                ll_search_result_empty.setVisibility(View.VISIBLE);
                            } else {
                                ll_search_result_empty.setVisibility(View.GONE);
                            }
                        }
                        radioAdapter.setMyChannelList(radioSearchResultBeanList);
                        rcv_search_radio.scrollToPosition(0);
                    } else {
                        isFirstRefresh = true;
                        ll_net_work_fail.setVisibility(View.VISIBLE);
                        mDataFactory.notifyOperatorResult(getString(R.string.search_fail_try_again_later), false);
                    }
                    ll_search_loading.setVisibility(View.GONE);
                    netAnimation.stop();
                }

                @Override
                public void onFailure(Call<RadioSearchRsBean> call, Throwable t) {
                    ll_search_loading.setVisibility(View.GONE);
                    netAnimation.stop();
                    isFirstRefresh = true;
                    ll_net_work_fail.setVisibility(View.VISIBLE);
                    mDataFactory.notifyOperatorResult(getString(R.string.search_fail_try_again_later), false);
                }
            }, key, 0, 20, getMusicServiceUris());
        }
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
