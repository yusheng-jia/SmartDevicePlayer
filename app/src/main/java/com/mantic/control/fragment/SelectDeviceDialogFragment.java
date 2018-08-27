package com.mantic.control.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import com.baidu.iot.sdk.HttpStatus;
import com.baidu.iot.sdk.IoTException;
import com.baidu.iot.sdk.IoTRequestListener;
import com.baidu.iot.sdk.model.DeviceOnlineStatus;
import com.baidu.iot.sdk.model.PageInfo;
import com.mantic.antservice.DeviceManager;
import com.mantic.control.R;
import com.mantic.control.activity.MainActivity;
import com.mantic.control.adapter.MusicDeviceSelectListAdapter;
import com.mantic.control.entiy.ManticDeviceInfo;
import com.mantic.control.utils.Glog;
import com.mantic.control.utils.ResourceUtils;
import com.mantic.control.utils.SharePreferenceUtil;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by root on 17-4-7.
 */
public class SelectDeviceDialogFragment extends DialogFragment implements MusicDeviceSelectListAdapter.OnItemClickLitener {
    public static final String TAG = "SelectDeviceDialogFragment";
    private Button select_device = null;
    private RecyclerView rv_select_device_list;
    private MusicDeviceSelectListAdapter mMusicDeviceSelectListAdapter;
    private ArrayList<ManticDeviceInfo> manticDeviceInfoList;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getDialog().setCanceledOnTouchOutside(true);
        View rootView = inflater.inflate(R.layout.fragment_select_device, container, false);
        initView(rootView);
        initData();
        //Do something
        // 设置宽度为屏宽、靠近屏幕底部。
        final Window window = getDialog().getWindow();
        //window.setBackgroundDrawableResource(android.R.color.transparent);
        //window.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#E6000000")));
        window.setBackgroundDrawableResource(R.color.topDialogBackgroundDark);
        window.getDecorView().setPadding(0, 0, 0, 0);
        WindowManager.LayoutParams wlp = window.getAttributes();
        wlp.gravity = Gravity.TOP;
        wlp.width = WindowManager.LayoutParams.MATCH_PARENT;
//        wlp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        wlp.height = ResourceUtils.dip2px(getActivity(),ResourceUtils.getXmlDef(getActivity(),R.dimen.top_dialog_height));
        Glog.i(TAG,"onCreateView wlp.height = "+wlp.height);
        window.setAttributes(wlp);
        window.setWindowAnimations(R.style.TopToBottomAnim);
        return rootView;
    }

    private void initView(View view) {
        select_device = (Button)view.findViewById(R.id.select_device);
        view.findViewById(R.id.select_device_setting_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        String deviceName = SharePreferenceUtil.getDeviceId(getContext());
        select_device.setText(TextUtils.isEmpty(deviceName)? SharePreferenceUtil.getDeviceId(getContext()) : deviceName);
        rv_select_device_list = (RecyclerView) view.findViewById(R.id.rv_select_device_list);
    }

    private void initData() {
        manticDeviceInfoList = new ArrayList<ManticDeviceInfo>();
//        manticDeviceInfoList = SharePreferenceUtil.loadArray(this.getActivity(),"ManticDeviceInfo");
        Glog.i(TAG,"manticDeviceInfoList"+manticDeviceInfoList);
        initOnlineStatus();


    }

    private void initOnlineStatus(){
        if (manticDeviceInfoList.size()!=0)
        for (int i =0; i< manticDeviceInfoList.size(); i++){
            ManticDeviceInfo info = manticDeviceInfoList.get(i);
            getDevicesOnlineStatus(info);

        }

        mMusicDeviceSelectListAdapter = new MusicDeviceSelectListAdapter(getContext(), manticDeviceInfoList);
        mMusicDeviceSelectListAdapter.setmOnItemClickLitener(this);
        rv_select_device_list.setAdapter(mMusicDeviceSelectListAdapter);
        rv_select_device_list.setLayoutManager(new LinearLayoutManager(getContext()));

    }

    private void getDevicesOnlineStatus(final ManticDeviceInfo manticDeviceInfo){
        DeviceManager.getInstance().deviceApi.getDevicesOnlineStatus(new String[]{manticDeviceInfo.getUuid()}, new IoTRequestListener<List<DeviceOnlineStatus>>() {
            @Override
            public void onSuccess(HttpStatus code, List<DeviceOnlineStatus> obj, PageInfo info) {
                List<DeviceOnlineStatus> onlineStatusList = obj;
                for (DeviceOnlineStatus onlineStatus : onlineStatusList) {
                    Glog.i(TAG, onlineStatus.getDeviceUuid() + " status=" + onlineStatus.getStatus());
                    if (onlineStatus.getStatus().equals("true")) {
                        manticDeviceInfo.setOnLine(true);
                        manticDeviceInfo.setBind(true);
                    } else {
                        manticDeviceInfo.setOnLine(false);
                        manticDeviceInfo.setBind(true);
                    }
                }
            }

            @Override
            public void onFailed(HttpStatus code) {
                Glog.i(TAG, "get device onlin status failed code=" + code);
                //setDeviceOnline(mDeviceUuid, DeviceInfo.DeviceStatus.OFFLINE.name());
            }

            @Override
            public void onError(IoTException error) {
                Glog.i(TAG, "get device onlin status failed error=" + error);
                //setDeviceOnline(mDeviceUuid, DeviceInfo.DeviceStatus.OFFLINE.name());
            }
        });
    }

    @Override
    public void onItemClick(View view, int position) {
        Toast.makeText(getContext(), "点击了" + manticDeviceInfoList.get(position).getDeviceName(), Toast.LENGTH_SHORT).show();
        for (int i = 0; i < manticDeviceInfoList.size(); i++) {
            if (i != position) {
                manticDeviceInfoList.get(i).setBind(false);
            } else {
                manticDeviceInfoList.get(i).setBind(true);
            }
        }
        mMusicDeviceSelectListAdapter.notifyDataSetChanged();
    }

    public static SelectDeviceDialogFragment showDialog(AppCompatActivity appCompatActivity) {
        FragmentManager fragmentManager = appCompatActivity.getSupportFragmentManager();
        SelectDeviceDialogFragment bottomDialogFragment =
                (SelectDeviceDialogFragment) fragmentManager.findFragmentByTag(TAG);
        if (null == bottomDialogFragment) {
            bottomDialogFragment = newInstance();
        }

        if (!appCompatActivity.isFinishing()
                && null != bottomDialogFragment
                && !bottomDialogFragment.isAdded()) {
            fragmentManager.beginTransaction()
                    .add(bottomDialogFragment, TAG)
                    .commitAllowingStateLoss();
        }

        return bottomDialogFragment;
    }

    private static SelectDeviceDialogFragment newInstance() {
        Bundle args = new Bundle();
        SelectDeviceDialogFragment fragment = new SelectDeviceDialogFragment();
        fragment.setArguments(args);
        return fragment;
    }
}
