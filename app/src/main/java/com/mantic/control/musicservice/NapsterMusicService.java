package com.mantic.control.musicservice;

import android.os.Bundle;

import com.google.gson.Gson;
import com.mantic.control.api.mopidy.MopidyRetrofit;
import com.mantic.control.api.mopidy.MopidyServiceApi;
import com.mantic.control.api.mopidy.MopidyTools;
import com.mantic.control.api.mopidy.bean.MopidyRqBrowseBean;
import com.mantic.control.api.mopidy.bean.MopidyRsBrowseBean;
import com.mantic.control.api.mopidy.bean.MopidyRqImageBean;
import com.mantic.control.utils.Glog;

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
 * Created by Jia on 2017/5/17.
 */

public class NapsterMusicService implements MyMusicService{
    public static final String TAG = "NapsterMusicService";

    private IMusicServiceSubItemList mIMusicServiceSubItemList;
    private IMusicServiceAlbum mIMusicServiceAlbum;

    private static final int TYPE_CATEGOROES = 0;
    private static final int TYPE_TAGS = 1;
    public static final int TYPE_ALBUMS = 2;
    private static final int TYPE_ALBUM = 3;

    private static final String CATEGORY_ID = "category_id";
    private static final String TAG_NAME = "tag_name";
    public static final String ALBUM_ID = "album_id";
    private static final String MOPIDY_NEXT_TYPE = "mopidy_next_type";

    private MopidyServiceApi mpServiceApi;

    private boolean mLoading = false;



    public void getCategoryList(){
        Glog.i(TAG,"getCategoryList().........");
        if(mLoading){
            return;
        }
        RequestBody body = createRequestBrowse("spotify:directory");
        Call<MopidyRsBrowseBean> call = mpServiceApi.postMopidyQuest(MopidyTools.getHeaders(),body);
        call.enqueue(new Callback<MopidyRsBrowseBean>() {
            @Override
            public void onResponse(Call<MopidyRsBrowseBean> call, Response<MopidyRsBrowseBean> response) {
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
                                return 0;
                            }
                        };
                        subItems.add(subItem);
                    }
                }
                if(mIMusicServiceSubItemList != null && subItems.size() > 0){
                    mIMusicServiceSubItemList.createMusicServiceSubItemList(subItems);
                }

                mLoading = false;
            }

            @Override
            public void onFailure(Call<MopidyRsBrowseBean> call, Throwable t) {
                Glog.i(TAG,"onFailure .....");
            }
        });
    }

    private void getTagList(String taguri){
        Glog.i(TAG,"getTagList()....... taguri:"+taguri);
        if(mLoading){
            return;
        }

        RequestBody body = createRequestBrowse(taguri);
        Call<MopidyRsBrowseBean> call = mpServiceApi.postMopidyQuest(MopidyTools.getHeaders(),body);
        call.enqueue(new Callback<MopidyRsBrowseBean>() {
            @Override
            public void onResponse(Call<MopidyRsBrowseBean> call, Response<MopidyRsBrowseBean> response) {
                MopidyRsBrowseBean bean = response.body();
                ArrayList<IMusicServiceSubItem> subItems = new ArrayList<IMusicServiceSubItem>();
                List<MopidyRsBrowseBean.Result> resultList = new ArrayList<MopidyRsBrowseBean.Result>();
                resultList = bean.results;
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

                if(mIMusicServiceSubItemList != null && subItems.size() > 0){
                    mIMusicServiceSubItemList.createMusicServiceSubItemList(subItems);
                }

                mLoading = false;
            }

            @Override
            public void onFailure(Call<MopidyRsBrowseBean> call, Throwable t) {

            }
        });
    }

    public void getAlbumList(final String uri){
        Glog.i(TAG,"getAlbumList()....... taguri:"+uri);
        if(mLoading){
            return;
        }


        RequestBody body = createRequestBrowse(uri);
        Call<MopidyRsBrowseBean> call = mpServiceApi.postMopidyQuest(MopidyTools.getHeaders(),body);
        call.enqueue(new Callback<MopidyRsBrowseBean>() {
            @Override
            public void onResponse(Call<MopidyRsBrowseBean> call, final Response<MopidyRsBrowseBean> response) {
                MopidyRsBrowseBean bean = response.body();
//                Glog.i(TAG,"MopidyRsBrowseBean :" +bean);
                List<MopidyRsBrowseBean.Result> resultList = new ArrayList<MopidyRsBrowseBean.Result>();
                resultList = bean.results;
                if (resultList != null && resultList.size()!= 0){
                    MopidyRsBrowseBean.Result result = resultList.get(0); //获取第一条数据来判断是显示list列表还是Directory目录
                    if (result.type.equals("directory") || result.type.equals("artist")) //目录
                    {
                        getTagList(uri);
                    }else{
                        updateImageAndItems(resultList);
                    }
                }

            }

            @Override
            public void onFailure(Call<MopidyRsBrowseBean> call, Throwable t) {

            }
        });


    }

    public void updateImageAndItems(final List<MopidyRsBrowseBean.Result> resultList){
        Glog.i(TAG,"===========updateImageAndItems===========" + resultList.size());
        if (resultList != null){
            final List<String> uris = new ArrayList<>();
            for (int i = 0; i<resultList.size(); i++){
                MopidyRsBrowseBean.Result result = resultList.get(i);
                uris.add(result.uri);
            }
            RequestBody body = createRequestImage(uris);
            Call<ResponseBody> call = mpServiceApi.postMopidyQuestImage(MopidyTools.getHeaders(),body);
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    try {
                        JSONObject mainObject = new JSONObject(response.body().string()); //json 主体

                        JSONObject resuleObject = mainObject.getJSONObject("result"); // result json 主体

                        ArrayList<IMusicServiceSubItem> subTagItems = new ArrayList<IMusicServiceSubItem>();
                        int count = 0;
                        if (uris.size() > 100 )
                        {
                            count = 100;
                        }else{
                            count = uris.size();
                        }
                        for (int i = 0; i<count;i++){

                            JSONArray imageArray = resuleObject.getJSONArray(uris.get(i));
                            JSONObject image = imageArray.getJSONObject(imageArray.length()-1);
                            final String imageUri = (String )image.get("uri");
//                            imageLists.add(imageUri);
                            Glog.i(TAG,"imageUri："+imageUri);
                            final String name = resultList.get(i).name;
                            IMusicServiceSubItemAlbum subItem = new IMusicServiceSubItemAlbum() {
                                @Override
                                public String getCoverUrl() {
                                    return imageUri;
                                }

                                @Override
                                public String getAlbumTitle() {
                                    return name;
                                }

                                @Override
                                public String getAlbumTags() {
                                    return imageUri;
                                }

                                @Override
                                public String getAlbumIntro() {
                                    return name;
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
                                    return null;
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
                                    return 0;
                                }

                                @Override
                                public String getPlayCount() {
                                    return null;
                                }

                                @Override
                                public String getItemIconUrl() {
                                    return imageUri;
                                }

                                @Override
                                public String getItemText() {
                                    return name;
                                }

                                @Override
                                public Bundle gotoNext() {
//                                    Bundle bundle = new Bundle();
//                                    bundle.putString(MY_MUSIC_SERVICE_ID,getMusicServiceID());
//                                    bundle.putInt(PRE_DATA_TYPE, TYPE_ALBUMS);
//                                    bundle.putString(ALBUM_ID,name);
//                                    return bundle;
                                    return null;
                                }

                                @Override
                                public int getNextDataType() {
                                    return 0;
                                }

                                @Override
                                public String getNextDataId() {
                                    return null;
                                }

                                @Override
                                public int getIconType() {
                                    return 0;
                                }
                            };
                            subTagItems.add(subItem);

                        }

                        if(mIMusicServiceSubItemList != null && subTagItems.size() > 0){
                            mIMusicServiceSubItemList.createMusicServiceSubItemList(subTagItems);
                        }

                        mLoading = false;

                    }catch (IOException e){
                        Glog.i(TAG,"IOException" +e.toString());
                    }catch (JSONException e){
                        Glog.i(TAG,"JSONException" +e.toString());
                    }


                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {

                }
            });
        }

    }


//    private void getImages(final MopidyRsBrowseBean.Result result){
//        final String uri = result.uri;
//        List<String> uris = new ArrayList<>();
//        uris.add(uri);
//        RequestBody body = createRequestImage(uris);
//        mpServiceApi.postMopidyQuestImage(body).enqueue(new Callback<ResponseBody>() {
//            @Override
//            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
//                try {
//                    String bean = response.body().string();
//                    JSONObject mainObject = new JSONObject(bean);
//                    JSONObject resuleObject = mainObject.getJSONObject("result");
//                    JSONArray imageList = resuleObject.getJSONArray(uri);
//                    int length = imageList.length();
//                    JSONObject image = imageList.getJSONObject(length-1);
//                    final String imageUri = (String) image.get("uri");
//                    Glog.i(TAG,"imageUri  :"+imageUri);
//                    IMusicServiceSubItemAlbum subItem = new IMusicServiceSubItemAlbum() {
//                        @Override
//                        public String getCoverUrl() {
//                            return imageUri;
//                        }
//
//                        @Override
//                        public String getAlbumTitle() {
//                            return result.name;
//                        }
//
//                        @Override
//                        public String getAlbumTags() {
//                            return imageUri;
//                        }
//
//                        @Override
//                        public String getAlbumIntro() {
//                            return result.name;
//                        }
//
//                        @Override
//                        public String getItemIconUrl() {
//                            return imageUri;
//                        }
//
//                        @Override
//                        public String getItemText() {
//                            return result.name;
//                        }
//
//                        @Override
//                        public Bundle gotoNext() {
//                            Bundle bundle = new Bundle();
//                            bundle.putString(MY_MUSIC_SERVICE_ID,getMusicServiceID());
//                            bundle.putInt(PRE_DATA_TYPE, TYPE_ALBUMS);
//                            bundle.putString(ALBUM_ID,result.uri);
//                            return bundle;
//                        }
//
//                        @Override
//                        public int getNextDataType() {
//                            return TYPE_DATA_ALBUM;
//                        }
//
//                        @Override
//                        public String getNextDataId() {
//                            return PRE_DATA_TYPE+TYPE_ALBUMS+result.uri;
//                        }
//                    };
//                    subTagItems.add(subItem);
//                }catch (IOException e){
//                    Glog.i(TAG,"IOException" +e.toString());
//                }catch (JSONException e){
//                    Glog.i(TAG,"JSONException" +e.toString());
//                }
//            }
//
//            @Override
//            public void onFailure(Call<ResponseBody> call, Throwable t) {
//
//            }
//        });
//
//    }

    /**
     *
     * @param uri
     * @return Browse 请求body
     */
    private RequestBody createRequestBrowse(String uri){
        MopidyRqBrowseBean requestBean = new MopidyRqBrowseBean();
        MopidyRqBrowseBean.ParamsBean paramsBean = new MopidyRqBrowseBean.ParamsBean();
        paramsBean.setUri(uri);
        requestBean.setId(1);
        requestBean.setJsonrpc("2.0");
        requestBean.setMethod("core.library.browse");
        requestBean.setParams(paramsBean);
        Gson gson=new Gson();
        String request = gson.toJson(requestBean);
        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"),request);
        return body;
    }

    /**
     *
     * @param uris
     * @return Image 请求body
     */
    private RequestBody createRequestImage(List<String> uris){
        MopidyRqImageBean requestBean = new MopidyRqImageBean();
        MopidyRqImageBean.ParamsBean paramsBean = new MopidyRqImageBean.ParamsBean();
        paramsBean.setUris(uris);
        requestBean.setId(1);
        requestBean.setJsonrpc("2.0");
        requestBean.setMethod("core.library.get_images");
        requestBean.setParams(paramsBean);
        Gson gson=new Gson();
        String request = gson.toJson(requestBean);
        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"),request);
        return body;
    }


    public NapsterMusicService() {
        mpServiceApi = MopidyRetrofit.getInstance().create(MopidyServiceApi.class);
    }

    @Override
    public String getMusicServiceID() {
        return "nodata";
    }

    @Override
    public void setIMusicServiceSubItemListCallBack(IMusicServiceSubItemList callback) {
//        mIMusicServiceSubItemList = callback; // 无法正常播放，暂时屏蔽
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
        if(type == TYPE_FIRST){
            this.getCategoryList();
        }
        else if(type == TYPE_CATEGOROES){
            String uri = bundle.getString(CATEGORY_ID);
            this.getTagList(uri);
        }
        else if(type == TYPE_TAGS){
            String uri = bundle.getString(TAG_NAME);
            this.getAlbumList(uri);
        }
// else if(type == TYPE_ALBUMS){
//            long id = bundle.getLong(ALBUM_ID);
//            this.getAudioList(id,1,20);
//        }
    }

    @Override
    public boolean isRefresh() {
        return false;
    }

    @Override
    public void RefreshAlbumList() {

    }
    @Override
    public void RefreshTrackList() {

    }

}
