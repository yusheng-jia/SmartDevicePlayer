package com.mantic.control;

import android.text.TextUtils;
import android.widget.TextView;

import com.mantic.control.data.DataFactory;
import com.mantic.control.utils.Glog;
import com.rogen.netcontrol.model.Music;

import java.util.ArrayList;

/**
 * Created by jayson on 2017/6/22.
 */

public class AudioHelper {
//    ArrayList<DataFactory.MusicService> sicServices = new ArrayList<DataFactory.MusicService>();
//
//    public void init(){
//        sicServices = DataFactory.getInstance().getmMyMusicServiceList();
//    }
    public static String getServiceNameById(String id){
        if (TextUtils.isEmpty(id)) {
            return "云音乐";
        }

        if (id.equals("fadd9f655df9480179e0b3181be58fa2")){
            return "喜马拉雅";
        }else if (id.equals("a806aa9062dd1d26a315b7533144b606")){
            return "虾米音乐";
        }else if (id.equals("wangyi")){
            return "网易音乐";
        }else if (id.equals("kaolo")){
            return "考拉FM";
        }else if (id.equals("beiwa")){
            return "贝瓦听听";
        }if (id.equals("qingting")){
            return "蜻蜓FM";
        }else {
            return "云音乐";
        }
    }

    public static String getServiceNameByUri(String uri){
        if (TextUtils.isEmpty(uri)) {
            return "云音乐";
        }

        if (uri.contains("ximalaya")){
            return "喜马拉雅";
        }else if (uri.contains("xiami")){
            return "虾米音乐";
        }else if (uri.contains("netease")){
            return "网易音乐";
        }else if (uri.contains("kaola")){
            return "考拉FM";
        }else if (uri.contains("beva")){
            return "贝瓦听听";
        }else if (uri.contains("qingting")){
            return "蜻蜓FM";
        }else if (uri.contains("baidu")){
            return "百度云音乐";
        } else if (uri.contains("east")){
            return "东方音乐";
        } else {
            return "云音乐";
        }
    }

    public static int getImageDrawableFormServiceId(String id){
        if (id != null){
            if (id.equals("fadd9f655df9480179e0b3181be58fa2")){
                return R.drawable.ximalaya_music_service_icon;
            }else if (id.equals("a806aa9062dd1d26a315b7533144b606")){
                return R.drawable.xiami_music_service_icon;
            }else if (id.equals("wangyi")){
                return R.drawable.wyyyy_music_service_icon;
            }else if (id.equals("kaolo")){
                return R.drawable.kaola_music_service_icon;
            }else if (id.equals("beiwa")){
                return R.drawable.beiwa_music_service_icon;
            }else if (id.equals("qingting")){
                return R.drawable.qingting_music_service_icon;
            }else if (id.equals("baidu")){
                return R.drawable.baidu_music_service_icon;
            }else {
                return R.drawable.ic_launcher;
            }
        }else {
            return R.drawable.ic_launcher;
        }
    }

    public static int getVolumeValue(int progress){
        Glog.i("jys","progress:" + progress);
        if (progress == 0){
            return 1;
        }else {
            if (progress == 150){
                return 15;
            }else {
                return progress/10+1;
            }
        }
    }

    public static void setTextMarquee(TextView textView) {
        if (textView != null) {
            textView.setEllipsize(TextUtils.TruncateAt.MARQUEE);
            textView.setSingleLine(true);
            textView.setSelected(true);
            textView.setFocusable(true);
            textView.setFocusableInTouchMode(true);
        }
    }
}
