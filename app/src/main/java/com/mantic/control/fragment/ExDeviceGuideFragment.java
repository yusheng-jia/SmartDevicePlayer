package com.mantic.control.fragment;


import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mantic.control.R;
import com.mantic.control.activity.NetworkConfigActivity;
import com.mantic.control.utils.Glog;
import com.mantic.control.utils.NetworkUtils;
import com.mantic.control.utils.SharePreferenceUtil;
import com.mantic.control.widget.CustomDialog;

/**
 * author: wujiangxia
 * blog: http://blog.csdn.net/jia4525036
 * time: 2017/4/1.
 * desc:
 */
public class ExDeviceGuideFragment extends Fragment implements View.OnClickListener {
    private View view;
    private ImageView dotImage;
    private TextView dotText;
    private boolean canNext = true;
    private LinearLayout nextPair;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Glog.v("wujx", "ExDeviceGuideFragment initView");
        if (view == null) {
            view = inflater.inflate(R.layout.exdevice_guide_frag,container,false);
            ImageView llback = (ImageView) view.findViewById(R.id.toolbar_back);
            TextView tvNext = (TextView) view.findViewById(R.id.toolbar_next);
            TextView tvTitle = (TextView) view.findViewById(R.id.toolbar_title);
            ImageView guideImage = (ImageView) view.findViewById(R.id.network_pair_image);
            TextView guideText = (TextView) view.findViewById(R.id.network_pair_text);
            LinearLayout select_next = (LinearLayout) view.findViewById(R.id.select_dot_next);
            dotImage = (ImageView)view.findViewById(R.id.dot_image);
            dotText = (TextView)view.findViewById(R.id.dot_text);
            nextPair = (LinearLayout)view.findViewById(R.id.pair_network_next) ;
            nextPair.setOnClickListener(this);
            if (canNext){
                nextPair.setEnabled(true);
            }else {
                nextPair.setEnabled(false);
            }
            tvTitle.setText(R.string.config_network);
            llback.setVisibility(View.VISIBLE);
//            tvNext.setVisibility(View.VISIBLE);
            llback.setOnClickListener(this);
            tvNext.setOnClickListener(this);
            select_next.setOnClickListener(this);

            Glog.i("jys","Custom name: " + SharePreferenceUtil.getCustomName(getActivity()));
            if(SharePreferenceUtil.getCustomName(getActivity()).equals("pidianchong")){
                guideImage.setImageResource(R.drawable.network_pair_image_pdc);
                guideText.setText(R.string.exdevice_tip_new_pdc);
            }else if(SharePreferenceUtil.getCustomName(getActivity()).equals("longxin")){
                guideImage.setImageResource(R.drawable.network_pair_image_lx);
                guideText.setText(R.string.exdevice_tip_new_lx);
            }else if (SharePreferenceUtil.getCustomName(getActivity()).equals("yalanshi")){
                if (SharePreferenceUtil.getDeviceLc(getActivity()).equals("LD00-DSPK-100000")){ //零道LC
                    guideImage.setImageResource(R.drawable.network_pair_image_pdc);
                    guideText.setText(R.string.exdevice_tip_new_pdc);
                }else {
                    guideImage.setImageResource(R.drawable.network_pair_image_yls);
                    guideText.setText(R.string.exdevice_tip_new_yls);
                }
            }else if (SharePreferenceUtil.getCustomName(getActivity()).equals("xinbeng")){
                guideImage.setImageResource(R.drawable.network_pair_image_xb);
                guideText.setText(R.string.exdevice_tip_new_xb);
            }else if (SharePreferenceUtil.getCustomName(getActivity()).equals("suoai")){
                guideImage.setImageResource(R.drawable.network_pair_image_yls);
                guideText.setText(R.string.exdevice_tip_new_yls);
            }
            else {
                guideImage.setImageResource(R.drawable.network_pair_image);
                guideText.setText(R.string.exdevice_tip_new);
            }

        }

        return view;
    }



    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.toolbar_back:
                getActivity().finish();
                break;
//            case R.id.toolbar_next:
//                if (this.getActivity() instanceof FragmentEntrust) {
//                    if (!NetworkUtils.isWifiConnected(getActivity())){
//                        showConfigDialog();
//                    }
//                    ((FragmentEntrust) this.getActivity()).pushFragment(new BluetoothListFragment(),"f2");
//                }
//                break;
            case R.id.select_dot_next:
                if (canNext) {
                    dotImage.setImageDrawable(getResources().getDrawable(R.drawable.unselect_dot));
                    canNext = false;
                    nextPair.setEnabled(false);
                } else{
                    dotImage.setImageDrawable(getResources().getDrawable(R.drawable.select_dot));
                    canNext = true;
                    nextPair.setEnabled(true);
                }
                break;
            case R.id.pair_network_next:
                if (this.getActivity() instanceof FragmentEntrust) {
//                    if (!NetworkUtils.isWifiConnected(getActivity())){
//                        showConfigDialog();
//                    }
                    if (NetworkConfigActivity.bleNetwork){
                        ((FragmentEntrust) this.getActivity()).pushFragment(new WifiConnectUIFrament(),"send_wifi");
                    }else {
                        ((FragmentEntrust) this.getActivity()).pushFragment(new BluetoothListFragment(),"bt_list");
                    }
                }
                break;

        }
    }

    private void showConfigDialog() {
        CustomDialog.Builder mBuilder = new CustomDialog.Builder(getActivity());
        mBuilder.setTitle("提示");
        mBuilder.setMessage("打开网络来允许手机连接路由器");
        mBuilder.setPositiveButton("是", new CustomDialog.Builder.DialogPositiveClickListener() {
            @Override
            public void onPositiveClick(CustomDialog dialog) {
                startWifiSet();
                dialog.dismiss();
            }
        });
        mBuilder.setNegativeButton("手动输入", new CustomDialog.Builder.DialogNegativeClickListener() {
            @Override
            public void onNegativeClick(CustomDialog dialog) {

                dialog.dismiss();
            }
        });
        mBuilder.create().show();
    }

    private void startWifiSet(){
        Intent wifiSettingsIntent = new Intent("android.settings.WIFI_SETTINGS");
        startActivity(wifiSettingsIntent);
    }
}
