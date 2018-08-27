package com.mantic.control.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.mantic.control.R;
import com.mantic.control.activity.MainActivity;
import com.mantic.control.utils.GlideImgManager;
import com.mantic.control.utils.TextUtil;

/**
 * Created by lin on 2017/6/6.
 */

public class ChannelDetailsDescribeFragment extends Fragment implements View.OnClickListener{
    private ImageView iv_channel_detail_cancel;
    private ImageView iv_channel_detail_icon;
    private TextView tv_channel_detail_desc;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_channel_detail_describe, container, false);
        initView(view);
        initData();
        return view;
    }


    private void initView(View view) {
        iv_channel_detail_cancel = (ImageView) view.findViewById(R.id.iv_channel_detail_cancel);
        iv_channel_detail_icon = (ImageView) view.findViewById(R.id.iv_channel_detail_icon);
        tv_channel_detail_desc = (TextView) view.findViewById(R.id.tv_channel_detail_desc);
        ((MainActivity)getActivity()).setAudioPlayerVisible(false);
    }

    private void initData() {
        String url = getArguments().getString("url", "");
        String desc = getArguments().getString("desc", "");
        if (!TextUtils.isEmpty(url)) {
            GlideImgManager.glideLoaderCircle(getContext(), url, R.drawable.fragment_channel_detail_cover,
                    R.drawable.fragment_channel_detail_cover, iv_channel_detail_icon);
        }

//        tv_channel_detail_desc.setText(TextUtil.ToDBC(desc));
        tv_channel_detail_desc.setText(desc);
        iv_channel_detail_cancel.setOnClickListener(this);
    }


    @Override
    public void onDestroy() {
        ((MainActivity)getActivity()).setAudioPlayerVisible(true);
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_channel_detail_cancel:
                if(getActivity() instanceof FragmentEntrust){
                    ((FragmentEntrust)getActivity()).popFragment(getTag());
                }
                break;
        }
    }
}
