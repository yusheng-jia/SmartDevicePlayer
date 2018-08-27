package com.mantic.control.musicservice;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;

import com.baidu.iot.sdk.IoTSDKManager;
import com.mantic.control.R;
import com.mantic.control.api.baidu.BaiduRetrofit;
import com.mantic.control.api.baidu.BaiduServiceApi;
import com.mantic.control.api.baidu.bean.BaiduTrackList;
import com.mantic.control.api.mopidy.MopidyRetrofit;
import com.mantic.control.api.mopidy.MopidyServiceApi;
import com.mantic.control.api.mopidy.MopidyTools;
import com.mantic.control.api.mopidy.bean.MopidyRsAlbumPageBean;
import com.mantic.control.api.mopidy.bean.MopidyRsTrackBean;
import com.mantic.control.utils.Glog;
import com.mantic.control.utils.TimeUtil;
import com.mantic.control.utils.Util;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * 娱乐页面的服务
 */
public class EntertainmentService implements MyMusicService {
    private static final String TAG = "EntertainmentService";

    private Context mContext;
    private static final int TYPE_MAIN = 0;
    private static final int TYPE_CATEGOROES = 1;
    private static final int TYPE_TAGS = 2;
    public static final int TYPE_ALBUMS = 3;
    private String curTrackListUri = "";
    private String curAlbumListUri = "";
    private MopidyServiceApi mpServiceApi;

    private static final String MAIN_ID = "main_id";
    private static final String CATEGORY_ID = "category_id";
    private static final String TAG_NAME = "tag_name";
    public static final String ALBUM_ID = "album_id";
    public static final String CHANNEL_INTRO = "channel_INTRO";
    private static final String TRACK_IMAGE = "track_image";
    private static final String TRACK_SINGER = "track_singer";

    private boolean mLoading = false;

    private ArrayList<IMusicServiceSubItem> albumList = new ArrayList<IMusicServiceSubItem>();
    private IMusicServiceSubItemList mIMusicServiceSubItemList;
    private IMusicServiceAlbum mIMusicServiceAlbum;

    public EntertainmentService(Context context) {
        this.init(context);
    }

    public void init(Context context) {
        mContext = context;
        mpServiceApi = MopidyRetrofit.getInstance().create(MopidyServiceApi.class);
    }

    @Override
    public String getMusicServiceID() {
        return "entertainment";
    }

    @Override
    public void setIMusicServiceSubItemListCallBack(IMusicServiceSubItemList callback) {
        this.mIMusicServiceSubItemList = callback;
    }

    @Override
    public void setIMusicServiceAlbum(IMusicServiceAlbum callback) {
        this.mIMusicServiceAlbum = callback;
    }

    @Override
    public IMusicServiceAlbum getIMusicServiceAlbum() {
        return this.mIMusicServiceAlbum;
    }

    @Override
    public void exec(Bundle bundle) {
        int type = bundle.getInt(PRE_DATA_TYPE); //上一级type
        String albumUri = bundle.getString(ALBUM_ID);
        Glog.i(TAG, "name == " + albumUri);
        Glog.i(TAG, "type == " + type);
        if (type != -1 && TextUtils.isEmpty(albumUri)) {
            if (mIMusicServiceAlbum != null) {
                mIMusicServiceAlbum.onError(404, "No data!");
            }
            mLoading = false;
            canRefresh = false;
            return;
        }
        switch (type) {
            case TYPE_TAGS:
                getAlbumList(albumUri);
                break;
            case TYPE_ALBUMS:
                loadTrackList(albumUri);
                break;
        }
    }

    private int currentTrackListPage = 0;
    private int currentAlbumListPage = 0;

    private boolean canRefresh = false;


    private void getAlbumList(final String uri){
        Glog.i(TAG,"getAlbumList()....... taguri:"+uri);
        if(mLoading){
            return;
        }
        canRefresh = true;
        currentAlbumListPage = 0;
        curAlbumListUri = uri;
        albumList.clear();
        RefreshAlbumList();
    }

    private void loadTrackList(String uri) {
        Glog.i(TAG, "loadTrackList..." + uri);
        if (mLoading) {
            return;
        }
        currentTrackListPage = 0;
        curTrackListUri = uri;
        mLoading = true;
        canRefresh = true;
        RefreshTrackList();
    }

    @Override
    public boolean isRefresh() {
        return canRefresh;
    }

    @Override
    public void RefreshAlbumList() {
        canRefresh = false;
        Glog.i(TAG,"curAlbumListUri :" + curAlbumListUri);
        RequestBody body = MopidyTools.createRequestPageBrowse(curAlbumListUri,currentAlbumListPage);
        Call<MopidyRsAlbumPageBean> call = mpServiceApi.postMopidyAlbumPageQuest(MopidyTools.getHeaders(),body);
        call.enqueue(new Callback<MopidyRsAlbumPageBean>() {
            @Override
            public void onResponse(Call<MopidyRsAlbumPageBean> call, Response<MopidyRsAlbumPageBean> response) {
                if (response.isSuccessful() && null == response.errorBody() && null != response.body()) {
                    List<MopidyRsAlbumPageBean.Result> resultAlbumMoreList = new ArrayList<MopidyRsAlbumPageBean.Result>();
                    resultAlbumMoreList = response.body().results;
//                Glog.i(TAG,"onResponse: " + resultAlbumMoreList);
                    if (resultAlbumMoreList != null && (resultAlbumMoreList.size()!=0)) {
                        for (int i = 0; i < resultAlbumMoreList.size(); i++) {
                            final MopidyRsAlbumPageBean.Result result = resultAlbumMoreList.get(i);
                            String singerStr = "";
                            if (null != result && null != result.artists_name) {
                                for (int j = 0; j < result.artists_name.size(); j++) {
                                    if (j == 0) {
                                        singerStr = result.artists_name.get(j);
                                    } else {
                                        singerStr = singerStr + "," + result.artists_name.get(j);
                                    }

                                }
                            }
                            final String singer = singerStr;
                            IMusicServiceSubItemAlbum subItem = new IMusicServiceSubItemAlbum() {
                                @Override
                                public String getCoverUrl() {
                                    return result.image;
                                }

                                @Override
                                public String getAlbumTitle() {
                                    return result.name;
                                }

                                @Override
                                public String getAlbumTags() {
                                    return "";
                                }

                                @Override
                                public String getAlbumIntro() {
                                    return result.description;
                                }

                                @Override
                                public long getTotalCount() {
                                    return result.count;
                                }

                                @Override
                                public long getUpdateAt() {
                                    if (result.update.isEmpty()){
                                        return 0;
                                    }
                                    return TimeUtil.timeMillionByTimeFormat("yyyy-MM-dd",result.update);
                                }

                                @Override
                                public String getAlbumId() {
                                    return result.uri;
                                }

                                @Override
                                public String getMainId() {
                                    return null;
                                }

                                @Override
                                public String getSinger() {
                                    return singer;
                                }

                                @Override
                                public int getType() {
                                    if (result.uri.contains("radio")){
                                        return 2;
                                    }else {
                                        return 0;
                                    }
                                }

                                @Override
                                public String getPlayCount() {
                                    return result.play_count;
                                }

                                @Override
                                public String getItemIconUrl() {
                                    return result.image;
                                }

                                @Override
                                public String getItemText() {
                                    return result.name;
                                }

                                @Override
                                public Bundle gotoNext() {
                                    Bundle bundle = new Bundle();
                                    bundle.putString(MY_MUSIC_SERVICE_ID, getMusicServiceID());
                                    bundle.putInt(PRE_DATA_TYPE, TYPE_ALBUMS);
                                    bundle.putString(ALBUM_ID, result.uri);
                                    bundle.putString(TRACK_IMAGE,result.image);
                                    bundle.putString(TRACK_SINGER,singer);
                                    return bundle;
                                }

                                @Override
                                public int getNextDataType() {
                                    return TYPE_DATA_ALBUM;
                                }

                                @Override
                                public String getNextDataId() {
                                    return PRE_DATA_TYPE + TYPE_ALBUMS + result.uri;
                                }

                                @Override
                                public int getIconType() {
                                    return 0;
                                }
                            };
                            albumList.add(subItem);
                        }
                        canRefresh = true;
                        currentAlbumListPage++;
                    }else {
                        canRefresh = false;
                    }

                    if (mIMusicServiceSubItemList != null && albumList.size() > 0) {
                        mIMusicServiceSubItemList.createMusicServiceSubItemList(albumList);
                    }else if (mIMusicServiceSubItemList != null) {
                        mIMusicServiceSubItemList.onEmpty();
                    }
                } else {
                    if(mIMusicServiceSubItemList != null){
                        mIMusicServiceSubItemList.onError(1,"");
                    }
                }
                mLoading = false;
            }

            @Override
            public void onFailure(Call<MopidyRsAlbumPageBean> call, Throwable t) {
                mLoading = false;
                if(mIMusicServiceSubItemList != null){
                    mIMusicServiceSubItemList.onError(1,"");
                }
            }
        });
    }

    @Override
    public void RefreshTrackList() {
        canRefresh = false;
        Glog.i(TAG,"curAlbumListUri :" + curTrackListUri);
        RequestBody body = MopidyTools.createRequestPageBrowse(curTrackListUri, currentTrackListPage);
        Call<MopidyRsTrackBean> call = mpServiceApi.postMopidyTrackPageQuest(MopidyTools.getHeaders(),body);
        call.enqueue(new Callback<MopidyRsTrackBean>() {
            @Override
            public void onResponse(Call<MopidyRsTrackBean> call, final Response<MopidyRsTrackBean> response) {
                if (response.isSuccessful() && null == response.errorBody() && null != response.body()) {
                    MopidyRsTrackBean bean = response.body();
                    List<MopidyRsTrackBean.Result> resultList = new ArrayList<MopidyRsTrackBean.Result>();
                    resultList = bean.results;
                    Glog.i(TAG,"resultList: " + resultList);
                    ArrayList<IMusicServiceTrackContent> trackContents = new ArrayList<IMusicServiceTrackContent>();
                    if (resultList != null && resultList.size()!= 0){
                        for (int i = 0 ; i < resultList.size(); i++){
                            final MopidyRsTrackBean.Result result = resultList.get(i);
                            IMusicServiceTrackContent content = new IMusicServiceTrackContent(){

                                @Override
                                public String getCoverUrlSmall() {
                                    return "album";
                                }

                                @Override
                                public String getCoverUrlMiddle() {
                                    return "album";
                                }

                                @Override
                                public String getCoverUrlLarge() {
                                    return "album";
                                }

                                @Override
                                public String getTrackTitle() {
                                    return result.name;
                                }

                                @Override
                                public int getUrlType() {
                                    return IMusicServiceTrackContent.STATIC_URL;
                                }

                                @Override
                                public String getPlayUrl() {
                                    return result.mantic_real_url;
                                }

                                @Override
                                public String getSinger() {
                                    String artist_name = "";
                                    if (result.mantic_artists_name != null){
                                        for (int i = 0; i< result.mantic_artists_name.size(); i++){
                                            if (i == 0) {
                                                artist_name = result.mantic_artists_name.get(i);
                                            } else {
                                                artist_name = artist_name + "," + result.mantic_artists_name.get(i);;
                                            }
                                        }
                                    }
                                    if (result.uri.contains("radio")&&artist_name.equals(" ")){
                                        return "未知";
                                    }else {
                                        return artist_name;
                                    }
                                }

                                @Override
                                public long getDuration() {
                                    return result.length;
                                }

                                @Override
                                public long getUpdateAt() {
                                    if (result.uri.contains("radio")){
                                        return 0;
                                    }else {
                                        if (TextUtils.isEmpty(result.update)) {
                                            return 0;
                                        }
                                        return TimeUtil.timeMillionByTimeFormat("yyyy-MM-dd", result.update);
                                    }
                                }

                                @Override
                                public String getUri() {
                                    return result.uri;
                                }

                                @Override
                                public String getTimePeriods() {
                                    return result.mantic_radio_length;
                                }

                                @Override
                                public String getAlbumId() {
                                    return result.mantic_album_uri;
                                }
                            };
                            trackContents.add(content);
                        }

                        if(mIMusicServiceAlbum != null){
                            mIMusicServiceAlbum.createMusicServiceAlbum(trackContents);
                        }
                        canRefresh = true;
                        currentTrackListPage++;
                    }else {// tracklist 为0  说明数据没有了
                        ArrayList<IMusicServiceTrackContent> noTracks = new ArrayList<IMusicServiceTrackContent>();
                        Glog.i(TAG,"noTracks size" + noTracks.size());
                        if(mIMusicServiceAlbum != null){
                            mIMusicServiceAlbum.createMusicServiceAlbum(noTracks);
                        }
                        canRefresh = false;
                    }
                } else {
                    if(mIMusicServiceAlbum != null){
                        mIMusicServiceAlbum.onError(1,"");
                    }
                }
                mLoading = false;
            }

            @Override
            public void onFailure(Call<MopidyRsTrackBean> call, Throwable t) {
                Glog.i(TAG,"onFailure...");
                mLoading = false;
                if(mIMusicServiceAlbum != null){
                    mIMusicServiceAlbum.onError(1,"");
                }
            }
        });
    }
}
