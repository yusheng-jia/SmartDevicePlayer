package com.mantic.control.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.mantic.control.fragment.MainFragment;

import java.util.List;

/**
 * Created by lin on 2018/1/17.
 */

public class MusicRankFragmentAdapter extends FragmentStatePagerAdapter {
    private List<Fragment> fragmentList;

    public MusicRankFragmentAdapter(FragmentManager fm, List<Fragment> fragmentList) {
        super(fm);
        this.fragmentList = fragmentList;
    }

    @Override
    public int getCount() {
        return fragmentList.size();
    }

    @Override
    public Fragment getItem(int position) {
        return fragmentList.get(position);
    }

}
