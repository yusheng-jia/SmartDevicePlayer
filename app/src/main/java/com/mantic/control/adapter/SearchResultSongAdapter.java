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
import android.widget.LinearLayout;
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
import com.mantic.control.api.searchresult.bean.SongSearchResultBean;
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

public class SearchResultSongAdapter extends RecyclerView.Adapter<SearchResultSongAdapter.ViewHolder> {
    private final String TAG = "SearchResultSongAdapter";


    private final int TYPE_ITEM = 0;
    private final int TYPE_SECTION = 1;

    private Context mContext;
    private Activity mActivity;
    private DataFactory mDataFactory;

    private ArrayList<SongSearchResultBean> songSearchResultBeanList;


    private ArrayList<Channel> beingPlayList;


    public interface OnTextMoreClickListener {
        void moreTextClickListener(int position);
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


    public SearchResultSongAdapter(Context mContext, Activity mActivity) {
        super();
        this.mContext = mContext;
        this.mActivity = mActivity;
        mDataFactory = DataFactory.newInstance(mContext);
        beingPlayList = mDataFactory.getBeingPlayList();
    }


    public void setChannelList(ArrayList<SongSearchResultBean> songSearchResultBeanList) {
      /*  this.channelList = channelList;
        for (int i = 0; i < channelList.size(); i++) {
            Channel channel = channelList.get(i);
            if (channel.getUri().equals(mDataFactory.getCurrChannel().getUri())
                    && channel.getName().equals(mDataFactory.getCurrChannel().getName())) {
                channel.setPlayState(mDataFactory.getCurrChannel().getPlayState());
            } else {
                channel.setPlayState(Channel.PLAY_STATE_STOP);
            }
            channelList.set(i, channel);
        }*/
        this.songSearchResultBeanList = songSearchResultBeanList;
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        if (!TextUtils.isEmpty(songSearchResultBeanList.get(position).getResultType())) {
            return TYPE_SECTION;
        }
        return TYPE_ITEM;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_SECTION) {
            return new ViewHolder(LayoutInflater.from(mContext).inflate(R.layout.author_header, parent, false));
        } else {
            return new ViewHolder(LayoutInflater.from(mContext).inflate(R.layout.search_result_song_item, parent, false));
        }
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        SongSearchResultBean songSearchResultBean = songSearchResultBeanList.get(position);

        if (!TextUtils.isEmpty(songSearchResultBean.getResultType())) {
            holder.tv_author_from_name.setText(songSearchResultBean.getResultType());
            if (songSearchResultBean.getResultType().equals("蜻蜓FM")) {
                holder.iv_author_from.setImageDrawable(mContext.getResources().getDrawable(R.drawable.qingting_music_service_icon));
            } else if (songSearchResultBean.getResultType().equals("网易云音乐")) {
                holder.iv_author_from.setImageDrawable(mContext.getResources().getDrawable(R.drawable.wyyyy_music_service_icon));
            } else if (songSearchResultBean.getResultType().equals("贝瓦")) {
                holder.iv_author_from.setImageDrawable(mContext.getResources().getDrawable(R.drawable.beiwa_music_service_icon));
            } else if (songSearchResultBean.getResultType().equals("喜马拉雅")) {
                holder.iv_author_from.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ximalaya_music_service_icon));
            } else if (songSearchResultBean.getResultType().equals("工程师爸爸")) {
                holder.iv_author_from.setImageDrawable(mContext.getResources().getDrawable(R.drawable.idaddy_music_service_icon));
            } else if (songSearchResultBean.getResultType().equals("百度云音乐")) {
                holder.iv_author_from.setImageDrawable(mContext.getResources().getDrawable(R.drawable.baidu_music_service_icon));
            }

            if (songSearchResultBean.getResultSize() < 3) {
                holder.ll_search_more.setVisibility(View.GONE);
            } else {
                holder.ll_search_more.setVisibility(View.VISIBLE);
                holder.ll_search_more.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (null != onTextMoreClickListener) {
                            onTextMoreClickListener.moreTextClickListener(position);
                            mDataFactory.notifyHideSoftKey();
                        }
                    }
                });
            }
            return;
        }

        final Channel channel = songSearchResultBean.getChannel();
        GlideImgManager.glideLoaderCircle(mContext, channel.getIconUrl(), R.drawable.fragment_channel_detail_cover,
                R.drawable.fragment_channel_detail_cover, holder.iv_song_icon);
        holder.tv_song_name.setText(channel.getName());

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
                    onItemMoreClickListener.moreClickListener(position);
                    mDataFactory.notifyHideSoftKey();
                }
            }
        });

        holder.rl_song_goto_channel_details.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.rl_song_goto_channel_details.setClickable(false);
                playPause(channel, holder.rl_song_goto_channel_details, position);
            }
        });

    }


    public void playPause(final Channel channel, final View view, final int position) {
        mDataFactory.notifyHideSoftKey();

        Channel currChannel = mDataFactory.getCurrChannel();

        if (null != currChannel && currChannel.getName().equals(channel.getName()) && currChannel.getUri().equals(channel.getUri())) {
            if (currChannel.getPlayState() == Channel.PLAY_STATE_PLAYING) {
                ((MainActivity) mActivity).setBottomSheetExpanded();
                if (null != view) {
                    view.setClickable(true);
                }
                return;
            }

        }

        playChannel(channel, view, position);

    }

    private void playChannel(final Channel oldChannel, final View view, final int position) {
        view.setClickable(false);
        AudioPlayerUtil.playOrPause(mContext, oldChannel, view, mActivity, mDataFactory);
    }


    @Override
    public int getItemCount() {
//        return (channelList.size() > 3) ? 4 : (channelList.size() == 0 ? 0 : channelList.size() + 1);
//        return (channelList.size() == 0) ? 0 : (channelList.size() + 1);

        if (null == songSearchResultBeanList || songSearchResultBeanList.size() == 0) {
            return 0;
        }
        return songSearchResultBeanList.size();

    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public RelativeLayout rl_song_goto_channel_details;
        public ImageView iv_song_icon;
        public TextView tv_song_name;
        public TextView tv_last_sync_time;
        public TextView tv_song_duration;
        public ImageButton btn_song_more;
        public LinearLayout ll_search_more;
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
            ll_search_more = (LinearLayout) itemView.findViewById(R.id.ll_search_more);
            tv_author_from_name = (TextView) itemView.findViewById(R.id.tv_author_from_name);
            iv_author_from = (CircleImageView) itemView.findViewById(R.id.iv_author_from);
        }
    }
}
