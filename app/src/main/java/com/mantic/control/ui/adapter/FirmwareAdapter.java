package com.mantic.control.ui.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.mantic.control.R;
import com.mantic.control.activity.FirmwareActivity;
import com.mantic.control.entiy.ManticDeviceInfo;
import com.mantic.control.utils.Glog;
import com.mantic.control.widget.CustomDialog;

import java.util.ArrayList;
import java.util.List;

/**
 * author: jayson
 * blog: http://blog.csdn.net/jia4525036
 * time: 2017/5/3.
 * desc:
 */
public class FirmwareAdapter extends RecyclerView.Adapter {
    private final static String TAG = "FirmwareActivity";
    private CustomDialog.Builder mBuilder;

    private Context mContext;
    private List<ManticDeviceInfo> manticDeviceInfos;

    public FirmwareAdapter(Context context) {
        mContext = context;
        if(manticDeviceInfos == null) {
            manticDeviceInfos = new ArrayList<>();
        }
    }

    public void refresh(List<ManticDeviceInfo> infolist) {
        manticDeviceInfos.clear();
        manticDeviceInfos.addAll(infolist);
        notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new FirmWareViewHolder(LayoutInflater.from(this.mContext).inflate(R.layout.firmware_item, parent, false), this.mContext);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof FirmWareViewHolder) {
            ((FirmWareViewHolder) holder).bindView(position);
        }
    }

    @Override
    public int getItemCount() {
        return manticDeviceInfos.size();
    }



    private class FirmWareViewHolder extends RecyclerView.ViewHolder{
        private TextView current_version;
        private TextView last_version;
        private TextView device_name;
        private TextView updating;
        private ProgressBar progressBar;
        private ListView innerListview;
        private Handler handler = new Handler();

        FirmWareViewHolder(View itemView, Context context) {
            super(itemView);
            current_version = (TextView) itemView.findViewById(R.id.current_version);
            last_version = (TextView) itemView.findViewById(R.id.last_version);
            device_name = (TextView) itemView.findViewById(R.id.device_name);
            updating = (TextView) itemView.findViewById(R.id.updating_firmware);
            progressBar = (ProgressBar) itemView.findViewById(R.id.firmware_progress);
            innerListview = (ListView) itemView.findViewById(R.id.list_inner);
        }

        @SuppressLint("SetTextI18n")
        void bindView(int position){
            device_name.setText(manticDeviceInfos.get(position).getDeviceName()+"("+FirmwareActivity.deviceOtaInfo.packageId+")");
            current_version.setText(mContext.getString(R.string.current_version) + manticDeviceInfos.get(position).getOldVersion());
            last_version.setText(mContext.getString(R.string.last_version) + manticDeviceInfos.get(position).getNewVersion());
            updating.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Glog.i("FirmwareActivity","Firmware updating  ... ready");
                    showUpdateDialog();
                }
            });

            List<String> info = new ArrayList<>();
            info.add("1.支持语音点歌语音点歌语音点歌");
            info.add("2.修复部分BUG");
            info.add("3.支持T卡播放");
            if (position == 0) {
                info.add("4.支持语音点歌");
            } else if (position == 1) {
                info.add("4.支持语音点歌");
                info.add("5.支持语音点歌");
            } else {
                info.add("4:支持语音点歌");
                info.add("5.支持语音点歌");
                info.add("6.支持语音点歌");
            }

            FirmwareInnerAdapter adapter = new FirmwareInnerAdapter(info, mContext);
            innerListview.setAdapter(adapter);
            //根据innerlistview的高度机损parentlistview item的高度

            setListViewHeightBasedOnChildren(innerListview);
        }

        private void showUpdateDialog(){
            CustomDialog.Builder mBuilder = new CustomDialog.Builder(mContext);
            mBuilder.setTitle(mContext.getString(R.string.ota_update_title));
            mBuilder.setMessage(mContext.getString(R.string.ota_update_content));
            mBuilder.setPositiveButton(mContext.getString(R.string.ok), new CustomDialog.Builder.DialogPositiveClickListener() {
                @Override
                public void onPositiveClick(final CustomDialog dialog) {
                    Glog.i(TAG,"Firmware updating  ... start ");
                    FirmwareActivity.updateOta();
                    setState(false);
                    handler.post(proRunnable);
                    dialog.dismiss();
                }
            });
            mBuilder.setNegativeButton(mContext.getString(R.string.cancel), new CustomDialog.Builder.DialogNegativeClickListener() {
                @Override
                public void onNegativeClick(CustomDialog dialog) {
                    dialog.dismiss();
                }
            });
            mBuilder.create().show();
        }

        private int progress = 0;
        private void updateProgress(){
            if (progress == 40) {//默认40秒完成
                Glog.i(TAG,"更新完成...");
                handler.removeCallbacks(proRunnable);
                ((FirmwareActivity)mContext).checkNewVersion();
                progressBar.setProgress(0);
                updating.setText(R.string.update);
                progress = 0;
            }
            updating.setText(R.string.updating);
            progressBar.setProgress(progress++);
        }

        Runnable proRunnable = new Runnable() {
            @Override
            public void run() {
                handler.postDelayed(this,1000);
                updateProgress();
            }
        };

        private void setState(boolean state){
            updating.setEnabled(state);
        }
    }

    private static void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            return;
        }
        int totalHeight = 0;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
    }
}
