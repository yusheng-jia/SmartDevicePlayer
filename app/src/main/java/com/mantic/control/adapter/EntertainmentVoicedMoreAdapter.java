package com.mantic.control.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mantic.control.R;
import com.mantic.control.utils.DensityUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 有声更多适配器
 */
public class EntertainmentVoicedMoreAdapter extends RecyclerView.Adapter<EntertainmentVoicedMoreAdapter.ViewHolder> {

    private Context mContext;
    private List<Integer> voicedMoreList = new ArrayList<Integer>();
    private List<String> voicedMoreStringList = new ArrayList<String>();
    private List<String> voicedMoreUriList = new ArrayList<String>();
    private WindowManager wm;
    private int width = 0;
    private boolean isShowMore = false;

    private EntertainmentVoicedAdapter.OnEntertainmentVoicedItemClickListener onEntertainmentVoicedItemClickListener;

    public void setOnEntertainmentVoicedItemClickListener(EntertainmentVoicedAdapter.OnEntertainmentVoicedItemClickListener onEntertainmentVoicedItemClickListener) {
        this.onEntertainmentVoicedItemClickListener = onEntertainmentVoicedItemClickListener;
    }

    public EntertainmentVoicedMoreAdapter(Context context) {
        super();
        this.mContext = context;
        wm = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        width = wm.getDefaultDisplay().getWidth();
        voicedMoreList.add(R.drawable.entertainment_voiced_emotion);
        voicedMoreList.add(R.drawable.entertainment_voiced_history);
        voicedMoreList.add(R.drawable.entertainment_voiced_health);
        voicedMoreList.add(R.drawable.entertainment_voiced_entertainment);
        voicedMoreList.add(R.drawable.entertainment_voiced_female);
        voicedMoreList.add(R.drawable.entertainment_voiced_car);
        voicedMoreList.add(R.drawable.entertainment_voiced_culture);
        voicedMoreList.add(R.drawable.entertainment_voiced_education);
        voicedMoreList.add(R.drawable.entertainment_voiced_storytelling);
        voicedMoreList.add(R.drawable.entertainment_voiced_opera);
        voicedMoreList.add(R.drawable.entertainment_voiced_finance);
        voicedMoreList.add(R.drawable.entertainment_voiced_technology);

        voicedMoreList.add(R.drawable.entertainment_voiced_movie);
//        voicedMoreList.add(R.drawable.entertainment_voiced_self_media);
        voicedMoreList.add(R.drawable.entertainment_voiced_campus);
        voicedMoreList.add(R.drawable.entertainment_voiced_sport);
        voicedMoreList.add(R.drawable.entertainment_voiced_game_anime);
        voicedMoreList.add(R.drawable.entertainment_voiced_radio_play);
        voicedMoreList.add(R.drawable.entertainment_voiced_tourism);
        voicedMoreList.add(R.drawable.entertainment_voiced_fashion);
        voicedMoreList.add(R.drawable.entertainment_voiced_open_class);
        voicedMoreList.add(R.drawable.entertainment_voiced_china_voice);

        voicedMoreStringList.add("情感");
        voicedMoreStringList.add("历史");
        voicedMoreStringList.add("健康");
        voicedMoreStringList.add("娱乐");
        voicedMoreStringList.add("女性");
        voicedMoreStringList.add("汽车");
        voicedMoreStringList.add("文化");
        voicedMoreStringList.add("教育");
        voicedMoreStringList.add("评书");
        voicedMoreStringList.add("戏曲");
        voicedMoreStringList.add("财经");
        voicedMoreStringList.add("科技");

        voicedMoreStringList.add("影视");
//        voicedMoreStringList.add("自媒体");
        voicedMoreStringList.add("校园");
        voicedMoreStringList.add("体育");
        voicedMoreStringList.add("游戏动漫");
        voicedMoreStringList.add("广播剧");
        voicedMoreStringList.add("旅游");
        voicedMoreStringList.add("时尚");
        voicedMoreStringList.add("公开课");
        voicedMoreStringList.add("中国之声");

        voicedMoreUriList.add("qingting:ondemand:529");
        voicedMoreUriList.add("qingting:ondemand:531");
        voicedMoreUriList.add("qingting:ondemand:539");
        voicedMoreUriList.add("qingting:ondemand:547");
        voicedMoreUriList.add("qingting:ondemand:3330");
        voicedMoreUriList.add("qingting:ondemand:3385");
        voicedMoreUriList.add("qingting:ondemand:3613");
        voicedMoreUriList.add("qingting:ondemand:537");
        voicedMoreUriList.add("qingting:ondemand:3496");
        voicedMoreUriList.add("qingting:ondemand:3276");
        voicedMoreUriList.add("qingting:ondemand:533");
        voicedMoreUriList.add("qingting:ondemand:535");
        voicedMoreUriList.add("qingting:ondemand:3588");
//        voicedMoreUriList.add("qingting:ondemand:3599");
        voicedMoreUriList.add("qingting:ondemand:1737");
        voicedMoreUriList.add("qingting:ondemand:3238");
        voicedMoreUriList.add("qingting:ondemand:3427");
        voicedMoreUriList.add("qingting:ondemand:3442");
        voicedMoreUriList.add("qingting:ondemand:3597");
        voicedMoreUriList.add("qingting:ondemand:3605");
        voicedMoreUriList.add("qingting:ondemand:1585");
        voicedMoreUriList.add("qingting:ondemand:3608");
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(mContext).inflate(R.layout.entertainment_voiced_more_item, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        Integer integer = voicedMoreList.get(position);
        String string = voicedMoreStringList.get(position);
        holder.iv_entertainment_voiced_more.setImageResource(integer);
        holder.tv_entertainment_voiced_more.setText(string);
        ViewGroup.LayoutParams viewParams = holder.ll_entertainment_voiced_more_item.getLayoutParams();
        viewParams.width = (width - DensityUtils.dip2px(mContext, 64))/ 2;

        if (!isShowMore) {
            if (position == 10 || position == 11) {
                holder.view_underline.setVisibility(View.GONE);
            } else {
                holder.view_underline.setVisibility(View.VISIBLE);
            }
        } else {
            if (position == voicedMoreList.size() - 2 || position == voicedMoreList.size() - 1) {
                holder.view_underline.setVisibility(View.GONE);
            } else {
                holder.view_underline.setVisibility(View.VISIBLE);
            }
        }

        holder.ll_entertainment_voiced_more_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (null != onEntertainmentVoicedItemClickListener) {
                    onEntertainmentVoicedItemClickListener.entertainmentVoicedItemClick(voicedMoreStringList.get(position), 1, voicedMoreUriList.get(position));
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
        if (isShowMore) {
            return voicedMoreList.size();
        } else {
            return voicedMoreList.size() > 12 ? 12 : voicedMoreList.size();
        }

    }

    public void setShowMore(boolean showMore) {
        isShowMore = showMore;
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private LinearLayout ll_entertainment_voiced_more_item;
        public ImageView iv_entertainment_voiced_more;
        public TextView tv_entertainment_voiced_more;
        public View view_underline;
        public ViewHolder(View itemView) {
            super(itemView);
            ll_entertainment_voiced_more_item = (LinearLayout) itemView.findViewById(R.id.ll_entertainment_voiced_more_item);
            iv_entertainment_voiced_more = (ImageView) itemView.findViewById(R.id.iv_entertainment_voiced_more);
            tv_entertainment_voiced_more = (TextView) itemView.findViewById(R.id.tv_entertainment_voiced_more);
            view_underline = itemView.findViewById(R.id.view_underline);
        }
    }

}
