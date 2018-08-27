package com.mantic.control.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.View;

import com.mantic.control.R;
import com.mantic.control.adapter.FmRadioAdapter;
import com.mantic.control.adapter.FmRadioFragmentAdapter;
import com.mantic.control.adapter.MusicRankAdapter;
import com.mantic.control.adapter.MusicRankFragmentAdapter;
import com.mantic.control.musicservice.MyMusicService;
import com.mantic.control.widget.RecyclerTabLayout;
import com.mantic.control.widget.TitleBar;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lin on 2018/1/16.
 * FM广播界面
 */

public class FMRadioFragment extends BaseSlideFragment implements TitleBar.OnButtonClickListener{
    private TitleBar tb_fm_radio;
    private RecyclerTabLayout recycler_tab_layout_fm_radio;

    private ViewPager vp_fm_radio;
    private FmRadioFragmentAdapter mFmRadioFragmentAdapter;
    private List<Fragment> fmRadioFragmentList = new ArrayList<>();
    private FmRadioAdapter mFmRadioAdapter;
    @Override
    protected void initData(Bundle arguments) {
        super.initData(arguments);
        fmRadioFragmentList.add(newInstance("qingting:radio:area", "地区"));
        fmRadioFragmentList.add(newInstance("qingting:radio:categories:5:1242", "国家台"));
        fmRadioFragmentList.add(newInstance("qingting:radio:categories:5:1243", "网络台"));
        fmRadioFragmentList.add(newInstance("qingting:radio:type", "分类"));
        mFmRadioFragmentAdapter = new FmRadioFragmentAdapter(getChildFragmentManager(), fmRadioFragmentList);
        vp_fm_radio.setAdapter(mFmRadioFragmentAdapter);
        mFmRadioAdapter = new FmRadioAdapter(vp_fm_radio, mContext);
        recycler_tab_layout_fm_radio.setUpWithAdapter(mFmRadioAdapter);
//        recycler_tab_layout_fm_radio.setPositionThreshold(0.5f);
    }

    @Override
    protected void initView(View view) {
        super.initView(view);
        tb_fm_radio = (TitleBar) view.findViewById(R.id.tb_fm_radio);
        tb_fm_radio.setOnButtonClickListener(this);
        recycler_tab_layout_fm_radio = (RecyclerTabLayout) view.findViewById(R.id.recycler_tab_layout_fm_radio);
        vp_fm_radio = (ViewPager) view.findViewById(R.id.vp_fm_radio);
        vp_fm_radio.setOffscreenPageLimit(4);
    }

    public MusicServiceSubItemLazyLoadFragment newInstance(String tagName, String title) {
        Bundle bundle = new Bundle();
        bundle.putString(MyMusicService.MY_MUSIC_SERVICE_ID, "qingting");
        bundle.putString(MusicServiceSubItemFragment.SUB_ITEM_TITLE, title);
        bundle.putInt(MyMusicService.PRE_DATA_TYPE, 1);
        bundle.putString("tag_name", tagName);
        bundle.putBoolean("isHideTitleBar", true);  //不需要显示titlebar
        bundle.putBoolean("isNotNeedSwipeBack", true);//不需要侧滑退出功能
        MusicServiceSubItemLazyLoadFragment f = new MusicServiceSubItemLazyLoadFragment();
        f.setArguments(bundle);
        return f;
    }

    @Override
    protected int setLayoutResourceId() {
        return R.layout.fragment_fm_radio;
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
