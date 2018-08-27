package com.mantic.control.api.mopidy;

import android.content.Context;

import com.baidu.iot.sdk.IoTSDKManager;
import com.google.gson.Gson;
import com.mantic.control.ManticApplication;
import com.mantic.control.api.mopidy.bean.MopidyRqBrowseBean;
import com.mantic.control.api.mopidy.bean.MopidyRqBrowsePageBean;
import com.mantic.control.api.mopidy.bean.MopidyRqGetRecBean;
import com.mantic.control.api.mopidy.bean.MopidyRqImageBean;
import com.mantic.control.api.mopidy.bean.MopidyRqSetRecBean;
import com.mantic.control.api.mylike.bean.MyLikeDeleteRqBean;
import com.mantic.control.api.mylike.bean.MyLikeDeltePrams;
import com.mantic.control.utils.Glog;
import com.mantic.control.utils.SharePreferenceUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.RequestBody;

/**
 * Created by Jia on 2017/6/9.
 */

public class MopidyTools {
    public static final String TYPE_DIRECTORY = "directory";
    public static final String TPYE_ALBUM = "album";
    public static final String TYPE_TRRACK = "track";

    public static final String ALBUM_TYPE_MORE = "more";
    public static final String ALBUM_TYPE_END = "end";

    /**
     *
     * @param uri
     * @return Browse 请求body
     */
    public static RequestBody createRequestBrowse(String uri){
        MopidyRqBrowseBean requestBean = new MopidyRqBrowseBean();
        MopidyRqBrowseBean.ParamsBean paramsBean = new MopidyRqBrowseBean.ParamsBean();
        paramsBean.setUri(uri);
        requestBean.setId(1);
        requestBean.setJsonrpc("2.0");
        requestBean.setMethod("core.library.browse_new");
        requestBean.setParams(paramsBean);
        Gson gson=new Gson();
        String request = gson.toJson(requestBean);
        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"),request);
        return body;
    }

    /**
     *
     * @param uri
     * @return Browse 请求body
     */
    public static RequestBody createRequestPageBrowse(String uri, int page){
        MopidyRqBrowsePageBean requestBean = new MopidyRqBrowsePageBean();
        MopidyRqBrowsePageBean.ParamsBean paramsBean = new MopidyRqBrowsePageBean.ParamsBean();
        paramsBean.setUri(uri);
        paramsBean.setPage(page);
        paramsBean.setPageSize(20);
        requestBean.setId(1);
        requestBean.setJsonrpc("2.0");
        requestBean.setMethod("core.library.browse_new");
        requestBean.setParams(paramsBean);
        Gson gson=new Gson();
        String request = gson.toJson(requestBean);
        Glog.i("lbj", "createRequestPageBrowse: " + request);
        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"),request);
        return body;
    }

    /**
     * 背景音乐用
     * @param
     * @return Browse 请求body
     */
    public static RequestBody createRequestPageBgBrowse(String uri, int page){
        MopidyRqBrowsePageBean requestBean = new MopidyRqBrowsePageBean();
        MopidyRqBrowsePageBean.ParamsBean paramsBean = new MopidyRqBrowsePageBean.ParamsBean();
        paramsBean.setUri(uri);
        paramsBean.setPage(page);
        paramsBean.setPageSize(40);
        requestBean.setId(1);
        requestBean.setJsonrpc("2.0");
        requestBean.setMethod("core.library.browse_new");
        requestBean.setParams(paramsBean);
        Gson gson=new Gson();
        String request = gson.toJson(requestBean);
        Glog.i("lbj", "createRequestPageBrowse: " + request);
        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"),request);
        return body;
    }

    /**
     *
     * @param uris
     * @return Image 请求body
     */
    public static RequestBody createRequestImage(List<String> uris){
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

    /**
     *
     * @param uris
     * @return Tracklist 详情 请求body
     */
    public static RequestBody createTrackDetail(List<String> uris){
        MopidyRqImageBean requestBean = new MopidyRqImageBean();
        MopidyRqImageBean.ParamsBean paramsBean = new MopidyRqImageBean.ParamsBean();
        paramsBean.setUris(uris);
        requestBean.setId(1);
        requestBean.setJsonrpc("2.0");
        requestBean.setMethod("core.library.manitc_get_tracks_detail");
        requestBean.setParams(paramsBean);
        Gson gson=new Gson();
        String request = gson.toJson(requestBean);
        Glog.i("lbj", "createTrackDetail: " + request);
        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"),request);
        return body;
    }


    /**
     *
     * @param rec_status
     * @return Tracklist 详情 请求body
     *
     */
    public static RequestBody createSetRecStatus(boolean rec_status, Context context){
        MopidyRqSetRecBean requestBean = new MopidyRqSetRecBean();
        MopidyRqSetRecBean.ParamsBean paramsBean = new MopidyRqSetRecBean.ParamsBean();
        paramsBean.setRec_status(rec_status);
        requestBean.setId(1);
        requestBean.setDevice_id(SharePreferenceUtil.getUserId(context));
        requestBean.setJsonrpc("2.0");
        requestBean.setMethod("core.playlists.mantic_set_recommend_status");
        requestBean.setParams(paramsBean);
        Gson gson=new Gson();
        String request = gson.toJson(requestBean);
        Glog.i("lbj", "createSetRecStatus: " + request);
        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"),request);
        return body;
    }


    /**
     *
     * @param context
     * @return Tracklist 详情 请求body
     *
     */
    public static RequestBody createGetRecStatus(Context context){
        MopidyRqGetRecBean requestBean = new MopidyRqGetRecBean();
        requestBean.setId(1);
        requestBean.setDevice_id(SharePreferenceUtil.getUserId(context));
        requestBean.setJsonrpc("2.0");
        requestBean.setMethod("core.playlists.mantic_get_recommend_status");
        Gson gson=new Gson();
        String request = gson.toJson(requestBean);
        Glog.i("lbj", "createGetRecStatus: " + request);
        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"),request);
        return body;
    }

    public static Map<String,String> getHeaders(){
        String accessToken = IoTSDKManager.getInstance().getAccessToken().getAccessToken();
        Map<String,String> map = new HashMap<String, String>();
        map.put("Authorization","Bearer " + accessToken);
        map.put("Lc", ManticApplication.channelId);
        return map;
    }

    public static   RequestBody createProductDeleteRqBean(List<String> uris, Context context) {
        MyLikeDeleteRqBean addBean = new MyLikeDeleteRqBean();
        addBean.setMethod("core.playlists.mantic_mywork_remove");
        addBean.setDevice_id(SharePreferenceUtil.getUserId(context));
        addBean.setJsonrpc("2.0");
        addBean.setId(50);
        MyLikeDeltePrams params = new MyLikeDeltePrams();
        params.setUri_scheme("mongodb");
        params.setTrack_uris(uris);
        addBean.setParams(params);
        Gson gson = new Gson();
        String request = gson.toJson(addBean);
        Glog.i("jys", "createDeleteRqBean: " + request);
        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), request.toString());
        return body;
    }

}
