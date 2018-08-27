package com.mantic.control.utils;

import android.content.Context;
import android.os.Environment;

import com.google.gson.JsonObject;
import com.mantic.control.data.MyChannel;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by lin on 2017/6/14.
 */

public class FileUtil {

    public static String getString(Context context, String fileName) {
        InputStream inputStream = null;
        InputStreamReader inputStreamReader = null;
        try {
            inputStream = context.getAssets().open(fileName);
            inputStreamReader = new InputStreamReader(inputStream, "utf-8");
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        BufferedReader reader = new BufferedReader(inputStreamReader);
        StringBuilder sb = new StringBuilder("");
        String line;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line);
                sb.append("\n");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                reader.close();
                inputStreamReader.close();
                inputStream.close();
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        return sb.toString();
    }


    public static ArrayList<MyChannel> getInterestChannel(String[] ids, String fileName, Context context) {
        ArrayList<MyChannel> myChannelList = new ArrayList<MyChannel>();
        String dataStr = getString(context, fileName);
        try  {
            JSONArray array = new JSONArray(dataStr);

            for (int i = 0; i < ids.length; i++) {
                JSONObject jsonObject = array.getJSONObject(Integer.parseInt(ids[i]));
                JSONArray channelList = jsonObject.getJSONArray("channelList");
                for (int j = 0; j < channelList.length(); j++) {
                    MyChannel myChannel = new MyChannel();
                    JSONObject object = channelList.getJSONObject(j);
                    myChannel.setChannelName(object.optString("channelName"));
                    myChannel.setMusicServiceId(object.optString("musicServiceId"));
                    myChannel.setChannelId(object.optString("channelId"));
                    myChannel.setChannelCoverUrl(object.optString("channelCoverUrl"));
                    myChannel.setChannelIntro(object.optString("channelIntro"));
                    myChannel.setmTotalCount(object.optInt("totalCount"));
                    myChannel.setChannelType(object.optInt("pre_data_type"));
                    myChannel.setAlbumId(object.optString("album_id"));
                    myChannel.setChannelName(object.optString("channelName"));
                    myChannel.setMainId(object.optString("main_id"));
                    myChannelList.add(myChannel);
                }

            }

            return myChannelList;
        } catch (Exception e) {
            e.printStackTrace();
            Glog.i("lbj", "getInterestChannel: " + e.getMessage());
        }
        return myChannelList;
    }


    //保存文件：如果没有会自动创建，如果有的话会覆盖。 当在创建时加入true参数，回实现对文件的续写。 false则会覆盖前面的数据
    public static void bufferSave(String msg) {
        String externalStorageDirectory = Environment.getExternalStorageDirectory().getPath() + "/mantic/";
        File tempFile = new File(externalStorageDirectory);
        if (!tempFile.exists()) {
            tempFile.mkdirs();
        }
        try {
            BufferedWriter bfw = new BufferedWriter(new FileWriter(externalStorageDirectory + "log.txt", true));
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy年MM月dd日   HH:mm:ss");
            Date curDate =  new Date(System.currentTimeMillis());
            String   dateStr   =   formatter.format(curDate);
            bfw.write(dateStr + "   " + msg);
            bfw.newLine();
            bfw.flush();
            bfw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
