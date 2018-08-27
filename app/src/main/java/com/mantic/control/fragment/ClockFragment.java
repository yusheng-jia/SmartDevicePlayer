package com.mantic.control.fragment;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.mantic.control.R;
import com.mantic.control.adapter.ClockAdapter;
import com.mantic.control.customclass.Clock;
import com.mantic.control.customclass.ClockDisplay;
import com.mantic.control.customclass.ClockSearch;
import com.mantic.control.utils.ClockUtil;
import com.mantic.control.utils.OkHttpEngine;
import com.mantic.control.utils.ResultCallback;
import com.mantic.control.utils.SharePreferenceUtil;
import com.mantic.control.widget.TitleBar;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import okhttp3.Request;
import okhttp3.Response;

public class ClockFragment extends BaseFragment implements TitleBar.OnButtonClickListener{
    public static final String baseUrl="https://api.coomaan.com";
    public static final String addUrl=baseUrl+"/maan/alarm/add";
    public static final String searchUrl=baseUrl+"/maan/alarm/search";
    public static final String deleteUrl=baseUrl+"/maan/alarm/delete";
    public static final String editUrl=baseUrl+"/maan/alarm/edit";

    private String userid,deviceid;
    public static final int REQUEST_CODE=12;
    public static final String TAG = "ClockFragment";
    private TitleBar titleBar;
    private RecyclerView recyclerView;
    public  List<ClockDisplay> clockList=new ArrayList<>();
    private Activity mActivity;
    private  ClockAdapter adapter;
    private SharedPreferences.Editor editor;
    private SharedPreferences preferences;
    private Gson gson;
    private long currentTime;
    private TextView remindTextview;
    @Override
    protected void initView(View view) {
        super.initView(view);
        titleBar=(TitleBar)view.findViewById(R.id.clock_add_titlebar);
        recyclerView=(RecyclerView)view.findViewById(R.id.clock_recycler);
        remindTextview=(TextView)view.findViewById(R.id.remind_view);

    }

    @Override
    protected void initData(Bundle arguments) {
        super.initData(arguments);
        userid= SharePreferenceUtil.getUserId(getContext());
        deviceid=SharePreferenceUtil.getDeviceId(getContext());
        //currentTime=new SimpleDateFormat("yyyy-MM-dd HH:mm").format(new Date());
        currentTime=new Date().getTime()/1000;
        mActivity=getActivity();
        titleBar.setOnButtonClickListener(this);
        LinearLayoutManager manager=new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(manager);
        postRequest(searchUrl,userid,deviceid,currentTime);
    }


    @Override
    public void onLeftClick() {
        getActivity().finish();
    }

    @Override
    protected int setLayoutResourceId() {
        return R.layout.clock_layout;
    }

    @Override
    public void onRightClick() {
        Bundle bundle=new Bundle();
        AddClockFragment addClockFragment=new AddClockFragment();
        addClockFragment.setTargetFragment(this,REQUEST_CODE);
       bundle.putInt("param1",-1);
       addClockFragment.setArguments(bundle);
        if(getActivity() instanceof ClockCallbacks){
            ((ClockCallbacks) getActivity()).pushFragment(addClockFragment,AddClockFragment.class.getName());
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        remindTextview.setVisibility(View.GONE);
        if (data==null){
            return;
        }
        switch(resultCode){
            case AddClockFragment.RESPONSE_CODE_ADD:

                postRequest(searchUrl,userid,deviceid,currentTime);

                break;
                case AddClockFragment.RESPONSE_CODE_ITEM:
                    postRequest(searchUrl,userid,deviceid,currentTime);
                    break;
        }


    }




    private void postRequest(String url,String userid,String deviceUuid,long startTime){

        String searchjson="{\"userid\":\""+userid+"\",\"deviceUuid\":\""+deviceUuid+"\",\"startTime\":"+startTime+"}";
        OkHttpEngine.getInstance(getContext()).postAsynHttp(searchjson, url, new ResultCallback() {
            @Override
            public void onResponse(Response response) throws IOException {
                String textString=response.body().string();
                Log.e(TAG, "onResponse: "+textString );
                ClockSearch clockSearch=ClockUtil.handleSearchResponse(textString);
                initAdapter(clockSearch.clockList);
            }

            @Override
            public void onError(Request request, Exception ex) {
                Toast.makeText(getContext(),"failed",Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void initAdapter(List<Clock> clocks){
        if (clocks.size()==0){
            remindTextview.setVisibility(View.VISIBLE);
        }
        adapter=new ClockAdapter(clocks,mActivity,this);
        recyclerView.setAdapter(adapter);
    }
}
