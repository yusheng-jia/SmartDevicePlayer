package com.mantic.control.musicservice;

import com.baidu.iot.sdk.AudioRequestListener;
import com.baidu.iot.sdk.HttpStatus;
import com.baidu.iot.sdk.IoTException;
import com.baidu.iot.sdk.IoTSDKManager;
import com.baidu.iot.sdk.model.AudioAlbum;
import com.baidu.iot.sdk.model.AudioCategory;
import com.baidu.iot.sdk.model.AudioPageInfo;
import com.baidu.iot.sdk.model.AudioTrack;
import com.mantic.control.utils.Glog;

import java.util.List;

/**
 * author: jayson
 * blog: http://blog.csdn.net/jia4525036
 * time: 2017/8/2.
 * desc:
 */

public class DuerMusicService {

    public void getDuerCategoryList(){
        IoTSDKManager.getInstance().createAudioAPI().getCategoryList(new AudioRequestListener<List<AudioCategory>>() {
            @Override
            public void onSuccess(HttpStatus httpStatus, List<AudioCategory> audioCategories, AudioPageInfo audioPageInfo) {
                for (int i = 0; i < audioCategories.size(); i++){
                    AudioCategory audioCategory = audioCategories.get(i);
                    Glog.i("duer","id : " + audioCategory.id);
                    Glog.i("duer","Category : " + audioCategory.category);
                    Glog.i("duer","cover_url : " + audioCategory.cover_url.middle);
                }

            }

            @Override
            public void onFailed(HttpStatus httpStatus) {

            }

            @Override
            public void onError(IoTException e) {

            }
        });
    }

    public void getDuerAlbumList(){
        IoTSDKManager.getInstance().createAudioAPI().getAlbumList("公开课", new AudioRequestListener<List<AudioAlbum>>() {
            @Override
            public void onSuccess(HttpStatus httpStatus, List<AudioAlbum> audioAlba, AudioPageInfo audioPageInfo) {
                for (int i = 0; i < audioAlba.size(); i++){
                    AudioAlbum audioAlbum = audioAlba.get(i);
                    Glog.i("duer","audioAlbum -> id : " + audioAlbum.id);
                }
            }

            @Override
            public void onFailed(HttpStatus httpStatus) {

            }

            @Override
            public void onError(IoTException e) {

            }
        },null);
    }

    public void getDuerTrackList(){
        IoTSDKManager.getInstance().createAudioAPI().getTrackList("56760074260", new AudioRequestListener<List<AudioTrack>>() {
            @Override
            public void onSuccess(HttpStatus httpStatus, List<AudioTrack> audioTracks, AudioPageInfo audioPageInfo) {
                for (int i = 0; i < audioTracks.size(); i ++){
                    AudioTrack audioTrack = audioTracks.get(i);
                    Glog.i("duer","audioTrack - > id " + audioTrack.id);
                    Glog.i("duer","audioTrack - > name " + audioTrack.name);
                    Glog.i("duer","audioTrack - > uri " + audioTrack.audioPlayUrl.m4a);
                }
            }

            @Override
            public void onFailed(HttpStatus httpStatus) {

            }

            @Override
            public void onError(IoTException e) {

            }
        },null);
    }

    public void getDuerCurrentTrack(){
        IoTSDKManager.getInstance().createDeviceAPI().getPlayInfo("");
    }
}
