package com.mantic.control.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.mantic.control.R;
import com.mantic.control.activity.MainActivity;
import com.mantic.control.adapter.MusicServiceListAdapter;
import com.mantic.control.data.DataFactory;
import com.mantic.control.decoration.MusicServiceItemDecoration;
import com.mantic.control.entiy.MusicService;
import com.mantic.control.musicservice.MyMusicService;
import com.mantic.control.widget.TitleBar;

import java.util.ArrayList;

/**
 * Created by lin on 2017/6/3.
 * 服务列表
 */

public class MusicServiceListFragment extends BaseSlideFragment implements MusicServiceListAdapter.OnItemClickLitener,
        TitleBar.OnButtonClickListener, DataFactory.OnAddMyMusciServiceListener {
    private DataFactory mDataFactory;
    private ArrayList<MusicService> mMusicServiceList;
    private RecyclerView rv_my_music_service;
    private TitleBar tb_music_service;
    private MusicServiceListAdapter mMusicServiceListAdapter;

    public MusicServiceListFragment(){
        this.initMusicServiceData();
    }


    private void initMusicServiceData(){
        this.mDataFactory = DataFactory.newInstance(getActivity());
        this.mMusicServiceList = this.mDataFactory.getMusicServiceList();
    }


    @Override
    protected void initData(Bundle arguments) {
        super.initData(arguments);
        ((MainActivity)mActivity).setAudioPlayerVisible(false);
    }

    @Override
    protected int setLayoutResourceId() {
        return R.layout.fragment_music_service_list;
    }

    protected void initView(View view) {
        super.initView(view);
        rv_my_music_service = (RecyclerView) view.findViewById(R.id.rv_music_service_list);
        mMusicServiceListAdapter = new MusicServiceListAdapter(getContext());
        mMusicServiceListAdapter.setmOnItemClickLitener(this);
        rv_my_music_service.setAdapter(mMusicServiceListAdapter);
        rv_my_music_service.setLayoutManager(new LinearLayoutManager(getContext()));
        rv_my_music_service.addItemDecoration(new MusicServiceItemDecoration(getContext()));
        rv_my_music_service.setNestedScrollingEnabled(false);
        tb_music_service = (TitleBar) view.findViewById(R.id.tb_music_service);
        tb_music_service.setOnButtonClickListener(this);
        mDataFactory.registerMyMusiceServiceListListener(this);
    }


    @Override
    public void onLeftClick() {
        if(getActivity() instanceof FragmentEntrust){
           ((FragmentEntrust)getActivity()).popFragment(getTag());
        }
    }

    @Override
    public void onRightClick() {

    }


    @Override
    public void onDestroy() {
        ((MainActivity)mActivity).setAudioPlayerVisible(true);
        super.onDestroy();
    }

    @Override
    public void addToMyMusicService(MusicService musicService, boolean isNeedNotify) {
        if (!isNeedNotify) {
            return;
        }

        for (int i = 0; i < mMusicServiceList.size(); i++) {
            if (mMusicServiceList.get(i).getName().equals(musicService.getName())) {
                mMusicServiceList.get(i).setActive(true);
            }
        }
        mMusicServiceListAdapter.notifyDataSetChanged();
    }

    @Override
    public void removeToMyMusicService(MusicService musicService, boolean isNeedNotify) {
        if (!isNeedNotify) {
            return;
        }
        for (int i = 0; i < mMusicServiceList.size(); i++) {
            if (mMusicServiceList.get(i).getName().equals(musicService.getName())) {
                mMusicServiceList.get(i).setActive(false);
            }
        }
        mMusicServiceListAdapter.notifyDataSetChanged();
    }

    @Override
    public void onItemClick(View view, int position) {
        MusicService musicService = this.mMusicServiceList.get(position + 2);

        MusicServiceDetailFragment msdFragment = new MusicServiceDetailFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("position", position + 2);
        msdFragment.setArguments(bundle);
        if (MusicServiceListFragment.this.getActivity() instanceof FragmentEntrust) {
            String fragmentTag = MyMusicService.PRE_DATA_TYPE+MyMusicService.TYPE_FIRST+musicService.getID();
            ((FragmentEntrust) MusicServiceListFragment.this.getActivity()).pushFragment(msdFragment, fragmentTag);
        }
    }
}
