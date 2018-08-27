package com.mantic.control.adapter;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mantic.control.R;
import com.mantic.control.entiy.ClassificationBean;
import com.mantic.control.fragment.ChannelDetailsFragment;
import com.mantic.control.fragment.FragmentEntrust;
import com.mantic.control.musicservice.IMusicServiceSubItemAlbum;
import com.mantic.control.musicservice.MyMusicService;
import com.mantic.control.utils.Glog;
import com.mantic.control.utils.Util;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lin on 2018/1/17.
 */

public class ClassificationSongAdapter extends RecyclerView.Adapter<ClassificationSongAdapter.ClassificationSongHolder>{

    protected List<ClassificationBean> classificationBeanList = new ArrayList<>();
    public final static int ITEM_VIEW_TYPE_TITLE = 1;
    public final static int ITEM_VIEW_TYPE_NORMAL = 0;
    private Context mContext;

    public ClassificationSongAdapter(Context context) {
        mContext = context;
    }

    public void setClassificationBeanList(List<ClassificationBean> classificationBeanList) {
        this.classificationBeanList = classificationBeanList;
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(ClassificationSongHolder holder, int position) {
        final ClassificationBean classificationBean = classificationBeanList.get(position);
        if (classificationBean.isTitle()) {
            if (null != holder.tv_classification_title_name) {
                holder.tv_classification_title_name.setText(classificationBean.getClassificationName());
            }
        } else {
            if (null != holder.tv_classification_song_name) {
                holder.tv_classification_song_name.setText(classificationBean.getClassificationName());
                holder.rl_classification_song_item.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        ChannelDetailsFragment fragment = new ChannelDetailsFragment();
                        Bundle bundle = new Bundle();
                        bundle.putString(MyMusicService.MY_MUSIC_SERVICE_ID, "baidu");
                        bundle.putString(ChannelDetailsFragment.CHANNEL_NAME, classificationBean.getClassificationName());
                        bundle.putString(ChannelDetailsFragment.CHANNEL_INTRO, "歌曲:0");
                        bundle.putString(ChannelDetailsFragment.CHANNEL_ID, "");
                        bundle.putString(ChannelDetailsFragment.ALBUM_ID, "baidu:album:" + classificationBean.getClassificationName());
                        bundle.putString(ChannelDetailsFragment.MAIN_ID, classificationBean.getClassificationName());
                        bundle.putInt(ChannelDetailsFragment.CHANNEL_TYPE, 0);
                        bundle.putInt(MyMusicService.PRE_DATA_TYPE, 3);
                        fragment.setArguments(bundle);
                        if (mContext instanceof FragmentEntrust) {
                            ((FragmentEntrust) mContext).pushFragment(fragment, "ClassificationSongFragment");
                        }
                    }
                });
            }

        }
    }

    @Override
    public ClassificationSongHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == ITEM_VIEW_TYPE_NORMAL) {
            return new ClassificationSongHolder(LayoutInflater.from(mContext).inflate(R.layout.classification_song_item, parent, false), false);
        } else {
            return new ClassificationSongHolder(LayoutInflater.from(mContext).inflate(R.layout.classification_title_item, parent, false), true);
        }
    }

    @Override
    public int getItemCount() {
        return classificationBeanList.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (classificationBeanList.get(position).isTitle()) {
            return ITEM_VIEW_TYPE_TITLE;
        }
        return ITEM_VIEW_TYPE_NORMAL;
    }

    public class ClassificationSongHolder extends RecyclerView.ViewHolder {
        private RelativeLayout rl_classification_song_item;
        private TextView tv_classification_title_name;
        private TextView tv_classification_song_name;
        private View view_underline;

        public ClassificationSongHolder( View itemView, boolean isTitle) {
            super(itemView);
            if (isTitle) {
                tv_classification_title_name = (TextView) itemView.findViewById(R.id.tv_classification_title_name);
            } else {
                rl_classification_song_item = (RelativeLayout) itemView.findViewById(R.id.rl_classification_song_item);
                tv_classification_song_name = (TextView) itemView.findViewById(R.id.tv_classification_song_name);
                view_underline = itemView.findViewById(R.id.view_underline);
            }


        }
    }
}
