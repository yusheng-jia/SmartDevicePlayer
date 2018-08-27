package com.mantic.control.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.mantic.control.R;
import com.mantic.control.adapter.MusicRankAdapter;
import com.mantic.control.adapter.MusicRankFragmentAdapter;
import com.mantic.control.musicservice.BaiduSoundData;
import com.mantic.control.musicservice.MyMusicService;
import com.mantic.control.widget.RecyclerTabLayout;
import com.mantic.control.widget.TitleBar;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lin on 2018/1/16.
 * 音乐排行榜
 */

public class MusicRankFragment extends BaseSlideFragment implements TitleBar.OnButtonClickListener, MusicRankAdapter.OnItemClickListener{
    private TitleBar tb_music_rank;
    private RecyclerTabLayout recycler_tab_layout_music_rank;

    private ViewPager vp_music_rank;
    private MusicRankFragmentAdapter mMusicRankFragmentAdapter;
    private List<Fragment> musicRankFragmentList = new ArrayList<>();
    private MusicRankAdapter mMusicRankAdapter;
    @Override
    protected void initData(Bundle arguments) {
        super.initData(arguments);
        musicRankFragmentList.add(newInstance("全球音乐榜"));
        musicRankFragmentList.add(newInstance("虾米音乐"));
        musicRankFragmentList.add(newInstance("网易云音乐"));
        musicRankFragmentList.add(newInstance("QQ音乐"));
        musicRankFragmentList.add(newInstance("酷我音乐"));
        musicRankFragmentList.add(newInstance("酷狗音乐"));
        mMusicRankFragmentAdapter = new MusicRankFragmentAdapter(getChildFragmentManager(), musicRankFragmentList);
        vp_music_rank.setAdapter(mMusicRankFragmentAdapter);

        //        mMusicRankAdapter = new MusicRankAdapter(mContext);
        mMusicRankAdapter = new MusicRankAdapter(vp_music_rank, mContext);
//        mMusicRankAdapter.setOnItemClickListener(this);
        recycler_tab_layout_music_rank.setUpWithAdapter(mMusicRankAdapter);
        recycler_tab_layout_music_rank.setPositionThreshold(0.5f);
    }

    @Override
    protected void initView(View view) {
        super.initView(view);
        tb_music_rank = (TitleBar) view.findViewById(R.id.tb_music_rank);
        tb_music_rank.setOnButtonClickListener(this);
        recycler_tab_layout_music_rank = (RecyclerTabLayout) view.findViewById(R.id.recycler_tab_layout_music_rank);
        vp_music_rank = (ViewPager) view.findViewById(R.id.vp_music_rank);
        vp_music_rank.setOffscreenPageLimit(6);
    }

    public MusicServiceSubItemFragment newInstance(String rankName) {
        Bundle bundle = new Bundle();
        bundle.putString(MyMusicService.MY_MUSIC_SERVICE_ID, "baidu");
        bundle.putString(MusicServiceSubItemFragment.SUB_ITEM_TITLE, rankName);
        bundle.putInt(MyMusicService.PRE_DATA_TYPE, 2);
        bundle.putString("main_id", rankName);
        bundle.putBoolean("isHideTitleBar", true);
        bundle.putBoolean("isNotNeedSwipeBack", true);//不需要侧滑退出功能
        MusicServiceSubItemFragment f = new MusicServiceSubItemFragment();
        f.setArguments(bundle);
        return f;
    }

    @Override
    protected int setLayoutResourceId() {
        return R.layout.fragment_music_rank;
    }


    @Override
    public void onItemClick(int position) {
        vp_music_rank.setCurrentItem(position);
    }

    @Override
    public void onLeftClick() {
        if(mActivity instanceof FragmentEntrust){
            ((FragmentEntrust) getActivity()).popFragment(getTag());
        }
    }

    @Override
    public void onRightClick() {

    }

}
