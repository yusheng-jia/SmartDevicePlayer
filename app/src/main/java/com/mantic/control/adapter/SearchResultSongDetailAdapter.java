package com.mantic.control.adapter;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.baidu.iot.sdk.HttpStatus;
import com.baidu.iot.sdk.IoTException;
import com.baidu.iot.sdk.model.ControlResult;
import com.baidu.iot.sdk.model.PageInfo;
import com.mantic.control.R;
import com.mantic.control.activity.MainActivity;
import com.mantic.control.api.channelplay.bean.AddResult;
import com.mantic.control.api.channelplay.bean.ChannelPlayAddRsBean;
import com.mantic.control.api.channelplay.bean.ChannelPlayInsertRsBean;
import com.mantic.control.api.channelplay.bean.SetCurrentChannelPlayRsBean;
import com.mantic.control.data.Channel;
import com.mantic.control.data.DataFactory;
import com.mantic.control.manager.ChannelPlayListManager;
import com.mantic.control.utils.AudioPlayerUtil;
import com.mantic.control.utils.GlideImgManager;
import com.mantic.control.utils.Glog;
import com.mantic.control.utils.SharePreferenceUtil;
import com.mantic.control.utils.SleepTimeSetUtils;
import com.mantic.control.utils.TimeUtil;
import com.mantic.control.utils.Util;
import com.mantic.control.widget.CircleImageView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.baidu.iot.sdk.HttpStatus.PUT_OR_POST_SUCCESS;
import static com.mantic.control.data.Channel.PLAY_STATE_PLAYING;
import static com.mantic.control.data.Channel.PLAY_STATE_STOP;

/**
 * Created by lin on 2017/7/7.
 */

public class SearchResultSongDetailAdapter extends RecyclerView.Adapter<SearchResultSongDetailAdapter.ViewHolder> {
    private final String TAG = " ";


    private final static int TYPE_ITEM = 0;
    private final static int TYPE_SECTION = 1;
    private final int TYPE_FOOTER = 2;
    private final int TYPE_FOOTER_COMPLETE = 3;  //说明是带有Footer的
    private static final int TYPE_FOOTER_EMPTY = 4;

    private int currentItemViewTpye = TYPE_FOOTER;


    private Context mContext;
    private Activity mActivity;
    private DataFactory mDataFactory;
    private ArrayList<Channel> channelList = new ArrayList<>();
    private String songType;

    private ArrayList<Channel> beingPlayList;


    public interface OnTextMoreClickListener {
        void moreTextClickListener();
    }

    private OnTextMoreClickListener onTextMoreClickListener;

    public void setOnTextMoreClickListener(OnTextMoreClickListener onTextMoreClickListener) {
        this.onTextMoreClickListener = onTextMoreClickListener;
    }

    public interface OnItemMoreClickListener {
        void moreClickListener(int position);
    }

    private OnItemMoreClickListener onItemMoreClickListener;

    public void setOnItemMoreClickListener(OnItemMoreClickListener onItemMoreClickListener) {
        this.onItemMoreClickListener = onItemMoreClickListener;
    }


    public SearchResultSongDetailAdapter(Context mContext, Activity mActivity, ArrayList<Channel> channelList, String songType) {
        super();
        this.mContext = mContext;
        this.mActivity = mActivity;
        mDataFactory = DataFactory.newInstance(mContext);
        if (null != channelList) {
            this.channelList = channelList;
        }
        this.songType = songType;
        beingPlayList = mDataFactory.getBeingPlayList();
    }


    public void setChannelList(ArrayList<Channel> channelList) {
        Glog.i(TAG, "setChannelList: ");
        this.channelList = channelList;
        for (int i = 0; i < channelList.size(); i++) {
            Channel channel = channelList.get(i);
            if (channel.getUri().equals(mDataFactory.getCurrChannel().getUri())
                    && channel.getName().equals(mDataFactory.getCurrChannel().getName())) {
                channel.setPlayState(mDataFactory.getCurrChannel().getPlayState());
            } else {
                channel.setPlayState(Channel.PLAY_STATE_STOP);
            }
            channelList.set(i, channel);
        }
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return TYPE_SECTION;
        } else if (position + 1 == getItemCount()) {
            return currentItemViewTpye;
        } else {
            return TYPE_ITEM;
        }
    }


    public void showLoadMore() {
        currentItemViewTpye = TYPE_FOOTER;
        notifyItemChanged(getItemCount());
    }

    public void showLoadComplete() {
        currentItemViewTpye = TYPE_FOOTER_COMPLETE;
        notifyItemChanged(getItemCount());
    }

    public void showLoadEmpty() {
        currentItemViewTpye = TYPE_FOOTER_EMPTY;
        notifyItemChanged(getItemCount());
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_SECTION) {
            return new ViewHolder(LayoutInflater.from(mContext).inflate(R.layout.author_header_back, parent, false));
        } else if (viewType == TYPE_FOOTER) {
            return new ViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_foot, parent, false));
        } else if (viewType == TYPE_FOOTER_COMPLETE) {
            return new ViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_foot_complete, parent, false));
        } else if (viewType == TYPE_FOOTER_EMPTY) {
            return new ViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_foot_empty, parent, false));
        } else {
            return new ViewHolder(LayoutInflater.from(mContext).inflate(R.layout.search_result_song_item, parent, false));
        }
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        if (position == 0) {
            holder.tv_search_more.setText("返回");
            holder.tv_author_from_name.setText(songType);
            if ("网易云音乐".equals(songType)) {
                holder.iv_author_from.setImageDrawable(mContext.getResources().getDrawable(R.drawable.wyyyy_music_service_icon));
             } else if ("蜻蜓FM".equals(songType)) {
                holder.iv_author_from.setImageDrawable(mContext.getResources().getDrawable(R.drawable.qingting_music_service_icon));
            } else if ("贝瓦".equals(songType)) {
                holder.iv_author_from.setImageDrawable(mContext.getResources().getDrawable(R.drawable.beiwa_music_service_icon));
            } else if ("喜马拉雅".equals(songType)) {
                holder.iv_author_from.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ximalaya_music_service_icon));
            } else if ("工程师爸爸".equals(songType)) {
                holder.iv_author_from.setImageDrawable(mContext.getResources().getDrawable(R.drawable.idaddy_music_service_icon));
            } else if ("百度云音乐".equals(songType)) {
                holder.iv_author_from.setImageDrawable(mContext.getResources().getDrawable(R.drawable.baidu_music_service_icon));
            }

            holder.tv_search_more.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (null != onTextMoreClickListener) {
                        onTextMoreClickListener.moreTextClickListener();
                    }
                }
            });
            return;
        } else if (position == channelList.size() + 1){
            return;
        }

        Channel channel = channelList.get(position - 1);
        GlideImgManager.glideLoaderCircle(mContext, channel.getIconUrl(), R.drawable.fragment_channel_detail_cover,
                R.drawable.fragment_channel_detail_cover, holder.iv_song_icon);
        holder.tv_song_name.setText(channel.getName());
       /* if (0 == channel.getLastSyncTime()) {
            holder.tv_last_sync_time.setVisibility(View.GONE);
        } else {
            holder.tv_last_sync_time.setVisibility(View.VISIBLE);
            holder.tv_last_sync_time.setText(TimeUtil.getDateFromMillisecond(channel.getLastSyncTime()));
        }*/
        holder.tv_last_sync_time.setText(TextUtils.isEmpty(channel.getSinger()) ? mContext.getString(R.string.unknown) : channel.getSinger());

        if (channel.getDuration() != 0) {
            holder.tv_song_duration.setText(String.format(mContext.getString(R.string.album_time), TimeUtil.secondToTime((int) channel.getDuration())));
            holder.tv_song_duration.setVisibility(View.VISIBLE);
        } else {
            holder.tv_song_duration.setVisibility(View.GONE);
        }

        holder.btn_song_more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != onItemMoreClickListener) {
                    onItemMoreClickListener.moreClickListener(position - 1);
                }
            }
        });

        holder.rl_song_goto_channel_details.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.rl_song_goto_channel_details.setClickable(false);
                playPause(position - 1, holder.rl_song_goto_channel_details);
            }
        });

    }


    public void playPause(final int position, final View view) {
        final Channel channel = channelList.get(position);
        playChannel(channel, view, position);
    }

    private void playChannel(final Channel oldChannel, final View view, final int position) {
        view.setClickable(false);
        AudioPlayerUtil.playOrPause(mContext, oldChannel, view, mActivity, mDataFactory);
    }


    @Override
    public int getItemCount() {
        return (channelList.size() == 0) ? 0 : (channelList.size() + 2);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public RelativeLayout rl_song_goto_channel_details;
        public ImageView iv_song_icon;
        public TextView tv_song_name;
        public TextView tv_last_sync_time;
        public TextView tv_song_duration;
        public ImageButton btn_song_more;
        public TextView tv_search_more;
        public TextView tv_author_from_name;
        public CircleImageView iv_author_from;

        public ViewHolder(View itemView) {
            super(itemView);

            rl_song_goto_channel_details = (RelativeLayout) itemView.findViewById(R.id.rl_song_goto_channel_details);
            iv_song_icon = (ImageView) itemView.findViewById(R.id.iv_song_icon);
            tv_song_name = (TextView) itemView.findViewById(R.id.tv_song_name);
            tv_last_sync_time = (TextView) itemView.findViewById(R.id.tv_last_sync_time);
            tv_song_duration = (TextView) itemView.findViewById(R.id.tv_song_duration);
            btn_song_more = (ImageButton) itemView.findViewById(R.id.btn_song_more);
            tv_search_more = (TextView) itemView.findViewById(R.id.tv_search_more);
            tv_author_from_name = (TextView) itemView.findViewById(R.id.tv_author_from_name);
            iv_author_from = (CircleImageView) itemView.findViewById(R.id.iv_author_from);
        }
    }
}
