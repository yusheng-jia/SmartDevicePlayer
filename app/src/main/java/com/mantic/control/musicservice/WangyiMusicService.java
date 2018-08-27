package com.mantic.control.musicservice;

import android.os.Bundle;

import com.google.gson.Gson;
import com.mantic.control.R;
import com.mantic.control.api.mopidy.MopidyRetrofit;
import com.mantic.control.api.mopidy.MopidyServiceApi;
import com.mantic.control.api.mopidy.MopidyTools;
import com.mantic.control.api.mopidy.MopidyUrl;
import com.mantic.control.api.mopidy.bean.MopidyRqBrowseBean;
import com.mantic.control.api.mopidy.bean.MopidyRsAlbumBean;
import com.mantic.control.api.mopidy.bean.MopidyRsAlbumMoreBean;
import com.mantic.control.api.mopidy.bean.MopidyRsAlbumPageBean;
import com.mantic.control.api.mopidy.bean.MopidyRsBrowseBean;
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
 * Created by Jia on 2017/5/16.
 */

public class WangyiMusicService implements MyMusicService{

    public static final String TAG = "WangyiMusicService";

    private IMusicServiceSubItemList mIMusicServiceSubItemList;
    private IMusicServiceAlbum mIMusicServiceAlbum;

    private static final int TYPE_CATEGOROES = 0;
    private static final int TYPE_TAGS = 1;
    public static final int TYPE_ALBUMS = 3;
//    private static final int TYPE_ALBUM = 3;

    private static final String CATEGORY_ID = "category_id";
    private static final String TAG_NAME = "tag_name";
    public static final String ALBUM_ID = "album_id";
    private static final String MOPIDY_NEXT_TYPE = "mopidy_next_type";

    // tracklist 没有 image 和 singer 字段，使用 album 的
    private static final String TRACK_IMAGE = "track_image";
    private static final String TRACK_SINGER = "track_singer";
    private MopidyServiceApi mpServiceApi;

    private boolean mLoading = false;


    public WangyiMusicService() {
        mpServiceApi = MopidyRetrofit.getInstance().create(MopidyServiceApi.class);
    }

    /**
     * 网易 category ：热门歌曲 新碟上架 热门歌手
     */
    public void getCategoryList(){
        Glog.i(TAG,"getCategoryList().........");
        if(mLoading){
            return;
        }
        canRefresh = false;
        RequestBody body = MopidyTools.createRequestBrowse("netease:root");
        Call<MopidyRsBrowseBean> call = mpServiceApi.postMopidyQuest(MopidyTools.getHeaders(),body);
        call.enqueue(new Callback<MopidyRsBrowseBean>() {
            @Override
            public void onResponse(Call<MopidyRsBrowseBean> call, Response<MopidyRsBrowseBean> response) {
                if (response.isSuccessful() && null == response.errorBody() && null != response.body()) {
                    MopidyRsBrowseBean bean = response.body();
                    ArrayList<IMusicServiceSubItem> subItems = new ArrayList<IMusicServiceSubItem>();
                    List<MopidyRsBrowseBean.Result> resultList = new ArrayList<MopidyRsBrowseBean.Result>();
                    resultList = bean.results;
                    if(resultList != null) {
                        for (int i = 0; i < resultList.size(); i++) {
                            final MopidyRsBrowseBean.Result result = resultList.get(i);
                            IMusicServiceSubItem subItem = new IMusicServiceSubItem() {
                                @Override
                                public String getItemIconUrl() {
                                    return getCategoryIcon(result.name);
                                }

                                @Override
                                public String getItemText() {
                                    return result.name;
                                }

                                @Override
                                public Bundle gotoNext() {
                                    Bundle bundle = new Bundle();
                                    bundle.putString(MY_MUSIC_SERVICE_ID, getMusicServiceID());
                                    bundle.putInt(PRE_DATA_TYPE, TYPE_CATEGOROES);
                                    bundle.putString(CATEGORY_ID, result.uri);
                                    return bundle;
                                }

                                @Override
                                public int getNextDataType() {
                                    return TYPE_DATA_LIST;
                                }

                                @Override
                                public String getNextDataId() {
                                    return PRE_DATA_TYPE + TYPE_CATEGOROES + result.uri;
                                }

                                @Override
                                public int getIconType() {
                                    return 1;
                                }
                            };
                            subItems.add(subItem);
                        }
                    }

                    if(mIMusicServiceSubItemList != null && subItems.size() > 0){
                        mIMusicServiceSubItemList.createMusicServiceSubItemList(subItems);
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
     * 网易 分类：（华语 日语 ...） 或者 (周杰伦 王力宏 ...)
     * @param taguri 上一级返回参数的uri
     */
    private void getTagList(String taguri){
        Glog.i(TAG,"getTagList()....... taguri:"+taguri);
        if(mLoading){
            return;
        }
        if (taguri.equals("netease:hotartists")){
            getAlbumList(taguri);
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
                        if (resultList.get(0).type.equals("track")){
//                        updateImageAndItems(resultList);
                        }else{
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
                                        bundle.putString(MOPIDY_NEXT_TYPE,result.type);
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
                                        return 0;
                                    }
                                };
                                subItems.add(subItem);
                            }

                        }
                        if(mIMusicServiceSubItemList != null && subItems.size() > 0){
                            mIMusicServiceSubItemList.createMusicServiceSubItemList(subItems);
                        }else {
                            mIMusicServiceSubItemList.onEmpty();
                        }
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

    private  List<MopidyRsAlbumBean.Result> resultAlbumList = new ArrayList<MopidyRsAlbumBean.Result>(); //一级Album目录 是文件夹数量 （0-19，20-39 ...）
    private  List<MopidyRsAlbumMoreBean.Result> resultAlbumMoreList = new ArrayList<MopidyRsAlbumMoreBean.Result>(); // 二级Album目录 具体Album 分页加载
    private int currentAlbumPage = 0;
    private boolean canRefresh = false;

    private ArrayList<IMusicServiceSubItem> albumList = new ArrayList<IMusicServiceSubItem>();
    /**
     * 网易 专辑列表
     * @param uri 上一级返回参数的uri
     */
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
                            final String singer = result.artists_name.get(0);
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
    private int currentAlbumListPage = 0;
    private int currentTrackListPage = 0;
    private String curAlbumListUri = "";
    private String curTrackListUri = "";
    private String currentTrackSinger = "";

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
                                    return "netease";
                                }

                                @Override
                                public String getSinger() {
                                    String artist_name = "";
                                    if (result.mantic_artists_name != null){
                                        for (int i = 0; i< result.mantic_artists_name.size(); i++){
                                            String singer = result.mantic_artists_name.get(i);
                                            artist_name = singer+" ";
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
                                        return TimeUtil.timeMillionByTimeFormat("yyyy-MM-dd",result.update);
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

    @Override
    public boolean isRefresh() {
        return canRefresh;
    }

    @Override
    public String getMusicServiceID() {
        return "wangyi";
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
        return this.mIMusicServiceAlbum;
    }

    @Override
    public void exec(Bundle bundle) {

        int type = bundle.getInt(PRE_DATA_TYPE);

        if (type == TYPE_FIRST){
            this.getCategoryList();
        }
        else if(type == TYPE_CATEGOROES){
            String uri = bundle.getString(CATEGORY_ID);
            this.getTagList(uri);
        }
        else if(type == TYPE_TAGS){
            String uri = bundle.getString(TAG_NAME);
            this.getAlbumList(uri);
        }else if (type == TYPE_ALBUMS){
            String uri = bundle.getString(ALBUM_ID);
            String image = bundle.getString(TRACK_IMAGE);
            String singer = bundle.getString(TRACK_SINGER);
            this.getTrackList(uri,image,singer);

        }
    }

    public String getCategoryIcon(String name){
        if (name.equals("热门歌单")){
            return String.valueOf(R.drawable.wy_hot_icon);
        } else if (name.equals("新碟上架")){
            return String.valueOf(R.drawable.wy_new_icon);
        }else if (name.equals("热门歌手")){
            return String.valueOf(R.drawable.wy_singer_icon);
        }else {
            return "";
        }
    }
}
