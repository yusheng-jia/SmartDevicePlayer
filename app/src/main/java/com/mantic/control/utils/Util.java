package com.mantic.control.utils;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.text.TextUtils;
import android.widget.ImageView;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.mantic.antservice.DeviceManager;
import com.mantic.control.R;
import com.mantic.control.activity.DeviceDetailActivity;
import com.mantic.control.activity.MainActivity;
import com.mantic.control.api.mopidy.MopidyTools;
import com.mantic.control.api.mopidy.bean.MopidyRsTrackBean;
import com.mantic.control.api.mychannel.bean.AddParams;
import com.mantic.control.api.mychannel.bean.AddPlayList;
import com.mantic.control.api.mychannel.bean.AddTrack;
import com.mantic.control.api.mychannel.bean.ClearParams;
import com.mantic.control.api.mychannel.bean.DetailParams;
import com.mantic.control.api.mychannel.bean.MyChannelAddRqBean;
import com.mantic.control.api.mychannel.bean.MyChannelClearRqBean;
import com.mantic.control.api.mychannel.bean.MyChannelDetailRqBean;
import com.mantic.control.api.mychannel.bean.MyChannelSaveRqBean;
import com.mantic.control.api.mychannel.bean.MyChannelUpdateRqBean;
import com.mantic.control.api.mychannel.bean.SaveParams;
import com.mantic.control.api.mychannel.bean.SavePlayList;
import com.mantic.control.api.mychannel.bean.UpdateParams;
import com.mantic.control.api.sound.MySoundAddBean;
import com.mantic.control.api.sound.SoundTrack;
import com.mantic.control.cache.ACacheUtil;
import com.mantic.control.data.Channel;
import com.mantic.control.data.DataFactory;
import com.mantic.control.data.MyChannel;
import com.mantic.control.listener.BaiduUnbindRequest;
import com.mantic.control.listener.DeviceStateController;
import com.mantic.control.widget.CustomDialog;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import okhttp3.RequestBody;
import retrofit2.Call;

/**
 * Created by lin on 2017/6/1.
 */

public class Util {


    public static void setCircleDrawable(Context mContext, int resource, ImageView imageView) {
        Bitmap bmp = BitmapFactory.decodeResource(mContext.getResources(), resource);
        RoundedBitmapDrawable circularBitmapDrawable =
                RoundedBitmapDrawableFactory.create(mContext.getResources(), bmp);
        circularBitmapDrawable.setCircular(true);
        imageView.setImageDrawable(circularBitmapDrawable);
        bmp.recycle();
        bmp = null;
    }

    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm == null) {
        } else {
            //如果仅仅是用来判断网络连接
            //则可以使用 cm.getActiveNetworkInfo().isAvailable();
            NetworkInfo[] info = cm.getAllNetworkInfo();
            if (info != null) {
                for (int i = 0; i < info.length; i++) {
                    if (info[i].getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * 验证手机格式
     */
    public static boolean isMobileNO(String mobiles) {
        /*
        移动：134、135、136、137、138、139、150、151、157(TD)、158、159、187、188
        联通：130、131、132、152、155、156、185、186
        电信：133、153、180、189、（1349卫通）
        总结起来就是第一位必定为1，第二位必定为3或5或8，其他位置的可以为0-9
        */
        String telRegex = "[1]\\d{10}";//"[1]"代表第1位为数字1，"[358]"代表第二位可以为3、5、8中的一个，"\\d{9}"代表后面是可以是0～9的数字，有9位。

        if (TextUtils.isEmpty(mobiles)) {
            return false;
        } else {
            return mobiles.matches(telRegex);
        }
    }


    /** 生成随机数 len 长度 */
    public static String randomString(int len) {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789"; /** A-Z a-z 0-9 */
        int maxPos = chars.length();
        String pwd = "";
        for (int i = 0; i < len; i++) {
            pwd += chars.charAt((int)(Math.floor(Math.random() * maxPos)));
        }
        return pwd;
    }

    public static String getSecurityAppKey(String nonce, String access_token) {
        return encryptToSHA(nonce + "d3S3SbItdlYDj4KaOB1qIfuM" +
                "221b497771f29a469236d77cf5f376de" + access_token);
    }


    private static String encryptToSHA(String info) {
        byte[] digesta = null;
        try {
            MessageDigest alga = MessageDigest.getInstance("SHA-1");
            alga.update(info.getBytes());
            digesta = alga.digest();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        String rs = byte2hex(digesta);
        return rs;
    }

    private static String byte2hex(byte[] b) {
        String hs = "";
        String stmp = "";
        for (int n = 0; n < b.length; n++) {
            stmp = (Integer.toHexString(b[n] & 0XFF));
            if (stmp.length() == 1) {
                hs = hs + "0" + stmp;
            } else {
                hs = hs + stmp;
            }
        }
        return hs;
    }

    /**
     * 从asset路径下读取对应文件转String输出
     * @param mContext
     * @return
     */
    public static String getJson(Context mContext, String fileName) {
        // TODO Auto-generated method stub
        StringBuilder sb = new StringBuilder();
        AssetManager am = mContext.getAssets();
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(
                    am.open(fileName)));
            String next = "";
            while (null != (next = br.readLine())) {
                sb.append(next);
            }
            br.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            sb.delete(0, sb.length());
        }
        return sb.toString().trim();
    }


    public static boolean isExistMyChannelName(String name, String url, ArrayList<MyChannel> channels) {
        int count = channels.size();
        List<String> nameList = new ArrayList<String>();
        for (int i = 0; i < count; i++) {
            if (!(channels.get(i).getUrl()).equals(url)) {
                nameList.add(channels.get(i).getChannelName());
            }
        }

        if (nameList.contains(name)) {
            return true;
        }

        return false;
    }

    public static String getEdit_new_channel_name(Context context, ArrayList<MyChannel> mDefinitionChannelList) {
        int count = mDefinitionChannelList.size();
        List<String> nameList = new ArrayList<String>();
        for (int i = 0; i < count; i++) {
            nameList.add(mDefinitionChannelList.get(i).getChannelName());
        }

        if (count == 0) {
            return context.getString(R.string.create_new_channel) + 1;
        } else {
            for (int i = 0; i < count; i++) {
                if (!nameList.contains(context.getString(R.string.create_new_channel) + (i + 1))) {
                    return context.getString(R.string.create_new_channel) + (i + 1);
                }
            }
        }

        return context.getString(R.string.create_new_channel) + (count + 1);
    }


    public static ArrayList<Channel> getRecentPlay(Context mContext, DataFactory mDataFactory) {
        ArrayList<Channel> channels = new ArrayList<>();
//        String recentPlayListStr = ACacheUtil.getData(mContext, "recentPlayList");
        String recentPlayListStr = SharePreferenceUtil.getSharePreferenceData(mContext, "Mantic", "recentPlayList");
        if (!TextUtils.isEmpty(recentPlayListStr)) {
            channels = (ArrayList<Channel>) GsonUtil.stringToChannelList(recentPlayListStr);

            for (int i = 0; i < channels.size(); i++) {
                Channel channel = channels.get(i);
                if (null == mDataFactory.getCurrChannel()) {
                    channel.setPlayState(Channel.PLAY_STATE_STOP);
                } else {
                    if (channel.getUri().equals(mDataFactory.getCurrChannel().getUri())
                            && channel.getName().equals(mDataFactory.getCurrChannel().getName())) {
                        channel.setPlayState(mDataFactory.getCurrChannel().getPlayState());
                    } else {
                        channel.setPlayState(Channel.PLAY_STATE_STOP);
                    }
                }

                channels.set(i, channel);
            }
            mDataFactory.setRecentPlayList(channels);
        }

        return channels;
    }

    public static boolean hasRecentPlayData(Context mContext) {
        String recentPlayListStr = SharePreferenceUtil.getSharePreferenceData(mContext, "Mantic", "recentPlayList");
        if (!TextUtils.isEmpty(recentPlayListStr)) {
            ArrayList<Channel> channels = (ArrayList<Channel>) GsonUtil.stringToChannelList(recentPlayListStr);
            if (channels != null && channels.size() > 0) {
                return true;
            }
            return false;
        }

        return false;
    }


    public static ArrayList<String> getSearchHistoryList(Context mContext, DataFactory mDataFactory) {
        ArrayList<String> searchHistoryList = new ArrayList<>();
        String searchHistoryListStr = SharePreferenceUtil.getSharePreferenceData(mContext, "Mantic", "searchHistoryList");
        if (!TextUtils.isEmpty(searchHistoryListStr)) {
            searchHistoryList = (ArrayList<String>) GsonUtil.stringToSearchList(searchHistoryListStr);
            mDataFactory.setSearchHistoryList(searchHistoryList);
        }

        return searchHistoryList;
    }

    public static boolean hasSearchHistoryData(Context mContext) {
        String searchHistoryListStr = SharePreferenceUtil.getSharePreferenceData(mContext, "Mantic", "searchHistoryList");
        if (!TextUtils.isEmpty(searchHistoryListStr)) {
            ArrayList<String> searchList = (ArrayList<String>) GsonUtil.stringToSearchList(searchHistoryListStr);
            if (searchList != null && searchList.size() > 0) {
                return true;
            }
            return false;
        }

        return false;
    }


    public static boolean isHaveNavigationBar(Context context) {

        boolean isHave = false;
        Resources rs = context.getResources();
        int id = rs.getIdentifier("config_showNavigationBar", "bool", "android");
        if (id > 0) {
            return rs.getBoolean(id);
        }
        try {
            Class systemPropertiesClass = Class.forName("android.os.SystemProperties");
            Method m = systemPropertiesClass.getMethod("get", String.class);
            String navBarOverride = (String) m.invoke(systemPropertiesClass, "qemu.hw.mainkeys");
            if ("1".equals(navBarOverride)) {
                isHave = false;
            } else if ("0".equals(navBarOverride)) {
                isHave = true;
            }
        } catch (Exception e) {
            Glog.w("TAG", e.getMessage());
        }


        return isHave;
    }

    public static boolean beingListTypeIsRadio(DataFactory mDataFactory, Context mContext) {
        if (null != mDataFactory.getBeingPlayList() && mDataFactory.getBeingPlayList().size() > 0) {
            try {
                String uri = mDataFactory.getBeingPlayList().get(0).getUri();
                int index = uri.indexOf(":");
                String interceptUri = uri.substring(index + 1);
                if (interceptUri.startsWith("radio")) {
                    return true;
                }
                if (uri.contains("radio:")) {
                    return true;
                }
            } catch (Exception e) {
                return false;
            }
            return false;
        }

        return false;
    }

    public static RequestBody createAddRqBean(MyChannel myChannel, Context context) {
        MyChannelAddRqBean addBean = new MyChannelAddRqBean();
        addBean.setMethod("core.playlists.mantic_create_direct");
        addBean.setDevice_id(SharePreferenceUtil.getUserId(context));
        addBean.setJsonrpc("2.0");
        addBean.setId(1);
        AddParams params = new AddParams();
        params.setUri_scheme("mongodb");
        AddPlayList playlist = new AddPlayList();
        playlist.set__model__("Playlist");
        playlist.setMantic_album_uri(myChannel.getMusicServiceId() + ":album:" + myChannel.getAlbumId());
        playlist.setMantic_artists_name(null);
        playlist.setMantic_describe(myChannel.getChannelIntro());
//        playlist.setMantic_last_modified(TimeUtil.getDateFromMillisecond(myChannel.getmUpdateAt()));
        playlist.setMantic_last_modified(myChannel.getMainId());
        playlist.setMantic_num_tracks(myChannel.getmTotalCount());
        playlist.setMantic_type(myChannel.getChannelType());
        playlist.setName(myChannel.getChannelName());
        playlist.setMantic_image(myChannel.getChannelCoverUrl());
        playlist.setMantic_play_count(myChannel.getPlayCount());

        if (myChannel.isSelfDefinition()) {
            List<AddTrack> tracks = new ArrayList<>();
            ArrayList<Channel> channelList = myChannel.getChannelList();
            if (channelList.size() > 0) {
                playlist.setMantic_album_uri(myChannel.getMusicServiceId() + ":album:" + channelList.get(0).getUri());
            }
            for (int i = 0; i < channelList.size(); i++) {
                Channel channel = channelList.get(i);
                AddTrack addTrack = new AddTrack();
                addTrack.setName(channel.getName());
                addTrack.set__model__("Track");
                addTrack.setLength(channel.getDuration());
                List<String> mantic_artists_name1 = new ArrayList<>();
                mantic_artists_name1.add(TextUtils.isEmpty(channel.getSinger()) ? "未知" : channel.getSinger());
                addTrack.setMantic_artists_name(mantic_artists_name1);
                addTrack.setMantic_image(channel.getIconUrl());
                addTrack.setMantic_last_modified(TimeUtil.getDateFromMillisecond(channel.getLastSyncTime()));
                addTrack.setTrack_no(0);
                addTrack.setUri(channel.getUri());
                addTrack.setMantic_real_url(channel.getPlayUrl());
                addTrack.setMantic_album_name(channel.getMantic_album_name());
                addTrack.setMantic_album_uri(channel.getMantic_album_uri());
                tracks.add(addTrack);
            }

            playlist.setTracks(tracks);
        } else {
            playlist.setTracks(null);
        }


        params.setPlaylist(playlist);
        addBean.setParams(params);
        Gson gson = new Gson();
        String request = gson.toJson(addBean);
        Glog.i("lbj", "createAddRqBean: " + request);
        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), request.toString());
        return body;
    }

    public static RequestBody createUpdateRqBean(MyChannel myChannel, ArrayList<Channel> channelList, Context context) {
        MyChannelUpdateRqBean bean = new MyChannelUpdateRqBean();
        bean.setMethod("core.playlists.mantic_add_tracks");
        bean.setDevice_id(SharePreferenceUtil.getUserId(context));
        bean.setJsonrpc("2.0");
        bean.setId(1);
        UpdateParams params = new UpdateParams();
        params.setUri(myChannel.getUrl());

        List<AddTrack> tracks = new ArrayList<AddTrack>();
        for (int i = 0; i < channelList.size(); i++) {
            Channel channel = channelList.get(i);
            AddTrack track = new AddTrack();
            track.set__model__("Track");
            track.setName(channel.getName());
            track.setMantic_last_modified(TimeUtil.getDateFromMillisecond(channel.getLastSyncTime()));
            track.setUri(channel.getUri());
            track.setMantic_image(channel.getIconUrl());
            track.setMantic_real_url(channel.getPlayUrl());
            track.setTrack_no(0);
            track.setLength(channel.getDuration());
            List<String> mantic_artists_name = new ArrayList<>();
            if (!TextUtils.isEmpty(channel.getSinger())) {
                mantic_artists_name.add(channel.getSinger());
            }
            track.setMantic_artists_name(mantic_artists_name);
            track.setMantic_album_name(channel.getMantic_album_name());
            tracks.add(track);
        }

        params.setTracks(tracks);
        bean.setParams(params);
        Gson gson = new Gson();
        String request = gson.toJson(bean);
        Glog.i("lbj", "createUpdateRqBean: " + request);
        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), request.toString());
        return body;
    }


    public static RequestBody createDeleteRqBean(MyChannel myChannel, Context context) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("method", "core.playlists.delete");
        jsonObject.addProperty("jsonrpc", "2.0");
        jsonObject.addProperty("device_id", SharePreferenceUtil.getUserId(context));
        jsonObject.addProperty("id", 1);
        JsonObject params = new JsonObject();
        params.addProperty("uri", myChannel.getUrl());
        jsonObject.add("params", params);
        Glog.i("lbj", "createDeleteRqBean: " + jsonObject.toString());
        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), jsonObject.toString());
        return body;
    }


    public static RequestBody createClearRqBean(Context context) {
        MyChannelClearRqBean bean = new MyChannelClearRqBean();
        bean.setDevice_id(SharePreferenceUtil.getUserId(context));
        bean.setId(1);
        bean.setMethod("core.playlists.mantic_clear");
        bean.setJsonrpc("2.0");
        ClearParams params = new ClearParams();
        params.setUri_scheme("mongodb");
        bean.setParams(params);
        Gson gson = new Gson();
        String request = gson.toJson(bean);
        Glog.i("lbj", "createDeleteRqBean: " + request);
        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), request.toString());
        return body;
    }

    public static RequestBody createGetListRqBean(Context context) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("method", "core.playlists.get_playlists");
        jsonObject.addProperty("jsonrpc", "2.0");
        jsonObject.addProperty("device_id", SharePreferenceUtil.getUserId(context));
        jsonObject.addProperty("id", 1);
        JsonObject params = new JsonObject();
        params.addProperty("include_tracks", false);
        jsonObject.add("params", params);
        Glog.i("lbj", "createDeleteRqBean: " + jsonObject.toString());
        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), jsonObject.toString());
        return body;
    }


    public static RequestBody createSaveListRqBean(MyChannel myChannel, Context context) {
        MyChannelSaveRqBean addBean = new MyChannelSaveRqBean();
        addBean.setMethod("core.playlists.save");
        addBean.setDevice_id(SharePreferenceUtil.getUserId(context));
        addBean.setJsonrpc("2.0");
        addBean.setId(1);
        SaveParams params = new SaveParams();
        SavePlayList playlist = new SavePlayList();
        playlist.set__model__("Playlist");
        playlist.setUri(myChannel.getUrl());
        playlist.setMantic_album_uri(myChannel.getUrl());
        playlist.setMantic_describe(myChannel.getChannelIntro());
        playlist.setMantic_last_modified(myChannel.getMainId());
        playlist.setMantic_num_tracks(myChannel.getmTotalCount());
        playlist.setMantic_type(myChannel.getChannelType());
        playlist.setName(myChannel.getChannelName());
        playlist.setMantic_image(myChannel.getChannelCoverUrl());


        if (myChannel.isSelfDefinition()) {

            List<AddTrack> tracks = new ArrayList<>();
            ArrayList<Channel> channelList = myChannel.getChannelList();
            Glog.i("lbj", "createSaveListRqBean: " + channelList.size());
            for (int i = 0; i < channelList.size(); i++) {
                Channel channel = channelList.get(i);
                AddTrack addTrack = new AddTrack();
                addTrack.setName(channel.getName());
                addTrack.set__model__("Track");
                addTrack.setLength(channel.getDuration());
                List<String> mantic_artists_name1 = new ArrayList<>();
                mantic_artists_name1.add(TextUtils.isEmpty(channel.getSinger()) ? "未知" : channel.getSinger());
                addTrack.setMantic_artists_name(mantic_artists_name1);
                addTrack.setMantic_image(channel.getIconUrl());
                addTrack.setMantic_last_modified(TimeUtil.getDateFromMillisecond(channel.getLastSyncTime()));
                addTrack.setTrack_no(0);
                addTrack.setUri(channel.getUri());
                addTrack.setMantic_real_url(channel.getPlayUrl());
                addTrack.setMantic_album_name(channel.getMantic_album_name());
                tracks.add(addTrack);
            }

            playlist.setTracks(tracks);
        } else {
            playlist.setTracks(null);
        }

        params.setPlaylist(playlist);
        addBean.setParams(params);
        Gson gson = new Gson();
        String request = gson.toJson(addBean);
        Glog.i("lbj", "createSaveListRqBean: " + request);
        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), request.toString());
        return body;
    }


    public static RequestBody createDetailRqBean(MyChannel myChannel, Context context) {
        MyChannelDetailRqBean addBean = new MyChannelDetailRqBean();
        addBean.setMethod("core.playlists.lookup");
        addBean.setDevice_id(SharePreferenceUtil.getUserId(context));
        addBean.setJsonrpc("2.0");
        addBean.setId(1);
        DetailParams params = new DetailParams();
        params.setUri(myChannel.getUrl());
        addBean.setParams(params);
        Gson gson = new Gson();
        String request = gson.toJson(addBean);
        Glog.i("lbj", "createDetailRqBean: " + request);
        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), request.toString());
        return body;
    }


    public static boolean isApkDebugable(Context context) {
        try {
            ApplicationInfo info= context.getApplicationInfo();
            return (info.flags&ApplicationInfo.FLAG_DEBUGGABLE)!=0;
        } catch (Exception e) {
        }
        return false;
    }

    /**
     * 获取本地IP
     * @return
     */
    public static String getIp(Context context){
        WifiManager wifiManager = (WifiManager)context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        int ipAddress = wifiInfo.getIpAddress();
        //  Glog.d(Tag, "int ip "+ipAddress);
        if(ipAddress==0)return null;
        return ((ipAddress & 0xff)+"."+(ipAddress>>8 & 0xff)+"."
                +(ipAddress>>16 & 0xff)+"."+(ipAddress>>24 & 0xff));
    }

    public static void minimumVolumeDialog(Context mContext){
        if (!SharePreferenceUtil.getMinVolumeDialogShow(mContext)){
            CustomDialog.Builder mBuilder = new CustomDialog.Builder(mContext);
            mBuilder.setTitle(mContext.getString(R.string.dialog_btn_prompt));
            mBuilder.setMessage(mContext.getString(R.string.minimum_volume_msg));
            mBuilder.setPositiveButton(mContext.getString(R.string.qr_code_positive_button_know), new CustomDialog.Builder.DialogPositiveClickListener() {
                @Override
                public void onPositiveClick(final CustomDialog dialog) {
                    dialog.dismiss();
                }
            });
            mBuilder.create().show();
            SharePreferenceUtil.setMinVolumeDialogShow(mContext);
        }

    }

    public static void bluetoothModeDialog(Context mContext){
//        if (!SharePreferenceUtil.getMinVolumeDialogShow(mContext)){
            CustomDialog.Builder mBuilder = new CustomDialog.Builder(mContext);
            mBuilder.setTitle(mContext.getString(R.string.dialog_btn_prompt));
            mBuilder.setMessage(mContext.getString(R.string.bluetooth_mode_dialog));
            mBuilder.setPositiveButton(mContext.getString(R.string.qr_code_positive_button_know), new CustomDialog.Builder.DialogPositiveClickListener() {
                @Override
                public void onPositiveClick(final CustomDialog dialog) {
                    dialog.dismiss();
                }
            });
            mBuilder.create().show();
//            SharePreferenceUtil.setMinVolumeDialogShow(mContext);
//        }

    }

    private static int compare(String str, String target)
    {
        int d[][];              // 矩阵
        int n = str.length();
        int m = target.length();
        int i;                  // 遍历str的
        int j;                  // 遍历target的
        char ch1;               // str的
        char ch2;               // target的
        int temp;               // 记录相同字符,在某个矩阵位置值的增量,不是0就是1
        if (n == 0) { return m; }
        if (m == 0) { return n; }
        d = new int[n + 1][m + 1];
        for (i = 0; i <= n; i++)
        {                       // 初始化第一列
            d[i][0] = i;
        }

        for (j = 0; j <= m; j++)
        {                       // 初始化第一行
            d[0][j] = j;
        }

        for (i = 1; i <= n; i++)
        {                       // 遍历str
            ch1 = str.charAt(i - 1);
            // 去匹配target
            for (j = 1; j <= m; j++)
            {
                ch2 = target.charAt(j - 1);
                if (ch1 == ch2 || ch1 == ch2+32 || ch1+32 == ch2)
                {
                    temp = 0;
                } else
                {
                    temp = 1;
                }
                // 左边+1,上边+1, 左上角+temp取最小
                d[i][j] = min(d[i - 1][j] + 1, d[i][j - 1] + 1, d[i - 1][j - 1] + temp);
            }
        }
        return d[n][m];
    }

    private static int min(int one, int two, int three)
    {
        return (one = one < two ? one : two) < three ? one : three;
    }

    /**
     * 获取两字符串的相似度
     */

    public static float getSimilarityRatio(String str, String target)
    {
        return 1 - (float) compare(str, target) / Math.max(str.length(), target.length());
    }


    public static boolean isActivityRunning(Context mContext, String activityClassName){
        ActivityManager activityManager = (ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE);
        List<android.app.ActivityManager.RunningTaskInfo> info = activityManager.getRunningTasks(1);
        if(info != null && info.size() > 0){
            ComponentName component = info.get(0).topActivity;
            if(activityClassName.equals(component.getClassName())){
                return true;
            }
        }
        return false;
    }


    public static RequestBody createAddSoundRqBean(SoundTrack soundTrack, Context context) {
        MySoundAddBean addBean = new MySoundAddBean();
        addBean.setMethod("core.playlists.mantic_mywork_add");
        addBean.setDevice_id(SharePreferenceUtil.getUserId(context));
        addBean.setJsonrpc("2.0");
        addBean.setId(50);
        com.mantic.control.api.sound.AddParams params = new com.mantic.control.api.sound.AddParams();
        params.setUri_scheme("mongodb");
        List<SoundTrack> trackList = new ArrayList<>();
        trackList.add(soundTrack);
        params.setListTracks(trackList);
        addBean.setParams(params);
        Gson gson = new Gson();
        String request = gson.toJson(addBean);
        Glog.i("jys", "createAddSoundRqBean: " + request);
        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), request.toString());
        return body;
    }

}
