package com.mantic.control.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.aigestudio.wheelpicker.WheelPicker;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.zxing.common.StringUtils;
import com.mantic.control.R;
import com.mantic.control.adapter.ClockRingBellAdapter;
import com.mantic.control.api.beiwa.bean.BwWordsBean;
import com.mantic.control.customclass.ClockAdd;
import com.mantic.control.customclass.ClockDisplay;
import com.mantic.control.customclass.ClockSearch;
import com.mantic.control.data.MyChannel;
import com.mantic.control.utils.ClockUtil;
import com.mantic.control.utils.GlideImgManager;
import com.mantic.control.utils.OkHttpEngine;
import com.mantic.control.utils.ResultCallback;
import com.mantic.control.utils.SharePreferenceUtil;
import com.mantic.control.widget.TitleBar;

import java.io.IOException;
import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import io.reactivex.internal.operators.flowable.FlowableElementAtSingle;
import okhttp3.Request;
import okhttp3.Response;

public class AddClockFragment extends BaseFragment implements TitleBar.OnButtonClickListener,View.OnClickListener,CompoundButton.
        OnCheckedChangeListener,View.OnTouchListener{
    private static final String TAG = "AddClockFragment";
    public static final int RESPONSE_CODE_ITEM = 0x010;
    public static final int RESPONSE_CODE_ADD = 0x011;
    public static final int REQUEST_CODE_ADD = 0x015;
    private TitleBar titleBar;
    private TextView one,two,three,four,five,six,seven;
    private TextView deleteButton;
    private WheelPicker hourpicker,minutepicker;
    private Switch aSwitch,repeatSwitch;
    private String hour,minute;
    private boolean isfromItem=false;
    private boolean isfromAdd=false;
    private boolean isEveryday=false;
    private boolean isWorkday=false;
    private boolean isRestday=false;
    private boolean isRepeat=false;
    private boolean isCallback=false;
    private int position;
    private String hourString="8";
    private String minuteString="0";
    private String[] dateString;
    private String clockStyle;
    private String mychannelString;
    private List<MyChannel> channels=new ArrayList<>();
    private  List<Integer> integers=new ArrayList<>();
    private Gson gson;
    private SharedPreferences.Editor editor;
    private SharedPreferences preferences;
    private String userid,deviceid,uuid,playListUri;
    private RelativeLayout rl_channel_manager;
    private ImageButton imageButton;
    private ImageView imageIcon;
    private TextView titleview;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getArguments().containsKey("param1")){
            position=getArguments().getInt("param1");
            if(position!=-1){
                isfromItem=true;
            }else{
                isfromAdd=true;
            }
        }
        if (getArguments().containsKey("param2")){
            String str=getArguments().getString("param2");
            String[] ss=str.split(":");
            hourString=ss[0];
            minuteString=ss[1];
        }
        if(getArguments().containsKey("param3")){
            uuid=getArguments().getString("param3");

        }
        if (getArguments().containsKey("param4")){
            clockStyle=getArguments().getString("param4").trim();
            if (clockStyle.contains("仅一次")){
                isRepeat=false;
            }else {
                isRepeat=true;
                if (clockStyle.contains("每")){
                    isEveryday=true;
                }
                if (clockStyle.contains("工作日")){
                    isWorkday=true;
                }
                if (clockStyle.contains("周末")){
                    isRestday=true;
                }else {
                    dateString=clockStyle.split("周");
                }
            }
        }
        if (getArguments().containsKey("param5")){
            playListUri=getArguments().getString("param5");
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initWheel();
    }

    @Override
    protected int setLayoutResourceId() {
        return R.layout.addclock_layout;
    }



    @Override
    protected void initView(View view) {
        super.initView(view);
        view.setOnTouchListener(this);
        titleBar=(TitleBar)view.findViewById(R.id.clock_save_titlebar);
        titleBar.setOnButtonClickListener(this);
        one=(TextView)view.findViewById(R.id.one);
        one.setOnClickListener(this);
        two=(TextView)view.findViewById(R.id.two);
        two.setOnClickListener(this);
        three=(TextView)view.findViewById(R.id.three);
        three.setOnClickListener(this);
        four=(TextView)view.findViewById(R.id.four);
        four.setOnClickListener(this);
        five=(TextView)view.findViewById(R.id.five);
        five.setOnClickListener(this);
        six=(TextView)view.findViewById(R.id.six);
        six.setOnClickListener(this);
        seven=(TextView)view.findViewById(R.id.seven);
        seven.setOnClickListener(this);
        deleteButton=(TextView) view.findViewById(R.id.delete_button);
        deleteButton.setOnClickListener(this);


        hourpicker=(WheelPicker)view.findViewById(R.id.hour_wheelpicker);
        minutepicker=(WheelPicker)view.findViewById(R.id.minute_wheelpicker);

        aSwitch=(Switch)view.findViewById(R.id.open_bell);
        aSwitch.setOnCheckedChangeListener(this);


        repeatSwitch=(Switch)view.findViewById(R.id.repeat_switch);
        repeatSwitch.setOnCheckedChangeListener(this);
        rl_channel_manager=(RelativeLayout)view.findViewById(R.id.rl_channel_management) ;
        rl_channel_manager.setOnClickListener(this);
        imageButton=(ImageButton)view.findViewById(R.id.channel_del_btn);
        imageIcon=(ImageView)view.findViewById(R.id.channel_icon);
        titleview=(TextView)view.findViewById(R.id.channel_title);
    }

    @Override
    protected void initData(Bundle arguments) {
        super.initData(arguments);
        userid=SharePreferenceUtil.getUserId(getContext());
        deviceid=SharePreferenceUtil.getDeviceId(getContext());
        preferences=PreferenceManager.getDefaultSharedPreferences(getContext());
        editor= PreferenceManager.getDefaultSharedPreferences(getContext()).edit();
        if (isfromAdd){
            mychannelString=preferences.getString("channels",null);

        }else {
            mychannelString=preferences.getString(playListUri,null);
            if (mychannelString==null){
                mychannelString=preferences.getString("channels",null);
            }else {
                aSwitch.setChecked(true);
            }

        }
        if(mychannelString!=null){
            String[] channelInfo=mychannelString.split(" ");

            GlideImgManager.glideLoaderCircle(mContext,channelInfo[0], R.drawable.fragment_channel_detail_cover,
                    R.drawable.fragment_channel_detail_cover, imageIcon);
            titleview.setText(channelInfo[1]);
        }
        else {
            rl_channel_manager.setVisibility(View.INVISIBLE);
        }



        if (isRepeat){
            repeatSwitch.setChecked(true);
        }
        if(!repeatSwitch.isChecked()){
          setDateClickable(false);
          setRepeatSwitch(false);
        }else {
            setDateClickable(true);
        }
        if (!aSwitch.isChecked()){
            rl_channel_manager.setClickable(false);
            imageIcon.setAlpha(0.5f);
            titleview.setTextColor(Color.parseColor("#3a000000"));
            imageButton.setAlpha(0.5f);
        }else {
            titleview.setTextColor(Color.parseColor("#8a000000"));
            imageIcon.setAlpha(1f);
            imageButton.setAlpha(0.5f);
        }
        if (isRepeat){
            initDateView();
        }
        if(isfromAdd){
            deleteButton.setVisibility(View.GONE);
        }






    }

    private void initWheel(){

        hourpicker.setData(createHours());
        hourpicker.setVisibleItemCount(3);
        hourpicker.setItemTextSize(100);
        hourpicker.setSelectedItemPosition(Integer.parseInt(hourString));


        minutepicker.setData(createMinutes());
        minutepicker.setVisibleItemCount(3);
        minutepicker.setItemTextSize(100);
        minutepicker.setSelectedItemPosition(Integer.parseInt(minuteString));
    }
    @Override
    public void onLeftClick() {
        if (getActivity() instanceof ClockCallbacks) {
            ((ClockCallbacks) getActivity()).popFragment(getTag());
        }
    }

    @Override
    public void onRightClick() {
        long timeDate;
        String time;
        hour=number2string(hourpicker.getCurrentItemPosition());
        minute=number2string(minutepicker.getCurrentItemPosition());
        time=hour+":"+minute;
        timeDate=getDate(time);
        if (isfromAdd){
            if (aSwitch.isChecked()){
                playListUri=preferences.getString("channels",null).split(" ")[2];
                postRequest(ClockFragment.addUrl,userid,uuid,deviceid,timeDate,playListUri);
            }else {
                postRequest(ClockFragment.addUrl,userid,uuid,deviceid,timeDate);
            }

        }else if(isfromItem){
            if (aSwitch.isChecked()){
                if (isCallback){
                    playListUri=preferences.getString("channels",null).split(" ")[2];
                }else if (playListUri.isEmpty()){
                    playListUri=mychannelString.split(" ")[2];
                }
                postRequest(ClockFragment.editUrl,userid,uuid,deviceid,timeDate,playListUri);
            }else {
                postRequest(ClockFragment.editUrl,userid,uuid,deviceid,timeDate,"");
            }

        }




        if(getActivity() instanceof ClockCallbacks){
            ((ClockCallbacks) getActivity()).popFragment(getTag());
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.one:
                initTextView(one);

                break;
            case R.id.two:
                initTextView(two);

                break;
            case R.id.three:
                initTextView(three);

                break;
            case R.id.four:
                initTextView(four);

                break;
            case R.id.five:
                initTextView(five);

                break;
            case R.id.six:
                initTextView(six);

                break;
            case R.id.seven:
                initTextView(seven);

                break;
            case R.id.delete_button:
               postRequest(ClockFragment.deleteUrl,userid,uuid,deviceid,0);
                if(getActivity() instanceof ClockCallbacks){
                    ((ClockCallbacks) getActivity()).popFragment(getTag());
                }
                break;
            case R.id.rl_channel_management:
                onclickCallback();
                break;
            default:
                break;
        }
    }

    private ArrayList<String> createHours(){
        ArrayList<String> list=new ArrayList<>();
        for(int i=0;i<24;i++){
            if(i<10){
                list.add("0"+i);
            }else {
                list.add(""+i);
            }
        }
        return list;
    }

    private ArrayList<String> createMinutes(){
        ArrayList<String> list=new ArrayList<>();
        for(int i=0;i<60;i++){
            if(i<10){
                list.add("0"+i);
            }else {
                list.add(""+i);
            }
        }
        return list;
    }
    private void initTextView(TextView view){
        if (view.isSelected()){
            view.setSelected(false);
            view.setTextColor(Color.parseColor("#8A000000"));
        }else {
            view.setSelected(true);
            view.setTextColor(Color.WHITE);
        }
    }


    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()){
            case R.id.open_bell:
                if(isChecked){
                    if (mychannelString==null){
                        AlertDialog.Builder dialog=new AlertDialog.Builder(getContext());
                        dialog.setMessage("你还没有选择频道，现在去选择");
                        dialog.setCancelable(false);
                        dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Bundle bundle=new Bundle();
                                bundle.putString("addchannel","choosechannel");
                                ChannelManagementFragment channelManagementFragment = new ChannelManagementFragment();
                                channelManagementFragment.setTargetFragment(AddClockFragment.this,AddClockFragment.REQUEST_CODE_ADD);
                                channelManagementFragment.setArguments(bundle);
                                if(getActivity() instanceof ClockCallbacks){
                                    ((ClockCallbacks) getActivity()).pushFragment(channelManagementFragment,ChannelManagementFragment.class.getName());
                                }
                                dialog.dismiss();
                            }

                        });
                        dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                aSwitch.setChecked(false);
                            }
                        });
                        dialog.show();
                    }else {
                        //To-do
                       rl_channel_manager.setClickable(true);
                       imageIcon.setAlpha(1f);
                       titleview.setTextColor(Color.parseColor("#8a000000"));
                       imageButton.setAlpha(1f);
                    }
                   /* AlertDialog.Builder dialog=new AlertDialog.Builder(getContext());
                    dialog.setMessage("当前不可用");
                    dialog.setCancelable(false);
                    dialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            aSwitch.setChecked(false);
                        }
                    });
                    dialog.show();*/

                    }else{
                  rl_channel_manager.setClickable(false);
                  imageIcon.setAlpha(0.5f);
                    titleview.setTextColor(Color.parseColor("#3a000000"));
                    imageButton.setAlpha(0.5f);
                }

                break;
            case R.id.repeat_switch:
                setRepeatSwitch(isChecked);
                setDateClickable(isChecked);
               /* if(isChecked){
                   *//* AlertDialog.Builder dialog=new AlertDialog.Builder(getContext());
                    dialog.setMessage("当前不可用");
                    dialog.setCancelable(false);
                    dialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                       dialog.dismiss();
                       repeatSwitch.setChecked(false);
                        }
                    });
                    dialog.show();*//*
                   setDateClickable(true);
                   Toast.makeText(getContext(),String.valueOf(isDateChoose[0]),Toast.LENGTH_SHORT).show();

                }else {
                   setDateClickable(false);
                    setRepeatSwitch(false);
                }*/
                break;
        }
    }




    private void backResult(long startTime) {

        if (getTargetFragment() == null) {
            return;
        }
        Intent intent = new Intent();
        intent.putExtra("clocktime", startTime);
        if (isfromAdd){
            getTargetFragment().onActivityResult(ClockFragment.REQUEST_CODE, RESPONSE_CODE_ADD, intent);
        }
        if(isfromItem){
            getTargetFragment().onActivityResult(ClockFragment.REQUEST_CODE, RESPONSE_CODE_ITEM, intent);
        }

    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        return true;
    }
    private void setDateClickable(boolean isClickable){
        one.setClickable(isClickable);
        two.setClickable(isClickable);
        three.setClickable(isClickable);
        four.setClickable(isClickable);
        five.setClickable(isClickable);
        six.setClickable(isClickable);
        seven.setClickable(isClickable);
        if (!isClickable){
            if (one.isSelected()){
                one.setTextColor(Color.parseColor("#b4ffffff"));
            }else {
                one.setTextColor(Color.parseColor("#3A000000"));
            }
            if (two.isSelected()){
                two.setTextColor(Color.parseColor("#b4ffffff"));
            }else {
                two.setTextColor(Color.parseColor("#3A000000"));
            }
            if (three.isSelected()){
                three.setTextColor(Color.parseColor("#b4ffffff"));
            }else {
                three.setTextColor(Color.parseColor("#3A000000"));
            }
            if (four.isSelected()){
                four.setTextColor(Color.parseColor("#b4ffffff"));
            }else {
                four.setTextColor(Color.parseColor("#3A000000"));
            }
          if (five.isSelected()){
                five.setTextColor(Color.parseColor("#b4ffffff"));
          }else {
              five.setTextColor(Color.parseColor("#3A000000"));
          }
          if (six.isSelected()){
                six.setTextColor(Color.parseColor("#b4ffffff"));
          }else {
              six.setTextColor(Color.parseColor("#3A000000"));
          }
         if (seven.isSelected()){
                seven.setTextColor(Color.parseColor("#b4ffffff"));
         }else {
             seven.setTextColor(Color.parseColor("#3A000000"));
         }

        }else{
            if (one.isSelected()){
                one.setTextColor(Color.WHITE);
            }else {
                one.setTextColor(Color.parseColor("#8A000000"));
            }
            if (two.isSelected()){
                two.setTextColor(Color.WHITE);
            }else {
                two.setTextColor(Color.parseColor("#8A000000"));
            }
           if (three.isSelected()){
                three.setTextColor(Color.WHITE);
           }else{
               three.setTextColor(Color.parseColor("#8A000000"));
           }
          if (four.isSelected()){
                four.setTextColor(Color.WHITE);
          }else{
              four.setTextColor(Color.parseColor("#8A000000"));
          }
         if (five.isSelected()){
                five.setTextColor(Color.WHITE);
         }else {
             five.setTextColor(Color.parseColor("#8A000000"));
         }
          if (six.isSelected()){
                six.setTextColor(Color.WHITE);
          }else {
              six.setTextColor(Color.parseColor("#8A000000"));
          }
         if (seven.isSelected()){
                seven.setTextColor(Color.WHITE);
         }else {
             seven.setTextColor(Color.parseColor("#8A000000"));
         }

        }
    }

    private void initWeekday(){
        for(int i=1;i<dateString.length;i++){
            if(dateString[i].contains("一")){
                one.setSelected(true);
                one.setTextColor(Color.WHITE);

            }
            if (dateString[i].contains("二")){
                two.setSelected(true);
                two.setTextColor(Color.WHITE);

            }
            if (dateString[i].contains("三")){
                three.setSelected(true);
                three.setTextColor(Color.WHITE);

            }
            if (dateString[i].contains("四")){
                four.setSelected(true);
                four.setTextColor(Color.WHITE);


            }
            if(dateString[i].contains("五")){
                five.setSelected(true);
                five.setTextColor(Color.WHITE);


            }
            if (dateString[i].contains("六")){
                six.setSelected(true);
                six.setTextColor(Color.WHITE);


            }
            if (dateString[i].contains("日")){
                seven.setSelected(true);
                seven.setTextColor(Color.WHITE);


            }
        }
    }

    private void initEveryday(){
        if (!isRestday){
            one.setSelected(true);
            one.setTextColor(Color.WHITE);
            two.setSelected(true);
            two.setTextColor(Color.WHITE);
            three.setSelected(true);
            three.setTextColor(Color.WHITE);
            four.setSelected(true);
            four.setTextColor(Color.WHITE);
            five.setSelected(true);
            five.setTextColor(Color.WHITE);
        }
       if (!isWorkday){
           six.setSelected(true);
           six.setTextColor(Color.WHITE);
           seven.setSelected(true);
           seven.setTextColor(Color.WHITE);
       }

    }

    private void initDateView(){
        if (isEveryday||isWorkday||isRestday){
            initEveryday();
        }else {
            initWeekday();
        }
    }



    private void setRepeatSwitch(boolean isSelected){
      if (isSelected){
          one.setBackground(getResources().getDrawable(R.drawable.date_background));
          two.setBackground(getResources().getDrawable(R.drawable.date_background));
          three.setBackground(getResources().getDrawable(R.drawable.date_background));
          four.setBackground(getResources().getDrawable(R.drawable.date_background));
          five.setBackground(getResources().getDrawable(R.drawable.date_background));
          six.setBackground(getResources().getDrawable(R.drawable.date_background));
          seven.setBackground(getResources().getDrawable(R.drawable.date_background));
      }else {
          one.setBackground(getResources().getDrawable(R.drawable.data_background_choose));
          two.setBackground(getResources().getDrawable(R.drawable.data_background_choose));
          three.setBackground(getResources().getDrawable(R.drawable.data_background_choose));
          four.setBackground(getResources().getDrawable(R.drawable.data_background_choose));
          five.setBackground(getResources().getDrawable(R.drawable.data_background_choose));
          six.setBackground(getResources().getDrawable(R.drawable.data_background_choose));
          seven.setBackground(getResources().getDrawable(R.drawable.data_background_choose));
      }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        isCallback=true;
        rl_channel_manager.setVisibility(View.VISIBLE);
        String imageurl=data.getStringExtra("imageUrl");
        String title=data.getStringExtra("title");
        String channelUrl=data.getStringExtra("channelUrl");
        MyChannel channel=new MyChannel();
        channel.setChannelName(title);
        channel.setUrl(channelUrl);
        channel.setChannelCoverUrl(imageurl);
        if (channels.size()==0){
            channels.add(0,channel);
        }else {
            channels.set(0,channel);
        }
        GlideImgManager.glideLoaderCircle(mContext,imageurl, R.drawable.fragment_channel_detail_cover,
                R.drawable.fragment_channel_detail_cover, imageIcon);
        titleview.setText(title);
        editor.putString("channels",imageurl+" "+title+" "+channelUrl);
        editor.putString(channelUrl,imageurl+" "+title);
        editor.commit();

    }


    public void   onclickCallback() {
        Bundle bundle=new Bundle();
        bundle.putString("addchannel","choosechannel "+titleview.getText().toString());
        ChannelManagementFragment channelManagementFragment = new ChannelManagementFragment();
        channelManagementFragment.setTargetFragment(this, AddClockFragment.REQUEST_CODE_ADD);
        channelManagementFragment.setArguments(bundle);
        if(mActivity instanceof ClockCallbacks){
            ((ClockCallbacks) mActivity).pushFragment(channelManagementFragment,ChannelManagementFragment.class.getName());
        }
    }

   private long getDate(String time){
        Date date=new Date();
        SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyy-MM-dd HH:mm");
        String currentTime=simpleDateFormat.format(date);
        long startTime=-1;
        if (time.compareTo(currentTime.split(" ")[1])>=0){
            currentTime= currentTime.split(" ")[0]+" "+time;
        }else {
            Calendar calendar=Calendar.getInstance();
            calendar.setTime(date);
            calendar.add(Calendar.DAY_OF_MONTH,+1);
            date=calendar.getTime();
            currentTime=simpleDateFormat.format(date);
            currentTime= currentTime.split(" ")[0]+" "+time;
        }
        try{
            startTime=simpleDateFormat.parse(currentTime).getTime()/1000;
        }catch (ParseException ex){
            ex.printStackTrace();
        }
       return startTime;
   }

    public void postRequest(final String url, String userid, final String uuid, String deviceUuid, final long startTime){
        String searchjson="";
        String days= repeatDays();;
        if (url.contains("/maan/alarm/delete")){
            searchjson="{\"uuid\":\""+uuid+"\",\"deviceUuid\":\""+deviceUuid+"\"}";
        }
        if (url.contains("/maan/alarm/add")){
            if (repeatSwitch.isChecked()&&integers.size()>0){

                long startime=time2circle(startTime);
                if (isEveryday){
                    searchjson="{\"userid\":\""+userid+"\",\"deviceUuid\":\""+deviceUuid+"\",\"startTime\":"+startime+",\"endTime\":"+startime+",\"period\":\"day\"}";
                }else {
                    searchjson="{\"userid\":\""+userid+"\",\"deviceUuid\":\""+deviceUuid+"\",\"startTime\":"+startime+",\"endTime\":"+startime+",\"days\":"+days+",\"period\":\"week\"}";
                }

            }else {
                searchjson="{\"userid\":\""+userid+"\",\"deviceUuid\":\""+deviceUuid+"\",\"startTime\":"+startTime+",\"endTime\":"+startTime+"}";
            }
        }
        if (url.contains("/maan/alarm/edit")){
            if (repeatSwitch.isChecked()&&integers.size()>0){
                long startime=time2circle(startTime);
                if (isEveryday){
                    searchjson="{\"uuid\":\""+uuid+"\",\"userid\":\""+userid+"\",\"deviceUuid\":\""+deviceUuid+"\",\"startTime\":"+startime+",\"endTime\":"+startime+",\"period\":\"day\",\"closed\":0}";
                }else {
                    searchjson="{\"uuid\":\""+uuid+"\",\"userid\":\""+userid+"\",\"deviceUuid\":\""+deviceUuid+"\",\"startTime\":"+startime+",\"endTime\":"+startime+",\"days\":"+days+",\"period\":\"week\",\"closed\":0}";
                }

            }else {
                searchjson="{\"uuid\":\""+uuid+"\",\"userid\":\""+userid+"\",\"deviceUuid\":\""+deviceUuid+"\",\"startTime\":"+startTime+",\"endTime\":"+startTime+",\"period\":\"\",\"closed\":0}";
            }

        }

        OkHttpEngine.getInstance(getContext()).postAsynHttp(searchjson, url, new ResultCallback() {
            @Override
            public void onResponse(Response response) throws IOException {

                    String text=response.body().string();
                    Log.e(TAG, "onResponse: "+text );


                backResult(startTime);

            }

            @Override
            public void onError(Request request, Exception ex) {
                Toast.makeText(getContext(),"failed",Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void postRequest(final String url, String userid, final String uuid, String deviceUuid, final long startTime,String playListUri){
        String searchjson="";
        String days= repeatDays();;
        if (url.contains("/maan/alarm/delete")){
            searchjson="{\"uuid\":\""+uuid+"\",\"deviceUuid\":\""+deviceUuid+"\"}";
        }
        if (url.contains("/maan/alarm/add")){
            if (repeatSwitch.isChecked()&&integers.size()>0){

                long startime=time2circle(startTime);
                if (isEveryday){
                    searchjson="{\"userid\":\""+userid+"\",\"deviceUuid\":\""+deviceUuid+"\",\"startTime\":"+startime+",\"endTime\":"+startime+",\"period\":\"day\",\"playListUri\":\""+playListUri+"\"}";
                }else {
                    searchjson="{\"userid\":\""+userid+"\",\"deviceUuid\":\""+deviceUuid+"\",\"startTime\":"+startime+",\"endTime\":"+startime+",\"days\":"+days+",\"period\":\"week\",\"playListUri\":\""+playListUri+"\"}";
                }

            }else {
                searchjson="{\"userid\":\""+userid+"\",\"deviceUuid\":\""+deviceUuid+"\",\"startTime\":"+startTime+",\"endTime\":"+startTime+",\"playListUri\":\""+playListUri+"\"}";
            }
        }
        if (url.contains("/maan/alarm/edit")){
            if (repeatSwitch.isChecked()&&integers.size()>0){
                long startime=time2circle(startTime);
                if (isEveryday){
                    searchjson="{\"uuid\":\""+uuid+"\",\"userid\":\""+userid+"\",\"deviceUuid\":\""+deviceUuid+"\",\"startTime\":"+startime+",\"endTime\":"+startime+",\"period\":\"day\",\"closed\":0,\"playListUri\":\""+playListUri+"\"}";
                }else {
                    searchjson="{\"uuid\":\""+uuid+"\",\"userid\":\""+userid+"\",\"deviceUuid\":\""+deviceUuid+"\",\"startTime\":"+startime+",\"endTime\":"+startime+",\"days\":"+days+",\"period\":\"week\",\"closed\":0,\"playListUri\":\""+playListUri+"\"}";
                }

            }else {
                searchjson="{\"uuid\":\""+uuid+"\",\"userid\":\""+userid+"\",\"deviceUuid\":\""+deviceUuid+"\",\"startTime\":"+startTime+",\"endTime\":"+startTime+",\"period\":\"\",\"closed\":0,\"playListUri\":\""+playListUri+"\"}";
            }

        }

        OkHttpEngine.getInstance(getContext()).postAsynHttp(searchjson, url, new ResultCallback() {
            @Override
            public void onResponse(Response response) throws IOException {

                String text=response.body().string();
                Log.e(TAG, "onResponse: "+text );


                backResult(startTime);

            }

            @Override
            public void onError(Request request, Exception ex) {
                Toast.makeText(getContext(),"failed",Toast.LENGTH_SHORT).show();
            }
        });
    }

    private String repeatDays(){
        integers.clear();
        String days;
        if(one.isSelected()){
            integers.add(1);
        }
        if (two.isSelected()){
            integers.add(2);
        }
        if (three.isSelected()){
            integers.add(3);
        }
        if (four.isSelected()){
            integers.add(4);
        }
        if (five.isSelected()){
            integers.add(5);
        }
        if (six.isSelected()){
            integers.add(6);
        }
        if (seven.isSelected()){
            integers.add(7);
        }
        if (integers.size()==7){
            isEveryday=true;
        }else{
            isEveryday=false;
        }
       days=TextUtils.join(",",integers);
        return "["+days+"]";
    }
    private long time2circle(long time){
        int hour,minute;
        SimpleDateFormat dateFormat=new SimpleDateFormat("yyyy-MM-dd HH:mm");
       String str=dateFormat.format(time*1000).split(" ")[1];
        hour=Integer.parseInt(str.split(":")[0]);
        minute=Integer.parseInt(str.split(":")[1]);

        return hour*60*60+minute*60;
    }
    private String number2string(int number){
        String numberString="";
        if (number<10){
            numberString="0"+number;
        }else {
            numberString=String.valueOf(number);
        }
        return numberString;
    }
}
