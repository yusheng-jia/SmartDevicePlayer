package com.mantic.control.fragment;

import android.content.Context;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.mantic.control.R;
import com.mantic.control.data.Channel;
import com.mantic.control.data.DataFactory;
import com.mantic.control.utils.Glog;
import com.ximalaya.ting.android.opensdk.constants.DTransferConstants;
import com.ximalaya.ting.android.opensdk.datatrasfer.CommonRequest;
import com.ximalaya.ting.android.opensdk.datatrasfer.IDataCallBack;
import com.ximalaya.ting.android.opensdk.model.track.SearchTrackList;
import com.ximalaya.ting.android.opensdk.model.track.Track;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by wujiangxia on 2017/5/9.
 */
public class SearchLastPlayListFragment extends Fragment {
    View view;
    private TrackAdapter mTrackAdapter;
    private RecyclerView rcv_recent_play;
    private boolean mLoading = false;
    private ArrayList<Channel> mBeingPlayList;
    private DataFactory mDataFactory;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Glog.v("wujx","SearchLastPlayListFragment onCreateView");
        if (view == null) {
            view = inflater.inflate(R.layout.search_list_view,container,false);
            rcv_recent_play= (RecyclerView) view.findViewById(R.id.rcv_recent_play);
            rcv_recent_play.setLayoutManager(new LinearLayoutManager(getActivity()));
            rcv_recent_play.addItemDecoration(new RecyclerView.ItemDecoration() {
                @Override
                public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                    outRect.set(0, 0, 0, 1);
                }
            });
        }
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        Glog.e("wujx", "onActivityCreated");
        super.onActivityCreated(savedInstanceState);
        this.mDataFactory = DataFactory.newInstance(getActivity().getApplicationContext());
        this.mBeingPlayList = this.mDataFactory.getBeingPlayList();
        for(int i=0;i<mBeingPlayList.size();i++) {
            Glog.e("wujx", "getName:" + mBeingPlayList.get(i).getName());
            Glog.e("wujx", "getAlbum:" + mBeingPlayList.get(i).getAlbum());
        }
        mTrackAdapter = new TrackAdapter(getActivity());
        rcv_recent_play.setAdapter(mTrackAdapter);

    }


    private void getPlaylist() {

    }

    public class TrackAdapter  extends RecyclerView.Adapter<ViewHolder> {
        public final static int TYPE_SECTION = 1;
        public final static int TYPE_ITEM = 0;
        private Context mContext;
        public TrackAdapter(Context context){
            this.mContext = context;


        }

        @Override
        public int getItemViewType(int position) {
            Glog.e("wujx","getItemViewType:");
          /*  if (position == 0 || position == mBeingPlayList.size()) {
                return TYPE_SECTION;
            } else {
                return TYPE_ITEM;
            }*/
            return TYPE_ITEM;

        }
        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = null;
            if (viewType == TYPE_SECTION) {
                view = LayoutInflater.from(mContext).inflate(R.layout.search_list_section, parent, false);
            } else {
                view = LayoutInflater.from(mContext).inflate(R.layout.track_content, parent, false);
            }
            ViewHolder holder = new ViewHolder(view);
            return holder;
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            int type = getItemViewType(position);
            if (type == TYPE_SECTION) {
                holder.mName.setText("喜马拉雅");
            } else {
                Channel sound = mBeingPlayList.get(position);
                holder.mName.setText(sound.getName());
                holder.mAuthor.setText(sound.getSinger() == null ? sound.getAlbum() : sound.getSinger());
                if(sound.getIcon()!=null)
                    holder.mIcon.setImageBitmap(sound.getIcon());
                    /*Glide
                            .with(mContext)
                            .load(sound.getCoverUrlLarge())
                            .placeholder(R.drawable.audio_bottom_bar_album_cover)
                            .into(holder.mIcon);*/
            }
        }

        @Override
        public int getItemCount() {
            int size=mBeingPlayList != null?mBeingPlayList.size():0;
            return size;
        }
    }



    private static class ViewHolder extends RecyclerView.ViewHolder {
        View mContainer;
        ImageView mIcon;
        TextView mName;
        TextView mAuthor;

        public ViewHolder(View view) {
            super(view);
            mIcon = (ImageView) view.findViewById(R.id.imageview);
            mName = (TextView) view.findViewById(R.id.trackname);
            mAuthor = (TextView) view.findViewById(R.id.tv_album_singer);
        }


    }
}
