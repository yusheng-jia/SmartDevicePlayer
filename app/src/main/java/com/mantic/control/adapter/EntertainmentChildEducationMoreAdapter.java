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
 * 儿童更多适配器
 */
public class EntertainmentChildEducationMoreAdapter extends RecyclerView.Adapter<EntertainmentChildEducationMoreAdapter.ViewHolder> {

    private Context mContext;
    private List<Integer> childEducationMoreList = new ArrayList<Integer>();
    private List<String> childEducationMoreStringList = new ArrayList<String>();
    private List<String> childEducationMoreUriList = new ArrayList<String>();
    private WindowManager wm;
    private int width = 0;

    private EntertainmentChildEducationAdapter.OnEntertainmentChildEducationItemClickListener onEntertainmentChildEducationItemClickListener;

    public void setOnEntertainmentChildEducationItemClickListener(EntertainmentChildEducationAdapter.OnEntertainmentChildEducationItemClickListener onEntertainmentChildEducationItemClickListener) {
        this.onEntertainmentChildEducationItemClickListener = onEntertainmentChildEducationItemClickListener;
    }

    public EntertainmentChildEducationMoreAdapter(Context context) {
        super();
        this.mContext = context;
        wm = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        width = wm.getDefaultDisplay().getWidth();
        childEducationMoreList.add(R.drawable.entertainment_child_education_encyclopedias);
        childEducationMoreList.add(R.drawable.entertainment_child_education_picture_books);
        childEducationMoreList.add(R.drawable.entertainment_child_education_cartoon);
        childEducationMoreList.add(R.drawable.entertainment_child_education_education);
        childEducationMoreList.add(R.drawable.entertainment_child_education_anchor);
        childEducationMoreList.add(R.drawable.entertainment_child_education_fetal_ducation);
        childEducationMoreList.add(R.drawable.entertainment_child_education_oxford);
        childEducationMoreList.add(R.drawable.entertainment_child_education_chinese_forgein);
        childEducationMoreList.add(R.drawable.entertainment_child_education_child_rearing);
        childEducationMoreList.add(R.drawable.entertainment_child_education_novel);

        childEducationMoreStringList.add("百科");
        childEducationMoreStringList.add("绘本");
        childEducationMoreStringList.add("卡通");
        childEducationMoreStringList.add("教育");
        childEducationMoreStringList.add("主播");
        childEducationMoreStringList.add("胎教");
        childEducationMoreStringList.add("牛津");
        childEducationMoreStringList.add("中外经典");
        childEducationMoreStringList.add("育儿");
        childEducationMoreStringList.add("小说");

        childEducationMoreUriList.add("qingting:ondemand:categories:1599:1375");
        childEducationMoreUriList.add("qingting:ondemand:categories:1599:1367");
        childEducationMoreUriList.add("qingting:ondemand:categories:1599:1369");
        childEducationMoreUriList.add("qingting:ondemand:categories:1599:1370");
        childEducationMoreUriList.add("qingting:ondemand:categories:1599:1372");
        childEducationMoreUriList.add("qingting:ondemand:categories:1599:3349");
        childEducationMoreUriList.add("qingting:ondemand:categories:1599:3011");
        childEducationMoreUriList.add("qingting:ondemand:categories:1599:1420");
        childEducationMoreUriList.add("qingting:ondemand:categories:1599:1376");
        childEducationMoreUriList.add("qingting:ondemand:categories:1599:1421");
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(mContext).inflate(R.layout.entertainment_child_education_more_item, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        Integer integer = childEducationMoreList.get(position);
        final String string = childEducationMoreStringList.get(position);
        holder.iv_entertainment_child_education_more.setBackgroundResource(integer);
        holder.tv_entertainment_child_education_more.setText(string);
        ViewGroup.LayoutParams viewParams = holder.ll_entertainment_child_education_more_item.getLayoutParams();
        viewParams.width = (width - DensityUtils.dip2px(mContext, 29.3f))/ 2;
        holder.ll_entertainment_child_education_more_item.setPadding(DensityUtils.dip2px(mContext, 46), 0, 0, 0);
        holder.ll_entertainment_child_education_more_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (null != onEntertainmentChildEducationItemClickListener) {
                    onEntertainmentChildEducationItemClickListener.entertainmentChildEducationItemClick(string, 2, childEducationMoreUriList.get(position));
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
        return childEducationMoreList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private LinearLayout ll_entertainment_child_education_more_item;
        public ImageView iv_entertainment_child_education_more;
        public TextView tv_entertainment_child_education_more;
        public ViewHolder(View itemView) {
            super(itemView);
            ll_entertainment_child_education_more_item = (LinearLayout) itemView.findViewById(R.id.ll_entertainment_child_education_more_item);
            iv_entertainment_child_education_more = (ImageView) itemView.findViewById(R.id.iv_entertainment_child_education_more);
            tv_entertainment_child_education_more = (TextView) itemView.findViewById(R.id.tv_entertainment_child_education_more);
        }
    }

}
