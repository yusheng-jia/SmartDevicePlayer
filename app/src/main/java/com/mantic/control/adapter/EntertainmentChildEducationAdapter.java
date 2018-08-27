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
 * 儿童教育适配器
 */
public class EntertainmentChildEducationAdapter extends RecyclerView.Adapter<EntertainmentChildEducationAdapter.ViewHolder> {

    private Context mContext;
    private List<Integer> childEducationList = new ArrayList<Integer>();
    private List<String> childEducationStringList = new ArrayList<String>();
    private List<String> childEducationUriList = new ArrayList<String>();
    private WindowManager wm;
    private int width = 0;

    public interface OnEntertainmentChildEducationItemClickListener {
        void entertainmentChildEducationItemClick(String name, int pre_data_type, String uri);
    }

    private OnEntertainmentChildEducationItemClickListener onEntertainmentChildEducationItemClickListener;

    public void setOnEntertainmentChildEducationItemClickListener(OnEntertainmentChildEducationItemClickListener onEntertainmentChildEducationItemClickListener) {
        this.onEntertainmentChildEducationItemClickListener = onEntertainmentChildEducationItemClickListener;
    }

    public EntertainmentChildEducationAdapter(Context context) {
        super();
        this.mContext = context;
        wm = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        width = wm.getDefaultDisplay().getWidth();
        childEducationList.add(R.drawable.entertainment_child_education_popular_nursery_rhymes);
        childEducationList.add(R.drawable.entertainment_child_education_fairy_tales);
        childEducationList.add(R.drawable.entertainment_child_education_english);
        childEducationList.add(R.drawable.entertainment_child_education_chinese);
        childEducationStringList.add("热门儿歌");
        childEducationStringList.add("童话故事");
        childEducationStringList.add("英语");
        childEducationStringList.add("国学");
        childEducationUriList.add("qingting:ondemand:categories:1599:1365");
        childEducationUriList.add("qingting:ondemand:categories:1599:1366");
        childEducationUriList.add("qingting:ondemand:categories:1599:3350");
        childEducationUriList.add("qingting:ondemand:categories:1599:1373");
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(mContext).inflate(R.layout.entertainment_child_education_item, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        Integer integer = childEducationList.get(position);
        String string = childEducationStringList.get(position);
        holder.tv_entertainment_child_education.setText(string);
        ViewGroup.LayoutParams layoutParams = holder.iv_entertainment_child_education.getLayoutParams();
        layoutParams.width = (width - DensityUtils.dip2px(mContext, 29.3f)) / 2;
        holder.iv_entertainment_child_education.setImageResource(integer);

        holder.ll_entertainment_child_education.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (null != onEntertainmentChildEducationItemClickListener) {
                    onEntertainmentChildEducationItemClickListener.entertainmentChildEducationItemClick(childEducationStringList.get(position), 2, childEducationUriList.get(position));
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
        return childEducationList.size();
    }



    public class ViewHolder extends RecyclerView.ViewHolder {
        public LinearLayout ll_entertainment_child_education;
        public ImageView iv_entertainment_child_education;
        public TextView tv_entertainment_child_education;
        public ViewHolder(View itemView) {
            super(itemView);
            ll_entertainment_child_education = (LinearLayout) itemView.findViewById(R.id.ll_entertainment_child_education);
            iv_entertainment_child_education = (ImageView) itemView.findViewById(R.id.iv_entertainment_child_education);
            tv_entertainment_child_education = (TextView) itemView.findViewById(R.id.tv_entertainment_child_education);
        }
    }

}
