package com.mantic.control.utils;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.Uri;

import java.util.ArrayList;
import java.util.List;

import static com.mantic.control.fragment.MakeSoundFragment.SOUND_DIR;

/**
 * author: jayson
 * blog: http://blog.csdn.net/jia4525036
 * time: 2018/5/15.
 * desc:
 */
public class DownloadManagerUtils {
    private Context context;
    List<Long> downloadIds;//记录所有下载任务id
    /** 下载结束监听 */
    private OnDownloadCompleted onDownloadCompleted;
    private DownloadCompleteReceiver downLoadCompleteReceiver;
    private  boolean register = false;

    public DownloadManagerUtils(Context mContext){
        context = mContext;
        getService();
        downLoadCompleteReceiver = new DownloadCompleteReceiver();
        downloadIds = new ArrayList<>();
    }

    DownloadManager downloadManager;
    private void getService() {
        String serviceString = Context.DOWNLOAD_SERVICE;
        downloadManager = (DownloadManager) context.getSystemService(serviceString);
    }

    /** * @param uil 下载地址
     * @param title 通知栏标题
     * @param description 描述
     * @return
     * */
    public long download(String uil, String fileName,String title, String description) {
        Uri uri = Uri.parse(uil);
        DownloadManager.Request request = new DownloadManager.Request(uri);//设置下载地址
        request.setTitle(title);//设置Notification的title信息
        request.setDescription(description);//设置Notification的message信息
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);//设置通知栏下载通知显示状态
        //下载到download文件夹下
        request.setDestinationInExternalPublicDir("/coomaan/", fileName);
        //只能在WiFi下进行下载
        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI);
        //reference变量是系统为当前的下载请求分配的一个唯一的ID，我们可以通过这个ID重新获得这个下载任务，进行一些自己想要进行的操作或者查询
        long id = downloadManager.enqueue(request);
        downloadIds.add(id);
        return id;
    }

    /**
     * 判断下载组件是否可用
     *
     * @param context
     * @return
     */
    private boolean canDownloadState(Context context) {
        try {
            int state = context.getPackageManager().getApplicationEnabledSetting("com.android.providers.downloads");
            if (state == PackageManager.COMPONENT_ENABLED_STATE_DISABLED
                    || state == PackageManager.COMPONENT_ENABLED_STATE_DISABLED_USER
                    || state == PackageManager.COMPONENT_ENABLED_STATE_DISABLED_UNTIL_USED) {
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * 启用下载组件
     *
     * @param context
     */
    private void enableDowaload(Context context) {
        String packageName = "com.android.providers.downloads";
        Intent intent = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.setData(Uri.parse("package:" + packageName));
        context.startActivity(intent);
    }

    /**
     * 下载完成监听
     */
    public interface OnDownloadCompleted {
        public void onDownloadCompleted(long completeDownloadId);
    }

    /**
     * 注册广播并注册监听接口
     *
     * @param onDownloadCompleted
     */
    public void registerReceiver(OnDownloadCompleted onDownloadCompleted) {
        context.registerReceiver(downLoadCompleteReceiver,
                new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
        this.onDownloadCompleted = onDownloadCompleted;
        register = true;
    }

    /**
     * 解除注册广播
     */
    public void unregisterReceiver() {
        context.unregisterReceiver(downLoadCompleteReceiver);
        register = false;
    }

    public boolean hasRegister(){
        return register;
    }

    /**
     * 下载完成监听
     */
    public class DownloadCompleteReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            long completeDownloadId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);//获取下载完成任务的id
            if (onDownloadCompleted != null) {
                onDownloadCompleted.onDownloadCompleted(completeDownloadId);//调用下载完成接口方法
            }
            //下载完成后删除id
            for (int i = 0; i < downloadIds.size(); i++) {
                if (completeDownloadId == downloadIds.get(i)) {
                    downloadIds.remove(i);
                }
            }
        }
    }
}
