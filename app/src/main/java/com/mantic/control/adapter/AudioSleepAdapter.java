package com.mantic.control.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
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
import com.mantic.antservice.util.ToastUtils;
import com.mantic.control.ManticApplication;
import com.mantic.control.R;
import com.mantic.control.api.channelplay.bean.ChannelPlayListDeleteRsBean;
import com.mantic.control.data.Channel;
import com.mantic.control.data.DataFactory;
import com.mantic.control.entiy.SleepInfo;
import com.mantic.control.manager.ChannelPlayListManager;
import com.mantic.control.utils.AudioPlayerUtil;
import com.mantic.control.utils.SharePreferenceUtil;
import com.mantic.control.utils.SleepTimeSetUtils;
import com.mantic.control.widget.CustomDialog;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.mantic.control.data.Channel.PLAY_STATE_PLAYING;

/**
 * Created by lin on 2017/7/13.
 */

public class AudioSleepAdapter extends RecyclerView.Adapter<AudioSleepAdapter.ViewHolder> {
    private final String TAG = "AudioSleepAdapter";
    private DataFactory mDataFactory;
    private Context mContext;
    private List<SleepInfo> sleepInfoList;

    public interface SleepSetResultListener {
        void success();
        void fail();
        void onError();
    }

    public AudioSleepAdapter(Context mContext) {
        this.mContext = mContext;
        mDataFactory = DataFactory.newInstance(mContext);
        sleepInfoList = mDataFactory.getSleepInfoList();
    }

    public void setSleepInfoList(List<SleepInfo> sleepInfoList) {
        this.sleepInfoList = sleepInfoList;
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(mContext).inflate(R.layout.audio_sleep_timelist_item, parent, false));
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final SleepInfo sleepInfo = sleepInfoList.get(position);
        holder.tv_sleep_time.setText(sleepInfo.getSleepTime());
        if (sleepInfo.isSetting()) {
            holder.iv_sleep_select.setVisibility(View.VISIBLE);
        } else {
            holder.iv_sleep_select.setVisibility(View.GONE);
        }

        holder.rl_audio_sleep_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mDataFactory.getCurrChannel() && mDataFactory.getCurrChannel().getUri().contains(":radio:")) {
                    if (position == 1) {
                        ToastUtils.showShortSafe(mContext.getString(R.string.current_play_is_broadcast_can_not_set_sleep_mode));
                        return;
                    }
                }
                /*else if (null != mDataFactory.getCurrChannel() && mDataFactory.getCurrChannel().getPlayState() != Channel.PLAY_STATE_PLAYING) {
                    if (position == 1) {
                        ToastUtils.showShortSafe(mContext.getString(R.string.current_play_is_not_playing_can_not_set_sleep_mode));
                        return;
                    }
                }*/

                if (sleepInfo.isSetting()) {
                    return;
                }

                final int sleepPosition = SleepTimeSetUtils.getSleepPosition(mDataFactory);
                holder.iv_sleep_select.setVisibility(View.VISIBLE);
                for (int i = 0; i < sleepInfoList.size(); i++) {
                    if (i != position) {
                        sleepInfoList.get(i).setSetting(false);
                    } else {
                        sleepInfoList.get(i).setSetting(true);
                    }
                }
                mDataFactory.setSleepInfoList(sleepInfoList);
                notifyDataSetChanged();
                SleepTimeSetUtils.addSleepTimeSet(sleepInfoList.get(position).getSleepTime(), mContext, mDataFactory, new SleepSetResultListener() {
                    @Override
                    public void success() {

                    }

                    @Override
                    public void fail() {
                        for (int i = 0; i < sleepInfoList.size(); i++) {
                            if (i != sleepPosition) {
                                sleepInfoList.get(i).setSetting(false);
                            } else {
                                sleepInfoList.get(i).setSetting(true);
                            }
                        }
                        mDataFactory.setSleepInfoList(sleepInfoList);
                        notifyDataSetChanged();
                    }

                    @Override
                    public void onError() {
                        for (int i = 0; i < sleepInfoList.size(); i++) {
                            if (i != sleepPosition) {
                                sleepInfoList.get(i).setSetting(false);
                            } else {
                                sleepInfoList.get(i).setSetting(true);
                            }
                        }
                        mDataFactory.setSleepInfoList(sleepInfoList);
                        notifyDataSetChanged();
                    }
                });
            }
        });
    }

    @Override
    public int getItemCount() {
        return sleepInfoList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tv_sleep_time;
        private ImageView iv_sleep_select;
        private RelativeLayout rl_audio_sleep_item;

        public ViewHolder(final View itemView) {
            super(itemView);
            this.tv_sleep_time = (TextView) itemView.findViewById(R.id.tv_sleep_time);
            this.iv_sleep_select = (ImageView) itemView.findViewById(R.id.iv_sleep_select);
            this.rl_audio_sleep_item = (RelativeLayout) itemView.findViewById(R.id.rl_audio_sleep_item);
        }
    }
}
