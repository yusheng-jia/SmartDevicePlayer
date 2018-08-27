package com.mantic.control.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mantic.control.R;
import com.mantic.control.activity.SoundPlayActivity;
import com.mantic.control.api.sound.MopidyRsSoundModalBean;
import com.mantic.control.utils.Glog;

import java.util.ArrayList;
import java.util.List;

/**
 * author: jayson
 * blog: http://blog.csdn.net/jia4525036
 * time: 2018/4/25.
 * desc:
 */

public class DubbingWorksAdapter extends PagerAdapter {
    private static final String TAG = "DubbingWorksAdapter";
    private List<MopidyRsSoundModalBean.Result> modalList = new ArrayList<MopidyRsSoundModalBean.Result>();
    private String worksTitle [] = {"手机通讯","服装卖场","家电卖场","商场超市","家居建材","美食餐饮","杂货小铺","黄金珠宝","地摊叫卖"};
    private String worksContent [] = {"手机电子产品促销","服装鞋帽促销","家用电器促销","美食新品促销","装饰建材促销","日用杂货促销","珠宝首饰优惠","超市限时折扣","走过路过不要错过"};
    private int workBackground [] = {R.drawable.shouji,R.drawable.fuzhuang,R.drawable.jiadian,R.drawable.shangchang,R.drawable.jiaju,R.drawable.meishi,
            R.drawable.zhahuo,R.drawable.huangjin,R.drawable.ditan};
    private Context mContext;
    @Override
    public int getCount() {
        return worksTitle.length;
    }

    public DubbingWorksAdapter(Context context){
        mContext = context;
    }
    @Override
    public Object instantiateItem(ViewGroup container, final int position) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.dubbing_works_item, container, false);
        LinearLayout itemView = (LinearLayout) view.findViewById(R.id.works_item);
        TextView titleView = (TextView)view.findViewById(R.id.work_title);
        TextView contentView = (TextView)view.findViewById(R.id.work_content);
        itemView.setBackgroundResource(workBackground[position]);
        if (modalList != null && modalList.size() != 0){
            MopidyRsSoundModalBean.Result result = modalList.get(position);
            titleView.setText(result.mantic_album_name);
            contentView.setText(result.mantic_album_more);
        }else {
            titleView.setText(worksTitle[position]);
            contentView.setText(worksContent[position]);
        }
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Glog.i(TAG,"Works click " + position);
                startSoundPlay(position);
            }
        });
        container.addView(view);
        return view;
    }

    private void startSoundPlay(int position) {
        MopidyRsSoundModalBean.Result soundModal = modalList.get(position);
        Intent intent = new Intent(mContext, SoundPlayActivity.class);
        intent.putExtra("modal",soundModal);
        mContext.startActivity(intent);
    }

    @Override
    public void destroyItem(ViewGroup view, int position, Object object) {
        view.removeView((View) object);
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

    @Override
    public float getPageWidth(int position) {
        return (float) 0.4;
    }

    public void setModalData(List<MopidyRsSoundModalBean.Result> resultList) {
        if (resultList!=null && resultList.size()!= 0 ){
            modalList = resultList;
            for (MopidyRsSoundModalBean.Result result :modalList){
                Glog.i("dubbing","result: " + result.toString());
            }
        }
    }
}
