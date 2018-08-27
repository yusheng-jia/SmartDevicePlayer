package com.mantic.control.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.mantic.control.R;
import com.mantic.control.customclass.Clock;
import com.mantic.control.customclass.ClockDisplay;
import com.mantic.control.fragment.AddClockFragment;
import com.mantic.control.fragment.ChannelManagementFragment;
import com.mantic.control.fragment.ClockCallbacks;
import com.mantic.control.fragment.ClockFragment;
import com.mantic.control.utils.OkHttpEngine;
import com.mantic.control.utils.ResultCallback;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import okhttp3.Request;
import okhttp3.Response;

public class ClockAdapter extends RecyclerView.Adapter<ClockAdapter.ViewHolder>{
    private Context mContext;
    private Activity mActivity;
    private List<Clock> clockDisplayList;
    private Fragment fragments;
    private SimpleDateFormat mSimpleDateFormat;
    private static final String TAG="ClockAdapter";

   public ClockAdapter(List<Clock> displays, Activity activity, Fragment fragment){
        clockDisplayList=displays;
        mActivity=activity;
        fragments=fragment;
   }


    class ViewHolder extends RecyclerView.ViewHolder{
       private TextView dataString;
       private TextView hintString;
       private Switch aSwitch;
       private LinearLayout linearLayout;
       private View clockitemView;
        public ViewHolder(View view){
            super(view);
            dataString=(TextView) view.findViewById(R.id.data_clock);
            hintString=(TextView)view.findViewById(R.id.hint_clock);
            aSwitch=(Switch)view.findViewById(R.id.switch_view);
            linearLayout=(LinearLayout)view.findViewById(R.id.clock_layout);
            clockitemView=(View)view.findViewById(R.id.clock_item_view);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
       if (mContext==null){
           mContext=parent.getContext();
       }
       View view= LayoutInflater.from(mContext).inflate(R.layout.clock_item,parent,false);
       mSimpleDateFormat=new SimpleDateFormat("yyyy-MM-dd HH:mm");
           initClockDisplayList();
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final Clock clockDisplay=clockDisplayList.get(position);
        final String mStartTime=mSimpleDateFormat.format(clockDisplay.startTime*1000);
        if (clockDisplay.closed==1){
            holder.aSwitch.setChecked(false);
        }
        if (clockDisplay.period.isEmpty()){
            holder.dataString.setText(mStartTime.split(" ")[1]);
            holder.hintString.setText(tomorrowortoday(holder.aSwitch,clockDisplay.startTime));
        }else{
            int minute=(int)clockDisplay.startTime%3600/60;
            int hour=(int)(clockDisplay.startTime/3600);
            holder.dataString.setText(time2string(hour,minute));
            if (clockDisplay.period.contains("week")){
                holder.hintString.setText(list2string(clockDisplay.integerList));
            }
            if (clockDisplay.period.contains("day")){
                holder.hintString.setText("每天");
            }

        }
        if (position==clockDisplayList.size()-1){
            holder.clockitemView.setVisibility(View.INVISIBLE);
        }

        if (!holder.aSwitch.isChecked()){
            holder.dataString.setTextColor(Color.parseColor("#9f9393"));
            holder.hintString.setTextColor(Color.parseColor("#969f9393"));


        }

        holder.aSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (!isChecked) {
                    holder.dataString.setTextColor(Color.parseColor("#9f9393"));
                    holder.hintString.setTextColor(Color.parseColor("#969f9393"));
                    editRequest(ClockFragment.editUrl,clockDisplay,1);
                    if (clockDisplay.period.isEmpty()){
                        holder.hintString.setText(tomorrowortoday(holder.aSwitch,clockDisplay.startTime));
                    }

                }else {
                    holder.dataString.setTextColor(Color.parseColor("#000000"));
                    holder.hintString.setTextColor(Color.parseColor("#9f9393"));
                    editRequest(ClockFragment.editUrl,clockDisplay,0);
                    if (clockDisplay.period.isEmpty()){
                        holder.hintString.setText(tomorrowortoday(holder.aSwitch,clockDisplay.startTime));
                    }

                }
            }
        });
        holder.linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddClockFragment addClockFragment=new AddClockFragment();
                addClockFragment.setTargetFragment(fragments,ClockFragment.REQUEST_CODE);
                Bundle argments=new Bundle();
                argments.putInt("param1",position);
                if (clockDisplay.period.isEmpty()){
                    argments.putString("param2",mStartTime.split(" ")[1]);
                }else{
                    argments.putString("param2",time2string((int)(clockDisplay.startTime/3600),(int)clockDisplay.startTime%3600/60));
                }

                argments.putString("param3",clockDisplay.uuid);
                argments.putString("param4",holder.hintString.getText().toString().replaceAll(" ",""));
                argments.putString("param5",clockDisplay.playListUri);
                addClockFragment.setArguments(argments);

                if(mActivity instanceof ClockCallbacks){
                    ((ClockCallbacks) mActivity).pushFragment(addClockFragment,AddClockFragment.class.getName());
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return clockDisplayList.size();
    }

    private void initClockDisplayList(){
        Collections.sort(clockDisplayList,new SortByTime());
    }

    class SortByTime implements Comparator {
        @Override
        public int compare(Object o1, Object o2) {
            String str1="";
            String str2="";
            Clock s1=(Clock)o1;
            Clock s2=(Clock)o2;
            if (s1.period.isEmpty()){
                str1=second2Date(s1.startTime).split(" ")[1];
            }else {
                str1=time2string((int)s1.startTime/3600,(int)s1.startTime%3600/60);
            }
            if (s2.period.isEmpty()){
                str2=second2Date(s2.startTime).split(" ")[1];
            }else {
                str2=time2string((int)s2.startTime/3600,(int)s2.startTime%3600/60);
            }
           if(str1.compareTo(str2)>=0){
               return 1;
            }else {
                return -1;
            }

        }
    }
    private String tomorrowortoday(Switch switchbutton,long startTime){
        //Date date=new Date();
        long date=new Date().getTime()/1000;
        date=startTime-date;
        int hour=(int)date/3600;
        int minute=(int)date%3600/60;
        if (switchbutton.isChecked()){
            return "仅一次 "+hour+"小时"+minute+"分钟后响铃";
        }
        else {
            return "仅一次";
        }

    }


    private void editRequest(String url,Clock clock,int closed){

       String searchjson="{\"uuid\":\""+clock.uuid+"\",\"userid\":\""+clock.userid+"\",\"deviceUuid\":\""+clock.deviceUuid+"\",\"closed\":"+closed+"}";

       OkHttpEngine.getInstance(mContext).postAsynHttp(searchjson, url, new ResultCallback() {
            @Override
            public void onResponse(Response response) throws IOException {
            }

            @Override
            public void onError(Request request, Exception ex) {
                Toast.makeText(mContext,"failed",Toast.LENGTH_SHORT).show();
            }
        });
    }
    private String second2Date(long startTime){
       return mSimpleDateFormat.format(startTime*1000);
    }
    private String time2string(int hour,int minute){
       String minuteString="";
       String hourString="";
       if(minute<10){
           minuteString="0"+minute;
       }else{
           minuteString=""+minute;
       }
       if (hour<10){
           hourString="0"+hour;
       }else {
           hourString=""+hour;
       }
       return hourString+":"+minuteString;
    }
    private String list2string(List<Integer> integerList){
       String weekString="";
       boolean one=false,two=false,three=false,four=false,five=false,six=false,seven=false;
        for (Integer i:integerList) {
            if(i==1){
                weekString+="周一 ";
                one=true;
            }
            if (i==2){
                weekString+="周二 ";
                two=true;
            }
            if (i==3){
                weekString+="周三 ";
                three=true;
            }
            if (i==4){
                weekString+="周四 ";
                four=true;
            }
            if (i==5){
                weekString+="周五 ";
                five=true;
            }
            if (i==6){
                weekString+="周六 ";
                six=true;
            }
            if (i==7){
                weekString+="周日 ";
                seven=true;
            }
        }
        if (integerList.size()==5&&one&&two&&three&&four&&five){
            weekString="工作日";
        }
        if (integerList.size()==2&&six&&seven){
            weekString="周末";
        }
        return weekString;
    }
}
