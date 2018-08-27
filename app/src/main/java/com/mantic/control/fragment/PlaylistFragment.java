package com.mantic.control.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mantic.control.R;

/**
 * Created by wujiangxia on 2017/5/9.
 */
public class PlaylistFragment extends SearchFragment {
    View view;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        if (view == null) {
            view = inflater.inflate(R.layout.search_fragment,container,false);
            initLayout();
        }
        return view;
    }

    private void initLayout() {
    }

    @Override
    public void setSearch(String key) {

    }
}
