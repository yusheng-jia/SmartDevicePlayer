package com.mantic.control.musicservice;

import android.os.Bundle;;

import com.mantic.control.api.mopidy.MopidyRetrofit;
import com.mantic.control.api.mopidy.MopidyServiceApi;
import com.mantic.control.api.mopidy.MopidyTools;
import com.mantic.control.api.mopidy.bean.MopidyRsAlbumPageBean;
import com.mantic.control.api.mopidy.bean.MopidyRsBrowseBean;
import com.mantic.control.api.mopidy.bean.MopidyRsLiveBrowseBean;
import com.mantic.control.api.mopidy.bean.MopidyRsTrackBean;
import com.mantic.control.utils.Glog;
import com.mantic.control.utils.TimeUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * author: jayson
 * blog: http://blog.csdn.net/jia4525036
 * time: 2017/8/17.
 * desc:工程师爸爸 音乐服务
 */

public class IdaddyMusicService implements MyMusicService{
    private static final String TAG = "IdaddyMusicService";

    private IMusicServiceSubItemList mIMusicServiceSubItemList;
    private IMusicServiceAlbum mIMusicServiceAlbum;
    private MopidyServiceApi mpServiceApi;
    private boolean mLoading = false;
    private boolean canRefresh = false; //当前界面是否支持刷新
    private String curAlbumListUri = "";
    private String curTrackListUri = "";
    private ArrayList<IMusicServiceSubItem> albumList = new ArrayList<IMusicServiceSubItem>();
    private int currentAlbumListPage = 0;
    private int currentTrackListPage = 0;

    private static final int TYPE_CATEGORIES = 0;
    private static final int TYPE_TAGS = 1;
    private static final int TYPE_LABEL = 2;
    private static final int TYPE_ALBUMS = 3;

    private static final String CATEGORY_ID = "category_id";
    private static final String TAG_NAME = "tag_name";
    public static final String ALBUM_ID = "album_id";

    // tracklist 没有 image 和 singer 字段，使用 album 的
    private static final String TRACK_IMAGE = "track_image";
    private static final String TRACK_SINGER = "track_singer";
    private String currentTrackImage = "";
    private String currentTrackSinger = "";

    public IdaddyMusicService (){
        mpServiceApi = MopidyRetrofit.getInstance().create(MopidyServiceApi.class);
    }

    @Override
    public String getMusicServiceID() {
        return "idaddy";
    }


    /**
     * 工程师爸爸 category ：分类
     */
    private void getCategoryList(){
        Glog.i(TAG,"getCategoryList().........");
        if(mLoading){
            return;
        }
        canRefresh = false;
        RequestBody body = MopidyTools.createRequestBrowse("idaddy:root");
        Call<MopidyRsBrowseBean> call = mpServiceApi.postMopidyQuest(MopidyTools.getHeaders(),body);
        call.enqueue(new Callback<MopidyRsBrowseBean>() {
            @Override
            public void onResponse(Call<MopidyRsBrowseBean> call, Response<MopidyRsBrowseBean> response) {
                if (response.isSuccessful() && null == response.errorBody() && null != response.body()) {
                    MopidyRsBrowseBean bean = response.body();
                    Glog.i(TAG,"onResponse: " + bean);
                    ArrayList<IMusicServiceSubItem> subItems = new ArrayList<IMusicServiceSubItem>();
                    List<MopidyRsBrowseBean.Result> resultList = new ArrayList<MopidyRsBrowseBean.Result>();
                    resultList = bean.results;
                    if(resultList != null) {
                        for (int i = 0; i < resultList.size(); i++) {
                            final MopidyRsBrowseBean.Result result = resultList.get(i);
                            if (result.uri.indexOf("album")>0){
                                IMusicServiceSubItemAlbum subItem1 = new IMusicServiceSubItemAlbum() {
                                    @Override
                                    public String getCoverUrl() {
                                        return "";
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
                                        return "";
                                    }

                                    @Override
                                    public long getTotalCount() {
                                        return 0;
                                    }

                                    @Override
                                    public long getUpdateAt() {
                                        return 0;
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
                                        return "";
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
                                        return "";
                                    }

                                    @Override
                                    public String getItemIconUrl() {
                                        return "";
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
                                        bundle.putString(TRACK_IMAGE,"");
                                        bundle.putString(TRACK_SINGER,"");
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
                                subItems.add(subItem1);
                            }else {
                                IMusicServiceSubItem subItem2 = new IMusicServiceSubItem() {
                                    @Override
                                    public String getItemIconUrl() {
                                        return "";
                                    }

                                    @Override
                                    public String getItemText() {
                                        return result.name;
                                    }

                                    @Override
                                    public Bundle gotoNext() {
                                        Bundle bundle = new Bundle();
                                        bundle.putString(MY_MUSIC_SERVICE_ID, getMusicServiceID());
                                        bundle.putInt(PRE_DATA_TYPE, TYPE_CATEGORIES);
                                        bundle.putString(CATEGORY_ID, result.uri);
                                        return bundle;
                                    }

                                    @Override
                                    public int getNextDataType() {
                                        return TYPE_DATA_LIST;
                                    }

                                    @Override
                                    public String getNextDataId() {
                                        return PRE_DATA_TYPE + TYPE_CATEGORIES + result.uri;
                                    }

                                    @Override
                                    public int getIconType() {
                                        return 1;
                                    }
                                };
                                subItems.add(subItem2);
                            }

                        }
                    }
                    if(mIMusicServiceSubItemList != null && subItems.size() > 0){
                        mIMusicServiceSubItemList.createMusicServiceSubItemList(subItems);
                    }else if (mIMusicServiceSubItemList != null){
                        mIMusicServiceSubItemList.onEmpty();
                    }

                } else {
                    if (mIMusicServiceSubItemList != null){
                        mIMusicServiceSubItemList.onError(1, "");
                    }
                }


                mLoading = false;
            }

            @Override
            public void onFailure(Call<MopidyRsBrowseBean> call, Throwable t) {
                Glog.i(TAG,"onFailure .....");
                mLoading = false;
                if(mIMusicServiceSubItemList != null){
                    mIMusicServiceSubItemList.onError(1,"");
                }
            }
        });
    }

    /**
     * 工程师爸爸 TAG 分类
     * @param taguri 上一级返回参数的uri
     */
    private void getTagList(final String taguri){
        Glog.i(TAG,"getTagList()....... taguri:"+taguri);
        if(mLoading){
            return;
        }
        canRefresh = false;
        RequestBody body = MopidyTools.createRequestBrowse(taguri);
        Call<MopidyRsBrowseBean> call = mpServiceApi.postMopidyQuest(MopidyTools.getHeaders(),body);
        call.enqueue(new Callback<MopidyRsBrowseBean>() {
            @Override
            public void onResponse(Call<MopidyRsBrowseBean> call, Response<MopidyRsBrowseBean> response) {
                if (response.isSuccessful() && null == response.errorBody() && null != response.body()) {
                    MopidyRsBrowseBean bean = response.body();
                    ArrayList<IMusicServiceSubItem> subItems = new ArrayList<IMusicServiceSubItem>();
                    List<MopidyRsBrowseBean.Result> resultList = new ArrayList<MopidyRsBrowseBean.Result>();
                    resultList = bean.results;
                    if (resultList != null && resultList.size()!=0) {
                        for (int i=0; i< resultList.size(); i++){
                            final MopidyRsBrowseBean.Result result = resultList.get(i);
                            IMusicServiceSubItem subItem = new IMusicServiceSubItem() {
                                @Override
                                public String getItemIconUrl() {
                                    return "";
                                }

                                @Override
                                public String getItemText() {
                                    return result.name;
                                }

                                @Override
                                public Bundle gotoNext() {
                                    Bundle bundle = new Bundle();
                                    bundle.putString(MY_MUSIC_SERVICE_ID,getMusicServiceID());
                                    bundle.putInt(PRE_DATA_TYPE, TYPE_TAGS);
                                    bundle.putString(TAG_NAME,result.uri);
                                    return bundle;
                                }

                                @Override
                                public int getNextDataType() {
                                    return TYPE_DATA_LIST;
                                }

                                @Override
                                public String getNextDataId() {
                                    return PRE_DATA_TYPE+TYPE_TAGS+result.uri;
                                }

                                @Override
                                public int getIconType() {
                                    if (taguri.contains("radio")){
                                        return 1;
                                    }else {
                                        return 0;
                                    }
                                }
                            };
                            subItems.add(subItem);
                        }
                    }

                    if(mIMusicServiceSubItemList != null && subItems.size() > 0){
                        mIMusicServiceSubItemList.createMusicServiceSubItemList(subItems);
                    }else if (mIMusicServiceSubItemList != null){
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
            public void onFailure(Call<MopidyRsBrowseBean> call, Throwable t) {
                mLoading = false;
                if(mIMusicServiceSubItemList != null){
                    mIMusicServiceSubItemList.onError(1,"");
                }
            }
        });
    }

    /**
     * 工程师爸爸 分类 -- 子分类：
     * @param taguri 上一级返回参数的uri
     */
    private void getLabelList(String taguri){
        Glog.i(TAG,"getLabelList()....... taguri:"+taguri);
        canRefresh = false;
        RequestBody body = MopidyTools.createRequestBrowse(taguri);
        Call<MopidyRsBrowseBean> call = mpServiceApi.postMopidyQuest(MopidyTools.getHeaders(),body);
        call.enqueue(new Callback<MopidyRsBrowseBean>() {
            @Override
            public void onResponse(Call<MopidyRsBrowseBean> call, Response<MopidyRsBrowseBean> response) {
                if (response.isSuccessful() && null == response.errorBody() && null != response.body()) {
                    MopidyRsBrowseBean bean = response.body();
                    ArrayList<IMusicServiceSubItem> subItems = new ArrayList<IMusicServiceSubItem>();
                    List<MopidyRsBrowseBean.Result> resultList = new ArrayList<MopidyRsBrowseBean.Result>();
                    resultList = bean.results;
                    if (resultList != null && resultList.size()!=0) {
                        for (int i=0; i< resultList.size(); i++){
                            final MopidyRsBrowseBean.Result result = resultList.get(i);
                            IMusicServiceSubItem subItem = new IMusicServiceSubItem() {
                                @Override
                                public String getItemIconUrl() {
                                    return "";
                                }

                                @Override
                                public String getItemText() {
                                    return result.name;
                                }

                                @Override
                                public Bundle gotoNext() {
                                    Bundle bundle = new Bundle();
                                    bundle.putString(MY_MUSIC_SERVICE_ID,getMusicServiceID());
                                    bundle.putInt(PRE_DATA_TYPE, TYPE_LABEL);
                                    bundle.putString(TAG_NAME,result.uri);
                                    return bundle;
                                }

                                @Override
                                public int getNextDataType() {
                                    return TYPE_DATA_LIST;
                                }

                                @Override
                                public String getNextDataId() {
                                    return PRE_DATA_TYPE+TYPE_LABEL+result.uri;
                                }

                                @Override
                                public int getIconType() {
                                    return 0;
                                }
                            };
                            subItems.add(subItem);
                        }
                    }

                    if(mIMusicServiceSubItemList != null && subItems.size() > 0){
                        mIMusicServiceSubItemList.createMusicServiceSubItemList(subItems);
                    }else if(mIMusicServiceSubItemList != null) {
                        mIMusicServiceSubItemList.onEmpty();
                    }
                } else {
                    if(mIMusicServiceSubItemList != null) {
                        mIMusicServiceSubItemList.onError(1,"");
                    }
                }

                mLoading = false;
            }

            @Override
            public void onFailure(Call<MopidyRsBrowseBean> call, Throwable t) {
                mLoading = false;
                if(mIMusicServiceSubItemList != null){
                    mIMusicServiceSubItemList.onError(1,"");
                }
            }
        });
    }

    private void getLiveLabelList(String taguri){
        Glog.i(TAG,"getLiveLabelList()....... taguri:"+taguri);
        if(mLoading){
            return;
        }
        canRefresh = false;
        RequestBody body = MopidyTools.createRequestBrowse(taguri);
        Call<MopidyRsLiveBrowseBean> call = mpServiceApi.postMopidyLiveQuest(MopidyTools.getHeaders(),body);
        call.enqueue(new Callback<MopidyRsLiveBrowseBean>() {
            @Override
            public void onResponse(Call<MopidyRsLiveBrowseBean> call, Response<MopidyRsLiveBrowseBean> response) {
                MopidyRsLiveBrowseBean bean = response.body();
                ArrayList<IMusicServiceSubItem> subItems = new ArrayList<IMusicServiceSubItem>();
                List<MopidyRsLiveBrowseBean.Result> resultList = new ArrayList<MopidyRsLiveBrowseBean.Result>();
                resultList = bean.results;
                if (resultList != null && resultList.size()!=0)
                {
                    for (int i=0; i< resultList.size(); i++){
                        final MopidyRsLiveBrowseBean.Result result = resultList.get(i);
                        IMusicServiceSubItem subItem = new IMusicServiceSubItem() {
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
                                bundle.putString(MY_MUSIC_SERVICE_ID,getMusicServiceID());
                                bundle.putInt(PRE_DATA_TYPE, TYPE_LABEL);
                                bundle.putString(TAG_NAME,result.uri);
                                return bundle;
                            }

                            @Override
                            public int getNextDataType() {
                                return TYPE_DATA_LIST;
                            }

                            @Override
                            public String getNextDataId() {
                                return PRE_DATA_TYPE+TYPE_LABEL+result.uri;
                            }

                            @Override
                            public int getIconType() {
                                return 0;
                            }
                        };
                        subItems.add(subItem);
                    }
                    if(mIMusicServiceSubItemList != null && subItems.size() > 0){
                        mIMusicServiceSubItemList.createMusicServiceSubItemList(subItems);
                    }else {
                        mIMusicServiceSubItemList.onEmpty();
                    }
                    mLoading = false;
                }
            }

            @Override
            public void onFailure(Call<MopidyRsLiveBrowseBean> call, Throwable t) {
                mLoading = false;
                if(mIMusicServiceSubItemList != null){
                    mIMusicServiceSubItemList.onError(1,"");
                }
            }
        });
    }

    /**
     * 工程师爸爸 专辑列表
     * @param uri 上一级返回参数的uri
     */
    private void getAlbumList(final String uri){
        Glog.i(TAG,"getAlbumList()....... taguri:"+uri);
        if(mLoading){
            return;
        }
        canRefresh = false; //工程师爸爸 专辑列表不支持刷新
        currentAlbumListPage = 0;
        curAlbumListUri = uri;
        albumList.clear();
        RefreshAlbumList();
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
                        currentAlbumListPage++;
                        canRefresh = true;
                    } else {
                        canRefresh = false;
                    }


                    if (mIMusicServiceSubItemList != null && albumList.size() > 0) {
                        mIMusicServiceSubItemList.createMusicServiceSubItemList(albumList);
                    }else {
                        mIMusicServiceSubItemList.onEmpty();
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

    private void getTrackList(String uri, String image, String singer) {
        if (mLoading) {
            return;
        }
        canRefresh = true;
        currentTrackListPage = 0;
        curTrackListUri = uri;
        currentTrackSinger = singer;
        RefreshTrackList();

    }

    @Override
    public void RefreshTrackList() {
        canRefresh = false;
        Glog.i(TAG,"curTrackListUri: " + curTrackListUri);
        RequestBody body = MopidyTools.createRequestPageBrowse(curTrackListUri,currentTrackListPage);
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
                                        if (!result.update.equals("")){
                                            return TimeUtil.timeMillionByTimeFormat("yyyy-MM-dd",result.update);
                                        }else {
                                            return 0;
                                        }
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
                                    return result.uri;
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

    //歌曲详情，后续点击获取url
    private void getTrackDetails(final String uri){
        final List<String> uris = new ArrayList<String>();
        uris.add(uri);
        RequestBody body = MopidyTools.createTrackDetail(uris);
        Call<ResponseBody> call = mpServiceApi.postMopidyTrackDetails(MopidyTools.getHeaders(),body);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    JSONObject mainObject = new JSONObject(response.body().string()); //json 主体
                    JSONObject resuleObject = mainObject.getJSONObject("result"); // result json 主体
                    JSONArray imageArray = resuleObject.getJSONArray(uri); //获取第一个item 作为整个TRACKLIST封面
                    final JSONObject image = imageArray.getJSONObject(imageArray.length()-1);

                    String imageurl = (String) image.get("mantic_image");
                    currentTrackImage = imageurl;
                    String playUrl = (String) image.get("mantic_real_url");
                    Glog.i(TAG,"playUrl --- >: " + playUrl);

                } catch (JSONException | IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });

    }


    @Override
    public void setIMusicServiceSubItemListCallBack(IMusicServiceSubItemList callback) {
        mIMusicServiceSubItemList = callback;
    }

    @Override
    public void setIMusicServiceAlbum(IMusicServiceAlbum callback) {
        mIMusicServiceAlbum = callback;
    }

    @Override
    public IMusicServiceAlbum getIMusicServiceAlbum() {
        return mIMusicServiceAlbum;
    }

    @Override
    public void exec(Bundle bundle) {
        int type = bundle.getInt(PRE_DATA_TYPE);

        Glog.i(TAG, "exec: " + type);
        if (type == TYPE_FIRST){
            getCategoryList();
        } else if(type == TYPE_CATEGORIES){
            String uri = bundle.getString(CATEGORY_ID);
            getTagList(uri);
        } else if(type == TYPE_TAGS){
            String uri = bundle.getString(TAG_NAME);
            getLabelList(uri);
        } else if(type == TYPE_LABEL){
            String uri = bundle.getString(TAG_NAME);
            Glog.i(TAG, "exec: " + uri);
            getAlbumList(uri);
        }else if (type == TYPE_ALBUMS){
            String uri = bundle.getString(ALBUM_ID);
            String image = bundle.getString(TRACK_IMAGE);
            String singer = bundle.getString(TRACK_SINGER);
            getTrackList(uri,image,singer);

        }
    }

    @Override
    public boolean isRefresh() {
        return canRefresh;
    }
}
