package com.mantic.control.adapter;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.mantic.control.R;
import com.mantic.control.fragment.MusicServiceSubItemFragment;
import com.mantic.control.fragment.MusicServiceSubItemLazyLoadFragment;
import com.mantic.control.musicservice.MyMusicService;
import com.mantic.control.utils.DensityUtils;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;

/**
 * 有声适配器
 */
public class EntertainmentVoicedAdapter extends RecyclerView.Adapter<EntertainmentVoicedAdapter.ViewHolder> {

    private Context mContext;
    private List<Integer> voicedList = new ArrayList<Integer>();
    private List<String> voicedStringList = new ArrayList<String>();
    private List<String> voicedUriList = new ArrayList<String>();
    private WindowManager wm;
    private int width = 0;

    public interface OnEntertainmentVoicedItemClickListener {
        void entertainmentVoicedItemClick(String name, int pre_data_type, String uri);
    }

    private OnEntertainmentVoicedItemClickListener onEntertainmentVoicedItemClickListener;

    public void setOnEntertainmentVoicedItemClickListener(OnEntertainmentVoicedItemClickListener onEntertainmentVoicedItemClickListener) {
        this.onEntertainmentVoicedItemClickListener = onEntertainmentVoicedItemClickListener;
    }

    public EntertainmentVoicedAdapter(Context context) {
        super();
        this.mContext = context;
        wm = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        width = wm.getDefaultDisplay().getWidth();
        voicedList.add(R.drawable.entertainment_voiced_anchor);
        voicedList.add(R.drawable.entertainment_voiced_novel);
        voicedList.add(R.drawable.entertainment_voiced_talkshow);
        voicedList.add(R.drawable.entertainment_voiced_headlines);
        voicedList.add(R.drawable.entertainment_voiced_crosstalk);
        voicedList.add(R.drawable.entertainment_voiced_funny);
        voicedUriList.add("qingting:podcasters");
        voicedUriList.add("qingting:ondemand:521");
        voicedUriList.add("qingting:ondemand:3251");
        voicedUriList.add("qingting:ondemand:545");
        voicedUriList.add("qingting:ondemand:527");
        voicedUriList.add("qingting:ondemand:3252");
        voicedStringList.add("主播");
        voicedStringList.add("小说");
        voicedStringList.add("脱口秀");
        voicedStringList.add("头条");
        voicedStringList.add("相声小品");
        voicedStringList.add("搞笑");
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(mContext).inflate(R.layout.entertainment_voiced_item, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        Integer integer = voicedList.get(position);
        ViewGroup.LayoutParams layoutParams = holder.iv_entertainment_voiced.getLayoutParams();
        layoutParams.width = (width - DensityUtils.dip2px(mContext, 32.6f)) / 3;
        layoutParams.height = (int)(layoutParams.width * 0.7);
        holder.iv_entertainment_voiced.setLayoutParams(layoutParams);
        holder.iv_entertainment_voiced.setImageResource(integer);
        holder.iv_entertainment_voiced.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (null != onEntertainmentVoicedItemClickListener) {
                    if (position == 0) {
                        onEntertainmentVoicedItemClickListener.entertainmentVoicedItemClick(voicedStringList.get(position), 0, voicedUriList.get(position));
                    } else {
                        onEntertainmentVoicedItemClickListener.entertainmentVoicedItemClick(voicedStringList.get(position), 1, voicedUriList.get(position));
                    }

                }
            }
        });
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    @Override
    public int getItemCount() {
        return voicedList.size() > 6 ? 6 : voicedList.size();
    }


    public void setVoicedList(List<Integer> voicedList) {
        this.voicedList = voicedList;
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView iv_entertainment_voiced;
        public ViewHolder(View itemView) {
            super(itemView);
            iv_entertainment_voiced = (ImageView) itemView.findViewById(R.id.iv_entertainment_voiced);
        }
    }

}
