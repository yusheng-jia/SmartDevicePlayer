package com.mantic.control.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mantic.control.R;
import com.mantic.control.data.DataFactory;
import com.mantic.control.entiy.MusicService;
import com.mantic.control.utils.AccountConstant;
import com.mantic.control.utils.OkHttpUtils;
import com.mantic.control.widget.CustomDialog;
import com.mantic.control.widget.TitleBar;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lin on 2017/6/6.
 */

public class MusicServiceDetailFragment extends BaseSlideFragment implements TitleBar.OnButtonClickListener, View.OnClickListener{
    private TitleBar tb_music_service_detail;
    private ImageView iv_music_service_detail_icon;
    private RelativeLayout rl_music_service_detail_operator;
    private TextView tv_music_service_name;
    private ImageView iv_music_service_detail_add;
    private RelativeLayout rl_music_service_added_success;
    private TextView tv_music_service_detail_desc;
    private MusicService musicService;
    private ArrayList<MusicService> musicServiceList;

    @Override
    protected void initData(Bundle arguments) {
        super.initData(arguments);
        int position = this.getArguments().getInt("position", -1);
        if (position == -1) {
            return;
        }
        musicServiceList = mDataFactory.getMusicServiceList();
        musicService = musicServiceList.get(position);
        tv_music_service_name.setText(musicService.getName());
        tv_music_service_detail_desc.setText("\t\t\t\t" + musicService.getIntroduction());
        iv_music_service_detail_icon.setImageBitmap(musicService.getIcon());
        if (musicService.getIsActive()) {
            iv_music_service_detail_add.setVisibility(View.GONE);
            rl_music_service_added_success.setVisibility(View.VISIBLE);
        } else {
            iv_music_service_detail_add.setVisibility(View.VISIBLE);
            rl_music_service_added_success.setVisibility(View.GONE);
        }
    }

    @Override
    protected int setLayoutResourceId() {
        return R.layout.fragment_music_service_detail;
    }

    @Override
    protected void initView(View view){
        super.initView(view);
        tb_music_service_detail = (TitleBar) view.findViewById(R.id.tb_music_service_detail);
        tb_music_service_detail.setOnButtonClickListener(this);
        iv_music_service_detail_icon = (ImageView) view.findViewById(R.id.iv_music_service_detail_icon);
        rl_music_service_detail_operator = (RelativeLayout) view.findViewById(R.id.rl_music_service_detail_operator);
        tv_music_service_name = (TextView) view.findViewById(R.id.tv_music_service_name);
        iv_music_service_detail_add = (ImageView) view.findViewById(R.id.iv_music_service_detail_add);
        rl_music_service_added_success = (RelativeLayout) view.findViewById(R.id.rl_music_service_added_success);
        tv_music_service_detail_desc = (TextView) view.findViewById(R.id.tv_music_service_detail_desc);
        rl_music_service_detail_operator.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_music_service_detail_operator:
                if (musicService.getIsActive()) {

                    CustomDialog.Builder mBuilder = new CustomDialog.Builder(mContext);
                    mBuilder.setTitle("提示");
                    mBuilder.setMessage(getString(R.string.sure_delete_music_service));
                    mBuilder.setPositiveButton("确定", new CustomDialog.Builder.DialogPositiveClickListener() {
                        @Override
                        public void onPositiveClick(CustomDialog dialog) {
                            //这里添加点击确定后的逻辑
                            iv_music_service_detail_add.setVisibility(View.VISIBLE);
                            rl_music_service_added_success.setVisibility(View.GONE);
                            mDataFactory.notifyBMyMusicServiceListChange(musicService, false, true);
                            dialog.dismiss();
                        }
                    });
                    mBuilder.setNegativeButton("取消", new CustomDialog.Builder.DialogNegativeClickListener() {
                        @Override
                        public void onNegativeClick(CustomDialog dialog) {
                            dialog.dismiss();
                        }
                    });
                    mBuilder.create().show();

                } else {
                    iv_music_service_detail_add.setVisibility(View.GONE);
                    rl_music_service_added_success.setVisibility(View.VISIBLE);
                    mDataFactory.notifyBMyMusicServiceListChange(musicService, true, true);
                }
                break;
        }
    }

    @Override
    public void onLeftClick() {
        if(getActivity() instanceof FragmentEntrust){
            ((FragmentEntrust)getActivity()).popFragment(getTag());
        }
    }

    @Override
    public void onRightClick() {

    }
}
