package com.mantic.control.adapter;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mantic.control.R;
import com.mantic.control.utils.DensityUtils;
import com.mantic.control.widget.RecyclerTabLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lin on 2018/1/17.
 */

public class FmRadioAdapter extends RecyclerTabLayout.Adapter<FmRadioAdapter.FmRadioHolder> {

    protected List<String> fmRadioList = new ArrayList<>();
    private Context mContext;
    private WindowManager wm;
    private int width = 0;

    public FmRadioAdapter(ViewPager viewPager, Context context) {
        super(viewPager);
        fmRadioList.add("地区");
        fmRadioList.add("国家台");
        fmRadioList.add("网络台");
        fmRadioList.add("分类");
        mContext = context;
        wm = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        width = wm.getDefaultDisplay().getWidth();
    }

    public void setfmRadioList(List<String> fmRadioList) {
        this.fmRadioList = fmRadioList;
    }

    @Override
    public void onBindViewHolder(FmRadioHolder holder, final int position) {
        String rankName = fmRadioList.get(position);
        holder.tv_fm_radio_name.setText(rankName);
        ViewGroup.LayoutParams layoutParams = holder.rl_fm_radio_item.getLayoutParams();
        layoutParams.width = (int)((width - DensityUtils.dip2px(mContext, 41.9f)) / 4.0);

        holder.rl_fm_radio_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getViewPager().setCurrentItem(position);
            }
        });

        if (position == getCurrentIndicatorPosition()) {
            holder.tv_fm_radio_name.setTextColor(mContext.getResources().getColor(R.color.rank_select));
            holder.rl_fm_radio_item.setBackgroundResource(R.drawable.child_education_item_selected_background);
        } else {
            holder.tv_fm_radio_name.setTextColor(mContext.getResources().getColor(R.color.rank_normal));
            holder.rl_fm_radio_item.setBackgroundResource(R.drawable.child_education_item_background);
        }
    }

    @Override
    public FmRadioHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new FmRadioHolder(LayoutInflater.from(mContext).inflate(R.layout.fm_radio_item, parent, false));
    }

    @Override
    public int getItemCount() {
        return fmRadioList.size();
    }

    public class FmRadioHolder extends RecyclerView.ViewHolder {
        private RelativeLayout rl_fm_radio_item;
        private TextView tv_fm_radio_name;

        public FmRadioHolder(View itemView) {
            super(itemView);
            tv_fm_radio_name = (TextView) itemView.findViewById(R.id.tv_fm_radio_name);
            rl_fm_radio_item = (RelativeLayout) itemView.findViewById(R.id.rl_fm_radio_item);
        }
    }
}
