package com.mantic.control.fragment;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.mantic.antservice.util.ToastUtils;
import com.mantic.control.R;
import com.mantic.control.adapter.MyMusicServiceAdapter;
import com.mantic.control.api.myservice.MyServiceOperatorRetrofit;
import com.mantic.control.api.myservice.MyServiceOperatorServiceApi;
import com.mantic.control.api.myservice.bean.MusicServiceBean;
import com.mantic.control.data.DataFactory;
import com.mantic.control.decoration.MyMusicServiceItemDecoration;
import com.mantic.control.entiy.MusicService;
import com.mantic.control.entiy.MyMusicServiceBean;
import com.mantic.control.musicservice.BaiduSoundData;
import com.mantic.control.musicservice.IdaddyMusicService;
import com.mantic.control.musicservice.MyMusicService;
import com.mantic.control.musicservice.QingtingMusicService;
import com.mantic.control.musicservice.WangyiMusicService;
import com.mantic.control.musicservice.XimalayaSoundData;
import com.mantic.control.utils.Glog;
import com.mantic.control.utils.GsonUtil;
import com.mantic.control.utils.NetworkUtils;
import com.mantic.control.utils.SharePreferenceUtil;
import com.mantic.control.widget.ReboundScrollView;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by lin on 2017/6/2.
 *
 */

public class MyMusicServiceFragment extends BaseFragment implements MyMusicServiceAdapter.OnItemClickLitener,
        DataFactory.OnAddMyMusciServiceListener, View.OnClickListener, ReboundScrollView.ScrollListener, SwipeRefreshLayout.OnRefreshListener{

    private DataFactory mDataFactory;
    private List<MusicService> mMyMusicServiceList;
    private SwipeRefreshLayout srl_my_music_service;
    private RecyclerView rv_my_music_service;
    private MyMusicServiceAdapter myMusicServiceAdapter;

    @Override
    protected void initData(Bundle arguments) {
        super.initData(arguments);
        this.mDataFactory = DataFactory.newInstance(mContext);

        this.mMyMusicServiceList = new ArrayList<MusicService>();
        myMusicServiceAdapter = new MyMusicServiceAdapter(mContext, mMyMusicServiceList);
        myMusicServiceAdapter.setmOnItemClickLitener(this);
        rv_my_music_service.setAdapter(myMusicServiceAdapter);
        rv_my_music_service.setLayoutManager(new LinearLayoutManager(mContext));
        rv_my_music_service.addItemDecoration(new MyMusicServiceItemDecoration(mContext));
        rv_my_music_service.setNestedScrollingEnabled(false);

        mDataFactory.registerMyMusiceServiceListListener(this);

        getMyServiceData();
    }

    @Override
    protected void initView(View view) {
        super.initView(view);
        rv_my_music_service = (RecyclerView) view.findViewById(R.id.rv_my_music_service);
        srl_my_music_service = (SwipeRefreshLayout) view.findViewById(R.id.srl_my_music_service);

        srl_my_music_service.setOnRefreshListener(this);
    }

    @Override
    public void onRefresh() {
        getMyServiceData();
    }

    private void getMyServiceData() {
        mMyMusicServiceList = new ArrayList<>();
        MyServiceOperatorServiceApi myServiceOperatorServiceApi = MyServiceOperatorRetrofit.getInstance().create(MyServiceOperatorServiceApi.class);
        Call<MusicServiceBean> getListCall = myServiceOperatorServiceApi.getMyServiceList();
        getListCall.enqueue(new Callback<MusicServiceBean>() {
            @Override
            public void onResponse(Call<MusicServiceBean> call, Response<MusicServiceBean> response) {
                if (response.isSuccessful() && null == response.errorBody()) {
                    MusicServiceBean musicServiceBean = response.body();
                    if (null != musicServiceBean) {
                        List<MusicServiceBean.ServiceBean> serviceBeanList = musicServiceBean.getMUSIC();
                        if (null != serviceBeanList && serviceBeanList.size() > 0) {
                            List<String> myMusicServiceUriList = mDataFactory.getMyMusicServiceUriList();
                            myMusicServiceUriList.clear();
                            MusicServiceBean.ServiceBean serviceBean = serviceBeanList.get(0);
                            List<MusicServiceBean.ServiceBean.MyServiceBean> myServiceBeanList = serviceBean.getSERVICE();
                            if (null != myServiceBeanList && myServiceBeanList.size() > 0) {
                                for (int i = 0; i < myServiceBeanList.size(); i++) {
                                    MusicService musicService = new MusicService();
                                    musicService.setIconUrl(myServiceBeanList.get(i).getPIC_URL());
                                    musicService.setName(myServiceBeanList.get(i).getNAME());
                                    musicService.setActive(true);
                                    musicService.setIntroduction(myServiceBeanList.get(i).getDES());
                                    switch (myServiceBeanList.get(i).getNAME()) {
                                        case "百度云音乐":
                                            musicService.setMyMusicService(new BaiduSoundData(mContext));
                                            mMyMusicServiceList.add(musicService);
                                            myMusicServiceUriList.add("baidu:");
                                            break;
                                        case "喜马拉雅":
                                            musicService.setMyMusicService(new XimalayaSoundData(mContext));
                                            mMyMusicServiceList.add(musicService);
                                            myMusicServiceUriList.add("ximalaya:");
                                            break;
                                        case "网易云音乐":
                                            musicService.setMyMusicService(new WangyiMusicService());
                                            mMyMusicServiceList.add(musicService);
                                            myMusicServiceUriList.add("netease:");
                                            break;
                                        case "蜻蜓FM":
                                            musicService.setMyMusicService(new QingtingMusicService());
                                            mMyMusicServiceList.add(musicService);
                                            myMusicServiceUriList.add("qingting:");
                                            break;
                                        case "工程师爸爸":
                                            musicService.setMyMusicService(new IdaddyMusicService());
                                            mMyMusicServiceList.add(musicService);
                                            myMusicServiceUriList.add("idaddy:");
                                            break;
                                        default:
                                            break;
                                    }
                                }

                                myMusicServiceAdapter.setMyMusicServiceList(mMyMusicServiceList);
                                mDataFactory.setMyMusicServiceUriList(myMusicServiceUriList);
                            }
                        }

                        if (srl_my_music_service.isRefreshing()) {
                            srl_my_music_service.setRefreshing(false);
                            mDataFactory.notifyOperatorResult(mContext.getString(R.string.refresh_data_success), true);
                        }
                    }

                } else {
                    if (srl_my_music_service.isRefreshing()) {
                        srl_my_music_service.setRefreshing(false);
                        if (!NetworkUtils.isAvailableByPing(mContext)) {
                            mDataFactory.notifyOperatorResult(mContext.getString(R.string.network_suck), false);
                        } else {
                            mDataFactory.notifyOperatorResult(mContext.getString(R.string.net_failed), false);
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<MusicServiceBean> call, Throwable t) {
                if (srl_my_music_service.isRefreshing()) {
                    srl_my_music_service.setRefreshing(false);
                    if (!NetworkUtils.isAvailableByPing(mContext)) {
                        mDataFactory.notifyOperatorResult(mContext.getString(R.string.network_suck), false);
                    } else {
                        mDataFactory.notifyOperatorResult(mContext.getString(R.string.net_failed), false);
                    }
                }
            }
        });

    }


    @Override
    public void onItemClick(View view, int position, boolean isLast) {
        if (isLast) {
            MusicServiceListFragment musicServiceListFragment = new MusicServiceListFragment();
            if (MyMusicServiceFragment.this.getActivity() instanceof FragmentEntrust) {
                String fragmentTag = "MusicServiceListFragment";
                ((FragmentEntrust) MyMusicServiceFragment.this.getActivity()).pushFragment(musicServiceListFragment, fragmentTag);
            }
        } else {
            MusicService musicService = this.mMyMusicServiceList.get(position);

            Glog.i("lbj", "onItemClick: " + musicService.getName());

            MusicServiceSubItemFragment mssiFragment = new MusicServiceSubItemFragment();
            Bundle bundle = new Bundle();
            bundle.putString(MyMusicService.MY_MUSIC_SERVICE_ID,musicService.getID());
            bundle.putInt(MyMusicService.PRE_DATA_TYPE,MyMusicService.TYPE_FIRST);
            bundle.putString(MusicServiceSubItemFragment.SUB_ITEM_TITLE,musicService.getName());
            mssiFragment.setArguments(bundle);
            if (MyMusicServiceFragment.this.getActivity() instanceof FragmentEntrust) {
                String fragmentTag = MyMusicService.PRE_DATA_TYPE+MyMusicService.TYPE_FIRST+musicService.getID();
                ((FragmentEntrust) MyMusicServiceFragment.this.getActivity()).pushFragment(mssiFragment, fragmentTag);
            }
        }
    }


    @Override
    protected int setLayoutResourceId() {
        return R.layout.fragment_my_music_service;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mDataFactory.unregisterMyMusiceServiceListListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            default:
                break;
        }
    }

    @Override
    public void addToMyMusicService(MusicService musicService, boolean isNeedChange) {
        List<MusicService> musicServices = mDataFactory.getmMyMusicServiceList();
        musicServices.add(musicService);
        mDataFactory.setMyMusicServiceList(removeDuplicate(musicServices));
        saveData();
        this.myMusicServiceAdapter.setMyMusicServiceList(removeDuplicate(mDataFactory.getmMyMusicServiceList()));
    }

    public List<MusicService> removeDuplicate(List<MusicService> list) {
        for (int i = 0; i < list.size(); i++) {
            for (int j = list.size() - 1; j > i; j--) {
                if (list.get(j).getName().equals(list.get(i).getName())) {
                    list.remove(j);
                }
            }
        }
        return list;
    }


    @Override
    public void removeToMyMusicService(MusicService musicService, boolean isNeedChange) {
        mDataFactory.removeMyService(musicService);

        saveData();
        this.myMusicServiceAdapter.setMyMusicServiceList(mDataFactory.getmMyMusicServiceList());
    }


    private void saveData() {
        List<MyMusicServiceBean> myMusicServiceBeans = new ArrayList<>();
        for (int i = 0; i < mDataFactory.getmMyMusicServiceList().size(); i++) {
            MusicService musicService = mDataFactory.getmMyMusicServiceList().get(i);
            MyMusicServiceBean bean = new MyMusicServiceBean();
            bean.setIntroduction(musicService.getIntroduction());
            bean.setName(musicService.getName());
            myMusicServiceBeans.add(bean);
        }

        SharePreferenceUtil.setSharePreferenceData(mContext, "Mantic", "MyMusicServiceBeanList", GsonUtil.myMusicServiceBeanListToString(myMusicServiceBeans));
//        ACacheUtil.putData(mContext, "MyMusicServiceBeanList", GsonUtil.myMusicServiceBeanListToString(myMusicServiceBeans));
    }
}
