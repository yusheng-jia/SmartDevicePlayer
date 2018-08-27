package com.mantic.control.adapter;

import android.app.Activity;
import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.mantic.antservice.util.ToastUtils;
import com.mantic.control.R;
import com.mantic.control.data.Channel;
import com.mantic.control.decoration.NewSongItemDecoration;
import com.mantic.control.utils.DensityUtils;
import com.mantic.control.utils.GlideImgManager;

import java.util.ArrayList;
import java.util.List;

/**
 * 新歌适配器
 */
public class EntertainmentNewSongAdapter extends PagerAdapter {

    private Context mContext;
    private Activity mActivity;
    private List<Channel> newSongList = new ArrayList<Channel>();

    private NewSongAdapter.OnItemMoreClickListener onItemMoreClickListener;

    public void setOnItemMoreClickListener(NewSongAdapter.OnItemMoreClickListener onItemMoreClickListener) {
        this.onItemMoreClickListener = onItemMoreClickListener;
    }

    public EntertainmentNewSongAdapter(Context context, Activity activity, List<Channel> newSongList) {
        super();
        this.mContext = context;
        this.mActivity = activity;
        if (null != newSongList) {
            this.newSongList = newSongList;
        }
    }

    public void setNewSongList(List<Channel> newSongList) {
        this.newSongList = newSongList;
        notifyDataSetChanged();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public void destroyItem(ViewGroup view, int position, Object object) {
        view.removeView((View) object);
    }

    @Override
    public int getCount() {
        return newSongList.size() >= 12 ? 3 : newSongList.size() / 4;
    }

    @Override
    public Object instantiateItem(ViewGroup container, final int position) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.entertainment_new_song_item, container, false);
        RecyclerView rcv_entertainment_new_song = (RecyclerView) view.findViewById(R.id.rcv_entertainment_new_song);
        rcv_entertainment_new_song.setLayoutManager(new LinearLayoutManager(mContext));
        rcv_entertainment_new_song.setFocusable(false);
        rcv_entertainment_new_song.setNestedScrollingEnabled(false);
        List<Channel> rcvSongList = new ArrayList<Channel>();
        int currentGroup = 0;
        if (position == 0) {
            rcvSongList = newSongList.size() > 4 ? newSongList.subList(0, 4) : newSongList;
            currentGroup = 0;
        } else if (position == 1) {
            rcvSongList = newSongList.size() > 8 ? newSongList.subList(4, 8) : newSongList.subList(4, newSongList.size());
            currentGroup = 1;
        } else {
            rcvSongList = newSongList.size() > 12 ? newSongList.subList(8, 12) : newSongList.subList(8, newSongList.size());
            currentGroup = 2;
        }
        NewSongAdapter newSongAdapter = new NewSongAdapter(mContext, mActivity, rcvSongList, currentGroup);
        newSongAdapter.setOnItemMoreClickListener(onItemMoreClickListener);
        rcv_entertainment_new_song.setAdapter(newSongAdapter);
        rcv_entertainment_new_song.addItemDecoration(new NewSongItemDecoration(mContext));
        container.addView(view);
        return view;
    }

}
