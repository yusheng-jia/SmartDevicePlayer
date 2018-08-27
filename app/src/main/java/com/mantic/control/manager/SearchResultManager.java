package com.mantic.control.manager;


import com.google.gson.Gson;
import com.mantic.control.api.mopidy.MopidyTools;
import com.mantic.control.api.searchresult.SearchOperatorRetrofit;
import com.mantic.control.api.searchresult.SearchOperatorServiceApi;
import com.mantic.control.api.searchresult.bean.AlbumSearchRqBean;
import com.mantic.control.api.searchresult.bean.AlbumSearchRqParams;
import com.mantic.control.api.searchresult.bean.AlbumSearchRqQuery;
import com.mantic.control.api.searchresult.bean.AlbumSearchRsBean;
import com.mantic.control.api.searchresult.bean.AuthorSearchRqBean;
import com.mantic.control.api.searchresult.bean.AuthorSearchRqParams;
import com.mantic.control.api.searchresult.bean.AuthorSearchRqQuery;
import com.mantic.control.api.searchresult.bean.AuthorSearchRsBean;
import com.mantic.control.api.searchresult.bean.RadioSearchRqBean;
import com.mantic.control.api.searchresult.bean.RadioSearchRqParams;
import com.mantic.control.api.searchresult.bean.RadioSearchRqQuery;
import com.mantic.control.api.searchresult.bean.RadioSearchRsBean;
import com.mantic.control.api.searchresult.bean.SearchRqBean;
import com.mantic.control.api.searchresult.bean.SearchRqParams;
import com.mantic.control.api.searchresult.bean.SearchRqQuery;
import com.mantic.control.api.searchresult.bean.SearchRsBean;
import com.mantic.control.api.searchresult.bean.SongSearchRqBean;
import com.mantic.control.api.searchresult.bean.SongSearchRqParams;
import com.mantic.control.api.searchresult.bean.SongSearchRqQuery;
import com.mantic.control.api.searchresult.bean.SongSearchRsBean;
import com.mantic.control.utils.Glog;

import java.util.ArrayList;
import java.util.List;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by lin on 2017/7/7.
 */

public class SearchResultManager {
    private static SearchResultManager instance = null;
    private SearchOperatorServiceApi serviceApi;

    private SearchResultManager() {
        serviceApi = SearchOperatorRetrofit.getInstance().create(SearchOperatorServiceApi.class);
    }

    public static SearchResultManager getInstance() {
        if (instance == null) {
            synchronized (SearchResultManager.class) {
                instance = new SearchResultManager();
            }
        }
        return instance;
    }


    public void getAuthorSearchResult(final Callback<AuthorSearchRsBean> callback, String searchKey, int start, int count, List<String> uris) {
        Call<AuthorSearchRsBean> addCall = serviceApi.postAuthorSearchResultQuest(MopidyTools.getHeaders(),createGetAuthorRqBean(searchKey, start, count, uris));
        addCall.enqueue(new Callback<AuthorSearchRsBean>() {
            @Override
            public void onResponse(Call<AuthorSearchRsBean> call, Response<AuthorSearchRsBean> response) {
                callback.onResponse(call, response);
            }

            @Override
            public void onFailure(Call<AuthorSearchRsBean> call, Throwable t) {
                callback.onFailure(call, t);
            }
        });
    }

    public void getAlbumSearchResult(final Callback<AlbumSearchRsBean> callback, String searchKey, int start, int count, List<String> uris) {
        Call<AlbumSearchRsBean> addCall = serviceApi.postAlbumSearchResultQuest(MopidyTools.getHeaders(),createGetAlbumRqBean(searchKey, start, count, uris));
        addCall.enqueue(new Callback<AlbumSearchRsBean>() {
            @Override
            public void onResponse(Call<AlbumSearchRsBean> call, Response<AlbumSearchRsBean> response) {
                callback.onResponse(call, response);
            }

            @Override
            public void onFailure(Call<AlbumSearchRsBean> call, Throwable t) {
                callback.onFailure(call, t);
            }
        });
    }

    public void getSongSearchResult(final Callback<SongSearchRsBean> callback, String searchKey, int start, int count, List<String> uris) {
        Call<SongSearchRsBean> addCall = serviceApi.postSongSearchResultQuest(MopidyTools.getHeaders(),createGetSongRqBean(searchKey, start, count, uris));
        addCall.enqueue(new Callback<SongSearchRsBean>() {
            @Override
            public void onResponse(Call<SongSearchRsBean> call, Response<SongSearchRsBean> response) {
                callback.onResponse(call, response);
            }

            @Override
            public void onFailure(Call<SongSearchRsBean> call, Throwable t) {
                callback.onFailure(call, t);
            }
        });
    }


    public void getRadioSearchResult(final Callback<RadioSearchRsBean> callback, String searchKey, int start, int count, List<String> uris) {
        Call<RadioSearchRsBean> addCall = serviceApi.postRadioSearchResultQuest(MopidyTools.getHeaders(),createGetRadioRqBean(searchKey, start, count, uris));
        addCall.enqueue(new Callback<RadioSearchRsBean>() {
            @Override
            public void onResponse(Call<RadioSearchRsBean> call, Response<RadioSearchRsBean> response) {
                callback.onResponse(call, response);
            }

            @Override
            public void onFailure(Call<RadioSearchRsBean> call, Throwable t) {
                callback.onFailure(call, t);
            }
        });
    }


    public  void getSearchResult(final Callback<SearchRsBean> callback, String searchKey) {
        Call<SearchRsBean> addCall = serviceApi.postSearchResultQuest(MopidyTools.getHeaders(),createGetRqBean(searchKey));
        addCall.enqueue(new Callback<SearchRsBean>() {
            @Override
            public void onResponse(Call<SearchRsBean> call, Response<SearchRsBean> response) {
                callback.onResponse(call, response);
            }

            @Override
            public void onFailure(Call<SearchRsBean> call, Throwable t) {
                callback.onFailure(call, t);
            }
        });
    }

    private RequestBody createGetRqBean(String searchKey) {
        SearchRqBean bean = new SearchRqBean();
        bean.setMethod("core.library.manitc_search");
        bean.setJsonrpc("2.0");
        bean.setId(1);
        SearchRqParams params = new SearchRqParams();

        params.setOffset(0);
        params.setLimit(10);
        params.setUris(null);
        SearchRqQuery query = new SearchRqQuery();
        List<String> any = new ArrayList<>();
        any.add(searchKey);
        query.setAny(any);
        params.setQuery(query);
        bean.setParams(params);
        Gson gson = new Gson();
        String request = gson.toJson(bean);
        Glog.i("lbj", "createGetRqBean: " + request);
        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), request.toString());
        return body;
    }


    private RequestBody createGetAuthorRqBean(String searchKey, int start, int count, List<String> uris) {
        AuthorSearchRqBean bean = new AuthorSearchRqBean();
        bean.setMethod("core.library.manitc_search");
        bean.setJsonrpc("2.0");
        bean.setId(1);
        AuthorSearchRqParams params = new AuthorSearchRqParams();

        params.setPage(start);
        params.setPagesize(count);
        params.setUris(uris);
        AuthorSearchRqQuery query = new AuthorSearchRqQuery();
        List<String> artist = new ArrayList<>();
        artist.add(searchKey);
        query.setArtist(artist);
        params.setQuery(query);
        bean.setParams(params);
        Gson gson = new Gson();
        String request = gson.toJson(bean);
        Glog.i("lbj", "createGetAuthorRqBean: " + request);
        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), request.toString());
        return body;
    }


    private RequestBody createGetAlbumRqBean(String searchKey, int start, int count, List<String> uris) {
        AlbumSearchRqBean bean = new AlbumSearchRqBean();
        bean.setMethod("core.library.manitc_search");
        bean.setJsonrpc("2.0");
        bean.setId(1);
        AlbumSearchRqParams params = new AlbumSearchRqParams();

        params.setPage(start);
        params.setPagesize(count);
        params.setUris(uris);
        AlbumSearchRqQuery query = new AlbumSearchRqQuery();
        List<String> albums = new ArrayList<>();
        albums.add(searchKey);
        query.setAlbum(albums);
        params.setQuery(query);
        bean.setParams(params);
        Gson gson = new Gson();
        String request = gson.toJson(bean);
        Glog.i("lbj", "createGetAlbumRqBean: " + request);
        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), request.toString());
        return body;
    }


    private RequestBody createGetSongRqBean(String searchKey, int start, int count, List<String> uris) {
        SongSearchRqBean bean = new SongSearchRqBean();
        bean.setMethod("core.library.manitc_search");
        bean.setJsonrpc("2.0");
        bean.setId(1);
        SongSearchRqParams params = new SongSearchRqParams();

        params.setPage(start);
        params.setPagesize(count);
        params.setUris(uris);
        SongSearchRqQuery query = new SongSearchRqQuery();
        List<String> trackName = new ArrayList<>();
        trackName.add(searchKey);
        query.setTrack_name(trackName);
        params.setQuery(query);
        bean.setParams(params);
        Gson gson = new Gson();
        String request = gson.toJson(bean);
        Glog.i("lbj", "createGetSongRqBean: " + request);
        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), request.toString());
        return body;
    }

    private RequestBody createGetRadioRqBean(String searchKey, int start, int count, List<String> uris) {
        RadioSearchRqBean bean = new RadioSearchRqBean();
        bean.setMethod("core.library.manitc_search");
        bean.setJsonrpc("2.0");
        bean.setId(1);
        RadioSearchRqParams params = new RadioSearchRqParams();

        params.setPage(start);
        params.setPagesize(count);
        params.setUris(uris);
        RadioSearchRqQuery query = new RadioSearchRqQuery();
        List<String> radios = new ArrayList<>();
        radios.add(searchKey);
        query.setRadio(radios);
        params.setQuery(query);
        bean.setParams(params);
        Gson gson = new Gson();
        String request = gson.toJson(bean);
        Glog.i("lbj", "createGetRadioRqBean: " + request);
        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), request.toString());
        return body;
    }

}
