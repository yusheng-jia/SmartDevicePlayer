package com.mantic.control.manager;

import android.content.Context;

import com.google.gson.Gson;
import com.mantic.control.api.mopidy.MopidyTools;
import com.mantic.control.api.mychannel.MyChannelOperatorRetrofit;
import com.mantic.control.api.mychannel.MyChannelOperatorServiceApi;
import com.mantic.control.api.mychannel.bean.AlbumDetailRqBean;
import com.mantic.control.api.mychannel.bean.AlbumParams;
import com.mantic.control.api.mychannel.bean.MyChannelAddRsBean;
import com.mantic.control.api.mychannel.bean.MyChannelClearRsBean;
import com.mantic.control.api.mychannel.bean.MyChannelDeleteRsBean;
import com.mantic.control.api.mychannel.bean.MyChannelDetailRsBean;
import com.mantic.control.api.mychannel.bean.MyChannelListPositionChangeRqBean;
import com.mantic.control.api.mychannel.bean.MyChannelListPositionChangeRsBean;
import com.mantic.control.api.mychannel.bean.MyChannelListRsBean;
import com.mantic.control.api.mychannel.bean.MyChannelSaveRsBean;
import com.mantic.control.api.mychannel.bean.PositionChangeParams;
import com.mantic.control.data.MyChannel;
import com.mantic.control.utils.Glog;
import com.mantic.control.utils.SharePreferenceUtil;

import java.util.ArrayList;
import java.util.List;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by lin on 2017/6/22.
 */

public class MyChannelManager {
    private static MyChannelManager instance;
    private static MyChannelOperatorServiceApi serviceApi;


    private MyChannelManager() {
        serviceApi = MyChannelOperatorRetrofit.getInstance().create(MyChannelOperatorServiceApi.class);
    }

    public static MyChannelManager getInstance() {
        if (instance == null) {
            synchronized (ChannelPlayListManager.class) {
                instance = new MyChannelManager();
            }
        }
        return instance;
    }

    public static void addMyChannel(Call<MyChannelAddRsBean> addCall, final Callback callback, MyChannel myChannel) {
        addCall.enqueue(new Callback<MyChannelAddRsBean>() {
            @Override
            public void onResponse(Call<MyChannelAddRsBean> call, Response<MyChannelAddRsBean> response) {
                if (null != callback) {
                    callback.onResponse(call, response);
                }
            }

            @Override
            public void onFailure(Call<MyChannelAddRsBean> call, Throwable t) {
                if (null != callback) {
                    callback.onFailure(call, t);
                }
            }
        });
    }


    public static void saveMyChannel(Call<MyChannelSaveRsBean> updateCall, final Callback<MyChannelSaveRsBean> callback, Context context) {
        updateCall.enqueue(new Callback<MyChannelSaveRsBean>() {
            @Override
            public void onResponse(Call<MyChannelSaveRsBean> call, Response<MyChannelSaveRsBean> response) {
                if (null != callback) {
                    callback.onResponse(call, response);
                }
            }

            @Override
            public void onFailure(Call<MyChannelSaveRsBean> call, Throwable t) {
                if (null != callback) {
                    callback.onFailure(call, t);
                }
            }
        });
    }

    public static void updateMyChannel(Call<ResponseBody> updateCall, final Callback<ResponseBody> callback) {
        updateCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (null != callback) {
                    callback.onResponse(call, response);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                if (null != callback) {
                    callback.onFailure(call, t);
                }
            }
        });
    }


    public static void detailMyChannel(Call<MyChannelDetailRsBean> updateCall, final Callback<MyChannelDetailRsBean> callback, MyChannel myChannel, Context context) {
        updateCall.enqueue(new Callback<MyChannelDetailRsBean>() {
            @Override
            public void onResponse(Call<MyChannelDetailRsBean> call, Response<MyChannelDetailRsBean> response) {
                if (null != callback) {
                    callback.onResponse(call, response);
                }
            }

            @Override
            public void onFailure(Call<MyChannelDetailRsBean> call, Throwable t) {
                if (null != callback) {
                    callback.onFailure(call, t);
                }
            }
        });
    }

    public void deleteMyChannel(Call<MyChannelDeleteRsBean> deleteCall, final Callback callback, MyChannel myChannel) {
        deleteCall.enqueue(new Callback<MyChannelDeleteRsBean>() {
            @Override
            public void onResponse(Call<MyChannelDeleteRsBean> call, Response<MyChannelDeleteRsBean> response) {
                callback.onResponse(call, response);
            }

            @Override
            public void onFailure(Call<MyChannelDeleteRsBean> call, Throwable t) {
                callback.onFailure(call, t);
            }
        });
    }


    public static void clearMyChannel(Call<MyChannelClearRsBean> deleteCall, final Callback<MyChannelClearRsBean> callback) {
        deleteCall.enqueue(new Callback<MyChannelClearRsBean>() {
            @Override
            public void onResponse(Call<MyChannelClearRsBean> call, Response<MyChannelClearRsBean> response) {
                callback.onResponse(call, response);
            }

            @Override
            public void onFailure(Call<MyChannelClearRsBean> call, Throwable t) {
                callback.onFailure(call, t);
            }
        });
    }

    public static void getMyChannelList(Call<MyChannelListRsBean> deleteCall, final Callback callback) {
        deleteCall.enqueue(new Callback<MyChannelListRsBean>() {
            @Override
            public void onResponse(Call<MyChannelListRsBean> call, Response<MyChannelListRsBean> response) {
                callback.onResponse(call, response);
            }

            @Override
            public void onFailure(Call<MyChannelListRsBean> call, Throwable t) {
                callback.onFailure(call, t);
            }
        });
    }

    public void changeMyChannelListPosition(final Callback<MyChannelListPositionChangeRsBean> callback, List<MyChannel> mychannels, Context context) {
        Call<MyChannelListPositionChangeRsBean> call = serviceApi.postMyChannelPositionChangeQuest(MopidyTools.getHeaders(),createChanngePositionRqBean(mychannels, context));
        call.enqueue(new Callback<MyChannelListPositionChangeRsBean>() {
            @Override
            public void onResponse(Call<MyChannelListPositionChangeRsBean> call, Response<MyChannelListPositionChangeRsBean> response) {
                callback.onResponse(call, response);
            }

            @Override
            public void onFailure(Call<MyChannelListPositionChangeRsBean> call, Throwable t) {
                callback.onFailure(call, t);
            }
        });
    }


    public void getAlbumDetail(final Callback<ResponseBody> tailCall, String uri) {
        Call<ResponseBody> call = serviceApi.postAlbumDetailQuest(MopidyTools.getHeaders(),createAlbumDetailRqBean(uri));
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                tailCall.onResponse(call, response);
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                tailCall.onFailure(call, t);
            }
        });
    }

    private RequestBody createChanngePositionRqBean(List<MyChannel> myChannels, Context context) {
        MyChannelListPositionChangeRqBean bean = new MyChannelListPositionChangeRqBean();
        bean.setMethod("core.playlists.mantic_update");
        bean.setDevice_id(SharePreferenceUtil.getDeviceId(context));
        bean.setJsonrpc("2.0");
        bean.setId(1);
        PositionChangeParams params = new PositionChangeParams();

        params.uris = new ArrayList<String>();
        for (int i = 0; i < myChannels.size(); i++) {
            params.uris.add(myChannels.get(i).getUrl());
        }
        bean.setParams(params);

        bean.setParams(params);
        Gson gson = new Gson();
        String request = gson.toJson(bean);
        Glog.i("lbj", "createAddRqBean: " + request);
        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), request.toString());
        return body;
    }



    private RequestBody createAlbumDetailRqBean(String uri) {
        AlbumDetailRqBean bean = new AlbumDetailRqBean();
        bean.setMethod("core.library.mantic_get_albums_detail");
        bean.setJsonrpc("2.0");
        bean.setId(1);
        AlbumParams params = new AlbumParams();
        List<String> uris = new ArrayList<>();
        uris.add(uri);
        params.setUris(uris);
        bean.setParams(params);
        Gson gson = new Gson();
        String request = gson.toJson(bean);
        Glog.i("lbj", "createAlbumDetailRqBean: " + request);
        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), request.toString());
        return body;
    }
}
